
package javax.tv.locator;

import org.davic.net.MyLocatorFactory;

/*

This class defines a factory for the creation of
 <code>Locator</code> objects.

*/
public abstract class LocatorFactory extends java.lang.Object {

protected static LocatorFactory self=new MyLocatorFactory();
/*
 
 Creates the LocatorFactory instance. */

protected LocatorFactory (){
}


/*
 
 Provides an instance of LocatorFactory . 
 Returns: A LocatorFactory instance. 
 
 
 */

public static LocatorFactory  getInstance (){
   return self;
}



/*
 
 Creates a Locator object from the specified locator
 string. The format of the locator string may be entirely
 implementation-specific. 
 Parameters:  locatorString - The string form of the Locator 
 to be created. Returns: A Locator object representing the resource
 referenced by the given locator string. Throws:  MalformedLocatorException  - If an incorrectly formatted
 locator string is detected. See Also:   Locator.toExternalForm()  
 
 
 */

public abstract Locator  createLocator (java.lang.String locatorString)
                throws MalformedLocatorException;


/*
 
 Transforms a Locator into its respective collection
 of transport dependent Locator objects. A
 transformation on a transport dependent Locator 
 results in an identity transformation, i.e. the same locator is
 returned in a single-element array. 
 Parameters:  source - The Locator to transform. Returns: An array of transport dependent Locator 
 objects for the given Locator . Throws:  InvalidLocatorException  - If source is not a valid
 Locator. 
 
 
*/

public abstract Locator [] transformLocator ( Locator source)
                  throws InvalidLocatorException;



}

