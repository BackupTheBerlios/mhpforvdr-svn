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

//Put this into an extra header because mhpcontrol.h has many header dependencies
//Implementation is in mhpcontrol.c


enum LoadingState { LoadingStateError, LoadingStateWaiting, LoadingStateLoading, LoadingStateLoaded, LoadingStateHibernated };

//the maximum number of apps which may concurrently be kept in hibernated state
#define MAX_HIBERNATED_APPS 3

class ProgressIndicator {
public:
   virtual void ShowProgress(float progress, int currentSize, int totalSize) = 0;
   virtual void SetApplicationName(const std::string &appName) = 0;
   virtual void HideProgress() = 0;
};

class MhpCarouselLoader;
namespace Cache { class Cache; }
template <class T> class SmartPtr;
class MhpLoadingManager : public ApplicationInfo::cApplicationStatus {
public:
   ~MhpLoadingManager();
   static MhpLoadingManager *getManager();
   static void CleanUp();
   
   void Load(ApplicationInfo::cApplication *a);
   void Stop(ApplicationInfo::cApplication *a);
   LoadingState getState(ApplicationInfo::cApplication *a);
   SmartPtr<Cache::Cache> getCache(ApplicationInfo::cApplication *a);
   
   void OnceASecond(ProgressIndicator *pi);
protected:
   MhpLoadingManager();
   //ApplicationStatus interface
   virtual void NewApplication(ApplicationInfo::cApplication *app);
   virtual void ApplicationRemoved(ApplicationInfo::cApplication *app);
private:
   //void Hibernate(ApplicationInfo::cApplication *a);
   static MhpLoadingManager *s_self;
   typedef std::map<ApplicationInfo::cApplication *, MhpCarouselLoader *> AppMap;
   AppMap apps;
   int hibernatedCount;
   cMutex mutex;
   MhpCarouselLoader *loadingApp;
};



#endif

