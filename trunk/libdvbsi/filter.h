/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef DVBSI_FILTER_H
#define DVBSI_FILTER_H

#include <vdr/filter.h>
#include <vdr/thread.h>

namespace DvbSi {

class Database;
class Request;

//See ETSI TR 101 211 (chapter 4.4) for minimum repetition rates.
//These here are a simplified version.
#define FILTER_TIMEOUT 10
#define FILTER_TIMEOUT_NIT 10
#define FILTER_TIMEOUT_BAT 10
#define FILTER_TIMEOUT_SDT 10
#define FILTER_TIMEOUT_EIT 10
#define FILTER_TIMEOUT_EIT_SCHEDULE_OTHER 30
#define FILTER_TIMEOUT_TOT 30
#define FILTER_TIMEOUT_TDT 30

class Filter : public cFilter {
friend class Database;
public:
   Filter(Database *db, bool attach=true);
   virtual ~Filter();
   void Attach();
   void Detach();
   Database *getDatabase() { return database; }
protected:
   Database *database;
private:
   bool attached;
};

class RequestFilter : public Filter {
public:
   RequestFilter(Database *db, Request *r) : Filter(db), request(r) {}
protected:
   Request *request;
};

/*class SingleShotFilter : public Filter {
public:
   SingleShotFilter(Database *db) : Filter(db) {}
protected:
   bool singleShot;
};*/

/*class IntermittentFilter : public Filter {
public:
   IntermittentFilter(Database *db) : Filter(db), isAttached(false) {}
   void SafeAttach();
   void SafeDetach();
protected:
   bool isAttached;
};*/


}

#endif
