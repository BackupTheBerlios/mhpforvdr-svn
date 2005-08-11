
package javax.tv.service.transport;

/*

This interface represents a collection of transport streams on a
 <code>Transport</code>.  <code>TransportStreamCollection</code> may
 be optionally implemented by <code>Transport</code> objects,
 depending on the SI data carried on that transport.

*/
public interface TransportStreamCollection extends Transport {

/*
 
 Retrieves the specified TransportStream from the
 collection. 
 
 
 
 Parameters:  locator - Locator referencing the
 TransportStream of interest. requestor - The SIRequestor to be notified
 when this retrieval operation completes. Returns: An SIRequest object identifying this
 asynchronous retrieval request. Throws:  InvalidLocatorException  - If locator does not
 reference a valid transport stream. java.lang.SecurityException - If the caller does not have
 javax.tv.service.ReadPermission(locator) . See Also:   TransportStream , 
 ReadPermission  
 
 
 */

public javax.tv.service.SIRequest  retrieveTransportStream ( javax.tv.locator.Locator locator,
                     javax.tv.service.SIRequestor requestor)
                 throws javax.tv.locator.InvalidLocatorException ,
                     java.lang.SecurityException;


/*
 
 Retrieves an array of the TransportStream objects in
 this TransportStreamCollection . The array will only
 contain TransportStream instances ts 
 for which the caller has
 javax.tv.service.ReadPermission(ts.getLocator()) . If
 no TransportStream instances meet this criteria,
 this method will result in an SIRequestFailureType 
 of DATA_UNAVAILABLE . 
 
 This method delivers its results asynchronously. 
 
 
 
 Parameters:  requestor - The SIRequestor to be notified
 when this retrieval operation completes. Returns: An SIRequest object identifying this
 asynchronous retrieval request. See Also:   TransportStream , 
 ReadPermission  
 
 
 */

public javax.tv.service.SIRequest  retrieveTransportStreams ( javax.tv.service.SIRequestor requestor);


/*
 
 Registers a TransportStreamChangeListener to be
 notified of changes to a TransportStream that is
 part of this TransportStreamCollection . Subsequent
 notification is made via TransportStreamChangeEvent 
 with this TransportStreamCollection as the event
 source and an SIChangeType of ADD ,
 REMOVE or MODIFY . Only changes to
 TransportStream instances ts for which
 the caller has
 javax.tv.service.ReadPermission(ts.getLocator()) 
 will be reported. 
 
 This method is only a request for notification. No guarantee is
 provided that the SI database will detect all, or even any, SI
 changes or whether such changes will be detected in a timely
 fashion. 
 
 If the specified TransportStreamChangeListener is
 already registered, no action is performed. 
 
 
 
 Parameters:  listener - A TransportStreamChangeListener to be
 notified about changes related to TransportStream 
 carried on this Transport . See Also:   TransportStreamChangeEvent , 
 ReadPermission  
 
 
 */

public void addTransportStreamChangeListener ( TransportStreamChangeListener listener);


/*
 
 Called to unregister an
 TransportStreamChangeListener . If the specified
 TransportStreamChangeListener is not registered, no
 action is performed. 
 
 
 
 Parameters:  listener - A previously registered listener. 
 
 
*/

public void removeTransportStreamChangeListener ( TransportStreamChangeListener listener);



}

