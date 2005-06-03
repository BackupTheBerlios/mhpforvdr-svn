
package org.havi.ui.event;

import org.havi.ui.event.HRcEvent;

/*This class is used to describe the (basic)remote control capabilities of the platform. 
This class is not intended to be constructed by applications.The parameters to the 
constructors are as follows,in cases where parameters are not used,then the constructor 
should use the default values. */

//TODO: Internationalization of the strings. Maybe send a message to VDR to request translation.
//      Create symbols (java.awt.Image) for the various keys. The spec gives examples.

public class HRcCapabilities extends HKeyCapabilities {

/*
It is not intended that applications should directly construct HRcCapabilities objects. Creates an HRcCapabilities 
object.See the class description for details of constructor parameters and default values. This method is protected to 
allow the platform to override it in a different package scope. */
protected HRcCapabilities() {
}

/*
Determine if a physical remote control exists in the system. Returns: true if a physical remote control exists in the 
system,false otherwise. */
public static boolean getInputDeviceSupported() {
   return true;
}

/*
Get the HEventRepresentation object for a speci  ed key event id. Parameters: aCode -the key event id for which the 
HEventRepresentation should be returned. Returns: an HEventRepresentation object for the speci  ed key event id,or null 
if there is no valid representation available. */
public static HEventRepresentation getRepresentation(int aCode) {
   System.out.println("org.havi.ui.event.HRcCapabilities: Returning pictogramms is unimplemented, code is "+aCode);
   switch (aCode) {
   case HRcEvent.VK_COLORED_KEY_0:
      return new HEventRepresentation(java.awt.Color.red, "Red", null);
   case HRcEvent.VK_COLORED_KEY_1:
      return new HEventRepresentation(java.awt.Color.green, "Green", null);
   case HRcEvent.VK_COLORED_KEY_2:
      return new HEventRepresentation(java.awt.Color.yellow, "Yellow", null);
   case HRcEvent.VK_COLORED_KEY_3:
      return new HEventRepresentation(java.awt.Color.blue, "Blue", null);
   
   case HRcEvent.VK_0:
      return new HEventRepresentation(null, "0", null);
   case HRcEvent.VK_1:
      return new HEventRepresentation(null, "1", null);
   case HRcEvent.VK_2:
      return new HEventRepresentation(null, "2", null);
   case HRcEvent.VK_3:
      return new HEventRepresentation(null, "3", null);
   case HRcEvent.VK_4:
      return new HEventRepresentation(null, "4", null);
   case HRcEvent.VK_5:
      return new HEventRepresentation(null, "5", null);
   case HRcEvent.VK_6:
      return new HEventRepresentation(null, "6", null);
   case HRcEvent.VK_7:
      return new HEventRepresentation(null, "7", null);
   case HRcEvent.VK_8:
      return new HEventRepresentation(null, "8", null);
   case HRcEvent.VK_9:
      return new HEventRepresentation(null, "9", null);
   
   case HRcEvent.VK_RIGHT:
      return new HEventRepresentation(null, "Right", null);
   case HRcEvent.VK_LEFT:
      return new HEventRepresentation(null, "Left", null);
   case HRcEvent.VK_UP:
      return new HEventRepresentation(null, "Up", null);
   case HRcEvent.VK_DOWN:
      return new HEventRepresentation(null, "Down", null);
   
   case HRcEvent.VK_CHANNEL_UP:
      return new HEventRepresentation(null, "Channel+", null);
   case HRcEvent.VK_CHANNEL_DOWN:
      return new HEventRepresentation(null, "Channel-", null);
   
   case HRcEvent.VK_PLAY:
      return new HEventRepresentation(null, "Play", null);
   case HRcEvent.VK_STOP:
      return new HEventRepresentation(null, "Stop", null);
   case HRcEvent.VK_RECORD:
      return new HEventRepresentation(null, "Record", null);
   case HRcEvent.VK_FAST_FWD:
      return new HEventRepresentation(null, "Fast forward", null);
   case HRcEvent.VK_REWIND:
      return new HEventRepresentation(null, "Rewind", null);
   
   case HRcEvent.VK_POWER:
      return new HEventRepresentation(null, "Power", null);
   
   case HRcEvent.VK_VOLUME_DOWN:
      return new HEventRepresentation(null, "Volume-", null);
   case HRcEvent.VK_VOLUME_UP:
      return new HEventRepresentation(null, "Volume+", null);
   case HRcEvent.VK_MUTE:
      return new HEventRepresentation(null, "Mute", null);
   default:
      return null;
   }
}

/*
Queries whether the remote control can directly generate an event of the given type.Note that this method will return 
false for key codes which can only be generated on this system via a virtual keyboard. Parameters: keycode -the keycode 
to query e.g.HRcEvent.VK_SPACE Returns: true if events with the given key code can be directly generated on this system via a 
physical remote control,false otherwise. */
public static boolean isSupported(int keycode) {
   switch (keycode) {
   case HRcEvent.VK_COLORED_KEY_0:
   case HRcEvent.VK_COLORED_KEY_1:
   case HRcEvent.VK_COLORED_KEY_2:
   case HRcEvent.VK_COLORED_KEY_3:
   
   case HRcEvent.VK_0:
   case HRcEvent.VK_1:
   case HRcEvent.VK_2:
   case HRcEvent.VK_3:
   case HRcEvent.VK_4:
   case HRcEvent.VK_5:
   case HRcEvent.VK_6:
   case HRcEvent.VK_7:
   case HRcEvent.VK_8:
   case HRcEvent.VK_9:
   
   case HRcEvent.VK_RIGHT:
   case HRcEvent.VK_LEFT:
   case HRcEvent.VK_UP:
   case HRcEvent.VK_DOWN:
   
   case HRcEvent.VK_CHANNEL_UP:
   case HRcEvent.VK_CHANNEL_DOWN:
   
   case HRcEvent.VK_PLAY:
   case HRcEvent.VK_STOP:
   case HRcEvent.VK_RECORD:
   case HRcEvent.VK_FAST_FWD:
   case HRcEvent.VK_REWIND:
   
   case HRcEvent.VK_POWER:
   
   case HRcEvent.VK_VOLUME_DOWN:
   case HRcEvent.VK_VOLUME_UP:
   case HRcEvent.VK_MUTE:
      return true;
   default:
      return false;
   }
}


}
