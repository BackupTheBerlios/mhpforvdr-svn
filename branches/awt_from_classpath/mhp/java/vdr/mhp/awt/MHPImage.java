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
      getRGBRegion(nativeData, 0, 0, width, height, data, 0, width);
      return data;
  }
      native static int getRGB( long nativeData, int x, int y);
      native static void getRGBRegion( long nativeData, int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize);

  /**
   * Sets the pixel data from a java array.
   */
  private void setPixels(int[] pixels) {
      setRGBRegion(nativeData, 0, 0, width, height, pixels, 0, width);
  }
      native static void setRGB( long nativeData, int x, int y, int rgb);
      native static void setRGBRegion( long nativeData, int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize);


  /**
   * Allocates a Gtk Pixbuf or pixmap
   */
  //private native void createPixmap();
      //private native long imgCreateImage( int w, int h);
      private native long createScreenImage( int w, int h);

  /**
   * Frees the above.
   */
  //private native void freePixmap();
      private native void freeImage ( long nativeData );


  /**
   * Sets the pixmap to scaled copy of src image. hints are rendering hints.
   */
  //private native void createScaledPixmap(MHPImage src, int hints);
      private native long stretchBlit( long nativeDataDestination, long nativeDataSource);



//native static int imgGetHeight( long nativeData);

//native static int imgGetWidth( long nativeData);

//native static long getSurface( long nativeData );
native static long getSubImage( long nativeData, int x, int y, int w, int h);

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
  public MHPImage (String filename)
  {
    isLoaded = true;
    observers = null;
    offScreen = false;
    setImageFromProvider(new DFBImageProvider(filename), new Hashtable());
  }

  /**
   * Constructs an empty MHPImage.
   */
  public MHPImage (int width, int height)
  {
    this.width = width;
    this.height = height;
    nativeData = createScreenImage(width, height);
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
    this(width, height);
    //offScreen = false;
    if (src.nativeData == 0)
       throw new IllegalArgumentException();
    stretchBlit(this.nativeData, src.nativeData);
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
    nativeData = createScreenImage(width, height);
    setPixels(pixels);
  }

  /**
   * Callback from the image consumer.
   */
  public void setImage(DFBImageProvider provider, Hashtable properties)
  {
    this.width = provider.getWidth();
    this.height = provider.getHeight();
    props = (properties != null) ? properties : new Hashtable();
    isLoaded = true;
    deliver();
    nativeData = createScreenImage(width, height);
    provider.renderTo(this);
  }
  
   long getNativeSurface() {
      return nativeData;
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
    /*
    if (offScreen)
      return new GdkGraphics(this);
    else
      throw new IllegalAccessError("This method only works for off-screen"
				   +" Images.");
      */
    return MHPNativeGraphics.getImageGraphics(this);
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
	if (nativeData != 0)
           freeImage(nativeData);
	nativeData = 0;
	source.startProduction(new MHPImageConsumer(this, source));
      }
  }

  public void finalize()
  {
    if (isLoaded) {
      if (nativeData != 0)
         freeImage(nativeData);
      nativeData = 0;
    }
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
  
  private void deliverError()
  {
    if (observers != null)
      for(int i=0; i < observers.size(); i++)
	((ImageObserver)observers.elementAt(i)).
	  imageUpdate(this, ImageObserver.ERROR, 0, 0, width, height);

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
  
  
  
  
  
  
  
/*** DVBBufferedImage implementation ***/

//TODO: I do not know whether using the Classpath implementation of BufferedImage
//available in awt/image, java.awt.image.BufferedImage is faster 
//than this hacky, but native approach accessing
//DirectFB's buffer directly. Currently leave it as it is.

//Is it right to throw all these exceptions? Or just do nothing?


    /**
     *	Returns an integer pixel in the default RGB color model
     * and default sRGB colorspace.  Color
     * conversion takes place if the used Sample Model is not 8-bit for each
     * color component There are only 8-bits of
     * precision for each color component in the returned data when using
     * this method. Note that whan a lower precission is used in this buffered
     * image getRGB may return different values than those used in setRGB()
     *     @param x,&nbsp;y the coordinates of the pixel from which to get
     * the pixel in the default RGB color model and sRGB color space
     * @return an integer pixel in the default RGB color model and
     * default sRGB colorspace.
     * @since MHP 1.0
     */
    public int getRGB( int x, int y ) {
        if (x>=width || y>=height || x<0 || y<0)
           throw new IllegalArgumentException();
        if (!isLoaded)
           throw new IllegalStateException();
        return getRGB(nativeData, x, y);
    }

    /**
     * Returns an array of integer pixels in the default RGB color model
     * (TYPE_INT_ARGB) and default sRGB color space,
     * from a portion of the image data. There are only 8-bits of precision for
     * each color component in the returned data when
     * using this method.  With a specified coordinate (x,&nbsp;y) in the
     * image, the ARGB pixel can be accessed in this way: <pre>
     *    pixel   = rgbArray[offset + (y-startY)*scansize + (x-startX)];
     * </pre>
     * @param startX,&nbsp; startY the starting coordinates
     * @param w           width of region
     * @param h           height of region
     * @param rgbArray    if not <code>null</code>, the rgb pixels
     * are written here
     * @param offset      offset into the <code>rgbArray</code>
     * @param scansize    scanline stride for the <code>rgbArray</code>
     * @return            array of RGB pixels.
     * @exception <code>IllegalArgumentException</code> if an unknown
     * datatype is specified
     * @since MHP 1.0
     */
    public int[] getRGB( int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize ) {
        if (startX+w >= width || startY+h >= height || w<0 || h<0)
            throw new IllegalArgumentException();
        if (!isLoaded)
           throw new IllegalStateException();
        int bufsize=offset+h*scansize+w;
        if (rgbArray.length < bufsize)
            return null;
        if (rgbArray == null)
            rgbArray=new int[bufsize];
        getRGBRegion(nativeData, startX, startY, w, h, rgbArray, offset, scansize);
        return rgbArray;
    }


    /**
     * Sets a pixel in this <code>DVBBufferedImage</code> to the specified
     *   RGB value. The pixel is assumed to be in the default RGB color
     * model, TYPE_INT_ARGB, and default sRGB color space.
     * @param x,&nbsp;y the coordinates of the pixel to set
     * @param rgb the RGB value
     * @since MHP 1.0
     */
    public synchronized void setRGB( int x, int y, int rgb ) {
         if (x>=width || y>=height || x<0 || y<0)
            throw new IllegalArgumentException();
        if (!isLoaded)
           throw new IllegalStateException();
         setRGB(nativeData, x, y, rgb);
    }

    /**
     * Sets an array of integer pixels in the default RGB color model
     * (TYPE_INT_ARGB) and default sRGB color space,
     * into a portion of the image data.   There are only 8-bits of precision for
     * each color component in the returned data when
     * using this method.  With a specified coordinate (x,&nbsp;y) in the
     *   this image, the ARGB pixel can be accessed in this way: <pre>
     *    pixel   = rgbArray[offset + (y-startY)*scansize + (x-startX)];
     * </pre> WARNING: No dithering takes place.
     * @param startX,&nbsp;startY the starting coordinates
     * @param w           width of the region
     * @param h           height of the region
     * @param rgbArray    the rgb pixels
     * @param offset      offset into the <code>rgbArray</code>
     * @param scansize    scanline stride for the <code>rgbArray</code>
     * @since MHP 1.0
     */
    public void setRGB( int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize ) {
        if (startX+w >= width || startY+h >= height || w<0 || h<0)
            throw new IllegalArgumentException();
        if (!isLoaded)
           throw new IllegalStateException();
        setRGBRegion(nativeData, startX, startY, w, h, rgbArray, offset, scansize);        
    }


    /**
     * Returns a subimage defined by a specified rectangular region.
     * The returned <code>DVBBufferedImage</code> shares the same
     * data array as the original image.
     * @param x,&nbsp;y the coordinates of the upper-left corner of the
     * specified rectangular region
     * @param w the width of the specified rectangular region
     * @param h the height of the specified rectangular region
     * @return a <code>DVBBufferdImage</code> that is the subimage of this
     * <code>DVBBufferdImage</code>.
     * @exception <code>RasterFormatException</code> if the specified
     * area is not contained within this <code>DVBBufferdImage</code>.
     * @since MHP 1.0
     */
    //Cannot call this getSubimage, because BufferedImage needs this signature.
    public DVBBufferedImage getSubimageDVB( int x, int y, int w, int h ) 
                            throws DVBRasterFormatException {
        if (x<width || y<height || x<0 || y<0)
            throw new IllegalArgumentException();
        if (!isLoaded)
           throw new IllegalStateException();
        Image ret=new DVBBufferedImage();
        ret.nativeData = getSubImage( nativeData, x, y, w, h );
        ret.width = w;
        ret.height = h;
        ret.flags = READY | SCREEN;
        return ret;
    }

}
