/*
 * NIST/DASE API Reference Implementation
 * $File: AWTVideoSizeControl.java $
 * Last changed on $Date: 2001/02/21 19:06:21 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package javax.tv.media;
import java.awt.Dimension;
import javax.media.Control;

/**
 *
 * <p>
 * This interface defines ways to change the clipping/scaling/translation
 * settings for a player.  See the official JavaTV API for information.
 *
 * <p>Revision information:<br>
 * $Revision: 1.4 $
 *
 */    

public interface AWTVideoSizeControl extends Control {

  public AWTVideoSize checkSize(AWTVideoSize sz);
  public AWTVideoSize getDefaultSize();
  public AWTVideoSize getSize();
  public Dimension getSourceVideoSize();
  public boolean setSize(AWTVideoSize size);
  
}
