
package javax.tv.service.navigation;

/*

This interface associates information related to
 the conditional access (CA) subsystem with certain SI objects.

*/
public interface CAIdentification {

/*
 
 Returns an array of CA System IDs associated with this object. This
 information may be obtained from the CAT MPEG message or a system
 specific conditional access descriptor (such as defined by Simulcrypt
 or ATSC). 
 Returns: An array of CA System IDs. An empty array is returned when no
 CA System IDs are available. 
 
 
 */

public int[] getCASystemIDs ();


/*
 
 Provides information concerning conditional access of this object. 
 Returns:  true if this Service is not protected by a
 conditional access; false if one or more components
 is protected by conditional access. 
 
 
*/

public boolean isFree ();



}

