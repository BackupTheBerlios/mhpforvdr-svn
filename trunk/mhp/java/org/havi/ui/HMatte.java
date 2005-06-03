
package org.havi.ui;

import org.havi.ui.event.HMatteListener;

/*HMatte is the base interface for all matte classes. Where pixels in a component already have an alpha value (e.g.from an 
image),the alpha value from the component and the alpha value from the HMatte are multiplied together to obtain the 
actual alpha value to be used for that pixel. The  nal displayed value of the component and its HMatte is obviously 
subject to the capabilities of the underlying hardware platform. */

public interface HMatte {
  /** WARNING: the compose() method is not part of the standard HAVi interface.
      It is for implementation classes' use only for rendering purposes
      and should not be used by any other.
      <p>Compose an image with the matte
      @param target image to compose with the background
      @param clip area of the image to compose with
  */
  public void compose(org.dvb.ui.DVBGraphics graphics, java.awt.Rectangle rect);
  /** WARNING: HMatte Listener support is not part of the HAVi API.
      It is for implementation classes' use only for rendering purposes
      and should not be used by any other.
      <p>Register to receive change events from this matte
      @param listener target listener
  */
  public void addListener(HMatteListener listener);

  /** WARNING: HMatte Listener support is not part of the HAVi API.
      It is for implementation classes' use only for rendering purposes
      and should not be used by any other.
      <p>Unregister a listener.
      @param listener listener to remove
  */
  public void removeListener(HMatteListener listener);

  


}
