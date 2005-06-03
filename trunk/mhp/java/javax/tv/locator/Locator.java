
package javax.tv.locator;

/*

The <code>Locator</code> interface provides an opaque reference to
 the location information of objects which are addressable within the
 Java TV API. A given locator may represent a transport independent
 object and have multiple mappings to transport dependent locators.
 Methods are provided for discovery of such circumstances and for
 transformation to transport dependent locators.

*/
public interface Locator {

/*
 
 Generates a canonical, string-based representation of this
 Locator . The string returned may be entirely
 platform-dependent. If two locators have identical external
 forms, they refer to the same resource. However, two locators
 that refer to the same resource may have different external
 forms. */

public java.lang.String toExternalForm ();


/*
 
 Indicates whether this Locator has a mapping to
 multiple transports. 
 Returns:  true if multiple transformations exist for
 this Locator , false otherwise. 
 
 
 */

public boolean hasMultipleTransformations ();


/*
 
 Compares this Locator with the specified object for
 equality. The result is true if and only if the
 specified object is also a Locator and has an
 external form identical to the external form of this
 Locator . 
 Overrides:  equals in class java.lang.Object 
 
 
 Parameters:  o - The object against which to compare this Locator . Returns:  true if the specified object is equal to this
 Locator . See Also:  String.equals(Object) 
 
 
 */

public boolean equals (java.lang.Object o);


/*
 
 Generates a hash code value for this Locator .
 Two Locator instances for which Locator.equals() 
 is true will have identical hash code values. 
 Overrides:  hashCode in class java.lang.Object 
 
 
 Returns: The hash code value for this Locator . See Also:   equals(Object)  
 
 
 */

public int hashCode ();


/*
 
 Returns the string used to create this locator. 
 Overrides:  toString in class java.lang.Object 
 
 
 Returns: The string used to create this locator. See Also:   LocatorFactory.createLocator(java.lang.String)  
 
 
*/

public java.lang.String toString ();



}

