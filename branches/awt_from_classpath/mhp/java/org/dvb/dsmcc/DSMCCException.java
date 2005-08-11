
package org.dvb.dsmcc;

/*The DSMCCException is the root class of all DSMCC related 
exceptions */

public class DSMCCException extends java.io.IOException {

/*
Construct a DSMCCException with no detail message */
public DSMCCException() {
   super();
}

/*
Construct a DSMCCException with the speci ed detail message.Parameters: s - the detail 
message */
public DSMCCException(java.lang.String s) {
   super(s);
}


}
