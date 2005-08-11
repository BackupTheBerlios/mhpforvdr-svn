
package org.dvb.application;

/*Abstract class for the  lters.Instances of concrete classes that extend AppsDatabaseFilter 
are passed to the AppsDatabase.getAppAttributes and AppsDatabase.getAppIDs methods to 
allow an applications to set a  lter on the list of applications (respectively 
AppAttributes and AppIDs)that it wants to retrieve from the AppDatabase. For this version 
of the speci  cation,only one subclass is de  ned:CurrentServiceFilter */

public abstract class AppsDatabaseFilter {

/*
Construct an AppsDatabaseFilter object. */
public AppsDatabaseFilter() {
}

/*
Test if a speci  ed appid should be included in the Enumeration. Parameters: appid -the speci  ed appid to test. 
Returns: true if the application with identi  er appid should be listed,false 
otherwise. */
public abstract boolean accept(AppID appid);


}
