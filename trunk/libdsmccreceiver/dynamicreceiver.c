/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include "dynamicreceiver.h"


#error This class is no longer needed and broken since VDR 1.3.18. It worked before, though.

/*---------------------------- cDynamicReceiver ----------------------------*/

cDynamicReceiver::cDynamicReceiver(int Ca, int Priority)
 : activated(false), priority(Priority), ca(Ca), device(0) 
{
}

cDynamicReceiver::~cDynamicReceiver() {
   Destroy();
}

void cDynamicReceiver::Destroy() {
   for (std::list<ActualReceiver *>::iterator it=receivers.begin(); it != receivers.end(); ++it)
      delete (*it);
   receivers.clear();
}

//requires a subsequent call to ActivatePids to take effect
void cDynamicReceiver::AddPid(int pid) {
   queue.push(pid);
}

/*void cDynamicReceiver::Add(vector<int> &pids) {
   for (vector<int>::const_iterator pit=pids.begin(); pit != pids.end(); ++pit) {
   }
}*/

bool cDynamicReceiver::Attach(cDevice *dev) {
   if (device==dev)
      return true;
   if (device)
      Detach();
   device=dev;
   bool success=true;
   for (std::list<ActualReceiver *>::const_iterator it=receivers.begin(); it != receivers.end(); ++it)
      success=(*it)->Attach(device) && success;
   return success;
}

bool cDynamicReceiver::ActivatePids() {
   bool success=true;
   
   //reduce number of receivers
   std::list<ActualReceiver *> toBeDetached;
   if (receivers.size() >= MAXCONCURRENTRECEIVERS) {
      //this algorithm does _not_ guarantee that there are never more than MAXCONCURRENTRECEIVERS receivers.
      //This is the case if more than 36 pids are added.
      int roomNeeded=queue.size();
      for (std::list<ActualReceiver *>::const_iterator it=receivers.begin(); it != receivers.end() && roomNeeded>0; ++it) {
         if ( (*it)->pids.size() < 12 ) { //12, 1<12<16, deliberately set
            for (std::vector<int>::const_iterator pit=(*it)->pids.begin(); pit != (*it)->pids.end(); ++pit) {
               AddPid(*pit);
            }
            toBeDetached.push_back(*it);
            roomNeeded -= 16-(*it)->pids.size();
         }
      }
   }
   
   while (!queue.empty()) {
      //take at most 16 pids per receiver
      std::vector<int> v( (queue.size() <? 16), 0);
      for (int i=0; i<16 && !queue.empty(); i++) {
         v[i]=queue.front();
         queue.pop();
      }
      ActualReceiver *r=new ActualReceiver(this, ca, priority, v);
      receivers.push_back(r);
      if (device)
         success=r->Attach(device) && success;
   }
   
   for (std::list<ActualReceiver *>::const_iterator it=toBeDetached.begin(); it != toBeDetached.end(); ++it) {
      receivers.remove(*it);
      delete (*it);
   }
   
   return success;
}

void cDynamicReceiver::Detach() {
   for (std::list<ActualReceiver *>::const_iterator it=receivers.begin(); it != receivers.end(); ++it)
      (*it)->Detach();
}

void cDynamicReceiver::ActivatedSubReceiver(bool On) {
   if (On) {
      if (activated==0) {
         Activate(true);
      }
      activated++;
   } else {
      if (activated==1) {
         Activate(false);
      }
      activated--;
   }
}


cDynamicReceiver::ActualReceiver::ActualReceiver(cDynamicReceiver *pa, int Ca, int Priority, std::vector<int> &p) 
   : cReceiver(Ca, Priority, 16, 
         p.size() > 0 ? p[0] : 0,
         p.size() > 1 ? p[1] : 0,
         p.size() > 2 ? p[2] : 0,
         p.size() > 3 ? p[3] : 0,
         p.size() > 4 ? p[4] : 0,
         p.size() > 5 ? p[5] : 0,
         p.size() > 6 ? p[6] : 0,
         p.size() > 7 ? p[7] : 0,
         p.size() > 8 ? p[8] : 0,
         p.size() > 9 ? p[9] : 0,
         p.size() > 10 ? p[10] : 0,
         p.size() > 11 ? p[11] : 0,
         p.size() > 12 ? p[12] : 0,
         p.size() > 13 ? p[13] : 0,
         p.size() > 14 ? p[14] : 0,
         p.size() > 15 ? p[15] : 0),
     pids(p),
     parent(pa),
     activated(false)
{
}

/*bool cDynamicReceiver::ActualReceiver::contains(int pid) {
   for (vector<int>::const_iterator pit=pids.begin(); pit != pids.end(); ++pit)
      if ( (*it)==pid )
         return true;
   return false;
}*/

bool cDynamicReceiver::ActualReceiver::Attach(cDevice *device) {
   if (!activated)
      return device->AttachReceiver(this);
   else return true;
}

void cDynamicReceiver::ActualReceiver::Receive(uchar *Data, int Length) {
   parent->Receive(Data, Length);
}

void cDynamicReceiver::ActualReceiver::Activate(bool ON) {
   activated=ON;
   parent->ActivatedSubReceiver(ON);
}




