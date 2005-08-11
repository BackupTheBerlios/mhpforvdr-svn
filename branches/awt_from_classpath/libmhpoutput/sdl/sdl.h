/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
#ifndef MHPOUTPUT_SDL_SDL_H
#define MHPOUTPUT_SDL_SDL_H
 
#include <libmhpoutput/output.h>


namespace MhpOutput {


class SdlSystem : public System {
public:
   SdlSystem();
   virtual ~SdlSystem();
   virtual IDirectFB *Interface() { return dfb; };
   
   IDirectFBDisplayLayer *GetMainLayer() { return layer; }
   bool HasVideoLayer() { return false; }
   IDirectFBDisplayLayer *GetVideoLayer() { return GetMainLayer(); }
   bool HasBackgroundLayer() { return false; }
   IDirectFBDisplayLayer *GetBackgroundLayer() { return GetMainLayer(); }
protected:
   virtual bool Initialize(const char *arg);
   virtual void Activate(class Player *player, bool On);
private:
   IDirectFB *dfb;
   IDirectFBDisplayLayer *layer;
};



}

#endif
