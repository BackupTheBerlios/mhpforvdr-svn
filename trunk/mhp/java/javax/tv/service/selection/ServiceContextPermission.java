
package javax.tv.service.selection;

/*

<code>ServiceContextPermission</code> represents permission to
 control a <code>ServiceContext</code>.  A
 <code>ServiceContextPermission</code> contains a name (also
 referred to as a "target name") and an actions string.

 <p> The target name is the name of the service context permission
 (see the table below).  Each permission identifies a method.  A
 wildcard match is signified by an asterisk, i.e., "*".

 <p><a name="actions"></a> The actions string is either "own" or
 "*".  From a security standpoint, a caller is said to "own" a
 <code>ServiceContext</code> instance if it was acquired through
 <A HREF="../../../../javax/tv/service/selection/ServiceContextFactory.html#createServiceContext()"><CODE>ServiceContextFactory.createServiceContext()</CODE></A> or <A HREF="../../../../javax/tv/service/selection/ServiceContextFactory.html#getServiceContext(javax.tv.xlet.XletContext)"><CODE>ServiceContextFactory.getServiceContext(javax.tv.xlet.XletContext)</CODE></A>.  The string "own" means
 the permission applies to your own service contexts; the string "*"
 implies permission to these, plus permission for service contexts
 obtained from all other sources.

 <p> The following table lists all the possible
 <code>ServiceContextPermission</code> target names, and describes
 what the permission allows for each.  <p>

 <table border=1 cellpadding=5>
 <tr>
 <th>Permission Target Name</th>
 <th>What the Permission Allows</th>
 </tr>

 <tr>
    <td>access</td>
    <td>Access to a <code>ServiceContext</code>, via <code>ServiceContextFactory.getServiceContexts()</code></td>
 </tr>
 
 <tr>
    <td>create</td>
    <td>Creation of a <code>ServiceContext</code>.</td>
 </tr>

 <tr>
    <td>destroy</td>
    <td>Destruction of a <code>ServiceContext</code>.</td>
 </tr>

 <tr>
    <td>getServiceContentHandlers</td>
    <td>Obtaining the service content handlers from a <code>ServiceContext</code>.</td>
 </tr>

 <tr>
    <td>stop</td>
    <td>Stopping a <code>ServiceContext</code>.</td>
 </tr>

 </table>

 <p>
 The permission ServiceContextPermission("access", "*") is intended
 to be granted only to special monitoring applications and not to
 general broadcast applications.<p>
 
 Note that undefined target and actions strings may be provided to
 the constructors of this class, but subsequent calls to
 <code>SecurityManager.checkPermission()</code> with the resulting
 <code>SelectPermission</code> object will fail.

*/
public final class ServiceContextPermission extends java.security.BasicPermission {

/*
 
 Creates a new ServiceContextPermission object with the specified
 name. The name is the symbolic name of the permission, such as
 "create". An asterisk may be used to signify a wildcard match. 
 Parameters:  name - The name of the ServiceContextPermission  actions - The actions string, as
 detailed in the class description . 
 
 */

String actions;
 
public ServiceContextPermission (java.lang.String name,
                java.lang.String actions){
   super(name);
   if (actions.equals("own") || actions.toLowerCase().equals("own"))
      this.actions = "own";
   else if (actions.equals("*"))
      this.actions = "*";
   else
      //throw new IllegalArgumentException("illegal action "+actions);
      this.actions = "invalid";
}


/*
 
 Checks if the specified permission is "implied" by this object. */

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
 
 Tests two ServiceContextPermission objects for
 equality. Returns true if and only if
 obj 's class is the same as the class of this
 object, and obj has the same name and actions
 string as this object. 
 Overrides:  equals in class java.security.BasicPermission 
 
 
 Parameters:  obj - The object to test for equality. Returns:  true if the two permissions are equal;
 false otherwise. 
 
 
 */

public boolean equals (java.lang.Object obj){
   return super.equals(obj) && getActions().equals(((ServiceContextPermission)obj).getActions());
}


/*
 
 Provides the hash code value of this object. Two
 ServiceContextPermission objects that are equal will
 return the same hash code. 
 Overrides:  hashCode in class java.security.BasicPermission 
 
 
 Returns: The hash code value of this object. 
 
 
 */

public int hashCode (){
   return actions.hashCode()+getName().hashCode();
}


/*
 
 Returns the canonical representation of the actions string. 
 Overrides:  getActions in class java.security.BasicPermission 
 
 
 Returns: The actions string of this permission. 
 
 
*/

public java.lang.String getActions (){
   return actions;
}



}

