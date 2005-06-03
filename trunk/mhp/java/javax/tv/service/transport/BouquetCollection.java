
package javax.tv.service.transport;

/*

This interface represents a collection of bouquets on a
 <code>Transport</code>.  In DVB SI, this information is contained
 in the BAT tables.  <code>BouquetCollection</code> may be
 optionally implemented by <code>Transport</code> objects, depending
 on the SI data carried on that transport.

*/
public interface BouquetCollection extends Transport {

/*
 
 Retrieves the specified Bouquet from the collection. */

public javax.tv.service.SIRequest  retrieveBouquet ( javax.tv.locator.Locator locator,
                 javax.tv.service.SIRequestor requestor)
             throws javax.tv.locator.InvalidLocatorException ,
                 java.lang.SecurityException;


/*
 
 Retrieves an array of all the Bouquet objects in
 this BouquetCollection . This array will only contain
 Bouquet instances b for which the caller has
 javax.tv.service.ReadPermission(b.getLocator()) . If
 no Bouquet instances meet this criteria, this method
 will result in an SIRequestFailureType of
 DATA_UNAVAILABLE . */

public javax.tv.service.SIRequest  retrieveBouquets ( javax.tv.service.SIRequestor requestor);


/*
 
 Registers a BouquetChangeListener to be notified of
 changes to a Bouquet that is part of this
 BouquetCollection . Subsequent notification is made
 via BouquetChangeEvent with this
 BouquetCollection as the event source and an
 SIChangeType of ADD ,
 REMOVE or MODIFY . Only changes to
 Bouquet instances b for which the
 caller has
 javax.tv.service.ReadPermission(b.getLocator()) will
 be reported. 
 
 This method is only a request for notification. No guarantee is
 provided that the SI database will detect all, or even any, SI
 changes or whether such changes will be detected in a timely
 fashion. 
 
 If the specified BouquetChangeListener is
 already registered, no action is performed. 
 
 
 
 Parameters:  listener - A BouquetChangeListener to be
 notified about changes related to Bouquet 
 carried on this Transport . See Also:   BouquetChangeEvent , 
 ReadPermission  
 
 
 */

public void addBouquetChangeListener ( BouquetChangeListener listener);


/*
 
 Called to unregister an
 BouquetChangeListener . If the specified
 BouquetChangeListener is not registered, no
 action is performed. 
 
 
 
 Parameters:  listener - A previously registered listener. 
 
 
*/

public void removeBouquetChangeListener ( BouquetChangeListener listener);



}

