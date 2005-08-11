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

#include "sirequests.h"
#include "database.h"

namespace DvbSi {



/*** Filter Requests ***/


PMTServicesRequest::PMTServicesRequest(Database *db, Listener *listener, IdTracker *tr, RetrieveMode mode, void *ad)
 : TableFilterRequest<PMT>(db, tr, listener, ad),
   currentPid(0)
{
   //mode is currently ignored
   SI::PAT pat;
   if (database->retrievePat(pat))
      addNext(pat);
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
         if (tracker->isIncluded(assoc.getServiceId())) {
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


NetworksRequest::NetworksRequest(Database *db, Listener *listener, IdTracker *tr, RetrieveMode mode, void *appData)
  : TableFilterTrackerRequest<NIT>(db, tr, listener, appData)
{
   ChangeInterval(FILTER_TIMEOUT_NIT);
   Add(0x10, SI::TableIdNIT);
   Add(0x10, SI::TableIdNIT_other);
}

void NetworksRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (Pid==0x10 && (Tid==SI::TableIdNIT || Tid==SI::TableIdNIT_other) && !finished) {
      NIT nit(Data);
      if (!nit.CheckCRCAndParse())
         return;
      
      printf("Having NIT %d %d %d %d\n", nit.getTableId(), nit.getTableIdExtension(), nit.getSectionNumber(), nit.getLastSectionNumber());
      finished = checkSection(nit);
      checkFinish();
   }
}

TransportStreamDescriptionRequest::TransportStreamDescriptionRequest(Database *db, Listener *listener,  RetrieveMode mode, void *appData)
   //the tableIdExtension of the TSDT has no meaning, so we have to use the duplicates mechanism
  : TableFilterTrackerRequest<TSDT>(db, new IdTracker(), listener, appData)
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

ServiceTableRequest::ServiceTableRequest(Database *db, Listener *listener, IdTracker *tr, RetrieveMode mode, void *appData)
  : TableFilterTrackerRequest<SDT>(db, tr, listener, appData), duplicatesOther(0)
{
   ChangeInterval(FILTER_TIMEOUT_SDT);
   Add(0x11, SI::TableIdSDT);
   Add(0x11, SI::TableIdSDT_other);
}

void ServiceTableRequest::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (Pid==0x11 && (Tid==SI::TableIdSDT || Tid==SI::TableIdSDT_other) && !finished) {
      SDT sdt(Data);
      if (!sdt.CheckCRCAndParse())
         return;
      
      //printf("Having SDT %d %d %d", sdt.getTableId(), sdt.getTableIdExtension(), sdt.getSectionNumber());
      if (tracker->isFinite()) {
         finished=checkSection(sdt);
      } else {
         //we need to different duplicates counter, one for SI::TableIdSDT, one for SI::TableIdSDT_other
         checkSection(sdt, Tid==SI::TableIdSDT ? duplicates : duplicatesOther);
         finished = duplicates >= MAX_DUPLICATES && duplicatesOther >= MAX_DUPLICATES;
      }
      checkFinish();
   }
}

EventTableRequest::EventTableRequest(Database *db, Listener *listener, bool presentFollowing, IdTracker *serviceIds, RetrieveMode mode, void *appData) 
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

EventTableOtherRequest::EventTableOtherRequest(Database *db, Listener *listener, bool presentFollowing, IdTracker *serviceIds, RetrieveMode mode, void *appData) 
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


BouquetsRequest::BouquetsRequest(Database *db, Listener *listener, IdTracker *bouquetIds, RetrieveMode mode, void *appData)
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

TDTRequest::TDTRequest(Database *db, Listener *listener, RetrieveMode mode, void *appData)
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
      result=ResultCodeSuccess;
      Detach();
   }
}

TOTRequest::TOTRequest(Database *db, Listener *listener, RetrieveMode mode, void *appData)
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
      result=ResultCodeSuccess;
      Detach();
   }
}




/*** Secondary Requests ***/



ActualTransportStreamRequest::ActualTransportStreamRequest(Database *db, Listener *listener, RetrieveMode mode, void *appData)
  : ListSecondaryRequest<NIT::TransportStream>(db, listener, appData), nid(0)
{
   tid=db->getTransportStreamId();
   req=new NetworksRequest(db, this, new IdTracker(db->getNetworkId()), FromCacheOrStream, 0);
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
   hasDispatched=true;
   getDatabase()->DispatchResult(this);   
}


TransportStreamRequest::TransportStreamRequest(Request *re, std::list<NIT> *nitlist,  Database *db, Listener *listener, RetrieveMode mode, void *appData)
  : ListSecondaryRequest<NIT::TransportStream>(db, listener, appData), nid(0)
{
   req=re;
   if (req->getResultCode() == ResultCodeSuccess) {
      setDataSource(req);
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
      result=req->getResultCode();
   hasDispatched=true;
   getDatabase()->DispatchResult(this);   
}


TransportStreamBATRequest::TransportStreamBATRequest(Request *re, std::list<BAT> *nitlist, Database *db, Listener *listener, RetrieveMode mode, void *appData)
  : ListSecondaryRequest<NIT::TransportStream>(db, listener, appData), bid(0)
{
   req=re;
   if (req->getResultCode() == ResultCodeSuccess) {
      setDataSource(source);
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
      result=req->getResultCode();
   hasDispatched=true;
   getDatabase()->DispatchResult(this);   
}


PMTElementaryStreamRequest::PMTElementaryStreamRequest(Database *db, Listener *listener, int serviceId, IdTracker *tr, RetrieveMode mode, void *appData)
  : ListSecondaryRequest<PMT::Stream>(db, listener, appData),
    sid(serviceId),
    tags(tr)
{
   tid=db->getTransportStreamId();
   req=new PMTServicesRequest(db, this, new IdTracker(serviceId), FromCacheOrStream, 0);
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
            if (d && tags->isIncluded(d->getComponentTag())) {
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
   hasDispatched=true;
   getDatabase()->DispatchResult(this);   
}


ServicesRequest::ServicesRequest(Database *db, Listener *listener, int originalNetworkId,
                         IdTracker *transportStreamIds, IdTracker * serviceIds, RetrieveMode mode, void *appData)
  : ListSecondaryRequest<SDT::Service>(db, listener, appData),
    nid(originalNetworkId),
    sids(serviceIds)
{
   req=new ServiceTableRequest(db, this, transportStreamIds, mode, appData);
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
               if (sids->isIncluded(service.getServiceId())) {
                  list.push_back(service);
                  result=ResultCodeSuccess;
               }
            }
         }
      }
   } else
      result=req->getResultCode();
   delete req;
   hasDispatched=true;
   getDatabase()->DispatchResult(this);   
}

ActualServicesRequest::ActualServicesRequest(Database *db, Listener *listener, IdTracker *serviceIds, RetrieveMode mode, void *appData)
  : ListSecondaryRequest<SDT::Service>(db, listener, appData),
    sids(serviceIds)
{
   req=new ServiceTableRequest(db, this, new IdTracker(db->getTransportStreamId()), mode, appData);
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
            if (sids->isIncluded(service.getServiceId())) {
               //printf("Found service %d\n", service.getServiceId());
               list.push_back(service);
               result=ResultCodeSuccess;
            }
         }
      }      
   } else
      result=req->getResultCode();
   delete req;
   hasDispatched=true;
   getDatabase()->DispatchResult(this);
}

PresentFollowingEventRequest::PresentFollowingEventRequest(Database *db, Listener *listener, 
                        int tid, int sid, bool presentOrFollowing, RetrieveMode mode, void *appData)
   : SecondaryRequest(listener, appData), sid(sid), tid(tid), presentOrFollowing(presentOrFollowing), database(db)
{
   //printf("PresentFollowingEventRequest: %d %d %d\n", tid, sid, db->getPat().getTransportStreamId());
   if (tid==db->getTransportStreamId())
      req=new EventTableRequest(db, this, true, new IdTracker(sid), mode, appData);
   else
      req=new EventTableOtherRequest(db, this, true, new IdTracker(sid), mode, appData);
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
   hasDispatched=true;
   database->DispatchResult(this);
}

ScheduleEventRequest::ScheduleEventRequest(Database *db, Listener *listener, 
                        int tid, int sid, RetrieveMode mode, void *appData)
   : ListSecondaryRequest<EIT::Event>(db, listener, appData), tid(tid)
{
   if (tid==db->getTransportStreamId())
      req=new EventTableRequest(db, this, false, new IdTracker(sid), mode, appData);
   else
      req=new EventTableOtherRequest(db, this, false, new IdTracker(sid), mode, appData);
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
         if (result==ResultCodeSuccess)
            break;
      }      
   } else
      result=req->getResultCode();
   delete req;
   hasDispatched=true;
   getDatabase()->DispatchResult(this);
}




}

