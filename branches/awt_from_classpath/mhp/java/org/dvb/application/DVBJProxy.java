
package org.dvb.application;

/*A DVBJProxy Object is a proxy to a DVBJ application. */

public interface DVBJProxy extends AppProxy {

/*
The application is in the loaded state. */
public static final int LOADED = 5;


/*
Requests the application manager calls the initXlet method on the application. This method is asynchronous and its 
completion will be noti  ed by an AppStateChangedEvent.In case of failure,the hasFailed method of the 
AppStateChangedEvent will return true.Calls to this method shall only succeed if the application is in the NOT_LOADED or 
LOADED states.If the application is in the NOT_LOADED state,the application will move through the LOADED state into the 
PAUSED state before calls to this method complete. In all cases,an AppStateChangeEvent will be sent,whether the call was 
successful or not. Throws: SecurityException -if the application is not entitled to load this application.being able to 
load an application requires to be entitled to start it. */
public void init();


/*
Provides a hint to preload at least the initial class of the application into local storage,resources permitting.This 
does not require loading of classes into the virtual machine or creation of a new logical virtual machine which are 
implications of the init method.This method is asynchronous and its completion will be noti  ed by an 
AppStateChangedEvent In case of failure,the hasFailed method of the AppStateChangedEvent will return true.Calls to this 
method shall only succeed if the application is in the NOT_LOADED state.In all cases,an AppStateChangeEvent will be 
sent,whether the call was successful or not. Throws: SecurityException -if the application is not entitled to load this 
application.being able to load an application requires to be entitled to start 
it. */
public void load();



}
