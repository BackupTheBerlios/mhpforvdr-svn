#include <stdio.h>
#include <stdlib.h>

#include "biop.h"


namespace Biop {


MsgHeader::MsgHeader() {
   valid=false;
}

void MsgHeader::Parse(RawData Data, int &off) {
   if(Data[off] !='B' || Data[off+1] !='I' || Data[off+2] !='O' || Data[off+3] !='P')
      return;
   valid=true;
   off+=4;
   
   version_major = Data[off++];
   version_minor = Data[off++];
   
   off+=2; //skip byte order & message type
   
   message_size  = Data.FourBytes(off);
   off+=4;
   
   int objkey_len = Data[off++];
   objkey.assign(Data+off, objkey_len);
   off+=objkey_len;
   
   int objkind_len=Data.FourBytes(off);
   off+=4;
   objkind.assign(Data+off, objkind_len);
   off+=objkind_len;
   
   int objinfo_len = Data.TwoBytes(off);
   off+=2;
   objinfo.assign(Data+off, objinfo_len);
   off+=objinfo_len;
}

Object::Object(MsgHeader *h) 
: header(h) {
}

Object::~Object() {
   //delete header;
}

Object *Object::DecideAndParse(RawData data, int &off) {
   MsgHeader *h=new MsgHeader();
   h->Parse(data, off);
   
   Object *o=0;
      
   if (h->objkind == "fil") {
      o=new File(h);
   } else if (h->objkind == "dir") {
      o=new Directory(h);
   } else if (h->objkind == "srg") {
      o=new ServiceGateway(h);
   } else if (h->objkind == "str") {
      o=new Stream(h);
   } else if (h->objkind == "str") {
      //o=new StreamEvent(h);
   }
   
   if (o && h->valid)
      o->Parse(data, off);
   return o;
}

File::File(MsgHeader *h) : Object(h) {
}

void File::Parse(RawData Data, int &off) {
   ASSERT_ASSUMPTION(Data[off] == 0);
   off++; //skip service context count
   
   msgbody_len = Data.FourBytes(off);
   off+=4;
   
   content_len = Data.FourBytes(off);
   off+=4;
   
   //Optimization: Points to buffer supplied for Parse(). No alloc, no memcpy!
   //If requirements change, use a CharArray instead.
   data.assign(Data+off, content_len);
   off+=content_len;
}

Directory::Directory(MsgHeader *h) : Object(h) {
}

void Directory::Parse(RawData Data, int &off) {
   ASSERT_ASSUMPTION(Data[off] == 0);
   off++; //skip service context count
   
   msgbody_len = Data.FourBytes(off);
   off+=4;
   
   unsigned int bindings_count = Data.TwoBytes(off);
   off+=2;
   
   for (uint i=0;i<bindings_count;i++) {
      Binding bind;
      bind.Parse(Data, off);
      bindings.push_back(bind);
   }
}

Stream::Stream(MsgHeader *h) : Object(h) {
}

void Stream::Parse(RawData data, int &offset) {
   printf("Stream::Parse: Implement me!\n");
}

ServiceGateway::ServiceGateway(MsgHeader *h) : Directory(h) {
}

StreamEvent::StreamEvent(MsgHeader *h) : Stream(h) {
}

   
Tap::Tap() {
}

void Tap::Parse(RawData Data, int &off) {
   id = Data.TwoBytes(off);
   off+=2;
   
   use = Data.TwoBytes(off);
   off+=2;
   
   assoc_tag = Data.TwoBytes(off);
   off+=2;
   
   int selector_len = Data[off++];
   selector_data.assign(Data+off, selector_len);
   off+=selector_len;
}



Binding::Binding() {
}

void Binding::Parse(RawData data, int &off) {
   name.Parse(data, off);
   
   binding_type=data[off++];
   
   ior.Parse(data, off);
   
   int objinfo_len = data.TwoBytes(off);
   off+=2;
   objinfo.assign(data+off, objinfo_len);
   off+=objinfo_len;
}

Name::Name() {
}

void Name::Parse(RawData data, int &off) {
   int comp_count = data[off++];
   for (int i=0;i<comp_count;i++) {
      NameComponent comp;
      comp.Parse(data, off);
      comps.push_back(comp);
   }
}

Name::NameComponent::NameComponent() {
}

void Name::NameComponent::Parse(RawData data, int &off) {
   int id_len = data[off++];
   id.assign(data+off, id_len);
   off+=id_len;
   
   int kind_len=data[off++];
   kind.assign(data+off, kind_len);
   off+=kind_len;
}

Ior::Ior() 
: body(0) 
{
}

Ior::~Ior() {
   //delete body;
}

void Ior::Parse(RawData data, int &off) {
   int type_id_len = data.FourBytes(off);
   off+=4;
   
   type_id.assign(data+off, type_id_len);
   off+=type_id_len;
   
   tagged_profiles_count = data.FourBytes(off);
   off+=4;
   
   profile_id_tag = data.FourBytes(off);
   off+=4;
   
   switch (profile_id_tag) {
   case 0x49534F06:
      body=new BiopProfileBody();
      body->Parse(data, off);
      break;
   case 0x49534F05:
      body=new LiteOptionsProfileBody();
      body->Parse(data, off);
      break;
   }
}

BiopProfileBody::BiopProfileBody() {
}

void BiopProfileBody::Parse(RawData data, int &off) {
   data_len = data.FourBytes(off);
   off+=4;
   
   off++; //skip bit order
   
   lite_components_count = data[off++];
   ASSERT_ASSUMPTION(lite_components_count == 2);
   
   obj_loc.Parse(data, off);
   dsm_conn.Parse(data, off);
}

LiteOptionsProfileBody::LiteOptionsProfileBody() {
}

void LiteOptionsProfileBody::Parse(RawData data, int &offset) {
	esyslog("BiopLite - Not Implemented Yet");
}

ConnBinder::ConnBinder() {
}

void ConnBinder::Parse(RawData data, int &off) {
   component_tag = data.FourBytes(off);
   off+=4;
   
   component_data_len = data[off++];
   
   taps_count = data[off++];
   ASSERT_ASSUMPTION(taps_count==1);
   
   tap.Parse(data, off);
}

ObjectLocation::ObjectLocation() {
}

void ObjectLocation::Parse(RawData data, int &off) {
   component_tag = data.FourBytes(off);
   off+=4;
   
   component_data_len = data[off++];
   
   carousel_id = data.FourBytes(off);
   off+=4;
   
   module_id = data.TwoBytes(off);
   off+=2;
   
   version_major = data[off++];
   version_minor = data[off++];
   
   int objkey_len = data[off++];
   objkey.assign(data+off, objkey_len);
   off+=objkey_len;
}

ModuleInfo::ModuleInfo() {
}

void ModuleInfo::Parse(RawData data, int &off) {
   mod_timeout =  data.FourBytes(off);
   off+=4;
   
   block_timeout =  data.FourBytes(off);
   off+=4;
   
   min_blocktime =  data.FourBytes(off);
   off+=4;
   
   taps_count = data[off++];
   ASSERT_ASSUMPTION(taps_count<=1);
   
   if (taps_count)
      tap.Parse(data, off);
   
   userinfo_len = data[off++];
   
   int curp=0;
   Dsmcc::Descriptor *d=0;
   while (userinfo_len > curp) {
      d=Dsmcc::Descriptor::DecideAndParse(data+off, curp);
      if (d)
         descriptors.push_back(Dsmcc::Descriptor::Ptr(d));
   }
   off+=curp;
}

ServiceGatewayInfo::ServiceGatewayInfo() {
}

void ServiceGatewayInfo::Parse(RawData data, int &off) {
   ior.Parse(data, off);
   taps_count = data[off++];
   ASSERT_ASSUMPTION(taps_count==0);
   
   //tap.Parse(data, off);
   int contextListCount = data[off++];
   ASSERT_ASSUMPTION(contextListCount==0);
   
   userinfo_len = data[off++];
   
   int curp=0;
   Dsmcc::Descriptor *d=0;
   while (userinfo_len > curp) {
      d=Dsmcc::Descriptor::DecideAndParse(data+off+curp, curp);
      if (d)
         descriptors.push_back(Dsmcc::Descriptor::Ptr(d));
   }
   off+=curp;
}




}


