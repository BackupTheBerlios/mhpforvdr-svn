/***************************************************************************
 *       Copyright (c) 2005 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef LIBSERVICE_MANAGER_H
#define LIBSERVICE_MANAGER_H

#include "service.h"

#include <vdr/channels.h>
#include <vdr/device.h>
#include <vdr/filter.h>
#include <vdr/status.h>

#include "servicecontext.h"

// This file contains classes implementing the interfaces in service.h

namespace Service {

// A lightweight cChannel wrapper
class ChIDService : public Service, public Tunable, public ElementaryStreams, public ChannelInformation {
public:
   typedef ChIDService *Ptr;
   ChIDService(const cChannel *chan);
   virtual Tunable *getTunable() { return this; }
   virtual ChannelInformation *getChannelInformation() { return this; }
   virtual ElementaryStreams *getElementaryStreams() { return this; }

   // ElementaryStreams
   virtual int getVideoPid();
   virtual void getAudioPids(PidList &list);
   virtual void getDolbyAudioPids(PidList &list);
   virtual void getSubtitlingPids(PidList &list);
   virtual int getTeletextPid();

   // ChannelInformation
   virtual const char *getName(void) { return getChannel()->Name(); }
   virtual const char *getShortName(bool OrName = false) { return getChannel()->ShortName(); }
   virtual const char *getProvider(void) { return getChannel()->Provider(); }
   virtual const char *getPortalName(void) { return getChannel()->PortalName(); }

   // ChannelListElement
   virtual int getChannelNumber() { return getChannel()->Number(); }

   // Tunable
   virtual const cChannel *getTunableChannel() { return getChannel(); }
   std::list<ChIDService::Ptr>::iterator managerIterator;
protected:
   const cChannel *getChannel();
   tChannelID id;
};

class DeviceTuner : public Tuner {
public:
   virtual ~DeviceTuner() {}
   static DeviceTuner *checkDevice(cDevice *device);

   virtual bool Tune(Tunable *tune);
   virtual bool IsTunedTo(Tunable *tune);
   virtual cDevice *getDevice() { return device; }
   virtual DeliverySystem getDeliverySystem() { return system; }

   virtual bool SetService(Service::Ptr service);
   bool IsTransportStreamChange(const cChannel *chan, SwitchSource &source, TransportStreamID &ts);
protected:
   DeviceTuner(cDevice *device, DeliverySystem system);
   static DeliverySystem getDeliverySystem(cDevice *device);
   bool SetChannel(const cChannel *chan, SwitchSource source, bool onlyCheckTune);
   cDevice *device;
   DeliverySystem system;
   class FilterForChannel : public cFilter {
      public:
         const cChannel *getChannel() { return cFilter::Channel(); }
      protected:
         virtual void Process(short unsigned int, unsigned char, const u_char*, int) {}
   };
   FilterForChannel filter;
   const cChannel *getChannel() { return filter.getChannel(); }
   SwitchSource currentSwitchSource;
   cMutex mutex;
   cCondVar condVar;
   TransportStreamID ts;
};

class PrimaryDevice : public PrimaryDecoder, public DeviceTuner {
public:
   static PrimaryDevice *getPrimaryDevice();

   virtual bool SwitchService(Service::Ptr service);
   virtual Service::Ptr getCurrentService();
   virtual ServiceID getCurrentServiceID() { return getCurrentService()->GetServiceID(); }

   bool IsServiceChange(cChannel *chan, SwitchSource &source, Service::Ptr &newService);
protected:
   PrimaryDevice(cDevice *device);
   Service::Ptr currentService;
};

class ManagerServiceSelectionProvider : public ServiceSelectionProvider {
   public:
      ManagerServiceSelectionProvider();
      virtual void SelectService(Service::Ptr service);
      virtual void StopPresentation();
   protected:
};

// ServiceManager based on ChIDService
class Manager : public ServiceManager, public cStatus {
public:
   Manager();
   ~Manager();

   virtual void CleanUp();
   virtual void Initialize() {}

   virtual void AddServiceListener(ServiceListener *listener);
   virtual void RemoveServiceListener(ServiceListener *listener);
   virtual PrimaryDecoder *getPrimaryDecoder() { return primary; }
   virtual Service::Ptr findService(Tunable *tunable, ServiceID id, ElementaryStreams *streams);
   virtual Service::Ptr findService(ServiceID id);
   virtual bool findService(int nid, int tid, int sid, std::list<Service::Ptr> services);
   virtual Tunable *findTunable(TransportStreamID id);
   virtual bool getTuners(std::list<Tuner *> &tuners);
   virtual Tuner *getFirstTuner();
   virtual Tuner *getTuner(Tunable *tunable);
   virtual Tuner *getTunerTuned(Tunable *tunable);
   virtual void Lock();
   virtual void Unlock();
   virtual Service::Ptr findService(int channelNumber);
   virtual Service::Ptr getFirstService();
   virtual Service::Ptr getLastService();
   virtual Service::Ptr getNextService(Service::Ptr);
   virtual Service::Ptr getPreviousService(Service::Ptr);
   virtual int getNumberOfServices();
   virtual int getNumberOfTuners();

   const cChannel *getFullChannel(Service::Ptr service);
   Service::Ptr getCurrentService();
protected:
   virtual void ChannelSwitch(const cDevice *Device, int ChannelNumber);
private:
   typedef std::list<ChIDService::Ptr> ChIDServiceList;
   typedef std::list<ServiceListener *> ServiceListenerList;
   typedef std::list<Tuner *> TunerList;
   ServiceListenerList listeners;
   ChIDServiceList services;
   TunerList tuners;
   Tuner *getTunerForDevice(const cDevice *device);
   PrimaryDevice *primary;
   cMutex mutex;
   ManagerServiceSelectionProvider selectionProvider;
};



}


#endif


