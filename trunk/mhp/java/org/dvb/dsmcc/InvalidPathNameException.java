
package org.dvb.dsmcc;

/*The InvalidPathNameException is thrown when the path name to a DSMCCObject does not exist 
or if the ServiceDomain has been detached. */

public class InvalidPathNameException extends DSMCCException {

/*
Construct an InvalidPathNameException with no detail message */
public InvalidPathNameException() {
   super();
}

/*
Construct an InvalidPathNameException with the speci ed detail message Parameters: s - the detail 
message */
public InvalidPathNameException(java.lang.String s) {
   super(s);
}


}
