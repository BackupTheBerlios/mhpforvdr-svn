package org.dvb.event.chain;
import org.dvb.event.UserEvent;


public abstract class FilterChainElement {

static public final int TYPE_MASK=0x01;
static public final int PRESS_MASK=0x02;
static public final int RELEASE_MASK=0x04;

FilterChainElement next=null;
FilterChainElement prev=null;



int getMask(int type) {
   switch (type) {
      case UserEvent.KEY_TYPED:
         return TYPE_MASK;
      case UserEvent.KEY_RELEASED:
         return RELEASE_MASK;
      case UserEvent.KEY_PRESSED:
         return PRESS_MASK;
   }
   return 0;
}


//actually dispatch the event according to the respective event subsystem
abstract void dispatch(UserEvent e);
//whether this event matches the filter criteria
abstract boolean includes(UserEvent e);
//whether this filter was registered by a client identified by object
abstract boolean belongsTo(Object o);


}
