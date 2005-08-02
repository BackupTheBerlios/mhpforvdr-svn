/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include <typeinfo>

#include <vdr/device.h>

#include "applicationmenu.h"
#include "mhpcontrol.h"
#include "i18n.h"

int cLocalApplication::nextId = -1;

cLocalApplication::cLocalApplication(char *name, char *basePath, char *initialClass, ApplicationInfo::cTransportProtocol *tp) {
   SetBaseDir(basePath);
   SetInitialClass(initialClass);
   SetTransportProtocol(tp);
   //give local apps unique app ids, even if illegal ones
   SetAid(nextId--);
   SetOid(-1);
   SetControlCode(Present);
   SetApplicationType(LocalDVBJApplication);
   SetServiceBound(false);
   AddName("deu", name); //TODO: change "deu" to real VDR language
}

MhpApplicationMenuItem::MhpApplicationMenuItem(ApplicationInfo::cApplication::Ptr a) : app(a) {
   char *buffer = NULL;
   
   const char *name;
   if (app->GetNumberOfNames())
      name=app->GetName(0)->name.c_str();
   else
      name=tr("<No name available>");
      
   if (app->GetTransportProtocol()->GetProtocol() == ApplicationInfo::cTransportProtocol::Local)
      asprintf(&buffer, "%s", name);
   else {
      cChannel *chan=app->GetChannel();
      asprintf(&buffer, "%s - %s", chan ? chan->Name() : tr("Unknown channel"), name);
   }
   
   SetText(buffer,false);
}

MhpApplicationMenuLabel::MhpApplicationMenuLabel(const char *text) {
   char *buffer = NULL;
   SetSelectable(false);
   asprintf(&buffer, "---%s ----------------------------------------------------------------", text);
   SetText(buffer,false);
}

MhpApplicationMenu::MhpApplicationMenu(std::list<ApplicationInfo::cApplication::Ptr> *l) : cOsdMenu(tr("MHP Applications")) {
   std::list<ApplicationInfo::cApplication::Ptr> apps;
   if (ApplicationInfo::Applications.findApplications(apps)) {
      bool labelSet=false;
      for (std::list<ApplicationInfo::cApplication::Ptr>::iterator it=apps.begin(); it != apps.end(); ++it) {
         if (GetReceptionState((*it)->GetChannel()) == StateCanBeReceived)
            Add(new MhpApplicationMenuItem(*it));
      }
      for (std::list<ApplicationInfo::cApplication::Ptr>::iterator it=apps.begin(); it != apps.end(); ++it) {
         if (GetReceptionState((*it)->GetChannel()) == StateNeedsTuning) {
            if (!labelSet) {
               Add(new MhpApplicationMenuLabel(tr("Tuning required:")));
               labelSet=true;
            }
            Add(new MhpApplicationMenuItem(*it));
         }
      }
      labelSet=false;
      for (std::list<ApplicationInfo::cApplication::Ptr>::iterator it=l->begin(); it != l->end(); ++it) {
         if (GetReceptionState((*it)->GetChannel()) == StateCanTemporarilyNotBeReceived) {
            if (!labelSet) {
               Add(new MhpApplicationMenuLabel(tr("Currently not available:")));
               labelSet=true;
            }
            Add(new MhpApplicationMenuItem(*it));
         }
      }
   } else {
      Add(new MhpApplicationMenuLabel(tr("No MHP applications available")));
   }
   if (l->size()) {
      Add(new MhpApplicationMenuLabel(tr("Local applications:")));
      for (std::list<ApplicationInfo::cApplication::Ptr>::iterator it=l->begin(); it != l->end(); ++it)
         Add(new MhpApplicationMenuItem(*it));
   }
}

eOSState MhpApplicationMenu::ProcessKey(eKeys Key) {
  eOSState state = cOsdMenu::ProcessKey(Key);

  if(state == osUnknown) {
    switch(Key) {
      case kOk:
        {
         cOsdItem *item=Get(Current());
         if (typeid(*item) == typeid(MhpApplicationMenuItem)) {
            MhpApplicationMenuItem *appitem=static_cast<MhpApplicationMenuItem *>(item);
            if (appitem->GetApplication()->GetTransportProtocol()->GetProtocol() == ApplicationInfo::cTransportProtocol::Local) {
               MhpControl::Start(appitem->GetApplication());
            } else {
               if (GetReceptionState(appitem->GetApplication()->GetChannel())<=StateNeedsTuning) {
                  cDevice *dev=cDevice::GetDevice(appitem->GetApplication()->GetChannel(), 0);
                  if (!dev) {
                     //Interface->Error(tr("Cannot receive application!"));
                     //Interface->Flush();
                     Skins.Message(mtError, tr("Cannot receive application!"));
                     return osContinue;
                  }
                  dev->SwitchChannel(appitem->GetApplication()->GetChannel(), false);
                  MhpControl::Start(appitem->GetApplication());
                  return osEnd;
               }
            }
         }
        }
      case kMenu:   return osEnd;
      case kRed:    
      case kGreen:  
      case kYellow: 
      case kBlue:   return osContinue;
      default:      break;
      }
    }
  return state;
}

ReceptionState MhpApplicationMenu::GetReceptionState(cChannel *channel) {
   if (!channel)
      return StateCannotBeReceived;
   
   bool needsDetachReceivers;
   cDevice *dev;
   ReceptionState result=StateCannotBeReceived;
   for (int i=0; i<cDevice::NumDevices(); i++) {
      dev=cDevice::GetDevice(i);
      if (dev->ProvidesChannel(channel, 0, &needsDetachReceivers)) {
         if (dev->IsPrimaryDevice()) {
            cChannel *chan=Channels.GetByNumber(cDevice::CurrentChannel());
            if (channel->Source() != chan->Source() || channel->Tid() != chan->Tid())
               result = (ReceptionState)(result <? StateNeedsTuning);
            else
               result = StateCanBeReceived;
         } else {
            if (needsDetachReceivers)
               result = (ReceptionState)(result <? StateCanTemporarilyNotBeReceived);
            else
               result = StateCanBeReceived;
         }
      }
   }
   return result;
}


