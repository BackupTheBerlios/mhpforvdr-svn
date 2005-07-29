
#include <libjava/jniinterface.h>
#include <syslog.h>
#include <sys/types.h>

#include <mhpmessages.h>

extern "C" {
void Java_vdr_mhp_Syslog_dsyslog (JNIEnv* env, jclass clazz, jstring s) {
   const char *str=(const char *)env->GetStringUTFChars(s, 0);
   syslog(LOG_DEBUG, str);
   env->ReleaseStringUTFChars(s, str);
}

void Java_vdr_mhp_Syslog_esyslog (JNIEnv* env, jclass clazz, jstring s) {
   const char *str=(const char *)env->GetStringUTFChars(s, 0);
   syslog(LOG_ERR, str);
   env->ReleaseStringUTFChars(s, str);
}

void Java_vdr_mhp_Osd_loadingFailed(JNIEnv* env, jclass clazz, jstring s) {
   MhpMessages::MhpMessages::DisplayMessage(MhpMessages::LoadingFailed);
}

void Java_vdr_mhp_Osd_startingFailed(JNIEnv* env, jclass clazz, jstring s) {
   MhpMessages::DisplayMessage(MhpMessages::StartingFailed);
}

}


