/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef DVBSI_OBJECTS_H
#define DVBSI_OBJECTS_H

#include <libsi/section.h>

namespace DvbSi {

class PMT : public SI::PMT {
public:
   PMT(const unsigned char *data, bool doCopy=true) : SI::PMT(data, doCopy) {}
   PMT() : SI::PMT() {}
   class Stream : public SI::PMT::Stream {
   public:
      Stream &operator=(const SI::PMT::Stream &stream) 
        { SI::PMT::Stream::operator=(stream); return *this; }
      int getComponentTag();
   };
};

//make DvbSi::NIT inherit from SI::BAT, which inherits from SI::NIT,
//and DvbSi::BAT can inherit from DvbSi::NIT and doesn't need to reimplement
//the getNetworkName functions.
class NIT : public SI::BAT {
public:
   NIT(const unsigned char *data, bool doCopy=true) : SI::BAT(data, doCopy) {}
   NIT() : SI::BAT() {}
   //If a network name descriptor is found, the name is written into buffer.
   //If no such descriptor is found, the empty string is returned.
   bool getNetworkName(char *buffer, unsigned int nLength);
   //Additionally returns the short name (TR 201 111) in second buffer.
   bool getNetworkName(char *buffer, char *shortVersion, unsigned int nLength, unsigned int nShortLength); 
};

//typedef SI::PMT PMT;
//typedef SI::NIT NIT;
//typedef SI::BAT BAT;
//typedef SI::EIT EIT;
typedef SI::TSDT TSDT;

class SDT : public SI::SDT {
public:
   SDT(const unsigned char *data, bool doCopy=true) : SI::SDT(data, doCopy) {}
   SDT() : SI::SDT() {}
   class Service : public SI::SDT::Service {
   public:
      void SetIds(int ni, int ti) { nid=ni; tid=ti; }
      Service &operator=(const SI::SDT::Service &service) 
        { SI::SDT::Service::operator=(service); return *this; }
      int getTransportStreamId() { return tid; }
      int getOriginalNetworkId() { return nid; }
      int getServiceType();
      //If a service descriptor is found, the name is written into buffer.
      //If no such descriptor is found, the empty string is returned.
      bool getServiceName(char *buffer,unsigned int nLength);
      //Additionally returns the short name (TR 201 111) in second buffer.
      bool getServiceName(char *buffer, char *shortVersion, unsigned int nLength, unsigned int nShortLength);
      bool getProviderName(char *buffer,unsigned int nLength);
      bool getProviderName(char *buffer, char *shortVersion, unsigned int nLength, unsigned int nShortLength);
   protected:
      int nid;
      int tid;
   };
};

class EIT : public SI::EIT {
public:
   EIT(const unsigned char *data, bool doCopy=true) : SI::EIT(data, doCopy) {}
   EIT() : SI::EIT() {}
   class Event : public SI::EIT::Event {
   public:
      Event &operator=(const SI::EIT::Event &event)
        { SI::EIT::Event::operator=(event); return *this; }
      //returns an array where each element represents a level1 content nibble
      //in its four least significant bits.
      //The array is allocated with new.
      //If no descriptor is found, 0 is returned.
      SI::EightBit *getContentNibbleLevel1(int &count);
      //returns an array where each element represents a level1 content nibble
      //in its most significant bits, level2 in the LSBs.
      //The array is allocated with new.
      //If no descriptor is found, 0 is returned.
      SI::EightBit * getContentNibbles(int &count);
      bool getEventName(char *buffer,unsigned int nLength);
      bool getEventName(char *buffer, char *shortVersion, unsigned int nLength, unsigned int nShortLength);
      bool getShortDescription(char *buffer, unsigned int nLength);
   };
};

class BAT : public NIT {
public:
   BAT(const unsigned char *data, bool doCopy=true) : NIT(data, doCopy) {}
   BAT() : NIT() {}   
   bool getBouquetName(char *buffer,unsigned int nLength)
     { return getNetworkName(buffer,nLength); }
   bool getBouquetName(char *buffer, char *shortVersion, unsigned int nLength, unsigned int nShortLength)
     { return getNetworkName(buffer, shortVersion, nLength, nShortLength); }
};


}

#endif

