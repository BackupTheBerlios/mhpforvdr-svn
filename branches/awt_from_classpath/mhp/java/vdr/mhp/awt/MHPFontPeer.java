/* 

Taken and adapted from:

   GdkFontPeer.java -- Implements FontPeer with GTK+
   Copyright (C) 1999, 2004, 2005  Free Software Foundation, Inc.

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
import gnu.java.awt.peer.ClasspathFontPeer;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class MHPFontPeer extends ClasspathFontPeer
{
   //static native void initStaticState();
   //private final int native_state = GtkGenericPeer.getUniqueInteger ();
   //private static ResourceBundle bundle;
  
   /*
   static 
   {
      
      if (Configuration.INIT_LOAD_LIBRARY)
      {
         System.loadLibrary("gtkpeer");
      }
      

      initStaticState ();

      
      try
      {
         bundle = ResourceBundle.getBundle ("gnu.java.awt.peer.gtk.font");
      }
      catch (Throwable ignored)
      {
         bundle = null;
      }
      
   }
   */

   //private native void initState ();
   private native long setFont (String family, int style, int size);
   private native void removeRef (long nativeData);

   private native void getFontMetrics(long nativeData, double [] metrics);
   private native void getTextMetrics(long nativeData, String str, double [] metrics);

   long nativeData;
   
   //called from MHPFontMetrics
   void getFontMetrics(double [] metrics) {
      getFontMetrics(nativeData, metrics);
   }
   void getTextMetrics(String str, double [] metrics) {
      getTextMetrics(nativeData, str, metrics);
   }
   
   protected void finalize ()
   {
      //if (GtkToolkit.useGraphics2D ())
        // MHPGraphics2D.releasePeerGraphicsResource(this);
      dispose();
   }
   
   public void dispose() {
      if (nativeData == 0) {
         removeRef(nativeData);
         nativeData = 0;
      }
   }

  /* 
   * Helpers for the 3-way overloading that this class seems to suffer
   * from. Remove them if you feel like they're a performance bottleneck,
   * for the time being I prefer my code not be written and debugged in
   * triplicate.
  */

   private String buildString(CharacterIterator iter)
   {
      StringBuffer sb = new StringBuffer();
      for(char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) 
         sb.append(c);
      return sb.toString();
   }

   private String buildString(CharacterIterator iter, int begin, int limit)
   {
      StringBuffer sb = new StringBuffer();
      int i = 0;
      for(char c = iter.first(); c != CharacterIterator.DONE; c = iter.next(), i++) 
      {
         if (begin <= i)
            sb.append(c);
         if (limit <= i)
            break;
      }
      return sb.toString();
   }
  
   private String buildString(char[] chars, int begin, int limit)
   {
      return new String(chars, begin, limit - begin);
   }

   /* Public API */

   public MHPFontPeer (String name, int style)
   {
    // All fonts get a default size of 12 if size is not specified.
      this(name, style, 12);
   }

   public MHPFontPeer (String name, int style, int size)
   {  
      super(name, style, size);    
      //initState ();
      setFont (this.familyName.toLowerCase(), this.style, (int)this.size);
   }

   public MHPFontPeer (String name, Map attributes)
   {
      super(name, attributes);
      //initState ();
      setFont (this.familyName.toLowerCase(), this.style, (int)this.size);
   }
  
   public String getSubFamilyName(Font font, Locale locale)
   {
      return null;
   }

   public String getPostScriptName(Font font)
   {
      return null;
   }

   public boolean canDisplay (Font font, char c)
   {
    // FIXME: inquire with pango
      return true;
   }

   public int canDisplayUpTo (Font font, CharacterIterator i, int start, int limit)
   {
    // FIXME: inquire with pango
      return -1;
   }
  
   private native MHPGlyphVector getGlyphVector(String txt, 
         Font f, 
         FontRenderContext ctx);

   public GlyphVector createGlyphVector (Font font, 
                                         FontRenderContext ctx, 
                                         CharacterIterator i)
   {
      return getGlyphVector(buildString (i), font, ctx);
   }

   public GlyphVector createGlyphVector (Font font, 
                                         FontRenderContext ctx, 
                                         int[] glyphCodes)
   {
      return null;
    //    return new MHPGlyphVector (font, this, ctx, glyphCodes);
   }

   public byte getBaselineFor (Font font, char c)
   {
      throw new UnsupportedOperationException ();
   }

   protected class MHPFontLineMetrics extends LineMetrics
   {
      FontMetrics fm;
      int nchars; 

      public MHPFontLineMetrics (FontMetrics m, int n)
      {
         fm = m;
         nchars = n;
      }

      public float getAscent()
      {
         return (float) fm.getAscent ();
      }
  
      public int getBaselineIndex()
      {
         return Font.ROMAN_BASELINE;
      }
    
      public float[] getBaselineOffsets()
      {
         return new float[3];
      }
    
      public float getDescent()
      {
         return (float) fm.getDescent ();
      }
    
      public float getHeight()
      {
         return (float) fm.getHeight ();
      }
    
      public float getLeading() { return 0.f; }    
      public int getNumChars() { return nchars; }
      public float getStrikethroughOffset() { return 0.f; }    
      public float getStrikethroughThickness() { return 0.f; }  
      public float getUnderlineOffset() { return 0.f; }
      public float getUnderlineThickness() { return 0.f; }

   }

   public LineMetrics getLineMetrics (Font font, CharacterIterator ci, 
                                      int begin, int limit, FontRenderContext rc)
   {
      return new MHPFontLineMetrics (getFontMetrics (font), limit - begin);
   }

   public Rectangle2D getMaxCharBounds (Font font, FontRenderContext rc)
   {
      throw new UnsupportedOperationException ();
   }

   public int getMissingGlyphCode (Font font)
   {
      throw new UnsupportedOperationException ();
   }

   public String getGlyphName (Font font, int glyphIndex)
   {
      throw new UnsupportedOperationException ();
   }

   public int getNumGlyphs (Font font)
   {
      throw new UnsupportedOperationException ();
   }

   public Rectangle2D getStringBounds (Font font, CharacterIterator ci, 
                                       int begin, int limit, FontRenderContext frc)
   {
      MHPGlyphVector gv = getGlyphVector(buildString (ci, begin, limit), font, frc);
      return gv.getVisualBounds();
   }

   public boolean hasUniformLineMetrics (Font font)
   {
      return true;
   }

   public GlyphVector layoutGlyphVector (Font font, FontRenderContext frc, 
                                         char[] chars, int start, int limit, 
                                         int flags)
   {
      int nchars = (limit - start) + 1;
      char[] nc = new char[nchars];

      for (int i = 0; i < nchars; ++i)
         nc[i] = chars[start + i];

      return createGlyphVector (font, frc, 
                                new StringCharacterIterator (new String (nc)));
   }

   public LineMetrics getLineMetrics (Font font, String str, 
                                      FontRenderContext frc)
   {
      return new MHPFontLineMetrics (getFontMetrics (font), str.length ());
   }

   public FontMetrics getFontMetrics (Font font)
   {
      return new MHPFontMetrics (font);
   }

}
