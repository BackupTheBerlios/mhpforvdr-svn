
package javax.tv.service;

/*

This interface is used to identify services by service (or channel)
 numbers. The service number may represent a receiver-specific service
 designation or a broadcaster-specific service designation delivered as a
 private descriptor. <p>

 Service and ServiceDetails objects may optionally implement this
 interface. <code>ServiceNumber</code> is extended by
 <code>ServiceMinorNumber</code> to report two-part ATSC channel numbers.

*/
public interface ServiceNumber {

/*
 
 Reports the service number of a service. 
 Returns: The number of the service. 
 
 
*/

public int getServiceNumber ();



}

