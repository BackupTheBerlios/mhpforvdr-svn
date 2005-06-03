/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include <libsi/descriptor.h>
#include <vdr/config.h>

#include "objects.h"

namespace DvbSi {

bool NIT::getNetworkName(char *buffer, unsigned int nLength) {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   if ( (d=commonDescriptors.getNext(it, SI::NetworkNameDescriptorTag)) ) {
      SI::NetworkNameDescriptor *nnd=(SI::NetworkNameDescriptor *)d;
      nnd->name.getText(buffer, nLength); 
      delete d;
      return true;
   }
   buffer[0]='\0';
   return false;
}

bool NIT::getNetworkName(char *buffer, char *shortVersion, unsigned int nLength, unsigned int nShortLength) {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   if ( (d=commonDescriptors.getNext(it, SI::NetworkNameDescriptorTag)) ) {
      SI::NetworkNameDescriptor *nnd=(SI::NetworkNameDescriptor *)d;
      nnd->name.getText(buffer, shortVersion, nLength, nShortLength); 
      delete d;
      return true;
   }
   buffer[0]='\0';
   shortVersion[0]='\0';
   return false;
}

int PMT::Stream::getComponentTag() {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   if ( (d=streamDescriptors.getNext(it, SI::StreamIdentifierDescriptorTag)) ) {
      SI::StreamIdentifierDescriptor *sd=(SI::StreamIdentifierDescriptor *)d;
      int ret=sd->getComponentTag();
      delete d;
      return ret;
   }
   return -2;
}

int SDT::Service::getServiceType() {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   if ( (d=serviceDescriptors.getNext(it, SI::ServiceDescriptorTag)) ) {
      SI::ServiceDescriptor *sd=(SI::ServiceDescriptor *)d;
      int ret=sd->getServiceType();
      delete d;
      return ret;
   }
   return -2;
}

bool SDT::Service::getServiceName(char *buffer, unsigned int nLength) {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   if ( (d=serviceDescriptors.getNext(it, SI::ServiceDescriptorTag)) ) {
      SI::ServiceDescriptor *sd=(SI::ServiceDescriptor *)d;
      sd->serviceName.getText(buffer, nLength);
      delete d;
      return true;
   }
   buffer[0]='\0';
   return false;
}

bool SDT::Service::getServiceName(char *buffer, char *shortVersion, unsigned int nLength, unsigned int nShortLength) {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   if ( (d=serviceDescriptors.getNext(it, SI::ServiceDescriptorTag)) ) {
      SI::ServiceDescriptor *sd=(SI::ServiceDescriptor *)d;
      sd->serviceName.getText(buffer, shortVersion, nLength, nShortLength); 
      delete d;
      return true;
   }
   buffer[0]='\0';
   shortVersion[0]='\0';
   return false;
}

bool SDT::Service::getProviderName(char *buffer, unsigned int nLength) {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   if ( (d=serviceDescriptors.getNext(it, SI::ServiceDescriptorTag)) ) {
      SI::ServiceDescriptor *sd=(SI::ServiceDescriptor *)d;
      sd->providerName.getText(buffer, nLength); 
      delete d;
      return true;
   }
   buffer[0]='\0';
   return false;
}

bool SDT::Service::getProviderName(char *buffer, char *shortVersion, unsigned int nLength, unsigned int nShortLength) {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   if ( (d=serviceDescriptors.getNext(it, SI::ServiceDescriptorTag)) ) {
      SI::ServiceDescriptor *sd=(SI::ServiceDescriptor *)d;
      sd->providerName.getText(buffer, shortVersion,nLength,nShortLength); 
      delete d;
      return true;
   }
   buffer[0]='\0';
   shortVersion[0]='\0';
   return false;
}



SI::EightBit *EIT::Event::getContentNibbleLevel1(int &count) {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   //currently only uses first found descriptor
   if ( (d=eventDescriptors.getNext(it, SI::ContentDescriptorTag)) ) {
      SI::ContentDescriptor *cd=(SI::ContentDescriptor *)d;
      SI::Loop::Iterator it2;
      int c=0;
      SI::ContentDescriptor::Nibble nibble;
      #if VDRVERSNUM > 10312
      while (cd->nibbleLoop.getNext(nibble, it2))
      #else
      while (cd->nibbleLoop.hasNext(it2))
      #endif
         c++;
      SI::EightBit *array=new SI::EightBit[c];
      count=c;
      it2.reset();
      c=0;
      #if VDRVERSNUM > 10312
      while (cd->nibbleLoop.getNext(nibble, it2))
         array[c++]=(nibble.getContentNibbleLevel1() & 0xFF);
      #else
      while (cd->nibbleLoop.hasNext(it2))
         array[c++]=(cd->nibbleLoop.getNext(it2).getContentNibbleLevel1() & 0xFF);
      #endif
      return array;
   }
   return 0;
}

SI::EightBit *EIT::Event::getContentNibbles(int &count) {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   if ( (d=eventDescriptors.getNext(it, SI::ContentDescriptorTag)) ) {
      SI::ContentDescriptor *cd=(SI::ContentDescriptor *)d;
      SI::Loop::Iterator it2;
      int c=0;
      SI::ContentDescriptor::Nibble nibble;
      #if VDRVERSNUM > 10312
      while (cd->nibbleLoop.getNext(nibble, it2))
      #else
      while (cd->nibbleLoop.hasNext(it2))
      #endif
         c++;
      SI::EightBit *array=new SI::EightBit[c];
      count=c;
      it2.reset();
      c=0;
      #if VDRVERSNUM > 10312
      while (cd->nibbleLoop.getNext(nibble, it2))
         array[c++]=(nibble.getContentNibbleLevel1() << 16) & (nibble.getContentNibbleLevel2());
      #else
      while (cd->nibbleLoop.hasNext(it2)) {
         nibble=cd->nibbleLoop.getNext(it2);
         array[c++]=(nibble.getContentNibbleLevel1() << 16) & (nibble.getContentNibbleLevel2());
      }
      #endif
      return array;
   }
   return 0;
}

bool EIT::Event::getEventName(char *buffer, unsigned int nLength) {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   if ( (d=eventDescriptors.getNext(it, SI::ShortEventDescriptorTag)) ) {
      SI::ShortEventDescriptor *sed=(SI::ShortEventDescriptor *)d;
      sed->name.getText(buffer, nLength);
      return true;
   }
   buffer[0]='\0';
   return false;
}

bool EIT::Event::getEventName(char *buffer, char *shortVersion, unsigned int nLength, unsigned int nShortLength) {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   if ( (d=eventDescriptors.getNext(it, SI::ShortEventDescriptorTag)) ) {
      SI::ShortEventDescriptor *sed=(SI::ShortEventDescriptor *)d;
      sed->name.getText(buffer, shortVersion, nLength, nShortLength); 
      return true;
   }
   buffer[0]='\0';
   return false;
}

bool EIT::Event::getShortDescription(char *buffer, unsigned int nLength) {
   SI::Loop::Iterator it;
   SI::Descriptor *d;
   if ( (d=eventDescriptors.getNext(it, SI::ShortEventDescriptorTag)) ) {
      SI::ShortEventDescriptor *sed=(SI::ShortEventDescriptor *)d;
      sed->text.getText(buffer, nLength); 
      return true;
   }
   buffer[0]='\0';
   return false;
}



}

