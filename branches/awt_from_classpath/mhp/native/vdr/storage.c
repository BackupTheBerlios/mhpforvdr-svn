#include <libjava/jniinterface.h>
#include <string.h>

#include <vdr/config.h>

#include <mhpmessages.h>

extern "C" {

jbyteArray copyConstCharIntoByteArray(JNIEnv* env, const char *str);

jbyteArray Java_org_dvb_user_UserPreferenceManager_getSettingsFilePath(JNIEnv* env, jobject obj) {
   char file[strlen(Mhp::ConfigPath)+14];
   sprintf(file, "%s%s", Mhp::ConfigPath, "/preferences");
   return copyConstCharIntoByteArray(env, file);
}

jbyteArray Java_org_dvb_user_UserPreferenceManager_getVDRLanguage(JNIEnv* env, jobject obj) {
   const char *code=I18nLanguageCode(Setup.OSDLanguage);
   if (strlen(code)>3) {
      //only return first of several possible codes
      char threeLetter[4];
      memcpy(threeLetter, code, 3);
      threeLetter[3]=0;
      return copyConstCharIntoByteArray(env, threeLetter);
   }
   return copyConstCharIntoByteArray(env, code);
}


}

