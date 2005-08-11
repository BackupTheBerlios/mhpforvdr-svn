
package javax.tv.service.transport;

/*

This interface represents a collection of networks on a
 <code>Transport</code>.  This information is carried in the DVB SI
 NIT or US Cable SI (A56) NIT tables.
 <code>NetworkCollection</code> may be optionally implemented by
 <code>Transport</code> objects, depending on the SI data carried on
 that transport.

*/
public interface NetworkCollection extends Transport {

/*
 
 Retrieves the specified Network from the collection. */

public javax.tv.service.SIRequest  retrieveNetwork ( javax.tv.locator.Locator locator,
                 javax.tv.service.SIRequestor requestor)
             throws javax.tv.locator.InvalidLocatorException ,
                 java.lang.SecurityException;


/*
 
 Retrieves an array of all the Network objects in
 this NetworkCollection . The array will only contain
 Network instances n for which the
 caller has
 javax.tv.service.ReadPermission(n.getLocator()) . If
 no Network instances meet this criteria, this method
 will result in an SIRequestFailureType of
 DATA_UNAVAILABLE . */

public javax.tv.service.SIRequest  retrieveNetworks ( javax.tv.service.SIRequestor requestor);


/*
 
 Registers a NetworkChangeListener to be notified of
 changes to a Network that is part of this
 NetworkCollection . Subsequent notification is made
 via NetworkChangeEvent with this
 NetworkCollection as the event source and an
 SIChangeType of ADD ,
 REMOVE or MODIFY . Only changes to
 Network instances n for which the
 caller has
 javax.tv.service.ReadPermission(n.getLocator()) will
 be reported. 
 
 This method is only a request for notification. No guarantee is
 provided that the SI database will detect all, or even any, SI
 changes or whether such changes will be detected in a timely
 fashion. 
 
 If the specified NetworkChangeListener is
 already registered, no action is performed. 
 
 
 
 Parameters:  listener - A NetworkChangeListener to be
 notified about changes related to Network 
 carried on this Transport . See Also:   NetworkChangeEvent , 
 ReadPermission  
 
 
 */

public void addNetworkChangeListener ( NetworkChangeListener listener);


/*
 
 Called to unregister an
 NetworkChangeListener . If the specified
 NetworkChangeListener is not registered, no
 action is performed. 
 
 
 
 Parameters:  listener - A previously registered listener. 
 
 
*/

public void removeNetworkChangeListener ( NetworkChangeListener listener);



}

