
package org.dvb.si;

/*This exception is thrown when one or more of the arguments passed to a method are invalid 
(e.g. numeric identi ers out of range, etc.) */

public class SIIllegalArgumentException extends SIException {

/*
Default constructor for the exception */
public SIIllegalArgumentException() {
}

/*
Constructor for the exception with a speci ed reason Parameters: reason - the reason why the exception was 
raised */
public SIIllegalArgumentException(java.lang.String reason) {
}


}
