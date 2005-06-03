package org.dvb.si;


public class PMTServiceImpl extends SICommonObject implements PMTService {

//nativeData is a pointer to a PMT

PMTServiceImpl (SIDatabaseRequest request, long nativeData) {
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
Gets a DvbLocator that identi es this service Returns: The DvbLocator of this 
service */
public org.davic.net.dvb.DvbLocator getDvbLocator() {
   try {
      return new org.davic.net.dvb.DvbLocator(getOriginalNetworkID(), getTransportStreamID(), getServiceID());
   } catch (javax.tv.locator.InvalidLocatorException e) {
      e.printStackTrace();
      return null;
   }
}

public short[] getDescriptorTags() {
   return descriptorTags(nativeData);
}
private native short[] descriptorTags(long nativeData);


/*
Get the original network identi cation. Returns: The original network identi cation identi 
er. */
public int getOriginalNetworkID() {
   return getSourceNid();
}


/*
Get the PCR pid. Returns: The PCR pid. */
public int getPcrPid() {
   return getPcrPid(nativeData);
}
private native int getPcrPid(long nativeData);


/*
Get the service identi cation. Returns: The service identi cation identi er. */
public int getServiceID() {
   return getServiceID(nativeData);
}
private native int getServiceID(long nativeData);


/*
Get the transport stream identi cation. Returns: The transport stream identi cation identi 
er. */
public int getTransportStreamID() {
   return getSourceTid();
}


/*
Retrieve information associated with the elementary streams which compose this service from the Program Map Table (PMT). 
The SIIterator that is returned with the event when the request completes successfully will contain one or more objects 
that implement the PMTElementaryStream interface. If no matching object was found,the appropriate one of the following 
events is sent: ObjectNotInCacheEvent ObjectNotInTableEvent or TableNotFoundEvent. Parameters: retrieveMode - Mode of 
retrieval indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if 
available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - An 
object supplied by the application. This object will be delivered to the listener when the request completes. The 
application can use this objects for internal communication purposes. If the application does not need any application 
data, the parameter can be null.listener - SIRetrievalListener that will receive the event informing about the 
completion of the request. somePMTDescriptorTags - A list of hints for descriptors (identi ed by their tags) the 
application is interested in. If the array contains -1 as its one and only element, the application is interested in all 
descriptors. If somePMTDescriptorTags is null, the application is not interested in descriptors. All non applicable tag 
values are ignored. Returns: An SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is 
invalid See Also: SIRequest, SIRetrievalListener, PMTElementaryStream */
public SIRequest retrievePMTElementaryStreams(short retrieveMode, java.lang.Object appData, SIRetrievalListener 
listener, short[] somePMTDescriptorTags) {
   int[] allComps=new int[1];
   allComps[0]=-1;
   return SIDatabaseRequest.PMTElementaryStreamsRequest(appData, listener, request.db, retrieveMode, getServiceID(), allComps);   
}


/*
This method de nes extra semantics for the SIInformation.retrieveDescriptors method (second prototype). If the NIT 
sub-table on which this SIBouquet object is based consists of multiple sections, then this method returns the requested 
descriptors in the order they appear when concatenating the descriptor loops of the different sections. Overrides: 
retrieveDescriptors(short, Object, SIRetrievalListener, short[]) in interface SIInformation Parameters: retrieveMode - 
Mode of retrieval indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache 
if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - 
An object supplied by the application. This object will be delivered to the listener when the request completes. The 
application can use this objects for internal communication purposes. If the application does not need any application 
data, the parameter can be null. listener - SIRetrievalListener that will receive the event informing about the 
completion of the request. */
public SIRequest retrieveDescriptors(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] someDescriptorTags) {
   return SIDatabaseRequest.DescriptorRequestPMTService(this, someDescriptorTags, appData, listener, request.db, retrieveMode);
}





}