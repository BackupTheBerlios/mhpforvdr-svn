package org.dvb.si;



public class SITransportStreamImpl extends SICommonObject 
         implements SITransportStreamNIT, SITransportStreamBAT, javax.tv.service.transport.TransportStream {

//nativeData is a pointer to a std::list<NIT::TransportStream>

SITransportStreamImpl (SIDatabaseRequest request, long nativeData) {
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
Get the identi cation of the network this transport stream is part of. Returns: The network identi cation identi 
er. */
public int getNetworkID() {
   return getNetworkID(request.nativeData);
}
private native int getNetworkID(long nativeREQUESTData);

/*
Get the identi cation of the bouquet this transport stream is part of. Returns: The bouquet identi cation identi 
er. */
public int getBouquetID() {
   return getNetworkID();
}

/*
Gets a DvbLocator that identi es this transport stream. Returns: The DvbLocator of this transport 
stream. */
public org.davic.net.dvb.DvbLocator getDvbLocator() {
   try {
      return new org.davic.net.dvb.DvbLocator(getOriginalNetworkID(), getTransportStreamID());
   } catch (javax.tv.locator.InvalidLocatorException e) {
      e.printStackTrace();
      return null;
   }
}


/*
Get the original network identi cation. Returns: The original network identi cation identi 
er. */
public int getOriginalNetworkID() {
   return getOriginalNetworkID(nativeData);
}
private native int getOriginalNetworkID(long nativeData);


/*
Get the transport stream identi cation. Returns: The transport stream identi cation identi 
er. */
public int getTransportStreamID() {
   return getTransportStreamID(nativeData);
}
private native int getTransportStreamID(long nativeData);


/*
Retrieve information associated with services carried via the transport stream. This method works in the same way for 
objects that implement the SITransportStreamNIT and SITransportStreamBAT interfaces. The SIIterator that is returned 
with the event when the request completes successfully will contain objects that implement the SIService interface. 
Parameters: retrieveMode - Mode of retrieval indicating whether the data should be retrieved only from the cache 
(FROM_CACHE_ONLY), from the cache if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the 
stream (FROM_STREAM_ONLY). appData - An object supplied by the application. This object will be delivered to the 
listener when the request completes. The application can use this objects for internal communication purposes. If the 
application does not need any application data, the parameter can be null.listener - SIRetrievalListener that will 
receive the event informing about the completion of the request. someDescriptorTags - A list of hints for descriptors 
(identi ed by their tags) the application is interested in. If the array contains -1 as its one and only element, the 
application is interested in all descriptors. If someDescriptorTags is null, the application is not interested in 
descriptors. All values that are out of the valid range for descriptor tags (i.e. 0...255) are ignored, except for the 
special meaning of -1 as the only element in the array. Returns: An SIRequest object Throws: SIIllegalArgumentException 
- thrown if the retrieveMode is invalid */
public SIRequest retrieveSIServices(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] 
someDescriptorTags) {
   return SIDatabaseRequest.ServicesRequest(appData, listener, request.db, retrieveMode, getOriginalNetworkID(), getTransportStreamID(), -1);
}



public SIRequest retrieveDescriptors(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] someDescriptorTags) {
   return SIDatabaseRequest.DescriptorRequestTransportStream(this, someDescriptorTags, appData, listener, request.db, retrieveMode);
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
   if (!(obj instanceof SITransportStreamImpl))
      return false;
      
   SITransportStreamImpl other=(SITransportStreamImpl)obj;   
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
 
 Reports the textual name or description of this transport stream. 
 
 
 
 Returns: A string representing the name of this transport stream, or
 an empty string if no information is available. 
 
*/

//According to the spec, return an empty string.
public java.lang.String getDescription () {
   return "";
}


}