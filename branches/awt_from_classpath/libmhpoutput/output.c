/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include <dlfcn.h>
#include "output.h"

#include <vdr/config.h>
 
namespace MhpOutput {

System *System::s_self = 0;

System::System() {
   s_self=this;
   dlhandle=0;
}

System::~System() {
   s_self=0;
}

bool System::Initialize(const char *arg) {
   try {
      DirectFB::Init();
   } catch (DFBException *e) {
      esyslog("MhpOutput: DirectFB: Error %s, %s.", e->GetAction(), e->GetResult());
      delete e;
      return false;
   }
   return true;      
}

eVideoSystem System::GetVideoSystem() {
   return cDevice::PrimaryDevice()->GetVideoSystem();
}

VideoFormat System::GetVideoFormat() {
   return Setup.VideoFormat ? SixteenToNine : FourToThree;
}

int System::GetDisplayWidth() {
   return GetVideoSystem() == vsPAL ? PAL_WIDTH : NTSC_WIDTH;
}

int System::GetDisplayHeight() {
   return GetVideoSystem() == vsPAL ? PAL_HEIGHT: NTSC_HEIGHT;
}

/*DripFeedDecoder *System::getDripFeedDecoder(IDirectFBSurface *surface) {
   if (!dripFeed->inUse) {
      dripFeed->inUse=true;
      dripFeed->surface=surface;
      return dripFeed;
   }
   return 0;
}

void System::releaseDripFeedDecoder(DripFeedDecoder *decoder) {
   if (decoder->inUse) {
      decoder->inUse=false;
      decoder->surface=0;
   }
}*/


}
