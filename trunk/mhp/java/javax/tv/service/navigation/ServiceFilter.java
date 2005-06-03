
package javax.tv.service.navigation;

/*

This class represents a set filtering criteria used to generate a
 <code>ServiceList</code>.  <code>ServiceFilter</code> is extended
 to create concrete filters based on various criteria.  Applications
 may also extend this class to define custom filters, although
 custom filters may not be supported on certain filtering
 operations.

*/
public abstract class ServiceFilter extends java.lang.Object {

/*
 
 Constructs the filter. */

protected ServiceFilter (){
}


/*
 
 Tests if a particular service passes this filter. 
 Subtypes of ServiceFilter override this method to
 provide the logic for a filtering operation on individual
 Service objects. 
 Parameters:  service - A Service to be evaluated
 against the filtering algorithm. Returns:  true if service satisfies the
 filtering algorithm; false otherwise. 
 
 
*/

public abstract boolean accept ( javax.tv.service.Service service);



}

