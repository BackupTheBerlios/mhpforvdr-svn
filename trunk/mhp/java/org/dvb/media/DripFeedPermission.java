
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


}
