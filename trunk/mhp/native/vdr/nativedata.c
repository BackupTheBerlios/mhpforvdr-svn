
#include <stdint.h>
#include <libjava/jniinterface.h>

extern "C" {

void Java_vdr_mhp_lang_NativeDataContainer_nativeFinalize(JNIEnv* env, jobject obj, jobject nativeData) {
   JNI::NativeData::Finalize(nativeData);
}

}

