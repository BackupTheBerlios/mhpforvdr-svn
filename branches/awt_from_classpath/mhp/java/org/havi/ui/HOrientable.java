
package org.havi.ui;

/*The HOrientable interface provides support for components which have an orientation. All interoperable implementations 
of the HOrientable interface must extend HComponent */

public interface HOrientable {

/*
A constant which speci  es that the HOrientable should be rendered with a vertical orientation, with the minimum value 
on the bottom,and the maximum value on the top. */
public static final int ORIENT_BOTTOM_TO_TOP=3;


/*
A constant which speci  es that the HOrientable should be rendered with a horizontal orientation, with the minimum value 
on the left side,and the maximum value on the right side. */
public static final int ORIENT_LEFT_TO_RIGHT = 0;


/*
A constant which speci  es that the HOrientable should be rendered with a horizontal orientation, with the minimum value 
on the right side,and the maximum value on the left side. */
public static final int ORIENT_RIGHT_TO_LEFT = 1;


/*
A constant which speci  es that the HOrientable should be rendered with a vertical orientation, with the minimum value 
on the top,and the maximum of the range on the bottom. */
public static final int ORIENT_TOP_TO_BOTTOM = 2;


/*
Retrieve the orientation of the HOrientable .The orientation controls how an associated HLook lays out the component and 
affects the visual behavior of the HAdjustmentEvent and HItemEvent events.For example,the system might use this 
information to select appropriate key mappings for these events. Returns: one of ORIENT_LEFT_TO_RIGHT 
,ORIENT_RIGHT_TO_LEFT ,ORIENT_TOP_TO_BOTTOM ,or ORIENT_BOTTOM_TO_TOP . */
public int getOrientation();


/*
Set the orientation of the HOrientable .The orientation controls how the associated HLook lays out the component. 
Parameters: orient -one of ORIENT_LEFT_TO_RIGHT ,ORIENT_RIGHT_TO_LEFT , ORIENT_TOP_TO_BOTTOM ,or ORIENT_BOTTOM_TO_TOP 
. */
public void setOrientation(int orient);



}
