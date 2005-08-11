/***************************************************************************
 *       Copyright (c) 2003 by Richard Palmer <richard@magicality.org>
 *                             Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include "dsmcc.h"
 
namespace Dsmcc {

Header::Header(int length) {
   valid=false;
   Length=length;
}

void Header::Parse(RawData Data, int &off) {
   int crc_offset = Length - 4 - 1 + off;
   unsigned short section_len;
   unsigned long crc32_decode;
   
   section_len = ((Data[off+1] & 0xF) << 8) | (Data[off+2]) ;
   section_len += 3;/* 3 bytes before length count starts */
   
   //skip to end, read last 4 bytes and store in crc
   crc = Data.FourBytes(crc_offset);
   
   unsigned char *crc_ptr=Data.getPointer(off, section_len);
   if( crc_ptr && (crc32_decode  = dsmcc_crc32(crc_ptr, section_len))!= 0) {
      esyslog("Corrupt CRC for section, dropping");
      return;
   }
   
   table_id = Data[off++];
   flags[0] = Data[off++];
   flags[1] = Data[off++];
   
   /* Check CRC is set and private_indicator is set to its complement,
    * else skip packet */
   if(((flags[0] & 0x80) == 0) || (flags[0] & 0x40) != 0) {
      return; /* Section invalid */
   }
   valid=true;
   
   off++; //reserved
   
   table_id_extension = Data.TwoBytes(off);
   off+=2;
   
   flags2 = Data[off++];
   
   off++;
}

Section::Section(int length) : Header(length) {
   body=0;
   bodyStolen=false;
}

Section::~Section() {
   if (!bodyStolen)
      delete body;
}


//main entry point for parsing a Dsmcc section
void Section::Parse(RawData Data, int &off) {
   Header::Parse(Data, off);
   
   if (!Header::valid)
      return;
   
   switch (table_id) {
      case DSMCC_SECTION_INDICATION:
         //either created A DSI or a DII and parses it.
         body=InformationBody::DecideAndParse(Data, off);
         break;
      case DSMCC_SECTION_DATA:
         body=new DDB();
         body->Parse(Data, off);
         break;
      case DSMCC_SECTION_DESCR:
         esyslog("Descriptor Section - unimplemented");
         break;
   }      
}

MsgHeader::MsgHeader() {
   valid=false;
}

void MsgHeader::Parse(RawData Data, int &off) {
   protocol = Data[off++];   
   type = Data[off++];
   
   valid = (type == 0x03) && (protocol == 0x11);
   if (!valid)
      return;
      
   message_id = Data.TwoBytes(off);
   off+=2;
   
   transaction_id = Data.FourBytes(off);
   off+=4;
   
   off+=2; // Data[8] - reserved 
           // Data[9] - adapationLength 0x00 
   
   message_len = Data.TwoBytes(off);
   off+=2;
   if (message_len > 4076) {
      valid=false;
      return;
   }
   
}

DataHeader::DataHeader() {
   valid=false;
}

void DataHeader::Parse(RawData Data, int &off) {
   protocol = Data[off++];   
   type = Data[off++];
   
   valid = (type == 0x03) && (protocol == 0x11);
   if (!valid)
      return;
      
   message_id = Data.TwoBytes(off);
   off+=2;
   
   download_id = Data.FourBytes(off);
   off+=4;
   
   off++; //skip reserved byte
   
   adaptation_len=Data[off++];
   ASSERT_ASSUMPTION(adaptation_len == 0);
   
   message_len = Data.TwoBytes(off);
   off+=2;
   
   /* TODO adapationHeader ?? */
}
 
InformationBody::InformationBody(MsgHeader *h) {
   header = h;
}

InformationBody::~InformationBody() {
   delete header;
}

//static; returns either a readily parsed DII or DSI, depending on what parsing the MsgHeader unveils
InformationBody *InformationBody::DecideAndParse(RawData data, int &off) {
   MsgHeader *h=new MsgHeader();
   h->Parse(data, off);
   InformationBody *i=0;
   
   if (h->message_id == 0x1006)
      i=new DSI(h);
   else if (h->message_id == 0x1002)
      i=new DII(h);
   else 
      esyslog("InformationBody::DecideAndParse: Unknown message_id %X", h->message_id);
   if (i)
      i->Parse(data, off);
   return i;   
}

DSI::DSI(MsgHeader *h) : InformationBody(h) {
}

void DSI::Parse(RawData data, int &off) {
   off+=22; //skip serverID (20 bytes), compatibilydescriptorlength (2 bytes)
   
   data_len=data.TwoBytes(off);
   off+=2;
   
   info.Parse(data, off);
}

ModuleInfo::ModuleInfo() {
}

void ModuleInfo::Parse(RawData Data, int &off)  {
   module_id = Data.TwoBytes(off);
   off+=2;
   
   module_size = Data.FourBytes(off);
   off+=4;
   
   module_version = Data[off++];
   
   module_info_len = Data[off++];
   
   modinfo.Parse(Data, off);
}

DII::DII(MsgHeader *h) : InformationBody(h) {
}

void DII::Parse(RawData Data, int &off) {
   download_id = Data.FourBytes(off);
   off+=4;
   
   block_size = Data.TwoBytes(off);
   off+=2;
   
   off+=6; // unused fields
   
   tc_download_scenario = Data.FourBytes(off);
   off+=4;
   
   off+=2; //skip unused compatibility descriptor len 
   
   unsigned short number_modules = Data.TwoBytes(off);
   off+=2;
   
   for (int i=0; i<number_modules; i++) {
      ModuleInfo info;
      info.Parse(Data, off);
      modules.push_back(info);
   }
   
   int private_data_len = Data.TwoBytes(off);
   off+=2;
   
   private_data.assign(Data+off, private_data_len);
   off+=private_data_len;
}

void DDB::Parse(RawData Data, int &off) {
   DataHeader::Parse(Data, off);
   
   module_id = Data.TwoBytes(off);
   off+=2;
   
   module_version = Data[off++];
   
   off++; //skip reserved byte
   
   block_number = Data.TwoBytes(off);
   off+=2;
   
   unsigned int len = message_len - 6;
   
   blockdata.assign(Data+off, len); //copy data 
   off+=len;  
}

bool DDB::operator<(const DDB&other) {
   return block_number<other.block_number;
}


}

