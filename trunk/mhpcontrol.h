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

#include <list>

#include "mhploading.h"

#include <vdr/player.h>
#include <vdr/status.h>

#include <libmhpoutput/output.h>
#include <libait/applications.h>
#include <libdvbsi/util.h>
#include <libdsmccreceiver/receiver.h>
#include <libdsmccreceiver/cache.h>
#include <libservice/servicecontext.h>


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

//the maximum number of apps which may concurrently be kept in hibernated state
#define MAX_HIBERNATED_APPS 15

class MhpCarouselLoader {
public:
   MhpCarouselLoader(ApplicationInfo::cApplication::Ptr a);
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
    //reset to false at Stop() and Hibernate()
   void SetForeground() { foreground = true; }
   bool IsForeground() { return foreground; }
   bool ChannelSwitchedAway(const cDevice *device, Service::TransportStreamID oldTs, Service::TransportStreamID newTs);
protected:
   void StartObjectCarousel(Dsmcc::ObjectCarousel *hibernated = 0);
   void StartLocalApp();
   ApplicationInfo::cApplication::Ptr app;
   cDsmccReceiver *receiver;
   Dsmcc::ObjectCarousel *carousel;
   cDevice *filterDevice;
   LoadingState state;
   ApplicationInfo::cTransportProtocol::Protocol protocol;
   time_t hibernatedTime;
   int totalSize;
   bool foreground;
};

class MhpChannelWatch : public cStatus {
public:
   MhpChannelWatch(MhpCarouselPreloader* preloader);
   Service::TransportStreamID getCurrentTransportStream() { return ts; }
protected:
  virtual void ChannelSwitch(const cDevice *Device, int ChannelNumber);
  MhpCarouselPreloader* preloader;
  Service::TransportStreamID ts;
};

class MhpCarouselPreloader : public DvbSi::SchedulerBySeconds {
public:
   MhpCarouselPreloader();
   void PreloadForTransportStream(Service::TransportStreamID oldTs, Service::TransportStreamID newTs);
protected:
   class TimedPreloader : public DvbSi::TimedBySeconds {
   public:
      TimedPreloader(Service::TransportStreamID newTs);
   protected:
      virtual void Execute();
   private:
      bool loading;
      std::list<ApplicationInfo::cApplication::Ptr > apps;
      std::list<ApplicationInfo::cApplication::Ptr >::iterator currentPosition;
      Service::TransportStreamID ts;
   };
private:
   TimedPreloader *currentLoader;
   Service::TransportStreamID ts;
};

class MhpServiceSelectionProvider : public Service::ServiceSelectionProvider {
public:
   MhpServiceSelectionProvider(MhpChannelWatch *watch);
   virtual void SelectService(cChannel *service);
   virtual void StopPresentation();
protected:
   MhpChannelWatch *watch;
};

class cSkinDisplayReplay;
class MhpControl : public cControl, public ProgressIndicator {
public:
   MhpControl(ApplicationInfo::cApplication::Ptr a);
   virtual ~MhpControl();
   
   //entry point, from MhpApplicationMenu
   static void Start(ApplicationInfo::cApplication::Ptr a);
   
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
      virtual void NewApplication(ApplicationInfo::cApplication::Ptr app);
      virtual void ApplicationRemoved(ApplicationInfo::cApplication::Ptr app);
   };
   ApplicationInfo::cApplication::Ptr app;
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


