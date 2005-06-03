
package org.davic.net;

public abstract class Locator extends Object implements javax.tv.locator.Locator 
{
protected String creatorString; //string used to create this locator


/*Locator that encapsulates an URL into an object*/
public Locator() {
   creatorString=null;
}

public Locator(String url) {
   creatorString=url;
}
/*Constructor for the locator Parameters: url - URL string */


/*----------------------------javax.tv.locator.Locator--------------------------
--*//*  (the descriptions from Sun are much better than those from DAVIC)
 
 Generates a canonical, string-based representation of this
 Locator . The string returned may be entirely
 platform-dependent. If two locators have identical external
 forms, they refer to the same resource. However, two locators
 that refer to the same resource may have different external
 forms. */

public abstract java.lang.String toExternalForm ();


/*
 
 Indicates whether this Locator has a mapping to
 multiple transports. 
 Returns:  true if multiple transformations exist for
 this Locator , false otherwise. 
 
 
 */

public boolean hasMultipleTransformations () {
   return false;
}


/*
 
 Compares this Locator with the specified object for
 equality. The result is true if and only if the
 specified object is also a Locator and has an
 external form identical to the external form of this
 Locator . 
 Overrides:  equals in class java.lang.Object 
 
 
 Parameters:  o - The object against which to compare this Locator . Returns:  
true if the specified object is equal to this Locator . See Also:  
String.equals(Object)  
 
 */

public boolean equals (java.lang.Object o) {
   return (o instanceof javax.tv.locator.Locator) &&
            ( ((javax.tv.locator.Locator)o).toExternalForm().equals(toExternalForm()));
}


/*
 
 Generates a hash code value for this Locator .
 Two Locator instances for which Locator.equals() 
 is true will have identical hash code values. 
 Overrides:  hashCode in class java.lang.Object 
 
 
 Returns: The hash code value for this Locator . See Also:   equals(Object)  
 
 
 */

public int hashCode () {
   return toExternalForm().hashCode();
}


/*
 
 Returns the string used to create this locator. 
 Overrides:  toString in class java.lang.Object 
 
 
 Returns: The string used to create this locator. See Also:   
LocatorFactory.createLocator(java.lang.String)   
 
*/

public java.lang.String toString () {
   if (creatorString ==null)
      return toExternalForm();
   else
      return creatorString;
}


}


/*11.7.6 
Content Referencing This API is formed of the classes found in section H.4 of 
annex H of DAVIC 1.4.1p9 [3 ] -the Locator and DvbLocator classes.It also 
includes the javax.tv.locator package as defined in Java TV [51 ].The signature 
of the org.davic.net.Locator class will be extended with: "implements 
javax.tv.locator.Locator" The createFactory()method of 
javax.tv.locator.LocatorFactory shall always return org.davic. 
net.Locator(s)which implement the javax.tv.locator.Locator interface when 
provided with DVB URLs as input (as de  ned in 14.1,"Namespace mapping (DVB 
Locator)"on page 353).In this specification,methods whose signature has a return 
type of org.davic.net.Locator or javax.tv. locator.Locator shall return an 
instance of org.davic.net.dvb.DvbLocator (or a platform de  ned subclass of 
that)where the locator returned can be represented by the DVB locator syntax 
described in DAVIC 1.4. 1p9 [3 ].In this case,the DvbLocator returned shall 
contain the numeric identi  ers of a DVB service (see 14.9,"Service identi  
cation"on page 359). In javax.tv.locator.Locator.toExternalForm(),the canonical 
form of a DVB locator is de  ned as follows: "For instances of 
org.davic.net.dvb.DvbNetworkBoundLocator this should be the format de  ned in 
the MHP speci  cation,including the transport stream id.For instances of 
org.davic.net.dvb.DvbLocator which are not instances of the above sub-class,this 
should be the format de  ned in the MHP speci  cation,excluding the transport 
stream id. Any optional extensions (for specifying components,events etc.)are 
considered in a comparison and if they are not equally present in both locators 
then the comparison shall fail. For the above locators "best effort"comparison 
shall be exact. The protected constructor of LocatorFactory is for 
implementation use.MHP applications shall not subclass LocatorFactory 
Implementations are not required to behave correctly if they should do this.

Page 353:
An extended format of the DAVIC DVB URL (DAVIC 1.4.1p9 [3 ])shall be used 
for addressing DVB-SI entities as well as  les within object carousels.This 
extension of the DAVIC locator is backwards compatible with both the original 
DAVIC locator as well as the UK DTG extension (UK MHEG Pro  le [B ]).The main 
extensions are support for multiple component tags for specifying a subset of 
the components of a service,and a speci  ed way of referencing  les in an object 
carousel within a service. Using the same informal notation as used above,the 
following locator formats shall be used:

dvb://<original_network_id>.[<transport_stream_id>][.<service_id>[.<compone
nt_tag>{&<component_ tag>}][;<event_id>]]{/<path_segments>} or 
dvb://'textual_service_identifier>'.<component_tag>{&<component_tag>}][;<event_id>]]{/<path_ segments>}
*/