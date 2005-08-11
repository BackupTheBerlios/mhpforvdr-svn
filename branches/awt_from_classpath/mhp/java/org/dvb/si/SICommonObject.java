
package org.dvb.si;

abstract class SICommonObject implements SIInformation {

SIDatabaseRequest request;
//a subclass specific pointer
long nativeData = 0;

SICommonObject(SIDatabaseRequest request, long nativeData) {
   this.request=request;
   this.nativeData=nativeData;
}

protected void finalize() throws java.lang.Throwable {
   super.finalize();
   cleanUp(nativeData);
}

protected void cleanUp(long nativeData) {
   //this default implementation deletes nativeData
   //as a SI::Object, which works for SITimeImpl, SITransportStreamImpl,
   //PMTServiceImpl, PMTElementaryStreamImpl, SIEventImpl, SIServiceImpl
   cleanUpSiObject(nativeData);
}
private native void cleanUpSiObject(long nativeData);

/*
Return the time when the information contained in the object that implements this interface was last updated. Returns: 
The date of the last update. */
public java.util.Date getUpdateTime() {
   return request.getUpdateTime();
}


/*
Return the root of the hierarchy the object that implements this interface belongs to. Returns: The root of the 
hierarchy. */
public SIDatabase getSIDatabase() {
   return request.db;
}


/*
Return the org.davic.mpeg.TransportStream object the information contained in the object that implements that interface 
was  ltered from. Returns: The org.davic.mpeg.TransportStream object the information was  ltered from. See Also: 
org.davic.mpeg.TransportStream */
public org.davic.mpeg.TransportStream getDataSource() {
   return request.getDataSource();
}

public int getSourceTid() {
   return request.getSourceTid();
}

public int getSourceNid() {
   return request.getSourceNid();
}

/*
This method de nes extra semantics for the SIInformation.retrieveDescriptors method ( rst prototype). If the NIT 
sub-table on which this SINetwork object is based consists of multiple sections, then this method returns the requested 
descriptors in the order they appear when concatenating the descriptor loops of the different sections. Overrides: 
retrieveDescriptors(short, Object, SIRetrievalListener) in interface SIInformation Parameters: retrieveMode - Mode of 
retrieval indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if 
available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY).appData - An 
object supplied by the application. This object will be delivered to the listener when the request completes. The 
application can use this objects for internal communication purposes. If the application does not need any application 
data, the parameter can be null. listener - SIRetrievalListener that will receive the event informing about the 
completion of the request. Returns: An SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode 
is invalid */
public SIRequest retrieveDescriptors(short retrieveMode, java.lang.Object appData, SIRetrievalListener 
listener) {
   return retrieveDescriptors(retrieveMode, appData, listener, null);
}




}