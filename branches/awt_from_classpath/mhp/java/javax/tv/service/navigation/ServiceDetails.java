
package javax.tv.service.navigation;
import javax.tv.service.*;

/*

This interface provides access to service meta-data. It provides more
 information about a <code>Service</code> object and represents a
 specific instance of a service bound to a transport stream. <p>

 A <code>ServiceDetails</code> object may optionally implement the
 <code>ServiceNumber</code> interface to report service numbers as
 assigned by the broadcaster of the service.<p>

 A <code>ServiceDetails</code> object may optionally implement the
 <code>ServiceProviderInformation</code> interface to report information
 concerning the service provider.

*/
public interface ServiceDetails extends javax.tv.service.SIElement , CAIdentification {

/*
 
 Retrieves a textual description of this service if available.
 This method delivers its results asynchronously. 
 
 
 
 Parameters:  requestor - The SIRequestor to be notified
 when this retrieval operation completes. Returns: An SIRequest object identifying this
 asynchronous retrieval request. See Also:   ServiceDescription  
 
 
 */

public javax.tv.service.SIRequest  retrieveServiceDescription ( javax.tv.service.SIRequestor requestor);


/*
 
 Returns the type of this service, for example, "digital
 television", "digital radio", "NVOD", etc. These values can be
 mapped to the ATSC service type in the VCT table and the DVB
 service type in the Service Descriptor. 
 
 
 
 Returns: Service type of this service. 
 
 
 */

public javax.tv.service.ServiceType  getServiceType ();


/*
 
 Retrieves an array of elementary components which are part of
 this service. The array will only contain
 ServiceComponent instances c for which
 the caller has
 javax.tv.service.ReadPermission(c.getLocator()) . If
 no ServiceComponent instances meet this criteria,
 this method will result in an SIRequestFailureType of
 DATA_UNAVAILABLE .*/

public javax.tv.service.SIRequest  retrieveComponents ( javax.tv.service.SIRequestor requestor);


/*
 
 Returns a schedule of program events associated with this service. 
 
 
 
 Returns: The program schedule for this service, or null 
 if no schedule is available. 
 
 
 */

public javax.tv.service.guide.ProgramSchedule  getProgramSchedule ();


/*
 
 Called to obtain a full service name. For example, this
 information may be delivered in the ATSC Extended Channel Name
 Descriptor, the DVB Service Descriptor or the DVB Multilingual
 Service Name Descriptor. 
 
 
 
 Returns: A string representing the full service name, or an empty
 string if the name is not available. 
 
 
 */

public java.lang.String getLongName ();


/*
 
 Returns the Service this ServiceDetails 
 object is associated with. 
 
 
 
 Returns: The Service to which this
 ServiceDetails belongs. 
 
 
 */

public javax.tv.service.Service  getService ();


/*
 
 Registers a ServiceComponentChangeListener to be
 notified of changes to a ServiceComponent that is
 part of this ServiceDetails . Subsequent notification
 is made via ServiceComponentChangeEvent with this
 ServiceDetails instance as the event source and an
 SIChangeType of ADD ,
 REMOVE or MODIFY . Only changes to
 ServiceComponent instances c for which
 the caller has
 javax.tv.service.ReadPermission(c.getLocator()) will
 be reported. 
 
 This method is only a request for notification. No guarantee is
 provided that the SI database will detect all, or even any, SI
 changes or whether such changes will be detected in a timely
 fashion. 
 
 If the specified ServiceComponentChangeListener is
 already registered, no action is performed. 
 
 
 
 Parameters:  listener - A ServiceComponentChangeListener to be
 notified about changes related to a ServiceComponent 
 in this ServiceDetails . See Also:   ServiceComponentChangeEvent , 
 ReadPermission  
 
 
 */

public void addServiceComponentChangeListener ( ServiceComponentChangeListener listener);


/*
 
 Called to unregister an
 ServiceComponentChangeListener . If the specified
 ServiceComponentChangeListener is not registered, no
 action is performed. 
 
 
 
 Parameters:  listener - A previously registered listener. 
 
 
 */

public void removeServiceComponentChangeListener ( ServiceComponentChangeListener listener);


/*
 
 Reports the type of mechanism by which this service was
 delivered. 
 
 
 
 Returns: The delivery system type of this service. 
 
 
*/

public DeliverySystemType  getDeliverySystemType ();



}

