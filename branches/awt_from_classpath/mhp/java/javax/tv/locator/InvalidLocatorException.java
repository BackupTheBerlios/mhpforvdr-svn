
package javax.tv.locator;

/*

This exception is thrown when a <code>Locator</code> is not valid
 in a particular context.  A <code>Locator</code> can be invalid or
 several reasons, including:

 <ul>

 <li> The <code>Locator</code> refers to a resource that is not
 valid at the time of usage.

 <li> The <code>Locator</code> refers to a type of resource that is
 not appropriate for usage as a particular method parameter.

 <li> The <code>Locator</code> refers to a type of
 resource whose usage is not supported on this system.

 </ul>

*/
public class InvalidLocatorException extends java.lang.Exception {

private Locator loc;
/*
 
 Constructs an InvalidLocatorException with no
 detail message. 
 Parameters:  locator - The offending Locator . 
 
 
 */

public InvalidLocatorException ( Locator locator){
   super(locator.toString());
   loc=locator;
}


/*
 
 Constructs an InvalidLocatorException with the
 specified detail message. 
 Parameters:  locator - The offending Locator . reason - The reason this Locator is invalid. 
 
 */

public InvalidLocatorException ( Locator locator,
                java.lang.String reason){
   super(locator.toString() + " " + reason);
   loc=locator;
}


/*
 
 Returns the offending Locator instance. 
 Returns: The locator that caused the exception. 
 
 
*/

public Locator  getInvalidLocator (){
   return loc;
}



}

