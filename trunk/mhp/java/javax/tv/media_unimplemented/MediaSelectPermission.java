/*
 * NIST/DASE API Reference Implementation
 * $File: MediaSelectPermission.java $
 * Last changed on $Date: 2001/02/21 19:09:56 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package javax.tv.media;
import javax.tv.locator.Locator;
import java.security.Permission;
import java.io.Serializable;

/** See JavaTV API for details
 * Revision information:
 * <p> $Revision: 1.3 $ */

public final class MediaSelectPermission
  extends Permission implements Serializable {

  private String actions;

  /** Create a new permission for a locator
   *  @param locator locator, or null for all */
  public MediaSelectPermission(Locator locator) {

    super( locator==null ? "*" : locator.toExternalForm() );
    this.actions = null;

  }

  /** Create a permission object with the external representation of a locator
   *  @param locator target locator, toExternalForm(), or "*" for all
   *  @param actions unused, must be null for now */
  public MediaSelectPermission(String locatorString, String actions) {
    
    super(locatorString);
    this.actions = actions;
    
  }

  /** Check if p is implied by this object
   *  @param p permission to check
   *  @return true or false */
  public boolean implies(Permission p) {

    /* This decision tree follows strictly the API (T3-R0) */
    if( ! (p instanceof MediaSelectPermission) ) {
      return false;
    }

    if(this.getName().equals("*")) {
      return true;
    }

    /* equal() matches the locator external form only */
    return this.equals(p);
    
  }

  /** Compare this object to another MediaSelectPermission
   *  @param other the other object to compare to
   *  @return true if both objects' locator are equal */
  public boolean equals(Object other) {

    if ( ! (other instanceof MediaSelectPermission) ) {
      return false;
    } else {
      MediaSelectPermission p = (MediaSelectPermission)other;
      return this.getName().equals(p.getName());
    }
  }

  public int hashCode() {
    /* The general guideline is that if a.equals(b),
       then a.hashCode()==b.hashCode() */
    return this.getName().hashCode();
  }

  /** Return the canonical string representation of the actions.
      The API explicitely requests that none be returned for now (T3-R0)
   *  @return actions, empty string for now */
  public String getActions() {
    return "";
  }

}
