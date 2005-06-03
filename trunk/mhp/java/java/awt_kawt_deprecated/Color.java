package java.awt;

import java.io.Serializable;

/**
 * Color - class to represent 8-8-8 RGB color values Copyright (c) 1998
 * Transvirtual Technologies Inc.  All rights reserved.
 * See the file "license.terms" for information on usage and
 * redistribution of this file.
 * @author P.C.Mehlitz
 */
public class Color implements Serializable {
    /* XXX implement serial form! */
    public int rgbValue;
    public int nativeValue = 0xffffffff;
    Color brighter;
    Color darker;
    final public static Color darkGray =
        new Color( ( byte )64, ( byte )64, ( byte )64 );
    final public static Color black =
        new Color( ( byte )0, ( byte )0, ( byte )0 );
    final public static Color red =
        new Color( ( byte )255, ( byte )0, ( byte )0 );
    final public static Color pink =
        new Color( ( byte )255, ( byte )175, ( byte )175 );
    final public static Color orange =
        new Color( ( byte )255, ( byte )200, ( byte )0 );
    final public static Color yellow =
        new Color( ( byte )255, ( byte )255, ( byte )0 );
    final public static Color green =
        new Color( ( byte )0, ( byte )255, ( byte )0 );
    final public static Color magenta =
        new Color( ( byte )255, ( byte )0, ( byte )255 );
    final public static Color cyan =
        new Color( ( byte )0, ( byte )255, ( byte )255 );
    final public static Color blue =
        new Color( ( byte )0, ( byte )0, ( byte )255 );
    final public static Color white =
        new Color( ( byte )255, ( byte )255, ( byte )255 );
    final public static Color lightGray =
        new Color( ( byte )192, ( byte )192, ( byte )192 );
    final public static Color gray =
        new Color( ( byte )128, ( byte )128, ( byte )128 );
    final private static long serialVersionUID = 118526816881161077L;

    static {
    // Make sure all the static fields which have not been initialized completely
    // (because of the Color->Toolkit->Defaults cyclic classinit problem) now get their
    // native values
    // Defaults has to be initialized at least up to its 'RgbRequests' field
    // BEFORE we call Toolkit.clrGetPixelValue(), since the first call of this
    // native method queries Defaults.RgbRequests (as part of a PseudoColor init).
    // If the native side kicks off the Defaults clinit, this (probably)
    // ends up in Defaults.clinit calling Color ctors before getting RgbRequests,
    // i.e. before it finishes color mapping. This would cause recursive
    // native color init
        try {
            Class.forName( "java/awt/Defaults" );
        } catch ( Exception _ ) {
        }
        gray.setNativeValue();
        darkGray.setNativeValue();
        black.setNativeValue();
        red.setNativeValue();
        pink.setNativeValue();
        orange.setNativeValue();
        yellow.setNativeValue();
        green.setNativeValue();
        magenta.setNativeValue();
        cyan.setNativeValue();
        blue.setNativeValue();
        white.setNativeValue();
        lightGray.setNativeValue();
    }

    private Color( byte r, byte g, byte b ) {
    // This is a iplementation quirk to overcome the Color->Toolkit->Defaults->Color
    // cyclic class init problem, which would end up in Defaults colors being still null
    // (all static Color field refs) because Color hasn't been initialized yet. This should
    // NOT be used outside of the Color initialization!! We accept this temp. inconsistency
    // of color objects only because it would be worse to keep  RgbRequests (which are
    // subsequently used throughout - and only in - Defaults) outside Defaults.
    // Make sure all objects initialized with this ctor are 'finished' with setNativeValue()
    // before they are used (e.g. in the static block)
        rgbValue = 0xff000000 | ( ( r & 0xff ) << 16 ) |
            ( ( g & 0xff ) << 8 ) | ( b & 0xff );
    }

    public Color( float r, float g, float b ) {
        rgbValue = 0xff000000 | // const alpha channel
            ( ( ( int )( r * 255 ) & 0xff ) << 16 ) |
            ( ( ( int )( g * 255 ) & 0xff ) << 8 ) |
            ( ( int )( b * 255 ) & 0xff );
      // NO: this looks expensive if done for each color
        nativeValue = Toolkit.clrGetPixelValue( rgbValue );
    }

    public Color( int rgb ) {
        rgbValue = rgb | 0xff000000;
        nativeValue = Toolkit.clrGetPixelValue( rgbValue );
    }

    public Color( int r, int g, int b ) {
        rgbValue = 0xff000000 | ( ( r & 0xff ) << 16 ) |
            ( ( g & 0xff ) << 8 ) | ( b & 0xff );
        nativeValue = Toolkit.clrGetPixelValue( rgbValue );
    }

    public Color ( int r, int g, int b, int a ) {
        rgbValue = ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8)  | (b & 0xff);

        nativeValue = Toolkit.clrGetPixelValue( rgbValue);
    }

    public Color( long pixRgb ) {
        rgbValue = ( int )( pixRgb & 0xffffffff );
        nativeValue = Toolkit.clrGetPixelValue( rgbValue );
    }

    /**
     * @rework
     */
    public static int HSBtoRGB( float hue, float sat, float bri ) {
        int r, g, b, hi, bi, x, y, z;
        float hfrac;
        if ( bri == 0.0 ) {
            return 0xff000000;
        } else if ( sat == 0.0 ) {
            r = ( int )( bri * 255 + 0.5f );
            return ( ( r << 16 ) | ( r << 8 ) | r ) | 0xff000000;
        } else {
            hue *= 6.0f; // remove scaling
            hi = ( int )Math.floor( hue );
            if ( hi == 6 ) {
                hi = 0;
            } // 360� == 0�
            hfrac = hue - hi;
            bri *= 255;
            bi = ( int )( bri + 0.5f );
            x = ( int )( ( 1 - sat ) * bri + 0.5f );
            y = ( int )( ( 1 - sat * hfrac ) * bri + 0.5f );
            z = ( int )( ( 1 - sat * ( 1 - hfrac ) ) * bri + 0.5f );
            switch ( hi ) {
                case 0:
                    r = bi;
                    g = z;
                    b = x;
                break;
                case 1:
                    r = y;
                    g = bi;
                    b = x;
                break;
                case 2:
                    r = x;
                    g = bi;
                    b = z;
                break;
                case 3:
                    r = x;
                    g = y;
                    b = bi;
                break;
                case 4:
                    r = z;
                    g = x;
                    b = bi;
                break;
                case 5:
                    r = bi;
                    g = x;
                    b = y;
                break;
                default:
                    r = 0;
                    g = 0;
                    b = 0; // can't be, hue is wrapped
            }
            return ( ( r << 16 ) | ( g << 8 ) | b ) | 0xff000000;
        }
    }

    /**
     * @rework
     */
    public static float[] RGBtoHSB( int r, int g, int b, float[] hsb ) {
        float min, max, dif, rf, gf, bf;
        rf = ( float )r / 255.0f;
        gf = ( float )g / 255.0f;
        bf = ( float )b / 255.0f;
        if ( hsb == null ) {
            hsb = new float[3];
        }
        if ( r > g ) { // get maximum/minimum color component
            if ( r > b ) {
                max = rf;
            } else {
                max = bf;
            }
            if ( b < g ) {
                min = bf;
            } else {
                min = gf;
            }
        } else {
            if ( b > g ) {
                max = bf;
            } else {
                max = gf;
            }
            if ( r < b ) {
                min = rf;
            } else {
                min = bf;
            }
        }
        hsb[2] = max; // value / brightness
        if ( max > 0 ) {
            dif = max - min;
            hsb[1] = dif / max; // saturation
            if ( dif > 0 ) {
                if ( max == rf ) // hue is scaled
                {
                    hsb[0] = ( ( gf - bf ) / dif ) / 6.0f;
                } else if ( max == gf ) {
                    hsb[0] = ( 2.0f + ( bf - rf ) / dif ) / 6.0f;
                } else {
                    hsb[0] = ( 4.0f + ( rf - gf ) / dif ) / 6.0f;
                }
                if ( hsb[0] < 0 ) {
                    hsb[0] += 1.0f;
                } // wrap hue around 360�
            } else { // we don't want NaNs
                hsb[0] = 0.0f;
            }
        } else { // all black (0.0)
            hsb[0] = 0.0f;
            hsb[1] = 0.0f;
        }
        return hsb;
    }

    public Color brighter() {
        if ( brighter == null ) {
      // we need to do this native because we don't know what native visual we use
      // (could be VGA 16 PseudoColor, which isn't realy suitable for arithmetic RGB ops)
            brighter = new Color( Toolkit.clrBright( rgbValue ) );
        }
        return brighter;
    }

    public Color darker() {
        if ( darker == null ) {
      // we need to do this native because we don't know what native visual we use
      // (could be VGA 16 PseudoColor, which isn't realy suitable for arithmetic RGB ops)
            darker = new Color( Toolkit.clrDark( rgbValue ) );
        }
        return darker;
    }

    public static Color decode( String s ) throws NumberFormatException {
        return new Color( ( Integer.decode( s ) ).intValue() );
    }

    public boolean equals( Object obj ) {
        return ( obj instanceof Color ) &&
            ( ( Color )obj ).rgbValue == rgbValue;
    }

    public int getAlpha() {
        return (( rgbValue & 0xff000000 ) >> 24);
    }

    public int getBlue() {
        return ( rgbValue & 0x0000ff );
    }

    public static Color getColor( String s ) {
        return getColor( s, ( Color )null );
    }

    public static Color getColor( String s, Color defClr ) {
        Integer rgb = Integer.getInteger( s );
        if ( rgb == null ) {
            return defClr;
        } else {
            return new Color( rgb.intValue() );
        }
    }

    public static Color getColor( String s, int defRgb ) {
        Integer rgb = Integer.getInteger( s );
        if ( rgb == null ) {
            return new Color( defRgb );
        } else {
            return new Color( rgb.intValue() );
        }
    }

    public int getGreen() {
        return ( ( rgbValue & 0x00ff00 ) >> 8 );
    }

    public static Color getHSBColor( float h, float s, float b ) {
        return new Color( HSBtoRGB( h, s, b ) );
    }

    public int getRGB() {
        return rgbValue;
    }

    public int getRed() {
        return ( ( rgbValue & 0xff0000 ) >> 16 );
    }

    public int hashCode() {
        return rgbValue;
    }

    void setNativeValue() {
        nativeValue = Toolkit.clrGetPixelValue( rgbValue );
    }

    public String toString() {
        return "Color [r=" + getRed() + ",g=" + getGreen() + ",b=" +
            getBlue() + "]";
    }
}

