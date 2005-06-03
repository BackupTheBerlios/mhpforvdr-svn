
package javax.tv.service;

/*

This interface is implemented by objects that are retrieved from SI
 data in the broadcast.

*/
public interface SIRetrievable {

/*
 
 Returns the time when this object was last updated from data in
 the broadcast. 
 Returns: The date of the last update in UTC format, or null 
 if unknown. 
 
 
*/

public java.util.Date getUpdateTime ();



}

