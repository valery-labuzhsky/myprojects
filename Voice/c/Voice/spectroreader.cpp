#include "spectroreader.h"

#include "spectroscope.h"

#include <stdlib.h>

#define min(a, b)  (((a) < (b)) ? (a) : (b)) 

extern "C"
SR_INFO * sr_open(char* soundFile, int frequencies, float min_freq, float max_freq){
	SR_INFO * sr_info = (SR_INFO*)malloc(sizeof(SR_INFO));
	sr_info->frequencies = frequencies;
	sr_info->min_freq = min_freq;
	sr_info->max_freq = max_freq;

	SF_INFO info;
	SNDFILE* sndfile = sf_open(soundFile, SFM_READ, &info);
	sr_info->sndfile = sndfile;

	sr_info->frames = info.frames;
	sr_info->channels = info.channels;
	sr_info->rate = (float)info.samplerate;

	return sr_info;
}

extern "C"
int * sr_iread(SR_INFO* sr_info, float& max) {
	int bufsize = 128*1024*1024/sizeof(float)/sr_info->frequencies;

	int *ifreq = (int*)malloc(sizeof(int)*sr_info->frames*sr_info->frequencies);

//	printf("before fread\n");
	float * freq = sr_fread(sr_info, max);

//	printf("after fread\n");
	int offset = 0;
	while (offset<sr_info->frames*sr_info->frequencies) {
		sp_ifreq(min(bufsize, sr_info->frames*sr_info->frequencies-offset), freq+offset, ifreq+offset, max);
		offset += bufsize;
	}

//	printf("after while\n");
	free(freq);

	return ifreq;
}

extern "C"
float * sr_fread(SR_INFO* sr_info, float& max) {
	int bufsize = 128*1024*1024/sizeof(float)/sr_info->frequencies;

	float *data = (float*)malloc(sizeof(float)*sr_info->channels*bufsize);
	float *freq = (float*)malloc(sizeof(float)*sr_info->frames*sr_info->frequencies);

	int frames = sr_info->frames;
	SNDFILE* sndfile = sr_info->sndfile;

	sp_init(bufsize, sr_info->frequencies, sr_info->min_freq, sr_info->max_freq, sr_info->rate);

	sf_count_t readed;
	int position = 0;
	do {

		readed = sf_readf_float(sndfile, data, bufsize);
		if (sr_info->channels > 1){
			for (int i=0; i<readed; i++) {
				float s = 0;
				for (int c=0; c<sr_info->channels; c++) {
					s += data[c + i*sr_info->channels];
				}
				s /= sr_info->channels;
				data[i] = s;
			}
		}

//		printf("before freq %i\n", readed);
		sp_freq(readed, data, freq+position);//*sizeof(float));
//		printf("after freq\n");
		position += readed*sr_info->frequencies;
	} while (readed > 0);

	max = sp_max();

	free(data);

	return freq;
}

extern "C"
void sr_close(SR_INFO* sr_info){
	sp_close();
	sf_close(sr_info->sndfile);
	free(sr_info);
}