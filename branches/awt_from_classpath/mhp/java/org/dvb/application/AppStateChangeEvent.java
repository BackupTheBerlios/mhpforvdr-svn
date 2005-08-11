
package org.dvb.application;

/*The AppStateChangeEvent class indicates a state transition of the application.If the state 
transition was requested by an application through this API,the method hasFailed indicates 
whether the state change failed or not.Where a state change succeeds,fromState and toState 
shall indicate the original and destination state of the transition.If it failed,fromState 
shall return the state the application was in before the state transition was requested 
and the toState method shall return the state the application would have been in if the 
state transition had succeeded. Attempting to start an application which is already 
running shall fail and generate an AppStateChangeEvent with hasFailed returning true and 
both fromstate and tostate being STARTED */

public class AppStateChangeEvent extends java.util.EventObject {

AppID appid;
int fromstate;
int tostate;
boolean hasFailed;
/*
Create an AppStateChangeEvent object. Parameters: appid -a registry entry representing the tracked application fromstate 
-the state the application was in before the state transition was requested,where the value of fromState is one of the 
state values de  ned in the AppProxy interface or in the interfaces inheriting from it tostate -state the application 
would be in if the state transition succeeds,where the value of toState is one of the state values de  ned in the 
AppProxy interface or in the interfaces inheriting from it hasFailed -an indication of whether the transition failed 
(true)or succeeded (false) source -the AppProxy where the state transition happened */
public AppStateChangeEvent(AppID _appid, int _fromstate, int _tostate, java.lang.Object _source, boolean 
_hasFailed) {
 super(_source);
 appid=_appid;
 fromstate=_fromstate;
 tostate=_tostate;
 hasFailed=_hasFailed;
}

/*
The application the listener was tracking has made a state transition from fromState to toState Returns: a registry 
entry representing the tracked application */
public AppID getAppID() {
   return appid;
}

/*
The application the listener is tracking was in fromState where the value of fromState is one of the state values de  
ned in the AppProxy interface or in the interfaces inheriting from it. Returns: the old 
state */
public int getFromState() {
   return fromstate;
}

/*
The application the listener is tracking is now in toState where the value of toState is one of the state values de  ned 
in the AppProxy interface or in the interfaces inheriting from it. Returns: the intended new 
state */
public int getToState() {
   return tostate;
}

/*
This method determines whether an attempt to change the state of an application has failed. Returns: true if the attempt 
to change the state of the application failed,false otherwise */
public boolean hasFailed() {
   return hasFailed;
}


}
