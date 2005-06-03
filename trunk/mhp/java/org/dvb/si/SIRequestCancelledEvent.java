
package org.dvb.si;

/*This event is sent in response to a SI retrieval request when the request is cancelled 
with the SIRequest.cancelRequest method call. */

public class SIRequestCancelledEvent extends SIRetrievalEvent {

/*
The constructor for the event Parameters: appData - the application data passed in the request method call request - the 
SIRequest instance which is the source of the event */
public SIRequestCancelledEvent(SIRequest request) {
   super(request);
}


}
