
package javax.tv.service.guide;

import org.dvb.si.SIServiceImpl;
import org.dvb.si.SIService;

/*

This interface represents a collection of program events for a given
 service ordered by time. It provides the current, next and future
 program events.<p>

 Note that all time values are in UTC time.

*/
public class DVBProgramSchedule implements ProgramSchedule {

//javax.tv.service.navigation.ServiceDetails service;
SIServiceImpl service;

public DVBProgramSchedule(SIServiceImpl service) {
   this.service=service;
}

/*
 
 Retrieves the current ProgramEvent . The resulting
 ProgramEvent is available for immediate viewing. */

public javax.tv.service.SIRequest  retrieveCurrentProgramEvent ( javax.tv.service.SIRequestor requestor) {
   javax.tv.service.OrgDvbSiRequestAdapter req=new javax.tv.service.OrgDvbSiRequestAdapter(requestor);
   req.setRequest(service.retrievePresentSIEvent(SIService.FROM_CACHE_OR_STREAM, null, req, null));
   return req;   
}


/*
 
 Retrieves the program event for the specified time. The
 specified time will fall between the resulting program event's
 start time (inclusive) and end time (exclusive). */

public javax.tv.service.SIRequest  retrieveFutureProgramEvent (java.util.Date time,
                       javax.tv.service.SIRequestor requestor)
                   throws javax.tv.service.SIException 
{
   javax.tv.service.OrgDvbSiRequestAdapter req=new javax.tv.service.OrgDvbSiRequestAdapter(requestor);
   req.setRequest(service.retrieveScheduledSIEvents(SIService.FROM_CACHE_OR_STREAM, null, req, null, time, time));
   return req;   
}


/*
 
 Retrieves all known program events on this service for the
 specified time interval. A program event pe is
 retrieved by this method if the time interval from
 pe.getStartTime() (inclusive) to
 pe.getEndTime() (exclusive) intersects the time
 interval from begin (inclusive) to end 
 (exclusive) specified by the input parameters. */

public javax.tv.service.SIRequest  retrieveFutureProgramEvents (java.util.Date begin,
                       java.util.Date end,
                       javax.tv.service.SIRequestor requestor)
                   throws javax.tv.service.SIException
{
   javax.tv.service.OrgDvbSiRequestAdapter req=new javax.tv.service.OrgDvbSiRequestAdapter(requestor);
   req.setRequest(service.retrieveScheduledSIEvents(SIService.FROM_CACHE_OR_STREAM, null, req, null, begin, end));
   return req;   
}

/*
 
 Retrieves a event which follows the specified event. */

public javax.tv.service.SIRequest  retrieveNextProgramEvent ( ProgramEvent event,
                      javax.tv.service.SIRequestor requestor)
                  throws javax.tv.service.SIException
{
   //TODO: currently returns the "next" event, but not the event following the one given
   javax.tv.service.OrgDvbSiRequestAdapter req=new javax.tv.service.OrgDvbSiRequestAdapter(requestor);
   req.setRequest(service.retrieveFollowingSIEvent(SIService.FROM_CACHE_OR_STREAM, null, req, null));
   return req;   
}


/*
 
 Retrieves a program event matching the locator. Note that
 the event must be part of this schedule. 
 
 This method returns data asynchronously. 
 Parameters:  locator - Locator referencing the ProgramEvent 
 of interest. requestor - The SIRequestor to be notified
 when this retrieval operation completes. Returns: An SIRequest object identifying this
 asynchronous retrieval request. Throws:  InvalidLocatorException  - If locator does not
 reference a valid ProgramEvent in this
 ProgramSchedule . java.lang.SecurityException - If the caller does not have
 javax.tv.service.ReadPermission(locator) . See Also:   ProgramEvent , 
 ReadPermission  
 
 
 */

public javax.tv.service.SIRequest  retrieveProgramEvent ( javax.tv.locator.Locator locator,
                    javax.tv.service.SIRequestor requestor)
                throws javax.tv.locator.InvalidLocatorException ,
                   java.lang.SecurityException
{
   //TODO
   //must be implemented in libdvbsi, but shouldn't be too much work
   return null;
}


/*
 
 Registers a ProgramScheduleListener to be notified of
 changes to program events on this ProgramSchedule .
 Subsequent changes will be indicated through instances of
 ProgramScheduleEvent , with this
 ProgramSchedule as the event source and an
 SIChangeType of ADD ,
 REMOVE , MODIFY , or
 CURRENT_PROGRAM_EVENT . Only changes to
 ProgramEvent instances p for which the
 caller has
 javax.tv.service.ReadPermission(p.getLocator()) will
 be reported. */

public void addListener ( ProgramScheduleListener listener) {
   //TODO
}


/*
 
 Unregisters a ProgramScheduleListener . If the
 specified ProgramScheduleListener is not registered, no
 action is performed. 
 Parameters:  listener - A previously registered listener. 
 
 
 */

public void removeListener ( ProgramScheduleListener listener) {
   //TODO
}


/*
 
 Reports the transport-dependent locator referencing the service to
 which this ProgramSchedule belongs. Note that
 applications may use this method to establish the identity of
 a ProgramSchedule after it has changed. 
 Returns: The transport-dependent locator referencing the service to
 which this ProgramSchedule belongs. See Also:   ProgramScheduleEvent.getProgramSchedule()  
 
 
*/

public javax.tv.locator.Locator  getServiceLocator () {
   return service.getLocator();
}



}

