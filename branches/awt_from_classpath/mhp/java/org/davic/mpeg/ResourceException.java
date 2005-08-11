
package org.davic.mpeg;

/*This exception must be thrown by MPEG related APIs when an operation could not be 
performed due to lack of resources. */

public class ResourceException extends Exception {

/*
Constructs a ResourceException with no detail message */
public ResourceException() {
}

/*
Constructs a ResourceException with the specified detail message */
public ResourceException(String s) {
   super(s);
}


}
