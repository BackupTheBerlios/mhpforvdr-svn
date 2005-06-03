
package javax.tv.service.selection;

/*

This class serves as a factory for the creation of
<code>ServiceContext</code> objects.

*/
public abstract class ServiceContextFactory extends java.lang.Object {

/*
 
 Creates a ServiceContextFactory . */

protected ServiceContextFactory (){
}

static ServiceContextFactory factory = new VDRServiceContextFactory();

/*
 
 Provides an instance of ServiceContextFactory . 
 Returns: An instance of ServiceContextFactory . 
 
 
 */

public static ServiceContextFactory  getInstance (){
   return factory;
}


/*
 
 Creates a ServiceContext object. The new
 ServiceContext is created in the not
 presenting state.*/

public abstract ServiceContext  createServiceContext ()
                       throws InsufficientResourcesException ,
                          java.lang.SecurityException;


/*
 
 Provides the ServiceContext instances to which the
 caller of the method is permitted access. If the caller has
 ServiceContextPermission("access","*") , then all
 current (i.e., undestroyed) ServiceContext instances
 are returned. If the application making this call is running in
 a ServiceContext and has
 ServiceContextPermission("access","own") , its own
 ServiceContext will be included in the returned
 array. If no ServiceContext instances are
 accessible to the caller, a zero-length array is returned. No
 ServiceContext instances in the destroyed 
 state are returned by this method. 
 Returns: An array of accessible ServiceContext objects. See Also:   ServiceContextPermission  
 
 
 */

public abstract ServiceContext [] getServiceContexts ();


/*
 
 Reports the ServiceContext in which the
 Xlet corresponding to the specified
 XletContext is running. The returned
 ServiceContext is the one from which the
 Service carrying the Xlet was selected. 
 Parameters:  ctx - The XletContext of the Xlet 
 of interest. Returns: The ServiceContext in which the Xlet 
 corresponding to ctx is running. Throws:  java.lang.SecurityException - If the
 Xlet corresponding to ctx does not have
 ServiceContextPermission("access", "own") .  ServiceContextException  - If the
 Xlet corresponding to ctx is not running
 within a ServiceContext . 
 
 
*/

public abstract ServiceContext  getServiceContext ( javax.tv.xlet.XletContext ctx)
                     throws java.lang.SecurityException,
                         ServiceContextException ;



}

