
package org.dvb.dsmcc;

/*An MPEGDEliveryException is thrown when an error (for instance, a time out or accessing 
the data would require tuning) occurs while loading data from an MPEG 
Stream. */

public class MPEGDeliveryException extends DSMCCException {

/*
Construct an MPEGDeliveryException with no detail message */
public MPEGDeliveryException() {
   super();
}

/*
Construct an MPEGDeliveryException with the speci ed detail message Parameters: s - the detail 
message */
public MPEGDeliveryException(java.lang.String s) {
   super(s);
}


}
