#include <libjava/jniinterface.h>
#include <vdr/channels.h>
#include <vdr/device.h>
#include <libdvbsi/database.h>
#include <libservice/servicecontext.h>
#include <libjava/nativewrappertypes.h>

extern "C" {

jbyteArray copyConstCharIntoByteArray(JNIEnv* env, const char *str);


/*--- javax.tv.service.VDRService ---*/

jstring Java_javax_tv_service_VDRService_name(JNIEnv* env, jobject obj, jobject nativeData) {
   return JNI::String(NativeChannelData(nativeData).Get()->Name());
}

jint Java_javax_tv_service_VDRService_onid(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeChannelData(nativeData).Get()->Nid();
}

jint Java_javax_tv_service_VDRService_tid(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeChannelData(nativeData).Get()->Tid();
}

jint Java_javax_tv_service_VDRService_sid(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeChannelData(nativeData).Get()->Sid();
}

jint Java_javax_tv_service_VDRService_serviceNumber(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeChannelData(nativeData).Get()->Number();
}

jboolean Java_javax_tv_service_VDRService_isRadio(JNIEnv* env, jobject obj, jobject nativeData) {
   //simply checks if video pid is 0
   return NativeChannelData(nativeData).Get()->Vpid()==0;
}

//static
jobject Java_javax_tv_service_VDRService_getCurrentChannelNative(JNIEnv* env, jclass clazz) {
   return NativeChannelData(Channels.GetByNumber(cDevice::CurrentChannel()));
}

jobject Java_javax_tv_service_VDRService_getServiceForChannelId(JNIEnv* env, jclass clazz, jint source, jint onid, jint tid, jint sid) {
   tChannelID chid(source, onid, tid, sid);
   return NativeChannelData(Channels.GetByChannelID(chid));
}

jobject Java_javax_tv_service_VDRService_getServiceForNidTidSid(JNIEnv* env, jclass clazz, jint onid, jint tid, jint sid) {
   //don't even think about using a VDR cList for cChannel *
   //that would destroy Channels :-)
   std::vector<cChannel *> list;
   DvbSi::Database::findChannels(onid, tid, sid, list);
   
   return NativeChannelData(list.front());
}


/*--- javax.tv.service.navigation.VDRServiceList ---*/

jboolean Java_javax_tv_service_navigation_VDRServiceList_acquireLock(JNIEnv* env, jobject obj) {
   return Channels.Lock(false, 1000);
}

void Java_javax_tv_service_navigation_VDRServiceList_releaseLock(JNIEnv* env, jobject obj) {
   return Channels.Unlock();
}

jobject Java_javax_tv_service_navigation_VDRServiceList_firstChannel(JNIEnv* env, jobject obj) {
   return NativeChannelData(Channels.First());
}

jobject Java_javax_tv_service_navigation_VDRServiceList_nextChannel(JNIEnv* env, jobject obj, jobject previousChannel) {
   cChannel *previous=NativeChannelData(previousChannel);
   return NativeChannelData((cChannel *)previous->Next());
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

void Java_javax_tv_service_selection_VDRServiceContext_doSelect(JNIEnv* env, jobject obj, jobject nativeData) {
   Service::Context::getContext()->SelectService(NativeChannelData(nativeData));
}

void Java_javax_tv_service_selection_VDRServiceContext_doStop(JNIEnv* env, jobject obj) {
   Service::Context::getContext()->StopPresentation();
}

jobject Java_javax_tv_service_selection_VDRServiceContext_getNativeService(JNIEnv* env, jobject obj) {
   return NativeChannelData(Service::Context::getContext()->getService());
}






}
