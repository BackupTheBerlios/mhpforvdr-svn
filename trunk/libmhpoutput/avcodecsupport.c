/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/


#include "avcodecsupport.h"

namespace MhpOutput {


bool AVCodecInitialisation::isInitialised = false;

void AVCodecInitialisation::Check() {
   if (!isInitialised) {
      avcodec_init();
      avcodec_register_all();
   }
}

/*
This code is neither tested nor complete.
It is abandoned since I saw the DirectFB provides this functionality out of the box.

AVCodecDripFeed::AVCodecDripFeed() {
   frameFinished=false;
   AVCodecInitialisation::Check();
   avContext = avcodec_alloc_context();
   avFrame = avcodec_alloc_frame();
   
   //these are only used for optional colorspace conversion
   AVFrame *pFrameRGB=0;
   uint8_t *rgbBuffer=0;
   
   if (!(avCodec = avcodec_find_decoder(CODEC_ID_MPEG1VIDEO)) ) {
       esyslog("MhpOutput: Encoder: Fatal: codec not found. Expect crash.");
       return;
   }
}

AVCodecDripFeed::~AVCodecDripFeed() {
   if (open)
      avcodec_close(avContext);
   free(avFrame);
   free(avContext);
   
   //only used optionally
   free(pFrameRGB);
   delete rgbBuffer;
}

bool AVCodecDripFeed::Decode(const unsigned char *data, int length) {
   int bytes_decoded=avcodec_decode_video(avContext, avFrame, &frameFinished, data, length);
   if (bytes_decoded < 0) {
      printf("AVCodecDripFeed: Error while decoding frame");
   }
   if (frameFinished) {
      //I don't think this will ever change, but check here.
      if (avContext->pix_fmt != PIX_FMT_YUV420P) {
         printf("AVCodecDripFeed: Unexpected error: MPEG2 pixel format is not YUV420P");
         return;
      }
      
      switch(surface->GetPixelFormat()) {
      case DSPF_I420:
         surface->Lock(
         break;
      case DSPF_ARGB:
         {
            static AVFrame *pFrameRGB=0;
            int     numBytes;
            static uint8_t *buffer=0;
            
            if (!pFrameRGB) {
               // Allocate an AVFrame structure
               pFrameRGB=avcodec_alloc_frame();
               
               // Determine required buffer size and allocate buffer
               numBytes=avpicture_get_size(PIX_FMT_RGBA32, pCodecCtx->width,
                  pCodecCtx->height);
               rgbBuffer=new uint8_t[numBytes];
               
               // Assign appropriate parts of buffer to image planes in pFrameRGB
               avpicture_fill((AVPicture *)pFrameRGB, rgbBuffer, PIX_FMT_RGBA32, avContext->width, avContext->height);
            }
            img_convert((AVPicture *)pFrameRGB, PIX_FMT_RGBA32, (AVPicture*)avFrame, avContext->pix_fmt, avContext->width, avContext->height);
         }
      default:
         printf("Unsupported DirectFB pixel format %d in AVCodecDripFeed", surface->GetPixelFormat());
      }
   }
}
*/

}

