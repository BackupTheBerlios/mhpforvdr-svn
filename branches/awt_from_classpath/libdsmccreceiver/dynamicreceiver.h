/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef DSMCC_DYNAMICRECEIVER_H
#define DSMCC_DYNAMICRECEIVER_H

#include <vector>
#include <list>
#include <queue>

#include <vdr/device.h>
#include <vdr/receiver.h>


#define MAXCONCURRENTRECEIVERS 3

/*
   cDynamicReceiver acts like a receiver to which pids can be added after creation.
   API:
   - virtual void Receive(uchar *Data, int Length) has the same meaning as with cReceiver
   - call myDynReceiver->Attach(chosenDevice) to attach receiver to a device
     (cDynamicReceiver does not inherit cReceiver).
   - call AddPid to add a Pid (before or after attaching). Every PID shall be added not more than once.
   - call ActivatePids to have previous calls to AddPid take effect.
     During the call to ActivatePids, if the receiver is attached, some packets may be received more than once.
     Adapt your TS interpreter to check the continuity counter appropriately.

*/  


class cDynamicReceiver {
   friend class ActualReceiver;
public:
   cDynamicReceiver(int Ca=0, int Priority=-1);
   virtual ~cDynamicReceiver();
   bool Attach(cDevice *device);
protected:
   //make this class cReceiver-like
   virtual void Receive(uchar *Data, int Length) {}
   virtual void Activate(bool ON) {}
   void Detach();
   void Destroy();
   
   class ActualReceiver : public cReceiver {
   public:
      ActualReceiver(cDynamicReceiver *p, int Ca, int Priority, std::vector<int> &pids);
      //bool contains(int pid);
      bool Attach(cDevice *device);
      void Detach() { cReceiver::Detach(); }
      std::vector<int> pids;
   protected:
      virtual void Receive(uchar *Data, int Length);
      virtual void Activate(bool ON);
   private:
      cDynamicReceiver *parent;
      bool activated;
   };
   
   //pids shall be guaranteed not to be already added
   void AddPid(int pid);
   //must be called to activate all previously added pids
   bool ActivatePids();
private:
   std::list<ActualReceiver *> receivers;
   void ActivatedSubReceiver(bool On);
   int activated;
   int priority;
   int ca;
   std::queue<int> queue;
   cDevice *device;
};




#endif 

