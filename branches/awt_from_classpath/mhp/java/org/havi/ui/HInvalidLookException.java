/*
 * NIST/DASE API Reference Implementation
 * $File: HInvalidLookException.java $
 * Last changed on $Date: 2000/12/14 17:08:25 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;

/**
 *
 * See the official (C) HaVi documentation for full specification details.
 * <br>Thrown when a change request to an HLook is not compatible with a
 * specific widget.
 * <p>Revision information:<br>
 * $Revision: 1.2 $
 */

public class HInvalidLookException extends HUIException {

  public  HInvalidLookException() {
     super();
  }

  // NOTE: this is not part of the API, but is pretty darn useful for
  // debugging
  public  HInvalidLookException(String s) {
     super(s);
  }

}
