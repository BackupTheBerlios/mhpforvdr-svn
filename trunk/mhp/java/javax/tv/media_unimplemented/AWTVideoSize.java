/*
 * NIST/DASE API Reference Implementation
 * $File: AWTVideoSize.java $
 * Last changed on $Date: 2001/02/21 19:09:01 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package javax.tv.media;
import java.awt.Rectangle;

/**
 *
 * <p> This class represents the "position, scaling and clipping" of a JMF
 * player as set by the corrresponding AWTVideoSizeControl.
 * // TODO: Make sure this works...
 * <p>Revision information:<br>
 * $Revision: 1.3 $
 * */

public class AWTVideoSize {

  /* (C) See javaTV API for information */
  private Rectangle source;
  private Rectangle destination;

  public AWTVideoSize(Rectangle source, Rectangle dest) {
    this.source = new Rectangle(source);
    this.destination = new Rectangle(destination);
  }


  /** Return a copy of the source rectangle in the screen coord. system.
   *  @return source rectangle
   */
  public Rectangle getSource() {
    return (Rectangle)(source.clone());
  }
  
  /** Return a copy of the destination rectangle in the screen coord. system.
   *  @return destination rectangle
   */
  public Rectangle getDestination() {
    return (Rectangle)(destination.clone());
  }


  /** Scaling factor applied to the video on the X axis
   *  @return X scaling */
  public float getXScale() {
    return (float)destination.width / (float)source.width;
  }

  /** Scaling factor applied to the video on the Y axis
   *  @return Y scaling */
  public float getYScale() {
    return (float)destination.height / (float)source.height;
  }

  
  /** Test if <code>other</other> is equal to this object, ie if all members
   *  are equal.
   *  @param other object to test
   *  @return true if they are
   */
  public boolean equals(java.lang.Object other) {

    AWTVideoSize o;
    
    if( !(other instanceof AWTVideoSize) ) {
      return false;
    }
    o = (AWTVideoSize)other;

    return ( o.source.equals(this.source)
             && o.destination.equals(this.destination) );
  }


  public int hashCode() {
    return source.hashCode() + destination.hashCode();
  }

  /** Return a textual representation.
   *  @return string describing this object.
   */
  public String toString() {
    return "Source: " + source.toString() +
      " ;  Destination: " + destination.toString() +
      " ;  Scaling x=" + getXScale() + " y=" + getYScale();
  }
  
}
