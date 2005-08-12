/* 

Taken and adapted from:

MHPImage.java
   Copyright (C) 2005 Free Software Foundation, Inc.

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

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
//import gnu.classpath.RawData;

/**
 * MHPImage - wraps a GdkPixbuf or GdkPixmap.
 *
 * The constructor MHPImage(int, int) creates an 'off-screen' GdkPixmap,
 * this can be drawn to (it's a GdkDrawable), and correspondingly, you can
 * create a GdkGraphics object for it. 
 *
 * This corresponds to the Image implementation returned by 
 * Component.createImage(int, int). 
 *
 * A GdkPixbuf is 'on-screen' and the gdk cannot draw to it,
 * this is used for the other constructors (and other createImage methods), and
 * corresponds to the Image implementations returned by the Toolkit.createImage
 * methods, and is basically immutable. 
 *
 * @author Sven de Marothy
 */
public class MHPImage extends Image
{
  int width = -1, height = -1;

  /**
   * Properties.
   */
  Hashtable props;

  /**
   * Loaded or not flag, for asynchronous compatibility.
   */
  boolean isLoaded;

  /**
   * Pointer to the GdkPixbuf
   */
  //RawData pixmap;
  long nativeData;

  /**
   * Observer queue.
   */
  Vector observers;

  /**
   * If offScreen is set, a GdkBitmap is wrapped and not a Pixbuf.
   */
  boolean offScreen;

  /**
   * Original source, if created from an ImageProducer.
   */
  ImageProducer source;

  /*
   * DirectFB's ARGB color format
   */
  static ColorModel nativeModel = new DirectColorModel(32, 
						       0x00FF0000,
						       0x0000FF00,
						       0x000000FF,
						       0xFF000000);

  /**
   * Returns a copy of the pixel data as a java array.
   */
  private int[] getPixels() {
      int[] data = newe int[nativeModel.getPixelSize() * width * height];
      imgGetRGBRegion(nativeData, 0, 0, width, height, data, 0, width);
      return data;
  }
      native static int imgGetRGB( long nativeData, int x, int y);
      native static void imgGetRGBRegion( long nativeData, int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize);

  /**
   * Sets the pixel data from a java array.
   */
  private void setPixels(int[] pixels) {
      imgSetRGBRegion(nativeData, 0, 0, width, height, pixels, 0, width);
  }
      native static void imgSetRGB( long nativeData, int x, int y, int rgb);
      native static void imgSetRGBRegion( long nativeData, int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize);

  /**
   * Loads an image using gdk-pixbuf.
   */
  //private native boolean loadPixbuf(String name);
      private native long imgCreateFromFileLocalEncoding(byte[] fileName);

  /**
   * Allocates a Gtk Pixbuf or pixmap
   */
  //private native void createPixmap();
      private native long imgCreateImage( int w, int h);
      private native long imgCreateScreenImage( int w, int h);

  /**
   * Frees the above.
   */
  private native void freePixmap();
      native static void imgFreeImage ( long nativeData );


  /**
   * Sets the pixmap to scaled copy of src image. hints are rendering hints.
   */
  //private native void createScaledPixmap(MHPImage src, int hints);
      private native long imgCreateScaledImage( long nativeData, int w, int h, int hints);



native static long imgCreateFromData( byte[] buf, int offset, int len);
native static int imgGetHeight( long nativeData);

native static int imgGetWidth( long nativeData);

native static long imgGetSurface( long nativeData );

native static long imgGetSubImage( long nativeData, int x, int y, int w, int h);

  /**
   * Constructs a MHPImage from an ImageProducer. Asynchronity is handled in
   * the following manner: 
   * A MHPImageConsumer gets the image data, and calls setImage() when 
   * completely finished. The MHPImage is not considered loaded until the
   * MHPImageConsumer is completely finished. We go for all "all or nothing".
   */
  public MHPImage (ImageProducer producer)
  {
    isLoaded = false;
    observers = new Vector();
    source = producer;
    source.startProduction(new MHPImageConsumer(this, source));
    offScreen = false;
  }

  /**
   * Constructs a MHPImage by loading a given file.
   *
   * @throws IllegalArgumentException if the image could not be loaded.
   */
  /*
  public MHPImage (String filename)
  {
    File f = new File(filename);
    try
      {
        nativeData = imgCreateFromFile(encodeNative(f.getCanonicalPath()));
	if (nativeData == 0)
	  throw new IllegalArgumentException("Couldn't load image: "+filename);
      } 
    catch(IOException e)
      {
	  throw new IllegalArgumentException("Couldn't load image: "+filename);
      }
    
    width = imgGetWidth(nativeData);
    height = imageGetHeight(nativeData);

    isLoaded = true;
    observers = null;
    offScreen = false;
    props = new Hashtable();
  }
  */

  /**
   * Constructs an empty MHPImage.
   */
  public MHPImage (int width, int height)
  {
    this.width = width;
    this.height = height;
    nativeData = imgCreateScreenImage(width, height);
    props = new Hashtable();
    isLoaded = true;
    observers = null;
    offScreen = true;
  }

  /**
   * Constructs a scaled version of the src bitmap, using the GDK.
   */
  private MHPImage (MHPImage src, int width, int height, int hints)
  {
    this.width = width;
    this.height = height;
    props = new Hashtable();
    isLoaded = true;
    observers = null;
    offScreen = false;

    // Use the GDK scaling method.
    imgCreateScaledImage(src, width, height, hints);
  }

  /**
   * Callback from the image consumer.
   */
  public void setImage(int width, int height, 
		       int[] pixels, Hashtable properties)
  {
    this.width = width;
    this.height = height;
    props = (properties != null) ? properties : new Hashtable();
    isLoaded = true;
    deliver();
    createPixmap();
    setPixels(pixels);
  }

  // java.awt.Image methods ////////////////////////////////////////////////

  public synchronized int getWidth (ImageObserver observer)
  {
    if (addObserver(observer))
      return -1;

    return width;
  }
  
  public synchronized int getHeight (ImageObserver observer)
  {
    if (addObserver(observer))
      return -1;
    
    return height;
  }

  public synchronized Object getProperty (String name, ImageObserver observer)
  {
    if (addObserver(observer))
      return UndefinedProperty;
    
    Object value = props.get (name);
    return (value == null) ? UndefinedProperty : value;
  }

  /**
   * Returns the source of this image.
   */
  public ImageProducer getSource ()
  {
    if (!isLoaded)
      return null;
    return new MemoryImageSource(width, height, nativeModel, getPixels(), 
				 0, width);
  }

  /**
   * Creates a GdkGraphics context for this pixmap.
   */
  public Graphics getGraphics ()
  {
    if (!isLoaded) 
      return null;
    if (offScreen)
      return new GdkGraphics(this);
    else
      throw new IllegalAccessError("This method only works for off-screen"
				   +" Images.");
  }
  
  /**
   * Returns a scaled instance of this pixmap.
   */
  public Image getScaledInstance(int width,
				 int height,
				 int hints)
  {
    if (width <= 0 || height <= 0)
      throw new IllegalArgumentException("Width and height of scaled bitmap"+
					 "must be >= 0");

    return new MHPImage(this, width, height, hints);
  }

  /**
   * If the image is loaded and comes from an ImageProducer, 
   * regenerate the image from there.
   *
   * I have no idea if this is ever actually used. Since MHPImage can't be
   * instantiated directly, how is the user to know if it was created from
   * an ImageProducer or not?
   */
  public synchronized void flush ()
  {
    if (isLoaded && source != null)
      {
	observers = new Vector();
	isLoaded = false;
	freePixmap();
	source.startProduction(new MHPImageConsumer(this, source));
      }
  }

  public void finalize()
  {
    if (isLoaded)
      freePixmap();
  }

  /**
   * Returns the image status, used by GtkToolkit
   */
  public int checkImage (ImageObserver observer)
  {
    if (addObserver(observer))
      return 0;

    return ImageObserver.ALLBITS | ImageObserver.WIDTH | ImageObserver.HEIGHT;
  }

  // Drawing methods ////////////////////////////////////////////////

  /**
   * Draws an image with eventual scaling/transforming.
   */
  /*
  public boolean drawImage (GdkGraphics g, int dx1, int dy1, int dx2, int dy2, 
			    int sx1, int sy1, int sx2, int sy2, 
			    Color bgcolor, ImageObserver observer)
  {
    if (addObserver(observer))
      return false;

    boolean flipX = (dx1 > dx2)^(sx1 > sx2);
    boolean flipY = (dy1 > dy2)^(sy1 > sy2);
    int dstWidth = Math.abs (dx2 - dx1);
    int dstHeight = Math.abs (dy2 - dy1);
    int srcWidth = Math.abs (sx2 - sx1);
    int srcHeight = Math.abs (sy2 - sy1);
    int srcX = (sx1 < sx2) ? sx1 : sx2;
    int srcY = (sy1 < sy2) ? sy1 : sy2;
    int dstX = (dx1 < dx2) ? dx1 : dx2;
    int dstY = (dy1 < dy2) ? dy1 : dy2;

    // Clipping. This requires the dst to be scaled as well, 
    if (srcWidth > width)
      {
	dstWidth = (int)((double)dstWidth*((double)width/(double)srcWidth));
	srcWidth = width - srcX;
      }

    if (srcHeight > height) 
      {
	dstHeight = (int)((double)dstHeight*((double)height/(double)srcHeight));
	srcHeight = height - srcY;
      }

    if (srcWidth + srcX > width)
      {
	dstWidth = (int)((double)dstWidth * (double)(width - srcX)/(double)srcWidth);
	srcWidth = width - srcX;
      }

    if (srcHeight + srcY > height)
      {
	dstHeight = (int)((double)dstHeight * (double)(width - srcY)/(double)srcHeight);
	srcHeight = height - srcY;
      }

    if ( srcWidth <= 0 || srcHeight <= 0 || dstWidth <= 0 || dstHeight <= 0)
      return true;

    if(bgcolor != null)
      drawPixelsScaledFlipped (g, bgcolor.getRed (), bgcolor.getGreen (), 
			       bgcolor.getBlue (), 
			       flipX, flipY,
			       srcX, srcY,
			       srcWidth, srcHeight,
			       dstX,  dstY,
			       dstWidth, dstHeight,
			       true);
    else
      drawPixelsScaledFlipped (g, 0, 0, 0, flipX, flipY,
			       srcX, srcY, srcWidth, srcHeight,
			       dstX,  dstY, dstWidth, dstHeight,
			       false);
    return true;
  }
  */

  /**
   * Draws an image to the GdkGraphics context, at (x,y) scaled to 
   * width and height, with optional compositing with a background color.
   */
  /*
  public boolean drawImage (GdkGraphics g, int x, int y, int width, int height,
			    Color bgcolor, ImageObserver observer)
  {
    if (addObserver(observer))
      return false;

    if(bgcolor != null)
      drawPixelsScaled(g, bgcolor.getRed (), bgcolor.getGreen (), 
		       bgcolor.getBlue (), x, y, width, height, true);
    else
      drawPixelsScaled(g, 0, 0, 0, x, y, width, height, false);

    return true;
  }
*/
  // Private methods ////////////////////////////////////////////////

  /**
   * Delivers notifications to all queued observers.
   */
  private void deliver()
  {
    int flags = ImageObserver.HEIGHT | 
      ImageObserver.WIDTH |
      ImageObserver.PROPERTIES |
      ImageObserver.ALLBITS;

    if (observers != null)
      for(int i=0; i < observers.size(); i++)
	((ImageObserver)observers.elementAt(i)).
	  imageUpdate(this, flags, 0, 0, width, height);

    observers = null;
  }
  
  /**
   * Adds an observer, if we need to.
   * @return true if an observer was added.
   */
  private boolean addObserver(ImageObserver observer)
  {
    if (!isLoaded)
      {
	if(observer != null)
	  if (!observers.contains (observer))
	    observers.addElement (observer);
	return true;
      }
    return false;
  }
}