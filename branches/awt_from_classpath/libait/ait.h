/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef __AIT_H
#define __AIT_H

#include <list>

#include <vdr/channels.h>
#include <vdr/thread.h>

#include <libsi/section.h>
#include <libsi/descriptor.h>

#include <libdvbsi/database.h>

#include "applications.h"

struct Descriptor;
struct Pid;
struct PidInfo;
struct TransportProtocolDescriptor;

namespace ApplicationInfo {

#define MAXAITENTRIES 20

class cApplicationMonitor : public DvbSi::Listener, public DvbSi::Filter, public DvbSi::DataSwitchListener {
public:
   static void InitializeAllDevices();
   cApplicationMonitor(DvbSi::Database *d);
   virtual ~cApplicationMonitor();
   virtual void Result(DvbSi::Request *r);
   virtual void DataSwitch(DvbSi::Database *db);
protected:
   cTransportStream *ts;
   DvbSi::PMTServicesRequest *request;
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
   bool AitVersionChanged(int PmtPid, int Version);
private:
   int aitVersion[MAXAITENTRIES];
   int numAitEntries;
   //virtual int  PMTInnerLoopDescriptor(struct Descriptor *d, struct Pid *pi, struct PidInfo *p, int currentSource, int currentTransponder);
   //virtual void AITSection(unsigned char *buffer, int aitPid, int currentSource, int currentTransponder);
   //virtual void PATDescriptor(struct Program *pr, int currentSource, int currentTransponder);
   //virtual void CurrentChannelID(tChannelID &channelid);
   //virtual void ShutdownFilters(int unique);
};


class cApplications : public cApplicationsDatabase, public cMutex {
friend class cAIT;
public:
   cApplications();
   ~cApplications();
   void StartMonitoring() { cApplicationMonitor::InitializeAllDevices(); }
   
   cTransportStream *GetTransportStream(int source, int nid, int tid) 
         { return TransportStreams.GetTransportStream(source, nid, tid); }
   cTransportStream *findTransportStream(int source, int nid, int tid)
         { return TransportStreams.findTransportStream(source, nid, tid); }
   
protected:
   cMutex mutex;
};

class cAIT : public SI::AIT {
public:
   cAIT(cApplications *db, cTransportStream *ts);
   ~cAIT();
   void ProcessAIT(int aitPid);
protected:
   cTransportProtocol *FindTransportProtocol(cTransportStream::ApplicationService *service, int label, SI::DescriptorLoop *commonDescriptors, SI::DescriptorLoop *specificLoop);
   //cTransportProtocol *FillTransportProtocol(cTransportStream::ApplicationService *service, int aitPid, SI::MHP_TransportProtocolDescriptor *Descriptor);
private:
   cApplications *database;
   cTransportStream *ts;
};

extern cApplications Applications;

} //end of namespace ApplicationInformation

#endif
