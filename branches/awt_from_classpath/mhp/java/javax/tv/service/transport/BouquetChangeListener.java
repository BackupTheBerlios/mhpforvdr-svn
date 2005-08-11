
package javax.tv.service.transport;

/*

This interface is implemented by applications wishing to receive
 notification of changes to <code>Bouquet</code> data.

*/
public interface BouquetChangeListener extends javax.tv.service.SIChangeListener {

/*
 
 Notifies the BouquetChangeListener of a
 change to a Bouquet . 
 
 
 
 Parameters:  event - A BouquetChangeEvent 
 describing what changed and how. 
 
 
*/

public void notifyChange ( BouquetChangeEvent event);



}

