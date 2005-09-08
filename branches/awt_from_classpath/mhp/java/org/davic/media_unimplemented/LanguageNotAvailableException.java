/*
 * NIST/DASE API Reference Implementation
 * $File: api/javalib/src/org/davic/media/LanguageNotAvailableException.java $
 * Last changed on $Date: 2000/03/11 22:43:32 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.davic.media;

/**
 *
 * <p>
 * This exception indicates that a requested language was not available.
 * See API for details and method description.
 * <p>Revision information:<br>
 * $Revision: 1.2 $
 */
    
public class LanguageNotAvailableException
  extends javax.media.MediaException {

  public LanguageNotAvailableException() {
    super();
  }
  public LanguageNotAvailableException(java.lang.String reason) {
    super(reason);
  }
    
}
