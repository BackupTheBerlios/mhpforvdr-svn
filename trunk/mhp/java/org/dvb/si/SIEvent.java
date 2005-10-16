
package org.dvb.si;

/*This interface represents a particular event within a service. Each object that implements 
the SIEvent interface is de ned by the combination of the identi ers original_network_id, 
transport_stream_id, service_id, event_id. */

public interface SIEvent extends SIInformation {

/*
This method returns the content nibbles related to the event. This information is extracted from the content_descriptor. 
If this descriptor is not present an empty array is returned (array with length 0). The return value is an array, each 
array element describes one content nibble. In each nibble the level 1 content nibbles occupy the four most signi cant 
bits of the returned bytes, level 2 content nibbles the four least signi cant bits. Returns: The content nibbles related 
to the event; level 1 content nibbles occupy the four most signi cant bits of the returned bytes, level 2 content 
nibbles the four least signi cant bits. */
public byte[] getContentNibbles();


/*
Get the duration of this event. Returns: The duration in milliseconds. */
public long getDuration();


/*
Gets a DvbLocator that identi es this event. Returns: The DvbLocator of this 
event */
public org.davic.net.dvb.DvbLocator getDvbLocator();


/*
Get the event identi cation. Returns: The event identi cation. */
public int getEventID();


/*
Get the free_CA_mode value for this event, false indicates none of the component streams of this event are scrambled. 
Returns: The free_CA_mode value. */
public boolean getFreeCAMode();


/*
This method returns the level 1 content nibbles of this event. This information is extracted from the 
content_descriptor. If this descriptor is not present an empty array is returned (array with length 0). The return value 
is an array, each array element describes one content nibble. In each nibble the data occupies the four least signi cant 
bits of the returned bytes with the four most signi cant bits set to 0. Returns: All level 1 content nibbles related to 
the event. */
public byte[] getLevel1ContentNibbles();


/*
This method returns the name of this event. The name is extracted from a short_event_descriptor. When this information 
is not available "" is returned. All control characters as de ned in ETR 211 are ignored. For each character the DVB-SI 
8 bit character code is mapped to the appropriate Unicode representation. Returns: The event name of this 
event. */
public java.lang.String getName();


/*
Get the original network identi cation identi er. Returns: The original network identi 
cation. */
public int getOriginalNetworkID();


/*
Get the running status of this event. Returns: The running status (the possible values are de ned in the SIRunningStatus 
interface). */
public byte getRunningStatus();


/*
Get the service identi cation identi er. Returns: The service identi cation. */
public int getServiceID();


/*
This method returns the description of this event. The description is extracted from a short_event_descriptor. When this 
information is not available, "" is returned. For each character the DVB-SI 8 bit character code is mapped to the 
appropriate Unicode representation Returns: The short description of this event. */
public java.lang.String getShortDescription();


/*
This method returns the short event name (ETR 211) of this event without emphasis marks. The name is extracted from a 
short_event_descriptor. When this information is not available "" is returned. For each character the DVB-SI 8 bit 
character code is mapped to the appropriate Unicode representation. Returns: The short event name of this 
event. */
public java.lang.String getShortEventName();


/*
Get the start time of this event in UTC time. Returns: The start time of this 
event. */
public java.util.Date getStartTime();


/*
Get the transport stream identi cation identi er. Returns: The transport stream identi 
cation. */
public int getTransportStreamID();


/*
This method retrieves the SIService object representing the service the event, represented by this SIEvent, is part 
of.The SIIterator that is returned with the event when the request completes successfully will contain an object that 
implements the SIService interface. If no matching object was found,the appropriate one of the following events is sent: 
ObjectNotInCacheEvent ObjectNotInTableEvent or TableNotFoundEvent. Parameters: retrieveMode - Mode of retrieval 
indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if available and 
if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - An object 
supplied by the application. This object will be delivered to the listener when the request completes. The application 
can use this objects for internal communication purposes. If the application does not need any application data, the 
parameter can be null. listener - SIRetrievalListener that will receive the event informing about the completion of the 
request. someDescriptorTags - A list of hints for descriptors (identi ed by their tags) the application is interested 
in. If the array contains -1 as its one and only element, the application is interested in all descriptors. If 
someDescriptorTags is null, the application is not interested in descriptors. All values that are out of the valid range 
for descriptor tags (i.e. 0...255) are ignored, except for the special meaning of -1 as the only element in the 
array.Returns: An SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is 
invalid */
public SIRequest retrieveSIService(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] someDescriptorTags) throws SIIllegalArgumentException;



}
