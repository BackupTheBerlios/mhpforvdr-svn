package org.dvb.event.chain;

import org.dvb.event.UserEvent;
import org.dvb.event.UserEventListener;
import org.dvb.event.RepositoryDescriptor;


public class ExclusiveUserEventChainElement extends UserEventChainElement implements ExclusiveFilterElement {

int mask;
RepositoryDescriptor descriptor;

public ExclusiveUserEventChainElement(UserEvent event, UserEventListener listener, RepositoryDescriptor descriptor) {
   super(event, listener);
   mask=getMask(event.getType());
   this.descriptor=descriptor;
}

public RepositoryDescriptor getRepositoryDescriptor() {
   return descriptor;
}

public void addType(int type) {
   mask |= getMask(type);
}

public void dispatch(UserEvent e) {
   if ( (getMask(e.getType()) & mask) != 0)
       listener.userEventReceived(e);  
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
   return e.sameKey(e);
}


}
