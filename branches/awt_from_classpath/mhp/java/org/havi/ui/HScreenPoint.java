
package org.havi.ui;

/*HScreenPoint denotes a screen location expressed as a relative value of the screen 
dimensions. Note that since these are relative dimensions they are effectively independent 
of any particular screen's physical dimensions,or aspect ratio. The x coordinate is in 
terms of the ratio of the particular horizontal screen location to the entire screen 
width. The y coordinate is in terms of the ratio of the particular vertical screen 
location to the entire screen width. All measurements should be taken from the top,left 
corner of the screen,measuring positive dimensions down and to the right. Note that x and 
y coordinates are not constrained -they may be negative,or have values greater than one 
-and hence,may denote locations that are not "on-screen".Hence, " (0.0, 0.0) denotes the 
top, left hand corner of the screen. " (1.0, 0.0) denotes the top, right hand corner of 
the screen. " (0.5, 0.5) denotes the center (middle) of the screen. " (0.0, 1.0) denotes 
the bottom, left hand corner of the screen. " (1.0, 1.0) denotes the bottom, right hand 
corner of the screen. Note that in practice,particularly in the case of television,the 
precise location may vary slightly due to effects of overscan,etc.The parameters to the 
constructors are as follows,in cases where parameters are not used,then the constructor 
should use the default values. */

public class HScreenPoint {

/*
 */
public float x;


/*
 */
public float y;


/*
Creates an HScreenPoint object.See the class description for details of constructor parameters and default 
values. */
public HScreenPoint(float _x, float _y) {
   x=_x;
   y=_y;
}

/*
Set the location of the HScreenPoint. Parameters: x -the horizontal position of the point y -the vertical position of 
the point */
public void setLocation(float _x, float _y) {
   x=_x;
   y=_y;
}


}
