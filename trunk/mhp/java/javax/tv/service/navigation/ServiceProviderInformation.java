
package javax.tv.service.navigation;

/*

This interface is used to report additional information concerning the
 provider of a service. ServiceDetails objects may optionally implement
 this interface on systems that provide information concerning the
 service provider in their specific SI format. It can be mapped to the
 DVB Service descriptor.

*/
public interface ServiceProviderInformation {

/*
 
 Returns the name of the service provider. It can be retrieved from the
 DVB Service Descriptor or the Multilingual Service Name Descriptor. 
 Returns: A string representing the service provider's name. It returns
 an empty string if no provider information is available. 
 
 
*/

public java.lang.String getProviderName ();



}

