
package javax.tv.service;

/*

The <code>Service</code> interface represents an abstract view on
 what is generally referred to as a television "service" or
 "channel". It may represent an MPEG-2 program, DVB service, an ATSC
 virtual channel, SCTE virtual channel, etc. It represents the basic
 information associated with a service, such as its name or number,
 which is guaranteed to be available on the receiver. 
*/
public interface Service {

/*
 
 This method retrieves additional information about the
 Service . This information is retrieved from the
 broadcast service information. */

public SIRequest  retrieveDetails ( SIRequestor requestor);


/*
 
 Returns a short service name or acronym. For example, in ATSC
 systems the service name is provided by the the PSIP VCT; in DVB
 systems, this information is provided by the DVB Service
 Descriptor or the Multilingual Service Name Descriptor. The
 service name may also be user-defined. 
 Returns: A string representing this service's short name. If the
 short name is unavailable, the string representation of the
 service number is returned. 
 
 
 */

public java.lang.String getName ();


/*
 
 This method indicates whether the service represented by this
 Service object is available on multiple
 transports, (e.g., the same content delivered over terrestrial and
 cable network). 
 Returns:  true if multiple transports carry the same
 content identified by this Service object;
 false if there is only one instance of this service. 
 
 
 */

public boolean hasMultipleInstances ();


/*
 
 Returns the type of this service, (for example, "digital
 television", "digital radio", "NVOD", etc.) These values can be
 mapped to the ATSC service type in the VCT table and the DVB
 service type in the service descriptor. 
 Returns: Service type of this Service . 
 
 
 */

public ServiceType  getServiceType ();


/*
 
 Reports the Locator of this Service .
 Note that if the resulting locator is transport-dependent, it
 will also correspond to a ServiceDetails object. 
 Returns: A locator referencing this Service . See Also:   ServiceDetails  
 
 
 */

public javax.tv.locator.Locator  getLocator ();


/*
 
 Tests two Service objects for equality. Returns
 true if and only if:
 
  obj 's class is the
 same as the class of this Service , and 
  obj 's Locator is equal to
 the Locator of this Service 
 (as reported by
 Service.getLocator() , and 
  obj and this object encapsulate identical data.
  
 Overrides:  equals in class java.lang.Object 
 
 
 Parameters:  obj - The object against which to test for equality. Returns:  true if the two Service objects
 are equal; false otherwise. 
 
 
 */

public boolean equals (java.lang.Object obj);


/*
 
 Reports the hash code value of this Service . Two
 Service objects that are equal will have identical
 hash codes. 
 Overrides:  hashCode in class java.lang.Object 
 
 
 Returns: The hash code value of this Service . 
 
 
*/

public int hashCode ();



}

