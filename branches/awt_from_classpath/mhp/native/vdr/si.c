
#include <typeinfo>
#include <list>

#include <libsi/descriptor.h>

#include <libdvbsi/database.h>
#include <libjava/jniinterface.h>
#include <libjava/jnithread.h>

//These classes, all inheriting from RequestWrapper, form a native layer for org.dvb.si
//encapsulating DvbSi objects that can't be stored in Java and using templates
//to access the DvbSi Request (which themselves are mostly templates)

class RequestWrapper : public DvbSi::Listener {
public:
   virtual ~RequestWrapper() {}
   virtual bool hasMoreElements(jlong iteratorData) = 0;
   virtual int numberOfRemainingObjects(jlong iteratorData) = 0;
   virtual jlong nextElement(jlong iteratorData) = 0;
   virtual jlong getNewIterator() = 0;
   virtual void DeleteIterator(jlong iteratorData) = 0;
   
   virtual int getResultCode() = 0;
   virtual void getDataSource(DvbSi::DataSource &source) = 0;
   
   virtual bool isAvailableInCache() = 0;
   virtual bool CancelRequest() = 0;
protected:
   JNI::GlobalObjectRef jniobject;
   JNI::InstanceMethod jnimethod;
};

//T is a DvbSi::Request
template <class R, class T = typename R::objectType> class SpecializedRequestWrapper : public RequestWrapper {
public:
   typedef T objectType;
   SpecializedRequestWrapper(jobject javaRequest)
     : req(0) {
      jniobject.SetObject(javaRequest);
      if (!jnimethod.SetMethodWithArguments(jniobject.GetClass(), "Result", JNI::Void, 1, JNI::Long))
         printf("SetMethod failed!\n");     
   }
   
   virtual ~SpecializedRequestWrapper() {
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
      printf("Received result %d\n", re->getResultCode());
      JNI::ReturnType ret;
      JNI::Thread::CheckAttachThread();
      jnimethod.CallMethod((jobject)jniobject, ret, (jlong )this);
      //this holds reference to jniobject, and this is deleted when
      //jniobject is finalized, so if we don't delete the reference
      //both won't be deleted.
      jniobject.Delete();
   }
   
   struct STLIteratorData {
      STLIteratorData() : remainingObjects(0) {}
      STLIteratorData(typename R::iterator it, int remainingObjects) : it(it), remainingObjects(remainingObjects) {}
      typename R::iterator it;
      int remainingObjects;
   };
};

//R is a DvbSi::Request which inherits ListSecondaryRequest or directly TableFilterRequest
template <class R> class ListRequestWrapper : public SpecializedRequestWrapper<R> {
public:
   typedef typename SpecializedRequestWrapper<R>::objectType objectType;
   typedef typename SpecializedRequestWrapper<R>::STLIteratorData STLIteratorData;
   ListRequestWrapper(jobject javaReq)
    : SpecializedRequestWrapper<R>(javaReq) {}
    
   virtual bool hasMoreElements(jlong iteratorData) {
      return ((STLIteratorData*)iteratorData)->remainingObjects>0;
   }
   
   virtual int numberOfRemainingObjects(jlong iteratorData) {
      return ((STLIteratorData*)iteratorData)->remainingObjects;
   }
   
   virtual jlong nextElement(jlong iteratorData) {
      if (this->req && hasMoreElements(iteratorData)) {
         ((STLIteratorData*)iteratorData)->remainingObjects--;
         return (jlong )new objectType( *( ((STLIteratorData*)iteratorData)->it )++ );
      } else
         return (jlong )0;
   }
   
   virtual jlong getNewIterator() {
      return this->req ? (jlong )new STLIteratorData(this->req->list.begin(), this->req->list.size())
               :  (jlong )new STLIteratorData();
   }
   
   virtual void DeleteIterator(jlong iteratorData) {
      delete (STLIteratorData*)iteratorData;
   }
   /*virtual void Result(DvbSi::Request *re) {
      if (req->getResultCode()==DvbSi::ResultCodeSuccess) {
         remainingObjects=req->list.size();
         it=req->list.begin();
      }
      SpecializedRequestWrapper<R>::Result(req);
   }*/
protected:
   //int remainingObjects;
};

//R is a DvbSi::Request which inherits TableFilterTrackerRequest
template <class R> class SubtableRequestWrapper : public SpecializedRequestWrapper<R, std::list<typename R::objectType> > {
public:
   typedef std::list<typename R::objectType> List;
   typedef typename SpecializedRequestWrapper<R, std::list<typename R::objectType> >::STLIteratorData STLIteratorData;
   
   SubtableRequestWrapper(jobject javaReq)
    : SpecializedRequestWrapper<R, List>(javaReq) {}
    
   virtual bool hasMoreElements(jlong iteratorData) {
      return ((STLIteratorData*)iteratorData)->remainingObjects>0;
   }
   
   virtual int numberOfRemainingObjects(jlong iteratorData) {
      return ((STLIteratorData*)iteratorData)->remainingObjects;
   }
   
   virtual jlong nextElement(jlong iteratorData) {
      if (this->req && hasMoreElements(iteratorData)) {
         ((STLIteratorData*)iteratorData)->remainingObjects--;
         //returns the iterator that points to the subtable that comes next in the list
         //to the one given as the argument
         typename R::iterator newSubtable=this->req->getNextSubtable(((STLIteratorData*)iteratorData)->it);
         //create a list of the current subtable, newSubtable points to the next,
         //creates list from including the first to excluding the second iterator
         List *ret=new List((((STLIteratorData*)iteratorData)->it), newSubtable);
         //set the iteratorData's iterator to the next subtable (or list->end()) for the next call
         (((STLIteratorData*)iteratorData)->it)=newSubtable;
         return (jlong )ret;
      } else
         return 0;
   }
   
   virtual jlong getNewIterator() {
      return this->req ? (jlong )new STLIteratorData(this->req->list.begin(), this->req->getNumberOfSubtables())
               :  (jlong )new STLIteratorData();
   }
   
   virtual void DeleteIterator(jlong iteratorData) {
      delete (STLIteratorData*)iteratorData;
   }
   /*virtual void Result(DvbSi::Request *re) {
      if (req->getResultCode()==DvbSi::ResultCodeSuccess) {
         remainingObjects=req->getNumberOfSubtables();
         it=req->list.begin();
      }
      SpecializedRequestWrapper<R, List>::Result(req);
   }*/
protected:
   //typename R::iterator it;
   //int remainingObjects;
};

//R is a plain FilterRequest or SecondaryRequest which implements getResultObject(),
template <class R, class T = typename R::objectType> class SingleRequestWrapper : public SpecializedRequestWrapper<R> {
public:
   typedef typename SpecializedRequestWrapper<R>::objectType objectType;
   
   SingleRequestWrapper(jobject javaReq)
    : SpecializedRequestWrapper<R>(javaReq) {}
   virtual bool hasMoreElements(jlong iteratorData) {
      return *(bool *)iteratorData;
   }
   virtual int numberOfRemainingObjects(jlong iteratorData) {
      return (*(bool *)iteratorData) ? 1 : 0;
   }
   virtual jlong nextElement(jlong iteratorData) {
      if (this->req && (*(bool *)iteratorData))
         (*(bool *)iteratorData)=false;
      else
         return 0;
      return (jlong )new T(this->req->getResultObject());
   }
   virtual jlong getNewIterator() {
      return (jlong )new bool(true);
   }
   virtual void DeleteIterator(jlong iteratorData) {
      delete (bool*)iteratorData;
   }
   /*virtual void Result(DvbSi::Request *req) {
      if (req->getResultCode()==DvbSi::ResultCodeSuccess) {
         elementRemaing=true;
      }
      SpecializedRequestWrapper<R>::Result(req);
   }*/
protected:
};


class DescriptorRequestWrapper : public ListRequestWrapper<DvbSi::DescriptorRequest> {
public:
   DescriptorRequestWrapper(jobject javaReq)
    : ListRequestWrapper<DvbSi::DescriptorRequest>(javaReq) {}
    
   virtual jlong nextElement(jlong iteratorData) {
      if (req && hasMoreElements(iteratorData)) {
         ((STLIteratorData*)iteratorData)->remainingObjects--;
         //we can't create a copy of the descriptor, subclass is unknown,
         //and doing "new SI::Descriptor *()" - creating a pointer! - is pretty useless.
         //So the java code will have to keep a reference to the RequestWrapper,
         //which it does anyway.
         return (jlong )( *( ((STLIteratorData*)iteratorData)->it )++ );
      } else
         return (jlong )0;
   }
};


typedef SubtableRequestWrapper<DvbSi::NetworksRequest> NetworksRequestWrapper;
typedef SubtableRequestWrapper<DvbSi::BouquetsRequest> BouquetsRequestWrapper;

typedef SubtableRequestWrapper<DvbSi::TransportStreamDescriptionRequest> TransportStreamDescriptionRequestWrapper;

typedef ListRequestWrapper<DvbSi::ServicesRequest> ServicesRequestWrapper;
typedef ListRequestWrapper<DvbSi::ActualServicesRequest> ActualServicesRequestWrapper;

typedef ListRequestWrapper<DvbSi::ScheduleEventRequest> EventRequestWrapper;
typedef SingleRequestWrapper<DvbSi::PresentFollowingEventRequest> SingleEventRequestWrapper;

typedef ListRequestWrapper<DvbSi::TransportStreamRequest> TransportStreamRequestWrapper;
typedef ListRequestWrapper<DvbSi::ActualTransportStreamRequest> ActualTransportStreamRequestWrapper;
typedef ListRequestWrapper<DvbSi::TransportStreamBATRequest> TransportStreamBATRequestWrapper;

typedef SingleRequestWrapper<DvbSi::TOTRequest> TOTRequestWrapper;
typedef SingleRequestWrapper<DvbSi::TDTRequest> TDTRequestWrapper;

typedef ListRequestWrapper<DvbSi::PMTServicesRequest> PMTServicesRequestWrapper;
typedef ListRequestWrapper<DvbSi::PMTElementaryStreamRequest> PMTElementaryStreamsRequestWrapper;



extern "C" {

jbyteArray copyConstCharIntoByteArray(JNIEnv* env, const char *str);



/*** SIDatabaseRequest ***/

jint Java_org_dvb_si_SIDatabase_numDatabases(JNIEnv* env, jclass clazz) {
   return DvbSi::Database::getNumberOfValidDatabases();
}

void Java_org_dvb_si_SIDatabase_checkDatabases(JNIEnv* env, jclass clazz) {
   //make sure that all devices have been tested so that numDatabases returns a valid number
   for (int i=0;i<DvbSi::Database::getNumberOfDatabases();i++)
      DvbSi::Database::getDatabase(i);
}

jlong Java_org_dvb_si_SIDatabase_databasePointer(JNIEnv* env, jclass clazz, jint index) {
   return (jlong )DvbSi::Database::getDatabase(index);
}

jlong Java_org_dvb_si_SIDatabase_databaseForChannel(JNIEnv* env, jclass clazz, jint nid, jint tid, jint sid) {
   return (jlong )DvbSi::Database::getDatabaseForChannel(nid, tid, sid, true);
}

void Java_org_dvb_si_SIDatabaseRequest_cleanUp(JNIEnv* env, jobject obj, jlong nativeData) {
   delete (RequestWrapper *)nativeData;
}

jint Java_org_dvb_si_SIDatabaseRequest_resultCode(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((RequestWrapper *)nativeData)->getResultCode();
}

jlong Java_org_dvb_si_SIDatabaseRequest_ActualNetworkRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   NetworksRequestWrapper *wrapper=new NetworksRequestWrapper(obj);
   wrapper->req=db->retrieveActualNetwork(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_ActualServicesRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   ActualServicesRequestWrapper *wrapper=new ActualServicesRequestWrapper(obj);
   wrapper->req=db->retrieveActualServices(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_ActualTransportStreamRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   ActualTransportStreamRequestWrapper *wrapper=new ActualTransportStreamRequestWrapper(obj);
   wrapper->req=db->retrieveActualTransportStream(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_PMTElementaryStreamsRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jint serviceId, jintArray componentTags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   PMTElementaryStreamsRequestWrapper *wrapper=new PMTElementaryStreamsRequestWrapper(obj);
   
   jint *ar=env->GetIntArrayElements(componentTags, 0);
   bool any= (env->GetArrayLength(componentTags)==1) && (ar[0]==-1);
   
   wrapper->req=db->retrievePMTElementaryStreams(wrapper, serviceId, any ? new DvbSi::IdTracker(): new DvbSi::IdTracker(ar),
               (DvbSi::RetrieveMode)retrieveMode);
               
   env->ReleaseIntArrayElements(componentTags, ar, JNI_ABORT);
   
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_PMTServicesRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jint serviceId) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   PMTServicesRequestWrapper *wrapper=new PMTServicesRequestWrapper(obj);
   wrapper->req=db->retrievePMTServices(wrapper, serviceId==-1 ? new DvbSi::IdTracker(): new DvbSi::IdTracker(serviceId),
               (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_BouquetsRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jint bouquetId) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   BouquetsRequestWrapper *wrapper=new BouquetsRequestWrapper(obj);
   wrapper->req=db->retrieveBouquets(wrapper, new DvbSi::IdTracker(bouquetId), (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_NetworksRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jint networkId) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   NetworksRequestWrapper *wrapper=new NetworksRequestWrapper(obj);
   if (networkId==-1)
      wrapper->req=db->retrieveNetworks(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   else
      wrapper->req=db->retrieveNetworks(wrapper, new DvbSi::IdTracker(networkId), (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_ServicesRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jint originalNetworkId, jint transportStreamId, jint serviceId) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   ServicesRequestWrapper *wrapper=new ServicesRequestWrapper(obj);
   wrapper->req=db->retrieveServices(wrapper, originalNetworkId,
               transportStreamId==-1 ? new DvbSi::IdTracker(): new DvbSi::IdTracker(transportStreamId),
               serviceId==-1 ? new DvbSi::IdTracker(): new DvbSi::IdTracker(serviceId),
               (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_TDTRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   TDTRequestWrapper *wrapper=new TDTRequestWrapper(obj);
   wrapper->req=db->retrieveTDT(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_TOTRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   TOTRequestWrapper *wrapper=new TOTRequestWrapper(obj);
   wrapper->req=db->retrieveTOT(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_TransportStreamDescriptionRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   TransportStreamDescriptionRequestWrapper *wrapper=new TransportStreamDescriptionRequestWrapper(obj);
   wrapper->req=db->retrieveTransportStreamDescription(wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_TransportStreamRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jlong nativeList, jlong nativeRequest) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   
   TransportStreamRequestWrapper *wrapper=new TransportStreamRequestWrapper(obj);
   
   wrapper->req=new DvbSi::TransportStreamRequest(((NetworksRequestWrapper *)nativeRequest)->req, (std::list<DvbSi::NIT> *)nativeList, db, wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_TransportStreamRequestBAT(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jlong nativeList, jlong nativeRequest) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   
   TransportStreamBATRequestWrapper *wrapper=new TransportStreamBATRequestWrapper(obj);
   
   wrapper->req=new DvbSi::TransportStreamBATRequest(((BouquetsRequestWrapper *)nativeRequest)->req, (std::list<DvbSi::BAT> *)nativeList, db, wrapper, (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_PresentFollowingEventRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jboolean presentOrFollowing, jint tid, jint sid) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   SingleEventRequestWrapper *wrapper=new SingleEventRequestWrapper(obj);
   wrapper->req=db->retrievePresentFollowingEvent(wrapper, tid, sid, presentOrFollowing, (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}

jlong Java_org_dvb_si_SIDatabaseRequest_ScheduledEventsRequest(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jint tid, jint sid) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   EventRequestWrapper *wrapper=new EventRequestWrapper(obj);
   wrapper->req=db->retrieveScheduledEvents(wrapper, tid, sid, (DvbSi::RetrieveMode)retrieveMode);
   return (jlong)wrapper;
}


jlong Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestNetwork(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jlong nativeList, jlong nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   std::list<DvbSi::NIT> *list=(std::list<DvbSi::NIT> *)nativeList;
   DvbSi::DataSource source;
   ((RequestWrapper *)nativeRequest)->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   for (std::list<DvbSi::NIT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      DvbSi::NIT nit(*stlit);
      //TODO: honor tags
      req->Add(nit.commonDescriptors);
   }
   db->DispatchResult(req);
   return (jlong)wrapper;
}


jlong Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestBouquet(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jlong nativeList, jlong nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   std::list<DvbSi::BAT> *list=(std::list<DvbSi::BAT> *)nativeList;
   DvbSi::DataSource source;
   ((RequestWrapper *)nativeRequest)->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   for (std::list<DvbSi::BAT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      DvbSi::BAT nit(*stlit);
      //TODO: honor tags
      req->Add(nit.commonDescriptors);
   }
   db->DispatchResult(req);
   return (jlong)wrapper;
}


jlong Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestTransportStreamDescription(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jlong nativeList, jlong nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   std::list<DvbSi::TSDT> *list=(std::list<DvbSi::TSDT> *)nativeList;
   DvbSi::DataSource source;
   ((RequestWrapper *)nativeRequest)->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   for (std::list<DvbSi::TSDT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      DvbSi::TSDT tsdt(*stlit);
      //TODO: honor tags
      req->Add(tsdt.transportStreamDescriptors);
   }
   db->DispatchResult(req);
   return (jlong)wrapper;
}


jlong Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestEvent(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jlong nativeEvent, jlong nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   DvbSi::EIT::Event *event=(DvbSi::EIT::Event *)nativeEvent;
   DvbSi::DataSource source;
   ((RequestWrapper *)nativeRequest)->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   //TODO: honor tags
   req->Add(event->eventDescriptors);
      
   db->DispatchResult(req);
   return (jlong)wrapper;
}


jlong Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestService(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jlong nativeService, jlong nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   DvbSi::SDT::Service *service=(DvbSi::SDT::Service *)nativeService;
   DvbSi::DataSource source;
   ((RequestWrapper *)nativeRequest)->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   //TODO: honor tags
   req->Add(service->serviceDescriptors);
      
   db->DispatchResult(req);
   return (jlong)wrapper;
}


jlong Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestTransportStream(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jlong nativeTransportStream, jlong nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   DvbSi::NIT::TransportStream *ts=(DvbSi::NIT::TransportStream *)nativeTransportStream;
   DvbSi::DataSource source;
   ((RequestWrapper *)nativeRequest)->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   //TODO: honor tags
   req->Add(ts->transportStreamDescriptors);
      
   db->DispatchResult(req);
   return (jlong)wrapper;
}


jlong Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestTime(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jlong nativeTime, jlong nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   RequestWrapper *wr=(RequestWrapper *)nativeRequest;
   
   DvbSi::DataSource source;
   wr->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   //TDT doesn't have any descriptor loop
   if (typeid(*wr)==typeid(TOTRequestWrapper))
      //TODO: honor tags
      req->Add(((SI::TOT *)nativeTime)->descriptorLoop);
      
   db->DispatchResult(req);
   return (jlong)wrapper;
}


jlong Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestPMTService(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jlong nativePMT, jlong nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   DvbSi::PMT *pmt=(DvbSi::PMT *)nativePMT;
   DvbSi::DataSource source;
   ((RequestWrapper *)nativeRequest)->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   //TODO: honor tags
   req->Add(pmt->commonDescriptors);
      
   db->DispatchResult(req);
   return (jlong)wrapper;
}


jlong Java_org_dvb_si_SIDatabaseRequest_DescriptorRequestPMTElementaryStream(JNIEnv* env, jobject obj, jlong nativeDatabase, jshort retrieveMode, jlong nativeStream, jlong nativeRequest, jshortArray tags) {
   JNI::JNIEnvProvider::SetJavaEnv(env);
   DvbSi::Database *db=(DvbSi::Database *)nativeDatabase;
   DvbSi::PMT::Stream *str=(DvbSi::PMT::Stream *)nativeStream;
   DvbSi::DataSource source;
   ((RequestWrapper *)nativeRequest)->getDataSource(source);
   
   DescriptorRequestWrapper *wrapper=new DescriptorRequestWrapper(obj);
   DvbSi::DescriptorRequest *req=new DvbSi::DescriptorRequest(source, wrapper);
   wrapper->req=req;
   
   //TODO: honor tags
   req->Add(str->streamDescriptors);
      
   db->DispatchResult(req);
   return (jlong)wrapper;
}




jint Java_org_dvb_si_SIDatabaseRequest_getSourceTid(JNIEnv* env, jobject obj, jlong nativeData) {
   DvbSi::DataSource source;
   ((RequestWrapper *)nativeData)->getDataSource(source);
   return source.tid;
}

jint Java_org_dvb_si_SIDatabaseRequest_getSourceNid(JNIEnv* env, jobject obj, jlong nativeData) {
   DvbSi::DataSource source;
   ((RequestWrapper *)nativeData)->getDataSource(source);
   return source.onid;
}

jint Java_org_dvb_si_SIDatabaseRequest_getSourceVDRSource(JNIEnv* env, jobject obj, jlong nativeData) {
   DvbSi::DataSource source;
   ((RequestWrapper *)nativeData)->getDataSource(source);
   return source.source;
}

jint Java_org_dvb_si_SIDatabaseRequest_getRetrievalTime(JNIEnv* env, jobject obj, jlong nativeData) {
   DvbSi::DataSource source;
   ((RequestWrapper *)nativeData)->getDataSource(source);
   return source.retrievalTime;
}

jboolean Java_org_dvb_si_SIDatabaseRequest_cancelRequest(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((RequestWrapper *)nativeData)->CancelRequest();
}

jboolean Java_org_dvb_si_SIDatabaseRequest_availableInCache(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((RequestWrapper *)nativeData)->isAvailableInCache();
}

jboolean Java_org_dvb_si_SIDatabaseRequest_hasMoreElements(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeIteratorData) {
   return ((RequestWrapper *)nativeData)->hasMoreElements(nativeIteratorData);
}

jint Java_org_dvb_si_SIDatabaseRequest_numberOfRemainingObjects(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeIteratorData) {
   return ((RequestWrapper *)nativeData)->numberOfRemainingObjects(nativeIteratorData);
}

jlong Java_org_dvb_si_SIDatabaseRequest_nextElement(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeIteratorData) {
   return ((RequestWrapper *)nativeData)->nextElement(nativeIteratorData);
}

jlong Java_org_dvb_si_SIDatabaseRequest_newIterator(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((RequestWrapper *)nativeData)->getNewIterator();
}

void Java_org_dvb_si_SIDatabaseRequest_cleanUpIterator(JNIEnv* env, jobject obj, jlong nativeData, jlong nativeIteratorData) {
   ((RequestWrapper *)nativeData)->DeleteIterator(nativeIteratorData);
}


/*** SICommonObject ***/


void Java_org_dvb_si_SICommonObject_cleanUpSiObject(JNIEnv* env, jobject obj, jlong nativeData) {
   delete (SI::Object *)nativeData;
}



/*** SINetworkImpl ***/

void Java_org_dvb_si_SINetworkImpl_cleanUpStdList(JNIEnv* env, jobject obj, jlong nativeData) {
   delete (std::list<DvbSi::NIT> *)nativeData;
}

jshortArray Java_org_dvb_si_SINetworkImpl_descriptorTags(JNIEnv* env, jobject obj, jlong nativeData) {
   std::list<DvbSi::NIT> *list=(std::list<DvbSi::NIT> *)nativeData;
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

jbyteArray Java_org_dvb_si_SINetworkImpl_name(JNIEnv* env, jobject obj, jlong nativeData) {
   std::list<DvbSi::NIT> *list=(std::list<DvbSi::NIT> *)nativeData;
   for (std::list<DvbSi::NIT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      char name[256];
      stlit->getNetworkName(name, sizeof(name));
      if (name[0])
         return copyConstCharIntoByteArray(env, name);
   }
   return copyConstCharIntoByteArray(env, "");
}

jbyteArray Java_org_dvb_si_SINetworkImpl_shortNetworkName(JNIEnv* env, jobject obj, jlong nativeData) {
   std::list<DvbSi::NIT> *list=(std::list<DvbSi::NIT> *)nativeData;
   for (std::list<DvbSi::NIT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      char name[256];
      char shortName[256];
      stlit->getNetworkName(name, shortName, sizeof(name), sizeof(shortName));
      if (name[0])
         //currently returns the full name if no short name is available.
         //I can't say if this is according to the spec.
         return copyConstCharIntoByteArray(env, shortName[0] ? shortName : name);
   }
   return copyConstCharIntoByteArray(env, "");
}

jint Java_org_dvb_si_SINetworkImpl_networkId(JNIEnv* env, jobject obj, jlong nativeData) {
   std::list<DvbSi::NIT> *list=(std::list<DvbSi::NIT> *)nativeData;
   //printf("SIZE of nit list %d\n", list->size());
   if (list->begin() != list->end())
      return list->begin()->getNetworkId();
   else
      return -1;
}



/*** SIBouquetImpl ***/

void Java_org_dvb_si_SIBouquetImpl_cleanUpStdList(JNIEnv* env, jobject obj, jlong nativeData) {
   delete (std::list<DvbSi::BAT> *)nativeData;
}

jshortArray Java_org_dvb_si_SIBouquetImpl_descriptorTags(JNIEnv* env, jobject obj, jlong nativeData) {
   std::list<DvbSi::BAT> *list=(std::list<DvbSi::BAT> *)nativeData;
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

jbyteArray Java_org_dvb_si_SIBouquetImpl_name(JNIEnv* env, jobject obj, jlong nativeData) {
   std::list<DvbSi::BAT> *list=(std::list<DvbSi::BAT> *)nativeData;
   for (std::list<DvbSi::BAT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      char name[256];
      stlit->getBouquetName(name, sizeof(name));
      if (name[0])
         return copyConstCharIntoByteArray(env, name);
   }
   return copyConstCharIntoByteArray(env, "");
}

jbyteArray Java_org_dvb_si_SIBouquetImpl_shortBouquetName(JNIEnv* env, jobject obj, jlong nativeData) {
   std::list<DvbSi::BAT> *list=(std::list<DvbSi::BAT> *)nativeData;
   for (std::list<DvbSi::BAT>::iterator stlit=list->begin(); stlit != list->end(); ++stlit) {
      char name[256];
      char shortName[256];
      stlit->getBouquetName(name, shortName,sizeof(name), sizeof(shortName));
      if (name[0])
         //currently returns the full name if no short name is available.
         //I can't say if this is according to the spec.
         return copyConstCharIntoByteArray(env, shortName[0] ? shortName : name);
   }
   return copyConstCharIntoByteArray(env, "");
}

jint Java_org_dvb_si_SIBouquetImpl_networkId(JNIEnv* env, jobject obj, jlong nativeData) {
   std::list<DvbSi::BAT> *list=(std::list<DvbSi::BAT> *)nativeData;
   if (list->begin() != list->end())
      return list->begin()->getBouquetId();
   else
      return -1;
}



/*** SITransportStreamDescriptionImpl ***/

void Java_org_dvb_si_SITransportStreamDescriptionImpl_cleanUpStdList(JNIEnv* env, jobject obj, jlong nativeData) {
   delete (std::list<DvbSi::TSDT> *)nativeData;
}

jshortArray Java_org_dvb_si_SITransportStreamDescriptionImpl_descriptorTags(JNIEnv* env, jobject obj, jlong nativeData) {
   std::list<DvbSi::TSDT> *list=(std::list<DvbSi::TSDT> *)nativeData;
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

jbyteArray Java_org_dvb_si_SIEventImpl_getContentNibbles(JNIEnv* env, jobject obj, jlong nativeData) {
   int count;
   SI::EightBit *array;
   if ( (array=((DvbSi::EIT::Event *)nativeData)->getContentNibbleLevel1(count)) ) {
      jbyteArray ar=env->NewByteArray(count);
      env->SetByteArrayRegion(ar, 0, count, (jbyte *)array);
      return ar;
   }
   else return env->NewByteArray(0);
}

jlong Java_org_dvb_si_SIEventImpl_getDuration(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::EIT::Event *)nativeData)->getDuration();
}

jint Java_org_dvb_si_SIEventImpl_getEventID(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::EIT::Event *)nativeData)->getEventId();
}

jboolean Java_org_dvb_si_SIEventImpl_getFreeCAMode(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::EIT::Event *)nativeData)->getFreeCaMode();
}

jbyteArray Java_org_dvb_si_SIEventImpl_getLevel1ContentNibbles(JNIEnv* env, jobject obj, jlong nativeData) {
   int count;
   SI::EightBit *array;
   if ( (array=((DvbSi::EIT::Event *)nativeData)->getContentNibbles(count)) ) {
      jbyteArray ar=env->NewByteArray(count);
      env->SetByteArrayRegion(ar, 0, count, (jbyte *)array);
      return ar;
   }
   else return env->NewByteArray(0);
}

jbyteArray Java_org_dvb_si_SIEventImpl_getName(JNIEnv* env, jobject obj, jlong nativeData) {
   char buf[256];
   if (((DvbSi::EIT::Event *)nativeData)->getEventName(buf, sizeof(buf))) {
      return copyConstCharIntoByteArray(env, buf);
   }
   return copyConstCharIntoByteArray(env, "");
}

jint Java_org_dvb_si_SIEventImpl_getOriginalNetworkID(JNIEnv* env, jobject obj, jlong nativeRequestWrapperData) {
   RequestWrapper *wr=(RequestWrapper *)nativeRequestWrapperData;
   if (typeid(*wr)==typeid(SingleEventRequestWrapper))
      return ((SingleEventRequestWrapper *)wr)->req->getOriginalNetworkId();
   else //if (typeid(*wr)==typeid(EventRequestWrapper))
      return ((EventRequestWrapper *)wr)->req->getOriginalNetworkId();
   //else
    //  return -1;
}

jbyte Java_org_dvb_si_SIEventImpl_getRunningStatus(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::EIT::Event *)nativeData)->getRunningStatus();
}

jint Java_org_dvb_si_SIEventImpl_getServiceID(JNIEnv* env, jobject obj, jlong nativeRequestWrapperData) {
   RequestWrapper *wr=(RequestWrapper *)nativeRequestWrapperData;
   if (typeid(*wr)==typeid(SingleEventRequestWrapper))
      return ((SingleEventRequestWrapper *)wr)->req->getServiceId();
   else //if (typeid(*wr)==typeid(EventRequestWrapper))
      return ((EventRequestWrapper *)wr)->req->getServiceId();
   //else
    //  return -1;
}

jbyteArray Java_org_dvb_si_SIEventImpl_getShortDescription(JNIEnv* env, jobject obj, jlong nativeData) {
   char buf[256];
   if (((DvbSi::EIT::Event *)nativeData)->getShortDescription(buf, sizeof(buf))) {
      return copyConstCharIntoByteArray(env, buf);
   }
   return copyConstCharIntoByteArray(env, "");
}

jbyteArray Java_org_dvb_si_SIEventImpl_getShortEventName(JNIEnv* env, jobject obj, jlong nativeData) {
   char buf[256];
   char shortVersion[256];
   if (((DvbSi::EIT::Event *)nativeData)->getEventName(buf, shortVersion, sizeof(buf), sizeof(shortVersion))) {
      return copyConstCharIntoByteArray(env, *shortVersion ? shortVersion : buf);
   }
   return copyConstCharIntoByteArray(env, "");
}

jlong Java_org_dvb_si_SIEventImpl_getStartTime(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::EIT::Event *)nativeData)->getStartTime();
}

jint Java_org_dvb_si_SIEventImpl_getTransportStreamID(JNIEnv* env, jobject obj, jlong nativeRequestWrapperData) {
   RequestWrapper *wr=(RequestWrapper *)nativeRequestWrapperData;
   //printf("%s %s %s\n", typeid(*wr).name(), typeid(SingleEventRequestWrapper).name(), typeid(EventRequestWrapper*).name());
   if (typeid(*wr)==typeid(SingleEventRequestWrapper))
      return ((SingleEventRequestWrapper *)wr)->req->getTransportStreamId();
   else //if (typeid(*wr)==typeid(EventRequestWrapper))
      return ((EventRequestWrapper *)wr)->req->getTransportStreamId();
   //else
   //   return -1;
}

jshortArray Java_org_dvb_si_SIEventImpl_descriptorTags(JNIEnv* env, jobject obj, jlong nativeData) {
   DvbSi::EIT::Event *event=(DvbSi::EIT::Event *)nativeData;
   int count=event->eventDescriptors.getNumberOfDescriptors();
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   event->eventDescriptors.getDescriptorTags<jshort>(ar);
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}



/*** SIServiceImpl ***/
jboolean Java_org_dvb_si_SIServiceImpl_getEITPresentFollowingFlag(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::SDT::Service *)nativeData)->getEITpresentFollowingFlag();
}

jboolean Java_org_dvb_si_SIServiceImpl_getEITScheduleFlag(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::SDT::Service *)nativeData)->getEITscheduleFlag();
}

jboolean Java_org_dvb_si_SIServiceImpl_getFreeCAMode(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::SDT::Service *)nativeData)->getFreeCaMode();
}

jbyteArray Java_org_dvb_si_SIServiceImpl_getName(JNIEnv* env, jobject obj, jlong nativeData) {
   char buf[256];
   if (((DvbSi::SDT::Service *)nativeData)->getServiceName(buf, sizeof(buf))) {
      return copyConstCharIntoByteArray(env, buf);
   }
   return copyConstCharIntoByteArray(env, "");
}

jint Java_org_dvb_si_SIServiceImpl_getOriginalNetworkID(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::SDT::Service *)nativeData)->getOriginalNetworkId();
}

jbyteArray Java_org_dvb_si_SIServiceImpl_getProviderName(JNIEnv* env, jobject obj, jlong nativeData) {
   char buf[256];
   if (((DvbSi::SDT::Service *)nativeData)->getProviderName(buf, sizeof(buf))) {
      return copyConstCharIntoByteArray(env, buf);
   }
   return copyConstCharIntoByteArray(env, "");
}

jbyte Java_org_dvb_si_SIServiceImpl_getRunningStatus(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::SDT::Service *)nativeData)->getRunningStatus();
}

jint Java_org_dvb_si_SIServiceImpl_getServiceID(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::SDT::Service *)nativeData)->getServiceId();
}

jbyteArray Java_org_dvb_si_SIServiceImpl_getShortProviderName(JNIEnv* env, jobject obj, jlong nativeData) {
   char buf[256];
   char shortVersion[256];
   if (((DvbSi::SDT::Service *)nativeData)->getProviderName(buf, shortVersion, sizeof(buf), sizeof(shortVersion))) {
      return copyConstCharIntoByteArray(env, *shortVersion ? shortVersion : buf);
   }
   return copyConstCharIntoByteArray(env, "");
}

jbyteArray Java_org_dvb_si_SIServiceImpl_getShortServiceName(JNIEnv* env, jobject obj, jlong nativeData) {
   char buf[256];
   char shortVersion[256];
   if (((DvbSi::SDT::Service *)nativeData)->getServiceName(buf, shortVersion, sizeof(buf), sizeof(shortVersion))) {
      return copyConstCharIntoByteArray(env, *shortVersion ? shortVersion : buf);
   }
   return copyConstCharIntoByteArray(env, "");
}

jshort Java_org_dvb_si_SIServiceImpl_getSIServiceType(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::SDT::Service *)nativeData)->getServiceType();
}

jint Java_org_dvb_si_SIServiceImpl_getTransportStreamID(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::SDT::Service *)nativeData)->getTransportStreamId();
}

jshortArray Java_org_dvb_si_SIServiceImpl_descriptorTags(JNIEnv* env, jobject obj, jlong nativeData) {
   DvbSi::SDT::Service *service=(DvbSi::SDT::Service *)nativeData;
   int count=service->serviceDescriptors.getNumberOfDescriptors();
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   service->serviceDescriptors.getDescriptorTags<jshort>(ar);
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}



/*** SITransportStreamImpl ***/
jshortArray Java_org_dvb_si_SITransportStreamImpl_descriptorTags(JNIEnv* env, jobject obj, jlong nativeData) {
   DvbSi::NIT::TransportStream *ts=(DvbSi::NIT::TransportStream *)nativeData;
   int count=ts->transportStreamDescriptors.getNumberOfDescriptors();
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   ts->transportStreamDescriptors.getDescriptorTags<jshort>(ar);
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}

jint Java_org_dvb_si_SITransportStreamImpl_getNetworkID(JNIEnv* env, jobject obj, jlong nativeRequestWrapperData) {
   RequestWrapper *wr=(RequestWrapper *)nativeRequestWrapperData;
   if (typeid(*wr)==typeid(TransportStreamRequestWrapper))
      return ((TransportStreamRequestWrapper*)wr)->req->getNetworkId();
   else if (typeid(*wr)==typeid(TransportStreamBATRequestWrapper))
      return ((TransportStreamBATRequestWrapper*)wr)->req->getBouquetId();
   else //if (typeid(*wr)==typeid(ActualTransportStreamRequestWrapper))
      return ((ActualTransportStreamRequestWrapper*)wr)->req->getNetworkId();
   //else
   //   return -1;
}

jint Java_org_dvb_si_SITransportStreamImpl_getOriginalNetworkID(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::NIT::TransportStream *)nativeData)->getOriginalNetworkId();
}

jint Java_org_dvb_si_SITransportStreamImpl_getTransportStreamID(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::NIT::TransportStream *)nativeData)->getTransportStreamId();
}



/*** SITimeImpl ***/

jint Java_org_dvb_si_SITimeImpl_getUTCTime(JNIEnv* env, jobject obj, jlong nativeData) {
   SI::Object *obje=(SI::Object *)nativeData;
   if (typeid(*obje)==typeid(SI::TDT))
      return ((SI::TDT *)nativeData)->getTime();
   else //if (typeid(*obj)==typeid(SI::TOT))
      return ((SI::TOT *)nativeData)->getTime();
}

jshortArray Java_org_dvb_si_SITimeImpl_descriptorTags(JNIEnv* env, jobject obj, jlong nativeData) {
   SI::Object *obje=(SI::Object *)nativeData;
   if (typeid(*obje)==typeid(SI::TDT))
      return env->NewShortArray(0);
   
   SI::TOT *tot=(SI::TOT *)nativeData;
   int count=tot->descriptorLoop.getNumberOfDescriptors();
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   tot->descriptorLoop.getDescriptorTags<jshort>(ar);
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}



/*** PMTServicesImpl ***/

jint Java_org_dvb_si_PMTServiceImpl_getPcrPid(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::PMT *)nativeData)->getPCRPid();
}

jint Java_org_dvb_si_PMTServiceImpl_getServiceID(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::PMT *)nativeData)->getServiceId();
}

jshortArray Java_org_dvb_si_PMTServiceImpl_descriptorTags(JNIEnv* env, jobject obj, jlong nativeData) {
   DvbSi::PMT *pmt=(DvbSi::PMT *)nativeData;
   int count=pmt->commonDescriptors.getNumberOfDescriptors();
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   pmt->commonDescriptors.getDescriptorTags<jshort>(ar);
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}


/*** PMTElementaryStreamImpl ***/

jint Java_org_dvb_si_PMTElementaryStreamImpl_getComponentTag(JNIEnv* env, jobject obj, jlong nativeData) {
  return ((DvbSi::PMT::Stream *)nativeData)->getComponentTag();
}

jshort Java_org_dvb_si_PMTElementaryStreamImpl_getElementaryPID(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::PMT::Stream *)nativeData)->getPid();
}

jint Java_org_dvb_si_PMTElementaryStreamImpl_getServiceID(JNIEnv* env, jobject obj, jlong nativeRequestData) {
   return ((PMTElementaryStreamsRequestWrapper *)nativeRequestData)->req->getServiceId();
}

jbyte Java_org_dvb_si_PMTElementaryStreamImpl_getStreamType(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((DvbSi::PMT::Stream *)nativeData)->getStreamType();
}

jint Java_org_dvb_si_PMTElementaryStreamImpl_getTransportStreamID(JNIEnv* env, jobject obj, jlong nativeRequestData) {
   return ((PMTElementaryStreamsRequestWrapper *)nativeRequestData)->req->getTransportStreamId();
}
                          
jshortArray Java_org_dvb_si_PMTElementaryStreamImpl_descriptorTags(JNIEnv* env, jobject obj, jlong nativeData) {
   DvbSi::SDT::Service *service=(DvbSi::SDT::Service *)nativeData;
   int count=service->serviceDescriptors.getNumberOfDescriptors();
   
   jshortArray javaAr=env->NewShortArray(count);
   jshort *ar=env->GetShortArrayElements(javaAr, 0);
   service->serviceDescriptors.getDescriptorTags<jshort>(ar);
   
   env->ReleaseShortArrayElements(javaAr, ar, 0);
   return javaAr;
}


/*** Descriptor ***/
jbyte Java_org_dvb_si_Descriptor_getData(JNIEnv* env, jobject obj, jlong nativeData, jint index) {
   return ((SI::Descriptor *)nativeData)->getData()[index];
}

jbyteArray Java_org_dvb_si_Descriptor_getDataArray(JNIEnv* env, jobject obj, jlong nativeData) {
   SI::Descriptor *d=((SI::Descriptor *)nativeData);
   int count=d->getLength();
   jbyteArray ar=env->NewByteArray(count);
   env->SetByteArrayRegion(ar, 0, count, (jbyte *)d->getData().getData(sizeof(SI::DescriptorHeader)));
   return ar;
}

jshort Java_org_dvb_si_Descriptor_getLength(JNIEnv* env, jobject obj, jlong nativeData) {
   //libsi always returns the size including the header, but the java API only wants
   //the content length, so the sizeof(DescriptorHeader) must be subtracted.
   return ((SI::Descriptor *)nativeData)->getLength()-sizeof(SI::DescriptorHeader);
}

jshort Java_org_dvb_si_Descriptor_getTag(JNIEnv* env, jobject obj, jlong nativeData) {
   return ((SI::Descriptor *)nativeData)->getDescriptorTag();
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





jlong Java_org_dvb_si_DvbSIEvent_startTime(JNIEnv* env, jobject obj, jint nativeData) {
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
