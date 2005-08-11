
package javax.tv.service.selection;

import javax.tv.locator.Locator;
import java.security.Permission;
import java.security.BasicPermission;

/*

<code>SelectPermission</code> represents permission to perform a
 <code>select()</code> operation on a <code>ServiceContext</code>.
 A caller might have permission to select some content but not
 others.

 <p>
 <a name="actions"></a>
 The <code>actions</code> string is either "own" or "*".  The
 string "own" means the permission applies to your own service
 context, acquired via
 <code>ServiceContextFactory.createServiceContext()</code> or
 <code>ServiceContextFactory.getServiceContext(javax.tv.xlet.XletContext)</code>.
 The string "*" implies permission to these, plus permission for service
 contexts obtained from all other sources.<p>

 Note that undefined actions strings may be provided to the
 constructors of this class, but subsequent calls to
 <code>SecurityManager.checkPermission()</code> with the resulting
 <code>SelectPermission</code> object will fail.

*/

//Spec says Permission, for ease of implementation derive from BasicPermission
public final class SelectPermission extends /*java.security.Permission*/BasicPermission implements java.io.Serializable {

/*
 
 Creates a new SelectPermission object for the specified locator. 
 Parameters:  locator - The locator. A value of null 
 indicates permission for all locators. actions - The actions string, as
 detailed in the class description . 
 
 
 */

String actions;
 
public SelectPermission ( Locator locator,
            java.lang.String actions){
   this(locator == null ? "*" : locator.toExternalForm(), actions);
}


/*
 
 Creates a new SelectPermission object for a locator with the
 given external form. This constructor exists for use by the
 Policy object to instantiate new Permission objects. 
 Parameters:  locator - The external form of the locator. The string
 "*" indicates all locators. actions - The actions string, as
 detailed in the class description . 
 
 */

public SelectPermission (java.lang.String locator,
            java.lang.String actions){
   super(locator);
   
   if (actions.equals("own") || actions.toLowerCase().equals("own"))
      this.actions = "own";
   else if (actions.equals("*"))
      this.actions = "*";
   else
      //throw new IllegalArgumentException("illegal action "+actions);
      this.actions = "invalid";
   this.actions=actions;
}



/*
 
 Checks if this SelectPermission object "implies" the specified
 permission. More specifically, this method returns true if:
 
  p is an instance of SelectPermission, and
  p 's action string matches this object's, or this object has
	"*" as an action string, and
  p 's locator's external form matches this object's locator
  string, or this object's locator string is "*".
  
 Overrides:  implies in class java.security.Permission 
 
 
 Parameters:  p - The permission against which to check. Returns:  true if the specified permission is
 implied by this object, false if not. 
 
 
 */

public boolean implies (java.security.Permission p){
   // BasicPermission checks for name and type.
   if (super.implies(p))
   {
      ServiceContextPermission o=(ServiceContextPermission)p;
      return (getActions().equals("*") || getActions().equals(o.getActions()) );
   }
   return false;
}


/*
 
 Checks two SelectPermission objects for equality. Tests that
 the given object is a SelectPermission and has the
 same Locator and actions string as this
 object. 
 Overrides:  equals in class java.security.Permission 
 
 
 Parameters:  other - The object to test for equality. Returns:  true if other is a
 SelectPermission and has the same locator and
 actions string as this
 SelectPermission object; false otherwise. 
 
 
 */

public boolean equals (java.lang.Object other){
   if (!(other instanceof SelectPermission))
      return false;
   SelectPermission o=(SelectPermission)other;
   return getActions().equals(o.getActions()) && getName().equals(o.getName());
}


/*
 
 Returns the hash code value for this object. 
 Overrides:  hashCode in class java.security.Permission 
 
 
 Returns: A hash code value for this object. 
 
 
 */

public int hashCode (){
   return actions.hashCode()+getName().hashCode();
}


/*
 
 Returns the canonical string representation of the actions. 
 Overrides:  getActions in class java.security.Permission 
 
 
 Returns: The canonical string representation of the actions. 
 
 
*/

public java.lang.String getActions (){
   return actions;
}



}

