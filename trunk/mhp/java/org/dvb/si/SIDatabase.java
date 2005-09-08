package org.dvb.si;

import vdr.mhp.lang.NativeData;



/*This class represents the root of the SI information hierarchy. There is one SIDatabase 
per network interface. In a system with a single network interface there is only one 
SIDatabase object. */

public class SIDatabase {

NativeData nativeData;

protected SIDatabase(NativeData nativeData) {
   this.nativeData=nativeData;
}

NativeData getNativeData() {
   return nativeData;
}

static SIDatabase[] databases = null;

/*
Return an array of SIDatabase objects (one object per network interface). In a system with one network interface, the 
length of this array will be one. The network interface of each SIDatabase is used as data source for all new data 
accessed by this SIDatabase or SIInformation instances obtained from it.This is the  rst method to be called to access 
the DVB-SI API. The returned SIDatabase objects provide the access point to the DVB-SI information. Returns: An array of 
SIDatabase objects, one per network interface. */
public static SIDatabase[] getSIDatabase() {
   if (databases == null) {
      checkDatabases();
      int num=numDatabases();
      databases=new SIDatabase[num];
      for (int i=0; i<num; i++)
         databases[i]=new SIDatabase(databasePointer(i));
   }
   return databases;
}
private static native int numDatabases();
private static native void checkDatabases();
private static native NativeData databasePointer(int index);

public static SIDatabase getDatabaseForChannel(int nid, int tid, int sid) {
   NativeData nD=databaseForChannel(nid, tid, sid);
   if (nD.isNull())
      return null;
      
   getSIDatabase();
   for (int i=0; i<databases.length; i++)
      if (databases[i].nativeData.equals(nD))
         return databases[i];
   return null;
}
private static native NativeData databaseForChannel(int nid, int tid, int sid);


/*static class SIIteratorOnCollection implements SIIterator {
   SIIteratorOnCollection(java.util.Collection c) {
      i = c.iterator();
      size = c.size();
      count = 0;
   }
   
   private final java.util.Iterator i;
   private final int size;
   private int count;
   
   public boolean hasMoreElements() {
      return i.hasNext();
   }
   public Object nextElement() {
      count++;
      return i.next();
   }
   public int numberOfRemainingObjects() {
      return size-count;
   }
}

public static SIIterator getSIIteratorForCollection(java.util.Collection c) {
   return new SIIteratorOnCollection(c);
}

static class OneElementEnumeration implements SIIterator {
   Object element;
   OneElementEnumeration(Object element) {
      this.element=element;
   }
   
   public boolean hasMoreElements() {
      return element!=null;
   }
   public int numberOfRemainingObjects() {
      if (element == null)
         return 0;
      else
         return 1;
   }
   public Object nextElement() throws java.util.NoSuchElementException {
      if (element == null) {
         throw new java.util.NoSuchElementException();
      } else {
         Object ret=element;
         element=null;
         return ret;
      }
   }
}

public static SIIterator getSIIteratorForOneElement(Object element) {
   return new OneElementEnumeration(element);
}
*/


/* --- add/removeXYListener API --- */

/*
Initiate monitoring of the bouquet information. When the bouquet information changes, an event will be delivered to the 
registered listener object. How the monitoring is performed is implementation dependent and especially does not 
necessarily need to be continuous. The event will be delivered as soon as the implementation notices the change which 
might have some delay relative to when the change was actually made in the stream due to resources for the monitoring 
being scheduled between the monitoring activities of different tables. This speci cation does not set any minimum 
requirements for monitoring of the SI tables. This is to be done at a best effort basis by the implementation and is 
entirely implementation dependent. The only requirement is that when an implementation detects a change, e.g. because a 
resident Navigator or an MHP application has retrieved some SI information from the stream, then these listeners are 
noti ed of the change. The monitoring stops silently and permanently when the network interface with which this 
SIDatabase object is associated starts tuning to another transport stream. Parameters: listener - listener object that 
will receive events when a change in the information is detected. bouquetId - bouquet identi er of the bouquet whose 
information will be monitored.Throws: SIIllegalArgumentException - thrown if the identi ers are invalid (e.g. out of 
range) See Also: SIMonitoringListener, SIMonitoringEvent */
public void addBouquetMonitoringListener(SIMonitoringListener listener, int 
bouquetId) {
}

/*
Initiate monitoring of information in the EIT related to present and following events. When the information related to 
those events changes, an event will be delivered to the registered listener object.The scope of the monitoring is 
determined by the original network identi er, transport stream identi er and service identi er. The listener will be 
noti ed about the change of the information in any present and following event within that scope. How the monitoring is 
performed is implementation dependent and especially does not necessarily need to be continuous. The event will be 
delivered as soon as the implementation notices the change which might have some delay relative to when the change was 
actually made in the stream due to resources for the monitoring being scheduled between the monitoring activities of 
different tables. This speci cation does not set any minimum requirements for monitoring of the SI tables. This is to be 
done at a best effort basis by the implementation and is entirely implementation dependent. The only requirement is that 
when an implementation detects a change, e.g. because a resident Navigator or an MHP application has retrieved some SI 
information from the stream, then these listeners are noti ed of the change. The monitoring stops silently and 
permanently when the network interface with which this SIDatabase object is associated starts tuning to another 
transport stream.Parameters: listener - listener object that will receive events when a change in the information is 
detected. originalNetworkId - original network identi er specifying the scope of the monitoring. transportStreamId - 
transport stream identi er specifying the scope of the monitoring. serviceId - service identi er specifying the scope of 
the monitoring Throws: SIIllegalArgumentException - thrown if the identi ers are invalid (e.g. out of range) See Also: 
SIMonitoringListener, SIMonitoringEvent */
public void addEventPresentFollowingMonitoringListener(SIMonitoringListener listener, int originalNetworkId, int 
transportStreamId, int serviceId) {
}

/*
Initiate monitoring of information in the EIT related to scheduled events. When the information related to those events 
changes, an event will be delivered to the registered listener object. The scope of the monitoring is determined by the 
original network identi er, transport stream identi er, service identi er, start time and end time of the schedule 
period. The listener will be noti ed about the change of the information in any scheduled event within that scope. How 
the monitoring is performed is implementation dependent and especially does not necessarily need to be continuous. The 
event will be delivered as soon as the implementation notices the change which might have some delay relative to when 
the change was actually made in the stream due to resources for the monitoring being scheduled between the monitoring 
activities of different tables. This speci cation does not set any minimum requirements for monitoring of the SI tables. 
This is to be done at a best effort basis by the implementation and is entirely implementation dependent. The only 
requirement is that when an implementation detects a change, e.g. because a resident Navigator or an MHP application has 
retrieved some SI information from the stream, then these listeners are noti ed of the 
change. */
public void addEventScheduleMonitoringListener(SIMonitoringListener listener, int originalNetworkId, int 
transportStreamId, int serviceId, java.util.Date startTime, java.util.Date endTime) {
}

/*
Initiate monitoring of the network information. When the network information changes, an event will be delivered to the 
registered listener object. How the monitoring is performed is implementation dependent and especially does not 
necessarily need to be continuous. The event will be delivered as soon as the implementation notices the change which 
might have some delay relative to when the change was actually made in the stream due to resources for the monitoring 
being scheduled between the monitoring activities of different tables. This speci cation does not set any minimum 
requirements for monitoring of the SI tables. This is to be done at a best effort basis by the implementation and is 
entirely implementation dependent. The only requirement is that when an implementation detects a change, e.g. because a 
resident Navigator or an MHP application has retrieved some SI information from the stream, then these listeners are 
noti ed of the change. The monitoring stops silently and permanently when the network interface with which this 
SIDatabase object is associated starts tuning to another transport stream.Parameters: listener - listener object that 
will receive events when a change in the information is detected. networkId - network identi er of the network whose 
information will be monitored. Throws: SIIllegalArgumentException - thrown if the identi ers are invalid (e.g. out of 
range) See Also: SIMonitoringListener, SIMonitoringEvent */
public void addNetworkMonitoringListener(SIMonitoringListener listener, int 
networkId) {
}

/*
Initiate monitoring of information in the PMT related to a service. When the information related to a service changes, 
an event will be delivered to the registered listener object. How the monitoring is performed is implementation 
dependent and especially does not necessarily need to be continuous. The event will be delivered as soon as the 
implementation notices the change which might have some delay relative to when the change was actually made in the 
stream due to resources for the monitoring being scheduled between the monitoring activities of different tables. This 
speci cation does not set any minimum requirements for monitoring of the SI tables. This is to be done at a best effort 
basis by the implementation and is entirely implementation dependent. The only requirement is that when an 
implementation detects a change, e.g. because a resident Navigator or an MHP application has retrieved some SI 
information from the stream, then these listeners are noti ed of the change. */
public void addPMTServiceMonitoringListener(SIMonitoringListener listener, int originalNetworkId, int transportStreamId, 
int serviceId) {
}

/*
Initiate monitoring of information in the SDT related to services. When the information related to services changes, an 
event will be delivered to the registered listener object. The scope of the monitoring is determined by the original 
network identi er and transport stream identi er. The listener will be noti ed about the change of the information in 
any service within that scope. How the monitoring is performed is implementation dependent and especially does not 
necessarily need to be continuous. The event will be delivered as soon as the implementation notices the change which 
might have some delay relative to when the change was actually made in the stream due to resources for the monitoring 
being scheduled between the monitoring activities of different tables. This speci cation does not set any minimum 
requirements for monitoring of the SI tables. This is to be done at a best effort basis by the implementation and is 
entirely implementation dependent. The only requirement is that when an implementation detects a change, e.g. because a 
resident Navigator or an MHP application has retrieved some SI information from the stream, then these listeners are 
noti ed of the change. The monitoring stops silently and permanently when the network interface with which this 
SIDatabase object is associated starts tuning to another transport stream. Parameters: listener - listener object that 
will receive events when a change in the information is detected. originalNetworkId - original network identi er 
specifying the scope of the monitoring. transportStreamId - transport stream identi er specifying the scope of the 
monitoring. Throws: SIIllegalArgumentException - thrown if the identi ers are invalid (e.g. out of range) See Also: 
SIMonitoringListener, SIMonitoringEvent */
public void addServiceMonitoringListener(SIMonitoringListener listener, int originalNetworkId, int 
transportStreamId) {
}

/*
Removes the registration of an event listener for bouquet information monitoring. If this method is called with a 
listener that is registered but not with the same identi ers of the SI objects as given in the parameters, the method 
shall fail silently and the listeners stays registered with those identi ers that it has been added. Parameters: 
listener - listener object that has previously been registered bouquetId - bouquet identi er of the bouquet whose 
information has been requested to be monitored Throws: SIIllegalArgumentException - thrown if the identi ers are invalid 
(e.g. out of range) See Also: SIMonitoringListener, SIMonitoringEvent */
public void removeBouquetMonitoringListener(SIMonitoringListener listener, int 
bouquetId) {
}

/*
Removes the registration of an event listener for monitoring information related to present and following events If this 
method is called with a listener that is registered but not with the same identi ers of the SI objects as given in the 
parameters, the method shall fail silently and the listeners stays registered with those identi ers that it has been 
added. Parameters: listener - listener object that has previously been registered originalNetworkId - original network 
identi er specifying the scope of the monitoring. transportStreamId - transport stream identi er specifying the scope of 
the monitoring. serviceId - service identi er specifying the scope of the monitoring Throws: SIIllegalArgumentException 
- thrown if the identi ers are invalid (e.g. out of range) See Also: SIMonitoringListener, 
SIMonitoringEvent */
public void removeEventPresentFollowingMonitoringListener(SIMonitoringListener listener, int originalNetworkId, int 
transportStreamId, int serviceId) {
}

/*
Removes the registration of an event listener for monitoring information related to scheduled events for all periods If 
this method is called with a listener that is registered but not with the same identi ers of the SI objects as given in 
the parameters, the method shall fail silently and the listeners stays registered with those identi ers that it has been 
added. Parameters: listener - listener object that has previously been registeredoriginalNetworkId - original network 
identi er specifying the scope of the monitoring. transportStreamId - transport stream identi er specifying the scope of 
the monitoring. serviceId - service identi er specifying the scope of the monitoring Throws: SIIllegalArgumentException 
- thrown if the identi ers are invalid (e.g. out of range) See Also: SIMonitoringListener, 
SIMonitoringEvent */
public void removeEventScheduleMonitoringListener(SIMonitoringListener listener, int originalNetworkId, int 
transportStreamId, int serviceId) {
}

/*
Removes the registration of an event listener for network information monitoring. If this method is called with a 
listener that is registered but not with the same identi ers of the SI objects as given in the parameter, the method 
shall fail silently and the listeners stays registered with those identi ers that it has been added. Parameters: 
listener - listener object that has previously been registered networkId - network identi er of the network which is no 
longer to be monitored by the listener Throws: SIIllegalArgumentException - thrown if the identi ers are invalid (e.g. 
out of range) See Also: SIMonitoringListener, SIMonitoringEvent */
public void removeNetworkMonitoringListener(SIMonitoringListener listener, int 
networkId) {
}

/*
Removes the registration of an event listener for monitoring information in the PMT related to a service. If this method 
is called with a listener that is registered but not with the same identi ers of the SI objects as given in the 
parameters, the method shall fail silently and the listeners stays registered with those identi ers that it has been 
added. Parameters: listener - listener object that has previously been registered originalNetworkId - original network 
identi er of the service transportStreamId - transport stream identi er of the service serviceId - service identi er 
specifying the service whose information has been requested to be monitored Throws: SIIllegalArgumentException - thrown 
if the identi ers are invalid (e.g. out of range) See Also: SIMonitoringListener, SIMonitoringEventpublic void 
removeServiceMonitoringListener(SIMonitoringListener listener, int originalNetwor */
public void removePMTServiceMonitoringListener(SIMonitoringListener listener, int originalNetworkId, int 
transportStreamId, int serviceId) {
}

/*
Removes the registration of an event listener for monitoring information related to services. If this method is called 
with a listener that is registered but not with the same identi ers of the SI objects asgiven in the parameters, the 
method shall fail silently and the listeners stays registered with those identi ers that it has been added. Parameters: 
listener - listener object that has previously been registered originalNetworkId - original network identi er specifying 
the scope of the monitoring. transportStreamId - transport stream identi er specifying the scope of the monitoring. 
Throws: SIIllegalArgumentException - thrown if the identi ers are invalid (e.g. out of range) See Also: 
SIMonitoringListener, SIMonitoringEvent */
public void removeServiceMonitoringListener(SIMonitoringListener listener, int originalNetworkId, int 
transportStreamId) {
}









/* --- retrieveXY API --- */


/*
Retrieve information associated with the actual network. The actual network is the network carrying the transport stream 
currently selected by the network interface connected to this SIDatabase. The SIIterator that is returned with the event 
when the request completes successfully will contain an object that implements the SINetwork interface. If no matching 
object was found, the appropriate one of the following events is sent:ObjectNotInCacheEvent ObjectNotInTableEvent or 
TableNotFoundEvent Parameters: retrieveMode - Mode of retrieval indicating whether the data should be retrieved only 
from the cache (FROM_CACHE_ONLY), from the cache if available and if not from the stream (FROM_CACHE_OR_STREAM), or 
always from the stream (FROM_STREAM_ONLY). appData - An object supplied by the application. This object will be 
delivered to the listener when the request completes. The application can use this objects for internal communication 
purposes. If the application does not need any application data, the parameter can be null.listener - 
SIRetrievalListener that will receive the event informing about the completion of the request. someDescriptorTags - A 
list of hints for descriptors (identi ed by their tags) the application is interested in. If the array contains -1 as 
its one and only element, the application is interested in all descriptors. If someDescriptorTags is null, the 
application is not interested in descriptors. All values that are out of the valid range for descriptor tags (i.e. 
0...255) are ignored, except for the special meaning of -1 as the only element in the array. Returns: An SIRequest 
object Throws: SIIllegalArgumentException - thrown if the retrieveMode is invalid */
public SIRequest retrieveActualSINetwork(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, 
short[] someDescriptorTags) {
   return SIDatabaseRequest.ActualNetworkRequest(appData, listener, this, retrieveMode);
}

/*
Retrieve information associated with the actual services. The actual services are the services in the transport stream 
currently selected by the network interface connected to this SIDatabase.The SIIterator that is returned with the event 
when the request completes successfully will contain one or more objects that implement the SIService interface. If no 
matching object was found, the appropriate one of the following events is sent: ObjectNotInCacheEvent, 
ObjectNotInTableEvent or TableNotFoundEvent. Parameters: retrieveMode - Mode of retrieval indicating whether the data 
should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if available and if not from the stream 
(FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - An object supplied by the application. 
This object will be delivered to the listener when the request completes. The application can use this objects for 
internal communication purposes. If the application does not need any application data, the parameter can be null. 
listener - SIRetrievalListener that will receive the event informing about the completion of the request. 
someDescriptorTags - A list of hints for descriptors (identi ed by their tags) the application is interested in. If the 
array contains -1 as its one and only element, the application is interested in all descriptors. If someDescriptorTags 
is null, the application is not interested in descriptors. All values that are out of the valid range for descriptor 
tags (i.e. 0...255) are ignored, except for the special meaning of -1 as the only element in the array. Returns: An 
SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is invalid See Also: SIRequest, 
SIRetrievalListener, SIService, DescriptorTag */
public SIRequest retrieveActualSIServices(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, 
short[] someDescriptorTags) {
   return SIDatabaseRequest.ActualServicesRequest(appData, listener, this, retrieveMode);
}

/*
Retrieve information associated with the actual transport stream. The actual transport stream is the transport stream 
currently selected by the network interface connected to this SIDatabase. The SIIterator that is returned with the event 
when the request completes successfully will contain an object that implements the SITransportStreamNIT interface. If no 
matching object was found,the appropriate one of the following events is sent: ObjectNotInCacheEvent 
ObjectNotInTableEvent or TableNotFoundEvent. Parameters: retrieveMode - Mode of retrieval indicating whether the data 
should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if available and if not from the stream 
(FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - An object supplied by the application. 
This object will be delivered to the listener when the request completes. The application can use this objects for 
internal communication purposes. If the application does not need any application data, the parameter can be 
null.listener - SIRetrievalListener that will receive the event informing about the completion of the request. 
someDescriptorTags - A list of hints for descriptors (identi ed by their tags) the application is interested in. If the 
array contains -1 as its one and only element, the application is interested in all descriptors. If someDescriptorTags 
is null, the application is not interested in descriptors. All values that are out of the valid range for descriptor 
tags (i.e. 0...255) are ignored, except for the special meaning of -1 as the only element in the array.Returns: An 
SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is 
invalid */
public SIRequest retrieveActualSITransportStream(short retrieveMode, java.lang.Object appData, SIRetrievalListener 
listener, short[] someDescriptorTags) {
   return SIDatabaseRequest.ActualTransportStreamRequest(appData, listener, this, retrieveMode);
}

/*
Retrieve PMT elementary stream information associated with components of a service. The required component(s) can be 
speci ed by its DVB locator. The SIIterator that is returned with the event when the request completes successfully will 
contain one or more objects that implement the PMTElementaryStream interface. If no matching object was found, the 
appropriate one of the following events is sent: ObjectNotInCacheEvent, ObjectNotInTableEvent or TableNotFoundEvent. 
Parameters: retrieveMode - Mode of retrieval indicating whether the data should be retrieved only from the cache 
(FROM_CACHE_ONLY), from the cache if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the 
stream (FROM_STREAM_ONLY). appData - An object supplied by the application. This object will be delivered to the 
listener when the request completes. The application can use this objects for internal communication purposes. If the 
application does not need any application data, the parameter can be null.listener - SIRetrievalListener that will 
receive the event informing about the completion of the request. dvbLocator - DVB Locator identifying the component(s) 
of a service. The locator may be more speci c than identifying one or more service components, but this method will only 
use the parts starting from the beginning up to the component tags. someDescriptorTags - A list of hints for descriptors 
(identi ed by their tags) the application is interested in. If the array contains -1 as its one and only element, the 
application is interested in all descriptors. If someDescriptorTags is null, the application is not interested in 
descriptors. All values that are out of the valid range for descriptor tags (i.e. 0...255) are ignored, except for the 
special meaning of -1 as the only element in the array. Returns: An SIRequest object Throws: SIIllegalArgumentException 
- thrown if the retrieveMode is invalid or if the locator is invalid and does not identify one or more service 
components */
public SIRequest retrievePMTElementaryStreams(short retrieveMode, java.lang.Object appData, SIRetrievalListener 
listener, org.davic.net.dvb.DvbLocator dvbLocator, short[] someDescriptorTags) {
   return SIDatabaseRequest.PMTElementaryStreamsRequest(appData, listener, this, retrieveMode, dvbLocator.getServiceId(), dvbLocator.getComponentTags());
}

/*
Retrieve PMT elementary stream information associated with components of a service from the actual transport stream of 
this SIDatabase object. The elementary streams can be speci ed by theiridenti cation. When -1 is speci ed for 
componentTag then elementary streams shall be retrieved regardless of their component tag. The SIIterator that is 
returned with the event when the request completes successfully will contain one or more objects that implement the 
PMTElementaryStream interface. If no matching object was found, the appropriate one of the following events is sent: 
ObjectNotInCacheEvent, ObjectNotInTableEvent or TableNotFoundEvent. Parameters: retrieveMode - Mode of retrieval 
indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if available and 
if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - An object 
supplied by the application. This object will be delivered to the listener when the request completes. The application 
can use this objects for internal communication purposes. If the application does not need any application data, the 
parameter can be null. listener - SIRetrievalListener that will receive the event informing about the completion of the 
request. serviceId - Identi cation of the elementary streams to be retrieved: service identi er componentTag - Identi 
cation of the elementary streams to be retrieved: component tag (-1 means return elementary streams regardless of their 
component tag) someDescriptorTags - A list of hints for descriptors (identi ed by their tags) the application is 
interested in. If the array contains -1 as its one and only element, the application is interested in all descriptors. 
If someDescriptorTags is null, the application is not interested in descriptors. All values that are out of the valid 
range for descriptor tags (i.e. 0...255) are ignored, except for the special meaning of -1 as the only element in the 
array. Returns: An SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is invalid or the 
numeric identi ers are out of range */
public SIRequest retrievePMTElementaryStreams(short retrieveMode, java.lang.Object appData, SIRetrievalListener 
listener, int serviceId, int componentTag, short[] someDescriptorTags) {
   int[] ctag=new int[1];
   ctag[0]=componentTag;
   return SIDatabaseRequest.PMTElementaryStreamsRequest(appData, listener, this, retrieveMode, serviceId, ctag);
}

/*
Retrieve PMT information associated with a service. The required service can be speci ed by its DVB locator. The 
SIIterator that is returned with the event when the request completes successfully will contain an object that 
implements the PMTService interface. If no matching object was found,the appropriate one of the following events is 
sent: ObjectNotInCacheEvent ObjectNotInTableEvent or TableNotFoundEvent. Parameters: retrieveMode - Mode of retrieval 
indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if available and 
if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - An object 
supplied by the application. This object will be delivered to the listener when the request completes. The application 
can use this objects for internal communication purposes. If the application does not need any application data, the 
parameter can be null.listener - SIRetrievalListener that will receive the event informing about the completion of the 
request. dvbLocator - DVB Locator identifying the service. The locator may be more speci c than identifying a service, 
but this method will only use the parts starting from the beginning up to the service id. someDescriptorTags - A list of 
hints for descriptors (identi ed by their tags) the application is interested in. If the array contains -1 as its one 
and only element, the application is interested in all descriptors. If someDescriptorTags is null, the application is 
not interested in descriptors. All values that are out of the valid range for descriptor tags (i.e. 0...255) are 
ignored, except for the special meaning of -1 as the only element in the array. Returns: An SIRequest object Throws: 
SIIllegalArgumentException - thrown if the retrieveMode is invalid or the locator is invalid and does not identify a 
service */
public SIRequest retrievePMTService(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, 
org.davic.net.dvb.DvbLocator dvbLocator, short[] someDescriptorTags) {
   return SIDatabaseRequest.PMTServicesRequest(appData, listener, this, retrieveMode, dvbLocator.getServiceId());
}

/*
Retrieve PMT information associated with services from the actual transport stream of this SIDatabase object. The 
required services can be speci ed by their identi cation. When -1 is speci ed as serviceId then services shall be 
retrieved regardless of their service id. The SIIterator that is returned with the event when the request completes 
successfully will contain one or more objects that implement the PMTService interface. If no matching object was found, 
the appropriate one of the following events is sent: ObjectNotInCacheEvent, ObjectNotInTableEvent or TableNotFoundEvent. 
Parameters: retrieveMode - Mode of retrieval indicating whether the data should be retrieved only from the cache 
(FROM_CACHE_ONLY), from the cache if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the 
stream (FROM_STREAM_ONLY).appData - An object supplied by the application. This object will be delivered to the listener 
when the request completes. The application can use this objects for internal communication purposes. If the application 
does not need any application data, the parameter can be null. listener - SIRetrievalListener that will receive the 
event informing about the completion of the request. serviceId - Identi cation of the services to be retrieved: service 
identi er (-1 means return services regardless of their service id) someDescriptorTags - A list of hints for descriptors 
(identi ed by their tags) the application is interested in. If the array contains -1 as its one and only element, the 
application is interested in all descriptors. If someDescriptorTags is null, the application is not interested in 
descriptors. All values that are out of the valid range for descriptor tags (i.e. 0...255) are ignored, except for the 
special meaning of -1 as the only element in the array. Returns: An SIRequest object Throws:SIIllegalArgumentException - 
thrown if the retrieveMode is invalid or the numeric identi ers are out of range */
public SIRequest retrievePMTServices(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, int 
serviceId, short[] someDescriptorTags) {
   return SIDatabaseRequest.PMTServicesRequest(appData, listener, this, retrieveMode, serviceId);
}

/*
Retrieve information associated with bouquets. A bouquet can be speci ed by its identi cation. When bouquetId is set to 
-1, all bouquets signalled in the BAT of the currently received transport stream on that network interface are 
retrieved. The SIIterator that is returned with the event when the request completes successfully will contain one or 
more objects that implement the SIBouquet interface. If no matching object was found, the appropriate one of the 
following events is sent: ObjectNotInCacheEvent, ObjectNotInTableEvent or TableNotFoundEvent. Parameters: retrieveMode - 
Mode of retrieval indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache 
if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - 
An object supplied by the application. This object will be delivered to the listener when the request completes. The 
application can use this objects for internal communication purposes. If the application does not need any application 
data, the parameter can be null.listener - SIRetrievalListener that will receive the event informing about the 
completion of the request. bouquetId - Identi er of the bouquet to be retrieved or -1 for all bouquets signalled on the 
currently received transport stream. someDescriptorTags - A list of hints for descriptors (identi ed by their tags) the 
application is interested in. If the array contains -1 as its one and only element, the application is interested in all 
descriptors. If someDescriptorTags is null, the application is not interested in descriptors. All values that are out of 
the valid range for descriptor tags (i.e. 0...255) are ignored, except for the special meaning of -1 as the only element 
in the array. Returns: An SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is invalid or 
the numeric identi ers are out of range */
public SIRequest retrieveSIBouquets(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, int 
bouquetId, short[] someDescriptorTags) {
   return SIDatabaseRequest.BouquetsRequest(appData, listener, this, retrieveMode, bouquetId);
}

/*
Retrieve information associated with networks. A network can be speci ed by its identi cation. When networkId is set to 
-1, all networks signalled in NIT Actual and Other of the currently received TransportStream on that network interface 
shall be retrieved. The SIIterator that is returned with the event when the request completes successfully will contain 
one or more objects that implement the SINetwork interface. If no matching object was found, theappropriate one of the 
following events is sent: ObjectNotInCacheEvent, ObjectNotInTableEvent or TableNotFoundEvent. Parameters: retrieveMode - 
Mode of retrieval indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache 
if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - 
An object supplied by the application. This object will be delivered to the listener when the request completes. The 
application can use this objects for internal communication purposes. If the application does not need any application 
data, the parameter can be null. listener - SIRetrievalListener that will receive the event informing about the 
completion of the request. networkId - Identi cation of the network to be retrieved or -1 for all networks currently 
signalled. someDescriptorTags - A list of hints for descriptors (identi ed by their tags) the application is interested 
in. If the array contains -1 as its one and only element, the application is interested in all descriptors. If 
someDescriptorTags is null, the application is not interested in descriptors. All values that are out of the valid range 
for descriptor tags (i.e. 0...255) are ignored, except for the special meaning of -1 as the only element in the array. 
Returns: An SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is invalid or the numeric 
identi ers are out of range */
public SIRequest retrieveSINetworks(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, int 
networkId, short[] someDescriptorTags) {
   return SIDatabaseRequest.NetworksRequest(appData, listener, this, retrieveMode, networkId);
}

/*
Retrieve information associated with a service. The required service can be speci ed by its DVB locator. The SIIterator 
that is returned with the event when the request completes successfully will contain an object that implements the 
SIService interface. If no matching object was found,the appropriate one of the following events is 
sent:ObjectNotInCacheEvent ObjectNotInTableEvent or TableNotFoundEvent" Parameters: retrieveMode - Mode of retrieval 
indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if available and 
if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - An object 
supplied by the application. This object will be delivered to the listener when the request completes. The application 
can use this objects for internal communication purposes. If the application does not need any application data, the 
parameter can be null. listener - SIRetrievalListener that will receive the event informing about the completion of the 
request. dvbLocator - DVB locator identifying the service.The locator may be more speci c than identifying a service, 
but this method will only use the parts starting from the beginning up to the service id.someDescriptorTags - A list of 
hints for descriptors (identi ed by their tags) the application is interested in. If the array contains -1 as its one 
and only element, the application is interested in all descriptors. If someDescriptorTags is null, the application is 
not interested in descriptors. All values that are out of the valid range for descriptor tags (i.e. 0...255) are 
ignored, except for the special meaning of -1 as the only element in the array. Returns: An SIRequest object Throws: 
SIIllegalArgumentException - thrown if the retrieveMode is invalid or the locator is invalid and does not identify a 
service */
public SIRequest retrieveSIService(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, 
org.davic.net.dvb.DvbLocator dvbLocator, short[] someDescriptorTags) {
   return SIDatabaseRequest.ServicesRequest(appData, listener, this, retrieveMode, dvbLocator.getOriginalNetworkId(), dvbLocator.getTransportStreamId(), dvbLocator.getServiceId());
}

/*
Retrieve information associated with services. The required services can be speci ed by their identi cation. When -1 is 
speci ed for transportStreamId then services shall be retrieved regardless of their transport stream id. When -1 is 
speci ed for serviceId then services shall be retrieved regardless of their service id. The SIIterator that is returned 
with the event when the request completes successfully will contain one or more objects that implement the SIService 
interface. If no matching object was found, the appropriate one of the following events is sent: ObjectNotInCacheEvent, 
ObjectNotInTableEvent or TableNotFoundEvent. Parameters: retrieveMode - Mode of retrieval indicating whether the data 
should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if available and if not from the stream 
(FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - An object supplied by the application. 
This object will be delivered to the listener when the request completes. The application can use this objects for 
internal communication purposes. If the application does not need any application data, the parameter can be 
null.listener - SIRetrievalListener that will receive the event informing about the completion of the request. 
originalNetworkId - Identi cation of the services to be retrieved: original network identi er transportStreamId - Identi 
cation of the services to be retrieved: transport stream identi er (-1 means return services regardless of their 
transport stream id) serviceId - Identi cation of the services to be retrieved: service identi er (-1 means return 
services regardless of their service id) someDescriptorTags - A list of hints for descriptors (identi ed by their tags) 
the application is interested in. If the array contains -1 as its one and only element, the application is interested in 
all descriptors. If someDescriptorTags is null, the application is not interested in descriptors. All values that are 
out of the valid range for descriptor tags (i.e. 0...255) are ignored, except for the special meaning of -1 as the only 
element in the array. Returns: An SIRequest object Throws:SIIllegalArgumentException - thrown if the retrieveMode is 
invalid or the numeric identi ers are out of range */
public SIRequest retrieveSIServices(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, int 
originalNetworkId, int transportStreamId, int serviceId, short[] someDescriptorTags) {
   return SIDatabaseRequest.ServicesRequest(appData, listener, this, retrieveMode, originalNetworkId, transportStreamId, serviceId);
}

/*
Retrieve information associated with time from the Time and Date Table (TDT) from the actual transport stream. The 
SIIterator that is returned with the event when the request completes successfully will contain an object that 
implements the SITime interface. If no matching object was found,the appropriate one of the following events is 
sent:ObjectNotInCacheEvent ObjectNotInTableEvent or TableNotFoundEvent. Parameters: retrieveMode - Mode of retrieval 
indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if available and 
if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - An object 
supplied by the application. This object will be delivered to the listener when the request completes. The application 
can use this objects for internal communication purposes. If the application does not need any application data, the 
parameter can be null. listener - SIRetrievalListener that will receive the event informing about the completion of the 
request.Returns: An SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is 
invalid */
public SIRequest retrieveSITimeFromTDT(short retrieveMode, java.lang.Object appData, SIRetrievalListener 
listener) {
   return SIDatabaseRequest.TDTRequest(appData, listener, this, retrieveMode);
}

/*
Retrieve information associated with time from the Time Offset Table (TOT) from the actual transport stream. The time 
information will be accompanied with offset information The SIIterator that is returned with the event when the request 
completes successfully will contain an object that implements the SITime interface. If no matching object was found,the 
appropriate one of the following events is sent:ObjectNotInCacheEvent ObjectNotInTableEvent or TableNotFoundEvent. 
Parameters: retrieveMode - Mode of retrieval indicating whether the data should be retrieved only from the cache 
(FROM_CACHE_ONLY), from the cache if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the 
stream (FROM_STREAM_ONLY). appData - An object supplied by the application. This object will be delivered to the 
listener when the request completes. The application can use this objects for internal communication purposes. If the 
application does not need any application data, the parameter can be null. listener - SIRetrievalListener that will 
receive the event informing about the completion of the request.someDescriptorTags - A list of hints for descriptors 
(identi ed by their tags) the application is interested in. If the array contains -1 as its one and only element, the 
application is interested in all descriptors. If someDescriptorTags is null, the application is not interested in 
descriptors. All values that are out of the valid range for descriptor tags (i.e. 0...255) are ignored, except for the 
special meaning of -1 as the only element in the array. Returns: An SIRequest object Throws: SIIllegalArgumentException 
- thrown if the retrieveMode is invalid */
public SIRequest retrieveSITimeFromTOT(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, 
short[] someDescriptorTags) {
   return SIDatabaseRequest.TDTRequest(appData, listener, this, retrieveMode);
}

/*
Retrieve the SITransportStreamDescription object representing the information of the TSDT table in the actual transport 
stream of this SIDatabase object. The SIIterator that is returned with the event when the request completes successfully 
will contain an object that implements the SITransportStreamDescription interface. If no matching object was found,the 
appropriate one of the following events is sent:ObjectNotInCacheEvent ObjectNotInTableEvent or TableNotFoundEvent. 
Parameters: retrieveMode - Mode of retrieval indicating whether the data should be retrieved only from the cache 
(FROM_CACHE_ONLY), from the cache if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the 
stream (FROM_STREAM_ONLY). appData - An object supplied by the application. This object will be delivered to the 
listener when the request completes. The application can use this objects for internal communication purposes. If the 
application does not need any application data, the parameter can be null. listener - SIRetrievalListener that will 
receive the event informing about the completion of the request. someDescriptorTags - A list of hints for descriptors 
(identi ed by their tags) the application is interested in. If the array contains -1 as its one and only element, the 
application is interested in all descriptors. If someDescriptorTags is null, the application is not interested in 
descriptors. All values that are out of the valid range for descriptor tags (i.e. 0...255) are ignored, except for the 
special meaning of -1 as the only element in the array. Returns: An SIRequest object Throws: SIIllegalArgumentException 
- thrown if the retrieveMode is invalid */
public SIRequest retrieveSITransportStreamDescription(short retrieveMode, java.lang.Object appData, SIRetrievalListener 
listener, short[] someDescriptorTags) {
   return SIDatabaseRequest.TransportStreamDescriptionRequest(appData, listener, this, retrieveMode);
}



}
