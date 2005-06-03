
package org.dvb.dsmcc;

/*This exception gets thrown when a request to subscribe to a stream event cannot be 
completed due to resource limitations. For example, no section  lters or system timers may 
be available. This exception will not get thrown when there is not enough memory available 
to complete the request - this will get signalled by a java.lang.OutOfMemoryError */

public class InsufficientResourcesException extends DSMCCException {

/*
Construct an Insuf cientResourcesException with no detail message */
public InsufficientResourcesException() {
   super();
}

/*
Construct an Insuf cientResourcesException with the speci ed detail message Parameters: message - the message for the 
exception */
public InsufficientResourcesException(java.lang.String s) {
   super(s);
}


}
