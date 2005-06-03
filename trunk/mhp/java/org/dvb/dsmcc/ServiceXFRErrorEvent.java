package org.dvb.dsmcc ;

import org.openmhp.util.Out;

/**
* @author tejopa
* @date 13.2.2004
* @status fully implemented
* @module internal
*/
public class ServiceXFRErrorEvent extends AsynchronousLoadingEvent {

   ServiceXFRReference reference;

   public ServiceXFRErrorEvent (DSMCCObject o, ServiceXFRReference ref) {
      super(o);
      reference = ref;
      Out.printMe(Out.TRACE);
   }

   public java.lang.Object getSource() {
      Out.printMe(Out.TRACE);
      return super.getSource();
   }

   public ServiceXFRReference getServiceXFR () {
      Out.printMe(Out.TRACE);
      return reference;
   }

}
