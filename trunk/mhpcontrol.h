/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *       parts taken from MP3 plugin,                                      *
 *          (c) 2001,2002 Stefan Huelswitt <huels@iname.com>               *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef __MHPCONTROL_H
#define __MHPCONTROL_H


#include "mhploading.h"

#include <vdr/player.h>

#include <libmhpoutput/output.h>
#include <libait/applications.h>
#include <libdsmccreceiver/receiver.h>
#include <libdsmccreceiver/cache.h>


class MhpControl;
class MhpPlayer : public MhpOutput::Player {
public:
   MhpPlayer(MhpControl *c) : control(c) {}
   ~MhpPlayer();
   void ActivateParent();
protected:
   virtual void Activate(bool On);
private:
   MhpControl *control;
};

class MhpCarouselLoader {
public:
   MhpCarouselLoader(ApplicationInfo::cApplication *a);
   ~MhpCarouselLoader();
   LoadingState getState();
   float getProgress(int &currentSize, int &totalSize);
   void Start();
   void Stop();
   void Hibernate();
   void WakeUp();
   SmartPtr<Cache::Cache> getCache();
   ApplicationInfo::cApplication::ApplicationName *getName();
   //only meaningful if hibernated
   time_t getHibernationTime() { return hibernatedTime; }
protected:
   void StartObjectCarousel(Dsmcc::ObjectCarousel *hibernated = 0);
   void StartLocalApp();
   ApplicationInfo::cApplication *app;
   cDsmccReceiver *receiver;
   Dsmcc::ObjectCarousel *carousel;
   LoadingState state;
   ApplicationInfo::cTransportProtocol::Protocol protocol;
   time_t hibernatedTime;
   int totalSize;
};

class cSkinDisplayReplay;
class MhpControl : public cControl, public ProgressIndicator {
public:
   MhpControl(ApplicationInfo::cApplication *a);
   virtual ~MhpControl();
   
   //entry point, from MhpApplicationMenu
   static void Start(ApplicationInfo::cApplication *a);
   
   //cControl interface
   virtual void Hide(void);
   virtual eOSState ProcessKey(eKeys Key);
   void Stop();
   
   //called from MhpPlayer::Activate
   void StartMhp();
   
   //ProgressIndicator interface
   
   virtual void ShowProgress(float progress, int currentSize, int totalSize);
   virtual void SetApplicationName(const std::string &appName);
   virtual void HideProgress();
private:
   class cMyApplicationStatus : public ApplicationInfo::cApplicationStatus {
   protected:
      virtual void NewApplication(ApplicationInfo::cApplication *app);
      virtual void ApplicationRemoved(ApplicationInfo::cApplication *app);
   };
   ApplicationInfo::cApplication *app;
   cMyApplicationStatus *monitor;
   MhpPlayer *player;
   enum MhpControlStatus { Waiting, Running, Stopped } status;
   static void CheckMessage();
   bool visible;
   bool doShow;
   cSkinDisplayReplay *display;
   std::string appName;
   std::string unknownName;
};

#endif


