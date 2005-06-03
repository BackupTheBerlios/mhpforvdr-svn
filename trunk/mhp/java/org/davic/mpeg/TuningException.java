
package org.davic.mpeg;

/* */

public class TuningException extends ResourceException {

/*
 */
public TuningException() {
}

/*
 */
public TuningException(String s) {
   super(s);
}

//simply access from within package
protected TransportStream transportStream = null;

public TransportStream getTransportStream() {
   return transportStream;
}

}
