/*
 * NIST/DASE API Reference Implementation
 * $File: MediaSelectSuccededEvent.java $
 * Last changed on $Date: 2001/02/21 19:08:08 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package javax.tv.media;
import javax.tv.locator.Locator;
import javax.media.Controller;

/**
 *
 * <p>
 * This class represents a notification event for indicating a success
 * in selecting a particular media.
 * <p> See API for details (C).
 *
 * <p>Revision information:<br>
 * $Revision: 1.5 $
 *
 */    


public class MediaSelectSuccededEvent extends MediaSelectEvent {

  public MediaSelectSuccededEvent(Controller source,
                                  Locator[] currentSelection) {
    super(source, currentSelection);
  }

}

