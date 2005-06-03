
package org.dvb.si;

/*This class is the base class for events about completion of a SI retrieval request. 
Exactly one event will be returned in response to an SI retrieval 
request. */

public abstract class SIRetrievalEvent extends java.util.EventObject {

/*
The constructor for the event Parameters: appData - the application data passed in the request method call request - the 
SIRequest instance which is the source of the event */
public SIRetrievalEvent(SIRequest request) {
   super(request);
}

/*
Returns the application data that was passed to the retrieve method Returns: the application 
data */
public java.lang.Object getAppData() {
   return ((SIDatabaseRequest)getSource()).getAppData();
}


}
