
package javax.tv.service.selection;

/*

The base class for exceptions related to service contexts.

*/
public class ServiceContextException extends java.lang.Exception {

/*
 
 Constructs a ServiceContextException with no detail message. 
 */

public ServiceContextException (){
   super();
}


/*
 
 Constructs a ServiceContextException with a detail message. 
 Parameters:  reason - The reason this exception was thrown. 
 
 */

public ServiceContextException (java.lang.String reason){
   super(reason);
}



}

