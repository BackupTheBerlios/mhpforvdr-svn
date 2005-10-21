/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef DVBSI_REQUESTUTIL_H
#define DVBSI_REQUESTUTIL_H

#include <set>
#include <vector>
#include <libsi/si.h>

#include "request.h"
#include "filter.h"
#include "util.h"


/* Some common code used by sirequests.h */


namespace DvbSi {

class Database;

//Request with clear status for finish and dispatch
class DatabaseRequest : public Request {
public:
   DatabaseRequest(Listener *l, void *ad=0) : Request(l, ad), finished(false), hasDispatched(false) {}
   virtual const DataSource &getDataSource() { return source; }
protected:
   void ScheduleDispatch(DatabasePtr db);
   void setDataSource(const DataSource &s) { source=s; }
   bool finished;
   bool hasDispatched;
   DataSource source;
};

//Request which is a cFilter and cleanly handles cancelling, channel switches, timeouts and dispatches
class FilterRequest : public DatabaseRequest, protected Filter, protected TimedBySeconds {
public:
   FilterRequest(DatabasePtr  db, Listener *l, void *ad=0);
   virtual ~FilterRequest();
   bool CancelRequest();
   virtual void Detach();
   virtual void Dispatch();
protected:
   void ScheduleDispatch() { DatabaseRequest::ScheduleDispatch(getDatabase()); }
   virtual void OtherTransportStream(Service::TransportStreamID ts);
   bool checkFinish();
   virtual void Execute(); //from TimedBySeconds to handle timeout
   //the code that shall be send if filter times out - per default TableNotFound
   ResultCode timeOutCode;
};

//Request which lets another request do the real work
class SecondaryRequest : public DatabaseRequest, public Listener {
public:
   SecondaryRequest(Listener *l, void *ad=0) : DatabaseRequest(l, ad), request(0) {}
   virtual bool CancelRequest();
protected:
   void setDataSource(Request *req);
   using DatabaseRequest::setDataSource;
   Request *request;
};

//a simple utility class required by the retrieveDescriptors call in org.dvb.si
class DescriptorRequest : public DatabaseRequest {
public:
   DescriptorRequest(DataSource &source, Listener *l, void *ad=0) : DatabaseRequest(l, ad) 
     { setDataSource(source); result=ResultCodeSuccess; }
   ~DescriptorRequest();
   void Add(SI::DescriptorLoop &loop);
   void Add(SI::DescriptorLoop &loop, int *tags, int size);
   bool CancelRequest() { return false; }
   std::list<SI::Descriptor *> list;
   typedef std::list<SI::Descriptor *>::iterator iterator;
   typedef SI::Descriptor* objectType;
};


#define MAX_DUPLICATES 3

//Request which returns a number of tables specified by subclass-specific ids.
//if v==0, all specific tables are returned.
template <class T> class SectionList {
protected:
   //used as an STL function object for the set
   template <class U> class SectionLess {
   public:
      //this function provides a "less than" ordering for NumberedSections
      bool operator()(const U &first, const U &second) {

         if (first.getTableId() == second.getTableId()) {

            if (first.getTableIdExtension() == second.getTableIdExtension()) {
               return first.getSectionNumber() < second.getSectionNumber();
            } else
               return first.getTableIdExtension() < second.getTableIdExtension();

         } else
            return first.getTableId() < second.getTableId();

      }
   };
public:
   SectionList(IdTracker *trl/*, const Compare &funcObj*/) : list(funcObj), /*funcObj(this),*/ tracker(trl) {}
   virtual ~SectionList() {
      delete tracker;
   }
   typedef std::set<T, SectionLess<T> > List;
   typedef typename List::iterator iterator;
   typedef T objectType;
   List list;

protected:
   void versionCheck(const T &section, std::pair<iterator, bool> &pair) {
      if ((*pair.first).getVersionNumber() != section.getVersionNumber()) {
         list.erase(pair.first);
         list.insert(section);
         //TODO: remove other sections with same old version number, added--!
      }
   }

   int addSection(const T &section) {
      int added=0;
      std::pair<iterator, bool> pair=list.insert(section);
      if (!pair.second) {
         versionCheck(section, pair);
      } else
         added++;
      return added;
   }

   // currentSubtableComplete indicates that the subtable the section belongs to is complete.
   // It will be true only for the one single call where the last missing section is added.
   virtual int addSection(const T &section, bool &currentSubtableComplete) {
      int added=0;
      std::pair<iterator, bool> pair=list.insert(section);
      if (!pair.second) {
         versionCheck(section, pair);
         //currentSubtableComplete shall be true exactly once,
         //when the last missing section is added
         currentSubtableComplete=false;
      } else {
         added++;
         if (section.moreThanOneSection()) {
            //printf("multiple section table, section added, %d %d %d\n", (*pair.first).getTableId(), (*pair.first).getTableIdExtension(), (*pair.first).getSectionNumber());

            int sectionCount=section.getLastSectionNumber()+1;

            iterator it=pair.first;
            //go from position of now added section to first already added section of current subtable
            for(;;--it) {
               if ( it->getTableId() == section.getTableId() && it->getTableIdExtension() == section.getTableIdExtension())
                  sectionCount--;
               else
                  break;
               if (it == list.begin())
                  break;
            }

            //now, go to end of subtable
            it=pair.first;
            for (++it; it != list.end(); ++it) {
               if ( it->getTableId() == section.getTableId() && it->getTableIdExtension() == section.getTableIdExtension())
                  sectionCount--;
               else
                  break;
            }

            //printf("moreThanOneSection: remaining sectionCount %d\n", sectionCount);
            currentSubtableComplete = (sectionCount==0);

         } else
            currentSubtableComplete=true;
      }
      return added;
   }

   //This is currently only needed for the EIT which may be segmented.
   //Each new segment starts with a multiple of 8 as SectionNumber .
   int addSectionSegmented(const T &section, bool &currentSubtableComplete) {
      int added=0;
      std::pair<iterator, bool> pair=list.insert(section);
      if (!pair.second) {
         versionCheck(section, pair);
         //currentSubtableComplete shall be true exactly once,
         //when the last missing section is added
         currentSubtableComplete=false;
      } else {
         added++;
         if (section.moreThanOneSection()) {
            //printf("multiple section table, section added, %d %d %d\n", (*pair.first).getTableId(), (*pair.first).getTableIdExtension(), (*pair.first).getSectionNumber());

            iterator it;
            //find first section of subtable
            for(it=pair.first; it->getTableId() == section.getTableId() && 
                               it->getTableIdExtension() == section.getTableIdExtension();
                               --it)
            {
               if (it == list.begin())
                  break;
            }

            int expect=0, end=section.getLastSectionNumber();
            currentSubtableComplete=false;
            //now, go to end of subtable
            for (; it != list.end() && it->getTableId() == section.getTableId() &&
                            it->getTableIdExtension() == section.getTableIdExtension(); ++it) {

               int current=it->getSectionNumber();
               if (current != expect) {
                  currentSubtableComplete=false;
                  break;
               }
               if (current == end) {
                  currentSubtableComplete=true;
                  break;
               }

               if (current == it->getSegmentLastSectionNumber()) {
                  int factor=(current-(current%8))/8;
                  expect=8*(factor+1);
               } else
                  expect++;
            }

         } else
            currentSubtableComplete=true;
      }
      return added;
   }

   // This is currently only needed for the EIT which may be segmented.
   // Each new segment starts with a multiple of 8 as SectionNumber.
   // This overloaded member allows to specify a list of segments.
   // This list can contain values from 0 to 512, where there are 32 segments in each section,
   // and for each 32 segments, the table id is incremented by 1.
   // There are 16 possible table_id values. (EIT: 0x50-0x5F, or 0x60-0x6F)
   // Works only with a single table id extension, there are no checks if sections with
   // different table id extentions are added!
   // The segmentList vector shall be ordered in ascending order.
   int addSectionSegmented(const T &section, std::vector<int> segmentList, bool &segmentsComplete) {
      if (segmentList.size() == 0) {
         segmentsComplete=true;
         return 0;
      }
      int added=0;
      std::pair<iterator, bool> pair=list.insert(section);
      if (!pair.second) {
         versionCheck(section, pair);
         // currentSubtableComplete shall be true exactly once,
         // when the last missing section is added
         segmentsComplete=false;
      } else {
         added++;
         // set return value to false initially
         segmentsComplete=false;
         // blend out last hexadecimal digit
         int tableIdBase = section.getTableId() & 0xF0;
         uint completeSegments=0;
         iterator it;
         // find first section of all tables
         for (it=list.begin(); it != list.end(); ++it)
         {
            if ((it->getTableId() & 0xF0) == tableIdBase)
               break;
         }
         //printf("EIT section with table_id %d, section %d, tableIdBase %d, have initial section %d\n", section.getTableId(), section.getSectionNumber(), tableIdBase, it != list.end());

         for (std::vector<int>::iterator segmentIt = segmentList.begin(); segmentIt != segmentList.end() && it != list.end(); ++segmentIt) {
            bool currentSegmentComplete=false;
            int tableId = tableIdBase + ((*segmentIt)/32);

            // find first section of subtable
            for(; it != list.end(); ++it)
            {
               if (it->getTableId() == tableId)
                  break;
            }

            // now, go to end of subtable
            int expect=(*segmentIt)*8;
            int end=section.getLastSectionNumber();
            //printf("Segment loop: tableId %d, segment %d, section %d, last section %d\n", tableId, *segmentIt, expect, end);
            for (; it != list.end() && it->getTableId() == tableId; ++it) {

               int current=it->getSectionNumber();
               if (current > expect) {
                  currentSegmentComplete=false;
                  break;
               }

               if (current == end) {
                  currentSegmentComplete=true;
                  break;
               }
               // unneeded sections are simply skipped
               if (current < expect)
                  continue;
               // current == expect
               if (current == it->getSegmentLastSectionNumber()) {
                  currentSegmentComplete=true;
                  break;
               } else
                  expect++;
            }

            if (currentSegmentComplete)
               completeSegments++;
            else
               break;
         }

         if (completeSegments == segmentList.size())
            segmentsComplete=true;
      }
      return added;
   }

   SectionLess<T> funcObj;
   IdTracker *tracker;
};

//this class simply merges the hierachy
template <class T> class TableFilterRequest : public FilterRequest, public SectionList<T> {
public:
   TableFilterRequest(DatabasePtr  db, IdTracker *tracker, Listener *l, void *ad=0)
      : FilterRequest(db, l, ad), SectionList<T>(tracker) {}
};

//this is a class that unifies code common to several requests (NIT, BAT, SDT)
template <class T> class TableFilterTrackerRequest : public TableFilterRequest<T> {
public:
   TableFilterTrackerRequest(DatabasePtr  db, IdTracker *tracker, Listener *l, void *ad=0)
      : TableFilterRequest<T>(db, tracker, l, ad),
        completeSubtables(0),
        duplicates(0) {}

   int getNumberOfSubtables() {
      if (this->tracker || completeSubtables)
         return completeSubtables;
      else {
         if (this->list.begin() == this->list.end())
            return 0;
         do {
            completeSubtables++;
         } while (getNextSubtable(this->list.begin()) != this->list.end());
         return completeSubtables;
      }
   }

   typename SectionList<T>::iterator getNextSubtable(typename SectionList<T>::iterator beginningOfOld) {
      for (typename SectionList<T>::iterator it=beginningOfOld; it != this->list.end(); ++it) {
         if (it->getTableId() !=  beginningOfOld->getTableId()|| 
               it->getTableIdExtension() != beginningOfOld->getTableIdExtension()) {
            return it;
         }
      }
      return this->list.end();
   }
protected:
   bool checkSection(const T &section, int &duplicates) {

      if (this->tracker) {
         if (this->tracker->isIncluded(section.getTableIdExtension())) {
            bool subtableComplete;
            addSection(section, subtableComplete);
            //printf("subtable completed ? %d %d\n", subtableComplete, completeSubtables);
            if (subtableComplete)
               completeSubtables++;
         }
         return (completeSubtables >= this->tracker->getSize());
      } else {
         if (!addSection(section))
            duplicates++;
         //printf("duplicates: %d\n", duplicates);

         //it is very likely that some filters time out before MAX_DUPLICATES has been reached,
         //although they contain useful, probably complete data. So simply set this to Success
         //for the non-finite trackers if at least one section was received.
         this->timeOutCode=ResultCodeSuccess;
         return (duplicates>MAX_DUPLICATES);
      }

   }

   bool checkSection(const T &section) { return checkSection(section, duplicates); }

   int completeSubtables;
   int duplicates; 
};

template <class T> class SegmentedTableFilterTrackerRequest : public TableFilterTrackerRequest<T> {
public:
   SegmentedTableFilterTrackerRequest(DatabasePtr  db, IdTracker *tracker, Listener *l, void *ad=0)
      : TableFilterTrackerRequest<T>(db, tracker, l, ad) {}
protected:
   virtual int addSection(const T &section, bool &currentSubtableComplete) {
      return addSectionSegmented(section, currentSubtableComplete);
   }
};


//a secondary request which will return a list of SI objects
template <class T> class ListSecondaryRequest : public SecondaryRequest {
public:
   ListSecondaryRequest(DatabasePtr  db, Listener *l, void *ad=0)
      : SecondaryRequest(l, ad), database(db) {}
   std::list<T> list;
   typedef typename std::list<T>::iterator iterator;
   typedef T objectType;
   DatabasePtr  getDatabase() { return database; }
protected:
   void ScheduleDispatch() { DatabaseRequest::ScheduleDispatch(getDatabase()); }
   DatabasePtr  database;
};



}


#endif
