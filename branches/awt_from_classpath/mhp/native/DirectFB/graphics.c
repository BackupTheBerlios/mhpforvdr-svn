#include <libjava/jniinterface.h>
#include <dfb++/dfb++.h>
#include <vdr/thread.h>


extern "C" {
#include <libxmi/sys-defines.h>
#include <libxmi/extern.h>
#include <libxmi/xmi.h>
#include <libxmi/mi_spans.h>
}

//large parts taken from kawt, Copyright (C) Convergence Integrated Media

extern "C" {

//A FlipData object encapsulates handling of changes and necessary Flip() calls
class FlipData {
public:
   virtual void enterBuffered() = 0;
   virtual void leaveBuffered() = 0;
   virtual void addUpdate(int x1, int y1, int x2, int y2) = 0;
protected:
   static DFBRegion nullRegion;
};
DFBRegion FlipData::nullRegion={0,0,0,0};

//The actual implementation
class ActualFlipData : public FlipData {
public:
   ActualFlipData(IDirectFBSurface *s) {
      dontFlip=0;
      region=nullRegion;
      regionValid=false;
      surface=s;
      //referenceCounter=1;
   }
   
/*   void addRef() {
      referenceCounter++;
   }
   
   void removeRef() {
      referenceCounter--;
      if (!referenceCounter)
         delete this;
   }*/
   
   void enterBuffered() {
      dontFlip++;
   }
   
   void leaveBuffered() {
      if (dontFlip)
         dontFlip--;
      flip(); //flip checks for dontFlip, no double check here
   }
   
   void addUpdate(int x1, int y1, int x2, int y2) {
      add(x1, y1, x2, y2);
      flip(); //again: flip checks for dontFlip, no double check here
   }
   
protected:
   int dontFlip;
   //int referenceCounter;
   DFBRegion region;
   bool regionValid;
   IDirectFBSurface *surface;
   
   void flip() {
      if (!dontFlip) {
         if (regionValid) {
            //printf("Flipping surface %p, region %dx%d - %dx%d\n", surface, region.x1, region.y1, region.x2, region.y2);
            try {
               surface->Flip(&region, DSFLIP_NONE);
            } catch (DFBException *e) {
               printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
               delete e;
            }
            region.y2--;
            region=nullRegion;
            regionValid=false;
         }
      }
   }
   
   void add(int x1, int y1, int x2, int y2) {
      if (regionValid) {
         //add to region
         region.x1 = region.x1 <? x1;
         region.y1 = region.y1 <? y1;
         region.x2 = region.x2 >? x2;
         region.y2 = region.y2 >? y2;
      } else {
         //initially set region
         region.x1 = x1;
         region.y1 = y1;
         region.x2 = x2;
         region.y2 = y2;
         regionValid=true;
      }
      //printf("Graphics: added %d, %d, %d, %d, now is %d, %d, %d, %d\n", x1,y1,x2,y2, region.x1,region.y1,region.x2,region.y2);
   }
};

//used for SubSurfaces - refers to "parent" ActualFlipData
class VirtualFlipData : public FlipData {
public:
   VirtualFlipData(FlipData *d, int x, int y) {
      data=d;
      offsetX=x;
      offsetY=y;
   }
   virtual void enterBuffered() {
      data->enterBuffered();
   }
   virtual void leaveBuffered() {
      data->leaveBuffered();
   }
   virtual void addUpdate(int x1, int y1, int x2, int y2) {
      data->addUpdate(x1+offsetX, y1+offsetY, x2+offsetX, y2+offsetY);
   }
protected:
   int offsetX, offsetY;
   FlipData *data;
};

//used for surfaces that do not need and do not support flipping (images)
class DummyFlipData {
public:
   virtual void enterBuffered() {}
   virtual void leaveBuffered() {}
   virtual void addUpdate(int x1, int y1, int x2, int y2) {}
};


#define DVBGRA_NOBLEND(d) (((d)->porter == DSPD_SRC) || ((d)->porter == DSPD_CLEAR))
#define JRGB(_r,_g,_b)	(_r<<16 | _g<<8 | _b)
#define JALPHA(_rgb)	((_rgb & 0xff000000) >> 24)
#define JRED(_rgb)		((_rgb & 0x00ff0000) >> 16)
#define JGREEN(_rgb)	((_rgb & 0x0000ff00) >>  8)
#define JBLUE(_rgb)		((_rgb & 0x000000ff)      )

static void setSurfaceColor(IDirectFBSurface *surface, int color) {
   surface->SetColor(JRED(color), JGREEN(color), JBLUE(color), JALPHA(color) );
}

void Java_vdr_mhp_awt_MHPNativeGraphics_addRef(JNIEnv* env, jobject obj, jlong nativeData) {
   ((IDirectFBSurface *)nativeData)->AddRef();
}

void Java_vdr_mhp_awt_MHPNativeGraphics_removeRef(JNIEnv* env, jobject obj, jlong nativeData) {
   ((IDirectFBSurface *)nativeData)->Release();
}

jlong Java_vdr_mhp_awt_MHPNativeGraphics_createFlipData(JNIEnv* env, jobject obj, jlong nativeData) {
   if (((IDirectFBSurface *)nativeData)->GetCapabilities() & DSCAPS_FLIPPING)
      return (jlong )new ActualFlipData((IDirectFBSurface *)nativeData);
   else 
      return (jlong)new DummyFlipData();
}

void Java_vdr_mhp_awt_MHPNativeGraphics_deleteFlipData(JNIEnv* env, jobject obj, jlong nativeFlipData) {
   delete (FlipData *)nativeFlipData;
}

jlong Java_vdr_mhp_awt_MHPNativeGraphics_createSubSurface(JNIEnv* env, jclass clazz, jlong nativeParentSurface, jint x, jint y, jint width, jint height) {
   try {
      return (jlong ) ((IDirectFBSurface *)nativeParentSurface)->GetSubSurface(x, y, width, height);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return 0;
   }
}

jlong Java_vdr_mhp_awt_MHPNativeGraphics_createSubFlipData(JNIEnv* env, jclass clazz, jlong nativeFlipData, jint x, jint y) {
   return (jlong )new VirtualFlipData((FlipData *)nativeFlipData, x, y);
}

jint Java_vdr_mhp_awt_MHPNativeGraphics_getHeight(JNIEnv* env, jobject obj, jlong nativeData) {
   int height;
   try {
      ((IDirectFBSurface *)nativeData)->GetSize(0, &height);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return 0;
   }
   return height;
}

jint Java_vdr_mhp_awt_MHPNativeGraphics_getWidth(JNIEnv* env, jobject obj, jlong nativeData) {
   int width;
   try {
      ((IDirectFBSurface *)nativeData)->GetSize(&width, 0);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return 0;
   }
   return width;
}

void Java_vdr_mhp_awt_MHPNativeGraphics_copyArea(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jint x, jint y, jint width, jint height, jint dx, jint dy) {
   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   try {
      DFBRectangle rect = { x, y, width, height };
      surface->SetBlittingFlags(DSBLIT_NOFX);
      surface->Blit(((IDirectFBSurface *)nativeData), &rect, dx, dy);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(dx, dy, dx+width-1, dx+height-1);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_draw3DRect(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, 
         jint x, jint y, jint width, jint height, jboolean raised, jint origColor, jint bright, jint dark) {
   int color = raised ? bright : dark;
   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   try {
     setSurfaceColor(surface, color);
     surface->SetDrawingFlags(DSDRAW_BLEND);

     surface->DrawLine(x, y, x+width-1, y );     
     surface->DrawLine(x, y+1, x, y+height-0 );

     color = raised ? dark : bright;
     setSurfaceColor(surface, color);

     surface->DrawLine(x+1, y+height-0, x+width-0, y+height-0 );
     surface->DrawLine(x+width-0, y, x+width-0, y+height-1 );

     setSurfaceColor(surface, origColor);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(x, y, x+width-0, y+height-0);   
}

void Java_vdr_mhp_awt_MHPNativeGraphics_drawArc(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jint x, jint y, jint width, jint height, jint startAngle, jint arcAngle) {

   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   
   if (width<=0 || height<=0)
      return;
   if (arcAngle==0) //negative values are okay
      return;
   if (startAngle > 360)
      startAngle = startAngle%360;
   
   
   //libxmi operations
   miPixel pixels[2];
   miGC *pGC;
   miPaintedSet *paintedSet;
   miEllipseCache *cache;
   miArc arcs[1];
   
   pixels[0]=0; // pixel value for `off' dashes, if drawn
   pixels[1]=1; // default pixel for drawing
   pGC = miNewGC(2, pixels);
   paintedSet = miNewPaintedSet();
   cache = miNewEllipseCache();
   
   arcs[0].x=x;
   arcs[0].y=y;
   arcs[0].width=width;
   arcs[0].height=height;
   arcs[0].angle1=startAngle*64; //starting angle, in 1/64 degrees, counterclockwise from x-axis
   arcs[0].angle2=arcAngle*64; //angle range, in 1/64 degrees
   
   
   miDrawArcs_r(paintedSet, pGC, 1, arcs, cache);
  
   miDeleteEllipseCache(cache);
   
   //do the actual drawing
   try {
      surface->SetDrawingFlags(DSDRAW_BLEND);
      
      //translate the point structures of the paintedSet to the surface's pixels
      for (int i = 0; i < paintedSet->ngroups; i++) {
         if (paintedSet->groups[i]->group[0].count > 0) {
            if (paintedSet->groups[i]->pixel) {
               for (int u=0;u<paintedSet->groups[i]->group[0].count;u++) {
               
                  surface->DrawLine(paintedSet->groups[i]->group[0].points[u].x,
                                    paintedSet->groups[i]->group[0].points[u].y,
                                    paintedSet->groups[i]->group[0].points[u].x + paintedSet->groups[i]->group[0].widths[u],
                                    paintedSet->groups[i]->group[0].points[u].y);
               }
            }
         }
      }
      
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      miDeleteGC (pGC);
      miDeletePaintedSet (paintedSet);
      delete e;
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(x, y, x+width+1, y+height+1);
   miDeleteGC (pGC);
   miDeletePaintedSet (paintedSet);
}

static void prepareBlitting(IDirectFBSurface *surface, IDirectFBSurface *sourceSurface, jint x, jint y, jint width, jint height, jint origColor, jint bgColor, jint extraAlpha) {
   
   // If bgColor is set, all transparent pixels shall be painted in this color.
   // Java spec says "This operation is equivalent to filling a rectangle of the width
   // and height of the specified image with the given color and then drawing the image
   // on top of it, but possibly more efficient.". So this is what we do.
   if (bgColor != -1) {
      surface->SetDrawingFlags(DSDRAW_BLEND);
      setSurfaceColor(surface, bgColor);
      surface->FillRectangle(x, y, width+1, height+1);
      setSurfaceColor(surface, origColor);
   }
   
   int blittingflags = DSBLIT_NOFX;
   
   // Information about a possible colorkey is currently not obtained from ImageProvider,
   // and is not available here. I don't know if this is necessary at all.
   /*
   if (imageDescription.caps & DICAPS_COLORKEY) {
         blittingflags |= DSBLIT_SRC_COLORKEY;
   }
   */
   
   // always blend with alpha channel
   blittingflags |= DSBLIT_BLEND_ALPHACHANNEL;

   // possibly additionally blend with the extraAlpha value
   if (extraAlpha < 255) {
      blittingflags |= (DFBSurfaceBlittingFlags)DSBLIT_BLEND_COLORALPHA;
      surface->SetColor(0, 0, 0, extraAlpha );
   }
   
   surface->SetBlittingFlags((DFBSurfaceBlittingFlags)blittingflags);
   
   // this is some very old code from the old kawt implementation
   
     /*if (img->hasalpha || extraAlpha < 255) {
          switch (porter) {
               case DSPD_SRC:
                    surface->SetSrcBlendFunction( dvbgra->gra->surface,
                                                               DSBF_SRCALPHA );
                    surface->SetDstBlendFunction( dvbgra->gra->surface,
                                                               DSBF_ZERO );
                    break;
               case DSPD_SRC_OVER:
                    surface->SetSrcBlendFunction( dvbgra->gra->surface,
                                                               DSBF_SRCALPHA );
                    // FIXME: destination alpha is ignored 
                    surface->SetDstBlendFunction( dvbgra->gra->surface,
                                                               DSBF_INVSRCALPHA );
                    break;
               default:
                    printf( "!!!!!! unsupported Porter/Duff for source "
                              "with alphachannel or extraAlpha != 1.0\n" );
                    surface->SetPorterDuff(porter);
                    break;
          }

          if (img->hasalpha)
               blittingflags |= DSBLIT_BLEND_ALPHACHANNEL;

          if (dvbgra->extraAlpha < 255) {
               blittingflags |= DSBLIT_BLEND_COLORALPHA;
               surface->SetColor( dvbgra->gra->surface, 0, 0, 0, dvbgra->extraAlpha );
          }
     }
     else if (dvbgra->porter != DSPD_SRC && dvbgra->porter != DSPD_SRC_OVER) {
          surface->SetPorterDuff( dvbgra->gra->surface,
                                               dvbgra->porter );
          blittingflags |= DSBLIT_BLEND_ALPHACHANNEL;
     }*/

}

void Java_vdr_mhp_awt_MHPNativeGraphics_drawImage(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jlong imgNativeData, 
                                               jint srcX, jint srcY, jint srcWidth, jint srcHeight,
                                               jint dstX, jint dstY, jint origColor, jint bgColor, jint extraAlpha)
{
   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   IDirectFBSurface *sourceSurface=((IDirectFBSurface *)imgNativeData);
   DFBRectangle rect = { srcX, srcY, srcWidth, srcHeight };
   // Here is srcWidth == dstWidth, srcHeight == dstHeight
   
   try {
      
      prepareBlitting(surface, sourceSurface, dstX, dstY, srcWidth, srcHeight, origColor, bgColor, extraAlpha);
      
      //printf("Graphics: blit'ing image, %dx%d-%dx%d, %d,%d,  update region %dx%d-%dx%d\n", rect.x, rect.y, rect.w, rect.h, x, y, x, y, x+width-1, y+height-1);
      surface->Blit(sourceSurface, &rect, dstX, dstY);
      
      setSurfaceColor(surface, origColor);

   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      JNI::Exception::Throw(JNI::JavaLangIllegalArgumentException, "Blitting failed");
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(dstX, dstY, dstX+srcWidth-1, dstY+srcHeight-1);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_drawImageScaled(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jlong imgNativeData, 
                                                      int srcX, int srcY, int srcWidth, int srcHeight,
                                                      int dstX, int dstY, int dstWidth, int dstHeight, 
                                                      jint origColor, jint bgColor, jint extraAlpha) 
{

   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   IDirectFBSurface *sourceSurface=((IDirectFBSurface *)imgNativeData);
   DFBRectangle sr = { srcX, srcY, srcWidth, srcHeight };
   DFBRectangle dr = { dstX, dstY, dstWidth, dstHeight };
   try {
      prepareBlitting(surface, sourceSurface, dstX, dstY, dstWidth, dstHeight, origColor, bgColor, extraAlpha);
     
      printf("Graphics: StretchBlit'ing image\n");
      surface->StretchBlit(sourceSurface, &sr, &dr);
     
      setSurfaceColor(surface, origColor);

   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      JNI::Exception::Throw(JNI::JavaLangIllegalArgumentException, "StretchBlitting failed");
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(dr.x, dr.y, dr.x+dr.w-1, dr.y+dr.h-1 );
}

void Java_vdr_mhp_awt_MHPNativeGraphics_drawImageTiled(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jlong imgNativeData, 
                                               jint srcX, jint srcY, jint srcWidth, jint srcHeight,
                                               jint dstX, jint dstY, jint origColor, jint bgColor, jint extraAlpha)
{
   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   IDirectFBSurface *sourceSurface=((IDirectFBSurface *)imgNativeData);
   DFBRectangle rect = { srcX, srcY, srcWidth, srcHeight };
   
   try {
      
      prepareBlitting(surface, sourceSurface, dstX, dstY, srcWidth, srcHeight, origColor, bgColor, extraAlpha);
      
      //printf("Graphics: TileBlit'ing image, %dx%d-%dx%d, %d,%d,  update region %dx%d-%dx%d\n", rect.x, rect.y, rect.w, rect.h, x, y, x, y, x+width-1, y+height-1);
      
      surface->TileBlit(sourceSurface, &rect, dstX, dstY);
      
      setSurfaceColor(surface, origColor);

   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      JNI::Exception::Throw(JNI::JavaLangIllegalArgumentException, "TileBlitting failed");
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(dstX, dstY, dstX+srcWidth-1, dstY+srcHeight-1);
}

// TODO: this does not look clean. Find out of this hack is necessary
void Java_vdr_mhp_awt_MHPNativeGraphics_tileBlitImageAlpha(JNIEnv* env, jobject obj, jlong nativeData,
             jlong nativeFlipData, jlong imgNativeData, jint x, jint y, jint porterDuffRule)
{
   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   IDirectFBSurface *sourceSurface=((IDirectFBSurface *)imgNativeData);
   int width,height;
   try {
     int blittingflags = DSBLIT_NOFX; //correct?
     surface->GetSize(&width, &height);
      
     surface->SetBlittingFlags((DFBSurfaceBlittingFlags)blittingflags);
     
     surface->SetPorterDuff(DSPD_DST_IN);
     
     printf("Graphics: TileBlit'ing image, %d,%d\n", x, y);
     surface->TileBlit(sourceSurface, NULL, x, y);
     
     //setSurfaceColor(surface, origColor);
     surface->SetPorterDuff((DFBSurfacePorterDuffRule)porterDuffRule);

   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      JNI::Exception::Throw(JNI::JavaLangIllegalArgumentException, "TileBlitting with alpha failed");
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(x, y, x+width, y+height);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_drawLine(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jint x1, jint y1, jint x2, jint y2) {
   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   try {
      //printf("DrawLine\n");
      surface->SetDrawingFlags(DSDRAW_BLEND);
      surface->DrawLine(x1, y1, x2, y2);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(x1 <? x2, y1 <? y2, x1 >? x2, y1 >? y2);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_drawOval(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jint x, jint y, jint width, jint height) {
   //draw a 360* arc
   Java_vdr_mhp_awt_MHPNativeGraphics_drawArc(env, obj, nativeData, nativeFlipData, x, y, width, height, 0, 360);
}

static void drawPoly(JNIEnv* env, jlong nativeData, jlong nativeFlipData, jintArray xPoints, jintArray yPoints, jint nPoints, bool closed) {
   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   int xmax=0, xmin, ymax=0, ymin;
   
   jint *xs=env->GetIntArrayElements(xPoints, 0);
   jint *ys=env->GetIntArrayElements(yPoints, 0);
   if (!xs || !ys || nPoints!=(jint)env->GetArrayLength(xPoints) || nPoints!=(jint)env->GetArrayLength(yPoints))
      return;
   
   for (int i=1;i<nPoints;i++) {
      xmin = xmin <? xs[i];
      ymin = ymin <? ys[i];
      xmax = xmax >? xs[i];
      ymax = ymax >? ys[i];
   }
   
   try {
      surface->GetSize(&xmin, &ymin);
      surface->SetDrawingFlags(DSDRAW_BLEND);
      
      
      for (int i=1;i<nPoints;i++) {
         surface->DrawLine(xs[i-1], ys[i-1], xs[i], ys[i]);
      }
      
      if (closed)
         surface->DrawLine(xs[nPoints-1], ys[nPoints-1], xs[0], ys[0]);
      
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      env->ReleaseIntArrayElements(xPoints, xs, JNI_ABORT);
      env->ReleaseIntArrayElements(yPoints, ys, JNI_ABORT);
      delete e;
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(xmin, ymin, xmax, ymax);
   
   env->ReleaseIntArrayElements(xPoints, xs, JNI_ABORT);
   env->ReleaseIntArrayElements(yPoints, ys, JNI_ABORT);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_drawPolygon(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jintArray xPoints, jintArray yPoints, jint nPoints) {
   drawPoly(env, nativeData, nativeFlipData, xPoints, yPoints, nPoints, true);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_drawPolyline(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jintArray xPoints, jintArray yPoints, jint nPoints) {
   drawPoly(env, nativeData, nativeFlipData, xPoints, yPoints, nPoints, false);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_drawRect(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jint x, jint y, jint width, jint height) {
   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   try {
      surface->SetDrawingFlags(DSDRAW_BLEND);
      surface->DrawRectangle(x, y, width+1, height+1);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(x, y, x+width, y+height);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_drawRoundRect(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jint x, jint y, jint width, jint height, jint arcWidth, jint arcHeight) {

   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   //printf("Java_vdr_mhp_awt_MHPNativeGraphics_fillRoundRect: %dx%d, %dx%d\n", x, y, width, height);
   
   if (width<=0 || height<=0 || arcWidth<0 || arcHeight<0)
      return;
   if (height<arcHeight || width<arcWidth)
      return Java_vdr_mhp_awt_MHPNativeGraphics_drawRect(env, obj, nativeData, nativeFlipData, x,y,width, height);
   
   //libxmi operations
   miPixel pixels[2];
   miGC *pGC;
   miPaintedSet *paintedSet;
   miEllipseCache *cache;
   miArc arcs[4];
   
   pixels[0]=0; // pixel value for `off' dashes, if drawn
   pixels[1]=1; // default pixel for drawing
   pGC = miNewGC(2, pixels);
   paintedSet = miNewPaintedSet();
   cache = miNewEllipseCache();
   
   //upper left corner
   arcs[0].x=x;
   arcs[0].y=y;
   arcs[0].width=arcWidth;
   arcs[0].height=arcHeight;
   arcs[0].angle1=90*64; //starting angle, in 1/64 degrees, counterclockwise from x-axis
   arcs[0].angle2=90*64; //angle range, in 1/64 degrees
   
   //lower left
   arcs[1].x=x;
   arcs[1].y=y+height-arcHeight;
   arcs[1].width=arcWidth;
   arcs[1].height=arcHeight;
   arcs[1].angle1=180*64;
   arcs[1].angle2=90*64;
   
   //uper right
   arcs[2].x=x+width-arcWidth;
   arcs[2].y=y;
   arcs[2].width=arcWidth;
   arcs[2].height=arcHeight;
   arcs[2].angle1=0*64;
   arcs[2].angle2=90*64;
   
   //lower right
   arcs[3].x=x+width-arcWidth;
   arcs[3].y=y+height-arcHeight;
   arcs[3].width=arcWidth;
   arcs[3].height=arcHeight;
   arcs[3].angle1=0*64;
   arcs[3].angle2=-90*64;
   
   miDrawArcs_r(paintedSet, pGC, 4, arcs, cache);
  
   miDeleteEllipseCache(cache);
   
   //do the actual drawing
   try {
      surface->SetDrawingFlags(DSDRAW_BLEND);
      
      //first draw four normal lines (the corners cut out):
      //left
      if ((y+height-arcHeight/2)>0)
         surface->DrawLine(x, y+arcHeight/2, x, y+height-arcHeight/2);
      //upper
      if ((x+width-arcWidth/2)>0)
         surface->DrawLine(x+arcWidth/2, y, x+width-arcWidth/2, y);
      //right
      if ((y+height-arcHeight/2)>0)
         surface->DrawLine(x+width, y+arcHeight/2, x+width, y+height-arcHeight/2); 
      //lower
      if ((x+width-arcWidth/2)>0)
         surface->DrawLine(x+arcWidth/2, y+height, x+width-arcWidth/2, y+height);
      
      //now translate the point structures of the paintedSet to the surface's pixels, i.e., draw the arcs
      for (int i = 0; i < paintedSet->ngroups; i++) {
         if (paintedSet->groups[i]->group[0].count > 0) {
            if (paintedSet->groups[i]->pixel) {
               for (int u=0;u<paintedSet->groups[i]->group[0].count;u++) {
               
                  surface->DrawLine(paintedSet->groups[i]->group[0].points[u].x,
                                    paintedSet->groups[i]->group[0].points[u].y,
                                    paintedSet->groups[i]->group[0].points[u].x + paintedSet->groups[i]->group[0].widths[u],
                                    paintedSet->groups[i]->group[0].points[u].y);
               }
            }
         }
      }
      
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      miDeleteGC (pGC);
      miDeletePaintedSet (paintedSet);
      delete e;
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(x, y, x+width+1, y+height+1);
   miDeleteGC (pGC);
   miDeletePaintedSet (paintedSet);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_drawString(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jstring s, jint x, jint y) {
   const char *str=(const char *)env->GetStringUTFChars(s, NULL);
   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   IDirectFBFont *font;
   try {
      font=surface->GetFont();
      
      surface->SetDrawingFlags(DSDRAW_BLEND);
      
      int length=env->GetStringUTFLength(s);
      int width=font->GetStringWidth(str, length);
      int descender=font->GetDescender();
      int ascender=font->GetAscender();
        
      surface->DrawString(str, length, x, y, DSTF_LEFT); 
      //printf("Graphics: DrawString with length %d, area is %dx%d-%dx%d because a %d, d %d, w %d\n", length, x, y-ascender, x+width, y-descender, ascender, descender, width);
      //descender is a negative value, so y-descender > y > y-ascender
      ((FlipData *)nativeFlipData)->addUpdate(x, y-ascender, x+width, y-descender);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }
   env->ReleaseStringUTFChars(s, str);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_fill3DRect(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, 
         jint x, jint y, jint width, jint height, jboolean raised, jint origColor, jint bright, jint dark) {
   int color = raised ? bright : dark;
   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   try {
     setSurfaceColor(surface, color);
     surface->SetDrawingFlags(DSDRAW_BLEND);

     surface->DrawLine(x, y, x+width-1, y );     
     surface->DrawLine(x, y+1, x, y+height-0 );

     color = raised ? dark : bright;
     setSurfaceColor(surface, color);

     surface->DrawLine(x+1, y+height-0, x+width-0, y+height-0 );
     surface->DrawLine(x+width-0, y, x+width-0, y+height-1 );

     surface->FillRectangle(x+1, y+1, width-2, height-2 );
     
     setSurfaceColor(surface, origColor);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(x, y, x+width-0, y+height-0);   
}

void Java_vdr_mhp_awt_MHPNativeGraphics_fillArc(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jint x, jint y, jint width, jint height, jint startAngle, jint arcAngle) {

   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   //printf("Java_vdr_mhp_awt_MHPNativeGraphics_fillRoundRect: %dx%d, %dx%d\n", x, y, width, height);
   
   if (width<=0 || height<=0)
      return;
   if (arcAngle==0) //negative values are okay
      return;
   if (startAngle > 360)
      startAngle = startAngle%360;
   
   
   //libxmi operations
   miPixel pixels[2];
   miGC *pGC;
   miPaintedSet *paintedSet;
   //miEllipseCache *cache;
   miArc arcs[1];
   
   pixels[0]=0; // pixel value for `off' dashes, if drawn
   pixels[1]=1; // default pixel for drawing
   pGC = miNewGC(2, pixels);
   paintedSet = miNewPaintedSet();
   //cache = miNewEllipseCache();
   
   arcs[0].x=x;
   arcs[0].y=y;
   arcs[0].width=width;
   arcs[0].height=height;
   arcs[0].angle1=startAngle*64; //starting angle, in 1/64 degrees, counterclockwise from x-axis
   arcs[0].angle2=arcAngle*64; //angle range, in 1/64 degrees
   
   
   miFillArcs(paintedSet, pGC, 1, arcs);
  
   //miDeleteEllipseCache(cache);
   
   //do the actual drawing
   try {
      surface->SetDrawingFlags(DSDRAW_BLEND);
      
      //translate the point structures of the paintedSet to the surface's pixels
      for (int i = 0; i < paintedSet->ngroups; i++) {
         if (paintedSet->groups[i]->group[0].count > 0) {
            if (paintedSet->groups[i]->pixel) {
               for (int u=0;u<paintedSet->groups[i]->group[0].count;u++) {
               
                  surface->DrawLine(paintedSet->groups[i]->group[0].points[u].x,
                                    paintedSet->groups[i]->group[0].points[u].y,
                                    paintedSet->groups[i]->group[0].points[u].x + paintedSet->groups[i]->group[0].widths[u],
                                    paintedSet->groups[i]->group[0].points[u].y);
               }
            }
         }
      }
      
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      miDeleteGC (pGC);
      miDeletePaintedSet (paintedSet);
      delete e;
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(x, y, x+width+1, y+height+1);
   miDeleteGC (pGC);
   miDeletePaintedSet (paintedSet);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_fillOval(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jint x, jint y, jint width, jint height) {
   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   try {
     surface->SetDrawingFlags(DSDRAW_BLEND);
     
     int local_x=x, local_y=y;
     int xx, yy, x_center, y_center, line_length; 
     float a, b, d1, d2;
     a = width/2;
     b = height/2;
     x_center = local_x + (int)a;
     y_center = local_y + (int)b;


     xx = 0;
     yy = (int)b;
     d1 = b*b - a*a*b + a*a/4;


     surface->DrawLine(              (int)(x_center+0.5), 
                                     (int)(y_center+0.5-yy),
                                     (int)(x_center+0.5),
                                     (int)(y_center+0.5-yy)+height);

     while ( (a*a*(yy-0.5)) > (b*b*(xx+1)) ) {
          if (d1 < 0) {
               d1 = d1 + b*b*(2*xx+3);
               xx++;
          }
          else {
               d1 = d1 + b*b*(2*xx+3)+a*a*(2-2*yy);
               xx++;
               yy--;
          }
          line_length = (int)(y_center+0.5+yy) - (int)(y_center+0.5-yy);

          surface->DrawLine(              (int)(x_center+0.5+xx), 
                                          (int)(y_center+0.5-yy),
                                          (int)(x_center+0.5+xx),
                                          (int)(y_center+0.5-yy)+line_length);
                                          
          surface->DrawLine(              (int)(x_center+0.5-xx), 
                                          (int)(y_center+0.5-yy),
                                          (int)(x_center+0.5-xx),
                                          (int)(y_center+0.5-yy)+line_length);


     }

     d2=b*b*(xx+0.5)*(xx+0.5)+a*a*(yy-1)*(yy-1)-a*a*b*b;
     while (yy>0) {
          if (d2<0) {
               d2=d2+b*b*(2*xx+2)+a*a*(3-2*yy);
               xx++;
               yy--;

               line_length = (int)(y_center+0.5+yy) - (int)(y_center+0.5-yy);

               surface->DrawLine(              (int)(x_center+0.5+xx), 
                                               (int)(y_center+0.5-yy),
                                               (int)(x_center+0.5+xx),
                                               (int)(y_center+0.5-yy)+line_length);
                                               
               surface->DrawLine(              (int)(x_center+0.5-xx), 
                                               (int)(y_center+0.5-yy),
                                               (int)(x_center+0.5-xx),
                                               (int)(y_center+0.5-yy)+line_length);
          }
          else {
               d2=d2+a*a*(3-2*yy);
               yy--;
          }


     }

   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(x, y, x+width-1, y+height-1 );

}

void Java_vdr_mhp_awt_MHPNativeGraphics_fillPolygon(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jintArray xPoints, jintArray yPoints, jint nPoints) {

   //printf("Java_vdr_mhp_awt_MHPNativeGraphics_fillPolygon\n");

   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   int xmax=0, xmin, ymax=0, ymin;
   
   jint *xs=env->GetIntArrayElements(xPoints, 0);
   jint *ys=env->GetIntArrayElements(yPoints, 0);
   if (!xs || !ys || nPoints!=(jint)env->GetArrayLength(xPoints) || nPoints!=(jint)env->GetArrayLength(yPoints))
      return;
      
   //libxmi operations
   miPixel pixels[2];
   miGC *pGC;
   miPaintedSet *paintedSet;
   
   pixels[0]=0; // pixel value for `off' dashes, if drawn
   pixels[1]=1; // default pixel for drawing
   pGC = miNewGC(2, pixels);
   paintedSet = miNewPaintedSet();
   
   //transscribe points to XMI structures
   miPoint *points=new miPoint[nPoints];
   for (int i=0;i<nPoints;i++) {
      points[i].x=xs[i];
      points[i].y=ys[i];
   }
   
   //do the actual drawing
   miFillPolygon(paintedSet, pGC, MI_SHAPE_GENERAL, MI_COORD_MODE_ORIGIN, nPoints, points);
   
   try {
      surface->SetDrawingFlags(DSDRAW_BLEND);
      surface->GetSize(&xmin, &ymin);
               
      //now translate the point structures of the paintedSet to the surface's pixels  
      for (int i = 0; i < paintedSet->ngroups; i++) {
         if (paintedSet->groups[i]->group[0].count > 0) {
            if (paintedSet->groups[i]->pixel) {
               for (int u=0;u<paintedSet->groups[i]->group[0].count;u++) {
               
                  surface->DrawLine(paintedSet->groups[i]->group[0].points[u].x,
                                    paintedSet->groups[i]->group[0].points[u].y,
                                    paintedSet->groups[i]->group[0].points[u].x + paintedSet->groups[i]->group[0].widths[u],
                                    paintedSet->groups[i]->group[0].points[u].y);
                                    
                  xmin = xmin <? paintedSet->groups[i]->group[0].points[u].x;
                  ymin = ymin <? paintedSet->groups[i]->group[0].points[u].y;
                  xmax = xmax >? paintedSet->groups[i]->group[0].points[u].x + (int)paintedSet->groups[i]->group[0].widths[u];
                  ymax = ymax >? paintedSet->groups[i]->group[0].points[u].y;
               }
            }
         }
      }
                           
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      miDeleteGC (pGC);
      miDeletePaintedSet (paintedSet);
      delete points;
      env->ReleaseIntArrayElements(xPoints, xs, JNI_ABORT);
      env->ReleaseIntArrayElements(yPoints, ys, JNI_ABORT);
      delete e;
      return;
   }
   
   ((FlipData *)nativeFlipData)->addUpdate(xmin, ymin, xmax, ymax);
   
   miDeleteGC (pGC);
   miDeletePaintedSet (paintedSet);
   delete points;
   env->ReleaseIntArrayElements(xPoints, xs, JNI_ABORT);
   env->ReleaseIntArrayElements(yPoints, ys, JNI_ABORT);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_fillRect(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jint x, jint y, jint width, jint height) {
   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   //printf("Java_vdr_mhp_awt_MHPNativeGraphics_fillRect: %dx%d, %dx%d\n", x, y, width, height);
   try {
      surface->SetDrawingFlags(DSDRAW_BLEND);
      surface->FillRectangle(x, y, width+1, height+1);
   } catch (DFBException *e) {
      int w,h;
      surface->GetSize(&w, &h);
      printf("DirectFB: Error %s, %s, %dx%d, %dx%d; %dx%d\n", e->GetAction(), e->GetResult(), x, y, width+1, height+1, w, h);
      delete e;
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(x, y, x+width+1, y+height+1);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_fillRoundRect(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeFlipData, jint x, jint y, jint width, jint height, int arcWidth, int arcHeight) {

   IDirectFBSurface *surface=((IDirectFBSurface *)nativeData);
   //printf("Java_vdr_mhp_awt_MHPNativeGraphics_fillRoundRect: %dx%d, %dx%d\n", x, y, width, height);
   
   if (width<=0 || height<=0 || arcWidth<0 || arcHeight<0)
      return;
   if (height<arcHeight || width<arcWidth)
      return Java_vdr_mhp_awt_MHPNativeGraphics_fillRect(env, obj, nativeData, nativeFlipData, x,y,width, height);
   
   //libxmi operations
   miPixel pixels[2];
   miGC *pGC;
   miPaintedSet *paintedSet;
   //miEllipseCache *cache;
   miArc arcs[4];

   //arcWidth and arcHeight shall be the "the horizontal/vertical diameter of the arc at the four corners"
   //Is the the diameter of the 1/4 ellipse, or is this the radius?
   //In the structures below, it is the radius.
   
   pixels[0]=0; // pixel value for `off' dashes, if drawn
   pixels[1]=1; // default pixel for drawing
   pGC = miNewGC(2, pixels);
   paintedSet = miNewPaintedSet();
   //cache = miNewEllipseCache();
   
   //upper left corner
   arcs[0].x=x;
   arcs[0].y=y;
   arcs[0].width=arcWidth;
   arcs[0].height=arcHeight;
   arcs[0].angle1=90*64; //starting angle, in 1/64 degrees, counterclockwise from x-axis
   arcs[0].angle2=90*64; //angle range, in 1/64 degrees
   
   //lower left
   arcs[1].x=x;
   arcs[1].y=y+height-arcHeight;
   arcs[1].width=arcWidth;
   arcs[1].height=arcHeight;
   arcs[1].angle1=180*64;
   arcs[1].angle2=90*64;
   
   //uper right
   arcs[2].x=x+width-arcWidth;
   arcs[2].y=y;
   arcs[2].width=arcWidth;
   arcs[2].height=arcHeight;
   arcs[2].angle1=0*64;
   arcs[2].angle2=90*64;
   
   //lower right      //lower
      if (width-(2*arcWidth)>0 && arcHeight>0)
         surface->FillRectangle(x+arcWidth, y+height-arcHeight, width-(2*arcWidth), arcHeight);

   arcs[3].x=x+width-arcWidth;
   arcs[3].y=y+height-arcHeight;
   arcs[3].width=arcWidth;
   arcs[3].height=arcHeight;
   arcs[3].angle1=0*64;
   arcs[3].angle2=-90*64;
   
   miFillArcs(paintedSet, pGC, 4, arcs);
  
   //miDeleteEllipseCache(cache);
   
   //do the actual drawing
   try {
      surface->SetDrawingFlags(DSDRAW_BLEND);
      
      //draw three rectangles, one with full length but height-archeight,
      //two smaller ones above and below with archeight and width-archeight
      if ((height - arcHeight + 1)>0)
         surface->FillRectangle(x, y + arcHeight / 2, width, height - arcHeight + 1);
      if ((width - arcWidth + 1)>0) {
         //upper
         surface->FillRectangle(x + arcWidth / 2, y, width - arcWidth + 1, arcHeight / 2);
         //lower
         surface->FillRectangle(x + arcWidth / 2, y + height - arcHeight / 2, width - arcWidth + 1, arcHeight / 2);
      }
   /*
      //first draw five normal rectangles (the corners cut out):
      //left rectangle
      if (arcWidth>0 && height-(2*arcHeight)>0)
         surface->FillRectangle(x, y+arcHeight, arcWidth, height-(2*arcHeight));
      //upper
      if (width-(2*arcWidth)>0 && arcHeight>0)
         surface->FillRectangle(x+arcWidth, y, width-(2*arcWidth), arcHeight);
      //right
      if (arcWidth>0 &&  height-(2*arcHeight)>0)
         surface->FillRectangle(x+width-arcWidth, y+arcHeight, arcWidth, height-(2*arcHeight));
      //lower
      if (width-(2*arcWidth)>0 && arcHeight>0)
         surface->FillRectangle(x+arcWidth, y+height-arcHeight, width-(2*arcWidth), arcHeight);
      //middle
      if (width-(2*arcWidth)>0 && height-(2*arcHeight)>0)
         surface->FillRectangle(x+arcWidth, y+arcHeight, width-(2*arcWidth), height-(2*arcHeight));
   */
      
      //now translate the point structures of the paintedSet to the surface's pixels  
      for (int i = 0; i < paintedSet->ngroups; i++) {
         if (paintedSet->groups[i]->group[0].count > 0) {
            if (paintedSet->groups[i]->pixel) {
               for (int u=0;u<paintedSet->groups[i]->group[0].count;u++) {
               
                  surface->DrawLine(paintedSet->groups[i]->group[0].points[u].x,
                                    paintedSet->groups[i]->group[0].points[u].y,
                                    paintedSet->groups[i]->group[0].points[u].x + paintedSet->groups[i]->group[0].widths[u],
                                    paintedSet->groups[i]->group[0].points[u].y);
               }
            }
         }
      }
      
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      miDeleteGC (pGC);
      miDeletePaintedSet (paintedSet);
      delete e;
      return;
   }
   ((FlipData *)nativeFlipData)->addUpdate(x, y, x+width+1, y+height+1);
   miDeleteGC (pGC);
   miDeletePaintedSet (paintedSet);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_setClip(JNIEnv* env, jobject obj, jlong nativeData, jint x, jint y, jint width, jint height) {
   try {
      //printf("MHPNativeGraphics_setClip for %p: %dx%d, %dx%d\n", nativeData, x, y, width, height);
      if (x==-1 && y==-1 && width==-1 && height==-1) //internal special case
         return ((IDirectFBSurface *)nativeData)->SetClip(NULL);
      DFBRegion region={x, y, x+width, y+height};
      return ((IDirectFBSurface *)nativeData)->SetClip( &region );
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }  
}

void Java_vdr_mhp_awt_MHPNativeGraphics_setColor(JNIEnv* env, jobject obj, jlong nativeData, jint r, jint g, jint b, jint a) {
   try {
      return ((IDirectFBSurface *)nativeData)->SetColor(r, g, b, a);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }  
}

void Java_vdr_mhp_awt_MHPNativeGraphics_setFont(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeDataFont) {
   try {
      return ((IDirectFBSurface *)nativeData)->SetFont((IDirectFBFont *)nativeDataFont);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }     
}

void Java_vdr_mhp_awt_MHPNativeGraphics_setPorterDuff(JNIEnv* env, jobject obj, jlong nativeData, jint rule) {
   try {
      return ((IDirectFBSurface *)nativeData)->SetPorterDuff((DFBSurfacePorterDuffRule)rule);
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
      return;
   }
}

void Java_vdr_mhp_awt_MHPNativeGraphics_enterBuffered(JNIEnv* env, jobject obj, jlong nativeFlipData) {
   ((FlipData *)nativeFlipData)->enterBuffered();
}

void Java_vdr_mhp_awt_MHPNativeGraphics_leaveBuffered(JNIEnv* env, jobject obj, jlong nativeFlipData) {
   ((FlipData *)nativeFlipData)->leaveBuffered();
}

/*void Java_vdr_mhp_awt_MHPNativeGraphics_addRefFlip(JNIEnv* env, jobject obj, int nativeFlipData) {
   ((FlipData *)nativeFlipData)->addRef(nativeFlipData);
}

void Java_vdr_mhp_awt_MHPNativeGraphics_removeRefFlip(JNIEnv* env, jobject obj, int nativeFlipData) {
   ((FlipData *)nativeFlipData)->removeRef(nativeFlipData);
}*/


} // extern "C"

