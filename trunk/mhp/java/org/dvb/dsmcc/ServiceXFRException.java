package org.dvb.dsmcc;

import org.openmhp.util.Out;

/**
* @author tejopa
* @date 11.2.2004
* @status partially implemented
* @module internal
*/
public class ServiceXFRException extends DSMCCException {

   org.davic.net.Locator service;
   int carouselId;
   String pathName;

   public ServiceXFRException(org.davic.net.Locator s, int c, String p) {
      service = s;
      carouselId = c;
      pathName = p;
      Out.printMe(Out.TRACE);
   }

   public ServiceXFRException(byte[] NSAPAddress, String pathName){
      Out.printMe(Out.TODO);
   }

   public ServiceXFRReference getServiceXFR(){
      Out.printMe(Out.TODO);
      return null;
   }
}
