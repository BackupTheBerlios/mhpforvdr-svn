
package org.davic.mpeg;

import org.dvb.application.MHPApplication;
import vdr.mhp.ApplicationManager;
import javax.tv.service.VDRService;
import org.davic.mpeg.dvb.DvbTransportStream;
import org.davic.mpeg.dvb.DvbService;


public class ApplicationOrigin {

/*
Returns: the service that contained the root object of the application, or null if the application was not contained in 
a service (e.g. in the case of a receiver-resident application). 
*/

public static Service getService() {
   //this is only a matter of tying implementation-specific classes together
   MHPApplication app = ApplicationManager.getManager().getApplicationFromStack();
   if (app == null)
      return null;
   VDRService service = app.getService();

   //TODO: implement NID/ONID distinction
   TransportStream ts=new DvbTransportStream(service.getTransportStreamId(), service.getOriginalNetworkId(), service.getOriginalNetworkId());
   return new org.davic.mpeg.dvb.DvbService(ts, service.getServiceId());
}


}
