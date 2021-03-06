package org.dvb.si;

import vdr.mhp.lang.NativeData;

public class SITimeImpl extends SICommonObject implements SITime {

//nativeData is a pointer to a SI::TDT or SI::TOT

SITimeImpl (SIDatabaseRequest request, NativeData nativeData) {
   super(request, nativeData);
}

/*
Get the UTC time as coded in the TDT or TOT table. Returns: The UTC as coded in the TDT or TOT 
table. */
public java.util.Date getUTCTime() {
   return new java.util.Date(getUTCTime(nativeData)*1000);
}
private native int getUTCTime(NativeData nativeData);

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
private native short[] descriptorTags(NativeData nativeData);

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
public SIRequest retrieveDescriptors(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] someDescriptorTags) throws SIIllegalArgumentException {
   SIDatabase.checkRetrieveMode(retrieveMode);
   return SIDatabaseRequest.DescriptorRequestTime(this, someDescriptorTags, appData, listener, request.db, retrieveMode);
}



}
