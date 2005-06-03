
package javax.tv.service;

import javax.tv.locator.Locator;
/*

This class represents permission to read the data referenced by a given
 <code>Locator</code>.

*/
public final class ReadPermission extends java.security.Permission implements java.io.Serializable {

/*
 
 Creates a new ReadPermission object for the specified locator. 
 Parameters:  locator - The locator. Null indicates permission for all locators. 
 
 
 */

private Locator locator;

public ReadPermission ( Locator locator){
   super("ReadPermission");
   this.locator=locator;
}


/*
 
 Creates a new ReadPermission object for a locator
 with the given external form. The actions string
 is currently unused and should be null . This
 constructor exists for use by the Policy object to
 instantiate new Permission objects. 
 Parameters:  locator - The external form of the locator. The string
 "*" indicates all locators. actions - Should be null . 
 
 */

public ReadPermission (java.lang.String locator,
           java.lang.String actions){
   super("ReadPermission");
   try {
   this.locator=javax.tv.locator.LocatorFactory.getInstance().createLocator(locator);
   } catch (javax.tv.locator.MalformedLocatorException ex) {
      ex.printStackTrace();
      this.locator=null;
   }
}


/*
 
 Checks if this ReadPermission object "implies" the specified
 permission. */

public boolean implies (java.security.Permission p){
   return true;
}


/*
 
 Checks two ReadPermission objects for equality. Checks that
 other is a ReadPermission, and has the same locator
 as this object. 
 Overrides:  equals in class java.security.Permission 
 
 
 Parameters:  other - the object we are testing for equality with this
 object. Returns:  true if other is of
 type ReadPermission and has the same locator as
 this ReadPermission object. 
 
 
 */

public boolean equals (java.lang.Object other){
   return other==this;
}


/*
 
 Returns the hash code value for this object. 
 Overrides:  hashCode in class java.security.Permission 
 
 
 Returns: A hash code value for this object. 
 
 
 */

public int hashCode () {
   return locator == null ? locator.hashCode() : 0;
}


/*
 
 Returns the canonical string representation of the actions,
 which currently is the empty string "", since there are no actions for
 a ReadPermission. 
 Overrides:  getActions in class java.security.Permission 
 
 
 Returns: the empty string "". 
 
 
*/

public java.lang.String getActions (){
   return "";
}



}

