
package org.havi.ui;

/*public class HScreenRectanglepublic class HScreenRectangleHScreenRectangle denotes a 
screen area expressed as a relative value of the screen dimensions. Note that since these 
are relative dimensions they are effectively independent of any particular screen's 
physical dimensions,or aspect ratio. Note that the x and y offset coordinates of the 
top,left corner of the area are not constrained -they may be negative,or have values 
greater than one -and hence,may denote an offset location that is not "on- screen".The 
width and height of the area should be positive (including zero),but are otherwise 
unconstrained -and hence may denote areas greater in size than the entire screen. Hence, " 
(0.0, 0.0, 1.0, 1.0) denotes the whole of the screen. " (0.0, 0.0, 0.5, 0.5) denotes the 
top, left hand quarter of the screen. " (0.5, 0.0, 0.5, 0.5) denotes the top, right hand 
quarter of the screen. " (0.25, 0.25, 0.5, 0.5) denotes a centered quarter-screen area of 
the screen. " (0.0, 0.5, 0.5, 0.5) denotes the bottom, left hand quarter of the screen. " 
(0.5, 0.5, 0.5, 0.5) denotes the bottom, right hand quarter of the screen. Note that in 
practice,particularly in the case of television,the precise location may vary slightly due 
to effects of overscan,etc. Note that systems using HScreenRectangle directly should 
consider the effects of rounding errors, etc.The parameters to the constructors are as 
follows,in cases where parameters are not used,then the constructor should use the default 
values. */

public class HScreenRectangle {

/*
 */
public float height;


/*
 */
public float width;


/*
 */
public float x;


/*
 */
public float y;


/*
Creates an HScreenRectangle object.See the class description for details of constructor parameters and default 
values. */
public HScreenRectangle(float _x, float _y, float _width, float _height) {
   x=_x;
   y=_y;
   width=_width;
   height=_height;
}

/*
Set the location of the top left corner of the HScreenRectangle. Parameters: x -the horizontal position of the top left 
corner */
public void setLocation(float _x, float _y) {
   x=_x;
   y=_y;
}

/*
Set the size of the HScreenRectangle. Parameters: width -the width of the HScreenRectangle height -the height of the 
HScreenRectangle */
public void setSize(float _width, float _height) {
   width=_width;
   height=_height;
}


}
