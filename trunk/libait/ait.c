/**************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include "ait.h"

#include <vdr/tools.h>
#include <vdr/device.h>
#include <vdr/config.h>


namespace ApplicationInfo {

cApplications Applications;


/* --------- cMyApplicationMonitor */

void cApplicationMonitor::InitializeAllDevices() {
   for (int i=0; i<DvbSi::Database::getNumberOfDatabases(); i++) {
      DvbSi::Database::Ptr d=DvbSi::Database::getDatabase(i);
      if (d)
         new cApplicationMonitor(d);
   }
}

cApplicationMonitor::cApplicationMonitor(DvbSi::Database::Ptr d)
  : DvbSi::Filter(d), ts(0), request(0), numAitEntries(0) 
{
   d->addDataSwitchListener(this);
}

cApplicationMonitor::~cApplicationMonitor() {
   delete request;
}

void cApplicationMonitor::Result(DvbSi::Request *r) {
   printf("cApplicationMonitor::Result %p %d\n", r, r->getResultCode());
   if (r==request && ts && r->getResultCode() == DvbSi::ResultCodeSuccess) {
      DvbSi::PMT pmt;
      SI::DescriptorTag tags[3];
      tags[0]=SI::ApplicationSignallingDescriptorTag;
      tags[1]=SI::CarouselIdentifierDescriptorTag;
      tags[2]=SI::StreamIdentifierDescriptorTag;
      for (DvbSi::PMTServicesRequest::iterator it=((DvbSi::PMTServicesRequest *)request)->list.begin(); it != ((DvbSi::PMTServicesRequest *)request)->list.end(); ++it) {
         pmt=(*it);
         cTransportStream::ApplicationService *service=ts->GetService(pmt.getServiceId());
         //printf("Having Service %d\n", pmt.getServiceId());
         if (service->pmtVersion==pmt.getVersionNumber())
            continue;
         service->Reset();
         service->pmtVersion=pmt.getVersionNumber();
         DvbSi::PMT::Stream stream;
         #if VDRVERSNUM > 10312
         for (SI::Loop::Iterator it; pmt.streamLoop.getNext(stream, it); ) {
         #else
         for (SI::Loop::Iterator it; pmt.streamLoop.hasNext(it); ) {
            stream=pmt.streamLoop.getNext(it);
         #endif
            //printf("Elementary Stream %d type %d %d\n", stream.getPid(), stream.getStreamType(), stream.streamDescriptors.getLength());
            SI::Descriptor *d;
            for (SI::Loop::Iterator it2; (d = stream.streamDescriptors.getNext(it2, tags, 3)); ) {
               switch (d->getDescriptorTag()) {
               case SI::ApplicationSignallingDescriptorTag:
                  service->AddAitPid(stream.getPid());
                  //often several services hint at the same AIT pid, so check.
                  if (!Matches(stream.getPid(), SI::TableIdAIT)) {
                     printf("Adding AIT %d %d\n", stream.getPid(), SI::TableIdAIT);
                     Add(stream.getPid(), SI::TableIdAIT);
                     SetStatus(true);
                  }
                  break;
               case SI::CarouselIdentifierDescriptorTag:
                  service->AddCarouselId(stream.getPid(), ((SI::CarouselIdentifierDescriptor *)d)->getCarouselId());
                  break;
               case SI::StreamIdentifierDescriptorTag:
                  /* This implementation currently only uses the stream_identifier_descriptor which provides an 8-bit component_tag.
                     The spec additionally defines the association_tag_descriptor which provides a 16-bit association_tag.
                     An association_tag with the upper byte set to 0x0 is equivalent to the component_tag with the same lower byte.
                     DSM-CC seems to use the 16-bit value, but for now I keep using the component_tag until I find a channel
                     which does not work with that.
                  */
                  service->AddComponentTag(stream.getPid(), ((SI::StreamIdentifierDescriptor*)d)->getComponentTag());
               default:
                  break;
               }
            delete d;
            }
         }
      }
   } else if (r==request &&  r->getResultCode() == DvbSi::ResultCodeDataSwitch) {
      //printf("libait: Data switch thus cancelling pmt request\n");
      delete request;
      request=0;
   }
}

void cApplicationMonitor::DataSwitch(DvbSi::Database::Ptr db) {
   /*if (request) {
      printf("AIT Data switch thus cancelling pmt filter\n");
      request->CancelRequest();
      delete request;
      request=0;
   }*/
   ts=Applications.GetTransportStream(db->getCurrentSource(), db->getNetworkId(), db->getPat().getTransportStreamId());
   delete request;
   request=0;
   request=db->retrievePMTServices(this);
   //printf("Current TS %d %d\n", ts->GetNid(), ts->GetTid());
}

void cApplicationMonitor::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   if (ts && Tid==SI::TableIdAIT) {
      cAIT ait(&Applications, ts);
      ait.setData(Data, Length, false);
      if (!ait.CheckCRCAndParse())
         return;
      if (!AitVersionChanged(Pid, ait.getVersionNumber()))
         return;
      ait.ProcessAIT(Pid);
   }
}

//taken from VDR 1.3.0, Copyright Klaus Schmidinger
bool cApplicationMonitor::AitVersionChanged(int AitPid, int Version) {
  Version <<= 16;
  for (int i = 0; i < numAitEntries; i++) {
      if ((aitVersion[i] & 0x0000FFFF) == AitPid) {
         bool Changed = (aitVersion[i] & 0x00FF0000) != Version;
         if (Changed)
            aitVersion[i] = AitPid | Version;
         return Changed;
         }
      }
  if (numAitEntries < MAXAITENTRIES)
     aitVersion[numAitEntries++] = AitPid | Version;
  else
     esyslog("MAXAITENTRIES exceeded. Please contact author of this MHP plugin");
  return true;
}




/* --------- cApplications ------------- */

cApplications::cApplications() {
   //currentChannel=0;
}

cApplications::~cApplications() {
}


/* ------------ cAIT ------------- */

cAIT::cAIT(cApplications *db, cTransportStream *trstr) 
: database(db),
  ts(trstr)
{
}

cAIT::~cAIT() {
}

void cAIT::ProcessAIT(int aitPid) {
     
   printf("Processing AIT %d\n", aitPid);
   
   cApplication               *a;
   bool                        foundTransportProtocol;
   
   cTransportStream::ApplicationService *service=ts->GetServiceForAitPid(aitPid);
   
   if (!service->GetChannel())
      return; //may happen if VDR does not know channel
   
      
   database->tagForDeletion(service, getApplicationType());
   
   SI::Loop::Iterator it1, it2, it3;
   
   SI::Descriptor *d;
   
   //read application loop
   SI::AIT::Application App;
   #if VDRVERSNUM > 10312
   for (it1.reset(); applicationLoop.getNext(App, it1);   ) {
   #else
   for (it1.reset(); applicationLoop.hasNext(it1);   ) {
      App=applicationLoop.getNext(it1);
   #endif
      foundTransportProtocol=false;
      a=new cApplication();
      a->SetApplicationType((cApplication::ApplicationType)getApplicationType()); //same for all apps of this section
      a->SetOid(App.getOrganisationId());
      a->SetAid(App.getApplicationId());
      a->SetControlCode((cApplication::ControlCode)App.getControlCode());
      a->SetService(service);
      //printf("%d %d %d\n", a->GetApplicationType(), a->GetControlCode(), a->GetAid());
               
      //read inner (application specific) loop for the second time
      for (it2.reset(); (d=App.applicationDescriptors.getNext(it2));   ) {
         switch(d->getDescriptorTag()) {
         case SI::MHP_ApplicationDescriptorTag:
           {
            SI::MHP_ApplicationDescriptor *de=(SI::MHP_ApplicationDescriptor*)d;
            a->SetVisibility((cApplication::Visibility)de->getVisibility());
            a->SetServiceBound(de->isServiceBound());
            a->SetPriority(de->getApplicationPriority());
            //read profile loop
            SI::MHP_ApplicationDescriptor::Profile ape;
            #if VDRVERSNUM > 10312
            for (it3.reset(); de->profileLoop.getNext(ape, it3);   ) {
            #else
            for (it3.reset(); de->profileLoop.hasNext(it3);   ) {
               ape=de->profileLoop.getNext(it3);
            #endif
               cApplication::ProfileVersion pv;
               pv.versionMajor=ape.getVersionMajor();
               pv.versionMinor=ape.getVersionMinor();
               pv.versionMicro=ape.getVersionMicro();
               pv.profile=(cApplication::Profile)ape.getApplicationProfile();
               a->AddProfileVersion(pv);               
            }
            //find suitable transport protocol
            for (int i=0; i<de->transportProtocolLabels.getCount() && !foundTransportProtocol; i++) {
               cTransportProtocol *tp=FindTransportProtocol(service, de->transportProtocolLabels[i], &commonDescriptors, &App.applicationDescriptors);
               if (tp) {
                  a->SetTransportProtocol(tp);
                  foundTransportProtocol=true;
                  break;
               }
            }
           }
           break;
         case SI::MHP_ApplicationNameDescriptorTag:
           {
            SI::MHP_ApplicationNameDescriptor *de=(SI::MHP_ApplicationNameDescriptor*)d;
            SI::MHP_ApplicationNameDescriptor::NameEntry ne;
            #if VDRVERSNUM > 10312
            for (it3.reset(); de->nameLoop.getNext(ne, it3);   ) {
            #else
            for (it3.reset(); de->nameLoop.hasNext(it3);   ) {
               ne=de->nameLoop.getNext(it3);
            #endif
               char text[256];
               a->AddName(ne.languageCode, ne.name.getText(text, sizeof(text)));
            }
           }
           break;
         case SI::MHP_DVBJApplicationDescriptorTag:
           {
            SI::MHP_DVBJApplicationDescriptor *de=(SI::MHP_DVBJApplicationDescriptor*)d;
            SI::MHP_DVBJApplicationDescriptor::ApplicationEntry ne;
            #if VDRVERSNUM > 10312
            for (it3.reset(); de->applicationLoop.getNext(ne, it3);   ) {
            #else
            for (it3.reset(); de->applicationLoop.hasNext(it3);   ) {
               ne=de->applicationLoop.getNext(it3);
            #endif
               char text[256];
               a->AddParameter(ne.parameter.getText(text, sizeof(text)));
            }
           }
           break;
         case SI::MHP_DVBJApplicationLocationDescriptorTag:
           {
            SI::MHP_DVBJApplicationLocationDescriptor *de=(SI::MHP_DVBJApplicationLocationDescriptor*)d;
            char text[256];           
            a->SetBaseDir(de->baseDirectory.getText(text, sizeof(text)));
            a->SetClassPath(de->classPath.getText(text, sizeof(text)));
            a->SetInitialClass(de->initialClass.getText(text, sizeof(text)));
           }
           break;
         case SI::MHP_ApplicationIconsDescriptorTag:
           {
            SI::MHP_ApplicationIconsDescriptor *de=(SI::MHP_ApplicationIconsDescriptor*)d;
            a->SetIconFlags(de->getIconFlags());
            char text[256];
            a->SetIconPath(de->iconLocator.getText(text, sizeof(text)));
           }
           break;
         default:
           break;
         }
         delete d;
      } //end of specific-descriptors loop
      
         //parsed all descriptors. Now add application only to the
         //list if a usable TransportProtocol has been found
         //(i.e. the app can be received)
      if (!foundTransportProtocol)
         delete a;
      else {
         printf("Read AIT of application %s type %d ccode %d aid %d oid %d\n", a->GetName(0) ? a->GetName(0)->name.c_str() : "noname", a->GetApplicationType(), a->GetControlCode(), a->GetAid(), a->GetOid());
         database->addApplication(a);
      }
   } //end of application loop
   database->deleteTagged();
   //database->TransportProtocols.deleteNonUsed();
}

cTransportProtocol *cAIT::FindTransportProtocol(cTransportStream::ApplicationService *service, int label, SI::DescriptorLoop *commonDescriptors, SI::DescriptorLoop *specificLoop) {
   SI::Loop::Iterator it;
   SI::MHP_TransportProtocolDescriptor *transport=0;
   //read common loop
   SI::Descriptor *d;
   for (it.reset(); (d=commonDescriptors->getNext(it, SI::MHP_TransportProtocolDescriptorTag));   ) {
      SI::MHP_TransportProtocolDescriptor *tpd=((SI::MHP_TransportProtocolDescriptor *)d);
      if (tpd->getProtocolLabel() == label) { //found protocol
         transport=tpd;
         break;
      }
      delete d;
   }   
   for (it.reset(); !transport && (d=specificLoop->getNext(it, SI::MHP_TransportProtocolDescriptorTag));   ) {
      SI::MHP_TransportProtocolDescriptor *tpd=((SI::MHP_TransportProtocolDescriptor *)d);
      if (tpd->getProtocolLabel() == label) { //found protocol
         transport=tpd;
         break;
      }
      delete d;
   }
   //printf("Found transport protocol %d\n", transport->getProtocolLabel());
   if (transport && cTransportProtocol::isSupported((cTransportProtocol::Protocol)transport->getProtocolId())) {
      switch(transport->getProtocolId()) {
         case cTransportProtocol::ObjectCarousel:
           {
            if (transport->isRemote())
               break; //TODO: implement in libsi, then cApplications.Get/AddTransportStream, otherts->GetService
            cTransportProtocolViaOC *oc=new cTransportProtocolViaOC(transport->getProtocolLabel());
            oc->SetService(service);
            oc->SetComponentTag(transport->getComponentTag());
            oc->SetRemote(false);
            int pid=service->GetPidForComponentTag(transport->getComponentTag());
            oc->SetCarouselId(service->GetCarouselIdForPid(pid));
            oc->SetPid(pid);
            return oc;
           }
         default:
            break;
      }
   }
   //printf("Didn't find transport\n");
   return new cUnsupportedTransportProtocol();
}

} //end of namespace ApplicationInformation

