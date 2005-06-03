
package org.dvb.dsmcc;

/*This Exception is thrown when the user is not entitled to access the content of the object 
(the Elementary Stream is scrambled and the user is not 
entitled). */

public class NotEntitledException extends DSMCCException {

/*
construct a NotEntitledException with no detail message */
public NotEntitledException() {
   super();
}

/*
construct a NotEntitledException with a detail message Parameters: s - detail 
message */
public NotEntitledException(java.lang.String s) {
   super(s);
}


}
