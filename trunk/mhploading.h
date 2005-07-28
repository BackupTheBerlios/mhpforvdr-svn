/***************************************************************************
 *       Copyright (c) 2004 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

 
#ifndef MHP_MHPLOADING_H
#define MHP_MHPLOADING_H

#include <map>
#include <string>
#include <time.h>

#include <vdr/thread.h>

#include <libait/applications.h>
#include <libservice/transportstream.h>

//Put this into an extra header because mhpcontrol.h has many header dependencies
//Implementation is in mhpcontrol.c


enum LoadingState { LoadingStateError, LoadingStateWaiting, LoadingStateLoading, LoadingStateLoaded, LoadingStateHibernated };

class ProgressIndicator {
public:
   virtual void ShowProgress(float progress, int currentSize, int totalSize) = 0;
   virtual void SetApplicationName(const std::string &appName) = 0;
   virtual void HideProgress() = 0;
};

class MhpCarouselLoader;
class MhpChannelWatch;
class MhpCarouselPreloader;
class MhpServiceSelectionProvider;
namespace Cache { class Cache; }
template <class T> class SmartPtr;

class MhpLoadingManager : public ApplicationInfo::cApplicationStatus {
public:
   ~MhpLoadingManager();
   static MhpLoadingManager *getManager();
   static void CleanUp();
   
   //Loads the given application.
   void Load(ApplicationInfo::cApplication::Ptr a, bool foreground = true);
   
   //Stop loading the given application
   void Stop(ApplicationInfo::cApplication::Ptr a);
   
   //Get state for given application
   LoadingState getState(ApplicationInfo::cApplication::Ptr a);
   
   //Get the cache the given application is stored in, if it is being loaded
   SmartPtr<Cache::Cache> getCache(ApplicationInfo::cApplication::Ptr a);
   
   void ChannelSwitch(const class cDevice *device, Service::TransportStreamID oldTs, Service::TransportStreamID newTs);
   void OnceASecond(ProgressIndicator *pi);
protected:
   MhpLoadingManager();
   //ApplicationStatus interface
   virtual void NewApplication(ApplicationInfo::cApplication::Ptr app);
   virtual void ApplicationRemoved(ApplicationInfo::cApplication::Ptr app);
   void Load(MhpCarouselLoader *l, bool foreground);
   void Stop(MhpCarouselLoader *l);
private:
   //void Hibernate(ApplicationInfo::cApplication::Ptr a);
   static MhpLoadingManager *s_self;
   typedef std::map<ApplicationInfo::cApplication::Ptr , MhpCarouselLoader *> AppMap;
   AppMap apps;
   MhpChannelWatch *watch;
   MhpCarouselPreloader *preloader;
   MhpServiceSelectionProvider *selectionProvider;
   int hibernatedCount;
   cMutex mutex;
   MhpCarouselLoader *loadingApp;
};



#endif

