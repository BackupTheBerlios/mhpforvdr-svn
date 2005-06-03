
package javax.tv.service.transport;

/*

This interface provides information about a transport stream.

*/
public interface TransportStream extends javax.tv.service.SIElement {

/*
 
 Reports the ID of this transport stream. 
 
 
 
 Returns: A number identifying this transport stream. 
 
 
 */

public int getTransportStreamID ();


/*
 
 Reports the textual name or description of this transport stream. 
 
 
 
 Returns: A string representing the name of this transport stream, or
 an empty string if no information is available. 
 
 
*/

public java.lang.String getDescription ();



}

