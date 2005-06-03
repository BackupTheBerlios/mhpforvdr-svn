package org.dvb.si;



public class SIEventImpl extends SICommonObject implements SIEvent,
   javax.tv.service.guide.ProgramEvent {
//nativeData is a pointer to an EIT::Event

SIEventImpl (SIDatabaseRequest request, long nativeData) {
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

public short[] getDescriptorTags() {
   return descriptorTags(nativeData);
}
private native short[] descriptorTags(long nativeData);


/*
This method returns the content nibbles related to the event. This information is extracted from the content_descriptor. 
If this descriptor is not present an empty array is returned (array with length 0). The return value is an array, each 
array element describes one content nibble. In each nibble the level 1 content nibbles occupy the four most signi cant 
bits of the returned bytes, level 2 content nibbles the four least signi cant bits. Returns: The content nibbles related 
to the event; level 1 content nibbles occupy the four most signi cant bits of the returned bytes, level 2 content 
nibbles the four least signi cant bits. */
public byte[] getContentNibbles() {
   return getContentNibbles(nativeData);
}
private native byte[] getContentNibbles(long nativeData);

/*
Get the duration of this event. Returns: The duration in milliseconds. */
public long getDuration() {
   return 1000*getDuration(nativeData);
}
private native long getDuration(long nativeData);


/*
Gets a DvbLocator that identi es this event. Returns: The DvbLocator of this 
event */
public org.davic.net.dvb.DvbLocator getDvbLocator() {
   try {
      return new org.davic.net.dvb.DvbLocator(getOriginalNetworkID(), getTransportStreamID(), getServiceID(), getEventID());
   } catch (javax.tv.locator.InvalidLocatorException e) {
      e.printStackTrace();
      return null;
   }
}


/*
Get the event identi cation. Returns: The event identi cation. */
public int getEventID() {
   return getEventID(nativeData);
}
private native int getEventID(long nativeData);


/*
Get the free_CA_mode value for this event, false indicates none of the component streams of this event are scrambled. 
Returns: The free_CA_mode value. */
public boolean getFreeCAMode() {
   return getFreeCAMode(nativeData);
}
private native boolean getFreeCAMode(long nativeData);


/*
This method returns the level 1 content nibbles of this event. This information is extracted from the 
content_descriptor. If this descriptor is not present an empty array is returned (array with length 0). The return value 
is an array, each array element describes one content nibble. In each nibble the data occupies the four least signi cant 
bits of the returned bytes with the four most signi cant bits set to 0. Returns: All level 1 content nibbles related to 
the event. */
public byte[] getLevel1ContentNibbles() {
   return getLevel1ContentNibbles(nativeData);
}
private native byte[] getLevel1ContentNibbles(long nativeData);


/*
This method returns the name of this event. The name is extracted from a short_event_descriptor. When this information 
is not available "" is returned. All control characters as de ned in ETR 211 are ignored. For each character the DVB-SI 
8 bit character code is mapped to the appropriate Unicode representation. Returns: The event name of this 
event. */
public java.lang.String getName() {
   return new String(getName(nativeData));
}
private native byte[] getName(long nativeData);


/*
Get the original network identi cation identi er. Returns: The original network identi 
cation. */
public int getOriginalNetworkID() {
   return getOriginalNetworkID(request.nativeData);
}
private native int getOriginalNetworkID(long nativeREQUESTData);


/*
Get the running status of this event. Returns: The running status (the possible values are de ned in the SIRunningStatus 
interface). */
public byte getRunningStatus() {
   return getRunningStatus(nativeData);
}
private native byte getRunningStatus(long nativeData);


/*
Get the service identi cation identi er. Returns: The service identi cation. */
public int getServiceID() {
   return getServiceID(request.nativeData);
}
private native int getServiceID(long nativeREQUESTData);


/*
This method returns the description of this event. The description is extracted from a short_event_descriptor. When this 
information is not available, "" is returned. For each character the DVB-SI 8 bit character code is mapped to the 
appropriate Unicode representation Returns: The short description of this event. */
public java.lang.String getShortDescription() {
   return new String(getShortDescription(nativeData));
}
private native byte[] getShortDescription(long nativeData);


/*
This method returns the short event name (ETR 211) of this event without emphasis marks. The name is extracted from a 
short_event_descriptor. When this information is not available "" is returned. For each character the DVB-SI 8 bit 
character code is mapped to the appropriate Unicode representation. Returns: The short event name of this 
event. */
public java.lang.String getShortEventName() {
   return new String(getShortEventName(nativeData));
}
private native byte[] getShortEventName(long nativeData);


/*
Get the start time of this event in UTC time. Returns: The start time of this 
event. */
public java.util.Date getStartTime() {
   return new java.util.Date(getStartTime(nativeData)*1000);
}
private native long getStartTime(long nativeData);


/*
Get the transport stream identi cation identi er. Returns: The transport stream identi 
cation. */
public int getTransportStreamID() {
   return getTransportStreamID(request.nativeData);
}
private native int getTransportStreamID(long nativeREQUESTData);


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
public SIRequest retrieveSIService(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] someDescriptorTags) {
   return SIDatabaseRequest.ServicesRequest(appData, listener, request.db, retrieveMode, getOriginalNetworkID(),
                                           getTransportStreamID(), getServiceID());
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
   return SIDatabaseRequest.DescriptorRequestEvent(this, someDescriptorTags, appData, listener, request.db, retrieveMode);
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

/*
 
 Returns the end time of this program event. The end time is in UTC time. 
 
 
 
 Returns: This program's end time (UTC). 
 
 
 */

public java.util.Date getEndTime () {
   return new java.util.Date(getStartTime().getTime()+getDuration());
}

/*
 
 Retrieves a textual description of the event. This method
 delivers its results asynchronously. 
 
 
 
 Parameters:  requestor - The SIRequestor to be notified
 when this retrieval operation completes. Returns: An SIRequest object identifying this
 asynchronous retrieval request. See Also:   ProgramEventDescription  
 
 
 */

public javax.tv.service.SIRequest retrieveDescription ( javax.tv.service.SIRequestor requestor) {
   //This object cannot implement ProgramEventDescription - spec does not allow that,
   //application will probably test with instanceof ProgramEvent and then ProgramEventDescription
   //(imagine what happens)
   return javax.tv.service.SIManager.deliverRequest
     (requestor, new javax.tv.service.guide.DVBProgramEventDescription(getShortDescription(), getUpdateTime()));
}


/*
 
 Reports content advisory information associated with this program for
 the local rating region. 
 
 
 
 Returns: A ContentRatingAdvisory object describing the
 rating of this ProgramEvent or null if
 no rating information is available. 
 
 
 */

public javax.tv.service.guide.ContentRatingAdvisory  getRating () {
   return null;
   //TODO:
   //return new javax.tv.service.DVBParentalRating(getRating(nativeData));
   //getRating(nativeData) shall return the rating from the parental
   //rating descriptor, 0 otherwise
}


/*
 
 Reports the Service this program event is associated with. 
 
 
 
 Returns: The Service this program event is delivered on. 
 
 
 */

public javax.tv.service.Service  getService () {
   return javax.tv.service.VDRService.getService(request.getSourceVDRSource(), getOriginalNetworkID(), getTransportStreamID(), getServiceID());
}

/*
 
 Retrieves an array of service components which are part of this
 ProgramEvent . Service component information may not
 always be available. If the ProgramEvent is current,
 this method will provide only service components associated with
 the Service to which the ProgramEvent 
 belongs. If the ProgramEvent is not current, no
 guarantee is provided that all or even any of its service
 components will be available. */

public javax.tv.service.SIRequest  retrieveComponents ( javax.tv.service.SIRequestor requestor) {
   //TODO:
   //retrieve component descriptors
   //create ServiceComponents of these
   return javax.tv.service.SIManager.deliverRequest(requestor, javax.tv.service.SIRequestFailureType.DATA_UNAVAILABLE);
}


}