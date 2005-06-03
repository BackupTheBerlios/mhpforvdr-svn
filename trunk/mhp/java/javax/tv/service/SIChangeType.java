
package javax.tv.service;

/*

This class represents types of changes to SI elements.

*/
public class SIChangeType extends java.lang.Object {

/*
 
 SIChangeType indicating that an SIElement 
 has been added. 
 */

public static final SIChangeType  ADD = new SIChangeType("ADD");


/*
 
 SIChangeType indicating that an SIElement 
 has been removed. 
 */

public static final SIChangeType  REMOVE = new SIChangeType("REMOVE");


/*
 
 SIChangeType indicating that an SIElement 
 has been modified. */

public static final SIChangeType  MODIFY = new SIChangeType("MODIFY");

String name;
/*
 
 Creates an SIChangeType object. 
 Parameters:  name - The string name of this type (e.g. "ADD"). 
 
 */

protected SIChangeType (java.lang.String name) {
   this.name=name;
}


/*
 
 Provides the string name of the type. For the type objects
 defined in this class, the string name will be identical to the
 class variable name. 
 Overrides:  toString in class java.lang.Object 
 
 
 Returns: The string name of the type. 
 
 
*/

public java.lang.String toString (){
   return name;
}



}

