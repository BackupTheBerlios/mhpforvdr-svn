
package org.dvb.event;
//import java.awt.event.KeyEvent; //for the constants
//import org.havi.ui.event.HRcEvent;

/*Represents a user event.A user event is de  ned by a family,a type and either a code or a character. Unless stated 
otherwise,all constants used in this class are de  ned in java.awt.event.KeyEvent and its parent 
classes. */

public class UserEvent extends java.util.EventObject {

/*
the family for events that are coming from the remote control or from the keyboard. */
public static final int UEF_KEY_EVENT = 0; //?
	final public static int KEY_FIRST = 400;
	final public static int KEY_LAST = 402;
	final public static int KEY_TYPED = KEY_FIRST;
	final public static int KEY_PRESSED = 1 + KEY_FIRST;
	final public static int KEY_RELEASED = 2 + KEY_FIRST;
	final public static int VK_ENTER = '\n';
	final public static int VK_BACK_SPACE = '\b';
	final public static int VK_TAB = '\t';
	final public static int VK_CANCEL = 0x03;
	final public static int VK_CLEAR = 0x0C;
	final public static int VK_SHIFT = 0x10;
	final public static int VK_CONTROL = 0x11;
	final public static int VK_ALT = 0x12;
	final public static int VK_PAUSE = 0x13;
	final public static int VK_CAPS_LOCK = 0x14;
	final public static int VK_ESCAPE = 0x1B;
	final public static int VK_SPACE = 0x20;
	final public static int VK_PAGE_UP = 0x21;
	final public static int VK_PAGE_DOWN = 0x22;
	final public static int VK_END = 0x23;
	final public static int VK_HOME = 0x24;
	final public static int VK_LEFT = 0x25;
	final public static int VK_UP = 0x26;
	final public static int VK_RIGHT = 0x27;
	final public static int VK_DOWN = 0x28;
	final public static int VK_COMMA = 0x2C;
	final public static int VK_PERIOD = 0x2E;
	final public static int VK_SLASH = 0x2F;
	final public static int VK_0 = 0x30;
	final public static int VK_1 = 0x31;
	final public static int VK_2 = 0x32;
	final public static int VK_3 = 0x33;
	final public static int VK_4 = 0x34;
	final public static int VK_5 = 0x35;
	final public static int VK_6 = 0x36;
	final public static int VK_7 = 0x37;
	final public static int VK_8 = 0x38;
	final public static int VK_9 = 0x39;
	final public static int VK_SEMICOLON = 0x3B;
	final public static int VK_EQUALS = 0x3D;
	final public static int VK_A = 0x41;
	final public static int VK_B = 0x42;
	final public static int VK_C = 0x43;
	final public static int VK_D = 0x44;
	final public static int VK_E = 0x45;
	final public static int VK_F = 0x46;
	final public static int VK_G = 0x47;
	final public static int VK_H = 0x48;
	final public static int VK_I = 0x49;
	final public static int VK_J = 0x4A;
	final public static int VK_K = 0x4B;
	final public static int VK_L = 0x4C;
	final public static int VK_M = 0x4D;
	final public static int VK_N = 0x4E;
	final public static int VK_O = 0x4F;
	final public static int VK_P = 0x50;
	final public static int VK_Q = 0x51;
	final public static int VK_R = 0x52;
	final public static int VK_S = 0x53;
	final public static int VK_T = 0x54;
	final public static int VK_U = 0x55;
	final public static int VK_V = 0x56;
	final public static int VK_W = 0x57;
	final public static int VK_X = 0x58;
	final public static int VK_Y = 0x59;
	final public static int VK_Z = 0x5A;
	final public static int VK_OPEN_BRACKET = 0x5B;
	final public static int VK_BACK_SLASH = 0x5C;
	final public static int VK_CLOSE_BRACKET = 0x5D;
	final public static int VK_NUMPAD0 = 0x60;
	final public static int VK_NUMPAD1 = 0x61;
	final public static int VK_NUMPAD2 = 0x62;
	final public static int VK_NUMPAD3 = 0x63;
	final public static int VK_NUMPAD4 = 0x64;
	final public static int VK_NUMPAD5 = 0x65;
	final public static int VK_NUMPAD6 = 0x66;
	final public static int VK_NUMPAD7 = 0x67;
	final public static int VK_NUMPAD8 = 0x68;
	final public static int VK_NUMPAD9 = 0x69;
	final public static int VK_MULTIPLY = 0x6A;
	final public static int VK_ADD = 0x6B;
	final public static int VK_SEPARATER = 0x6C;
	final public static int VK_SUBTRACT = 0x6D;
	final public static int VK_DECIMAL = 0x6E;
	final public static int VK_DIVIDE = 0x6F;
	final public static int VK_F1 = 0x70;
	final public static int VK_F2 = 0x71;
	final public static int VK_F3 = 0x72;
	final public static int VK_F4 = 0x73;
	final public static int VK_F5 = 0x74;
	final public static int VK_F6 = 0x75;
	final public static int VK_F7 = 0x76;
	final public static int VK_F8 = 0x77;
	final public static int VK_F9 = 0x78;
	final public static int VK_F10 = 0x79;
	final public static int VK_F11 = 0x7A;
	final public static int VK_F12 = 0x7B;
	final public static int VK_DELETE = 0x7F;
	final public static int VK_NUM_LOCK = 0x90;
	final public static int VK_SCROLL_LOCK = 0x91;
	final public static int VK_PRINTSCREEN = 0x9A;
	final public static int VK_INSERT = 0x9B;
	final public static int VK_HELP = 0x9C;
	final public static int VK_META = 0x9D;
	final public static int VK_BACK_QUOTE = 0xC0;
	final public static int VK_QUOTE = 0xDE;
	final public static int VK_FINAL = 0x18;
	final public static int VK_CONVERT = 0x1C;
	final public static int VK_NONCONVERT = 0x1D;
	final public static int VK_ACCEPT = 0x1E;
	final public static int VK_MODECHANGE = 0x1F;
	final public static int VK_KANA = 0x15;
	final public static int VK_KANJI = 0x19;
	final public static int VK_UNDEFINED = 0x0;
	final public static char CHAR_UNDEFINED = 0xffff;
	final public static int SHIFT_MASK = 1;
	final public static int CTRL_MASK = 2;
	final public static int META_MASK = 4;
	final public static int ALT_MASK = 8;
	final public static int VK_COLORED_KEY_0 = org.havi.ui.event.HRcEvent.VK_COLORED_KEY_0;
	final public static int VK_COLORED_KEY_1 = org.havi.ui.event.HRcEvent.VK_COLORED_KEY_1;
	final public static int VK_COLORED_KEY_2 = org.havi.ui.event.HRcEvent.VK_COLORED_KEY_2;
	final public static int VK_COLORED_KEY_3 = org.havi.ui.event.HRcEvent.VK_COLORED_KEY_3;
   
private int Family;
private char KeyChar;
private int KeyCode;
private int Modifiers;
private int Type;
/*
Constructor for a new UserEvent object representing a key being typed.This is the combination of a key being pressed and 
then being released.The type of UserEvents created with this constructor shall be KEY_TYPED.Key combinations which do 
not result in characters,such as action keys like F1,shall not generate KEY_TYPED events. Parameters: source -the 
EventManager which is the source of the event family -the event family. keyChar -the character typed Since: MHP 
1.0.1 */
public UserEvent(java.lang.Object source, int family, char keyChar) {
   super(source);
   
   Family=family;
   KeyChar=keyChar;
   Type=KEY_TYPED;
   Modifiers=0;
   KeyCode=VK_UNDEFINED; //see below
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
   if (type==KEY_PRESSED)
      Type=KEY_PRESSED;
   else
      Type=KEY_RELEASED;
   KeyCode=code;
   Modifiers=modifiers;
}

/*
Returns the event code.For KEY_TYPED events,the code is VK_UNDEFINED. Returns: an int representing the event 
code. */
public int getCode() {
   return KeyCode;
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
   if (getType() == KEY_TYPED)
      return KeyChar;
      
   //taken from Kaffe
	if ( (KeyCode >= VK_0 && KeyCode <= VK_9) || (KeyCode >= VK_A && KeyCode <= VK_Z) ||
       KeyCode == ',' || KeyCode == '.' || KeyCode == '/' || KeyCode == ';' ||
	     KeyCode == '=' || KeyCode == '[' || KeyCode == '\\' || KeyCode == ']' )
		return (char)KeyCode;
   else return CHAR_UNDEFINED;
}

/*
Returns the modi  ers  ag for this event. Returns: the modi  ers  ag for this 
event */
public int getModifiers() {
   return Modifiers;
}

/*
Returns the event type.Could be KEY_PRESSED,KEY_RELEASED or KEY_TYPED. Returns: an int representing the event 
type. */
public int getType() {
   return Type;
}

/*
Returns whether or not the Alt modi  er is down on this event. Returns: whether the Alt modi  er is down on this event 
Since: MHP 1.0.1 */
public boolean isAltDown() {
   return (Modifiers & ALT_MASK) != 0 ;
}

/*
Returns whether or not the Control modi  er is down on this event. Returns: whether the Control modi  er is down on this 
event Since: MHP 1.0.1 */
public boolean isControlDown() {
   return (Modifiers & CTRL_MASK) != 0 ;
}

/*
Returns whether or not the Meta modi  er is down on this event. Returns: whether the Meta modi  er is down on this event 
Since: MHP 1.0.1 */
public boolean isMetaDown() {
   return (Modifiers & META_MASK) != 0 ;
}

/*
Returns whether or not the Shift modi  er is down on this event. Returns: whether the Shift modi  er is down on this 
event Since: MHP 1.0.1 */
public boolean isShiftDown() {
   return (Modifiers & SHIFT_MASK) != 0 ;
}

//for internal use only
public boolean sameKey(UserEvent e) {
   //should I care for modifiers?
   if (e.getType() == KEY_TYPED) {
      return (e.getKeyChar() == KeyChar);
   } else {
      return (e.getCode() == KeyCode);
   }
}

//for internal use only
public boolean equals(UserEvent e) {
   return ( (e.getType() == Type) && sameKey(e));
}

}
