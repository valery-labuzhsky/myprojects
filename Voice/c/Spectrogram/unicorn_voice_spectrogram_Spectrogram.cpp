#include "unicorn_voice_spectrogram_Spectrogram.h"

#include <stdlib.h>
#include <spectroreader.h>

JNIEXPORT void JNICALL Java_unicorn_voice_spectrogram_Spectrogram_read
(JNIEnv * env, jobject thiz, jbyteArray sndfile, jint freq_count, jfloat min_freq, jfloat max_freq){
	jboolean isCopy;
	jbyte* filename = env->GetByteArrayElements(sndfile, &isCopy);
	SR_INFO * sr_info = sr_open((char*)filename, freq_count, min_freq, max_freq);
	if (isCopy) {
		env->ReleaseByteArrayElements(sndfile, filename, JNI_ABORT);
	}
	
	jclass Spectrogram = env->GetObjectClass(thiz);

	jclass Header = env->FindClass("unicorn/voice/spectrogram/Header");
	jmethodID newHeader = env->GetMethodID(Header, "<init>", "(IIFFF)V");
	jobject header = env->NewObject(Header, newHeader, sr_info->frames, freq_count, min_freq, max_freq);
	jfieldID this_header = env->GetFieldID(Spectrogram, "header", "Lunicorn/voice/spectrogram/Header;");
	env->SetObjectField(thiz, this_header, header);

	float max;
	int* freq = sr_iread(sr_info, max);

	jfieldID this_maximum = env->GetFieldID(Spectrogram, "maximum", "F");
	env->SetFloatField(thiz, this_maximum, max);

	int size = sr_info->frames*sr_info->frequencies;

	/*jfloatArray frequencies = env->NewFloatArray(size);
	env->SetFloatArrayRegion(frequencies, 0, size, freq);

	jfieldID this_frequencies = env->GetFieldID(Spectrogram, "data", "[F");
	env->SetObjectField(thiz, this_frequencies, frequencies);*/

	jintArray image = env->NewIntArray(size);
	env->SetIntArrayRegion(image, 0, size, (jint*)freq);

	jfieldID this_image = env->GetFieldID(Spectrogram, "image", "[I");
	env->SetObjectField(thiz, this_image, image);

	sr_close(sr_info);
	free(freq);

}