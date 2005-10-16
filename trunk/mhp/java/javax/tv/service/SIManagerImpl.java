
package javax.tv.service;

import javax.tv.locator.Locator;
import javax.tv.locator.InvalidLocatorException;
import javax.tv.service.navigation.ServiceList;
import javax.tv.service.navigation.ServiceFilter;
import javax.tv.service.navigation.VDRServiceList;
import javax.tv.service.transport.Transport;
import javax.tv.service.transport.DVBTransport;
import javax.tv.service.guide.VDRProgramSchedule;

import org.davic.net.dvb.DvbLocator;

import org.dvb.si.SIDatabase;

/*

An <code>SIManager</code> represents a managing entity which has
 knowledge of all the broadcast resources available to a receiver.  An
 <code>SIManager</code> can be used to retrieve any
 <code>SIElement</code> or create a collection of
 <code>Service</code> objects according to filtering rules. <p>

 An <code>SIManager</code> may also be used to set parameters such
 as the preferred language for multilingual text information.
 Normally an application will create a single instance of
 <code>SIManager</code> and use that instance to access all SI
 information.  If an application creates more than one instance of
 <code>SIManager</code> it may experience degraded caching
 performance, particularly if the <code>SIManager</code> instances
 use different preferred languages.

*/
public class SIManagerImpl extends SIManager {

String preferredLanguage = null;


// make this static? Don't know.
Transport[] transports = null;


/*
 
 Constructs an SIManager object. */

protected SIManagerImpl () {
   //TODO: set preferredLanguage to VDR default
   //db=SIDatabase.getSIDatabase()[0];
}


/*
 
 Overrides the system-level preferred language for objects
 obtained through this SIManager . The specified
 language will be used by the textual information obtained from
 the SIElement objects obtained through this
 SIManager , if such information is available in the
 specified language. If the specified language is not available
 the system-level preferred language is used. If the system-level
 preferred language is not available either, the first available
 language will be used. */

public void setPreferredLanguage (java.lang.String language) {
   preferredLanguage=language;
}


/*
 
 Reports the preferred language for this SIManager .
 The resulting string is a language code indicating
 the language desired when retrieving multilingual text. This
 is typically a three-character code as specified by ISO 639.2/B. 
 Returns: A string representing a language code defining the language
 used to retrieve multilingual strings. If no language preference
 is in effect, null is returned. 
 
 
 */

public java.lang.String getPreferredLanguage () {
   return preferredLanguage;
}


/*
 
 Provides a hint to the SI database that the application desires
 SI information as complete as possible about the specified
 SIElement . As a result, the SI database might tune
 to the transport stream delivering the desired information and
 begin caching it, depending on resource availability.*/

public void registerInterest ( Locator locator,
                   boolean active)
                throws InvalidLocatorException ,
                   java.lang.SecurityException
{
   //do nothing currently
}


/*
 
 Provides the names of available rating dimensions in the local
 rating region. A zero-length array is returned if no rating
 dimensions are available. 
 Returns: An array of strings representing the names of available
 rating dimensions in this rating region. See Also:   RatingDimension  
 
 
 */

public java.lang.String[] getSupportedDimensions () {
   return new String [] { DVBRatingDimension.dimensionName };
}

/*
 
 Reports the RatingDimension corresponding to the
 specified string name. 
 Parameters:  name - The name of the requested rating dimension. Returns: The requested RatingDimension . Throws:  SIException  - If name is not a supported
 rating dimension, as returned by getSupportedDimensions() . See Also:   getSupportedDimensions()  
 
 
 */

public RatingDimension  getRatingDimension (java.lang.String name)
                      throws SIException {
   if (name.equals(DVBRatingDimension.dimensionName))
      return new DVBRatingDimension();
   throw new SIException("Unknown RatingDimension "+name);
}


/*
 
 Reports the various content delivery mechanisms currently
 available on this platform. The implementation must be capable
 of supporting at least one Transport instance. 
 Returns: An array of Transport objects representing
 the content delivery mechanisms currently available on this
 platform. 
 
 
 */

public Transport [] getTransports () {
   if (transports == null) {
      SIDatabase[] databases = SIDatabase.getSIDatabase();
      Transport[] transports= new Transport[databases.length];
      for (int i=0;i<databases.length;i++)
         transports[i]=new DVBTransport(databases[i]);
   }
   return transports;
}


/*
 
 Retrieves the SIElement corresponding to the
 specified Locator . If the locator identifies more
 than one SIElement , all matching
 SIElements are retrieved. */

public SIRequest  retrieveSIElement ( Locator locator,
                       SIRequestor requestor)
                   throws InvalidLocatorException ,
                      java.lang.SecurityException 
{
   // What does the spec say?
   //  When passed a locator that points to a service, an object implementing
   //  the ServiceDetails interface shall be returned. Other types of locators are not supported.
   //  Locators representing program events are not supported and shall fail with an
   //  SIRequestFailureType(INSUFFICIENT_RESOURCES).
   //  Other types of locators are supported as de?ned.
   // But then, we can retrieve an event, so why not do that?
         if (locator instanceof DvbLocator) {
      DvbLocator loc=(DvbLocator)locator;
      if (loc.is(DvbLocator.SERVICE)) {
         return retrieveServiceDetails(locator, requestor);
      } else if (loc.is(DvbLocator.EVENT)) {
         // return deliverRequest(SIRequestFailureType.INSUFFICIENT_RESOURCES);
         return retrieveProgramEvent(locator, requestor);
      } else
         throw new InvalidLocatorException(locator, "Only locators pointing to a service are accepted");
   } else
      throw new InvalidLocatorException(locator, "Currently only org.davic.net.DvbLocators supported");
}


/*
 
 Provides the Service referred to by a given
 Locator . An implementation must be capable of
 supporting at least one Service instance.  
 Parameters:  locator - A locator specifying a service. Returns: The Service object corresponding to the
 specified locator. Throws:  InvalidLocatorException  - If locator does not
 reference a valid Service . java.lang.SecurityException - If the caller does not have
 javax.tv.service.ReadPermission(locator) . See Also:   ReadPermission  
 
 
 */

public Service  getService ( Locator locator)
              throws InvalidLocatorException ,
                  java.lang.SecurityException
{
   if (locator instanceof DvbLocator) {
      DvbLocator loc=(DvbLocator)locator;
      if (loc.is(DvbLocator.SERVICE)) {
         Service service = VDRService.getService(loc.getOriginalNetworkId(), loc.getTransportStreamId(), loc.getServiceId());
         if (service == null)
            throw new InvalidLocatorException(locator, "Service referenced by locator not found");
         else
            return service;
      } else
         throw new InvalidLocatorException(locator, "Only locators pointing to a service are accepted");
   } else
      throw new InvalidLocatorException(locator, "Currently only org.davic.net.DvbLocators supported");
}


/*

   Retrieves the ServiceDetails object corresponding to
   the given Locator .
   Note that the locator may point to an SI element lower in the hierarchy than a
   service (such as a program event). In such a case, the ServiceDetails for the
   service that the program event is part of will be returned. 
   If a transport-independent locator is provided, one or more ServiceDetails
   objects may be returned. However, it is permissible in this case for this method
   to always retrieve a single ServiceDetails object, as determined by the
   implementation, user preferences, or availability. To obtain all of the
   corresponding ServiceDetails objects, the application may transform the
   transport-independent locator into multiple transport-dependent locators and
   retrieve a ServiceDetails object for each.
   This method delivers its results asynchronously.
   Parameters:
   locator - A locator referencing a Service
   requestor - The SIRequestor to be notified when this retrieval operation
   completes.
   Returns:
   An SIRequest object identifying this asynchronous retrieval request.
   Throws:
   InvalidLocatorException - If locator does not reference a valid Service.
   java.lang.SecurityException - If the caller does not have
   javax.tv.service.ReadPermission(locator).

*/

public SIRequest  retrieveServiceDetails ( Locator locator,
                         SIRequestor requestor)
                     throws InvalidLocatorException ,
                         java.lang.SecurityException
{
   if (locator instanceof DvbLocator) {
      DvbLocator loc=(DvbLocator)locator;
      if (loc.is(DvbLocator.SERVICE)) {
         Service service = getService(locator);
         return service.retrieveDetails(requestor);
         /*
         SIDatabase db=SIDatabase.getDatabaseForChannel
            (loc.getOriginalNetworkId(), loc.getTransportStreamId(), loc.getServiceId());
         System.out.println("Database: "+db);
         if (db == null)
            return deliverRequest(requestor, SIRequestFailureType.DATA_UNAVAILABLE);
         else {
            javax.tv.service.OrgDvbSiRequestAdapter req=new javax.tv.service.OrgDvbSiRequestAdapter(requestor);
            req.setRequest(db.retrieveSIService(org.dvb.si.SIInformation.FROM_CACHE_OR_STREAM, null, req, loc, null));
            return req;
         }
         */
      } else
         throw new InvalidLocatorException(locator, "Only locators pointing to a service are accepted");
   } else
      throw new InvalidLocatorException(locator, "Currently only org.davic.net.DvbLocators supported");
}


/*
 
 Retrieves the ProgramEvent corresponding to the
 given Locator . If a transport-independent locator
 is provided (e.g., one referencing the same movie shown at
 different times and/or on different services), this method may
 retrieve multiple ProgramEvent objects. */

public SIRequest  retrieveProgramEvent ( Locator locator,
                        SIRequestor requestor)
                    throws InvalidLocatorException ,
                        java.lang.SecurityException
{
   VDRService service = (VDRService)getService(locator);
   return new VDRProgramSchedule(service).retrieveProgramEvent(locator, requestor);
}


/*
 
 Filters the available services using the
 ServiceFilter provided to generate a
 ServiceList containing the matching services. If
 the filter parameter is null , a list of all known
 services is generated. Only Service instances for
 which the caller has javax.tv.service.ReadPermission 
 on the underlying locator will be present in the returned
 list. 
 
 Note that for each Service to be filtered, the
 accept() method of the given
 ServiceFilter will be invoked with the same
 application thread that invokes this method. 
 Parameters:  filter - A ServiceFilter by which to generate
 the requested service list, or null . Returns: A ServiceList generated according to the
 specified filtering rules. See Also:   ServiceFilter.accept(javax.tv.service.Service)  
 
 
*/

public ServiceList  filterServices ( ServiceFilter filter) {
   return new VDRServiceList(filter);
}



}

