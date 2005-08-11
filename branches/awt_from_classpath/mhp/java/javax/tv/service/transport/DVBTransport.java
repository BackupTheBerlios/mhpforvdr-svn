
package javax.tv.service.transport;
import org.dvb.si.SIDatabase;
import org.davic.net.dvb.DvbLocator;

/*
   As the spec wants it, one object to implement them all.
   I am not quite sure what this object is supposed to represent.
   The spec only mentions "the object implementing Transport",
   so this means only one object which represents the receiving devices
   abstractly.
   However, since VDR supports multiple devices with possibly different delivery
   systems, there are some difficulties.
   => TODO for the future.
*/

public class DVBTransport implements Transport, NetworkCollection, BouquetCollection {

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

public void addServiceDetailsChangeListener ( ServiceDetailsChangeListener listener) {
}


/*
 
 Called to unregister an
 ServiceDetailsChangeListener . If the specified
 ServiceDetailsChangeListener is not registered, no
 action is performed. 
 Parameters:  listener - A previously registered listener. 
 
 
 */

public void removeServiceDetailsChangeListener ( ServiceDetailsChangeListener listener) {
}


/*
 
 Reports the type of mechanism by which this
 Transport delivers content. 
 Returns: The delivery system type of this transport. 
 
 
*/

public javax.tv.service.navigation.DeliverySystemType  getDeliverySystemType () {
   return javax.tv.service.navigation.DeliverySystemType.UNKNOWN;
}



/*
 
 Retrieves the specified Network from the collection. */

public javax.tv.service.SIRequest  retrieveNetwork ( javax.tv.locator.Locator locator,
                 javax.tv.service.SIRequestor requestor)
             throws javax.tv.locator.InvalidLocatorException ,
                 java.lang.SecurityException
{
   SIDatabase db=SIDatabase.getSIDatabase()[0];
   if (!(locator instanceof DvbLocator))
      throw new javax.tv.locator.InvalidLocatorException(locator, "Unsupported locator class");
   if (!((DvbLocator)locator).provides(DvbLocator.NETWORK))
      throw new javax.tv.locator.InvalidLocatorException(locator, "Locator does not specify network");
   int nid=((DvbLocator)locator).getNetworkId();
   javax.tv.service.OrgDvbSiRequestAdapter req=new javax.tv.service.OrgDvbSiRequestAdapter(requestor);
   req.setRequest(db.retrieveSINetworks(org.dvb.si.SIInformation.FROM_CACHE_OR_STREAM, null, req, nid, null));
   return req;
}

/*
 
 Retrieves an array of all the Network objects in
 this NetworkCollection . The array will only contain
 Network instances n for which the
 caller has
 javax.tv.service.ReadPermission(n.getLocator()) . If
 no Network instances meet this criteria, this method
 will result in an SIRequestFailureType of
 DATA_UNAVAILABLE . */

public javax.tv.service.SIRequest  retrieveNetworks ( javax.tv.service.SIRequestor requestor) {
   SIDatabase db=SIDatabase.getSIDatabase()[0];
   javax.tv.service.OrgDvbSiRequestAdapter req=new javax.tv.service.OrgDvbSiRequestAdapter(requestor);
   req.setRequest(db.retrieveSINetworks(org.dvb.si.SIInformation.FROM_CACHE_OR_STREAM, null, req, -1, null));
   return req;
}


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

public void addNetworkChangeListener ( NetworkChangeListener listener) {
}


/*
 
 Called to unregister an
 NetworkChangeListener . If the specified
 NetworkChangeListener is not registered, no
 action is performed. 
 
 
 
 Parameters:  listener - A previously registered listener. 
 
 
*/

public void removeNetworkChangeListener ( NetworkChangeListener listener) {
}


/*
 
 Retrieves the specified Bouquet from the collection. */

public javax.tv.service.SIRequest  retrieveBouquet ( javax.tv.locator.Locator locator,
                 javax.tv.service.SIRequestor requestor)
             throws javax.tv.locator.InvalidLocatorException ,
                 java.lang.SecurityException
{
   SIDatabase db=SIDatabase.getSIDatabase()[0];
   if (!(locator instanceof DvbLocator))
      throw new javax.tv.locator.InvalidLocatorException(locator, "Unsupported locator class");
   if (!((DvbLocator)locator).provides(DvbLocator.BOUQUET))
      throw new javax.tv.locator.InvalidLocatorException(locator, "Locator does not specify bouquet");
   int bid=((DvbLocator)locator).getBouquetId();
   javax.tv.service.OrgDvbSiRequestAdapter req=new javax.tv.service.OrgDvbSiRequestAdapter(requestor);
   req.setRequest(db.retrieveSIBouquets(org.dvb.si.SIInformation.FROM_CACHE_OR_STREAM, null, req, bid, null));
   return req;
}


/*
 
 Retrieves an array of all the Bouquet objects in
 this BouquetCollection . This array will only contain
 Bouquet instances b for which the caller has
 javax.tv.service.ReadPermission(b.getLocator()) . If
 no Bouquet instances meet this criteria, this method
 will result in an SIRequestFailureType of
 DATA_UNAVAILABLE . */

public javax.tv.service.SIRequest  retrieveBouquets ( javax.tv.service.SIRequestor requestor)
{
   SIDatabase db=SIDatabase.getSIDatabase()[0];
   javax.tv.service.OrgDvbSiRequestAdapter req=new javax.tv.service.OrgDvbSiRequestAdapter(requestor);
   req.setRequest(db.retrieveSIBouquets(org.dvb.si.SIInformation.FROM_CACHE_OR_STREAM, null, req, -1, null));
   return req;
}


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

public void addBouquetChangeListener ( BouquetChangeListener listener) {
}


/*
 
 Called to unregister an
 BouquetChangeListener . If the specified
 BouquetChangeListener is not registered, no
 action is performed. 
 
 
 
 Parameters:  listener - A previously registered listener. 
 
 
*/

public void removeBouquetChangeListener ( BouquetChangeListener listener) {
}



}