package javax.tv.service;

import org.dvb.si.SIRequest;
import org.dvb.si.SIIterator;
import org.dvb.si.SIRetrievalListener;

//A bridge between the two APIs
public class OrgDvbSiRequestAdapter implements javax.tv.service.SIRequest, SIRetrievalListener {

SIRequestor requestor;
org.dvb.si.SIRequest request=null;

public OrgDvbSiRequestAdapter(SIRequestor requestor) {
   this.requestor=requestor;
}

//can't be done in constructor due to a hen-and-egg problem
public void setRequest(org.dvb.si.SIRequest request) {
   this.request=request;
}

public boolean cancel () {
   if (request != null)
      return request.cancelRequest();
   else
      return false;
}

public void postRetrievalEvent(org.dvb.si.SIRetrievalEvent event) {
   if (event instanceof org.dvb.si.SISuccessfulRetrieveEvent) {
      SIIterator it=((org.dvb.si.SISuccessfulRetrieveEvent)event).getResult();
      int len=it.numberOfRemainingObjects();
      SIRetrievable ret[]=new SIRetrievable[len];
      for (int i=0;i<len;i++)
         ret[i]=(SIRetrievable)it.nextElement();
      requestor.notifySuccess(ret);
   } else if (event instanceof org.dvb.si.SILackOfResourcesEvent ) {
      requestor.notifyFailure(SIRequestFailureType.INSUFFICIENT_RESOURCES);
   } else if (event instanceof org.dvb.si.SINotInCacheEvent ) {
      requestor.notifyFailure(SIRequestFailureType.DATA_UNAVAILABLE);
   } else if (event instanceof org.dvb.si.SIObjectNotInTableEvent ) {
      requestor.notifyFailure(SIRequestFailureType.DATA_UNAVAILABLE);
   } else if (event instanceof org.dvb.si.SITableNotFoundEvent ) {
      requestor.notifyFailure(SIRequestFailureType.DATA_UNAVAILABLE);
   } else if (event instanceof org.dvb.si.SITableUpdatedEvent ) {
      requestor.notifyFailure(SIRequestFailureType.DATA_UNAVAILABLE);
   } else if (event instanceof org.dvb.si.SIRequestCancelledEvent ) {
      requestor.notifyFailure(SIRequestFailureType.CANCELED);
   }
}


}