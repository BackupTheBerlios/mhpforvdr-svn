
package org.dvb.si;

/*This event is sent in response to a SI retrieval request when the retrieve request was 
successfully completed. The result of the request can be obtained from the getResult 
method. */

public class SISuccessfulRetrieveEvent extends SIRetrievalEvent {

/*
The constructor for the event Parameters: appData - the application data passed in the request method call request - the 
SIRequest instance which is the source of the event result - an SIIterator containing the retrieved 
objects */
public SISuccessfulRetrieveEvent(SIRequest request) {
   super(request);
}

/*
Returns the requested data in an SIIterator object. Returns: An SIIterator containing the requested objects See Also: 
SIObjectNotInTableEvent */
public SIIterator getResult() {
   return ((SIDatabaseRequest)getSource()).getIterator();
}


}
