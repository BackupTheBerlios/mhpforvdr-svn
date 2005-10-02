#include <libjava/jniinterface.h>
#include <vdr/channels.h>
#include <vdr/device.h>
#include <libdvbsi/database.h>
#include <libservice/service.h>
#include <libservice/servicecontext.h>
#include <libjava/nativewrappertypes.h>

extern "C" {

jbyteArray copyConstCharIntoByteArray(JNIEnv* env, const char *str);


/*--- javax.tv.service.VDRService ---*/

jstring Java_javax_tv_service_VDRService_name(JNIEnv* env, jobject obj, jobject nativeData) {
   Service::ServiceManager::ServiceLock lock;
   return JNI::String(NativeChannelData(nativeData).Get()->getChannelInformation()->getName());
}

jint Java_javax_tv_service_VDRService_onid(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeChannelData(nativeData).Get()->GetNid();
}

jint Java_javax_tv_service_VDRService_tid(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeChannelData(nativeData).Get()->GetTid();
}

jint Java_javax_tv_service_VDRService_sid(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeChannelData(nativeData).Get()->GetSid();
}

jint Java_javax_tv_service_VDRService_serviceNumber(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeChannelData(nativeData).Get()->getChannelNumber();
}

jboolean Java_javax_tv_service_VDRService_isRadio(JNIEnv* env, jobject obj, jobject nativeData) {
   Service::ServiceManager::ServiceLock lock;
   //simply checks if video pid is 0
   return NativeChannelData(nativeData).Get()->getElementaryStreams()->getVideoPid()==0;
}

//static
jobject Java_javax_tv_service_VDRService_getCurrentServiceNative(JNIEnv* env, jclass clazz) {
   Service::ServiceManager::ServiceLock lock;
   return NativeChannelData(Service::ServiceManager::getManager()->getPrimaryDecoder()->getCurrentService());
}

jobject Java_javax_tv_service_VDRService_getServiceForChannelId(JNIEnv* env, jclass clazz, jint source, jint onid, jint tid, jint sid) {
   Service::ServiceID id(source, onid, tid, sid);
   return NativeChannelData(Service::ServiceManager::getManager()->findService(id));
}

jobject Java_javax_tv_service_VDRService_getServiceForNidTidSid(JNIEnv* env, jclass clazz, jint onid, jint tid, jint sid) {
   //don't even think about using a VDR cList for cChannel *
   //that would destroy Channels :-)
   std::list<Service::Service::Ptr> list;
   Service::ServiceManager::getManager()->findService(onid, tid, sid, list);
   
   return NativeChannelData(list.front());
}


/*--- javax.tv.service.navigation.VDRServiceList ---*/

static JNI::InstanceMethod nextServiceCallback;

void Java_javax_tv_service_navigation_VDRServiceList_initStaticState(JNIEnv* env, jclass clazz) {
   nextServiceCallback.SetExceptionHandling(JNI::DoNotClearExceptions);
   nextServiceCallback.SetMethodWithArguments("javax/tv/service/navigation/VDRServiceList$ListBuilder", "nextService", JNI::Void, 1, JNI::Object, "vdr/mhp/lang/NativeData", (char *)0);
}

void Java_javax_tv_service_navigation_VDRServiceList_listServices(JNIEnv* env, jobject obj, jobject builder) {
   Service::ServiceManager::ServiceLock lock;
   Service::ServiceManager *manager=Service::ServiceManager::getManager();
   JNI::ReturnType returnValue;
   for (Service::Service::Ptr service=manager->getFirstService(); service; service=manager->getNextService(service)) {
      nextServiceCallback.CallMethod(builder, returnValue, (jobject)NativeChannelData(service));
   }
}

/*--- javax.tv.service.selection.VDRServiceContext ---*/

class VDRServiceStatus : public Service::ServiceStatus {
public:
   VDRServiceStatus(jclass clazz) {
      callback.SetMethodWithArguments(clazz, "nativeServiceEvent", JNI::Void, 1, JNI::Int);
   }
protected:
   virtual void ServiceEvent(Message event, Service::Service::Ptr service)
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
