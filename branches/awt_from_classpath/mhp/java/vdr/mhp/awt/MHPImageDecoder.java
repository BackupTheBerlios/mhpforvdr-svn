/* MHPImageDecoder.java -- Image data decoding object
   Copyright (C) 2003, 2004, 2005  Free Software Foundation, Inc.

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

import gnu.classpath.Configuration;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;


public class MHPImageDecoder extends gnu.java.awt.image.ImageDecoder
{
  
  // the current set of ImageConsumers for this decoder
  Vector curr;

  // interface to GdkPixbuf
  private native long initState ();
  private native void pumpBytes(long nativeProviderData, byte[] bytes, int len);
  private native long pumpDone(long nativeProviderData);
  //private native void finish (long nativeData);
  
  
  //DirectFB's ARGB color format
  static ColorModel cm = new DirectColorModel(32, 
						       0x00FF0000,
						       0x0000FF00,
						       0x000000FF,
						       0xFF000000);
  
  public MHPImageDecoder (InputStream in)
  {
    super (in);
  }

  public MHPImageDecoder (String filename)
  {
    super (filename);
  }
  
  public MHPImageDecoder (URL url)
  {
    super (url);
  }

  public MHPImageDecoder (byte[] imagedata, int imageoffset, int imagelength)
  {
    super (imagedata, imageoffset, imagelength);
  }

  /*
  // called back by native side
  void areaPrepared (int width, int height)
  {

    if (curr == null)
      return;

    for (int i = 0; i < curr.size (); i++)
      {
        ImageConsumer ic = (ImageConsumer) curr.elementAt (i);
        ic.setDimensions (width, height);
        ic.setColorModel (cm);
        ic.setHints (ImageConsumer.RANDOMPIXELORDER);
      }
  }
  
  // called back by native side
  void areaUpdated (int x, int y, int width, int height, 
                    int pixels[], int scansize)
  {
    if (curr == null)
      return;
    
    for (int i = 0; i < curr.size (); i++)
      {
        ImageConsumer ic = (ImageConsumer) curr.elementAt (i);
        ic.setPixels (x, y, width, height, cm, pixels, 0, scansize);
      }
  }
  */
  
  // called from an async image loader of one sort or another, this method
  // repeatedly reads bytes from the input stream and passes them through a
  // GdkPixbufLoader using the native method pumpBytes. pumpBytes in turn
  // decodes the image data and calls back areaPrepared and areaUpdated on
  // this object, feeding back decoded pixel blocks, which we pass to each
  // of the ImageConsumers in the provided Vector.

  public void produce (Vector v, InputStream is) throws IOException
  {
    curr = v;

    byte bytes[] = new byte[4096];
    int len = 0;
    initState();
    while ((len = is.read (bytes)) != -1)
      pumpBytes (bytes, len);
    pumpDone();
    
    for (int i = 0; i < curr.size (); i++)
      {
        ImageConsumer ic = (ImageConsumer) curr.elementAt (i);
        ic.imageComplete (ImageConsumer.STATICIMAGEDONE);
      }

    curr = null;
  }

  public void finalize()
  {
    finish();
  }

}
