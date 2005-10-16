/***************************************************************************
 *       Copyright (c) 2005 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef LIBSERVICe_FILTER_H
#define LIBSERVICe_FILTER_H

#include <vdr/filter.h>

#include "transportstream.h"

namespace Service {

// A filter that has the important property that it is bound to one transport stream.
// If the device it is attached to switches to a different transport stream, it will
// deactivate filtering, if it switches back to the specified transport stream, it
// will reactivate filtering. As well, channel switches which do not change
// the transport stream will be handled gracefully.
class TransportStreamFilter : public cFilter {
public:
   TransportStreamFilter();
   // Calls RemoveFilterData, AddFilterData will not be called again.
   // Note that this call is irreversible for the lifetime of this object.
   // The usual solution to stop a filter - reversibly - is to detach it from the device.
   void DeactivateFilter();
   // Returns the TS this Filter wants to receive, was initially receiving,
   // _not_ the current TS of the device.
   TransportStreamID getFilterTransportStreamID() { return ts; }
   bool getFilterStatus() { return status == Active; }
protected:
   // Overridden from cFilter, do not override
   virtual void SetStatus(bool On);
   // Override these
   // (Re-)Add filter data
   virtual void AddFilterData() = 0;
   // Remove filter data
   virtual void RemoveFilterData() = 0;
   // Override optionally: Called when a different transport stream is entered
   virtual void OtherTransportStream(TransportStreamID ts) {};
private:
   TransportStreamID ts;
   enum TransportStreamFilterStatus { TransportStreamUnknown, Active, Inactive, OnOtherTransportStream, Deactivated };
   TransportStreamFilterStatus status;
};

// Provides cFilter Add/Del API for TransportStreamFilter.
// Now, the data added with Add() applies and is re-applied if the specified
// transport stream is received, as specified above.
class FilterTransportStreamFilter : public TransportStreamFilter {
public:
   FilterTransportStreamFilter() {}
protected:
   cList<cFilterData> data;
   virtual void AddFilterData();
   virtual void RemoveFilterData();
   // Overrides or hides non-virtually from cFilter
   void Add(u_short Pid, u_char Tid, u_char Mask = 0xFF);
   void Del(u_short Pid, u_char Tid, u_char Mask = 0xFF);
private:
   // hide this, Sticky is simply wrong with a TransportStreamFilter
   void Add(u_short Pid, u_char Tid, u_char Mask, bool Sticky) {}
   void Set(u_short Pid, u_char Tid, u_char Mask = 0xFF) {}
};

}

#endif

