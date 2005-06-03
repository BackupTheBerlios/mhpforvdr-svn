
/*
 * NIST/DASE API Reference Implementation
 * $File: api/javalib/src/org/davic/media/MediaTimeEventListener.java $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.davic.media;

/**
 *
 * <p> This interface should be implemented by the application in order to
 * receive the events associated to a media position by the application.
 * <p> See API (C) for details.
 * <p>Revision information:<br>
 * $Revision: 1.3 $
 * <br>
 * $Date: 2000/03/11 22:42:40 UTC $
 *
 */


import org.davic.media.MediaTimeEvent;

public interface MediaTimeEventListener {

  public void receiveMediaTimeEvent(MediaTimeEvent e);

}

