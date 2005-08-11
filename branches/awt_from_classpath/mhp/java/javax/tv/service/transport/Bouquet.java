
package javax.tv.service.transport;

/*

This interface represents information about a bouquet.<p>

 A <code>Bouquet</code> object may optionally implement the
 <code>CAIdentification</code> interface. Note that bouquets are not
 supported in ATSC.

*/
public interface Bouquet extends javax.tv.service.SIElement {

/*
 
 Reports the ID of this bouquet definition. 
 
 
 
 Returns: A number identifying this bouquet 
 
 
 */

public int getBouquetID ();


/*
 
 Reports the name of this bouquet. 
 
 
 
 Returns: A string representing the name of this bouquet, or an empty
 string if the name is not available. 
 
 
*/

public java.lang.String getName ();



}

