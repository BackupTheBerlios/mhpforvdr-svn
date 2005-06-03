
package org.dvb.dsmcc;

/*This Exception is thrown when the application attempted to create a DSMCCStream or 
DSMCCStreamEvent object with an object or a path that did not correspond to a DSMCC Stream 
or DSMCC StreamEvent respectively */

public class IllegalObjectTypeException extends DSMCCException {

/*
constructor of the exception with no detail message */
public IllegalObjectTypeException() {
   super();
}

/*
constructor of the exception Parameters: s - detail message */
public IllegalObjectTypeException(java.lang.String s) {
   super(s);
}


}
