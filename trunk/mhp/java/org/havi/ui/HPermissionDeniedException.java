/*
 * NIST/DASE API Reference Implementation
 * $File: HPermissionDeniedException.java $
 * Last changed on $Date: 2000/11/28 03:30:15 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;

/**
 *
 * See the official (C) HaVi documentation for full specification details.
 * <br>Permission expection.
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 */


public class HPermissionDeniedException extends Exception {

  public HPermissionDeniedException() {
    super();
  }

  public HPermissionDeniedException(java.lang.String reason) {
    super(reason);
  }

}
