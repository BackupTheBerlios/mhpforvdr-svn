/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include <libsi/descriptor.h>
#include <vdr/config.h>

#include "sirequests.h"
#include "database.h"

namespace DvbSi {



/*** Filter Requests ***/


PMTServicesRequest::PMTServicesRequest(Database::Ptr db, Listener *listener, int originalNetworkId, int transportStreamId, IdTracker *sid, RetrieveMode mode, void *ad)
 : TableFilterRequest<PMT>(db, sid, listener, ad),
   currentPid(0)
{
   //mode is currently ignored
   // if both are -1, the "actual" stream is meant.
   // If Onid is specified, a specific transport stream may be meant. Tid may be -1.
   if ( (originalNetworkId == -1 && transportStreamId == -1) ||
               (originalNetworkId == database->getOriginalNetworkId() 
             && ( transportStreamId == -1 || transportStreamId == database->getTransportStreamId()) )
      ) {
      SI::PAT pat;
      if (database->retrievePat(pat))
         addNext(pat);
   } else {
      result=ResultCodeObjectNotInTable;
      Detach();
   }
}

void PMTServicesRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   //printf("PMTFilter processing %d %d\n", Pid, Tid);
   if (Pid==currentPid && Tid==SI::TableIdPMT && !finished) {
      PMT pmt(Data);
      if (!pmt.CheckCRCAndParse())
         return;
      //printf("Adding PMT %d %d %d\n", pmt.getServiceId(), list.size(), pmt.getLength());
      //printf("Having PMT %d %d %d", pmt.getTableId(), pmt.getTableIdExtension(), pmt.getSectionNumber());
      bool sectionComplete;
      addSection(pmt, sectionComplete);
      
      if (!sectionComplete)
         return;
      
      finished=addNext(database->getPat()); //pat is locked by sectionFilter mutex
      checkFinish();
   }
}

bool PMTServicesRequest::addNext(SI::PAT &pat) {
   //printf("PMTFilter::addNext\n");
   //Database::DatabaseLock lock(database);
   //SI::PAT &pat=database->getPat();
   SI::PAT::Association assoc;
   #if VDRVERSNUM > 10312
   while (pat.associationLoop.getNext(assoc, it)) {
   #else
   while (pat.associationLoop.hasNext(it)) {
      assoc = pat.associationLoop.getNext(it);
   #endif
      if (currentPid) {
         //printf("PMTFilter removing %d %d\n", currentPid, SI::TableIdPMT);
         Del(currentPid, SI::TableIdPMT);
      }
      if (!assoc.isNITPid()) {
         if (!tracker || tracker->isIncluded(assoc.getServiceId())) {
            currentPid=assoc.getPid();
            //printf("PMTFilter Adding %d %d\n", currentPid, SI::TableIdPMT);
            Add(currentPid, SI::TableIdPMT);
            return false;
         }
      }
   } 
   //printf("PMTFilter nothing more to add\n");
   return true;
}


NetworksRequest::NetworksRequest(Database::Ptr db, Listener *listener, IdTracker *nids, RetrieveMode mode, void *appData)
  : TableFilterTrackerRequest<NIT>(db, nids, listener, appData)
{
   // Here, nids refers to the network IDs, not the original network IDs!
   // Since currently the network id (in contrast to the ONID) of the current TS
   // is not available from the Database, we do not know in advance (as with the SDT request below)
   // whether the TableIdNIT_other tables are necessary.
   ChangeInterval(FILTER_TIMEOUT_NIT);
   Add(0x10, SI::TableIdNIT);
   Add(0x10, SI::TableIdNIT_other);
}

void NetworksRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (Pid==0x10 && (Tid==SI::TableIdNIT || Tid==SI::TableIdNIT_other) && !finished) {
      NIT nit(Data);
      if (!nit.CheckCRCAndParse())
         return;

      //printf("Having NIT %d %d %d %d\n", nit.getTableId(), nit.getTableIdExtension(), nit.getSectionNumber(), nit.getLastSectionNumber());
      finished = checkSection(nit);
      checkFinish();
   }
}

ActualNetworkRequest::ActualNetworkRequest(Database::Ptr db, Listener *listener, RetrieveMode mode, void *appData)
   : TableFilterTrackerRequest<NIT>(db, 0, listener, appData)
{
   ChangeInterval(FILTER_TIMEOUT_NIT);
   Add(0x10, SI::TableIdNIT);
}

void ActualNetworkRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (Pid==0x10 && Tid==SI::TableIdNIT && !finished) {
      NIT nit(Data);
      if (!nit.CheckCRCAndParse())
         return;

      //printf("Having NIT %d %d %d %d\n", nit.getTableId(), nit.getTableIdExtension(), nit.getSectionNumber(), nit.getLastSectionNumber());
      finished = checkSection(nit);
      checkFinish();
   }
}

TransportStreamDescriptionRequest::TransportStreamDescriptionRequest(Database::Ptr db, Listener *listener,  RetrieveMode mode, void *appData)
   //the tableIdExtension of the TSDT has no meaning, so we have to use the duplicates mechanism
  : TableFilterTrackerRequest<TSDT>(db, 0, listener, appData)
{
   Add(0x02, SI::TableIdTSDT);
}

void TransportStreamDescriptionRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (Pid==0x02 && Tid==SI::TableIdTSDT && !finished) {
      TSDT tsdt(Data);
      if (!tsdt.CheckCRCAndParse())
         return;
      
      //printf("Having NIT %d %d %d", nit.getTableId(), nit.getTableIdExtension(), nit.getSectionNumber());
      finished = checkSection(tsdt);
      checkFinish();
   }
}

ServiceTableRequest::ServiceTableRequest(Database::Ptr db, Listener *listener, IdTracker *originalNetworkIds, IdTracker *tids, RetrieveMode mode, void *appData)
   : TableFilterTrackerRequest<SDT>(db, tids, listener, appData), originalNetworkIds(originalNetworkIds), duplicatesOther(0)
{
   ChangeInterval(FILTER_TIMEOUT_SDT);
   // If tracker only includes the current TS, only add one PID.
   if (tids && tids->getSize() == 1 && tids->isIncluded(db->getTransportStreamId()) ) {
      Add(0x11, SI::TableIdSDT);
      // bypass check below
      duplicatesOther=MAX_DUPLICATES;
   } else {
      Add(0x11, SI::TableIdSDT);
      Add(0x11, SI::TableIdSDT_other);
   }
}

void ServiceTableRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (Pid==0x11 && (Tid==SI::TableIdSDT || Tid==SI::TableIdSDT_other) && !finished) {
      SDT sdt(Data);
      if (!sdt.CheckCRCAndParse())
         return;
      
      //printf("Having SDT %d %d %d", sdt.getTableId(), sdt.getTableIdExtension(), sdt.getSectionNumber());
      if (originalNetworkIds && !originalNetworkIds->isIncluded(sdt.getOriginalNetworkId()))
         return;
      if (tracker) {
         finished=checkSection(sdt);
      } else {
         //we need to different duplicates counter, one for SI::TableIdSDT, one for SI::TableIdSDT_other
         checkSection(sdt, Tid==SI::TableIdSDT ? duplicates : duplicatesOther);
         finished = duplicates >= MAX_DUPLICATES && duplicatesOther >= MAX_DUPLICATES;
      }
      checkFinish();
   }
}

EventTableRequest::EventTableRequest(Database::Ptr db, Listener *listener, bool presentFollowing, IdTracker *serviceIds, RetrieveMode mode, void *appData) 
  : SegmentedTableFilterTrackerRequest<EIT>(db, serviceIds, listener, appData), presentFollowing(presentFollowing)
{
   ChangeInterval(FILTER_TIMEOUT_EIT);
   if (presentFollowing)
      Add(0x12, SI::TableIdEIT_presentFollowing);
   else
      Add(0x12, SI::TableIdEIT_schedule_first, 0xF0);
}

void EventTableRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (Pid==0x12 && !finished) {
      EIT eit(Data);
      if (!eit.CheckCRCAndParse())
         return;
      
      //printf("Having EIT %d %d %d\n", eit.getTableId(), eit.getTableIdExtension(), eit.getSectionNumber());
      finished = checkSection(eit);
      checkFinish();
   }
}

EventTableOtherRequest::EventTableOtherRequest(Database::Ptr db, Listener *listener, bool presentFollowing, IdTracker *serviceIds, RetrieveMode mode, void *appData) 
  : SegmentedTableFilterTrackerRequest<EIT>(db, serviceIds, listener, appData), presentFollowing(presentFollowing)
{
   if (presentFollowing) {
      ChangeInterval(FILTER_TIMEOUT_EIT);
      Add(0x12, SI::TableIdEIT_presentFollowing_other);
   } else {
      ChangeInterval(FILTER_TIMEOUT_EIT_SCHEDULE_OTHER);
      Add(0x12, SI::TableIdEIT_schedule_Other_first, 0xF0);
   }
}

void EventTableOtherRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (Pid==0x12 && !finished) {
      EIT eit(Data);
      if (!eit.CheckCRCAndParse())
         return;
      
      //printf("Having EIT %d %d %d %d %d %d\n", eit.getTableId(), 
              //eit.getTableIdExtension(), eit.getSectionNumber(), eit.getLastSectionNumber(), eit.getOriginalNetworkId(), eit.getTransportStreamId());
      finished = checkSection(eit);
      checkFinish();
   }
}

TimeScheduleEventTableRequest::TimeScheduleEventTableRequest(Database::Ptr db, Listener *listener, time_t begin, time_t end,
      int tid, int sid, RetrieveMode mode, void *appData)
   : TableFilterTrackerRequest<EIT>(db, new SingleIdTracker(sid), listener, appData), /*presentFollowingFinished(true),*/ scheduleFinished(false)
{
   if (!buildSegmentList(begin, end)) {
      result=ResultCodeObjectNotInTable;
      Detach();
      return;
   }
   // Note: reception of the next/following section is currently disabled, commented out below and above.
   // It seems the data is also available in the Schedule sections.
   // If this proves wrong, needs code to check for duplicate entries and keep order of events.
   if (tid==db->getTransportStreamId()) {
      ChangeInterval(FILTER_TIMEOUT_EIT);
      //Add(0x12, SI::TableIdEIT_presentFollowing);
      Add(0x12, SI::TableIdEIT_schedule_first, 0xF0);
   } else {
      ChangeInterval(FILTER_TIMEOUT_EIT_SCHEDULE_OTHER);
      //Add(0x12, SI::TableIdEIT_presentFollowing_other);
      Add(0x12, SI::TableIdEIT_schedule_Other_first, 0xF0);
   }
}

void TimeScheduleEventTableRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (Pid==0x12 && !finished) {
      EIT eit(Data);
      if (!eit.CheckCRCAndParse())
         return;
      // replacing checkSection(), using addSection directly
      if (Tid == SI::TableIdEIT_presentFollowing || Tid == SI::TableIdEIT_presentFollowing_other) {
         /*
         if (tracker->isIncluded(eit.getServiceId())) {
            bool subtableComplete;
            addSection(eit, subtableComplete);
            if (subtableComplete) {
               presentFollowingFinished=true;
               Del(Pid, Tid);
            }
         }
         */
      } else {
         if (tracker->isIncluded(eit.getServiceId())) {
            bool segmentsComplete;
            addSectionSegmented(eit, segments, segmentsComplete);
            if (segmentsComplete) {
               scheduleFinished=true;
               Del(Pid, Tid & 0xF0, 0xF0);
            }
         }
      }
      finished = /*presentFollowingFinished && */scheduleFinished;
      checkFinish();
   }
}

bool TimeScheduleEventTableRequest::buildSegmentList(time_t begin, time_t end) {
   // The mechanism is described in ETSI TR 101 211 (Implementation guidelines for SI).
   // Assignment of section numbers to time intervalls is relative to last midnight in UTC
   // There are 16 table_id values, each table can have up to 32 segments, each describing a 3-hour period.
   // This is maximum 512 segments (64 days in the future since last mignight UTC).
   if (begin > end)
      return false;
   time_t now;
   time(&now);
   if (end < now)
      return false;
   struct tm tmTime;
   gmtime_r(&now, &tmTime);
   tmTime.tm_hour=0;
   tmTime.tm_min=0;
   tmTime.tm_sec=0;
   time_t midnight=mktime(&tmTime);

   time_t midnightToBegin=begin-midnight;
   time_t midnightToEnd=end-midnight;
   if (midnightToBegin < 0 || midnightToEnd <= 0)
      return false;
   int beginSegmentDiff=(midnightToBegin / (3*3600));
   int endSegmentDiff=(midnightToEnd / (3*3600));
   if (beginSegmentDiff > 512)
      return false;
   if (endSegmentDiff > 512)
      endSegmentDiff=512;
   //printf("buildSegmentList: begin %d end %d, now %d, midnight %d, midnightToBegin %d, midnightToEnd %d, beginSegmentDiff %d, endSegmentDiff %d", begin, end, now, midnight, midnightToBegin, midnightToEnd, beginSegmentDiff, endSegmentDiff);
   segments.reserve(endSegmentDiff-beginSegmentDiff+1);
   for (int i=beginSegmentDiff; i<=endSegmentDiff; i++)
      segments.push_back(i);
   return true;
}



BouquetsRequest::BouquetsRequest(Database::Ptr db, Listener *listener, IdTracker *bouquetIds, RetrieveMode mode, void *appData)
  : TableFilterTrackerRequest<BAT>(db, bouquetIds, listener, appData)
{
   ChangeInterval(FILTER_TIMEOUT_BAT);
   Add(0x11, SI::TableIdBAT);
}

void BouquetsRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (Pid==0x11 && Tid==SI::TableIdBAT && !finished) {
      BAT bat(Data);
      if (!bat.CheckCRCAndParse())
         return;
      
      //printf("Having BAT %d %d %d", bat.getTableId(), bat.getTableIdExtension(), bat.getSectionNumber());
      finished = checkSection(bat);
      checkFinish();
   }
}

TDTRequest::TDTRequest(Database::Ptr db, Listener *listener, RetrieveMode mode, void *appData)
  : FilterRequest(db, listener, appData)
{
   ChangeInterval(FILTER_TIMEOUT_TDT);
   Add(0x14, SI::TableIdTDT);
}

void TDTRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (Pid==0x14 && Tid==SI::TableIdTDT && !finished) {
      tdt.setData(Data, Length);
      tdt.CheckParse();
      //printf("Having TDT %d", tdt.getTableId());
      finished=true;
      checkFinish();
   }
}

TOTRequest::TOTRequest(Database::Ptr db, Listener *listener, RetrieveMode mode, void *appData)
  : FilterRequest(db, listener, appData)
{
   ChangeInterval(FILTER_TIMEOUT_TOT);
   Add(0x14, SI::TableIdTOT);
}

void TOTRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (Pid==0x14 && Tid==SI::TableIdTOT && !finished) {
      tot.setData(Data, Length);
      if (!tot.CheckCRCAndParse())
         return;
      //printf("Having TOT %d", tot.getTableId());
      finished=true;
      checkFinish();
   }
}




/*** Secondary Requests ***/



ActualTransportStreamRequest::ActualTransportStreamRequest(Database::Ptr db, Listener *listener, RetrieveMode mode, void *appData)
  : ListSecondaryRequest<NIT::TransportStream>(db, listener, appData), nid(0)
{
   tid=db->getTransportStreamId();
   request=new ActualNetworkRequest(db, this, FromCacheOrStream, 0);
}

void ActualTransportStreamRequest::Result(Request *req) {
   if (req->getResultCode() == ResultCodeSuccess) {
      setDataSource(req);
      NetworksRequest *anreq=(NetworksRequest *)req;
      NIT::TransportStream ts;
      result=ResultCodeObjectNotInTable;
      for (NetworksRequest::iterator stlit=anreq->list.begin(); stlit != anreq->list.end(); ++stlit) {
         NIT nit(*stlit);
         nid=nit.getNetworkId();
         SI::Loop::Iterator it;
         #if VDRVERSNUM > 10312
         while (nit.transportStreamLoop.getNext(ts, it)) {
         #else
         while (nit.transportStreamLoop.hasNext(it)) {
            ts=nit.transportStreamLoop.getNext(it);
         #endif
            if ( finished=(tid==ts.getTransportStreamId()) ) {
               list.push_back(ts);
               result=ResultCodeSuccess;
               break;
            }
         }
      }
   } else
      result=req->getResultCode();
   delete req;
   ScheduleDispatch();
}


TransportStreamRequest::TransportStreamRequest(Request *re, std::list<NIT> *nitlist,  Database::Ptr db, Listener *listener, RetrieveMode mode, void *appData)
  : ListSecondaryRequest<NIT::TransportStream>(db, listener, appData), nid(0)
{
   request=re;
   if (request->getResultCode() == ResultCodeSuccess) {
      setDataSource(request);
      NIT::TransportStream ts;
      result=ResultCodeObjectNotInTable;
      for (std::list<NIT>::iterator stlit=nitlist->begin(); stlit != nitlist->end(); ++stlit) {
         NIT nit(*stlit);
         nid=nit.getNetworkId();
         SI::Loop::Iterator it;
         NIT::TransportStream stream;
         #if VDRVERSNUM > 10312
         while (nit.transportStreamLoop.getNext(stream, it)) {
         #else
         while (nit.transportStreamLoop.hasNext(it)) {
            stream=nit.transportStreamLoop.getNext(it);
         #endif
            list.push_back(stream);
            result=ResultCodeSuccess;
         }
         finished=true;
      }
   } else
      result=request->getResultCode();
   ScheduleDispatch();
}


TransportStreamBATRequest::TransportStreamBATRequest(Request *re, std::list<BAT> *nitlist, Database::Ptr db, Listener *listener, RetrieveMode mode, void *appData)
  : ListSecondaryRequest<NIT::TransportStream>(db, listener, appData), bid(0)
{
   request=re;
   if (request->getResultCode() == ResultCodeSuccess) {
      setDataSource(request);
      BAT::TransportStream ts;
      result=ResultCodeObjectNotInTable;
      for (std::list<BAT>::iterator stlit=nitlist->begin(); stlit != nitlist->end(); ++stlit) {
         BAT bat(*stlit);
         bid=bat.getBouquetId();
         SI::Loop::Iterator it;
         BAT::TransportStream stream;
         #if VDRVERSNUM > 10312
         while (bat.transportStreamLoop.getNext(stream, it)) {
         #else
         while (bat.transportStreamLoop.hasNext(it)) {
            stream=bat.transportStreamLoop.getNext(it);
         #endif
            result=ResultCodeSuccess;
         }
         finished=true;
      }
   } else
      result=request->getResultCode();
   ScheduleDispatch();
}


PMTElementaryStreamRequest::PMTElementaryStreamRequest(Database::Ptr db, Listener *listener, int originalNetworkId, int transportStreamId, int serviceId, IdTracker *tr, RetrieveMode mode, void *appData)
  : ListSecondaryRequest<PMT::Stream>(db, listener, appData),
    sid(serviceId),
    tags(tr)
{
   tid=db->getTransportStreamId();
   request=new PMTServicesRequest(db, this, originalNetworkId, transportStreamId, new SingleIdTracker(serviceId), FromCacheOrStream, 0);
}

PMTElementaryStreamRequest::~PMTElementaryStreamRequest() {
   delete tags;
}


void PMTElementaryStreamRequest::Result(Request *req) {
   if (req->getResultCode() == ResultCodeSuccess) {
      setDataSource(req);
      PMTServicesRequest *pmtreq=(PMTServicesRequest *)req;
      PMT::Stream str;
      result=ResultCodeObjectNotInTable;
         
      for (PMTServicesRequest::iterator stlit=pmtreq->list.begin(); stlit != pmtreq->list.end(); ++stlit) {
         PMT pmt(*stlit);
         SI::Loop::Iterator it;
         #if VDRVERSNUM > 10312
         while (pmt.streamLoop.getNext(str, it)) {
         #else
         while (pmt.streamLoop.hasNext(it)) {
            str=pmt.streamLoop.getNext(it);
         #endif
            SI::Loop::Iterator it2;
            SI::StreamIdentifierDescriptor *d=
               (SI::StreamIdentifierDescriptor *)str.streamDescriptors.getNext(it2, SI::StreamIdentifierDescriptorTag);
            if (d && (!tags || tags->isIncluded(d->getComponentTag())) ) {
               list.push_back(str);
               //I do not know which return code shall be used if not all ES are found
               result=ResultCodeSuccess;
            }
            delete d;
         }
      }
   } else
      result=req->getResultCode();
   delete req;
   ScheduleDispatch();
}


ServicesRequest::ServicesRequest(Database::Ptr db, Listener *listener, int originalNetworkId,
                         IdTracker *transportStreamIds, IdTracker * serviceIds, RetrieveMode mode, void *appData)
  : ListSecondaryRequest<SDT::Service>(db, listener, appData),
    nid(originalNetworkId),
    sids(serviceIds)
{
   request=new ServiceTableRequest(db, this, new SingleIdTracker(originalNetworkId), transportStreamIds, mode, appData);
}

ServicesRequest::~ServicesRequest() {
   delete sids;
}

void ServicesRequest::Result(Request *req) {
   if (req->getResultCode() == ResultCodeSuccess) {
      setDataSource(req);
      ServiceTableRequest *sreq=(ServiceTableRequest *)req;
      SDT::Service service;
      result=ResultCodeObjectNotInTable;
         
      for (ServiceTableRequest::iterator stlit=sreq->list.begin(); stlit != sreq->list.end(); ++stlit) {
         SDT sdt(*stlit);
         if (nid==sdt.getOriginalNetworkId()) {
            SI::Loop::Iterator it;
            #if VDRVERSNUM > 10312
            while (sdt.serviceLoop.getNext(service, it)) {
            #else
            while (sdt.serviceLoop.hasNext(it)) {
               service=sdt.serviceLoop.getNext(it);
            #endif
               service.SetIds(nid, sdt.getTransportStreamId());
               if (!sids || sids->isIncluded(service.getServiceId())) {
                  list.push_back(service);
                  result=ResultCodeSuccess;
               }
            }
         }
      }
   } else
      result=req->getResultCode();
   delete req;
   ScheduleDispatch();
}

ActualServicesRequest::ActualServicesRequest(Database::Ptr db, Listener *listener, IdTracker *serviceIds, RetrieveMode mode, void *appData)
  : ListSecondaryRequest<SDT::Service>(db, listener, appData),
    sids(serviceIds)
{
   request=new ServiceTableRequest(db, this, new SingleIdTracker(db->getOriginalNetworkId()), new SingleIdTracker(db->getTransportStreamId()), mode, appData);
}

ActualServicesRequest::~ActualServicesRequest() {
   delete sids;
}

void ActualServicesRequest::Result(Request *req) {
   if (req->getResultCode() == ResultCodeSuccess) {
      setDataSource(req);
      ServiceTableRequest *sreq=(ServiceTableRequest *)req;
      SDT::Service service;
      result=ResultCodeObjectNotInTable;
         
      for (ServiceTableRequest::iterator stlit=sreq->list.begin(); stlit != sreq->list.end(); ++stlit) {
         SDT sdt(*stlit);
         tid=sdt.getTransportStreamId();
         nid=sdt.getOriginalNetworkId();
         //printf("Having SDT %d %d %d %d %d\n", sdt.getTableId(), sdt.getTableIdExtension(), sdt.getSectionNumber(), tid, nid);
         SI::Loop::Iterator it;
         #if VDRVERSNUM > 10312
         while (sdt.serviceLoop.getNext(service, it)) {
         #else
         while (sdt.serviceLoop.hasNext(it)) {
            service=sdt.serviceLoop.getNext(it);
         #endif
            service.SetIds(nid, tid);
            if (!sids || sids->isIncluded(service.getServiceId())) {
               //printf("Found service %d\n", service.getServiceId());
               list.push_back(service);
               result=ResultCodeSuccess;
            }
         }
      }      
   } else
      result=req->getResultCode();
   delete req;
   ScheduleDispatch();
}

PresentFollowingEventRequest::PresentFollowingEventRequest(Database::Ptr db, Listener *listener, 
                        int tid, int sid, bool presentOrFollowing, RetrieveMode mode, void *appData)
   : SecondaryRequest(listener, appData), sid(sid), tid(tid), presentOrFollowing(presentOrFollowing), database(db)
{
   //printf("PresentFollowingEventRequest: %d %d %d\n", tid, sid, db->getPat().getTransportStreamId());
   if (tid==db->getTransportStreamId())
      request=new EventTableRequest(db, this, true, new SingleIdTracker(sid), mode, appData);
   else
      request=new EventTableOtherRequest(db, this, true, new SingleIdTracker(sid), mode, appData);
}

void PresentFollowingEventRequest::Result(Request *req) {
   //printf("PresentFollowingEventRequest: received first result, code %d\n", req->getResultCode());
   if (req->getResultCode() == ResultCodeSuccess) {
      setDataSource(req);
      EventTableRequest *sreq=(EventTableRequest *)req;
      result=ResultCodeObjectNotInTable;
      
      for (EventTableRequest::iterator stlit=sreq->list.begin(); stlit != sreq->list.end(); ++stlit) {
         if ((*stlit).getTransportStreamId() != tid) //is this necessary?
            continue;
         EIT eit(*stlit);
         if (!eit.isPresentFollowing()) //this function means "present or following"
            continue;
         nid=eit.getOriginalNetworkId();
         SI::Loop::Iterator it;
         #if VDRVERSNUM > 10312
         while (eit.eventLoop.getNext(event, it)) {
         #else
         while (eit.eventLoop.hasNext(it)) {
            event=eit.eventLoop.getNext(it);
         #endif
            if (presentOrFollowing) {
               //look for present event               
               if (event.getRunningStatus() == SI::RunningStatusRunning || event.getRunningStatus() == SI::RunningStatusRunning) {
                  result=ResultCodeSuccess;
                  break;
               }
            } else {
               //look for following event
               if (event.getRunningStatus() != SI::RunningStatusRunning && event.getRunningStatus() != SI::RunningStatusRunning) {
                  result=ResultCodeSuccess;
                  break;
               }
            }
         }
         if (result==ResultCodeSuccess)
            break;
      }
   } else
      result=req->getResultCode();
   delete req;
   ScheduleDispatch(database);
}

ScheduleEventRequest::ScheduleEventRequest(Database::Ptr db, Listener *listener, 
                        int tid, int sid, RetrieveMode mode, void *appData)
   : ListSecondaryRequest<EIT::Event>(db, listener, appData), sid(sid), tid(tid)
{
   if (tid==db->getTransportStreamId())
      request=new EventTableRequest(db, this, false, new SingleIdTracker(sid), mode, appData);
   else
      request=new EventTableOtherRequest(db, this, false, new SingleIdTracker(sid), mode, appData);
}

void ScheduleEventRequest::Result(Request *req) {
   //printf("ScheduleEventRequest: received first result, code %d\n", req->getResultCode());
   if (req->getResultCode() == ResultCodeSuccess) {
      setDataSource(req);
      EventTableRequest *sreq=(EventTableRequest *)req;
      EIT::Event event;
      result=ResultCodeObjectNotInTable;

      for (EventTableRequest::iterator stlit=sreq->list.begin(); stlit != sreq->list.end(); ++stlit) {
         if ((*stlit).getTransportStreamId() != tid) //is this necessary?
            continue;
         EIT eit(*stlit);
         nid=eit.getOriginalNetworkId();
         SI::Loop::Iterator it;
         #if VDRVERSNUM > 10312
         while (eit.eventLoop.getNext(event, it)) {
         #else
         while (eit.eventLoop.hasNext(it)) {
            event=eit.eventLoop.getNext(it);
         #endif
            result=ResultCodeSuccess;
            list.push_back(event);
         }
      }
   } else
      result=req->getResultCode();
   delete req;
   ScheduleDispatch();
}

TimeScheduleEventRequest::TimeScheduleEventRequest(Database::Ptr db, Listener *listener, time_t begin, time_t end,
            int tid, int sid, RetrieveMode mode, void *appData)
   : ListSecondaryRequest<EIT::Event>(db, listener, appData), begin(begin), end(end), first(false), mode(mode), sid(sid), tid(tid)
{
   request=new TimeScheduleEventTableRequest(db, this, begin, end, tid, sid, mode, appData);
}

void TimeScheduleEventRequest::Result(Request *req) {
   //printf("ScheduleEventRequest: received first result, code %d\n", req->getResultCode());
   if (req->getResultCode() == ResultCodeSuccess) {
      setDataSource(req);
      EventTableRequest *sreq=(EventTableRequest *)req;
      EIT::Event event;
      result=ResultCodeObjectNotInTable;

      for (EventTableRequest::iterator stlit=sreq->list.begin(); stlit != sreq->list.end(); ++stlit) {
         if ((*stlit).getTransportStreamId() != tid) //is this necessary?
            continue;
         EIT eit(*stlit);
         nid=eit.getOriginalNetworkId();
         SI::Loop::Iterator it;
         #if VDRVERSNUM > 10312
         while (eit.eventLoop.getNext(event, it)) {
         #else
         while (eit.eventLoop.hasNext(it)) {
            event=eit.eventLoop.getNext(it);
         #endif
            time_t endTime=event.getStartTime()+event.getDuration();
            if ( (event.getStartTime() >= begin && event.getStartTime() < end)
                 || (endTime >= begin && endTime <= end) ) {
               result=ResultCodeSuccess;
               list.push_back(event);
            }
         }
      }
   } else
      result=req->getResultCode();
   delete req;
   ScheduleDispatch();
}


/*
// The following implementation is sub-optimal: First the whole table is acquired, which may take a
// considerable amount of time, only then it is parsed.
// A better implementation: Compute which segments of which table are required, quit when they are received.
TimeScheduleEventRequest::TimeScheduleEventRequest(Database::Ptr db, Listener *listener, time_t begin, time_t end,
                        int tid, int sid, RetrieveMode mode, void *appData)
   : ListSecondaryRequest<EIT::Event>(db, listener, appData), begin(begin), end(end), first(false), mode(mode), sid(sid), tid(tid)
{
   // first retrieve Present/Following table
   if (tid==db->getTransportStreamId()) {
      request=new EventTableRequest(db, this, true, new IdTracker(sid), mode, appData);
   } else {
      request=new EventTableOtherRequest(db, this, true, new IdTracker(sid), mode, appData);
   }
   result=ResultCodeObjectNotInTable;
}

void TimeScheduleEventRequest::Result(Request *req) {
   //printf("ScheduleEventRequest: received first result, code %d\n", req->getResultCode());
   if (req->getResultCode() == ResultCodeSuccess) {
      setDataSource(req);
      EventTableRequest *sreq=(EventTableRequest *)req;
      EIT::Event event;
         
      for (EventTableRequest::iterator stlit=sreq->list.begin(); stlit != sreq->list.end(); ++stlit) {
         if ((*stlit).getTransportStreamId() != tid) //is this necessary?
            continue;
         EIT eit(*stlit);
         nid=eit.getOriginalNetworkId();
         SI::Loop::Iterator it;
         #if VDRVERSNUM > 10312
         while (eit.eventLoop.getNext(event, it)) {
         #else
         while (eit.eventLoop.hasNext(it)) {
            event=eit.eventLoop.getNext(it);
         #endif
            time_t endTime=event.getStartTime()+event.getDuration();
            if ( (event.getStartTime() >= begin && event.getStartTime() < end)
                 || (endTime >= begin && endTime <= end) ) {
               result=ResultCodeSuccess;
               list.push_back(event);
            }
         }
      }
   } else {
      result=req->getResultCode();
      delete req;
      ScheduleDispatch();
      return;
   }
   
   if (first) {
      first=false;
      delete req;
      // Now retrieve schedule table
      if (tid==getDatabase()->getTransportStreamId()) {
         request=new EventTableRequest(getDatabase(), this, false, new IdTracker(sid), mode, getAppData());
      } else {
         request=new EventTableOtherRequest(getDatabase(), this, false, new IdTracker(sid), mode, getAppData());
      }
   } else {
      delete req;
      ScheduleDispatch();
   }
}
*/



}

