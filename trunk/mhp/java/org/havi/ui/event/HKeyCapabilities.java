
package org.havi.ui.event;

/*This class is used to describe the (basic)keyboard capabilities of the platform. This 
class is not intended to be constructed by applications.The parameters to the constructors 
are as follows,in cases where parameters are not used,then the constructor should use the 
default values. */

//TODO: adding support for keyboard requires functionality in the plugin on VDR's side,
//      in the messaging library and the event dispatching routines.

public class HKeyCapabilities {

/*
It is not intended that applications should directly construct HKeyCapabilities objects. Creates an HKeyCapabilities 
object.See the class description for details of constructor parameters and default values. This method is protected to 
allow the platform to override it in a different package scope. */
protected HKeyCapabilities() {
}

/*
Determine if keyboard input functionality exists in the system.Note that this functionality may be provided through a 
"virtual"keyboard. Returns: true if keyboard input functionality exists in the system,false 
otherwise. */
public static boolean getInputDeviceSupported() {
   return false;
}

/*
Queries whether the system keyboard can generate an event of the given type.Note that this method does not distinguish 
between key codes which can only be generated via a virtual keyboard and key codes generated as a result of "real"key 
presses. Parameters: keycode -the keycode to query e.g.VK_SPACE Returns: true if events with the given key code can 
(ever)be generated on this system,false otherwise. */
public static boolean isSupported(int keycode) {
   return false;
}


}
