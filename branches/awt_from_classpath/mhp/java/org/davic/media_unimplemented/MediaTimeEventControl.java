
/*
 * NIST/DASE API Reference Implementation
 * $File: api/javalib/src/org/davic/media/MediaTimeEventControl.java $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.davic.media;

/**
 *
 * <p> This interface describes methods for the application to associate events
 * with the media time of the current stream.
 * <p> See the official API (C) for details.
 * <p>Revision information:<br>
 * $Revision: 1.3 $
 * <br>
 * $Date: 2000/03/11 22:43:43 UTC $
 *
 */

import org.davic.media.MediaTimeEventListener;

public interface MediaTimeEventControl extends javax.media.Control {

  public void notifyWhen(MediaTimeEventListener i, long mediaTime);
  public void notifyWhen(MediaTimeEventListener i, long mediaTime, int id);

}

