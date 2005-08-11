
package org.dvb.dsmcc;

/*A NothingToAbortException is thrown when the abort method is called and there is no 
loading in progress. */

public class NothingToAbortException extends DSMCCException {

/*
Construct a NothingToAbortException with no detail message */
public NothingToAbortException() {
   super();
}

/*
Construct a NothingToAbortException with the speci ed detail message Parameters: s - the detail 
message */
public NothingToAbortException(java.lang.String s) {
   super(s);
}


}
