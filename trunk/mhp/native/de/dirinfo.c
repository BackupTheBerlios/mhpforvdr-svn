
#include <libjava/jniinterface.h>

extern "C" {
jstring Java_vdr_mhp_DirInfo_libdir(JNIEnv* env, jclass clazz) {
   return env->NewStringUTF(MHPLIBDIR);
}

jstring Java_vdr_mhp_DirInfo_datadir(JNIEnv* env, jclass clazz) {
   return env->NewStringUTF(MHPDATADIR);
}

jstring Java_vdr_mhp_DirInfo_fontdir(JNIEnv* env, jclass clazz) {
   return env->NewStringUTF(MHPFONTDIR);
}
}

