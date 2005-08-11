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
#include <map>
#include <set>
#include <string>

#include <time.h>

#include <vdr/thread.h>
#include <vdr/player.h>
#include <vdr/status.h>

#include <libmhpoutput/output.h>
#include <libait/applications.h>
#include <libdvbsi/util.h>
#include <libdsmccreceiver/receiver.h>
#include <libdsmccreceiver/cache.h>
#include <libservice/servicecontext.h>

#include "mhploading.h"

class cSkinDisplayReplay;

namespace Mhp {

class Control;
class CarouselLoader;
class ChannelWatch;
class CarouselPreloader;
class ControlServiceSelectionProvider;

//the maximum number of apps which may concurrently be kept in hibernated state
#define MAX_HIBERNATED_APPS 15

class CarouselLoader {
public:
   CarouselLoader(ApplicationInfo::cApplication::Ptr a);
   ~CarouselLoader();
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

class ChannelWatch : public cStatus {
public:
   ChannelWatch(CarouselPreloader* preloader);
   Service::TransportStreamID getCurrentTransportStream() { return ts; }
protected:
  virtual void ChannelSwitch(const cDevice *Device, int ChannelNumber);
  CarouselPreloader* preloader;
  Service::TransportStreamID ts;
};

class CarouselPreloader : public DvbSi::SchedulerBySeconds {
public:
   CarouselPreloader();
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

class ControlServiceSelectionProvider : public Service::ServiceSelectionProvider {
public:
   ControlServiceSelectionProvider(ChannelWatch *watch);
   virtual void SelectService(cChannel *service);
   virtual void StopPresentation();
protected:
   ChannelWatch *watch;
};

class ControlLoadingManager : public LoadingManager, public ApplicationInfo::cApplicationStatus {
public:
   ControlLoadingManager();
   ~ControlLoadingManager();
   virtual void Initialize();
   virtual void CleanUp();
   
   //Loads the given application.
   virtual void Load(ApplicationInfo::cApplication::Ptr a, bool foreground = true);
   
   //Stop loading the given application
   virtual void Stop(ApplicationInfo::cApplication::Ptr a);
   
   //Get state for given application
   virtual LoadingState getState(ApplicationInfo::cApplication::Ptr a);
   
   //Get the cache the given application is stored in, if it is being loaded
   virtual SmartPtr<Cache::Cache> getCache(ApplicationInfo::cApplication::Ptr a);
   
   virtual void ChannelSwitch(const class cDevice *device, Service::TransportStreamID oldTs, Service::TransportStreamID newTs);
   virtual void ProgressInfo(ProgressIndicator *pi);
protected:
   //ApplicationStatus interface
   virtual void NewApplication(ApplicationInfo::cApplication::Ptr app);
   virtual void ApplicationRemoved(ApplicationInfo::cApplication::Ptr app);
   void Load(CarouselLoader *l, bool foreground);
   void Stop(CarouselLoader *l);
private:
   typedef std::map<ApplicationInfo::cApplication::Ptr , CarouselLoader *> AppMap;
   AppMap apps;
   ChannelWatch *watch;
   CarouselPreloader *preloader;
   ControlServiceSelectionProvider *selectionProvider;
   int hibernatedCount;
   cMutex mutex;
   CarouselLoader *loadingApp;
};

class Player : public MhpOutput::Player {
public:
   Player(Control *c) : control(c) {}
   ~Player();
   void ActivateParent();
protected:
   virtual void Activate(bool On);
private:
   Control *control;
};

class Control : public cControl, public ProgressIndicator {
public:
   Control();
   virtual ~Control();
   
   //cControl interface
   virtual void Hide(void);
   virtual eOSState ProcessKey(eKeys Key);
   
   //called from ControlRunningManager
   void Stop();
   void SetApplication(ApplicationInfo::cApplication::Ptr a);
   static void CheckMessage();
   
   //called from Player::Activate or ControlRunningManager
   void StartMhp();
   
   //ProgressIndicator interface   
   virtual void ShowProgress(float progress, int currentSize, int totalSize);
   virtual void SetApplicationName(const std::string &appName);
   virtual void HideProgress();
private:
   Player *player;
   ApplicationInfo::cApplication::Ptr app;
   enum ControlStatus { Waiting, Running, Stopped } status;
   bool visible;
   bool doShow;
   cSkinDisplayReplay *display;
   std::string appName;
   std::string unknownName;
};

class ControlRunningManager : public RunningManager, public ApplicationInfo::cApplicationStatus {
public:
   ControlRunningManager();
   ~ControlRunningManager();
   virtual void Initialize();
   virtual void CleanUp();
   
   //Start the given application
   virtual void Start(ApplicationInfo::cApplication::Ptr a);
   
   //Stop the given application, return control to VDR if no other application remains running
   virtual void Stop(ApplicationInfo::cApplication::Ptr a);
   
   //Stop all applications, return control to VDR
   virtual void Stop();
   
   //Inform manager that the given application has been started
   virtual void ApplicationStarted(ApplicationInfo::cApplication::Ptr a);
   
   //Inform manager that the given application is no longer running;
   //the manager may return control to VDR if no other application remains running
   virtual void ApplicationStopped(ApplicationInfo::cApplication::Ptr a);
   
   void ShutdownControl();
protected:
   //ApplicationStatus interface
   virtual void NewApplication(ApplicationInfo::cApplication::Ptr app);
   virtual void ApplicationRemoved(ApplicationInfo::cApplication::Ptr app);
private:
   typedef std::set<ApplicationInfo::cApplication::Ptr> AppList;
   AppList apps;
   ApplicationInfo::cApplication::Ptr foregroundApp;
   Control *control;
   cMutex mutex;
};


}

#endif


