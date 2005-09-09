/*
 * NIST/DASE API Reference Implementation
 * $File: HVideoDevice.java $
 * Last changed on $Date: 2001/01/31 18:01:52 UTC $
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
 * $Revision: 1.1 $
 * <br> Video device. Any implementation for a specific device
 * should subclass this one.
 */

//TODO: when the video stuff is implemented natively
//then we will think of doing the same as with the other H...Configuration.

public class HVideoDevice extends HScreenDevice {

HVideoConfiguration config;

HVideoDevice() {
   config = new HVideoConfiguration(true, true, MHPScreen.getAspectRatio(), MHPScreen.getResolution(),
             new HScreenRectangle(0, 0, 1, 1), this);
}

public java.awt.Dimension getScreenAspectRatio() {
   return MHPScreen.getAspectRatio();
}

public  String getIDstring() {
   return "Video Device (" + this.toString() + ")";
}

public HVideoConfiguration[] getConfigurations() {
   HVideoConfiguration[] ret=new HVideoConfiguration[1];
   ret[0]=config;
   return ret;
}

public HVideoConfiguration getDefaultConfiguration() {
   return config;
}

public HVideoConfiguration getBestConfiguration(HVideoConfigTemplate hbc) {
   return config;
}

public HVideoConfiguration getCurrentConfiguration() {
   return config;
}

public HVideoConfiguration getBestConfiguration(HVideoConfigTemplate[] hbcta) {
   return config;
}

public boolean setVideoConfiguration(HVideoConfiguration hbc)
   throws SecurityException, HPermissionDeniedException, HConfigurationException
{
   return false;
}

  /*
  Obtain a reference to the source of the video being presented by this device at this moment.
  The precise class to be be returned must be speci  ed outside the HAVi user-interface 
  speci  cation.Null is returned if no video is being presented. 
  Returns: a reference to the source of the video 
  Throws: SecurityException -if the application does not have suf  cient rights to get 
  the VideoSource object. 
  HPermissionDeniedException -(HPermissionDeniedException )if the application does not 
  currently have the right to get the VideoSource object.
  */
public java.lang.Object getVideoSource() throws java.lang.SecurityException, HPermissionDeniedException {
   return null;
}

  /*
  Obtain a reference to the object which controls the presentation of the video.
  Null is returned if no video is being presented.
  In systems based on JMF,this would be the javax.media.Player instance
  which owns the resource.
  Returns: the object which controls the presentation of the video
  Throws: SecurityException -if the application does not have suf  cient
  rights to get the VideoPlayer object.
  HPermissionDeniedException -(HPermissionDeniedException )if the application does not
  currently have the right to get the VideoPlayer object.
  */
public java.lang.Object getVideoController() throws java.lang.SecurityException, HPermissionDeniedException {
   return null;
}

}

