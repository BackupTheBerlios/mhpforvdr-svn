
package org.dvb.dsmcc;

/*The objects that implements the ObjectChangeEventListener interface can receive 
ObjectChangeEvent event. */

public interface ObjectChangeEventListener extends java.util.EventListener {

/*
Send a ObjectChangeEvent to the ObjectChangeEventListener. Parameters: e - the ObjectChangeEvent 
event. */
public void receiveObjectChangeEvent(ObjectChangeEvent e);



}
