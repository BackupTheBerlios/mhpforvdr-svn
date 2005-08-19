package org.dvb.event.chain;

import org.dvb.application.MHPApplication;
import vdr.mhp.ApplicationManager;
import org.dvb.event.UserEvent;
import org.dvb.event.RepositoryDescriptor;
import java.awt.MHPPlane;
import java.awt.KeyboardFocusManager;
import java.awt.Component;
import java.awt.event.KeyEvent;

public class ExclusiveAWTChainElement extends FilterChainElement implements ExclusiveFilterElement {

int mask;
MHPApplication app;
RepositoryDescriptor descriptor;
UserEvent event;
MHPPlane scene;

public ExclusiveAWTChainElement(UserEvent event, RepositoryDescriptor descriptor) {
   this.event=event;
   this.descriptor=descriptor;
   mask=getMask(event.getType());
   app=ApplicationManager.getManager().getApplicationFromStack();
}

public void addType(int type) {
   mask |= getMask(type);
}

public RepositoryDescriptor getRepositoryDescriptor() {
   return descriptor;
}

public void dispatch(UserEvent e) {
   if ( (getMask(e.getType()) & mask) != 0) {
      KeyboardFocusManager manager;
      manager = KeyboardFocusManager.getCurrentKeyboardFocusManager ();
      Component focusComponent = manager.getFocusOwner();
      if (focusComponent==null)
         return;
      if (MHPPlane.getApplication(focusComponent) == app)
         dispatchAWT(e.getAWTEvent());
   }
}

/*
"Where a repository includes a KEY_TYPED event without 
the corresponding KEY_PRESSED and KEY_RELEASED events (excluding 
KEY_PRESSED or KEY_RELEASED events for modi  ers),when 
an exclusive reservations is requested,it shall also 
be made for those corresponding KEY_PRESSED and KEY_RELEASED events 
but only the requested event shall be received by the listener."
*/

boolean includes(UserEvent e) {
   return event.sameKey(e);
}

boolean belongsTo(Object o) {
   return (o instanceof org.davic.resources.ResourceClient) 
            ?  ((org.davic.resources.ResourceClient)o)==descriptor.getClient() : false;
}

void dispatchAWT(KeyEvent evt) {
   Object source = evt.getSource();

   if (source instanceof Component)
   {
      Component srccmp = (Component) source;
      srccmp.dispatchEvent(evt);
   }
}

}
