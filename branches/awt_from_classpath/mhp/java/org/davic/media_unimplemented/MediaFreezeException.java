/*
 * NIST/DASE API Reference Implementation
 * $File: api/javalib/src/org/davic/media/MediaFreezeException.java $
 * Last changed on $Date: 2000/03/11 22:43:45 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.davic.media;

/**
 *
 * <p>
 * This exception indicates that either the freeze method or the resume
 * method was unsuccessful.
 *
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 */
    
public class MediaFreezeException extends javax.media.MediaException {

  /**
   *
   * Constructor without reason
   *
   */
  
  public MediaFreezeException() {
    super();
  }
    

  /**
   *
   * Constructor with reason
   *
   * @param reason  the reason why the exception was thrown
   *
   */
  
  public MediaFreezeException(java.lang.String reason) {
    super(reason);
  }
    
}
