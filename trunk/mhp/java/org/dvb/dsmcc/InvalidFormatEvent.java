
package org.dvb.dsmcc;

/*This event is generated if the format of the data received is 
inconsistent. */

public class InvalidFormatEvent extends AsynchronousLoadingEvent {

/*
Create an InvalidFormatException object. Parameters: o - the DSMCCObject that generated the 
event. */
public InvalidFormatEvent(DSMCCObject o) {
   super(o);
}

/*
Returns the DSMCCObject that generated the event Overrides: getSource() in class AsynchronousLoadingEvent Returns: the 
DSMCCObject that generated the event */
public java.lang.Object getSource() {
   return super.getSource();
}


}
