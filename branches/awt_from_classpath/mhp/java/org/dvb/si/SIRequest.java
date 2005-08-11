
package org.dvb.si;

/*Object instances of this class represent SI retrieval requests made by the application. 
The application may cancel the request using this object. */

public abstract class SIRequest {

SIRetrievalListener listener;
java.lang.Object appData;
SIDatabase db;


SIRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db) {
   this.appData=appData;
   this.listener=listener;
   this.db=db;
}


/*
Cancels the retrieval request. Returns: true if the request was cancelled and an SIRequestCancelledEvent will be 
delivered to the listener, false if the request has already completed (either successfully, with an error or due to a 
prior cancel method call) */
public abstract boolean cancelRequest();

/*
Returns whether the information will be returned from cache or from the stream Returns: true if the information will be 
returned from cache, false if the information will be retrieved from the stream */
public abstract boolean isAvailableInCache();

//not API
/*
Returns the application data that was passed to the retrieve method Returns: the application 
data */
public java.lang.Object getAppData() {
   return appData;
}


}
