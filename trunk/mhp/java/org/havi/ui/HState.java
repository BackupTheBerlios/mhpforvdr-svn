
package org.havi.ui;

/*The HState interface encapsulates constants for component states which are used in the various HVisible setContent and 
getContent methods,to indicate which state the speci  ed content is to be set. There are two sets of constants de  ned 
in this interface.The  rst set are mutually exclusive state bits, which de  ne properties of the component.The order of 
the states is important;each state has precedence over the one before it when considering the effect on the 
component.For example,the DISABLED_STATE_BIT property is considered the most signi  cant property of a state.The state 
bits are shown in the table below. (page 1087) ... */

public interface HState {

/*
This state bit indicates that the widget has the input focus.
This state is only valid for widgets implementing HNavigable.
If state-based content is not used,the associated look should 
visually distinguish components with this bit set e.g.by highlighting them. */

    public static final int FOCUSED_STATE_BIT = 0x01;
/*
 This state bit indicates that the widget has been actioned.
 HActionable components only have this bit set for the duration 
 of the calls to their registered ActionListeners,whereas HSwitchable 
 components may remain with the ACTIONED bit set until further user 
 input causes them to leave it.If state-based content is not used,
 the associated look should visually distinguish components with 
 this bit set e.g.by drawing them as "pushed in".*/
 
    public static final int ACTIONED_STATE_BIT = 0x02;
    
/*
 This state bit indicates that the widget has been actioned.
 HActionable components only have this bit set for the duration 
 of the calls to their registered ActionListeners,whereas HSwitchable 
 components may remain with the ACTIONED bit set until further user 
 input causes them to leave it.If state-based content is not used,
 the associated look should visually distinguish components with 
 this bit set e.g.by drawing them as "pushed in".*/
 
    public static final int DISABLED_STATE_BIT = 0x04;
    
/*
Constant used to indicate the value of the  rst (builtin)component state. */

    public static final int FIRST_STATE = 0x80;

/*
This constant (i.e.no state bits set)indicates that the 
widget is in its normal state.This state is applicable to all 
HVisible components.  */
    
    public static final int NORMAL_STATE = 0x80;
    
/*
This state indicates that the widget has input focus.This state is applicable to all HNavigable 
components. */

    public static final int FOCUSED_STATE = 0x81;

/*
This state indicates that the widget has been actioned,but does not have focus.HSwitchable components may stay in this 
state until they are actioned again.This state is applicable to all HActionable and HSwitchable 
components. */
    
    public static final int ACTIONED_STATE = 0x82;

/*
This state indicates that the widget has been actioned,and has focus.HSwitchable components may stay in this state until 
they are actioned again.This state is applicable to all HActionable and HSwitchable 
components. */
    
    public static final int ACTIONED_FOCUSED_STATE = 0x83;

/*
This state indicates that the widget is disabled.This state is applicable to all HVisible 
components. */
    
    public static final int DISABLED_STATE = 0x84;
/*
This state indicates that the widget has input focus but is disabled.This state is applicable to all HNavigable 
components. */

    public static final int DISABLED_FOCUSED_STATE = 0x85;
    
/*
This state indicates that the widget has been actioned but is disabled.This state is applicable to all HSwitchable 
components. */
    
    public static final int DISABLED_ACTIONED_STATE = 0x86;
    
/*
This state indicates that the widget has been actioned and has input focus but is disabled.This state is applicable to 
all HSwitchable components. */
    
    public static final int DISABLED_ACTIONED_FOCUSED_STATE = 0x87;

/*
Constant used to indicate all of the applicable states for a given component. Note that the ALL_STATES constant should 
only be used in setting content setTextContent(String, int)setGraphicContent(Image, int) setAnimateContent(Image[], 
int)setContent(Object, int) The ALL_STATES constant should not be used for retrieving content:getTextContent(int) 
getGraphicContent(int)getAnimateContent(int)getContent(int) */
    
    public static final int ALL_STATES = 0x07;
    
/*
Constant used to indicate the value of the last (builtin)component state. */
//Spec says 0x07, OpenMHP uses 0x87, which makes sense I think
//If you revert this to 0x07, fix HVisible.checkState() accordingly

    public static final int LAST_STATE = 0x87;


}
