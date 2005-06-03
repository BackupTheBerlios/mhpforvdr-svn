
package org.havi.ui;

/*This interface is implemented by all HAVi UI components that can be navigated to by the user (i.e. components which can 
gain focus). Event Behavior Subclasses of HComponent which implement HNavigable must respond to HFocusEvent events. 
Applications should assume that classes which implement HNavigable can only generate events of the type HFocusEvent in 
response to other types of input event. An application may add one or more HFocusListener listeners to the component.The 
focusGained and focusLost methods of the HFocusListener are invoked whenever the HNavigable gains or loses focus. An 
HNavigable has an arbitrary focus traversal table associated with it (see setMove(int, HNavigable)and getMove(int)).This 
mechanism allows the four-way focus behavior of a set of components to be set (see setFocusTraversal(HNavigable, 
HNavigable, HNavigable, HNavigable),setMove(int, HNavigable)and getMove(int)). HAVi focus events are discussed in detail 
in the HNavigationInputPreferred interface description. Interaction States The following interaction states are valid 
for this HNavigable component: (page 987) */

public interface HNavigable extends HNavigationInputPreferred {

/*
Get the sound associated with the gain focus event. Returns: The sound played when the component gains focus.If no sound 
is associated with gaining focus,then null shall be returned. */
public HSound getGainFocusSound();


/*
Get the sound associated with the lost focus event. Returns: The sound played when the component loses focus.If no sound 
is associated with losing focus, then null shall be returned. */
public HSound getLoseFocusSound();


/*
Provides the HNavigable object that is navigated to when a particular key is pressed. Parameters: keyCode -The key code 
of the pressed key. Returns: Returns the HNavigable object,or if no HNavigable is associated with the keyCode then 
returns null */
public HNavigable getMove(int keyCode);


/*
Indicates if this component has focus. Returns: true if the component has focus,otherwise returns 
false */
public boolean isSelected();


/*
Set the focus control for an HNavigable component.Note setFocusTraversal(HNavigable, HNavigable, HNavigable, 
HNavigable)is a convenience function for application programmers where a standard up,down,left and right focus traversal 
between components is required. Note setFocusTraversal(HNavigable, HNavigable, HNavigable, HNavigable)is equivalent to 
multiple calls to setMove(int, HNavigable),where the key codes VK_UP, VK_DOWN,VK_LEFT,VK_RIGHT are used. Note that this 
API does not prevent the creation of "isolated"HNavigable components ---authors should endeavor to avoid confusing the 
user.Parameters: up -The HNavigable component to move to,when the user generates a VK_UP KeyEvent.If there is no 
HNavigable component to move "up"to,then null should be speci  ed. down -The HNavigable component to move to,when the 
user generates a VK_DOWN KeyEvent.If there is no HNavigable component to move "down"to,then null should be speci  ed. 
left -The HNavigable component to move to,when the user generates a VK_LEFT KeyEvent.If there is no HNavigable component 
to move "left"to,then null should be speci  ed. right -The HNavigable component to move to,when the user generates a 
VK_RIGHT KeyEvent.If there is no HNavigable component to move "right"to,then null should be speci  
ed. */
public void setFocusTraversal(HNavigable up, HNavigable down, HNavigable left, HNavigable 
right);


/*
Associate a sound with gaining focus,i.e.when the HNavigable receives a java.awt.event.FocusEvent event of type 
FOCUS_GAINED This sound will start to be played when an object implementing this interface gains focus.It is not 
guaranteed to be played to completion.If the object implementing this interface loses focus before the audio completes 
playing, the audio will be truncated.Applications wishing to ensure the audio is always played to completion must 
implement special logic to slow down the focus transitions. By default,an HNavigable object does not have any gain focus 
sound associated with it. Note that the ordering of playing sounds is dependent on the order of the focus lost,gained 
events. Parameters: sound -the sound to be played,when the component gains focus.If sound content is already set,the 
original content is replaced.To remove the sound specify a null HSound . */
public void setGainFocusSound(HSound sound);


/*
Associate a sound with losing focus,i.e.when the HNavigable receives a java.awt.event.FocusEvent event of type 
FOCUS_LOST.This sound will start to be played when an object implementing this interface loses focus.It is not 
guaranteed to be played to completion.It is implementation dependent whether and when this sound will be truncated by 
any gain focus sound played by the next object to gain focus. By default,an HNavigable object does not have any lose 
focus sound associated with it. Note that the ordering of playing sounds is dependent on the order of the focus 
lost,gained events. Parameters: sound -the sound to be played,when the component loses focus.If sound content is already 
set,the original content is replaced.To remove the sound specify a null HSound 
. */
public void setLoseFocusSound(HSound sound);


/*
De  nes the navigation path from the current HNavigable to another HNavigable when a particular key is pressed. Note 
that setFocusTraversal(HNavigable, HNavigable, HNavigable, HNavigable) is equivalent to multiple calls to setMove(int, 
HNavigable),where the key codes VK_UP VK_DOWN VK_LEFT VK_RIGHT are used.Parameters: keyCode -The key code of the pressed 
key.Any numerical keycode is allowed,but the platform may not be able to generate all keycodes.Application authors 
should only use keys for which HRcCapabilities.isSupported()or HKeyCapabilities.isSupported()returns true. target -The 
target HNavigable object that should be navigated to.If a target is to be removed from a particular navigation path,then 
null should be speci  ed. */
public void setMove(int keyCode, HNavigable target);




}
