
/*
 * NIST/DASE API Reference Implementation
 * $File: api/javalib/src/org/davic/media/FreezeControl.java $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.davic.media;

/**
 *
 * <p>
 * This interface provides means to freeze and resume a media player.
 * For details, see the DAVIC API (C).
 * <p>Revision information:<br>
 * $Revision: 1.3 $
 * <br>
 * $Date: 2000/03/11 22:43:10 UTC $
 *
 */

import org.davic.media.MediaFreezeException;

public interface FreezeControl extends javax.media.Control {
  
  public void freeze() throws MediaFreezeException;
  public void resume() throws MediaFreezeException;

}

