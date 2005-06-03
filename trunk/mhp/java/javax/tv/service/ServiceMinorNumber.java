
package javax.tv.service;

/*

This interface extends the basic <code>ServiceNumber</code> interface to
 provide the minor number of two-part service numbers described in
 <em>major.minor</em> format. <p>

 Service and ServiceDetails objects may optionally implement this
 interface. <p>

 The major number of a service is obtained from the
 <code>ServiceNumber.getServiceNumber</code> method.

*/
public interface ServiceMinorNumber extends ServiceNumber {

/*
 
 Reports the minor number of the service. 
 
 
 
 Returns: The minor number of this service. 
 
 
*/

public int getMinorNumber ();



}

