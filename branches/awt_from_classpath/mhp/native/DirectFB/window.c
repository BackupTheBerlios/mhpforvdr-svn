#include <libjava/jniinterface.h>
#include <dfb++/dfb++.h>
#include <libmhpoutput/output.h>
#include <vdr/config.h>
#include <vdr/device.h>
#include "image.h"


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

/*** DFBWindowPeer ***/


jlong Java_vdr_mhp_awt_DFBWindowPeer_createDFBWindow(JNIEnv* env, jobject obj, jlong nativeLayer, jint x, jint y, jint width, jint height) {
     IDirectFBDisplayLayer *layer = (IDirectFBDisplayLayer *)nativeLayer;
     IDirectFBWindow       *window;
     DFBWindowDescription  desc;

     if (!layer)
        layer=MhpOutput::System::self()->GetMainLayer();

     printf("createDFBWindow %d, %d - %dx%d on layer %p ID %d\n", x, y, width, height, layer, layer->GetID());

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
          desc.caps=(DFBWindowCapabilities)0;
          DFB_ADD_WINDOW_DESC(desc.flags, DWDESC_CAPS);
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
     printf("Created window %p, size %dx%d\n", window, ww, hh);
     
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
   printf("MHPPlane_setOpacity %d\n", opacity);
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
      return;
   }
   
   env->ReleaseIntArrayElements(java_points, native_points, JNI_COMMIT);
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
      if (((IDirectFBEventBuffer *)nativeData)->GetEvent(ev)==DFB_OK) {
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
   int data[15];
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
   env->SetIntArrayRegion(eventData, 0, 1, data);
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



}

