
package javax.tv.service.transport;
import javax.tv.service.*;

/*

A <code>TransportStreamChangeEvent</code> notifies an
 <code>TransportStreamChangeListener</code> of changes detected in a
 <code>TransportStreamCollection</code>.  Specifically, this event
 signals the addition, removal, or modification of a
 <code>TransportStream</code>.

*/
public class TransportStreamChangeEvent extends TransportSIChangeEvent {

/*
 
 Constructs a TransportStreamChangeEvent . 
 Parameters:  collection - The transport stream collection in which the
 change occurred. type - The type of change that occurred. ts - The TransportStream that changed. 
 
 */

public TransportStreamChangeEvent ( TransportStreamCollection collection,
                  SIChangeType type,
                  TransportStream ts){
   super(collection, type, ts);
}


/*
 
 Reports the TransportStreamCollection that generated
 the event. It will be identical to the object returned by the
 getTransport() method. 
 Returns: The TransportStreamCollection that generated
 the event. 
 
 
 */

public TransportStreamCollection  getTransportStreamCollection (){
   return (TransportStreamCollection)getSource();
}


/*
 
 Reports the TransportStream that changed. It will be
 identical to the object returned by the inherited
 SIChangeEvent.getSIElement method. 
 Returns: The TransportStream that changed. 
 
 
*/

public TransportStream  getTransportStream (){
   return (TransportStream)getSIElement();
}



}

