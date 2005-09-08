
package javax.tv.service.selection;


import javax.tv.service.VDRService;
import org.dvb.application.MHPApplication;
import javax.tv.xlet.XletContext;
import vdr.mhp.ApplicationManager;

class VDRServiceContextFactory extends ServiceContextFactory {

/*
"A receiver has a number of service contexts. 
There may be a maximum number for a receiver 
(and this maximum may differ between receivers),
 and this maximum number may be one."
 
"The simple way of thinking about this is to imagine
that a digital TV receiver that supports JavaTV has
as many service contexts as it has tuners - this isn't
entirely correct, but it helps illustrate the point."

So, since we have one current channel and one primary device
in VDR, we have exactly one current service context.
The resource is freed when it enters the DESTROYED state,
and accessible by all applications.
*/


protected VDRServiceContextFactory (){
}

/*
 
 Creates a ServiceContext object. The new
 ServiceContext is created in the not
 presenting state.*/

public ServiceContext  createServiceContext ()
 throws InsufficientResourcesException, java.lang.SecurityException {
   SecurityManager s = System.getSecurityManager();
   if (s != null) {
      s.checkPermission(new ServiceContextPermission("create", "own"));
   }
   
   return VDRServiceContext.getContext();
   /*
   MHPApplication app = ApplicationManager.getManager().getApplicationFromStack());
   if (app == null)
      return null;
   else {
      return createServiceContext(app);
   }
   */
}


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

public ServiceContext [] getServiceContexts () {
   //in the current implementation, there only one service context
   // so no need to check for the "*" permission
   SecurityManager s = System.getSecurityManager();
   if (s != null) {
      try {
         s.checkPermission(new ServiceContextPermission("access", "own"));
      } catch (SecurityException e) {
         e.printStackTrace();
         return new ServiceContext [] {};
      }
   }

   return new VDRServiceContext [] { VDRServiceContext.getContext() };
   /*
   MHPApplication app = ApplicationManager.getManager().getApplicationFromStack());
   if (app == null)
      return new ServiceContext[0];
   else {
      ServiceContext[] ar = { createServiceContext(app) };
      return ar;
   }
   */
}


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

public ServiceContext  getServiceContext ( javax.tv.xlet.XletContext ctx)
                     throws java.lang.SecurityException,
                         ServiceContextException
{
   SecurityManager s = System.getSecurityManager();
   if (s != null) {
      s.checkPermission(new ServiceContextPermission("access", "own"));
   }
   if (ctx==null || !(ctx instanceof MHPApplication))
      throw new ServiceContextException("invalid service context");

   return VDRServiceContext.getContext();
   /*
   if (context == null)
      context = createServiceContext((MHPApplication)ctx);
   return context;
   */
}

/*
private createServiceContext(MHPApplication app) {
   return new VDRServiceContext(app);
}
*/

}

