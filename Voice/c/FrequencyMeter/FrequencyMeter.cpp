const int THREAD_N = 128;
const int BLOCK_N = 4;

int bufFrameLength;
int freqCount;

__device__ float *d_xs;
__device__ float *d_ys;
__device__ float *d_as;
__device__ float *d_maxes;

__device__ float *d_periods;
__device__ float *d_rces;
__device__ float *d_rses;
__device__ float *d_dampes;

#define PI 3.14159265358979323846264338327950288f

__global__ void dev_init(){
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
		d_dampes[f] = powf(1/PI, 1/period);
	}
}

void sp_init(int _bufFrameLength, int _freqCount, float minFreq, float maxFreq, float rate) {
	bufFrameLength = _bufFrameLength;
	freqCount = _freqCount;

	CUT_DEVICE_INIT();

	CUDA_SAFE_CALL( cudaMalloc((void **)&d_data, bufFrameLength * sizeof(float)) );
	CUDA_SAFE_CALL( cudaMalloc((void **)&d_freq, freqCount * bufFrameLength * sizeof(float)) );

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

	dev_init<<<BLOCK_N, THREAD_N>>>();
}

__device__ void rotate(float &x, float &y, const float rc, const float rs, const float c) {
	const float xn = (x-c)*rc - y*rs;
	y = (x-c)*rs + y*rc;
	x = xn+c;
}

__global__ void dev_freq(int frames, float *data, float* freq, int freqCount) {
	const int tid = blockDim.x * blockIdx.x + threadIdx.x;
	const int threadN = blockDim.x * gridDim.x;

	for (int f = tid; f<freqCount; f+=threadN){
		float x = d_xs[f];
		float y = d_ys[f];
		float a = d_as[f];
		float m = d_maxes[f];
		float maxy = 0;
		const float period = d_periods[f]/2;
		const float rc = d_rces[f];
		const float rs = d_rses[f];
		const float damp = d_dampes[f];

		for (int t=0, float p=0; t<frames; t++, p++) {
		    __syncthreads();
			const float d = data[t];		
			rotate(x, y, rc, rs, d);
			y *= damp;

			maxy = fmaxf(maxy, fabsf(y));

			if (p>=period) {
				p = 0;
				a = maxy;
				maxy = 0;
			}

			freq[f + freqCount*t] = a;

			m = fmaxf[m, a];
		}

		d_xs[f] = x;
		d_ys[f] = y;
		d_as[f] = a;
		d_maxes[f] = m;
	}
}

void sp_freq(int frames, float *data, float* freq) {
	float *d_data;
	float *d_freq;

	CUDA_SAFE_CALL( cudaMemcpy(d_data, data, frames * sizeof(float), cudaMemcpyHostToDevice) );

    CUDA_SAFE_CALL( cudaThreadSynchronize() );
	dev_freq<<<BLOCK_N, THREAD_N>>>(frames, d_data, d_freq, freqCount);
    CUDA_SAFE_CALL( cudaThreadSynchronize() );

	CUDA_SAFE_CALL( cudaMemcpy(freq, d_freq, frequenciesCount * frames * sizeof(float), cudaMemcpyDeviceToHost) );	
}

float sp_max() {
	float *maxes = (float*)malloc(sizeof(float) * freqCount);

	CUDA_SAFE_CALL( cudaMemcpy(maxes, d_maxes, frequenciesCount * sizeof(float), cudaMemcpyDeviceToHost) );	

	float m = 0;
	for (int i=0; i<freqCount; i++) {
		m = fmaxf(m, maxes[i]);
	}

	return m;
}

void sp_close() {
	CUDA_SAFE_CALL( cudaFree(d_data) );
	CUDA_SAFE_CALL( cudaFree(d_freq) );

	CUDA_SAFE_CALL( cudaFree(d_xs) );
	CUDA_SAFE_CALL( cudaFree(d_ys) );
	CUDA_SAFE_CALL( cudaFree(d_as) );
	CUDA_SAFE_CALL( cudaFree(d_maxes) );

	CUDA_SAFE_CALL( cudaFree(d_periods) );
	CUDA_SAFE_CALL( cudaFree(d_rces) );
	CUDA_SAFE_CALL( cudaFree(d_rses) );
	CUDA_SAFE_CALL( cudaFree(d_dampes) );
}