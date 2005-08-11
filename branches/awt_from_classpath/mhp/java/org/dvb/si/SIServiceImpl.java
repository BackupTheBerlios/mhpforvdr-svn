package org.dvb.si;



public class SIServiceImpl extends SICommonObject implements SIService,
       javax.tv.service.navigation.ServiceDetails, javax.tv.service.navigation.CAIdentification {

//nativeData is a pointer to an SDT::Service

SIServiceImpl (SIDatabaseRequest request, long nativeData) {
   super(request, nativeData);
}

/*
Return true when the information contained in the object that implements this interface was  ltered from an 'actual' 
table or from a table with no 'actual/other' distinction. Returns: true if the information comes from an 'actual' table 
or from a table with no 'actual/other' distiction, otherwise returns false */
public boolean fromActual() {
   //TODO
   return true;
}

/*
This method de nes extra semantics for the SIInformation.getDescriptorTags method. If the NIT subtable on which this 
SINetwork object is based consists of multiple sections, then this method returns the descriptor tags in the order they 
appear when concatenating the descriptor loops of the different sections. Overrides: getDescriptorTags() in interface 
SIInformation Returns: The tags of the descriptors actually broadcast for the object (identi ed by their 
tags). */
public short[] getDescriptorTags() {
   return descriptorTags(nativeData);
}
private native short[] descriptorTags(long nativeData);


/*
Gets a DvbLocator that identi es this service. Returns: The DvbLocator of this 
service */
public org.davic.net.dvb.DvbLocator getDvbLocator() {
   try {
      return new org.davic.net.dvb.DvbLocator(getOriginalNetworkID(), getTransportStreamID(), getServiceID());
   } catch (javax.tv.locator.InvalidLocatorException e) {
      e.printStackTrace();
      return null;
   }
}


/*
Get the EIT_present_following_ ag value, true indicates this service has present and/or following event information. 
Returns: The EIT_present_following_ ag value. */
public boolean getEITPresentFollowingFlag() {
   return getEITPresentFollowingFlag(nativeData);
}
private native boolean getEITPresentFollowingFlag(long nativeData);


/*
Get the EIT_schedule_ ag value, true indicates this services has scheduled event information. Returns: The EIT_schedule_ 
ag value. */
public boolean getEITScheduleFlag() {
   return getEITScheduleFlag(nativeData);
}
private native boolean getEITScheduleFlag(long nativeData);


/*
Retrieve the free_CA_mode value of this service, false indicates none of the components of this service are scrambled. 
Returns:The free_CA_mode value of this service. */
public boolean getFreeCAMode() {
   return getFreeCAMode(nativeData);
}
private native boolean getFreeCAMode(long nativeData);


/*
This method returns the name of the service represented by this service. The name is extracted from the 
service_descriptor or optionally from the multilingual_service_name_descriptor. If this descriptor is not present "" is 
returned. All control characters as de ned in ETR 211 are ignored. For each character the DVB-SI 8 bit character code is 
mapped to the appropriate Unicode representation. Returns: The name of this 
service. */
public java.lang.String getName() {
   return new String(getName(nativeData));
}
private native byte[] getName(long nativeData);


/*
Get the original network identi cation. Returns: The original network identi cation identi 
er. */
public int getOriginalNetworkID() {
   return getOriginalNetworkID(nativeData);
}
private native int getOriginalNetworkID(long nativeData);


/*
This method returns the service provider name of this service The service provider name is extracted from the 
service_descriptor or optionally from the multilingual_service_name_descriptor. If this descriptor is not present "" is 
returned. All control characters as de ned in ETR 211 are ignored. For each character the DVB-SI 8 bit character code is 
mapped to the appropriate Unicode representation. Returns: The service provider name of this 
service. */
public java.lang.String getProviderName() {
   return new String(getProviderName(nativeData));
}
private native byte[] getProviderName(long nativeData);


/*
Retrieve the running status of this service. Returns: The running status (the possible values are de ned in the 
SIRunningStatus interface) */
public byte getRunningStatus() {
   return getRunningStatus(nativeData);
}
private native byte getRunningStatus(long nativeData);


/*
Get the service identi cation. Returns: The service identi cation identi er. */
public int getServiceID() {
   return getServiceID(nativeData);
}
private native int getServiceID(long nativeData);


/*
This method returns the short name (ETR 211) of the service provider of this service without emphasis marks. The name is 
extracted from the service_descriptor or optionally from the multilingual_service_name_descriptor. When this information 
is not available "" is returned. For each character the DVB-SI 8 bit character code is mapped to the appropriate Unicode 
representation. Returns: The short service provider name of this service. */
public java.lang.String getShortProviderName() {
   return new String(getShortProviderName(nativeData));
}
private native byte[] getShortProviderName(long nativeData);


/*
This method returns the short name (ETR 211) of this service without emphasis marks. The name is extracted from the 
service_descriptor or optionally from the multilingual_service_name_descriptor. When this information is not available 
"" is returned. For each character the DVB-SI 8 bit character code is mapped to the appropriate Unicode representation. 
Returns: The short name of this service. */
public java.lang.String getShortServiceName() {
   return new String(getShortServiceName(nativeData));
}
private native byte[] getShortServiceName(long nativeData);


/*
Get the service type. The service type is extracted from the service_descriptor. Returns: The service type. (Some of the 
possible values are de ned in the SIServiceType interface.) */
public short getSIServiceType() {
   return getSIServiceType(nativeData);
}
private native short getSIServiceType(long nativeData);


/*
Returns the textual service identi ers related to this object. Overrides: getTextualServiceIdentifiers() in interface 
TextualServiceIdentifierQuery Returns: an array of String objects containing the textual service identi ers or null if 
none are present. */
public java.lang.String[] getTextualServiceIdentifiers() {
   //TODO
   return null;
}


/*
Get the transport stream identi cation. Returns: The transport stream identi cation identi 
er. */
public int getTransportStreamID() {
   return getTransportStreamID(nativeData);
}
private native int getTransportStreamID(long nativeData);


/*
Retrieve information associated with the following event from the EIT-present/following.The SIIterator that is returned 
with the event when the request completes successfully will contain an object that implements the SIEvent interface. If 
no matching object was found,the appropriate one of the following events is sent: ObjectNotInCacheEvent 
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
SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is 
invalid */
public SIRequest retrieveFollowingSIEvent(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] someDescriptorTags) {
   //tags can be ignored, will be fetched anyway
   return SIDatabaseRequest.PresentFollowingEventRequest(appData, listener, request.db, retrieveMode, false, getTransportStreamID(), getServiceID());
}


/*
Retrieve the PMTService information associated with this service. The SIIterator that is returned with the event when 
the request completes successfully will contain an object that implements the PMTService interface. If no matching 
object was found,the appropriate one of the following events is sent:ObjectNotInCacheEvent ObjectNotInTableEvent or 
TableNotFoundEvent. Parameters: retrieveMode - Mode of retrieval indicating whether the data should be retrieved only 
from the cache (FROM_CACHE_ONLY), from the cache if available and if not from the stream (FROM_CACHE_OR_STREAM), or 
always from the stream (FROM_STREAM_ONLY). appData - An object supplied by the application. This object will be 
delivered to the listener when the request completes. The application can use this objects for internal communication 
purposes. If the application does not need any application data, the parameter can be null. listener - 
SIRetrievalListener that will receive the event informing about the completion of the request. someDescriptorTags - A 
list of hints for descriptors (identi ed by their tags) the application is interested in. If the array contains -1 as 
its one and only element, the application is interested in all descriptors. If someDescriptorTags is null, the 
application is not interested in descriptors. All values that are out of the valid range for descriptor tags (i.e. 
0...255) are ignored, except for the special meaning of -1 as the only element in the array. Returns:An SIRequest object 
Throws: SIIllegalArgumentException - thrown if the retrieveMode is invalid */
public SIRequest retrievePMTService(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] 
someDescriptorTags) {
   //tags can be ignored, will be fetched anyway
   return SIDatabaseRequest.PMTServicesRequest(appData, listener, request.db, retrieveMode, getServiceID());
}


/*
Retrieve information associated with the present event from the EIT-present/following. The SIIterator that is returned 
with the event when the request completes successfully will contain an object that implements the SIEvent interface. If 
no matching object was found,the appropriate one of the following events is sent:ObjectNotInCacheEvent 
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
SIRequesappData, SIRetrievalListener listener, short[] someDescriptorTagst) object Throws: SIIllegalArgumentException - thrown if the retrieveMode is 
invalid */
public SIRequest retrievePresentSIEvent(short retrieveMode, java.lang.Object 
appData, SIRetrievalListener listener, short[] someDescriptorTags) {
   //tags can be ignored, will be fetched anyway
   return SIDatabaseRequest.PresentFollowingEventRequest(appData, listener, request.db, retrieveMode, true, getTransportStreamID(), getServiceID());
}


/*
Retrieve information associated with the scheduled events within the service for a requested period from the 
EIT-schedule. The events are presented in the order they are present in the EIT-schedule. The SIIterator that is 
returned with the event when the request completes successfully will contain one or more objects that implement the 
SIEvent interface. Parameters: retrieveMode - Mode of retrieval indicating whether the data should be retrieved only 
from the cache (FROM_CACHE_ONLY), from the cache if available and if not from the stream (FROM_CACHE_OR_STREAM), or 
always from the stream (FROM_STREAM_ONLY).appData - An object supplied by the application. This object will be delivered 
to the listener when the request completes. The application can use this objects for internal communication purposes. If 
the application does not need any application data, the parameter can be null. listener - SIRetrievalListener that will 
receive the event informing about the completion of the request. someDescriptorTags - A list of hints for descriptors 
(identi ed by their tags) the application is interested in. If the array contains -1 as its one and only element, the 
application is interested in all descriptors. If someDescriptorTags is null, the application is not interested in 
descriptors. All values that are out of the valid range for descriptor tags (i.e. 0...255) are ignored, except for the 
special meaning of -1 as the only element in the array. startTime - The beginning of the required period in UTC time. 
endTime - The end of the required period in UTC time. Returns: An SIRequest object Throws: SIIllegalArgumentException - 
thrown if the retrieveMode is invalid */
public SIRequest retrieveScheduledSIEvents(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] someDescriptorTags, java.util.Date startTime, java.util.Date endTime) {
   //tags can be ignored, will be fetched anyway
   //TODO: implement start/end time. Real solution is non-trivial, see ETSI TR 101 211, must be done in libdvbsi.
   return SIDatabaseRequest.ScheduledEventsRequest(appData, listener, request.db, retrieveMode, getTransportStreamID(), getServiceID());
}

/*
This method de nes extra semantics for the SIInformation.retrieveDescriptors method (second prototype). If the NIT 
sub-table on which this SINetwork object is based consists of multiple sections, then this method returns the requested 
descriptors in the order they appear when concatenating the descriptor loops of the different sections. Overrides: 
retrieveDescriptors(short, Object, SIRetrievalListener, short[]) in interface SIInformation Parameters: retrieveMode - 
Mode of retrieval indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache 
if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - 
An object supplied by the application. This object will be delivered to the listener when the request completes. The 
application can use this objects for internal communication purposes. If the application does not need any application 
data, the parameter can be null. listener - SIRetrievalListener that will receive the event informing about the 
completion of the request. */
public SIRequest retrieveDescriptors(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] someDescriptorTags) {
   return SIDatabaseRequest.DescriptorRequestService(this, someDescriptorTags, appData, listener, request.db, retrieveMode);
}

/* -------- javax.tv --------- */

/*
 
 Returns the time when this object was last updated from data in
 the broadcast. 
 Returns: The date of the last update in UTC format, or null 
 if unknown. 
 
 
*/

public java.util.Date getUpdateTime () {
   return request.getUpdateTime();
}

/*
 
 Reports the Locator of this SIElement . 
 
 
 
 Returns: Locator The locator referencing this
 SIElement 
 
 
 */

public javax.tv.locator.Locator  getLocator () {
   //The spec allows this to be non-standard.
   //We use an internal non-standard DvbLocator extension.
   return getDvbLocator();
}

/*
 Tests two SIElement objects for equality. Returns
 true if and only if:
 
  obj 's class is the
 same as the class of this SIElement , and 
  obj 's Locator is equal to
 the Locator of this object (as reported by
 SIElement.getLocator() , and 
  obj and this object encapsulate identical data.
  
 Overrides:  equals in class java.lang.Object 
 
 
 Parameters:  obj - The object against which to test for equality. Returns:  true if the two SIElement objects
 are equal; false otherwise.  
 */

public boolean equals (java.lang.Object obj) {
   if (!(obj instanceof SIServiceImpl))
      return false;
      
   SIServiceImpl other=(SIServiceImpl)obj;   
   return getLocator().equals(other.getLocator());
}


/*
 Reports the hash code value of this SIElement . Two
 SIElement objects that are equal will have identical
 hash codes. 
 Overrides:  hashCode in class java.lang.Object 
 
 
 Returns: The hash code value of this SIElement . 
 */

public int hashCode () {
   return getLocator().hashCode();
}


/*
 Reports the SI format in which this SIElement was
 delivered. 
 Returns: The SI format in which this SI element was delivered. 
*/

public javax.tv.service.ServiceInformationType  getServiceInformationType () {
   return javax.tv.service.ServiceInformationType.DVB_SI;
}

/*
 
 Retrieves a textual description of this service if available.
 This method delivers its results asynchronously. 
 
 
 
 Parameters:  requestor - The SIRequestor to be notified
 when this retrieval operation completes. Returns: An SIRequest object identifying this
 asynchronous retrieval request. See Also:   ServiceDescription  
 
 
 */

public javax.tv.service.SIRequest  retrieveServiceDescription ( javax.tv.service.SIRequestor requestor) {
   //according to the spec
   return javax.tv.service.SIManager.deliverRequest(requestor, javax.tv.service.SIRequestFailureType.DATA_UNAVAILABLE);
}


/*
 
 Returns the type of this service, for example, "digital
 television", "digital radio", "NVOD", etc. These values can be
 mapped to the ATSC service type in the VCT table and the DVB
 service type in the Service Descriptor. 
 
 
 
 Returns: Service type of this service. 
 
 
 */

public javax.tv.service.ServiceType  getServiceType () {
   short type = getSIServiceType();
   switch (type) {
   case 0x01: return javax.tv.service.ServiceType.DIGITAL_TV;
   case 0x02: return javax.tv.service.ServiceType.DIGITAL_RADIO;
   case 0x03: return javax.tv.service.ServiceType.DATA_BROADCAST;
   case 0x04: return javax.tv.service.ServiceType.NVOD_REFERENCE;
   case 0x05: return javax.tv.service.ServiceType.NVOD_TIME_SHIFTED;
   case 0x06: return javax.tv.service.ServiceType.DIGITAL_TV;
   case 0x07: return javax.tv.service.ServiceType.ANALOG_TV;
   case 0x08: return javax.tv.service.ServiceType.ANALOG_TV;
   case 0x09: return javax.tv.service.ServiceType.ANALOG_TV;
   case 0x0A: return javax.tv.service.ServiceType.ANALOG_RADIO;
   case 0x0B: return javax.tv.service.ServiceType.ANALOG_TV;
   case 0x0C: return javax.tv.service.ServiceType.DATA_BROADCAST;
   case 0x10: return javax.tv.service.ServiceType.DATA_APPLICATION;
   default:   return javax.tv.service.ServiceType.UNKNOWN;
   }
}


/*
 
 Retrieves an array of elementary components which are part of
 this service. The array will only contain
 ServiceComponent instances c for which
 the caller has
 javax.tv.service.ReadPermission(c.getLocator()) . If
 no ServiceComponent instances meet this criteria,
 this method will result in an SIRequestFailureType of
 DATA_UNAVAILABLE .*/

public javax.tv.service.SIRequest  retrieveComponents ( javax.tv.service.SIRequestor requestor) {
   //TODO:
   //Retrieve present event
   //retrieve component descriptors from this EIT
   //create ServiceComponents from these.
   return null;
}


/*
 
 Returns a schedule of program events associated with this service. 
 
 
 
 Returns: The program schedule for this service, or null 
 if no schedule is available. 
 
 
 */

public javax.tv.service.guide.ProgramSchedule  getProgramSchedule () {
   return new javax.tv.service.guide.DVBProgramSchedule(this);
}


/*
 
 Called to obtain a full service name. For example, this
 information may be delivered in the ATSC Extended Channel Name
 Descriptor, the DVB Service Descriptor or the DVB Multilingual
 Service Name Descriptor. 
 
 
 
 Returns: A string representing the full service name, or an empty
 string if the name is not available. 
 
 
 */

public java.lang.String getLongName () {
   return getName();
}


/*
 
 Returns the Service this ServiceDetails 
 object is associated with. 
 
 
 
 Returns: The Service to which this
 ServiceDetails belongs. 
 
 
 */

public javax.tv.service.Service  getService () {
   return javax.tv.service.VDRService.getService(request.getSourceVDRSource(), getOriginalNetworkID(), getTransportStreamID(), getServiceID());
}


/*
 
 Registers a ServiceComponentChangeListener to be
 notified of changes to a ServiceComponent that is
 part of this ServiceDetails . Subsequent notification
 is made via ServiceComponentChangeEvent with this
 ServiceDetails instance as the event source and an
 SIChangeType of ADD ,
 REMOVE or MODIFY . Only changes to
 ServiceComponent instances c for which
 the caller has
 javax.tv.service.ReadPermission(c.getLocator()) will
 be reported. 
 
 This method is only a request for notification. No guarantee is
 provided that the SI database will detect all, or even any, SI
 changes or whether such changes will be detected in a timely
 fashion. 
 
 If the specified ServiceComponentChangeListener is
 already registered, no action is performed. 
 
 
 
 Parameters:  listener - A ServiceComponentChangeListener to be
 notified about changes related to a ServiceComponent 
 in this ServiceDetails . See Also:   ServiceComponentChangeEvent , 
 ReadPermission  
 
 
 */

public void addServiceComponentChangeListener ( javax.tv.service.navigation.ServiceComponentChangeListener listener) {
   //TODO
}


/*
 
 Called to unregister an
 ServiceComponentChangeListener . If the specified
 ServiceComponentChangeListener is not registered, no
 action is performed. 
 
 
 
 Parameters:  listener - A previously registered listener. 
 
 
 */

public void removeServiceComponentChangeListener ( javax.tv.service.navigation.ServiceComponentChangeListener listener) {
   //TODO
}


/*
 
 Reports the type of mechanism by which this service was
 delivered. 
 
 
 
 Returns: The delivery system type of this service. 
 
 
*/

public javax.tv.service.navigation.DeliverySystemType  getDeliverySystemType () {
   //TODO
   return null;
}

/*
 
 Returns an array of CA System IDs associated with this object. This
 information may be obtained from the CAT MPEG message or a system
 specific conditional access descriptor (such as defined by Simulcrypt
 or ATSC). 
 Returns: An array of CA System IDs. An empty array is returned when no
 CA System IDs are available. 
 
 
 */
public int[] getCASystemIDs () {
   //TODO
   return new int[0];
}

/*
 
 Provides information concerning conditional access of this object. 
 Returns:  true if this Service is not protected by a
 conditional access; false if one or more components
 is protected by conditional access. 
 
 
*/
public boolean isFree () {
   //TODO
   return true;
}

}
