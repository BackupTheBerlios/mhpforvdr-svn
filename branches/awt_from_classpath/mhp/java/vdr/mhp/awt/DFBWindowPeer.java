/* DFBWindowPeer.java -- Implements ComponentPeer with GTK
   Copyright (C) 1998, 1999, 2002, 2004, 2005  Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package vdr.mhp.awt;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.WindowPeer;
import java.awt.MHPPlane;
import java.awt.MHPScreen;
import java.util.Date;

public class DFBWindowPeer
  implements WindowPeer
{
   long nativeData = 0; //pointer to an IDirectFBWindow
   long nativeEventBuffer=0;//pointer to an IDirectFBEventBuffer
   long nativeLayer=0; //pointer to an IDirectFBDisplayLayer
   EventThread eventThread;
   boolean withEventThread=true;
   
  //VolatileImage backBuffer;
  //BufferCapabilities caps;

  MHPPlane awtComponent;

  Insets insets;

  boolean isInRepaint;

  void create ()
  {
    throw new RuntimeException ();
  }

  native void connectSignals ();

  protected DFBWindowPeer (MHPPlane awtComponent)
  {
    //super (awtComponent);
    this.awtComponent = awtComponent;
    insets = new Insets (0, 0, 0, 0);

    /*
    connectSignals ();

    if (awtComponent.getForeground () != null)
      setForeground (awtComponent.getForeground ());
    if (awtComponent.getBackground () != null)
      setBackground (awtComponent.getBackground ());
    if (awtComponent.getFont() != null)
      setFont(awtComponent.getFont());

    Component parent = awtComponent.getParent ();

    // Only set our parent on the GTK side if our parent on the AWT
    // side is not showing.  Otherwise the gtk peer will be shown
    // before we've had a chance to position and size it properly.
    if (awtComponent instanceof Window
        || (parent != null && ! parent.isShowing ()))
      setParentAndBounds ();
    */
  }
  
  public void create(int x, int y, int width, int height, long nativeLayer, boolean withEventThread) {
     //do the actual native creation
     nativeData=createDFBWindow(nativeLayer,x,y,width, height);
     if (withEventThread) {
        //MHPScreen.checkEventDispatching(); //make sure dispatching thread started
        nativeEventBuffer=attachEventBuffer(nativeData);
        if (nativeEventBuffer!=0)
           eventThread=new EventThread(awtComponent, nativeEventBuffer);
     }
  }
  private native long createDFBWindow(long layer, int x, int y, int width, int height);
  private native long attachEventBuffer(long nativeData);

  public void dispose() {
     if (nativeData == 0) {
        destroy(nativeData);
        removeRefs(nativeData, nativeEventBuffer);
        nativeData = 0;
     }
  }
  private native void destroy(long nativeData);
  private native void removeRefs(long nativeWindow, long nativeEventBuffer);

  /*
  void setParentAndBounds ()
  {
    setParent ();

    setComponentBounds ();

    setVisibleAndEnabled ();
  }

  void setParent ()
  {
    ComponentPeer p;
    Component component = awtComponent;
    do
      {
        component = component.getParent ();
        p = component.getPeer ();
      }
    while (p instanceof java.awt.peer.LightweightPeer);

    if (p != null)
      gtkWidgetSetParent (p);
  }

  void beginNativeRepaint ()
  {
    isInRepaint = true;
  }

  void endNativeRepaint ()
  {
    isInRepaint = false;
  }
  */

  /*
   * Set the bounds of this peer's AWT Component based on dimensions
   * returned by the native windowing system.  Most Components impose
   * their dimensions on the peers which is what the default
   * implementation does.  However some peers, like GtkFileDialogPeer,
   * need to pass their size back to the AWT Component.
   */
  /*
  void setComponentBounds ()
  {
    Rectangle bounds = awtComponent.getBounds ();

    if (bounds.x == 0 && bounds.y == 0
        && bounds.width == 0 && bounds.height == 0)
      return;

    setBounds (bounds.x, bounds.y, bounds.width, bounds.height);
  }
  
  void setVisibleAndEnabled ()
  {
    setVisible (awtComponent.isVisible ());
    setEnabled (awtComponent.isEnabled ());
  }
  */

  public int checkImage (Image image, int width, int height, 
			 ImageObserver observer) 
  {
    return getToolkit().checkImage(image, width, height, observer);
  }

  public Image createImage (ImageProducer producer) 
  {
    return new MHPImage (producer);
  }

  public Image createImage (int width, int height)
  {
    Image image;
    /*
    if (MHPToolkit.useGraphics2D ())
      image = new BufferedImage (width, height, BufferedImage.TYPE_INT_RGB);
    else*/
      image = new MHPImage (width, height);

    Graphics g = image.getGraphics();
    g.setColor(awtComponent.getBackground());
    g.fillRect(0, 0, width, height);
    return image;
  }

  public void disable () 
  {
    setEnabled (false);
  }

  public void enable () 
  {
    setEnabled (true);
  }

  public ColorModel getColorModel () 
  {
     return getToolkit().getColorModel();
    //return ColorModel.getRGBdefault ();
  }

  public FontMetrics getFontMetrics (Font font)
  {
    return getToolkit().getFontMetrics(font);
  }

  public Graphics getGraphics ()
  {
     return MHPNativeGraphics.createClippedGraphics(awtComponent);
     /*
    if (MHPToolkit.useGraphics2D ())
        return new GdkGraphics2D (this);
    else
        return new GdkGraphics (this);
     */
  }

  public Point getLocationOnScreen () 
  { 
     //may be awtComponent.getLocation is enough, but doing it native does not hurt
    int point[] = new int[2];
    getPosition(nativeData, point);
    return new Point (point[0], point[1]);
  }
  private native void getPosition(long nativeData, int[] position);

  public Dimension getMinimumSize () 
  {
    return minimumSize ();
  }

  public Dimension getPreferredSize ()
  {
    return preferredSize ();
  }

  public Toolkit getToolkit ()
  {
    return Toolkit.getDefaultToolkit();
  }
  
  public void handleEvent (AWTEvent event)
  {
     /*
    int id = event.getID();
    KeyEvent ke = null;

    switch (id)
      {
      case PaintEvent.PAINT:
      case PaintEvent.UPDATE:
        {
          try 
            {
              Graphics g = getGraphics ();
          
              // Some peers like GtkFileDialogPeer are repainted by Gtk itself
              if (g == null)
                break;

              g.setClip (((PaintEvent) event).getUpdateRect());

              if (id == PaintEvent.PAINT)
                awtComponent.paint (g);
              else
                awtComponent.update (g);

              g.dispose ();
            }
          catch (InternalError e)
            {
              System.err.println (e);
            }
        }
        break;
      case KeyEvent.KEY_PRESSED:
        ke = (KeyEvent) event;
        gtkWidgetDispatchKeyEvent (ke.getID (), ke.getWhen (), ke.getModifiersEx (),
                                   ke.getKeyCode (), ke.getKeyLocation ());
        break;
      case KeyEvent.KEY_RELEASED:
        ke = (KeyEvent) event;
        gtkWidgetDispatchKeyEvent (ke.getID (), ke.getWhen (), ke.getModifiersEx (),
                                   ke.getKeyCode (), ke.getKeyLocation ());
        break;
      }
     */
  }
  
  public boolean isFocusTraversable () 
  {
    return true;
  }

  public Dimension minimumSize () 
  {
     return awtComponent.getSize();
     /*
    int dim[] = new int[2];

    gtkWidgetGetPreferredDimensions (dim);

    return new Dimension (dim[0], dim[1]);
     */
  }

  public void paint (Graphics g)
  {
  }

  public Dimension preferredSize ()
  {
     return awtComponent.getSize();
     /*
    int dim[] = new int[2];

    gtkWidgetGetPreferredDimensions (dim);

    return new Dimension (dim[0], dim[1]);
     */
  }

  public boolean prepareImage (Image image, int width, int height,
			       ImageObserver observer) 
  {
    return getToolkit().prepareImage(image, width, height, observer);
  }

  public void print (Graphics g) 
  {
    throw new RuntimeException ();
  }

  public void repaint (long tm, int x, int y, int width, int height)
  {
    if (x == 0 && y == 0 && width == 0 && height == 0)
      return;

    q().postEvent (new PaintEvent (awtComponent, PaintEvent.UPDATE,
                                 new Rectangle (x, y, width, height)));
  }

  public void requestFocus ()
  {
    requestFocus(nativeData);
    //gtkWidgetRequestFocus();
    postFocusEvent(FocusEvent.FOCUS_GAINED, false);
  }
  private native void requestFocus(long nativeData);


  public void reshape (int x, int y, int width, int height) 
  {
    setBounds (x, y, width, height);
  }

  /*
  public void setBackground (Color c) 
  {
    gtkWidgetSetBackground (c.getRed(), c.getGreen(), c.getBlue());
  }
  */

  private native void setSize (long nativeData, int width, int height);
  private native void moveTo (long nativeData, int x, int y);

  public void setBounds (int x, int y, int width, int height)
  {
    setSize (nativeData, width, height);
    moveTo(nativeData, x, y);
  }

  void setCursor ()
  {
    setCursor (awtComponent.getCursor ());
  }

  public void setCursor (Cursor cursor) 
  {
    //gtkWidgetSetCursor (cursor.getType ());
  }

  public void setEnabled (boolean b)
  {
    //gtkWidgetSetSensitive (b);
  }

  /*
  public void setFont (Font f)
  {
    // FIXME: This should really affect the widget tree below me.
    // Currently this is only handled if the call is made directly on
    // a text widget, which implements setFont() itself.
    gtkWidgetModifyFont(f.getName(), f.getStyle(), f.getSize());
  }
  */

  public void setForeground (Color c) 
  {
    //gtkWidgetSetForeground (c.getRed(), c.getGreen(), c.getBlue());
  }

  /*
  public Color getForeground ()
  {
    int rgb[] = gtkWidgetGetForeground ();
    return new Color (rgb[0], rgb[1], rgb[2]);
  }

  public Color getBackground ()
  {
    int rgb[] = gtkWidgetGetBackground ();
    return new Color (rgb[0], rgb[1], rgb[2]);
  }
  */

  public void setVisible (boolean b)
  {
    if (b)
      show ();
    else
      hide ();
  }

  public native void hide ();
  public native void show ();

  public int getOpacity() {
     return getOpacity(nativeData);
  }
  private native int getOpacity(long nativeData);

  public void setOpacity(int opacity) {
     if ( 0x00 <= opacity && opacity <= 0xFF )
        setOpacity(nativeData, opacity);
     else
        throw new IllegalArgumentException("Opacity "+opacity+" out of range");
  }
  private native void setOpacity(long nativeData, int opacity);

//the returned IDirectedFBSurface must be Release'd!
  public long getNativeSurface() {
     return nativeData == 0 ? 0 : getSurface(nativeData);
  }
  private native long getSurface(long nativeData);

  public void setStackingClass(int stacking) {
     setStackingClass(nativeData, stacking);
  }
  private native void setStackingClass(long nativeData, int stacking);

//These functions work on the native DirectFB window stack.
//Please note that they take the stacking class into account,
//so HScenes can be moved around freely without danger of putting them behind
//a background plane
  public void raise() {
     //if (nativeData != 0)
        raise(nativeData);
  }
  private native void raise(long nativeData);

  public void lower() {
     //if (nativeData != 0)
        lower(nativeData);
  }
  private native void lower(long nativeData);

  public void raiseToTop() {
     //if (nativeData != 0)
        raiseToTop(nativeData);
  }
  private native void raiseToTop(long nativeData);

  public void lowerToBottom() {
     //if (nativeData != 0)
        lowerToBottom(nativeData);
  }
  private native void lowerToBottom(long nativeData);

  public void putAtop(DFBWindowPeer other) {
        putAtop(nativeData, other.nativeData);
  }
  private native void putAtop(long nativeData, long otherNativeData);

  public void putBelow(DFBWindowPeer other) {
     //if (nativeData != 0 && other.nativeData != 0)
        putBelow(nativeData, other.nativeData);
  }
  private native void putBelow(long nativeData, long otherNativeData);
  
  // The two function from WindowPeer
  public void toBack() {
     lowerToBottom();
  }
  public void toFront() {
     raiseToTop();
  }
  
  // helpers
  
  static EventQueue q ()
  {
     return Toolkit.getDefaultToolkit ().getSystemEventQueue ();
  }

  protected void postFocusEvent (int id, boolean temporary)
  {
     q().postEvent (new FocusEvent (awtComponent, id, temporary));
  }
  /*
  protected void postMouseEvent(int id, long when, int mods, int x, int y, 
				int clickCount, boolean popupTrigger) 
  {
    q().postEvent(new MouseEvent(awtComponent, id, when, mods, x, y, 
			       clickCount, popupTrigger));
  }

  protected void postExposeEvent (int x, int y, int width, int height)
  {
    if (!isInRepaint)
      q().postEvent (new PaintEvent (awtComponent, PaintEvent.PAINT,
                                   new Rectangle (x, y, width, height)));
  }

  protected void postKeyEvent (int id, long when, int mods,
                               int keyCode, char keyChar, int keyLocation)
  {
    KeyEvent keyEvent = new KeyEvent (awtComponent, id, when, mods,
                                      keyCode, keyChar, keyLocation);

    // Also post a KEY_TYPED event if keyEvent is a key press that
    // doesn't represent an action or modifier key.
    if (keyEvent.getID () == KeyEvent.KEY_PRESSED
        && (!keyEvent.isActionKey ()
            && keyCode != KeyEvent.VK_SHIFT
            && keyCode != KeyEvent.VK_CONTROL
            && keyCode != KeyEvent.VK_ALT))
      {
        synchronized (q)
          {
            q().postEvent (keyEvent);
            q().postEvent (new KeyEvent (awtComponent, KeyEvent.KEY_TYPED, when, mods,
                                        KeyEvent.VK_UNDEFINED, keyChar, keyLocation));
          }
      }
    else
      q().postEvent (keyEvent);
  }

  protected void postItemEvent (Object item, int stateChange)
  {
    q().postEvent (new ItemEvent ((ItemSelectable)awtComponent, 
				ItemEvent.ITEM_STATE_CHANGED,
				item, stateChange));
  }
  
  void postWindowEvent (int id, Window opposite, int newState)
  {
    if (id == WindowEvent.WINDOW_OPENED)
      {
	// Post a WINDOW_OPENED event the first time this window is shown.
	if (!hasBeenShown)
	  {
	    q().postEvent (new WindowEvent ((Window) awtComponent, id,
					  opposite));
	    hasBeenShown = true;
	  }
      }
    else if (id == WindowEvent.WINDOW_STATE_CHANGED)
      {
	if (oldState != newState)
	  {
	    q().postEvent (new WindowEvent ((Window) awtComponent, id, opposite,
					  oldState, newState));
	    oldState = newState;
	  }
      }
    else
      q().postEvent (new WindowEvent ((Window) awtComponent, id, opposite));
  }
  */

  public GraphicsConfiguration getGraphicsConfiguration ()
  {
    // FIXME: just a stub for now.
    return null;
  }

  public void setEventMask (long mask)
  {
    // FIXME: just a stub for now.
  }

  public boolean isFocusable ()
  {
    return false;
  }

  public boolean requestFocus (Component source, boolean b1, 
                               boolean b2, long x)
  {
    return false;
  }

  public boolean isObscured ()
  {
    return false;
  }

  public boolean canDetermineObscurity ()
  {
    return false;
  }

  public void coalescePaintEvent (PaintEvent e)
  {
    
  }

  public void updateCursorImmediately ()
  {
    
  }

  public boolean handlesWheelScrolling ()
  {
    return false;
  }

  // Convenience method to create a new volatile image on the screen
  // on which this component is displayed.
  public VolatileImage createVolatileImage (int width, int height)
  {
     throw new UnsupportedOperationException();
    //return new GtkVolatileImage (width, height);
  }

  // Creates buffers used in a buffering strategy.
  public void createBuffers (int numBuffers, BufferCapabilities caps)
    throws AWTException
  {
     //in DirectFB everything is back-buffered anyway
     throw new AWTException("No access to backbuffering");
     /*
    // numBuffers == 2 implies double-buffering, meaning one back
    // buffer and one front buffer.
    if (numBuffers == 2)
      backBuffer = new GtkVolatileImage(awtComponent.getWidth(),
					awtComponent.getHeight(),
					caps.getBackBufferCapabilities());
    else
      throw new AWTException("DFBWindowPeer.createBuffers:"
			     + " multi-buffering not supported");
    this.caps = caps;
     */
  }

  // Return the back buffer.
  public Image getBackBuffer ()
  {
     throw new UnsupportedOperationException();
    //return backBuffer;
  }

  // FIXME: flip should be implemented as a fast native operation
  public void flip (BufferCapabilities.FlipContents contents)
  {
     throw new UnsupportedOperationException();
     /*
    getGraphics().drawImage(backBuffer,
			    awtComponent.getWidth(),
			    awtComponent.getHeight(),
			    null);

    // create new back buffer and clear it to the background color.
    if (contents == BufferCapabilities.FlipContents.BACKGROUND)
	{
	  backBuffer = createVolatileImage(awtComponent.getWidth(),
					   awtComponent.getHeight());
	  backBuffer.getGraphics().clearRect(0, 0,
					     awtComponent.getWidth(),
					     awtComponent.getHeight());
	}
    // FIXME: support BufferCapabilities.FlipContents.PRIOR
     */
  }

  // Release the resources allocated to back buffers.
  public void destroyBuffers ()
  {
    //backBuffer.flush();
  }



  /* --- ContainerPeer --- */
  
  boolean isValidating;

  public void beginValidate ()
  {
     isValidating = true;
  }

  public void endValidate ()
  {
     // this code seems to set parent and boundss for non-lightweight children.
     // Since there are none in MHP, skip it.
     /*
     Component parent = awtComponent.getParent ();

    // Only set our parent on the GTK side if our parent on the AWT
    // side is not showing.  Otherwise the gtk peer will be shown
    // before we've had a chance to position and size it properly.
     if (parent != null && parent.isShowing ())
     {
        Component[] components = ((Container) awtComponent).getComponents ();
        int ncomponents = components.length;

        for (int i = 0; i < ncomponents; i++)
        {
           ComponentPeer peer = components[i].getPeer ();

            // Skip lightweight peers.
           if (peer instanceof GtkComponentPeer)
              ((GtkComponentPeer) peer).setParentAndBounds ();
        }

        // GTK windows don't have parents.
        if (!(awtComponent instanceof Window))
           setParentAndBounds ();
     }
     */

     isValidating = false;
  }

  public Insets getInsets() 
  {
     return insets;
  }

  public Insets insets() 
  {
     return getInsets ();
  }

  public void setFont(Font f)
  {
     //super.setFont(f);
     Component[] components = ((Container) awtComponent).getComponents();
     for (int i = 0; i < components.length; i++)
     {
        if (components[i].isLightweight ())
           components[i].setFont (f);
        /*
        else
        {
           GtkComponentPeer peer = (GtkComponentPeer) components[i].getPeer();
           if (peer != null && ! peer.awtComponent.isFontSet())
              peer.setFont(f);
        }
        */
     }
  }

  public void beginLayout () { }
  public void endLayout () { }
  public boolean isPaintPending () { return false; }

  public void setBackground (Color c)
  {
     //super.setBackground(c);
  
     Object components[] = ((Container) awtComponent).getComponents();
     for (int i = 0; i < components.length; i++)
     {
        Component comp = (Component) components[i];

        // If the child's background has not been explicitly set yet,
        // it should inherit this container's background. This makes the
        // child component appear as if it has a transparent background.
        // Note that we do not alter the background property of the child,
        // but only repaint the child with the parent's background color.
        if (!comp.isBackgroundSet() && comp.getPeer() != null)
           comp.getPeer().setBackground(c);
     }
  }

  





/** Event handling **/


interface DFBEventConstants {
   final static int DWET_POSITION       = 0x00000001;  /* window has been moved by
                                         window manager or the
                                         application itself */
   final static int DWET_SIZE           = 0x00000002;  /* window has been resized
                                         by window manager or the
                                         application itself */
   final static int DWET_CLOSE          = 0x00000004;  /* closing this window has been
                                         requested only */
   final static int DWET_DESTROYED      = 0x00000008;  /* window got destroyed by global
                                         deinitialization function or
                                         the application itself */
   final static int DWET_GOTFOCUS       = 0x00000010;  /* window got focus */
   final static int DWET_LOSTFOCUS      = 0x00000020;  /* window lost focus */

   final static int DWET_KEYDOWN        = 0x00000100;  /* a key has gone down while
                                         window has focus */
   final static int DWET_KEYUP          = 0x00000200;  /* a key has gone up while
                                         window has focus */

   final static int DWET_BUTTONDOWN     = 0x00010000;  /* mouse button went down in
                                         the window */
   final static int DWET_BUTTONUP       = 0x00020000;  /* mouse button went up in
                                         the window */
   final static int DWET_MOTION         = 0x00040000;  /* mouse cursor changed its
                                         position in window */
   final static int DWET_ENTER          = 0x00080000;  /* mouse cursor entered
                                         the window */
   final static int DWET_LEAVE          = 0x00100000;  /* mouse cursor left the window */

   final static int DWET_WHEEL          = 0x00200000;  /* mouse wheel was moved while
                                         window has focus */

   final static int DWET_POSITION_SIZE  = DWET_POSITION | DWET_SIZE;/* initially sent to
                                                      window when it's
                                                      created */
                                                      
   final static int DIMM_SHIFT     = 1<<1;    /* Shift key is pressed */
   final static int DIMM_CONTROL   = 1<<2;  /* Control key is pressed */
   final static int DIMM_ALT       = 1<<3;      /* Alt key is pressed */
   final static int DIMM_ALTGR     = 1<<4;    /* AltGr key is pressed */
   final static int DIMM_META      = 1<<5;     /* Meta key is pressed */
   
}

//this class translates the relevant native DirectFB events
//to Java AWTEvents. These are posted to the Java eventQueue.
class EventThread extends Thread implements DFBEventConstants {

   long nativeData;
   long nativeEvent; //one DFBEvent being recycled
   MHPPlane plane;
   private boolean running = false;
   int[] eventData=new int[15];
   long midnight;


   EventThread(MHPPlane p, long nativeEventBuffer) {
      plane=p;
      nativeData=nativeEventBuffer;
      nativeEvent=allocateEvent();
      
      Date today=new Date();
      today.setHours(0);
      today.setMinutes(0);
      today.setSeconds(0);
      midnight=today.getTime();
      
      running=true;
      start();
   }
   private native long allocateEvent();
   
   public void finalize() {
      deleteEvent(nativeEvent);
   }
   private native void deleteEvent(long nativeEvent);
   
   public void run() {
      AWTEvent e;
      try {
         while (running) {
            e=getNextEvent();
            if (e != null)
               Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
         }
         running=false;
      } catch (Exception ex) {
         ex.printStackTrace();
      } catch (Throwable x) {
         vdr.mhp.ApplicationManager.reportError(x);
      }
   }
   
   AWTEvent getNextEvent() {
      //System.out.println("Waiting for DFBEvent");
      waitForEvent(nativeData);
      //System.out.println("Waited for DFBEvent");
      if (getEvent(nativeData, nativeEvent)) {
         fillEventInformation(nativeEvent, eventData);
         return createAWTEvent();
      }
      return null;
   }
   private native void waitForEvent(long nativeData);
      //returns true if event is window event (should always be, I think)
   private native boolean getEvent(long nativeData, long nativeEvent);
   private native void fillEventInformation(long nativeEvent, int[] eventData);
   /* the array is filled as follows:
   data[0]=e->type;               type of event, DFBEventConstants
   data[1]=e->x;                  x position of window or coordinate within window
   data[2]=e->y;                  y position of window or coordinate within window
   data[3]=e->cx;                 x cursor position
   data[4]=e->cy;                 y ~
   data[5]=e->step;               wheel step
   data[6]=e->w;                  width of window
   data[7]=e->h;                  height ~
   data[8]=e->key_id;             basic modifier independant mapping
   data[9]=e->key_symbol;         advanced, unicode compatible, modifier independant mapping
   data[10]=e->modifiers;         pressed modifiers
   data[11]=e->button;            button being pressed or released
   data[12]=e->buttons;           mask of currently pressed buttons
   data[13]=e->timestamp.tv_sec;  timestamp, seconds of day
   data[14]=e->timestamp.tv_usec; timestamp, microseconds of day
   */

   //creates an AWT event from DirectFB eventData
   AWTEvent createAWTEvent() {
      //System.out.println("createAWTEvent: "+eventData[0]);
      switch (eventData[0]) {
         case DWET_POSITION:
            return new ComponentEvent(plane, ComponentEvent.COMPONENT_MOVED);
         case DWET_SIZE:
            return new ComponentEvent(plane, ComponentEvent.COMPONENT_RESIZED);
         case DWET_KEYDOWN:
            //kaffe's key codes are unicode compatible, so DFB key_symbol == Java keyCode == Java keyChar
            return new KeyEvent(findKeyTarget(), KeyEvent.KEY_PRESSED, getMillis(eventData[13], eventData[14]), 
                                getModifierMask(eventData[10]), eventData[9], (char)eventData[9]);
         case DWET_KEYUP:
            return new KeyEvent(findKeyTarget(), KeyEvent.KEY_RELEASED, getMillis(eventData[13], eventData[14]), 
                                getModifierMask(eventData[10]), eventData[9], (char)eventData[9]);
         case DWET_BUTTONDOWN:
         case DWET_BUTTONUP:
         case DWET_MOTION:
         default:
            return null;
      }
   }
   
   Component findKeyTarget() {
      KeyboardFocusManager manager;
      manager = KeyboardFocusManager.getCurrentKeyboardFocusManager ();
      Component focusComponent = manager.getFocusOwner();
      if (focusComponent != null) {
         return focusComponent;
      } else {
         plane.requestFocus();
         return plane;
      }
   }
   
   //converts time-since-midnight to time-since-Epoch
   long getMillis(int secs, int usecs) {
      long ret=midnight+secs*1000 + (usecs/1000);
      if (System.currentTimeMillis() < ret) //new day
         midnight+=24*60*60*1000;
      return ret;
   }
   
   int getModifierMask(int DFBmask) {
      int mask=0;
      if ((DFBmask & DIMM_SHIFT)!=0)
         mask |= KeyEvent.SHIFT_MASK;
      if ((DFBmask & DIMM_CONTROL)!=0)
         mask |= KeyEvent.CTRL_MASK;
      if ((DFBmask & DIMM_ALT)!=0)
         mask |= KeyEvent.ALT_MASK;
      if ((DFBmask & DIMM_META)!=0)
         mask |= KeyEvent.META_MASK;
      return mask;
   }
   
}



}
