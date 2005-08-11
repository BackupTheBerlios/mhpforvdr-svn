
package org.davic.mpeg;

/*This class is thrown by MPEG related APIs when access is requested to information which is 
scrambled and to which access is not permitted by the security 
system. */

public class NotAuthorizedException extends Exception {

/*
Constructs a NotAuthorizedException with no detail message */
public NotAuthorizedException() {
}

/*
Constructs a NotAuthorizedException with the specified detail message Parameters: s - the detail 
message */
public NotAuthorizedException(String s) {
   super(s);
}


}
