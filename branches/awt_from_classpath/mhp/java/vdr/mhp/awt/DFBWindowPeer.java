/* 

Taken and adapted from:

   Gtk{Component, Container, Window, Frame}Peer.java -- Implements *Peer with GTK
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
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.FramePeer;
import java.awt.MHPPlane;
import java.awt.MHPScreen;
import java.util.Date;

public class DFBWindowPeer
  implements FramePeer
{
   long nativeData = 0; // pointer to an IDirectFBWindow
   long nativeEventBuffer = 0; // pointer to an IDirectFBEventBuffer
   long nativeLayer = 0; // pointer to an IDirectFBDisplayLayer
   
   EventThread eventThread;
   boolean withEventThread = true;
   
   boolean wasVisible = false;
   
  //VolatileImage backBuffer;
  //BufferCapabilities caps;

  MHPPlane awtComponent;

  Insets insets;

  //boolean isInRepaint;

  private native long createDFBWindow(long layer, int x, int y, int width, int height);
  private native long attachEventBuffer(long nativeData);
  private native void destroy(long nativeData);
  private native void removeRefs(long nativeWindow, long nativeEventBuffer);
  private native void getPosition(long nativeData, int[] position);
  private native void getSize(long nativeData, int[] position);
  private native void requestFocus(long nativeData);
  private native void setSize (long nativeData, int width, int height);
  private native void moveTo (long nativeData, int x, int y);
  private native int getOpacity(long nativeData);
  private native void setOpacity(long nativeData, int opacity);
  private native long getSurface(long nativeData);
  private native void setStackingClass(long nativeData, int stacking);
  private native void raise(long nativeData);
  private native void lower(long nativeData);
  private native void raiseToTop(long nativeData);
  private native void lowerToBottom(long nativeData);
  private native void putAtop(long nativeData, long otherNativeData);
  private native void putBelow(long nativeData, long otherNativeData);

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
  
  // Called only from MHPPlane's addNotify() immediately after constructing this object.
  public void create(int x, int y, int width, int height, long nativeLayer, boolean withEventThread) {
     // do the actual native creation
     nativeData=createDFBWindow(nativeLayer,x,y,width, height);
     if (withEventThread) {
        nativeEventBuffer=attachEventBuffer(nativeData);
        if (nativeEventBuffer!=0)
           eventThread=new EventThread(awtComponent, nativeEventBuffer);
     }
     
     // sync native state to state of AWT component - may have been changed before addNotify'ing!
     setVisible(awtComponent.isVisible());
  }

  public synchronized void dispose() {
     if (nativeData != 0) {
        System.out.println("DFBWindowPeer.dispose()");
        destroy(nativeData);
        // bring event thread to a clean end, handling all pending events
        if (withEventThread)
           eventThread.endEventHandling();
        removeRefs(nativeData, nativeEventBuffer);
        nativeData = 0;
     }
  }

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
  
  // this is internal, not needed for WindowPeer interface
  Dimension getSize() {
     int size[] = new int[2];
     getSize(nativeData, size);
     return new Dimension(size[0], size[1]);
  }

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
     System.out.println("DFBWindowPeer.handleEvent "+event);
    int id = event.getID();
    //KeyEvent ke = null;

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
        /*
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
        */
      }
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
     System.out.println("DFBWindowPeer.repaint "+x+","+y+", "+width+"-"+height);
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

  public void show() {
     setVisible(true);
  }
  
  public void hide() {
     setVisible(false);
  }
  
  public void setVisible (boolean b)
  {
     // Need to check here if an initial painting is necessary.
     // DirectFB does not send expose events or something else to trigger initial repaint.
     if (b) {
       setOpacity(0xFF);
       if (!wasVisible) {
          wasVisible = true;
          triggerRepaint();
       }
     } else {
       setOpacity(0x00);
       wasVisible = false;
     }
  }


  public int getOpacity() {
     return getOpacity(nativeData);
  }

  public void setOpacity(int opacity) {
     if ( 0x00 <= opacity && opacity <= 0xFF )
        setOpacity(nativeData, opacity);
     else
        throw new IllegalArgumentException("Opacity "+opacity+" out of range");
  }

//the returned IDirectedFBSurface must be Release'd!
  public long getNativeSurface() {
     return nativeData == 0 ? 0 : getSurface(nativeData);
  }

  public void setStackingClass(int stacking) {
     setStackingClass(nativeData, stacking);
  }

//These functions work on the native DirectFB window stack.
//Please note that they take the stacking class into account,
//so HScenes can be moved around freely without danger of putting them behind
//a background plane
  public void raise() {
     //if (nativeData != 0)
        raise(nativeData);
  }

  public void lower() {
     //if (nativeData != 0)
        lower(nativeData);
  }

  public void raiseToTop() {
     //if (nativeData != 0)
        raiseToTop(nativeData);
  }

  public void lowerToBottom() {
     //if (nativeData != 0)
        lowerToBottom(nativeData);
  }

  public void putAtop(DFBWindowPeer other) {
        putAtop(nativeData, other.nativeData);
  }

  public void putBelow(DFBWindowPeer other) {
     //if (nativeData != 0 && other.nativeData != 0)
        putBelow(nativeData, other.nativeData);
  }
  
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
  
  protected void triggerRepaint()
  {
     //if (!isInRepaint)
     q().postEvent (new PaintEvent (awtComponent, PaintEvent.PAINT, new Rectangle (0, 0, awtComponent.getWidth(), awtComponent.getHeight())));
  }

  protected void postKeyEvent (int id, long when, int mods,
                               int keyCode, char keyChar)
  {
     KeyEvent keyEvent = new KeyEvent (awtComponent, id, when, mods,
                                       keyCode, keyChar);

     //System.out.println("DFBWindowPeer: posting KeyEvent "+keyEvent);
    // Also post a KEY_TYPED event if keyEvent is a key press that
    // doesn't represent an action or modifier key.
     if (keyEvent.getID () == KeyEvent.KEY_PRESSED
         && (!keyEvent.isActionKey ()
         && keyCode != KeyEvent.VK_SHIFT
         && keyCode != KeyEvent.VK_CONTROL
         && keyCode != KeyEvent.VK_ALT))
     {
        synchronized (q())
        {
           q().postEvent (keyEvent);
           q().postEvent (new KeyEvent (awtComponent, KeyEvent.KEY_TYPED, when, mods,
           KeyEvent.VK_UNDEFINED, keyChar));
        }
     }
     else
        q().postEvent (keyEvent);
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



  // --- ContainerPeer ---
  
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

  
  // --- FramePeer ---

  public void setIconImage(Image image) {
     throw new UnsupportedOperationException();
  }
  
  public void setMenuBar(MenuBar mb) {
     throw new UnsupportedOperationException();
  }
  
  public void setResizable(boolean resizable) {
  }
  
  public void setTitle(String title) {
     throw new UnsupportedOperationException();
  }
  
  public int getState() {
     return 0;
  }
  
  public void setState(int state) {
  }
  
  public void setMaximizedBounds(Rectangle r) {
  }




/** Event handling **/


interface DFBEventConstants {
   // Taken from directfb.h
   
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
   
   //for use with event[12]
   final static int DIBM_LEFT           = 0x00000001;  /* left mouse button */
   final static int DIBM_RIGHT          = 0x00000002;  /* right mouse button */
   final static int DIBM_MIDDLE         = 0x00000004;  /* middle mouse button */
   
   //for use with event[11]
   final static int DIBI_LEFT           = 0x00000000;  /* left mouse button */
   final static int DIBI_RIGHT          = 0x00000001;  /* right mouse button */
   final static int DIBI_MIDDLE         = 0x00000002;  /* middle mouse button */
   final static int DIBI_FIRST          = DIBI_LEFT;   /* other buttons:  DIBI_FIRST + zero based index */
   final static int DIBI_LAST           = 0x0000001F;   /* 32 buttons maximum */
}

/*
typedef struct {
   DFBEventClass                   clazz;      // clazz of event

   DFBWindowEventType              type;       // type of event 
   DFBWindowID                     window_id;  // source of event 

     // used by DWET_MOVE, DWET_MOTION, DWET_BUTTONDOWN, DWET_BUTTONUP, DWET_ENTER, DWET_LEAVE 
   int                             x;          // x position of window or coordinate within window 
   int                             y;          // y position of window or coordinate within window 

     // used by DWET_MOTION, DWET_BUTTONDOWN, DWET_BUTTONUP, DWET_ENTER, DWET_LEAVE 
   int                             cx;         // x cursor position 
   int                             cy;         // y cursor position 

   // used by DWET_WHEEL 
   int                             step;       // wheel step 

   // used by DWET_RESIZE 
   int                             w;          // width of window 
   int                             h;          // height of window 

   // used by DWET_KEYDOWN, DWET_KEYUP 
   int                             key_code;   // hardware keycode, no mapping, -1 if device doesn't differentiate between several keys 
   DFBInputDeviceKeyIdentifier     key_id;     // basic mapping, modifier independent 
   DFBInputDeviceKeySymbol         key_symbol; // advanced mapping, unicode compatible, modifier dependent 
   DFBInputDeviceModifierMask      modifiers;  // pressed modifiers 
   DFBInputDeviceLockState         locks;      // active locks 

   // used by DWET_BUTTONDOWN, DWET_BUTTONUP 
   DFBInputDeviceButtonIdentifier  button;     // button being pressed or released 
   // used by DWET_MOTION, DWET_BUTTONDOWN, DWET_BUTTONUP 
   DFBInputDeviceButtonMask        buttons;    // mask of currently pressed buttons 

   struct timeval                  timestamp;  // always set 
} DFBWindowEvent;
*/

//this class translates the relevant native DirectFB events
//to Java AWTEvents. These are posted to the Java eventQueue.
class EventThread extends Thread implements DFBEventConstants {

   long nativeData; // an IDirectFBEventBuffer
   long nativeEvent; //one DFBEvent being recycled
   MHPPlane plane;
   private boolean running = false;
   private boolean comeToAnEnd = false;
   int[] eventData=new int[16];
   long midnight;

   // allocate a native event structure
   private native long allocateEvent();
   // delete native event structure
   private native void deleteEvent(long nativeEvent);
   // wait for events on native event buffer
   private native void waitForEvent(long nativeData);
   // wake up any threads waiting in waitForEvent
   private native void wakeUp(long nativeData);
   // returns true if another event is available, but does not wait
   private native boolean hasEvent(long nativeData);
   // read the next native event in native event structure
   // returns true if event is window event (should always be, I think)
   private native boolean getEvent(long nativeData, long nativeEvent);
   // fills information from native event structure into Java array
   private native void fillEventInformation(long nativeEvent, int[] eventData);
   /*
   The eventData array contains the data from a DFBWindowEvent.
   See above for this structure and for which events which fields are valid.
   The array is filled as follows:
   data[0]=e->type;               type of event, DFBEventConstants
   data[1]=e->x;                  x position of window or coordinate within window
   data[2]=e->y;                  y position of window or coordinate within window
   data[3]=e->cx;                 x cursor position
   data[4]=e->cy;                 y cursor position
   data[5]=e->step;               wheel step
   data[6]=e->w;                  width of window
   data[7]=e->h;                  height of window
   data[8]=e->key_id;             basic modifier independant mapping
   data[9]=e->key_symbol;         advanced, unicode compatible, modifier independant mapping
   data[10]=e->modifiers;         pressed modifiers
   data[11]=e->button;            button being pressed or released
   data[12]=e->buttons;           mask of currently pressed buttons
   data[13]=e->timestamp.tv_sec;  timestamp, seconds of day
   data[14]=e->timestamp.tv_usec; timestamp, microseconds of day
   data[15]=AWT virtual key id    translation of key_id, which is a DFBInputDeviceKeyIdentifier, to KeyEvent's VK_ constants
   */

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
   
   public void finalize() {
      deleteEvent(nativeEvent);
   }
   
   public void run() {
      try {
         while (running) {
            handleNextEvent();
         }
         running=false;
      } catch (Exception ex) {
         ex.printStackTrace();
      } catch (Throwable x) {
         vdr.mhp.ApplicationManager.reportError(x);
      }
   }
   
   AWTEvent handleNextEvent() {
      //System.out.println("Waiting for DFBEvent");
      if (comeToAnEnd) {
         // handle pending events, but do not wait
         if (!hasEvent(nativeData)) {
            running = false;
            return null;
         }
      } else
         waitForEvent(nativeData);
      //System.out.println("Waited for DFBEvent");
      if (getEvent(nativeData, nativeEvent)) {
         fillEventInformation(nativeEvent, eventData);
         createAWTEvent();
      }
      ((MHPToolkit)Toolkit.getDefaultToolkit()).wakeNativeWait();
      return null;
   }
   
   void endEventHandling() {
      comeToAnEnd = true;
      wakeUp(nativeData);
      try {
         join(1000);
      } catch (InterruptedException e) {
      }
   }

   //creates an AWT event from DirectFB eventData
   void createAWTEvent() {
      //System.out.println("createAWTEvent: "+eventData[0]);
      switch (eventData[0]) {
         case DWET_POSITION:
            awtComponent.setBoundsCallback(eventData[1], eventData[2], awtComponent.getWidth(), awtComponent.getHeight());
            break;
         case DWET_SIZE:
            awtComponent.setBoundsCallback(awtComponent.getX(), awtComponent.getY(), eventData[6], eventData[7]);
            break;
         case DWET_POSITION_SIZE:
            awtComponent.setBoundsCallback(eventData[1], eventData[2], eventData[6], eventData[7]);
            break;
            // This stuff should work, but the one-liner above is probably easier :-)
            /*
            //need to call package-private method of java.awt.Window here which handles these changes
            AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  java.lang.reflect.Method method = java.awt.Window.class.getDeclaredMethod("setBoundsCallback", new Class[] { int.class, int.class, int.class, int.class });
                  // protected method invocaton
                  method.setAccessible(true);
                  // both access native information
                  Point p = ;
                  Dimension size = ;
                  try {
                     Object[] args = new Object[] { new Integer(p.x), new Integer(p.y), new Integer(size.width), new Integer(size.height)};
                     method.invoke(awtComponent, args);
                  } finally {
                     method.setAccessible(false);
                  }
               }
            });
            */

         case DWET_KEYDOWN:
            postKeyEvent(KeyEvent.KEY_PRESSED, getMillis(), getModifierMask(), eventData[15], (char)eventData[9]);
            break;
         case DWET_KEYUP:
            postKeyEvent(KeyEvent.KEY_RELEASED, getMillis(), getModifierMask(), eventData[15], (char)eventData[9]);
            break;

         // See http://java.sun.com/j2se/1.4.2/docs/api/java/awt/doc-files/FocusSpec.html
         case DWET_GOTFOCUS:
            // do not post a FocusEvent here!
            //System.out.println("DFBWindowPeer.EventThread: Posting WindowEvent.WINDOW_GAINED_FOCUS");
            q().postEvent(new WindowEvent(awtComponent, WindowEvent.WINDOW_ACTIVATED));
            q().postEvent(new WindowEvent(awtComponent, WindowEvent.WINDOW_GAINED_FOCUS));
            break;
         case DWET_LOSTFOCUS:
            //System.out.println("DFBWindowPeer.EventThread: Posting WindowEvent.WINDOW_LOST_FOCUS");
            q().postEvent(new WindowEvent(awtComponent, WindowEvent.WINDOW_LOST_FOCUS));
            q().postEvent(new WindowEvent(awtComponent, WindowEvent.WINDOW_DEACTIVATED));
            break;

         case DWET_CLOSE:
            //System.out.println("DFBWindowPeer.EventThread: Posting WindowEvent.WINDOW_CLOSING");
            q().postEvent(new WindowEvent(awtComponent, WindowEvent.WINDOW_CLOSING));
            break;
         case DWET_DESTROYED:
            //System.out.println("DFBWindowPeer.EventThread: Posting WindowEvent.WINDOW_CLOSED");
            q().postEvent(new WindowEvent(awtComponent, WindowEvent.WINDOW_CLOSED));
            break;

         case DWET_BUTTONDOWN:
         case DWET_BUTTONUP:
         case DWET_MOTION:
         case DWET_ENTER:
         case DWET_LEAVE:
         case DWET_WHEEL:
            createMouseEvent();
            break;
         default:
            break;
      }
   }
   
   int button_number = -1;
   int click_count = 1;
   boolean hasBeenDragged = false;
   long button_click_time = 0;
   final int MULTI_CLICK_TIME = 250;
   
   void createMouseEvent() {
      //System.out.println("DFBWindowPeer.EventThread: Posting MouseEvent");
      long millis = getMillis();
      if ( (millis < (button_click_time + MULTI_CLICK_TIME))
             && (eventData[11] == button_number))
         click_count++;
      else
         click_count = 1;
      
      button_click_time = millis;
      button_number = eventData[11];
      
      // use button, not buttons, Sun says "the button mask returned by InputEvent.getModifiers()
      // reflects only the button that changed state, not the current state of all buttons."
      // Classpath says:
      /* "Modifier key events need special treatment.  In Sun's peer
      implementation, when a modifier key is pressed, the KEY_PRESSED
      event has that modifier in its modifiers list.  The corresponding
      KEY_RELEASED event's modifier list does not contain the modifier.
      For example, pressing and releasing the shift key will produce a
      key press event with modifiers=Shift, and a key release event with
      no modifiers."  */
      
      // Then, information from DirectFB is somewhat limited. Not all fields are valid for all events, see structure
      // with comments above, but these comments are the only source of this information, no official specification.
      
      switch(eventData[0]) {
         case DWET_BUTTONDOWN:
            q().postEvent(new MouseEvent(awtComponent, MouseEvent.MOUSE_PRESSED, millis, getModifierMaskForButton(), eventData[1], eventData[2], click_count, ((eventData[11] == DIBI_RIGHT) ? true : false)));
            hasBeenDragged=false;
            break;
            
         case DWET_BUTTONUP:
            q().postEvent(new MouseEvent(awtComponent, MouseEvent.MOUSE_RELEASED, millis, getModifierMaskForButton(), eventData[1], eventData[2], click_count, false));
            if (!hasBeenDragged)
               q().postEvent(new MouseEvent(awtComponent, MouseEvent.MOUSE_RELEASED, millis, getModifierMaskForButton(), eventData[1], eventData[2], click_count, false));
            break;
            
         case DWET_MOTION:
            if ((getModifierMaskForButtonMask() & (InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
               q().postEvent(new MouseEvent(awtComponent, MouseEvent.MOUSE_DRAGGED, millis, getModifierMaskForButtonMask(), eventData[1], eventData[2], 0, false));
               hasBeenDragged=true;
            } else {
               q().postEvent(new MouseEvent(awtComponent, MouseEvent.MOUSE_MOVED, millis, getModifierMaskForButtonMask(), eventData[1], eventData[2], 0, false));
            }
            break;
         case DWET_ENTER:
            q().postEvent(new MouseEvent(awtComponent, MouseEvent.MOUSE_ENTERED, millis, 0, eventData[1], eventData[2], 0, false));
            break;
         case DWET_LEAVE:
            q().postEvent(new MouseEvent(awtComponent, MouseEvent.MOUSE_EXITED, millis, 0, eventData[1], eventData[2], 0, false));
            break;
         case DWET_WHEEL:
            q().postEvent(new MouseEvent(awtComponent, MouseEvent.MOUSE_WHEEL, millis, 0, eventData[1], eventData[2], 0, false));
            break;
         default:
            break;
      }
   }
   
   /*
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
   */
   
   //converts time-since-midnight to time-since-Epoch
   long getMillis() {
      int secs=eventData[13];
      int usecs=eventData[14];
      long ret=midnight+secs*1000 + (usecs/1000);
      if (System.currentTimeMillis() < ret) //new day
         midnight+=24*60*60*1000;
      return ret;
   }
   
   //TODO?
   /* Modifier key events need special treatment.  In Sun's peer
   implementation, when a modifier key is pressed, the KEY_PRESSED
   event has that modifier in its modifiers list.  The corresponding
   KEY_RELEASED event's modifier list does not contain the modifier.
   For example, pressing and releasing the shift key will produce a
   key press event with modifiers=Shift, and a key release event with
   no modifiers.  */
   int getModifierMask() {
      int DFBmask=eventData[10];
      int mask = 0;
      if ((DFBmask & DIMM_SHIFT)!=0)
         mask |= InputEvent.SHIFT_MASK | InputEvent.SHIFT_DOWN_MASK;
      if ((DFBmask & DIMM_CONTROL)!=0)
         mask |= InputEvent.CTRL_MASK | InputEvent.CTRL_MASK;
      if ((DFBmask & DIMM_ALT)!=0)
         mask |= InputEvent.ALT_MASK | InputEvent.ALT_DOWN_MASK;
      if ((DFBmask & DIMM_META)!=0)
         mask |= InputEvent.META_MASK | InputEvent.META_MASK;
      return mask;
   }
   
   int getModifierMaskForButtonMask() {
      int DFBbuttons = eventData[12];
      int mask=0;
      if ((DFBbuttons & DIBM_LEFT)!=0)
         mask |= InputEvent.BUTTON1_MASK;
      if ((DFBbuttons & DIBM_RIGHT)!=0)
         mask |= InputEvent.BUTTON3_MASK;
      if ((DFBbuttons & DIBM_MIDDLE)!=0)
         mask |= InputEvent.BUTTON2_MASK;
      return mask;
   }
   
   int getModifierMaskForButton() {
      int DFBbuttons = eventData[11];
      int mask=0;
      if ((DFBbuttons & DIBI_LEFT)!=0)
         mask |= InputEvent.BUTTON1_MASK;
      if ((DFBbuttons & DIBI_RIGHT)!=0)
         mask |= InputEvent.BUTTON3_MASK;
      if ((DFBbuttons & DIBI_MIDDLE)!=0)
         mask |= InputEvent.BUTTON2_MASK;
      return mask;
   }
   
}



}
