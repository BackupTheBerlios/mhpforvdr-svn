
package org.dvb.dsmcc;

/*This class described an Object event which is used to notify the loading of a DSMCC 
object. */

public abstract class AsynchronousLoadingEvent extends java.util.EventObject {

/*
Creates an AsynchronousLoadingEvent. Parameters: o - the DSMCCObject that generated the 
event. */
public AsynchronousLoadingEvent(DSMCCObject o) {
   super(o);
}

/*
Returns the DSMCCObject that generated the event. Overrides: java.util.EventObject.getSource() in class 
java.util.EventObject Returns: the DSMCCObject that generated the event. */
public java.lang.Object getSource() {
   return super.getSource();
}


}
