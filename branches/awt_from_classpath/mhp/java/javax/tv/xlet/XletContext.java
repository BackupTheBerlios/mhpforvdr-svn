
package javax.tv.xlet;

/*

An interface that provides methods allowing an Xlet to discover
 information about its environment. An XletContext is passed
 to an Xlet when the Xlet is initialized. It provides an Xlet with a
 mechanism to retrieve properties, as well as a way to signal
 internal state changes.

*/
public interface XletContext {

/*
 
 The property key used to obtain initialization arguments for the
 Xlet. The call
 XletContext.getXletProperty(XletContext.ARGS) will
 return the arguments as an array of Strings. If there are
 no arguments, then an array of length 0 will be returned. 
 See Also:   getXletProperty(java.lang.String)  
 
 */

public static final java.lang.String ARGS = "javax.tv.xlet.args";


/*
 
 Used by an application to notify its manager that it
 has entered into the
 Destroyed state. The application manager will not
 call the Xlet's destroy method, and all resources
 held by the Xlet will be considered eligible for reclamation. 
 Before calling this method,
 the Xlet must have performed the same operations
 (clean up, releasing of resources etc.) it would have if the
 Xlet.destroyXlet() had been called. 
 */

public void notifyDestroyed ();


/*
 
 Notifies the manager that the Xlet does not want to be active and has
 entered the Paused state. Invoking this method will
 have no effect if the Xlet is destroyed, or if it has not
 yet been started. */

public void notifyPaused ();


/*
 
 Provides an Xlet with a mechanism to retrieve named
 properties from the XletContext. 
 Parameters:  key - The name of the property. Returns: A reference to an object representing the property.
 null is returned if no value is available for key. 
 
 
 */

public java.lang.Object getXletProperty (java.lang.String key);


/*
 
 Provides the Xlet with a mechanism to indicate that it is
 interested in entering the Active state. Calls to this
 method can be used by an application manager to determine which
 Xlets to move to Active state. Any subsequent call to
 Xlet.startXlet() as a result of this method will
 be made via a different thread than the one used to call
 resumeRequest() . 
 See Also:   Xlet.startXlet()  
 
 
*/

public void resumeRequest ();



}

