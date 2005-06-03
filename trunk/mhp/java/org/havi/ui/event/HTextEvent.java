
package org.havi.ui.event;

import org.havi.ui.HTextValue;

/*An HTextEvent event is used to interact with a component implementing the 
HKeyboardInputPreferred interface as follows: " An HTextEvent event may be sent from the 
HAVi system to the component to cause a change to the caret position or editable mode of 
the component as a result of user interaction. For example, a platform which lacks 
suitable caret positioning or mode switching keys may choose to generate this using a 
virtual keyboard user interface. " An HTextEvent event is sent from the component to all 
registered HTextListener when a change to the text content, caret position or editable 
mode of the component occurs. All interoperable HAVi components which expect to receive 
HTextEvent events should implement the HKeyboardInputPreferred interface.The parameters to 
the constructors are as follows,in cases where parameters are not used,then the 
constructor should use the default values. */

public class HTextEvent extends java.awt.AWTEvent {

/*
When a text event with this id is sent to a HTextValue component,then its caret position should move one character 
forward.If such an event is sent from a component to HTextListener ,then it was moved. */
public static final int CARET_NEXT_CHAR = 2019;


/*
When a text event with this id is sent to a HTextValue component,then its caret position should move down one line.If 
such an event is sent from a component to HTextListener ,then it wa moved.It is widget speci  c,if the caret remains at 
the same column or at an approximate horizontal pixel position for non- xed-width 
fonts. */
public static final int CARET_NEXT_LINE = 2020;


/*
When a text event with this id is sent to a HTextValue component,then its caret position should move down to the last 
possible line in the visible window.If the caret position is already on the last visible line then the caret should move 
down so that the last visible line scrolls up to the top of the visible window.If such an event is sent from a component 
to HTextListener ,then it was moved.It is widget speci  c,if the caret remains at the same column or at an approximate 
horizontal pixel position for non- xed-width fonts. */
public static final int CARET_NEXT_PAGE = 2023;


/*
When a text event with this id is sent to a HTextValue component,then its caret position should move one character 
backward.If such an event is sent from a component to HTextListener ,then it was 
moved. */
public static final int CARET_PREV_CHAR = 2021;


/*
When a text event with this id is sent to a HTextValue component,then its caret position should move up one line.If such 
an event is sent from a component to HTextListener ,then it wa moved.It is widget speci  c,if the caret remains at the 
same column or at an approximate horizontal pixel position for non- xed-width fonts. */
public static final int CARET_PREV_LINE = 2022;


/*
When a text event with this id is sent to a HTextValue component,then its caret position should move up to the  rst 
possible line in the visible window.If the caret position is already on the  rst visible line then the caret should move 
down so that the  rst visible line scrolls down to the bottom of the visible window.If such an event is sent from a 
component to HTextListener ,then it wa moved.It is widget speci  c,if the caret remains at the same column or at an 
approximate horizontal pixel position for non- xed-width fonts. */
public static final int CARET_PREV_PAGE = 2024;


/*
A text event with this id is sent from the component whenever the caret position of an HTextValue component is 
changed.This event will be sent only if the caret position changed in a manner not noti  ed by the CARET_NEXT_CHAR 
,CARET_NEXT_LINE ,CARET_PREV_CHAR , CARET_PREV_LINE ,CARET_NEXT_PAGE ,or CARET_PREV_PAGE 
events. */
public static final int TEXT_CARET_CHANGE = 2017;


/*
A text event with this id is sent from the component whenever the textual content of an HTextValue component is 
changed. */
public static final int TEXT_CHANGE = 2016;


/*
A text event with this id indicates that the textual content of an HTextValue component has been  nally set.This event 
is sent to or from the component when the user causes the component to leave its editable mode.Note that it is a 
platform speci  c implementation option for such components to leave editable mode automatically e.g.when they lose 
input focus.In such a case the order in which the HFocusEvent and HTextEvent are sent is platform speci  
c. */
public static final int TEXT_END_CHANGE = 2018;


/*
A text event with this id indicates that the textual content of an HTextValue component may be about to change.This 
event is sent to or from the component when the user causes the component to enter its editable mode.Note that it is a 
platform speci  c implementation option for such components to enter editable mode automatically e.g.when they receive 
input focus.In such a case the order in which the HFocusEvent and HTextEvent are sent is platform speci  
c. */
public static final int TEXT_START_CHANGE = 2015;


/*
The  rst integer id in the range of event ids supported by the HTextEvent class. */
public static final int TEXT_FIRST = 2015;


/*
The last integer id in the range of event ids supported by the HTextEvent class. */
public static final int TEXT_LAST = 2024;


/*
Constructs an HTextEvent . Parameters: source -The HTextValue component whose value has been modi  ed. id -The event id 
of the HTextEvent generated by the HTextValue component.This is the value that will be returned by the event object's 
getID method. */
public HTextEvent(HTextValue source, int id) {
   super(source, id);
}


}
