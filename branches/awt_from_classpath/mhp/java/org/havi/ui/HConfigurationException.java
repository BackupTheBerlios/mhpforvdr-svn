/*
 * NIST/DASE API Reference Implementation
 * $File: HConfigurationException.java $
 * Last changed on $Date: 2000/11/28 03:29:11 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;

/**
 *
 * See the official (C) HaVi documentation for full specification details.
 * <br>Thrown when a change request to an HScreenConfiguration cannot be
 * completed.
 *
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 */


public class HConfigurationException extends Exception {

  public HConfigurationException() {
    super();
  }

  public HConfigurationException(String reason) {
    super(reason);
  }

}
