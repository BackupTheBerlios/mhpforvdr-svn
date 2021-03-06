
package javax.tv.service;

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

long nativeData;

VDRService(long nativeData) {
   this.nativeData=nativeData;
}

//For local applications or unkown IDs, a null object is returned

//internal API
public static VDRService getServiceForMHPApplication(org.dvb.application.MHPApplication app) {
   return getServiceForNativeChannel(app.getNativeChannel());
}

//internal API
public static VDRService getServiceForNativeChannel(long nativeChannel) {
   if (nativeChannel != 0)
      return new VDRService(nativeChannel);
   return null;
}

//internal API
public static VDRService getService(int source, int onid, int tid, int sid) {
   long nD=getServiceForChannelId(source, onid, tid, sid);
   if (nD != 0)
      return new VDRService(nD);
   else
      return null;
}
private static native long getServiceForChannelId(int source, int onid, int tid, int sid);

//internal API
public static VDRService getService(int onid, int tid, int sid) {
   long nD=getServiceForNidTidSid(onid, tid, sid);
   if (nD != 0)
      return new VDRService(nD);
   else
      return null;
}
private static native long getServiceForNidTidSid(int onid, int tid, int sid);

//internal API
//returns object corresponding to VDR's cDevice::GetCurrentChannel()
public static VDRService getCurrentService() {
   long nativeCurrentChannel=getCurrentChannelNative();
   if (nativeCurrentChannel==0)
      return new VDRService(nativeCurrentChannel);
   else 
      return null;
}
private static native long getCurrentChannelNative();

//internal API
public long getNativeData() {
   return nativeData;
}

/*
 
 This method retrieves additional information about the
 Service . This information is retrieved from the
 broadcast service information. */

public SIRequest  retrieveDetails ( SIRequestor requestor) {
   org.dvb.si.SIDatabase db=org.dvb.si.SIDatabase.getDatabaseForChannel
      (getOriginalNetworkId(), getTransportStreamId(), getServiceId());
   if (db == null)
      return javax.tv.service.SIManager.deliverRequest(requestor, SIRequestFailureType.DATA_UNAVAILABLE);
   else {
      javax.tv.service.OrgDvbSiRequestAdapter req=new javax.tv.service.OrgDvbSiRequestAdapter(requestor);
      req.setRequest(db.retrieveSIService(org.dvb.si.SIInformation.FROM_CACHE_OR_STREAM, null, req, (org.davic.net.dvb.DvbLocator)getLocator(), null));
      return req;
   }
}

/*
 
 Reports the service number of a service. 
 Returns: The number of the service. 
 
 
*/

public int getServiceNumber () {
   return serviceNumber(nativeData);
}

private native int serviceNumber(long nativeData);

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
   return new String(name(nativeData));
}

private native byte[] name(long nativeData);

//internal API

public int getOriginalNetworkId() {
   return onid(nativeData);
}

private native int onid(long nativeData);

public int getTransportStreamId() {
   return tid(nativeData);
}

private native int tid(long nativeData);

public int getServiceId() {
   return sid(nativeData);
}

private native int sid(long nativeData);

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
   return isRadio(nativeData) ? ServiceType.DIGITAL_TV : ServiceType.DIGITAL_RADIO;
}
private native boolean isRadio(long nativeData);


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
               ((VDRService)obj).nativeData==nativeData    ||
               ((VDRService)obj).getLocator().equals(getLocator()) 
            );
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




}

