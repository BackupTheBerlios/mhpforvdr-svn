
package javax.tv.service.navigation;

import javax.tv.service.ServiceType;
/*

<code>ServiceTypeFilter</code> represents a
 <code>ServiceFilter</code> based on a particular
 <code>ServiceType</code>.  A <code>ServiceList</code> resulting
 from this filter will include only <code>Service</code> objects of
 the specified service type.

*/
public final class ServiceTypeFilter extends ServiceFilter {

/*
 
 Constructs the filter based on a particular ServiceType . 
 Parameters:  type - A ServiceType object indicating the type
 of services to be included in a resulting service list. 
 
 */

ServiceType type;

public ServiceTypeFilter ( ServiceType type){
   this.type=type;
}


/*
 
 Reports the ServiceType used to create this filter. 
 Returns: The ServiceType used to create this filter. 
 
 
 */

public ServiceType  getFilterValue (){
   return type;
}


/*
 
 Tests if the given service passes the filter. 
 Overrides:  accept  in class  ServiceFilter  
 
 
 Parameters:  service - An individual Service to be evaluated
 against the filtering algorithm. Returns:  true if service is of the type
 indicated by the filter value; false otherwise. 
 
 
*/

public boolean accept ( javax.tv.service.Service service){
   //we can use == here, no need to use equals().
   return service!=null && service.getServiceType()==type;
}



}

