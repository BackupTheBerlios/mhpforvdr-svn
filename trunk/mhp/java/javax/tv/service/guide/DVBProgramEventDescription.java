package javax.tv.service.guide;

// DEPRECATED

//a trivial implementation - created by SIEventImpl with the data of that object
public class DVBProgramEventDescription implements ProgramEventDescription {

String descr;
java.util.Date updateTime;

public DVBProgramEventDescription(String descr, java.util.Date updateTime) {
   this.descr=descr;
   this.updateTime=updateTime;
}

/*
 
 Provides a textual description of the ProgramEvent . 
 
 
 
 Returns: A textual description of the ProgramEvent ,
 or an empty string if no description is available. 
 
 
*/

public java.lang.String getProgramEventDescription () {
   return descr;
}

public java.util.Date getUpdateTime () {
   return updateTime;
}


}