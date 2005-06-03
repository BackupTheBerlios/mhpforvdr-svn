
package javax.tv.service.transport;
import javax.tv.service.*;

/*

A <code>ServiceDetailsChangeEvent</code> notifies an
 <code>ServiceDetailsChangeListener</code> of changes detected to a
 <code>ServiceDetails</code> on a <code>Transport</code>.
 Specifically, this event signals the addition, removal, or
 modification of a <code>ServiceDetails</code>.

*/
public class ServiceDetailsChangeEvent extends TransportSIChangeEvent {

/*
 
 Constructs a ServiceDetailsChangeEvent . 
 Parameters:  transport - The Transport on which the change
 occurred. type - The type of change that occurred. s - The ServiceDetails that changed. 
 
 */

public ServiceDetailsChangeEvent ( Transport transport,
                 SIChangeType type,
                 javax.tv.service.navigation.ServiceDetails s){
   super(transport, type, s);
}


/*
 
 Reports the ServiceDetails that changed. It will be
 identical to the object returned by the inherited
 SIChangeEvent.getSIElement method. 
 Returns: The ServiceDetails that changed. 
 
 
*/

public javax.tv.service.navigation.ServiceDetails  getServiceDetails (){
   return (javax.tv.service.navigation.ServiceDetails)getSIElement();
}



}

