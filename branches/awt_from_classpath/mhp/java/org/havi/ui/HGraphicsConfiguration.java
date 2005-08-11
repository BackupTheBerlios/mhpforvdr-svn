/*
 * NIST/DASE API Reference Implementation
 * $File: HGraphicsConfiguration.java $
 * Last changed on $Date: 2001/02/16 20:17:12 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Container;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;

//This is a mess, mostly unimplemented. For documentation, refer to the
//spec rather than the comments here.

/**
 *
 * See the official (C) HaVi documentation for full specification details.
 * <br>Implementation of the HGraphicsConfiguration object.
 * There can be several configurations for a single graphics device.
 *
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 */

public class HGraphicsConfiguration extends HScreenConfiguration {


  /** Source */
  private HGraphicsDevice sourceDevice;
  
  /** Construct a new HScreenConfiguration */
  public HGraphicsConfiguration(boolean flicker, boolean interlaced,
                                Dimension aspectRatio, Dimension resolution,
                                HScreenRectangle area,
                                HGraphicsDevice source) {
    super(flicker, interlaced, aspectRatio, resolution, area);
    this.sourceDevice = source;
  }
  
  

  /* Methods specific to Graphics configurations */


  /** Return the graphics device associated with this configuration
      @return source HGraphics device */
  public HGraphicsDevice getDevice() {
    return this.sourceDevice;
  }

  /** Return a configuration template that matches the current configuration,
      with REQUIRED priority for properties/features that are supported and
      REQUIRED_NOT for properties that are not.
      @return a matching template */
  public HGraphicsConfigTemplate getConfigTemplate() {

    /* //TODO: sanity check
       HGraphicsDevice.getBestMatch(HGraphicsConfiguration.getConfigTemplate())
       should return the original configuration */
    // TODO NOT IMPLEMENTED
    System.err.println("HGraphicsConfiguration.getConfigTemplate : Only returning null, TODO!!");
    return null;
    

  }


  /** Return the on-screen location of a Component as expressed as an
      HScreenRectangle for this Graphics device in the current configuration.
      Return null if not currently a child of the HScene.
      @param component AWT component to evaluate
      @return on-screen location or null if unavailable */
  public HScreenRectangle getComponentHScreenRectangle(Component component) {
    // TODO implement this
    System.err.println("HGraphicsConfiguration.getComponentHScreenRectangle: Only returning null, TODO!!");
    return null;
  }

   public void dispose(Color color){
      System.err.println("HGraphicsConfiguration.dispose: Doing nothing, TODO!!");
   }

   public Font[] getAllFonts(){
      System.err.println("HGraphicsConfiguration.getAllFonts: Only returning Tiresias, TODO!!");
      return new Font[]{ new Font("Tiresias",Font.PLAIN, 24) };
   }

   public Rectangle getPixelCoordinatesHScreenRectangle(HScreenRectangle hscreenrectangle, Container container){
      System.err.println("HGraphicsConfiguration.getPixelCoordinatesHScreenRectangle: Only returning null, TODO!!");
      return null;
   }

   public Image getCompatibleImage(Image image, HImageHints himagehints){
      System.err.println("HGraphicsConfiguration.getCompatibleImage: Only returning null, TODO!!");
      return null;
   }

   public Color getPunchThroughToBackgroundColor(int i){
      System.err.println("HGraphicsConfiguration.getPunchThroughToBackgroundColor: Only returning null, TODO!!");
      return null;
   }

   public Color getPunchThroughToBackgroundColor(int i, HVideoDevice hvideodevice){
      System.err.println("HGraphicsConfiguration.getPunchThroughToBackgroundColor: Only returning null, TODO!!");
      return null;
   }

   public Color getPunchThroughToBackgroundColor(Color color, int i){
      System.err.println("HGraphicsConfiguration.getPunchThroughToBackgroundColor: Only returning null, TODO!!");
      return null;
   }

   public Color getPunchThroughToBackgroundColor(Color color, int i, HVideoDevice hvideodevice){
      System.err.println("HGraphicsConfiguration.getPunchThroughToBackgroundColor: Only returning null, TODO!!");
      return null;
   }

  /* 
getPixelCoordinatesHScreenRectangle

public java.awt.Rectangle getPixelCoordinatesHScreenRectangle(HScreenRectangle sr,
                                                              java.awt.Container cont)

      Returns a java.awt.Rectangle which contains the graphics (AWT) pixel area for an HScreenRectangle
      relative to the supplied java.awt.Container.
      Parameters:
            sr - the screen location expressed as an HScreenRectangle.
            cont - the java.awt.Container in whose coordinate system the screen location should be
            expressed.
      Returns:
            a java.awt.Rectangle which contains the graphics (AWT) pixel area for an HScreenRectangle
            relative to the supplied java.awt.Container. The returned x, y, width, height values in the
            java.awt.Rectangle should be such that a 
                  r = getPixelCoordinatesHScreenRectangle(sr, cont); 
                  cont.add(component); 
                  component.setBounds(r.x, r.y, r.width, r.height); 
            should ensure that the dimensions of the component on-screen should correspond to the
            given HScreenRectangle, subject to clipping by its parent container, cont. 

            Note that the HScreenRectangle (HScreenPoint) coordinates are in floats - conversion to pixel
            coordinate systems necessarily implies a potential loss of precision - however, such
            conversion should be to the "nearest" integer pixel coordinate.



getCompatibleImage

public java.awt.Image getCompatibleImage(java.awt.Image input,
                                         HImageHints ih)

      Modifies a java.awt.Image so that it is compatible with the current HGraphicsConfiguration. 

      Note: Unmodified Images, or Images modified for other HGraphicsConfiguration's should still be able
      to be rendered within this HGraphicsConfiguration, but may not be as efficient (rapid) in terms of
      rendering, and may not be presented optimally. For example, an 8bit per RGB component image
      loaded onto a configuration with a 4bit per RGB component framebuffer may have its pixel values
      truncated, if this Image is then displayed on an alternate configuration with 16bits per RGB
      component then it will obviously not be displayed optimally. 

      The HImageHints provide a mechanism to indicate how any conversion to a constrained graphics
      environment might best be performed, by describing the general image contents. 

      It is implementation (and algorithmically) dependent whether this method operates on partial, or
      complete Image pixel data.
      Parameters:
            input - the java.awt.Image to be modified
            ih - an HImageHints object that indicates the expected type of the input Image, so that its
            presentation can be optimally adjusted.



getAllFonts

public java.awt.Font[] getAllFonts()

      List the fonts that are always available on the device, but does not list fonts that may be (temporarily)
      available for download from other sources.



getAvailableFontFamilyNames

public java.lang.String[] getAvailableFontFamilyNames()

      List the font family names that are always available on the device, but does not list font family names
      that may be (temporarily) available for download from other sources.



getAvailableFontFamilyNames

public java.lang.String[] getAvailableFontFamilyNames(java.util.Locale l)

      List the font family names that are always available on the device, but does not list font family names
      that may be (temporarily) available for download from other sources.



getPunchThroughToBackgroundColor

public java.awt.Color getPunchThroughToBackgroundColor(int percentage)

      This method returns a Color that may be used in standard graphics drawing operations, which has
      the effect of modifying the existing colour of a pixel to make it partially (or wholly) transparent to the
      background. The existing pixel percentage transparency to the background at that point shall be
      equivalent to the (closest) percentage value as specified in the
      getPunchThroughToBackgroundColor percentage parameter. 

      The existing RGB values of the pixel are unchanged as far as possible, within the limits of the
      platform. Platforms with restricted color spaces may make approximations as required to obtain the
      best possible match. 

      The precise contents of the background are as defined by the platform including any
      HBackgroundDevice, etc.
      Parameters:
            percentage - the new blending value for each pixel drawn with this colour with respect to what
            is outside this HGraphicsConfiguration. The specified value will be clamped to the range 0 to
            100.
      Returns:
            a Color with the desired effect or null for configurations which do not or are currently unable to
            support this rendering mode.



getPunchThroughToBackgroundColor

public java.awt.Color getPunchThroughToBackgroundColor(int percentage,
                                                       HVideoDevice hvd)

      This method returns a Color that may be used in standard graphics drawing operations, which has
      the effect of "punching though" the HGraphicsDevice in which the drawing operation is performed. The
      specifed HVideoDevice is revealed through the drawn "hole". The value specified replaces the
      blending value (with respect to this HVideoDevice ) of each pixel drawn with this colour. The existing
      RGB values of the pixel are unchanged as far as possible within the limits of the platform. Platforms
      with restricted color spaces may make approximations as required to obtain the best match possible.
      Parameters:
            percentage - the new alpha value for each pixel drawn with this colour with respect to the the
            HVideoDevice specified. The specified value will be clamped to the range 0 to 100.
            hvd - the HVideoDevice to reveal.
      Returns:
            a Color with the desired effect or null for configurations which do not or are currently unable to
            support this rendering mode.



getPunchThroughToBackgroundColor

public java.awt.Color getPunchThroughToBackgroundColor(java.awt.Color color,
                                                       int percentage)

      This method returns a Color that may be used in standard graphics drawing operations, which has
      the effect of "punching though" all Components that are behind the Component in which the drawing
      operation is performed. This includes any visual Components acquired from JMF players. What is
      behind this HGraphicsConfiguration is revealed through the drawn "hole" blended with the graphics
      colour specified as the first parameter to this method. Platforms with restricted color spaces may
      make approximations as required to obtain the best match possible.
      Parameters:
            color - the graphics colour to blend
            percentage - the blending value for this colour with respect to what is outside this
            HGraphicsConfiguration. The specified value will be clamped to the range 0 to 100.
      Returns:
            a Color with the desired effect or null for configurations which do not or are currently unable to
            support this rendering mode.



getPunchThroughToBackgroundColor

public java.awt.Color getPunchThroughToBackgroundColor(java.awt.Color color,
                                                       int percentage,
                                                       HVideoDevice v)

      This method returns a Color that may be used in standard graphics drawing operations, which has
      the effect of modifying the existing colour of a pixel to make it partially (or wholly) transparent to the
      background. The existing pixel percentage transparency to the background at that point shall be
      equivalent to the (closest) percentage value as specified in the
      getPunchThroughToBackgroundColor percentage parameter. 

      The existing RGB values of the pixel are unchanged as far as possible, within the limits of the
      platform. Platforms with restricted color spaces may make approximations as required to obtain the
      best possible match. 

      The precise contents of the background are as defined by the platform including any
      HBackgroundDevice, etc.
      Parameters:
            color - the graphics colour to blend
            percentage - the alpha value for this colour with respect to what is outside this
            HGraphicsConfiguration. The specified value will be clamped to the range 0 to 100.
      Returns:
            a Color with the desired effect or null for configurations which do not or are currently unable to
            support this rendering mode.



dispose

public void dispose(java.awt.Color c)

      This method is used by an application when a colour returned from those versions of the method
      getPunchThroughToBackgroundColor with a Color as a parameter is no longer equired. It is the
      responsibility of applications to ensure that no pixels which they had drawn using this colour are still
      displayed on the screen before calling this method. The result of using such a Color after calling this
      method is implementation dependent. Using a colour obtained from another source apart from the
      specified methods will result in this method having no effect.
      Parameters:
            c - the Color which is no longer required


 Overview 
           Package 
                      Class 
                            Tree 
                                 Deprecated 
                                             Index 
                                                    Help 

                                                              HAVi Java APIs 1.0 January 18, 2000 
  PREV CLASS   NEXT CLASS
                                   FRAMES    NO FRAMES
 SUMMARY:  INNER | FIELD | CONSTR | METHOD
                                   DETAIL:  FIELD | CONSTR | METHOD


The HAVi Specification: Appendix A HAVi Java APIs.
Copyright 2000 by Grundig, Hitachi, Matsushita, Philips, Sharp, Sony, Thomson and Toshiba. 
Java is a trademark of Sun Microsystems, Inc. 
All rights reserved. 
  */

}
