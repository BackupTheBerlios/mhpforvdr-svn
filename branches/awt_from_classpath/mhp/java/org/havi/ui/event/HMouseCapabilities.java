
package org.havi.ui.event;

/*This class is used to describe the (basic)mouse capabilities of the platform. This class 
is not intended to be constructed by applications.The parameters to the constructors are 
as follows,in cases where parameters are not used,then the constructor should use the 
default values. */

//TODO: Mouse input support might be available through directfb's driver.
//      In this case some event dispatching would have to be done (or not, will AWT do it already?)
//      and getInputDeviceSupported should know about it.


public class HMouseCapabilities {

/*
It is not intended that applications should directly construct HMouseCapabilities objects. Creates an HMouseCapabilities 
object.See the class description for details of constructor parameters and default values. This method is protected to 
allow the platform to override it in a different package scope. */
protected HMouseCapabilities() {
}

/*
Determine if a mouse exists in the system. Returns: true if a mouse exists in the system,false 
otherwise. */
public static boolean getInputDeviceSupported() {
   return false;
}


}
