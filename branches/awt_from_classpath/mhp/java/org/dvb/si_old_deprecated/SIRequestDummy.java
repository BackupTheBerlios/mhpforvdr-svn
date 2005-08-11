
package org.dvb.si;

/*Object instances of this class represent SI retrieval requests made by the application. 
The application may cancel the request using this object. */

public class SIRequestDummy extends SIRequest {

boolean inCache = true;

protected SIRetrievalListener listener = null;
protected java.lang.Object appData = null;
protected SIDatabase db = null;


SIRequestDummy(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db) {
   super(appData, listener, db);
}

void Execute() {   
}

boolean CanExecute() {
   return true;
}




/*
Cancels the retrieval request. Returns: true if the request was cancelled and an SIRequestCancelledEvent will be 
delivered to the listener, false if the request has already completed (either successfully, with an error or due to a 
prior cancel method call) */
public boolean cancelRequest() {
   return true;
}

/*
Returns whether the information will be returned from cache or from the stream Returns: true if the information will be 
returned from cache, false if the information will be retrieved from the stream */
public boolean isAvailableInCache() {
   return false;
}


}
