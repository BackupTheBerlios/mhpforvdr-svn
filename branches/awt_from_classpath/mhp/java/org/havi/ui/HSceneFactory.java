/*
 * NIST/DASE API Reference Implementation
 * $File: HSceneFactory.java $
 * Last changed on $Date: 2001/03/26 20:50:46 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.MHPScreen;

/**
 * Implementation of HSceneFactory, the main facility to create HScenes.
 * This interfaces with the window manager to request screen real estate.
 * Note: the specs require that an application cannot acquire more than
 * one HScene. <B>NOTE<B>: this requirement is not enforced at this point.
 * <br>See (C) official HAVi documentation for reference.
 * <p>
 * <p>Revision information:<br>
 * $Revision: 1.5 $
 *
 */

// TODO : implement singe-HScene policy

public class HSceneFactory {

  /** Single instance of HSceneFactory returned by getInstance */
  private static HSceneFactory instance = new HSceneFactory();
  
  /** Constructor. Note: the Factory should not be instantiated.
      Use the static methods getInstance instead */
  protected HSceneFactory() {
  }
  
  /** Return a reference to an instance of HSceneFactory.
      Multiple calls return a reference to the same object. */
  public static HSceneFactory getInstance() {
    return HSceneFactory.instance;
  }

  /** Return the closest valid match to a template
      @param hst requested template
      @return the best match or null if none */
  public HSceneTemplate getBestSceneTemplate(HSceneTemplate hst) {
    // TODO : implement this
    return hst;
  }

  private int sceneIndex = 0;

  /** Return an HScene that best matches the provided template
      @param hst HScene template describing the requested scene
      @return the best matching scene, or null if none matches */
  public HScene getBestScene(HSceneTemplate hst) {

    
    //TODO: implement that: for now, simply return an HScene
    // within the default graphics device
    // with the requested geometry in SCENE_PIXEL_RECTANGLE.
    // If none (or other), give a full-screen.

    Rectangle geom;
    
    Object p = hst.getPreferenceObject(HSceneTemplate.SCENE_PIXEL_DIMENSION);
    if(p==null || !( p instanceof Rectangle) ) {
      geom = new Rectangle(0,0, MHPScreen.getResolution().width, MHPScreen.getResolution().height);
    } else {
      geom = (Rectangle)p;
    }
    System.out.println("HSceneFactory.getBestScene: creating scene with geometry "+geom+" for application "+vdr.mhp.ApplicationManager.getManager().getApplicationFromStack().getName());

    HScene newScene = new HScene(geom.x, geom.y, geom.width, geom.height, vdr.mhp.ApplicationManager.getManager().getApplicationFromStack());
    HScreen.getDefaultHScreen().getDefaultHGraphicsDevice().addHScene(newScene, "HScene " + sceneIndex++);
    return newScene;

  }

  /** Request that the specified HScene be resized to match the new template
      Only the geometry (size+location) are taken into consideration.
      If the request cannot be satisfied, the HScene is left unchanged
      @param hs target HScene
      @param hst new template to apply
      @exception IllegalStateException if the HScene has already been disposed
  */
  public HSceneTemplate resizeScene(HScene hs, HSceneTemplate hst)
    throws IllegalStateException {

    //TODO: implement this
    
    Rectangle geom;

    Object p = hst.getPreferenceObject(HSceneTemplate.SCENE_PIXEL_DIMENSION);
    if(p==null || !( p instanceof Rectangle) ) {
      return hs.getSceneTemplate();
    } else {
      geom = (Rectangle)p;
    }
    
    hs.setSize(geom.width, geom.height);
    hs.setLocation(geom.x, geom.y);
    return hs.getSceneTemplate();

  }


  /*
    
    public HScene getSelectedScene(HGraphicsConfiguration[] selection,
                               HScreenRectangle screenRectangle,
                               Dimension resolution) {
  //TODO Not implemented
  return null

      Create a HScene that is aligned exactly to the area on-screen, from a limited set of
      HGraphicsConfiguration's, for example, those compatible with video presentation.
      Parameters:
            selection - an array of HGraphicsConfiguration objects amongst which the selection
            should be made.
            screenRectangle - an HScreenRectangle denoting an on-screen location.
            resolution - the pixel resolution represented as a Dimension object.
      Returns:
            a created HScene, derived from a set of HGraphicsConfiguration objects, an on-screen
            location and a pixel resolution if possible, or null otherwise.


  */

  /** Return a full screen scene at the requested pixel resolution.
      In this implementation, if the requested resolution does not match the
      current resolution, the call fails.
      @param device target device
      @return a newly created scene or null. */
  public HScene getFullScreenScene(HGraphicsDevice device) {
    HScene newScene = new HScene(0, 0, MHPScreen.getResolution().width, MHPScreen.getResolution().height, vdr.mhp.ApplicationManager.getManager().getApplicationFromStack());
    HScreen.getDefaultHScreen().getDefaultHGraphicsDevice().addHScene(newScene, "HScene " + sceneIndex++);
    return newScene;
  }
  
  
/* Create the default HScene for the default HScreen for this application.
This shall be identical to calling 
org.havi.ui.HScene.getDefaultHscene(org.havi.ui.HScreen.getDefaultHScreen()) 
Returns: the default HScene for the default HScreen .If the application has 
already obtained an HScene for the default HScreen ,then that HScene is returned. */
public HScene getDefaultHScene() {
   return getDefaultHScene(HScreen.getDefaultHScreen());
}
 
/*
Create the default HScene for this HScreen .
This shall use the HGraphicsConfiguration returned 
by calling screen.getDefaultHGraphicsDevice().getDefaultConfiguration() 
Parameters: screen -the screen for which the HScene should be returned. 
Returns: the default HScene for this HScreen .If the application has 
already obtained an HScene for this HScreen ,then that HScene is returned.
*/
 
public HScene getDefaultHScene(HScreen screen) {
   //TODO: rethink, really implement
   return getFullScreenScene(screen.getDefaultHGraphicsDevice());
}  
  
  /** Dispose of an HScene.
      The specs require that an java.lang.IllegalStateException be thrown
      on further references to the HScene. // TODO: not enforced.
      @param scene HScene to remove */
  public void dispose(HScene scene) {
    HScreen.getDefaultHScreen().getDefaultHGraphicsDevice().removeHScene(scene);
  }

}
