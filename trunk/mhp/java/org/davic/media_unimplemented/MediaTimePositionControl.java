
/*
 * NIST/DASE API Reference Implementation
 * $File: api/javalib/src/org/davic/media/MediaTimePositionControl.java $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.davic.media;

/**
 *
 * <p> Control the timeline of the media player.
 * See API for details. (Copyrighted material).
 *
 * <p>Revision information:<br>
 * $Revision: 1.3 $
 * <br>
 * $Date: 2000/03/11 22:43:05 UTC $
 *
 */


public interface MediaTimePositionControl extends javax.media.Control {
  
  public javax.media.Time getMediaTimePosition();
  public javax.media.Time setMediaTimePosition(javax.media.Time mediaTime);

}

