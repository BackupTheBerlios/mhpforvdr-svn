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

#include "database.h"

namespace DvbSi {

Database **Database::databases=0;
Database::DispatchThread *Database::dispatchThread=new Database::DispatchThread();
bool *Database::noReceptionDatabases=0;
int Database::primaryIndex = -1;

int Database::getNumberOfValidDatabases() {
   int count=getNumberOfDatabases();
   for (int i=0;i<MAXDEVICES;i++)
      if (noReceptionDatabases[i])
         count--;
   return count;
}

Database::Ptr Database::getPrimaryDatabase() {
   checkInitialDatabaseSetup();
      
   if (primaryIndex != -1)
      return databases[primaryIndex];
   
   DeliverySystem system;
   int indexOfPrimary=cDevice::PrimaryDevice()->DeviceNumber();
   if (!databases[indexOfPrimary] && !noReceptionDatabases[indexOfPrimary]) {    
      if ((system=getDeliverySystem(cDevice::PrimaryDevice())) != DeliverySystemNone) {
         primaryIndex=indexOfPrimary;
         databases[primaryIndex]=new Database(cDevice::PrimaryDevice(), system);
         return databases[primaryIndex];
      } else
         noReceptionDatabases[indexOfPrimary]=true;
   }
   
   Database::Ptr db(0);
   for (int i=0;i<cDevice::NumDevices();i++) {
      if ( (db=getDatabase(i)) ) {
         primaryIndex=i;
         return db;
      }
   }
   esyslog("Could not find a device which can receive a broadcast");
   return 0;
}

Database::Ptr Database::getDatabase(cDevice *dev) {
   checkInitialDatabaseSetup();
   
   for (int i=0;i<cDevice::NumDevices();i++)
      if (databases[i] && databases[i]->device==dev)
         return databases[i];
   DeliverySystem system;
   for (int i=0;i<cDevice::NumDevices();i++) {
      cDevice  *idev=cDevice::GetDevice(i);
      if (idev==dev && !noReceptionDatabases[i]) {
         if ((system=getDeliverySystem(idev)) != DeliverySystemNone) {
            databases[i]=new Database(idev, system);
            return databases[i];
         } else
            noReceptionDatabases[i]=true;
      }
   }
   return 0;
}

Database::Ptr Database::getDatabase(int numDevice) {
   checkInitialDatabaseSetup();
   
   DeliverySystem system;
   cDevice  *idev=cDevice::GetDevice(numDevice);
   if (!databases[numDevice] && !noReceptionDatabases[numDevice]) {
      if ((system=getDeliverySystem(idev)) != DeliverySystemNone)
         databases[numDevice]=new Database(idev, system);
      else
         noReceptionDatabases[numDevice]=true;
   }
   return databases[numDevice];
}



Database::Ptr Database::getDatabaseForChannel(int nid, int tid, int sid, bool shallBeTunedTo, cChannel **chan) {
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

Database::Ptr Database::Database::getDatabaseForChannel(int source, int nid, int tid, int sid, bool shallBeTunedTo) {
   cChannel *channel=0;
   ReadLock rwlock(&Channels);
      
   tChannelID cid(source, nid, tid, sid);
   channel=Channels.GetByChannelID(cid);
   
   Database::Ptr db= shallBeTunedTo ? getDatabaseTunedForChannel(channel) : getDatabaseForChannel(channel);
   
   return db;
}

      //to be used with Channels rwlocked
Database::Ptr Database::Database::getDatabaseForChannel(cChannel *channel) {
   if (channel) {
      //first try primary database
      Database::Ptr db=getPrimaryDatabase();
      if (db->getDevice()->ProvidesTransponder(channel))
         return db;
      
      //then try created databases
      for (int i=0;i<cDevice::NumDevices();i++) {
         if ( (db=databases[i]) )
            if (db->getDevice()->ProvidesTransponder(channel))
               return db;
      }
      
      //then try all possibilities
      for (int i=0;i<cDevice::NumDevices();i++) {
         if ( (db=getDatabase(i)) )
            if (db->getDevice()->ProvidesTransponder(channel))
               return db;
      }
   }
   return 0;
}

      //to be used with Channels rwlocked
Database::Ptr Database::Database::getDatabaseTunedForChannel(cChannel *channel) {
   bool needsDetachReceivers;
   if (channel) {
      //first try primary database
      Database::Ptr db=getPrimaryDatabase();
      if (db->getDevice()->ProvidesChannel(channel, Setup.PrimaryLimit, &needsDetachReceivers) && !needsDetachReceivers)
         return db;
      
      //then try created databases
      for (int i=0;i<cDevice::NumDevices();i++) {
         if ( (db=databases[i]) )
            if (db->getDevice()->ProvidesChannel(channel, Setup.PrimaryLimit, &needsDetachReceivers) && !needsDetachReceivers)
               return db;
      }
      
      //then try all possibilities
      for (int i=0;i<cDevice::NumDevices();i++) {
         if ( (db=getDatabase(i)) )
            if (db->getDevice()->ProvidesChannel(channel, Setup.PrimaryLimit, &needsDetachReceivers) && !needsDetachReceivers)
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

int Database::getCurrentSource() {
   ReadLock rwlock(&Channels);
   return Channels.GetByNumber(device->CurrentChannel())->Source();
}


void Database::CleanUp() {
   delete dispatchThread;
   dispatchThread=0;
   if (databases) {
      for (int i=0;i<MAXDEVICES;i++)
         delete databases[i];
      delete databases;
      databases=0;
   }
}

bool Database::retrievePat(SI::PAT &Pat) {
   if (HasState(TuningStatePat)) {
      DatabaseLock lock(this);
      Pat=pat;
      return true;
   }
   return false;
}

PMTServicesRequest *Database::retrievePMTServices(Listener *listener, IdTracker *serviceIds, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStatePat))
      return 0;
   return new PMTServicesRequest(this, listener, serviceIds, mode, appData);
}

NetworksRequest *Database::retrieveNetworks(Listener *listener, IdTracker *networkIds, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new NetworksRequest(this, listener, networkIds, mode, appData);
}

NetworksRequest *Database::retrieveActualNetwork(Listener *listener, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new NetworksRequest(this, listener, new IdTracker(getNetworkId()), mode, appData);
}

TransportStreamDescriptionRequest *Database::retrieveTransportStreamDescription(Listener *listener, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new TransportStreamDescriptionRequest(this, listener, mode, appData);
}

ActualTransportStreamRequest *Database::retrieveActualTransportStream(Listener *listener, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStatePat))
      return 0;
   return new ActualTransportStreamRequest(this, listener, mode, appData);
}

ServiceTableRequest *Database::retrieveServiceTable(Listener *listener, IdTracker *transportStreamIds, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new ServiceTableRequest(this, listener, transportStreamIds, mode, appData);
}

PMTElementaryStreamRequest *Database::retrievePMTElementaryStreams(Listener *listener, int serviceId, IdTracker *componentTags, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStatePat))
      return 0;
   return new PMTElementaryStreamRequest(this, listener, serviceId, componentTags, mode, appData);
}

EventTableRequest *Database::retrieveEventTable(Listener *listener, bool presentFollowingOrOther, 
                                                IdTracker *serviceIds, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new EventTableRequest(this, listener, presentFollowingOrOther, serviceIds, mode, appData);
}

EventTableOtherRequest *Database::retrieveEventTableOther(Listener *listener, bool presentFollowingOrOther, 
                                                IdTracker *serviceIds, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new EventTableOtherRequest(this, listener, presentFollowingOrOther, serviceIds, mode, appData);
}


BouquetsRequest *Database::retrieveBouquets(Listener *listener, IdTracker *bouquetIds, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new BouquetsRequest(this, listener, bouquetIds, mode, appData);
}

TDTRequest *Database::retrieveTDT(Listener *listener, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new TDTRequest(this, listener, mode, appData);
}

TOTRequest *Database::retrieveTOT(Listener *listener, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new TOTRequest(this, listener, mode, appData);
}

ServicesRequest *Database::retrieveServices(Listener *listener, int originalNetworkId, IdTracker *transportStreamIds,
                   IdTracker * serviceIds, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStateChannel))
      return 0;
   return new ServicesRequest(this, listener, originalNetworkId, transportStreamIds, serviceIds, mode, appData);
}

ActualServicesRequest *Database::retrieveActualServices(Listener *listener, IdTracker *serviceIds, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStatePat))
      return 0;
   return new ActualServicesRequest(this, listener, serviceIds, mode, appData);
}

PresentFollowingEventRequest *Database::retrievePresentFollowingEvent(Listener *listener, 
         int tid, int sid, bool presentOrFollowing, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStatePat))
      return 0;
   return new PresentFollowingEventRequest(this, listener, tid, sid, presentOrFollowing, mode, appData);
}

ScheduleEventRequest *Database::retrieveScheduledEvents(Listener *listener, 
                        int tid, int sid, RetrieveMode mode, void *appData) {
   //DatabaseLock lock(this);
   cMutexLock lock(&switchMutex);
   if (!WaitForDefinedState(TuningStatePat))
      return 0;
   return new ScheduleEventRequest(this, listener, tid, sid, mode, appData);
}



Database::Database(cDevice *dev, DeliverySystem system) 
  : patFilter(this), nid(-1), tid(-1), state(TuningStateUnknown), device(dev), deliverySystem(system)
{
   printf("Creating database for device %d\n", dev->DeviceNumber());
   //channelMonitor=new ChannelMonitor(this);
   //new ChannelMonitor();
   //new cStatus;
   //patFilter is _not_ attached by its constructor, since at that time this->device is still 0!
   Attach(&patFilter);
   Add(&patFilter, false); //schedule
}

Database::~Database() {
   //printf("Deleting database\n");
   {
      cMutexLock lock(&listMutex);
      /*
      for (std::list<Filter *>::iterator it=activeFilters.begin(); it != activeFilters.end(); ++it) {
         Detach(*it); //do not delete a filter, they do not belong to us
      }
      activeFilters.clear();
      */
      dataSwitchListener.clear();
   }
}

//called by Filter->Attach()
void Database::Attach(Filter *filter) {
   if (filter->attached)
      return;
   filter->attached=true;
   {
      cMutexLock lock(&listMutex);
      //printf("Adding active Filter %p\n", filter);
      activeFilters.push_back(filter);
   }
   device->AttachFilter(filter);
}

//called by Filter->Detach()
void Database::Detach(Filter *filter) {
   if (!filter->attached)
      return;
   filter->attached=false;
   {
      cMutexLock lock(&listMutex);
      //printf("Removing active Filter %p\n", filter);
      activeFilters.remove(filter);
   }
   device->Detach(filter);
   //printf("Removed active Filter %p\n", filter);
}

void Database::ChannelSwitch(const cDevice *Device, int ChannelNumber) {
   if (Device==device) {
      cMutexLock lock(&switchMutex);
      if (ChannelNumber) {
         cChannel *channel=Channels.GetByNumber(ChannelNumber);
         if (!channel)
            return;
         int oldNid, oldTid;
         {
            DatabaseLock lock(this);
            oldNid=nid;
            oldTid=tid;
            nid=channel->Nid();
            tid=channel->Tid();
         }
         printf("DATABASE: set nid %d tid %d\n", nid, tid);
         
         if (HasState(TuningStatePat)) {
            if ( (nid == oldNid) && (tid == oldTid) ) {
               //no tuning occurred
               state=TuningStatePat;
               HandleDataSwitch(DataSwitchServiceChange);
               return;
            } else {
               state=TuningStateChannel;
               HandleDataSwitch(DataSwitchDataSwitching);
               InvalidatePat();
            }
         } else {
            state=TuningStateChannel;
            HandleDataSwitch(DataSwitchDataSwitching);
         }
         
         //Restart PAT filter
         InvalidatePat();
         //call WaitForPat from a different thread - dont block channel switch :-)
         dispatchThread->SynchronizeOnPat(this);
      } else {
         state=TuningStateTuning;
      }
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

Database::DeliverySystem Database::getDeliverySystem(cDevice *candidate) {
   DeliverySystem system=DeliverySystemNone;
   ReadLock rwlock(&Channels);
   for (cChannel *channel = Channels.First(); channel; channel = Channels.Next(channel)) {
      if (!channel->GroupSep() && candidate->ProvidesTransponder(channel)) {
         if (channel->IsSat())
            system=DeliverySystemSatellite;
         else if (channel->IsTerr())
            system=DeliverySystemTerrestrial;
         else if (channel->IsCable())
            system=DeliverySystemCable;
         break;
      }
   }
   //no channel found that is provided by the device
   // => device has no reception capabilities (is there a more elegant way to find this out?)
   return system;
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


void Database::checkInitialDatabaseSetup() {
   if (!databases) {
      //initial setup
      databases=new Database*[MAXDEVICES];
      noReceptionDatabases=new bool[MAXDEVICES];
      for (int i=0;i<MAXDEVICES;i++) {
         databases[i]=0;
         noReceptionDatabases[i]=false;
      }
   }
}


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
      //for (std::list<Filter *>::iterator it=activeFilters.begin(); it != activeFilters.end(); ++it)
       //  (*it)->FilterDataSwitching();
      break;
   case DataSwitchServiceChange:
      for (std::list<DataSwitchListener *>::iterator it=dataSwitchListener.begin(); it != dataSwitchListener.end(); ++it)
         (*it)->ServiceChange(this);
      break;
   }
}


Database::PatFilter::PatFilter(Database *db) : Filter(db, false), TimedBySeconds(30) 
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
         database->tid=pat.getTransportStreamId();
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

