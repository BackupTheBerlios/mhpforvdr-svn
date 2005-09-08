#include <libjava/jniinterface.h>
#include <vdr/channels.h>
#include <vdr/device.h>
#include <libdvbsi/database.h>
#include <libservice/servicecontext.h>

extern "C" {

jbyteArray copyConstCharIntoByteArray(JNIEnv* env, const char *str);


/*--- javax.tv.service.VDRService ---*/

jbyteArray Java_javax_tv_service_VDRService_name(JNIEnv* env, jobject obj, jlong nativeData) {
   return copyConstCharIntoByteArray(env, ((cChannel*)nativeData)->Name());
}

jint Java_javax_tv_service_VDRService_onid(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((cChannel*)nativeData)->Nid();
}

jint Java_javax_tv_service_VDRService_tid(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((cChannel*)nativeData)->Tid();
}

jint Java_javax_tv_service_VDRService_sid(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((cChannel*)nativeData)->Sid();
}

jint Java_javax_tv_service_VDRService_serviceNumber(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((cChannel*)nativeData)->Number();
}

jboolean Java_javax_tv_service_VDRService_isRadio(JNIEnv* env, jobject obj, jlong nativeData) {
   //simply checks if video pid is 0
   return ((cChannel*)nativeData)->Vpid()==0;
}

//static
jlong Java_javax_tv_service_VDRService_getCurrentChannelNative(JNIEnv* env, jclass clazz) {
   return (jlong )Channels.GetByNumber(cDevice::CurrentChannel());
}

jlong Java_javax_tv_service_VDRService_getServiceForChannelId(JNIEnv* env, jclass clazz, jint source, jint onid, jint tid, jint sid) {
   tChannelID chid(source, onid, tid, sid);
   return (jlong )Channels.GetByChannelID(chid);
}

jlong Java_javax_tv_service_VDRService_getServiceForNidTidSid(JNIEnv* env, jclass clazz, jint onid, jint tid, jint sid) {
   //don't even think about using a VDR cList for cChannel *
   //that would destroy Channels :-)
   std::vector<cChannel *> list;
   DvbSi::Database::findChannels(onid, tid, sid, list);
   
   return (jlong)list.front();
}


/*--- javax.tv.service.navigation.VDRServiceList ---*/

jboolean Java_javax_tv_service_navigation_VDRServiceList_acquireLock(JNIEnv* env, jobject obj) {
   return Channels.Lock(false, 1000);
}

void Java_javax_tv_service_navigation_VDRServiceList_releaseLock(JNIEnv* env, jobject obj) {
   return Channels.Unlock();
}

jlong Java_javax_tv_service_navigation_VDRServiceList_firstChannel(JNIEnv* env, jobject obj) {
   return (jlong)Channels.First();
}

jlong Java_javax_tv_service_navigation_VDRServiceList_nextChannel(JNIEnv* env, jobject obj, jlong previousChannel) {
   return (jlong)((cChannel *)previousChannel)->Next();
}


/*--- javax.tv.service.selection.VDRServiceContext ---*/

class VDRServiceStatus : public Service::ServiceStatus {
public:
   VDRServiceStatus(jclass clazz) {
      callback.SetMethodWithArguments(clazz, "nativeServiceEvent", JNI::Void, 1, JNI::Int);
   }
protected:
   virtual void ServiceEvent(Message event, Service::Service service)
   {
      JNI::ReturnType type;
      callback.CallMethod(type, (jint)event);
   }
private:
   static JNI::StaticMethod callback;
};

JNI::StaticMethod VDRServiceStatus::callback;
VDRServiceStatus *status=0;

void Java_javax_tv_service_selection_VDRServiceContext_initializeStatus(JNIEnv* env, jclass clazz) {
   status=new VDRServiceStatus(clazz);
}

jboolean Java_javax_tv_service_selection_VDRServiceContext_isPresenting(JNIEnv* env, jobject obj) {
   return Service::Context::getContext()->isPresenting();
}

void Java_javax_tv_service_selection_VDRServiceContext_doSelect(JNIEnv* env, jobject obj, jlong nativeData) {
   Service::Context::getContext()->SelectService((cChannel *)nativeData);
}

void Java_javax_tv_service_selection_VDRServiceContext_doStop(JNIEnv* env, jobject obj) {
   Service::Context::getContext()->StopPresentation();
}

jlong Java_javax_tv_service_selection_VDRServiceContext_getNativeService(JNIEnv* env, jobject obj) {
   return (jlong)Service::Context::getContext()->getService();
}






}
