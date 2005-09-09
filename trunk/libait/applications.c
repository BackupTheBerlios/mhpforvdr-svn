/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software {}  you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation {}  either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
 
#include <string.h>
#include "applications.h"
   
namespace ApplicationInfo {


/* --------- cTransportStream ------------- */

cTransportStream::cTransportStream(int s, int n, int t)
 : Service::TransportStream(s,n,t)
{
}

cTransportStream::ApplicationService *cTransportStream::GetService(int sid) {
   cMutexLock lock(&mutex);
   ApplicationService *s=findService(sid);
   if (!s) {
      s=new ApplicationService(this, sid);
      services.push_back(s);
   }
   return s;
}

cTransportStream::ApplicationService *cTransportStream::findService(int sid) {
   for (std::list<ApplicationService *>::iterator it=services.begin(); it != services.end(); ++it) {
      if ( (*it)->sid==sid )
         return (*it);
   }
   return 0;
}

void cTransportStream::ApplicationService::AddAitPid(int pid) {
   cTransportStreamMutexLock lock(ts);
   aitables.push_back(pid);
}

void cTransportStream::ApplicationService::AddComponentTag(int pid, int componentTag) {
   cTransportStreamMutexLock lock(ts);
   Component comp(pid, componentTag);
   components.push_back(comp);
}

void cTransportStream::ApplicationService::AddCarouselId(int pid, int carouselId) {
   cTransportStreamMutexLock lock(ts);
   Carousel car(pid, carouselId);
   carousels.push_back(car);
}

int cTransportStream::ApplicationService::GetPidForComponentTag(int componentTag) {
   cTransportStreamMutexLock lock(ts);
   for (std::list<Component>::iterator it=components.begin(); it != components.end(); ++it) {
      if ( (*it).componentTag == componentTag )
         return (*it).pid;
   }
   return 0;
}

int cTransportStream::ApplicationService::GetCarouselIdForPid(int pid) {
   cTransportStreamMutexLock lock(ts);
   for (std::list<Carousel>::iterator it=carousels.begin(); it != carousels.end(); ++it) {
      if ( (*it).pid == pid )
         return (*it).carouselId;
   }
   return 0;
}

void cTransportStream::ApplicationService::Reset() {
   cTransportStreamMutexLock lock(ts);
   aitables.clear();
   components.clear();
   carousels.clear(); 
}

int cTransportStream::GetPidForComponentTag(int serviceId, int componentTag) {
   cMutexLock lock(&mutex);
   ApplicationService *s=findService(serviceId);
   return s ? s->GetPidForComponentTag(componentTag) : 0;
}
   
int cTransportStream::GetCarouselIdForPid(int pid) {
   cMutexLock lock(&mutex);
   for (std::list<ApplicationService *>::iterator it=services.begin(); it != services.end(); ++it) {   
      ApplicationService *s=(*it);
      for (std::list<Carousel>::iterator it=s->carousels.begin(); it != s->carousels.end(); ++it) {
         if ( (*it).pid == pid )
            return (*it).carouselId;
      }
   }
   return 0;
}

cChannel *cTransportStream::GetChannelForAitPid(int aitPid) {
   ApplicationService *s=GetServiceForAitPid(aitPid);
   if (!s)
      return 0;
   cChannel *c=s->GetChannel(); //may be 0 if VDR does not know channel
   if (!c)
      esyslog("Did not find channel for application PID %d", aitPid); //programming mistake
   return c;
}

cTransportStream::ApplicationService *cTransportStream::GetServiceForAitPid(int aitPid) {
   cMutexLock lock(&mutex);
   for (std::list<ApplicationService *>::iterator it=services.begin(); it != services.end(); ++it) {
      ApplicationService *s=(*it);
      for (std::list<int>::iterator it=s->aitables.begin(); it != s->aitables.end(); ++it) {
         if ( (*it) == aitPid ) {
            return s;
         }
      }
   }
   return 0;
}

/* --------- cTransportProtocol ------------- */

bool cTransportProtocol::isSupported(Protocol p) {
   switch(p) {
   case Unknown:
      return false;
   case ObjectCarousel:
      return MHP_TRANSPORT_VIA_OC;
   case IPviaDVB:
      return MHP_TRANSPORT_IP_VIA_DVB;
   case HTTPoverInteractionChannel:
      return MHP_TRANSPORT_HTTP_OVER_INTERACTIONCHANNEL;
   case Local:
      return true;
   }
   return false;
}

void cTransportProtocolViaOC::SetCache(SmartPtr<Cache::Cache> c) {
   cache=c;
   if (cache.getPointer()) {
      char buf[PATH_MAX];
      sprintf(buf, "%s/%s", cache->Root(), cache->getName());
      SetPath(buf);
   } else
      SetPath("");
}

cTransportProtocolWithPath::~cTransportProtocolWithPath() {
   free(path);
}
   
void cTransportProtocolWithPath::SetPath(const char *pa) {
   path=strdup(pa);
}
 
const char *cTransportProtocolWithPath::GetFileSystemRepresentation() {
   return path; 
}



/* --------- cApplication ------------- */

cApplication::cApplication() {
   aid=-1;
   oid=-1;
   controlCode=Present;
   type=DVBJApplication;
   serviceBound=true;
   visibility=UsersAndApi;
   priority=0;
   iconFlags=0;
   //iconPath=0;
   //baseDir=0;
   //classPath=0;
   //initialClass=0;
   transportProtocol=0;
   service=0;
   tagged=false;
}

cApplication::~cApplication() {
} 

int cApplication::GetAid() {
   return aid;
} 
int cApplication::GetOid() {
   return oid;
} 

cApplication::ControlCode cApplication::GetControlCode() {
   return controlCode;
}
 
cApplication::ApplicationType cApplication::GetApplicationType() {
   return type;
}
 
bool cApplication::GetServiceBound() {
   return serviceBound;
} 

cApplication::Visibility cApplication::GetVisibility() {
   return visibility;
}
 
int cApplication::GetPriority() {
   return priority;
} 

cApplication::ApplicationName *cApplication::GetName(int index) {
   return names.Get(index);
} 

int cApplication::GetNumberOfNames() {
   return names.Count();
} 

const char * cApplication::GetIconPath() {
   return iconPath.c_str();
}
 
int cApplication::GetIconFlags() {
   return iconFlags;
} 

const char *cApplication::GetParameter(int index) {
   return parameters.Get(index)->parameter.c_str();
}
 
int cApplication::GetNumberOfParameters() {
   return parameters.Count();
}
 
const char * cApplication::GetBaseDir() {
   return baseDir.c_str();
}
 
const char * cApplication::GetClassPath() {
   return classPath.c_str();
}
 
const char * cApplication::GetInitialClass() {
   return initialClass.c_str();
} 

cTransportProtocol *cApplication::GetTransportProtocol() {
   return transportProtocol;
}
 
cApplication::ProfileVersion *cApplication::GetProfileVersion(int index) {
   return profileVersions.Get(index);
} 

#define microIsSupported(pmic,mic) ( pmic <= mic )
#define minorIsSupported(pmin,pmic,min,mic) ( ( pmin<min ) || ( pmin == min && microIsSupported(pmic,mic)) )
#define isSupp(pmaj,pmin,pmic,maj,min,mic) ( ( pmaj<maj ) || ( pmaj == maj && minorIsSupported(pmin,pmic,min,mic)) )
bool cApplication::ProfileVersion::isSupported(cApplication::ProfileVersion version) {
   return (  (version.profile <= MHP_IMPLEMENTATION_HIGHEST_PROFILE)
            && isSupp( version.versionMajor, version.versionMinor, version.versionMicro, 
                            MHP_IMPLEMENTATION_VERSION_MAJOR,
                            MHP_IMPLEMENTATION_VERSION_MINOR,
                            MHP_IMPLEMENTATION_VERSION_MICRO )       
          );
}


int cApplication::GetNumOfProfileVersions() {
   return profileVersions.Count();
} 

cChannel *cApplication::GetChannel() {
   return service ? service->GetChannel() : 0;
}

cTransportStream::ApplicationService *cApplication::GetService() {
   return service;
}

void cApplication::SetAid(int ai) {
   aid=ai;
}

void cApplication::SetOid(int od) {
   oid=od;
}

void cApplication::SetControlCode(cApplication::ControlCode cc) {
   controlCode=cc;
}

void cApplication::SetApplicationType(cApplication::ApplicationType ty) {
   type=ty;
}

void cApplication::SetServiceBound(bool serv) {
   serviceBound=serv;
}

void cApplication::SetVisibility(cApplication::Visibility visi) {
   visibility=visi;
}

void cApplication::SetPriority(int prio) {
   priority=prio;
}



//--

cApplication::ApplicationName::ApplicationName() {
   iso639Code[3]=0;
}

cApplication::ApplicationName::~ApplicationName() {
}

void cApplication::ApplicationName::Set(char *iso, char *na) {
   iso639Code[0]=iso[0];
   iso639Code[1]=iso[1];
   iso639Code[2]=iso[2];
   name=na;
}
//--



void cApplication::AddName(char *iso, char *name) {
   ApplicationName *a=new ApplicationName();
   a->Set(iso, name);
   names.Add(a);
}

void cApplication::SetIconPath(char *ip) {
   iconPath=ip;
}

void cApplication::SetIconFlags(int flags) {
   iconFlags=flags;
}



//--

cApplication::ApplicationParameter::ApplicationParameter() {
   //parameter=0;
}

cApplication::ApplicationParameter::~ApplicationParameter() {
}

void cApplication::ApplicationParameter::Set(char *para) {
   parameter=para;
}

//--

void cApplication::AddParameter(char *param) {
   ApplicationParameter *a=new ApplicationParameter();
   a->Set(param);
   parameters.Add(a);
}

void cApplication::SetBaseDir(char *bd) {
   baseDir=bd;
}

void cApplication::SetClassPath(char *cp) {
   classPath=cp;
}

void cApplication::SetInitialClass(char *ic) {
   initialClass=ic;
}

void cApplication::SetTransportProtocol(cTransportProtocol *tp) {
   transportProtocol=tp;
}

void cApplication::AddProfileVersion(cApplication::ProfileVersion &pv) {
   ProfileVersion *p=new ProfileVersion(pv); //use builtin copy constructor
   profileVersions.Add(p);
}

void cApplication::SetService(cTransportStream::ApplicationService *ser) {
   service=ser;
}


/* --------- cApplicationsDatabase ------------- */

cApplication::Ptr cApplicationsDatabase::findApplication(int aid, int oid, int type) {
   for (AppList::iterator it=apps.begin(); it != apps.end(); ++it) {
      //0xffff and 0xfffe are wildcards as described in spec, page 214
      if ( (*it)->GetOid() == oid 
           &&  ((*it)->GetAid() == aid || aid == 0xffff || (aid == 0xfffe && 0x4000<=(*it)->GetAid()<=0x7fff))
           && (*it)->GetApplicationType() == type )
         return (*it);
   }
   return 0;
}

bool cApplicationsDatabase::findApplications(ApplicationList &addAppsToThisList) {
   addAppsToThisList=apps;
   return apps.size();
}

bool cApplicationsDatabase::findApplicationsForTransportStream(ApplicationList &addAppsToThisList, int source, int nid, int tid) {
   cTransportStream *ts = TransportStreams.GetTransportStream(source, nid, tid);
   if (ts == 0)
      return false;
   for (AppList::iterator it=apps.begin(); it != apps.end(); ++it) {
      if ((*it)->GetService()->GetTransportStream() == ts)
         addAppsToThisList.push_back(*it);
   }
   return true;
}

bool cApplicationsDatabase::findApplicationsForService(ApplicationList &addAppsToThisList, int source, int nid, int tid, int sid) {
   cTransportStream *ts = TransportStreams.GetTransportStream(source, nid, tid);
   if (ts == 0)
      return false;
   cTransportStream::ApplicationService *service = ts->findService(sid);
   if (service == 0)
      return false;

   for (AppList::iterator it=apps.begin(); it != apps.end(); ++it) {
      if ((*it)->GetService() == service)
         addAppsToThisList.push_back(*it);
   }
   return true;
}

void cApplicationsDatabase::addApplication(cApplication *newApp) {
   cApplication::Ptr a=findApplication(newApp->GetAid(), newApp->GetOid(), newApp->GetApplicationType());
   if (a) {
      a->tagged=false;
      printf("Application already in list, deleting object\n");
      delete newApp; //already in list
   } else {
      a=newApp;
      apps.push_back(a);
      cApplicationStatus::MsgNewApplication(a);
   }
}

void cApplicationsDatabase::tagForDeletion(cTransportStream::ApplicationService *s, int type) {
   for (AppList::iterator it=apps.begin(); it != apps.end(); ++it) {
      if ((*it)->GetService() == s && (*it)->GetApplicationType() == type)
         (*it)->tagged=true;
   }
}

void cApplicationsDatabase::deleteTagged() {
   for (AppList::iterator it=apps.begin(); it != apps.end(); ++it) {
      if ((*it)->tagged) {
         it=apps.erase(it);
         cApplicationStatus::MsgApplicationRemoved(*it);
      }
   }
}


/* --------- cTransportStreams ------------- */

cTransportStream *cTransportStreams::findTransportStream(int source, int nid, int tid) {
   static cTransportStream *last=0;

   if (last && last->equals(source, nid, tid) )
      return last;
   for (cTransportStream *ts=First(); ts; ts=Next(ts)) {
      if ( ts->equals(source, nid, tid) )
         return ts;
   }
   return 0;
}

cTransportStream *cTransportStreams::GetTransportStream(int source, int nid, int tid) {
   cTransportStream *ts=findTransportStream(source, nid, tid);
   if (ts == 0) {
      ts=new cTransportStream(source, nid, tid);
      Add(ts);
   }
   return ts;
}


/* --------- cApplicationStatus ------------- */

cList<cApplicationStatus> cApplicationStatus::list;

cApplicationStatus::cApplicationStatus() {
   list.Add(this);
}

cApplicationStatus::~cApplicationStatus() {
   list.Del(this, false);
}

void cApplicationStatus::MsgNewApplication(cApplication::Ptr app) {
   for (cApplicationStatus *s=list.First(); s; s=list.Next(s)) {
      s->NewApplication(app);
   }
}

void cApplicationStatus::MsgApplicationRemoved(cApplication::Ptr app) {
   for (cApplicationStatus *s=list.First(); s; s=list.Next(s)) {
      s->ApplicationRemoved(app);
   }
}

} //end of namespace ApplicationInformation
