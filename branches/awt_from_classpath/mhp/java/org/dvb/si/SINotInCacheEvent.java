
package org.dvb.si;

/*This event is sent in response to a SI retrieval request when the request was made with 
the FROM_CACHE_ONLY mode and the requested data is not present in the 
cache. */

public class SINotInCacheEvent extends SIRetrievalEvent {

/*
 The constructor for the event Parameters: appData 
- the application data passed in the request method call request - the SIRequest instance which is the source of the 
event 
*/
public SINotInCacheEvent(SIRequest request) {
   super(request);
}


}
