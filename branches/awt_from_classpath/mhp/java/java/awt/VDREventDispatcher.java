
package java.awt;
import org.havi.ui.event.HRcEvent;


//inherits constants from our non-standard KeyConstants interface
public class VDREventDispatcher implements java.awt.event.KeyConstants {

//This is done here, and we must keep track with changes in VDR's keys.h.
//It it is done in native code, we must keep track with Java constants, but here
// we have access to KeyConstants.
//If we use an intermediate key code, we have to convert twice, which introduces
// more possibilities for errors and information loss.

//Some of these keys are never passed to plugins, but we keep them here anyway, doesn't hurt.
     // a pity only Java 1.5 has enums...
final public static int kUp = 0;
final public static int kDown = 1;
final public static int kMenu = 2;
final public static int kOk = 3;
final public static int kBack = 4;
final public static int kLeft = 5;
final public static int kRight = 6;
final public static int kRed = 7;
final public static int kGreen = 8;
final public static int kYellow = 9;
final public static int kBlue = 10;
final public static int k0 = 11;
final public static int k1 = k0+1; 
final public static int k2 = k0+2;
final public static int k3 = k0+3;
final public static int k4 = k0+4;
final public static int k5 = k0+5;
final public static int k6 = k0+6;
final public static int k7 = k0+7;
final public static int k8 = k0+8;
final public static int k9 = k0+9;
final public static int kPlay = 21;
final public static int kPause = 22;
final public static int kStop = 23;
final public static int kRecord = 24;
final public static int kFastFwd = 25;
final public static int kFastRew = 26;
final public static int kPower = 27;
final public static int kChanUp = 28;
final public static int kChanDn = 29;
final public static int kVolUp = 30;
final public static int kVolDn = 31;
final public static int kMute = 32;
final public static int kNone = 48;

public static void dispatchKey(int eKey) {
   int key;
   switch (eKey) {
      case kUp:
         key=VK_UP;
         break;
      case kDown:
         key=VK_DOWN;
         break;
      //kMenu
      case kOk:
         key=VK_ENTER; //?
         break;
      //Don't know what to assign to kBack - perhaps better not to dispatch it at all
      //case kBack:
      //   key=;
      //   break;
      case kLeft:
         key=VK_LEFT;
         break;
      case kRight:
         key=VK_RIGHT;
         break;
      //the mapping of the colored keys is in accordance to
      //org.havi.ui.event.HRcCapabilities
      case kRed:
         key=VK_COLORED_KEY_0;
         break;
      case kGreen:
         key=VK_COLORED_KEY_1;
         break;
      case kYellow:
         key=VK_COLORED_KEY_2;
         break;
      case kBlue:
         key=VK_COLORED_KEY_3;
         break;
      case k0:
         key=VK_0;
         break;
      case k1:
         key=VK_1;
         break;
      case k2:
         key=VK_2;
         break;
      case k3:
         key=VK_3;
         break;
      case k4:
         key=VK_4;
         break;
      case k5:
         key=VK_5;
         break;
      case k6:
         key=VK_6;
         break;
      case k7:
         key=VK_7;
         break;
      case k8:
         key=VK_8;
         break;
      case k9:
         key=VK_9;
         break;
      case kPlay:
         key=VK_PLAY;
         break;
      case kPause:
         key=VK_PAUSE;
         break;
      case kStop:
         key=VK_STOP;
         break;
      case kRecord:
         key=VK_RECORD;
         break;
      case kFastFwd:
         key=VK_FAST_FWD;
         break;
      case kFastRew:
         key=VK_REWIND;
         break;
      //kPower;
      case kChanUp:
         key=VK_CHANNEL_UP;
         break;
      case kChanDn:
         key=VK_CHANNEL_DOWN;
         break;
      case kVolUp:
         key=VK_VOLUME_UP;
         break;
      case kVolDn:
         key=VK_VOLUME_DOWN;
         break;
      case kMute:
         key=VK_MUTE;
         break;
      case kNone:
         return; //this is no real key
      default:
         System.out.println("java.awt.VDREventDispatcher: Unknown VDR key "+eKey);
         return; //unknown;
   }
   
   
   long millis=System.currentTimeMillis(); //use current time
   java.awt.Component comp = findKeyTarget();
   if (comp==null)
      return;
      
   Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                                 new HRcEvent(comp,
                                             HRcEvent.KEY_PRESSED,
                                             millis,
                                             0, //no modifier pressed
                                             key
                                             )
                               );
   Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                                 new HRcEvent(comp,
                                             HRcEvent.KEY_RELEASED,
                                             millis,
                                             0, //no modifier pressed
                                             key
                                             )
                               );
}

static java.awt.Component findKeyTarget() {
   KeyboardFocusManager manager;
   manager = KeyboardFocusManager.getCurrentKeyboardFocusManager ();
   Component focusComponent = manager.getGlobalFocusOwner();
   if (focusComponent == null)
      System.out.println("java.awt.VDREventDispatcher: KeyboardFocusManager.getGlobalFocusOwner() is Null, don't know where to send VDR event!");
   return focusComponent;
}


}
