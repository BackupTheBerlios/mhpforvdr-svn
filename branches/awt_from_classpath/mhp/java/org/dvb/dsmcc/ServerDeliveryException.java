
package org.dvb.dsmcc;

/*A ServerDeliveryException is thrown when the local machine can not communicate with the 
server. This exception is only used with  les implemented by delivery over a 
bi-directional IP connection. For the object carousel the MPEGDeliveryException is used 
instead. */

public class ServerDeliveryException extends DSMCCException {

/*
Construct a ServerDeliveryException with no detail message */
public ServerDeliveryException() {
   super();
}

/*
Construct a ServerDeliveryException with the speci ed detail message Parameters: s - the detail 
message */
public ServerDeliveryException(java.lang.String s) {
   super(s);
}


}
