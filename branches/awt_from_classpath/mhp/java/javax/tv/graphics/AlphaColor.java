package javax.tv.graphics;

import java.awt.Color;

public class AlphaColor extends Color {

    public AlphaColor( float r, float g, float b, float a ) {
        super( r, g, b, a);
    }

    public AlphaColor( int r, int g, int b, int a ) {
        super( r, g, b, a);
    }

    public AlphaColor( int argb, boolean hasAlpha ) {
        super( argb, hasAlpha );

    }

    public AlphaColor( Color c ) {
       super(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

}

