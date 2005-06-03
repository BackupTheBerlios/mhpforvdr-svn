/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include "sdl.h"

//This "output system" simply starts DirectFB with its 
//SDL output activated. Currently nothing else is done (but should be).

namespace MhpOutput {

SdlSystem::SdlSystem() :dfb(0), layer(0) {
   //do everything in Initialize()
}

SdlSystem::~SdlSystem() {
   //order is important, don't destroy a layer after dfb
   if (dfb) {
      if (layer) {
         layer->Release();
      }
      dfb->Release();
   }
}

void SdlSystem::Initialize(const char *arg) {
   const char *dfbargs="DFBARGS";
   const char *newArgs="system=sdl";
   char *previousArgs=getenv(dfbargs);
   
   if (previousArgs) {
      int len=strlen(newArgs);
      int prevLen=strlen(previousArgs);
      char args[len+prevLen+2];
      strcpy(args, previousArgs);
      args[prevLen]=' ';
      strcpy(args+prevLen+1, newArgs);
      setenv(dfbargs, args, true);
   } else {
      setenv(dfbargs, newArgs, true);
   }
   DirectFB::Init();
   
   try {
      dfb=DirectFB::Create();
      layer=dfb->GetDisplayLayer(DLID_PRIMARY);
      layer->SetCooperativeLevel(DLSCL_ADMINISTRATIVE);
   } catch (DFBException *e) {
        esyslog("MhpOutput: SDL: Error %s, %s. Expect crash.", e->GetAction(), e->GetResult());
        delete e;
        return;
   }
   
   try {
      layer->EnableCursor(false);
      //layer->SetBackgroundColor(0,0,0,255); //or better white?
   } catch (DFBException *e) {
        esyslog("MhpOutput: SDL: Error %s, %s.", e->GetAction(), e->GetResult());
        delete e;
        return;
   }
}

void SdlSystem::Activate(class Player *player, bool On) {
   //TODO!
   //Here the output window on the desktop should somehow be hidden.
   //I do not know if this is feasible.
}




}

MHPOUTPUTPLUGINCREATOR(MhpOutput::SdlSystem)

