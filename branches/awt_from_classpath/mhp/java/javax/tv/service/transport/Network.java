
package javax.tv.service.transport;

/*

This interface provides descriptive information concerning a network.

*/
public interface Network extends javax.tv.service.SIElement {

/*
 
 Reports the ID of this network. 
 
 
 
 Returns: A number identifying this network. 
 
 
 */

public int getNetworkID ();


/*
 
 Reports the name of this network. 
 
 
 
 Returns: A string representing the name of this network, or an empty
 string if the name is unavailable. 
 
 
 */

public java.lang.String getName ();


/*
 
 Retrieves an array of TransportStream objects
 representing the transport streams carried in this
 Network . Only TransportStream instances
 ts for which the caller has
 javax.tv.service.ReadPermission(ts.getLocator()) 
 will be present in the array. If no TransportStream 
 instances meet this criteria or if this Network does
 not aggregate transport streams, the result is an
 SIRequestFailureType of
 DATA_UNAVAILABLE . 
 
 This method delivers its results asynchronously. 
 
 
 
 Parameters:  requestor - The SIRequestor to be notified
 when this retrieval operation completes. Returns: An SIRequest object identifying this
 asynchronous retrieval request. See Also:   TransportStream , 
 ReadPermission  
 
 
*/

public javax.tv.service.SIRequest  retrieveTransportStreams ( javax.tv.service.SIRequestor requestor);



}

