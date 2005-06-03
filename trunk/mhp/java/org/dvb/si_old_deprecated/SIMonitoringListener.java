
package org.dvb.si;

/*This interface shall be implemented by using application classes in order to listen to 
changes in monitored SI objects. */

public interface SIMonitoringListener extends java.util.EventListener {

/*
This method is called back by the SI API implementation to notify the listener about an event. Parameters: anEvent - The 
noti ed event. See Also: SIMonitoringEvent */
public void postMonitoringEvent(SIMonitoringEvent anEvent);



}
