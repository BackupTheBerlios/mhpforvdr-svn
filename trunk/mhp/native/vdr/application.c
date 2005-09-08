
#include <queue>
#include <string.h>

#include <vdr/thread.h>

#include <mhploading.h>
#include <libait/applications.h>
#include <libdsmccreceiver/cache.h>
#include <libjava/jniinterface.h>
#include <libjava/nativewrappertypes.h>

extern "C" {

//tools
jbyteArray copyConstCharIntoByteArray(JNIEnv* env, const char *str) {
   int len=strlen(str);
   jbyteArray ar=env->NewByteArray(len);
   if (len)
      //only copy len chars, do _not_ include the trailing null character
      env->SetByteArrayRegion(ar, 0, len, (jbyte*)str);
   return ar;
}


/*
   cApplications are passed around as SmartPtrs. They cannot be passed to Java, only pointers can.
   When a native pointer is passed to Java, it it a pointer to a SmartPtr.
   Currently, these pointer (created in this file in getApplicationPointer, and in libjava/javainterface.c)
   are _not_ deleted, which means they and the wrapped objects are leaked. This is not critical,
   but a low priority TODO for the future.
*/

//MHPApplication
jint Java_org_dvb_application_MHPApplication_getAid(JNIEnv* env, jclass clazz, jobject nativeData) {
   return NativeApplicationData(nativeData).Get()->GetAid();
}

jint Java_org_dvb_application_MHPApplication_getOid(JNIEnv* env, jclass clazz, jobject nativeData) {
   return NativeApplicationData(nativeData).Get()->GetOid();
}

jstring Java_org_dvb_application_MHPApplication_carouselRoot(JNIEnv* env, jobject obj, jobject nativeData) {
   ApplicationInfo::cTransportProtocol *tp=NativeApplicationData(nativeData).Get()->GetTransportProtocol();
   if (tp->HasFileSystemRepresentation())
      return JNI::String(tp->GetFileSystemRepresentation()).toJavaString();
   return env->NewByteArray(0);
}

jboolean Java_org_dvb_application_MHPApplication_isServiceBound(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeApplicationData(nativeData).Get()->GetServiceBound();
}

jlong Java_org_dvb_application_MHPApplication_channel(JNIEnv* env, jobject obj, jobject nativeData) {
   //TODO
   //NativeChannelData chan(nativeChannel);
   return (jlong)NativeApplicationData(nativeData).Get()->GetChannel();
}

jstring Java_org_dvb_application_MHPApplication_name(JNIEnv* env, jobject obj, jobject nativeData) {
   NativeApplicationData app(nativeData);
   int num=app.Get()->GetNumberOfNames();
   const char *name;
   if (num)
      name=app.Get()->GetName(0)->name.c_str();
   else
      name="Unknown";
   return JNI::String(name).toJavaString();
}

jstring Java_org_dvb_application_MHPApplication_nameForLanguage(JNIEnv* env, jobject obj, jobject nativeData, jstring iso639code) {
   NativeApplicationData app(nativeData);
   int num=app.Get()->GetNumberOfNames();
   JNI::String code(iso639code);
   for (int i=0;i<num;i++) {
      ApplicationInfo::cApplication::ApplicationName *name=app.Get()->GetName(i);
      if (strcasecmp(name->iso639Code, code.toCString()) == 0)
         return JNI::String(app.Get()->GetName(i)->name.c_str()).toJavaString();
   }
   return NULL; //exception will be thrown
}

jint Java_org_dvb_application_MHPApplication_priority(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeApplicationData(nativeData).Get()->GetPriority();
}

jint Java_org_dvb_application_MHPApplication_componentTag(JNIEnv* env, jobject obj, jobject nativeData, jstring index) {
   ApplicationInfo::cTransportProtocolViaOC *tp=dynamic_cast<ApplicationInfo::cTransportProtocolViaOC*>(NativeApplicationData(nativeData).Get()->GetTransportProtocol());
   if (tp)
      return tp->GetComponentTag();
   return 0;
}

jint Java_org_dvb_application_MHPApplication_type(JNIEnv* env, jclass clazz, jobject nativeData) {
   return (int)NativeApplicationData(nativeData).Get()->GetApplicationType();
}

jboolean Java_org_dvb_application_MHPApplication_startable(JNIEnv* env, jobject obj, jobject nativeData) {
   //TODO
   return true;
}




//DVBJApplication
jstring Java_org_dvb_application_DVBJApplication_baseDir(JNIEnv* env, jobject obj, jobject nativeData) {
   return JNI::String(NativeApplicationData(nativeData).Get()->GetBaseDir()).toJavaString();
}

jstring Java_org_dvb_application_DVBJApplication_classPath(JNIEnv* env, jobject obj, jobject nativeData) {
   return JNI::String(NativeApplicationData(nativeData).Get()->GetClassPath()).toJavaString();
}

jstring Java_org_dvb_application_DVBJApplication_initialClass(JNIEnv* env, jobject obj, jobject nativeData) {
   return JNI::String(NativeApplicationData(nativeData).Get()->GetInitialClass()).toJavaString();
}

jobject Java_org_dvb_application_DVBJApplication_getApplicationPointer(JNIEnv* env, jclass clazz, jobject nativeDBData, jint oid, jint aid) {
   NativeDBData db(nativeDBData);
   ApplicationInfo::cApplication::Ptr ptr = db.Get()->findApplication(aid, oid, ApplicationInfo::cApplication::DVBJApplication);
   return (jobject)NativeApplicationData(ptr, !ptr);
}

jint Java_org_dvb_application_DVBJApplication_numberOfParameters(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeApplicationData(nativeData).Get()->GetNumberOfParameters();
}

jstring Java_org_dvb_application_DVBJApplication_parameter(JNIEnv* env, jobject obj, jobject nativeData, jint index) {
   return JNI::String(NativeApplicationData(nativeData).Get()->GetParameter(index)).toJavaString();
}


//AppsDatabase
jint Java_org_dvb_application_AppsDatabase_getSize(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeDBData(nativeData).Get()->Count();
}


//ApplicationManager$LoadingManagerInterface
//The $, unicode 0x24, is mangled to _00024
void Java_vdr_mhp_ApplicationManager_00024LoadingManagerInterface_load(JNIEnv* env, jclass clazz, jobject nativeData) {
   Mhp::LoadingManager::getManager()->Load(NativeApplicationData(nativeData));
}

void Java_vdr_mhp_ApplicationManager_00024LoadingManagerInterface_stop(JNIEnv* env, jclass clazz, jobject nativeData) {
   Mhp::LoadingManager::getManager()->Stop(NativeApplicationData(nativeData));
}

jboolean Java_vdr_mhp_ApplicationManager_00024LoadingManagerInterface_isAcquired(JNIEnv* env, jclass clazz, jobject nativeData) {
   return Mhp::LoadingManager::getManager()->getState(NativeApplicationData(nativeData)) == Mhp::LoadingStateLoaded;
}

//the standard is not very explicit about the mangling of JNI methods, but this is definitely the way to go.
//However, some VMs may be slightly broken...
void Java_vdr_mhp_ApplicationManager_$LoadingManagerInterface_load(JNIEnv* env, jclass clazz, jobject nativeData) {
   return Java_vdr_mhp_ApplicationManager_00024LoadingManagerInterface_load(env, clazz, nativeData);
}
void Java_vdr_mhp_ApplicationManager_$LoadingManagerInterface_stop(JNIEnv* env, jclass clazz, jobject nativeData) {
   return Java_vdr_mhp_ApplicationManager_00024LoadingManagerInterface_stop(env, clazz, nativeData);
}
jboolean Java_vdr_mhp_ApplicationManager_$LoadingManagerInterface_isAcquired(JNIEnv* env, jclass clazz, jobject nativeData) {
   return Java_vdr_mhp_ApplicationManager_00024LoadingManagerInterface_isAcquired(env, clazz, nativeData);
}

//ApplicationManager$RunningManagerInterface
void Java_vdr_mhp_ApplicationManager_00024RunningManagerInterface_started(JNIEnv* env, jclass clazz, jobject nativeData) {
   Mhp::RunningManager::getManager()->ApplicationStarted(NativeApplicationData(nativeData));
}

void Java_vdr_mhp_ApplicationManager_00024RunningManagerInterface_stopped(JNIEnv* env, jclass clazz, jobject nativeData) {
   Mhp::RunningManager::getManager()->ApplicationStopped(NativeApplicationData(nativeData));
}

void Java_vdr_mhp_ApplicationManager_$RunningManagerInterface_started(JNIEnv* env, jclass clazz, jobject nativeData) {
   return Java_vdr_mhp_ApplicationManager_00024RunningManagerInterface_started(env, clazz, nativeData);
}

void Java_vdr_mhp_ApplicationManager_$RunningManagerInterface_stopped(JNIEnv* env, jclass clazz, jobject nativeData) {
   return Java_vdr_mhp_ApplicationManager_00024RunningManagerInterface_stopped(env, clazz, nativeData);
}


//DSMCCObject

struct DSMCCEvent {
public:
   JNI::GlobalObjectRef dsmccObject;
   int versionNumber;
};

class DSMCCObjectListener : public Cache::CacheListener {
public:
   DSMCCObjectListener() : cache(0) {}
   virtual ~DSMCCObjectListener() {}
   virtual void objectChanged(Cache::CacheObject::Ptr ptr);
   SmartPtr<Cache::Cache> cache; 
   JNI::GlobalObjectRef dsmccObject;
};

class DSMCCEvents {
public:
   static DSMCCEvents *self();
   void append(DSMCCEvent *event);
   DSMCCEvent *getNext();
   cMutex mutex;
   cCondVar condVar;
   std::queue<DSMCCEvent *> events;
protected:
   DSMCCEvents();
   static DSMCCEvents *s_self;
};



DSMCCEvents *DSMCCEvents::s_self = 0;

DSMCCEvents::DSMCCEvents() {
}

DSMCCEvents *DSMCCEvents::self() {
   if (!s_self)
      s_self=new DSMCCEvents();
   return s_self;
}

void DSMCCEvents::append(DSMCCEvent *event) {
   cMutexLock lock(&mutex);
   events.push(event);
}

DSMCCEvent *DSMCCEvents::getNext() {
   cMutexLock lock(&mutex);
   
   if (events.empty())
      condVar.TimedWait(mutex, 2000);
      
   if (!events.empty()) {
      DSMCCEvent *ret=events.front();
      events.pop();
      return ret;
   } else
      return 0;
}

void DSMCCObjectListener::objectChanged(Cache::CacheObject::Ptr ptr) {
   DSMCCEvent *event=new DSMCCEvent;
   event->versionNumber=ptr->version;
   event->dsmccObject=dsmccObject;
   DSMCCEvents::self()->append(event);
}

jlong Java_org_dvb_dsmcc_DSMCCObject_createListener(JNIEnv* env, jobject obj, jobject nativeApp, jstring java_path) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   JNI::String path(java_path);
   
   DSMCCObjectListener *l=new DSMCCObjectListener();
   l->dsmccObject.SetObject(obj);
   l->cache=Mhp::LoadingManager::getManager()->getCache(NativeApplicationData(nativeApp).Get());
   
   if (l->cache)
      l->cache->addListener(path.toCString(), l);
   else
      printf("Java_org_dvb_dsmcc_DSMCCObject_createListener: Cache is null. This is all right for local applications.\n");
      
   return (jlong)l;
}

void Java_org_dvb_dsmcc_DSMCCObject_removeListener(JNIEnv* env, jobject obj, jlong nativeData) {
   DSMCCObjectListener *l=(DSMCCObjectListener *)nativeData;
   l->cache->removeListener(l);
   delete l;
}


jlong Java_org_dvb_dsmcc_DSMCCObject_00024EventThread_nextEvent(JNIEnv* env, jclass clazz) {
   return (jlong)DSMCCEvents::self()->getNext();
}

jint Java_org_dvb_dsmcc_DSMCCObject_00024EventThread_getVersion(JNIEnv* env, jclass clazz, jlong nativeEvent) {
   return ((DSMCCEvent *)nativeEvent)->versionNumber;
}

jobject Java_org_dvb_dsmcc_DSMCCObject_00024EventThread_getRef(JNIEnv* env, jclass clazz, jlong nativeEvent) {
   return ((DSMCCEvent *)nativeEvent)->dsmccObject;
}

void Java_org_dvb_dsmcc_DSMCCObject_00024EventThread_deleteEvent(JNIEnv* env, jclass clazz, jlong nativeEvent) {
   delete ((DSMCCEvent *)nativeEvent);
}

//$ -> 00024
jlong Java_org_dvb_dsmcc_DSMCCObject_$EventThread_nextEvent(JNIEnv* env, jclass clazz) {
   return Java_org_dvb_dsmcc_DSMCCObject_00024EventThread_nextEvent(env, clazz);
}

jint Java_org_dvb_dsmcc_DSMCCObject_$EventThread_getVersion(JNIEnv* env, jclass clazz, jlong nativeEvent) {
   return Java_org_dvb_dsmcc_DSMCCObject_00024EventThread_getVersion(env, clazz, nativeEvent);
}

jobject Java_org_dvb_dsmcc_DSMCCObject_$EventThread_getRef(JNIEnv* env, jclass clazz, jlong nativeEvent) {
   return Java_org_dvb_dsmcc_DSMCCObject_00024EventThread_getRef(env, clazz, nativeEvent);
}

void Java_org_dvb_dsmcc_DSMCCObject_$EventThread_deleteEvent(JNIEnv* env, jclass clazz, jlong nativeEvent) {
   return Java_org_dvb_dsmcc_DSMCCObject_00024EventThread_deleteEvent(env, clazz, nativeEvent);
}

}
