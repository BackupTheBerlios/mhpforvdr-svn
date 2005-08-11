
package javax.tv.service.transport;

/*

This interface represents an individual content delivery mechanism.
 A <code>Transport</code> serves as an access point for acquiring
 information about services and their groupings.<p>

 A <code>Transport</code> may expose various types of
 entities (e.g. bouquets, networks and/or transport streams) by
 optionally implementing additional interfaces
 (i.e. <code>BouquetCollection</code>,
 <code>NetworkCollection</code>, and/or
 <code>TransportStreamCollection</code>), depending on the particular
 SI format used and the presence of optional elements and tables in
 the SI data being broadcast.<p>

*/
public interface Transport {

/*
 
 Registers a ServiceDetailsChangeListener to be
 notified of changes to ServiceDetails that are
 carried on this Transport . Subsequent notification
 is made via ServiceDetailsChangeEvent with this
 Transport instance as the event source and an
 SIChangeType of ADD ,
 REMOVE or MODIFY . Only changes to
 ServiceDetails  sd for which the caller
 has javax.tv.service.ReadPermission(sd.getLocator()) 
 will be reported. 
 
 This method is only a request for notification. No guarantee is
 provided that the SI database will detect all, or even any, SI
 changes or whether such changes will be detected in a timely
 fashion. Applications may indicate ServiceDetails 
 of particular interest via the method  SIManager.registerInterest(javax.tv.locator.Locator, boolean) . 
 
 If the specified ServiceDetailsChangeListener is
 already registered, no action is performed. 
 Parameters:  listener - An ServiceDetailsChangeListener to be
 notified about changes related to ServiceDetails 
 carried on this Transport . See Also:   ServiceDetailsChangeEvent , 
 SIManager.registerInterest(javax.tv.locator.Locator, boolean) , 
 ReadPermission  
 
 
 */

public void addServiceDetailsChangeListener ( ServiceDetailsChangeListener listener);


/*
 
 Called to unregister an
 ServiceDetailsChangeListener . If the specified
 ServiceDetailsChangeListener is not registered, no
 action is performed. 
 Parameters:  listener - A previously registered listener. 
 
 
 */

public void removeServiceDetailsChangeListener ( ServiceDetailsChangeListener listener);


/*
 
 Reports the type of mechanism by which this
 Transport delivers content. 
 Returns: The delivery system type of this transport. 
 
 
*/

public javax.tv.service.navigation.DeliverySystemType  getDeliverySystemType ();



}

