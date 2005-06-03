/*
 * NIST/DASE API Reference Implementation
 * $File: HGraphicsDevice.java $
 * Last changed on $Date: 2001/05/18 14:29:34 UTC $
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
 * $Revision: 1.3 $
 * <br> Base class for Graphics device. The actual implementation for
 * a specific device should be done in a subclass.
 */

//TODO: All the configuration stuff is unimplemented. It simply always takes
// the default configuration.
 
public class HGraphicsDevice extends HScreenDevice {

//see spec for comments
protected HGraphicsConfiguration config;

protected HGraphicsDevice() {
   config = new HGraphicsConfiguration(true, true, MHPScreen.getAspectRatio(), MHPScreen.getResolution(),
             new HScreenRectangle(0, 0, 1, 1), this);
}

//NOT PART OF API
/*protected void setConfig(HGraphicsConfiguration c) {
   config=c;
}*/

public HGraphicsConfiguration getBestConfiguration(HGraphicsConfigTemplate hgct) {
   return config;
}

public HGraphicsConfiguration getBestConfiguration(HGraphicsConfigTemplate[] hgcta) {
   return config;
}

public HGraphicsConfiguration[] getConfigurations() {
   HGraphicsConfiguration[] ret=new HGraphicsConfiguration[1];
   ret[0]=config;
   return ret;
}

public HGraphicsConfiguration getCurrentConfiguration(){
   return config;
}

public HGraphicsConfiguration getDefaultConfiguration() {
   return config;
}

public boolean setGraphicsConfiguration(HGraphicsConfiguration hgc) {
   return false;
}

public java.awt.Dimension getScreenAspectRatio() {
   return MHPScreen.getAspectRatio();
}

public  String getIDstring() {
   return "Graphics Device (" + this.toString() + ")";
}

  /** Add a new HScene to the Graphics device
      <i>Note:</i> This is an implementation method not part of the API
      @param hscene new HScene to add
      @param string String describing this scene (for navigation) */
  void addHScene(HScene scene, String string) {
  }

  /** Remove an HScene from Graphics device
      <i>Note:</i> This is an implementation method not part of the API
      @param hscene new HScene to remove */
  void removeHScene(HScene scene) {
  }

  
  /** Add a new HScene to the Graphics device
      <i>Note:</i> This is an implementation method not part of the API
      @param hscene new HScene to add
      @param string String describing this scene (for navigation) */
  //public abstract void addHScene(HScene scene, String string);
  
  /** Remove an HScene from Graphics device
      <i>Note:</i> This is an implementation method not part of the API
      @param hscene new HScene to remove */
  //public abstract void removeHScene(HScene scene);

  
  /**************************** NOT FROM API ******************************/

  /** This <i>implementation</i> method provides a means to send a soft
      keyboard event to this graphics device.
      A typical application for this is a software keyboard for settop
      boxes that do not have a keyboard attached to it.
      <BR><B>WARNING</B>: this is an implementation method that is not
      part of the official HAVi APIs. Portable applications should not
      use it. Underlining implementation devices may not support it, in
      which case they return null (default).
      @param event KeyEvent to dispatch to the graphics device
      @return the event actually dispatched, or null if this feature is not
              supported.
  */
  /*public KeyEvent fireKeyEvent(KeyEvent event) {
    return null;
  }*/
  

}
