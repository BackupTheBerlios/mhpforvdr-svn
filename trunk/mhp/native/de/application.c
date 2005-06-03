
#include <queue>
#include <string.h>

#include <vdr/thread.h>

#include <mhploading.h>
#include <libait/applications.h>
#include <libdsmccreceiver/cache.h>
#include <libjava/jniinterface.h>

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


//MHPApplication
jint Java_org_dvb_application_MHPApplication_getAid(JNIEnv* env, jclass clazz, jlong nativeData) {
   return ((ApplicationInfo::cApplication *)nativeData)->GetAid();
}

jint Java_org_dvb_application_MHPApplication_getOid(JNIEnv* env, jclass clazz, jlong nativeData) {
   return ((ApplicationInfo::cApplication *)nativeData)->GetOid();
}

jbyteArray Java_org_dvb_application_MHPApplication_carouselRoot(JNIEnv* env, jobject obj, jlong nativeData) {
   ApplicationInfo::cTransportProtocol *tp=((ApplicationInfo::cApplication *)nativeData)->GetTransportProtocol();
   if (tp->HasFileSystemRepresentation())
      return copyConstCharIntoByteArray(env, tp->GetFileSystemRepresentation());
   return env->NewByteArray(0);
}

jboolean Java_org_dvb_application_MHPApplication_isServiceBound(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((ApplicationInfo::cApplication *)nativeData)->GetServiceBound();
}

jlong Java_org_dvb_application_MHPApplication_channel(JNIEnv* env, jobject obj, jlong nativeData) {
   return (jlong )((ApplicationInfo::cApplication *)nativeData)->GetChannel();
}

jbyteArray Java_org_dvb_application_MHPApplication_name(JNIEnv* env, jobject obj, jlong nativeData) {
   int num=((ApplicationInfo::cApplication *)nativeData)->GetNumberOfNames();
   if (num)
      return copyConstCharIntoByteArray(env, ((ApplicationInfo::cApplication *)nativeData)->GetName(0)->name.c_str()); //return first name
   return copyConstCharIntoByteArray(env, "Unknown");
}

jbyteArray Java_org_dvb_application_MHPApplication_nameForLanguage(JNIEnv* env, jobject obj, jlong nativeData, jbyteArray iso639code) {
   int num=((ApplicationInfo::cApplication *)nativeData)->GetNumberOfNames();
   for (int i=0;i<num;i++) {
      ApplicationInfo::cApplication::ApplicationName *name=((ApplicationInfo::cApplication *)nativeData)->GetName(i);
      if (strcasecmp(name->iso639Code, (const char*)iso639code) == 0)
         return copyConstCharIntoByteArray(env, ((ApplicationInfo::cApplication *)nativeData)->GetName(i)->name.c_str());
   }
   return NULL; //exception will be thrown
}

jint Java_org_dvb_application_MHPApplication_priority(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((ApplicationInfo::cApplication *)nativeData)->GetPriority();
}

jint Java_org_dvb_application_MHPApplication_componentTag(JNIEnv* env, jobject obj, jlong nativeData, jstring index) {
   ApplicationInfo::cTransportProtocolViaOC *tp=dynamic_cast<ApplicationInfo::cTransportProtocolViaOC*>(((ApplicationInfo::cApplication *)nativeData)->GetTransportProtocol());
   if (tp)
      return tp->GetComponentTag();
   return 0;
}

jint Java_org_dvb_application_MHPApplication_type(JNIEnv* env, jclass clazz, jlong nativeData) {
   return (int)((ApplicationInfo::cApplication *)nativeData)->GetApplicationType();
}

jboolean Java_org_dvb_application_MHPApplication_startable(JNIEnv* env, jobject obj, jlong nativeData) {
   //TODO
   return true;
}




//DVBJApplication
jbyteArray Java_org_dvb_application_DVBJApplication_baseDir(JNIEnv* env, jobject obj, jlong nativeData) {
   return copyConstCharIntoByteArray(env, ((ApplicationInfo::cApplication *)nativeData)->GetBaseDir());
}

jbyteArray Java_org_dvb_application_DVBJApplication_classPath(JNIEnv* env, jobject obj, jlong nativeData) {
   return copyConstCharIntoByteArray(env, ((ApplicationInfo::cApplication *)nativeData)->GetClassPath());
}

jbyteArray Java_org_dvb_application_DVBJApplication_initialClass(JNIEnv* env, jobject obj, jlong nativeData) {
   return copyConstCharIntoByteArray(env, ((ApplicationInfo::cApplication *)nativeData)->GetInitialClass());
}

jlong Java_org_dvb_application_DVBJApplication_getApplicationPointer(JNIEnv* env, jclass clazz, jlong nativeData, jint oid, jint aid) {
   return (jlong )((ApplicationInfo::cApplicationsDatabase*)nativeData)->findApplication(aid, oid, ApplicationInfo::cApplication::DVBJApplication);
}

jint Java_org_dvb_application_DVBJApplication_numberOfParameters(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((ApplicationInfo::cApplication *)nativeData)->GetNumberOfParameters();
}

jbyteArray Java_org_dvb_application_DVBJApplication_parameter(JNIEnv* env, jobject obj, jlong nativeData, jint index) {
   return copyConstCharIntoByteArray(env, ((ApplicationInfo::cApplication *)nativeData)->GetParameter(index));
}


//AppsDatabase
jint Java_org_dvb_application_AppsDatabase_getSize(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((ApplicationInfo::cApplicationsDatabase *)nativeData)->Count();
}


//ApplicationManager$LoadingManagerInterface
//The $, unicode 0x24, is mangled to _00024
void Java_vdr_mhp_ApplicationManager_00024LoadingManagerInterface_load(JNIEnv* env, jclass clazz, jlong nativeData) {
   MhpLoadingManager::getManager()->Load((ApplicationInfo::cApplication *)nativeData);
}

void Java_vdr_mhp_ApplicationManager_00024LoadingManagerInterface_stop(JNIEnv* env, jclass clazz, jlong nativeData) {
   MhpLoadingManager::getManager()->Stop((ApplicationInfo::cApplication *)nativeData);
}

jboolean Java_vdr_mhp_ApplicationManager_00024LoadingManagerInterface_isAcquired(JNIEnv* env, jclass clazz, jlong nativeData) {
   return MhpLoadingManager::getManager()->getState((ApplicationInfo::cApplication *)nativeData) == LoadingStateLoaded;
}

//the standard is not very explicit about the mangling of JNI methods, but this is definitely the way to go.
//However, some VMs may be slightly broken...
void Java_vdr_mhp_ApplicationManager_$LoadingManagerInterface_load(JNIEnv* env, jclass clazz, jlong nativeData) {
   return Java_vdr_mhp_ApplicationManager_00024LoadingManagerInterface_load(env, clazz, nativeData);
}
void Java_vdr_mhp_ApplicationManager_$LoadingManagerInterface_stop(JNIEnv* env, jclass clazz, jlong nativeData) {
   return Java_vdr_mhp_ApplicationManager_00024LoadingManagerInterface_stop(env, clazz, nativeData);
}
jboolean Java_vdr_mhp_ApplicationManager_$LoadingManagerInterface_isAcquired(JNIEnv* env, jclass clazz, jlong nativeData) {
   return Java_vdr_mhp_ApplicationManager_00024LoadingManagerInterface_isAcquired(env, clazz, nativeData);
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

jlong Java_org_dvb_dsmcc_DSMCCObject_createListener(JNIEnv* env, jobject obj, jlong nativeApp, jbyteArray pathAr) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   const char *path=(const char *)env->GetByteArrayElements(pathAr, 0);
   
   DSMCCObjectListener *l=new DSMCCObjectListener();
   l->dsmccObject.SetObject(obj);
   l->cache=MhpLoadingManager::getManager()->getCache((ApplicationInfo::cApplication *)nativeApp);
   
   if (l->cache)
      l->cache->addListener(path, l);
   else
      printf("Java_org_dvb_dsmcc_DSMCCObject_createListener: Cache is null!\n");
      
   env->ReleaseByteArrayElements(pathAr, (jbyte *)path, JNI_ABORT);
   
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
