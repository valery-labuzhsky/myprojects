#include <stdio.h>
#include <stdlib.h>

#include "sndfile.h"

#include <vector_types.h>
#include "cutil.h"

#include "spectroscope.h"
#include "spectroreader.h"

void printUsage(char * prog){
	printf("Usage: %s <soundfile>\n", prog);
}

typedef struct
{  int frames;
   int frequencies;
   float min_freq;
   float max_freq;
   float rate;
} SP_INFO;

int main(int argc, char **argv){
	if (argc<2) printUsage(argv[0]);
	char* soundFile = argv[1];
	
	SP_INFO sp_info;
	sp_info.frequencies = 1024;
	sp_info.min_freq = 300;
	sp_info.max_freq = 3400;

	SF_INFO info;
	SNDFILE* sndfile = sf_open(soundFile, SFM_READ, &info);

	int bufsize = 128*1024*1024/sizeof(float)/sp_info.frequencies;
	sp_info.frames = info.frames;
	sp_info.rate = (float)info.samplerate;

	float *data = (float*)malloc(sizeof(float)*info.channels*bufsize);
	float *freq = (float*)malloc(sizeof(float)*sp_info.frames*sp_info.frequencies);
	printf("Freq %i %i\n", sp_info.frames, sp_info.frequencies);

	FILE *stream;
	int  result;

	float m = 0;

	if ( fopen_s( &stream, "out.sp", "w+b" ) != 0 )
	{
		printf( "The file out.sp was not opened\n" );
		return -1;
	}
	
	int frames = info.frames;

	fwrite( &sp_info, 4*5, 1, stream );

	//return 0;

	fwrite( &m, sizeof(float), 1, stream );

	sp_init(bufsize, sp_info.frequencies, sp_info.min_freq, sp_info.max_freq, sp_info.rate);

	sf_count_t readed;
	do {
		readed = sf_readf_float(sndfile, data, bufsize);
		if (info.channels > 1){
			for (int i=0; i<readed; i++) {
				float s = 0;
				for (int c=0; c<info.channels; c++) {
					s += data[c + i*info.channels];
				}
				s /= info.channels;
				data[i] = s;
			}
		}

		sp_freq(readed, data, freq);

		/*for (int i=0; i<readed; i++){
			printf("%f ", freq[i]);
		}*/

		//fwrite(freq, sizeof(float), readed*sp_info.frequencies, stream);
	} while (readed > 0);


	m = sp_max();

	printf("Max %f\n", m);

	result = fseek( stream, 5*4, SEEK_SET);
	if( result )
		perror( "Fseek failed" );
	else
	{
		fwrite( &m, sizeof(float), 1, stream );
	}

	fclose( stream );

	sp_close();

	sf_close(sndfile);

	free(data);
	free(freq);

	scanf("\n");
}