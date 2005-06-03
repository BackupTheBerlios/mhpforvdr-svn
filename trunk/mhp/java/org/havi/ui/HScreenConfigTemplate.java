package org.havi.ui;

//Taken and adapted from OpenMHP, license is LGPL
/**
* @author tejopa
* @date 6.3.2004
* @statusfully implemented
* @module internal, graphics
* TODO attach to real HScreens
* HOME
*/
public abstract class HScreenConfigTemplate{

   public static final int REQUIRED                     = 1;
   public static final int PREFERRED                    = 2;
   public static final int DONT_CARE                    = 3;
   public static final int PREFERRED_NOT                = 4;
   public static final int REQUIRED_NOT                 = 5;

   public static final int ZERO_BACKGROUND_IMPACT       = 1;
   public static final int ZERO_GRAPHICS_IMPACT         = 2;
   public static final int ZERO_VIDEO_IMPACT            = 3;
   public static final int INTERLACED_DISPLAY           = 4;
   public static final int FLICKER_FILTERING            = 5;
   public static final int VIDEO_GRAPHICS_PIXEL_ALIGNED = 6;
   public static final int PIXEL_ASPECT_RATIO           = 7;
   public static final int PIXEL_RESOLUTION             = 8;
   public static final int SCREEN_RECTANGLE             = 9;

//For information 
   //from HBackgroundConfigTemplate
   //public static final int CHANGEABLE_SINGLE_COLOR = 0x0A;
   //public static final int STILL_IMAGE             = 0x0B;
   
   //from HGraphicsConfigTemplate
   //public static final int VIDEO_MIXING          = 0x0C;
   //public static final int MATTE_SUPPORT         = 0x0D;
   //public static final int IMAGE_SCALING_SUPPORT = 0x0E;
   
   //from HVideoConfigTemplate
   //public static final int GRAPHICS_MIXING = 0x0F;
   
   //internal
   public static final int LAST_PREFERENCE = 0x0F;
   
   
   //The arrays are large enough to be used in all inherited classes, see above
   private int[] priorities = new int[LAST_PREFERENCE+1];
   private Object[] objects = new Object[LAST_PREFERENCE+1];

   public HScreenConfigTemplate(){
      /* set DONT_CARE to each preference */
      for(int i = 0; i < priorities.length; i++){
         priorities[i] = HScreenConfigTemplate.DONT_CARE;
         objects[i] = null;
      }
   }

   public void setPreference(int preference, int priority){
      checkPriority(preference);
      priorities[preference] = priority;
   }

   public void setPreference(int preference, Object object, int priority){
      checkPriority(preference);
      priorities[preference] = priority;
      objects[preference] = object;
   }

   public int getPreferencePriority(int preference){
      if (preference <= LAST_PREFERENCE)
         return priorities[preference];
      else
         return DONT_CARE;
   }

   public Object getPreferenceObject(int preference){
      if (preference <= LAST_PREFERENCE)
         return objects[preference];
      else
         return null;
   }
   
   //internal helper, overridden in subclasses
   void checkPriority(int preference){
      switch (preference) {
         case ZERO_GRAPHICS_IMPACT:
         case ZERO_BACKGROUND_IMPACT:
         case ZERO_VIDEO_IMPACT:
         case INTERLACED_DISPLAY:
         case FLICKER_FILTERING:
            break;
         default:
            throw new IllegalArgumentException();
      }
   }
}


/*
 * NIST/DASE API Reference Implementation
 * $File: HScreenConfigTemplate.java $
 * Last changed on $Date: 2001/03/13 15:16:04 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

/*package org.havi.ui;
import java.awt.Dimension;
import java.util.Hashtable;*/


/*
 * Template to request and identifiy HScreen configurations.
 * <br>See (C) official HaVi documentation for reference.
 * <p>
 * <p>Revision information:<br>
 * $Revision: 1.3 $
 *
 *


public abstract class HScreenConfigTemplate {


  public static final int PRIORITY_LOWEST = 1;

  public static final int DONT_CARE = 3; // API
  public static final int PREFERRED = 2; // API 
  public static final int REQUIRED = 1; // API 
  public static final int PREFERRED_NOT = 4; // API 
  public static final int REQUIRED_NOT = 5; // API 

  public static final int PRIORITY_HIGHEST = 5;
  

  // static final int FIRST_PREFERENCE = 100;

  public static final int ZERO_BACKGROUND_IMPACT = 0x01;
  public static final int ZERO_GRAPHICS_IMPACT = 0x02;
  public static final int ZERO_VIDEO_IMPACT = 0x03;
  public static final int INTERLACED_DISPLAY = 0x04;
  public static final int FLICKER_FILTERING = 0x05;
  public static final int VIDEO_GRAPHICS_PIXEL_ALIGNED = 0x06;
  public static final int PIXEL_ASPECT_RATIO = 0x07;
  public static final int PIXEL_RESOLUTION = 0x08;
  public static final int SCREEN_RECTANGLE = 0x09;

  // Not from API 
  public static final int UNKNOWN_PREFERENCE =  FIRST_PREFERENCE + 7;

  //public static final int LAST_PREFERENCE = UNKNOWN_PREFERENCE;


  // Utility class to store preferences
  private class Preference {
    int priority;
    Object value;

    public Preference(int priority, Object object) {

      if ( priority < PRIORITY_LOWEST ) {
        this.priority = PRIORITY_LOWEST;
      } else if ( priority > PRIORITY_HIGHEST ) {
        this.priority = PRIORITY_HIGHEST;
      } else {
        this.priority = priority;
      }

      this.value = object;
      
    }
    
  }

  // Hashtable of preferences. No record in the hastable equates to
  //    an (UNNECESSARY, null) preference 
  private Hashtable prefs = new Hashtable();

  
  
  // Constructor: initialize the template with default values (see specs) 
  public HScreenConfigTemplate() {

    // Enforce individual defaults.
    // TODO : consolidate all platform-specific defaults in one place
    // Default aspect ratio: this is platform specific 
    this.setPreference(PIXEL_ASPECT_RATIO, new Dimension(1,1), PREFERRED);
    
    // Default pixel resolution: this is platform specific 
    this.setPreference(PIXEL_RESOLUTION, new Dimension(640, 480), PREFERRED);
    
    // Default screen location: specs require full screen 
    this.setPreference(PIXEL_RESOLUTION,
                       new HScreenRectangle(0.0F, 0.0F, 1.0F, 1.0F),
                       PREFERRED);

  }


  // Indicates whether this specific configuration is compatible with
  //    this template
  //    @param hsc candidate HScreenConfiguration to match against this template
  //    @return true if the configuration supports the required features 
  public boolean isDisplayConfigSupported(HScreenConfiguration hsc) {
    // Relies entirely on the matching 
    // TODO WARNING : This method has disappeared in 1.01.
    return hsc.isCompatibleConfiguration(this);
  }


  // Set a preference's priority.  Existing values are lost.
  //    @param preference one preference types defined in this class
  //                      (see constants)
  //    @param priority priority setting 
    public void setPreference(int preference,
                              int priority) {

      setPreference(preference, null, priority);
      
    }

  
  // Set a preference priority and value. Existing values are lost.
  //    @param preference one preference types defined in this class
  //                      (see constants)
  //    @param object preference value
  //    @param priority priority setting  
  public void setPreference(int preference, Object object, int priority) {
    
    Integer prefInt = new Integer(preference);

    // No Sanity check because it would interfere with
    //   child-added preferences 
    prefs.put(prefInt, new Preference(priority, object));

  }


  // Query the current value of a preference setting
  //   @param preference preference to query
  //   @return generic Object containing the value for this preference 
  public Object getPreferenceObject(int preference) {

    Integer prefInt = new Integer(preference);

    if( prefs.get(prefInt) == null ) {
      return null;
    } else {
      return ((Preference)prefs.get(prefInt)).value;
    }
  }

  // Query the current priority of a preference setting
  //   @param preference preference to query
  //   @return priority  
  public int getPreferencePriority(int preference) {

    Integer prefInt = new Integer(preference);

    if( prefs.get(prefInt) == null ) {
      return UNNECESSARY;
    } else {
      return ((Preference)prefs.get(prefInt)).priority;
    }

  }

}
*/
