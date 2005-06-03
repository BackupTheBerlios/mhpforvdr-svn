
package org.dvb.application;

/*The AppIcon encapsulates the information concerning the icon attached to the 
application */


//TODO: Icon loading is unimplemented.

public class AppIcon {


/*
The constructor for the class.This constructor is intended for implementation convenience and evolution of the speci  
cation and not for use by MHP applications.Applications should obtain instances of this class from 
AppAttributes.getAppIcon */
public AppIcon() {
}

/*
This method returns the  ags identifying which icons are provided for the application. Returns: the icon  ags encoded as 
a BitSet */
public java.util.BitSet getIconFlags() {
   return new java.util.BitSet();
}

/*
This method returns the location of the directory containing the application icons. Returns: the location of the 
directory containing the application icons. */
public org.davic.net.Locator getLocator() {
   System.err.println("AppIcon.getLocator(): Returning null!");
   return null;
}


}
