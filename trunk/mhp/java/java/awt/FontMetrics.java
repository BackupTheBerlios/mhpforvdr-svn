/**
 * Component - abstract root of all widgets
 *
 * Copyright (c) 1998
 *    Transvirtual Technologies, Inc.  All rights reserved.
 *
 * See the file "license.terms" for information on usage and redistribution
 * of this file.
 *
 * @author P.C.Mehlitz
 */

package java.awt;

import java.util.Hashtable;

public class FontMetrics
  implements java.io.Serializable
{
        // We store the font because the font metrics
        // depends on the native data maintained by the font
        // in this way the original font object will not be
        // finalized (and the native data freed).
        // Note that since fontmetrics instances are cached
        // their life span is higher than the Font object one.
        // (Maurizio De Cecco maurizio@mandrakesoft.com).

        Font font;  
        final private static long serialVersionUID = 1681126225205050147L;

protected FontMetrics ( Font font ) {
        this.font = font;
}

public int bytesWidth ( byte data[], int off, int len ) {
   if (font != null && font.nativeData != 0)
      return 0;
   String s=new String(data, off, len);
   return stringWidth(s);
}

public int charWidth ( char c ) {
   if (font != null && font.nativeData != 0)
      return charWidth(font.nativeData, c);
   else
      return 0;
}

private native int charWidth(long nativeData, char c); 

public int charWidth ( int c ) {
        return charWidth( (char) c);
}

public int charsWidth ( char data[], int off, int len ) {
   if (font != null && font.nativeData != 0)
      return 0;
   String s=new String(data, off, len);
   return stringWidth(s);
}

/*
protected void finalize () throws Throwable {
        if ( nativeData != 0 ) {
                Toolkit.fntFreeFontMetrics( nativeData);
                nativeData = 0;
        }
        super.finalize();
}*/

public int getAscent() {
   if (font != null && font.nativeData != 0)
      return ascent(font.nativeData);
   else
      return 0;
}

private native int ascent(long nativeData); 

public int getDescent() {
   if (font != null && font.nativeData != 0)
      return descent(font.nativeData);
   else
      return 0;
}

private native int descent(long nativeData); 

public Font getFont() {
        return font;
}

static FontMetrics getFontMetrics ( Font font ) {
   return new FontMetrics(font);
}

public int getHeight() {
   if (font != null && font.nativeData != 0)
      return height(font.nativeData);
   else
      return 0;
}

private native int height(long nativeData); 


public int getLeading() {
   return 0; //unimplemented by DirectFB
}

public int getMaxAdvance() {
   if (font != null && font.nativeData != 0)
      return maxAdvance(font.nativeData);
   else
      return 0;
}

private native int maxAdvance(long nativeData); 

public int getMaxAscent() {
   return getAscent(); //no more information implemented in DirectFB
}

/**
 * @deprecated, use getMaxDescent()
 */
public int getMaxDecent() {
   return getDescent(); //no more information implemented in DirectFB
}

public int getMaxDescent() {
   return getDescent(); //no more information implemented in DirectFB
}

public int[] getWidths () {
   if (font != null && font.nativeData != 0)
      return widths(font.nativeData);
   else
      return new int[0];
}

private native int[] widths(long nativeData); 

public int stringWidth ( String s ) {
   if (font != null && s != null && font.nativeData != 0)
      return stringWidth(font.nativeData, s);
   else
      return 0;
}

private native int stringWidth(long nativeData, String s);

public String toString () {
        return getClass().getName() +
               " [" + font.encode() + ']';
}

}
