/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef DVBSI_DATABASE_H
#define DVBSI_DATABASE_H

#include <queue>
#include <vector>

#include <vdr/device.h>
#include <vdr/channels.h>
#include <vdr/status.h>
#include <libsi/section.h>
#include <libjava/jnithread.h>

#include "request.h"
#include "filter.h"
#include "objects.h"
#include "sirequests.h"


namespace DvbSi {

class PMTServicesRequest;
class DataSwitchListener;

class Database : public SchedulerBySeconds, public cStatus, public SmartPtrObject {
public:
   typedef DatabasePtr Ptr;
   operator Ptr() { return Ptr(this); }
   
   //public API
   static int getNumberOfDatabases() { return cDevice::NumDevices(); }
   static int getNumberOfValidDatabases();
      //this function will try to find a receiving device
      //if the primary device has no reception capabilities.
      //If no device is found, NULL will be returned.
   static Database::Ptr getPrimaryDatabase();
      //these tow functions may return NULL if device has no reception capabilities
   static Database::Ptr getDatabase(cDevice *dev);
   static Database::Ptr getDatabase(int numDevice);
   
      //in DVB, nid-tid-sid uniquely identifies a channel.
      //VDR additionally uses the Source. However, channels
      //with different source but same IDs should be identical
      //(although I can't rule out broken configurations, channels or SI data)
   static Database::Ptr getDatabaseForChannel(int nid, int tid, int sid, bool shallBeTunedTo, cChannel **channel=NULL);
   static Database::Ptr getDatabaseForChannel(int source, int nid, int tid, int sid, bool shallBeTunedTo);
      //to be used with Channels rwlocked
   static Database::Ptr getDatabaseForChannel(cChannel *channel);
      //to be used with Channels rwlocked
   static Database::Ptr getDatabaseTunedForChannel(cChannel *channel);
      //to be used with Channels rwlocked
   static void findChannels(int nid, int tid, int sid, std::vector<cChannel *> &list);
   
   cDevice *getDevice() { return device; };
   int getCurrentSource();
   //listener is notified when a channel switch occured and the new NIT/PAT is available
   void addDataSwitchListener(DataSwitchListener *listener);
   
   // PAT
     //returns true if Pat contains a valid PAT on return
   bool retrievePat(SI::PAT &Pat);
   //to be used under DatabaseLock
   SI::PAT &getPat() { return pat; }
     //only valid if HasState(TuningStateChannel)
   int getNetworkId() { return nid; }
     //only valid if HasState(TuningStatePat)
   int getTransportStreamId() { return tid; }
   
      //None means no reception capabilities
   enum DeliverySystem { DeliverySystemNone, DeliverySystemSatellite,
                         DeliverySystemCable, DeliverySystemTerrestrial };
   DeliverySystem getDeliverySystem() { return deliverySystem; }
   
  //for semantics and parameter descriptions of the following "retrieve..." functions see sirequests.h
   // PMT
   PMTServicesRequest *retrievePMTServices(Listener *listener, IdTracker *serviceIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   PMTServicesRequest *retrievePMTServices(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrievePMTServices(listener, new IdTracker(), mode, appData); }
     
   PMTElementaryStreamRequest *retrievePMTElementaryStreams(Listener *listener, int serviceId, IdTracker *componentTags, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   PMTElementaryStreamRequest *retrievePMTElementaryStreams(Listener *listener, int serviceId, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrievePMTElementaryStreams(listener, serviceId, new IdTracker(), mode, appData); }
    
   // NIT
   NetworksRequest *retrieveNetworks(Listener *listener, IdTracker *networkIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   NetworksRequest *retrieveNetworks(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveNetworks(listener, new IdTracker(), mode, appData); }
   NetworksRequest *retrieveActualNetwork(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   ActualTransportStreamRequest *retrieveActualTransportStream(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   
   // TSDT
   TransportStreamDescriptionRequest *retrieveTransportStreamDescription(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   
   // SDT
   ServiceTableRequest *retrieveServiceTable(Listener *listener, IdTracker *transportStreamIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   ServiceTableRequest *retrieveServiceTable(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveServiceTable(listener, new IdTracker(), mode, appData); }
   ServicesRequest *retrieveServices(Listener *listener, int originalNetworkId, IdTracker *transportStreamIds,
                   IdTracker * serviceIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   ActualServicesRequest *retrieveActualServices(Listener *listener, IdTracker *serviceIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   ActualServicesRequest *retrieveActualServices(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveActualServices(listener, new IdTracker(), mode, appData); }
   
   // EIT
   EventTableRequest *retrieveEventTable(Listener *listener, bool presentFollowingOrOther, IdTracker *serviceIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   EventTableRequest *retrieveEventTable(Listener *listener, bool presentFollowingOrOther=true, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveEventTable(listener, presentFollowingOrOther, new IdTracker(), mode, appData); }
     
   EventTableOtherRequest *retrieveEventTableOther(Listener *listener, bool presentFollowingOrOther, IdTracker *serviceIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   EventTableOtherRequest *retrieveEventTableOther(Listener *listener, bool presentFollowingOrOther=true, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveEventTableOther(listener, presentFollowingOrOther, new IdTracker(), mode, appData); }
     
   //if presentOrFollowing is true, the present event will be retrieved, else the following event.
   PresentFollowingEventRequest *retrievePresentFollowingEvent(Listener *listener, 
         int tid, int sid, bool presentOrFollowing, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   ScheduleEventRequest *retrieveScheduledEvents(Listener *listener, 
                        int tid, int sid, RetrieveMode mode=FromCacheOrStream, void *appData=0);     
     
   // BAT
   BouquetsRequest *retrieveBouquets(Listener *listener, IdTracker *bouquetIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   BouquetsRequest *retrieveBouquets(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveBouquets(listener, new IdTracker(), mode, appData); }
     
   // TDT/TOT
   TDTRequest *retrieveTDT(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   TOTRequest *retrieveTOT(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   
   
   
   //package API
   virtual ~Database();
   static void CleanUp();
   void DispatchResult(Request *req) { dispatchThread->Queue(req); }
   void Attach(Filter *filter);
   void Detach(Filter *filter);
   
   enum TuningState { TuningStateUnknown,
                      TuningStateTuning,
                      TuningStateChannel,
                      TuningStatePat
                    };
   bool WaitForDefinedState(TuningState state);
   bool HasState(TuningState st) { return state >= st; }
   
   class DatabaseLock : public cMutexLock {
   public:
      DatabaseLock(Database::Ptr db) : cMutexLock(&db->mutex) {}
   };
   friend class DatabaseLock;
   
   enum DataSwitchEvent { DataSwitchDataSwitch,
                          DataSwitchDataSwitching,
                          DataSwitchServiceChange 
                        };
   void HandleDataSwitch(DataSwitchEvent e);
   
protected:
   virtual void ChannelSwitch(const cDevice *Device, int ChannelNumber);
   
private:   
   class PatFilter : public Filter, public TimedBySeconds {
   public:
      PatFilter(Database *db);
      virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
   protected:
      virtual void Execute();
   };
   friend class PatFilter;
   
   class DispatchThread : public JNI::Thread {
   public:
      DispatchThread() : running(false) {}
      ~DispatchThread();
      void Queue(Request *r) { Queue(new RequestDispatcher(r)); }
      void SynchronizeOnPat(Database *db) { Queue(new SynchronizerOnPat(db)); }
   protected:
      virtual void Action();
   private:
   
      class Dispatcher {
      public:
         virtual void Dispatch() = 0;
      };
      
      class RequestDispatcher : public Dispatcher {
      public:
         RequestDispatcher(Request *req) : r(req) {}
         virtual void Dispatch() { r->getListener()->Result(r); }
      private:
         Request *r;
      };
      
      class SynchronizerOnPat : public Dispatcher {
      public:
         SynchronizerOnPat(Database *db) : d(db) {}
         virtual void Dispatch();
      private:
         Database *d;
      };
      
      void Queue(Dispatcher *d);
      std::queue<Dispatcher*> queue;
      bool running;
      cMutex mutex;
      cCondVar condVar;
   };
   
   Database(cDevice *dev, DeliverySystem system);
   void InvalidatePat();
   
   std::list<DataSwitchListener *> dataSwitchListener;
   std::list<Filter *> activeFilters;
   cMutex listMutex;
   
   cMutex switchMutex;
   
   SI::PAT pat;
   cMutex mutex;
   cCondVar stateVar;
   PatFilter patFilter;
   int nid;
   int tid;
   TuningState state;
   //bool nitValid;
   cDevice *device;   
   DeliverySystem deliverySystem;
   
   static DeliverySystem getDeliverySystem(cDevice *candidate);
   static void checkInitialDatabaseSetup();
   
   static DispatchThread *dispatchThread;
   static Database **databases;
   static bool *noReceptionDatabases;
   static int primaryIndex;
};

class DataSwitchListener {
protected:
   friend class Database;
     //indicates the SI data (transport stream) was switched - the database is in TuningStatePat.
   virtual void DataSwitch(Database::Ptr db) {};
     //indicates the SI data (transport stream) is switching - the database is in TuningStateChannel.
   virtual void DataSwitching(Database::Ptr db) {};
     //indicated that the service changed, but not the transport stream.
   virtual void ServiceChange(Database::Ptr db) {};
};

}



#endif
