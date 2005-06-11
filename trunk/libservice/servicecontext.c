/***************************************************************************
 *       Copyright (c) 2005 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include <typeinfo>

#include <vdr/player.h>
#include <vdr/channels.h>

#include <mhpcontrol.h>

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
   //may be null
   return currentChannel;
}

bool Context::isPresenting() {
   //Please note that this access of cControl::Control out of VDR's main thread is
   //not thread-safe possibly illegal.
   cControl *c=cControl::Control();
   return (c==0 || typeid(*cControl::Control()) != typeid(MhpControl));
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
   provider->SelectService(service);
}
      //I am not sure what this is supposed to do.
      //It is guaranteed that ServiceStatus is called subsequently.
void Context::StopPresentation() {
   provider->StopPresentation();
}

void Context::ChannelSwitch(const cDevice *Device, int ChannelNumber) {
   if (Device == cDevice::PrimaryDevice()) {
      if (ChannelNumber == 0)
         currentChannel=0;
      else
         currentChannel=Channels.GetByNumber(ChannelNumber);
   }
}

void Context::Replaying(const cControl *Control, const char *Name) {
   /*
   //Only normal recordings are announce by this way!
   //So, if currentControl is 0, this does not mean cControl::Control() is null!
   if (Name==NULL)
      currentControl=0;
   else
      currentControl=Control;
   */
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


