
package javax.tv.service;

/*

This class represents type codes for failure of asynchronous SI
 retrieval requests.  It is subclassed to provide the individual
 type codes.

*/
public class SIRequestFailureType extends java.lang.Object {

/*
 
 The type generated when the SIRequest is canceled. 
 See Also:   SIRequest.cancel()  
 
 
 */

public static final SIRequestFailureType  CANCELED = new SIRequestFailureType("CANCELED");


/*
 
 The type generated when the resources required to fulfill an
 asynchronous SI retrieval (such as a tuner, section filter, etc.)
 are unavailable. The application may attempt to release some
 resources and attempt the request again. 
 */

public static final SIRequestFailureType  INSUFFICIENT_RESOURCES = new SIRequestFailureType("INSUFFICIENT_RESOURCES");


/*
 
 The type generated when the system cannot find the
 requested data. This occurs when the
 requested data is not present in the transport stream, when the
 data is present on some transport/network but the SI database
 does not know about it, or when the type of requested data is
 not supported by the broadcast environment. 
 */

public static final SIRequestFailureType  DATA_UNAVAILABLE = new SIRequestFailureType("DATA_UNAVAILABLE");


/*
 
 The type for the failure is unknown. */

public static final SIRequestFailureType  UNKNOWN = new SIRequestFailureType("UNKNOWN");


/*
 
 Creates an SIRequestFailureType object. 
 Parameters:  name - The string name of this type (e.g., "CANCELED"). 
 
 */
 
String name;

protected SIRequestFailureType (java.lang.String name){
   this.name=name;
}


/*
 
 Provides the string name of the type. For the type objects
 defined in this class, the string name will be identical to the
 class variable name. 
 Overrides:  toString in class java.lang.Object 
 
 
 Returns: The string name of the type. 
 
 
*/

public java.lang.String toString (){
   return name;
}



}

