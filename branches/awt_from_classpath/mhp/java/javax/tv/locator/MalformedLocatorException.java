
package javax.tv.locator;

/*

This exception is thrown to indicate that a malformed locator
  string has been used.  Either no legal mapping could be determined
  for the specified string, or the string could not be parsed.

*/
public class MalformedLocatorException extends java.lang.Exception {

/*
 
 Constructs a MalformedLocatorException with no
 detail message. 
 */

public MalformedLocatorException (){
}


/*
 
 Constructs a MalformedLocatorException with the
 specified detail message. 
 Parameters:  reason - The reason the exception was raised. 
 
 */

public MalformedLocatorException (java.lang.String reason){
}



}

