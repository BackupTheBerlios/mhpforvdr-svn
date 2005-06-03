/*
 * NIST/DASE API Reference Implementation
 * $File: HAbstractFlatMatte.java $
 * Last changed on $Date: 2001/06/26 17:45:22 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.util.ArrayList;
import org.havi.ui.event.HMatteEvent;
import org.havi.ui.event.HMatteListener;
import java.awt.Color;
import java.awt.Graphics;
//import java.awt.Graphics2D;
import org.dvb.ui.DVBGraphics;
import java.awt.Image;
import java.awt.Component;
import java.awt.Rectangle;


/**
 * This abstract class is the parents of both FlatMatte and EffectFlatMatte.
 * FlatEffectMatte should naturally subclass FlatMatte but this cannot be
 * done directly because getMatteData() returns different types.
 * This class contains everything for FlatMatte except getMatteData
 * <br>See (C) official HaVi documentation for reference.
 * <p>
 * <p>Revision information:<br>
 * $Revision: 1.10 $
 *
 */

public abstract class HAbstractFlatMatte implements HMatte {

  /** Opacity level in the [0:1] range, where 0.0 is fully transparent
   *  and 1.0 is fully opaque */
  protected float data;
  
  /** Constructor with no parameter. Default for data = 1.0 (fully opaque) */
  public HAbstractFlatMatte() {
    this(1.0F);
  }

  /** Constructor with opacity level
   *  @param data opacity level between 0.0 and 1.0 */
  public HAbstractFlatMatte(float data) {
    setMatteData(data);
  }

  /** Change the opacity level. If not in the [0:1] range, the new value
   *  is simply ignored.
   *  @param data new opacity level between 0.0 and 1.0 */
  public void setMatteData(float data) {
    if( (data<0.0) || (data>1.0) ) {
      return;
    }
    this.data = data;
    notifyListeners();
  }

  // See HFlatMatte for this
  // public Object getMatteData()


  /* ************** NOT PART OF API ********************** */

  /** List of listeners registered to receive matte change events */
  private ArrayList listeners = new ArrayList();

  /** Register to receive matte change events
      @param listener target listener to add */
  public void addListener(HMatteListener listener) {
    synchronized(listeners) {
      listeners.add(listener);
    }
  }

  /** Remove a listener
      @param listener listener to remove */
  public void removeListener(HMatteListener listener) {
    synchronized(listeners) {
      /* listener may have been registered several times */
      while(listeners.remove(listener)) {}
    }
  }

  private class NotifyThread extends Thread {
    private HMatteEvent event;
    public NotifyThread(HMatteEvent e) {
      super();
      this.event = e;
      setName("HAbstractFlatMatte listeners notify thread");
    }
    public void run() {
      synchronized(listeners) {
        for(int i=0; i < listeners.size(); i++) {
          ((HMatteListener)(listeners.get(i))).matteUpdate(event);
        }
      }
    }
  }
    
  
  /** Notify all listeners of a matte change */
  public void notifyListeners() {

    Thread notifyThread = new NotifyThread(new HMatteEvent(this));
    notifyThread.start();

  }
  
  
  // TODO: ADD ACTUAL RENDERING TOOLS HERE

  public void paintContainerBackground(Graphics g, Component c) {

    Color bg = c.getBackground();
    Color alphabg =  new Color(bg.getRed(), bg.getGreen(), bg.getBlue(),
                                (int)(bg.getAlpha()*data));
    g.setColor(alphabg);
    g.fillRect(0, 0, c.getWidth()-1, c.getHeight()-1);
    g.setColor(bg);
    
  }

  /** WARNING: This is not part of the official HAVi APIs.
      <p>Compose an image based on the current matte rules.
      @param source image to compose with the matte
      @param clip area of the image to compose with (from calling Graphics)
  */
  public void compose(DVBGraphics source, Rectangle rect) {


    /* Optimization: Fully opaque: do nothing */
    if(data==1.0F) {
      return;
    }
    
    //DST_IN takes the color from destination and multiplies source and destination alpha
    try {
      source.setDVBComposite(org.dvb.ui.DVBAlphaComposite.DstIn);
    } catch (org.dvb.ui.UnsupportedDrawingOperationException ex) {
      return;
    }
    source.setColor(new Color(0,0,0, (int)(255.0*data)));
    source.fillRect(rect.x, rect.y, rect.width, rect.height);
    
    //using a non-official natively supported extension
    //source.tileBlitImageAlpha(tileImage, 0, 0);
    
    /*BufferedImage subimage;
    //try {
    subimage = (BufferedImage)source.getSubimage( clip.x, clip.y,
                                                 clip.width, clip.height);
    //} catch (org.dvb.ui.DVBRasterFormatException ex) {
    //   ex.printStackTrace();
    //   return;
    //}
    Graphics sourceg = source.getGraphics();
    
    if (sourceg != null) {
      try {
        
        // source is declared as a 4-banded INT_ARGB image 
        // TODO: check the color model for safety
        float[] scale = { 1.0F, 1.0F, 1.0F, this.data };
        float[] offset = { 0.0F, 0.0F, 0.0F, 0.0F };
        RescaleOp op = new RescaleOp( scale, offset, null);
        op.filter(subimage, subimage);
        // Note: since the subimage is completed loaded (obviously), the
        //   copy operation is synchronous 
        if(! sourceg.drawImage(subimage, clip.x, clip.y, null)) {
          // TODO: act on this
          System.out.println("Ooops: HAbstractMatte subimage not complete");
        }
      } finally {
        sourceg.dispose();
      }
    }*/
  }

}
