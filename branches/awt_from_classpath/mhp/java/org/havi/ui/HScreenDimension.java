
package org.havi.ui;

/*HScreenDimension denotes a screen dimension expressed as a relative value of the screen 
dimensions.Note that since these are relative dimensions they are effectively independent 
of any particular screen's physical dimensions,or aspect ratio. Note that the extents of 
the dimension must be positive (including zero),but are otherwise unconstrained -and hence 
may denote areas greater in size than the entire screen. Hence, " (1.0, 1.0) denotes the 
size of the entire screen. " (0.5, 0.5) denotes a quarter of the screen. Note that in 
practice,particularly in the case of television,the precise dimension may vary slightly 
due to effects of overscan,etc. Note that systems using HScreenDimension directly should 
consider the effects of rounding errors, etc.The parameters to the constructors are as 
follows,in cases where parameters are not used,then the constructor should use the default 
values. */

public class HScreenDimension {

/*
 */
public float height;


/*
 */
public float width;


/*
Creates an HScreenDimension object.See the class description for details of constructor parameters and default 
values. */
public HScreenDimension(float _width, float _height) {
   height=_height;
   width=_width;
}

/*
Set the extents of the HScreenDimension. Parameters: width -the horizontal extent of the HScreenDimension height -the 
vertical extent of the HScreenDimension */
public void setSize(float _width, float _height) {
   height=_height;
   width=_width;
}


}
