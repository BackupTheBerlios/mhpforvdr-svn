/*
 * NIST/DASE API Reference Implementation
 * $File: MediaSelectFailedEvent.java $
 * Last changed on $Date: 2001/02/21 19:09:20 UTC $
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
 * This class represents an exception event for indicating a failure
 * in selecting a particular media
 *
 * <p>Revision information:<br>
 * $Revision: 1.6 $
 *
 */    

public class MediaSelectFailedEvent extends MediaSelectEvent {

  public MediaSelectFailedEvent(Controller source,  Locator[] selection) {
    super(source, selection);
  }

}



