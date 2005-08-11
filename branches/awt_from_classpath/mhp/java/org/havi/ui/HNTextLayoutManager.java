/*
 * NIST/DASE API Reference Implementation
 * $File: HNTextLayoutManager.java $
 * Last changed on $Date: 2001/02/16 20:14:47 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Graphics;
import java.awt.Dimension;


/**
 * See (C) official HaVi documentation for reference
 * <p>
 * NIST Reference implementation extension to HTextLayout interface.
 * <B>THIS IS NOT PART OF THE STANDARD API</B>.
 * Add a method to accurately evaluate the bounding box.
 * Note that it is perfectly legal to set the layout manager of a component
 * to a manager implementing the official interface; the reference TextLook
 * will try to guess anyway.
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 */

/* HN prefix for NIST implementation HAVi extensions */

public interface HNTextLayoutManager extends HTextLayoutManager {

  /** WARNING: THIS IS AN IMPLEMENTATION METHOD: NOT PART OF THE API
   *  Return the preferred size for a string or null if unknown
   *  @param markedUpString string to render
   *  @param v target HVisible
   *  @return bounding box */
  public Dimension getPreferredSize(String markedUpString,
                             HVisible v);

}
