/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef DVBSI_UTIL_H
#define DVBSI_UTIL_H

#include <time.h>
#include <list>

#include <vdr/thread.h>

/* Some utility classes used in the library */

namespace DvbSi {


class TimedObject {
public:
   //returns true if waiting time has elapsed
   virtual bool Check() = 0;
   //called immediately before Execute - this shall be overridden
   //only by subclasses implementing the timing facilities
   virtual void SetExecutingTime() {}
protected:
   friend class Scheduler;
   //actual method to be executed - this shall be overridden
   //only by subclasses using the timing facilities
   virtual void Execute() {}
};

class Scheduler {
public:
   virtual void Add(TimedObject *o, bool initialExecute=true) = 0;
   virtual void Remove(TimedObject *o) = 0;
   virtual void RemoveAll(bool deleteEntries=false) = 0;
   virtual void ExecuteNow(TimedObject *o) = 0;
protected:
   void DoExecute(TimedObject *o) { o->Execute(); }
};

class TimedBySeconds : public TimedObject {
public:
   TimedBySeconds(time_t seconds=15) : last(0), interval(seconds) {}
   virtual ~TimedBySeconds() {}
   virtual bool Check();
   virtual void SetExecutingTime();
   void ChangeInterval(time_t seconds) { interval=seconds; }
protected:
   time_t last;
   time_t interval;
};

class SchedulerBySeconds : public Scheduler, public cThread {
public:
   SchedulerBySeconds(int granularityInSeconds=2);
   virtual ~SchedulerBySeconds();
   virtual void Add(TimedObject *o, bool initialExecute=true);
   virtual void Remove(TimedObject *o);
   virtual void RemoveAll(bool deleteEntries=false);
   virtual void ExecuteNow(TimedObject *o);
protected:
   std::list<TimedObject *> list;
   virtual void Action();
   bool running;
   int granularity;
   cMutex schedulerMutex;
   cCondVar sleepVar;
   bool havingObjects;
};

class IdTracker {
public:
   IdTracker() : ids(0), size(-1) {}
   IdTracker(int id) : ids(0), size(-1) { Set(id); }
   IdTracker(int *id, bool copy=true) : ids(0), size(-1) { Set(id, copy); }
   
   ~IdTracker();
   
   bool isIncluded(int id);   
   
   void IncludeAll();
   void Set(int id);
   void Set(int *id, bool copy=true);
   bool isFinite() { return size != -1; }
   int getSize() { return size; }
private:
   int *ids;
   int size;
};

class ReadWriteLock {
public:
   ReadWriteLock(class cRwLock *lock, bool Write);
   ~ReadWriteLock();
private:
   class cRwLock *lock;
};

class ReadLock : public ReadWriteLock {
public:
   ReadLock(class cRwLock *lock) : ReadWriteLock(lock, false) {}
};

class WriteLock : public ReadWriteLock {
public:
   WriteLock(class cRwLock *lock) : ReadWriteLock(lock, true) {}
};


}



#endif
