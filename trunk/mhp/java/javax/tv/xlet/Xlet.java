
package javax.tv.xlet;

/*

This interface allows an application manager to create,
 initialize, start, pause, and destroy an Xlet.
 An Xlet is an application or service designed to be run and
 controlled by an application manager via this lifecycle interface.
 The lifecycle states allow the application manager to manage
 the activities of multiple Xlets within a runtime environment
 selecting which Xlets are active at a given time.
 The application manager maintains the state of the Xlet and
 invokes method on the Xlet via the lifecycle methods.  The Xlet
 implements these methods to update its internal activities and
 resource usage as directed by the application manager. 
 The Xlet can initiate some state changes itself and informs
 the application manager of those state changes  
 by invoking methods on <code>XletContext</code>.<p>

 In order to support interoperability between Xlets and application
 managers, all Xlet classes must provide a public no-argument
 constructor.<p>
 
 <b>Note:</b> The methods on this interface are meant to signal state 
 changes. The state change is not considered complete until the state
 change method has returned. It is intended that these methods return
 quickly.<p>

*/
public interface Xlet {

/*
 
 Signals the Xlet to initialize itself and enter the 
 Paused state.
 The Xlet shall initialize itself in preparation for providing service.
 It should not hold shared resources but should be prepared to provide 
 service in a reasonable amount of time. 
 An XletContext is used by the Xlet to access
 properties associated with its runtime environment.
 After this method returns successfully, the Xlet
 is in the Paused state and should be quiescent. 
 Note: This method shall only be called once.  
 Parameters:  ctx - The XletContext of this Xlet. Throws:  XletStateChangeException  - If the Xlet cannot be
 initialized. See Also:   XletContext  
 
 
 */

public void initXlet ( XletContext ctx)
       throws XletStateChangeException ;


/*
 
 Signals the Xlet to start providing service and
 enter the Active state.
 In the Active state the Xlet may hold shared resources.
 The method will only be called when
 the Xlet is in the paused state.
  
 Throws:  XletStateChangeException  - is thrown if the Xlet
		cannot start providing service. 
 
 
 */

public void startXlet ()
        throws XletStateChangeException ;


/*
 
 Signals the Xlet to stop providing service and
 enter the Paused state.
 In the Paused state the Xlet must stop providing
 service, and might release all shared resources
 and become quiescent. This method will only be called
 called when the Xlet is in the Active state.  
 */

public void pauseXlet ();


/*
 
 Signals the Xlet to terminate and enter the Destroyed state.
 In the destroyed state the Xlet must release
 all resources and save any persistent state. This method may
 be called from the Loaded , Paused or 
 Active states. 
 Xlets should
 perform any operations required before being terminated, such as
 releasing resources or saving preferences or
 state. */

public void destroyXlet (boolean unconditional)
         throws XletStateChangeException ;



}

