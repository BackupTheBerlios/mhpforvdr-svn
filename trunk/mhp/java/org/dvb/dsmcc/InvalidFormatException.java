
package org.dvb.dsmcc;

/*An InvalidFormatException is thrown when an inconsistent DSMCC message is 
received. */

public class InvalidFormatException extends DSMCCException {

/*
Construct an InvalidFormatException with no detail message */
public InvalidFormatException() {
   super();
}

/*
Construct an InvalidFormatException with the speci ed detail message Parameters: s - the detail 
message */
public InvalidFormatException(java.lang.String s) {
   super(s);
}


}
