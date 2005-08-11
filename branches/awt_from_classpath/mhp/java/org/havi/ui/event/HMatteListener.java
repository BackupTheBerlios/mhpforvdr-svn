/*
 * NIST/DASE API Reference Implementation
 * $File: HMatteListener.java $
 * Last changed on $Date: 2001/01/18 21:34:34 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui.event;

/**
 * Listener interface to receive event originating from an HMatte.
 * This implementation object is not part of the HAVi API and is for
 * internal use only.
 *
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 */

public interface HMatteListener extends java.util.EventListener {

  public void matteUpdate(HMatteEvent e);

}
