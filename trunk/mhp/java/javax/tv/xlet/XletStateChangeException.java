
package javax.tv.xlet;

/*

Signals that a requested Xlet state change failed. This
 exception is thrown in response to state change calls
 in the <code>Xlet</code> interface.

*/
public class XletStateChangeException extends java.lang.Exception {

/*
 
 Constructs an exception with no specified detail message. 
 */

public XletStateChangeException (){
   super();
}


/*
 
 Constructs an exception with the specified detail message. 
 Parameters:  s - the detail message 
 
 */

public XletStateChangeException (java.lang.String s){
   super(s);
}



}

