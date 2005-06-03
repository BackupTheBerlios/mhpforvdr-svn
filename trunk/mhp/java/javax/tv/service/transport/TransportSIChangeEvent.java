
package javax.tv.service.transport;
/*

An <code>TransportSIChangeEvent</code> notifies an
 <code>SIChangeListener</code> of changes detected to the SI on a
 <code>Transport</code>.<p>

 Subtypes <code>ServiceDetailsChangeEvent</code>,
 <code>TransportStreamChangeEvent</code>,
 <code>NetworkChangeEvent</code> and <code>BouquetChangeEvent</code>
 are used to signal changes to service details, transport streams,
 networks and bouquets, respectively.  Changes to program events are
 signaled through <code>ProgramScheduleChangeEvent</code>.

*/
public abstract class TransportSIChangeEvent extends javax.tv.service.SIChangeEvent {

/*
 
 Constructs an TransportSIChangeEvent . 
 Parameters:  transport - The Transport on which the change
 occurred. type - The type of change that occurred. e - The SIElement that changed. 
 
 */

public TransportSIChangeEvent ( Transport transport,
                javax.tv.service.SIChangeType type,
                javax.tv.service.SIElement e){
   super(transport, type, e);
}


/*
 
 Reports the Transport that generated the event. It
 will be identical to the object returned by the
 getSource() method. 
 Returns: The Transport that generated the event. 
 
 
*/

public Transport  getTransport (){
   return (Transport)getSource();
}



}

