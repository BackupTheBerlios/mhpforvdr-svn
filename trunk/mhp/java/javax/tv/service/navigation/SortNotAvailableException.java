
package javax.tv.service.navigation;

/*

This exception indicates that the requested sorting method is not
 available for the particular <code>ServiceList</code>, for example,
 sorting by service numbers.

*/
public class SortNotAvailableException extends javax.tv.service.SIException {

/*
 
 Constructs a SortNotAvailableException with no
 detail message. 
 */

public SortNotAvailableException (){
   super();
}


/*
 
 Constructs a SortNotAvailableException with a
 detail message. 
 Parameters:  reason - The reason this exception was thrown. 
 
 */

public SortNotAvailableException (java.lang.String reason){
   super(reason);
}



}

