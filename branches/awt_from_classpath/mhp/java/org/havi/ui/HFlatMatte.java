/*
 * NIST/DASE API Reference Implementation
 * $File: HFlatMatte.java $
 * Last changed on $Date: 2001/01/18 21:40:08 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;


/**
 * This standard HAVi matte implements a constant matte over space and time.
 * 99% of the code comes from HAbstractFlatMatte. See this class for details.
 * <br>See (C) official HaVi documentation for reference.
 * <p>
 * <p>Revision information:<br>
 * $Revision: 1.3 $
 *
 */

public class HFlatMatte extends HAbstractFlatMatte {

  /** Constructor with no parameter. */
  public HFlatMatte() {
    super();
  }

  /** Constructor with opacity level
   *  @param data opacity level between 0.0 and 1.0 */
  public HFlatMatte(float data) {
    super(data);
  }

  /** Return the current opacity level for this matte
   *  @return current opacity ratio */
  public float getMatteData() {
    return this.data;
  }

}
