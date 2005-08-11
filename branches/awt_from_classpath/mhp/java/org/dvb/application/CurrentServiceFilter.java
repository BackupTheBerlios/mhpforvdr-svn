
package org.dvb.application;

/*Instances of CurrentServiceFilter are used to set a  lter on the list of applications that 
are retrieved from the AppsDatabase (See methods getAppsAttributes and getAppsIDs) For 
this version of the speci  cation,only the CurrentServiceFilter class is de  ned A 
CurrentServiceFilter is used to indicate that only broadcast applications that are 
signalled in one of the AITs of the current service shall be returned by the 
getAppsAttributes and getAppIDs methods of AppsDatabase.Subclasses of CurrentServiceFilter 
can override the accept method so as to implement their own  lter criteria on the AppID's 
values. */

//TODO: Implement

public class CurrentServiceFilter extends AppsDatabaseFilter {

/*
public Constructor of the CurrentServiceFilter */
public CurrentServiceFilter() {
}

/*
Test if a speci  ed appid should be included in the Enumeration. Overrides: accept(AppID)in class AppsDatabaseFilter 
Parameters: appid -the speci  ed appid to test. Returns: true if the application with identi  er appid should be 
listed,false otherwise. */
public boolean accept(AppID appid) {
   return true;
}


}
