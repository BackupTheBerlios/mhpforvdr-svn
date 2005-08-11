
package org.havi.ui.event;

/*The remote control event class. The presence or absence of these keys and their desired 
representation is provided by the HRcCapabilities class. Note that it is an implementation 
option if remote control key events are repeated. Instances of HRcEvent are reported 
through the normal java.awt event mechanism Note that the reception of these events by a 
java.awt.Component is dependent on it having java.awt.event.KeyEvent events enabled. Note 
that it is an implementation constraint that the HRcEvent event range should not intersect 
with the Java AWT key event range.The parameters to the constructors are as follows,in 
cases where parameters are not used,then the constructor should use the default 
values. */

public class HRcEvent extends HKeyEvent {

/*
Marks the  rst integer id for the range of remote control event ids. */
public static final int RC_FIRST = 400;


/*
Marks the last integer id for the range of remote control event ids. */
public static final int RC_LAST = 460;

//all VK_* constants inherited from (non-API) java.awt.event.KeyConstants


/*
Deprecated. See explanation in java.awt.event.KeyEvent. Constructs an HRcEvent object with the speci  ed source 
component,type,modi  ers and key. Parameters: source -the object where the event originated. id -the identi  er. when 
-the time stamp for this event. modifiers -indication of any modi  cation keys that are active for this event. keyCode 
-the code of the key associated with this event. */
public HRcEvent(java.awt.Component source, int id, long when, int modifiers, int 
keyCode) {
   super(source, id, when, modifiers, keyCode);
}

/*
Constructs an HRcEvent object with the speci  ed source component,type,modi  ers and key. Parameters: source -the object 
where the event originated. id -the identi  er. when -the time stamp for this event. modifiers -indication of any modi  
cation keys that are active for this event. keyCode -the code of the key associated with this event. keyChar -the 
character representation of the key associated with this event. */
public HRcEvent(java.awt.Component source, int id, long when, int modifiers, int keyCode, char 
keyChar) {
   super(source, id, when, modifiers, keyCode, keyChar);
}

/*static HRcEvent cache;
//Not part of API! Used for internal event creation (see evt.c) 
static synchronized HRcEvent getEvent( int srcIdx, int id, int keyCode, int keyChar, int modifier ) {
            HRcEvent e;
            java.awt.Component source = java.awt.AWTEvent.sources[srcIdx];
            long when = System.currentTimeMillis();
	// Check for modifier keystrokes which have been "eaten" by the native window system.
	// Unfortunately, this can happen if the window manager temporarily grabs the keyboard
	// (e.g. fvwm2 during an initial window positioning)
            if ( ( modifier == 0 ) && ( inputModifier != 0 ) ) {
                inputModifier = 0;
            }
            if ( cache == null ) {
                e = new HRcEvent( source, id, when, inputModifier, keyCode,
                    ( char )keyChar );
            } else {
                e = cache;
                cache = ( HRcEvent )e.next;
                e.next = null;
                e.id = id;
                e.source = source;
                e.when = when;
                e.modifiers = inputModifier;
                e.keyCode = keyCode;
                e.keyChar = ( char )keyChar;
                e.consumed = false;
            }
            if ( ( java.awt.Toolkit.flags & java.awt.Toolkit.NATIVE_DISPATCHER_LOOP ) != 0 ) {
		// this is not used as a direct return value for EventQueue.getNextEvent(),
		// it has to be Java-queued by the native layer
                java.awt.Toolkit.eventQueue.postEvent( e );
            }
            return e;
}*/

}
