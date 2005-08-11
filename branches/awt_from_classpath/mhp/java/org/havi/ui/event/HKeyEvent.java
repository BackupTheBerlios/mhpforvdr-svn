
package org.havi.ui.event;

/*An HKeyEvent event is used to interact with a component implementing the 
HKeyboardInputPreferred interface as follows: " An HKeyEvent event may be sent from the 
HAVi system to inform the component about key-input. The source of the input may be either 
a real or a virtual keyboard. " An HKeyEvent event is sent from the component to all 
registered HKeyListener whenever the compo-nent received input. Note that the HAVi system 
should only generate KEY_PRESSED events.Neither KEY_TYPED nor KEY_RELEASED events should 
be generated.Furthermore,the system should collapse combined events.For example,a usual 
Java Virtual Machine generates for the letter A three events: KEY_PRESSED for modi  er key 
Shift,KEY_PRESSED for letter 'A'and KEY_TYPED for 'A'.This should be collapsed into one 
single KEY_PRESSED event with the letter 'A'and the Shift modi  er set. This is to 
simplify the key event handling of applications. All interoperable HAVi components which 
expect to receive HKeyEvent events must either implement the HKeyboardInputPreferred 
interface or subclass components providing the processHKeyEvent(HKeyEvent )method.The 
parameters to the constructors are as follows,in cases where parameters are not used,then 
the constructor should use the default values. */

public class HKeyEvent extends java.awt.event.KeyEvent {

/*
Deprecated. See explanation in java.awt.event.KeyEvent. Constructs an HKeyEvent object with the speci  ed source 
component,type,modi  ers and key. Parameters: source -the object where the event originated. id -the identi  er. when 
-the time stamp for this event. modifiers -indication of any modi  cation keys that are active for this event. keyCode 
-the code of the key associated with this event. */
public HKeyEvent(java.awt.Component source, int id, long when, int modifiers, int 
keyCode) {
   super(source, id, when, modifiers, keyCode);
}

/*
Constructs an HKeyEvent object with the speci  ed source component,type,modi  ers and key. Parameters: source -the 
object where the event originated. id -the identi  er. when -the time stamp for this event. modifiers -indication of any 
modi  cation keys that are active for this event. keyCode -the code of the key associated with this event. keyChar -the 
character representation of the key associated with this event. */
public HKeyEvent(java.awt.Component source, int id, long when, int modifiers, int keyCode, char 
keyChar) {
   super(source, id, when, modifiers, keyCode, keyChar);
}



}
