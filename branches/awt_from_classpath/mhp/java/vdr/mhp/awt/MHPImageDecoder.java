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
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;


public class MHPImageDecoder implements java.awt.image.ImageProducer
{

  Vector consumers = new Vector ();
  String filename;
  URL url;
  byte[] data;
  int offset;
  int length;
  InputStream input;
  
  public MHPImageDecoder (String filename)
  {
    this.filename = filename;
  }

  public MHPImageDecoder (URL url)
  {
    this.url = url;
  }

  public MHPImageDecoder (InputStream is)
  {
    this.input = is;
  }

  public MHPImageDecoder (byte[] imagedata, int imageoffset, int imagelength)
  {
    data = imagedata;
    offset = imageoffset;
    length = imagelength;
  }
  
  
  // --- ImageProducer interface ---

  public void addConsumer (ImageConsumer ic) 
  {
    consumers.addElement (ic);
  }

  public boolean isConsumer (ImageConsumer ic)
  {
    return consumers.contains (ic);
  }
  
  public void removeConsumer (ImageConsumer ic)
  {
    consumers.removeElement (ic);
  }

  public void startProduction (ImageConsumer newIc)
  {
    if (!isConsumer(newIc))
      addConsumer(newIc);

    Vector list = (Vector) consumers.clone ();
    
    DFBDataBuffer buffer = null;
    DFBImageProvider provider = null;
    
    try {
      // Create the data buffer and image provider here rather than in the
      // ImageDecoder constructors so that exceptions cause
      // imageComplete to be called with an appropriate error
      // status.
      
      if (filename != null) {
         //no buffer needed
         provider = new DFBImageProvider(filename);
      } else if (data != null) {
         buffer = new DFBDataBuffer(data, offset, length);
         provider = new DFBImageProvider(buffer);
      } else {
         InputStream stream;
         if (url != null) {
            buffer = new DFBDataBuffer(url.openStream());
         } else if (input != null) {
            buffer = new DFBDataBuffer(input);
         } else
            throw new IllegalArgumentException("Null value");
         provider = new DFBImageProvider(buffer);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      for (int i = 0; i < list.size (); i++)
      {
         ImageConsumer ic = (ImageConsumer) list.elementAt (i);
         ic.imageComplete (ImageConsumer.IMAGEERROR);
      }
      if (provider != null)
         provider.dispose();
      if (buffer != null)
         buffer.dispose();
      return;
    }
    
      // For MHPImages respectively MHPImageConsumers, its helper class,
      // there is a special handling which does everything on the native side.
      // For other consumers, the pixel must be passed through Java.
    
    MHPImage image = null;
    for (int i = 0; i < list.size (); i++) {
       ImageConsumer ic = (ImageConsumer) list.elementAt (i);
       try {
         if (ic instanceof MHPImageConsumer) {
            ((MHPImageConsumer)ic).setImage(provider);
         } else {
            ic.setDimensions (provider.getWidth(), provider.getHeight());
            ic.setColorModel (provider.getColorModel());
            ic.setHints (ImageConsumer.RANDOMPIXELORDER);
            //create temporary image to copy pixels from
            if (image == null)
               image = provider.createImage();
            ic.setPixels (0, 0, image.width, image.height, provider.getColorModel(), image.getPixels(), 0, image.width);
         }
       } catch (Exception e) {
          e.printStackTrace();
          ic.imageComplete(ImageConsumer.IMAGEERROR);
       }
    }
    
    if (provider != null)
       provider.dispose();
    if (buffer != null)
       buffer.dispose();
    
  }

  public void requestTopDownLeftRightResend (ImageConsumer ic) 
  { 
     //TODO?
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

  /*
  public void produce (Vector v, InputStream is) throws IOException
  {
    curr = v;

    DFBDataBuffer buffer = new DFBDataBuffer(is);
    DFBImageProvider provider;
    
    try {
    
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
   */
}
