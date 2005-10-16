/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include <vector>

#include <vdr/channels.h>
#include <vdr/device.h>

#include "database.h"

namespace DvbSi {

Database::DatabaseArray Database::databases;
Database::DispatchThread *Database::dispatchThread=new Database::DispatchThread();

Database::Ptr Database::getDatabaseForTuner(Service::Tuner *tuner) {
   for (uint i=0;i<databases.size();i++)
      if (databases[i]->tuner == tuner)
         return databases[i];
   return 0;
}

bool Database::getDatabases(std::list<Database::Ptr> &list) {
   for (uint i=0;i<databases.size();i++)
      list.push_back(databases[i]);
   return databases.size();
}

/*
Database::Ptr Database::getDatabaseForChannel(int nid, int tid, int sid, bool shallBeTunedTo) {
   ReadLock rwlock(&Channels);
   std::vector<cChannel *> list;
   findChannels(nid, tid, sid, list);
   
   std::vector<cChannel *>::iterator it;
   Database::Ptr db(0);
   for (it=list.begin(); it != list.end();++it) {
      if ( (db=shallBeTunedTo ? getDatabaseTunedForChannel(*it) : getDatabaseForChannel(*it)) )
         break;
   }
   
   if (chan != 0)
      *chan=(*it);
   return db;
}

Database::Ptr Database::getDatabaseForChannel(int source, int nid, int tid, int sid, bool shallBeTunedTo) {
   cChannel *channel=0;
   ReadLock rwlock(&Channels);
      
   tChannelID cid(source, nid, tid, sid);
   channel=Channels.GetByChannelID(cid);
   
   Database::Ptr db= shallBeTunedTo ? getDatabaseTunedForChannel(channel) : getDatabaseForChannel(channel);
   
   return db;
}

      //to be used with Channels rwlocked
Database::Ptr Database::getDatabaseForChannel(cChannel *channel) {
   if (channel) {
      //first try primary database
      Database::Ptr db=getPrimaryDatabase();
      if (db->device && db->device->ProvidesTransponder(channel))
         return db;
      
      //then try created databases
      for (int i=0;i<cDevice::NumDevices();i++) {
         if ( (db=databases[i]) )
            if (db->device && db->device->ProvidesTransponder(channel))
               return db;
      }
      
      //then try all possibilities
      for (int i=0;i<cDevice::NumDevices();i++) {
         if ( (db=getDatabase(i)) )
            if (db->device && db->device->ProvidesTransponder(channel))
               return db;
      }
   }
   return 0;
}

      //to be used with Channels rwlocked
Database::Ptr Database::getDatabaseTunedForChannel(cChannel *channel) {
   bool needsDetachReceivers;
   if (channel) {
      //first try primary database
      Database::Ptr db=getPrimaryDatabase();
      if (db->device && db->device->ProvidesChannel(channel, Setup.PrimaryLimit, &needsDetachReceivers) && !needsDetachReceivers)
         return db;
      
      //then try created databases
      for (int i=0;i<cDevice::NumDevices();i++) {
         if ( (db=databases[i]) )
            if (db->device && db->device->ProvidesChannel(channel, Setup.PrimaryLimit, &needsDetachReceivers) && !needsDetachReceivers)
               return db;
      }
      
      //then try all possibilities
      for (int i=0;i<cDevice::NumDevices();i++) {
         if ( (db=getDatabase(i)) )
            if (db->device && db->device->ProvidesChannel(channel, Setup.PrimaryLimit, &needsDetachReceivers) && !needsDetachReceivers)
               return db;
      }
   }
   return 0;
}

      //to be used with Channels rwlocked
void Database::findChannels(int nid, int tid, int sid, std::vector<cChannel *> &list) {
   ReadLock rwlock(&Channels);
   
   for (cChannel *channel=Channels.First(); channel; channel = Channels.Next(channel)) {
      //printf("Channel %s\n", channel->Name());
      if (!channel->GroupSep() && channel->Sid()==sid && channel->Tid()==tid && channel->Nid()==nid ) {
         printf("Adding channel %p\n", channel);
         list.push_back(channel);
      }         
   }
   
}
*/
Database::Ptr Database::getDatabaseForService(Service::Service::Ptr service) {
   if (!service)
      return 0;
   //Service::ServiceManager::ServiceLock lock;
   Service::ServiceManager *manager=Service::ServiceManager::getManager();
   Service::Tunable *tunable=service->getTunable();
   Service::Tuner *tuner=manager->getTunerTuned(tunable);
   if (!tuner)
      tuner=manager->getTuner(tunable);
   return getDatabaseForTuner(tuner);
}

Database::Ptr Database::getDatabaseTunedForService(Service::Service::Ptr service) {
   if (!service)
      return 0;
   Database::Ptr db=getDatabaseForService(service);
   Service::Tunable *tunable=service->getTunable();
   if (db)
      db->tuner->Tune(tunable);
   return db;
}

bool Database::retrievePat(SI::PAT &Pat) {
   if (HasState(TuningStatePat)) {
      DatabaseLock lock(this);
      Pat=pat;
      return true;
   }
   return false;
}

PMTServicesRequest *Database::retrievePMTServices(Listener *listener, int originalNetworkId, int transportStreamId, IdTracker *serviceIds, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStatePat))
      return 0;
   return new PMTServicesRequest(this, listener, originalNetworkId, transportStreamId, serviceIds, mode, appData);
}


NetworksRequest *Database::retrieveNetworks(Listener *listener, IdTracker *networkIds, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new NetworksRequest(this, listener, networkIds, mode, appData);
}

ActualNetworkRequest *Database::retrieveActualNetwork(Listener *listener, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new ActualNetworkRequest(this, listener, mode, appData);
}

TransportStreamDescriptionRequest *Database::retrieveTransportStreamDescription(Listener *listener, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new TransportStreamDescriptionRequest(this, listener, mode, appData);
}

ActualTransportStreamRequest *Database::retrieveActualTransportStream(Listener *listener, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new ActualTransportStreamRequest(this, listener, mode, appData);
}

ServiceTableRequest *Database::retrieveServiceTable(Listener *listener, IdTracker *originalNetworkIds, IdTracker *transportStreamIds, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new ServiceTableRequest(this, listener, originalNetworkIds, transportStreamIds, mode, appData);
}

PMTElementaryStreamRequest *Database::retrievePMTElementaryStreams(Listener *listener, int originalNetworkId, int transportStreamId, int serviceId, IdTracker *componentTags, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStatePat))
      return 0;
   return new PMTElementaryStreamRequest(this, listener, originalNetworkId, transportStreamId, serviceId, componentTags, mode, appData);
}

EventTableRequest *Database::retrieveEventTable(Listener *listener, bool presentFollowingOrOther, 
                                                IdTracker *serviceIds, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new EventTableRequest(this, listener, presentFollowingOrOther, serviceIds, mode, appData);
}

EventTableOtherRequest *Database::retrieveEventTableOther(Listener *listener, bool presentFollowingOrOther, 
                                                IdTracker *serviceIds, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new EventTableOtherRequest(this, listener, presentFollowingOrOther, serviceIds, mode, appData);
}


BouquetsRequest *Database::retrieveBouquets(Listener *listener, IdTracker *bouquetIds, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new BouquetsRequest(this, listener, bouquetIds, mode, appData);
}

TDTRequest *Database::retrieveTDT(Listener *listener, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new TDTRequest(this, listener, mode, appData);
}

TOTRequest *Database::retrieveTOT(Listener *listener, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new TOTRequest(this, listener, mode, appData);
}

ServicesRequest *Database::retrieveServices(Listener *listener, int originalNetworkId, IdTracker *transportStreamIds,
                   IdTracker * serviceIds, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new ServicesRequest(this, listener, originalNetworkId, transportStreamIds, serviceIds, mode, appData);
}

ActualServicesRequest *Database::retrieveActualServices(Listener *listener, IdTracker *serviceIds, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new ActualServicesRequest(this, listener, serviceIds, mode, appData);
}

PresentFollowingEventRequest *Database::retrievePresentFollowingEvent(Listener *listener, 
         int tid, int sid, bool presentOrFollowing, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new PresentFollowingEventRequest(this, listener, tid, sid, presentOrFollowing, mode, appData);
}

ScheduleEventRequest *Database::retrieveScheduledEvents(Listener *listener, 
                        int tid, int sid, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new ScheduleEventRequest(this, listener, tid, sid, mode, appData);
}

TimeScheduleEventRequest *Database::retrieveTimeScheduledEvents(Listener *listener, time_t begin, time_t end,
                        int tid, int sid, RetrieveMode mode, void *appData) {
   DatabaseLock lock(this);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new TimeScheduleEventRequest(this, listener, begin, end, tid, sid, mode, appData);
}


void Database::Initialize() {
   Service::ServiceManager::ServiceLock lock;
   Service::ServiceManager *manager=Service::ServiceManager::getManager();
   std::list<Service::Tuner *> tuners;
   manager->getTuners(tuners);
   databases.reserve(tuners.size());
   for (std::list<Service::Tuner *>::iterator it=tuners.begin(); it != tuners.end(); ++it) {
      databases.push_back(Database::Ptr(new Database(*it)));
   }
}

void Database::CleanUp() {
   delete dispatchThread;
   dispatchThread=0;
   for (uint i=0;i<databases.size();i++) {
      if (databases[i]) {
         databases[i]->Stop();
         databases[i]=0;
      }
   }
}

Database::Database(Service::Tuner *tuner) 
   : patFilter(this), state(TuningStateUnknown), tuner(tuner)
{
   printf("Creating database for device %d\n", tuner->getDevice()->DeviceNumber());
   Service::ServiceManager::getManager()->AddServiceListener(this);
   tuner->getDevice()->AttachFilter(&patFilter);
   Add(&patFilter, false); //schedule
}

Database::~Database() {
   printf("Deleting database\n");
   Stop();
}

void Database::Stop() {
   printf("Stopping database\n");
   if (!tuner)
      return;

   // remove scheduled objects, patFilter
   RemoveAll();

   tuner->getDevice()->Detach(&patFilter);
   {
      cMutexLock lock(&listMutex);
      for (std::list<Filter *>::iterator it=activeFilters.begin(); it != activeFilters.end(); ) {
         // list will be modified by Detach. All iterators will remain valid, except the current one.
         Filter *detachFilter = *it;
         ++it;
         Detach(detachFilter); //do not delete a filter, they do not belong to us
      }
      activeFilters.clear();
      dataSwitchListener.clear();
   }
   // disable database
   tuner=0;
}

//called by Filter->Attach()
void Database::Attach(Filter *filter) {
   if (filter->attached || !tuner)
      return;
   {
      cMutexLock lock(&listMutex);
      filter->attached=true;
      //printf("Adding active Filter %p\n", filter);
      activeFilters.push_back(filter);
   }
   tuner->getDevice()->AttachFilter(filter);
}

//called by Filter->Detach()
void Database::Detach(Filter *filter) {
   if (!filter->attached)
      return;
   {
      cMutexLock lock(&listMutex);
      //printf("Removing active Filter %p\n", filter);
      filter->attached=false;
      activeFilters.remove(filter);
   }
   //printf("Device is %p\n", tuner->getDevice());
   if (tuner)
      tuner->getDevice()->Detach(filter);
}

void Database::TransportStreamChange(Service::TransportStreamID transportstream, Service::SwitchSource source, Service::Tuner *t) {
   if (tuner == t) {
      {
         DatabaseLock lock(this);
         ts=transportstream;
         state=TuningStateChannel;
      }
      HandleDataSwitch(DataSwitchDataSwitching);
      printf("DATABASE: set nid %d tid %d\n", ts.GetNid(), ts.GetTid());
         //Restart PAT filter
      InvalidatePat();
         //call WaitForPat from a different thread - dont block channel switch :-)
      dispatchThread->SynchronizeOnPat(this);
   } else {
      state=TuningStateTuning;
   }
}

void Database::InvalidatePat() {
   //printf("InvalidateNitPat\n");
   /*{
      DatabaseLock lock(this);
      patValid=false;
      if (alsoNit)
         nitValid=false;
   }*/
   ExecuteNow(&patFilter);
}

bool Database::WaitForDefinedState(TuningState newstate) {
   if (HasState(newstate))
      return true;
      
   DatabaseLock lock(this);
   //wait at most 8 seconds (in general, PAT should be acquired in less than 1 second)
   for (int i=0; i<8; i++) {
      if (HasState(newstate)) {
         return true;
      }
      stateVar.TimedWait(mutex, 1000);
   }
   return false;
}

/*bool Database::findNextChannel(int nid, int tid, int sid, cChannel *&channel) {
   //only use when Channels is locked?
   //printf("findNextChannel %p %p %p %p\n", last, Channels.Next(last), Channels.Next(Channels.Next(last)), //Channels.Next(Channels.Next(Channels.Next(last))) );
   for (; channel; channel = Channels.Next(channel)) {
      //printf("Channel %s\n", channel->Name());
      if (!channel->GroupSep() && channel->Sid()==sid && channel->Tid()==tid && channel->Nid()==nid )
         return channel;
   }
   //printf("findNextChannel: returning 0\n");
   return 0;
}*/


void Database::addDataSwitchListener(DataSwitchListener *listener) {
   cMutexLock lock(&listMutex);
   dataSwitchListener.push_back(listener);
}

void Database::HandleDataSwitch(DataSwitchEvent event) {
   cMutexLock lock(&listMutex);
   //printf("HandleDataSwitch event %d\n", event);
   switch (event) {
   case DataSwitchDataSwitch:
      for (std::list<DataSwitchListener *>::iterator it=dataSwitchListener.begin(); it != dataSwitchListener.end(); ++it)
         (*it)->DataSwitch(this);
      break;
   case DataSwitchDataSwitching:
      for (std::list<DataSwitchListener *>::iterator it=dataSwitchListener.begin(); it != dataSwitchListener.end(); ++it)
         (*it)->DataSwitching(this);
      break;
   }
}


Database::PatFilter::PatFilter(Database *db) : TimedBySeconds(30), database(db)
{
   Set(0x00, SI::TableIdPAT);
   //Set(0x10, SI::TableIdNIT);
}

void Database::PatFilter::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   //printf("PatFilter: Processing Pid %d, Tid %d\n", Pid, Tid);
   if (Pid==0x00 && Tid==SI::TableIdPAT) {
      DatabaseLock lock(database);
      if (!database->HasState(TuningStatePat)) {
         SI::PAT pat(Data);
         if (!pat.CheckCRCAndParse())
            return;
         if (pat.getSectionNumber())
            printf(" !!! PAT SECTION %d !!!\n", pat.getSectionNumber());
         database->pat=pat;
         database->state=TuningStatePat;
         database->ts.tid=pat.getTransportStreamId();
         database->stateVar.Broadcast();
         //printf("Setting database's pat\n");
      }
      SetStatus(false);
   } 
   /*else if (Pid==0x10 && Tid==SI::TableIdNIT && !database->nitValid) {
      printf("PatFilter: Processing Pid %d, Tid %d\n", Pid, Tid);
      DatabaseLock lock(database);
      if (!database->nitValid) {
         SI::NIT nit(Data);
         if (!nit.CheckCRCAndParse())
            return;
         database->nit=nit;
         database->nitValid=true;
         database->patVar.Broadcast();
         //printf("Setting database's nit\n");
      }
   }
   if (database->nitValid && database->patValid) {
      //printf("PatFilter: Setting status false\n");
      SetStatus(false);
   }*/
}

void Database::PatFilter::Execute() {
   //if (!database->nitValid)
   //   Add(0x10, SI::TableIdNIT);
   //printf("Executing PatFilter\n");
   //DatabaseLock lock(database);
   if (!database->HasState(TuningStatePat))
      SetStatus(true);
}

Database::DispatchThread::~DispatchThread() {
   running=false;
   //In theory, the mutex must be locked when broadcasting.
   //In practice, at this point (shutting down), the thread will somehow hang and needs to be cancelled.
   //I don't know why, same with SchedulerBySeconds in util.c
   //cMutexLock lock(&mutex);
   condVar.Broadcast();
   Cancel(2);
}

void Database::DispatchThread::Queue(Dispatcher *d) {
   //printf("DispatchThread queuing %p\n", d);
   cMutexLock lock(&mutex);
   queue.push(d);
   condVar.Broadcast();
   if (!running) {
      running=true;
      Start();
   }
}

void Database::DispatchThread::Action() {
   Dispatcher *d=0;
   while (running) {
      if (d) {
         //Dispatch() may possibly lock the database. This class has its own mutex.
         //Two mutexes -> Dead lock danger! So don't lock our mutex when calling Dispatch().
         //printf("DispatchThread dispatching %p\n", d);
         d->Dispatch();
         delete d;
         d=0;
      }
      cMutexLock lock(&mutex);
      if (!queue.empty()) {
         d=queue.front();
         queue.pop();
      } else
         condVar.TimedWait(mutex, 1000);
   }
   //this must be done in the execution context of the actual thread, thus in Action()
   //(and not in the destructor ;-)  )
   CheckDetachThread();
}

void Database::DispatchThread::SynchronizerOnPat::Dispatch() {
   //printf("SynchronizerOnNitPat::Dispatch\n");
   if (d->WaitForDefinedState(TuningStatePat)) {
      //printf("SynchronizerOnNitPat::Dispatch success, now notifying.\n");      
      d->HandleDataSwitch(DataSwitchDataSwitch);
   }
}



}

