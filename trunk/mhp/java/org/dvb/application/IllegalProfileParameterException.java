
package org.dvb.application;

/*The IllegalProfileParameter exception is thrown if the application attempts to ask for a 
version number for a pro  le not speci  ed for the application. */

public class IllegalProfileParameterException extends java.lang.Exception {

/*
Construct a IllegalPro  leParameterException with no detail message */
public IllegalProfileParameterException() {
}

/*
Construct a IllegalPro  leParameterException with a detail message Parameters: s -detail 
message */
public IllegalProfileParameterException(java.lang.String s) {
   super(s);
}


}
