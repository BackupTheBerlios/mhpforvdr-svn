
package org.dvb.event;

import org.dvb.event.UserEvent;
import org.dvb.event.EventManager;
import java.util.Vector;
import org.dvb.event.chain.*;

/*The application will use this class to de  ne the events that it wants to receive.Events that are able to be put in the 
repository are de  ned in the UserEvent class. Where a repository includes a KeyPressed type event without the 
KeyReleased type event for the same key code or vice versa then exclusive reservations shall be made for both event 
types but only the one requested shall be received by the listener.Where a repository includes a KEY_TYPED event without 
the corresponding KEY_PRESSED and KEY_RELEASED events (excluding KEY_PRESSED or KEY_RELEASED events for modi  ers),when 
an exclusive reservations is requested,it shall also be made for those corresponding KEY_PRESSED and KEY_RELEASED events 
but only the requested event shall be received by the listener. Repositories do not keep a count of the number of times 
a particular user event is added or removed. Repeatedly adding an event to a repository has no effect.Removing an event 
removes it regardless of the number of times it has been added.For example, 
org.dvb.event.UserEventRepository.addUserEvent(UserEvent event)does nothing in case that the event is already in the 
repository.Events are considered to be already in the repository if an event with the same triplet of family,type and 
code is already in the repository. Repositories are resolved when they are passed into the methods of 
EventManager.Adding or removing events from the repository after those method calls does not affect the subscription to 
those events. */


public class UserEventRepository extends java.lang.Object {
protected String RepositoryName;
protected Vector events;


/*
The method to construct a new UserEventRepository. Parameters: name -the name of the 
repository. */
public UserEventRepository(java.lang.String name) {
   RepositoryName=name;
   events=new Vector();
}

public UserEvent get(int index) {
   return (UserEvent)events.get(index);
}

/*
A shortcut to create a new key event type entry in the repository.If a key is already in the repository, this method has 
no effect. Parameters: keycode -the key code. */
public void addKey(int keycode) {
   UserEvent e=new UserEvent(EventManager.getInstance(), UserEvent.UEF_KEY_EVENT,
                              UserEvent.KEY_PRESSED, keycode, 0);
   addUserEvent(e);
}

/*
Adds the given user event to the repository. Parameters: event -the user event to be added in the 
repository. */
public void addUserEvent(UserEvent e) {
   events.add(e);
}


/*
The method to remove a key from the repository.Removing a key which is not in the repository has no effect. Parameters: 
keycode -the key code. */
public void removeKey(int keycode) {
   UserEvent e=new UserEvent(EventManager.getInstance(), UserEvent.UEF_KEY_EVENT,
                              UserEvent.KEY_PRESSED, keycode, 0);
   removeUserEvent(e);
}

/*
Remove a user event from the repository.Removing a user event which is not in the repository shall have no effect. 
Parameters: event -the event to be removed from the repository. */
public void removeUserEvent(UserEvent event) {
   for (int i=0; i<events.size(); i++) {
      if (((UserEvent)events.get(i)).equals(event)) {
         events.remove(i);
         return;
      }
   }
   //nothing found, nothing to remove
}





/*
Adds the key codes for the arrow keys (VK_LEFT,VK_RIGHT,VK_UP,VK_DOWN).Any key codes already in the repository will not 
be added again. */
public void addAllArrowKeys() {
   addKey(UserEvent.VK_LEFT);
   addKey(UserEvent.VK_RIGHT);
   addKey(UserEvent.VK_UP);
   addKey(UserEvent.VK_DOWN);
}

/*
Adds the key codes for the colour keys (VK_COLORED_KEY_0,VK_COLORED_KEY_1, VK_COLORED_KEY_2,VK_COLORED_KEY_3).Any key 
codes already in the repository will not be added again. */
public void addAllColourKeys() {
   addKey(UserEvent.VK_COLORED_KEY_0);
   addKey(UserEvent.VK_COLORED_KEY_1);
   addKey(UserEvent.VK_COLORED_KEY_2);
   addKey(UserEvent.VK_COLORED_KEY_3);
}

/*
Adds the key codes for the numeric keys (VK_0,VK_1,VK_2,VK_3,VK_4,VK_5,VK_6,VK_7, VK_8,VK_9).Any key codes already in 
the repository will not be added again. */
public void addAllNumericKeys() {
   addKey(UserEvent.VK_0);
   addKey(UserEvent.VK_1);
   addKey(UserEvent.VK_2);
   addKey(UserEvent.VK_3);
   addKey(UserEvent.VK_4);
   addKey(UserEvent.VK_5);
   addKey(UserEvent.VK_6);
   addKey(UserEvent.VK_7);
   addKey(UserEvent.VK_8);
   addKey(UserEvent.VK_9);
}

/*


Removes the key codes for the arrow keys (VK_LEFT,VK_RIGHT,VK_UP,VK_DOWN).Key codes from this set which are not present 
in the repository will be ignored. */
public void removeAllArrowKeys() {
   removeKey(UserEvent.VK_LEFT);
   removeKey(UserEvent.VK_RIGHT);
   removeKey(UserEvent.VK_UP);
   removeKey(UserEvent.VK_DOWN);
}

/*
Removes the key codes for the colour keys (VK_COLORED_KEY_0,VK_COLORED_KEY_1, VK_COLORED_KEY_2,VK_COLORED_KEY_3).Key 
codes from this set which are not present in the repository will be ignored. */
public void removeAllColourKeys() {
   removeKey(UserEvent.VK_COLORED_KEY_0);
   removeKey(UserEvent.VK_COLORED_KEY_1);
   removeKey(UserEvent.VK_COLORED_KEY_2);
   removeKey(UserEvent.VK_COLORED_KEY_3);
}

/*
Remove the key codes for the numeric keys (VK_0,VK_1,VK_2,VK_3,VK_4,VK_5,VK_6,VK_7, VK_8,VK_9).Key codes from this set 
which are not present in the repository will be ignored. */
public void removeAllNumericKeys() {
   removeKey(UserEvent.VK_0);
   removeKey(UserEvent.VK_1);
   removeKey(UserEvent.VK_2);
   removeKey(UserEvent.VK_3);
   removeKey(UserEvent.VK_4);
   removeKey(UserEvent.VK_5);
   removeKey(UserEvent.VK_6);
   removeKey(UserEvent.VK_7);
   removeKey(UserEvent.VK_8);
   removeKey(UserEvent.VK_9);
}


}
