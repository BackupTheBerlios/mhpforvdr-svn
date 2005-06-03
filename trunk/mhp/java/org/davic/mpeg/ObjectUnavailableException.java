
package org.davic.mpeg;

/*This exception must be thrown by MPEG related APIs when an object is not 
available. */

public class ObjectUnavailableException extends Exception {

/*
Constructs a ObjectUnavailableException with no detail message */
public ObjectUnavailableException() {
}

/*
Constructs a ObjectUnavailableException with the specified detail message Parameters: s - the detail 
message */
public ObjectUnavailableException(String s) {
   super(s);
}


}
