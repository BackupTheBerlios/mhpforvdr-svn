package java.awt;

import org.dvb.event.EventManager;
import org.dvb.event.UserEvent;
import java.awt.event.KeyEvent;

/***
  MHP has three different Event APIs:
  - AWT
  - Havi (partially based on AWT)
  - org.dvb.event (fully independent)
  
  This class knows about this.
***/

public class MHPEventFilter {

public static void dispatch(AWTEvent e) {
   System.out.println("dispatching event "+e);
   //org.dvb.event currently only handles KeyEvent + similar
   if (e instanceof KeyEvent) {
      EventManager manager=EventManager.getInstance();
      if (manager.DispatchEvent(new UserEvent((KeyEvent)e)))
         return; //manager wants to handle this event exclusively
   }
   dispatchAWT(e);
}

public static void dispatchAWT(AWTEvent e) {
   //handles AWT+Havi via virtual overloading approach
   e.dispatch();
}

public static Component getFocusComponent() {
   return AWTEvent.keyTgt;
}

}
