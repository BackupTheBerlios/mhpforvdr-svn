
package org.dvb.dsmcc;

/*This event indicates that the asynchronous loading was successful. */

public class SuccessEvent extends AsynchronousLoadingEvent {

/*
Creates a SuccessEvent object. Parameters: o - the DSMCCObject which was successfully 
loaded. */
public SuccessEvent(DSMCCObject o) {
   super(o);
}

/*
Returns the DSMCCObject which was successfully loaded. Overrides: getSource() in class AsynchronousLoadingEvent Returns: 
the loaded DSMCCObject */
public java.lang.Object getSource() {
   return super.getSource();
}


}
