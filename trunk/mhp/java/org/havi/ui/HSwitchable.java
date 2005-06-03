
package org.havi.ui;

/*This interface is implemented for all user interface components that can be actioned such that they "toggle"on and off 
and maintain the chosen state. Event Behavior Subclasses of HComponent which implement HSwitchable must respond to 
HFocusEvent and HActionEvent events. Applications should assume that classes which implement HSwitchable can generate 
events of the types HFocusEvent and HActionEvent in response to other types of input event. An application may add one 
or more HActionListener listeners to the component.The actionPerformed method of the HActionListener is invoked whenever 
the HSwitchable is actioned. HAVi action events are discussed in detail in the HActionInputPreferred interface 
description. Interaction States The following interaction states are valid for this HSwitchable component: (page 1113) 
... */

public interface HSwitchable extends HActionable {

/*
Returns the current switchable state of this HSwitchable . Returns: the current switchable state of this 
HSwitchable. */
public boolean getSwitchableState();


/*
Get the sound to be played when the interaction state of the HSwitchable makes the following transitions: " 
ACTIONED_STATE to NORMAL_STATE " ACTIONED_FOCUSED_STATE to FOCUSED_STATE Returns: the sound to be played when the 
HSwitchable transitions from an actioned state. */
public HSound getUnsetActionSound();


/*
Sets the current state of the button.Note that ActionListeners are only called when an ACTION_PERFORMED event is 
received,or if they are called directly,e.g.via processActionEvent they are not called by 
setSwitchableState(boolean). */
public void setSwitchableState(boolean state);


/*
Associate a sound to be played when the interaction state of the HSwitchable makes the following transitions: " 
ACTIONED_STATE to NORMAL_STATE " ACTIONED_FOCUSED_STATE to FOCUSED_STATE Parameters: sound -a sound to be played when 
the HSwitchable transitions from an actioned state.If sound content is already set,the original content is replaced.To 
remove the sound specify a null HSound . */
public void setUnsetActionSound(HSound sound);



}
