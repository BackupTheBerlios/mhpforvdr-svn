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
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

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
   static {
      initStaticState();
      fontDir = getFontDir();
      // buildFontFilePath is very private. Do not use a logical font name such as SansSerif for this.
      // familyNameToAvailableFont is the place to know which fonts are available natively, so leave this
      // to the (not available) Tiresias here, will be mapped to available default font.
      defaultFontFile = buildFontFilePath("Tiresias", Font.PLAIN);
   }

   private static native void initStaticState();
   private static native String getFontDir();
   private native long setFont (String filename, int style, int size);
   private native void removeRef (long nativeData);

   private native void getFontMetrics(long nativeData, double [] metrics);
   private native void getTextMetrics(long nativeData, String str, double [] metrics);

   long nativeData;
   private static String fontDir;
   private static String defaultFontFile;
   
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
   
   public synchronized void dispose() {
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
   
  /*
   * The 3 names of this font. all fonts have 3 names, some of which
   * may be equal:
   *
   * logical -- name the font was constructed from
   * family  -- a designer or brand name (Helvetica)
   * face -- specific instance of a design (Helvetica Regular)
   *
   * Confusingly, a Logical Font is a concept unrelated to
   * a Font's Logical Name. 
   *
   * A Logical Font is one of 6 built-in, abstract font types
   * which must be supported by any java environment: SansSerif,
   * Serif, Monospaced, Dialog, and DialogInput. 
   *
   * A Font's Logical Name is the name the font was constructed
   * from. This might be the name of a Logical Font, or it might
   * be the name of a Font Face.
   protected String logicalName;
   protected String familyName;
   protected String faceName;
  */
   /* MHP:
      The embedded font "Tiresias" shall have:
         - the logical name "SansSerif" (for example returned by java.awt.Toolkit.getFontList)
         - the family name "Tiresias" (for example returned by java.awt.Font.getFamily)
         - the font face name "Tiresias PLAIN"
   */

   // Knows which fonts are packaged. (Vera Bitstream)
   private static String familyNameToAvailableFont(String familyName) {
      String lname = familyName.toLowerCase();
      if (lname.equals("tiresias"))
          return "vera";
      else if (lname.equals("courier"))
         return "veramo";
      else if (lname.equals("times"))
         return "veras";
      else
         return lname;
   }
   
   private static String styleToFileSuffix(int style) {
      switch (style) {
         default:
         case Font.PLAIN:
            return "";
         case Font.BOLD:
            return "bd";
         case Font.ITALIC:
            return "it";
         case Font.BOLD | Font.ITALIC:
            return "bi";
      }
   }
   
   private static String buildFontFilePath(String familyName, int style) {
      return fontDir + "/" + familyNameToAvailableFont(familyName) + styleToFileSuffix(style) + ".ttf";
   }
   
   private void setNativeFont() {
      // faceName and familyName as well as style and size have been set,
      // possibly with the help of below functions.
      nativeData = setFont(buildFontFilePath(familyName, style), style, (int)size);
      if (nativeData == 0) {
         // Even one of the vera fonts does not come with italic style, try PLAIN first.
         nativeData = setFont(buildFontFilePath(familyName, Font.PLAIN), Font.PLAIN, (int)size);
         if (nativeData == 0) {
            nativeData = setFont(defaultFontFile, Font.PLAIN, (int)size);
            if (nativeData == 0)
               throw new InternalError("Cannot find default font, no fonts available");
            else
               System.out.println("Font "+logicalName+", "+familyName+", style "+style+", "+buildFontFilePath(familyName, style)+" not available, nor in style PLAIN, resorting to default font.");
         } else
            System.out.println("Font "+logicalName+", "+familyName+", style "+style+" not available, resorting to style PLAIN.");
      }
   }

   // this is static, hides implementation from ClasspathFontPeer
   protected static String logicalFontNameToFaceName (String name)
   {
      String uname = name.toUpperCase ();
      if (uname.equals("SANSSERIF"))
         return "Tiresias Plain";
      else if (uname.equals ("SERIF"))
         return "Times Plain";
      else if (uname.equals ("MONOSPACED"))
         return "Courier Plain";
      else if (uname.equals ("DIALOG"))
         return "Tiresias Plain";
      else if (uname.equals ("DIALOGINPUT"))
         return "Tiresias Plain";
      else
         return "Tiresias Plain";
   }

   // this is static, hides implementation from ClasspathFontPeer
   protected static String faceNameToFamilyName (String faceName)
   {
      String name = null;

      StringTokenizer st = new StringTokenizer(faceName, "- ");
      while (st.hasMoreTokens())
      {
         String token = st.nextToken();
         if (name == null)
         {
            name = token;
            break;
         }
      }
      return name;
   }

   // overridden from ClasspathFontPeer
   protected void setStandardAttributes (String name, String family, int style, 
                                         float size, AffineTransform trans)
   {
      this.logicalName = name;

      if (isLogicalFontName (name))
         this.faceName = logicalFontNameToFaceName (name);
      else
         this.faceName = name;

      if (family != null)
         this.familyName = family;
      else
         this.familyName = faceNameToFamilyName (faceName);
    
      this.style = style;
      this.size = size;
      this.transform = trans;
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
      setNativeFont();
   }

   public MHPFontPeer (String name, Map attributes)
   {
      // superclass cares for translating attributes -> style, size
      super(name, attributes);
      //initState ();
      setNativeFont();
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
