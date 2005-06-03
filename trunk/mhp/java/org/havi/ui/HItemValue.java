
package org.havi.ui;

import org.havi.ui.event.HItemListener;

/*This interface is implemented by all HAVi UI components which have some form of selectable content (e.g.a list group). 
Event Behavior Subclasses of HComponent which implement HItemValue must respond to HFocusEvent and HItemEvent events. 
Applications should assume that classes which implement HItemValue can generate events of the types HFocusEvent and 
HItemEvent in response to other types of input event. An application may add one or more HItemListener listeners to the 
component.The selectionChanged(HItemEvent)method of the HItemListener is invoked whenever the selection managed by the 
HItemValue is changed. HAVi item events are discussed in detail in the HSelectionInputPreferred interface description. 
Interaction States The following interaction states are valid for this HItemValue component: " NORMAL_STATE " 
FOCUSED_STATE " DISABLED_STATE " DISABLED_FOCUSED_STATE The state machine diagram below shows the valid state 
transitions for an HItemValue component. " NORMAL_STATE " FOCUSED_STATE " DISABLED_STATE " DISABLED_FOCUSED_STATE The 
state machine diagram below shows the valid state transitions for an HItemValue component.Platform Classes The following 
HAVi platform classes implement or inherit the HItemValue interface.These classes shall all generate both HFocusEvent 
and HItemEvent events in addition to any other events speci  ed in the respective class descriptions. " 
HListGroup */

public interface HItemValue extends HNavigable, HSelectionInputPreferred {

/*
Adds the speci  ed HItemListener to receive HItemEvent sent from this object.If the listener has already been added 
further calls will add further references to the listener,which will then receive multiple copies of a single event. 
Parameters: l -the HItemListener to be noti  ed. */
public void addItemListener(HItemListener l);


/*
Get the sound to be played when the selection changes. Returns: The sound played when the selection 
changes */
public HSound getSelectionSound();


/*
Removes the speci  ed HItemListener so that it no longer receives HItemEvent from this object.If the speci  ed listener 
is not registered,the method has no effect.If multiple references to a single listener have been registered it should be 
noted that this method will only remove one reference per call. Parameters: l -the HItemListener to be removed from noti 
 cation. */
public void removeItemListener(HItemListener l);


/*
Associate a sound to be played when the selection is modi  ed.The sound is played irrespective of whether an HItemEvent 
is sent to one or more listeners. Parameters: sound -the sound to be played,when the selection is modi  ed.If sound 
content is already set, the original content is replaced.To remove the sound specify a null HSound 
. */
public void setSelectionSound(HSound sound);



}
