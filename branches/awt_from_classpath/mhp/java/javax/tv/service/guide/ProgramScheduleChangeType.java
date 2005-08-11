
package javax.tv.service.guide;

/*

This class represents types of changes to program schedules.

*/
public class ProgramScheduleChangeType extends javax.tv.service.SIChangeType {

/*
 
 ProgramScheduleChangeType indicating that the
 current program event has changed. */

public static final ProgramScheduleChangeType  CURRENT_PROGRAM_EVENT = new ProgramScheduleChangeType("CURRENT_PROGRAM_EVENT");


/*
 
 Creates an ProgramScheduleChangeType object. 
 Parameters:  name - The string name of this type (e.g. "CURRENT_PROGRAM_EVENT"). 
 
 */

protected ProgramScheduleChangeType (java.lang.String name){
   super(name);
}


/*
 
 Provides the string name of the type. For the type objects
 defined in this class, the string name will be identical to the
 class variable name. 
 Overrides:  toString  in class  SIChangeType  
 
 
 Returns: The string name of the type. 
 
 
*/

public java.lang.String toString (){
   return super.toString();
}



}

