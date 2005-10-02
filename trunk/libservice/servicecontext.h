/***************************************************************************
 *       Copyright (c) 2005 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef LIBSERVICE_SERVICECONTEXT_H
#define LIBSERVICE_SERVICECONTEXT_H

#include <list>

#include <vdr/channels.h>
#include <vdr/tools.h>
#include <vdr/status.h>

#include <libait/applications.h>

#include "transportstream.h"
#include "service.h"

namespace Service {


   //This class provides the actual implementation of channel switching.
   //As other parts may be concerned, this is moved to a higher layer.
   //The implementor will inform the ContextStatus listeners!
   //See documentation of identically named methods below.
class ServiceSelectionProvider {
public:
   virtual void SelectService(Service::Ptr service) = 0;
   virtual void StopPresentation() = 0;
};

class Context {
public:
   static Context *getContext();
   //void getServices(std::list<Service> services);
   void getApplications(std::list<ApplicationInfo::cApplication::Ptr> &apps);
   Service::Ptr getService();
   bool isPresenting();
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
   void SelectService(Service::Ptr service);
      //I am not sure what this is supposed to do.
      //It is guaranteed that ServiceStatus is called subsequently.
   void StopPresentation();
      //To be called once by the implementing higher layer
   void SetServiceSelectionProvider(ServiceSelectionProvider *p) { provider=p; }
protected:
   Context();
   virtual ~Context() {}
   ServiceSelectionProvider *provider;
private:
   static Context *s_self;
   const cControl *currentControl;
};

class ServiceStatus : public cListObject {
public:
   ServiceStatus();
   virtual ~ServiceStatus();
   //Keep this enum in sync with the constants
   //in javax.tv.service.selection.VDRServiceContext.
   //The messages are inspired by the demands of the JavaTV API
   //rather than the VDR architecture, so some of them might not be fitting too well to reality.
   enum Message {
         //service has successfully been selected
      MessageServiceSelected = 0,
         //service has successfully been selected, but 
         //alternative content due to the CA system is being presented
      MessageServiceSelectedAlternativeContent,
         //presentation terminated because CA permission has been withdrawn
      MessageCaAccessWithdrawn,
         //presentation terminated because necessary resources have become unavailable
      MessageResourcesRemoved,
         //presentation terminated because the service is no longer broadcasting
      MessageServiceVanishedFromNetwork,
         //presentation terminated because the tuner was tuned away
      MessageTunedAway,
         //presentation terminated because the user requested to stop
      MessageUserStop,
         //request for service selection failed because CA permission was not granted
      MessageCaRefusal,
         //request for service selection failed because the service was not found
      MessageContentNotFound,
         //request for service selection failed because necessary resources are not available
      MessageInsufficientResources,
         //request for service selection failed because the content is in a form that cannot be presented
      MessageMissingHandler,
         //request for service selection failed because tuning failed
      MessageTuningFailure
   };
   static void MsgServiceEvent(Message event, Service::Ptr service);
protected:
      //service is either the selected service, the previously selected service which terminated,
      //or the service which failed to be selected. So it is not necessarily identical with
      //the Service returned by Context::getService().
   virtual void ServiceEvent(Message event, Service::Ptr service) {};
private:
   static cList<ServiceStatus> list;
};


}



#endif

