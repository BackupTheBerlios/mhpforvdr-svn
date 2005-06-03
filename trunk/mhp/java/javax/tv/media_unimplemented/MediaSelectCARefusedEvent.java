/*
 * NIST/DASE API Reference Implementation
 * $File: MediaSelectCARefusedEvent.java $
 * Last changed on $Date: 2001/02/21 17:50:45 UTC $
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
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 */    

public class MediaSelectCARefusedEvent extends MediaSelectFailedEvent {

  public MediaSelectCARefusedEvent(Controller source,  Locator[] selection) {
    super(source, selection);
  }

}



