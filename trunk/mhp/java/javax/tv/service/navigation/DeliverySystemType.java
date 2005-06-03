
package javax.tv.service.navigation;

/*

This class represents values of various types of delivery systems,
 for example, satellite, cable, etc.

*/
public class DeliverySystemType extends java.lang.Object {

/*
 
 Satellite delivery system type. 
 */

public static final DeliverySystemType  SATELLITE = new DeliverySystemType("SATELLITE");


/*
 
 Cable delivery system type. 
 */

public static final DeliverySystemType  CABLE = new DeliverySystemType("CABLE");


/*
 
 Terrestrial delivery system type. 
 */

public static final DeliverySystemType  TERRESTRIAL = new DeliverySystemType("TERRESTRIAL");


/*
 
 Unknown delivery system type. */

public static final DeliverySystemType  UNKNOWN = new DeliverySystemType("UNKNOWN");


/*
 
 Creates a delivery system type object. 
 Parameters:  name - The string name of this type (e.g., "SATELLITE"). 
 
 */
 
 String name;

protected DeliverySystemType (java.lang.String name){
   this.name=name;
}


/*
 
 Provides the string name of delivery system type. For the type
 objects defined in this class, the string name will be identical
 to the class variable name. 
 Overrides:  toString in class java.lang.Object 
 
 
 Returns: The string name of the delivery system type. 
 
 
*/

public java.lang.String toString (){
   return name;
}



}

