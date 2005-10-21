/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include "requestutil.h"
#include "database.h"

namespace DvbSi {

void DatabaseRequest::ScheduleDispatch(Database::Ptr db) {
   if (!hasDispatched) {
      hasDispatched=true;
      db->DispatchResult(this);
   }
}


FilterRequest::FilterRequest(Database::Ptr db, Listener *l, void *ad)
 : DatabaseRequest(l, ad), Filter(db), TimedBySeconds(FILTER_TIMEOUT), timeOutCode(ResultCodeTableNotFound) {
   // add to scheduler for timeout
   db->Add(this, false);
   // Attach filter to device
   Attach();
}

FilterRequest::~FilterRequest() {
   //remove from scheduler - this must be done here, TimedObject doesn't provide this functionality
   getDatabase()->Remove(this);
}

// Call this to finish a request.
// (Do not call from section filter thread, i.e. from cFilter::Process().
//  From there use ScheduleDispatch() only, or rather checkFinish())
void FilterRequest::Detach() {
   Filter::Detach();
   ScheduleDispatch();
}

// Called from Database's dispatch thread
void FilterRequest::Dispatch() {
   // Overridden from DvbSi::Request
   // If the dispatch was scheduled from checkFinish, the filter has not yet been detached (see below),
   // so do this here. In other cases, calling Detach() once again is a no-op.
   // Call directly Detach of superclass because Detach is overridden here for use prior to scheduling dispatch.
   // Here, we are already dispatched, and calling Detach() of this class would create an endless dispatching loop.
   Filter::Detach();
   DatabaseRequest::Dispatch();
}

// If the transport stream changes (channel switch from VDR e.g.) the request will be cancelled.
// Called from VDR main thread, other internal thread, or user thread
void FilterRequest::OtherTransportStream(Service::TransportStreamID ts) {
   if (!hasDispatched) {
      if (result==ResultCodeUnknown) //set status called by VDR because of channel switch
         result=finished ? ResultCodeSuccess : ResultCodeDataSwitch;
      Detach();
   }
}

// Called from Scheduler thread
void FilterRequest::Execute() {
   if (!hasDispatched) {
      result=timeOutCode;
      setDataSource(DataSource(getDatabase()->getSource(), 
                    getDatabase()->getOriginalNetworkId(), getDatabase()->getTransportStreamId()));
      Detach();
   }
}

// Called from user thread
bool FilterRequest::CancelRequest() {
   if (!hasDispatched) {
      result=ResultCodeRequestCancelled;
      setDataSource(DataSource(getDatabase()->getSource(), 
                    getDatabase()->getOriginalNetworkId(), getDatabase()->getTransportStreamId()));
      Detach();
      return true;
   }
   return false;
}

// Called from section filter thread
bool FilterRequest::checkFinish() {
   if (finished) {
      result=ResultCodeSuccess;
      setDataSource(DataSource(getDatabase()->getSource(), 
                    getDatabase()->getOriginalNetworkId(), getDatabase()->getTransportStreamId()));
      // Do not call Detach() here!
      // This method is called from within Process() of a cFilter, and Process() is called from within a loop
      // traversing the attached filters. Detach will modify this loop, which in itself will probably even work.
      // However, when this request is dispatched, the Listener will possibly delete it. Then, if it is detached, and with
      // the necessary duplicate checks Filter::Detach performs, and the destructor will not call cDevice's DetachFilter, and
      // the section filter mutex will not be locked, it is possible that the request object is deleted while it is still
      // in Process(), resulting in a crash sooner or later. (Believe me, this happened. NPTL is great.)
      ScheduleDispatch();
      return true;
   }
   return false;
}

bool SecondaryRequest::CancelRequest() {
   if (request && !hasDispatched) {
      return request->CancelRequest();
   }
   return false;
}

void SecondaryRequest::setDataSource(Request *req) {
   if (req)
      setDataSource(req->getDataSource());
}

DescriptorRequest::~DescriptorRequest() {
   for (iterator it=list.begin(); it != list.end(); ++it) {
      delete (*it);
   }
}

void DescriptorRequest::Add(SI::DescriptorLoop &loop) {
   SI::Descriptor *d;
   for (SI::Loop::Iterator it; (d=loop.getNext(it));  ) {
      list.push_back(d);
   }
}

void DescriptorRequest::Add(SI::DescriptorLoop &loop, int *tags, int size) {
   SI::Descriptor *d;
   for (SI::Loop::Iterator it; (d=loop.getNext(it, (SI::DescriptorTag *)tags, size));  ) {
      list.push_back(d);
   }
}


}

