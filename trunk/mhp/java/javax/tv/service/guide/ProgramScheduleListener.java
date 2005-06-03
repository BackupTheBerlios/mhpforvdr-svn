
package javax.tv.service.guide;

/*

This interface is implemented by applications wishing to receive
 notification of changes to <code>ProgramSchedule</code> data.

*/
public interface ProgramScheduleListener extends javax.tv.service.SIChangeListener {

/*
 
 Notifies the ProgramScheduleListener of a
 change to a ProgramSchedule . 
 
 
 
 Parameters:  event - A ProgramScheduleEvent 
 describing what changed and how. 
 
 
*/

public void notifyChange ( ProgramScheduleEvent event);



}

