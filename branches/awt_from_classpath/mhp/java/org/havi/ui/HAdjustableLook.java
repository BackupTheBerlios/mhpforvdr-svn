
package org.havi.ui;

/*All Known Implementing Classes: HRangeLook HListGroupLook Description The HAdjustableLook interface is implemented by 
all platform looks which support adjustable components (i.e.those components which implement the HAdjustmentValue 
interface.The following platform looks shall implement this interface: " HRangeLook " HListGroupLook The HAdjustableLook 
interface supports pointer based systems by providing a mechanism of "hit- testing"which allows the HAdjustmentValue 
component to determine which part of the on-screen representation has been clicked in,and to adjust its internal value 
accordingly. The diagram below shows one possible on-screen representation of an HAdjustmentValue component,with 
ORIENT_LEFT_TO_RIGHT orientation. (Picture page 805) HLook implementations which implement HAdjustableLook may use the 
getOrientation() method to determine the appropriate constant to return from hitTest(HAdjustmentValue, Point)since the 
correct constant is dependent on the orientation of the component. It is a valid implementation option to return 
ADJUST_NONE from the hitTest(HAdjustmentValue, Point)method in all cases. It is a valid implementation option to never 
return ADJUST_BUTTON_LESS and ADJUST_BUTTON_MORE in the case where such active areas are not presented on screen by the 
HLook */

public interface HAdjustableLook extends HLook {

/*
A constant which may be returned from the hitTest(HAdjustmentValue, Point)method to indicate that the pointer was 
clicked in an adjustment area which indicates that the adjustable value should be decremented by one unit. Such an area 
should be drawn with an arrow pointing towards the minimum end of the range, according to the orientation as retrieved 
with getOrientation(). Use of this constant is implementation-speci  c. */
public static final int ADJUST_BUTTON_LESS = 1;


/*
A constant which may be returned from the hitTest(HAdjustmentValue, Point)method to indicate that the pointer was 
clicked in an adjustment area which indicates that the adjustable value should be incremented by one unit. Such an area 
should be drawn with an arrow pointing towards the maximum end of the range, according to the orientation as retrieved 
with getOrientation(). Use of this constant is implementation-speci  c. */
public static final int ADJUST_BUTTON_MORE = 2;


/*
A constant which may be returned from the hitTest(HAdjustmentValue, Point)method to indicate that the pointer was not 
clicked over an active adjustment area. */
public static final int ADJUST_NONE = 0;


/*
A constant which may be returned from the hitTest(HAdjustmentValue, Point)method to indicate that the pointer was 
clicked in an adjustment area which indicates that the adjustable value should be decremented by one 
block. */
public static final int ADJUST_PAGE_LESS = 2;


/*
A constant which may be returned from the hitTest(HAdjustmentValue, Point)method to indicate that the pointer was 
clicked in an adjustment area which indicates that the adjustable value should be incremented by one 
block. */
public static final int ADJUST_PAGE_MORE = 3;


/*
A constant which may be returned from the hitTest(HAdjustmentValue, Point)method to indicate that the pointer was 
clicked in an adjustment area which indicates that the adjustable value should change according to pointer motion events 
received by the component,until the pointer button is released. */
public static final int ADJUST_THUMB = 4;


/*
Returns a value which indicates the pointer click position in the on-screen representation of the adjustable 
component.Note that it is a valid implementation option to always return ADJUST_NONE . Parameters: component -the 
HAdjustmentValue component for which the hit position should be calculated. pt -the pointer click point. Returns: one of 
ADJUST_NONE ,ADJUST_BUTTON_LESS ,ADJUST_PAGE_LESS ,ADJUST_THUMB , ADJUST_PAGE_MORE or 
ADJUST_BUTTON_MORE */
public int hitTest(HAdjustmentValue component, java.awt.Point pt);



}
