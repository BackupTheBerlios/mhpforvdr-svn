/*
 * NIST/DASE API Reference Implementation
 * $File: api/javalib/src/org/davic/media/MediaTimeEvent.java $
 * Last changed on $Date: 2000/05/22 16:22:30 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.davic.media;

/**
 *
 * <p> See API (copyrighted material) for details.
 *
 * <p>Revision information:<br>
 * $Revision: 1.3 $
 */


public class MediaTimeEvent extends java.lang.Object {

  /* Source, time and ID of the event, as specified in constructor */
  private java.lang.Object source;
  private long eventTime;
  private int eventID;
  
  
  /**
   *
   * Constructor: Make an event
   *
   * @param source  the object generating the event
   * @param eventTime  the media time at which the event fired
   * @param ID  the event identification
   *
   */

  public MediaTimeEvent(java.lang.Object source,
                        long eventTime,
                        int ID) {
    super();
    this.source = source;
    this.eventTime = eventTime;
    this.eventID = ID;
  }

  public long getEventTime() {
    return this.eventTime;
  }

  public int getEventId() {
    return this.eventID;
  }

  public java.lang.Object getSource() {
    return this.source;
  }

  public java.lang.String toString() {
    return super.toString() + " EventTime:" + this.eventTime + " EventID:" + this.eventID;
  }
  
}
