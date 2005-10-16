/***************************************************************************
 *       Copyright (c) 2005 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include <vdr/config.h>

#include "filter.h"

namespace Service {

TransportStreamFilter::TransportStreamFilter()
   : status(TransportStreamUnknown)
{
}

void TransportStreamFilter::DeactivateFilter() {
   status=Deactivated;
   RemoveFilterData();
}

void TransportStreamFilter::SetStatus(bool On) {
   cFilter::SetStatus(On);
   #if VDRVERSNUM <= 10327
      #error "Unfortunately, VDR versions up to 1.3.27 contain a bug that prevents this code from working properly. Please use VDR version 1.3.28 or later."
   #endif
   //printf("TransportStreamFilter::SetStatus , status is %d, On is %d\n", status, On);
   TransportStreamID currentTs=TransportStream(Channel()).GetTransportStreamID();
   if (On) {
      switch (status) {
         case TransportStreamUnknown:
            ts=currentTs;
            status=Active;
            AddFilterData();
            break;
         case Active:
            break; // should not happen
         case Inactive:
         case OnOtherTransportStream:
            if (currentTs == ts) {
               status=Active;
               AddFilterData();
            } else {
               status=OnOtherTransportStream;
               OtherTransportStream(currentTs);
            }
            break;
         case Deactivated:
            break;
      }
   } else {
      switch (status) {
         case TransportStreamUnknown:
            break;
         case Active:
            status=Inactive;
            RemoveFilterData();
            break;
         case Inactive:
         case OnOtherTransportStream:
            break;
         case Deactivated:
            break;
      }
   }
   //printf("TransportStreamFilter::SetStatus, leaving, status is %d\n", status);
}


void FilterTransportStreamFilter::AddFilterData() {
   //printf("FilterTransportStreamFilter::AddFilterData()\n");
   for (cFilterData *fd = data.First(); fd; fd = data.Next(fd)) {
      cFilter::Add(fd->pid, fd->tid, fd->mask);
   }
}

void FilterTransportStreamFilter::RemoveFilterData() {
   //printf("FilterTransportStreamFilter::RemoveFilterData()\n");
   for (cFilterData *fd = data.First(); fd; fd = data.Next(fd)) {
      cFilter::Del(fd->pid, fd->tid, fd->mask);
   }
}

void FilterTransportStreamFilter::Add(u_short Pid, u_char Tid, u_char Mask) {
   //printf("FilterTransportStreamFilter::Add, %d, %d, %d, status %d\n", Pid, Tid, Mask, getFilterStatus());
   cFilterData *fd = new cFilterData(Pid, Tid, Mask, false);
   data.Add(fd);
   if (getFilterStatus())
      cFilter::Add(Pid, Tid, Mask);
}

void FilterTransportStreamFilter::Del(u_short Pid, u_char Tid, u_char Mask) {
   //printf("FilterTransportStreamFilter::Del, %d, %d, %d, status %d\n", Pid, Tid, Mask, getFilterStatus());
   for (cFilterData *fd = data.First(); fd; fd = data.Next(fd)) {
      if (fd->Is(Pid, Tid, Mask)) {
         if (getFilterStatus())
            cFilter::Del(Pid, Tid, Mask);
         data.Del(fd);
         return;
      }
   }
}


}


