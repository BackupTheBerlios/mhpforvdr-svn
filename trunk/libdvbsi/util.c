/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include <unistd.h>
#include "util.h"

namespace DvbSi {

bool TimedBySeconds::Check() {
   return time(0)-last > interval;
}

void TimedBySeconds::SetExecutingTime() {
   last=time(0);
}

SchedulerBySeconds::~SchedulerBySeconds() {
   running=false;
   //In theory, the mutex must be locked when broadcasting.
   //In practice, at this point (shutting down), the thread will somehow hang and needs to be cancelled.
   //I don't know why, same with DispatcherThread in database.c
   //cMutexLock lock(&schedulerMutex);
   sleepVar.Broadcast();
   Cancel(3);
}

void SchedulerBySeconds::Add(TimedObject *o, bool initialExecute) {
   cMutexLock lock(&schedulerMutex);
   list.push_back(o);
   o->SetExecutingTime();
   if (initialExecute)
      DoExecute(o);
   if (!running) {
      running=true;
      Start();
   }
}

void SchedulerBySeconds::Remove(TimedObject *o) {
   cMutexLock lock(&schedulerMutex);
   list.remove(o);
   if (!list.size())
      running=false;
}

void SchedulerBySeconds::ExecuteNow(TimedObject *o) {
   cMutexLock lock(&schedulerMutex);
   o->SetExecutingTime();
   DoExecute(o);
}

void SchedulerBySeconds::Action() {
   while (running) {
   
      cMutexLock lock(&schedulerMutex);
      
      for (std::list<TimedObject *>::iterator it=list.begin(); it != list.end(); ++it) {
         if ((*it)->Check()) {
            (*it)->SetExecutingTime();
            DoExecute(*it);
         }
      }
      
      for (int i=0; (i<granularity) && running; i++)
         sleepVar.TimedWait(schedulerMutex, 1000);
   
   }
}

IdTracker::~IdTracker() {
   delete ids;
}

bool IdTracker::isIncluded(int id) {
   if (size==-1)
      return true;
   if (!ids)
      return false;
   for (int i=0;i<size;i++) {
      if (ids[i]==id)
         return true;
   }
   return false;
}

void IdTracker::IncludeAll() {
   delete ids;
   ids=0;
   size=-1;
}

void IdTracker::Set(int id) {
   delete ids;
   ids=new int[1];
   size=1;
   ids[0]=id;
}

void IdTracker::Set(int *id, bool copy) {
   delete ids;
   if (copy) {
      ids=new int[size];
      memcpy(ids, id, sizeof(int)*size);
   } else
      ids=id;
}

ReadLock::ReadLock(class cRwLock *lock) : lock(lock) {
   lock->Lock(false);
}

ReadLock::~ReadLock() {
   lock->Unlock();
}


}
