
package org.havi.ui.event;

/*This event informs that a device on this HScreen has been reserved by an application or 
other entity in the system.The parameters to the constructors are as follows,in cases 
where parameters are not used, then the constructor should use the default 
values. */

public class HScreenDeviceReservedEvent extends org.davic.resources.ResourceStatusEvent {

/*
Creates an HScreenDeviceReservedEvent object.See the class description for details of constructor parameters and default 
values. Parameters: source -the HScreenDevice representing the device which has been 
reserved */
public HScreenDeviceReservedEvent(java.lang.Object source) {
   super(source);
}

/*
Returns the device that has been reserved Overrides: org.davic.resources.ResourceStatusEvent.getSource()in class 
org.davic.resources.ResourceStatusEvent Returns: an HScreenDevice representing the device that has been 
reserved */
public java.lang.Object getSource() {
   return super.getSource();
}


}
