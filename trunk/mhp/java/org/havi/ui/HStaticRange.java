/*
 * NIST/DASE API Reference Implementation
 * $File: HStaticRange.java $
 * Last changed on $Date: 2001/05/01 13:38:07 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Color;
import java.awt.Font;

/**
 * See (C) official HaVi documentation for reference
 * <p>
 * HStaticRange is a widget that provides a basic graphical value display.
 * This HVisible has only one state.
 * <p><b>Important note</b>: although the specs do not specify this
 * explicitely, changing the value also set the text content of the HVisible
 * to a text representation of the current value and range "10 in [0:100]".
 * This choice was made throughout the implementation to make any HAVi UI
 * application fully accessible to the visually impaired with a simple
 * set of text-to-speech HLooks.
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 */


public class HStaticRange extends HVisible implements HNoInputPreferred, HOrientable {

  /* Inherited for Component: x, y, width, height, font
     Inherited from HVisible: tlm default to DefaultTextLayoutManager
     state, hlook */

  /* New information: orientation, minimum, maximum, value
     default look, thumb offsets and behaviors.
     Default values as required by specs. */


  /* Constants */
    
  /** Orientation (see constants) [OR_HORIZ] */
  private int orientation = ORIENT_LEFT_TO_RIGHT;

  /** Minimum value [0] */
  private int minValue = 0;

  /** Maximum value [100] */
  private int maxValue = 100;
  
  /** Current value [0] */
  private int value = 0;

  /** Behavior [SLIDER_BEHAVIOR] */
  private int behavior = SLIDER_BEHAVIOR;
  
  /** Minimum thumb offset (see behavior constants) [0] */
  private int thumbMinOffset = 0;

  /** Maximum thumb offset (see behavior constants) [0] */
  private int thumbMaxOffset = 0;

  /** Default look */
  private static HRangeLook defaultLook = new HRangeLook();
  

  /** This object should behave as a slider, ie it can be set to all
      possible values in [min:max] (ignore the thumb settings) */
  public static final int SLIDER_BEHAVIOR = 0;

  /** Scrollbar behavior: limited to [min+thumbMinOffset:max-maxThumbOffset]
      See specs for details. */
  public static final int SCROLLBAR_BEHAVIOR = 1;
  

  /** Constructor with no parameters */
  public HStaticRange() {
    super();
    doSetDefaults();
  }

  /** Constructor with a set of initial values and geometry*/
  public HStaticRange(int orientation,
                      int minimum, int maximum, int value,
                      int x, int y, int width, int height) {
    super(HStaticRange.getDefaultLook(), x, y, width, height);
    doSetDefaults();
    this.setValue(value);
    this.setOrientation(orientation);
    this.setRange(minimum, maximum);
  }
  
  /** Private utility method to set to defaults */
  private final void doSetDefaults() {
    setInteractionState(HState.NORMAL_STATE);
    doSetValue(0);
    try {
      setLook(HStaticRange.getDefaultLook());
    } catch(HInvalidLookException e) {
      // Just ignore, this cannot happen anyway
    }
  }

  /** Constructor with initial value configuraton */
  public HStaticRange(int orientation,
                      int minimum, int maximum, int value) {
    super();
    doSetDefaults();
    this.setRange(minimum, maximum);
    this.setOrientation(orientation);
  }
  

  /** Assign a new look. Overrides the HVisible method to check
      first that the new look is an HTextLook
      @param hlook new look to be applied
      @exception HInvalidLookException if the new look is not an HRangeLook
  */
  public void setLook(HLook hlook) throws HInvalidLookException {
    if ( hlook instanceof HRangeLook ) {
      super.setLook(hlook);
    } else {
      throw new HInvalidLookException(
"New look is not an HRangeLook and would not render this HStaticRange properly");
    }
  }


  /** Set new values for minimum and maximum
      @param minimum new minimum
      @param maximum new maximum
      @return false if invalid range, true otherwise */
  public synchronized boolean setRange(int minimum,
                                       int maximum) {
    if(maximum<=minimum) {
      return false;
    } else {
      this.minValue = minimum;
      this.maxValue = maximum;
      return true;
    }
  }

  /** Query the lower of the current range
      @return minimum value for this range object */
  public int getMinValue() {
    return this.minValue;
  }

  /** Query the upper of the current range
      @return maximum value for this range object */
  public int getMaxValue() {
    return this.maxValue;
  }

  /* Set the orientation of this HRange.
     @param orientation new orientation (OR_HORIZ, OR_VERT or OR_DIAL) */
  public void setOrientation(int orientation) {
    /* Sanity check */
    if( (orientation != ORIENT_LEFT_TO_RIGHT) &&
        (orientation != ORIENT_RIGHT_TO_LEFT) &&
        (orientation != ORIENT_TOP_TO_BOTTOM) &&
        (orientation != ORIENT_BOTTOM_TO_TOP) ) {
      return;
    }
    this.orientation = orientation;
  }

  
  /** Query the orientation of this HRange
      @return orientation (OR_HORIZ, OR_VERT or OR_DIAL) */
  public int getOrientation() {
    return this.orientation;
  }


  /** Set the value of this HRange object Ignored if out of bound
      (based on min, max and thumb offsets)
      @param value new value. */
  public void setValue(int value) {
    switch(this.behavior) {

    case SLIDER_BEHAVIOR:
      if( (value>=minValue) && (value<=maxValue) ) {
        doSetValue(value);
      }
      break;

    case SCROLLBAR_BEHAVIOR:
      if( (value>=(minValue+thumbMinOffset)) &&
          (value<=(maxValue-thumbMaxOffset)) ) {
        doSetValue(value);
      }
      break;
      // Ignore all others
    }
  }

  /** Private version to set the value. No range checking, but sets
      the text content property accordingly.
      @param value new value. */
  private void doSetValue(int value) {
    this.value=value;
    this.setTextContent(value +
                        " in [" + this.minValue + ":" + this.maxValue + "]",
                        HState.ALL_STATES);
  }
  
  /** Query current value. Note that this value may be different from
      what was used in a previous setValue() call.
      @return current value */
  public int getValue() {
    return this.value;
  }
  
  
  /** Set a new default look for the HStaticRange class.
      @param hlook new look
      @exception HInvalidLookException if the new look is not an HRangeLook
  */
  public static void setDefaultLook(HRangeLook hlook)
    throws HInvalidLookException {
    if ( hlook instanceof HRangeLook ) {
      defaultLook = hlook;
    } else {
      throw new HInvalidLookException(
"New look is not an HRangeLook and would not render HStaticRange properly");
    }
  }

  /** Return the current default look for this class
      @return default look */
  public static HRangeLook getDefaultLook() {
    return defaultLook;
  }
  


  /** Set the offsets for the thumb area in the scrollbar behavior.
      The HRangeLook should render the object with a "thumb extending
      from (value - minOffset), to (value + maxOffset). minOffset and
      maxOffset do not have to be the same. See specs for detail.
      The new values are ignored if out of bound (<0).
      @param minOffset new value for minOffset thumb info
      @param maxOffset new value for maxOffset thumb info */
  public synchronized void setThumbOffsets(int minOffset,  int maxOffset) {
    if(minOffset>=0) {
      this.thumbMinOffset = minOffset;
    }
    if(maxOffset>=0) {
      this.thumbMaxOffset = maxOffset;
    }
  }

  /** Query the current minOffset setting of the thumb area
      @return minOffset */
  public int getThumbMinOffset() {
    return this.thumbMinOffset;
  }
  
  /** Query the current maxOffset setting of the thumb area
      @return maxOffset */
  public int getThumbMaxOffset() {
    return this.thumbMaxOffset;
  }
  

  /** Set the behavior for this range object
      (SLIDER_BEHAVIOR or SCROLLBAR_BEHAVIOR)
      @param behavior new behavior; ignored if not a valid setting */
  public void setBehavior(int behavior) {
    if( (behavior==HStaticRange.SLIDER_BEHAVIOR) ||
        (behavior==HStaticRange.SLIDER_BEHAVIOR) ) {
      this.behavior = behavior;
    }
  }

  
  /** Query the current behavior for this range object
      (SLIDER_BEHAVIOR or SCROLLBAR_BEHAVIOR)
      @return current behavior */
  public int getBehavior() {
    return this.behavior;
  }
}
