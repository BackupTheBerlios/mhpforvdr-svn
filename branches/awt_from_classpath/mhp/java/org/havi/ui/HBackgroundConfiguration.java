/*
 * NIST/DASE API Reference Implementation
 * $File: HBackgroundConfiguration.java $
 * Last changed on $Date: 2001/03/22 17:51:24 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Background device configuration.
 * See the official (C) HaVi documentation for full specification details.
 *
 * <p>Revision information:<br>
 * $Revision: 1.2 $
 *
 */

public class HBackgroundConfiguration extends HScreenConfiguration {

  /* Added information: color, whether it is changeable, whether it supports
     a still image, device associated with it. */

  /** Device associated with this configuration */
  protected HBackgroundDevice source = null;

  /** Background color */
  protected Color color = null;

  /** True if the background color can be changed */
  protected boolean supportsColorChange = false;

  /** True if the source device supports a background image */
  protected boolean supportsStillImage = false;


  /** Constructor -- NOT PART OF THE API */
  HBackgroundConfiguration(boolean flicker, boolean interlaced,
                                  Dimension aspectRatio, Dimension resolution,
                                  HScreenRectangle area,
                                  HBackgroundDevice source, Color color,
                                  boolean supportsColorChange,
                                  boolean supportsStillImage) {
    super(flicker, interlaced, aspectRatio, resolution, area);
    this.source = source;
    this.color = color;
    this.supportsColorChange = supportsColorChange;
    this.supportsStillImage = supportsStillImage;

  }

  /** Return the device associated with this configuration
      @return source device */
  public HBackgroundDevice getDevice() {
    return source;
  }

  /** Return a configuration template that describes/identifies
      this configuration. Supported properties will have a REQUIRED priority,
      non-supported ones REQUIRED_NOT.
      Sanity check:
      <pre>BackgroundDevice.getBestMatch(HBackgroundConfiguration.getConfigTemplate())</pre>
      @return a matching config. template */
  public HBackgroundConfigTemplate getConfigTemplate() {
    // TODO : implement this
    System.out.println("HBackgroundConfiguration.getConfigTemplate only returning null, TODO!");
    return null;
  }


  /** Return the current background color. There is no guarantee that the
      returned color will match the last setColor() call
      @return current color */
  public Color getColor() {
    return source.getColor();
  }

  /** Set the background color
      @param color new background color
      @exception HPermissionDeniedException is this operation is not permitted
      @exception HConfigurationException is the requested color is not supported
  */
  public void setColor(Color color)
    throws HPermissionDeniedException,
           HConfigurationException {
    //it seems to me as if this method should change the color "live", i.e.
    //when called and not when this configuration is set on the device.
    source.setColor(color);
  }


  /** Overrides the HScreenConfiguration template to take into account
      additional information specific to background devices.
      Check that a configuration template is compatible with this one.
      @param hsc target configuration template
      @return true if compatible */
  /*public boolean isCompatibleConfiguration(HScreenConfigTemplate hsc) {

    
    // Match the REQUIRED and REQUIRED_NOT parameters one by one:
    //   still image an color-changeable

    // changeable bg color
    if(hsc.getPreferencePriority(HBackgroundConfigTemplate.CHANGEABLE_SINGLE_COLOR)
       == HScreenConfigTemplate.REQUIRED) {
      if( !supportsColorChange ) {
        return false;
      }
    }
    if(hsc.getPreferencePriority(HBackgroundConfigTemplate.CHANGEABLE_SINGLE_COLOR)
       == HScreenConfigTemplate.REQUIRED_NOT) {
      if( supportsColorChange ) {
        return false;
      }
    }

    // still bg image support 
    if(hsc.getPreferencePriority(HBackgroundConfigTemplate.STILL_IMAGE)
       == HScreenConfigTemplate.REQUIRED) {
      if( !supportsStillImage ) {
        return false;
      }
    }
    if(hsc.getPreferencePriority(HBackgroundConfigTemplate.STILL_IMAGE)
       == HScreenConfigTemplate.REQUIRED_NOT) {
      if( supportsStillImage ) {
        return false;
      }
    }
    return super.isCompatibleConfiguration(hsc);
    
  }
  */

}
