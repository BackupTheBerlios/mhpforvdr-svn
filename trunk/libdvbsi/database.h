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

#include <time.h>

#include <libsi/section.h>
#include <libjava/jnithread.h>
#include <libservice/service.h>

#include "request.h"
#include "filter.h"
#include "objects.h"
#include "sirequests.h"


namespace DvbSi {

class PMTServicesRequest;
class DataSwitchListener;

class Database : public SchedulerBySeconds, public Service::ServiceListener, public SmartPtrObject {
public:
   typedef DatabasePtr Ptr;
   operator Ptr() { return Ptr(this); }

   //public API
   static int getNumberOfDatabases() { return databases.size(); }
   static Database::Ptr getFirstDatabase() { return databases.front(); }
   static Database::Ptr getDatabaseForTuner(Service::Tuner *tuner);
   static bool getDatabases(std::list<Database::Ptr> &list);
   static Database::Ptr getDatabaseForService(Service::Service::Ptr service);
   static Database::Ptr getDatabaseTunedForService(Service::Service::Ptr service);

      //in DVB, nid-tid-sid uniquely identifies a channel.
      //VDR additionally uses the Source. However, channels
      //with different source but same IDs should be identical
      //(although I can't rule out broken configurations, channels or SI data)
   /*
   static Database::Ptr getDatabaseForChannel(int nid, int tid, int sid, bool shallBeTunedTo);
   static Database::Ptr getDatabaseForChannel(int source, int nid, int tid, int sid, bool shallBeTunedTo);
   static Database::Ptr getDatabaseForChannel(Service::Ptr service);
   static Database::Ptr getDatabaseTunedForChannel(Service::Ptr service);
   */

   //cDevice *getDevice() { return device; };
   //listener is notified when a channel switch occured and the new NIT/PAT is available
   void addDataSwitchListener(DataSwitchListener *listener);

   // PAT
     //returns true if Pat contains a valid PAT on return
   bool retrievePat(SI::PAT &Pat);
   //to be used under DatabaseLock
   SI::PAT &getPat() { return pat; }
     //only valid if HasState(TuningStateChannel)
   int getOriginalNetworkId() { return ts.GetNid(); }
     //only valid if HasState(TuningStatePat)
   int getTransportStreamId() { return ts.GetTid(); }
   int getSource() { return ts.GetSource(); }
   Service::TransportStreamID getTransportStreamID() { return ts; }

   Service::DeliverySystem getDeliverySystem() { return tuner->getDeliverySystem(); }

  // Description of common arguments of the following "retrieve..." functions:
  //  Listener *listener  -  the DvbSi::Listener which will receive notification about the result of the request
  //  RetrieveMode mode   -  specifies whether the SI data may, shall or must not be taken from a cache
  //  void *appData       -  an opaque value that can be retrieved from the request object and will in no way be interpreted
  // Any IdTrackers: Passing a NULL pointer means "accept any ID", otherwise pass n object implementing the IdTracker interface.
  //for specific semantics and parameter descriptions see sirequests.h

   // PMT
   PMTServicesRequest *retrieveActualPMTServices(Listener *listener, IdTracker *serviceIds, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrievePMTServices(listener, -1, -1, serviceIds, mode, appData); }
   PMTServicesRequest *retrieveActualPMTServices(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveActualPMTServices(listener, 0, mode, appData); }

   PMTServicesRequest *retrievePMTServices(Listener *listener, int originalNetworkId, int transportStreamId, IdTracker *serviceIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);

   PMTElementaryStreamRequest *retrieveActualPMTElementaryStreams(Listener *listener, int serviceId, IdTracker *componentTags, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrievePMTElementaryStreams(listener, -1, -1, serviceId, componentTags, mode, appData); }
   PMTElementaryStreamRequest *retrieveActualPMTElementaryStreams(Listener *listener, int serviceId, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveActualPMTElementaryStreams(listener, serviceId, 0, mode, appData); }

   PMTElementaryStreamRequest *retrievePMTElementaryStreams(Listener *listener, int originalNetworkId, int transportStreamId, int serviceId, IdTracker *componentTags, RetrieveMode mode=FromCacheOrStream, void *appData=0);

   // NIT
   NetworksRequest *retrieveNetworks(Listener *listener, IdTracker *networkIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   NetworksRequest *retrieveNetworks(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveNetworks(listener, 0, mode, appData); }
   ActualNetworkRequest *retrieveActualNetwork(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   ActualTransportStreamRequest *retrieveActualTransportStream(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0);

   // TSDT
   TransportStreamDescriptionRequest *retrieveTransportStreamDescription(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0);

   // SDT
   ServiceTableRequest *retrieveServiceTable(Listener *listener, IdTracker *originalNetworkIds, IdTracker *transportStreamIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   ServiceTableRequest *retrieveServiceTable(Listener *listener, int originalNetworkId, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveServiceTable(listener, new SingleIdTracker(originalNetworkId), 0, mode, appData); }
   ServicesRequest *retrieveServices(Listener *listener, int originalNetworkId, IdTracker *transportStreamIds,
                   IdTracker * serviceIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   ActualServicesRequest *retrieveActualServices(Listener *listener, IdTracker *serviceIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   ActualServicesRequest *retrieveActualServices(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveActualServices(listener, 0, mode, appData); }

   // EIT
   EventTableRequest *retrieveEventTable(Listener *listener, bool presentFollowingOrOther, IdTracker *serviceIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   EventTableRequest *retrieveEventTable(Listener *listener, bool presentFollowingOrOther=true, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveEventTable(listener, presentFollowingOrOther, 0, mode, appData); }

   EventTableOtherRequest *retrieveEventTableOther(Listener *listener, bool presentFollowingOrOther, IdTracker *serviceIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   EventTableOtherRequest *retrieveEventTableOther(Listener *listener, bool presentFollowingOrOther=true, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveEventTableOther(listener, presentFollowingOrOther, 0, mode, appData); }

   //if presentOrFollowing is true, the present event will be retrieved, else the following event.
   PresentFollowingEventRequest *retrievePresentFollowingEvent(Listener *listener, 
         int tid, int sid, bool presentOrFollowing, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   ScheduleEventRequest *retrieveScheduledEvents(Listener *listener, 
                        int tid, int sid, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   TimeScheduleEventRequest *retrieveTimeScheduledEvents(Listener *listener, time_t begin, time_t end,
                        int tid, int sid, RetrieveMode mode=FromCacheOrStream, void *appData=0);

   // BAT
   BouquetsRequest *retrieveBouquets(Listener *listener, IdTracker *bouquetIds, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   BouquetsRequest *retrieveBouquets(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0)
     { return retrieveBouquets(listener, 0, mode, appData); }

   // TDT/TOT
   TDTRequest *retrieveTDT(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0);
   TOTRequest *retrieveTOT(Listener *listener, RetrieveMode mode=FromCacheOrStream, void *appData=0);


   // Service::ServiceListener
   virtual void TransportStreamChange(Service::TransportStreamID ts, Service::SwitchSource source, Service::Tuner *tuner);

   //package API
   virtual ~Database();
   static void Initialize();
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
                        };
   void HandleDataSwitch(DataSwitchEvent e);

private:
   class PatFilter : public cFilter, public TimedBySeconds {
   public:
      PatFilter(Database *db);
      virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
   protected:
      virtual void Execute();
      DatabasePtr database;
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

   Database(Service::Tuner *tuner);
   void InvalidatePat();
   void Stop();

   std::list<DataSwitchListener *> dataSwitchListener;
   std::list<Filter *> activeFilters;
   cMutex listMutex;

   cMutex switchMutex;

   SI::PAT pat;
   cMutex mutex;
   cCondVar stateVar;
   PatFilter patFilter;
   Service::TransportStreamID ts;
   TuningState state;
   Service::Tuner *tuner;

   static DispatchThread *dispatchThread;
   typedef std::vector<Database::Ptr> DatabaseArray;
   static DatabaseArray databases;
};

class DataSwitchListener {
protected:
   friend class Database;
     //indicates the SI data (transport stream) was switched - the database is in TuningStatePat.
   virtual void DataSwitch(Database::Ptr db) {};
     //indicates the SI data (transport stream) is switching - the database is in TuningStateChannel.
   virtual void DataSwitching(Database::Ptr db) {};
};

}



#endif
