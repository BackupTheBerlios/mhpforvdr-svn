
package javax.tv.service.transport;

/*

This interface is implemented by applications wishing to receive
 notification of changes to <code>Network</code> data.

*/
public interface NetworkChangeListener extends javax.tv.service.SIChangeListener {

/*
 
 Notifies the NetworkChangeListener of a
 change to a Network . 
 
 
 
 Parameters:  event - A NetworkChangeEvent 
 describing what changed and how. 
 
 
*/

public void notifyChange ( NetworkChangeEvent event);



}

