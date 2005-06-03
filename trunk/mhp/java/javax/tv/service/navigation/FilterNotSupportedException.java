
package javax.tv.service.navigation;

/*

This exception indicates that the specified <code>ServiceFilter</code> is
 not supported.

*/
public class FilterNotSupportedException extends javax.tv.service.SIException {

/*
 
 Constructs a FilterNotSupportedException with no
 detail message. 
 */

public FilterNotSupportedException (){
   super();
}


/*
 
 Constructs a FilterNotSupportedException with a
 detail message. 
 Parameters:  reason - The reason why this exception was thrown. 
 
 */

public FilterNotSupportedException (java.lang.String reason){
   super(reason);
}



}

