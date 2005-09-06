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


FilterRequest::FilterRequest(Database::Ptr db, Listener *l, void *ad)
 : DatabaseRequest(l, ad), Filter(db), TimedBySeconds(FILTER_TIMEOUT), timeOutCode(ResultCodeTableNotFound) {
   db->Add(this, false); //add to scheduler for timeout
}

FilterRequest::~FilterRequest() {
   //remove from scheduler - this must be done here, TimedObject doesn't provide this functionality
   getDatabase()->Remove(this);
}

//This is a crucial hook to cleanly finish a request:
//If the request is finished, it will call this Function.
//If VDR calls this (channel switch e.g.) the request will be cancelled.
void FilterRequest::SetStatus(bool On) {
   cFilter::SetStatus(On);
   if (!On && !hasDispatched) {
      hasDispatched=true;
      if (result==ResultCodeUnknown) //set status called by VDR because of channel switch
         result=finished ? ResultCodeSuccess : ResultCodeDataSwitch;
      getDatabase()->DispatchResult(this);
   }
}

void FilterRequest::Execute() {
   if (!hasDispatched) {
      result=timeOutCode;
      setDataSource(DataSource(getDatabase()->getCurrentSource(), 
                    getDatabase()->getNetworkId(), getDatabase()->getTransportStreamId()));
      Detach();
   }
}

bool FilterRequest::CancelRequest() {
   if (!hasDispatched) {
      result=ResultCodeRequestCancelled;
      setDataSource(DataSource(getDatabase()->getCurrentSource(), 
                    getDatabase()->getNetworkId(), getDatabase()->getTransportStreamId()));
      Detach();
      return true;
   }
   return false;
}

bool FilterRequest::checkFinish() {
   if (finished) {
      result=ResultCodeSuccess;
      setDataSource(DataSource(getDatabase()->getCurrentSource(), 
                    getDatabase()->getNetworkId(), getDatabase()->getTransportStreamId()));
      Detach();
      return true;
   }
   return false;
}

bool SecondaryRequest::CancelRequest() {
   if (req && !hasDispatched) {
      return req->CancelRequest();
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

