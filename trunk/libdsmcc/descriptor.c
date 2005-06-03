#include <stdlib.h>
#include "descriptor.h"

namespace Dsmcc {

Descriptor *Descriptor::DecideAndParse(RawData Data, int &off) {
   unsigned char tag=Data[off++];
   
   Descriptor *d=0;
   switch (tag) {
   case Type:
      d=new TypeDescriptor();
      break;
   case Name:
      d=new NameDescriptor();
      break;
   case Info:
      d=new InfoDescriptor();
      break;
   case Modlink:
      d=new ModlinkDescriptor();
      break;
   case Crc32:
      d=new Crc32Descriptor();
      break;
   case Location:
      d=new LocationDescriptor();
      break;
   case DlTime:
      d=new DlTimeDescriptor();
      break;
   case Grouplink:
      d=new GrouplinkDescriptor();
      break;
   case Compressed:
      d=new CompressedDescriptor();
      break;
   default:
      if (0x0A <= tag <= 0xFF)
         d=new PrivateDescriptor();
   }
   
   if (d) {
      d->tag=tag;
      d->len=Data[off++];
      d->Parse(Data, off);
   }
   
   return d;
}

Descriptor::Descriptor() {
}


void TypeDescriptor::Parse(RawData Data, int &off) {
   text=(char *)Data.getPointer(off, len); //text is a std::string, so this implies copying!
   off+=len;
}

void NameDescriptor::Parse(RawData Data, int &off) {
   text=(char *)Data.getPointer(off, len);
   off+=len;
}

void InfoDescriptor::Parse(RawData Data, int &off) {
   lang_code[0]=Data[off++];
   lang_code[1]=Data[off++];
   lang_code[2]=Data[off++];
   
   text=(char *)Data.getPointer(off, len);
   off+=len;
}

void ModlinkDescriptor::Parse(RawData Data, int &off) {
   position=Data[off++];
   module_id=Data.TwoBytes(off);
   off+=2;
}

void Crc32Descriptor::Parse(RawData Data, int &off) {
   crc=Data.FourBytes(off);
   off+=4;
}

void LocationDescriptor::Parse(RawData Data, int &off) {
   location_tag=Data[off++];
}

void DlTimeDescriptor::Parse(RawData Data, int &off) {
   download_time=Data.FourBytes(off);
   off+=4;
}

void GrouplinkDescriptor::Parse(RawData Data, int &off) {
   position=Data[off++];
   group_id=Data.FourBytes(off);
   off+=4;
}

void PrivateDescriptor::Parse(RawData Data, int &off) {
   //currently simply skip over contents
   off+=len;
}

void CompressedDescriptor::Parse(RawData Data, int &off) {
   method=Data[off++];
   original_size=Data.FourBytes(off);
   off+=4;
}

}




