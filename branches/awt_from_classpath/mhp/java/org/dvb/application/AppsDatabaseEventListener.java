
package org.dvb.application;

/*The AppsDatabaseListener class allows an application to monitor the application database 
so that it can keep an up to date interface without polling the state.The application 
shall receive these events in a timely fashion after the AIT changes,however it is system 
dependant how often the AIT table is checked. */

public interface AppsDatabaseEventListener extends java.util.EventListener {

/*
The AppsDataBase has had an application entry added. Parameters: evt -the AppsDatabaseEvent. */
public void entryAdded(AppsDatabaseEvent evt);


/*
The AppsDataBase has had an application entry changed. Parameters: evt -the 
AppsDatabaseEvent. */
public void entryChanged(AppsDatabaseEvent evt);


/*
The AppsDataBase has had an application entry removed. Parameters: evt -the 
AppsDatabaseEvent. */
public void entryRemoved(AppsDatabaseEvent evt);


/*
The AppsDataBase has radically changed. Parameters: evt -the AppsDatabaseEvent. */
public void newDatabase(AppsDatabaseEvent evt);



}
