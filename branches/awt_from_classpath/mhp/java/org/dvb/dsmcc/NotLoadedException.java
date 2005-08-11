
package org.dvb.dsmcc;

/*A NotLoadedException is thrown when the Stream object constructor is called with a DSMCC 
Object which is not loaded. */

public class NotLoadedException extends DSMCCException {

/*
Construct a NotLoadedException with no detail message */
public NotLoadedException() {
   super();
}

/*
Construct a NotLoadedException with the speci ed detail message Parameters: s - the detail 
message */
public NotLoadedException(java.lang.String s) {
   super(s);
}


}
