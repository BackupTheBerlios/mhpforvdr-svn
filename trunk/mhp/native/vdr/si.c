
#include <typeinfo>
#include <list>

#include <libsi/descriptor.h>

#include <libdvbsi/database.h>
#include <libjava/jniinterface.h>
#include <libjava/jnithread.h>

#include <libjava/nativewrappertypes.h>

//These classes, all inheriting from RequestWrapper, form a native layer for org.dvb.si
//encapsulating DvbSi objects that can't be stored in Java and using templates
//to access the DvbSi Request (which themselves are mostly templates)

class RequestWrapper : public DvbSi::Listener {
public:
   virtual ~RequestWrapper() {}
   virtual bool hasMoreElements(jobject iteratorData) = 0;
   virtual int numberOfRemainingObjects(jobject iteratorData) = 0;
   virtual jobject nextElement(jobject iteratorData) = 0;
   virtual jobject getNewIterator() = 0;
   virtual void DeleteIterator(jobject iteratorData) = 0;
   
   virtual int getResultCode() = 0;
   virtual void getDataSource(DvbSi::DataSource &source) = 0;
   
   virtual bool isAvailableInCache() = 0;
   virtual bool CancelRequest() = 0;
   
   static void initMethod() {
      jnimethod.SetExceptionHandling(JNI::DoNotClearExceptions);
      jnimethod.SetMethodWithArguments("org/dvb/si/SIDatabaseRequest", "Result", JNI::Void, 1, JNI::Object, "vdr/mhp/lang/NativeData", (char *)0);
   }
protected:
   JNI::GlobalObjectRef jniobject;
   static JNI::InstanceMethod jnimethod;
};

//T is a DvbSi::Request
template <class R, class T = typename R::objectType> class SpecializedRequestWrapper : public RequestWrapper {
public:
   typedef T objectType;
   SpecializedRequestWrapper(jobject javaRequest)
     : req(0) {
      jniobject.SetObject(javaRequest);
   }

   virtual ~SpecializedRequestWrapper() {
      printf("~SpecializedRequestWrapper()");
      delete req;
   }

   virtual int getResultCode() {
      return req ? req->getResultCode() : 0;
   }

   virtual bool isAvailableInCache() {
      return req ? req->isAvailableInCache() : false;
   }

   virtual bool CancelRequest() {
      return req ? req->CancelRequest() : false;
   }

   virtual void getDataSource(DvbSi::DataSource &source) {
      if (req)
         source=req->getDataSource();
   }

   R *req;
protected:
   virtual void Result(DvbSi::Request *re) {
      printf("Received result %d, request %p\n", re->getResultCode(), re);
      JNI::ReturnType ret;
      JNI::Thread::CheckAttachThread();
      jnimethod.CallMethod((jobject)jniobject, ret, (jobject)JNI::PointerNativeData<RequestWrapper>(this));
      printf("Left CallMethod, now deleting jniobject\n");
      //this holds reference to jniobject, and this is deleted when
      //jniobject is finalized, so if we don't delete the reference
      //both won't be deleted.
      jniobject.Delete();
      printf("Deleted jniobject\n");
   }

   struct STLIteratorData {
      STLIteratorData() : remainingObjects(0) {}
      STLIteratorData(typename R::iterator it, int remainingObjects) : it(it), remainingObjects(remainingObjects) {}
      typename R::iterator it;
      int remainingObjects;
   };
   typedef JNI::PointerNativeData<STLIteratorData> STLIteratorDataNativeData;
   typedef JNI::PointerNativeData<objectType> ObjectTypeNativeData;
};

#include <typeinfo>

//R is a DvbSi::Request which inherits ListSecondaryRequest or directly TableFilterRequest
template <class R> class ListRequestWrapper : public SpecializedRequestWrapper<R> {
public:
   typedef typename SpecializedRequestWrapper<R>::objectType objectType;
   typedef typename SpecializedRequestWrapper<R>::STLIteratorData STLIteratorData;
   ListRequestWrapper(jobject javaReq)
    : SpecializedRequestWrapper<R>(javaReq) {}

   virtual bool hasMoreElements(jobject iteratorData) {
      return STLIteratorDataNativeData(iteratorData).Get()->remainingObjects>0;
   }

   virtual int numberOfRemainingObjects(jobject iteratorData) {
      return STLIteratorDataNativeData(iteratorData).Get()->remainingObjects;
   }

   virtual jobject nextElement(jobject iteratorData) {
      if (this->req && hasMoreElements(iteratorData)) {
         STLIteratorData *itdata=STLIteratorDataNativeData(iteratorData).Get();
         objectType *next=new objectType(*itdata->it);
         itdata->remainingObjects--;
         ++(itdata->it);
         return ObjectTypeNativeData(next);
      } else
         return ObjectTypeNativeData((objectType *)0);
   }

   virtual jobject getNewIterator() {
      printf("ListRequestWrapper'::getNewIterator, list of size %d\n", this->req->list.size());
      return this->req ? STLIteratorDataNativeData(new STLIteratorData(this->req->list.begin(), this->req->list.size()))
                      :  STLIteratorDataNativeData(new STLIteratorData());
   }

   virtual void DeleteIterator(jobject iteratorData) {
      delete STLIteratorDataNativeData(iteratorData).Get();
   }
protected:
   typedef typename SpecializedRequestWrapper<R>::STLIteratorDataNativeData STLIteratorDataNativeData;
   typedef typename SpecializedRequestWrapper<R>::ObjectTypeNativeData ObjectTypeNativeData;
};

//R is a DvbSi::Request which inherits TableFilterTrackerRequest
template <class R> class SubtableRequestWrapper : public SpecializedRequestWrapper<R, std::list<typename R::objectType> > {
public:
   typedef std::list<typename R::objectType> List;
   typedef typename SpecializedRequestWrapper<R, std::list<typename R::objectType> >::STLIteratorData STLIteratorData;

   SubtableRequestWrapper(jobject javaReq)
    : SpecializedRequestWrapper<R, List>(javaReq) {}

   virtual bool hasMoreElements(jobject iteratorData) {
      return STLIteratorDataNativeData(iteratorData).Get()->remainingObjects>0;
   }

   virtual int numberOfRemainingObjects(jobject iteratorData) {
      return STLIteratorDataNativeData(iteratorData).Get()->remainingObjects;
   }

   virtual jobject nextElement(jobject iteratorData) {
      if (this->req && hasMoreElements(iteratorData)) {
         STLIteratorData *itdata=STLIteratorDataNativeData(iteratorData).Get();
         itdata->remainingObjects--;
         //returns the iterator that points to the subtable that comes next in the list
         //to the one given as the argument
         typename R::iterator newSubtable=this->req->getNextSubtable(itdata->it);
         //create a list of the current subtable, newSubtable points to the next,
         //creates list from including the first to excluding the second iterator
         List *ret=new List(itdata->it, newSubtable);
         //set the iteratorData's iterator to the next subtable (or list->end()) for the next call
         itdata->it=newSubtable;
         return ObjectTypeNativeData(ret);
      } else
         return 0;
   }

   virtual jobject getNewIterator() {
      return this->req ? STLIteratorDataNativeData(new STLIteratorData(this->req->list.begin(), this->req->getNumberOfSubtables()))
                      :  STLIteratorDataNativeData(new STLIteratorData());
   }

   virtual void DeleteIterator(jobject iteratorData) {
      delete STLIteratorDataNativeData(iteratorData).Get();
   }
protected:
   typedef typename SpecializedRequestWrapper<R, std::list<typename R::objectType> >::STLIteratorDataNativeData STLIteratorDataNativeData;
   typedef typename SpecializedRequestWrapper<R, std::list<typename R::objectType> >::ObjectTypeNativeData ObjectTypeNativeData;
};

//R is a plain FilterRequest or SecondaryRequest which implements getResultObject(),
template <class R, class T = typename R::objectType> class SingleRequestWrapper : public SpecializedRequestWrapper<R> {
public:
   typedef typename SpecializedRequestWrapper<R>::objectType objectType;

   SingleRequestWrapper(jobject javaReq)
    : SpecializedRequestWrapper<R>(javaReq) {}
   virtual bool hasMoreElements(jobject iteratorData) {
      return SimpleNativeData(iteratorData).Get();
   }
   virtual int numberOfRemainingObjects(jobject iteratorData) {
      return SimpleNativeData(iteratorData).Get() ? 1 : 0;
   }
   virtual jobject nextElement(jobject iteratorData) {
      if (this->req && (SimpleNativeData(iteratorData).Get()))
         SimpleNativeData(iteratorData).Set(0);
      else
         return 0;
      return ObjectTypeNativeData(new T(this->req->getResultObject()));
   }
   virtual jobject getNewIterator() {
      return SimpleNativeData((bool *)1);
   }
   virtual void DeleteIterator(jobject iteratorData) {
   }
protected:
   // using a bool * as bool value, could as well use a void *,
   // this is for type safety and to confuse the reader
   typedef JNI::PointerNativeData<bool> SimpleNativeData;
   typedef typename SpecializedRequestWrapper<R>::ObjectTypeNativeData ObjectTypeNativeData;
};


class DescriptorRequestWrapper : public ListRequestWrapper<DvbSi::DescriptorRequest> {
public:
   DescriptorRequestWrapper(jobject javaReq)
    : ListRequestWrapper<DvbSi::DescriptorRequest>(javaReq) {}

   virtual jobject nextElement(jobject iteratorData) {
      if (req && hasMoreElements(iteratorData)) {
         STLIteratorData *itdata=STLIteratorDataNativeData(iteratorData).Get();
         // We can't create a copy of the descriptor, subclass is unknown,
         // and doing "new SI::Descriptor *()" - creating a pointer! - is pretty useless.
         // Just pass the pointer directly.
         // So the java code will have to keep a reference to the RequestWrapper,
         // which it does anyway.
         objectType next=(*itdata->it);
         itdata->remainingObjects--;
         ++(itdata->it);
         return ObjectTypeNativeData(next);
      } else
         return ObjectTypeNativeData((SI::Descriptor *)0);
   }
protected:
   //objectType is SI::Descriptor*, for PointerNativeData we need to pass only SI::Descriptor
   typedef JNI::PointerNativeData<SI::Descriptor> ObjectTypeNativeData;
};

JNI::InstanceMethod RequestWrapper::jnimethod;


typedef SubtableRequestWrapper<DvbSi::NetworksRequest> NetworksRequestWrapper;
typedef SubtableRequestWrapper<DvbSi::ActualNetworkRequest> ActualNetworkRequestWrapper;
typedef SubtableRequestWrapper<DvbSi::BouquetsRequest> BouquetsRequestWrapper;

typedef SubtableRequestWrapper<DvbSi::TransportStreamDescriptionRequest> TransportStreamDescriptionRequestWrapper;

typedef ListRequestWrapper<DvbSi::ServicesRequest> ServicesRequestWrapper;
typedef ListRequestWrapper<DvbSi::ActualServicesRequest> ActualServicesRequestWrapper;

typedef ListRequestWrapper<DvbSi::ScheduleEventRequest> EventRequestWrapper;
typedef ListRequestWrapper<DvbSi::TimeScheduleEventRequest> TimeEventRequestWrapper;
typedef SingleRequestWrapper<DvbSi::PresentFollowingEventRequest> SingleEventRequestWrapper;

typedef ListRequestWrapper<DvbSi::TransportStreamRequest> TransportStreamRequestWrapper;
typedef ListRequestWrapper<DvbSi::ActualTransportStreamRequest> ActualTransportStreamRequestWrapper;
typedef ListRequestWrapper<DvbSi::TransportStreamBATRequest> TransportStreamBATRequestWrapper;

typedef SingleRequestWrapper<DvbSi::TOTRequest> TOTRequestWrapper;
typedef SingleRequestWrapper<DvbSi::TDTRequest> TDTRequestWrapper;

typedef ListRequestWrapper<DvbSi::PMTServicesRequest> PMTServicesRequestWrapper;
typedef ListRequestWrapper<DvbSi::PMTElementaryStreamRequest> PMTElementaryStreamsRequestWrapper;

// NativeData typedefs
typedef JNI::PointerNativeData<RequestWrapper> RequestWrapperNativeData;

typedef JNI::PointerNativeData<NetworksRequestWrapper> NetworksRequestWrapperNativeData;
typedef JNI::PointerNativeData<ActualNetworkRequestWrapper> ActualNetworkRequestWrapperNativeData;
typedef JNI::PointerNativeData<std::list<DvbSi::NIT> > StdListDvbsiNitNativeData;

typedef JNI::PointerNativeData<BouquetsRequestWrapper> BouquetsRequestWrapperNativeData;
typedef JNI::PointerNativeData<std::list<DvbSi::BAT> > StdListDvbsiBatNativeData;

typedef JNI::PointerNativeData<std::list<DvbSi::TSDT> > StdListDvbsiTsdtNativeData;
typedef JNI::PointerNativeData<DvbSi::EIT::Event> DvbsiEitEventNativeData;
typedef JNI::PointerNativeData<DvbSi::SDT::Service> DvbsiSdtServiceNativeData;
typedef JNI::PointerNativeData<DvbSi::NIT::TransportStream> DvbsiNitTransportStreamNativeData;
typedef JNI::PointerNativeData<SI::TOT> SiTotNativeData;
typedef JNI::PointerNativeData<SI::TDT> SiTdtNativeData;
typedef JNI::PointerNativeData<DvbSi::PMT> DvbsiPmtNativeData;
typedef JNI::PointerNativeData<DvbSi::PMT::Stream> DvbsiPmtStreamNativeData;
typedef JNI::PointerNativeData<SI::Object> SiObjectNativeData;
typedef JNI::PointerNativeData<SI::Descriptor> SiDescriptorNativeData;



extern "C" {



/*** SIDatabase ***/

void Java_org_dvb_si_SIDatabase_initializeList(JNIEnv* env, jclass clazz, jobject builder) {
   JNI::InstanceMethod builderCallback;
   builderCallback.SetExceptionHandling(JNI::DoNotClearExceptions);
   if (!builderCallback.SetMethodWithArguments("org/dvb/si/SIDatabase$DatabaseListBuilder", "nextDatabase", JNI::Void, 1, JNI::Object, "vdr/mhp/lang/NativeData", (char *)0))
      return;
   std::list<DvbSi::Database::Ptr> list;
   DvbSi::Database::getDatabases(list);
   JNI::ReturnType retValue;
   for (std::list<DvbSi::Database::Ptr>::iterator it = list.begin(); it != list.end(); ++it) {
      if (!builderCallback.CallMethod( builder, retValue, (jobject)NativeDvbsiDatabaseData(*it, !(*it)) ))
         return;
   }
}

jobject Java_org_dvb_si_SIDatabase_databaseForChannel(JNIEnv* env, jclass clazz, jint nid, jint tid, jint sid) {
   Service::Service::Ptr service=Service::ServiceManager::getManager()->findService(nid, tid, sid);
   DvbSi::Database::Ptr ptr = DvbSi::Database::getDatabaseTunedForService(service);
   return (jobject)NativeDvbsiDatabaseData(ptr, !ptr);
}

jint Java_org_dvb_si_SIDatabase_deliverySystemType(JNIEnv* env, jobject obj, jobject nativeData) {
   return NativeDvbsiDatabaseData(nativeData).Get()->getDeliverySystem();
}

/*** SIDatabaseRequest ***/

void Java_org_dvb_si_SIDatabaseRequest_initStaticState(JNIEnv* env, jclass clazz) {
   RequestWrapper::initMethod();
}

void Java_org_dvb_si_SIDatabaseRequest_cleanUp(JNIEnv* env, jobject obj, jobject nativeData) {
   delete RequestWrapperNativeData(nativeData).Get();
}

jint Java_org_dvb_si_SIDatabaseRequest_resultCode(JNIEnv* env, jobject obj, jobject nativeData) {
   return RequestWrapperNativeData(nativeData).Get()->getResultCode();
}

jobject Java_org_dvb_si_SIDatabaseRequest_ActualNetworkRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   ActualNetworkRequestWrapper *wrapper=new ActualNetworkRequestWrapper(obj);
   wrapper->req=db->retrieveActualNetwork(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_ActualServicesRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   ActualServicesRequestWrapper *wrapper=new ActualServicesRequestWrapper(obj);
   wrapper->req=db->retrieveActualServices(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_ActualTransportStreamRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   ActualTransportStreamRequestWrapper *wrapper=new ActualTransportStreamRequestWrapper(obj);
   wrapper->req=db->retrieveActualTransportStream(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_PMTElementaryStreamsRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jint originalNetworkId, jint transportStreamId, jint serviceId, jintArray componentTags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   PMTElementaryStreamsRequestWrapper *wrapper=new PMTElementaryStreamsRequestWrapper(obj);

   jint *ar=env->GetIntArrayElements(componentTags, 0);
   bool any= (env->GetArrayLength(componentTags)==1) && (ar[0]==-1);

   wrapper->req=db->retrievePMTElementaryStreams(wrapper, originalNetworkId, transportStreamId, serviceId, any ? 0 : new DvbSi::ListIdTracker(ar), (DvbSi::RetrieveMode)retrieveMode);

   env->ReleaseIntArrayElements(componentTags, ar, JNI_ABORT);

   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_PMTServicesRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jint originalNetworkId, jint transportStreamId, jint serviceId) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   PMTServicesRequestWrapper *wrapper=new PMTServicesRequestWrapper(obj);
   wrapper->req=db->retrievePMTServices(wrapper, originalNetworkId, transportStreamId, serviceId==-1 ? 0: new DvbSi::SingleIdTracker(serviceId), (DvbSi::RetrieveMode)retrieveMode);
   printf("PMTServicesRequest: creating request %p, wrapper %p\n", wrapper->req, wrapper);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_BouquetsRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jint bouquetId) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   BouquetsRequestWrapper *wrapper=new BouquetsRequestWrapper(obj);
   wrapper->req=db->retrieveBouquets(wrapper, bouquetId==-1 ? 0: new DvbSi::SingleIdTracker(bouquetId), (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_NetworksRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jint networkId) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   NetworksRequestWrapper *wrapper=new NetworksRequestWrapper(obj);
   if (networkId==-1)
      wrapper->req=db->retrieveNetworks(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   else
      wrapper->req=db->retrieveNetworks(wrapper, new DvbSi::SingleIdTracker(networkId), (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_ServicesRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jint originalNetworkId, jint transportStreamId, jint serviceId) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   ServicesRequestWrapper *wrapper=new ServicesRequestWrapper(obj);
   wrapper->req=db->retrieveServices(wrapper, originalNetworkId,
               transportStreamId==-1 ? 0: new DvbSi::SingleIdTracker(transportStreamId),
               serviceId==-1 ? 0: new DvbSi::SingleIdTracker(serviceId),
               (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_TDTRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   TDTRequestWrapper *wrapper=new TDTRequestWrapper(obj);
   wrapper->req=db->retrieveTDT(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_TOTRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   TOTRequestWrapper *wrapper=new TOTRequestWrapper(obj);
   wrapper->req=db->retrieveTOT(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_TransportStreamDescriptionRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   TransportStreamDescriptionRequestWrapper *wrapper=new TransportStreamDescriptionRequestWrapper(obj);
   wrapper->req=db->retrieveTransportStreamDescription(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_TransportStreamRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jobject nativeList, jobject nativeRequest) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   RequestWrapper *wr=RequestWrapperNativeData(nativeRequest);
   
   TransportStreamRequestWrapper *wrapper=new TransportStreamRequestWrapper(obj);
   
   if (typeid(*wr) == typeid(NetworksRequestWrapper))
      wrapper->req=new DvbSi::TransportStreamRequest(NetworksRequestWrapperNativeData(nativeRequest).Get()->req, StdListDvbsiNitNativeData(nativeList), db, wrapper, (DvbSi::RetrieveMode)retrieveMode);
   else //if (typeid(*wr) == typeid(ActualNetworkRequestWrapper))
      wrapper->req=new DvbSi::TransportStreamRequest(ActualNetworkRequestWrapperNativeData(nativeRequest).Get()->req, StdListDvbsiNitNativeData(nativeList), db, wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_TransportStreamRequestBAT(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jobject nativeList, jobject nativeRequest) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   
   TransportStreamBATRequestWrapper *wrapper=new TransportStreamBATRequestWrapper(obj);
   
   wrapper->req=new DvbSi::TransportStreamBATRequest(BouquetsRequestWrapperNativeData(nativeRequest).Get()->req, StdListDvbsiBatNativeData(nativeList), db, wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_PresentFollowingEventRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jboolean presentOrFollowing, jint tid, jint sid) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   SingleEventRequestWrapper *wrapper=new SingleEventRequestWrapper(obj);
   wrapper->req=db->retrievePresentFollowingEvent(wrapper, tid, sid, presentOrFollowing, (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_ScheduledEventsRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jint tid, jint sid) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   EventRequestWrapper *wrapper=new EventRequestWrapper(obj);
   wrapper->req=db->retrieveScheduledEvents(wrapper, tid, sid, (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}

jobject Java_org_dvb_si_SIDatabaseRequest_TimeScheduledEventsRequest(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jlong begin, jlong end, jint tid, jint sid) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   TimeEventRequestWrapper *wrapper=new TimeEventRequestWrapper(obj);
   wrapper->req=db->retrieveTimeScheduledEvents(wrapper, begin, end, tid, sid, (DvbSi::RetrieveMode)retrieveMode);
   return RequestWrapperNativeData(wrapper);
}


jobject Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestNetwork(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jobject nativeList, jobject nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   std::list<DvbSi::NIT> *list=StdListDvbsiNitNativeData(nativeList);
   DvbSi::DataSource source;
   RequestWrapperNativeData(nativeRequest).Get()->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   for (std::list<DvbSi::NIT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      DvbSi::NIT nit(*stlit);
      //TODO: honor tags
      req->Add(nit.commonDescriptors);
   }
   db->DispatchResult(req);
   return RequestWrapperNativeData(wrapper);
}


jobject Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestBouquet(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jobject nativeList, jobject nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   std::list<DvbSi::BAT> *list=StdListDvbsiBatNativeData(nativeList);
   DvbSi::DataSource source;
   RequestWrapperNativeData(nativeRequest).Get()->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   for (std::list<DvbSi::BAT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      DvbSi::BAT nit(*stlit);
      //TODO: honor tags
      req->Add(nit.commonDescriptors);
   }
   db->DispatchResult(req);
   return RequestWrapperNativeData(wrapper);
}


jobject Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestTransportStreamDescription(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jobject nativeList, jobject nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   std::list<DvbSi::TSDT> *list=StdListDvbsiTsdtNativeData(nativeList);
   DvbSi::DataSource source;
   RequestWrapperNativeData(nativeRequest).Get()->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   for (std::list<DvbSi::TSDT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      DvbSi::TSDT tsdt(*stlit);
      //TODO: honor tags
      req->Add(tsdt.transportStreamDescriptors);
   }
   db->DispatchResult(req);
   return RequestWrapperNativeData(wrapper);
}


jobject Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestEvent(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jobject nativeEvent, jobject nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   DvbSi::EIT::Event *event=DvbsiEitEventNativeData(nativeEvent);
   DvbSi::DataSource source;
   RequestWrapperNativeData(nativeRequest).Get()->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   //TODO: honor tags
   req->Add(event->eventDescriptors);
      
   db->DispatchResult(req);
   return RequestWrapperNativeData(wrapper);
}


jobject Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestService(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jobject nativeService, jobject nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   DvbSi::SDT::Service *service=DvbsiSdtServiceNativeData(nativeService);
   DvbSi::DataSource source;
   RequestWrapperNativeData(nativeRequest).Get()->getDataSource(source);

   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   //TODO: honor tags
   req->Add(service->serviceDescriptors);
      
   db->DispatchResult(req);
   return RequestWrapperNativeData(wrapper);
}


jobject Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestTransportStream(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jobject nativeTransportStream, jobject nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   DvbSi::NIT::TransportStream *ts=DvbsiNitTransportStreamNativeData(nativeTransportStream);
   DvbSi::DataSource source;
   RequestWrapperNativeData(nativeRequest).Get()->getDataSource(source);

   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   //TODO: honor tags
   req->Add(ts->transportStreamDescriptors);
      
   db->DispatchResult(req);
   return RequestWrapperNativeData(wrapper);
}


jobject Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestTime(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jobject nativeTime, jobject nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   RequestWrapper *wr=RequestWrapperNativeData(nativeRequest);
   
   DvbSi::DataSource source;
   wr->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   //TDT doesn't have any descriptor loop
   if (typeid(*wr)==typeid(TOTRequestWrapper))
      //TODO: honor tags
      req->Add(SiTotNativeData(nativeTime).Get()->descriptorLoop);
      
   db->DispatchResult(req);
   return RequestWrapperNativeData(wrapper);
}


jobject Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestPMTService(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jobject nativePMT, jobject nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   DvbSi::PMT *pmt=DvbsiPmtNativeData(nativePMT);
   DvbSi::DataSource source;
   RequestWrapperNativeData(nativeRequest).Get()->getDataSource(source);

   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   //TODO: honor tags
   req->Add(pmt->commonDescriptors);
      
   db->DispatchResult(req);
   return RequestWrapperNativeData(wrapper);
}


jobject Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestPMTElementaryStream(JNIEnv* env, jobject obj, jobject nativeDatabase, jshort retrieveMode, jobject nativeStream, jobject nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database::Ptr db=NativeDvbsiDatabaseData(nativeDatabase);
   DvbSi::PMT::Stream *str=DvbsiPmtStreamNativeData(nativeStream);
   DvbSi::DataSource source;
   RequestWrapperNativeData(nativeRequest).Get()->getDataSource(source);

   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   //TODO: honor tags
   req->Add(str->streamDescriptors);
      
   db->DispatchResult(req);
   return RequestWrapperNativeData(wrapper);
}




jint Java_org_dvb_si_SIDatabaseRequest_getSourceTid(JNIEnv* env, jobject obj, jobject nativeData) {
   DvbSi::DataSource source;
   RequestWrapperNativeData(nativeData).Get()->getDataSource(source);

   return source.tid;
}

jint Java_org_dvb_si_SIDatabaseRequest_getSourceNid(JNIEnv* env, jobject obj, jobject nativeData) {
   DvbSi::DataSource source;
   RequestWrapperNativeData(nativeData).Get()->getDataSource(source);

   return source.onid;
}

jint Java_org_dvb_si_SIDatabaseRequest_getSourceVDRSource(JNIEnv* env, jobject obj, jobject nativeData) {
   DvbSi::DataSource source;
   RequestWrapperNativeData(nativeData).Get()->getDataSource(source);

   return source.source;
}

jlong Java_org_dvb_si_SIDatabaseRequest_getRetrievalTime(JNIEnv* env, jobject obj, jobject nativeData) {
   DvbSi::DataSource source;
   RequestWrapperNativeData(nativeData).Get()->getDataSource(source);

   return (jlong)source.retrievalTime;
}

jboolean Java_org_dvb_si_SIDatabaseRequest_cancelRequest(JNIEnv* env, jobject obj, jobject nativeData) {
   return RequestWrapperNativeData(nativeData).Get()->CancelRequest();
}

jboolean Java_org_dvb_si_SIDatabaseRequest_availableInCache(JNIEnv* env, jobject obj, jobject nativeData) {
   return RequestWrapperNativeData(nativeData).Get()->isAvailableInCache();
}

jboolean Java_org_dvb_si_SIDatabaseRequest_hasMoreElements(JNIEnv* env, jobject obj, jobject nativeData, jobject nativeIteratorData) {
   return RequestWrapperNativeData(nativeData).Get()->hasMoreElements(nativeIteratorData);
}

jint Java_org_dvb_si_SIDatabaseRequest_numberOfRemainingObjects(JNIEnv* env, jobject obj, jobject nativeData, jobject nativeIteratorData) {
   return RequestWrapperNativeData(nativeData).Get()->numberOfRemainingObjects(nativeIteratorData);
}

jobject Java_org_dvb_si_SIDatabaseRequest_nextElement(JNIEnv* env, jobject obj, jobject nativeData, jobject nativeIteratorData) {
   return RequestWrapperNativeData(nativeData).Get()->nextElement(nativeIteratorData);
}

jobject Java_org_dvb_si_SIDatabaseRequest_newIterator(JNIEnv* env, jobject obj, jobject nativeData) {
   return RequestWrapperNativeData(nativeData).Get()->getNewIterator();
}

void Java_org_dvb_si_SIDatabaseRequest_cleanUpIterator(JNIEnv* env, jobject obj, jobject nativeData, jobject nativeIteratorData) {
   RequestWrapperNativeData(nativeData).Get()->DeleteIterator(nativeIteratorData);
}


/*** SICommonObject ***/


void Java_org_dvb_si_SICommonObject_cleanUpSiObject(JNIEnv* env, jobject obj, jobject nativeData) {
   delete SiObjectNativeData(nativeData).Get();
}



/*** SINetworkImpl ***/

void Java_org_dvb_si_SINetworkImpl_cleanUpStdList(JNIEnv* env, jobject obj, jobject nativeData) {
   delete StdListDvbsiNitNativeData(nativeData).Get();
}

jshortArray Java_org_dvb_si_SINetworkImpl_descriptorTags(JNIEnv* env, jobject obj, jobject nativeData) {
   std::list<DvbSi::NIT> *list=StdListDvbsiNitNativeData(nativeData);
   int count=0;
   for (std::list<DvbSi::NIT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      DvbSi::NIT nit(*stlit);
      count+=nit.commonDescriptors.getNumberOfDescriptors();
   }
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   jshort *current=ar;
   
   for (std::list<DvbSi::NIT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      DvbSi::NIT nit(*stlit);
      current+=nit.commonDescriptors.getDescriptorTags<jshort>(current);
   }
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}

jstring Java_org_dvb_si_SINetworkImpl_name(JNIEnv* env, jobject obj, jobject nativeData) {
   std::list<DvbSi::NIT> *list=StdListDvbsiNitNativeData(nativeData);
   for (std::list<DvbSi::NIT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      char name[256];
      stlit->getNetworkName(name, sizeof(name));
      if (name[0])
         return JNI::String(name);
   }
   return JNI::String("");
}

jstring Java_org_dvb_si_SINetworkImpl_shortNetworkName(JNIEnv* env, jobject obj, jobject nativeData) {
   std::list<DvbSi::NIT> *list=StdListDvbsiNitNativeData(nativeData);
   for (std::list<DvbSi::NIT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      char name[256];
      char shortName[256];
      stlit->getNetworkName(name, shortName, sizeof(name), sizeof(shortName));
      if (name[0])
         //currently returns the full name if no short name is available.
         //I can't say if this is according to the spec.
         return JNI::String(*shortName ? shortName : name);
   }
   return JNI::String("");
}

jint Java_org_dvb_si_SINetworkImpl_networkId(JNIEnv* env, jobject obj, jobject nativeData) {
   std::list<DvbSi::NIT> *list=StdListDvbsiNitNativeData(nativeData);
   //printf("SIZE of nit list %d\n", list->size());
   if (list->begin() != list->end())
      return list->begin()->getNetworkId();
   else
      return -1;
}



/*** SIBouquetImpl ***/

void Java_org_dvb_si_SIBouquetImpl_cleanUpStdList(JNIEnv* env, jobject obj, jobject nativeData) {
   delete StdListDvbsiBatNativeData(nativeData).Get();
}

jshortArray Java_org_dvb_si_SIBouquetImpl_descriptorTags(JNIEnv* env, jobject obj, jobject nativeData) {
   std::list<DvbSi::BAT> *list=StdListDvbsiBatNativeData(nativeData);
   int count=0;
   for (std::list<DvbSi::BAT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      DvbSi::BAT nit(*stlit);
      count+=nit.commonDescriptors.getNumberOfDescriptors();
   }
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   jshort *current=ar;
   
   for (std::list<DvbSi::BAT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      DvbSi::BAT nit(*stlit);
      current+=nit.commonDescriptors.getDescriptorTags<jshort>(current);
   }
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}

jstring Java_org_dvb_si_SIBouquetImpl_name(JNIEnv* env, jobject obj, jobject nativeData) {
   std::list<DvbSi::BAT> *list=StdListDvbsiBatNativeData(nativeData);
   for (std::list<DvbSi::BAT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      char name[256];
      stlit->getBouquetName(name, sizeof(name));
      if (name[0])
         return JNI::String(name);
   }
   return JNI::String("");
}

jstring Java_org_dvb_si_SIBouquetImpl_shortBouquetName(JNIEnv* env, jobject obj, jobject nativeData) {
   std::list<DvbSi::BAT> *list=StdListDvbsiBatNativeData(nativeData);
   for (std::list<DvbSi::BAT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      char name[256];
      char shortName[256];
      stlit->getBouquetName(name, shortName,sizeof(name), sizeof(shortName));
      if (name[0])
         //currently returns the full name if no short name is available.
         //I can't say if this is according to the spec.
         return JNI::String(*shortName ? shortName : name);
   }
   return JNI::String("");
}

jint Java_org_dvb_si_SIBouquetImpl_networkId(JNIEnv* env, jobject obj, jobject nativeData) {
   std::list<DvbSi::BAT> *list=StdListDvbsiBatNativeData(nativeData);
   if (list->begin() != list->end())
      return list->begin()->getBouquetId();
   else
      return -1;
}



/*** SITransportStreamDescriptionImpl ***/

void Java_org_dvb_si_SITransportStreamDescriptionImpl_cleanUpStdList(JNIEnv* env, jobject obj, jobject nativeData) {
   delete StdListDvbsiTsdtNativeData(nativeData).Get();
}

jshortArray Java_org_dvb_si_SITransportStreamDescriptionImpl_descriptorTags(JNIEnv* env, jobject obj, jobject nativeData) {
   std::list<DvbSi::TSDT> *list=StdListDvbsiTsdtNativeData(nativeData);
   int count=0;
   for (std::list<DvbSi::TSDT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      //DvbSi::TSDT tsdt(*stlit);
      count+=stlit->transportStreamDescriptors.getNumberOfDescriptors();
   }
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   jshort *current=ar;
   
   for (std::list<DvbSi::TSDT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      DvbSi::TSDT tsdt(*stlit);
      current+=stlit->transportStreamDescriptors.getDescriptorTags<jshort>(current);
   }
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}



/*** SIEventImpl ***/

jbyteArray Java_org_dvb_si_SIEventImpl_getContentNibbles(JNIEnv* env, jobject obj, jobject nativeData) {
   int count;
   SI::EightBit *array;
   if ( (array=DvbsiEitEventNativeData(nativeData).Get()->getContentNibbleLevel1(count)) ) {
      jbyteArray ar=env->NewByteArray(count);
      env->SetByteArrayRegion(ar, 0, count, (jbyte *)array);
      return ar;
   }
   else return env->NewByteArray(0);
}

jlong Java_org_dvb_si_SIEventImpl_getDuration(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiEitEventNativeData(nativeData).Get()->getDuration();
}

jint Java_org_dvb_si_SIEventImpl_getEventID(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiEitEventNativeData(nativeData).Get()->getEventId();
}

jboolean Java_org_dvb_si_SIEventImpl_getFreeCAMode(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiEitEventNativeData(nativeData).Get()->getFreeCaMode();
}

jbyteArray Java_org_dvb_si_SIEventImpl_getLevel1ContentNibbles(JNIEnv* env, jobject obj, jobject nativeData) {
   int count;
   SI::EightBit *array;
   if ( (array=DvbsiEitEventNativeData(nativeData).Get()->getContentNibbles(count)) ) {
      jbyteArray ar=env->NewByteArray(count);
      env->SetByteArrayRegion(ar, 0, count, (jbyte *)array);
      return ar;
   }
   else return env->NewByteArray(0);
}

jstring Java_org_dvb_si_SIEventImpl_getName(JNIEnv* env, jobject obj, jobject nativeData) {
   char buf[256];
   if (DvbsiEitEventNativeData(nativeData).Get()->getEventName(buf, sizeof(buf))) {
      return JNI::String(buf);
   }
   return JNI::String("");
}

jint Java_org_dvb_si_SIEventImpl_getOriginalNetworkID(JNIEnv* env, jobject obj, jobject nativeRequestWrapperData) {
   RequestWrapper *wr=RequestWrapperNativeData(nativeRequestWrapperData);
   if (typeid(*wr)==typeid(SingleEventRequestWrapper))
      return ((SingleEventRequestWrapper *)wr)->req->getOriginalNetworkId();
   else if (typeid(*wr)==typeid(EventRequestWrapper))
      return ((EventRequestWrapper *)wr)->req->getOriginalNetworkId();
   else //if (typeid(*wr)==typeid(TimeEventRequestWrapper))
      return ((TimeEventRequestWrapper *)wr)->req->getOriginalNetworkId();
   //else
    //  return -1;
}

jbyte Java_org_dvb_si_SIEventImpl_getRunningStatus(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiEitEventNativeData(nativeData).Get()->getRunningStatus();
}

jint Java_org_dvb_si_SIEventImpl_getServiceID(JNIEnv* env, jobject obj, jobject nativeRequestWrapperData) {
   RequestWrapper *wr=RequestWrapperNativeData(nativeRequestWrapperData);
   if (typeid(*wr)==typeid(SingleEventRequestWrapper))
      return ((SingleEventRequestWrapper *)wr)->req->getServiceId();
   else if (typeid(*wr)==typeid(EventRequestWrapper))
      return ((EventRequestWrapper *)wr)->req->getServiceId();
   else //if (typeid(*wr)==typeid(TimeEventRequestWrapper))
      return ((TimeEventRequestWrapper *)wr)->req->getServiceId();
   //else
    //  return -1;
}

jbyteArray Java_org_dvb_si_SIEventImpl_getShortDescription(JNIEnv* env, jobject obj, jobject nativeData) {
   char buf[256];
   if (DvbsiEitEventNativeData(nativeData).Get()->getShortDescription(buf, sizeof(buf))) {
      return JNI::String(buf);
   }
   return JNI::String("");
}

jbyteArray Java_org_dvb_si_SIEventImpl_getShortEventName(JNIEnv* env, jobject obj, jobject nativeData) {
   char buf[256];
   char shortVersion[256];
   if (DvbsiEitEventNativeData(nativeData).Get()->getEventName(buf, shortVersion, sizeof(buf), sizeof(shortVersion))) {
      return JNI::String(*shortVersion ? shortVersion : buf);
   }
   return JNI::String("");
}

jlong Java_org_dvb_si_SIEventImpl_getStartTime(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiEitEventNativeData(nativeData).Get()->getStartTime();
}

jint Java_org_dvb_si_SIEventImpl_getTransportStreamID(JNIEnv* env, jobject obj, jobject nativeRequestWrapperData) {
   RequestWrapper *wr=RequestWrapperNativeData(nativeRequestWrapperData);
   //printf("%s %s %s\n", typeid(*wr).name(), typeid(SingleEventRequestWrapper).name(), typeid(EventRequestWrapper*).name());
   if (typeid(*wr)==typeid(SingleEventRequestWrapper))
      return ((SingleEventRequestWrapper *)wr)->req->getTransportStreamId();
   else if (typeid(*wr)==typeid(EventRequestWrapper))
      return ((EventRequestWrapper *)wr)->req->getTransportStreamId();
   else //if (typeid(*wr)==typeid(TimeEventRequestWrapper))
      return ((TimeEventRequestWrapper *)wr)->req->getTransportStreamId();
   //else
   //   return -1;
}

jshortArray Java_org_dvb_si_SIEventImpl_descriptorTags(JNIEnv* env, jobject obj, jobject nativeData) {
   DvbSi::EIT::Event *event=DvbsiEitEventNativeData(nativeData);
   int count=event->eventDescriptors.getNumberOfDescriptors();
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   event->eventDescriptors.getDescriptorTags<jshort>(ar);
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}



/*** SIServiceImpl ***/
jboolean Java_org_dvb_si_SIServiceImpl_getEITPresentFollowingFlag(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiSdtServiceNativeData(nativeData).Get()->getEITpresentFollowingFlag();
}

jboolean Java_org_dvb_si_SIServiceImpl_getEITScheduleFlag(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiSdtServiceNativeData(nativeData).Get()->getEITscheduleFlag();
}

jboolean Java_org_dvb_si_SIServiceImpl_getFreeCAMode(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiSdtServiceNativeData(nativeData).Get()->getFreeCaMode();
}

jstring Java_org_dvb_si_SIServiceImpl_getName(JNIEnv* env, jobject obj, jobject nativeData) {
   char buf[256];
   if (DvbsiSdtServiceNativeData(nativeData).Get()->getServiceName(buf, sizeof(buf))) {
      return JNI::String(buf);
   }
   return JNI::String("");
}

jint Java_org_dvb_si_SIServiceImpl_getOriginalNetworkID(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiSdtServiceNativeData(nativeData).Get()->getOriginalNetworkId();
}

jstring Java_org_dvb_si_SIServiceImpl_getProviderName(JNIEnv* env, jobject obj, jobject nativeData) {
   char buf[256];
   if (DvbsiSdtServiceNativeData(nativeData).Get()->getProviderName(buf, sizeof(buf))) {
      return JNI::String(buf);
   }
   return JNI::String("");
}

jbyte Java_org_dvb_si_SIServiceImpl_getRunningStatus(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiSdtServiceNativeData(nativeData).Get()->getRunningStatus();
}

jint Java_org_dvb_si_SIServiceImpl_getServiceID(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiSdtServiceNativeData(nativeData).Get()->getServiceId();
}

jstring Java_org_dvb_si_SIServiceImpl_getShortProviderName(JNIEnv* env, jobject obj, jobject nativeData) {
   char buf[256];
   char shortVersion[256];
   if (DvbsiSdtServiceNativeData(nativeData).Get()->getProviderName(buf, shortVersion, sizeof(buf), sizeof(shortVersion))) {
      return JNI::String(*shortVersion ? shortVersion : buf);
   }
   return JNI::String("");
}

jbyteArray Java_org_dvb_si_SIServiceImpl_getShortServiceName(JNIEnv* env, jobject obj, jobject nativeData) {
   char buf[256];
   char shortVersion[256];
   if (DvbsiSdtServiceNativeData(nativeData).Get()->getServiceName(buf, shortVersion, sizeof(buf), sizeof(shortVersion))) {
      return JNI::String(*shortVersion ? shortVersion : buf);
   }
   return JNI::String("");
}

jshort Java_org_dvb_si_SIServiceImpl_getSIServiceType(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiSdtServiceNativeData(nativeData).Get()->getServiceType();
}

jint Java_org_dvb_si_SIServiceImpl_getTransportStreamID(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiSdtServiceNativeData(nativeData).Get()->getTransportStreamId();
}

jshortArray Java_org_dvb_si_SIServiceImpl_descriptorTags(JNIEnv* env, jobject obj, jobject nativeData) {
   DvbSi::SDT::Service *service=DvbsiSdtServiceNativeData(nativeData);
   int count=service->serviceDescriptors.getNumberOfDescriptors();
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   service->serviceDescriptors.getDescriptorTags<jshort>(ar);
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}



/*** SITransportStreamImpl ***/
jshortArray Java_org_dvb_si_SITransportStreamImpl_descriptorTags(JNIEnv* env, jobject obj, jobject nativeData) {
   DvbSi::NIT::TransportStream *ts=DvbsiNitTransportStreamNativeData(nativeData);
   int count=ts->transportStreamDescriptors.getNumberOfDescriptors();
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   ts->transportStreamDescriptors.getDescriptorTags<jshort>(ar);
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}

jint Java_org_dvb_si_SITransportStreamImpl_getNetworkID(JNIEnv* env, jobject obj, jobject nativeRequestWrapperData) {
   RequestWrapper *wr=RequestWrapperNativeData(nativeRequestWrapperData);
   if (typeid(*wr)==typeid(TransportStreamRequestWrapper))
      return ((TransportStreamRequestWrapper*)wr)->req->getNetworkId();
   else if (typeid(*wr)==typeid(TransportStreamBATRequestWrapper))
      return ((TransportStreamBATRequestWrapper*)wr)->req->getBouquetId();
   else //if (typeid(*wr)==typeid(ActualTransportStreamRequestWrapper))
      return ((ActualTransportStreamRequestWrapper*)wr)->req->getNetworkId();
   //else
   //   return -1;
}

jint Java_org_dvb_si_SITransportStreamImpl_getOriginalNetworkID(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiNitTransportStreamNativeData(nativeData).Get()->getOriginalNetworkId();
}

jint Java_org_dvb_si_SITransportStreamImpl_getTransportStreamID(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiNitTransportStreamNativeData(nativeData).Get()->getTransportStreamId();
}



/*** SITimeImpl ***/

jint Java_org_dvb_si_SITimeImpl_getUTCTime(JNIEnv* env, jobject obj, jobject nativeData) {
   SI::Object *obje=SiObjectNativeData(nativeData);
   if (typeid(*obje)==typeid(SI::TDT))
      return SiTdtNativeData(nativeData).Get()->getTime();
   else //if (typeid(*obj)==typeid(SI::TOT))
      return SiTotNativeData(nativeData).Get()->getTime();
}

jshortArray Java_org_dvb_si_SITimeImpl_descriptorTags(JNIEnv* env, jobject obj, jobject nativeData) {
   SI::Object *obje=SiObjectNativeData(nativeData);
   if (typeid(*obje)==typeid(SI::TDT))
      return env->NewShortArray(0);
   
   SI::TOT *tot=SiTotNativeData(nativeData);
   int count=tot->descriptorLoop.getNumberOfDescriptors();
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   tot->descriptorLoop.getDescriptorTags<jshort>(ar);
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}



/*** PMTServicesImpl ***/

jint Java_org_dvb_si_PMTServiceImpl_getPcrPid(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiPmtNativeData(nativeData).Get()->getPCRPid();
}

jint Java_org_dvb_si_PMTServiceImpl_getServiceID(JNIEnv* env, jobject obj, jobject nativeData) {
   printf("PMTServiceImpl_getServiceID, PMT element is %p\n", DvbsiPmtNativeData(nativeData).Get());
   return DvbsiPmtNativeData(nativeData).Get()->getServiceId();
}

jshortArray Java_org_dvb_si_PMTServiceImpl_descriptorTags(JNIEnv* env, jobject obj, jobject nativeData) {
   DvbSi::PMT *pmt=DvbsiPmtNativeData(nativeData);
   int count=pmt->commonDescriptors.getNumberOfDescriptors();
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   pmt->commonDescriptors.getDescriptorTags<jshort>(ar);
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}


/*** PMTElementaryStreamImpl ***/

jint Java_org_dvb_si_PMTElementaryStreamImpl_getComponentTag(JNIEnv* env, jobject obj, jobject nativeData) {
  return DvbsiPmtStreamNativeData(nativeData).Get()->getComponentTag();
}

jshort Java_org_dvb_si_PMTElementaryStreamImpl_getElementaryPID(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiPmtStreamNativeData(nativeData).Get()->getPid();
}

jint Java_org_dvb_si_PMTElementaryStreamImpl_getServiceID(JNIEnv* env, jobject obj, jobject nativeRequestData) {
   return ((PMTElementaryStreamsRequestWrapper *)RequestWrapperNativeData(nativeRequestData).Get())->req->getServiceId();
}

jbyte Java_org_dvb_si_PMTElementaryStreamImpl_getStreamType(JNIEnv* env, jobject obj, jobject nativeData) {
   return DvbsiPmtStreamNativeData(nativeData).Get()->getStreamType();
}

jint Java_org_dvb_si_PMTElementaryStreamImpl_getTransportStreamID(JNIEnv* env, jobject obj, jobject nativeRequestData) {
   return ((PMTElementaryStreamsRequestWrapper *)RequestWrapperNativeData(nativeRequestData).Get())->req->getTransportStreamId();
}
                          
jshortArray Java_org_dvb_si_PMTElementaryStreamImpl_descriptorTags(JNIEnv* env, jobject obj, jobject nativeData) {
   DvbSi::SDT::Service *service=DvbsiSdtServiceNativeData(nativeData);
   int count=service->serviceDescriptors.getNumberOfDescriptors();
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   service->serviceDescriptors.getDescriptorTags<jshort>(ar);
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}


/*** Descriptor ***/
jbyte Java_org_dvb_si_Descriptor_getData(JNIEnv* env, jobject obj, jobject nativeData, jint index) {
   return SiDescriptorNativeData(nativeData).Get()->getData()[index];
}

jbyteArray Java_org_dvb_si_Descriptor_getDataArray(JNIEnv* env, jobject obj, jobject nativeData) {
   SI::Descriptor *d=SiDescriptorNativeData(nativeData);
   int count=d->getLength();
   jbyteArray ar=env->NewByteArray(count);
   env->SetByteArrayRegion(ar, 0, count, (jbyte *)d->getData().getData(sizeof(SI::DescriptorHeader)));
   return ar;
}

jshort Java_org_dvb_si_Descriptor_getLength(JNIEnv* env, jobject obj, jobject nativeData) {
   //libsi always returns the size including the header, but the java API only wants
   //the content length, so the sizeof(DescriptorHeader) must be subtracted.
   return SiDescriptorNativeData(nativeData).Get()->getLength()-sizeof(SI::DescriptorHeader);
}

jshort Java_org_dvb_si_Descriptor_getTag(JNIEnv* env, jobject obj, jobject nativeData) {
   return SiDescriptorNativeData(nativeData).Get()->getDescriptorTag();
}



//jint Java_org_dvb_si_SIDatabaseRequest_Iterator_nextElement(JNIEnv* env, jobject obj, jlong nativeData) {
//}


/*
Limited version for VDR < 1.3.0. Rewrite necessary for VDR 1.3.x/1.4.x/libdvbsi.

jint Java_org_dvb_si_SIRequestService_channel(JNIEnv* env, jobject obj, jint source, jint sid) {
   return (int)Channels.GetByServiceID(source, sid);
}

jint Java_org_dvb_si_SIRequestService_ca(JNIEnv* env, jobject obj, jint nativeData) {
   return ((cChannel *)nativeData)->Ca();
}

jbyteArray Java_org_dvb_si_SIRequestService_name(JNIEnv* env, jobject obj, jint nativeData) {
   return copyConstCharIntoByteArray(env, ((cChannel *)nativeData)->Name());
}





jobject Java_org_dvb_si_DvbSIEvent_startTime(JNIEnv* env, jobject obj, jint nativeData) {
   return ((cEventInfo *)nativeData)->GetTime();
}

jint Java_org_dvb_si_DvbSIEvent_channelCa(JNIEnv* env, jobject obj, jint nativeData) {
   tChannelID id=((cEventInfo *)nativeData)->GetChannelID();
   cChannel *c=Channels.GetByChannelID(id);
   if (c)
      return c->Ca();
   else
      return 0;
}

jlong Java_org_dvb_si_DvbSIEvent_duration(JNIEnv* env, jobject obj, jint nativeData) {
   return ((cEventInfo *)nativeData)->GetDuration();
}

jint Java_org_dvb_si_DvbSIEvent_eId(JNIEnv* env, jobject obj, jint nativeData) {
   return ((cEventInfo *)nativeData)->GetEventID();
}

jbyteArray Java_org_dvb_si_DvbSIEvent_name(JNIEnv* env, jobject obj, jint nativeData) {
   return copyConstCharIntoByteArray(env, ((cEventInfo *)nativeData)->GetTitle());
}

jbyteArray Java_org_dvb_si_DvbSIEvent_extendedDescription(JNIEnv* env, jobject obj, jint nativeData) {
   return copyConstCharIntoByteArray(env, ((cEventInfo *)nativeData)->GetExtendedDescription());
}





jint Java_org_dvb_si_SIRequestEvent_channel(JNIEnv* env, jobject obj, jint source, jint sid) {
   return (int)Channels.GetByServiceID(source, sid);
}

//the following 4 methods cannot be used as is,
//they require a certain order.
cMutexLock *mutexLock=0;

jint Java_org_dvb_si_SIRequestEvent_eventInfoPresent(JNIEnv* env, jobject obj, jint nativeData) {
   if (!mutexLock)
      return 0;
   const cSchedules *schedules=cSIProcessor::Schedules(*mutexLock);
   tChannelID id=((cChannel *)nativeData)->GetChannelID();
   const cSchedule *s=schedules->GetSchedule(id);
   if (s)
      return (int)s->GetPresentEvent();
   return 0;
}

jint Java_org_dvb_si_SIRequestEvent_eventInfoFollowing(JNIEnv* env, jobject obj, jint nativeData) {
   if (!mutexLock)
      return 0;
   const cSchedules *schedules=cSIProcessor::Schedules(*mutexLock);
   tChannelID id=((cChannel *)nativeData)->GetChannelID();
   const cSchedule *s=schedules->GetSchedule(id);
   if (s)
      return (int)s->GetFollowingEvent();
   return 0;
}

jboolean Java_org_dvb_si_SIRequestEvent_LockSIProcessor(JNIEnv* env, jobject obj) {
   if (!mutexLock)
      return false;
   mutexLock=new cMutexLock();
   return true;
}

void Java_org_dvb_si_SIRequestEvent_ReleaseSIProcessor(JNIEnv* env, jobject obj) {
   delete mutexLock;
   mutexLock=0;
}

*/

}
