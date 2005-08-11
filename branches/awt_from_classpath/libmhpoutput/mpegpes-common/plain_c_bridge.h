
//Currently, DirectFB does not support colorspace conversion RGB->YUV.
//This means, we have to provide an RGB buffer and convert it to YUV with
//libavcodec while encoding.
//The idea was to have a base layer with I420 and all windows above it (HScenes)
//in ARGB and let DirectFB do the conversion only for changes.
//But currently, this does not work, see above. So only uncomment this if/when
//DirectFB includes heavily optimized RGB->YUV conversion support.
//#define MPEGPES_LAYER_I420

//the files of the DirectFB output driver must be compiled with plain old C gcc,
//g++ fails.
//These functions are implemented in the C++ world.
     
extern int   mpegpes_get_default_width();
extern int   mpegpes_get_default_height();
extern DFBResult  mpegpes_set_configuration(int width, int height, DFBSurfacePixelFormat pixelformat);
extern DFBResult  mpegpes_update_region(DFBRegion *region);
extern void *mpegpes_allocate(int width, int height);
