
package javax.tv.service.transport;
import javax.tv.service.*;

/*

This interface is implemented by applications wishing to receive
 notification of changes to <code>ServiceDetails</code> data.

*/
public interface ServiceDetailsChangeListener extends SIChangeListener {

/*
 
 Notifies the ServiceDetailsChangeListener of a
 change to a ServiceDetails . 
 
 
 
 Parameters:  event - A ServiceDetailsChangeEvent 
 describing what changed and how. 
 
 
*/

public void notifyChange ( ServiceDetailsChangeEvent event);



}

