#ifndef DSMCC_BIOP_H
#define DSMCC_BIOP_H

#include <string>
#include <list>

#include "util.h"
#include "descriptor.h"

//#define	BIOP_OBJ_OFFSET	11
//#define BIOP_TAG_OFFSET 17

#define ASSERT_ASSUMPTION(x) do { if (!(x)) esyslog("Assumption (" #x ") NOT correct, file %s, line %d!!", __FILE__, __LINE__); } while(0)


namespace Biop {

class MsgHeader : public Parsable, public SmartPtrObject {
public:
   MsgHeader();
   virtual void Parse(RawData data, int &offset);
   
   bool valid;
   
   unsigned char version_major;
   unsigned char version_minor;
   unsigned int message_size;
   CharArray objkey;
   CharArray objkind;
   CharArray objinfo;
};


//A Biop Message, base class for Stream, File, Directory
class Object : public Parsable  {
public:
   Object(MsgHeader *header);
   ~Object();
   static Object * DecideAndParse(RawData data, int &offset);
   SmartPtr<MsgHeader> header;
   enum Type { MessageFile, MessageDirectory, MessageServiceGateway, MessageStream, MessageStreamEvent };
   virtual Type getType() = 0;
protected:
   unsigned long msgbody_len;
};


class Tap : public Parsable {
public:
   Tap();
   enum UseValue { BIOP_DELIVERY_PARA_USE = 0x16, //used in ConnBinder of BiopProfileBody of an IOR
         BIOP_OBJECT_USE = 0x17, //Used in DIIs, not in IOR
         BIOP_ES_USE /* values unknown? */, BIOP_PROGRAM_USE, //Used in Stream objects. Never seen such an object in real life.
         STR_STATUS_AND_EVENT_USE, STR_EVENT_USE, STR_STATUS_USE, STR_NPT_USE }; //Used in Stream and StreamEvent objects. Same as above. 
   virtual void Parse(RawData data, int &offset);
   unsigned short id;
   unsigned short use;
   unsigned short assoc_tag;
   CharArray selector_data;
};

/*class ProgramTap : public Tap {
public:
   ProgramTap();
};

class EsTap : public Tap {
public:
   EsTap();
};*/




class Name : public Parsable {
public:
   Name();
   virtual void Parse(RawData data, int &offset);
   
   class NameComponent : public Parsable {
   public:
      NameComponent();
      virtual void Parse(RawData data, int &offset);
      CharArray id;
      CharArray kind;
   };
   std::list<NameComponent> comps;
};

class ProfileBody;
class Ior : public Parsable {
public:
   Ior();
   ~Ior();
   virtual void Parse(RawData data, int &offset);
   
   CharArray type_id;
   unsigned long tagged_profiles_count;
   unsigned long profile_id_tag;
   SmartPtr<ProfileBody> body;
};

class Binding : public Parsable {
public:
   Binding();
   virtual void Parse(RawData data, int &offset);
   
   Name name;
   char binding_type;
   Ior ior;
   CharArray objinfo;
};


class ObjectLocation : public Parsable {
public:
   ObjectLocation();
   virtual void Parse(RawData data, int &offset);
   
   unsigned long component_tag;
   char component_data_len;
   unsigned long carousel_id;
   unsigned short module_id;
   char version_major;
   char version_minor;
   CharArray objkey;
};

class ConnBinder : public Parsable {
public:
   ConnBinder();
   virtual void Parse(RawData data, int &offset);
   
   unsigned long component_tag;
   unsigned char component_data_len;
   unsigned char taps_count;
   Tap tap;
};

class ProfileBody : public Parsable, public SmartPtrObject {
   //base class only
public:
   virtual bool IsBiopProfile() = 0;
};


class BiopProfileBody : public ProfileBody {
public:
   BiopProfileBody();
   virtual void Parse(RawData data, int &offset);
   
   virtual bool IsBiopProfile() { return true; }
   
   unsigned long data_len;
   char byte_order;
   char lite_components_count;
   ObjectLocation obj_loc;
   ConnBinder dsm_conn;
};

class LiteOptionsProfileBody : public ProfileBody {
public:
   LiteOptionsProfileBody();
   virtual void Parse(RawData data, int &offset);
   virtual bool IsBiopProfile() { return false; }   
   //not implemented!
};

class Stream : public Object {
public:
   Stream(MsgHeader *header);
   virtual void Parse(RawData data, int &offset);
   virtual Type getType() { return Object::MessageStream; }
   
   class Info_T : public Parsable {
     // virtual void Parse(RawData data, int &offset);
      CharArray aDesc;
      unsigned long aSec;
      unsigned long aMicro;
      unsigned char audio;
      unsigned char video;
      unsigned char data;
   };
   //COMPILE_COMMENT TODO Info_T info_t;
   class Context {
   };
   std::list<Context> contexts;
   std::list<Tap> taps;
};

class StreamEvent : public Stream {
public:
   StreamEvent(MsgHeader *header);
   //not implemented
};

class File : public Object {
public:
   File(MsgHeader *header);
   virtual void Parse(RawData data, int &offset);
   virtual Type getType() { return Object::MessageFile; }
   
   unsigned long content_len;
   
   CharArray data;
};

class Directory : public Object {
public:
   Directory(MsgHeader *header);
   virtual void Parse(RawData data, int &offset);
   virtual Type getType() { return Object::MessageDirectory; }
   
   //unsigned int bindings_count;
   std::list<Binding> bindings;
};

class ServiceGateway : public Directory {
public:
   ServiceGateway(MsgHeader *header);
   virtual Type getType() { return Object::MessageServiceGateway; }
   //same as directory; it is simply the base directory.
};



class ModuleInfo : public Parsable {
public:
   ModuleInfo();
   virtual void Parse(RawData data, int &off);
   
   unsigned long mod_timeout;
   unsigned long block_timeout;
   unsigned long min_blocktime;
   unsigned char taps_count;
   Tap tap;
   unsigned char userinfo_len;
   typedef std::list<Dsmcc::Descriptor::Ptr> DescriptorList;
   DescriptorList descriptors;   
};

class ServiceGatewayInfo : public Parsable {
public:
   ServiceGatewayInfo();
   virtual void Parse(RawData data, int &off);
   
   Ior ior;
   unsigned char taps_count;
   //Tap tap;
   unsigned char userinfo_len;
   typedef std::list<Dsmcc::Descriptor::Ptr> DescriptorList;
   DescriptorList descriptors;   
};

}

#endif
