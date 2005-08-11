
package org.dvb.dsmcc;

/*An InvalidAddressException is thrown when the format of an NSAP address is not 
recognized. */

public class InvalidAddressException extends DSMCCException {

/*
Construct a InvalidAddressException with no detail message */
public InvalidAddressException() {
   super();
}

/*
Construct a InvalidAddressException with the speci ed detail message Parameters: s - the detail 
message */
public InvalidAddressException(java.lang.String s) {
   super(s);
}


}
