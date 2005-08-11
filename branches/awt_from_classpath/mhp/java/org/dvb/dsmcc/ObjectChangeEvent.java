
package org.dvb.dsmcc;

/*This class describes an object change event that is used to monitor the arrival of a new 
version of a DSMCCObject. For  les carried in a DSMCC object carousel, when a change in a 
module is detected, this event shall be sent to all registered listeners for all objects 
carried in that module. */

public class ObjectChangeEvent extends java.util.EventObject {

int versionNumber;
/*
Creates an ObjectChangeEvent indicating that a new version of the monitored DSMCC Object has been detected. It is up to 
the application to reload the new version of the object. Parameters: source - the DSMCCObject whose version has changed 
aVersionNumber - the new version number. */
public ObjectChangeEvent(DSMCCObject source, int aVersionNumber) {
   super(source);
   versionNumber=aVersionNumber;
}

/*
This method is used to get the new version number of the monitored DSMCCObject. For  les carried in a DSMCC object 
carousel, this method shall return the version number of the module carrying the  le. Returns: the new version 
number. */
public int getNewVersionNumber() {
   return versionNumber;
}

/*
Returns the DSMCCObject that has changed Overrides: java.util.EventObject.getSource() in class java.util.EventObject 
Returns: the DSMCCObject that has changed */
public java.lang.Object getSource() {
   return super.getSource();
}


}
