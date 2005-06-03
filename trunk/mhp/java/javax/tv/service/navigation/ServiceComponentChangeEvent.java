
package javax.tv.service.navigation;

/*

A <code>ServiceComponentChangeEvent</code> notifies an
 <code>ServiceComponentChangeListener</code> of changes to a
 <code>ServiceComponent</code> detected in a
 <code>ServiceDetails</code>.  Specifically, this event signals the
 addition, removal, or modification of a
 <code>ServiceComponent</code>.

*/
public class ServiceComponentChangeEvent extends ServiceDetailsSIChangeEvent {

/*
 
 Constructs a ServiceComponentChangeEvent . 
 Parameters:  service - The ServiceDetails in which the
 change occurred. type - The type of change that occurred. c - The ServiceComponent that changed. 
 
 */

public ServiceComponentChangeEvent ( ServiceDetails service,
                  javax.tv.service.SIChangeType type,
                  ServiceComponent c){
   super(service, type, c);
}


/*
 
 Reports the ServiceComponent that changed. It will be
 identical to the object returned by the inherited
 SIChangeEvent.getSIElement method. 
 Returns: The ServiceComponent that changed. 
 
 
*/

public ServiceComponent  getServiceComponent (){
   return (ServiceComponent)getSIElement();
}



}

