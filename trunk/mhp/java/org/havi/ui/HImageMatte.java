/*
 * NIST/DASE API Reference Implementation
 * $File: HImageMatte.java $
 * Last changed on $Date: 2001/01/31 17:43:57 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Image;


/**
 * This standard HAVi matte implements a variable matte over space as
 * described by a single channel image.
 * 99% of the code comes from HAbstractImageMatte. See this class for details.
 * <br>See (C) official HaVi documentation for reference.
 * <p>
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 */

public class HImageMatte extends HAbstractImageMatte {

  /** Constructor with no parameter. */
  public HImageMatte() {
    super();
  }

  /** Constructor with opacity level
   *  @param data opacity mask */
  public HImageMatte(Image data) {
    super(data);
  }

  /** Return the current opacity mask.
   *  @return current opacity mask */
  public Image getMatteData() {
    return this.data;
  }

}
