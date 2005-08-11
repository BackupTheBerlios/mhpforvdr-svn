
package org.havi.ui;

/*This class represents a background con  guration which supports the installation of still 
images.The platform using the HAVi user-interface speci  cation must specify which image 
formats are supported. The java.awt.Image class is intentionally not used in order to 
allow the support of image formats which carry suf  cient restrictions that expressing 
them through the API of that class would require extensive use of runtime errors.One speci 
 c example of this is MPEG I-frames.The parameters to the constructors are as follows,in 
cases where parameters are not used,then the constructor should use the default 
values. */


public class HStillImageBackgroundConfiguration extends HBackgroundConfiguration {

/*
It is not intended that applications should directly construct HStillImageBackgroundConfiguration objects. Creates an 
HStillImageBackgroundConfiguration object.See the class description for details of constructor parameters and default
values. */
protected HStillImageBackgroundConfiguration(boolean flicker, boolean interlaced,
                                  java.awt.Dimension aspectRatio, java.awt.Dimension resolution,
                                  HScreenRectangle area,
                                  HBackgroundDevice source, java.awt.Color color,
                                  boolean supportsColorChange,
                                  boolean supportsStillImage) {
   super(flicker, interlaced, aspectRatio, resolution, area, source, color, supportsColorChange, supportsStillImage);
}

/*
Display an image.If the data for the image has not been loaded then this method will block while the data is loaded.It 
is platform dependent whether this image is scaled to  t or whether it is cropped (where too large)or repeated (where 
too small).The position of the image is platform-dependent.If the platform does not scale the image to  t,the previous 
color set using setColor(Color)is shown in the areas where no image is displayed.If no color has been set what is shown 
in this area is platform dependent.What is displayed while the image is loading is implementation speci  c. Note that 
the image may be removed by calling the setColor(Color)method. If the image parameter is null a 
java.lang.NullPointerException is thrown. Parameters: image -the image to display. Throws: java.io.IOException -if the 
data for the HBackgroundImage is not loaded and loading the data is impossible or fails. 
java.lang.IllegalArgumentException -if the HBackgroundImage does not contain an image in a supported image encoding 
format HPermissionDeniedException -if the HBackgroundDevice concerned is not reserved.HConfigurationException -if the 
HStillImageBackgroundConfiguration is not the currently set con  guration for its HBackgroundDevice 
. 
java.io.IOException - if the data for the HBackgroundImage is not loaded and loading the data is impossible or fails. java.lang.IllegalArgumentException - if the HBackgroundImage does not contain an image in a supported image encoding format HPermissionDeniedException - if the HBackgroundDevice concerned is not reserved. HConfigurationException - if the HStillImageBackgroundConfiguration is not the currently set con guration for its HBackgroundDevice .
*/
public void displayImage(HBackgroundImage image) 
   throws java.io.IOException, java.lang.IllegalArgumentException,
          HPermissionDeniedException, HConfigurationException
{
   source.displayImage(image);
}

/*
Display an image to cover a particular area of the screen.If the data for the image has not been loaded then this method 
will block while the data is loaded.It is platform dependent whether this image is scaled to  t or whether it is cropped 
(where too large)or repeated (where too small).The position of the image within the rectangle is platform-dependent.If 
the platform does not scale the image to  t,or the rectangle does not cover the entire display area,the previous color 
set using setColor(Color)is shown in the areas where no image is displayed.If no color has been set what is shown in 
this area is platform dependent. Note that the image may be removed by calling the setColor(Color)method. If either or 
both parameters are null a java.lang.NullPointerException is thrown. Parameters: image -the image to display r -the area 
of the screen to cover with the image Throws: java.io.IOException -if the data for the HBackgroundImage is not loaded 
and loading the data is impossible or fails.java.lang.IllegalArgumentException -if the HBackgroundImage does not contain 
an image in a supported image encoding format HPermissionDeniedException -if the HBackgroundDevice concerned is not 
reserved. HConfigurationException -if the HStillImageBackgroundConfiguration is not the currently set con  guration for 
its HBackgroundDevice . */
public void displayImage(HBackgroundImage image, HScreenRectangle r) {
   source.displayImage(image, r);
}

/*
Set the current color of this background.On platforms where there is a sub-class of java.awt.Color supporting 
transparency of any kind,passing an object representing a non-opaque color is illegal. Platforms with a limited color 
resolution for backgrounds may approximate this value to the nearest available.The getColor()method will return the 
actual value used. Note that calling this method will clear any image currently displayed by the HBackgroundDevice . 
Overrides: setColor(Color)in class HBackgroundConfiguration Parameters: color -the color to be used for the background 
Throws: HPermissionDeniedException -if this HBackgroundDevice does not have the right to control the background 
HConfigurationException -if the color speci  ed is illegal for this platform. */
public void setColor(java.awt.Color color) 
 throws HPermissionDeniedException, HConfigurationException{
   super.setColor(color);
}


}
