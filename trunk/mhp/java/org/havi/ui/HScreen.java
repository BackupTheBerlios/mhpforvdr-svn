/*
 * NIST/DASE API Reference Implementation
 * $File: HScreen.java $
 * Last changed on $Date: 2001/02/01 23:07:44 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;

import java.awt.MHPScreen;

/**
 *
 * See the official (C) HaVi documentation for full specification details.
 *
 * <p>Revision information:<br>
 * $Revision: 1.2 $
 *
 */

/** This class describes the output capabilities of the output display, in
 *  terms of video, graphics, background devices.  See the HaVi specs for
 *  reference.
 *  Implementation subclasses only need to override those methods that
 *  relate to functionalities they implement. By default, return a
 *  non-available state.
 */

public class HScreen extends MHPScreen {


  static final HScreen defaultHScreen = new HScreen();
  
  HGraphicsDevice defaultGraphicsDevice = new HGraphicsDevice();
  HBackgroundDevice defaultBackgroundDevice = new HBackgroundDevice();
  HVideoDevice defaultVideoDevice = new HVideoDevice();
  
  /** Constructor */
  HScreen() {
  }

  /** Return the HScreens available on this system */
  public static HScreen[] getHScreens() {
     return new HScreen [] { defaultHScreen };
  }

  /** Return the default HScreen for this "application"
   * //TODO: not clear what this is from the specs.
   */
  public static HScreen getDefaultHScreen() {
    return defaultHScreen;
  }

  /** Return an array of available video devices on this screen
   *  If video is associated with the current application, then the first
   *  entry must contain the corresponding video device if the video was
   *  started before the application. (See specs for details)
   *  @return list of video devices or null if none */
  public HVideoDevice[] getHVideoDevices() {
   //TODO when no longer null
    return null;
  }

  /** Default video device for this screen.
   *  @return default HVideoDevice or null if none */
  public HVideoDevice getDefaultHVideoDevice() {
    return defaultVideoDevice;
  }


  /** Return an array of available graphics devices on this screen.
   *  @return list of graphics devices or null if none */
  public HGraphicsDevice[] getHGraphicsDevices() {
     return new HGraphicsDevice [] { defaultGraphicsDevice };
  }
  
  /** Default graphics device for this screen.
   *  @return default HGraphicsDevice or null if none */
  public HGraphicsDevice getDefaultHGraphicsDevice() {
    return defaultGraphicsDevice;
  }

  /** Return an array of available background devices on this screen.
   *  @return list of background devices or null if none */
  public HBackgroundDevice[] getHBackgroundDevices() {
     return new HBackgroundDevice [] { defaultBackgroundDevice };
  }

  
  /** Default background device for this screen.
   *  @return default HBackgroundDevice or null if none */
  public HBackgroundDevice getDefaultHBackgroundDevice() {
    return defaultBackgroundDevice;
  }
  
  /** Return a set of coherent screen configurations matching the template.
   *  *** SEE SPECS (C) ***
   *  @param hscta  an array of objects describing desired/required config.
   *  @return array of non-null objects or null if no match */
  public HScreenConfiguration[] getCoherentScreenConfigurations(HScreenConfigTemplate[] hscta) {
    System.err.println("HScreen.getCoherentScreenConfigurations: Only returning null");
    return null;
  }


  /** See specs
   * @param hsca  the array of configurations that should be applied
   *              atomically (where possible).
   * @return true if all the configurations could be applied. Beware that if
   *         this returns false, *some* device configuration may have changed
   *         already and the caller should take extra steps to undo
   *         the partial changes if this is critical. See specs.
   * @exception  java.lang.SecurityException insufficient rights
   * @exception  HPermissionDeniedException HaVi permission error
   * @exception  HConfigurationException invalid configuration
   */
  public boolean setCoherentScreenConfigurations(HScreenConfiguration[] hsca)
                                        throws java.lang.SecurityException,
                                               HPermissionDeniedException,
                                               HConfigurationException {
    System.err.println("HScreen.setCoherentScreenConfigurations: Only returning false");
    return false;
  }

}
