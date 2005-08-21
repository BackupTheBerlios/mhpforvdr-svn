/*
 *  AWT on DirectFB - img.c
 *
 *    Copyright (C) 2000 Denis Oliver Kropp (convergence integrated media GmbH)
 *
 *  This file is subject to the terms and conditions of the GNU General Public
 *  License. See the file COPYING in the main directory of this archive for
 *  more details.
 */


#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <libjava/jniinterface.h>
#include <dfb++/dfb++.h>
#include <libmhpoutput/output.h>
#include "image.h"


// ----------- MHPImage ------------

jlong 
Java_vdr_mhp_awt_MHPImage_createScreenImage ( JNIEnv* env, jobject obj, jint width, jint height )
{
   DFBSurfacePixelFormat format;
   DFBSurfaceDescription desc;
   IDirectFBSurface *surface;

   printf( "Java_vdr_mhp_awt_MHPImage_createScreenImage (%dx%d)\n", width, height );

   desc.flags = (DFBSurfaceDescriptionFlags)(DSDESC_WIDTH | DSDESC_HEIGHT | DSDESC_PIXELFORMAT);

   desc.width = width;
   desc.height = height;
   desc.pixelformat = DSPF_ARGB;
      

   try {
      surface=MhpOutput::System::self()->Interface()->CreateSurface(desc);
      format=surface->GetPixelFormat();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      //What kind of exception should be thrown?
      JNI::Exception::Throw(JNI::JavaLangRuntimeException, "Failed to create DFBSurface for Image");
      return 0;
   }

   return (jlong )surface;
}

void
Java_vdr_mhp_awt_MHPImage_stretchBlit( JNIEnv* env, jobject obj,
                         jlong nativeDataDestination, jlong nativeDataSource )
{
   IDirectFBSurface *source = ((IDirectFBSurface *)nativeDataSource);
   IDirectFBSurface *destination = ((IDirectFBSurface *)nativeDataDestination);
   
   try {
      destination->SetBlittingFlags(DSBLIT_NOFX);
      destination->StretchBlit(source, 0, 0);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      //What kind of exception should be thrown?
      JNI::Exception::Throw(JNI::JavaLangRuntimeException, "Failed to StretchBlit image");
      return;
   }
}




void
Java_vdr_mhp_awt_MHPImage_freeImage( JNIEnv* env, jobject obj, jlong nativeData)
{
   printf("Java_vdr_mhp_awt_MHPImage_freeImage: surface %p\n", nativeData);
   ((IDirectFBSurface *)nativeData)->Release();
}


jint
Java_vdr_mhp_awt_MHPImage_getRGB(  JNIEnv* env, jobject obj, jlong nativeData, jint x, jint y) {
     u_int32_t             *dst;
     IDirectFBSurface      *surface = ((IDirectFBSurface *)nativeData);
     int                    pitch,ret;

     try {
      surface->Lock( DSLF_READ, (void**)&dst, &pitch );

      ret=(u_int32_t)dst[pitch/4 * y + x];

      surface->Unlock();
     } catch (DFBException *e) {
          printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
          delete e;
          return 0;
     }
    
     return ret;     
}

void
Java_vdr_mhp_awt_MHPImage_getRGBRegion(  JNIEnv* env, jobject obj, jlong nativeData, jint startX, jint startY, jint w, jint h, jintArray rgbArray, jint offset, jint scansize) {
     u_int32_t             *dst;
     IDirectFBSurface      *surface = ((IDirectFBSurface *)nativeData);
     int                    pitch;
     jboolean               isCopy;
     int                   *userArray;

     userArray=env->GetIntArrayElements(rgbArray, &isCopy);
     if (!userArray)
         return;
          
     try {
      surface->Lock( DSLF_READ, (void**)&dst, &pitch );

      for (int x=startX; x<startX+w; x++) {
         for (int y=startY; y<startY+h; y++) {
            userArray[offset + (y-startY)*scansize + (x-startX)]=(u_int32_t)dst[pitch/4 * y + x];
         }
      }
      
      surface->Unlock();
     } catch (DFBException *e) {
          env->ReleaseIntArrayElements(rgbArray, userArray, JNI_ABORT);    
          printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
          delete e;
          return;
     }
     env->ReleaseIntArrayElements(rgbArray, userArray, JNI_ABORT);    
}

void
Java_vdr_mhp_awt_MHPImage_setRGB(  JNIEnv* env, jobject obj, jlong nativeData, jint x, jint y, jint rgb) {
     u_int32_t             *dst;
     IDirectFBSurface      *surface = ((IDirectFBSurface *)nativeData);
     int                    pitch;

     try {
      surface->Lock( DSLF_WRITE, (void**)&dst, &pitch );

      dst[pitch/4 * y + x]=(u_int32_t)rgb;

      surface->Unlock();
     } catch (DFBException *e) {
         printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
         delete e;
     }
}

void
Java_vdr_mhp_awt_MHPImage_setRGBRegion(JNIEnv* env, jobject obj, jlong nativeData, jint startX, jint startY, jint w, jint h, jintArray rgbArray, jint offset, jint scansize) {
     u_int32_t             *dst;
     IDirectFBSurface      *surface = ((IDirectFBSurface *)nativeData);
     int                    pitch;
     jboolean               isCopy;
     int                   *userArray;

     userArray=env->GetIntArrayElements(rgbArray, &isCopy);
     if (!userArray)
         return;
          
     try {
      surface->Lock( DSLF_WRITE, (void**)&dst, &pitch );

      for (int x=startX; x<startX+w; x++) {
         for (int y=startY; y<startY+h; y++) {
            dst[pitch/4 * y + x]=(u_int32_t)userArray[offset + (y-startY)*scansize + (x-startX)];
         }
      }
      
      surface->Unlock();
     } catch (DFBException *e) {
          env->ReleaseIntArrayElements(rgbArray, userArray, JNI_ABORT);    
          printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
          delete e;
          return;
     }
     env->ReleaseIntArrayElements(rgbArray, userArray, JNI_COMMIT);    
}

jlong 
Java_vdr_mhp_awt_MHPImage_getSubImage(JNIEnv* env, jobject obj, jlong nativeData, jint x, jint y, jint w, jint h) {
   IDirectFBSurface      *surface = ((IDirectFBSurface *)nativeData);
   IDirectFBSurface      *newSurface = 0;
   try {
       newSurface=surface->GetSubSurface(x, y, w, h);
   } catch (DFBException *e) {
       printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
       delete e;
       return 0;
   }
   return (jlong )newSurface;
}

/************************************************************************************
 * field access
 */

/*
jint
Java_vdr_mhp_awt_MHPImage_imgGetWidth ( JNIEnv* env, jobject obj, jint nativeHandle)
{
  Image *img = (Image*) nativeHandle;

  if(!img)
     return 0;
     
    int width = -1;
    
    try {
       img->surface->GetSize( &width, NULL );
    } catch (DFBException *e) {
       printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
       delete e;
    }
    
    return width;
}

jlong 
Java_vdr_mhp_awt_MHPImage_imgGetSurface ( JNIEnv* env, jobject obj, jint nativeHandle)
{
    Image *img = (Image*) nativeHandle;

  if(!img)
     return 0;
     
    //printf("Java_vdr_mhp_awt_MHPImage_imgGetSurface: %p, control %d\n", img->surface, img->surface->GetPixelFormat());
    return (jlong ) img->surface;
}

jint
Java_vdr_mhp_awt_MHPImage_imgGetHeight ( JNIEnv* env, jobject obj, jint nativeHandle)
{
  Image *img = (Image*) nativeHandle;
  
  if(!img)
     return 0;
     
    int height = -1;

    try {    
       img->surface->GetSize( NULL, &height );
    } catch (DFBException *e) {
       printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
       delete e;
    }
    
    return height;
}
*/





// ------- DFBImageProvider --------

static JNI::InstanceMethod setPropertiesMethod;

void Java_vdr_mhp_awt_DFBImageProvider_initStaticState(JNIEnv* env, jclass clazz) {
   setPropertiesMethod.SetMethodWithArguments(clazz, "setProperties", JNI::Void, 3, JNI::Boolean, JNI::Int, JNI::Int);
   setPropertiesMethod.SetExceptionHandling(JNI::DoNotClearExceptions);
}

static bool setProperties(IDirectFBImageProvider *provider, jobject obj) {
   DFBSurfaceDescription desc;
   try {
      provider->GetSurfaceDescription(&desc);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      JNI::Exception::Throw(JNI::JavaLangIllegalArgumentException, "Failed to get image information from ImageProvider");
      delete e;
      return false;
   }
   
   JNI::ReturnType type;
   return setPropertiesMethod.CallMethod(obj, type, true, desc.width, desc.height);
}

jlong Java_vdr_mhp_awt_DFBImageProvider_createImageProviderFromFile(JNIEnv* env, jobject obj, jbyteArray filename) //throws IllegalArgumentException
{
   IDirectFBImageProvider *provider = 0;
   DFBSurfaceDescription desc;
   const char* fn = 0;

   fn = (const char *)env->GetByteArrayElements(filename, 0);
   //fn = env->GetStringUTFChars(fileName, NULL);

   printf( "Java_vdr_mhp_awt_DFBImageProvider_createImageProviderFromFile(\"%s\") called.\n", fn );

   try {
      provider=MhpOutput::System::self()->Interface()->CreateImageProvider(fn);
   } catch (DFBException *e) {
      fprintf( stderr, "Unable to create the "
               "Media Provider for `%s': %s", fn, e->GetResult() );
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      env->ReleaseByteArrayElements(filename, (jbyte *)fn, JNI_ABORT);
      char *msg;
      asprintf(&msg, "Error trying to create ImageProvider from file %s", fn);
      JNI::Exception::Throw(JNI::JavaLangIllegalArgumentException, "Failed to get image information from ImageProvider");
      free(msg);
      delete e;
      return 0;
   }
   
   setProperties(provider, obj);

   return (jlong)provider;
}

jlong Java_vdr_mhp_awt_DFBImageProvider_createImageProviderFromDataBuffer(JNIEnv* env, jobject obj, jlong nativeBufferData) //throws IllegalArgumentException;
{
   IDirectFBImageProvider *provider = 0;
   IDirectFBDataBuffer *buffer = (IDirectFBDataBuffer *)nativeBufferData;
   DFBSurfaceDescription desc;
   
   if (!buffer) {
      JNI::Exception::Throw(JNI::JavaLangIllegalArgumentException, "Invalid data buffer");
      return 0;
   }
   
   try {
      provider=buffer->CreateImageProvider();
   } catch (DFBException *e) {
      fprintf( stderr, "Unable to create image provider from data buffer: %s", e->GetResult() );
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      JNI::Exception::Throw(JNI::JavaLangIllegalArgumentException, "Failed to create ImageProvider from DataBuffer");
      delete e;
      return 0;
   }
   
   setProperties(provider, obj);

   return (jlong)provider;
}

void Java_vdr_mhp_awt_DFBImageProvider_renderTo(JNIEnv* env, jobject obj, jlong nativeProviderData, jlong nativeImageData) {
   IDirectFBImageProvider *provider = (IDirectFBImageProvider *)nativeProviderData;
   IDirectFBSurface *surface = (IDirectFBSurface *)nativeImageData;
   
   try {
      provider->RenderTo(surface, NULL);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      JNI::Exception::Throw(JNI::JavaLangIllegalArgumentException, "Failed to render image");
      delete e;
   }
}

void Java_vdr_mhp_awt_DFBImageProvider_removeRef(jlong nativeData) {
   IDirectFBImageProvider *provider = (IDirectFBImageProvider *)nativeData;
   provider->Release();
}




// ------------ DFBDataBuffer ------------


jlong Java_vdr_mhp_awt_DFBDataBuffer_createBufferFromFile(JNIEnv* env, jobject obj, jbyteArray filename) //throws IOException;
{
   IDirectFBDataBuffer *buffer;
   DFBDataBufferDescription bufDesc;
   const char* fn;

   fn = (const char *)env->GetByteArrayElements(filename, 0);
   bufDesc.flags=DBDESC_FILE;
   bufDesc.file=fn;

   try {
      buffer=MhpOutput::System::self()->Interface()->CreateDataBuffer(bufDesc);
   } catch (DFBException *e) {
      fprintf( stderr, "Unable to create the Data buffer: %s", e->GetResult() );
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      env->ReleaseByteArrayElements(filename, (jbyte *)fn, JNI_ABORT);
      JNI::Exception::Throw(JNI::JavaIoIOException, "Unable to create data buffer for filename");
      delete e;
      return 0;
   }
   
   env->ReleaseByteArrayElements(filename, (jbyte *)fn, JNI_ABORT);
   
   return (jlong)buffer;
}

jlong Java_vdr_mhp_awt_DFBDataBuffer_createBufferFromData(JNIEnv* env, jobject obj, jbyteArray jbuffer, jint off, jint len) //throws IOException;
{
   int       n;
   jboolean  isCopy;
   jbyte     *jcomplete, *joffset;

   n = env->GetArrayLength(jbuffer);                             // length
   if ( off+len > n )
      len = n - off;
   
   if (len <= 0 || off < 0) {
      JNI::Exception::Throw(JNI::JavaLangIllegalArgumentException, "Invalid length/offset parameter");
      return 0;
   }
   
   jcomplete = env->GetByteArrayElements(jbuffer, &isCopy);      // complete copy
   joffset = jcomplete + off;                                    // copy after +offset

   if (jcomplete == NULL) {
         env->ReleaseByteArrayElements(jbuffer, jcomplete, JNI_ABORT);
         JNI::Exception::Throw(JNI::JavaLangIllegalArgumentException, "Invalid byte array");
         return 0;
   }

   
   IDirectFBDataBuffer *buffer;
   DFBDataBufferDescription bufDesc;
   bufDesc.flags=DBDESC_MEMORY;
   bufDesc.memory.data=joffset;
   bufDesc.memory.length=len;

   try {
      buffer=MhpOutput::System::self()->Interface()->CreateDataBuffer(bufDesc);
   } catch (DFBException *e) {
      fprintf( stderr, "Unable to create the Data buffer: %s", e->GetResult() );
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      env->ReleaseByteArrayElements(jbuffer, jcomplete, JNI_ABORT);
      JNI::Exception::Throw(JNI::JavaIoIOException, "Unable to create data buffer");
      delete e;
      return 0;
   }
   
   env->ReleaseByteArrayElements(jbuffer, jcomplete, JNI_ABORT);
   
   return (jlong)buffer;
}

jlong Java_vdr_mhp_awt_DFBDataBuffer_createBufferForStreaming(JNIEnv* env, jobject obj) //throws IOException;
{
   //TODO: DFB++/DirectFB is broken in this aspect. Fix this, then reenable this code.
/*
   IDirectFBDataBuffer *buffer;
   
   try {
      buffer=MhpOutput::System::self()->Interface()->CreateDataBuffer(bufDesc);
   } catch (DFBException *e) {
      fprintf( stderr, "Unable to create the Data buffer: %s", e->GetResult() );
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      Exception exp;
      exp.Throw("java/io/IOException", "Unable to create streaming data buffer");
      delete e;
      return 0;
   }
   
   env->ReleaseByteArrayElements(fileName, (jbyte *)fn, JNI_ABORT);
   
   return buffer;
   */
}

void Java_vdr_mhp_awt_DFBDataBuffer_putData(JNIEnv* env, jobject obj, jlong nativeData, jbyteArray data, jint len) {
   jbyte *d = env->GetByteArrayElements(data, 0);
   ((IDirectFBDataBuffer *)nativeData)->PutData(d, len);
   env->ReleaseByteArrayElements(data, (jbyte *)d, JNI_ABORT);
}

void Java_vdr_mhp_awt_DFBDataBuffer_removeRef(JNIEnv* env, jobject obj, jlong nativeData) {
   ((IDirectFBDataBuffer *)nativeData)->Release();
}


