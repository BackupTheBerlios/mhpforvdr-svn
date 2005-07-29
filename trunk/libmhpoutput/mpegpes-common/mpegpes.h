/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef MHPOUTPUT_MPEGPES_MPEGPES_H
#define MHPOUTPUT_MPEGPES_MPEGPES_H

#include <endian.h>
#include <time.h>

#include <vdr/thread.h>

#include <dfb++/dfb++.h>
#include <libmhpoutput/output.h>
#include <libmhpoutput/avcodecsupport.h>

#include "pesheaders.h"

namespace MhpOutput {

class TwoPointers {
public:
   TwoPointers();
   void Set(unsigned char *a, unsigned char *b);
   unsigned char *getNextForWriting();
   void releaseChanged(unsigned char *);
   unsigned char *getNextForReading();
   void releaseAvailable(unsigned char *);
private:
   unsigned char *p[2];
   enum State { Available, Writing, Changed, Reading };
   State state[2];
   //cMutex mutex;
};

class PTSGenerator {
public:
   PTSGenerator();
   void Start(unsigned long long startPts);
   void Adjust(unsigned long long pts);
   unsigned long long GetTimestamp();
private:
   unsigned long long cur_pts;
   struct timeval cur_time;
   double speed_factor;
};

#define PES_MAX_SIZE 2048
class PESBuilder : protected PTSGenerator {
public:
   PESBuilder();
   void SendData(unsigned char *data, uint length);
protected:
   Player *player;
   PESHeader headerWithPts;
   PESHeader headerWithoutPts;
   PTS pts;
   unsigned long long timestamp;
};

class Encoder : public cThread, protected PESBuilder, public TwoPointers {
public:
   Encoder();
   ~Encoder();
   bool SetConfiguration(int width, int height, DFBSurfacePixelFormat pixelformat);
   void *GetBuffer(int width, int height);
   void UpdateRegion(DFBRegion *region);
   int width, height;
   void Activate(Player *player, bool On);
protected:
   virtual void Action();
   bool Initialize();
   IDirectFBSurface *primarySurface;
   bool init;
   AVCodec *avCodec;
private:
   bool running;
   bool open;
   AVCodecContext *avContext;
   AVFrame *avFrame1, *avFrame2, *avFrameRGB;
   unsigned char *mpegBuffer;
   int mpegBufferSize, mpegSize;
   cMutex mutex;
   cCondVar condVar;
};

class MpegPesSystem : public System, public Encoder {
public:
   MpegPesSystem();
   ~MpegPesSystem();
   virtual IDirectFB *Interface() { return dfb; }
   
   IDirectFBDisplayLayer *GetMainLayer() { return layer; }
   bool HasVideoLayer() { return false; }
   IDirectFBDisplayLayer *GetVideoLayer() { return GetMainLayer(); }
   bool HasBackgroundLayer() { return false; }
   IDirectFBDisplayLayer *GetBackgroundLayer() { return GetMainLayer(); }
protected:
   virtual bool Initialize(const char *arg);
   virtual void Activate(Player *player, bool On) { return Encoder::Activate(player, On); }
private:
   IDirectFB *dfb;
   IDirectFBDisplayLayer *layer;
   //static DFBEnumerationResult layerCallback(DFBDisplayLayerID layer_id, DFBDisplayLayerDescription desc, void *callbackdata);
};



}




#endif
