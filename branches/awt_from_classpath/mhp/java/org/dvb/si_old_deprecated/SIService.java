
package org.dvb.si;

/*This interface represents a particular service carried by a transport stream. Information 
that can be obtained through the methods of this interface is retrieved from the SDT 
table. Each object that implements the SIService interface is identi ed by the combination 
of the following identi ers: original_network_id, transport_stream_id, 
service_id. */

public interface SIService extends SIInformation, TextualServiceIdentifierQuery {

/*
Gets a DvbLocator that identi es this service. Returns: The DvbLocator of this 
service */
public org.davic.net.dvb.DvbLocator getDvbLocator();


/*
Get the EIT_present_following_ ag value, true indicates this service has present and/or following event information. 
Returns: The EIT_present_following_ ag value. */
public boolean getEITPresentFollowingFlag();


/*
Get the EIT_schedule_ ag value, true indicates this services has scheduled event information. Returns: The EIT_schedule_ 
ag value. */
public boolean getEITScheduleFlag();


/*
Retrieve the free_CA_mode value of this service, false indicates none of the components of this service are scrambled. 
Returns:The free_CA_mode value of this service. */
public boolean getFreeCAMode();


/*
This method returns the name of the service represented by this service. The name is extracted from the 
service_descriptor or optionally from the multilingual_service_name_descriptor. If this descriptor is not present "" is 
returned. All control characters as de ned in ETR 211 are ignored. For each character the DVB-SI 8 bit character code is 
mapped to the appropriate Unicode representation. Returns: The name of this 
service. */
public java.lang.String getName();


/*
Get the original network identi cation. Returns: The original network identi cation identi 
er. */
public int getOriginalNetworkID();


/*
This method returns the service provider name of this service The service provider name is extracted from the 
service_descriptor or optionally from the multilingual_service_name_descriptor. If this descriptor is not present "" is 
returned. All control characters as de ned in ETR 211 are ignored. For each character the DVB-SI 8 bit character code is 
mapped to the appropriate Unicode representation. Returns: The service provider name of this 
service. */
public java.lang.String getProviderName();


/*
Retrieve the running status of this service. Returns: The running status (the possible values are de ned in the 
SIRunningStatus interface) */
public byte getRunningStatus();


/*
Get the service identi cation. Returns: The service identi cation identi er. */
public int getServiceID();


/*
This method returns the short name (ETR 211) of the service provider of this service without emphasis marks. The name is 
extracted from the service_descriptor or optionally from the multilingual_service_name_descriptor. When this information 
is not available "" is returned. For each character the DVB-SI 8 bit character code is mapped to the appropriate Unicode 
representation. Returns: The short service provider name of this service. */
public java.lang.String getShortProviderName();


/*
This method returns the short name (ETR 211) of this service without emphasis marks. The name is extracted from the 
service_descriptor or optionally from the multilingual_service_name_descriptor. When this information is not available 
"" is returned. For each character the DVB-SI 8 bit character code is mapped to the appropriate Unicode representation. 
Returns: The short name of this service. */
public java.lang.String getShortServiceName();


/*
Get the service type. The service type is extracted from the service_descriptor. Returns: The service type. (Some of the 
possible values are de ned in the SIServiceType interface.) */
public short getSIServiceType();


/*
Returns the textual service identi ers related to this object. Overrides: getTextualServiceIdentifiers() in interface 
TextualServiceIdentifierQuery Returns: an array of String objects containing the textual service identi ers or null if 
none are present. */
public java.lang.String[] getTextualServiceIdentifiers();


/*
Get the transport stream identi cation. Returns: The transport stream identi cation identi 
er. */
public int getTransportStreamID();


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
public SIRequest retrieveFollowingSIEvent(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, 
short[] someDescriptorTags);


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
someDescriptorTags);


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
appData, SIRetrievalListener listener, short[] someDescriptorTags);


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
public SIRequest retrieveScheduledSIEvents(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, 
short[] someDescriptorTags, java.util.Date startTime, java.util.Date endTime);



}
