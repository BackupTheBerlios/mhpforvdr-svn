
package javax.tv.service.navigation;

import org.davic.net.dvb.DvbLocator;
import javax.tv.service.VDRService;

/*

<code>LocatorFilter</code> represents a <code>ServiceFilter</code>
 based on a set of locators.  A <code>ServiceList</code> resulting
 from this filter will include only services matching the specified
 locators.

*/
public final class LocatorFilter extends ServiceFilter {

/*
 
 Constructs the filter based on a set of locators. 
 Parameters:  locators - An array of locators representing services
 to be included in a resulting ServiceList . Throws:  InvalidLocatorException  - If one of the given
 locators does not reference a valid
 Service . 
 
 */
 
javax.tv.locator.Locator [] locators;

public LocatorFilter ( javax.tv.locator.Locator [] locators)
       throws javax.tv.locator.InvalidLocatorException {
   if (locators==null || locators.length==0)
      throw new javax.tv.locator.InvalidLocatorException(null);
   this.locators=locators;
   for (int i=0;i<locators.length;i++) {
      if (!(locators[i] instanceof DvbLocator))
         throw new javax.tv.locator.InvalidLocatorException(locators[i]);
      if (((DvbLocator)locators[i]).getServiceId()==-1)
         throw new javax.tv.locator.InvalidLocatorException(locators[i]);
   }
}


/*
 
 Reports the locators used to create this filter. 
 Returns: The array of locators used to create this filter. 
 
 
 */

public javax.tv.locator.Locator [] getFilterValue (){
   return locators;
}


/*
 
 Tests if the given service passes the filter. 
 Overrides:  accept  in class  ServiceFilter  
 
 
 Parameters:  service - An individual Service to be evaluated
 against the filtering algorithm. Returns:  true if service belongs to the
 set of locators indicated by the filter value; false 
 otherwise. 
 
 
*/

public boolean accept ( javax.tv.service.Service service){
   if (service==null || locators==null || !(service instanceof VDRService))
      return false;
   VDRService s=(VDRService)service;
   for (int i=0;i<locators.length;i++) {
      DvbLocator loc=(DvbLocator)locators[i];
      if (    s.getServiceId()==loc.getServiceId()
           && s.getOriginalNetworkId()==loc.getOriginalNetworkId() )
         return true;
   }
   return false;
}



}

