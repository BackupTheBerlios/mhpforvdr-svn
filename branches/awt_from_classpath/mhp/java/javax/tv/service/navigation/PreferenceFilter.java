
package javax.tv.service.navigation;

/*

<code>PreferenceFilter</code> represents a
 <code>ServiceFilter</code> based on a user preference for favorite
 services. A <code>ServiceList</code> resulting from this filter
 will include only user favorite services contained in the specified
 preference.

*/

//I do not know if this class can be implemented in VDR context - 
// maybe we list the channel group names as FavoriteServicesNames

public final class PreferenceFilter extends ServiceFilter {

/*
 
 Constructs the filter based on a particular user preference
 for favorite services. 
 Parameters:  preference - A named user preference, obtained from
 the listPreferences() method, representing favorite
 Services to be included in a resulting service list. Throws:  java.lang.IllegalArgumentException - If the specified preference is
 not obtainable from the listPreferences() method. See Also:   listPreferences()  
 
 */
 
FavoriteServicesName preference;

public PreferenceFilter ( FavoriteServicesName preference){
   this.preference=preference;
}


/*
 
 Reports the available favorite service preferences that
 can be used to create this filter. 
 Returns: An array of preferences for favorite services. If none
 exist or are supported, an empty array is returned. 
 
 
 */

public static FavoriteServicesName [] listPreferences (){
   return new FavoriteServicesName[0];
}


/*
 
 Reports the user preference used to create this filter. 
 Returns: The user preference representing the favorite services
 by which the filter was constructed. 
 
 
 */

public FavoriteServicesName  getFilterValue (){
   return preference;
}


/*
 
 Tests if the given service passes the filter. 
 Overrides:  accept  in class  ServiceFilter  
 
 
 Parameters:  service - An individual Service to be evaluated
 against the filtering algorithm. Returns:  true if service is part of the
 favorite services indicated by the filter value; false 
 otherwise. 
 
 
*/

public boolean accept ( javax.tv.service.Service service){
   return false;
}



}

