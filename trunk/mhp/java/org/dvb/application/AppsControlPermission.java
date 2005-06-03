
package org.dvb.application;

/*This class represents a Permission to control the lifecycle of another 
application. */

//TODO: This class is substantially unimplemented.

public final class AppsControlPermission extends java.security.BasicPermission {

/*
Creates a new AppsControlPermission.There is a simple mapping between the Application control Permission requests and 
the way the AppsControlPermission are granted.This mapping is de  ned in the main body of this speci  
cation. */
public AppsControlPermission() {
   super("AppsControlPermission");
}

/*
Creates a new AppsControlPermission.There is a simple mapping between the Application control Permission requests and 
the way the AppsControlPermission are granted.This mapping is de  ned in the main body of this speci  cation.The actions 
string is currently unused and should be null.The name string is currently unused and should be empty.This constructor 
exists for use by the java.security.Policy object to instantiate new permission objects. Parameters: name -the name of 
the permission actions -the actions string */
public AppsControlPermission(java.lang.String name, java.lang.String actions) {
   super(name, actions);
}

/*
Checks for equality against this AppsControlPermission object. Overrides: java.security.BasicPermission.equals(java.lang.Object)in 
class java.security.BasicPermission Parameters: obj -the object to test for equality with this AppsControlPermission 
object. Returns: true if obj is an AppsControlPermission */
public boolean equals(java.lang.Object obj) {
   return super.equals(obj);
}

/*
Returns the list of actions that had been passed to the constructor -it shall return null. Overrides: 
java.security.BasicPermission.getActions()in class java.security.BasicPermission Returns: a null 
String. */
public java.lang.String getActions() {
   return super.getActions();
}

/*
Returns the hash code value for this object. Overrides: java.security.BasicPermission.hashCode()in class 
java.security.BasicPermission Returns: the hash code value for this object. */
public int hashCode() {
   return super.hashCode();
}

/*
Checks if this AppsControlPermission object "implies"the speci  ed permission. Overrides: 
java.security.BasicPermission.implies(java.security.Permission)in class java.security.BasicPermission Parameters: 
permission -the speci  ed permission to check. Returns: true if and only if the speci  ed permission is an instanceof 
AppsControlPermission,false otherwise. */
public boolean implies(java.security.Permission permission) {
   //this constitutes a security hole
   return true;
}


}
