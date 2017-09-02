#include "sndfile.h"

typedef struct
{  int frames;
   int frequencies;
   float min_freq;
   float max_freq;
   float rate;

   int channels;
   SNDFILE* sndfile;
} SR_INFO;

extern "C"
SR_INFO * sr_open(char* soundFile, int frequencies, float min_freq, float max_freq);

extern "C"
float * sr_fread(SR_INFO* info, float& max);

extern "C"
int * sr_iread(SR_INFO* sr_info, float& max);

extern "C"
void sr_close(SR_INFO* info);