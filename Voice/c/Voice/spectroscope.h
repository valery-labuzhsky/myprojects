extern "C"
void sp_init(int _bufFrameLength, int _freqCount, float minFreq, float maxFreq, float rate);

extern "C"
void sp_freq(int frames, float *data, float* freq);

extern "C"
void sp_ifreq(int lenght, float* freq, int* ifreq, float max);

extern "C"
float sp_max();

extern "C"
void sp_close();