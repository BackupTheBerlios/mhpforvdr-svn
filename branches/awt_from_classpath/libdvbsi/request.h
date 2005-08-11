/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef DVBSI_REQUEST_H
#define DVBSI_REQUEST_H

#include <stdint.h>
#include <time.h>

namespace DvbSi {


enum ResultCode {
      ResultCodeUnknown = 0,
      ResultCodeSuccess,
      ResultCodeLackOfResources,
      ResultCodeNotInCache,
      ResultCodeObjectNotInTable,
      ResultCodeRequestCancelled,
      ResultCodeTableNotFound,
      ResultCodeTableUpdated,
      
      ResultCodeDataSwitch //transport stream changed while monitoring
};

enum RetrieveMode { FromCacheOnly=1, FromCacheOrStream=2, FromStreamOnly=3 };

class Listener;
class Filter;

struct DataSource {
   DataSource() { retrievalTime=0; source=0; onid=0; tid=0; }
   DataSource(int source, int oni, int ti) : source(source), onid(oni), tid(ti) { SetTime(); }
   void SetTime() { time(&retrievalTime); }
   void SetIds(int sourc, int oni, int ti) { source=sourc; onid=oni; tid=ti; }
   time_t retrievalTime;
   int source;
   uint16_t onid;
   uint16_t tid;
};

class Request {
public:
   Request(Listener *l, void *ad=0) : result(ResultCodeUnknown), listener(l), appData(ad) {}
   virtual ~Request() {}
   virtual bool CancelRequest() = 0;
   virtual const DataSource &getDataSource() = 0;
   virtual bool isAvailableInCache() { return false; }
   void *getAppData() { return appData; }
   Listener *getListener() { return listener; }
   ResultCode getResultCode() { return result; }
protected:
   ResultCode result;
private:
   Listener *listener;
   void *appData;
};

class Listener {
public:
   virtual void Result(Request *req) {}
};


}

#endif
