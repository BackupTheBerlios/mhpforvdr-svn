/***************************************************************************
 *       Copyright (c) 2005 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef LIBSERVICE_SERVICE_H
#define LIBSERVICE_SERVICE_H

#include <list>
#include <vector>
#include <string>
#include <libdsmcc/util.h>

#include "transportstream.h"

class cDevice;

namespace Service {

   // The possible delivery systems.
   // DeliverySystemNone means no tuner at all (not always applicable)
   // DeliverySystemSatellite, -Cable and -Terrestrial means DVB-S, -C or T respectively.
   // DeliverySystemOther means a tuner with a different delivery system.
enum DeliverySystem 
     {
      DeliverySystemNone, DeliverySystemSatellite,
      DeliverySystemCable, DeliverySystemTerrestrial,
      DeliverySystemOther
     };

 // An interface representing an object a tuner can be tuned to,
// such as a transport stream or transponder.
class Tunable {
public:
   // Returns a cChannels which holds all necessary data for tuning.
   // Any other fields (IDs, PIDs, Names, ...) shall be ignored.
   // Note that <vdr/channels.h> is not included by this file.
   virtual const cChannel *getTunableChannel() = 0;
   virtual DeliverySystem getDeliverySystem() = 0;
};

// An interface representing a collection of Elementary Streams
class ElementaryStreams {
public:
   struct PidAndLanguage {
      int pid;
      std::string language;
   };
   typedef std::vector<PidAndLanguage> PidList;
   virtual int getVideoPid() = 0;
   virtual void getAudioPids(PidList &list) = 0;
   virtual void getDolbyAudioPids(PidList &list) = 0;
   virtual void getSubtitlingPids(PidList &list) = 0;
   virtual int getTeletextPid() = 0;
};

// An interface providing human-readable information about a channel
class ChannelInformation {
public:
   virtual const char *getName(void) = 0;
   // If there is a short name, it is returned.
   // If there is not short name, and orname is false, a null is returned,
   // if orname is true, the (long) name is returned.
   virtual const char *getShortName(bool OrName = false) = 0;
   virtual const char *getProvider(void) = 0;
   virtual const char *getPortalName(void) = 0;
};

class CaInformation {
public:
   typedef std::vector<int> CaIDList;
   virtual void getCaIDs(CaIDList &list) = 0;
   virtual bool isFreeToAir() = 0;
};

// An interface for elements of a ChannelList
class ChannelListElement {
public:
   virtual int getChannelNumber() = 0;
};

// The main interface representing a service.
// Objects of this class may be stored.
class Service : public ServiceIDAdapter, public ChannelListElement {
public:
   Service(int so, int n, int t, int sid) : ServiceIDAdapter(so, n, t, sid) {}
   Service(const cChannel *channel) : ServiceIDAdapter(channel) {}
   typedef Service* Ptr;
   virtual Tunable *getTunable() = 0;
   virtual ChannelInformation *getChannelInformation() = 0;
   virtual ElementaryStreams *getElementaryStreams() = 0;
   virtual CaInformation *getCaInformation() = 0;
};

enum SwitchSource {
                    // origin is unknown
                    SwitchSourceUnknown,
                    // originates from the VDR middleware (TransportStreamChange or ServiceChange)
                    SwitchSourceMiddleware,
                    // originates from a service switch (TransportStreamChange or ServiceChange)
                    SwitchSourceServiceSwitch,
                    // originates from a tuning operation (only TransportStreamChange)
                    SwitchSourceTuning
                  };

// A Tuner is a device that can receive and be tuned to Tunable objects.
class Tuner {
public:
   // Tunes to given transponder, without service change.
   // Returns true if tuning was successful or if Tuner was already tuned to transponder.
   virtual bool Tune(Tunable *tune) = 0;
   // Returns true if this tuned is currently tuned to given transponder
   virtual bool IsTunedTo(Tunable *tune) = 0;
   // Returns if the tuner is capable of receiving the given Tunable
   virtual bool Provides(Tunable *tune) = 0;
   // Returns the ID of the current transport stream
   virtual TransportStreamID getCurrentTransportStream() = 0;
   // The cDevice of this tuner. Note that <vdr/device.h> is not included by this file.
   virtual cDevice *getDevice() = 0;
   // Get delivery system.
   virtual DeliverySystem getDeliverySystem() = 0;
};

class PrimaryDecoder {
public:
   // The primary decoder holds the notion of one "current service".
   // A tuner is typically tuned to that service and delivering the data, unless
   // a recording is played back etc. In that case, there is still the same current service as before.
   // Sets the given service, tuning if necessary
   // Returns true if switching was successful or if service was already set.
   virtual bool SwitchService(Service::Ptr service) = 0;
   // Returns the current service
   virtual Service::Ptr getCurrentService() = 0;
   // Return ServiceID of current service
   virtual ServiceID getCurrentServiceID() = 0;
   // Sets the given service, without tuning.
   // If tuning is required, this will fail.
   // Returns true if setting was successful or if service was already set.
   //virtual bool SetService(Service::Ptr service) = 0;
};

// An interface for objects interested in tuning and service change events.
// Call ServiceManager::AddServiceListener to receive notifications.
// All changes reported here are guaranteed to be actual changes, i.e. no change will
// be signalled where the new TS/service is the same as the old one.
class ServiceListener {
public:
   virtual ~ServiceListener();
   // Reports a change of the transport stream, i.e. actual tuning.
   // A ServiceChange _may_ happen afterwards.
   virtual void TransportStreamChange(TransportStreamID ts, SwitchSource source, Tuner *tuner) {}
   // Reports a change of the Service. A TransportStreamChange _may_ happen before.
   // The notion of a service change only applies to the PrimaryDecoder, i.e. there is
   // exactly one current service at a time.
   virtual void ServiceChange(Service::Ptr service, SwitchSource source) {}
};

class ServiceManager {
public:
   static ServiceManager *getManager();

   virtual void Initialize() = 0;
   virtual void CleanUp() = 0;

   virtual void AddServiceListener(ServiceListener *listener) = 0;
   virtual void RemoveServiceListener(ServiceListener *listener) = 0;

   // Returns the primary decoder, which allows to switch the current service
   virtual PrimaryDecoder *getPrimaryDecoder() = 0;

   // Tries to find a service or create a new one for the given parameters. Retuns NULL on failure.
   virtual Service::Ptr findService(Tunable *tunable, ServiceID id, ElementaryStreams *streams) = 0;

   // Finds a service for the given ID. Retuns a null Ptr if none is found.
   // None of the implications below apply, specifically, the TID is not ignored if it is -1.
   virtual Service::Ptr findService(ServiceID id) = 0;
   // Channels with different sources (which is a VDR specific parameter), but same ONID-TID-SID
   // should be identical. Even more, channels with same ONID-SID should be identical.
   // However, there may be broken SI data out there.
   // Tries to find all services for the given ID. If the TID is -1, it is ignored.
   // Returns false if none are found.
   virtual bool findServices(int onid, int tid, int sid, std::list<Service::Ptr> services) = 0;
   // Same implications as above.
   // Returns the service with given ID, or the most probable channel if multiple services are found.
   // One valid way to choose the most probable channel is to return the first found channel.
   virtual Service::Ptr findService(int onid, int tid, int sid) = 0;
   // Finds the service associated with the given channel number. Retuns a null Ptr if channel number is invalid.
   virtual Service::Ptr findService(int channelNumber) = 0;
   // Get the current number of services
   virtual int getNumberOfServices() = 0;

   // Enumerating the service list: Get first channel.
   // If there is a channel with channel number 1, it will probably be the one returned, but this is not guaranteed.
   virtual Service::Ptr getFirstService() = 0;
   // Enumerating the service list.
   virtual Service::Ptr getLastService() = 0;
   virtual Service::Ptr getNextService(Service::Ptr) = 0;
   virtual Service::Ptr getPreviousService(Service::Ptr) = 0;

   // Finds a Tunable object for the given ID. Retuns NULL if none is found.
   virtual Tunable *findTunable(TransportStreamID id) = 0;

   // Get the list of currently available tuners. Returns true if list is valid and contains at least one tuner.
   virtual bool getTuners(std::list<Tuner *> &tuners) = 0;
   // Returns the number of available tuners
   virtual int getNumberOfTuners() = 0;
   // Returns the first tuner of the list, or NULL if no tuner is available
   virtual Tuner *getFirstTuner() = 0;
   // Returns a tuner that can receive the given Tunable, or NULL if none is found.
   // No tuning will be triggered from this call!
   virtual Tuner *getTuner(Tunable *tunable) = 0;
   // Returns a tuner that is already tuned to given Tunable at the time of the call, or NULL if none is found.
   // No tuning will be triggered from this call!
   virtual Tuner *getTunerTuned(Tunable *tunable) = 0;

   // While the validity of Service objects is guaranteed,
   // a ServiceLock shall be acquired on a stack frame accessing any other objects
   class ServiceLock {
      public:
         ServiceLock();
         ~ServiceLock();
   };
   virtual void Lock() = 0;
   virtual void Unlock() = 0;
protected:
   virtual ~ServiceManager();
private:
   static ServiceManager *s_self;
};


}


#endif

