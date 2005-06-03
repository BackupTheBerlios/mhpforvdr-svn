package org.havi.ui;

//Taken and adapted from OpenMHP, license is LGPL
/**
* @author tejopa
* @date 15.2.2004
* @status partially implemented
* @module internal
* TODO implementation specific preferences
* HOME
*/

public class HBackgroundConfigTemplate extends HScreenConfigTemplate{

   public static final int CHANGEABLE_SINGLE_COLOR = 0x0A;
   public static final int STILL_IMAGE             = 0x0B;

   public HBackgroundConfigTemplate(){
   }
   
   public boolean isConfigSupported(HBackgroundConfiguration hbc){
      //Out.printMe(Out.TODO);
      return true;
   }

   void checkPriority(int preference){
      switch (preference) {
      case ZERO_BACKGROUND_IMPACT:
      case ZERO_GRAPHICS_IMPACT:
      case ZERO_VIDEO_IMPACT:
      case INTERLACED_DISPLAY:
      case FLICKER_FILTERING:
      case VIDEO_GRAPHICS_PIXEL_ALIGNED:
      case CHANGEABLE_SINGLE_COLOR:
      case STILL_IMAGE:
         break;
      default:
         throw new IllegalArgumentException();
      }
   }

}

/*
 * NIST/DASE API Reference Implementation
 * $File: HBackgroundConfigTemplate.java $
 * Last changed on $Date: 2001/03/13 15:13:33 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 *

package org.havi.ui;


/**
 * Template to request and identifiy HBackgroundDevice configurations.
 * <br>See (C) official HaVi documentation for reference.
 * <p>
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 *

public class HBackgroundConfigTemplate  extends HScreenConfigTemplate {

  // Nearly everything is inherited from HScreenConfigTemplate 
  
  public static final int CHANGEABLE_SINGLE_COLOR = 0x0A;

  public static final int STILL_IMAGE = 0x0B;

  // Not from API, but useful to chain constants down the hierarchy and
   //   avoid overlaps.
  public static final int LAST_PREFERENCE = STILL_IMAGE;
  

  /// Constructor with no parameters 
  public HBackgroundConfigTemplate() {
    super();
  }

}
*/