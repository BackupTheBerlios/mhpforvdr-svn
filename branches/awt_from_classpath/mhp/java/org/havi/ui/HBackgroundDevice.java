/*
 * NIST/DASE API Reference Implementation
 * $File: HBackgroundDevice.java $
 * Last changed on $Date: 2001/03/22 17:50:30 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;

import java.awt.Color;
import java.awt.MHPScreen;
import java.awt.MHPBackgroundPlane;

/**
 *
 * See the official (C) HaVi documentation for full specification details.
 *
 * <p>Revision information:<br>
 * $Revision: 1.2 $
 * <br> Background device. Declared abstract (not the case in specs)
 */

//TODO: All the configuration stuff is unimplemented. It simply always takes
// the default configuration.

public class HBackgroundDevice extends HScreenDevice {
  
HBackgroundConfiguration config;
MHPBackgroundPlane plane=MHPScreen.createBackgroundPlane(0, 0, MHPScreen.getResolution().width, MHPScreen.getResolution().height);

protected HBackgroundDevice() {
   config=new HStillImageBackgroundConfiguration
      (false, true, MHPScreen.getAspectRatio(), MHPScreen.getResolution(),
        new HScreenRectangle(0, 0, 1, 1), this, plane.getColor(), true, true);
   plane.setVisible(true);
}

public HBackgroundConfiguration getBestConfiguration(HBackgroundConfigTemplate hgct) {
   //TODO
   return config;
}

public HBackgroundConfiguration getBestConfiguration(HBackgroundConfigTemplate[] hgcta) {
   //TODO
   return config;
}

public HBackgroundConfiguration[] getConfigurations() {
   HBackgroundConfiguration[] ret=new HBackgroundConfiguration[1];
   ret[0]=config;
   return ret;
}

public HBackgroundConfiguration getCurrentConfiguration(){
   return config;
}

public HBackgroundConfiguration getDefaultConfiguration() {
   //TODO
   return config;
}

public boolean setBackgroundConfiguration(HBackgroundConfiguration hgc) {
   //TODO!
   return false;
}


public java.awt.Dimension getScreenAspectRatio() {
     return MHPScreen.getAspectRatio();
}

  /** Device dependant */
public  String getIDstring() {
    return "Background Device (" + this.toString() + ")";
}

  
  /* ******************* NOT PART OF THE API ******************* */

   /** Get the current background color
         @return current background color */
   java.awt.Color getColor() {
      return plane.getColor();
   }
   
   /** Set the background color. This may fail if the device does not support
         variable colors or the caller does not have permission to change it.
         @param newColor new background color */
   void setColor(java.awt.Color color)
               throws HPermissionDeniedException,
                        HConfigurationException {
      plane.setColor(color);
   }
   
   void displayImage(HBackgroundImage image) {
      plane.displayImage(image.getImage());
   }
   
   void displayImage(HBackgroundImage image, HScreenRectangle r) {
      java.awt.Dimension pixelResolution=MHPScreen.getResolution();
      plane.displayImage(image.getImage(), (int) (r.x*((float)pixelResolution.width)),
                                 (int) (r.y*((float)pixelResolution.height)),
                                 (int) (r.width*((float)pixelResolution.width)),
                                 (int) (r.height*((float)pixelResolution.height)) );
   }
  
   //for use by org.dvb.media.content.dripfeed.Player
   public void displayDripfeed(byte[] data) {
      plane.displayDripfeed(data);
   }


}
