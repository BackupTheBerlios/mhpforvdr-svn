
package org.dvb.si;

/*This event is sent in response to a SI descriptor retrieval request when the table 
carrying the information about the object has been updated and the set of descriptors 
consistent with the old object can not be retrieved. The application should in this case  
rst update the SIInformation object and then request the descriptors 
again. */

public class SITableUpdatedEvent extends SIRetrievalEvent {

/*
The constructor for the event Parameters: appData - the application data passed in the request method call request - the 
SIRequest instance which is the source of the event */
public SITableUpdatedEvent(SIRequest request) {
   super(request);
}


}
