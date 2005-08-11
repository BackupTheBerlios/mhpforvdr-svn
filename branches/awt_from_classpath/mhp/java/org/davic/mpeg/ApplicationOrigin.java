
package org.davic.mpeg;

/* */

public class ApplicationOrigin {

/*
Returns: the service that contained the root object of the application, or null if the application was not contained in 
a service (e.g. in the case of a receiver-resident application). */
public static Service getService() {
   //this is only a matter of tying implementation-specific classes together
   org.dvb.application.MHPApplication app = vdr.mhp.ApplicationManager.getManager().getApplicationFromStack();
   javax.tv.service.VDRService service = javax.tv.service.VDRService.getServiceForMHPApplication(app);
   if (service==null)
      return null;
      
   //TODO: implement NID/ONID distinction
   TransportStream ts=new org.davic.mpeg.dvb.DvbTransportStream(service.getTransportStreamId(), service.getOriginalNetworkId(), service.getOriginalNetworkId());
   return new org.davic.mpeg.dvb.DvbService(ts, service.getServiceId());
}


}
