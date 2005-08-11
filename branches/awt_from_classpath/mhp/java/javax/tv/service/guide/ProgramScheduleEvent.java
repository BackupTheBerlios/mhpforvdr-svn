
package javax.tv.service.guide;

/*

A <code>ProgramScheduleEvent</code> notifies an
 <code>ProgramScheduleListener</code> of changes to program events
 detected in a <code>ProgramSchedule</code>.  Specifically, this
 event signals the addition, removal, or modification of a
 <code>ProgramEvent</code> in a <code>ProgramSchedule</code>, or a
 change to the <code>ProgramEvent</code> that is current.<p>

 The class <code>ProgramScheduleChangeType</code> defines the kinds
 of changes reported by <code>ProgramScheduleEvent</code>.  A
 <code>ProgramScheduleChangeType</code> of
 <code>CURRENT_PROGRAM_EVENT</code> indicates that the current
 <code>ProgramEvent</code> of a <code>ProgramSchedule</code> has
 changed in identity.

*/
public class ProgramScheduleEvent extends javax.tv.service.SIChangeEvent {

/*
 
 Constructs a ProgramScheduleEvent . 
 Parameters:  schedule - The schedule in which the change occurred. type - The type of change that occurred. e - The ProgramEvent that changed. 
 
 */

public ProgramScheduleEvent ( ProgramSchedule schedule,
               javax.tv.service.SIChangeType type,
               ProgramEvent e){
   super(schedule, type, e);
}


/*
 
 Reports the ProgramSchedule that generated the
 event. The object returned will be identical to the object
 returned by the inherited EventObject.getSource() 
 method. 
 Returns: The ProgramSchedule that generated the event. See Also:  EventObject.getSource() 
 
 
 */

public ProgramSchedule  getProgramSchedule (){
   return (ProgramSchedule)getSource();
}


/*
 
 Reports the ProgramEvent that changed. If the
 ProgramScheduleChangeType is
 CURRENT_PROGRAM_EVENT , the ProgramEvent 
 that became current will be returned. The object returned will
 be identical to the object returned by inherited
 SIChangeEvent.getSIElement method. 
 Returns: The ProgramEvent that changed. See Also:   SIChangeEvent.getSIElement()  
 
 
*/

public ProgramEvent  getProgramEvent (){
   return (ProgramEvent)getSIElement();
}



}

