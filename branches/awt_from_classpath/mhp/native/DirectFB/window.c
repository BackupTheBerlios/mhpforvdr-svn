#include <libjava/jniinterface.h>
#include <dfb++/dfb++.h>
#include <libmhpoutput/output.h>
#include <vdr/config.h>
#include <vdr/device.h>
#include "awtconstants.h"

static int translateDFBInputDeviceKeyIdentifierToAWTVirtualKeyConstant(DFBInputDeviceKeyIdentifier id);

extern "C" {

/*** MHPScreen ***/

jlong Java_java_awt_MHPScreen_getMainLayer(JNIEnv* env, jclass clazz) {
   return (jlong )MhpOutput::System::self()->GetMainLayer();
}

jlong Java_java_awt_MHPScreen_getVideoLayer(JNIEnv* env, jclass clazz) {
   return (jlong )MhpOutput::System::self()->GetVideoLayer();
}

jboolean Java_java_awt_MHPScreen_hasVideoLayer(JNIEnv* env, jclass clazz) {
   return MhpOutput::System::self()->HasVideoLayer();
}

jlong Java_java_awt_MHPScreen_getBackgroundLayer(JNIEnv* env, jclass clazz) {
   return (jlong )MhpOutput::System::self()->GetBackgroundLayer();
}

jboolean Java_java_awt_MHPScreen_hasBackgroundLayer(JNIEnv* env, jclass clazz) {
   return MhpOutput::System::self()->HasBackgroundLayer();
}

/*** MHPBackgroundLayer ***/

enum MHPBackgroundLayerMode {
   MODE_DONTCARE = 0,
   MODE_COLOR = 1,
   MODE_IMAGESTRETCH = 2,
   MODE_IMAGETILE = 3,
};

void Java_java_awt_MHPBackgroundLayer_setLayerBackgroundMode(JNIEnv* env, jobject obj, long nativeLayer, int mode) {
   IDirectFBDisplayLayer *layer=(IDirectFBDisplayLayer *)nativeLayer;
   try {
      switch ((MHPBackgroundLayerMode)mode) {
         case MODE_DONTCARE:
            layer->SetBackgroundMode(DLBM_DONTCARE);
            break;
         case MODE_IMAGESTRETCH:
            layer->SetBackgroundMode(DLBM_IMAGE);
            break;
         case MODE_IMAGETILE:
            layer->SetBackgroundMode(DLBM_TILE);
            break;
         default:
         case MODE_COLOR:
            layer->SetBackgroundMode(DLBM_COLOR);
            break;
      }
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }
}

void Java_java_awt_MHPBackgroundLayer_setLayerBackgroundColor(JNIEnv* env, jobject obj, long nativeLayer, int r, int g, int b, int a) {
   try {
      ((IDirectFBDisplayLayer *)nativeLayer)->SetBackgroundColor(r, g, b, a);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }
}

void Java_java_awt_MHPBackgroundLayer_setLayerBackgroundImage(JNIEnv* env, jobject obj, long nativeLayer, long nativeSurface) {
   try {
      ((IDirectFBDisplayLayer *)nativeLayer)->SetBackgroundImage((IDirectFBSurface *)nativeSurface);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }
}

/*** DFBWindowPeer ***/


jlong Java_vdr_mhp_awt_DFBWindowPeer_createDFBWindow(JNIEnv* env, jobject obj, jlong nativeLayer, jint x, jint y, jint width, jint height) {
     IDirectFBDisplayLayer *layer = (IDirectFBDisplayLayer *)nativeLayer;
     IDirectFBWindow       *window;
     DFBWindowDescription  desc;

     if (!layer)
        layer=MhpOutput::System::self()->GetMainLayer();

     //printf("createDFBWindow %d, %d - %dx%d on layer %p ID %d\n", x, y, width, height, layer, layer->GetID());

     /*if (getenv( "MHP_NO_ALPHA" ))
          bgAlpha = 255;

     if (bgAlpha < 255) {
          desc.flags = DWDESC_CAPS;
          desc.caps  = DWCAPS_ALPHACHANNEL;
     }
     else
          desc.flags = 0;*/
          
     desc.flags=(DFBWindowDescriptionFlags)0;
     
     if (!getenv( "MHP_NO_ALPHA" )) {
          DFB_ADD_WINDOW_DESC(desc.flags, DWDESC_CAPS);
          desc.caps=DWCAPS_NONE;
          DFB_ADD_WINDOW_CAPS(desc.caps, DWCAPS_ALPHACHANNEL);
          DFB_ADD_WINDOW_CAPS(desc.caps, DWCAPS_DOUBLEBUFFER);
     }


     if (x >= 0) {
          DFB_ADD_WINDOW_DESC(desc.flags, DWDESC_POSX);
          desc.posx = x;
     }
     if (y >= 0) {
          DFB_ADD_WINDOW_DESC(desc.flags, DWDESC_POSY);
          desc.posy = y;
     }
     if (width > 0) {
          DFB_ADD_WINDOW_DESC(desc.flags, DWDESC_WIDTH);
          desc.width = width;
     }
     if (height > 0) {
          DFB_ADD_WINDOW_DESC(desc.flags, DWDESC_HEIGHT);
          desc.height = height;
     }
     

     try {
          window=layer->CreateWindow(desc);
     } catch (DFBException *e) {
         printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
         delete e;
         return 0;
     }
      int ww,hh;window->GetSize(&ww, &hh);
     //printf("Created window %p, size %dx%d\n", window, ww, hh);
     
     return (jlong )window;
}

jlong Java_vdr_mhp_awt_DFBWindowPeer_attachEventBuffer(JNIEnv* env, jobject obj, jlong nativeData) {
   try {
      return (jlong ) ((IDirectFBWindow *)nativeData)->CreateEventBuffer();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return 0;
   }   
}

void Java_vdr_mhp_awt_DFBWindowPeer_requestFocus(JNIEnv* env, jobject obj, jlong nativeData) {
   try {
      printf("Request Focus: Checking args %d %d %d\n", ((IDirectFBWindow *)nativeData)->GetOptions(), (((IDirectFBWindow *)nativeData)->GetOptions() & DWOP_GHOST), ((IDirectFBWindow *)nativeData)->GetOpacity());
      ((IDirectFBWindow *)nativeData)->RequestFocus();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }
}

jlong Java_vdr_mhp_awt_DFBWindowPeer_getSurface(JNIEnv* env, jobject obj, jlong nativeData) {
   try {
      return (jlong ) ((IDirectFBWindow *)nativeData)->GetSurface();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return 0;
   }
}

//static final int STACKING_LOWER  = 0;
//static final int STACKING_MIDDLE = 1;
//static final int STACKING_UPPER  = 2;
void Java_vdr_mhp_awt_DFBWindowPeer_setStackingClass(JNIEnv* env, jobject obj, jlong nativeData, jint stacking) {
   try {
      switch (stacking) {
         case 0:
            ((IDirectFBWindow *)nativeData)->SetStackingClass(DWSC_LOWER);
            break;
         case 1:
            ((IDirectFBWindow *)nativeData)->SetStackingClass(DWSC_MIDDLE);
            break;
         case 2:
            ((IDirectFBWindow *)nativeData)->SetStackingClass(DWSC_UPPER);
            break;
      }
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
}


void Java_vdr_mhp_awt_DFBWindowPeer_setOpacity(JNIEnv* env, jobject obj, jlong nativeData, jint opacity) {
   //printf("MHPPlane_setOpacity %d\n", opacity);
   try {
      ((IDirectFBWindow *)nativeData)->SetOpacity(opacity);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
}

jint Java_vdr_mhp_awt_DFBWindowPeer_getOpacity(JNIEnv* env, jobject obj, jlong nativeData) {
   try {
      return ((IDirectFBWindow *)nativeData)->GetOpacity();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return 0;
   }
}

void Java_vdr_mhp_awt_DFBWindowPeer_raise(JNIEnv* env, jobject obj, jlong nativeData) {
   try {
      return ((IDirectFBWindow *)nativeData)->Raise();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
}

void Java_vdr_mhp_awt_DFBWindowPeer_lower(JNIEnv* env, jobject obj, jlong nativeData) {
   try {
      return ((IDirectFBWindow *)nativeData)->Lower();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
}

void Java_vdr_mhp_awt_DFBWindowPeer_raiseToTop(JNIEnv* env, jobject obj, jlong nativeData) {
   try {
      return ((IDirectFBWindow *)nativeData)->RaiseToTop();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
}

void Java_vdr_mhp_awt_DFBWindowPeer_lowerToBottom(JNIEnv* env, jobject obj, jlong nativeData) {
   try {
      return ((IDirectFBWindow *)nativeData)->LowerToBottom();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
}

void Java_vdr_mhp_awt_DFBWindowPeer_putAtop(JNIEnv* env, jobject obj, jlong nativeData, jlong otherNativeData) {
   try {
      return ((IDirectFBWindow *)nativeData)->PutAtop((IDirectFBWindow *)otherNativeData);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
}

void Java_vdr_mhp_awt_DFBWindowPeer_putBelow(JNIEnv* env, jobject obj, jlong nativeData, jlong otherNativeData) {
   try {
      return ((IDirectFBWindow *)nativeData)->PutBelow((IDirectFBWindow *)otherNativeData);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
}

void Java_vdr_mhp_awt_DFBWindowPeer_getPosition(JNIEnv* env, jobject obj, jlong nativeData, jintArray java_points) {
   int *native_points;
   
   native_points = env->GetIntArrayElements(java_points, NULL);
   
   try {
      return ((IDirectFBWindow *)nativeData)->GetPosition(&native_points[0], &native_points[1]);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }
   
   env->ReleaseIntArrayElements(java_points, native_points, 0);
}

void Java_vdr_mhp_awt_DFBWindowPeer_getSize(JNIEnv* env, jobject obj, jlong nativeData, jintArray java_size) {
   int *native_size;
   
   native_size = env->GetIntArrayElements(java_size, NULL);
   
   try {
      return ((IDirectFBWindow *)nativeData)->GetSize(&native_size[0], &native_size[1]);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }
   
   env->ReleaseIntArrayElements(java_size, native_size, 0);
}

void Java_vdr_mhp_awt_DFBWindowPeer_setSize(JNIEnv* env, jobject obj, jlong nativeData, jint width, jint height) {
   try {
      return ((IDirectFBWindow *)nativeData)->Resize(width, height);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
}

void Java_vdr_mhp_awt_DFBWindowPeer_moveTo(JNIEnv* env, jobject obj, jlong nativeData, jint x, jint y) {
   try {
      return ((IDirectFBWindow *)nativeData)->MoveTo(x, y);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
}

/*void Java_java_awt_MHPScreen_lowerToBottom(JNIEnv* env, jobject obj, jlong nativeData) {
   try {
      ((IDirectFBWindow *)nativeData)->LowerToBottom();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      return 0;
   }
}

void Java_java_awt_MHPScreen_putAtop(JNIEnv* env, jobject obj, jlong nativeData, jlong lowerNativeData) {
   try {
      ((IDirectFBWindow *)nativeData)->PutAtop((IDirectFBWindow *)lowerNativeData);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      return 0;
   }
}*/

void Java_vdr_mhp_awt_DFBWindowPeer_destroy(JNIEnv* env, jobject obj, jlong nativeData) {
   try {
      ((IDirectFBWindow *)nativeData)->Destroy();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }
}

void Java_vdr_mhp_awt_DFBWindowPeer_removeRefs(JNIEnv* env, jobject obj, jlong nativeWindow, jlong nativeEventBuffer) {
   try {
      if (nativeWindow)
         ((IDirectFBWindow *)nativeWindow)->Release();
      if (nativeEventBuffer)
         ((IDirectFBEventBuffer *)nativeEventBuffer)->Release();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }
}

//The $, unicode 0x24, is mangled to _00024
jlong Java_vdr_mhp_awt_DFBWindowPeer_00024EventThread_allocateEvent(JNIEnv* env, jobject obj) {
   return (jlong ) new DFBEvent;
}

void Java_vdr_mhp_awt_DFBWindowPeer_00024EventThread_deleteEvent(JNIEnv* env, jobject obj, jlong nativeEvent) {
   delete (DFBEvent *)nativeEvent;
}

void Java_vdr_mhp_awt_DFBWindowPeer_00024EventThread_waitForEvent(JNIEnv* env, jobject obj, jlong nativeData) {
   try {
      ((IDirectFBEventBuffer *)nativeData)->WaitForEvent();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }
}

jboolean Java_vdr_mhp_awt_DFBWindowPeer_00024EventThread_getEvent(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeEvent) {
   try {
      DFBEvent *ev=(DFBEvent *)nativeEvent;
      if ( ((IDirectFBEventBuffer *) nativeData)->GetEvent(ev) ) {
         if (ev->clazz == DFEC_WINDOW)
            return true;
      }
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }
   return false;
}

//simple copying from native structure to Java array
void Java_vdr_mhp_awt_DFBWindowPeer_00024EventThread_fillEventInformation(JNIEnv* env, jobject obj, jlong nativeEvent, jintArray eventData) {
   jint data[16];
   DFBWindowEvent *e=(DFBWindowEvent *)nativeEvent;
   data[0]=e->type;
   data[1]=e->x;
   data[2]=e->y;
   data[3]=e->cx;
   data[4]=e->cy;
   data[5]=e->step;
   data[6]=e->w;
   data[7]=e->h;
   data[8]=e->key_id;
   data[9]=e->key_symbol;
   data[10]=e->modifiers;
   data[11]=e->button;
   data[12]=e->buttons;
   data[13]=e->timestamp.tv_sec;
   data[14]=e->timestamp.tv_usec;
   data[15]=translateDFBInputDeviceKeyIdentifierToAWTVirtualKeyConstant(e->key_id);
   env->SetIntArrayRegion(eventData, 0, 16, data);
}

jlong Java_vdr_mhp_awt_DFBWindowPeer_$EventThread_allocateEvent(JNIEnv* env, jobject obj) {
   return Java_vdr_mhp_awt_DFBWindowPeer_00024EventThread_allocateEvent(env, obj);
}
void Java_vdr_mhp_awt_DFBWindowPeer_$EventThread_deleteEvent(JNIEnv* env, jobject obj, jlong nativeEvent) {
   return Java_vdr_mhp_awt_DFBWindowPeer_00024EventThread_deleteEvent(env, obj, nativeEvent);
}
void Java_vdr_mhp_awt_DFBWindowPeer_$EventThread_waitForEvent(JNIEnv* env, jobject obj, jlong nativeData) {
   return Java_vdr_mhp_awt_DFBWindowPeer_00024EventThread_waitForEvent(env, obj, nativeData);
}
jboolean Java_vdr_mhp_awt_DFBWindowPeer_$EventThread_getEvent(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeEvent) {
   return Java_vdr_mhp_awt_DFBWindowPeer_00024EventThread_getEvent(env, obj, nativeData, nativeEvent);
}
void Java_vdr_mhp_awt_DFBWindowPeer_$EventThread_fillEventInformation(JNIEnv* env, jobject obj, jlong nativeEvent, jintArray eventData) {
   return Java_vdr_mhp_awt_DFBWindowPeer_00024EventThread_fillEventInformation(env, obj, nativeEvent, eventData);
}
/*** MHPScreen ***/

//0 => 4:3
//1 => 16:9
jint Java_java_awt_MHPScreen_aspectRatio(JNIEnv* env, jclass clazz) {
   return MhpOutput::System::self()->GetVideoFormat();
}


jint Java_java_awt_MHPScreen_getDeviceResolutionX(JNIEnv* env, jclass clazz) {
   return MhpOutput::System::self()->GetDisplayWidth();
}

jint Java_java_awt_MHPScreen_getDeviceResolutionY(JNIEnv* env, jclass clazz) {
   return MhpOutput::System::self()->GetDisplayHeight();
}

void Java_java_awt_MHPScreen_waitIdle(JNIEnv* env, jclass clazz) {
   MhpOutput::System::self()->Interface()->WaitIdle();
}

/*
void Java_java_awt_MHPBackgroundPlane_displayDripfeed(JNIEnv* env, jobject obj, jlong nativeSurface, jbyteArray d) {
   jsize count=env->GetArrayLength(d);
   jbyte *data=env->GetByteArrayElements(d, 0);
   IDirectFBDataBuffer *buffer;
   IDirectFBImageProvider *provider;
   
   try {
      DFBDataBufferDescription desc = { DBDESC_MEMORY, 0, data, count };
      buffer = MhpOutput::System::self()->Interface()->CreateDataBuffer(desc);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      env->ReleaseByteArrayElements(d, data, JNI_ABORT);
      ((IDirectFBSurface *)nativeSurface)->Release();
      return;
   }
   
   try {
      provider=buffer->CreateImageProvider();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s. Probably dripfeed format was not recognized.\n", e->GetAction(), e->GetResult());
      buffer->Release();
      env->ReleaseByteArrayElements(d, data, JNI_ABORT);
      ((IDirectFBSurface *)nativeSurface)->Release();
      delete e;
      return;
   }
   
   try {
      DFBSurfaceDescription desc;
      provider->GetSurfaceDescription(&desc);
      DFBRectangle rect = { 0, 0, desc.width, desc.height };
      
      //finally draw
      provider->RenderTo(((IDirectFBSurface *)nativeSurface), &rect);
      
      ((IDirectFBSurface *)nativeSurface)->Flip();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }
   
   provider->Release();
   buffer->Release();
   env->ReleaseByteArrayElements(d, data, JNI_ABORT);
   ((IDirectFBSurface *)nativeSurface)->Release();
}
*/


} // extern "C"

int translateDFBInputDeviceKeyIdentifierToAWTVirtualKeyConstant(DFBInputDeviceKeyIdentifier id) {
   if (DIKI_A <= id && id <= DIKI_Z)
      return VK_A + (id - DIKI_A);
   if (DIKI_0 <= id && id <= DIKI_9)
      return VK_0 + (id - DIKI_0);
   if (DIKI_F1 <= id && id <= DIKI_F12)
      return VK_F1 + (id - DIKI_F1);
   
   switch (id) {
      case DIKI_SHIFT_L:
      case DIKI_SHIFT_R:
         return VK_SHIFT;
      case DIKI_CONTROL_L:
      case DIKI_CONTROL_R:
         return VK_CONTROL;
      case DIKI_ALT_L:
      case DIKI_ALT_R:
         return VK_ALT;
      case DIKI_ALTGR:
         return VK_ALT_GRAPH;
      case DIKI_META_L:
      case DIKI_META_R:
         return VK_META;
      case DIKI_CAPS_LOCK: return VK_CAPS_LOCK;
      case DIKI_NUM_LOCK: return VK_NUM_LOCK;
      case DIKI_SCROLL_LOCK: return VK_SCROLL_LOCK;

      case DIKI_ESCAPE: return VK_ESCAPE;
      case DIKI_LEFT: return VK_LEFT;
      case DIKI_RIGHT: return VK_RIGHT;
      case DIKI_UP: return VK_UP;
      case DIKI_DOWN: return VK_DOWN;
      case DIKI_TAB: return VK_TAB;
      case DIKI_ENTER: return VK_ENTER;
      case DIKI_SPACE: return VK_SPACE;
      case DIKI_BACKSPACE: return VK_BACK_SPACE;
      case DIKI_INSERT: return VK_INSERT;
      case DIKI_DELETE: return VK_DELETE;
      case DIKI_HOME: return VK_HOME;
      case DIKI_END: return VK_END;
      case DIKI_PAGE_UP: return VK_PAGE_UP;
      case DIKI_PAGE_DOWN: return VK_PAGE_DOWN;
      case DIKI_PRINT: return VK_PRINTSCREEN;
      case DIKI_PAUSE: return VK_PAUSE;
      
      // DirectFB says: "The labels on these keys depend on the type of keyboard.
      //  We've choosen the names from a US keyboard layout. The
      //  comments refer to the ISO 9995 terminology."
      // After the ISO labels are the produced characters on a German keyboard
      // I do not quite know what to do with this, especially where German label differs
      // completely from American layout.
      case DIKI_QUOTE_LEFT: return VK_CIRCUMFLEX;           // TLDE: ^°¬
      case DIKI_MINUS_SIGN: return VK_MINUS;                // AE11: ß?\
      case DIKI_EQUALS_SIGN: return VK_EQUALS;              // AE12: ´`¸
      case DIKI_BRACKET_LEFT: return VK_OPEN_BRACKET;       // AD11: üÜ
      case DIKI_BRACKET_RIGHT: return VK_CLOSE_BRACKET;     //AD12: +*~
      case DIKI_BACKSLASH: return VK_BACK_SLASH;            //BKSL: #'`
      case DIKI_SEMICOLON: return VK_SEMICOLON;             //AC10: öÖ?
      case DIKI_QUOTE_RIGHT: return VK_QUOTE;               //AC11: äÄ^
      case DIKI_COMMA: return VK_COMMA;                     //AB08: ,;?
      case DIKI_PERIOD: return VK_PERIOD;                   //AB09: .:·
      case DIKI_SLASH: return VK_SLASH;                     //AB10: -_
      case DIKI_LESS_SIGN: return VK_LESS;

      case DIKI_KP_DIV: return VK_DIVIDE;
      case DIKI_KP_MULT: return VK_MULTIPLY;
      case DIKI_KP_MINUS: return VK_SUBTRACT;
      case DIKI_KP_PLUS: return VK_ADD;
      case DIKI_KP_ENTER: return VK_ENTER;
      case DIKI_KP_SPACE: return VK_SPACE;
      case DIKI_KP_TAB: return VK_TAB;
      case DIKI_KP_F1: return VK_F1;
      case DIKI_KP_F2: return VK_F2;
      case DIKI_KP_F3: return VK_F3;
      case DIKI_KP_F4: return VK_F4;
      case DIKI_KP_EQUAL: return VK_EQUALS;
      case DIKI_KP_SEPARATOR: return VK_SEPARATOR;

      case DIKI_KP_DECIMAL: return VK_DECIMAL;
      case DIKI_KP_0: return VK_NUMPAD0;
      case DIKI_KP_1: return VK_NUMPAD1;
      case DIKI_KP_2: return VK_NUMPAD2;
      case DIKI_KP_3: return VK_NUMPAD3;
      case DIKI_KP_4: return VK_NUMPAD4;
      case DIKI_KP_5: return VK_NUMPAD5;
      case DIKI_KP_6: return VK_NUMPAD6;
      case DIKI_KP_7: return VK_NUMPAD7;
      case DIKI_KP_8: return VK_NUMPAD8;
      case DIKI_KP_9: return VK_NUMPAD9;
      
      case DIKI_SUPER_L:
      case DIKI_SUPER_R:
      case DIKI_HYPER_L:
      case DIKI_HYPER_R:
      case DIKI_UNKNOWN:
      default:
         break;
   }
   return VK_UNDEFINED;
}
