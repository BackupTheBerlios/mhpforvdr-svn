package org.dvb.si;


public class PMTElementaryStreamImpl extends SICommonObject implements PMTElementaryStream {

//nativeData is a pointer to a PMT::Stream

PMTElementaryStreamImpl (SIDatabaseRequest request, long nativeData) {
   super(request, nativeData);
}

/*
Return true when the information contained in the object that implements this interface was  ltered from an 'actual' 
table or from a table with no 'actual/other' distinction. Returns: true if the information comes from an 'actual' table 
or from a table with no 'actual/other' distiction, otherwise returns false */
public boolean fromActual() {
   return true;
}


/*
Get the component tag identi er. Returns: The component tag. If the elementary stream does not have an associated 
component tag, this method returns -2. */
public int getComponentTag() {
   return getComponentTag(nativeData);
}
private native int getComponentTag(long nativeData);

/*
Gets a DvbLocator that identi es this elementary stream Returns: The DvbLocator of this elementary 
stream */
public org.davic.net.dvb.DvbLocator getDvbLocator() {
   try {
      return new org.davic.net.dvb.DvbLocator(getOriginalNetworkID(), getTransportStreamID(), getServiceID(), getComponentTag());
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
Get the elementary PID. Returns: The PID the data of elementary stream is sent on in the transport 
stream. */
public short getElementaryPID() {
   return getElementaryPID(nativeData);
}
private native short getElementaryPID(long nativeData);

/*
Get the original network identi cation identi er. Returns: The original network identi 
cation. */
public int getOriginalNetworkID() {
   return getSourceNid();
}


/*
Get the service identi cation identi er. Returns: The service identi cation. */
public int getServiceID() {
   return getServiceID(request.nativeData);
}
private native int getServiceID(long nativREQUESTeData);


/*
Get the stream type of this elemetary stream. Returns: The stream type (some of the possible values are de ned in the 
PMTStreamType interface). See Also: PMTStreamType */
public byte getStreamType() {
   return getStreamType(nativeData);
}
private native byte getStreamType(long nativeData);


/*
Get the transport stream identi cation identi er. Returns: The transport stream identi 
cation. */
public int getTransportStreamID() {
   return getTransportStreamID(request.nativeData);
}
private native int getTransportStreamID(long nativeREQUESTData);


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
   return SIDatabaseRequest.DescriptorRequestPMTElementaryStream(this, someDescriptorTags, appData, listener, request.db, retrieveMode);
}






}