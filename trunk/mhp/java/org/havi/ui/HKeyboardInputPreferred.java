
package org.havi.ui;

import org.havi.ui.event.HKeyEvent;
import org.havi.ui.event.HTextEvent;

/*A component which implements HKeyboardInputPreferred indicates that this component expects to receive both HKeyEvent and 
HTextEvent input events. All interoperable implementations of the HKeyboardInputPreferred interface must extend 
HComponent . The set of characters which the component expects to receive via HKeyEvent events is de  ned by the return 
code from the getType()method. When this component has focus,platforms without a physical means of generating key events 
with the desired range of characters will provide another means for keyboard entry e.g.by offering an on-screen 
"virtual&quot keyboard.Applications can query the system about the support of speci  c keyCodes through the 
isSupported(int)method. Note that the java.awt.Component method isFocusTraversable should always return true for a 
java.awt.Component implementing this interface. */

public interface HKeyboardInputPreferred {

/*
This constant indicates that the component only requires alphanumeric input,as determined by the java.lang.Character 
isLetter method. */
public static final int INPUT_ALPHA = 2;


/*
This constant indicates that the component only requires alphanumeric input,as determined by the java.lang.Character 
isLetterOrDigit method. */
public static final int INPUT_ALPHANUMERIC = 0;


/*
Indicates that the component requires any possible character as input,as determined by the java.lang.Character isDefined 
method. */
public static final int INPUT_ANY = 4;


/*
Indicates that the component requires as input the characters present in the array returned from the 
getValidInput()method. */
public static final int INPUT_CUSTOMIZED = 3;


/*
This constant indicates that the component only requires alphanumeric input,as determined by the java.lang.Character 
isDigit method. */
public static final int INPUT_NUMERIC = 1;


/*
Get the editing mode for this HKeyboardInputPreferred .If the returned value is true the component is in edit mode,and 
its textual content may be changed through user interaction such as keyboard events. The component is witched into and 
out of edit mode on receiving TEXT_START_CHANGE and TEXT_END_CHANGE events. Returns: true if this component is in edit 
mode,false otherwise. */
public boolean getEditMode();


/*
Retrieve the desired input type for this component.This value should be set to indicate to the system which input keys 
are required by this component. Returns: one of INPUT_ALPHANUMERIC ,INPUT_NUMERIC ,INPUT_ALPHA ,or 
INPUT_CUSTOMIZED */
public int getType();


/*
Retrieve the customized input character range.The return value of this method should re  ect the range of input keys 
which the component wishes to see,should getType()return INPUT_CUSTOMIZED .This method may return null if and only if it 
can guarantee that customized input is never required. If customized input can be used but no speci  c customized input 
is set,this method should return the same range as the INPUT_ALPHANUMERIC input type,i.e.that range of characters for 
which the java.lang.Character isLetterOrDigit returns true Returns: an array containing the characters which this 
component expects the platform to provide,or null to indicate that customized characters are not 
used. */
public char[] getValidInput();


/*
Process an HKeyEvent sent to this HKeyboardInputPreferred . Parameters: evt -the HKeyEvent to 
process. */
public void processHKeyEvent(HKeyEvent evt);


/*
Process an HTextEvent sent to this HKeyboardInputPreferred . Parameters: evt -the HTextEvent to 
process. */
public void processHTextEvent(HTextEvent evt);


/*
Set the editing mode for this HKeyboardInputPreferred . This method is provided for the convenience of component 
implementors.Interoperable applications shall not call this method.It cannot be made protected because interfaces cannot 
have protected methods. Parameters: edit -true to switch this component into edit mode,false otherwise. See Also: 
getEditMode() */
public void setEditMode(boolean edit);



}
