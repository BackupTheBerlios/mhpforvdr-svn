
package org.dvb.application;

/*The LanguageNotAvailableException exception is thrown if the application asks for the name 
of an application in a language not signalled in the AIT. */

public class LanguageNotAvailableException extends java.lang.Exception {

/*
Construct a LanguageNotAvailableException with no detail message */
public LanguageNotAvailableException() {
}

/*
Construct a LanguageNotAvailableException with a detail message Parameters: s -detail 
message */
public LanguageNotAvailableException(java.lang.String s) {
   super(s);
}


}
