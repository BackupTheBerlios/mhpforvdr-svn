
package org.dvb.application;

/*The AppStateChangeEventListener class allows a launcher application to keep track of 
applications it launches or other applications running as part of the same 
service. */

public interface AppStateChangeEventListener extends java.util.EventListener {

/*
The application the listener was tracking has made a state transition from fromState to toState and this method will be 
given the state event. Parameters: evt -the AppStateChangeEvent. */
public void stateChange(AppStateChangeEvent evt);



}
