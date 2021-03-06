/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef DVBSI_SIREQUESTS_H
#define DVBSI_SIREQUESTS_H

#include <list>
#include <libsi/section.h>

#include "requestutil.h"
#include "objects.h"

namespace DvbSi {


/*** Filter Requests ***/

//retrieve one or more NITs for specified network IDs
class NetworksRequest : public TableFilterTrackerRequest<NIT> {
public:
   NetworksRequest(DatabasePtr  db, Listener *listener, IdTracker *networkIds, RetrieveMode mode, void *appData);
protected:
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
};

//retrieve NIT for actual network
class ActualNetworkRequest : public TableFilterTrackerRequest<NIT> {
public:
   ActualNetworkRequest(DatabasePtr  db, Listener *listener, RetrieveMode mode, void *appData);
protected:
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
};

//retrieve current TSDT
class TransportStreamDescriptionRequest : public TableFilterTrackerRequest<TSDT> {
public:
   TransportStreamDescriptionRequest(DatabasePtr  db, Listener *listener, RetrieveMode mode, void *appData);
protected:
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
};

//retrieve PMTs of current TS for specified service IDs
class PMTServicesRequest : public TableFilterRequest<PMT> { //needs PAT
public:
   PMTServicesRequest(DatabasePtr  db, Listener *listener, int originalNetworkId, int transportStreamId, IdTracker *serviceIds, RetrieveMode mode, void *appData);
   //onid, tid, sid equals data source
protected:
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
   bool addNext(SI::PAT &pat);
   int currentPid;
private:
   SI::Loop::Iterator it;
};

//retrieve SDTs for specified transport stream IDs
class ServiceTableRequest : public TableFilterTrackerRequest<SDT> {
public:
   ServiceTableRequest(DatabasePtr  db, Listener *listener, IdTracker *originalNetworkId, IdTracker *transportStreamIds, RetrieveMode mode, void *appData);
protected:
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
   IdTracker *originalNetworkIds;
   int duplicatesOther;
};

//retrieve events for actual transport stream and specified service IDs
//retrieve "PresentFollowing" table if presentFollowing is true, else retrieves "Schedule" table
class EventTableRequest : public SegmentedTableFilterTrackerRequest<EIT> {
public:
   EventTableRequest(DatabasePtr  db, Listener *listener, bool presentFollowingOrOther, IdTracker *serviceIds, RetrieveMode mode, void *appData);
   bool isPresentFollowing() { return presentFollowing; }
protected:
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
   bool presentFollowing;
};

//retrieve for other transport streams and specified service IDs
//retrieve "PresentFollowing" table if presentFollowing is true, else retrieves "Schedule" table
//Since the SubtableId of the EIT is the ServiceId, no mechanism for filtering by TransportStreamId is provided here.
class EventTableOtherRequest : public SegmentedTableFilterTrackerRequest<EIT> {
public:
   EventTableOtherRequest(DatabasePtr  db, Listener *listener, bool presentFollowingOrOther, IdTracker *serviceIds, RetrieveMode mode, void *appData);
protected:
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
   bool presentFollowing;
};

class TimeScheduleEventTableRequest : public TableFilterTrackerRequest<EIT> {
public:
   TimeScheduleEventTableRequest(DatabasePtr  db, Listener *listener, time_t begin, time_t end, int tid, int sid, RetrieveMode mode, void *appData);
protected:
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
   std::vector<int> segments;
   bool presentFollowingFinished, scheduleFinished;
   bool buildSegmentList(time_t begin, time_t end);
};

//retrieve one or more BATs for specified bouquet IDs
class BouquetsRequest : public TableFilterTrackerRequest<BAT> {
public:
   BouquetsRequest(DatabasePtr  db, Listener *listener, IdTracker *bouquetIds, RetrieveMode mode, void *appData);
protected:
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
};

//TDT and TOT cannot be split in multiple sections, so only one plain result object is necessary

//retrieve current TDT
class TDTRequest : public FilterRequest {
public:
   TDTRequest(DatabasePtr  db, Listener *listener, RetrieveMode mode, void *appData);
   SI::TDT tdt;
   typedef SI::TDT objectType;
   SI::TDT &getResultObject() { return tdt; }
protected:
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
};

//retrieve current TOT
class TOTRequest : public FilterRequest {
public:
   TOTRequest(DatabasePtr  db, Listener *listener, RetrieveMode mode, void *appData);
   SI::TOT tot;
   typedef SI::TOT objectType;
   SI::TOT &getResultObject() { return tot; }
protected:
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
};





/*** Secondary Requests ***/



//retrieve actual transport stream from current NIT
class ActualTransportStreamRequest : public ListSecondaryRequest<NIT::TransportStream> { //needs PAT
public:
   ActualTransportStreamRequest(DatabasePtr  db, Listener *listener, RetrieveMode mode, void *appData);
   virtual void Result(Request *req);
   int getNetworkId() { return nid; }
protected:
   int nid;
   int tid;
};

class TransportStreamRequest : public ListSecondaryRequest<NIT::TransportStream> {
public:
   TransportStreamRequest(Request *req, std::list<NIT> *list, DatabasePtr  db, Listener *listener, RetrieveMode mode, void *appData=0);
   int getNetworkId() { return nid; }
protected:
   int nid;
};

//BAT::TransportStream is NIT::TransportStream
class TransportStreamBATRequest : public ListSecondaryRequest<NIT::TransportStream> {
public:
   TransportStreamBATRequest(Request *req, std::list<BAT> *list, DatabasePtr  db, Listener *listener, RetrieveMode mode, void *appData=0);
   int getBouquetId() { return bid; }
protected:
   int bid;
};

//retrieve elementary streams for specified component tags from PMT of service specified
class PMTElementaryStreamRequest : public ListSecondaryRequest<PMT::Stream> { //needs PAT
public:
   PMTElementaryStreamRequest(DatabasePtr  db, Listener *listener, int originalNetworkId, int transportStreamId, int serviceId, IdTracker *componentTags, RetrieveMode mode, void *appData);
   ~PMTElementaryStreamRequest();
   virtual void Result(Request *req);
   int getTransportStreamId() { return tid; }
   int getServiceId() { return sid; }
protected:
   int tid;
   int sid;
   IdTracker *tags;
};

//retrieve one or more services from SDTs for specified service data (NID/TIDs/SIDs)
class ServicesRequest : public ListSecondaryRequest<SDT::Service> {
public:
   ServicesRequest(DatabasePtr  db, Listener *listener, int originalNetworkId, IdTracker *transportStreamIds,
                   IdTracker * serviceIds, RetrieveMode mode, void *appData);
   ~ServicesRequest();
   virtual void Result(Request *req);
   int getOriginalNetworkId() { return nid; }
protected:
   int nid;
   IdTracker *sids;
};

//retrieve the present or the following event of a service of actual network with specified TID/SID
class PresentFollowingEventRequest : public SecondaryRequest { //needs PAT
public:
   PresentFollowingEventRequest(DatabasePtr  db, Listener *listener, 
                        int tid, int sid, bool presentOrFollowing, RetrieveMode mode, void *appData);
   virtual void Result(Request *req);
   EIT::Event event;
   typedef EIT::Event objectType;
   EIT::Event &getResultObject() { return event; }
   int getServiceId() { return sid; }
   int getTransportStreamId() { return tid; }
   int getOriginalNetworkId() { return nid; }
protected:
   int sid;
   int tid;
   int nid;
   bool presentOrFollowing;
   DatabasePtr  database;
};

//retrieve the scheduled events (all events after present/following)
// of a service of actual network with specified TID/SID
class ScheduleEventRequest : public ListSecondaryRequest<EIT::Event> { //needs PAT
public:
   ScheduleEventRequest(DatabasePtr  db, Listener *listener, 
                        int tid, int sid, RetrieveMode mode, void *appData);
   virtual void Result(Request *req);
   int getServiceId() { return sid; }
   int getTransportStreamId() { return tid; }
   int getOriginalNetworkId() { return nid; }
protected:
   int sid;
   int tid;
   int nid;
};

//retrieve the scheduled events (all events after present/following)
// of a service of actual network with specified TID/SID
class TimeScheduleEventRequest : public ListSecondaryRequest<EIT::Event> { //needs PAT
public:
   TimeScheduleEventRequest(DatabasePtr  db, Listener *listener, time_t begin, time_t end,
                        int tid, int sid, RetrieveMode mode, void *appData);
   virtual void Result(Request *req);
   int getServiceId() { return sid; }
   int getTransportStreamId() { return tid; }
   int getOriginalNetworkId() { return nid; }
protected:
   time_t begin, end;
   bool first;
   RetrieveMode mode;
   int sid;
   int tid;
   int nid;
};

//retrieve services with specified service IDs of actual transport stream
class ActualServicesRequest : public ListSecondaryRequest<SDT::Service> { //needs PAT
public:
   ActualServicesRequest(DatabasePtr  db, Listener *listener, IdTracker *serviceIds, RetrieveMode mode, void *appData);
   ~ActualServicesRequest();
   virtual void Result(Request *req);
   int getTransportStreamId() { return tid; }
   int getOriginalNetworkId() { return nid; }
protected:
   int tid;
   int nid;
   IdTracker *sids;
};



}

#endif
