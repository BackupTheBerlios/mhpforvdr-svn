/*
 * NIST/DASE API Reference Implementation
 * $File: HScreenConfiguration.java $
 * Last changed on $Date: 2001/03/13 15:16:43 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Dimension;


/**
 * Abstract base class for all screen device configurations
 * See the official (C) HaVi documentation for full specification details.
 *
 * <p>Revision information:<br>
 * $Revision: 1.3 $
 *
 */


public abstract class HScreenConfiguration {


  /** Construct a new HScreenConfiguration */
  public HScreenConfiguration(boolean flicker, boolean interlaced,
                              Dimension aspectRatio, Dimension resolution,
                              HScreenRectangle area) {
    this.hasFlickerFilter = flicker;
    this.isInterlaced = interlaced;
    this.aspectRatio = aspectRatio;
    this.pixelResolution = resolution;
    this.screenArea = screenArea;
  }
  
  /** True if the current configuration has an interlacing flicker filter.*/
  protected boolean hasFlickerFilter;

  /** True if this configuration is interlaced */
  protected boolean isInterlaced;

  /** Configuration's pixel aspect ratio HORIZONTAL:VERTICAL */
  protected Dimension aspectRatio;
 
  /** Configuration's resolution in pixels */
  protected Dimension pixelResolution;

  /** Area of the screen (express in the HScreen device-independent
      coordinate system) used by this configuration. */
  protected HScreenRectangle screenArea;

  
  /** Convert a pixel coordinate in the current configuration to pixel
      coordinates in the target screen configuration. Use this to reduce
      rounding errors that happen while going through the normalized
      HScreenPoint space.
      <p>This may fail (and return null) if information is missing in either
      configuration, or if the conversion is non-linear. Subclasses that
      cannot guarantee that a straightforward linear conversion based on
      getOffset() and getPixelRatio() should override this method.
      @param destination target configuration
      @param source pixel location in this configuration
      @return location in the target configuration or null if no conversion
  */
  public Dimension convertTo(HScreenConfiguration destination,
                             Dimension source) {

    Dimension offset = this.getOffset(destination);

    if(offset == null) {
      return null;
    }
    
    if( (this.getPixelResolution()==null) ||
        (this.getScreenArea()==null) ||
        (destination.getPixelResolution()==null) ||
        (destination.getScreenArea()==null) ) {
      return null;
    }

    Dimension result = new Dimension(source);

    /* Convert to position compared to destination's origin, in current
       pixel space */
    result.width -= offset.width;
    result.height -= offset.height;

    /* Now proceed with the linear conversion to dest's pixel space,
       based on the respective aspect ratios and resolutions:
        1 pixel (on screen) = Resolution / ScreenSize
    */

    result.width *= destination.getPixelResolution().width
      / destination.getScreenArea().width
      * this.getScreenArea().width
      / this.getPixelResolution().width;
    
    result.height *= destination.getPixelResolution().height
      / destination.getScreenArea().height
      * this.getScreenArea().height
      / this.getPixelResolution().height;

    return result;
    
  }

  /** Check that a configuration template is compatible with this one.
      Note: subclass should override this method as needed since a generic
      screen configuration is unaware of such considerations as pixel
      impact or alignment.
      @param hsc target configuration template
      @return true if compatible */
  public boolean isCompatibleConfiguration(HScreenConfigTemplate hsc) {

    /* Match the REQUIRED and REQUIRED_NOT parameters one by one:
     flicker filtering, interlaced, aspect ratio, resolution, location */

    /* Flicker filtering */
    if(hsc.getPreferencePriority(HScreenConfigTemplate.FLICKER_FILTERING)
       == HScreenConfigTemplate.REQUIRED) {
      if( !getFlickerFilter() ) {
        return false;
      }
    }
    if(hsc.getPreferencePriority(HScreenConfigTemplate.FLICKER_FILTERING)
       == HScreenConfigTemplate.REQUIRED_NOT) {
      if( getFlickerFilter() ) {
        return false;
      }
    }

    /* interlacing */
    if(hsc.getPreferencePriority(HScreenConfigTemplate.INTERLACED_DISPLAY)
       == HScreenConfigTemplate.REQUIRED) {
      if( !getInterlaced() ) {
        return false;
      }
    }
    if(hsc.getPreferencePriority(HScreenConfigTemplate.INTERLACED_DISPLAY)
       == HScreenConfigTemplate.REQUIRED_NOT) {
      if( getInterlaced() ) {
        return false;
      }
    }

    /* Aspect ratio */
    Dimension currentRatio = getPixelAspectRatio();
    
    if(hsc.getPreferencePriority(HScreenConfigTemplate.PIXEL_ASPECT_RATIO)
       == HScreenConfigTemplate.REQUIRED) {
      Dimension requestedRatio = (Dimension)
        hsc.getPreferenceObject(HScreenConfigTemplate.PIXEL_ASPECT_RATIO);
      /* A1/B1 == A2/B2 <=> A1.B2 == A2.B1 */
      if( (requestedRatio.width * currentRatio.height) !=
          (requestedRatio.height * currentRatio.width) ) {
        return false;
      }
    }

    /* This would make no sense... */
    if(hsc.getPreferencePriority(HScreenConfigTemplate.PIXEL_ASPECT_RATIO)
       == HScreenConfigTemplate.REQUIRED_NOT) {
      Dimension requestedRatio = (Dimension)
        hsc.getPreferenceObject(HScreenConfigTemplate.PIXEL_ASPECT_RATIO);
      /* A1/B1 == A2/B2 <=> A1.B2 == A2.B1 */
      if( (requestedRatio.width * currentRatio.height) ==
          (requestedRatio.height * currentRatio.width) ) {
        return false;
      }
    }


    /* Resolution */
    Dimension currentResolution = getPixelResolution();
    
    if(hsc.getPreferencePriority(HScreenConfigTemplate.PIXEL_RESOLUTION)
       == HScreenConfigTemplate.REQUIRED) {
      Dimension requestedResolution = (Dimension)
        hsc.getPreferenceObject(HScreenConfigTemplate.PIXEL_RESOLUTION);
      if( ! requestedResolution.equals(currentResolution) ) {
        return false;
      }
    }

    /* This would make very little sense... */
    if(hsc.getPreferencePriority(HScreenConfigTemplate.PIXEL_RESOLUTION)
       == HScreenConfigTemplate.REQUIRED_NOT) {
      Dimension requestedResolution = (Dimension)
        hsc.getPreferenceObject(HScreenConfigTemplate.PIXEL_RESOLUTION);
      if( requestedResolution.equals(currentResolution) ) {
        return false;
      }
    }


    /* Location (screen area) */
    HScreenRectangle currentArea = getScreenArea();
    
    if(hsc.getPreferencePriority(HScreenConfigTemplate.SCREEN_RECTANGLE)
       == HScreenConfigTemplate.REQUIRED) {
      HScreenRectangle requestedArea = (HScreenRectangle)
        hsc.getPreferenceObject(HScreenConfigTemplate.SCREEN_RECTANGLE);
      if( ! currentArea.equals(requestedArea) ) {
        return false;
      }
    }

    if(hsc.getPreferencePriority(HScreenConfigTemplate.SCREEN_RECTANGLE)
       == HScreenConfigTemplate.REQUIRED_NOT) {
      HScreenRectangle requestedArea = (HScreenRectangle)
        hsc.getPreferenceObject(HScreenConfigTemplate.SCREEN_RECTANGLE);
      if( currentArea.equals(requestedArea) ) {
        return false;
      }
    }

    
    return true;
  }
  

  /** Return true if the current configuration includes an interlacing
      flicker filter.
      @return true if there is a flicker filter */
  public boolean getFlickerFilter() {
    return hasFlickerFilter;
  }

  /** Query whether this configuration is interlaced
      @return true if interlaced */
  public boolean getInterlaced() {
    return isInterlaced;
  }

  /** Query the configuration's pixel aspect ratio HORIZONTAL:VERTICAL */
  public Dimension getPixelAspectRatio() {
    return aspectRatio;
  }
 

  /** Query the configuration's resolution in pixels */
  public Dimension getPixelResolution() {
    return pixelResolution;
  }

  /** Query the area of the screen (express in the HScreen device-independent
      coordinate system) used by this configuration. */
  public HScreenRectangle getScreenArea() {
    return screenArea;
  }

  /** Return the offset between the target configuration's origin and
      the configuration's origin, in this configuration's pixel space:
      <code>target@this = offset</code>
      <p>This may fail (and return null) if information is missing in either
      configuration, or if the conversion is non-linear. Subclasses that
      cannot guarantee that a straightforward linear conversion based on
      getPixelResolution and getScreenArea should override this method.
      @param hsc target configuration
      @return offset in pixels or null if this information is not available */
  public Dimension getOffset(HScreenConfiguration target) {
    
    if( (this.getPixelResolution()==null) ||
        (this.getScreenArea()==null) ||
        (target.getPixelResolution()==null) ||
        (target.getScreenArea()==null) ) {
      return null;
    }

    /* Location of target's origin relative to this' origin */
    HScreenPoint targetSP = new HScreenPoint(
         target.getScreenArea().x - this.getScreenArea().x,
         target.getScreenArea().y - this.getScreenArea().y);

    /* Convert to this' space coordinates */
    return new Dimension( (int)(targetSP.x
                                * this.getPixelResolution().width
                                / this.getScreenArea().width),
                          (int)(targetSP.y
                                * this.getPixelResolution().height
                                / this.getScreenArea().height));
    
  }

    
}
