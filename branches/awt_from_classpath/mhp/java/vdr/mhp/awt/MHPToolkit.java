/* Toolkit.java -- AWT Toolkit superclass
   Copyright (C) 1999, 2000, 2001, 2002, 2003, 2004, 2005
   Free Software Foundation, Inc.

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


package java.awt;

import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.LightweightPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

public class MHPToolkit
{
   static EventQueue eventQueue;

  public MHPToolkit()
  {
  }

  /**
   * Creates a peer object for the specified <code>Button</code>.
   *
   * @param target The <code>Button</code> to create the peer for.
   * 
   * @return The peer for the specified <code>Button</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected ButtonPeer createButton(Button target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>TextField</code>.
   *
   * @param target The <code>TextField</code> to create the peer for.
   *
   * @return The peer for the specified <code>TextField</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected TextFieldPeer createTextField(TextField target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>Label</code>.
   *
   * @param target The <code>Label</code> to create the peer for.
   *
   * @return The peer for the specified <code>Label</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected LabelPeer createLabel(Label target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>List</code>.
   *
   * @param target The <code>List</code> to create the peer for.
   *
   * @return The peer for the specified <code>List</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected ListPeer createList(List target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>Checkbox</code>.
   *
   * @param target The <code>Checkbox</code> to create the peer for.
   *
   * @return The peer for the specified <code>Checkbox</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected CheckboxPeer createCheckbox(Checkbox target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>Scrollbar</code>.
   *
   * @param target The <code>Scrollbar</code> to create the peer for.
   *
   * @return The peer for the specified <code>Scrollbar</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected ScrollbarPeer createScrollbar(Scrollbar target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>ScrollPane</code>.
   *
   * @param target The <code>ScrollPane</code> to create the peer for.
   *
   * @return The peer for the specified <code>ScrollPane</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected ScrollPanePeer createScrollPane(ScrollPane target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>TextArea</code>.
   *
   * @param target The <code>TextArea</code> to create the peer for.
   *
   * @return The peer for the specified <code>TextArea</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected TextAreaPeer createTextArea(TextArea target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>Choice</code>.
   *
   * @param target The <code>Choice</code> to create the peer for.
   *
   * @return The peer for the specified <code>Choice</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected ChoicePeer createChoice(Choice target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>Frame</code>.
   *
   * @param target The <code>Frame</code> to create the peer for.
   *
   * @return The peer for the specified <code>Frame</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected FramePeer createFrame(Frame target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>Canvas</code>.
   *
   * @param target The <code>Canvas</code> to create the peer for.
   *
   * @return The peer for the specified <code>Canvas</code> object.
   */
  protected CanvasPeer createCanvas(Canvas target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>Panel</code>.
   *
   * @param target The <code>Panel</code> to create the peer for.
   *
   * @return The peer for the specified <code>Panel</code> object.
   */
  protected PanelPeer createPanel(Panel target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>Window</code>.
   *
   * @param target The <code>Window</code> to create the peer for.
   *
   * @return The peer for the specified <code>Window</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected WindowPeer createWindow(Window target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>Dialog</code>.
   *
   * @param target The dialog to create the peer for
   *
   * @return The peer for the specified font name.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected DialogPeer createDialog(Dialog target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>MenuBar</code>.
   *
   * @param target The <code>MenuBar</code> to create the peer for.
   *
   * @return The peer for the specified <code>MenuBar</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected MenuBarPeer createMenuBar(MenuBar target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>Menu</code>.
   *
   * @param target The <code>Menu</code> to create the peer for.
   *
   * @return The peer for the specified <code>Menu</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected MenuPeer createMenu(Menu target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>PopupMenu</code>.
   *
   * @param target The <code>PopupMenu</code> to create the peer for.
   *
   * @return The peer for the specified <code>PopupMenu</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected PopupMenuPeer createPopupMenu(PopupMenu target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>MenuItem</code>.
   *
   * @param target The <code>MenuItem</code> to create the peer for.
   *
   * @return The peer for the specified <code>MenuItem</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected MenuItemPeer createMenuItem(MenuItem target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>FileDialog</code>.
   *
   * @param target The <code>FileDialog</code> to create the peer for.
   *
   * @return The peer for the specified <code>FileDialog</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected FileDialogPeer createFileDialog(FileDialog target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>CheckboxMenuItem</code>.
   *
   * @param target The <code>CheckboxMenuItem</code> to create the peer for.
   *
   * @return The peer for the specified <code>CheckboxMenuItem</code> object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  protected CheckboxMenuItemPeer
    createCheckboxMenuItem(CheckboxMenuItem target) {
     return null;
  }

  /**
   * Creates a peer object for the specified <code>Component</code>.  The
   * peer returned by this method is not a native windowing system peer
   * with its own native window.  Instead, this method allows the component
   * to draw on its parent window as a "lightweight" widget.
   *
   * @param target The <code>Component</code> to create the peer for.
   *
   * @return The peer for the specified <code>Component</code> object.
   */
  /*
  protected LightweightPeer createComponent(Component target)
  {
    return new gnu.java.awt.peer.GLightweightPeer (target);
  }
  */

  /**
   * Creates a peer object for the specified font name.
   *
   * @param name The font to create the peer for.
   * @param style The font style to create the peer for.
   *
   * @return The peer for the specified font name.
   *
   * @deprecated
   */
  protected FontPeer getFontPeer(String name, int style) {
  }

  /**
   * Copies the current system colors into the specified array.  This is
   * the interface used by the <code>SystemColor</code> class.  Although
   * this method fills in the array with some default colors a real Toolkit
   * should override this method and provide real system colors for the
   * native GUI platform.
   *
   * @param systemColors The array to copy the system colors into.
   * It must be at least 26 elements.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   *
   * @see java.awt.SystemColor
   */
  /*
  protected void loadSystemColors(int systemColors[])
  {
    systemColors[SystemColor.DESKTOP]                 = 0xFF005C5C;
    systemColors[SystemColor.ACTIVE_CAPTION]          = 0xFF000080;
    systemColors[SystemColor.ACTIVE_CAPTION_TEXT]     = 0xFFFFFFFF;
    systemColors[SystemColor.ACTIVE_CAPTION_BORDER]   = 0xFFC0C0C0;
    systemColors[SystemColor.INACTIVE_CAPTION]        = 0xFF808080;
    systemColors[SystemColor.INACTIVE_CAPTION_TEXT]   = 0xFFC0C0C0;
    systemColors[SystemColor.INACTIVE_CAPTION_BORDER] = 0xFFC0C0C0;
    systemColors[SystemColor.WINDOW]                  = 0xFFFFFFFF;
    systemColors[SystemColor.WINDOW_BORDER]           = 0xFF000000;
    systemColors[SystemColor.WINDOW_TEXT]             = 0xFF000000;
    systemColors[SystemColor.MENU]                    = 0xFFC0C0C0;
    systemColors[SystemColor.MENU_TEXT]               = 0xFF000000;
    systemColors[SystemColor.TEXT]                    = 0xFFC0C0C0;
    systemColors[SystemColor.TEXT_TEXT]               = 0xFF000000;
    systemColors[SystemColor.TEXT_HIGHLIGHT]          = 0xFF000090;
    systemColors[SystemColor.TEXT_HIGHLIGHT_TEXT]     = 0xFFFFFFFF;
    systemColors[SystemColor.TEXT_INACTIVE_TEXT]      = 0xFF808080;
    systemColors[SystemColor.CONTROL]                 = 0xFFC0C0C0;
    systemColors[SystemColor.CONTROL_TEXT]            = 0xFF000000;
    systemColors[SystemColor.CONTROL_HIGHLIGHT]       = 0xFFFFFFFF;
    systemColors[SystemColor.CONTROL_LT_HIGHLIGHT]    = 0xFFE0E0E0;
    systemColors[SystemColor.CONTROL_SHADOW]          = 0xFF808080;
    systemColors[SystemColor.CONTROL_DK_SHADOW]       = 0xFF000000;
    systemColors[SystemColor.SCROLLBAR]               = 0xFFE0E0E0;
    systemColors[SystemColor.INFO]                    = 0xFFE0E000;
    systemColors[SystemColor.INFO_TEXT]               = 0xFF000000;
  }
  */


  /**
   * Returns the dimensions of the screen in pixels.
   *
   * @return The dimensions of the screen in pixels.
   * 
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  public Dimension getScreenSize() {
     return MHPScreen.getDotsPerInch();
  }

  /**
   * Returns the screen resolution in dots per square inch.
   *
   * @return The screen resolution in dots per square inch.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  public int getScreenResolution() {
     return MHPScreen.getResolution();
  }

  /**
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   *
   * @since 1.4
   */
  public Insets getScreenInsets(GraphicsConfiguration gc)
  {
    return null;
  }

  /**
   * Returns the color model of the screen.
   *
   * @return The color model of the screen.
   * 
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  public ColorModel getColorModel() {
      if ( colorModel == null ){
         colorModel = MHPScreen.getColorModel();
      }
      return colorModel;
  }


  /**
   * Returns the names of the available fonts.
   *
   * @return The names of the available fonts.
   *
   * @deprecated
   */
  public String[] getFontList() {
     String[] list = { "Default", "Monospaced",
                     "SansSerif", "Serif",
                       "Dialog", "DialogInput", "ZapfDingbats" };
     return list;
  }

  /**
   * Return the font metrics for the specified font
   *
   * @param name The name of the font to return metrics for.
   *
   * @return The requested font metrics.
   *
   * @deprecated
   */
   public FontMetrics getFontMetrics(Font name) {
      //TODO
      return FontMetrics.getFontMetrics( font);
   }

  /**
   * Flushes any buffered data to the screen so that it is in sync with
   * what the AWT system has drawn to it.
   */
  public void sync() {
     MHPScreen.sync();
  }

  /**
   * Returns an image from the specified file, which must be in a
   * recognized format.  Supported formats vary from toolkit to toolkit.
   *
   * @return name The name of the file to read the image from.
   */
   public Image getImage(String name) {
      return MHPImageProducer.getImage(name);
   }

  /**
   * Returns an image from the specified URL, which must be in a
   * recognized format.  Supported formats vary from toolkit to toolkit.
   *
   * @return url The URl to read the image from.
   */
  public Image getImage(URL url) {
     return MHPImageProducer.getImage(url);
  }

  public Image createImage(String filename) {
     return MHPImageProducer.createImage(filename);
  }

  public Image createImage(URL url) {
     return MHPImageProducer.createImage(url);
  }

  /**
   * Readies an image to be rendered on the screen.  The width and height
   * values can be set to the default sizes for the image by passing -1
   * in those parameters.
   *
   * @param image The image to prepare for rendering.
   * @param width The width of the image.
   * @param height The height of the image.
   * @param observer The observer to receive events about the preparation
   * process.
   *
   * @return <code>true</code> if the image is already prepared for rendering,
   * <code>false</code> otherwise.
   */
  public boolean prepareImage(Image image, int width, int height,
                                       ImageObserver observer)
  {
    /* GtkImages are always prepared, as long as they're loaded. */
    if (image instanceof MHPImage)
      return ((((MHPImage)image).checkImage (observer) & 
	       ImageObserver.ALLBITS) != 0);

    /* Assume anything else is too */
    return true;
  }

  /**
   * Checks the status of specified image as it is being readied for
   * rendering.
   *
   * @param image The image to prepare for rendering.
   * @param width The width of the image.
   * @param height The height of the image.
   * @param observer The observer to receive events about the preparation
   * process.
   *
   * @return A union of the bitmasks from
   * <code>java.awt.image.ImageObserver</code> that indicates the current
   * state of the imaging readying process.
   */
  public int checkImage(Image image, int width, int height,
                                 ImageObserver observer);
  {
    int status = ImageObserver.ALLBITS 
      | ImageObserver.WIDTH 
      | ImageObserver.HEIGHT;

    if (image instanceof MHPImage)
	return ((MHPImage) image).checkImage (observer);

    if (observer != null)
      observer.imageUpdate (image, status,
                            -1, -1,
                            image.getWidth (observer),
                            image.getHeight (observer));
    
    return status;
  }

  /**
   * Creates an image using the specified <code>ImageProducer</code>
   *
   * @param producer The <code>ImageProducer</code> to create the image from.
   *
   * @return The created image.
   */
  public Image createImage(ImageProducer producer) {
      return MHPImageProducer.createImage(producer);
  }

  /**
   * Creates an image from the specified byte array. The array must be in
   * a recognized format.  Supported formats vary from toolkit to toolkit.
   *
   * @param data The raw image data.
   *
   * @return The created image.
   */
  public Image createImage(byte[] data)
  {
    return createImage(data, 0, data.length);
  }

  /**
   * Creates an image from the specified portion of the byte array passed.
   * The array must be in a recognized format.  Supported formats vary from
   * toolkit to toolkit.
   *
   * @param data The raw image data.
   * @param offset The offset into the data where the image data starts.
   * @param len The length of the image data.
   *
   * @return The created image.
   */
  public Image createImage(byte[] data, int offset, int len) {
      return MHPImageProducer.createImage(data, offset, len);
  }

  /**
   * Returns a instance of <code>PrintJob</code> for the specified
   * arguments.
   *
   * @param frame The window initiating the print job.
   * @param title The print job title.
   * @param props The print job properties.
   *
   * @return The requested print job, or <code>null</code> if the job
   * was cancelled.
   *
   * @exception NullPointerException If frame is null,
   * or GraphicsEnvironment.isHeadless() returns true.
   * @exception SecurityException If this thread is not allowed to initiate
   * a print job request.
   */
  public PrintJob getPrintJob(Frame frame, String title,
                                       Properties props)
  {
      return null;
  }

  /**
   * Causes a "beep" tone to be generated.
   */
  public void beep() {
  }

  /**
   * Returns the system clipboard.
   *
   * @return THe system clipboard.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  public Clipboard getSystemClipboard() {
      return null;
  }

  /**
   * Gets the singleton instance of the system selection as a Clipboard object.
   *
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   *
   * @since 1.4
   */
  /*
  public Clipboard getSystemSelection()
  {
    return null;
  }
  */

  /**
   * Creates a new custom cursor object.
   *
   * @exception IndexOutOfBoundsException If the hotSpot values are outside
   * the bounds of the cursor.
   * @exception HeadlessException If GraphicsEnvironment.isHeadless() is true.
   */
  /*
  public Cursor createCustomCursor(Image cursor, Point hotSpot, String name)
  {
    // Presumably the only reason this isn't is for backwards
    // compatibility? FIXME?
    return null;
  }
  */

  /**
   * Returns the event queue that is suitable for the calling context.
   *
   * <p>Despite the word &#x201c;System&#x201d; in the name of this
   * method, a toolkit may provide different event queues for each
   * applet. There is no guarantee that the same queue is shared
   * system-wide.
   *
   * <p>The implementation first checks whether a
   * SecurityManager has been installed. If so, its {@link
   * java.lang.SecurityManager#checkAwtEventQueueAccess()} method gets
   * called. The security manager will throw a SecurityException if it
   * does not grant the permission to access the event queue.
   *
   * <p>Next, the call is delegated to {@link
   * #getSystemEventQueueImpl()}.
   *
   * @return The event queue for this applet (or application).
   *
   * @throws SecurityException if a security manager has been
   * installed, and it does not grant the permission to access the
   * event queue.
   */
  /*
  public final EventQueue getSystemEventQueue()
  {
    SecurityManager sm;

    sm = System.getSecurityManager();
    if (sm != null)
      sm.checkAwtEventQueueAccess();

    return getSystemEventQueueImpl();
  }
  */


  /**
   * Returns the event queue that is suitable for the calling context.
   *
   * <p>Despite the word &#x201c;System&#x201d; in the name of this
   * method, a toolkit may provide different event queues for each
   * applet. There is no guarantee that the same queue is shared
   * system-wide.
   *
   * <p>No security checks are performed, which is why this method
   * may only be called by Toolkits.
   *
   * @see #getSystemEventQueue()
   */
   protected EventQueue getSystemEventQueueImpl() {
      synchronized (this.class) {
         if (eventQueue == null) {
            eventQueue = new EventQueue();
         }
      }
      return eventQueue;
   }


  /**
   * @since 1.3
   */
  public DragSourceContextPeer
    createDragSourceContextPeer(DragGestureEvent e)
  {
      return null;
  }

  /**
   * @since 1.3
   */
  /*
  public DragGestureRecognizer
    createDragGestureRecognizer(Class recognizer, DragSource ds,
                                Component comp, int actions,
                                DragGestureListener l)
  {
    return null;
  }
  */

  protected void initializeDesktopProperties()
  {
    // Overridden by toolkit implementation?
  }

  /**
   * @since 1.3
   */
  public Map mapInputMethodHighlight(InputMethodHighlight highlight)
  {
      return null;
  }

static synchronized void startDispatch () {
   if ( eventThread == null ) {
      eventThread = new EventDispatchThread( eventQueue);
      eventThread.start();
}

static synchronized void stopDispatch () {
   if ( eventThread != null ) {
      eventThread.stopDispatching();
      eventThread = null;
   }
}

} // class Toolkit
