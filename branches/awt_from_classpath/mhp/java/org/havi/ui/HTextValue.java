
package org.havi.ui;

/*This interface is implemented by all HAVi UI components which have have editable text content (e.g.a text entry 
control). Event Behavior Subclasses of HComponent which implement HTextValue must respond to HFocusEvent , HKeyEvent and 
HTextEvent events. Applications should assume that classes which implement HTextValue can generate events of the types 
HFocusEvent and HTextEvent in response to other types of input event. An application may add one or more HTextListener 
listeners to the component.The textChanged(HTextEvent)method of the HTextListener is invoked whenever the text in the 
HTextValue is changed,and the caretMoved(HTextEvent)method of the HTextListener is invoked whenever the text caret 
position is altered. HAVi text events are discussed in detail in the HKeyboardInputPreferred interface description. 
Interaction States The following interaction states are valid for this HTextValue component: (page 1140) 
... */

public interface HTextValue extends HNavigable, HKeyboardInputPreferred {


}
