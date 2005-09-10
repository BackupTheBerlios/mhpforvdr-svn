
package org.dvb.media;

/*This class represents a permission to access the drip feed 
mode. */

public class DripFeedPermission extends java.security.BasicPermission {

/*
Create a new DripFeedPermission. Parameters: name - the name string is currently unused and should be 
empty */
public DripFeedPermission(java.lang.String name) {
   super(name);
}

/*
Create a new DripFeedPermission. This constructor is used by the policy class to instantiate new permission objects. 
Parameters: name - The name string is currently unused and should be empty actions - The actions string is currently 
unused and should be null. */
public DripFeedPermission(java.lang.String name, java.lang.String actions) {
   super(name, actions);
}

/*
Checks if the speci?ed permission is implied by this object.
Since name and actions aren't used, the only check needed is whether p is also a
DripFeedPermission.
Overrides:
    implies in class BasicPermission
Parameters:
    p - the permission to check against.
Returns:
    true if the passed permission is equal to or implied by this permission, false
*/
public boolean implies(java.security.Permission p) {
   return p instanceof DripFeedPermission;
}

}
