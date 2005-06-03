
package org.dvb.si;

/*This interface shall be implemented by application classes in order to receive events 
about completion of SI requests. */

public interface SIRetrievalListener extends java.util.EventListener {

/*
This method is called by the SI API implementation to notify the listener about completion of an SI request. Parameters: 
event - The event object. */
public void postRetrievalEvent(SIRetrievalEvent event);



}
