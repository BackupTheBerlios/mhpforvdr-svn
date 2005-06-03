#ifndef DSMCC_RECEIVER_H
#define DSMCC_RECEIVER_H

#include <list>

#include <vdr/filter.h>
#include <vdr/tools.h>
#include <libsi/util.h>

#include <libdsmcc/biop.h>
#include <libdsmcc/dsmcc.h>

#include "cache.h"




#define	MAXCAROUSELS		16



/* ------------------ Classes interpreting/containing parsed data --------------------*/

namespace Dsmcc {

class ObjectCarousel {
public:
   ObjectCarousel(cDsmccReceiver *rec, const char *channelName, int id=0);
   ~ObjectCarousel();
   //main entry point from ObjectCarousels (which in turn is called by receiver)
   void ProcessSection(unsigned char *data, int length);
   void SetId(unsigned long _id) { id=_id; }
   unsigned long getId() { return id; }
   
   //Public API
   float getProgress() { return getProgress(0, 0); }
                        //returns value between 0 and 1.0
                        //which is the percentage of the data already received
   float getProgress(int *currentSize, int *totalSize); 
                        //if the pointers are non-null, returns the currently loaded
                        //and the total expected size in Bytes
   //Public API
   //the Cache will survive as long as a smart pointer is kept!
   SmartPtr<Cache::Cache> getCache() { return filecache; }
   
   bool isHibernated() { return hibernationState==StateHibernated || hibernationState==StateHibernatedCleared; }
   
   
   //Internal API (cDsmccReceiver only)
   
   //Hibernate the carousel. All subsequent calls to ProcessSection
   //will be ignored, the return value of getProgress is undefined,
   //the cache returned from getCache shall not be used.
   void Hibernate();
   //Wake up a hibernated carousel for specified receiver
   void WakeUp(cDsmccReceiver *rec);
   
protected:
   void InterpretDSI(Dsmcc::Section &section);
   void InterpretDII(Dsmcc::Section &section);
   void InterpretDDB(Dsmcc::Section &section);
   
   enum Stage { StageNone, StageDSI, StageDII, StageDDB };
   Stage stage;
   enum HibernationState { StateNormal, StateHibernated, StateHibernatedCleared,
                           StateWakingUpNone, StateWakingUpDSI, StateWakingUpDII };
   HibernationState hibernationState;
   
   void InterpretTap(Biop::Tap &tap);
   void AddStreamForTap(int assoc_tag);
   
   SmartPtr<Cache::Cache> filecache;

   class ModuleData {
   public:
      ModuleData(ObjectCarousel *car);
      ~ModuleData();
      void Set(ModuleInfo &info, DII &dii);
      void AddData(DDB &ddb);
      void ProcessModule(unsigned char *data, int len);
      
      unsigned short module_id;

      unsigned char version;
      unsigned long size;
      unsigned long curp;

      char *bstatus;	/* Block status bit field */
      std::list<DDB> blocks;
      bool cached;
      ObjectCarousel *carousel;

      unsigned short tag;
      bool isHibernated;

      typedef std::list<Dsmcc::Descriptor::Ptr> DescriptorList;
      DescriptorList descriptors;
   };
   friend class ModuleData;
   
   std::list<ModuleData> cache;
   unsigned long id;
   DSI *gate;
   
   cDsmccReceiver *receiver;
};


} //end of namespace Dsmcc

class DsmccStream {
public:
   DsmccStream();
   DsmccStream(int pid, int assoc_tag, Dsmcc::ObjectCarousel *car);
   DsmccStream(const DsmccStream &source);
   DsmccStream& operator=(const DsmccStream &source);
   int pid;
   int assoc_tag;
   bool receiving; //stream has been added with AddPid
   Dsmcc::ObjectCarousel *carousel;
};
typedef std::list<DsmccStream> DsmccStreamList;


class cDsmccReceiver : public cFilter, cThread {
private:
   bool running;

protected:
   virtual void Process(u_short Pid, u_char Tid, const u_char *Data, int Length);
   virtual void Action();
   void ActivateStream(DsmccStream *str);
   void RemoveStream(DsmccStream *str);
   std::string name;
   
   std::list<Dsmcc::ObjectCarousel *> carousels;
   cMutex carouselMutex;
   
   struct TodoSection {
      Dsmcc::ObjectCarousel* carousel;
      SI::CharArray data;
   };
   std::list<TodoSection *> todoList;
   cMutex todoMutex;
   cCondVar todoVar;
   
   DsmccStreamList streams;   
   cMutex streamListMutex;
   
   cMutex processingMutex;

public:
   //Public API
   cDsmccReceiver(const char *channel);
   ~cDsmccReceiver();
   
   //Public API
   //Adds stream information. Every stream of the service can be added.
   //The stream which contains the DSI of the carousel must be added with car != 0
   //However, you do not need to know that immediately. It can be called multiple times - e.g. the first
   //call can add the assoc_tag with car=0, second call adds carousel (while assoc_tag=0).
   void AddStream(int pid, int assoc_tag = 0, Dsmcc::ObjectCarousel *car = 0);
   
   //Public API
   //Activates the stream, i.e. the stream will be received.
   //Only possible for streams that contains DSMCC data.
   //Typically the stream carrying the DSI must be activated, the rest is added via Taps.
   void ActivateStream(int pid);
   
   //Public API
   //convenience function - activate all streams with a carousel
   //void ActivateStreams();
   
   //Public API
   //Add a carousel with given id (which may, but should not, be 0)
   //the pointer returned may be kept for the lifetime of the receiver
   Dsmcc::ObjectCarousel *AddCarousel(unsigned long id);
   
   //Public API
   //Hibernates carousel with given ID, deactivate and remove all related streams, 
   //return hibernated object for storage with the caller.
   //The pointer returned may be re-added later or deleted.
   Dsmcc::ObjectCarousel *HibernateCarousel(unsigned long id);
   
   //Public API
   //Re-add hibernated carousel. It must be guaranteed by the caller
   //that all necessary streams are added again, just as for a new carousel
   Dsmcc::ObjectCarousel *AddHibernatedCarousel(Dsmcc::ObjectCarousel *hibernatedCar);
   
   //For use by ObjectCarousel only
   void AddStreamForTap(int assoc_tag, Dsmcc::ObjectCarousel *car);
   
   const char *Name(void) { return name.c_str(); }
   bool Active() { return running; }
};

#endif

