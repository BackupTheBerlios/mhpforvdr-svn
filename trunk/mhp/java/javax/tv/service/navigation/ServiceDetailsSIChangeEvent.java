
package javax.tv.service.navigation;

/*

A <code>ServiceDetailsSIChangeEvent</code> notifies an
 <code>SIChangeListener</code> of changes to a
 <code>ServiceDetails</code>.

*/
public abstract class ServiceDetailsSIChangeEvent extends javax.tv.service.SIChangeEvent {

/*
 
 Constructs a ServiceDetailsSIChangeEvent . 
 Parameters:  service - The ServiceDetails in which the
 change occurred. type - The type of change that occurred. e - The SIElement that changed. 
 
 */

public ServiceDetailsSIChangeEvent ( ServiceDetails service,
                  javax.tv.service.SIChangeType type,
                  javax.tv.service.SIElement e){
   super(service, type, e);
}


/*
 
 Reports the ServiceDetails that generated the
 event. It will be identical to the object returned by the
 getSource() method. 
 Returns: The ServiceDetails that generated the
 event. 
 
 
*/

public ServiceDetails  getServiceDetails (){
   return (ServiceDetails)getSource();
}



}

