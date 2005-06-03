/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef LIBMHPOUTPUT_AVCODECSUPPORT_H
#define LIBMHPOUTPUT_AVCODECSUPPORT_H

#include "output.h"

extern "C" {
#include <ffmpeg/avcodec.h>
}

//This file contains some classes that are helpful if libavcodec
//is used to implement parts or all of the output system.

namespace MhpOutput {

class AVCodecInitialisation {
public:
   static void Check();
private:
   static bool isInitialised;
};

/*class AVCodecDripFeed : public DripFeedDecoder {
public:
   AVCodecDripFeed();
   ~AVCodecDripFeed();
   virtual bool Decode(const unsigned char *data, int length);
protected:
   AVCodec *avCodec;
   AVCodecContext *avContext;
   AVFrame *avFrame;
   bool frameFinished;
   AVFrame *pFrameRGB;
   uint8_t *rgbBuffer;
};*/


}

#endif
