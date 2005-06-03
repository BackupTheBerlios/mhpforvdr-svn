
package org.dvb.si;

/*This exception is thrown when a speci ed period is invalid (for example, start time is 
after the end time) */

public class SIInvalidPeriodException extends SIException {

/*
Default constructor for the exception */
public SIInvalidPeriodException() {
}

/*
Constructor for the exception with a speci ed reason Parameters: reason - the reason why the exception was 
raised */
public SIInvalidPeriodException(java.lang.String reason) {
}


}
