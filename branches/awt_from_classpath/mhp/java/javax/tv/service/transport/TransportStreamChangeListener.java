
package javax.tv.service.transport;

/*

This interface is implemented by applications wishing to receive
 notification of changes to <code>TransportStream</code> data.

*/
public interface TransportStreamChangeListener extends javax.tv.service.SIChangeListener {

/*
 
 Notifies the TransportStreamChangeListener of a
 change to a TransportStream . 
 
 
 
 Parameters:  event - A TransportStreamChangeEvent 
 describing what changed and how. 
 
 
*/

public void notifyChange ( TransportStreamChangeEvent event);



}

