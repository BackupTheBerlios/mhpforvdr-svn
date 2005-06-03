/***************************************************************************
 *       Copyright (c) 2005 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/


#include "servicecontext.h"

namespace Service {

Transponder::Transponder(int source, int onid, int tid)
  : source(source), onid(onid), tid(tid)
{
}

Transponder::Transponder(cChannel *channel) {
   source=channel->Source();
   onid=channel->Nid();
   tid=channel->Tid();
}

Service::Service(int source, int onid, int tid, int sid)
  : Transponder(source, onid, tid), sid(sid)
{
}

Service::Service(cChannel *channel)
  : Transponder(channel)
{
   sid=channel->Sid();
}

Context *Context::s_self = 0;

Context *Context::getContext() {
   if (s_self == 0)
      s_self = new Context();
   return s_self;
}

void Context::getApplications(std::list<ApplicationInfo::cApplication *> apps) {
   //TODO
}

cChannel *Context::getService() {
   //TODO
   return 0;
}

bool Context::isPresenting() {
   //TODO
   return false;
}
      //Tries to switch to given service - including tuning!
      //It is guaranteed that ServiceStatus is called subsequently.
      //Possible Messages:
      //If selection is successful, MessageServiceSelected
      //If selection fails:
      //    if the previously presenting service can be reselected
      //       a message why the selection failed 
      //    if no service was being presented
      //    or if the previously presenting service cannot be reselected
      //       a message why the selection failed followed by a message why that presentation terminated
      //If the presentation is terminated afterwards,
      //    a message why the presentation terminated
void Context::SelectService(cChannel *service) {
   //TODO
}
      //I am not sure what this is supposed to do.
      //It is guaranteed that ServiceStatus is called subsequently.
void Context::StopPresentation() {
   //TODO
}

void Context::ChannelSwitch(const cDevice *Device, int ChannelNumber) {
   //TODO
}

void Context::Replaying(const cControl *Control, const char *Name) {
   //TODO
}

cList<ServiceStatus> ServiceStatus::list;

ServiceStatus::ServiceStatus() {
   list.Add(this);
}

ServiceStatus::~ServiceStatus() {
   list.Del(this, false);
}

void ServiceStatus::MsgServiceEvent(Message event, Service service) {
   for (ServiceStatus *ss = list.First(); ss; ss = list.Next(ss))
      ss->ServiceEvent(event, service);
}


} //namespace Service


