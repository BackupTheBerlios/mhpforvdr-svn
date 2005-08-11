
package org.dvb.si;

/*Object instances of this class represent SI retrieval requests made by the application. 
The application may cancel the request using this object. */

public class SIRequestService extends SIRequest {

public SIRequestService(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db, short retrieveMode,
                  org.davic.net.dvb.DvbLocator dvbLocator, short[] someDescriptorTag) 
{
   super(appData, listener, db);
   this.retrieveMode=retrieveMode;
   this.dvbLocator=dvbLocator;
   this.someDescriptorTag=someDescriptorTag;
}

short retrieveMode;
org.davic.net.dvb.DvbLocator dvbLocator;
short[] someDescriptorTag;

void Execute() {
   SIRetrievalEvent e;
   int c=channel(db.source, dvbLocator.getServiceId());
   if (c==0)
      e=new SIObjectNotInTableEvent(listener, this);
   else {
      //change this when VDR supports NID/TID
      DvbSIService service=new DvbSIService(db, dvbLocator.getOriginalNetworkId(), dvbLocator.getTransportStreamId(),
                  dvbLocator.getServiceId(), new String(name(c)), ca(c)==0);
      service.setUpdateTimeToNow();
      e=new SISuccessfulRetrieveEvent(appData, this, SIDatabase.getSIIteratorForOneElement(service));
   }
   listener.postRetrievalEvent(e);
}

private native int channel(int source, int sid);
private native int ca(int nativeData);
private native byte[] name(int nativeData);


boolean CanExecute() {
   return true;
}



/*
Cancels the retrieval request. Returns: true if the request was cancelled and an SIRequestCancelledEvent will be 
delivered to the listener, false if the request has already completed (either successfully, with an error or due to a 
prior cancel method call) */
public boolean cancelRequest() {
   return false;
}

/*
Returns whether the information will be returned from cache or from the stream Returns: true if the information will be 
returned from cache, false if the information will be retrieved from the stream */
public boolean isAvailableInCache() {
   return true;
}


}
