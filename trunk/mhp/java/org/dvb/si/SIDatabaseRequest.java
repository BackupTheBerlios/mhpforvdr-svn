package org.dvb.si;

import vdr.mhp.lang.NativeData;
import java.util.Date;

public class SIDatabaseRequest extends SIRequest {

//keep in sync with DvbSi's request.h!
static final int ResultCodeUnknown = 0;
static final int ResultCodeSuccess = 1;
static final int ResultCodeLackOfResources = 2;
static final int ResultCodeNotInCache = 3;
static final int ResultCodeObjectNotInTable = 4;
static final int ResultCodeRequestCancelled = 5;
static final int ResultCodeTableNotFound = 6;
static final int ResultCodeTableUpdated = 7;
static final int ResultCodeDataSwitch = 8; //transport stream changed while monitoring


NativeData nativeData = null; //a RequestWrapper object
//Class iteratorType;
IteratorFactory iteratorFactory;

static {
   initStaticState();
}
private native static void initStaticState();

private SIDatabaseRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db) {
   super(appData, listener, db);
   this.nativeData=nativeData;
}

protected void finalize() throws java.lang.Throwable {
   super.finalize();
   cleanUp(nativeData);
}

private native void cleanUp(NativeData nativeData);

static SIDatabaseRequest ActualNetworkRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db,
                          short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   //as strange as this syntax appears (to me as a C++ programmer):
   //inner classes contain an implicit "this" reference to the enclosing class!
   req.iteratorFactory=req.new NetworksIteratorFactory();
   req.nativeData=req.ActualNetworkRequest(db.getNativeData(), retrieveMode);
   return req;
}
private native NativeData ActualNetworkRequest(NativeData nativeDatabase, short retrieveMode);


static SIDatabaseRequest ActualServicesRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db,
                          short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new ServicesIteratorFactory();
   req.nativeData=req.ActualServicesRequest(db.getNativeData(), retrieveMode);
   return req;
}
private native NativeData ActualServicesRequest(NativeData nativeDatabase, short retrieveMode);


static SIDatabaseRequest ActualTransportStreamRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new TransportStreamIteratorFactory();
   req.nativeData=req.ActualTransportStreamRequest(db.getNativeData(), retrieveMode);
   return req;
}
private native NativeData ActualTransportStreamRequest(NativeData nativeDatabase, short retrieveMode);


static SIDatabaseRequest PMTElementaryStreamsRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode, int originalNetworkId, int transportStreamId, int serviceId, int[] componentTags) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new PMTElementaryStreamsIteratorFactory();
   req.nativeData=req.PMTElementaryStreamsRequest(db.getNativeData(), retrieveMode, originalNetworkId, transportStreamId, serviceId, componentTags);
   return req;
}
private native NativeData PMTElementaryStreamsRequest(NativeData nativeDatabase, short retrieveMode, int originalNetworkId, int transportStreamId, int serviceId, int[] componentTags);


static SIDatabaseRequest PMTServicesRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db,
                          short retrieveMode, int originalNetworkId, int transportStreamId, int serviceId) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new PMTServicesIteratorFactory();
   req.nativeData=req.PMTServicesRequest(db.getNativeData(), retrieveMode, originalNetworkId, transportStreamId, serviceId);
   return req;
}
private native NativeData PMTServicesRequest(NativeData nativeDatabase, short retrieveMode, int originalNetworkId, int transportStreamId, int serviceId);


static SIDatabaseRequest BouquetsRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db,
                          short retrieveMode, int bouquetId) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new BouquetsIteratorFactory();
   req.nativeData=req.BouquetsRequest(db.getNativeData(), retrieveMode, bouquetId);
   return req;
}
private native NativeData BouquetsRequest(NativeData nativeDatabase, short retrieveMode, int bouquetId);


static SIDatabaseRequest NetworksRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db,
                          short retrieveMode, int networkId) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new NetworksIteratorFactory();
   req.nativeData=req.NetworksRequest(db.getNativeData(), retrieveMode, networkId);
   return req;
}
private native NativeData NetworksRequest(NativeData nativeDatabase, short retrieveMode, int networkId);


static SIDatabaseRequest ServicesRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db,
                          short retrieveMode, int originalNetworkId, int transportStreamId, int serviceId) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new ServicesIteratorFactory();
   req.nativeData=req.ServicesRequest(db.getNativeData(), retrieveMode, originalNetworkId, transportStreamId, serviceId);
   return req;
}
private native NativeData ServicesRequest(NativeData nativeDatabase, short retrieveMode, int originalNetworkId, int transportStreamId, int serviceId);

static SIDatabaseRequest TDTRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db,
                          short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new TimeIteratorFactory();
   req.nativeData=req.TDTRequest(db.getNativeData(), retrieveMode);
   return req;
}
private native NativeData TDTRequest(NativeData nativeDatabase, short retrieveMode);


static SIDatabaseRequest TOTRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db,
                          short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new TimeIteratorFactory();
   req.nativeData=req.TOTRequest(db.getNativeData(), retrieveMode);
   return req;
}
private native NativeData TOTRequest(NativeData nativeDatabase, short retrieveMode);


static SIDatabaseRequest TransportStreamDescriptionRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new TransportStreamDescriptionIteratorFactory();
   req.nativeData=req.TransportStreamDescriptionRequest(db.getNativeData(), retrieveMode);
   return req;
}
private native NativeData TransportStreamDescriptionRequest(NativeData nativeDatabase, short retrieveMode);




static SIDatabaseRequest TransportStreamRequest(SINetworkImpl nits, java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new TransportStreamIteratorFactory();
   req.nativeData=req.TransportStreamRequest(db.getNativeData(), retrieveMode, nits.nativeData, nits.request.nativeData);
   return req;
}
private native NativeData TransportStreamRequest(NativeData nativeDatabase, short retrieveMode, NativeData nativeList, NativeData nativeRequest);


static SIDatabaseRequest TransportStreamRequest(SIBouquetImpl nits, java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new TransportStreamIteratorFactory();
   req.nativeData=req.TransportStreamRequestBAT(db.getNativeData(), retrieveMode, nits.nativeData, nits.request.nativeData);
   return req;
}
private native NativeData TransportStreamRequestBAT(NativeData nativeDatabase, short retrieveMode, NativeData nativeList, NativeData nativeRequest);


static SIDatabaseRequest PresentFollowingEventRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode, boolean presentOrFollowing, int tid, int sid) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new EventsIteratorFactory();
   req.nativeData=req.PresentFollowingEventRequest(db.getNativeData(), retrieveMode, presentOrFollowing, tid, sid);
   return req;
}
private native NativeData PresentFollowingEventRequest(NativeData nativeDatabase, short retrieveMode, boolean presentOrFollowing, int tid, int sid);


static SIDatabaseRequest ScheduledEventsRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode, int tid, int sid) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new EventsIteratorFactory();
   req.nativeData=req.ScheduledEventsRequest(db.getNativeData(), retrieveMode, tid, sid);
   return req;
}
private native NativeData ScheduledEventsRequest(NativeData nativeDatabase, short retrieveMode, int tid, int sid);


static SIDatabaseRequest TimeScheduledEventsRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode, Date begin, Date end, int tid, int sid) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new EventsIteratorFactory();
   req.nativeData=req.TimeScheduledEventsRequest(db.getNativeData(), retrieveMode, begin.getTime() / 1000, end.getTime() / 1000, tid, sid);
   return req;
}
private native NativeData TimeScheduledEventsRequest(NativeData nativeDatabase, short retrieveMode, long begin, long end, int tid, int sid);






//internal descriptor requests
static SIDatabaseRequest DescriptorRequestNetwork(SINetworkImpl nits, short[] tags, java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new DescriptorIteratorFactory();
   req.nativeData=req.DescriptorRequestNetwork(db.getNativeData(), retrieveMode, nits.nativeData, nits.request.nativeData, tags);
   return req;
}
private native NativeData DescriptorRequestNetwork(NativeData nativeDatabase, short retrieveMode, NativeData nativeList, NativeData nativeRequest, short[] tags);

static SIDatabaseRequest DescriptorRequestBouquet(SIBouquetImpl nits, short[] tags, java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new DescriptorIteratorFactory();
   req.nativeData=req.DescriptorRequestBouquet(db.getNativeData(), retrieveMode, nits.nativeData, nits.request.nativeData, tags);
   return req;
}
private native NativeData DescriptorRequestBouquet(NativeData nativeDatabase, short retrieveMode, NativeData nativeList, NativeData nativeRequest, short[] tags);

static SIDatabaseRequest DescriptorRequestEvent(SIEventImpl evs, short[] tags, java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new DescriptorIteratorFactory();
   req.nativeData=req.DescriptorRequestEvent(db.getNativeData(), retrieveMode, evs.nativeData, evs.request.nativeData, tags);
   return req;
}
private native NativeData DescriptorRequestEvent(NativeData nativeDatabase, short retrieveMode, NativeData nativeList, NativeData nativeRequest, short[] tags);

static SIDatabaseRequest DescriptorRequestService(SIServiceImpl evs, short[] tags, java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new DescriptorIteratorFactory();
   req.nativeData=req.DescriptorRequestService(db.getNativeData(), retrieveMode, evs.nativeData, evs.request.nativeData, tags);
   return req;
}
private native NativeData DescriptorRequestService(NativeData nativeDatabase, short retrieveMode, NativeData nativeList, NativeData nativeRequest, short[] tags);

static SIDatabaseRequest DescriptorRequestTransportStream(SITransportStreamImpl evs, short[] tags, java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new DescriptorIteratorFactory();
   req.nativeData=req.DescriptorRequestTransportStream(db.getNativeData(), retrieveMode, evs.nativeData, evs.request.nativeData, tags);
   return req;
}
private native NativeData DescriptorRequestTransportStream(NativeData nativeDatabase, short retrieveMode, NativeData nativeList, NativeData nativeRequest, short[] tags);

static SIDatabaseRequest DescriptorRequestTime(SITimeImpl evs, short[] tags, java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new DescriptorIteratorFactory();
   req.nativeData=req.DescriptorRequestTime(db.getNativeData(), retrieveMode, evs.nativeData, evs.request.nativeData, tags);
   return req;
}
private native NativeData DescriptorRequestTime(NativeData nativeDatabase, short retrieveMode, NativeData nativeTime, NativeData nativeRequest, short[] tags);

static SIDatabaseRequest DescriptorRequestPMTService(PMTServiceImpl evs, short[] tags, java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new DescriptorIteratorFactory();
   req.nativeData=req.DescriptorRequestPMTService(db.getNativeData(), retrieveMode, evs.nativeData, evs.request.nativeData, tags);
   return req;
}
private native NativeData DescriptorRequestPMTService(NativeData nativeDatabase, short retrieveMode, NativeData nativeTime, NativeData nativeRequest, short[] tags);

static SIDatabaseRequest DescriptorRequestPMTElementaryStream(PMTElementaryStreamImpl evs, short[] tags, java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new DescriptorIteratorFactory();
   req.nativeData=req.DescriptorRequestPMTElementaryStream(db.getNativeData(), retrieveMode, evs.nativeData, evs.request.nativeData, tags);
   return req;
}
private native NativeData DescriptorRequestPMTElementaryStream(NativeData nativeDatabase, short retrieveMode, NativeData nativeTime, NativeData nativeRequest, short[] tags);

static SIDatabaseRequest DescriptorRequestTransportStreamDescription(SITransportStreamDescriptionImpl evs, short[] tags, java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode) {
   SIDatabaseRequest req=new SIDatabaseRequest(appData, listener, db);
   req.iteratorFactory=req.new DescriptorIteratorFactory();
   req.nativeData=req.DescriptorRequestTransportStreamDescription(db.getNativeData(), retrieveMode, evs.nativeData, evs.request.nativeData, tags);
   return req;
}
private native NativeData DescriptorRequestTransportStreamDescription(NativeData nativeDatabase, short retrieveMode, NativeData nativeTime, NativeData nativeRequest, short[] tags);




/*
Cancels the retrieval request. Returns: true if the request was cancelled and an SIRequestCancelledEvent will be 
delivered to the listener, false if the request has already completed (either successfully, with an error or due to a 
prior cancel method call) */
public boolean cancelRequest() {
   return cancelRequest(nativeData);
}
private native boolean cancelRequest(NativeData nativeData);

//the quick solution
class TransportStream extends org.davic.mpeg.TransportStream {
public TransportStream(int tid) {
   super(tid);
}
}

/*
Return the org.davic.mpeg.TransportStream object the information contained in the object that implements that interface 
was  ltered from. Returns: The org.davic.mpeg.TransportStream object the information was  ltered from. See Also: 
org.davic.mpeg.TransportStream */
public org.davic.mpeg.TransportStream getDataSource() {
   return new TransportStream(getSourceTid());
}

public int getSourceTid() {
   return getSourceTid(nativeData);
}
private native int getSourceTid(NativeData nativeData);

public int getSourceNid() {
   return getSourceNid(nativeData);
}
private native int getSourceNid(NativeData nativeData);

public int getSourceVDRSource() {
   return getSourceVDRSource(nativeData);
}
private native int getSourceVDRSource(NativeData nativeData);

public java.util.Date getUpdateTime() {
   return new java.util.Date(1000*getRetrievalTime(nativeData));
}
private native long getRetrievalTime(NativeData nativeData);

/*
Returns whether the information will be returned from cache or from the stream Returns: true if the information will be 
returned from cache, false if the information will be retrieved from the stream */
public boolean isAvailableInCache() {
   return availableInCache(nativeData);
}
private native boolean availableInCache(NativeData nativeData);

//called from the native code
public void Result(NativeData nativeData) {
   try {
      //I don't know if it is necessary, but at least in theory,
      //Result might be called from the native layer before the creating native funtion returned.
      if (this.nativeData == null)
         this.nativeData=nativeData;
      int result=resultCode(nativeData);
      switch (result) {
      case ResultCodeUnknown:
         listener.postRetrievalEvent(new SITableNotFoundEvent(this));
         break;
      case ResultCodeSuccess:
         System.out.println("Entering postRetrievalEvent");
         listener.postRetrievalEvent(new SISuccessfulRetrieveEvent(this));
         System.out.println("Leaving postRetrievalEvent");
         break;
      case ResultCodeLackOfResources:
         listener.postRetrievalEvent(new SILackOfResourcesEvent(this));
         break;
      case ResultCodeNotInCache:
         listener.postRetrievalEvent(new SINotInCacheEvent(this));
         break;
      case ResultCodeObjectNotInTable:
         listener.postRetrievalEvent(new SIObjectNotInTableEvent(this));
         break;
      case ResultCodeRequestCancelled:
         listener.postRetrievalEvent(new SIRequestCancelledEvent(this));
         break;
      case ResultCodeTableNotFound:
         listener.postRetrievalEvent(new SITableNotFoundEvent(this));
         break;
      case ResultCodeTableUpdated:
         listener.postRetrievalEvent(new SITableUpdatedEvent(this));
         break;
      case ResultCodeDataSwitch:
         listener.postRetrievalEvent(new SIRequestCancelledEvent(this));
         break;
      }
   } catch (Exception e) {
      //this is a real entry point from native code, so catch all exceptions
      e.printStackTrace();
   }
   System.out.println("Leaving DatabaseRequest.Result()");
}
private native int resultCode(NativeData nativeData);




/*** Iterator implementation ***/


//these three are called from Iterator
boolean hasMoreElements(NativeData nativeIteratorData) {
   return hasMoreElements(nativeData, nativeIteratorData);
}
private native boolean hasMoreElements(NativeData nativeData, NativeData nativeIteratorData);

int numberOfRemainingObjects(NativeData nativeIteratorData) {
   return numberOfRemainingObjects(nativeData, nativeIteratorData);
}
private native int numberOfRemainingObjects(NativeData nativeData, NativeData nativeIteratorData);

NativeData nextElement(NativeData nativeIteratorData) {
   return nextElement(nativeData, nativeIteratorData);
}
private native NativeData nextElement(NativeData nativeData, NativeData nativeIteratorData);
private native NativeData newIterator(NativeData nativeData);

//called from SuccessfulRetrieveEvent
SIIterator getIterator() {
   Iterator it=iteratorFactory.create();
   return it;
}

void cleanUpIterator(NativeData nativeIteratorData) {
   cleanUpIterator(nativeData, nativeIteratorData);
}
private native void cleanUpIterator(NativeData nativeData, NativeData nativeIteratorData);


//the base class for implementation of iterator - only nextElement needs to be subclass-specific
abstract class Iterator implements SIIterator {
   NativeData nativeIteratorData; //a void * pointer known to the RequestWrapper subclass
   
   Iterator() {
      nativeIteratorData=SIDatabaseRequest.this.newIterator(nativeData);
   }
   
   public boolean hasMoreElements() {
      return SIDatabaseRequest.this.hasMoreElements(nativeIteratorData);
   }
   
   public int numberOfRemainingObjects() {
      return SIDatabaseRequest.this.numberOfRemainingObjects(nativeIteratorData);
   }
   
   //while the other two fulfill nicely the Java interface, the one does not implement nextElement()
   NativeData nextElementNative() {
      return SIDatabaseRequest.this.nextElement(nativeIteratorData);
   }
   
   protected void finalize() throws java.lang.Throwable {
      super.finalize();
      cleanUpIterator(nativeIteratorData);
   }
}

class NetworksIterator extends Iterator {
   //public NetworksIterator() {
   //}   
   public Object nextElement() {
      return new SINetworkImpl(SIDatabaseRequest.this, SIDatabaseRequest.this.nextElement(nativeIteratorData));
   }
}

class BouquetsIterator extends Iterator {
   //public NetworksIterator() {
   //}   
   public Object nextElement() {
      return new SIBouquetImpl(SIDatabaseRequest.this, SIDatabaseRequest.this.nextElement(nativeIteratorData));
   }
}

class ServicesIterator extends Iterator {
   //public ServicesIterator() {
   //}   
   public Object nextElement() {
      return new SIServiceImpl(SIDatabaseRequest.this, SIDatabaseRequest.this.nextElement(nativeIteratorData));
   }
}

class EventsIterator extends Iterator {
   //public EventsIterator() {
   //}   
   public Object nextElement() {
      return new SIEventImpl(SIDatabaseRequest.this, SIDatabaseRequest.this.nextElement(nativeIteratorData));
   }
}

class PMTServicesIterator extends Iterator {
   public Object nextElement() {
      return new PMTServiceImpl(SIDatabaseRequest.this, SIDatabaseRequest.this.nextElement(nativeIteratorData));
   }
}

class PMTElementaryStreamsIterator extends Iterator {
   public Object nextElement() {
      return new PMTElementaryStreamImpl(SIDatabaseRequest.this, SIDatabaseRequest.this.nextElement(nativeIteratorData));
   }
}

class TransportStreamIterator extends Iterator {
   public Object nextElement() {
      return new SITransportStreamImpl(SIDatabaseRequest.this, SIDatabaseRequest.this.nextElement(nativeIteratorData));
   }
}

class TimeIterator extends Iterator {
   public Object nextElement() {
      return new SITimeImpl(SIDatabaseRequest.this, SIDatabaseRequest.this.nextElement(nativeIteratorData));
   }
}

class TransportStreamDescriptionIterator extends Iterator {
   public Object nextElement() {
      return new SITransportStreamDescriptionImpl(SIDatabaseRequest.this, SIDatabaseRequest.this.nextElement(nativeIteratorData));
   }
}

class DescriptorIterator extends Iterator {
   public Object nextElement() {
      return new Descriptor(SIDatabaseRequest.this, SIDatabaseRequest.this.nextElement(nativeIteratorData));
   }
}




interface IteratorFactory {
   SIDatabaseRequest.Iterator create();
}

class NetworksIteratorFactory implements IteratorFactory {
   public Iterator create() {
      return new NetworksIterator();
   }
}

class BouquetsIteratorFactory implements IteratorFactory {
   public Iterator create() {
      return new BouquetsIterator();
   }
}

class ServicesIteratorFactory implements IteratorFactory {
   public Iterator create() {
      return new ServicesIterator();
   }
}

class EventsIteratorFactory implements IteratorFactory {
   public Iterator create() {
      return new EventsIterator();
   }
}

class PMTServicesIteratorFactory implements IteratorFactory {
   public Iterator create() {
      return new PMTServicesIterator();
   }
}

class PMTElementaryStreamsIteratorFactory implements IteratorFactory {
   public Iterator create() {
      return new PMTElementaryStreamsIterator();
   }
}

class TransportStreamIteratorFactory implements IteratorFactory {
   public Iterator create() {
      return new TransportStreamIterator();
   }
}

class TimeIteratorFactory implements IteratorFactory {
   public Iterator create() {
      return new TimeIterator();
   }
}

class TransportStreamDescriptionIteratorFactory implements IteratorFactory {
   public Iterator create() {
      return new TransportStreamDescriptionIterator();
   }
}

class DescriptorIteratorFactory implements IteratorFactory {
   public Iterator create() {
      return new DescriptorIterator();
   }
}

}
