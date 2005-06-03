
package org.dvb.application;

/*An AppProxy Object is a proxy to an application.A call to the start,stop or pause will 
cause the resident Application Manager to respectively start,stop or pause the application 
bound to this AppProxy object. Each of these three method calls can throw a Security 
Exception if the calling application is not entitled to do so. Each of these method call 
is asynchronous and will result in exactly one AppStateChangedEvent to be generated 
whether the method call was successful or not.If the method call was not successful,any 
call to the hasFailed method of the corresponding AppStateChangeEvent will return true. 
Some of the methods here allow the AppProxy to transition through several states before 
the  nal state is reached.If this compound state transition is unsuccessful at any 
point,the resulting AppStateChangedEvent shall have a fromstate and a tostate which are 
both the last state in this transition which the AppProxy successfully entered.For 
instance,if an application were to call start on an AppProxy for a DVB-J application in 
the NOT_LOADED state and that DVB-J application was to throw a XletStateChangeException 
from its startXlet method,both the getFromState and getToState will return PAUSED. Calling 
the start method for an application which is already running shall fail and generate an 
AppStateChangeEvent with hasFailed returning true and both fromstate and tostate being 
STARTEDSee the de  nition of AppStateChangeEvent for more 
information. */

public interface AppProxy {

/*
The application has not yet been loaded from the network at all. */
public static final int NOT_LOADED = 2;


/*
The application is in the active state. */
public static final int STARTED = 0;


/*
The application is in the paused state. */
public static final int PAUSED = 3;


/*
The application is in the destroyed state. */
public static final int DESTROYED = 1;


/*
Add a listener to the application proxy so that an application can be informed if the application changes state. 
Parameters: listener -the listener to be added. */
public void addAppStateChangeEventListener(AppStateChangeEventListener listener);


/*
Return the current state of the application. Returns: the state of the application. */
public int getState();


/*
Request that the application manager pause the application bound to this information structure. The application will be 
paused.Calls to this method shall fail if the application is not in the active state.If the application represented by 
this AppProxy is a DVB-J application,calling this method will, if successful,result in the pauseXlet method being called 
on the Xlet making up the DVB-J application. Throws: SecurityException -if the application is not entitled to pause this 
application.Note that if an application is entitled to stop an application,it is also entitled to pause it:having the 
right to stop an application is logically equivalent to having the right to pause 
it. */
public void pause();


/*
Remove a listener on the database. Parameters: listener -the listener to be 
removed. */
public void removeAppStateChangeEventListener(AppStateChangeEventListener listener);


/*
Request that the application manager resume the execution of the application.The application will be started.This method 
will throw a security exception if the application does not have the authority to resume the application.Calls to this 
method shall fail if the application is not in the paused state. This method is asynchronous and its completion will be 
noti  ed by an AppStateChangedEvent.In case of failure,the hasFailed method of the AppStateChangedEvent will return 
true.If the application represented by this AppProxy is a DVB-J application,calling this method will,if 
successful,result in the startXlet method being called on the Xlet making up the DVB-J application. Throws: 
SecurityException -if the application is not entitled to resume this application. */
public void resume();


/*
Request that the application manager start the application bound to this information structure. The application will be 
started.This method will throw a security exception if the application does not have the authority to start 
applications.Calls to this method shall only succeed if the application is in the not loaded or paused states.If the 
application was not loaded at the moment of this call, then the application will be started.In the case of a DVB-J 
application,it will be initialized and then started by the Application Manager,hence causing the Xlet to go from 
NotLoaded to Paused and then from Paused to Active.If the application was in the Paused state at the moment of the call 
and had never been in the Active state,then the application will be started.If the application represented by this 
AppProxy is a DVB-J application,calling this method will,if successful,result in the startXlet method being called on 
the Xlet making up the DVB-J application. This method is asynchronous and its completion will be noti  ed by an 
AppStateChangedEvent.In case of failure,the hasFailed method of the AppStateChangedEvent will return 
true. */
public void start();


/*
Request that the application manager start the application bound to this information structure passing to that 
application the speci  ed parameters.The application will be started.This method will throw a security exception if the 
application does not have the authority to start applications.Calls to this method shall only succeed if the application 
is in the not loaded or paused states.If the application was not loaded at the moment of this call, then the application 
will be started.In the case of a DVB-J application,it will be initialized and then started by the Application 
Manager,hence causing the Xlet to go from NotLoaded to Paused and then from Paused to Active.If the application was in 
the Paused state at the moment of the call and had never been in the Active state,then the application will be 
started.If the application represented by this AppProxy is a DVB-J application,calling this method will,if 
successful,result in the startXlet method being called on the Xlet making up the DVB-J application. This method is 
asynchronous and its completion will be noti  ed by an AppStateChangedEvent.In case of failure,the hasFailed method of 
the AppStateChangedEvent will return true. Parameters: args -the parameters to be passed into the application being 
started Throws: SecurityException -if the application is not entitled to start this 
application. */
public void start(java.lang.String[] args);


/*
Request that the application manager stop the application bound to this information structure. The application will be 
stopped.A call to this method shall fail if the application was already in the destroyed state.This method call will 
stop the application if it was in any other state before the call.If the application is in the NOT_LOADED state then it 
shall move directly to the DESTROYED state with no other action being taken.If the application represented by this 
AppProxy is a DVB-J application and is not in the DESTROYED state then calling this method will,if successful,result in 
the destroyXlet method being called on the Xlet making up the DVB-J application with the same value for the parameter as 
passed to this method. This method is asynchronous and its completion will be noti  ed by an AppStateChangedEvent.In 
case of failure,the hasFailed method of the AppStateChangedEvent will return true. Parameters: forced -if true then do 
not ask the application but forcibly terminate it,if false give the application an opportunity to refuse. Throws: 
SecurityException -if the application is not entitled to stop this application. */
public void stop(boolean forced);



}
