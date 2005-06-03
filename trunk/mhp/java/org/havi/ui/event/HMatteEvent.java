/*
 * NIST/DASE API Reference Implementation
 * $File: HMatteEvent.java $
 * Last changed on $Date: 2001/01/18 21:34:19 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui.event;

/**
 *
 * Fired when an HMatte has changed, which should probably trigger an
 * update of the HComponents using it.
 * This implementation object is not part of the HAVi API and is for
 * internal use only.
 *
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 */

public class HMatteEvent extends java.util.EventObject {

  public HMatteEvent(Object source) {
    super(source);
  }

  // Inherited from parent
  // public Object getSource()

}
