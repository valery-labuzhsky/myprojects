#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

#include <cutil.h>

const int THREAD_N = 128;
const int BLOCK_N = 4;

int bufFrameLength;
int freqCount;

__device__ float *d_data;
__device__ float *d_freq;
__device__ int *d_ifreq;

__device__ float *d_xs;
__device__ float *d_ys;
__device__ float *d_as;
__device__ float *d_maxes;

__device__ float *d_periods;
__device__ float *d_rces;
__device__ float *d_rses;
__device__ float *d_dampes;

#define PI 3.14159265358979323846264338327950288f

__global__ void dev_init(int freqCount, 
						 float *d_xs,
						 float *d_ys,
						 float *d_as,
						 float *d_maxes,
						 
						 float *d_periods, 
						 float *d_rces,
						 float *d_rses,
						 float *d_dampes
){
	const int tid = blockDim.x * blockIdx.x + threadIdx.x;
	const int threadN = blockDim.x * gridDim.x;

	for (int f = tid; f<freqCount; f+=threadN){
		d_xs[f] = 0;
		d_ys[f] = 0;
		d_as[f] = 0;
		d_maxes[f] = 0;

		const float period = d_periods[f];
		d_rces[f] = cosf(2*PI/period);
		d_rses[f] = sinf(2*PI/period);
		d_dampes[f] = powf(1.0/PI, 1.0/period);
	}
}

extern "C"
void sp_init(int _bufFrameLength, int _freqCount, float minFreq, float maxFreq, float rate) {
	bufFrameLength = _bufFrameLength;
	freqCount = _freqCount;

	CUT_DEVICE_INIT();

	CUDA_SAFE_CALL( cudaMalloc((void **)&d_data, bufFrameLength * sizeof(float)) );
	CUDA_SAFE_CALL( cudaMalloc((void **)&d_freq, freqCount * bufFrameLength * sizeof(float)) );
	CUDA_SAFE_CALL( cudaMalloc((void **)&d_ifreq, freqCount * bufFrameLength * sizeof(int)) );

	CUDA_SAFE_CALL( cudaMalloc((void **)&d_xs, freqCount * sizeof(float)) );
	CUDA_SAFE_CALL( cudaMalloc((void **)&d_ys, freqCount * sizeof(float)) );
	CUDA_SAFE_CALL( cudaMalloc((void **)&d_as, freqCount * sizeof(float)) );
	CUDA_SAFE_CALL( cudaMalloc((void **)&d_maxes, freqCount * sizeof(float)) );

	CUDA_SAFE_CALL( cudaMalloc((void **)&d_periods, freqCount * sizeof(float)) );
	CUDA_SAFE_CALL( cudaMalloc((void **)&d_rces, freqCount * sizeof(float)) );
	CUDA_SAFE_CALL( cudaMalloc((void **)&d_rses, freqCount * sizeof(float)) );
	CUDA_SAFE_CALL( cudaMalloc((void **)&d_dampes, freqCount * sizeof(float)) );

	float *periods = (float*)malloc(sizeof(float) * freqCount);
	float max = rate/minFreq;
	float min = rate/maxFreq;
    min = logf(min);
    max = logf(max);

	for (int f=0; f<freqCount; f++){
      periods[f] = expf(min + f * (max - min) / (freqCount - 1));
	}

	CUDA_SAFE_CALL( cudaMemcpy(d_periods, periods, freqCount * sizeof(float), cudaMemcpyHostToDevice) );

    CUDA_SAFE_CALL( cudaThreadSynchronize() );
	dev_init<<<BLOCK_N, THREAD_N>>>(freqCount, d_xs, d_ys, d_as, d_maxes, d_periods, d_rces, d_rses, d_dampes);

	CUT_CHECK_ERROR("dev_init() execution failed\n");
    CUDA_SAFE_CALL( cudaThreadSynchronize() );

	free(periods);
}

__device__ void rotate(float &x, float &y, const float rc, const float rs, const float c) {
	const float xn = (x-c)*rc - y*rs;
	y = (x-c)*rs + y*rc;
	x = xn+c;
}

__global__ void dev_freq(int frames, float *data, float* freq, int freqCount,
						 float *d_xs,
						 float *d_ys,
						 float *d_as,
						 float *d_maxes,
						 
						 float *d_periods, 
						 float *d_rces,
						 float *d_rses,
						 float *d_dampes
) {
	const int tid = blockDim.x * blockIdx.x + threadIdx.x;
	const int threadN = blockDim.x * gridDim.x;

	for (int f = tid; f<freqCount; f+=threadN){
		float x = d_xs[f];
		float y = d_ys[f];
		float a = d_as[f];
		float m = d_maxes[f];
		//float maxy = 0;
		//const float period = d_periods[f]/2;
		const float rc = d_rces[f];
		const float rs = d_rses[f];
		const float damp = d_dampes[f];

		float p = 0;
		for (int t=0; t<frames; t++, p++) {
		    __syncthreads();
			const float d = data[t];		
			//rotate(x, y, rc, rs, d);
			y += d;
			rotate(x, y, rc, rs, 0);
			y *= damp;
			//x *= damp;

			//maxy = fmaxf(maxy, fabsf(y));

			/*if (p>=period) {
				p = 0;
				a = maxy;
				maxy = 0;
			}*/

			//a = sqrtf((x-d)*(x-d) + y*y);
			a = sqrtf(x*x + y*y) / d_periods[f];

			freq[f + freqCount*t] = a;

			m = fmaxf(m, a);
		}

		d_xs[f] = x;
		d_ys[f] = y;
		d_as[f] = a;
		d_maxes[f] = m;
	}
}

__global__ void dev_ifreq(int lenght, float *freq, int* ifreq, float max){
	const int tid = blockDim.x * blockIdx.x + threadIdx.x;
	const int threadN = blockDim.x * gridDim.x;

	for (int f = tid; f<lenght; f+=threadN){
	    __syncthreads();
		int ifr = (int)(freq[f]/max*255.0); 
		ifreq[f] = ifr | (ifr << 8) | (ifr << 16) | 0xFF000000;
	}
}

extern "C"
void sp_freq(int frames, float *data, float* freq) {
	CUDA_SAFE_CALL( cudaMemcpy(d_data, data, frames * sizeof(float), cudaMemcpyHostToDevice) );

    CUDA_SAFE_CALL( cudaThreadSynchronize() );
	dev_freq<<<BLOCK_N, THREAD_N>>>(frames, d_data, d_freq, freqCount, d_xs, d_ys, d_as, d_maxes, d_periods, d_rces, d_rses, d_dampes);
    CUDA_SAFE_CALL( cudaThreadSynchronize() );

//	printf("before memory copy\n");
	CUDA_SAFE_CALL( cudaMemcpy(freq, d_freq, freqCount * frames * sizeof(float), cudaMemcpyDeviceToHost) );	
}

extern "C"
void sp_ifreq(int lenght, float* freq, int* ifreq, float max) {
	CUDA_SAFE_CALL( cudaMemcpy(d_freq, freq, lenght * sizeof(float), cudaMemcpyHostToDevice) );

    CUDA_SAFE_CALL( cudaThreadSynchronize() );
	dev_ifreq<<<BLOCK_N, THREAD_N>>>(lenght, d_freq, d_ifreq, max);
    CUDA_SAFE_CALL( cudaThreadSynchronize() );

	CUDA_SAFE_CALL( cudaMemcpy(ifreq, d_ifreq, lenght * sizeof(float), cudaMemcpyDeviceToHost) );	
}

extern "C"
float sp_max() {
	float *maxes = (float*)malloc(sizeof(float) * freqCount);

	CUDA_SAFE_CALL( cudaMemcpy(maxes, d_maxes, freqCount * sizeof(float), cudaMemcpyDeviceToHost) );	

	float m = 0;
	for (int i=0; i<freqCount; i++) {
		m = fmaxf(m, maxes[i]);
	}
	
	free(maxes);

	return m;
}

extern "C"
void sp_close() {
	CUDA_SAFE_CALL( cudaFree(d_data) );
	CUDA_SAFE_CALL( cudaFree(d_freq) );
	CUDA_SAFE_CALL( cudaFree(d_ifreq) );

	CUDA_SAFE_CALL( cudaFree(d_xs) );
	CUDA_SAFE_CALL( cudaFree(d_ys) );
	CUDA_SAFE_CALL( cudaFree(d_as) );
	CUDA_SAFE_CALL( cudaFree(d_maxes) );

	CUDA_SAFE_CALL( cudaFree(d_periods) );
	CUDA_SAFE_CALL( cudaFree(d_rces) );
	CUDA_SAFE_CALL( cudaFree(d_rses) );
	CUDA_SAFE_CALL( cudaFree(d_dampes) );
}
