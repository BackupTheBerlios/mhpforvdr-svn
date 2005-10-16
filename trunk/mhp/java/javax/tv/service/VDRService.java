
package javax.tv.service;

import java.util.HashMap;
import java.util.LinkedList;
import org.dvb.application.MHPApplication;
import org.dvb.application.AppAttributes;
import org.davic.net.dvb.DvbLocator;
import vdr.mhp.lang.NativeData;
import javax.tv.service.navigation.ServiceDetails;
import javax.tv.service.navigation.ServiceProviderInformation;
import javax.tv.service.navigation.ServiceComponent;
import javax.tv.service.navigation.DeliverySystemType;
import javax.tv.service.navigation.ServiceComponentChangeListener;
import javax.tv.service.navigation.StreamType;
import javax.tv.service.guide.VDRProgramSchedule;

/*

The <code>Service</code> interface represents an abstract view on
 what is generally referred to as a television "service" or
 "channel". It may represent an MPEG-2 program, DVB service, an ATSC
 virtual channel, SCTE virtual channel, etc. It represents the basic
 information associated with a service, such as its name or number,
 which is guaranteed to be available on the receiver. 
*/

/* 
   This class is quite different from org.dvb.si.SIServiceImpl because
   it represent a VDR cChannel* while SIServiceImpl represents information
   taken directly from the SI stream.
*/

public class VDRService implements Service, ServiceNumber, org.dvb.si.TextualServiceIdentifierQuery {

NativeData nativeData;
static HashMap hashmap = new HashMap();

static {
   initStaticState();
}
private static native void initStaticState();

private VDRService(NativeData nativeData) {
   this.nativeData=nativeData;
}

//internal API
// Returns a VDRService objects for given native channel, or NULL if nativeChannel is null.
public static VDRService getService(NativeData nativeChannel) {
   //There may be several 1000 channels, so limiting the number of Service objects is a good idea.
   if (!nativeChannel.isNull()) {
      synchronized(hashmap) {
         // The NativeData object will contain a hashCode() method that depends on the native data
         // rather than the Java Object's hashCode.
         VDRService service = (VDRService)hashmap.get(nativeChannel);
         if (service == null) {
            service = new VDRService(nativeChannel);
            hashmap.put(nativeChannel, service);
         }
         return service;
      }
   }
   return null;
}

//internal API
public static VDRService getService(int source, int onid, int tid, int sid) {
   return getService(getServiceForChannelId(source, onid, tid, sid));
}
private static native NativeData getServiceForChannelId(int source, int onid, int tid, int sid);

//internal API
public static VDRService getService(int onid, int tid, int sid) {
   return getService(getServiceForNidTidSid(onid, tid, sid));
}
private static native NativeData getServiceForNidTidSid(int onid, int tid, int sid);

//internal API
//returns object corresponding to VDR's cDevice::GetCurrentChannel()
public static VDRService getCurrentService() {
   return getService(getCurrentServiceNative());
}
private static native NativeData getCurrentServiceNative();

//internal API
public NativeData getNativeData() {
   return nativeData;
}

/*
 
 This method retrieves additional information about the
 Service . This information is retrieved from the
 broadcast service information. */

public SIRequest  retrieveDetails ( SIRequestor requestor) {
   /*
   org.dvb.si.SIDatabase db=org.dvb.si.SIDatabase.getDatabaseForChannel
      (getOriginalNetworkId(), getTransportStreamId(), getServiceId());
   if (db == null)
      return javax.tv.service.SIManager.deliverRequest(requestor, SIRequestFailureType.DATA_UNAVAILABLE);
   else {
      javax.tv.service.OrgDvbSiRequestAdapter req=new javax.tv.service.OrgDvbSiRequestAdapter(requestor);
      req.setRequest(db.retrieveSIService(org.dvb.si.SIInformation.FROM_CACHE_OR_STREAM, null, req, (org.davic.net.dvb.DvbLocator)getLocator(), null));
      return req;
   }
   */
   return javax.tv.service.SIManager.deliverRequest(requestor, new VDRServiceDetails());

}

/*
 
 Reports the service number of a service. 
 Returns: The number of the service. 
 
 
*/

public int getServiceNumber () {
   return serviceNumber(nativeData);
}

private native int serviceNumber(NativeData nativeData);

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

public java.lang.String getName () {
   return shortName(nativeData);
}

private native String longName(NativeData nativeData);
private native String shortName(NativeData nativeData);

//internal API

public int getOriginalNetworkId() {
   return onid(nativeData);
}

private native int onid(NativeData nativeData);

public int getTransportStreamId() {
   return tid(nativeData);
}

private native int tid(NativeData nativeData);

public int getServiceId() {
   return sid(nativeData);
}

private native int sid(NativeData nativeData);

/*
 
 This method indicates whether the service represented by this
 Service object is available on multiple
 transports, (e.g., the same content delivered over terrestrial and
 cable network). 
 Returns:  true if multiple transports carry the same
 content identified by this Service object;
 false if there is only one instance of this service. 
 
 
 */

public boolean hasMultipleInstances () {
   return false;
}


/*
 
 Returns the type of this service, (for example, "digital
 television", "digital radio", "NVOD", etc.) These values can be
 mapped to the ATSC service type in the VCT table and the DVB
 service type in the service descriptor. 
 Returns: Service type of this Service . 
 
 
 */

public ServiceType  getServiceType () {
   //not a complete, but a sufficient implementation
   return isRadio(nativeData) ? ServiceType.DIGITAL_RADIO : ServiceType.DIGITAL_TV;
}
private native boolean isRadio(NativeData nativeData);


/*
 
 Reports the Locator of this Service .
 Note that if the resulting locator is transport-dependent, it
 will also correspond to a ServiceDetails object. 
 Returns: A locator referencing this Service . See Also:   ServiceDetails  
 
 
 */

public javax.tv.locator.Locator  getLocator () {
   try {
      return new org.davic.net.dvb.DvbLocator(getOriginalNetworkId(), getTransportStreamId(), getServiceId());
   } catch (javax.tv.locator.InvalidLocatorException e) {
      e.printStackTrace();
      return null;
   }
}


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

public boolean equals (java.lang.Object obj) {
   return   (obj instanceof VDRService)
         && (
               ((VDRService)obj).nativeData.equals(nativeData)    ||
               ((VDRService)obj).getLocator().equals(getLocator()) 
            );
}

// internal extension
public boolean sameService(DvbLocator locator) {
   if (!locator.provides(DvbLocator.SERVICE))
      return false;
   return locator.getServiceId() == getServiceId()
          && locator.getOriginalNetworkId() != getOriginalNetworkId()
            && ( (locator.getTransportStreamId() == -1) || (locator.getTransportStreamId() == getTransportStreamId()) );
}

/*
 
 Reports the hash code value of this Service . Two
 Service objects that are equal will have identical
 hash codes. 
 Overrides:  hashCode in class java.lang.Object 
 
 
 Returns: The hash code value of this Service . 
 
 
*/

public int hashCode () {
   return getLocator().hashCode();
}

/*
Returns the textual service identi ers related to this object. Returns: an array of String objects containing the 
textual service identi ers or null if none are present. Since: MHP1.0.1 */
public java.lang.String[] getTextualServiceIdentifiers() {
   //the spec wants this to be implemented also by this class
   //currently no implementation on any level
   return new String[0];
}




// --- ServiceDetails ---

private native int deliverySystemType(NativeData nativeData);
private native boolean freeToAir(NativeData nativeData);
private native int[] caIDs(NativeData nativeData);
private native String providerName(NativeData nativeData);

class VDRServiceDetails implements ServiceDetails, ServiceNumber, ServiceProviderInformation {
   /*
   
   Retrieves a textual description of this service if available.
   This method delivers its results asynchronously. 
   
   
   
   Parameters:  requestor - The SIRequestor to be notified
   when this retrieval operation completes. Returns: An SIRequest object identifying this
   asynchronous retrieval request. See Also:   ServiceDescription  
   
   
   */
   
   public javax.tv.service.SIRequest  retrieveServiceDescription ( javax.tv.service.SIRequestor requestor) {
      //according to the spec
      return javax.tv.service.SIManager.deliverRequest(requestor, javax.tv.service.SIRequestFailureType.DATA_UNAVAILABLE);
   }
   
   
   /*
   
   Returns the type of this service, for example, "digital
   television", "digital radio", "NVOD", etc. These values can be
   mapped to the ATSC service type in the VCT table and the DVB
   service type in the Service Descriptor. 
   
   
   
   Returns: Service type of this service. 
   
   
   */
   
   public javax.tv.service.ServiceType  getServiceType () {
      return VDRService.this.getServiceType();
   }
   
   
   /*
   
   Retrieves an array of elementary components which are part of
   this service. The array will only contain
   ServiceComponent instances c for which
   the caller has
   javax.tv.service.ReadPermission(c.getLocator()) . If
   no ServiceComponent instances meet this criteria,
   this method will result in an SIRequestFailureType of
   DATA_UNAVAILABLE .*/
   
   public javax.tv.service.SIRequest retrieveComponents ( javax.tv.service.SIRequestor requestor) {
      return javax.tv.service.SIManager.deliverRequest(requestor, getComponents());
   }
   
   
   /*
   
   Returns a schedule of program events associated with this service. 
   
   
   
   Returns: The program schedule for this service, or null 
   if no schedule is available. 
   
   
   */
   
   public javax.tv.service.guide.ProgramSchedule  getProgramSchedule () {
      return new VDRProgramSchedule(VDRService.this);
   }
   
   
   /*
   
   Called to obtain a full service name. For example, this
   information may be delivered in the ATSC Extended Channel Name
   Descriptor, the DVB Service Descriptor or the DVB Multilingual
   Service Name Descriptor. 
   
   
   
   Returns: A string representing the full service name, or an empty
   string if the name is not available. 
   
   
   */
   
   public java.lang.String getLongName () {
      return longName(nativeData);
   }
   
   
   /*
   
   Returns the Service this ServiceDetails 
   object is associated with. 
   
   
   
   Returns: The Service to which this
   ServiceDetails belongs. 
   
   
   */
   
   public javax.tv.service.Service  getService () {
      return VDRService.this;
   }
   
   
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
   
   public void addServiceComponentChangeListener ( ServiceComponentChangeListener listener) {
      System.out.println("VDRService.addServiceComponentChangeListener: implement me");
   }
   
   
   /*
   
   Called to unregister an
   ServiceComponentChangeListener . If the specified
   ServiceComponentChangeListener is not registered, no
   action is performed. 
   
   
   
   Parameters:  listener - A previously registered listener. 
   
   
   */
   
   public void removeServiceComponentChangeListener ( ServiceComponentChangeListener listener) {
      System.out.println("VDRService.removeServiceComponentChangeListener: implement me");
   }
   
   
   /*
   
   Reports the type of mechanism by which this service was
   delivered. 
   
   
   
   Returns: The delivery system type of this service. 
   
   
   */
   
   public DeliverySystemType  getDeliverySystemType () {
      // constants from libservice/service.h
      switch(deliverySystemType(nativeData)) {
      case 1:  return DeliverySystemType.SATELLITE;
      case 2:  return DeliverySystemType.CABLE;
      case 3:  return DeliverySystemType.TERRESTRIAL;
      default: return DeliverySystemType.UNKNOWN;
      }
   }
/*
 
   Returns an array of CA System IDs associated with this object. This
   information may be obtained from the CAT MPEG message or a system
   specific conditional access descriptor (such as defined by Simulcrypt
   or ATSC). 
   Returns: An array of CA System IDs. An empty array is returned when no
   CA System IDs are available. 
 
 
 */
   public int[] getCASystemIDs () {
      return caIDs(nativeData);
   }

/*
 
   Provides information concerning conditional access of this object. 
   Returns:  true if this Service is not protected by a
   conditional access; false if one or more components
   is protected by conditional access. 
 
 
*/
   public boolean isFree () {
      return freeToAir(nativeData);
   }

   /*
   Returns the name of the service provider. It can be retrieved from the DVB Service Descriptor or the Multilingual Service Name Descriptor.
   Returns:
   A string representing the service provider's name. It returns an empty string if no provider information is available.
   */
   public String getProviderName() {
      return providerName(nativeData);
   }
   
   /*
   Reports the service number of a service.
   Returns:
   The number of the service.
   */
   public int getServiceNumber() {
      return VDRService.this.getServiceNumber();
   }
/*
 
   Returns the time when this object was last updated from data in
   the broadcast. 
   Returns: The date of the last update in UTC format, or null 
   if unknown. 
 
 
*/

   public java.util.Date getUpdateTime () {
      // unknown
      return null;
   }
/*
 
   Reports the Locator of this SIElement . 
 
 
 
   Returns: Locator The locator referencing this
   SIElement 
 
 
 */

   public javax.tv.locator.Locator  getLocator () {
      return VDRService.this.getLocator();
   }

/*
   Tests two SIElement objects for equality. Returns
 true if and only if:
 
   obj 's class is the
   same as the class of this SIElement , and 
   obj 's Locator is equal to
   the Locator of this object (as reported by
   SIElement.getLocator() , and 
   obj and this object encapsulate identical data.
  
   Overrides:  equals in class java.lang.Object 
 
 
   Parameters:  obj - The object against which to test for equality. Returns:  true if the two SIElement objects
   are equal; false otherwise.  
 */

   public boolean equals (java.lang.Object obj) {
      if (!(obj instanceof VDRServiceDetails))
         return false;
      
      VDRServiceDetails other=(VDRServiceDetails)obj;
      return getLocator().equals(other.getLocator());
   }


/*
   Reports the hash code value of this SIElement . Two
   SIElement objects that are equal will have identical
   hash codes. 
   Overrides:  hashCode in class java.lang.Object 
 
 
   Returns: The hash code value of this SIElement . 
 */

   public int hashCode () {
      return VDRService.this.hashCode();
   }


/*
   Reports the SI format in which this SIElement was
   delivered. 
   Returns: The SI format in which this SI element was delivered. 
*/

   public javax.tv.service.ServiceInformationType  getServiceInformationType () {
      return javax.tv.service.ServiceInformationType.DVB_SI;
   }
}



// There is another, more complete components implementation for guide.VDRProgramSchedule.VDRProgramEvent.
// The component descriptor is only contained in the EIT, so th event is the most natural place for this query.
// Here, the audio and video streams from the PMT (stored by VDR) are taken to build the ServiceComponent list.

VDRServiceComponent[] getComponents() {
   ServiceComponentListBuilder builder = new ServiceComponentListBuilder();
   buildComponentsList(nativeData, builder);
   return builder.getArray();
}

private native void buildComponentsList(NativeData nativeData, ServiceComponentListBuilder builder);

class ServiceComponentListBuilder {
   LinkedList list = new LinkedList();
   
   // called from native side
   void nextComponent(int componentTag, int t, String language) {
      StreamType type=StreamType.UNKNOWN;
      switch (t) {
         // private constants
         case 1:
            type=StreamType.VIDEO;
            break;
         case 2:
            type=StreamType.AUDIO;
            break;
         case 3:
            type=StreamType.SUBTITLES;
            break;
      }
      list.add(new VDRServiceComponent(componentTag, type, language));
   }
   
   VDRServiceComponent[] getArray() {
      VDRServiceComponent[] array = new VDRServiceComponent[list.size()];
      return (VDRServiceComponent[])list.toArray(array);
   }
}


// --- ServiceComponents ---

class VDRServiceComponent implements ServiceComponent {

   int componentTag;
   StreamType type;
   String language;
   
   VDRServiceComponent(int componentTag, StreamType type, String language) {
      this.componentTag=componentTag;
      this.type=type;
      this.language=language;
   }
/*
 
   Returns a name associated with this component. The Component Descriptor
   (DVB) or Component Name Descriptor (ATSC) may be used if present. A
   generic name (e.g., "video", "first audio", etc.) may be used otherwise. 
 
 
 
   Returns: A string representing the component name or an empty string
   if no name can be associated with this component. 
 
 
 */

   public java.lang.String getName () {
      // TODO: This is available and cached by VDR in cComponents,
      // but this is not easily accessible. Not worth the work currently.
      System.out.println("VDRServiceComponent.getName(): Implement me");
      return "";
   }


/*
 
   Identifies the language used for the elementary stream. The
   associated language is indicated using a language code. This is
   typically a three-character language code as specified by ISO
   639.2/B, but the code may be system-dependent. 
 
 
 
   Returns: A string representing a language code defining the
   language associated with this component. An empty string is
   returned when there is no language associated with this component. 
 
 
 */

   public java.lang.String getAssociatedLanguage () {
      return language;
   }


/*
 
   Provides the stream type of this component. (For example, "video",
   "audio", etc.) 
 
 
 
   Returns: Stream type of this component. 
 
 
 */

   public javax.tv.service.navigation.StreamType  getStreamType () {
      return type;
   }


/*
 
   Provides the Service object to which this
   ServiceComponent belongs. The result may be
   null if the Service cannot be determined. 
 
 
 
   Returns: The Service to which this
   ServiceComponent belongs. 
 
 
*/

   public javax.tv.service.Service  getService () {
      return VDRService.this;
   }

/*
 
   Returns the time when this object was last updated from data in
   the broadcast. 
   Returns: The date of the last update in UTC format, or null 
   if unknown. 
 
 
*/

   public java.util.Date getUpdateTime () {
      // unknown
      return null;
   }
/*
 
   Reports the Locator of this SIElement . 
 
 
 
   Returns: Locator The locator referencing this
   SIElement 
 
 
 */

   public javax.tv.locator.Locator  getLocator () {
      try {
         return new org.davic.net.dvb.DvbLocator(getOriginalNetworkId(), getTransportStreamId(), getServiceId(), -1, componentTag);
      } catch (javax.tv.locator.InvalidLocatorException e) {
         e.printStackTrace();
         return null;
      }
   }

/*
   Tests two SIElement objects for equality. Returns
 true if and only if:
 
   obj 's class is the
   same as the class of this SIElement , and 
   obj 's Locator is equal to
   the Locator of this object (as reported by
   SIElement.getLocator() , and 
   obj and this object encapsulate identical data.
  
   Overrides:  equals in class java.lang.Object 
 
 
   Parameters:  obj - The object against which to test for equality. Returns:  true if the two SIElement objects
   are equal; false otherwise.  
 */

   public boolean equals (java.lang.Object obj) {
      if (!(obj instanceof VDRServiceComponent))
         return false;
      
      VDRServiceComponent other=(VDRServiceComponent)obj;
      return getLocator().equals(other.getLocator());
   }


/*
   Reports the hash code value of this SIElement . Two
   SIElement objects that are equal will have identical
   hash codes. 
   Overrides:  hashCode in class java.lang.Object 
 
 
   Returns: The hash code value of this SIElement . 
 */

   public int hashCode () {
      return getLocator().hashCode();
   }


/*
   Reports the SI format in which this SIElement was
   delivered. 
   Returns: The SI format in which this SI element was delivered. 
*/

   public javax.tv.service.ServiceInformationType  getServiceInformationType () {
      return javax.tv.service.ServiceInformationType.DVB_SI;
   }


}





}

