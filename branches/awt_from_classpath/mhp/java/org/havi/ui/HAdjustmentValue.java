
package org.havi.ui;

import org.havi.ui.event.HAdjustmentListener;

/*This interface is implemented by all HAVi UI components which have some form of adjustable numerical value (e.g.a range 
control) Event Behavior Subclasses of HComponent which implement HAdjustmentValue must respond to HFocusEvent and 
HAdjustmentEvent events. Applications should assume that classes which implement HAdjustmentValue can generate events of 
the types HFocusEvent and HAdjustmentEvent in response to other types of input event. An application may add one or more 
HAdjustmentListener listeners to the component.The valueChanged(HAdjustmentEvent)method of the HAdjustmentListener is 
invoked whenever the value of the HAdjustmentValue is modi  ed. HAVi adjustment events are discussed in detail in the 
HAdjustmentInputPreferred interface description. Interaction States The following interaction states are valid for this 
HAdjustmentValue component: " NORMAL_STATE " FOCUSED_STATE " DISABLED_STATE " DISABLED_FOCUSED_STATE The state machine 
diagram below shows the valid state transitions for an HAdjustmentValue component. Platform Classes The following HAVi 
platform classes implement or inherit the HAdjustmentValue interface.These classes shall all generate both HFocusEvent 
and HAdjustmentEvent events in addition to any other events speci  ed in the respective class descriptions. " 
HRangeValue " HListGroup See Also: HNavigable HOrientable HAdjustmentInputPreferred HAdjustmentEvent 
HAdjustmentListener */

public interface HAdjustmentValue extends HNavigable, HAdjustmentInputPreferred {

/*
Adds the speci  ed HAdjustmentListener to receive HAdjustmentEvent sent from this object. If the listener has already 
been added further calls will add further references to the listener,which will then receive multiple copies of a single 
event. Parameters: l -the HAdjustmentListener to be noti  ed. */
public void addAdjustmentListener(HAdjustmentListener l);


/*
Get the sound to be played when the value changes. Returns: The sound played when the value 
changes */
public HSound getAdjustmentSound();


/*
Get the block increment for this HAdjustmentValue . Returns: the block increment value for this 
HAdjustmentValue */
public int getBlockIncrement();


/*
Get the unit increment for this HAdjustmentValue . Returns: the increment value for this 
HAdjustmentValue */
public int getUnitIncrement();


/*
Removes the speci  ed HAdjustmentListener so that it no longer receives HAdjustmentEvent from this object.If the speci  
ed listener is not registered,the method has no effect.If multiple references to a single listener have been registered 
it should be noted that this method will only remove one reference per call. Parameters: l -the HAdjustmentListener to 
be removed from noti  cation. */
public void removeAdjustmentListener(HAdjustmentListener l);


/*
Associate a sound to be played when the value is modi  ed.The sound is played irrespective of whether an 
HAdjustmentEvent is sent to one or more listeners. Parameters: sound -the sound to be played,when the value is modi  
ed.If sound content is already set,the original content is replaced.To remove the sound specify a null HSound 
. */
public void setAdjustmentSound(HSound sound);


/*
Set the block increment for this HAdjustmentValue . Parameters: increment -the amount by which the value of the 
HAdjustmentValue should change when an ADJUST_PAGE_LESS or ADJUST_PAGE_MORE event is received.Values of increment less 
than one shall be treated as a value of one. */
public void setBlockIncrement(int increment);


/*
Set the unit increment for this HAdjustmentValue . Parameters: increment -the amount by which the value of the 
HAdjustmentValue should change when an ADJUST_LESS or ADJUST_MORE event is received.Values of increment less than one 
shall be treated as a value of one. */
public void setUnitIncrement(int increment);



}
