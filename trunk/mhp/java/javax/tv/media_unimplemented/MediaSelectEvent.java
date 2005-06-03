/*
 * NIST/DASE API Reference Implementation
 * $File: MediaSelectEvent.java $
 * Last changed on $Date: 2001/02/21 19:05:44 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package javax.tv.media;
import javax.media.Controller;
import java.util.EventObject;
import javax.tv.locator.Locator;

/**
 *
 * <p>
 * This class is the parent class for media selection exception events
 * <p> See the official JavaTV API for details.
 *
 * <p>Revision information:<br>
 * $Revision: 1.5 $
 *
 */    


public class MediaSelectEvent extends EventObject {

  /**
   * The currently selected service.
   */
  protected  Locator[] selection;

  /**
   * Controller that fired this event.
   */
  protected Controller controller;

  
  public MediaSelectEvent(Controller source,  Locator[] newSelection) {
    super(source);
    this.controller = source;
    this.selection  = newSelection;
  }

  public Controller getController() {
    return controller;
  }

  public Locator[] getSelection() {
    return selection;
  }

}
