/*
 * NIST/DASE API Reference Implementation
 * $File: HStaticText.java $
 * Last changed on $Date: 2000/12/14 16:58:50 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Color;
import java.awt.Font;

/**
 * See (C) official HaVi documentation for reference
 * <p>
 * HStaticText is a widget that provides basic text display (aka label).
 * This HVisible has only one state.
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 */


public class HStaticText extends HVisible implements HNoInputPreferred {

  /* Inherited for Component: x, y, width, height, font
     Inherited from HVisible: tlm default to DefaultTextLayoutManager
                              state, hlook
     Text is stored with setTextContent */

  private static HTextLook defaultLook =
    new HTextLook(HTextLook.LABEL_DECORATION);
  
  
  /** Contructor with no parameters. Use all default values */
  public HStaticText() {
    super();
    /* Initialize default values */
    setDefaults();
  }

  /** Private utility method to set to defaults */
  private void setDefaults() {
    setInteractionState(HState.NORMAL_STATE);
    setTextContent(null, getInteractionState());
    setTextLayoutManager(HToolkit.getDefaultTextLayoutManager());
    try {
      setLook(HStaticText.getDefaultLook());
    } catch(HInvalidLookException e) {
      // Just ignore, this cannot happen anyway
    }
  }

  
  /** Constructor with initial geometry  and string
      @param text initial text in the HStaticText
      @param x X-axis position
      @param y Y-axis position
      @param width width
      @param height height */
  public HStaticText(String text,
                     int x, int y, int width, int height) {
    super(HStaticText.getDefaultLook(), x, y, width, height);
    setDefaults();
    setTextContent(text, getInteractionState());
  }
    

  /** Constructor with initial geometry  and string and other rendering options.
      @param text initial text in the HStaticText
      @param x X-axis position
      @param y Y-axis position
      @param width width
      @param height height
      @param font font
      @param foreground foreground color
      @param background background color
      @param tlm text layout manager */

  public HStaticText(String text,
                     int x, int y, int width, int height,
                     Font font, Color foreground, Color background,
                     HTextLayoutManager tlm) {
    
    super(HStaticText.getDefaultLook(), x, y, width, height);

    setDefaults();

    setTextContent(text, getInteractionState());
    setTextLayoutManager(tlm);
    setFont(font);
    setForeground(foreground);
    setBackground(background);

  }
  

  /** Constructor with initial text
      @param text initial text in the HStaticText */
  public HStaticText(String text) {

    super();

    setDefaults();

    setTextContent(text, getInteractionState());

  }

  /** Constructor with initial string and other rendering options.
      @param text initial text in the HStaticText
      @param font font
      @param foreground foreground color
      @param background background color
      @param tlm text layout manager */
  public HStaticText(String text,
                     Font font, Color foreground, Color background,
                     HTextLayoutManager tlm) {
    
    super();
    setDefaults();

    setTextContent(text, getInteractionState());
    setTextLayoutManager(tlm);
    setFont(font);
    setForeground(foreground);
    setBackground(background);

}

  /** Assign a new look. Overrides the HVisible method to check
      first that the new look is an HTextLook
      @param hlook new look to be applied
      @exception HInvalidLookException if the new look is not an HLook
  */
  public void setLook(HLook hlook) throws HInvalidLookException {
    if ( hlook instanceof HTextLook ) {
      super.setLook(hlook);
    } else {
      throw new HInvalidLookException(
"New look is not an HTextLook and would not render this HStaticText properly");
    }
  }


  /** Set a new default look for the HStaticText class.
      @param hlook new look
      @exception HInvalidLookException if the new look is not an HLook
  */
  public static void setDefaultLook(HTextLook hlook)
    throws HInvalidLookException {
    if ( hlook instanceof HTextLook ) {
      defaultLook = hlook;
    } else {
      throw new HInvalidLookException(
"New look is not an HTextLook and would not render HStaticText properly");
    }
  }

  /** Return the current default look for this class
      @return default look */
  public static HTextLook getDefaultLook() {
    return defaultLook;
  }

}
