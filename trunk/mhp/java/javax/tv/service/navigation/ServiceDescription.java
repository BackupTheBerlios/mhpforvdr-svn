
package javax.tv.service.navigation;

/*

This interface provides a textual description of a
 <code>Service</code>.
 (In ATSC PSIP, this information is obtained from the ETT
 associated with this service.)

*/
public interface ServiceDescription extends javax.tv.service.SIRetrievable {

/*
 
 Provides a textual description of the Service . 
 
 
 
 Returns: A textual description of the Service , or
 an empty string if no description is available. 
 
 
*/

public java.lang.String getServiceDescription ();



}

