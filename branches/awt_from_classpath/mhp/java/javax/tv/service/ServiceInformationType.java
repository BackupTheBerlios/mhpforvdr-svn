
package javax.tv.service;

/*

This class represents values of service information (SI) formats.

*/
public class ServiceInformationType extends java.lang.Object {

/*
 
 ATSC PSIP format. 
 */

public static final ServiceInformationType  ATSC_PSIP = new ServiceInformationType("ATSC_PSIP");


/*
 
 DVB SI format. 
 */

public static final ServiceInformationType  DVB_SI = new ServiceInformationType("DVB_SI");


/*
 
 SCTE SI format. 
 */

public static final ServiceInformationType  SCTE_SI = new ServiceInformationType("SCTE_SI");


/*
 
 Unknown format. */

public static final ServiceInformationType  UNKNOWN = new ServiceInformationType("UNKNOWN");

String name;
/*
 
 Creates a service information type object. 
 Parameters:  name - The string name of this type (e.g., "ATSC_PSIP"). 
 
 */

protected ServiceInformationType (java.lang.String name){
   this.name=name;
}


/*
 
 Provides the string name of the SI type. For the type objects
 defined in this class, the string name will be identical to the
 class variable name. 
 Overrides:  toString in class java.lang.Object 
 
 
 Returns: The string name of the SI type. 
 
 
*/

public java.lang.String toString (){
   return name;
}



}

