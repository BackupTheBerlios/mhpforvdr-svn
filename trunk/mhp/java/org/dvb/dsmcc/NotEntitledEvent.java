
package org.dvb.dsmcc;

/*This event is sent when an attempt to asynchronously load an object has failed because the 
elementary stream carrying the object is scrambled and the user is not entitled to access 
the content of the object. */

public class NotEntitledEvent extends AsynchronousLoadingEvent {

/*
Creates a NotEntitledEvent object. Parameters: o - the DSMCCObject that generated the 
event. */
public NotEntitledEvent(DSMCCObject o) {
   super(o);
}

/*
Returns the DSMCCObject that generated the event. Overrides: getSource() in class AsynchronousLoadingEvent Returns: the 
DSMCCObject that generated the event. */
public java.lang.Object getSource() {
   return super.getSource();
}


}
