#ifndef DSMCC_CACHE_H
#define DSMCC_CACHE_H

#include <list>
#include <string>
#include <map>

#include <libdsmcc/biop.h>
#include <libdsmcc/util.h>

#include <vdr/thread.h>

namespace Cache {

class DiskWriter {
public:
   DiskWriter(class Cache *c);
   void WriteFile(const char *basepath, const char *fileName, unsigned char *data, uint length);
   void WriteDirectory(const char *basepath, const char *fileName);
   void WriteBasePath();
   void UnlinkFile(const char *basepath, const char *fileName);
   void UnlinkDirectory(const char *basepath, const char *fileName);
   void UnlinkBasePath();
protected:
   class Cache *cache;
   void ReportErrorInErrno(const char *filename, const char *msg, bool critical=false);
   bool CheckWriteDir(const char *path);
};

class CacheListener;
class CacheObject : public SmartPtrObject {
public:
   CacheObject(unsigned long carId, unsigned short modId, CharArray objectKey);
   virtual ~CacheObject() {}
   
   unsigned long carousel_id;
   unsigned short module_id;
   CharArray objkey;
   unsigned char version;
   
   void setVersion(unsigned char v) { version=v; }
   
   bool operator==(const CacheObject &other) {
      return carousel_id==other.carousel_id && module_id==other.module_id
             && objkey==other.objkey;
      }
   const char *toString();
   class Ptr : public SmartPtr<CacheObject> {
   public:
      Ptr(CacheObject *p) : SmartPtr<CacheObject>(p) {}
      //compare by value, not by pointer
      bool operator==(const Ptr &other) { return (*getPointer())==(*other); } 
      bool operator==(const CacheObject &reference) { return (*getPointer())==reference; }
      class File *asFile() { return (class File *)getPointer(); }
      class Directory *asDirectory() { return (class Directory *)getPointer(); }
      class TemporaryDirectoryEntry *asTemporaryDirectoryEntry() { return (class TemporaryDirectoryEntry *)getPointer(); }
   };
   
   enum CacheObjectType { TypeNone, TypeFile, TypeDirectory, TypeServiceGateway };
   virtual CacheObjectType getType() { return TypeNone; }
   virtual void WriteToDisk(std::string &basePath, DiskWriter *writer) {}
   virtual void RemoveFromDisk(std::string &basePath, DiskWriter *writer) {}
};

class RealCacheObject : public CacheObject {
public:
   RealCacheObject(unsigned long carId, unsigned short modId, CharArray objectKey, CacheListener *listener);
protected:
   CacheListener *listener;
   virtual void ReportChange();
};

class File : public RealCacheObject {
public:
   //typedef Ptr SmartPtr<File>;
   File(unsigned long carId, unsigned short modId, CharArray objectKey, CacheListener *listener);
   CharArray data;
   Ptr parent;
   std::string filename;
   virtual CacheObjectType getType() { return TypeFile; }
   void AddInfo(Biop::File &fil);
   virtual void WriteToDisk(std::string &basePath, DiskWriter *writer);
   virtual void RemoveFromDisk(std::string &basePath, DiskWriter *writer);
   virtual bool GetPath(std::string &path, bool includeBaseName = true);
private:
   bool written;
   bool rewrite;
};

class Directory : public RealCacheObject {
public:
   //typedef Ptr SmartPtr<Directory>;
   Directory(unsigned long carId, unsigned short modId, CharArray objectKey, CacheListener *listener);
   std::list<Ptr> children;
   //std::string dirpath;
   Ptr parent;
   std::string filename;
   virtual CacheObjectType getType() { return TypeDirectory; }
   
   Ptr FindObject(CacheObject compare);
   void AddInfo(Biop::Directory &dir);
   void UpdateInfo(Biop::Directory &dir, DiskWriter *writer);
   void AssignChildren(std::list<CacheObject::Ptr> *unassigned);
   virtual void WriteToDisk(std::string &basePath, DiskWriter *writer);
   virtual void RemoveFromDisk(std::string &basePath, DiskWriter *writer);
   virtual void WriteFileToDisk(File::Ptr f, DiskWriter *writer);
   virtual bool GetPath(std::string &path);
   //virtual Ptr FindObject(std::string &path);
   
   std::list<CacheObject::Ptr> toBeAssigned;
protected:
   bool TryToAssignFromList(CacheObject::Ptr object, std::list<CacheObject::Ptr> *unassigned);
private:
   bool written;
};

//the root directory
class ServiceGateway : public Directory {
public:
   ServiceGateway(unsigned long carId, unsigned short modId, CharArray objectKey, CacheListener *listener);
   virtual CacheObjectType getType() { return TypeServiceGateway; }
   virtual void WriteToDisk(std::string &basePath, DiskWriter *writer);
   virtual void RemoveFromDisk(std::string &basePath, DiskWriter *writer);
   virtual bool GetPath(std::string &path);
};

class TemporaryDirectoryEntry : public CacheObject {
public:
   TemporaryDirectoryEntry(unsigned long carId, unsigned short modId, CharArray objectKey, CharArray name);
   std::string filename;
};

class RootDirectory {
public:
   static RootDirectory *getDefaultRoot() { return defaultRoot; }
   static void SetDefaultRoot(RootDirectory *root) { defaultRoot=root; }
   
   virtual const char *Root() { return "/tmp/cache"; }
protected:
   static RootDirectory *defaultRoot;
};

class CacheListener {
public:
   virtual void objectChanged(CacheObject::Ptr ptr) = 0;
};

class Cache : public SmartPtrObject, public CacheListener {
public:
   Cache(const char *basepath, RootDirectory *r=RootDirectory::getDefaultRoot());
   virtual ~Cache();
   void CacheFile(unsigned long carId, unsigned short modId, unsigned char version, Biop::File &fil);
   void CacheDirectory(unsigned long carId, unsigned short modId, unsigned char version, Biop::Directory &dir);
   void CacheServiceGateway(unsigned long carId, unsigned short modId, unsigned char version, Biop::ServiceGateway &srg);
   void Flush();
   const char *Root() { return root->Root(); }
   const char *getName() { return name.c_str(); }
   void Hibernate();
   void WakeUp();
   void Clear();
   void addListener(const char *path, CacheListener *listener);
   void removeListener(const char *path, CacheListener *listener);
   void removeListener(CacheListener *listener);
   
   virtual void objectChanged(CacheObject::Ptr ptr);
protected:
   cMutex listenerMutex;
   Directory::Ptr gateway;
   std::string name;
   std::list<CacheObject::Ptr> unassigned;
   DiskWriter writer;
   RootDirectory *root;
   typedef std::multimap<std::string, CacheListener *> ListenerList;
   ListenerList listeners;
   bool hasListeners;
};


}




#endif
