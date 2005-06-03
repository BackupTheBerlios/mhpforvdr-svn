#ifndef LIBDSMCC_DESCRIPTOR_H
#define LIBDSMCC_DESCRIPTOR_H

#include <string>
#include "util.h"
#include <stdio.h>

namespace Dsmcc {

class Descriptor : public Parsable, public SmartPtrObject {
public:
   Descriptor();
   enum DescriptorType { Type = 0x01, Name = 0x02, Info = 0x03, Modlink = 0x04,
                Crc32 = 0x05, Location = 0x06, DlTime = 0x07, Grouplink = 0x08,
                 Compressed = 0x09, Private = 0x0A };
            /* According to spec ETSI EN 301 192:
               0x0A subgroup association descriptor (no structure given in spec)
               0x0B-0x6F  future use by DVB
               0x70-0x7F  future use by MHP
               0x80-0xFF  private descriptors */
   virtual DescriptorType getType() = 0;
   static Descriptor *DecideAndParse(RawData Data, int &off);
   unsigned char tag;
   unsigned char len;
   typedef SmartPtr<Descriptor> Ptr;
};

class TypeDescriptor : public Descriptor {
public:
   virtual DescriptorType getType() { return Descriptor::Type;}
   virtual void Parse(RawData Data, int &off);
   std::string text;
};

class NameDescriptor : public Descriptor {
public:
   virtual DescriptorType getType() { return Descriptor::Name;}
   virtual void Parse(RawData Data, int &off);
   std::string text;
};

class InfoDescriptor : public Descriptor {
public:
   virtual DescriptorType getType() { return Descriptor::Info;}
   virtual void Parse(RawData Data, int &off);
   char lang_code[3];
   std::string text;
};

class ModlinkDescriptor : public Descriptor {
public:
   virtual DescriptorType getType() { return Descriptor::Modlink;}
   virtual void Parse(RawData Data, int &off);
   char position;
   unsigned short module_id;
};

class Crc32Descriptor : public Descriptor {
public:
   virtual DescriptorType getType() { return Descriptor::Crc32;}
   virtual void Parse(RawData Data, int &off);
   unsigned long crc;
};

class LocationDescriptor : public Descriptor {
public:
   virtual DescriptorType getType() { return Descriptor::Location;}
   virtual void Parse(RawData Data, int &off);
   char location_tag;
};

class DlTimeDescriptor : public Descriptor {
public:
   virtual DescriptorType getType() { return Descriptor::DlTime;}
   virtual void Parse(RawData Data, int &off);
   unsigned long download_time;
};

class GrouplinkDescriptor : public Descriptor {
public:
   virtual DescriptorType getType() { return Descriptor::Grouplink;}
   virtual void Parse(RawData Data, int &off);
   char position;
   unsigned long group_id;
};

class PrivateDescriptor : public Descriptor {
public:
   virtual DescriptorType getType() { return Descriptor::Private;}
   virtual void Parse(RawData Data, int &off);
};

class CompressedDescriptor : public Descriptor {
public:
   virtual DescriptorType getType() { return Descriptor::Compressed;}
   virtual void Parse(RawData Data, int &off);
   char method;
   unsigned long original_size;
};

}


 
#endif
