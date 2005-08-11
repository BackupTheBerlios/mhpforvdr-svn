
package javax.tv.service.guide;

/*

This <code>SIElement</code> provides a textual description of a
 <code>ProgramEvent</code>.  In ATSC PSIP, this information is
 obtained from the Extended Text Table; in DVB SI, from the Short
 Event Descriptor.)

*/
public interface ProgramEventDescription extends javax.tv.service.SIRetrievable {

/*
 
 Provides a textual description of the ProgramEvent . 
 
 
 
 Returns: A textual description of the ProgramEvent ,
 or an empty string if no description is available. 
 
 
*/

public java.lang.String getProgramEventDescription ();



}

