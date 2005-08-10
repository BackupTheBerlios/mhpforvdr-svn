/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef LIBMHPOUTPUT_OUTPUT_H
#define LIBMHPOUTPUT_OUTPUT_H

#include <vdr/player.h>
#include <vdr/device.h>

#include <dfb++/dfb++.h>

//little extension to dfb++
#ifndef DFB_ADD_WINDOW_DESC
#define DFB_ADD_WINDOW_DESC(d,f)   (d) = (DFBWindowDescriptionFlags)  ((d) | (f))
#endif
#ifndef DFB_ADD_WINDOW_CAPS
#define DFB_ADD_WINDOW_CAPS(c,f)   (c) = (DFBWindowCapabilities)      ((c) | (f))
#endif


namespace MhpOutput {

//Enforce explicit, C++ style cast to enforce type safety.
//If PluginClass would not inherit MhpOutput::System, strange crashes will occur.
#define MHPOUTPUTPLUGINCREATOR(PluginClass)      \
   extern "C" void *MhpOutputPluginCreator(void) \
   { return static_cast<MhpOutput::System *>(new PluginClass()); }

#define PAL_WIDTH   720
#define PAL_HEIGHT  576
#define NTSC_WIDTH  720
#define NTSC_HEIGHT 480

enum VideoFormat {
   FourToThree,
   SixteenToNine
};

/*
The DripFeedDecoder functionality is now done by DirectFB directly.
It is no longer necessary here in libmhpoutput-

class DripFeedDecoder {
public:
   //Decodes given MPEG1 frame and draws to surface associated with this decoder.
   //Returns true on success.
   virtual bool Decode(const unsigned char *data, int length) = 0;
protected:
   friend class System;
   DripFeedDecoder() : inUse(false), surface(0) {}
   bool inUse;
   IDirectFBSurface *surface;
};*/

class System {
friend class Administration;
public:
   static System *self() { return s_self; }
   virtual IDirectFB *Interface() = 0;
   virtual eVideoSystem GetVideoSystem();
   virtual VideoFormat GetVideoFormat();
   virtual int GetDisplayWidth();
   virtual int GetDisplayHeight();
   //returns the uppermost, graphics capable layer.
   //A system may only provide one layer, the main layer.
   virtual IDirectFBDisplayLayer *GetMainLayer() = 0;
   //returns whether the output system provides an additional independent video layer
   //below the main layer
   virtual bool HasVideoLayer() = 0;
   //if HasVideoLayer() is true, returns the video layer,
   //else returns the main layer.
   virtual IDirectFBDisplayLayer *GetVideoLayer() = 0;
   //returns whether the output system provides an additional independent background layer
   //below the any other layer
   virtual bool HasBackgroundLayer() = 0;
   //if HasBackgroundLayer() is true, returns the background layer,
   //else returns the main layer.
   virtual IDirectFBDisplayLayer *GetBackgroundLayer() = 0;
   /*
   //if available, reserves a drip feed decoder for exclusive use
   //returns a pointer to a drip feed decoder drawing to the specified surface.
   //Returns 0 if no decoder is available.
   virtual DripFeedDecoder *getDripFeedDecoder(IDirectFBSurface *surface);
   //releases the dripFeedDecoder received via getDripFeedDecoder
   virtual void releaseDripFeedDecoder(DripFeedDecoder *decoder);
   */
protected:
   System();
   virtual ~System();
   friend class Player;
   //Do initialization here, especially DirectFB startup.
   //A return value of false indicates a severe error which
   //makes using the output system impossible.
   virtual bool Initialize(const char *arg);
   //Notifies output system when output is started/stopped. MPEG data
   //can be fed into provided player.
   virtual void Activate(class Player *player, bool On) = 0;
   //DripFeedDecoder *dripFeed;
private:
   static System *s_self;
   void *dlhandle;
};

class Player : public cPlayer {
public:
   Player(ePlayMode PlayMode = pmAudioVideo) : cPlayer(PlayMode) {}
   int PlayVideo(const uchar *Data, int Length) { return cPlayer::PlayPes(Data, Length); }   
protected:
   virtual void Activate(bool On) { System::self()->Activate(this, On); }
};



}

#endif

