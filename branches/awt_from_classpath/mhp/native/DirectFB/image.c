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

#if 0
Image::Image()
 : surface(0), hasalpha(0), left(0), top(0),
   latency(0), frame(0), next(0)
{
   memset(&desc, 0, sizeof(desc));
}

extern "C" {
 
jlong 
Java_vdr_mhp_awt_MHPImage_imgCreateImage ( JNIEnv* env, jobject obj, jint width, jint height )
{
   DFBSurfaceDescription desc;
   IDirectFBSurface *surface;
   Image *img;

  //printf( "Java_vdr_mhp_awt_MHPImage_imgCreateImage(%i, %i) called.\n", width, height );


   desc.flags = (DFBSurfaceDescriptionFlags)(DSDESC_WIDTH | DSDESC_HEIGHT);// | DSDESC_PIXELFORMAT;
   desc.width = width;
   desc.height = height;
   //    desc.pixelformat = DSPF_ARGB;

   try {
      surface=MhpOutput::System::self()->Interface()->CreateSurface(desc);
   } catch (DFBException *e) {
      return 0;
   }

   img = new Image;

   img->surface = surface;
   img->hasalpha = 0;

   return (jlong ) img;
}


jlong 
Java_vdr_mhp_awt_MHPImage_imgCreateScreenImage ( JNIEnv* env, jobject obj, jint width, jint height )
{
    DFBSurfacePixelFormat format;
    DFBSurfaceDescription desc;
    IDirectFBSurface *surface;
    Image *img;

    printf( "Java_vdr_mhp_awt_MHPImage_imgCreateScreenImage (%dx%d)\n", width, height );

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
      Exception exp;
      exp.Throw("java/lang/RuntimeException", "Failed to create DFBSurface for Image");
      return 0;
   }
   

   img = new Image;   
   img->surface = surface;
    
   switch (format) 
   {
      case DSPF_ARGB:
      case DSPF_A8:
         img->hasalpha = 1;
         break;
      default:
         img->hasalpha = 0;
         break;
   }
    
    
    printf( "Java_vdr_mhp_awt_MHPImage_imgCreateScreenImage: created an image (%dx%d): %p with surface %p, pixelformat %d, check %d\n", 
          width, height, img, img->surface, format, img->surface->GetPixelFormat());

    return (jlong ) img;
}



void
Java_vdr_mhp_awt_MHPImage_imgFreeImage( JNIEnv* env, jobject obj, jint nativeHandle)
{
  Image *img = (Image*) nativeHandle;
  printf("Java_vdr_mhp_awt_MHPImage_imgFreeImage: img %p, surface %p\n", img, img->surface);
  
  if(!img)
     return;          

  img->surface->Release();
  
  delete img;
}


jlong 
Java_vdr_mhp_awt_MHPImage_imgCreateScaledImage ( JNIEnv* env, jobject obj,
                         jint nativeHandle, int width, int height )
{
   
  //TODO!!
  return 0;
}


jlong 
Java_vdr_mhp_awt_MHPImage_imgCreateFromFileLocalEncoding ( JNIEnv* env, jobject obj, jbyteArray fileName )
{
    DFBSurfaceDescription desc;
    IDirectFBImageProvider *provider;
    IDirectFBSurface *surface;
    Image *img;
    const char* fn;

    fn = (const char *)env->GetByteArrayElements(fileName, 0);
    //fn = env->GetStringUTFChars(fileName, NULL);

    printf( "Java_vdr_mhp_awt_MHPImage_imgCreateFromFile(\"%s\") called.\n", fn );

    try {
       provider=MhpOutput::System::self()->Interface()->CreateImageProvider(fn);
    } catch (DFBException *e) {
        fprintf( stderr, "Unable to create the "
                 "Media Provider for `%s': %s", fn, e->GetResult() );
       printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
       env->ReleaseByteArrayElements(fileName, (jbyte *)fn, JNI_ABORT);
       delete e;
       return 0;
    }
        
    try {
       provider->GetSurfaceDescription(&desc);
    } catch (DFBException *e) {
       provider->Release();
       env->ReleaseByteArrayElements(fileName, (jbyte *)fn, JNI_ABORT);
       printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
       delete e;
       return 0;
    }

    try {
       surface=MhpOutput::System::self()->Interface()->CreateSurface(desc);
    } catch (DFBException *e) {
       provider->Release();
       env->ReleaseByteArrayElements(fileName, (jbyte *)fn, JNI_ABORT);
       printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
       delete e;
       return 0;
    }

    img = new Image;
    img->surface = surface;
    
    try {
       provider->RenderTo(surface, NULL);
       provider->GetImageDescription(&img->desc);
    } catch (DFBException *e) {
      provider->Release();
      env->ReleaseByteArrayElements(fileName, (jbyte *)fn, JNI_ABORT);
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
    }

    
    provider->Release();    
    env->ReleaseByteArrayElements(fileName, (jbyte *)fn, JNI_ABORT);
    //env->ReleaseStringUTFChars(fileName, fn);
    

    switch (desc.pixelformat) {
        case DSPF_ARGB:
        case DSPF_A8:
            img->hasalpha = 1;
            break;
        default:
            img->hasalpha = 0;
            break;
    }


    //printf( "Java_vdr_mhp_awt_MHPImage_imgCreateFromFile(\"%s\") done. img: %p\n", fn, img );
    if (img->desc.caps & DICAPS_COLORKEY) {
         //printf( ", colorkey: %02x %02x %02x", img->desc.colorkey_r, img->desc.colorkey_g, img->desc.colorkey_b );
         surface->SetSrcColorKey( img->desc.colorkey_r, img->desc.colorkey_g, img->desc.colorkey_b );
    }

    return (jlong ) img;
}

jlong 
Java_vdr_mhp_awt_MHPImage_imgCreateFromData ( JNIEnv* env, jobject obj,
                      jbyteArray jbuffer, jint off, jint len )
{
     DFBSurfaceDescription desc;
     IDirectFBImageProvider *provider;
     IDirectFBSurface *surface;
     Image *img;
     
     int       n;
     jboolean  isCopy;
     jbyte     *jcomplete, *joffset;

     
     n = env->GetArrayLength(jbuffer);                             /* length             */
     jcomplete = env->GetByteArrayElements(jbuffer, &isCopy);      /* complete copy      */
     joffset = jcomplete + off;                                         /* copy after +offset */

     if (jcomplete == NULL) {
          env->ReleaseByteArrayElements(jbuffer, jcomplete, JNI_ABORT);
          return 0;
     }

     if ( off+len > n )
          len = n - off;

     if (len <= 0)
         return 0;

     env->ReleaseByteArrayElements(jbuffer, jcomplete, JNI_ABORT);
     
     
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
       delete e;
       return 0;
    }
    
    try {
       provider=buffer->CreateImageProvider();
    } catch (DFBException *e) {
       fprintf( stderr, "Unable to create image provider from data buffer: %s", e->GetResult() );
       printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
       delete e;
       buffer->Release();
       return 0;
    }
        
    try {
       provider->GetSurfaceDescription(&desc);
    } catch (DFBException *e) {
       buffer->Release();
       provider->Release();
       printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
       delete e;
       return 0;
    }

    try {
       surface=MhpOutput::System::self()->Interface()->CreateSurface(desc);
    } catch (DFBException *e) {
       buffer->Release();
       provider->Release();
       printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
       delete e;
       return 0;
    }

    img = new Image;
    img->surface = surface;
    
    try {
       provider->RenderTo(surface, NULL);
       provider->GetImageDescription(&img->desc);
    } catch (DFBException *e) {
       printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
       delete e;
    }
    

     
     buffer->Release();
     provider->Release();
     

     switch (desc.pixelformat) {
         case DSPF_ARGB:
         case DSPF_A8:
             img->hasalpha = 1;
             break;
         default:
             img->hasalpha = 0;
             break;
     }


     printf( "Java_vdr_mhp_awt_MHPImage_imgCreateFromData() done. img: %p (alpha: %d", img, img->hasalpha );
     if (img->desc.caps & DICAPS_COLORKEY) {
          printf( ", colorkey: %02x %02x %02x", img->desc.colorkey_r, img->desc.colorkey_g, img->desc.colorkey_b );
          surface->SetSrcColorKey( img->desc.colorkey_r, img->desc.colorkey_g, img->desc.colorkey_b );
     }
     printf( ")\n" );

     return (jlong ) img;
}

/************************************************************************************
 * field access
 */

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


jint
Java_vdr_mhp_awt_MHPImage_imgGetRGB(  JNIEnv* env, jobject obj, jint imgData, jint x, jint y) {
     u_int32_t             *dst;
     Image                 *img = (Image*) imgData;
     int                    pitch,ret;

     if(!img)
          return 0;
          
     try {
      img->surface->Lock( DSLF_READ, (void**)&dst, &pitch );

      ret=(u_int32_t)dst[pitch/4 * y + x];

      img->surface->Unlock();
     } catch (DFBException *e) {
          printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
          delete e;
          return 0;
     }
    
     return ret;     
}

void
Java_vdr_mhp_awt_MHPImage_imgGetRGBRegion(  JNIEnv* env, jobject obj, jint imgData, jint startX, jint startY, jint w, jint h, jintArray rgbArray, jint offset, jint scansize) {
     u_int32_t             *dst;
     Image                 *img = (Image*) imgData;
     int                    pitch;
     jboolean               isCopy;
     int                   *userArray;

     if(!img)
          return;
          
     userArray=env->GetIntArrayElements(rgbArray, &isCopy);
     if (!userArray)
         return;
          
     try {
      img->surface->Lock( DSLF_READ, (void**)&dst, &pitch );

      for (int x=startX; x<startX+w; x++) {
         for (int y=startY; y<startY+h; y++) {
            userArray[offset + (y-startY)*scansize + (x-startX)]=(u_int32_t)dst[pitch/4 * y + x];
         }
      }
      
      img->surface->Unlock();
     } catch (DFBException *e) {
          env->ReleaseIntArrayElements(rgbArray, userArray, JNI_ABORT);    
          printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
          delete e;
          return;
     }
     env->ReleaseIntArrayElements(rgbArray, userArray, JNI_ABORT);    
}

void
Java_vdr_mhp_awt_MHPImage_imgSetRGB(  JNIEnv* env, jobject obj, jint imgData, jint x, jint y, jint rgb) {
     u_int32_t             *dst;
     Image                 *img = (Image*) imgData;
     int                    pitch;

     if(!img)
          return;
          
     try {
      img->surface->Lock( DSLF_WRITE, (void**)&dst, &pitch );

      dst[pitch/4 * y + x]=(u_int32_t)rgb;

      img->surface->Unlock();
     } catch (DFBException *e) {
         printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
         delete e;
     }    
}

void
Java_vdr_mhp_awt_MHPImage_imgSetRGBRegion(  JNIEnv* env, jobject obj, jint imgData, jint startX, jint startY, jint w, jint h, jintArray rgbArray, jint offset, jint scansize) {
     u_int32_t             *dst;
     Image                 *img = (Image*) imgData;
     int                    pitch;
     jboolean               isCopy;
     int                   *userArray;

     if(!img)
          return;
          
     userArray=env->GetIntArrayElements(rgbArray, &isCopy);
     if (!userArray)
         return;
          
     try {
      img->surface->Lock( DSLF_WRITE, (void**)&dst, &pitch );

      for (int x=startX; x<startX+w; x++) {
         for (int y=startY; y<startY+h; y++) {
            dst[pitch/4 * y + x]=(u_int32_t)userArray[offset + (y-startY)*scansize + (x-startX)];
         }
      }
      
      img->surface->Unlock();
     } catch (DFBException *e) {
          env->ReleaseIntArrayElements(rgbArray, userArray, JNI_ABORT);    
          printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
          delete e;
          return;
     }
     env->ReleaseIntArrayElements(rgbArray, userArray, JNI_COMMIT);    
}

jlong 
Java_vdr_mhp_awt_MHPImage_imgGetSubImage(  JNIEnv* env, jobject obj, jint imgData, jint x, jint y, jint w, jint h) {
   Image                 *img = (Image*) imgData;
   
   if(!img)
      return 0;
      
   Image *newImage=new Image;
   newImage->desc=img->desc;
   newImage->hasalpha=img->hasalpha;
   newImage->left=img->left;
   newImage->top=img->top;
   newImage->latency=img->latency;
   newImage->frame=img->frame;
   newImage->next=img->next;
      
   try {
       newImage->surface=img->surface->GetSubSurface(x, y, w, h);
   } catch (DFBException *e) {
       printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
       delete e;
       return 0;
   }
   return (jlong )newImage;
}
#endif








jlong 
Java_vdr_mhp_awt_MHPImage_imgCreateScreenImage ( JNIEnv* env, jobject obj, jint width, jint height )
{
   DFBSurfacePixelFormat format;
   DFBSurfaceDescription desc;
   IDirectFBSurface *surface;

   printf( "Java_vdr_mhp_awt_MHPImage_imgCreateScreenImage (%dx%d)\n", width, height );

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
      Exception exp;
      //What kind of exception should be thrown?
      exp.Throw("java/lang/RuntimeException", "Failed to create DFBSurface for Image");
      return 0;
   }

   return (jlong )surface;
}












JNI::InstanceMethod setProperties;

void Java_vdr_mhp_awt_DFBImageProvider_initStaticState(JNIEnv* env, jclass clazz) {
   char sig[100];
   JNI::BaseObject::getSignature(sig, JNI::Void, 3, JNI::Boolean, JNI::Int, JNI::Int);
   if (!setProperties.SetMethod(clazz, "setProperties", sig)) {
      Exception exp;
      exp.Throw("NoSuchMethodException", "DFBImageProvider.setProperties(boolean, int, int)");
   }
}

static bool setProperties(IDirectFBImageProvider *provider, jobject obj) {
   try {
      provider->GetSurfaceDescription(&desc);
   } catch (DFBException *e) {
      buffer->Release();
      provider->Release();
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      Exception exp;
      exp.Throw("java/lang/IllegalArgumentException", "Failed to get image information from ImageProvider");
      delete e;
      return false;
   }
   
   ReturnType type;
   return setProperties.CallMethod(obj, type, JNI::Void, valid, width, height);
}

jlong Java_vdr_mhp_awt_DFBImageProvider_createImageProviderFromFile(JNIEnv* env, jobject obj, jbyteArray filename) //throws IllegalArgumentException
{
   IDirectFBImageProvider *provider = 0;
   DFBSurfaceDescription desc;
   const char* fn = 0;

   fn = (const char *)env->GetByteArrayElements(fileName, 0);
   //fn = env->GetStringUTFChars(fileName, NULL);

   printf( "Java_vdr_mhp_awt_DFBImageProvider_createImageProviderFromFile(\"%s\") called.\n", fn );

   try {
      provider=MhpOutput::System::self()->Interface()->CreateImageProvider(fn);
   } catch (DFBException *e) {
      fprintf( stderr, "Unable to create the "
               "Media Provider for `%s': %s", fn, e->GetResult() );
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      env->ReleaseByteArrayElements(fileName, (jbyte *)fn, JNI_ABORT);
      Exception exp;
      char *msg;
      asprintf(&msg, "Error trying to create ImageProvider from file %s", fn);
      exp.Throw("java/io/IllegalArgumentException", msg);
      free(msg);
      delete e;
      return 0;
   }
   
   setProperties(provider, obj);

   return (jlong)provider;
}

jlong Java_vdr_mhp_awt_DFBImageProvider_createImageProviderFromDataBuffer(JNIEnv* env, jobject obj, jlong nativeBufferData) //throws IllegalArgumentException, IOException;
{
   IDirectFBImageProvider *provider = 0;
   IDirectFBDataBuffer *buffer = (IDirectFBDataBuffer *)nativeBufferData;
   DFBSurfaceDescription desc;
   
   if (!buffer) {
      Exception exp;
      exp.Throw("java/lang/IllegalArgumentException", "Invalid data buffer");
      return 0;
   }
   
   try {
      provider=buffer->CreateImageProvider();
   } catch (DFBException *e) {
      fprintf( stderr, "Unable to create image provider from data buffer: %s", e->GetResult() );
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      Exception exp;
      exp.Throw("java/lang/IllegalArgumentException", "Failed to create ImageProvider from DataBuffer");
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
      provider->Release();
      env->ReleaseByteArrayElements(fileName, (jbyte *)fn, JNI_ABORT);
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      Exception exp;
      exp.Throw("java/lang/IllegalArgumentException", "Failed to render image");
      delete e;
   }
}

void Java_vdr_mhp_awt_DFBImageProvider_removeRef(jlong nativeData) {
   IDirectFBImageProvider *provider = (IDirectFBImageProvider *)nativeProviderData;
   provider->Release();
}

jlong Java_vdr_mhp_awt_DFBDataBuffer_createBufferFromFile(JNIEnv* env, jobject obj, jbyteArray filename) //throws IOException;
{
}

jlong Java_vdr_mhp_awt_DFBDataBuffer_createBufferFromData(JNIEnv* env, jobject obj, jbyteArray data, jint offset, jint len) //throws IOException;
{
   if (len <= 0 || offset < 0) {
      Exception exp;
      exp.Throw("java/lang/IllegalArgumentException", "Invalid length parameter");
   }
   
   int       n;
   jboolean  isCopy;
   jbyte     *jcomplete, *joffset;

   
   n = env->GetArrayLength(jbuffer);                             // length
   jcomplete = env->GetByteArrayElements(jbuffer, &isCopy);      // complete copy
   joffset = jcomplete + off;                                    // copy after +offset

   if (jcomplete == NULL) {
         env->ReleaseByteArrayElements(jbuffer, jcomplete, JNI_ABORT);
         return 0;
   }

   if ( off+len > n )
         len = n - off;
   if (len <= 0)
      return 0;
   
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
      Exception exp;
      exp.Throw("java/io/IOException", "Unable to create data buffer");
      delete e;
      return 0;
   }
   
   env->ReleaseByteArrayElements(jbuffer, jcomplete, JNI_ABORT);
   
   return (jlong)buffer;
}

jlong Java_vdr_mhp_awt_DFBDataBuffer_createBufferForStreaming(JNIEnv* env, jobject obj) //throws IOException;
{
}

void Java_vdr_mhp_awt_DFBDataBuffer_putData(JNIEnv* env, jobject obj, jlong nativeData, jbyteArray data, jint len) {
}

void Java_vdr_mhp_awt_DFBDataBuffer_removeRef(JNIEnv* env, jobject obj, jlong nativeData) {
}


}
