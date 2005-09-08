/*
 * NIST/DASE API Reference Implementation
 * $File: api/javalib/src/org/davic/media/NotAuthorizedException.java $
 * Last changed on $Date: 2000/03/11 22:42:54 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.davic.media;

/**
 *
 * <p>
 * This exception indicates that the source can not be accessed in order to
 * reference the new content, the source has not been accepted.
 *
 * <p>Revision information:<br>
 * $Revision: 1.2 $
 */
 
public class NotAuthorizedException extends  java.io.IOException {

  public NotAuthorizedException() {
    super();
  }
    
  public NotAuthorizedException(java.lang.String reason) {
    super(reason);
  }
    
}
