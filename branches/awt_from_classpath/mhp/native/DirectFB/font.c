#include <libjava/jniinterface.h>
#include <dfb++/dfb++.h>
#include <libmhpoutput/output.h>
#include <vdr/tools.h>

extern "C" {

enum java_awt_font_style {
   java_awt_font_PLAIN = 0,
   java_awt_font_BOLD = 1,
   java_awt_font_ITALIC = 2
};

enum java_awt_font_baseline {
   java_awt_font_ROMAN_BASELINE = 0,
   java_awt_font_CENTER_BASELINE = 1,
   java_awt_font_HANGING_BASELINE = 2
};

static char *styles[4] = {
     "",   //plain
     "bd", //bold
     "i",  //italic
     "bi"  //bold italic
};

#define FONT_METRICS_ASCENT      0
#define FONT_METRICS_MAX_ASCENT  1
#define FONT_METRICS_DESCENT     2
#define FONT_METRICS_MAX_DESCENT 3
#define FONT_METRICS_MAX_ADVANCE 4
#define NUM_FONT_METRICS 5

#define TEXT_METRICS_X_BEARING 0
#define TEXT_METRICS_Y_BEARING 1
#define TEXT_METRICS_WIDTH     2
#define TEXT_METRICS_HEIGHT    3
#define TEXT_METRICS_X_ADVANCE 4
#define TEXT_METRICS_Y_ADVANCE 5
#define NUM_TEXT_METRICS 6

#define NUM_GLYPH_METRICS 10

#define GLYPH_LOG_X(i)      (NUM_GLYPH_METRICS * (i)    )
#define GLYPH_LOG_Y(i)      (NUM_GLYPH_METRICS * (i) + 1)
#define GLYPH_LOG_WIDTH(i)  (NUM_GLYPH_METRICS * (i) + 2)
#define GLYPH_LOG_HEIGHT(i) (NUM_GLYPH_METRICS * (i) + 3)

#define GLYPH_INK_X(i)      (NUM_GLYPH_METRICS * (i) + 4)
#define GLYPH_INK_Y(i)      (NUM_GLYPH_METRICS * (i) + 5)
#define GLYPH_INK_WIDTH(i)  (NUM_GLYPH_METRICS * (i) + 6)
#define GLYPH_INK_HEIGHT(i) (NUM_GLYPH_METRICS * (i) + 7)

#define GLYPH_POS_X(i)      (NUM_GLYPH_METRICS * (i) + 8)
#define GLYPH_POS_Y(i)      (NUM_GLYPH_METRICS * (i) + 9)

static char *defaultFont= MHPFONTDIR "/vera.ttf";
static JNI::Constructor mhpGlyphVectorConstructor;

void Java_vdr_mhp_awt_MHPFontPeer_initStaticState(JNIEnv* env, jclass clazz) {
   //public MHPGlyphVector(double[] extents, int[] codes, Font font, FontRenderContext frc)
   mhpGlyphVectorConstructor.SetConstructorWithArguments("vdr/mhp/awt/MHPGlyphVector", 4, JNI::Array, JNI::Double, JNI::Array, JNI::Int, JNI::Object, "java/awt/Font", JNI::Object, "java/awt/font/FontRenderContext");
   mhpGlyphVectorConstructor.SetExceptionHandling(JNI::DoNotClearExceptions);
}

jlong Java_vdr_mhp_awt_MHPFontPeer_setFont(JNIEnv* env, jobject obj, jstring spec, jint style, jint size) {
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
         //Throw an InternalError instead?
         JNI::Exception::Throw(JNI::JavaLangIllegalArgumentException, "Request font and default font not found");
         delete e;
         return 0;
      }
   }
   return (jlong )font;
}

void Java_vdr_mhp_awt_MHPFontPeer_removeRef(JNIEnv* env, jobject obj, jlong nativeData) {
   ((IDirectFBFont *)nativeData)->Release();
}


void Java_vdr_mhp_awt_MHPFontPeer_getFontMetrics(JNIEnv* env, jobject obj, jlong nativeData, jdoubleArray java_metrics) {
   IDirectFBFont *font = (IDirectFBFont *)nativeData;
   jdouble *native_metrics;
   //DFBRectangle logicalRectangle;
   
   if (!font) {
      JNI::Exception::Throw(JNI::JavaLangNullPointerException, "IDirectFBFont is NULL");
      return;
   }
   
   native_metrics 
         = env->GetDoubleArrayElements (java_metrics, NULL);

   native_metrics[FONT_METRICS_ASCENT] = font->GetAscender();
         // = PANGO_PIXELS (pango_font_metrics_get_ascent (pango_metrics));

   native_metrics[FONT_METRICS_MAX_ASCENT] 
          = native_metrics[FONT_METRICS_ASCENT];

   native_metrics[FONT_METRICS_DESCENT] = font->GetDescender();
         // = PANGO_PIXELS (pango_font_metrics_get_descent (pango_metrics));

   if (native_metrics[FONT_METRICS_DESCENT] < 0)
      native_metrics[FONT_METRICS_DESCENT] 
            = - native_metrics[FONT_METRICS_DESCENT];

   native_metrics[FONT_METRICS_MAX_DESCENT] 
          = native_metrics[FONT_METRICS_DESCENT];

   native_metrics[FONT_METRICS_MAX_ADVANCE] = font->GetMaxAdvance();
         //= PANGO_PIXELS (pango_font_metrics_get_approximate_char_width (pango_metrics));
	 
   env->ReleaseDoubleArrayElements (java_metrics, native_metrics, JNI_COMMIT);
}

void Java_vdr_mhp_awt_MHPFontPeer_getTextMetrics(JNIEnv* env, jobject obj, jlong nativeData, jstring str, jdoubleArray java_metrics) {
   IDirectFBFont *font = (IDirectFBFont *)nativeData;
   jdouble *native_metrics;
   const char *text;
   DFBRectangle logicalRectangle;
   
   if (!font) {
      JNI::Exception::Throw(JNI::JavaLangNullPointerException, "IDirectFBFont is NULL");
      return;
   }
   
   text=(const char *)env->GetStringUTFChars(str, NULL);
   
   //GTK's implementation uses Pango which has similar semantics,
   //such as a logical rectangle and an ink rectangle. I just do it as they did it.
   font->GetStringExtents(text, env->GetStringUTFLength(str), &logicalRectangle, NULL);
   
   native_metrics = env->GetDoubleArrayElements (java_metrics, NULL);

   native_metrics[TEXT_METRICS_X_BEARING] = (double) logicalRectangle.x;
        // = PANGO_PIXELS( ((double)log.x) );

   native_metrics[TEXT_METRICS_Y_BEARING] = (double) logicalRectangle.y;
        // = PANGO_PIXELS( ((double)log.y) );

   native_metrics[TEXT_METRICS_WIDTH] = (double) logicalRectangle.w;
        // = PANGO_PIXELS( ((double)log.width) );

   native_metrics[TEXT_METRICS_HEIGHT] = (double) logicalRectangle.h;
        // = PANGO_PIXELS( ((double)log.height) );

   native_metrics[TEXT_METRICS_X_ADVANCE] = (double) (logicalRectangle.x + logicalRectangle.w);
        // = PANGO_PIXELS( ((double) (log.x + log.width)) );

   native_metrics[TEXT_METRICS_Y_ADVANCE] = (double) (logicalRectangle.y + logicalRectangle.h);
        // = PANGO_PIXELS( ((double) (log.y + log.height)) );
	 
   env->ReleaseDoubleArrayElements (java_metrics, native_metrics, JNI_COMMIT);
}

static int utf8GetCharWidth(const char *utf) {
   //JNI UTF8 character are 1, 2 or 3 bytes wide.
   //See http://java.sun.com/docs/books/jni/html/types.html
   if ( (*utf) & 0x0800)
      return 3;
   else if ( (*utf) & 0x0080)
      return 2;
   else
      return 1;
}

static int utf8GetUnicodeIndex(const char *utf) {
   //See http://java.sun.com/docs/books/jni/html/types.html
   if ( (*utf) & 0x0800)
      return ((utf[0] & 0xf) << 12) + ((utf[1] & 0x3f) << 6) + (utf[2] & 0x3f);
   else if ( (*utf) & 0x0080)
      return ((utf[0] & 0x1f) << 6) + (utf[1] & 0x3f);
   else
      return utf[0];
}

jobject Java_vdr_mhp_awt_MHPFontPeer_getGlyphVector(JNIEnv* env, jobject obj, jlong nativeData, jstring chars, jobject java_font, jobject fontRenderContext)
{
   IDirectFBFont *font = (IDirectFBFont *)nativeData;
   int len, j, clen, cj;
   double *native_extents;
   int *native_codes;
   jintArray java_codes = NULL;
   jdoubleArray java_extents = NULL;
   const char *str;

   if (!font) {
      JNI::Exception::Throw(JNI::JavaLangNullPointerException, "IDirectFBFont is NULL");
      return 0;
   }
   
   len = env->GetStringUTFLength (chars);
   clen = env->GetStringLength (chars);
   str = env->GetStringUTFChars (chars, NULL);
   
   if (len > 0 && str[len-1] == '\0')
      len--;
  
   int x = 0;
   //double scale = ((double) PANGO_SCALE);

   java_extents = env->NewDoubleArray (clen * NUM_GLYPH_METRICS);
   java_codes = env->NewIntArray (clen);
   native_extents = env->GetDoubleArrayElements (java_extents, NULL);
   native_codes = env->GetIntArrayElements (java_codes, NULL);

   // len is the length in bytes, clen in characters - in UTF8, a character can have 1..3 bytes.
   // j and cj are the respective counters.
   for (j = 0, cj = 0; j < len; cj++ )
   {
      DFBRectangle ink;
      DFBRectangle logical;
      int byteLen = utf8GetCharWidth(str+j);
      
      font->GetStringExtents(str+j, byteLen, &logical, &ink);
      //PangoGlyphGeometry *geom = &glyphs->glyphs[j].geometry;

      //don't what code is wanted here, using unicode index
      native_codes[cj] = utf8GetUnicodeIndex(str+j);
      //native_codes[j] = glyphs->glyphs[j].glyph;

      native_extents[ GLYPH_LOG_X(cj)      ] = (logical.x);
      native_extents[ GLYPH_LOG_Y(cj)      ] = (- logical.y);
      native_extents[ GLYPH_LOG_WIDTH(cj)  ] = (logical.w);
      native_extents[ GLYPH_LOG_HEIGHT(cj) ] = (logical.h);

      native_extents[ GLYPH_INK_X(cj)      ] = (ink.x);
      native_extents[ GLYPH_INK_Y(cj)      ] = (- ink.y);
      native_extents[ GLYPH_INK_WIDTH(cj)  ] = (ink.w) ;
      native_extents[ GLYPH_INK_HEIGHT(cj) ] = (ink.h);

      //here I am very unsure as well. See the Pango implementation below,
      //I don't have these values in DirectFB.
      native_extents[ GLYPH_POS_X(cj)      ] = (x + logical.x);
      native_extents[ GLYPH_POS_Y(cj)      ] = (  - logical.y);
      x += logical.x;
      //native_extents[ GLYPH_POS_X(j)      ] = (x + geom->x_offset);
      //native_extents[ GLYPH_POS_Y(j)      ] = (  - geom->y_offset);
      //x += geom->width;
      
      //increment for loop!
      j += byteLen;
   }
   env->ReleaseDoubleArrayElements (java_extents, native_extents, JNI_COMMIT);
   env->ReleaseIntArrayElements (java_codes, native_codes, JNI_COMMIT);


   env->ReleaseStringUTFChars (chars, str);

   jobject newObj;
   mhpGlyphVectorConstructor.NewObject(newObj, java_extents, java_codes, java_font, fontRenderContext);
   return newObj;
}


/*

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
*/

}
