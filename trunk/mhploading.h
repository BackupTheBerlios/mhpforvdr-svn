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


#include <libait/applications.h>
#include <libservice/transportstream.h>

//Put this into an extra header because mhpcontrol.h has many header dependencies
//Implementation is in mhpcontrol.c

namespace Cache { class Cache; }
template <class T> class SmartPtr;

namespace Mhp {

enum LoadingState { LoadingStateError, LoadingStateWaiting, LoadingStateLoading, LoadingStateLoaded, LoadingStateHibernated };

class ProgressIndicator {
public:
   virtual void ShowProgress(int currentSize, int totalSize) = 0;
   virtual void SetApplicationName(const std::string &appName) = 0;
   virtual void HideProgress() = 0;
};

class LoadingManager {
public:
   virtual ~LoadingManager();
   static LoadingManager *getManager();
   virtual void Initialize() = 0;
   virtual void CleanUp() = 0;
   
   //Loads the given application.
   virtual void Load(ApplicationInfo::cApplication::Ptr a, bool foreground = true) = 0;
   
   //Stop loading the given application
   virtual void Stop(ApplicationInfo::cApplication::Ptr a) = 0;
   
   //Get state for given application
   virtual LoadingState getState(ApplicationInfo::cApplication::Ptr a) = 0;
   
   //Get the cache the given application is stored in, if it is being loaded
   virtual SmartPtr<Cache::Cache> getCache(ApplicationInfo::cApplication::Ptr a) = 0;
   
   virtual void ProgressInfo(ProgressIndicator *pi) = 0;
   virtual void ChannelSwitch(const class cDevice *device, Service::TransportStreamID oldTs, Service::TransportStreamID newTs) = 0;
protected:
   LoadingManager();
   static LoadingManager *s_self;
};

class RunningManager {
public:
   virtual ~RunningManager();
   static RunningManager *getManager();
   virtual void Initialize() = 0;
   virtual void CleanUp() = 0;
   
   //Start the given application
   virtual void Start(ApplicationInfo::cApplication::Ptr a) = 0;
   
   //Stop the given application, return control to VDR if no other application remains running
   virtual void Stop(ApplicationInfo::cApplication::Ptr a) = 0;
   
   //Stop all applications, return control to VDR
   virtual void Stop() = 0;
   
   //Inform manager that the given application has been started
   virtual void ApplicationStarted(ApplicationInfo::cApplication::Ptr a) = 0;
   
   //Inform manager that the given application is no longer running;
   //the manager may return control to VDR if no other application remains running
   virtual void ApplicationStopped(ApplicationInfo::cApplication::Ptr a) = 0;
protected:
   RunningManager();
   static RunningManager *s_self;
};

}

#endif

