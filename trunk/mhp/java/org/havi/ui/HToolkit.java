/*
 * NIST/DASE API Reference Implementation
 * $File: HToolkit.java $
 * Last changed on $Date: 2001/03/23 22:06:42 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;

/**
 * This is the core of the NIST reference implementation of HAVi UI.  HToolkit
 * instantiates all the classes required to use the implementation, including
 * the simulated GraphicsDevices.  <p>A static instance of this class is
 * automatically created the first time an HComponent during the initialization
 * of the class and is accessible through HComponent.getHToolkit().
 * <p>The default HScreen (see org.havi.ui.HScreen) in this implementation is a
 * single (heavyweight) AWT Frame hosting all the simulated HDevices.  There is
 * no way to access the root container in Java nor to make a heavyweight
 * (including Frame and Window) transparent).
 * <p>Porting the implementation will require changing the HToolkit to
 * instantiate your native GraphicsDevices instead.
 * <p>HState interface declaration. Use these constants to describe the
 * current state of a widget.
 * <p>Revision information:
 * <br> $Revision: 1.5 $
 *
 */


public class HToolkit {

  public static boolean showBorder = false;
  
  public static HTextLayoutManager defaultTextLayoutManager
     = new HDefaultTextLayoutManager();

  public HToolkit() {
  }

  public static HTextLayoutManager getDefaultTextLayoutManager() {
    return defaultTextLayoutManager;
  }
  
}
