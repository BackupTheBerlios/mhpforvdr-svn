
package org.davic.net;

import org.davic.net.dvb.*;
import javax.tv.locator.InvalidLocatorException;

//TODO: transformLocator does nothing useful

public class MyLocatorFactory extends javax.tv.locator.LocatorFactory {
/*
 
 Creates a Locator object from the specified locator
 string. The format of the locator string may be entirely
 implementation-specific. 
 Parameters:  locatorString - The string form of the Locator 
 to be created. Returns: A Locator object representing the resource
 referenced by the given locator string. Throws:  MalformedLocatorException  - If an incorrectly formatted
 locator string is detected. See Also:   Locator.toExternalForm()  
 
 
 */

public javax.tv.locator.Locator  createLocator (java.lang.String locatorString)
                throws javax.tv.locator.MalformedLocatorException
{
   try {
   return (javax.tv.locator.Locator)(new DvbLocator(locatorString));
   } catch (javax.tv.locator.InvalidLocatorException ex) {
      throw new javax.tv.locator.MalformedLocatorException(ex.getMessage()+", first caught in MyLocatorFactory.createLocator");
   }
}


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

public javax.tv.locator.Locator [] transformLocator ( javax.tv.locator.Locator source)
                  throws javax.tv.locator.InvalidLocatorException {
   //TODO: return NetworkBoundLocator
   DvbLocator loc=new DvbLocator(source.toExternalForm());
   Locator[] ret=new Locator[1];
   ret[1]=loc;
   return (javax.tv.locator.Locator[])ret;
}


}
