
package org.dvb.dsmcc;

/*The UnknownEventException is thrown when a method tries to access to an unknown event. 
This exception may get thrown because the event in question is not being signalled yet. It 
does not indicate that the event is permanently unavailable. Applications may choose to 
attempt to subscribe to the event again at a later point in time in the expectation that 
the event has become available since the previous attempt. */

public class UnknownEventException extends DSMCCException {

/*
Construct an UnknownEventException with no detail message */
public UnknownEventException() {
   super();
}

/*
Construct an UnknownEventException with the speci ed detail message Parameters: s - the detail 
message */
public UnknownEventException(java.lang.String s) {
   super(s);
}


}
