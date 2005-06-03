
package javax.tv.service.selection;

/*

<code>SelectionFailedEvent</code> is generated when a service
 selection operation fails.  <code>SelectionFailedEvent</code> is
 not generated when a service selection fails with an exception. <p>

 Presentation failures enforced via a conditional access system may
 be reported by this event (with the reason code CA_REFUSAL) or by
 <code>AlternativeContentEvent.</code> Which of these is used
 depends on the precise nature of the conditional access
 system. Applications must allow for both modes of failure.

*/
public class SelectionFailedEvent extends ServiceContextEvent {

/*
 
 Reason code : Selection has been interrupted by another selection
 request. 
 */

public static final int INTERRUPTED = 1;


/*
 
 Reason code : Selection failed due to the CA system refusing to
 permit it. 
 */

public static final int CA_REFUSAL = 2;


/*
 
 Reason code : Selection failed because the requested content
 could not be found in the network. 
 */

public static final int CONTENT_NOT_FOUND = 3;


/*
 
 Reason code : Selection failed due to absence of a 
 ServiceContentHandler required to present the requested
 service. 
 See Also:   ServiceContentHandler  
 
 
 */

public static final int MISSING_HANDLER = 4;


/*
 
 Reason code : Selection failed due to problems with tuning. 
 */

public static final int TUNING_FAILURE = 5;


/*
 
 Reason code : Selection failed due to a lack of resources required to
 present this service. */

public static final int INSUFFICIENT_RESOURCES = 6;


/*
 
 Constructs the event with a reason code. 
 Parameters:  source - The ServiceContext that generated the event. reason - The reason why the selection failed. 
 
 */

int reason;
 
public SelectionFailedEvent ( ServiceContext source,
              int reason){
   super(source);
   this.reason=reason;
}


/*
 
 Reports the reason why the selection failed. 
 Returns: The reason why the selection failed. 
 
 
*/

public int getReason (){
   return reason;
}



}

