#include <libjava/jniinterface.h>
#include <vdr/channels.h>
#include <vdr/device.h>
#include <vdr/epg.h>
#include <libdvbsi/database.h>
#include <libservice/service.h>
#include <libservice/servicecontext.h>
#include <libjava/nativewrappertypes.h>

extern "C" {

jbyteArray copyConstCharIntoByteArray(JNIEnv* env, const char *str);


/*--- javax.tv.service.VDRService ---*/

static JNI::InstanceMethod componentsCallback;

void Java_javax_tv_service_VDRService_initStaticState(JNIEnv* env, jclass clazz) {
   componentsCallback.SetExceptionHandling(JNI::DoNotClearExceptions);
   componentsCallback.SetMethodWithArguments("javax/tv/service/VDRService$ServiceComponentListBuilder", "nextComponent", JNI::Void, 3, JNI::Int, JNI::Int, JNI::Object, "java/lang/String", (char *)0);
}

jstring Java_javax_tv_service_VDRService_shortName(JNIEnv* env, jobject obj, jobject nativeData) {
   /*
   MHP specification, Annex O:
   Returns the name of the service as stored in the MHP terminal. Depending on the MHP terminal implementation, the end
   user may have the possibility to edit these names according to his preferences. If the contents of this ?eld are retrieved by
   the MHP terminal by default from DVB SI, it is recommended that the MHP terminal uses the abbreviated form of the
   service name from the Service descriptor (see 4.6.1 "Use of control codes in names" in ETSI ETR 211 [11]).
   */
   Service::ServiceManager::ServiceLock lock;
   // return short name, or, if unavailable, long name.
   return JNI::String(NativeChannelData(nativeData).Get()->getChannelInformation()->getShortName(true));
}

jstring Java_javax_tv_service_VDRService_longName(JNIEnv* env, jobject obj, jobject nativeData) {
   /*
   MHP specification, Annex O:
   If the language returned by javax.tv.service.SIManager.getPreferredLanguage corresponds to the
   language of a multilingual service descriptor in the SDT, return the service name in that language. Otherwise return an
   implementation dependent selection between the descriptors available in the SDT.
   */
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
   return NativeChannelData(Service::ServiceManager::getManager()->findService(onid, tid, sid));
}

jint Java_javax_tv_service_VDRService_deliverySystemType(JNIEnv* env, jobject obj, jobject nativeData) {
   Service::ServiceManager::ServiceLock lock;
   return NativeChannelData(nativeData).Get()->getTunable()->getDeliverySystem();
}

jboolean Java_javax_tv_service_VDRService_freeToAir(JNIEnv* env, jobject obj, jobject nativeData) {
   Service::ServiceManager::ServiceLock lock;
   return NativeChannelData(nativeData).Get()->getCaInformation()->isFreeToAir();
}

jintArray Java_javax_tv_service_VDRService_caIDs(JNIEnv* env, jobject obj, jobject nativeData) {
   Service::ServiceManager::ServiceLock lock;
   Service::CaInformation::CaIDList list;
   NativeChannelData(nativeData).Get()->getCaInformation()->getCaIDs(list);
   jintArray javaar = env->NewIntArray(list.size());
   jint *ar = env->GetIntArrayElements(javaar, NULL);
   for (uint i=0; i<list.size(); i++)
      ar[i]=list[i];
   env->ReleaseIntArrayElements(javaar, ar, 0);
   return javaar;
}

jstring Java_javax_tv_service_VDRService_providerName(JNIEnv* env, jobject obj, jobject nativeData) {
   Service::ServiceManager::ServiceLock lock;
   return JNI::String(NativeChannelData(nativeData).Get()->getChannelInformation()->getProvider());
}

void Java_javax_tv_service_VDRService_buildComponentsList(JNIEnv* env, jobject obj, jobject nativeData, jobject builder) {
   Service::ServiceManager::ServiceLock lock;
   JNI::ReturnType returnValue;
   Service::ElementaryStreams *streams = NativeChannelData(nativeData).Get()->getElementaryStreams();
   componentsCallback.CallMethod(builder, returnValue, (jint)streams->getVideoPid(), (jint)1, (jstring)JNI::String(""));
   Service::ElementaryStreams::PidList list;
   streams->getAudioPids(list);
   // streams->getDolbyPids(list); Add them?
   for (Service::ElementaryStreams::PidList::iterator it = list.begin(); it != list.end(); ++it)
      componentsCallback.CallMethod(builder, returnValue, (jint)it->pid, (jint)2, (jstring)JNI::String(it->language.c_str()));
   list.clear();
   streams->getSubtitlingPids(list);
   for (Service::ElementaryStreams::PidList::iterator it = list.begin(); it != list.end(); ++it)
      componentsCallback.CallMethod(builder, returnValue, (jint)it->pid, (jint)3, (jstring)JNI::String(it->language.c_str()));
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
      if (!nextServiceCallback.CallMethod(builder, returnValue, (jobject)NativeChannelData(service)))
         return;
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

// --- javax.tv.service.guide.VDRProgramSchedule ---

JNI::InstanceMethod eventListBuilderCallback;
JNI::InstanceMethod eventComponentListBuilderCallback;
JNI::Field eventDescriptionField;

void Java_javax_tv_service_guide_VDRProgramSchedule_initStaticState(JNIEnv* env, jclass clazz) {
   eventListBuilderCallback.SetExceptionHandling(JNI::DoNotClearExceptions);
   eventComponentListBuilderCallback.SetExceptionHandling(JNI::DoNotClearExceptions);
   eventDescriptionField.SetExceptionHandling(JNI::DoNotClearExceptions);

   // void nextEvent(int eventID, long startTime, long endTime, long duration, long updateTime, String name)
   if (!eventListBuilderCallback.SetMethodWithArguments("javax/tv/service/guide/VDRProgramSchedule$EventListBuilder", "nextEvent", JNI::Void, 6, JNI::Int, JNI::Long, JNI::Long, JNI::Long, JNI::Long, JNI::Object, "java/lang/String", (char *)0))
      return;

   //    void nextComponent(int componentTag, int stream_content, int content_type, String language, String name, long updateTime)
   if (!eventComponentListBuilderCallback.SetMethodWithArguments("javax/tv/service/guide/VDRProgramSchedule$ComponentListBuilder", "nextComponent", JNI::Void, 6, JNI::Int, JNI::Int, JNI::Int, JNI::Object, "java/lang/String", JNI::Object, "java/lang/String", JNI::Long))
      return;

   eventDescriptionField.SetField("javax/tv/service/guide/VDRProgramSchedule$VDRProgramEvent", "description", JNI::Object, "java/lang/String");
}

static const cSchedule *getSchedule(cSchedulesLock &lock, jobject nativeServiceData) {
   Service::Service::Ptr service=NativeChannelData(nativeServiceData);
   tChannelID id(service->GetSource(), service->GetNid(), service->GetTid(), service->GetSid());
   const cSchedules *schedules=cSchedules::Schedules(lock);
   if (schedules) {
      return schedules->GetSchedule(id);
   } else
      return 0;
}

static jboolean addEvent(const cEvent *event, jobject builder) {
   if (event) {
      JNI::ReturnType returnValue;
      if (eventListBuilderCallback.CallMethod(builder, returnValue, (jint)event->EventID(), (jlong)event->StartTime(), (jlong)event->EndTime(), (jlong)event->Duration(), (jlong)event->Seen(), (jstring)JNI::String(event->Title())))
         return JNI_TRUE;
   }
   return JNI_FALSE;
}

static const cEvent *findEvent(cSchedulesLock &lock, jobject nativeServiceData, int eventID) {
   const cSchedule *schedule=getSchedule(lock, nativeServiceData);
   if (schedule) {
      return schedule->GetEvent(eventID);
   } else
      return 0;
}

// all time values in seconds
jboolean Java_javax_tv_service_guide_VDRProgramSchedule_currentEvent(JNIEnv* env, jobject obj, jobject nativeServiceData, jobject builder) {
   cSchedulesLock lock;
   const cSchedule *schedule=getSchedule(lock, nativeServiceData);
   printf("currentEvent: schedule %p, present event %p\n", schedule, schedule->GetPresentEvent());
   if (schedule) {
      return addEvent(schedule->GetPresentEvent(), builder);
   } else
      return JNI_FALSE;
}

jboolean Java_javax_tv_service_guide_VDRProgramSchedule_futureEvent(JNIEnv* env, jobject obj, jobject nativeServiceData, jlong time, jobject builder) {
   cSchedulesLock lock;
   const cSchedule *schedule=getSchedule(lock, nativeServiceData);
   if (schedule) {
      return addEvent(schedule->GetEventAround(time), builder);
   } else
      return JNI_FALSE;
}

/*
 A program event pe is retrieved by this method if the time interval from pe.getStartTime() (inclusive)
 to pe.getEndTime() (exclusive) intersects the time interval from begin (inclusive) to end (exclusive)
 specified by the input parameters.
*/

jboolean Java_javax_tv_service_guide_VDRProgramSchedule_futureEvents(JNIEnv* env, jobject obj, jobject nativeServiceData, jlong begin, jlong end, jobject builder) {
   cSchedulesLock lock;
   const cSchedule *schedule=getSchedule(lock, nativeServiceData);
   jboolean success=JNI_FALSE;
   if (schedule) {
      const cList<cEvent> *events=schedule->Events();
      for (cEvent *event = events->First(); event; event = events->Next(event)) {
         if ( (event->StartTime() >= begin && event->StartTime() < end)
               || (event->EndTime() >= begin && event->EndTime() <= end) ) {
            if (addEvent(event, builder))
               success=JNI_TRUE;
         }
      }
   }
   return success;
}

jboolean Java_javax_tv_service_guide_VDRProgramSchedule_nextEvent(JNIEnv* env, jobject obj, jobject nativeServiceData, jobject builder) {
   cSchedulesLock lock;
   const cSchedule *schedule=getSchedule(lock, nativeServiceData);
   if (schedule) {
      return addEvent(schedule->GetFollowingEvent(), builder);
   } else
      return JNI_FALSE;
}

jboolean Java_javax_tv_service_guide_VDRProgramSchedule_event(JNIEnv* env, jobject obj, jobject nativeServiceData, jint eventID, jobject builder) {
   cSchedulesLock lock;
   const cSchedule *schedule=getSchedule(lock, nativeServiceData);
   if (schedule) {
      return addEvent(schedule->GetEvent(eventID), builder);
   } else
      return JNI_FALSE;
}

jboolean Java_javax_tv_service_guide_VDRProgramSchedule_fillEventDescription(JNIEnv* env, jobject obj, jobject nativeServiceData, jint eventID, jobject eventObject) {
   cSchedulesLock lock;
   const cEvent *event=findEvent(lock, nativeServiceData, eventID);
   if (event) {
      JNI::ReturnType value;
      value.TypeObject = (jstring)JNI::String(event->Description());
      eventDescriptionField.SetValue(eventObject, value);
      return JNI_TRUE;
   }
   return JNI_FALSE;
}


jboolean Java_javax_tv_service_guide_VDRProgramSchedule_components(JNIEnv* env, jobject obj, jobject nativeServiceData, jint eventID, jobject builder) {
   cSchedulesLock lock;
   const cEvent *event=findEvent(lock, nativeServiceData, eventID);
   if (event) {
      const cComponents *components=event->Components();
      if (components) {
         printf("Java_javax_tv_service_guide_VDRProgramSchedule_components: componentTag not stored by VDR!\n");
         for (int i=0;i<components->NumComponents();i++) {
            const tComponent *comp=components->Component(i);
            JNI::ReturnType returnValue;
            if (!eventComponentListBuilderCallback.CallMethod(builder, returnValue, (jint)0, (jint)comp->stream, (jint)comp->type, (jstring)JNI::String(comp->language), (jstring)JNI::String(comp->description), (jlong)event->Seen()))
               return JNI_FALSE;
         }
         return JNI_TRUE;
      }
   }
   return JNI_FALSE;
}




}
