/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef __APPLICATIONMENU_H
#define __APPLICATIONMENU_H

#include <list>

#include <vdr/osd.h>
#include <vdr/tools.h>
#include <libait/ait.h>
#include <libservice/service.h>

enum ReceptionState { StateCanBeReceived, StateNeedsTuning, StateCanTemporarilyNotBeReceived, StateCannotBeReceived };

class cLocalApplication : public ApplicationInfo::cApplication {
public:
   cLocalApplication(char *name, char *basePath, char *initialClass, ApplicationInfo::cTransportProtocol *tp);
protected:
   static int nextId;
};

class MhpApplicationMenuItem : public cOsdItem {
public:
   MhpApplicationMenuItem(ApplicationInfo::cApplication::Ptr a, bool selectable = true);
   ApplicationInfo::cApplication::Ptr GetApplication() { return app; }
private:
   ApplicationInfo::cApplication::Ptr app;
};

class MhpApplicationMenuLabel : public cOsdItem {
public:
   MhpApplicationMenuLabel(const char *text);
};

class MhpApplicationMenu : public cOsdMenu {
public:
   MhpApplicationMenu(std::list<ApplicationInfo::cApplication::Ptr> *localApps);
protected:
   virtual eOSState ProcessKey(eKeys Key);
   
   ReceptionState GetReceptionState(Service::Service::Ptr channel);
};



#endif

