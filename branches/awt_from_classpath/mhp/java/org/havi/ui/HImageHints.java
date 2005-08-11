
package org.havi.ui;

/*The HImageHints object allows an application to pass hints to the system how best to 
tailor an image to match a (possibly)restricted HGraphicsConfiguration .The parameters to 
the constructors are as follows,in cases where parameters are not used,then the 
constructor should use the default values. */

public class HImageHints {

/*
The image is business graphics,with strong,well-de  ned,blocks of solid color,etc.Not suitable for dithering,suitable 
for nearest color matching. */
public static final int BUSINESS_GRAPHICS = 0x03;

/*
The image is a cartoon,with strong,well-de  ned,blocks of solid color,etc.Not suitable for dithering, suitable for 
nearest color matching. */
public static final int CARTOON = 0x02;

/*
The image is a two-tone lineart,with colors varying between foreground and background,etc.Not suitable for 
dithering.Possibly suitable for color-map adjustment,etc.,if applicable. */
public static final int LINE_ART = 0x04;

/*
The image is a "natural"scene,with subtle gradations of color,etc.Suitable for 
dithering. */
public static final int NATURAL_IMAGE = 0x01;

private int type;
/*
Created an HImageHints object */
public HImageHints() {
   type = NATURAL_IMAGE;
}

/*
Get the expected type of the image being loaded. */
public int getType() {
   return type;
}

/*
Set the expected type of the image being loaded. Parameters: type -the expected type of 
image */
public void setType(int _type) {
   type=_type;
}


}
