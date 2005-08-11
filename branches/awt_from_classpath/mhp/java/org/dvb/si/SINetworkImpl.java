package org.dvb.si;


/*This interface (together with the SITransportStreamNIT interface) represents a sub-table 
of the Network Information Table (NIT) describing a particular network. Each object that 
implements the SINetwork interface is identi ed by the identi er 
network_id. */

public class SINetworkImpl extends SICommonObject implements SINetwork, javax.tv.service.transport.Network {

//nativeData is a pointer to a std::list<NIT>

SINetworkImpl (SIDatabaseRequest request, long nativeData) {
   super(request, nativeData);
}

protected void cleanUp(long nativeData) {
   cleanUpStdList(nativeData);
}
private native void cleanUpStdList(long nativeData);

/*
Return true when the information contained in the object that implements this interface was  ltered from an 'actual' 
table or from a table with no 'actual/other' distinction. Returns: true if the information comes from an 'actual' table 
or from a table with no 'actual/other' distiction, otherwise returns false */
public boolean fromActual() {
   //TODO
   return true;
}

/*public org.davic.mpeg.TransportStream getDataSource() {
   return super.getDataSource();
}
*/

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
This method returns the name of this network. The name is extracted from the network_name_descriptor or optionally from 
the multilingual_network_name_descriptor. When this information is not available "" is returned. All control characters 
as de ned in ETR 211 are ignored. For each character the DVB-SI 8 bit character code is mapped to the appropriate 
Unicode representation. Returns: The network name of this network. */
public java.lang.String getName() {
   return new String(name(nativeData));
}
private native byte[] name(long nativeData);


/*
Get the identi cation of this network. Returns: The network identi cation identi 
er. */
public int getNetworkID() {
   return networkId(nativeData);
}
private native int networkId(long nativeData);


/*
This method returns the short name (ETR 211) of this network without emphasis marks. The name is extracted from the 
network_name_descriptor or optionally from the multilingual_network_name_descriptor. When this information is not 
available "" is returned. For each character the DVB-SI 8 bit character code is mapped to the appropriate Unicode 
representation. Returns: The short network name of this network. */
public java.lang.String getShortNetworkName() {
   return new String(shortNetworkName(nativeData));
}
private native byte[] shortNetworkName(long nativeData);


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
   return SIDatabaseRequest.DescriptorRequestNetwork(this, someDescriptorTags, appData, listener, request.db, retrieveMode);
}

/*Retrieve information associated with transport streams carried via the network. The SIIterator that is returned with the event when the request completes successfully will contain one or more objects that implement the SITransportStreamNIT interface. Parameters: retrieveMode - Mode of retrieval indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - An object supplied by the application. This object will be delivered to the listener when the request completes. The application can use this objects for internal communication purposes. If the application does not need any application data, the parameter can be null. listener - SIRetrievalListener that will receive the event informing about the completion of the request. someDescriptorTags - A list of hints for descriptors (identi ed by their tags) the application is interested in. If the array contains -1 as its one and only element, the application is interested in all descriptors. If someDescriptorTags is null, the application is not interested in descriptors. All 546 ETSI TS 102 812 V1.1.1 (2001-11) values that are out of the valid range for descriptor tags (i.e. 0...255) are ignored, except for the special meaning of -1 as the only element in the array. Returns: An SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is invalid 
See Also: SIRequest, SIRetrievalListener, SITransportStreamNIT, DescriptorTag*/
public SIRequest retrieveSITransportStreams(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, 
short[] someDescriptorTags) {
   return SIDatabaseRequest.TransportStreamRequest(this, appData, listener, request.db, retrieveMode);
}



/* ----- javax.tv interface ------ */

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
   try {
      return new org.davic.net.dvb.DvbLocator(getNetworkID(), true);
   } catch (javax.tv.locator.InvalidLocatorException e) {
      e.printStackTrace();
      return null;
   }
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
   if (!(obj instanceof SINetworkImpl))
      return false;
      
   SINetworkImpl other=(SINetworkImpl)obj;
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
 
 Retrieves an array of TransportStream objects
 representing the transport streams carried in this
 Network . Only TransportStream instances
 ts for which the caller has
 javax.tv.service.ReadPermission(ts.getLocator()) 
 will be present in the array. If no TransportStream 
 instances meet this criteria or if this Network does
 not aggregate transport streams, the result is an
 SIRequestFailureType of
 DATA_UNAVAILABLE . 
 
 This method delivers its results asynchronously. 
 
 
 
 Parameters:  requestor - The SIRequestor to be notified
 when this retrieval operation completes. Returns: An SIRequest object identifying this
 asynchronous retrieval request. See Also:   TransportStream , 
 ReadPermission  
 
 
*/

public javax.tv.service.SIRequest  retrieveTransportStreams ( javax.tv.service.SIRequestor requestor) {
   javax.tv.service.OrgDvbSiRequestAdapter req=new javax.tv.service.OrgDvbSiRequestAdapter(requestor);
   req.setRequest(retrieveSITransportStreams(FROM_CACHE_OR_STREAM, null, req, null));
   return req;
}


}
