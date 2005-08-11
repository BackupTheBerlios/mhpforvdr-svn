
package org.havi.ui;

import org.havi.ui.event.HActionListener;
/*This interface is implemented by all HAVi UI components that can be actioned by the user. Event Behaviour Subclasses of 
HComponent which implement HActionable must respond to HFocusEvent and HActionEvent events. Applications should assume 
that classes which implement HActionable can generate events of the types HFocusEvent and HActionEvent in response to 
other types of input event. An application may add one or more HActionListener listeners to the component.The 
actionPerformed method of the HActionListener is invoked whenever the HActionable is actioned. HAVi action events are 
discussed in detail in the HActionInputPreferred interface description. Interaction States The following interaction 
states are valid for this HActionable component: " NORMAL_STATE " FOCUSED_STATE " ACTIONED_STATE " 
ACTIONED_FOCUSED_STATE " DISABLED_STATE " DISABLED_FOCUSED_STATE The state machine diagram below shows the valid state 
transitions for an HActionable component. Unlike HSwitchable components the transition back from an actioned state 
(i.e.one with the ACTIONED_STATE_BIT bit set is automatically  red once all registered HActionListener listeners have 
been called. A direct consequence of is that HActionable components can only achieve the ACTIONED_STATE and 
ACTIONED_FOCUSED_STATE states on a temporary basis. HActionable components may not be disabled while actioned. Platform 
Classes The following HAVi platform classes implement or inherit the HActionable interface.These classes shall all 
generate both HFocusEvent and HActionEvent events in addition to any other events speci  ed in the respective class 
descriptions. " HGraphicButton " HTextButton " HToggleButton " HListGroup */

public interface HActionable extends HNavigable, HActionInputPreferred {

/*
Adds the speci  ed HActionListener to receive HActionEvent events sent from this HActionable .If the listener has 
already been added further calls will add further references to the listener,which will then receive multiple copies of 
a single event. Parameters: l -the HActionListener. */
public void addHActionListener(HActionListener l);


/*
Gets the command name for the HActionEvent event  red by this HActionable . Returns: A String representing the command 
name of the HActionEvent  red by this HActionable . See Also: getActionCommand() */
public java.lang.String getActionCommand();


/*
Associate a sound to be played when the interaction state of the HActionable makes the following transitions: " 
NORMAL_STATE to ACTIONED_STATE " FOCUSED_STATE to ACTIONED_FOCUSED_STATE Returns: The sound played when the component is 
actioned. */
public HSound getActionSound();


/*
Removes the speci  ed HActionListener so that it no longer receives HActionEvent events from this HActionable .If the 
speci  ed listener is not registered,the method has no effect.If multiple references to a single listener have been 
registered it should be noted that this method will only remove one reference per call. Parameters: l -the 
HActionListener. */
public void removeHActionListener(HActionListener l);


/*
Sets the command name for the HActionEvent event  red by this HActionable . Parameters: command -a String used to set 
the action command. See Also: getActionCommand() */
public void setActionCommand(java.lang.String command);


/*
Associate a sound to be played when the interaction state of the HActionable makes the following transitions: " 
NORMAL_STATE to ACTIONED_STATE " FOCUSED_STATE to ACTIONED_FOCUSED_STATE Parameters: sound -the sound to be played,when 
the component is actioned.If sound content is already set,the original content is replaced.To remove the sound specify a 
null HSound */
public void setActionSound(HSound sound);



}
