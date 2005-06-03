
package org.havi.ui;

import org.havi.ui.event.HActionEvent;

/*A component which implements HActionInputPreferred indicates that this component expects to receive HActionEvent input 
events. All interoperable implementations of the HActionInputPreferred interface must extend HComponent . Note that the 
java.awt.Component method isFocusTraversable should always return true for a java.awt.Component implementing this 
interface. */

public interface HActionInputPreferred {

/*
Process an HActionEvent sent to this HActionInputPreferred . Parameters: evt -the HActionEvent to 
process. */
public void processHActionEvent(HActionEvent evt);



}
