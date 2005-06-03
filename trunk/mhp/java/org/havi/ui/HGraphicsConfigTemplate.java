package org.havi.ui;

//Taken and adapted from OpenMHP, license is LGPL
/**
* @author tejopa
* @date 6.3.2004
* @status partially implemented
* @module internal, graphics
* HOME
*/
public class HGraphicsConfigTemplate extends HScreenConfigTemplate{


   public static final int VIDEO_MIXING          = 0x0C;
   public static final int MATTE_SUPPORT         = 0x0D;
   public static final int IMAGE_SCALING_SUPPORT = 0x0E;

   public HGraphicsConfigTemplate() {
   }

   public boolean isConfigSupported(HGraphicsConfiguration hgc){
      //Out.printMe(Out.FIXME);
      System.err.println("HGraphicsConfigTemplate.getBestConfiguration: Only returning null, TODO!!");
      return true;
   }
   
   void checkPriority(int preference){
      switch (preference) {
      case ZERO_BACKGROUND_IMPACT:
      case ZERO_GRAPHICS_IMPACT:
      case ZERO_VIDEO_IMPACT:
      case INTERLACED_DISPLAY:
      case FLICKER_FILTERING:
      case MATTE_SUPPORT:
      case IMAGE_SCALING_SUPPORT:
         break;
      default:
         throw new IllegalArgumentException();
      }
   }


}


/*
 * NIST/DASE API Reference Implementation
 * $File: HGraphicsConfigTemplate.java $
 * Last changed on $Date: 2001/02/16 20:16:14 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 *

package org.havi.ui;


/*
 *
 * See the official (C) HaVi documentation for full specification details.
 * <br>Implementation of the HGraphicsConfigTemplate class.
 *
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *



public class HGraphicsConfigTemplate extends HScreenConfigTemplate {

  
  public static final int VIDEO_MIXING = 0x0C;
  public static final int MATTE_SUPPORT ? 0x0D;
  public static final int IMAGE_SCALING_SUPPORT =0x0E;

  // Not from API. Use this to chain preferences from parent to child 
  public static final int LAST_PREFERENCE = IMAGE_SCALING_SUPPORT;
  
  // Create a new template with all default values 
  public HGraphicsConfigTemplate() {
    super();
  }

  public HGraphicsConfiguration getBestConfiguration(HGraphicsConfigTemplate[] hgcta) {
    System.err.println("HGraphicsConfiguration.getBestConfiguration: Only returning null, TODO!!");
    // TODO !!!
    return null;
  }

/*   The getBestConfiguration method attempts to return an HGraphicsConfiguration that matches the
      specified HGraphicsConfigTemplate. If this is not possible it will attempt to construct an
      HEmulatedGraphicsConfiguration where the emulated configuration best matches this
      HGraphicsConfigTemplate. 

      Best in this sense means satisfying the preferences in the config template as follows based on the
      priority (as supplied to HScreenConfigTemplate.setPreference() ) 
         1.satisfying all the preferences in that config template whose was REQUIRED 
         2.excluding configurations with priorites which were REQUIRED_NOT 
         3.satisfying as many as possible of the preferences whose priority was PREFERRED. 
         4.Satisfying as few as possible of the preferences whose priority was PREFERRED_NOT. 
      Parameters:
            hgcta - - the array of HGraphicsConfigTemplate objects to choose from.
      Returns:
            an HGraphicsConfiguration object that is the best configuration possible.

*/
//}
