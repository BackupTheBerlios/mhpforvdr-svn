#include <libjava/jniinterface.h>
#include <dfb++/dfb++.h>
#include <libmhpoutput/output.h>
#include <vdr/tools.h>

extern "C" {

static char *styles[4] = {
     "",   //plain
     "bd", //bold
     "i",  //italic
     "bi"  //bold italic
};

static char *defaultFont= MHPFONTDIR "arial.ttf";

jlong Java_java_awt_Font_init(JNIEnv* env, jobject obj, jstring spec, jint style, jint size) {
   DFBFontDescription desc;
   
   const char *fontname=(const char *)env->GetStringUTFChars(spec, NULL);
   int len=strlen(MHPFONTDIR)+strlen(fontname)+3+4;
   char filename[len];
   sprintf(filename, "%s/%s%s%s", MHPFONTDIR, fontname, styles[style & 0x3], ".ttf");
   env->ReleaseStringUTFChars(spec, fontname);
   
   desc.flags = (DFBFontDescriptionFlags) (DFDESC_WIDTH | DFDESC_HEIGHT);
   desc.width = size * 3 / 4;
   desc.height = size;   
   
   IDirectFBFont *font;
   try {
      font=MhpOutput::System::self()->Interface()->CreateFont(filename, desc);
   } catch (DFBException *e) {
      delete e;
      try {
         font=MhpOutput::System::self()->Interface()->CreateFont(defaultFont, desc);
      } catch (DFBException *e) {
         printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
         esyslog("MHP Graphics subsystem short of panicking: No fonts found! Font path is %s", MHPFONTDIR);
         delete e;
         return 0;
      }
   }
   return (jlong )font;
}

void Java_java_awt_Font_removeRef(JNIEnv* env, jobject obj, jlong nativeData) {
   ((IDirectFBFont *)nativeData)->Release();
}





/*** FontMetrics ***/


jint Java_java_awt_FontMetrics_charWidth(JNIEnv* env, jobject obj, jlong nativeData, jchar c) {
   char *str = " ";     
   str[0] = (char)c;
     
   return ((IDirectFBFont *)nativeData)->GetStringWidth(str, -1);
}

jint Java_java_awt_FontMetrics_ascent(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((IDirectFBFont *)nativeData)->GetAscender();
}

jint Java_java_awt_FontMetrics_descent(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((IDirectFBFont *)nativeData)->GetDescender();
}

jint Java_java_awt_FontMetrics_height(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((IDirectFBFont *)nativeData)->GetHeight();
}

jint Java_java_awt_FontMetrics_maxAdvance(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((IDirectFBFont *)nativeData)->GetMaxAdvance();
}

jintArray Java_java_awt_FontMetrics_widths(JNIEnv* env, jobject obj, jlong nativeData) {
     int         n = 128;
     jintArray   widths;
     jint       *jw;
     jboolean    isCopy;
     char        str;

     widths = env->NewIntArray(n);
     jw = env->GetIntArrayElements(widths, &isCopy );

     while (n--) {
          str = n;          
          jw[n]=((IDirectFBFont *)nativeData)->GetStringWidth(&str, 1);
     }

     env->ReleaseIntArrayElements(widths, jw, 0);

     return widths;
}

jint Java_java_awt_FontMetrics_stringWidth(JNIEnv* env, jobject obj, jlong nativeData, jstring s) {
   const char *str=(const char *)env->GetStringUTFChars(s, NULL);
   int ret=((IDirectFBFont *)nativeData)->GetStringWidth(str, env->GetStringUTFLength(s));
   env->ReleaseStringUTFChars(s, str);
   return ret;
}


}
