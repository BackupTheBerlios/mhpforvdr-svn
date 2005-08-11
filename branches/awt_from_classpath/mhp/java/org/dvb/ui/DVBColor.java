package org.dvb.ui;

import java.awt.Color;
import java.awt.Toolkit;

import javax.tv.graphics.AlphaColor;

/**
 *	Java Stubs Generated by Doc2Java (c) Immo Benjes, IfN, TU Braunschweig
 * Doc2Java is a tool which generates Java stubs out of Javadoc HTML pages
 * (Version 1.2 only) Last change:  IB   21 Jan 100    1:03 pm
 */

/*import java.awt.Color;*/

/*import org.davic.awt.Color;*/

/**
 *	A Color class which adds the notion of alpha. It is compatible with the
 * JDK1.2 java.awt.Color class DVBColor extends org.davic.awt.Color which
 * extends java.awt.Color.	In implementations using the JDK1.1 this class
 * adds support for alpha In implementations using the JDK1.2 the additional
 * methods would just call super. Because DVBColor extends Color the
 * signatures in the existing classes do not change. Classes
 * like Component should work with DVBColor internaly.
 * @since MHP 1.0
 */

/* public class DVBColor extends org.davic.awt.Color */

public class DVBColor extends AlphaColor {

    /**
     *	Creates an sRGB color with the specified red, green, blue, and
     * alpha values in the range (0.0 - 1.0).  The actual color
     * used in rendering will depend on finding the best match given the
     * color space available for a given output device.
     *	@param r - the red componentg - the green componentb - the blue
     * componenta - the alpha component
     *	@see java.awt.Color#getRed()
     *	@see  java.awt.Color#getGreen()
     *	@see  java.awt.Color#getBlue()
     *	@see  #getAlpha()
     *	@see  #getRGB()
     */
    public DVBColor( float r, float g, float b, float a ) {
        super( r, g, b, a );
    }

    /**
     *	Creates an sRGB color with the specified red, green, blue, and alpha
     * values in the range (0 - 255).
     *	@param r - the red componentg - the green componentb - the blue
     * componenta - the alpha component
     *	@see java.awt.Color#getRed()
     *	@see  java.awt.Color#getGreen()
     *	@see  java.awt.Color#getBlue()
     *	@see  #getAlpha()
     *	@see  #getRGB()
     */
    public DVBColor( int r, int g, int b, int a ) {
        super( r, g, b, a );
    }

    /**
     *	Creates an sRGB color with the specified combined RGBA value consisting
     * of the alpha component in bits 24-31, the red component in bits 16-23,
     * the green component in bits 8-15, and the blue component in bits 0-7.
     * If the hasalpha argument is False, alpha is defaulted to 255.
     *	@param argb - the combined RGBA componentshasalpha - true if the alpha
     * bits are valid
     *	@param  hasalpha otherwise
     *	@see java.awt.Color#getRed()
     *	@see  java.awt.Color#getGreen()
     *	@see  java.awt.Color#getBlue()
     *	@see  #getAlpha()
     *	@see  #getRGB()
     */
    public DVBColor( int argb, boolean hasalpha ) {
        super( argb, hasalpha );
    }

    /**
     *	Constructs a new DVBColor using the specified java.awt.Color. If c is
     * a JDK1.1 color alpha will be set to 1.0 (opaque), if c is a JDK1.2
     * color the alpha of c will be used.
     */
    public DVBColor( Color c ) {
        super( c );
    }

    public DVBColor( long pixRgb ) {
        super( ( int )pixRgb, ( ( ( ( int )( pixRgb & 0xff000000 ) ) >>
            24 ) > 0 ) );
    }

}

