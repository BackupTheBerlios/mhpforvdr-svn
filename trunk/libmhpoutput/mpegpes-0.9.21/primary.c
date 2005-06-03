/*
   (c) Copyright 2000-2002  convergence integrated media GmbH.
   (c) Copyright 2002       convergence GmbH.
   
   All rights reserved.

   Written by Denis Oliver Kropp <dok@directfb.org>,
              Andreas Hundt <andi@fischlustig.de> and
              Sven Neumann <sven@convergence.de>.

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the
   Free Software Foundation, Inc., 59 Temple Place - Suite 330,
   Boston, MA 02111-1307, USA.
*/

#include "dfb_config/config.h"
#include <stdio.h>

#include <directfb.h>
                                   
#include <core/coredefs.h>
#include <core/coretypes.h>
#include <core/layers.h>
#include <core/palette.h>
#include <core/surfaces.h>
#include <core/system.h>

#include <gfx/convert.h>

#include <misc/conf.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <errno.h>
#include <unistd.h> 
#include <sys/mman.h> 
#include <syslog.h>

 
#include "shm.h"
#include "primary.h"
#include "plain_c_bridge.h"


static int
primaryLayerDataSize     ();
     
static int       
primaryRegionDataSize();

static DFBResult
primaryInitLayer                 ( CoreLayer                  *layer,
                                  void                       *driver_data,
                                  void                       *layer_data,
                                  DFBDisplayLayerDescription *description,
                                  DFBDisplayLayerConfig      *config,
                                  DFBColorAdjustment         *adjustment );
                                  
                                  
static DFBResult 
primaryGetCurrentOutputField           ( CoreLayer              *layer,
                                         void                   *driver_data,
                                         void                   *layer_data,
                                         int                    *field );
                                  
static DFBResult 
primaryGetLevel                        ( CoreLayer              *layer,
                                         void                   *driver_data,
                                         void                   *layer_data,
                                         int                    *level );
static DFBResult 
primarySetLevel                       ( CoreLayer              *layer,
                                         void                   *driver_data,
                                         void                   *layer_data,
                                         int                     level );
static DFBResult
primarySetColorAdjustment             ( CoreLayer              *layer,
                                         void                   *driver_data,
                                         void                   *layer_data,
                                         DFBColorAdjustment     *adjustment );
     /*
      * Check all parameters and return if this region is supported.
      */
static DFBResult 
primaryTestRegion              ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 CoreLayerRegionConfig      *config,
                                 CoreLayerRegionConfigFlags *failed );

     /*
      * Add a new region to the layer, but don't program hardware, yet.
      */
static DFBResult
primaryAddRegion               ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 void                       *region_data,
                                 CoreLayerRegionConfig      *config );

     /*
      * Setup hardware, called once after AddRegion() or when parameters
      * have changed. Surface and palette are only set if updated or new.
      */
static DFBResult 
primarySetRegion              ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 void                       *region_data,
                                 CoreLayerRegionConfig      *config,
                                 CoreLayerRegionConfigFlags  updated,
                                 CoreSurface                *surface,
                                 CorePalette                *palette );

     /*
      * Remove a region from the layer.
      */
static DFBResult 
primaryRemoveRegion            ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 void                       *region_data );

     /*
      * Flip the surface of the region.
      */
static DFBResult 
primaryFlipRegion              ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 void                       *region_data,
                                 CoreSurface                *surface,
                                 DFBSurfaceFlipFlags         flags );

     /*
      * Indicate updates to the front buffer content.
      */
static DFBResult 
primaryUpdateRegion            ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 void                       *region_data,
                                 CoreSurface                *surface,
                                 DFBRegion                  *update );

     /*
      * Control hardware deinterlacing.
      */
static DFBResult 
primarySetInputField           ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 void                       *region_data,
                                 int                         field );


   /** Override defaults. Subject to change. **/

     /*
      * Allocate the surface of the region.
      */
static DFBResult primaryAllocateSurface   ( CoreLayer              *layer,
                                     void                   *driver_data,
                                     void                   *layer_data,
                                     void                   *region_data,
                                     CoreLayerRegionConfig  *config,
                                     CoreSurface           **ret_surface );

     /*
      * Reallocate the surface of the region.
      */
static DFBResult 
primaryReallocateSurface           ( CoreLayer              *layer,
                                     void                   *driver_data,
                                     void                   *layer_data,
                                     void                   *region_data,
                                     CoreLayerRegionConfig  *config,
                                     CoreSurface            *surface );
                             
                                  
                                  
                                  
                                  
                                  

DisplayLayerFuncs shmPrimaryLayerFuncs = {
     LayerDataSize:      primaryLayerDataSize,
     RegionDataSize:     primaryRegionDataSize,
     InitLayer:          primaryInitLayer,
     GetCurrentOutputField:   primaryGetCurrentOutputField,
     GetLevel:           primaryGetLevel,
     SetLevel:           primarySetLevel,
     SetColorAdjustment: primarySetColorAdjustment,
     
     TestRegion:         primaryTestRegion,
     AddRegion:          primaryAddRegion,
     SetRegion:          primarySetRegion,
     RemoveRegion:       primaryRemoveRegion,
     FlipRegion:         primaryFlipRegion,
     UpdateRegion:       primaryUpdateRegion,     
     SetInputField:      primarySetInputField,
     AllocateSurface:    primaryAllocateSurface,
     ReallocateSurface:  primaryReallocateSurface,
     DeallocateSurface:  NULL          
};


static DFBResult
primaryInitScreen( CoreScreen           *screen,
                   GraphicsDevice       *device,
                   void                 *driver_data,
                   void                 *screen_data,
                   DFBScreenDescription *description )
{
     /* Set the screen capabilities. */
     description->caps = DSCCAPS_NONE;

     /* Set the screen name. */
     snprintf( description->name,
               DFB_SCREEN_DESC_NAME_LENGTH, "MPEGPES Primary Screen" );

     return DFB_OK;
}

static DFBResult
primaryGetScreenSize( CoreScreen *screen,
                      void       *driver_data,
                      void       *screen_data,
                      int        *ret_width,
                      int        *ret_height )
{
     /*D_ASSERT( dfb_sdl != NULL );

     if (dfb_sdl->primary) {
          *ret_width  = dfb_sdl->primary->width;
          *ret_height = dfb_sdl->primary->height;
     }
     else {
          if (dfb_config->mode.width)
               *ret_width  = dfb_config->mode.width;
          else
               *ret_width  = 640;

          if (dfb_config->mode.height)
               *ret_height = dfb_config->mode.height;
          else
               *ret_height = 480;
     }*/
     *ret_width  = mpegpes_get_default_width();
     *ret_height = mpegpes_get_default_height();

     return DFB_OK;
}

ScreenFuncs shmPrimaryScreenFuncs = {
     .InitScreen    = primaryInitScreen,
     .GetScreenSize = primaryGetScreenSize
};

//static DFBResult
//update_screen( CoreSurface *surface, int x, int y, int w, int h );

//static SDL_Surface *screen = NULL;



/** primary layer functions **/

static int
primaryLayerDataSize     ()
{
     return 0;
}

static int       
primaryRegionDataSize() {
   return 0;
}

     
static DFBResult
primaryInitLayer         ( CoreLayer                  *layer,
                           void                       *driver_data,
                           void                       *layer_data,
                           DFBDisplayLayerDescription *desc,
                           DFBDisplayLayerConfig      *default_config,
                           DFBColorAdjustment         *adjustment
                         )
{
     /* set capabilities and type */
     desc->caps = DLCAPS_SURFACE;
     desc->type = DLTF_ALL;

     /* set name */
     snprintf( desc->name,
               DFB_DISPLAY_LAYER_DESC_NAME_LENGTH, "MPEG PES Primary Layer" );
     desc->regions=1; //or 0?
     desc->level=0; //?
               
     adjustment->flags=DCAF_NONE;

     /* fill out the default configuration */
     default_config->flags       = DLCONF_WIDTH | DLCONF_HEIGHT |
                                   DLCONF_PIXELFORMAT | DLCONF_BUFFERMODE;
     //default_config->buffermode  = DLBM_BACKSYSTEM;
     default_config->buffermode  = DLBM_FRONTONLY;

     default_config->width  = mpegpes_get_default_width();
     default_config->height = mpegpes_get_default_height();
     /*if (dfb_config->mode.width)
          default_config->width  = dfb_config->mode.width;
     else
          default_config->width  = 720;

     if (dfb_config->mode.height)
          default_config->height = dfb_config->mode.height;
     else
          default_config->height = 576;*/
     
     /*if (dfb_config->mode.format != DSPF_UNKNOWN)
          default_config->pixelformat = dfb_config->mode.format;
     else if (dfb_config->mode.depth > 0)
          default_config->pixelformat = dfb_pixelformat_for_depth( dfb_config->mode.depth );
     else
          default_config->pixelformat = DSPF_RGB16;
     */
     
     #ifdef MPEGPES_LAYER_I420
     default_config->pixelformat = DSPF_I420;
     #else
     default_config->pixelformat = DSPF_RGB24;
     #endif
     
     printf("primaryInitLayer: %d %d %d %d %d\n", default_config->width, default_config->height, default_config->pixelformat, default_config->buffermode, default_config->options);
     
     //fusion_skirmish_prevail( &dfb_shm_lock );
     
     /* Set video mode */
     /*if ( (screen=SDL_SetVideoMode(default_config->width,
                                   default_config->height,
                                   DFB_BITS_PER_PIXEL(default_config->pixelformat),
                                   SDL_HWSURFACE | SDL_DOUBLEBUF)) == NULL ) {
             ERRORMSG("Couldn't set %dx%dx%d video mode: %s\n",
                      default_config->width, default_config->height,
                      DFB_BITS_PER_PIXEL(default_config->pixelformat), SDL_GetError());
             fusion_skirmish_dismiss( &dfb_shm_lock );
             return DFB_FAILURE;
     }*/
     
     
     
     
     //fusion_skirmish_dismiss( &dfb_shm_lock );
     
     return DFB_OK;
}

static DFBResult 
primaryGetCurrentOutputField           ( CoreLayer              *layer,
                                         void                   *driver_data,
                                         void                   *layer_data,
                                         int                    *field )
{
   return DFB_UNSUPPORTED;
}


static DFBResult 
primaryGetLevel                        ( CoreLayer              *layer,
                                         void                   *driver_data,
                                         void                   *layer_data,
                                         int                    *level )
{
   level=0;
}


static DFBResult 
primarySetLevel                       ( CoreLayer              *layer,
                                         void                   *driver_data,
                                         void                   *layer_data,
                                         int                     level )
{
   return DFB_UNSUPPORTED;
}


static DFBResult
primarySetColorAdjustment             ( CoreLayer              *layer,
                                         void                   *driver_data,
                                         void                   *layer_data,
                                         DFBColorAdjustment     *adjustment )
{
   return DFB_UNSUPPORTED;
}



     /*
      * Check all parameters and return if this region is supported.
      */
static DFBResult 
primaryTestRegion              ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 CoreLayerRegionConfig      *config,
                                 CoreLayerRegionConfigFlags *failed )
{
     CoreLayerRegionConfigFlags fail = 0;
     
     #ifdef MPEGPES_LAYER_I420
     if (config->format != DSPF_I420)
        fail |= CLRCF_FORMAT;
     #else
     if (config->format != DSPF_RGB24)
        fail |= CLRCF_FORMAT;
     #endif
        
     if (config->buffermode == DLBM_BACKVIDEO)
        fail |= CLRCF_BUFFERMODE;
        
     if (config->options != DLOP_NONE)
        fail |= CLRCF_OPTIONS;
        
     printf("primaryTestConfiguration: %d %d %d %d %d\n", config->width, config->height, config->format, config->buffermode, config->options);
     
     //currently no realloction of buffer
     /*else if (buffer_size != BUFFER_SIZE(config->width, config->height, DFB_BYTES_PER_PIXEL(config->pixelformat)))
        fail |= (DLCONF_WIDTH | DLCONF_HEIGHT | DLCONF_PIXELFORMAT);*/

/*     if (config->buffermode == DLBM_FRONTONLY)
          fail |= DLCONF_BUFFERMODE;*/

     if (failed)
          *failed = fail;

     if (fail)
          return DFB_UNSUPPORTED;

     return DFB_OK;
}

     /*
      * Add a new region to the layer, but don't program hardware, yet.
      */
static DFBResult
primaryAddRegion               ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 void                       *region_data,
                                 CoreLayerRegionConfig      *config )
{
   return DFB_OK;
}

     /*
      * Setup hardware, called once after AddRegion() or when parameters
      * have changed. Surface and palette are only set if updated or new.
      */
static DFBResult 
primarySetRegion              ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 void                       *region_data,
                                 CoreLayerRegionConfig      *config,
                                 CoreLayerRegionConfigFlags  updated,
                                 CoreSurface                *surface,
                                 CorePalette                *palette )
{
     printf("primarySetConfiguration: %d %d %d %d %d\n", config->width, config->height, config->format, config->buffermode, config->options);
     return mpegpes_set_configuration(config->width, config->height, config->format);
     //uint       flags;
     //CoreSurface *surface = dfb_layer_surface( layer );

     //flags = SDL_HWSURFACE;

     //if (config->buffermode != DLBM_FRONTONLY)
       //   flags |= SDL_DOUBLEBUF;

     //fusion_skirmish_prevail( &dfb_shm_lock );
     
     /* Set video mode */
     /*if ( (screen=SDL_SetVideoMode(config->width,
                                   config->height,
                                   DFB_BITS_PER_PIXEL(config->pixelformat),
                                   flags)) == NULL ) {
             ERRORMSG("Couldn't set %dx%dx%d video mode: %s\n",
                      config->width, config->height,
                      DFB_BITS_PER_PIXEL(config->pixelformat), SDL_GetError());
             fusion_skirmish_dismiss( &dfb_shm_lock );
             return DFB_FAILURE;
     }*/
     
     /*if ( config->pixelformat != DSPF_RGB24 ||
          buffer_size != BUFFER_SIZE(config->width, config->height, DFB_BYTES_PER_PIXEL(config->pixelformat))) 
        return DFB_UNSUPPORTED;
        
     //fusion_skirmish_dismiss( &dfb_shm_lock );
     
     surface->back_buffer->system.addr  = buffer;
     surface->back_buffer->system.pitch = SCANLINE_LENGTH(config->width, DFB_BYTES_PER_PIXEL(config->pixelformat));
     
     surface->front_buffer->system.addr  = buffer;
     surface->front_buffer->system.pitch = SCANLINE_LENGTH(config->width, DFB_BYTES_PER_PIXEL(config->pixelformat));
     */
     //return DFB_OK;
}

     /*
      * Remove a region from the layer.
      */
static DFBResult 
primaryRemoveRegion            ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 void                       *region_data )
{
   return DFB_OK;
}

     /*
      * Flip the surface of the region.
      */
static DFBResult 
primaryFlipRegion              ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 void                       *region_data,
                                 CoreSurface                *surface,
                                 DFBSurfaceFlipFlags         flags )
{
    printf("primaryFlipBuffers\n");
     return DFB_UNSUPPORTED;
     /*CoreSurface *surface = dfb_layer_surface( layer );

     dfb_surface_flip_buffers( surface );

     fusion_skirmish_prevail( &dfb_shm_lock );

     //SDL_Flip( screen );
     notifyChange();

     fusion_skirmish_dismiss( &dfb_shm_lock );

     surface->back_buffer->system.addr  = buffer;
     surface->back_buffer->system.pitch = SCANLINE_LENGTH(header->width, header->bytes_per_pixel);
     
     surface->front_buffer->system.addr  = buffer;
     surface->front_buffer->system.pitch = SCANLINE_LENGTH(header->width, header->bytes_per_pixel);
     
     return DFB_OK;*/
}
     
static DFBResult 
primaryUpdateRegion            ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 void                       *region_data,
                                 CoreSurface                *surface,
                                 DFBRegion                  *update )
{
     /*CoreSurface *surface = dfb_layer_surface( layer );

     if (!region)
          return update_screen( surface,
                                0, 0, surface->width, surface->height );

     return update_screen( surface,
                           region->x1, region->y1,
                           region->x2 - region->x1 + 1,
                           region->y2 - region->y1 + 1 );*/
     return mpegpes_update_region(update);
                           
     //return DFB_OK;
}
     
static DFBResult 
primarySetInputField           ( CoreLayer                  *layer,
                                 void                       *driver_data,
                                 void                       *layer_data,
                                 void                       *region_data,
                                 int                         field )
{
     return DFB_UNSUPPORTED;
}








static DFBResult primaryAllocateSurface   ( CoreLayer              *layer,
                                     void                   *driver_data,
                                     void                   *layer_data,
                                     void                   *region_data,
                                     CoreLayerRegionConfig  *config,
                                     CoreSurface           **ret_surface )
{
     DFBSurfaceCapabilities caps = DSCAPS_SYSTEMONLY;

     //if (config->buffermode != DLBM_FRONTONLY)
      //    caps |= DSCAPS_FLIPPING;
      
     //taken from dfb_surface_allocate_buffer, surfaces.c
     int pitch = DFB_BYTES_PER_LINE( config->format, config->width );
     if (pitch & 3)
        pitch += 4 - (pitch & 3);
         
     int size = DFB_PLANE_MULTIPLY( config->format, config->height  * pitch );
     printf("primaryAllocateSurface %d %d %d %d\n", config->width, config->height, pitch, size);
                                               
     void *buffer=mpegpes_allocate(config->width, config->height);
     
     if (!buffer)
        return DFB_FAILURE;

     return dfb_surface_create_preallocated( core, config->width, config->height,
                                config->format, CSP_SYSTEMONLY,
                                caps, NULL, buffer, NULL,
                                pitch, 0, ret_surface);
}

static DFBResult 
primaryReallocateSurface           ( CoreLayer              *layer,
                                     void                   *driver_data,
                                     void                   *layer_data,
                                     void                   *region_data,
                                     CoreLayerRegionConfig  *config,
                                     CoreSurface            *surface )
{
   printf("primaryReallocateSurface %d %d %d %d %d\n", config->width, config->height, config->format, config->buffermode, config->options);
   
   if (config->width != surface->width ||
       config->height != surface->height ||
       config->format != config->format) {
      printf("Real reallocation not supported");
      return DFB_UNSUPPORTED;
   }
         
   /*+switch (config->buffermode) {
      case DLBM_BACKSYSTEM:
            surface->caps |= DSCAPS_FLIPPING;
            surface->caps &= ~DSCAPS_TRIPLE;
            ret = dfb_surface_reconfig( surface,
                                       CSP_VIDEOONLY, CSP_SYSTEMONLY );
            break;
      case DLBM_FRONTONLY:
            surface->caps &= ~(DSCAPS_FLIPPING | DSCAPS_TRIPLE);
            ret = dfb_surface_reconfig( surface,
                                       CSP_VIDEOONLY, CSP_VIDEOONLY );
            break;
      case DLBM_TRIPLE:
      case DLBM_BACKVIDEO:
      case DLBM_WINDOWS:
            return DFB_UNSUPPORTED;
      
      default:
            printf("unknown buffermode");
            return DFB_BUG;
   }*/

   return DFB_OK;
     /*DFBResult ret;
     
     
     switch (config->buffermode) {
          case DLBM_BACKVIDEO:
          case DLBM_BACKSYSTEM:
               surface->caps |= DSCAPS_FLIPPING;

               ret = dfb_surface_reconfig( surface,
                                           CSP_SYSTEMONLY, CSP_SYSTEMONLY );
               break;

          case DLBM_FRONTONLY:
               surface->caps &= ~DSCAPS_FLIPPING;

               ret = dfb_surface_reconfig( surface,
                                           CSP_SYSTEMONLY, CSP_SYSTEMONLY );
               break;
          
          default:
               BUG("unknown buffermode");
               return DFB_BUG;
     }
     if (ret)
          return ret;

     ret = dfb_surface_reformat( surface, config->width,
                                 config->height, config->pixelformat );
     if (ret)
          return ret;

     if (config->options & DLOP_DEINTERLACING)
          surface->caps |= DSCAPS_INTERLACED;
     else
          surface->caps &= ~DSCAPS_INTERLACED;

     surface->width  = config->width;
     surface->height = config->height;
     surface->format = config->pixelformat;

     switch (config->buffermode) {
          case DLBM_BACKVIDEO:
          case DLBM_BACKSYSTEM:
               surface->caps |= DSCAPS_FLIPPING;
               break;

          case DLBM_FRONTONLY:
               surface->caps &= ~DSCAPS_FLIPPING;
               break;
          
          default:
               BUG("unknown buffermode");
               return DFB_BUG;
     }
     
     if (DFB_PIXELFORMAT_IS_INDEXED(config->pixelformat) && !surface->palette) {
          DFBResult    ret;
          CorePalette *palette;
           
          ret = dfb_palette_create( 256, &palette );
          if (ret)
               return ret;

          if (config->pixelformat == DSPF_LUT8)
               dfb_palette_generate_rgb332_map( palette );
          
          dfb_surface_set_palette( surface, palette );

          dfb_palette_unref( palette );
     }
     
     return DFB_OK;*/
}


/******************************************************************************/


/*static DFBResult
update_screen( CoreSurface *surface, int x, int y, int w, int h )
{
#if 0
     int          i;
     void        *dst;
     void        *src;
     int          pitch;
     DFBResult    ret;

     DFB_ASSERT( surface != NULL );
     
     if (SDL_LockSurface( screen ) < 0) {
          ERRORMSG( "DirectFB/SDL: "
                    "Couldn't lock the display surface: %s\n", SDL_GetError() );
          return DFB_FAILURE;
     }

     ret = dfb_surface_soft_lock( surface, DSLF_READ, &src, &pitch, true );
     if (ret) {
          ERRORMSG( "DirectFB/SDL: Couldn't lock layer surface: %s\n",
                    DirectFBErrorString( ret ) );
          SDL_UnlockSurface(screen);
          return ret;
     }

     dst = screen->pixels;

     src += DFB_BYTES_PER_LINE( surface->format, x ) + y * pitch;
     dst += DFB_BYTES_PER_LINE( surface->format, x ) + y * screen->pitch;

     for (i=0; i<h; ++i) {
          dfb_memcpy( dst, src,
                      DFB_BYTES_PER_LINE( surface->format, w ) );

          src += pitch;
          dst += screen->pitch;
     }

     dfb_surface_unlock( surface, true );
     
     SDL_UnlockSurface( screen );
#endif     
     
     fusion_skirmish_prevail( &dfb_shm_lock );
     
     SDL_UpdateRect( screen, x, y, w, h );
     
     fusion_skirmish_dismiss( &dfb_shm_lock );
     
     return DFB_OK;
}*/

