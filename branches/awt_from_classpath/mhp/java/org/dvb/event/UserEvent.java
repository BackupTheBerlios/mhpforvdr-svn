
package org.dvb.event;

import java.awt.event.KeyEvent;
import org.havi.ui.event.HRcEvent;
import org.havi.ui.HEventMulticaster;

/*Represents a user event.A user event is de  ned by a family,a type and either a code or a character. Unless stated 
otherwise,all constants used in this class are de  ned in java.awt.event.KeyEvent and its parent 
classes. */

public class UserEvent extends java.util.EventObject implements java.awt.event.KeyConstants {

/*
the family for events that are coming from the remote control or from the keyboard. */
public static final int UEF_KEY_EVENT = 1;
   
int Family;
KeyEvent event;


//not API, the constructor used by the framework
public UserEvent (KeyEvent e) {
   super(e.getSource());
   Family=UEF_KEY_EVENT;
   event=e;
}

static class FakeComponent extends java.awt.Component {
   public FakeComponent() {}
}
//we need a fake component for out fake keyEvents
static FakeComponent fakeComponent=new FakeComponent();

/*
Constructor for a new UserEvent object representing a key being typed.This is the combination of a key being pressed and 
then being released.The type of UserEvents created with this constructor shall be KEY_TYPED.Key combinations which do 
not result in characters,such as action keys like F1,shall not generate KEY_TYPED events. Parameters: source -the 
EventManager which is the source of the event family -the event family. keyChar -the character typed Since: MHP 
1.0.1 */
public UserEvent(java.lang.Object source, int family, char keyChar) {
   super(source);   
   event=new KeyEvent(fakeComponent, KEY_TYPED, System.currentTimeMillis(), 0, VK_UNDEFINED, keyChar);  
   Family=family;
}

/*
Constructor for a new UserEvent object representing a key being pressed. Parameters: source -the EventManager which is 
the source of the event family -the event family. type -the event type.Either one of KEY_PRESSED or KEY_RELEASED. code 
-the event code.One of the constants whose name begins in "VK_"de  ned in java.ui.event.KeyEvent or org.havi.ui.event. 
modifiers -the modi  ers active when the key was pressed.These have the same semantics as modi  ers in 
java.awt.event.KeyEvent */
public UserEvent(java.lang.Object source, int family, int type, int code, int modifiers) {
   super(source);   
   Family=family;
   int Type;
   if (type==KEY_PRESSED)
      Type=KEY_PRESSED;
   else
      Type=KEY_RELEASED;
   event=new KeyEvent(fakeComponent, Type, System.currentTimeMillis(), modifiers, code);  
}

/*
Returns the event code.For KEY_TYPED events,the code is VK_UNDEFINED. Returns: an int representing the event 
code. */
public int getCode() {
   return event.getKeyCode();
}


/*
Returns the event family.Could be UEF_KEY_EVENT. Returns: an int representing the event 
family. */
public int getFamily() {
   return Family;
}

/*
Returns the character associated with the key in this event.If no valid Unicode character exists for this key 
event,keyChar is CHAR_UNDEFINED. Returns: a character Since: MHP 1.0.1 */
public char getKeyChar() {
   return event.getKeyChar();
}

/*
Returns the modi  ers  ag for this event. Returns: the modi  ers  ag for this 
event */
public int getModifiers() {
   return event.getModifiers();
}

/*
Returns the event type.Could be KEY_PRESSED,KEY_RELEASED or KEY_TYPED. Returns: an int representing the event 
type. */
public int getType() {
   return event.getID();
}

/*
Returns whether or not the Alt modi  er is down on this event. Returns: whether the Alt modi  er is down on this event 
Since: MHP 1.0.1 */
public boolean isAltDown() {
   return event.isAltDown();
}

/*
Returns whether or not the Control modi  er is down on this event. Returns: whether the Control modi  er is down on this 
event Since: MHP 1.0.1 */
public boolean isControlDown() {
   return event.isControlDown();
}

/*
Returns whether or not the Meta modi  er is down on this event. Returns: whether the Meta modi  er is down on this event 
Since: MHP 1.0.1 */
public boolean isMetaDown() {
   return event.isMetaDown();
}

/*
Returns whether or not the Shift modi  er is down on this event. Returns: whether the Shift modi  er is down on this 
event Since: MHP 1.0.1 */
public boolean isShiftDown() {
   return event.isShiftDown();
}

//for internal use only
public boolean sameKey(UserEvent e) {
   if (e.getType() == KEY_TYPED) {
      return (e.getKeyChar() == getKeyChar()); //modifier independent
   } else {
      return (e.getCode() == getCode()) && (e.getModifiers() == getModifiers());
   }
}

//for internal use only
public boolean equals(UserEvent e) {
   return ( (e.getType() == getType()) && sameKey(e));
}

public KeyEvent getAWTEvent() {
   return event;
}


}
