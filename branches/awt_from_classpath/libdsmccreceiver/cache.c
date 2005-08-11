#include <stdlib.h>
#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <algorithm> 

#include <libdsmcc/descriptor.h>

#include "cache.h"
#include "receiver.h"

namespace Cache {


/*---------------------------- CacheObject ----------------------------*/

CacheObject::CacheObject(unsigned long carId, unsigned short modId, CharArray ar) 
: carousel_id(carId),
  module_id(modId),
  objkey(ar),
  version(0)
{
}

//for debugging only
const char *CacheObject::toString() {
   static char buf[50];
   char *p=buf+sprintf(buf, "%ld-%d-", carousel_id, module_id);
   for (int i=0; i<objkey.getLength(); i++) {
      p+=sprintf(p, "%d", objkey.getData()[i]);
   }
   p[0]=0;
   return buf;
}


RealCacheObject::RealCacheObject(unsigned long carId, unsigned short modId, CharArray ar, CacheListener *listener) 
: CacheObject(carId, modId, ar),
  listener(listener)
{
}

void RealCacheObject::ReportChange() {
   //the listener is the Cache, which will dispatch the message
   listener->objectChanged(Ptr(this));
   /*if (hasListeners) {
      Ptr p(this);
      for (std::list<CacheListener *>::iterator it=listeners.begin(); it != listeners.end(); ++it) {
         it->objectChanged(p);
      }
   }*/
}

/*---------------------------- File ----------------------------*/

File::File(unsigned long carId, unsigned short modId, CharArray objectKey, CacheListener *listener) 
: RealCacheObject(carId, modId, objectKey, listener),
  parent(0)
{
   written=false;
   rewrite=false;
}


void File::AddInfo(Biop::File &fil) {
   data=fil.data;
   rewrite=true;
   //filename is assigned when a directory takes the file   
}

void File::WriteToDisk(std::string &basePath, DiskWriter *writer) {
   if (!written || rewrite) {
      writer->WriteFile(basePath.c_str(), filename.c_str(), data.getData(), data.getLength());
      data=CharArray();
      written=true;
      rewrite=false;
      ReportChange();
   }
}

void File::RemoveFromDisk(std::string &basePath, DiskWriter *writer) {
   if (written) {
      writer->UnlinkFile(basePath.c_str(), filename.c_str());
      written=false;
   }
}

bool File::GetPath(std::string &path, bool includeBaseName) {
   //Only applicable if File is in the hierarchy
   if (includeBaseName)
      path=filename;
      
   if (parent)
      return parent.asDirectory()->GetPath(path);
   else
      return false;
}


/*---------------------------- Directory ----------------------------*/

Directory::Directory(unsigned long carId, unsigned short modId, CharArray objectKey, CacheListener *listener) 
: RealCacheObject(carId, modId, objectKey, listener),
  parent(0)
{
   written=false;
}

void Directory::AddInfo(Biop::Directory &dir) {
   for (std::list<Biop::Binding>::iterator it=dir.bindings.begin(); it != dir.bindings.end(); ++it) {
      TemporaryDirectoryEntry::Ptr direntry(new TemporaryDirectoryEntry( ((Biop::BiopProfileBody *)(*it).ior.body.getPointer())->obj_loc.carousel_id,
                                             ((Biop::BiopProfileBody *)(*it).ior.body.getPointer())->obj_loc.module_id,
                                             ((Biop::BiopProfileBody *)(*it).ior.body.getPointer())->obj_loc.objkey,
                                             (*it).name.comps.front().id ) );
      //std::list<CacheObject::Ptr> *izgosh=&toBeAssigned;
      toBeAssigned.push_back(direntry);
   }
}

void Directory::UpdateInfo(Biop::Directory &dir, DiskWriter *writer) {
   //TODO: Check for changed/removed/added directory entries
}

void Directory::AssignChildren(std::list<CacheObject::Ptr> *unassigned) {
   //iterate over own list of children that need to be found
   for (std::list<CacheObject::Ptr>::iterator it=toBeAssigned.begin(); it != toBeAssigned.end(); ) {
      //printf("In AssignChildren: %s\n", (*it)->toString());
      if (TryToAssignFromList(*it, unassigned)) { //iterates over unassigned
         it=toBeAssigned.erase(it);
      } else
         ++it;
   }
   //printf("%d toBeAssigned dir entries\n", toBeAssigned.size());
   //iterate child directories
   for (std::list<CacheObject::Ptr>::iterator it=children.begin(); it != children.end(); ++it) {
      if ((*it)->getType() == TypeDirectory) {
         //printf("In Directory %s:", (*it).asDirectory()->filename.c_str());
         (*it).asDirectory()->AssignChildren(unassigned);
      }
   }
}

//internal helper function
bool Directory::TryToAssignFromList(CacheObject::Ptr object, std::list<CacheObject::Ptr> *unassigned) {
   //object is a TemporaryDirectoryEntry, whereas unassigned contains real Files or Directories with data
   for (std::list<CacheObject::Ptr>::iterator it=unassigned->begin(); it != unassigned->end(); ) {
      if ( (*it)==object) {
         TemporaryDirectoryEntry *tempentry=dynamic_cast<TemporaryDirectoryEntry*>(object.getPointer());
         //printf("In TryToAssignFromList: %s\n", (*it)->toString());
         if ( (*it)->getType() == TypeDirectory) {
            //CacheObject::Ptr newdir(new Directory((*it)->carousel_id, (*it)->module_id, (*it)->objkey));
            //Directory *dir=dynamic_cast<Directory*>((*it).getPointer());
            
            (*it).asDirectory()->parent=this;
            (*it).asDirectory()->filename=tempentry->filename;
            
            //printf("Successfully assigned dir %s %s\n", (*it)->toString(), (*it).asDirectory()->filename.c_str());
            
            children.push_back(*it);
            it=unassigned->erase(it);
            return true;
            
         } else if ( (*it)->getType() == TypeFile) {
            //CacheObject::Ptr newfile(new File((*it)->carousel_id, (*it)->module_id, (*it)->objkey));
            //File *file=dynamic_cast<File *>((*it).getPointer());
            
            (*it).asFile()->parent=this;
            (*it).asFile()->filename=tempentry->filename;
            
            //printf("Successfully assigned file %s\n", (*it).asFile()->filename.c_str());
            
            children.push_back(*it);
            it=unassigned->erase(it);
            return true;
            
         }
      } else
         ++it;
   }
   return false;
}

void Directory::WriteToDisk(std::string &basePath, DiskWriter *writer) {
   if (!written) {
      writer->WriteDirectory(basePath.c_str(), filename.c_str());
      written=true;
      ReportChange();
   }
   std::string newBasePath=basePath+'/'+filename;
   for (std::list<Ptr>::iterator it=children.begin(); it != children.end(); ++it) {
      (*it)->WriteToDisk(newBasePath, writer);
   }
}

void Directory::RemoveFromDisk(std::string &basePath, DiskWriter *writer) {
   std::string newBasePath=basePath+'/'+filename;
   for (std::list<Ptr>::iterator it=children.begin(); it != children.end(); ++it) {
      (*it)->RemoveFromDisk(newBasePath, writer);
   }
   if (written) {
      writer->UnlinkDirectory(basePath.c_str(), filename.c_str());
      written=false;
   }
}

void Directory::WriteFileToDisk(File::Ptr f, DiskWriter *writer) {
   //write only the specified file to disk
   
   //first get path (without basename, only directory path)
   //the function is recursive, so this works even if this is not the
   //ServiceGateway, but that one is somewhere up the hierarchy
   if (f->getType() != TypeFile)
      return;
   std::string path;
   if (!f.asFile()->GetPath(path, false))
      return;      
   //then write to disk
   f->WriteToDisk(path, writer);
}

bool Directory::GetPath(std::string &path) {
   //Only applicable if Directory is in the hierarchy
   //goes recursively up in the hierarchy until the ServiceGateway is reached
   path=filename+'/'+path;
   if (parent)
      return parent.asDirectory()->GetPath(path);
   else
      return false;
}


//I should do that with a SmartPtr. Currently, resulting pointer should not be kept!
CacheObject::Ptr Directory::FindObject(CacheObject compare) {
   std::list<Ptr>::iterator it;
   
   //search own directory entries
   if ( (it=find(children.begin(), children.end(), compare)) != children.end())
      return (*it);
      
   //search subdirs recursively
   for (it=children.begin(); it!=children.end(); ++it) {
      if ((*it)->getType() == TypeDirectory) {
         Ptr result(0);
         if ( (result=(*it).asDirectory()->FindObject(compare)) )
            return result;
      }
   }
   return Ptr(0);
}

/*CacheObject::Ptr Directory::FindObject(std::string &path) {
   string::size_type pos = path.find('/');
   std::string name
   if (pos==string::npos)
      name=path;
   else
      name=path.substring(0, pos);
      
   std::list<Ptr>::iterator it;
   for (it=children.begin(); it!=children.end(); ++it) {
      if (it->filename==name) {
         if (it->getType()==TypeDirectory)
            return it->FindObject(path.substring(pos+1, path.size()-(pos+1)));
         else if (it->getType==TypeFile
      }
   }
   return 0;
}
*/


/*---------------------------- TemporaryDirectoryEntry ----------------------------*/

TemporaryDirectoryEntry::TemporaryDirectoryEntry(unsigned long carId, unsigned short modId, CharArray objectKey,  CharArray name)
: CacheObject(carId, modId, objectKey),
  filename((char *)name.getData(), name.getLength()-1)
{
   //printf("New TemporaryDirectoryEntry %s %s\n", filename.c_str(), toString());
}


/*---------------------------- ServiceGateway ----------------------------*/

ServiceGateway::ServiceGateway(unsigned long carId, unsigned short modId, CharArray objectKey, CacheListener *listener) 
 : Directory(carId, modId, objectKey, listener)
{
}

void ServiceGateway::WriteToDisk(std::string &basePath, DiskWriter *writer) {
   //printf("ServiceGateway::WriteToDisk\n");
   //differs from directory insofar as itself need not to be written
   for (std::list<Ptr>::iterator it=children.begin(); it != children.end(); ++it) {
      (*it)->WriteToDisk(basePath, writer);
   }
}

void ServiceGateway::RemoveFromDisk(std::string &basePath, DiskWriter *writer) { 
   for (std::list<Ptr>::iterator it=children.begin(); it != children.end(); ++it) {
      (*it)->RemoveFromDisk(basePath, writer);
   }
}

bool ServiceGateway::GetPath(std::string &path) {
   //calls to GetPath of a file or directory will only return true
   //if they reach the function, which means that the file is fully
   //in the hierarchy and the path returned makes sense
   return true;
}


/*---------------------------- RootDirectory ----------------------------*/

RootDirectory *RootDirectory::defaultRoot=new RootDirectory();


/*---------------------------- Cache ----------------------------*/

Cache::Cache(const char *Name, RootDirectory *r)
: gateway(0),
  name(Name),
  writer(this),
  root(r),
  hasListeners(false)
{
   writer.WriteBasePath();
}

Cache::~Cache() {
   Clear();
}

void Cache::CacheFile(unsigned long carId, unsigned short modId, unsigned char version, Biop::File &fil) {
   cMutexLock lock(&listenerMutex);
   //printf("Caching file %s\n", CacheObject(carId, modId, fil.header->objkey).toString());
   CacheObject::Ptr f(0);
   
   if (gateway.getPointer() != 0) {
      f = gateway.asDirectory()->FindObject(CacheObject(carId, modId, fil.header->objkey));
      if (f) {
         //already in tree, check version
         if (f->version != version) {
            f->setVersion(version);
            f.asFile()->AddInfo(fil);
            //only do that here when file is in the hierarchy, not below in the unassigned list
            gateway.asDirectory()->WriteFileToDisk(f, &writer);
         }
         return;
      }
   }
   
   if (!f) {
      std::list<CacheObject::Ptr>::iterator it = find(unassigned.begin(), unassigned.end(), CacheObject(carId, modId, fil.header->objkey));
      if (it != unassigned.end()) {
         //already in "unassigned" list, check version
         if (f->version != version) {
            f->setVersion(version);
            f.asFile()->AddInfo(fil);
         }
         return;
      }
   }
   
   if (!f) {
      //neither in tree nor in "unassigned" list yet cached, put into "unassigned" list
      f = new File(carId, modId, fil.header->objkey, (CacheListener *)this);
      f->setVersion(version);
      f.asFile()->AddInfo(fil);
      unassigned.push_back(f);
   } else {
      f->setVersion(version);
      f.asFile()->AddInfo(fil);
   }
   
}

void Cache::CacheDirectory(unsigned long carId, unsigned short modId, unsigned char version, Biop::Directory &dir) {
   cMutexLock lock(&listenerMutex);
   //printf("Caching dir %s\n", CacheObject(carId, modId, dir.header->objkey).toString());
   CacheObject::Ptr d(0);   
   //TODO: Handle changes of directory entries, see UpdateInfo
   
   if (gateway.getPointer() != 0) {
      if ( (d=gateway.asDirectory()->FindObject(CacheObject(carId, modId, dir.header->objkey))) ) {
         //printf("dir already in tree\n");
         if (d->getType() == CacheObject::TypeDirectory && d->version != version) {
            d->setVersion(version);          
            d.asDirectory()->UpdateInfo(dir, &writer);
         }
         return;
      }
   }
   
   CacheObject compare(carId, modId, dir.header->objkey);
   for (std::list<CacheObject::Ptr>::iterator it=unassigned.begin(); it !=unassigned.end(); ++it) {
      if ( (*it)==compare ) {
         //already in "unassigned" list
         d=(*it);
         if (d->getType() == CacheObject::TypeDirectory && d->version != version) {
            d->setVersion(version);          
            d.asDirectory()->UpdateInfo(dir, &writer);
         }
         return;
      }
   }
   
   //not yet cached, put into first list
   d = new Directory(carId, modId, dir.header->objkey, (CacheListener *)this);
   d->setVersion(version);
   d.asDirectory()->AddInfo(dir);
   d.asDirectory()->AssignChildren(&unassigned);
   unassigned.push_back(d);
   Flush();
}

void Cache::CacheServiceGateway(unsigned long carId, unsigned short modId, unsigned char version, Biop::ServiceGateway &srg) {
   cMutexLock lock(&listenerMutex);
   //printf("Caching gateway %ld, %d\n", carId, modId);
   if (!gateway) { //not yet cached
      Directory::Ptr newdir(new ServiceGateway(0,0,CharArray(), (CacheListener *)this));
      gateway=newdir;
      gateway->setVersion(version);
      gateway.asDirectory()->AddInfo(srg);
      gateway.asDirectory()->AssignChildren(&unassigned);
   } else if (gateway->version != version) {
      gateway->setVersion(version);
      gateway.asDirectory()->UpdateInfo(srg, &writer);
   }
}

void Cache::Hibernate() {
   //TODO?
}

void Cache::WakeUp() {
   //TODO?
}

void Cache::Flush() {
   cMutexLock lock(&listenerMutex);
   if (gateway.getPointer() != 0) {
      gateway.asDirectory()->AssignChildren(&unassigned);
      std::string s;
      gateway->WriteToDisk(s, &writer);
   }
}

void Cache::Clear() {
   cMutexLock lock(&listenerMutex);
   if (gateway.getPointer() != 0) {
      std::string s;
      gateway->RemoveFromDisk(s, &writer);
   }
   writer.UnlinkBasePath();
   gateway=0;
   unassigned.clear();
}

void Cache::addListener(const char *path, CacheListener *listener) {
   cMutexLock lock(&listenerMutex);
   listeners.insert(ListenerList::value_type(path, listener));
   hasListeners=true;
   printf("Cache::addListener: Added listener for %s\n", path);
}

void Cache::removeListener(const char *path, CacheListener *listener) {
   cMutexLock lock(&listenerMutex);
   for (ListenerList::iterator it = listeners.begin(); it != listeners.end(); ) {
      if (it->second==listener && it->first==path) {
         ListenerList::iterator toBeErased(it);
         ++it;
         listeners.erase(toBeErased);
      } else
         ++it;
   }
   hasListeners=listeners.size();
}

void Cache::removeListener(CacheListener *listener) {
   cMutexLock lock(&listenerMutex);
   for (ListenerList::iterator it = listeners.begin(); it != listeners.end(); ) {
      if (it->second==listener) {
         ListenerList::iterator toBeErased(it);
         ++it;
         listeners.erase(toBeErased);
      } else
         ++it;
   }
   hasListeners=listeners.size();
}

/*void Cache::addListener(CacheListener *listener) {
   cMutexLock lock(&listenerMutex);
   hasListeners=true;
   listeners.push_back(listener);
}

void Cache::removeListener(CacheListener *listener) {
   cMutexLock lock(&listenerMutex);
   listeners.remove(listener);
   hasListeners=listeners.size();
}*/

void Cache::objectChanged(CacheObject::Ptr ptr) {
//Called by CacheObject's ReportChange
   if (!hasListeners)
      return;
      
   std::string path;
   if (ptr->getType()==CacheObject::TypeFile)
      ptr.asFile()->GetPath(path);
   if (ptr->getType()==CacheObject::TypeDirectory)
      path=ptr.asDirectory()->GetPath(path);
   else
      return;
   printf("Cache::objectChanged %s\n", path.c_str());
   std::pair<ListenerList::iterator, ListenerList::iterator> range=listeners.equal_range(path);
   for (ListenerList::iterator it=range.first; it != range.second; ++it)
      it->second->objectChanged(ptr);
}



/*---------------------------- DiskWriter ----------------------------*/

DiskWriter::DiskWriter(class Cache *c) : cache(c) {
}

void DiskWriter::WriteFile(const char *basepath, const char *filename, unsigned char *data, uint length) {
   char buf[256];
   FILE *data_fd;
   sprintf(buf, "%s/%s/%s/%s", cache->Root(), cache->getName(), basepath, filename);
   struct stat info;
   if (stat(buf, &info)==-1) {
      switch (errno) {
      case ENOENT:
         break; //all right, does not yet exist
      default:  //other error, report
         ReportErrorInErrno(buf, "Failed to stat() file");
         return;
      }
   } //else file with given name exists, overwrite, all right
       
   //printf("Writing file %s\n", buf);
   if ( (data_fd=fopen(buf, "w")) ) {
      fwrite(data, 1, length, data_fd);
      fclose(data_fd);
   } else {
      ReportErrorInErrno(buf, "Failed to write file");
   }
}

void DiskWriter::WriteDirectory(const char *basepath, const char *filename) {
   char dirbuf[256];
   sprintf(dirbuf, "%s/%s/%s/%s", cache->Root(), cache->getName(), basepath, filename);
   if (CheckWriteDir(dirbuf)) {   
      //printf("Writing dir %s\n", dirbuf);
      if (mkdir(dirbuf, 0755) == -1) {
         ReportErrorInErrno(dirbuf, "Could not create data subdirectory", true);
      }
   }
}

void DiskWriter::WriteBasePath() {
   char dirbuf[256];
   sprintf(dirbuf, "%s", cache->Root());
   
   if (CheckWriteDir(dirbuf)) {
      if (mkdir(dirbuf, 0755) == -1) {
         ReportErrorInErrno(dirbuf, "Could not create main cache directory", true);
      }
   }
      
   sprintf(dirbuf, "%s/%s", cache->Root(), cache->getName());
   if (CheckWriteDir(dirbuf)) {
      if (mkdir(dirbuf, 0755) == -1) {
         ReportErrorInErrno(dirbuf, "Could not create cache subdirectory", true);
      }
   }
}

//internal helper
//checks for several normal and error conditions and decides whether the given directory should be created
//returns true if dir should be written
bool DiskWriter::CheckWriteDir(const char *dirbuf) {
   struct stat info;
   if (stat(dirbuf, &info)==-1) {
      switch (errno) {
      case ENOENT:
         break; //all right, does not yet exist
      default:  //other error, report
         ReportErrorInErrno(dirbuf, "Failed to stat() file");
      }
   } else {
      if (S_ISDIR(info.st_mode)) {
         //all right
         return false;
      } else if (S_ISREG(info.st_mode) || S_ISLNK(info.st_mode)) {
         //file is a regular file, not a dir. Try deleting it.
         if (unlink(dirbuf)==-1) {
            ReportErrorInErrno(dirbuf, "Trying to write a directory, encountered a file with the same name, which could not be deleted", true);
            return false;
         }
      } else { //something different??
         ReportErrorInErrno(dirbuf, "Trying to write a directory, encountered something different", true);
         return false;
      }
   }
   return true;
}

void DiskWriter::UnlinkFile(const char *basepath, const char *filename) {
   char buf[256];
   sprintf(buf, "%s/%s/%s/%s", cache->Root(), cache->getName(), basepath, filename);
   if (unlink(buf) == -1) {
      ReportErrorInErrno(buf, "Could not remove file");
   }
}

void DiskWriter::UnlinkDirectory(const char *basepath, const char *filename) {
   char buf[256];
   sprintf(buf, "%s/%s/%s/%s", cache->Root(), cache->getName(), basepath, filename);
   if (rmdir(buf) == -1) {
      ReportErrorInErrno(buf, "Could not remove directory");
   }
}

void DiskWriter::UnlinkBasePath() {
   //hehe... of course it is bad to delete /tmp/cache. 
   //Only removing the subdirectory
   
   char buf[256];
   sprintf(buf, "%s/%s", cache->Root(), cache->getName());
   //If operation is cancelled before base directory was written, may return ENOENT
   if (rmdir(buf) == -1 && errno != ENOENT) {
      ReportErrorInErrno(buf, "Could not remove base directory. Please make sure the directory is empty and writable.", true);
   }
}


void DiskWriter::ReportErrorInErrno(const char *filename, const char *msg, bool critical) {
   if (critical)
      esyslog("Dsmcc: %s: %s, %s\n", msg, filename, strerror(errno));
   fprintf(stderr, "Dsmcc: %s: %s, %s\n", msg, filename, strerror(errno));
}

} //end of namespace Cache


