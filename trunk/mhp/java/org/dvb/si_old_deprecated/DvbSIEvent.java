
package org.dvb.si;

/*This interface represents a particular event within a service. Each object that implements 
the SIEvent interface is de ned by the combination of the identi ers original_network_id, 
transport_stream_id, service_id, event_id. */

public class DvbSIEvent extends TrivialSIInformation implements SIEvent {


org.davic.net.dvb.DvbLocator locator;
long duration;
int eventId;
boolean freeCaMode;
String name;
String extendedDescription;
java.util.Date startTime;

DvbSIEvent(SIDatabase db, org.davic.net.dvb.DvbLocator locator, int nativeData) {
   super(db);
   //as long as VDR doesnt support TID/NID, locator cannot 
   // be constructed from the native object.
   this.locator=locator;
   
   //!!!It is not possible to store a pointer to nativeData
   //because the implementation in VDR requires a global Mutex
   //as long as the pointer is accessed!!!
   
   //this.nativeData=nativeData;
   LoadInfo(nativeData);
}

void LoadInfo(int nativeData) {
   duration=duration(nativeData);
   eventId=eventId(nativeData);
   freeCaMode=channelCa(nativeData)!=0;
   name=new String(name(nativeData));
   extendedDescription=new String(extendedDescription(nativeData));
   startTime=new java.util.Date(startTime(nativeData));
}

/*
This method returns the content nibbles related to the event. This information is extracted from the content_descriptor. 
If this descriptor is not present an empty array is returned (array with length 0). The return value is an array, each 
array element describes one content nibble. In each nibble the level 1 content nibbles occupy the four most signi cant 
bits of the returned bytes, level 2 content nibbles the four least signi cant bits. Returns: The content nibbles related 
to the event; level 1 content nibbles occupy the four most signi cant bits of the returned bytes, level 2 content 
nibbles the four least signi cant bits. */
public byte[] getContentNibbles() {
   return new byte[0];
}


/*
Get the duration of this event. Returns: The duration in milliseconds. */
public long getDuration() {
   return duration;
}

private native long duration(int nativeData);


/*
Gets a DvbLocator that identi es this event. Returns: The DvbLocator of this 
event */
public org.davic.net.dvb.DvbLocator getDvbLocator() {
   return locator;
}


/*
Get the event identi cation. Returns: The event identi cation. */
public int getEventID() {
   return eventId;
}


private native int eventId(int nativeData);


/*
Get the free_CA_mode value for this event, false indicates none of the component streams of this event are scrambled. 
Returns: The free_CA_mode value. */
public boolean getFreeCAMode() {
   return freeCaMode;
}
   //getchannelid => channel => Ca
private native int channelCa(int nativeData);


/*
This method returns the level 1 content nibbles of this event. This information is extracted from the 
content_descriptor. If this descriptor is not present an empty array is returned (array with length 0). The return value 
is an array, each array element describes one content nibble. In each nibble the data occupies the four least signi cant 
bits of the returned bytes with the four most signi cant bits set to 0. Returns: All level 1 content nibbles related to 
the event. */
public byte[] getLevel1ContentNibbles() {
   return new byte[0];
}


/*
This method returns the name of this event. The name is extracted from a short_event_descriptor. When this information 
is not available "" is returned. All control characters as de ned in ETR 211 are ignored. For each character the DVB-SI 
8 bit character code is mapped to the appropriate Unicode representation. Returns: The event name of this 
event. */
public java.lang.String getName() {
   return name;
}

private native byte[] name(int nativeData);


/*
Get the original network identi cation identi er. Returns: The original network identi 
cation. */
public int getOriginalNetworkID() {
   return locator.getOriginalNetworkId();
}


/*
Get the running status of this event. Returns: The running status (the possible values are de ned in the SIRunningStatus 
interface). */
public byte getRunningStatus() {
   return SIRunningStatus.UNDEFINED;
}


/*
Get the service identi cation identi er. Returns: The service identi cation. */
public int getServiceID() {
   return locator.getServiceId();
}


/*
This method returns the description of this event. The description is extracted from a short_event_descriptor. When this 
information is not available, "" is returned. For each character the DVB-SI 8 bit character code is mapped to the 
appropriate Unicode representation Returns: The short description of this event. */
public java.lang.String getShortDescription() {
   return extendedDescription;
}

private native byte[] extendedDescription(int nativeData);


/*
This method returns the short event name (ETR 211) of this event without emphasis marks. The name is extracted from a 
short_event_descriptor. When this information is not available "" is returned. For each character the DVB-SI 8 bit 
character code is mapped to the appropriate Unicode representation. Returns: The short event name of this 
event. */
public java.lang.String getShortEventName() {
   //the short_event_descriptor only contains one name, not a short and a longer one.
   return getName();
}


/*
Get the start time of this event in UTC time. Returns: The start time of this 
event. */
public java.util.Date getStartTime() {
   return startTime;
}

private native long startTime(int nativeData);


/*
Get the transport stream identi cation identi er. Returns: The transport stream identi 
cation. */
public int getTransportStreamID() {
   return locator.getTransportStreamId();
}


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
public SIRequest retrieveSIService(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] 
someDescriptorTags) {
   return new SIRequestDummy(appData, listener, db);
}



}
