
package javax.tv.service.navigation;
import javax.tv.service.SIElement;
import javax.tv.service.Service;
import javax.tv.service.VDRService;
import javax.tv.service.guide.ProgramEvent;
import javax.tv.service.transport.Network;
import javax.tv.service.transport.Bouquet;
import javax.tv.service.transport.TransportStream;
import javax.tv.service.navigation.ServiceComponent;
import javax.tv.service.navigation.ServiceDetails;

/*

<code>SIElementFilter</code> represents a
 <code>ServiceFilter</code> based on a particular
 <code>SIElement</code> (such as a <code>TransportStream</code> or
 <code>ProgramEvent</code>).  A <code>ServiceList</code> resulting
 from this filter will include only <code>Service</code> objects
 with one or more corresponding <code>ServiceDetails</code>,
 <code>sd</code>, such that:
 <ul>
 <li> <code>sd</code> is contained by
 the specified <code>SIElement</code>, or
 <li><code>sd</code>
 contains the specified <code>SIElement</code>
 </ul>
 -- according to the
 type of <code>SIElement</code> provided.  Note that no guarantee
 is made that every <code>SIElement</code> type is supported for
 filtering.

*/
public final class SIElementFilter extends ServiceFilter {

/*
 
 Constructs the filter based on a particular SIElement . 
 Parameters:  element - An SIElement indicating the services
 to be included in a resulting service list. Throws:  FilterNotSupportedException  - If element is
 not supported for filtering. 
 
 */

SIElement element;
Filter filter;

interface Filter {
   public boolean accept ( javax.tv.service.Service service);
}

class NetworkFilter implements Filter {
   int networkId;
   NetworkFilter(int networkId) {
      this.networkId=networkId;
   }

   public boolean accept ( javax.tv.service.Service service) {
      return (service instanceof VDRService)
        && ((VDRService)service).getOriginalNetworkId()==networkId;
   }
}

class SameServiceFilter implements Filter {
   Service service;
   SameServiceFilter(Service service) {
      this.service=service;
   }
   public boolean accept ( javax.tv.service.Service s) {
      return service != null && service.equals(s);
   }
}

class TransportStreamFilter implements Filter {
   int tid;
   TransportStreamFilter(int tid) {
      this.tid=tid;
   }
   public boolean accept ( javax.tv.service.Service service) {
      return (service instanceof VDRService)
        && ((VDRService)service).getTransportStreamId()==tid;      
   }
}

public SIElementFilter ( SIElement element)
        throws FilterNotSupportedException {
   this.element=element;
   
   if (element instanceof Network) 
      filter=new NetworkFilter(((Network)element).getNetworkID());
   else if (element instanceof ServiceDetails)
      filter=new SameServiceFilter(((ServiceDetails)element).getService());
   else if (element instanceof ProgramEvent)
      filter=new SameServiceFilter(((ProgramEvent)element).getService());
   else if (element instanceof ServiceComponent)
      filter=new SameServiceFilter(((ServiceComponent)element).getService());
   else if (element instanceof TransportStream)
      filter=new TransportStreamFilter(((TransportStream)element).getTransportStreamID());
   //we have no access to bouquet ID in VDRService, so Bouquet is not supported
   else {
      filter=null; 
      throw new FilterNotSupportedException();
   }
}


/*
 
 Reports the SIElement used to create this filter. 
 Returns: The SIElement used to create this filter. 
 
 
 */

public SIElement  getFilterValue (){
   return element;
}


/*
 
 Tests if the given service passes the filter. 
 Overrides:  accept  in class  ServiceFilter  
 
 
 Parameters:  service - An individual Service to be evaluated
 against the filtering algorithm. Returns:  true if service has a
 corresponding ServiceDetails which contains or
 is contained by the SIElement indicated
 by the filter value; false otherwise. 
 
 
*/

public boolean accept ( javax.tv.service.Service service){
   return service!=null && filter!=null && filter.accept(service);
}



}

