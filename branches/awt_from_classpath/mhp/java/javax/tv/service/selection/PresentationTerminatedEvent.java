
package javax.tv.service.selection;

/*

<code>PresentationTerminatedEvent</code> is generated when the
 presentation of a service terminates.  This includes both normal
 termination (e.g., due to an application calling the <code>stop()</code>
 method) and abnormal termination (e.g., due to some change in the
 environment). Examples of abnormal termination include:

 <ul>
 <li>a tuning operation making the service unavailable </li>

 <li>removal of fundamental resources required to present the
 service</li>

 <li>withdrawal of CA authorization</li>
 </ul>

 <code>PresentationTerminatedEvent</code> is also generated
 following a <code>SelectionFailedEvent</code> either if the service
 context was not previously in the <em>presenting</em> state or if
 recovery of what was being presented previously is not possible.
 <code>PresentationTerminatedEvent</code> is only generated when no
 components of the requested service can be presented.  <p>

 Once this event has been generated, a <code>ServiceContext</code>
 will be in the <em>not presenting</em> state until a call to a
 <code>select()</code> method succeeds.  When this event is
 generated, all resources used for the presentation have been
 released, and <code>ServiceContentHandler</code> instances
 previously associated with the <code>ServiceContext</code> will
 have ceased presentation of their content.

*/
public class PresentationTerminatedEvent extends ServiceContextEvent {

/*
 
 Reason code : The service vanished from the network. 
 */

public static final int SERVICE_VANISHED = 1;


/*
 
 Reason code : Tuning made the service unavailable. 
 */

public static final int TUNED_AWAY = 2;


/*
 
 Reason code : Resources needed to present the service have been removed. 
 */

public static final int RESOURCES_REMOVED = 3;


/*
 
 Reason code : Access to the service or some component of it has been
 withdrawn by the system. An example of this is the end of a free 
 preview period for IPPV content. 
 */

public static final int ACCESS_WITHDRAWN = 4;


/*
 
 Reason code : The user requested that the presentation be stopped. */

public static final int USER_STOP = 5;


/*
 
 Constructs the event with a reason code. 
 Parameters:  source - The ServiceContext that generated the event. reason - The reason for which the presentation was terminated. 
 
 */
int reason;
 
public PresentationTerminatedEvent ( ServiceContext source,
                  int reason){
   super(source);
   this.reason=reason;
}


/*
 
 Reports the reason for which the presentation was terminated. 
 Returns: A reason code for why the presentation was terminated. 
 
 
*/

public int getReason (){
   return reason;
}



}

