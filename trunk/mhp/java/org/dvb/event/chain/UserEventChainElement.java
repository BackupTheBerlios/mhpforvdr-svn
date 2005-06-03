package org.dvb.event.chain;
import org.dvb.event.UserEvent;
import org.dvb.event.UserEventListener;



public class UserEventChainElement extends FilterChainElement {
UserEvent event;
UserEventListener listener;

public UserEventChainElement(UserEvent event, UserEventListener listener) {
   this.event=event;
   this.listener=listener;
}

void dispatch(UserEvent e) {
   listener.userEventReceived(e);
}

boolean includes(UserEvent e) {
   return event.equals(e);
}

boolean belongsTo(Object o) {
   return (o instanceof UserEventListener) ? ((UserEventListener)o)==listener : false;
}

}
