
package javax.tv.service.transport;

/*

A <code>NetworkChangeEvent</code> notifies an
 <code>NetworkChangeListener</code> of changes detected in a
 <code>NetworkCollection</code>.  Specifically, this event
 signals the addition, removal, or modification of a
 <code>Network</code>.

*/
public class NetworkChangeEvent extends TransportSIChangeEvent {

/*
 
 Constructs a NetworkChangeEvent . 
 Parameters:  collection - The network collection in which the change
 occurred. type - The type of change that occurred. n - The Network that changed. 
 
 */

public NetworkChangeEvent ( NetworkCollection collection,
              javax.tv.service.SIChangeType type,
              Network n){
   super(collection, type, n);
}


/*
 
 Reports the NetworkCollection that generated the
 event. It will be identical to the object returned by the
 getTransport() method. 
 Returns: The NetworkCollection that generated the
 event. 
 
 
 */

public NetworkCollection  getNetworkCollection (){
   return (NetworkCollection)getSource();
}


/*
 
 Reports the Network that changed. It will be
 identical to the object returned by the inherited
 SIChangeEvent.getSIElement method. 
 Returns: The Network that changed. 
 
 
*/

public Network  getNetwork (){
   return (Network)getSIElement();
}



}

