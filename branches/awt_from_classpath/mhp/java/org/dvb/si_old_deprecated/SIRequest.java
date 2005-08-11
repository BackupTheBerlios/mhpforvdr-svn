
package org.dvb.si;

/*Object instances of this class represent SI retrieval requests made by the application. 
The application may cancel the request using this object. */

public abstract class SIRequest {

boolean inCache = true;

protected SIRetrievalListener listener = null;
protected java.lang.Object appData = null;
protected SIDatabase db = null;


SIRequest(java.lang.Object appData, SIRetrievalListener listener, SIDatabase db) {
   this.appData=appData;
   this.listener=listener;
   this.db=db;
   db.appendRequest(this);
}

/*not API
  Called by SIDatabase thread.
  Gives request some time to gather information (but not to do asynchronous tasks - 
  for that, return false in CanExecute) and send itself to the listener.
  All references will be removed after this call. */
abstract void Execute();

/*not API
  called by SIDatabase thread
  Returns true if enough information is available to Execute() be removed afterwards.
  Returns false, and it will be asked again and again until it is ready. */
abstract boolean CanExecute(); 




/*
Cancels the retrieval request. Returns: true if the request was cancelled and an SIRequestCancelledEvent will be 
delivered to the listener, false if the request has already completed (either successfully, with an error or due to a 
prior cancel method call) */
public abstract boolean cancelRequest();

/*
Returns whether the information will be returned from cache or from the stream Returns: true if the information will be 
returned from cache, false if the information will be retrieved from the stream */
public abstract boolean isAvailableInCache();


}
