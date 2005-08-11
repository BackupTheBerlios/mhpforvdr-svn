
package javax.tv.service.transport;

/*

A <code>BouquetChangeEvent</code> notifies an
 <code>BouquetChangeListener</code> of changes detected in a
 <code>BouquetCollection</code>.  Specifically, this event
 signals the addition, removal, or modification of a
 <code>Bouquet</code>.

*/
public class BouquetChangeEvent extends TransportSIChangeEvent {

/*
 
 Constructs a BouquetChangeEvent . 
 Parameters:  collection - The BouquetCollection in which the
 change occurred. type - The type of change that occurred. b - The Bouquet that changed. 
 
 */

public BouquetChangeEvent ( BouquetCollection collection,
              javax.tv.service.SIChangeType type,
              Bouquet b){
   super(collection, type, b);
}


/*
 
 Reports the BouquetCollection that generated the
 event. It will be identical to the object returned by the
 getTransport() method. 
 Returns: The BouquetCollection that generated the
 event. 
 
 
 */

public BouquetCollection  getBouquetCollection (){
   return (BouquetCollection)getSource();
}


/*
 
 Reports the Bouquet that changed. It will be
 identical to the object returned by the inherited
 SIChangeEvent.getSIElement method. 
 Returns: The Bouquet that changed. 
 
 
*/

public Bouquet  getBouquet (){
   return (Bouquet)getSIElement();
}



}

