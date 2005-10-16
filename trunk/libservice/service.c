/***************************************************************************
 *       Copyright (c) 2005 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include "service.h"

#include <vdr/thread.h>
#include <vdr/status.h>

// for ReadLock class
#include <libdvbsi/util.h>

#include "manager.h"

namespace Service {

TransportStream::TransportStream(const cChannel *channel) {
   if (channel) {
      data.source=channel->Source();
      data.onid=channel->Nid();
      data.tid=channel->Tid();
   } else {
      data.source=0;
      data.onid=0;
      data.tid=0;
   }
}

ServiceIDAdapter::ServiceIDAdapter(const cChannel *channel)
   : TransportStream(channel)
{
   if (channel)
      sid=channel->Sid();
   else
      sid=0;
}

ServiceListener::~ServiceListener() {
   ServiceManager *manager=ServiceManager::getManager();
   if (manager)
      manager->RemoveServiceListener(this);
}


// --- ServiceManager ---

ServiceManager *ServiceManager::s_self;

ServiceManager *ServiceManager::getManager() {
   if (!s_self)
      s_self = new Manager();
   return s_self;
}

ServiceManager::~ServiceManager() {
   s_self=0;
}

ServiceManager::ServiceLock::ServiceLock() {
   ServiceManager::getManager()->Lock();
}

ServiceManager::ServiceLock::~ServiceLock() {
   ServiceManager::getManager()->Unlock();
}


// --- Manager ---

// A note on locking: With two different mutexes, Channels' rwlock and Manager::mutex,
// there is danger of a deadlock. Both may never be locked at the same time.

Manager::Manager() {
   // Keep this lock here for the whole method, needed as well by Tuner.
   // No mutex protection here at construction.
   DvbSi::ReadLock rwlock(&Channels);
   for (cChannel *chan=Channels.First(); chan; chan=Channels.Next(chan)) {
      ChIDService::Ptr s=new ChIDService(chan);
      s->managerIterator=services.insert(services.end(), s);
   }
   for (int i=0; i<cDevice::NumDevices(); i++) {
      DeviceTuner *tuner = DeviceTuner::checkDevice(cDevice::GetDevice(i));
      if (tuner)
         tuners.push_back(tuner);
   }
   primary=PrimaryDevice::getPrimaryDevice();
   Context::getContext()->SetServiceSelectionProvider(&selectionProvider);
}

Manager::~Manager() {
   cMutexLock lock(&mutex);
   listeners.clear();
   for (ChIDServiceList::iterator it=services.begin(); it != services.end(); ++it)
      delete *it;
   services.clear();
   for (TunerList::iterator it = tuners.begin(); it != tuners.end(); ++it)
      delete *it;
   tuners.clear();
   delete primary;
   primary=0;
}

void Manager::CleanUp() {
   delete this;
}

void Manager::AddServiceListener(ServiceListener *listener) {
   cMutexLock lock(&mutex);
   listeners.push_back(listener);
}

void Manager::RemoveServiceListener(ServiceListener *listener) {
   cMutexLock lock(&mutex);
   listeners.remove(listener);
}

void Manager::ChannelSwitch(const cDevice *Device, int ChannelNumber) {
   if (!ChannelNumber || !Device)
      return;

   cChannel *chan=Channels.GetByNumber(ChannelNumber);
   if (!chan)
      return;

   cMutexLock lock(&mutex);

   DeviceTuner *tuner=(DeviceTuner *)getTunerForDevice(Device);
   SwitchSource source;
   TransportStreamID ts;
   if (tuner && tuner->IsTransportStreamChange(chan, source, ts)) {
      for (ServiceListenerList::iterator it = listeners.begin(); it != listeners.end(); ++it)
         (*it)->TransportStreamChange(ts, source, tuner);
   }

   Service::Ptr service;
   if (Device==primary->getDevice()) {
      if (primary->IsServiceChange(chan, source, service)) {
         for (ServiceListenerList::iterator it = listeners.begin(); it != listeners.end(); ++it)
            (*it)->ServiceChange(service, source);
      }
   }
}

Service::Ptr Manager::findService(Tunable *tunable, ServiceID id, ElementaryStreams *streams) {
   // TODO: Create channel if necessary! This is a stub implementation!
   return findService(id);
}

Service::Ptr Manager::findService(ServiceID id) {
   cMutexLock lock(&mutex);
   for (ChIDServiceList::iterator it=services.begin(); it != services.end(); ++it) {
      if ((**it)==id)
         return *it;
   }
   return Service::Ptr(0);
}

bool Manager::findServices(int onid, int tid, int sid, std::list<Service::Ptr> retlist) {
   cMutexLock lock(&mutex);
   bool success = false;
   for (ChIDServiceList::iterator it=services.begin(); it != services.end(); ++it) {
      if ((*it)->GetSid()==sid && ((*it)->GetTid()==tid || tid == -1) && (*it)->GetNid()==onid) {
         retlist.push_back(*it);
         success=true;
      }
   }
   return success;
}

Service::Ptr Manager::findService(int onid, int tid, int sid) {
   cMutexLock lock(&mutex);
   for (ChIDServiceList::iterator it=services.begin(); it != services.end(); ++it) {
      if ((*it)->GetSid()==sid && ((*it)->GetTid()==tid || tid == -1) && (*it)->GetNid()==onid) {
         return *it;
      }
   }
   return Service::Ptr(0);
}

Tunable *Manager::findTunable(TransportStreamID id) {
   cMutexLock lock(&mutex);
   for (ChIDServiceList::iterator it=services.begin(); it != services.end(); ++it) {
      if ((*it)->GetTransportStreamID()==id)
         return (*it)->getTunable();
   }
   return 0;
}

bool Manager::getTuners(TunerList &t) {
   cMutexLock lock(&mutex);
   t=tuners;
   return tuners.size();
}

Tuner *Manager::getFirstTuner() {
   cMutexLock lock(&mutex);
   if (tuners.size())
      return (tuners.front());
   else
      return 0;
}

Tuner *Manager::getTuner(Tunable *tunable) {
   cMutexLock lock(&mutex);
   const cChannel *chan=tunable->getTunableChannel();
   cDevice *device=cDevice::GetDevice(chan, 0);
   if (device) {
      return getTunerForDevice(device);
   }
   return 0;
}

Tuner *Manager::getTunerTuned(Tunable *tunable) {
   cMutexLock lock(&mutex);
   for (TunerList::iterator it = tuners.begin(); it != tuners.end(); ++it)
      if ((*it)->IsTunedTo(tunable))
         return (*it);
   return 0;
}

void Manager::Lock() {
   mutex.Lock();
}

void Manager::Unlock() {
   mutex.Unlock();
}

Service::Ptr Manager::findService(int channelNumber) {
   cMutexLock lock(&mutex);
   for (ChIDServiceList::iterator it=services.begin(); it != services.end(); ++it)
      if ((*it)->getChannelNumber() == channelNumber)
         return *it;
   return Service::Ptr(0);
}

Service::Ptr Manager::getFirstService() {
   cMutexLock lock(&mutex);
   return services.front();
}

Service::Ptr Manager::getLastService() {
   cMutexLock lock(&mutex);
   return services.front();
}

Service::Ptr Manager::getNextService(Service::Ptr service) {
   cMutexLock lock(&mutex);
   ChIDServiceList::iterator it(((ChIDService::Ptr)service)->managerIterator);
   ++it;
   if (it == services.end())
      return Service::Ptr(0);
   else
      return *it;
}

Service::Ptr Manager::getPreviousService(Service::Ptr service) {
   cMutexLock lock(&mutex);
   ChIDServiceList::iterator it(((ChIDService::Ptr)service)->managerIterator);
   if (it == services.begin())
      return Service::Ptr(0);
   else
      return *(--it);
}

int Manager::getNumberOfServices() {
   cMutexLock lock(&mutex);
   return services.size();
}

int Manager::getNumberOfTuners() {
   cMutexLock lock(&mutex);
   return tuners.size();
}


const cChannel *Manager::getFullChannel(Service::Ptr service) {
   // there is only one Service implementation currently,
   // ChIDService provides a full channel in this way.
   return service->getTunable()->getTunableChannel();
}

Tuner *Manager::getTunerForDevice(const cDevice *device) {
   for (TunerList::iterator it = tuners.begin(); it != tuners.end(); ++it)
      if ((*it)->getDevice() == device)
         return (*it);
   return 0;
}

Service::Ptr Manager::getCurrentService() {
   return findService(cDevice::CurrentChannel());
}


// --- DeviceTuner ---

DeviceTuner::DeviceTuner(cDevice *device, DeliverySystem system)
   : device(device), system(system), currentSwitchSource(SwitchSourceMiddleware)
{
   // Currently the only way to obtain the current channel of a device
   device->AttachFilter(&filter);
}

DeviceTuner *DeviceTuner::checkDevice(cDevice *device) {
   DeliverySystem system=getDeliverySystem(device);
   if (system != DeliverySystemNone)
      return new DeviceTuner(device, system);
   return 0;
}

bool DeviceTuner::Tune(Tunable *tune) {
   return SetChannel(tune->getTunableChannel(), SwitchSourceTuning, true);
}

bool DeviceTuner::IsTunedTo(Tunable *tune) {
   return TransportStream(getChannel())==TransportStream(tune->getTunableChannel());
}

bool DeviceTuner::Provides(Tunable *tune) {
   return device->ProvidesTransponder(tune->getTunableChannel());
}

TransportStreamID DeviceTuner::getCurrentTransportStream() {
   return TransportStream(getChannel()).GetTransportStreamID();
}

bool DeviceTuner::SetService(Service::Ptr service) {
   const cChannel *chan=((Manager *)ServiceManager::getManager())->getFullChannel(service);
   return SetChannel(chan, SwitchSourceServiceSwitch, false);
}

bool DeviceTuner::SetChannel(const cChannel *chan, SwitchSource source, bool isTuning) {
   {
      cMutexLock lock(&mutex);
      // currentSwitchSource serves as a flag
      while (currentSwitchSource != SwitchSourceMiddleware)
         condVar.Wait(mutex);
      if (isTuning) {
         if (TransportStream(getChannel())==TransportStream(chan))
            return true;
      } else {
         if (ServiceIDAdapter(getChannel())==ServiceIDAdapter(chan))
            return true;
      }
      currentSwitchSource=source;
   }
   // Don't dare having a locked mutex when calling this function with unknown implications!
   // Setting the currentSwitchSource which is accessed only under mutex is sufficient.
   bool success=device->SwitchChannel(chan, !isTuning);
   {
      cMutexLock lock(&mutex);
      // reset flag
      currentSwitchSource=SwitchSourceMiddleware;
      condVar.Broadcast();
   }
   return success;
}

DeliverySystem DeviceTuner::getDeliverySystem(cDevice *device) {
   DeliverySystem system=DeliverySystemNone;
   DvbSi::ReadLock lock(&Channels);
   for (cChannel *channel = Channels.First(); channel; channel = Channels.Next(channel)) {
      if (!channel->GroupSep() && device->ProvidesTransponder(channel)) {
         if (channel->IsSat())
            system=DeliverySystemSatellite;
         else if (channel->IsTerr())
            system=DeliverySystemTerrestrial;
         else if (channel->IsCable())
            system=DeliverySystemCable;
         break;
      }
   }
   //no channel found that is provided by the device
   // => device has no reception capabilities (is there a more elegant way to find this out?)
   return system;
}

bool DeviceTuner::IsTransportStreamChange(const cChannel *chan, SwitchSource &source, TransportStreamID &newTs) {
   cMutexLock lock(&mutex);
   newTs = TransportStream(chan).GetTransportStreamID();
   if (ts != newTs) {
      ts=newTs;
      source=currentSwitchSource;
      return true;
   } else
      return false;
}


// --- PrimaryDevice ---

PrimaryDevice *PrimaryDevice::getPrimaryDevice() {
   return new PrimaryDevice(cDevice::PrimaryDevice());
}

PrimaryDevice::PrimaryDevice(cDevice *device) : DeviceTuner(device, DeliverySystemNone) {
}

bool PrimaryDevice::SwitchService(Service::Ptr service) {
   return SetService(service);
}

bool PrimaryDevice::IsServiceChange(cChannel *chan, SwitchSource &source, Service::Ptr &newService) {
   if (currentSwitchSource == SwitchSourceTuning) {
      // Tuning only does not constitute a service change
      return false;
   } else if (ServiceIDAdapter(chan).GetServiceID() != getCurrentServiceID()) {
      currentService=0;
      newService=getCurrentService();
      source=currentSwitchSource;
      return true;
   } else
      return false;
}

Service::Ptr PrimaryDevice::getCurrentService() {
   if (!currentService)
      currentService=((Manager *)ServiceManager::getManager())->getCurrentService();
   return currentService;
}



// --- ChIDService ---

ChIDService::ChIDService(const cChannel *chan) : Service(chan) {
   id=chan->GetChannelID();
}

const cChannel *ChIDService::getChannel() {
   // here it should be guaranteed that a valid channel is returned,
   // regardless of deletion or other changes.
   // TODO / wait for VDR 1.5
   return Channels.GetByChannelID(id);
}

int ChIDService::getVideoPid() {
   return getChannel()->Vpid();
}

int ChIDService::getTeletextPid() {
   return getChannel()->Tpid();
}

void ChIDService::getAudioPids(ElementaryStreams::PidList &list) {
   const cChannel *chan=getChannel();
   const int *pids=chan->Apids();
   for (int i=0; pids[i]; i++) {
      PidAndLanguage pl;
      pl.pid=pids[i];
      pl.language=chan->Alang(i);
      list.push_back(pl);
   }
}

void ChIDService::getDolbyAudioPids(ElementaryStreams::PidList &list) {
   const cChannel *chan=getChannel();
   const int *pids=chan->Dpids();
   for (int i=0; pids[i]; i++) {
      PidAndLanguage pl;
      pl.pid=pids[i];
      pl.language=chan->Dlang(i);
      list.push_back(pl);
   }
}

void ChIDService::getSubtitlingPids(ElementaryStreams::PidList &list) {
   const cChannel *chan=getChannel();
   const int *pids=chan->Spids();
   for (int i=0; pids[i]; i++) {
      PidAndLanguage pl;
      pl.pid=pids[i];
      pl.language=chan->Slang(i);
      list.push_back(pl);
   }
}

DeliverySystem ChIDService::getDeliverySystem() {
   const cChannel *chan=getChannel();
   if (chan->IsSat())
      return DeliverySystemSatellite;
   else if (chan->IsTerr())
      return DeliverySystemTerrestrial;
   else if (chan->IsCable())
      return DeliverySystemCable;
   else
      return DeliverySystemOther;
}

void ChIDService::getCaIDs(CaIDList &list) {
   const cChannel *chan=getChannel();
   int caid;
   for (int i=0; (caid=chan->Ca(i)); i++)
      list.push_back(caid);
}

bool ChIDService::isFreeToAir() {
   return !getChannel()->Ca();
}


// --- ManagerServiceSelectionProvider ---

ManagerServiceSelectionProvider::ManagerServiceSelectionProvider() {
}

void ManagerServiceSelectionProvider::SelectService(Service::Ptr service) {
   printf("ControlServiceSelectionProvider::SelectService(): Selection channel %d-%d-%d\n", service->GetNid(), service->GetTid(), service->GetSid());

   if (!service) {
      ServiceStatus::MsgServiceEvent(ServiceStatus::MessageContentNotFound, service);
      return;
   }
   bool success;
   {
      ServiceManager::ServiceLock lock;
      success=ServiceManager::getManager()->getPrimaryDecoder()->SwitchService(service);
   }
   if (success) {
      ServiceStatus::MsgServiceEvent(ServiceStatus::MessageServiceSelected, service);
   } else {
      ServiceStatus::MsgServiceEvent(ServiceStatus::MessageInsufficientResources, service);
   }
}

void ManagerServiceSelectionProvider::StopPresentation() {
   //TODO: Find out what to do here
   printf("ManagerServiceSelectionProvider::StopPresentation(): Doing nothing\n");
   ServiceStatus::MsgServiceEvent(ServiceStatus::MessageUserStop, ServiceManager::getManager()->getPrimaryDecoder()->getCurrentService());
}


}

