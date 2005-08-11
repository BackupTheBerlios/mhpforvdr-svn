
package org.havi.ui.event;

/*This event informs an application that a device for this HScreen has been released by an 
application or other entity in the system.The parameters to the constructors are as 
follows,in cases where parameters are not used,then the constructor should use the default 
values. */

public class HScreenDeviceReleasedEvent extends org.davic.resources.ResourceStatusEvent {

/*
Creates an HScreenDeviceReleasedEvent object.See the class description for details of constructor parameters and default 
values. Parameters: source -the HScreenDevice which has been released */
public HScreenDeviceReleasedEvent(java.lang.Object source) {
   super(source);
}

/*
Returns the device that has been released Overrides: org.davic.resources.ResourceStatusEvent.getSource()in class 
org.davic.resources.ResourceStatusEvent Returns: the HScreenDevice object representing the device that has been 
released */
public java.lang.Object getSource() {
   return super.getSource();
}


}
