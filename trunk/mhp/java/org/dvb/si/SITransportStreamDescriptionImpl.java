package org.dvb.si;


public class SITransportStreamDescriptionImpl extends SICommonObject {

//nativeData is a pointer to a std::list<TSDT>

SITransportStreamDescriptionImpl (SIDatabaseRequest request, long nativeData) {
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
   return true;
}

public short[] getDescriptorTags() {
   return descriptorTags(nativeData);
}
private native short[] descriptorTags(long nativeData);


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
   return SIDatabaseRequest.DescriptorRequestTransportStreamDescription(this, someDescriptorTags, appData, listener, request.db, retrieveMode);
}



}
