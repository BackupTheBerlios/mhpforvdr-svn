
package javax.tv.service.navigation;

/*

This interface is implemented by applications wishing to receive
 notification of changes to <code>ServiceComponent</code> data.

*/
public interface ServiceComponentChangeListener extends javax.tv.service.SIChangeListener {

/*
 
 Notifies the ServiceComponentChangeListener of a
 change to a ServiceComponent . 
 
 
 
 Parameters:  event - A ServiceComponentChangeEvent 
 describing what changed and how. 
 
 
*/

public void notifyChange ( ServiceComponentChangeEvent event);



}

