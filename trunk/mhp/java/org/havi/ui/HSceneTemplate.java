package org.havi.ui;

import java.awt.Dimension;


//Taken from OpenMHP, license is LGPL
/**
* @author tejopa
* @date 6.3.2004
* @status fully implemented
* @module internal
* TODO Check LARGEST_PIXEL_DIMENSION
*/
public class HSceneTemplate{

	public static final int REQUIRED                = 0x01;
    public static final int PREFERRED               = 0x02;
    public static final int UNNECESSARY             = 0x03;

    public static final int GRAPHICS_CONFIGURATION  = 0x00;
    public static final int SCENE_PIXEL_DIMENSION   = 0x01;
    public static final int SCENE_PIXEL_LOCATION    = 0x02;
    public static final int SCENE_SCREEN_DIMENSION  = 0x04;
    public static final int SCENE_SCREEN_LOCATION   = 0x08;

    public static final Dimension LARGEST_PIXEL_DIMENSION = java.awt.MHPScreen.getResolution();

    private Object[] objects;
    private int[] priorities;

    public HSceneTemplate(){
		objects 	= new Object[9];
		priorities 	= new int[9];
		for (int i=0;i<priorities.length;i++) {
			priorities[i] = UNNECESSARY;
		}
	}

    public void setPreference(int preference, Object object, int priority){
		objects[preference] = object;
		priorities[preference] = priority;
	}

    public Object getPreferenceObject(int preference){
        return objects[preference];
    }

    public int getPreferencePriority(int preference){
        return priorities[preference];
    }
}
















/*
 * NIST/DASE API Reference Implementation
 * $File: HSceneTemplate.java $
 * Last changed on $Date: 2000/12/14 16:59:27 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

/*package org.havi.ui;
import java.awt.Dimension;

*/
/**
 * Template to request and identifiy HScene configuration.
 * <br>See (C) official HaVi documentation for reference.
 * <p>
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 */

/*public class HSceneTemplate implements Cloneable {

  public static final int PRIORITY_LOWEST = 1;

  public static final int UNNECESSARY = 1; // API
  public static final int PREFERRED = 2; // API
  public static final int REQUIRED = 3; // API
  public static final int PRIORITY_HIGHEST = 3;
  

  public static final Dimension LARGEST_DIMENSION = new Dimension(); // API


  public static final int FIRST_PREFERENCE = 4;
  
  public static final int GRAPHICS_CONFIGURATION =FIRST_PREFERENCE+0; // API
  public static final int SCENE_PIXEL_LOCATION =FIRST_PREFERENCE+1; // API
  public static final int SCENE_PIXEL_DIMENSION =FIRST_PREFERENCE+2; // API
  public static final int SCENE_SCREEN_LOCATION =FIRST_PREFERENCE+3; // API
  public static final int SCENE_SCREEN_DIMENSION =FIRST_PREFERENCE+3; // API

  public static final int UNKNOWN_PREFERENCE =FIRST_PREFERENCE+4; // API

  public static final int LAST_PREFERENCE = UNKNOWN_PREFERENCE;
  public static final int PREFERENCE_COUNT =
                                LAST_PREFERENCE - FIRST_PREFERENCE +1;


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

  // array of preferences:
    //  prefs[0] => FIRST_PREFERENCE, etc. 
  private Preference[] prefs = new Preference[PREFERENCE_COUNT];

  
  // Constructor 
  public HSceneTemplate() {
    for(int i=0; i<PREFERENCE_COUNT; i++) {
      prefs[i] = new Preference(UNNECESSARY, null);
    }
  }
*/

  /** Set a preference
      @param preference one preference types defined in this class
                        (see constants)
      @param object preference value
      @param priority priority setting in UNNECESSARY, REQUIRED, PREFERRED */
 /* public void setPreference(int preference, Object object, int priority) {
    
    // Sanity check 
    if( (preference<FIRST_PREFERENCE) || (preference>LAST_PREFERENCE) ) {
      return;
    }

    prefs[preference - FIRST_PREFERENCE] = new Preference(priority, object);

  }
*/

  /* Query the current value of a preference setting
     @param preference preference to query
     @return Opaque object containing the value for this preference */
 /* public Object getPreferenceObject(int preference) {

    // Sanity check
    if( (preference<FIRST_PREFERENCE) || (preference>LAST_PREFERENCE) ) {
      return null;
    }

    return prefs[preference - FIRST_PREFERENCE].value;

  }*/

  /* Query the current priority of a preference setting
     @param preference preference to query
     @return priority in UNNECESSARY, REQUIRED, PREFERRED */
  /*public int getPreferencePriority(int preference) {

    // Sanity check
    if( (preference<FIRST_PREFERENCE) || (preference>LAST_PREFERENCE) ) {
      return UNNECESSARY; // the default
    }

    return prefs[preference - FIRST_PREFERENCE].priority;

  }

}
*/