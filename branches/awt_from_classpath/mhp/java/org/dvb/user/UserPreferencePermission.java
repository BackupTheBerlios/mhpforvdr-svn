package org.dvb.user;

//taken from OpenMHP, license is LGPL

/**
* @author tejopa
* @date 7.3.2004
* @status fully implemented
* @module internal
* @HOME
*/

/*This class is for user preference and setting permissions. A UserPreferencePermission contains a name, but no actions list. The permission name can either be "read" or "write". The "read" permission allows an application to read the user preferences and settings (using UserPreferenceManager.read) for which read access is not always granted. Access to the following settings/preferences is always granted: "User Language", "Parental Rating", "Default Font Size" and "Country Code" The "write" permission allows an application to modify user preferences and settings (using UserPreferenceManager.write).*/

public class UserPreferencePermission extends java.security.BasicPermission {

   public UserPreferencePermission(String name){
      super(name);
   }

   public UserPreferencePermission(String name, String actions){
      super(name,actions);
   }

}

