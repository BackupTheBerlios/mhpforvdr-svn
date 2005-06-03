
package org.dvb.dsmcc;

/*This event will be sent to the AsynchronousEventListener when an asynchronous loading 
operation is aborted. */

public class LoadingAbortedEvent extends AsynchronousLoadingEvent {

/*
Creates a LoadingAbortedEvent object. Parameters: aDSMCCObject - the DSMCCObject that generated the 
event. */
public LoadingAbortedEvent(DSMCCObject o) {
   super(o);
}

/*
Returns the DSMCCObject that generated the event. Overrides: getSource() in class 
AsynchronousLoadingEvent */
public java.lang.Object getSource() {
   return super.getSource();
}


}
