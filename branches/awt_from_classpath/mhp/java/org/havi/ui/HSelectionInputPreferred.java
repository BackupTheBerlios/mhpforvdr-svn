
package org.havi.ui;

import org.havi.ui.event.HItemEvent;

/*A component which implements HSelectionInputPreferred indicates that this component expects to receive HItemEvent input 
events. All interoperable implementations of the HSelectionInputPreferred interface must extend HComponent . Note that 
the java.awt.Component method isFocusTraversable should always return true for a java.awt.Component implementing this 
interface. */

public interface HSelectionInputPreferred extends HOrientable {

/*
Get the selection mode for this HSelectionInputPreferred .If the returned value is true the component is in selection 
mode,and the selection may be changed. The component is witched into and out of selection mode on receiving 
ITEM_START_CHANGE and ITEM_END_CHANGE events. Returns: true if this component is in selection mode,false 
otherwise. */
public boolean getSelectionMode();


/*
Process an HItemEvent sent to this HSelectionInputPreferred . Parameters: evt -the HItemEvent to 
process. */
public void processHItemEvent(HItemEvent evt);


/*
Set the selection mode for this HSelectionInputPreferred .This method is provided for the convenience of component 
implementors.Interoperable applications shall not call this method.It cannot be made protected because interfaces cannot 
have protected methods. Parameters: edit -true to switch this component into selection mode,false otherwise. See Also: 
getSelectionMode() */
public void setSelectionMode(boolean adjust);



}
