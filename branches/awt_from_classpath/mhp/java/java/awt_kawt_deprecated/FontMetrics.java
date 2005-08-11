/**
 * Component - abstract root of all widgets Copyright (c) 1998
 * Transvirtual Technologies, Inc.  All rights reserved.
 * See the file "license.terms" for information on usage and
 * redistribution of this file.
 * @author P.C.Mehlitz
 */

package java.awt;

import java.io.Serializable;
import java.util.Hashtable;

import org.apache.log4j.Category;

public class FontMetrics implements Serializable {
    static final Category CAT = Category.getInstance( FontMetrics.class );
    transient int nativeData;
    protected String fontSpec;
    transient int height;
    transient int descent;
    transient int ascent;
    transient int leading;
    transient int maxAdvance;
    transient int maxDescent;
    transient int maxAscent;
    transient int fixedWidth;
    transient int[] widths;
    transient boolean isWideFont;
    static transient Hashtable cache = new Hashtable();
    final private static long serialVersionUID = 1681126225205050147L;

    FontMetrics( Font font ) {
        fontSpec = font.encode();
        nativeData = Toolkit.fntInitFontMetrics( font.nativeData );
        height = Toolkit.fntGetHeight( nativeData );
        descent = Toolkit.fntGetDescent( nativeData );
        maxDescent = Toolkit.fntGetMaxDescent( nativeData );
        ascent = Toolkit.fntGetAscent( nativeData );
        leading = Toolkit.fntGetLeading( nativeData );
        maxAscent = Toolkit.fntGetMaxAscent( nativeData );
        maxAdvance = Toolkit.fntGetMaxAdvance( nativeData );
        fixedWidth = Toolkit.fntGetFixedWidth( nativeData );
    // we defer the widths init because it is rarely used and quite expensive
        isWideFont = Toolkit.fntIsWideFont( nativeData );
    }

    public int bytesWidth( byte data[], int off, int len ) {
        CAT.debug( "byteswidth" );
        if ( fixedWidth != 0 ) {
            return len * fixedWidth;
        } else if ( !isWideFont ) {
            int i, w, n = off + len;
            if ( widths == null ) {
                widths = Toolkit.fntGetWidths( nativeData );
            }
            try {
                for ( i = off, w = 0; i < n; i++ ) {
                    w += widths[data[i]];
                }
            } catch ( ArrayIndexOutOfBoundsException x ) {
                return 0;
            }
            return w;
        } else {
            return Toolkit.fntBytesWidth( nativeData, data, off, len );
        }
    }

    public int charWidth( char c ) {
        int retval = charWidth2( c );
        if ( CAT.isDebugEnabled() ) {
            CAT.debug( "querying charwidth for: <" + c +
                "> width = " + retval );
        }
        return retval;
//    if ( fixedWidth != 0 )
//      return fixedWidth;
//    else if ( c < 256 ){
//      if ( widths == null ) {
//	widths = Toolkit.fntGetWidths( nativeData);
//      }
//      if(widths != null)    // added by joe@convergence.de
//	  return widths[c];
//      else {
//          CAT.warn("using default char width");
//	  return 10;
//      }
//    }
//    else
//      return Toolkit.fntCharWidth( nativeData, c);
    }

    private int charWidth2( char c ) {
        if ( fixedWidth != 0 ) {
            return fixedWidth;
        } else if ( c < 256 ) {
            if ( widths == null ) {
                widths = Toolkit.fntGetWidths( nativeData );
            }
            if ( widths != null ) // added by joe@convergence.de
                {
                    return widths[c];
            } else {
                CAT.warn( "using default char width" );
                return 10;
            }
        } else {
            return Toolkit.fntCharWidth( nativeData, c );
        }
    }

    public int charWidth( int c ) {
        return charWidth( ( char )c );
    }

    public int charsWidth( char data[], int off, int len ) {
        if ( fixedWidth != 0 ) {
            return len * fixedWidth;
        } else if ( !isWideFont ) {
            int i, w, n = off + len;
            if ( widths == null ) {
                widths = Toolkit.fntGetWidths( nativeData );
            }
            try {
                for ( i = off, w = 0; i < n; i++ ) {
                    if ( data[i] >= widths.length ) {
                        char c = translateChar( data[i] );
                        if ( CAT.isDebugEnabled() );
                        CAT.debug( "bad char '" + data[i] +
                            "', translated to '" + c + "'" );
                        w += widths[c];
                    } else {
                        w += widths[data[i]];
                    }
                }
            } catch ( ArrayIndexOutOfBoundsException x ) {
                CAT.debug( "Bad char, will use width of 'm'!", x );
                if ( 'm' < widths.length ) {
                    return widths['m'];
                } else {
                    CAT.error( "Panic. Don't know what charwidth to use" );
                }
                return 0;
            }
            return w;
        } else {
            return Toolkit.fntCharsWidth( nativeData, data, off, len );
        }
    }

    private char translateChar( char src ) {
        switch ( src ) {
            case 'ä':
                return 'a';
            case 'ö':
                return 'o';
            case 'ü':
                return 'u';
            case 'Ä':
                return 'A';
            case 'Ö':
                return 'O';
            case 'Ü':
                return 'U';
            case 'ß':
                return 'B';
            default:
                CAT.warn( "non-translatable char: '" + src +
                    "', returning default 'm'" );
                return 'm';
        }
    }

    protected void finalize() throws Throwable {
        if ( nativeData != 0 ) {
            Toolkit.fntFreeFontMetrics( nativeData );
            nativeData = 0;
        }
        super.finalize();
    }

    public int getAscent() {
        return ascent;
    }

    public int getDescent() {
        return descent;
    }

    public Font getFont() {
        return Font.decode( fontSpec );
    }

    public static FontMetrics getFontMetrics( Font font ) {
        String key = font.encode();
        FontMetrics metrics = ( FontMetrics )cache.get( key );
        if ( metrics == null ) {
            metrics = new FontMetrics( font );
            cache.put( key, metrics );
        }
        return metrics;
    }

    public int getHeight() {
        return height;
    }

    public int getLeading() {
        return leading;
    }

    public int getMaxAdvance() {
        return maxAdvance;
    }

    public int getMaxAscent() {
        return maxAscent;
    }

    /**
     * @deprecated, use getMaxDescent()
     */
    public int getMaxDecent() {
        return ( getMaxDescent() );
    }

    public int getMaxDescent() {
        return maxDescent;
    }

    public int[] getWidths() {
        if ( widths == null ) {
            widths = Toolkit.fntGetWidths( nativeData );
        }
        return widths;
    }

    public int stringWidth( String s ) {
	int width = -1;

	try
 	{
	    width = Toolkit.fntStringWidth( nativeData, s );
	} catch (RuntimeException e)
        {
            System.out.println("java.awt.FontMetrics: Cought "+e);
            e.printStackTrace();
        }

        return width;
    }

    public String toString() {
        return getClass().getName() + " [" + fontSpec + ']';
    }
}

