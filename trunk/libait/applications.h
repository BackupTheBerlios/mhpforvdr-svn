/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef LIBAIT_APPLICATIONS_H
#define LIBAIT_APPLICATIONS_H
 
#include <string>
#include <list>

#include <vdr/tools.h>
#include <vdr/channels.h>
#include <vdr/thread.h>
#include <libdsmccreceiver/cache.h>
#include <libservice/transportstream.h>
#include <libdsmcc/util.h>

#include <mhp/implementation.h>

namespace ApplicationInfo {

class cAIT;
class cApplicationsDatabase;
class cTransportProtocols;


//A stream broadcast in a network (for DVB-S, on a given source, frequency and polarisation)
class cTransportStream : public cListObject, public Service::TransportStream {
public:
   cTransportStream(int source, int nid, int tid);
   
   /*bool operator==(cTransportStream &other)
      { return id==other.id; }
   bool operator==(TransportStreamID &otherID)
      { return id==otherID; }
   bool equals(int s, int n ,int t)
      { return id.equals(s,n,t); }*/
      
   struct Component {
      Component(int pi=0, int comp=0) : pid(pi), componentTag(comp) {}
      int pid;
      int componentTag;
   };
   
   struct Carousel {
      Carousel(int pi=0, int carId=0) : pid(pi), carouselId(carId) {}
      int pid;
      int carouselId;
   };
      
   class ApplicationService : public Service::ServiceAndTransportStream<ApplicationInfo::cTransportStream> {
   public:
      ApplicationService(cTransportStream *t, int Sid) : Service::ServiceAndTransportStream<cTransportStream>(t, Sid), pmtVersion(-1) {}
      void Reset();
      
      //int GetSid() { return sid; }
      cTransportStream *GetTransportStream() { return ts; }
      
      void AddAitPid(int pid);
      void AddComponentTag(int pid, int componentTag);
      void AddCarouselId(int pid, int carouselId);
      
      int GetPidForComponentTag(int componentTag);
      int GetCarouselIdForPid(int pid);   
      int pmtVersion;
      
      cChannel *GetChannel() 
         { return Channels.GetByChannelID(tChannelID(ts->GetSource(), ts->GetNid(), ts->GetTid(), sid)); }
         
      std::list<Component> *GetComponents() { return &components; }
      std::list<Carousel> *GetCarousels() { return &carousels; }
   private:
      friend class cTransportStream;
      /*cTransportStream *ts;
      int sid;*/
      std::list<int> aitables;
      std::list<Component> components;
      std::list<Carousel> carousels;
   };
   
   /*
   int GetSource() { return id.source; }
   int GetNid() { return id.nid; }
   int GetTid() { return id.tid; }
   TransportStreamID GetID() { return id; }*/
   
   //Returns either service found in service list or a newly created ApplicationService object
   ApplicationService *GetService(int sid);
   cChannel *GetChannel(ApplicationService *s)
      { return s->GetChannel(); }
   //Returns service found in service list or NULL if not found
   ApplicationService *findService(int sid);
   
   int GetPidForComponentTag(int serviceId, int componentTag);   
   int GetCarouselIdForPid(int pid);   
   ApplicationService *GetServiceForAitPid(int aitPid);
   cChannel *GetChannelForAitPid(int aitPid);
   
   void Lock() { mutex.Lock(); }
   void Unlock() { mutex.Unlock(); }
   
   friend class TransportStreamMutexLock;
   class cTransportStreamMutexLock : public cMutexLock {
   public:
      cTransportStreamMutexLock(cTransportStream *ts) : cMutexLock(&ts->mutex) {}
   };
   
protected:
   std::list<ApplicationService *> services;
   //TransportStreamID id;
   cMutex mutex;
};

//typedef cTransportStream::TransportStreamID TransportStreamID;


//The way how an MHP application is broadcast/transported.
//In real life, it is a PID in an TransportStream carrying the DSI of the OC.
class cTransportProtocol {
public:
   enum Protocol { Unknown = 0,
                   ObjectCarousel = 0x01,
                   IPviaDVB = 0x02,
                   HTTPoverInteractionChannel = 0x03,
                   Local //application is not broadcast but locally available
                 };
   
   cTransportProtocol(int lab) : label(lab) {}
   virtual ~cTransportProtocol() {}
   
   int GetLabel() { return label; } 
   
   virtual Protocol GetProtocol() = 0;
   //returns whether the data is accessible in the filesystem
   virtual bool HasFileSystemRepresentation() = 0;
   //If the above is true, returns the readable path.
   virtual const char *GetFileSystemRepresentation() = 0;
   static bool isSupported(Protocol p);
protected:
   int label;
};

class cUnsupportedTransportProtocol : public cTransportProtocol {
public:
   cUnsupportedTransportProtocol() : cTransportProtocol(0) {}
   virtual Protocol GetProtocol() { return Unknown; }
   virtual bool HasFileSystemRepresentation() { return false; }
   virtual const char *GetFileSystemRepresentation() { return 0; }
};

class cTransportProtocolWithPath : public cTransportProtocol {
public:
   cTransportProtocolWithPath(int label)
     : cTransportProtocol(label), path(0) {}
   virtual ~cTransportProtocolWithPath();
   
   virtual bool HasFileSystemRepresentation() { return true; }
   virtual const char *GetFileSystemRepresentation();
protected:
   void SetPath(const char *path);
   char *path;   
};

class cTransportProtocolViaOC : public cTransportProtocolWithPath {
public:
   cTransportProtocolViaOC(int label)
     : cTransportProtocolWithPath(label), service(0), componentTag(0),
       carouselId(0), pid(0), remote(false), cache(0) {}
     
   virtual Protocol GetProtocol() { return ObjectCarousel; }
   
   cTransportStream::ApplicationService *GetService() { return service; }
   int GetComponentTag() { return componentTag; }
   int GetCarouselId() { return carouselId; }
   int GetPid() { return pid; }
   bool isRemote() { return remote; }
   SmartPtr<Cache::Cache> GetCache() { return cache; }
   
   void SetCache(SmartPtr<Cache::Cache> c);
protected:
   friend class cAIT;
   void SetService(cTransportStream::ApplicationService *s) { service=s; }
   void SetComponentTag(int ct) { componentTag=ct; }
   void SetPid(int p) { pid=p; }
   void SetCarouselId(int id) { carouselId=id; }
   void SetRemote(bool r) { remote=r; }
   
   cTransportStream::ApplicationService *service;
   int componentTag;
   int carouselId;
   int pid;
   bool remote;
   SmartPtr<Cache::Cache> cache;
};

class cTransportProtocolLocal : public cTransportProtocolWithPath {
public:
   cTransportProtocolLocal()
     : cTransportProtocolWithPath(0) {}
   
   void SetPath(const char *path) { cTransportProtocolWithPath::SetPath(path); }
   
   virtual Protocol GetProtocol() { return Local; }
};


//An MHP Application
class cApplication  : public SmartPtrObject {
friend class cAIT;
friend class cApplicationsDatabase;
public:
   typedef SmartPtr<cApplication> Ptr;
   operator Ptr() { return Ptr(this); }

   enum ApplicationType { DVBJApplication = 0x01, DVBHTMLApplication = 0x02,
                          LocalDVBJApplication = 0xffff1, LocalDVBHTMLApplication = 0xffff2 }; 
                          //local codes are not standard-defined
   enum ControlCode { Autostart = 0x01, Present = 0x02, Destroy = 0x03, Kill = 0x04, 
                      DVBHTMLPrefetch = 0x05, DVBJRemote = 0x06 };
   enum Visibility { NotVisible = 0, OnlyApi = 1, UsersAndApi = 3 };
   enum Profile { EnhancedBroadcast = 1, InteractiveBroadcast = 2, InternetAccess = 3 };
   
   class ProfileVersion  : public cListObject {
   public:
      Profile profile;
      char    versionMajor;
      char    versionMinor;
      char    versionMicro;
      static bool isSupported(ProfileVersion version);
   };
   
   class ApplicationName : public cListObject {
   friend class cApplication;
   public:
      ApplicationName();
      ~ApplicationName();
      char   iso639Code[4];
      std::string name;
   protected:
      void Set(char *iso, char *name);
   };
   
   class ApplicationParameter : public cListObject {
   public:
      ApplicationParameter();
      ~ApplicationParameter();
      void Set(char *para);
      std::string parameter;
   };
   
   cApplication();
   ~cApplication();
   
   int GetAid();
   int GetOid();
   ControlCode GetControlCode();
   ApplicationType GetApplicationType();
   bool GetServiceBound();
   Visibility GetVisibility();
   int GetPriority();
   ApplicationName *GetName(int index);
   int GetNumberOfNames();
   const char * GetIconPath();
   int GetIconFlags();
   const char *GetParameter(int index);
   int GetNumberOfParameters();
   const char * GetBaseDir();
   const char * GetClassPath();
   const char * GetInitialClass();
   cTransportProtocol *GetTransportProtocol();
   ProfileVersion *GetProfileVersion(int index);
   int GetNumOfProfileVersions();
   cTransportStream::ApplicationService *GetService();
   cChannel *GetChannel();
   
protected:
   void SetAid(int aid);
   void SetOid(int oid);
   void SetControlCode(cApplication::ControlCode cc);
   void SetApplicationType(cApplication::ApplicationType ty);
   void SetServiceBound(bool serv);
   void SetVisibility(Visibility visi);
   void SetPriority(int prio);
   void AddName(char *iso, char *name);
   void SetIconPath(char *ip);
   void SetIconFlags(int flags);
   void AddParameter(char *param);
   void SetBaseDir(char *);
   void SetClassPath(char *);
   void SetInitialClass(char *);
   void SetTransportProtocol(cTransportProtocol *tp);
   void AddProfileVersion(cApplication::ProfileVersion &pv);
   void SetService(cTransportStream::ApplicationService *s);

private:
   int aid;
   int oid;
   ControlCode controlCode;
   ApplicationType type;
   bool serviceBound;
   Visibility visibility;
   int priority;
   int iconFlags;
   
   std::string iconPath;
   std::string baseDir;
   std::string classPath;
   std::string initialClass;
   
   cList<ApplicationParameter>  parameters;
   cList<ApplicationName>       names;
   cList<ProfileVersion>        profileVersions;
   
   cTransportProtocol *transportProtocol;
   cTransportStream::ApplicationService *service;
   
   //internal
   bool tagged;
};

class cTransportStreams : public cList<cTransportStream> {
public:
   //Returns a cTransportStream found in list or NULL if not found
   cTransportStream *findTransportStream(int source, int nid, int tid);
   //Returns a cTransportStream found in list or a newly created object
   cTransportStream *GetTransportStream(int source, int nid, int tid);
};


class cApplicationsDatabase {
friend class cAIT;
public:
   //Returns application for given identifiers, or NULL if not found.
   cApplication::Ptr findApplication(int aid, int oid, int type);

   typedef std::list<cApplication::Ptr > ApplicationList;
   //Fills list with all applications. Returns false if list is empty.
   bool findApplications(ApplicationList &addAppsToThisList);
   //Fills list with applications found on given tranport stream.
   //Returns true if information about that TS in known, or false if
   //no information (AIT) has yet been received on that TS.
   //If information is known, all applications signalled on the TS will be added to the list.
   bool findApplicationsForTransportStream(ApplicationList &addAppsToThisList, int source, int nid, int tid);
   //Fills list with applications found on given service.
   //Returns true if information about that service, or false if
   //no information (AIT) has yet been received for that service on its transport stream.
   //If information is known, all applications signalled on the TS will be added to the list.
   bool findApplicationsForService(ApplicationList &addAppsToThisList, int source, int nid, int tid, int sid);
   int Count() { return apps.size(); }
protected:
   void addApplication(cApplication *newApp);
   void tagForDeletion(cTransportStream::ApplicationService *service, int type);
   void deleteTagged();
   typedef ApplicationList AppList;
   AppList apps;
   cTransportStreams TransportStreams;
};

class cApplicationStatus : public cListObject {
public:
   static void MsgNewApplication(cApplication::Ptr app);
   static void MsgApplicationRemoved(cApplication::Ptr app);
protected:
   cApplicationStatus();
   ~cApplicationStatus();
   virtual void NewApplication(cApplication::Ptr app) = 0;
   virtual void ApplicationRemoved(cApplication::Ptr app) = 0;
private:
   static cList<cApplicationStatus> list;
};

} //end of namespace ApplicationInformation


#endif

