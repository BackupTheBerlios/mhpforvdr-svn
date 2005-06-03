
package org.havi.ui;

import org.havi.ui.event.HAdjustmentEvent;

/*All Known Implementing Classes: HRangeValue Description A component which implements HAdjustmentInputPreferred indicates 
that this component expects to receive HAdjustmentEvent input events. The system must provide a means of generating 
HAdjustmentEvent events as necessary.For platforms with a restricted number of physical keys this may involve a "virtual 
keyboard&quot or similar mechanism. All interoperable implementations of the HAdjustmentInputPreferred interface must 
extend HComponent . Note that the java.awt.Component method isFocusTraversable should always return true for a 
java.awt.Component implementing this interface. */

public interface HAdjustmentInputPreferred extends HOrientable {

/*
Get the adjustment mode for this HAdjustmentInputPreferred .If the returned value is true the component is in adjustment 
mode,and its value may be changed on receipt of ADJUST_LESS and ADJUST_MORE events. The component is witched into and 
out of adjustment mode on receiving ADJUST_START_CHANGE and ADJUST_END_CHANGE events. Returns: true if this component is 
in adjustment mode,false otherwise. */
public boolean getAdjustMode();


/*
Process an HAdjustmentEvent sent to this HAdjustmentInputPreferred . Parameters: evt -the HAdjustmentEvent to 
process. */
public void processHAdjustmentEvent(HAdjustmentEvent evt);


/*
Set the adjustment mode for this HAdjustmentInputPreferred . This method is provided for the convenience of component 
implementors.Interoperable applications shall not call this method.It cannot be made protected because interfaces cannot 
have protected methods. Parameters: edit -true to switch this component into adjustment mode,false otherwise. See Also: 
getAdjustMode() */
public void setAdjustMode(boolean adjust);



}
