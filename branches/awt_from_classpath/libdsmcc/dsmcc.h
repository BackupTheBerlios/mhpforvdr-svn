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

#ifndef LIBDSMCC_DSMCC_H
#define LIBDSMCC_DSMCC_H

#include "util.h"
#include "biop.h"



#define DSMCC_SYNC_BYTE		0x47
#define DSMCC_TRANSPORT_ERROR	0x80
#define DSMCC_START_INDICATOR	0x40

#define DSMCC_MESSAGE_DSI	0x1006
#define DSMCC_MESSAGE_DII	0x1002
#define DSMCC_MESSAGE_DDB	0x1003

#define DSMCC_SECTION_INDICATION	0x3B
#define DSMCC_SECTION_DATA		0x3C
#define DSMCC_SECTION_DESCR		0x3D


#define BLOCK_GOT(s,i)	(s[i/8]& (1<<(i%8)))
#define BLOCK_SET(s,i)  (s[i/8]|=(1<<(i%8)))

class cDsmccReceiver;
struct stream;

namespace Dsmcc {

/* -------------- Classes used for Parsing (inherit Parsable) --------------------*/


class Header : virtual public Parsable {
public:
   Header(int length);
   virtual void Parse(RawData Data, int &off);
   
   bool valid; //CRC and content
   int Length;
   
   char table_id; //always 0x3B
   unsigned char flags[2];
   unsigned short table_id_extension;
   /*
   *  unsigned int section_syntax_indicator : 1; UKProfile - always 1
   *  unsigned int private_indicator : 1;  UKProfile - hence always 0
   *  unsigned int reserved : 2;  always 11b
   *  unsigned int dsmcc_section_length : 12;
   **/
   unsigned char flags2;
   /*
   *  unsigned int reserved : 2;  always 11b
   *  unsigned int version_number : 5;  00000b
   *  unsigned int current_next_indicator : 1  1b
   * */
   unsigned long crc;
};

class Body : virtual public Parsable {
//base class only, inherited by DSI, DII, DDB
public:
   Body() {}
   enum Type { MessageDSI, MessageDII, MessageDDB };
   virtual Type getType() = 0;
   virtual bool isValid() = 0;
};


class Section : virtual public Parsable, protected Header {
public:
   Section(int length);
   ~Section();
   virtual void Parse(RawData Data, int &off);
   //do not delete body upon destruction
   void StealBody() { bodyStolen=true; }
   //returns true if SectionHeader is valid, a body exists (DSI, DII, DDB), and the body's header is valid
   bool isValid() { return Header::valid && (body ? body->isValid() : false); }
   
   Body *body;
private:
   bool bodyStolen;
};

class MessageHeader : virtual public Parsable {
//base class only, inherited by MsgHeader and DataHeader
public:
   bool valid; //content valid (protocol...)
   
   unsigned char protocol; //0x11
   unsigned char type;        // 0x03 U-N  unsigned char protocol;
   unsigned short message_id; //Msg 0x1002, Data 0x1003
};

class MsgHeader : public MessageHeader {
public:
   MsgHeader();
   virtual void Parse(RawData Data, int &off);
   
   unsigned long transaction_id;
        /* transactionID
    * unsigned int orig_subfield : 2;
    * unsigned int version_subfield : 14;
    * unsigned int id_subfield : 15;
    * unsigned int update_subfield : 1;
    */
   unsigned short message_len;
};

class DataHeader : public MessageHeader {
public:
   DataHeader();
   virtual void Parse(RawData Data, int &off);
   
   unsigned long download_id;
   char adaptation_len;/* 0x00 or 0x08 */
   unsigned short message_len;
};

class InformationBody : public Body {
//base class for DSI and DII
//reason: The MsgHeader must be parsed before we can decide
//        if it is a DSI or DII
public:
   InformationBody(MsgHeader *header);
   ~InformationBody();
   MsgHeader *header;
   //returns either a readily parsed DII or DSI, depending on what parsing the MsgHeader unveils
   static InformationBody *DecideAndParse(RawData Data, int &off);
   virtual bool isValid() { return header && header->valid; }
};

class DSI : public InformationBody {
public:
   DSI(MsgHeader *header);
   virtual void Parse(RawData Data, int &off);
   virtual Type getType() { return Body::MessageDSI; }
   
   unsigned short data_len;
   //unsigned short num_groups;
   Biop::ServiceGatewayInfo info;
   //Biop::Ior profile;
   //CharArray user_data;
};

class ModuleInfo : public Parsable {
public:
   ModuleInfo();
   virtual void Parse(RawData Data, int &off);
   
   unsigned short module_id;
   unsigned long  module_size;
   unsigned char module_version;
   unsigned char module_info_len;
   Biop::ModuleInfo modinfo;
   unsigned char *data;
   //unsigned int curp;
};

class DII : public InformationBody {
public:
   DII(MsgHeader *header);
   virtual void Parse(RawData Data, int &off);
   virtual Type getType() { return Body::MessageDII; }

   unsigned long download_id;
   unsigned short block_size;
   unsigned long tc_download_scenario;
   std::list<ModuleInfo> modules;
   CharArray private_data;
};

class DDB : public Body, protected DataHeader {
public:
   virtual void Parse(RawData Data, int &off);
   virtual bool isValid() { return MessageHeader::valid; }
   virtual Type getType() { return Body::MessageDDB; }
   
   bool operator<(const DDB&other);

   unsigned short module_id;
   unsigned char module_version;
   unsigned short block_number;
   unsigned long getDownload_id() { return download_id; }
   CharArray blockdata;
};


}


#endif

