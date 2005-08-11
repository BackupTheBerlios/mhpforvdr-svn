/*
 * NIST/DASE API Reference Implementation
 * $File: api/javalib/src/javax/tv/media/MediaSelectListener.java $
 * Last changed on $Date: 2000/06/20 20:41:11 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package javax.tv.media;

/**
 *
 * <p>
 * This interface is the listener interface to media selection events ????
 *
 * <p>Revision information:<br>
 * $Revision: 1.5 $
 *
 */    


public interface MediaSelectListener
     extends java.util.EventListener {

  /**
   *
   * This method is called when a selection is complete
   *
   * @param event   describing what happened
   *
   */

  public void selectionComplete(MediaSelectEvent event);

}
