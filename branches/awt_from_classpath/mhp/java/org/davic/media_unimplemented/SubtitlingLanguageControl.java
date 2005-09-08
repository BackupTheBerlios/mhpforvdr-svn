
/*
 * NIST/DASE API Reference Implementation
 * $File: api/javalib/src/org/davic/media/SubtitlingLanguageControl.java $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.davic.media;

/**
 *
 * <p>
 * Subtitling language control.
 *
 * <p>Revision information:<br>
 * $Revision: 1.4 $
 * <br>
 * $Date: 2000/03/11 22:43:38 UTC $
 *
 */

public interface SubtitlingLanguageControl
        extends org.davic.media.LanguageControl {

  public boolean isSubtitlingOn();
  public boolean setSubtitling(boolean new_value);
  
}

