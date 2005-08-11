/*
 * NIST/DASE API Reference Implementation
 * $File: api/javalib/src/org/davic/media/MediaTimePositionChangedEvent.java $
 * Last changed on $Date: 2000/03/11 22:42:59 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.davic.media;

/**
 *
 * <p> Fired as a result of setMediaPosition if the media position actually
 * changed. <p> See API for details (C).
 *
 * <p>Revision information:<br>
 * $Revision: 1.3 $
 */
    
public class MediaTimePositionChangedEvent
  extends javax.media.RestartingEvent {

  public MediaTimePositionChangedEvent(javax.media.Controller source) {
    // TODO Clarify this with API people -MIC
    /* Fudge: use source to fill in the state/mediaTime information
       required for a RestartingEvent.  Bug in the API ? -MIC 2000-03-11 */
    super(source, source.Started, source.getState(),
          source.getTargetState(), source.getMediaTime());
  }
    
}
