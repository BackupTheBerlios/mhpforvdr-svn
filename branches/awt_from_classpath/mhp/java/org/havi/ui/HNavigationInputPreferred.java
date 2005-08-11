
package org.havi.ui;

import org.havi.ui.event.HFocusEvent;

/*A component which implements HNavigationInputPreferred indicates that this component expects to receive HFocusEvent 
events.The focus event system in HAVi is designed to be compatible with standard AWT focus mechanisms while supporting 
key event-based focus traversal for HAVi UI components. All interoperable implementations of the 
HNavigationInputPreferred interface must extend HComponent . Components which implement HNavigationInputPreferred to 
handle HFocusEvent events can optionally manage focus traversal based on keyboard input events,in addition to the normal 
semantics of the FOCUS_GAINED and FOCUS_LOST event types.The focus traversal mechanism speci  ed by the HAVI UI 
HNavigable interface is one such system. In the case where such an implementation requires speci  c keys to manage focus 
traversal the getNavigationKeys()method is provided to allow the HAVi platform to query the set of keys for which a 
navigation target has been set.When such a component has the input focus,platforms without a physical means of 
generating the desired keystrokes shall provide another means for navigation e.g.by offering an on-screen "virtual&quot 
keyboard.Applications can query the system about the support of speci  c keyCodes through the isSupported(int)method. 
The keyCodes for navigation keystrokes generated on the HNavigationInputPreferred will bepassed to the 
HNavigationInputPreferred as an HFocusEvent transferId through the processHFocusEvent(HFocusEvent)method.No HKeyEvent 
will be generated on the HNavigationInputPreferred as a result of these keystrokes. Note that the java.awt.Component 
method isFocusTraversable should always return true for a java.awt.Component implementing this 
interface. */

public interface HNavigationInputPreferred {

/*
Retrieve the set of key codes which this component maps to navigation targets. Returns: an array of key codes,or null if 
no navigation targets are set on this component. */
public int[] getNavigationKeys();


/*
Process an HFocusEvent sent to this HNavigationInputPreferred . Parameters: evt -the HFocusEvent to 
process. */
public void processHFocusEvent(HFocusEvent evt);



}
