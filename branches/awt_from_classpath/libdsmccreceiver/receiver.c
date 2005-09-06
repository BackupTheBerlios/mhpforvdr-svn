#include <stdlib.h>
#include <zlib.h>
#include <unistd.h>
#include <vdr/receiver.h>
#include <vdr/channels.h>

#include <libdsmcc/descriptor.h>
#include "cache.h"

#include "receiver.h"


namespace Dsmcc {



/*---------------------------- ObjectCarousel ----------------------------*/

ObjectCarousel::ObjectCarousel(cDsmccReceiver *rec, const char *channelName, int _id /*=0*/) 
 : filecache(new Cache::Cache(channelName)),
   id(_id),
   receiver(rec)
{
   gate=0;
   //streams=0;
   stage=StageNone;
   hibernationState=StateNormal;
}

ObjectCarousel::~ObjectCarousel() {
   delete gate;
}

   //main entry point from receiver
void ObjectCarousel::ProcessSection(unsigned char *data, int length) {
   if (isHibernated())
      return;
   
   int offset=0;
   Dsmcc::Section section(length);
   //this starts the parsing process over the whole DSMCC object hierarchy
   // (_not_ the DDB data, this happens later of course)
   RawData rawdata(data, length);
   section.Parse(rawdata, offset);
   
   if ( !section.isValid() ) //tests for every possible reason for "not valid"
      return;
      
   if (hibernationState==StateNormal) { //Normal operation
      
      switch (section.body->getType()) {
      case Body::MessageDSI:
         stage = (ObjectCarousel::Stage) (stage >? StageDSI);
         InterpretDSI( section );
         break;
      case Body::MessageDII:
         if ( stage >= StageDSI ) { //get DSI first
            InterpretDII( section );
            stage = (ObjectCarousel::Stage) (stage >? StageDII);
         }
         break;
      case Body::MessageDDB:
         if ( stage >= StageDII ) {//get DIIs first
            InterpretDDB( section );
            stage = StageDDB;
         }
         break;
      }
   
   } else { //WakingUp
      
      //The wake-up process is inspired by TR 101 202, section 4.6.5
      //If and only if a DII changed, the DSI's transactionID changes.
      //If and only if a module version changed, the DII's transactionID changes.
      //If and only if data of a module changed, its modules version changes.
      //Furthermore, we will only be here if before the hibernation, all data
      //had already been acquired.
      switch (section.body->getType()) {
      case Body::MessageDSI:
         if (hibernationState==StateWakingUpNone) {
            if ( ((Dsmcc::DSI *)section.body)->header->transaction_id == gate->header->transaction_id) {
               //nothing changed
               hibernationState=StateNormal;
            } else {
               InterpretDSI( section );
               hibernationState=StateWakingUpDSI;
            }
         }
         break;
      case Body::MessageDII:
         //DIIs are not stored, so we do not use the transactionID.
         //With DIIs, we have direct access to the module versions.
         if ( hibernationState >= StateWakingUpDSI ) { //get DSI first
            InterpretDII( section );
            hibernationState=StateWakingUpDII;
            
            //Check if again have received all DII and woken up all ModuleDatas
            //If this is true, we can return to normal operation
            bool allWoken=true;
            std::list<ModuleData>::iterator dit;
            for (dit=cache.begin(); allWoken && dit != cache.end(); ++dit) {
               if ((*dit).isHibernated)
                  allWoken=false;
            }
            if (allWoken)
               hibernationState=StateNormal;
         }
         break;
      case Body::MessageDDB:
         if ( hibernationState >= StateWakingUpDII ) {//get DIIs first
            InterpretDDB( section );
         }
         break;
      }
   
   }
}

void ObjectCarousel::InterpretDSI(Dsmcc::Section &section) {
   if (gate)
      return; //already have DSI/gateway
      
   uint carouselId=((Biop::BiopProfileBody *)((Dsmcc::DSI *)section.body)->info.ior.body.getPointer())->obj_loc.carousel_id;
   Biop::ObjectLocation &loc=((Biop::BiopProfileBody *)((Dsmcc::DSI *)section.body)->info.ior.body.getPointer())->obj_loc;
   printf("Got DSI: %ld %d %ld %d %d %d\n", loc.component_tag, loc.component_data_len, loc.carousel_id, 
      loc.module_id, loc.version_major, loc.version_minor);
   if (!id) //if not yet given, set carousel id
      id=carouselId;
   //On RTL, carousel ID was 0 in the DSI, but 1 in the signalling.
   //I am not sure, but I think an ID of 0 is invalid.
   //So check here for carouselId before returning - if it is 0,
   //we assume the DSI belongs to our carousel anyway.
   else if (carouselId && carouselId != id) //else check if we have the correct DSI
      return;
   
   gate=(Dsmcc::DSI *)section.body;
   section.StealBody();
   
   InterpretTap(((Biop::BiopProfileBody *)gate->info.ior.body.getPointer())->dsm_conn.tap);
}

void ObjectCarousel::InterpretDII(Dsmcc::Section &section) {
   Dsmcc::DII *dii=(Dsmcc::DII*)section.body;
      
   if (dii->download_id != id)
      return;
      
   //a DII has a list of Dsmcc::ModuleInfo called modules.
   //"this" (ObjectCarousel) has a list of ObjectCarousel::ModuleData called cache
   std::list<ModuleInfo>::iterator iit;
   std::list<ModuleData>::iterator dit;
   //outer loop
   for (iit=dii->modules.begin(); iit != dii->modules.end(); ++iit) {
      //inner loop
      for (dit=cache.begin(); dit != cache.end(); ++dit) {
         if ( (*iit).module_id == (*dit).module_id ) {
            break; //found it
         }
      }
      //end of inner loop
      
      if ( dit != cache.end() ) {//found module in cache
         if ((*dit).version == (*iit).module_version) {
            //found module, already known, no change
            (*dit).isHibernated=false;
            return;
         } else {
            //found module, but new version, so update it below
            //isHibernated will be reset by ModuleData.Set
         }
      } else {
         cache.push_back(ModuleData(this));
         dit= -- cache.end();
      }
      
      (*dit).Set( (*iit), *dii);
      AddStreamForTap( (*dit).tag );
      
   } //end of outer loop
}

void ObjectCarousel::getProgress(bool *retComplete, uint *retTotalSize, uint *retCurrentSizeReceivedData, uint *retCurrentSizeCompleteBlocks, float *retPercentage) {
   if (stage < StageDII) {
      if (retTotalSize)
         (*retTotalSize)=0;
      if (retCurrentSizeCompleteBlocks)
         (*retCurrentSizeCompleteBlocks)=0;
      if (retCurrentSizeReceivedData)
         (*retCurrentSizeReceivedData)=0;
      if (retPercentage)
         (*retPercentage)=0.0;
      if (retComplete)
         (*retComplete)=false;
      return;
   }
   
   int totalSize=0, cachedSize=0, cachedSizeBlock=0;
   bool complete=true;
   for (std::list<ModuleData>::iterator iit=cache.begin(); iit != cache.end(); ++iit) {
      totalSize += (*iit).size;
      // With this code only data of blocks which are completely received is added
      //if ((*iit).cached && !(*iit).isHibernated)
        // cachedSize += (*iit).size;
      // also add the data received for blocks that are not yet completely received,
      // more fluent progress bar
      if (!(*iit).isHibernated) {
         cachedSize += (*iit).curp;
         if ((*iit).cached)
            cachedSizeBlock += (*iit).size;
      }
      complete = (complete && (*iit).cached && !(*iit).isHibernated);
   }
   
   if (retTotalSize)
      (*retTotalSize)=totalSize;
   if (retCurrentSizeCompleteBlocks)
      (*retCurrentSizeCompleteBlocks)=cachedSizeBlock;
   if (retCurrentSizeReceivedData)
      (*retCurrentSizeReceivedData)=cachedSize;
   if (retPercentage)
      (*retPercentage)=((float)cachedSize) / ((float)totalSize);
   if (retComplete)
      (*retComplete)=complete;
}

void ObjectCarousel::ModuleData::Set(ModuleInfo &info, DII &dii) {
   printf("ModuleData::Set: id %d, size %ld\n", info.module_id, info.module_size);
   module_id=info.module_id;
   version = info.module_version;
   size = info.module_size;
   
   carousel->InterpretTap(info.modinfo.tap);
      
   int num_blocks = size / dii.block_size;
   if ((size % dii.block_size) != 0)
      num_blocks++;
   if (bstatus)
      delete[] bstatus;
   blocks.clear();
   bstatus=new Bitset(num_blocks);
   tag=info.modinfo.tap.assoc_tag;
   cached=false;
   curp=0;
   isHibernated=false;
   descriptors=info.modinfo.descriptors;
}

void ObjectCarousel::ModuleData::AddData(DDB &ddb) {
   if (cached)
      return; //Already got complete module
   if (bstatus->isSet(ddb.block_number)) { //block not yet received
      blocks.push_back(ddb);
      curp+=ddb.blockdata.getLength();
      char bl[bstatus->getSize()+1];for (int i=0;i<bstatus->getSize();i++) bl[i]=(bstatus->isSet(i) ? '1':'0');bl[bstatus->getSize()]=0;
      printf("Received block number %d of module %d, size %d, curp now %ld, blocks %s\n", ddb.block_number, module_id, ddb.blockdata.getLength(), curp, bl);
      bstatus->Set(ddb.block_number);
   }
   if (bstatus->isComplete()) {
      //HERE begins the second stage, the parsing of BIOP messages
      HeapCharArray data(size);
      blocks.sort(); //DDB implement operator<, so this sorts according to block_number
      std::list<DDB>::iterator it;
      
      unsigned char *p=data;
      //RawData p(data, size);
      for (it=blocks.begin(); it != blocks.end(); ++it) {
         if ( (*it).blockdata.getData() ) {
            memcpy(p, (*it).blockdata.getData(), (*it).blockdata.getLength());
            p+=(*it).blockdata.getLength();
         }
      }
      printf("blocks.size() %d, data[0] %d, module %d.\n", blocks.size(), data[(uint)0], module_id);
      
      blocks.clear();
      
      bool isCompressed=false;
      DescriptorList::iterator dit;
      for(dit=descriptors.begin(); dit!=descriptors.end(); ++dit) {
         if((*dit)->tag == 0x09) { isCompressed=true; break; }
      }
      if (isCompressed) { //need to uncompress
         unsigned long data_len = dynamic_cast<CompressedDescriptor*>((*dit).getPointer())->original_size+1;
         //printf ("data_len %ld\n", data_len);
         HeapCharArray uncompressed_data(data_len);
         switch (uncompress(uncompressed_data, &data_len, data, size)) {
         case Z_DATA_ERROR:
            fprintf(stderr, "Data error uncompressing module %d.\n", module_id);
            return;
         case Z_BUF_ERROR:
            fprintf(stderr, "Buffer error uncompressing module %d.\n", module_id);
            return;
         case Z_MEM_ERROR:
            fprintf(stderr, "Memory error uncompressing module %d.\n", module_id);
            //delete uncompressed_data; delete data;
            return;
         }
         ProcessModule(uncompressed_data, data_len);
      } else {
         ProcessModule(data, size);
      }
      cached=true;
   }
}

//main entry point for processing the BIOP messages of a complete module
void ObjectCarousel::ModuleData::ProcessModule(unsigned char *da, int len) {
   int curp=0;
   RawData data(da, len);
   while (curp<len) {
      Biop::Object *o=Biop::Object::DecideAndParse(data, curp);
      if (!o || !o->header->valid) {
         printf("Invalid Biop::Object!\n");
         return; //if one module is not valid, the rest will most 
                  //probably not be valid too (wrong offset at least).
                   // So do not continue, but return.
      }
      switch (o->getType()) {
      case Biop::Object::MessageFile:
         //carousel->filecache.CacheFile( *(Biop::File *)o, carousel->id, module_id);
         carousel->filecache->CacheFile(carousel->id, module_id, version, *(Biop::File *)o);
         break;
      case Biop::Object::MessageDirectory:
        {
         carousel->filecache->CacheDirectory(carousel->id, module_id, version, *(Biop::Directory *)o);
         std::list<Biop::Binding>::iterator it;
         Biop::Directory *dir=(Biop::Directory *)o;
         for (it=dir->bindings.begin(); it != dir->bindings.end(); ++it) {
            Biop::BiopProfileBody *body=(Biop::BiopProfileBody *)(*it).ior.body.getPointer();
            carousel->InterpretTap(body->dsm_conn.tap);
            /*if ( (*it).name.comps.front().kind == "dir" ) {
               carousel->filecache.CacheDirInfo(module_id, dir->header->objkey.getLength(),
                                                 (char *)dir->header->objkey.getData(), &(*it));
            } else if ( (*it).name.comps.front().kind == "fil" ) {
               carousel->filecache.CacheFileInfo(module_id, dir->header->objkey.getLength(),
                                                 (char *)dir->header->objkey.getData(), &(*it));
            }*/
         }
         //carousel->filecache.num_dirs--;
        }
         break;
      case Biop::Object::MessageServiceGateway:
        {
         carousel->filecache->CacheServiceGateway(carousel->id, module_id, version, *(Biop::ServiceGateway *)o);
         std::list<Biop::Binding>::iterator it;
         Biop::ServiceGateway *dir=(Biop::ServiceGateway *)o;
         for (it=dir->bindings.begin(); it != dir->bindings.end(); ++it) {
            Biop::BiopProfileBody *body=(Biop::BiopProfileBody *)(*it).ior.body.getPointer();
            carousel->InterpretTap(body->dsm_conn.tap);
            /*if ( (*it).name.comps.front().kind == "dir" ) {
               carousel->filecache.CacheDirInfo(0, 0, NULL, &(*it));
            } else if ( (*it).name.comps.front().kind == "fil" ) {
               carousel->filecache.CacheFileInfo(0, 0, NULL, &(*it));
            }*/
         }
        }
         break;
      case Biop::Object::MessageStream:
         //unimplemented
         break;
      case Biop::Object::MessageStreamEvent:
         //unimplemented
         break;
      }
      delete o;
   }
   carousel->filecache->Flush();
}


ObjectCarousel::ModuleData::~ModuleData() {
   delete bstatus;
}

ObjectCarousel::ModuleData::ModuleData(ObjectCarousel *car) {
   carousel=car;
   cached=false;
   bstatus=0;
   size=0;
   curp=0;
   isHibernated=false;
}

/*void ObjectCarousel::ModuleData::Hibernate() {
}

void ObjectCarousel::ModuleData::WakeUp() {
}*/

void ObjectCarousel::InterpretDDB(Dsmcc::Section &section) {
   Dsmcc::DDB *ddb=(Dsmcc::DDB*)section.body;
   
   if (ddb->getDownload_id() != id)
      return;
      
   std::list<ModuleData>::iterator it;
   for (it=cache.begin(); it != cache.end(); ++it) {
      if ( ((*it).module_id == ddb->module_id) ) {
         if ((*it).version == ddb->module_version) {
            //correct module info found
            break;
         } else {
            //info not found (no DII yet), or outdated
            return;
         }
      }
   }
   if (it != cache.end()) //found module, add data
      (*it).AddData(*ddb);
}

void ObjectCarousel::InterpretTap(Biop::Tap &tap) {
   switch (tap.use) {
   case Biop::Tap::BIOP_DELIVERY_PARA_USE:
      //printf("Tap BIOP_DELIVERY_PARA_USE to assoc_tag %d\n", tap.assoc_tag);
      break;
   case Biop::Tap::BIOP_OBJECT_USE:
      //printf("Tap BIOP_OBJECT_USE to assoc_tag %d\n", tap.assoc_tag);
      break;
   default:
      dlog("New Tap Use value!\n");
      return;
   }
   AddStreamForTap(tap.assoc_tag);
}

void ObjectCarousel::AddStreamForTap(int assoc_tag) {
      //((Biop::BiopProfileBody *)gate->info.ior.body)->dsm_conn.tap.assoc_tag
   receiver->AddStreamForTap(assoc_tag, this);
}

void ObjectCarousel::Hibernate() {
   bool haveAll=(stage==StageDDB && hibernationState==StateNormal);   
   for (std::list<ModuleData>::iterator iit=cache.begin(); haveAll && iit != cache.end(); ++iit) {
      if (!(*iit).cached)
         haveAll=false;
   }
   
   if (!haveAll) {
      hibernationState=StateHibernatedCleared;
      //reset everything
      stage=StageNone;
      delete gate;
      gate=0;
      cache.clear();
      filecache->Clear();
   } else {
      hibernationState=StateHibernated;
      for (std::list<ModuleData>::iterator iit=cache.begin(); iit != cache.end(); ++iit)
         (*iit).isHibernated=true;
   }
}

void ObjectCarousel::WakeUp(cDsmccReceiver *rec) {
   if (!isHibernated())
      return;
      
   receiver=rec;
   
   
   if (hibernationState==StateHibernated)
      hibernationState=StateWakingUpNone;
   else //StateHibernatedCleared, everything was cleared, start from the beginning
      hibernationState=StateNormal;
}




} //end of namespace Dsmcc




/*---------------------------- DsmccStream ----------------------------*/

DsmccStream::DsmccStream() {
   pid = 0;
   carousel=0;
   status=NotReceiving;
   assoc_tag=0;
}

DsmccStream::DsmccStream(int pi, int as_t, Dsmcc::ObjectCarousel *car) {
   pid = pi;
   carousel=car;
   status=NotReceiving;
   assoc_tag=as_t;
}

DsmccStream::DsmccStream(const DsmccStream &source) {
   operator=(source);
}

DsmccStream& DsmccStream::operator=(const DsmccStream &source) {
   //the CharArray has copy-on-write functionality - no need to care about memcpy'ing/delete'ing
   pid = source.pid;
   carousel=source.carousel;
   status=source.status;
   assoc_tag=source.assoc_tag;
   return *this;
}


/*---------------------------- cDsmccReceiver ----------------------------*/

cDsmccReceiver::cDsmccReceiver(const char *channel, Service::TransportStreamID tsid) {
   running=false;
   filterOn=false;
   name = channel ? channel : "Unknown";
   ts=tsid;
}

cDsmccReceiver::~cDsmccReceiver() {
   //Stop thread
   running=false;
   todoVar.Broadcast();
   Cancel(2);

   for (std::list<Dsmcc::ObjectCarousel*>::iterator it=carousels.begin(); it != carousels.end(); ++it)
      delete (*it);
}

void cDsmccReceiver::SetStatus(bool On) {
   cFilter::SetStatus(On);

   if (On) {
      //Filter is activated. There might previously have been a channel switch:
      //If we are on the same transponder, restore all streams.
      //Otherwise, higher levels should take appropriate action (Hibernation, Deletion)
      #if VDRVERSNUM <= 10327
      #error "Unfortunately, VDR versions up to 1.3.27 contain a bug that prevents this code from working properly. Please use VDR version 1.3.28 or later."
      #endif
      const cChannel *newChan=cFilter::Channel();
      filterOn=ts.equals(newChan->Source(), newChan->Nid(), newChan->Tid());
      printf("cDsmccReceiver::SetStatus, filterOn is %d, ts is %d-%d-%d, new is %d-%d-%d\n", filterOn, ts.GetSource(), ts.GetNid(), ts.GetTid(), newChan->Source(), newChan->Nid(), newChan->Tid());
      if (filterOn) {
         streamListMutex.Lock();
         for (DsmccStreamList::iterator sit=streams.begin(); sit!=streams.end(); ++sit) {
            if (sit->status==DsmccStream::ActivatedNotReceiving)
               ActivateStream(&(*sit));
         }
         streamListMutex.Unlock();
      }
   } else {
      filterOn=false;
      //SetStatus will remove all filters when On=false.
      //Sync internal list to this situation
      printf("cDsmccReceiver::SetStatus, filterOn is false\n");
      streamListMutex.Lock();
      for (DsmccStreamList::iterator sit=streams.begin(); sit!=streams.end(); ++sit) {
         if (sit->status==DsmccStream::Receiving)
            SuspendStream(&(*sit));
      }
      streamListMutex.Unlock();
   }
}

Dsmcc::ObjectCarousel *cDsmccReceiver::AddCarousel(unsigned long id) {
   cMutexLock lock(&carouselMutex);
   for (std::list<Dsmcc::ObjectCarousel*>::iterator it=carousels.begin(); it != carousels.end(); ++it)
      if ((*it)->getId() == id)
         return (*it);
   char buf[256];
   sprintf(buf, "%s-%ld", name.c_str(), id);
   Dsmcc::ObjectCarousel *car=new Dsmcc::ObjectCarousel(this, buf, id);
   carousels.push_back(car);
   return car;
}

Dsmcc::ObjectCarousel *cDsmccReceiver::AddHibernatedCarousel(Dsmcc::ObjectCarousel *hibernatedCar) {
   //Note that AddStream and ActivateStream must be called subsequently by API user

   {
      cMutexLock lock(&carouselMutex);
      for (std::list<Dsmcc::ObjectCarousel*>::iterator it=carousels.begin(); it != carousels.end(); ++it)
         if ((*it) == hibernatedCar)
            return hibernatedCar;
            
      carousels.push_back(hibernatedCar);
   }
   
   processingMutex.Lock();
   hibernatedCar->WakeUp(this);
   processingMutex.Unlock();
   
   return hibernatedCar;
}

Dsmcc::ObjectCarousel *cDsmccReceiver::HibernateCarousel(unsigned long id) {
   Dsmcc::ObjectCarousel *car=0;
   std::list<Dsmcc::ObjectCarousel*>::iterator it;
   
   carouselMutex.Lock();
   for (it=carousels.begin(); it != carousels.end(); ++it) {
      if ((*it)->getId() == id) {
         car=(*it);
         break;
      }
   }
   carouselMutex.Unlock();
   
   if (car) {
      
      //Remove streams from list and deactivate PIDs from filter
      streamListMutex.Lock();      
      for (DsmccStreamList::iterator sit=streams.begin(); sit!=streams.end(); ) {
         if ((*sit).carousel == car) {
            RemoveStream(&(*sit));
            sit=streams.erase(sit);
         } else
            ++sit;
      }
      streamListMutex.Unlock();
      
      //remove from carousels list
      carouselMutex.Lock();
      carousels.erase(it);
      carouselMutex.Unlock();
      
      //remove all sections to be processed by the carousel from todo list:
      //The carousel object _may_ be deleted upon return from this method
      todoMutex.Lock();
      for (std::list<TodoSection *>::iterator tit=todoList.begin(); tit != todoList.end(); ) {
         if ((*tit)->carousel == car)
            tit=todoList.erase(tit);
         else
            ++tit;
      }
      todoMutex.Unlock();
      
      //We must make sure that car->ProcessSection is not called right now and is not executing.
      processingMutex.Lock();
      car->Hibernate();
      processingMutex.Unlock();
   }
   
   return car;
}

//AddStream only adds stream to list, does _not_ activate it
void cDsmccReceiver::AddStream(int pid, int assoc_tag, Dsmcc::ObjectCarousel *car) {
   cMutexLock lock(&streamListMutex);
   DsmccStreamList::iterator it;
   for (it=streams.begin(); it!=streams.end(); ++it) {
      if ( (*it).pid==pid ) {
         if (!(*it).carousel)
            (*it).carousel=car;
         if (!(*it).assoc_tag)
            (*it).assoc_tag=assoc_tag;
         return;         
      }
   }   
   //not found, insert new stream into list
   streams.push_back(DsmccStream(pid, assoc_tag, car));
}

void cDsmccReceiver::ActivateStream(int pid) {
   cMutexLock lock(&streamListMutex);
   DsmccStreamList::iterator it;
   for (it=streams.begin(); it!=streams.end(); ++it) {
      if ( (*it).pid==pid ) {
         if (!(*it).carousel) //do not allow a stream without a carousel to be received
            return;
         ActivateStream(&(*it));
         
         if(!running) { //start thread if necessary
            running = true;
            SetStatus(true);
            Start();
         }         
      }
   }
   
}

void cDsmccReceiver::AddStreamForTap(int assoc_tag, Dsmcc::ObjectCarousel *car) {
   cMutexLock lock(&streamListMutex);
   DsmccStreamList::iterator it;
   for (it=streams.begin(); it!=streams.end(); ++it) {
      if ( (*it).assoc_tag==assoc_tag) {
         if ((*it).carousel)
            return;
         AddStream((*it).pid, assoc_tag, car);
         ActivateStream((*it).pid);
         return;
      }
   }
}

/*void cDsmccReceiver::ActivateStreams() {
   cMutexLock lock(&streamListMutex);
   DsmccStreamList::iterator it;
   for (it=streams.begin(); it!=streams.end(); ++it) {
      if (!(*it).carousel) //do not allow a stream without a carousel to be received
         continue;
      ActivateStream(&(*it));

      if(!running) { //start thread if necessary
         running = true;
         Start();
      }
   }
   SetStatus(true);
}*/

//Moves stream to status Receiving, or, if filter is not attached, ActivatedNotReceiving
void cDsmccReceiver::ActivateStream(DsmccStream *str) {
   //called by above functions only, mutex is locked
   switch (str->status) {
   case DsmccStream::NotReceiving:
   case DsmccStream::ActivatedNotReceiving:
      if (!filterOn) {
         str->status=DsmccStream::ActivatedNotReceiving;
         return;
      }
      str->status=DsmccStream::Receiving;
      printf("Added filter for PID %d\n", str->pid);
      //TableIDs are 0x3B for DII, DSI, 0x3C for DDB
      Add(str->pid, 0x3B);
      Add(str->pid, 0x3C);
      break;
   case DsmccStream::Receiving:
      break;
   }
}

//Moves stream to status NotReceiving
void cDsmccReceiver::RemoveStream(DsmccStream *str) {
   //called by above functions only, mutex is locked
   switch (str->status) {
   case DsmccStream::NotReceiving:
      break;
   case DsmccStream::ActivatedNotReceiving:
      str->status=DsmccStream::NotReceiving;
      break;
   case DsmccStream::Receiving:
      str->status=DsmccStream::NotReceiving;
      printf("Removed filter for PID %d\n", str->pid);
      //TableIDs are 0x3B for DII, DSI, 0x3C for DDB
      Del(str->pid, 0x3B);
      Del(str->pid, 0x3C);
      break;
   }
}

//Moves stream to status ActivatedNotReceiving
void cDsmccReceiver::SuspendStream(DsmccStream *str) {
   //called by above functions only, mutex is locked
   switch (str->status) {
   case DsmccStream::NotReceiving:
      str->status=DsmccStream::ActivatedNotReceiving;
      break;
   case DsmccStream::ActivatedNotReceiving:
      break;
   case DsmccStream::Receiving:
      str->status=DsmccStream::ActivatedNotReceiving;
      printf("(Temporarily) Removed filter for PID %d\n", str->pid);
      //TableIDs are 0x3B for DII, DSI, 0x3C for DDB
      Del(str->pid, 0x3B);
      Del(str->pid, 0x3C);
      break;
   }
}


void cDsmccReceiver::Action() {
   TodoSection *todo;
   
   while (running) {
   
      {
         cMutexLock lock(&todoMutex);
         if (todoList.size()) {
            todo=todoList.front();
            todoList.pop_front();
         } else {
            todoVar.TimedWait(todoMutex, 2000);
            continue;
         }
      }
            
      if (todo) {
      
         processingMutex.Lock();
         //TODO: Make everything const correct
         todo->carousel->ProcessSection((u_char *)todo->data.getData(), todo->data.getLength());
         processingMutex.Unlock();
         
         delete todo;
         
      } else
         //usleep(1);
         pthread_yield();
   }
}

void cDsmccReceiver::Process(u_short Pid, u_char Tid, const u_char *Data, int Length) {
   Dsmcc::ObjectCarousel *car=0;
   
   streamListMutex.Lock();
   DsmccStreamList::iterator it;
   for (it=streams.begin(); it!=streams.end(); ++it) {
      if ( (*it).pid==Pid ) {
         car=(*it).carousel;
         break;
      }
   }
   streamListMutex.Unlock();
   
   if (!car) {
      printf("No carousel for stream.");
      return;
   }

   static bool notifiedOverflow=false;
   
   TodoSection *todo=new TodoSection;
   todo->carousel=car;
   todo->data.assign((u_char *)Data, Length);
   bool overflow;
   
   todoMutex.Lock();
   if ( todoList.size()<=50 ) { //limit "ring buffer" size
      overflow=false;
      todoList.push_back(todo);
      todoVar.Broadcast();
      notifiedOverflow=false;
   } else
      overflow=true;
   todoMutex.Unlock();
   
   if (overflow) {
      delete todo;
      if (!notifiedOverflow) {
         printf("Section buffer overflow. Dropping section.\n");
         notifiedOverflow=true;
      }
   }
   
   
}







#if 0
cDsmccReceiver::cDsmccReceiver(const char *channel) : cDynamicReceiver() {
   running=false;
   name = channel ? channel : "Unknown";
   processing=false;
}

cDsmccReceiver::~cDsmccReceiver() {
   //Stop thread
   running=false;
   Cancel(1);

   for (std::list<Dsmcc::ObjectCarousel*>::iterator it=carousels.begin(); it != carousels.end(); ++it)
      delete (*it);
}

Dsmcc::ObjectCarousel *cDsmccReceiver::AddCarousel(unsigned long id) {
   for (std::list<Dsmcc::ObjectCarousel*>::iterator it=carousels.begin(); it != carousels.end(); ++it)
      if ((*it)->getId() == id)
         return (*it);
   char buf[256];
   sprintf(buf, "%s-%ld", name.c_str(), id);
   Dsmcc::ObjectCarousel *car=new Dsmcc::ObjectCarousel(this, buf, id);
   carousels.push_back(car);
   return car;
}

Dsmcc::ObjectCarousel *cDsmccReceiver::AddHibernatedCarousel(Dsmcc::ObjectCarousel *hibernatedCar) {
   for (std::list<Dsmcc::ObjectCarousel*>::iterator it=carousels.begin(); it != carousels.end(); ++it)
      if ((*it) == hibernatedCar)
         return hibernatedCar;
         
   carousels.push_back(hibernatedCar);
   hibernatedCar->WakeUp(this);
   return hibernatedCar;
}

Dsmcc::ObjectCarousel *cDsmccReceiver::HibernateCarousel(unsigned long id) {
   Dsmcc::ObjectCarousel *car=0;
   std::list<Dsmcc::ObjectCarousel*>::iterator it;
   
   for (it=carousels.begin(); it != carousels.end(); ++it) {
      if ((*it)->getId() == id)
         break;
   }
   
   if ((car=(*it))) {
      LOCK_THREAD;
      //we must make sure that car->ProcessSection is not called and is not executing
      //This is not the 100% clean way, but I suppose it works
      while (processing)
         pthread_yield();
      DsmccStreamList::iterator sit;
      for (sit=streams.begin(); sit!=streams.end();) {
         if ((*sit).carousel == car)
            sit=streams.erase(sit);
         else
            ++sit;
      }
      carousels.erase(it);
      car->Hibernate();
   }
   
   return car;
}

void cDsmccReceiver::Activate(bool On) {
}

//AddStream only adds stream to list, does _not_ activate it
void cDsmccReceiver::AddStream(int pid, int assoc_tag, Dsmcc::ObjectCarousel *car) {
   LOCK_THREAD;
   DsmccStreamList::iterator it;
   for (it=streams.begin(); it!=streams.end(); ++it) {
      if ( (*it).pid==pid ) {
         if (!(*it).carousel)
            (*it).carousel=car;
         if (!(*it).assoc_tag)
            (*it).assoc_tag=assoc_tag;
         return;         
      }
   }   
   //not found, insert new stream into list
   streams.push_back(DsmccStream(pid, assoc_tag, car));
}

void ObjectCarousel::AddStreamForTap(int assoc_tag, Dsmcc::ObjectCarousel *car) {
   cMutexLock(&streamListMutex);
   DsmccStreamList::iterator it;
   for (it=streams.begin(); it!=streams.end(); ++it) {
      if ( (*it).assoc_tag==assoc_tag) {
         if ((*it).carousel)
            return;
         receiver->AddStream((*it).pid, assoc_tag, car);
         receiver->ActivateStream((*it).pid);
         return;
      }
   }
}


void cDsmccReceiver::ActivateStream(int pid, bool applyImmediately) {
   LOCK_THREAD;
   DsmccStreamList::iterator it;
   for (it=streams.begin(); it!=streams.end(); ++it) {
      if ( (*it).pid==pid ) {
         if (!(*it).carousel) //do not allow a stream without a carousel to be received
            return;
         ActivateStream(&(*it));
         
         if(!running) { //start thread if necessary
            running = true;
            Start();
         }         
      }
   }
   if (applyImmediately)
      ActivatePids();
}

void cDsmccReceiver::ActivateStreams() {
   LOCK_THREAD;
   DsmccStreamList::iterator it;
   for (it=streams.begin(); it!=streams.end(); ++it) {
      if (!(*it).carousel) //do not allow a stream without a carousel to be received
         continue;
      ActivateStream(&(*it));

      if(!running) { //start thread if necessary
         running = true;
         Start();
      }
   }
   ActivatePids();
}

void cDsmccReceiver::ActivateStream(DsmccStream *str) {
   if (!str->receiving) {
      str->receiving=true;
      printf("Added PID %d\n", str->pid);
      AddPid(str->pid);
   }
}


void cDsmccReceiver::Action() {
   DsmccStream stream;
   bool got;
   processing=false;
   while (running) {
   //LOCK_THREAD;
      Lock();
      if ( (got=todo.size()) ) {
         stream=todo.front();
         processing=true;
         //if (todo.size()>40)
            //printf("Get section from TODO, number %d", todo.size());
      }
      Unlock();
      
      if (got) {
         stream.carousel->ProcessSection(stream.data.getData(), stream.in_section);
         processing=false;
         
         Lock();
         todo.pop_front();
         Unlock();
      } else
         //usleep(1);
         pthread_yield();
   }
}

#define SECTION_BUFFER_SIZE 4284*2
void cDsmccReceiver::Receive(uchar *Data, int Length) {
//   struct pid_buffer *buf;
   int pid = 0;
   int cont;

   exdlog("New transport packet");

   if(Length <= 0 || Length != 188) {
      dlog("ts: Packet length invalid (%d bytes)", Length);
      return;
   }

   exdlog("ts: Packet Length: %d bytes", Length);

   if(!Data || *Data != DSMCC_SYNC_BYTE) {
      dlog("ts: Packet sync byte invalid (%X)", *Data);
      /* Cancel current section as skipped a packet */
      return;
   }

   /* Test if error set */

   if(*(Data+1) & DSMCC_TRANSPORT_ERROR) {
      dlog("ts: Packet transport error");
      return;
   }

   pid = ((*(Data+1) & 0x1F) << 8) | *(Data+2);

   exdlog("ts: Packet Pid: %d\n", pid);

   
   LOCK_THREAD;
   DsmccStreamList::iterator it;
   for (it=streams.begin(); it!=streams.end(); ++it) {
      if ( (*it).pid==pid )
         break;
   }
   
   if (it == streams.end()) {
      elog("ts: No buffer for pid %d", pid);
      return;
   }
   
   DsmccStream &buf=(*it);
   Dsmcc::ObjectCarousel *car=buf.carousel;
   if (!car) {
      printf("No carousel for stream.");
      return;
   }

      
   /* Test if start on new dsmcc_section */

   exdlog("ts(%d): Packet flags: %X %X %X\n", pid, *(Data+1), *(Data+2), *(Data+3));

   cont = *(Data+3) & 0x0F;

   exdlog("ts(%d): Packet Continutity Counter %d", pid, cont);

   if(buf.cont == 0xF && cont == 0) {
      buf.cont = 0;
      exdlog("ts(%d): Counter looped round", pid);
   } else if(buf.cont+1 == cont) {
      buf.cont++;
   } else if(buf.cont == -1) {
      exdlog("ts(%d): Set counter to %d", pid, cont);
      buf.cont = cont;
   } else if(buf.cont == cont) {
      exdlog("ts(%d): Duplicate packet, ignoring", pid);
      return;
   } else {
      /* Out of sequence packet, drop current section */
      exdlog("ts(%d): Counter out of sequence", pid);
      buf.in_section = 0;
      //memset(buf.data, 0xFF, SECTION_BUFFER_SIZE);
   }

   if(*(Data+1) & DSMCC_START_INDICATOR) {
      exdlog("dsmcc: New dsmcc section\n");
//      esyslog("new dsmcc section");
      if(buf.in_section) {
         exdlog("dsmcc(%d): Ending old section", pid);
         buf.pointer_field = *(Data+4);
         if(buf.pointer_field >= 0 && buf.pointer_field <183) {
            exdlog("dsmcc(%d): Reading upto %d bytes", pid, buf.pointer_field);
            if(buf.pointer_field > 0) {
               //assert(buf.in_section+buf.pointer_field<SECTION_BUFFER_SIZE);
               memcpy(buf.data.getData()+buf.in_section, Data+5,
                      buf.pointer_field);
            }
            exdlog("dsmcc(%d): Processing packet (length = %d )", pid, buf.in_section);
             
            static bool notifiedOverflow=false;
            if ( car ) { 
               if (todo.size()<=50) { //so "ring buffer" maximum size is 50*SECTION_BUFFER_SIZE
                  todo.push_back(buf); //put into todo list for use by processing thread
                                       //this is done by value, and the CharArray cares for itself
                  notifiedOverflow=false;
                  //if (todo.size()>30)
                     //printf("Put section in TODO, number %d\n", todo.size());
               } else if (!notifiedOverflow) {
                  printf("Section buffer overflow. Dropping section.\n");
                  notifiedOverflow=true;
               }
            } else {
               printf("No carousel found for pid %d!\n", pid);
            }
                
            //ProcessSection(buf.data, buf.in_section, pid);

            /* zero buffer ? */
            //memset(buf.data, 0xFF, SECTION_BUFFER_SIZE);
            buf.data.assign(SECTION_BUFFER_SIZE); //allocate and memset
            /* read data upto this and append to buf */
            buf.in_section = 183 - buf.pointer_field;
            buf.cont = -1;
            memcpy(buf.data.getData(), Data+(5+buf.pointer_field), buf.in_section);
         } else {
            elog("dsmcc(%d): - corrupted packet", pid);
            /* corrupted ? */
         }
      } else {
         buf.data.assign(SECTION_BUFFER_SIZE); //allocate and memset
         buf.in_section = 183;
         memcpy(buf.data.getData(), Data+5, 183);
         /* allocate memory and save data (test end ? ) */
      }
   } else {
       //exdlog("dsmcc(%d): Last packet continued");
      if(buf.in_section > 0) {
         if(buf.in_section > SECTION_BUFFER_SIZE) {
            esyslog("Packet overwritten buffer");
            return;
         }
         /* append data to buf */
         //printf("memcpy'ing %d bytes, in_section %d, buf %d\n", 184, buf.in_section, &buf);
         //assert(buf.in_section+184<SECTION_BUFFER_SIZE);
         memcpy(buf.data.getData()+buf.in_section, Data+4, 184);
         buf.in_section += 184;
      } else {
         /* error ? */
         exdlog("dsmcc(%d): Not synched to section yet", pid);
      }
   }

}

#endif



