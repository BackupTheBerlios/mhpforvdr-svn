/*
 * NIST/DASE API Reference Implementation
 * $File: HStaticIcon.java $
 * Last changed on $Date: 2001/01/17 21:43:14 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Image;

/**
 * See (C) official HaVi documentation for reference
 * <p>
 * HStaticIcon is a widget that provides basic graphical display.
 * This HVisible has only one state.
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 */


public class HStaticIcon extends HVisible implements HNoInputPreferred {

  /* Inherited for Component: x, y, width, height, font
     Inherited from HVisible: tlm default to DefaultTextLayoutManager
                              state, hlook
     Image is stored with setImageContent */

  private static HGraphicLook defaultLook =
    new HGraphicLook(HGraphicLook.BUTTON_DECORATION);
  

  /** Contructor with no parameters. Use all default values */
  public HStaticIcon() {
    super();
    /* Initialize default values */
    setDefaults();
  }

  /** Private utility method to set to defaults */
  private void setDefaults() {
    setInteractionState(HState.NORMAL_STATE);
    setGraphicContent(null, getInteractionState());
    setTextLayoutManager(HToolkit.getDefaultTextLayoutManager());
    try {
      setLook(HStaticIcon.getDefaultLook());
    } catch(HInvalidLookException e) {
      // Just ignore, this cannot happen anyway
      System.err.println("Invalid default look in HStaticIcon: " + e );
    }
  }

  
  /** Constructor with initial geometry  and string
      @param image initial image in the HStaticIcon
      @param x X-axis position
      @param y Y-axis position
      @param width width
      @param height height */
  public HStaticIcon(Image image,
                     int x, int y, int width, int height) {
    super(HStaticIcon.getDefaultLook(), x, y, width, height);
    setDefaults();
    setGraphicContent(image, getInteractionState());
  }
    

  /** Constructor with initial image
      @param image initial image in the HStaticIcon */
  public HStaticIcon(Image image) {

    super();

    setDefaults();

    setGraphicContent(image, getInteractionState());

  }


  /** Assign a new look. Overrides the HVisible method to check
      first that the new look is an HGraphicLook
      @param hlook new look to be applied
      @exception HInvalidLookException if the new look is not an HGraphicLook
  */
  public void setLook(HLook hlook) throws HInvalidLookException {
    if ( hlook instanceof HGraphicLook ) {
      super.setLook(hlook);
    } else {
      throw new HInvalidLookException(
"New look is not an HGraphicLook and would not render this HStaticIcon properly");
    }
  }


  /** Set a new default look for the HStaticIcon class.
      @param hlook new look
      @exception HInvalidLookException if the new look is not a valid look
  */
  public static void setDefaultLook(HGraphicLook hlook)
    throws HInvalidLookException {
    if ( hlook instanceof HGraphicLook ) {
      defaultLook = hlook;
    } else {
      throw new HInvalidLookException(
"New look is not an HGraphicLook and would not render HStaticIcon properly");
    }
  }

  /** Return the current default look for this class
      @return default look */
  public static HGraphicLook getDefaultLook() {
    return defaultLook;
  }


  /** Override the standard Component imageUpdate to force a layout
      when an image become available */
  public boolean imageUpdate(Image img,
                             int infoflags,
                             int x,
                             int y,
                             int width,
                             int height) {
    boolean retval = super.imageUpdate(img, infoflags, x, y, width, height);
    invalidate();
    return retval;
  }
}
