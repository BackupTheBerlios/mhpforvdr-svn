package org.dvb.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.Hashtable;

//Implementation (get/setRGB, getSubimage) is done in superclass
//There is a Classpath implementation in awt/image, see comment
//in java.awt.Image


/**
 * The <code>DVBBufferedImage</code> subclass describes an {@link Image} with
 * an accessible buffer of image data.
 * The DVBBufferedImage is an adapter class for java.awt.image.BufferedImage.
 * It supports two different platform dependent sample models TYPE_BASE and
 * TYPE_ADVANCED. Buffered images with the TYPE_BASE have the same sample
 * model as the on screen graphics buffer, thus
 * TYPE_BASE could be CLUT based. TYPE_ADVANCED has a direct color model but
 * it is not specified how many bits are used to store the different color
 * components. By default, a new DVBBufferedImage is transparent. All alpha
 * values are set to 0;
 * @since MHP 1.0
 */
public class DVBBufferedImage extends vdr.mhp.awt.MHPImage {
    /**
     *	Represents an image stored in a best possible SampleModel (platform
     * dependent) The image has a DirectColorModel
     * with alpha. The color data in this image is considered not to be
     * premultiplied with alpha. The data returned by getRGB()
     * will be in the TYPE_INT_ARGB color model that is alpha component in
     * bits 24-31, the red component in bits 16-23,
     * the green component in bits 8-15, and the blue component in bits 0-7.
     * The data for setRGB() shall be
     * in the TYPE_INT_ARGB color model as well.
     * @since MHP 1.0
     */
    public static final int TYPE_ADVANCED = 20; 

    /**
     *	Represents an image stored in a platform dependent Sample Model.
     * This color model is not visible to applications. The data returned by
     * getRGB() will be in the TYPE_INT_ARGB color model that is alpha
     * component in bits 24-31, the red component in bits 16-23,
     * the green component in bits 8-15, and the blue component in bits 0-7.
     * The data for setRGB() shall be
     * in the TYPE_INT_ARGB color model as well.
     *    @since MHP 1.0
     */
    public static final int TYPE_BASE = 21;

    Hashtable properties;
    int type;
    //boolean isAlphaPremultiplied; // If true, alpha has been premultiplied in
    // color channels
    
    //not API
    public DVBBufferedImage() {
    }

    /**
     *	Constructs a DVBBufferedImage with the specified width and height.
     * The Sample Model used the image is the
     * native Sample Model (TYPE_BASE) of the implementation.
     * Note that a request can lead to an java.lang.OutOfMemoryError.
     * Applications should be aware of this.
     *	@param width - width of the created image
     *    @parm height - height of the created image
     *    @since MHP 1.0
     */
    public DVBBufferedImage( int width, int height ) {
        super(width, height);
        type = TYPE_BASE;
    }

    /**
     *    Constructs a new DVBBufferedImage with the specified width and height
     * in the Sample Model specified by typ.
     * Note that a request can lead to an java.lang.OutOfMemoryError.
     * Applications should be aware of this.
     *	@param width - the width of the DVBBufferedImage
     *    @param height - the height of the DVBBufferedImage
     *    @param type - the ColorSpace of the DVBBufferedImage
     *    @since MHP 1.0
     */
    public DVBBufferedImage( int width, int height, int typ ) {
        super(width, height);
        type = typ;
    }

    /* * private DVBBufferedImage(BufferedImage b) * { *    bimg = b; * } */

    /**
     * Creates a <code>DVBGraphics</code>, which can be used to draw into
     * this <code>DVBBufferedImage</code>.
     * @return a <code>DVBGraphics</code>, used for drawing into this image.
     * @since MHP 1.0
     */
    public DVBGraphics createGraphics() {
        return (DVBGraphics)getGraphics();
    }

    /**
     * Flushes all resources being used to cache optimization information.
     * The underlying pixel data is unaffected.
     */
    public void flush() {
        super.flush();

        /* bimg.flush(); */
    }


    /**
     *  In implementations using the JDK1.2 API this returns an BufferedImage
     * cast to a java.awt.Image, in other implementations it returns this
     * DVBBufferedImage as an image.
     * @since MHP 1.0
     */
    public Image getImage() {
        /* return (Image)bimg; */
        return ( ( Image )this );
    }

    /**
     * Returns a property of the image by name.  Individual property names
     * are defined by the various image formats.  If a property is not
     * defined for a particular image, this method returns the
     * <code>UndefinedProperty</code> field.  If the properties
     * for this image are not yet known, then this method returns
     * <code>null</code> and the <code>ImageObserver</code> object is
     * notified later.  The property name "comment" should be used to
     * store an optional comment that can be presented to the user as a
     * description of the image, its source, or its author.
     * @param name the property name
     * @param observer the <code>ImageObserver</code> that receives
     * notification regarding image information
     * @return an {@link Object} that is the property referred to by the
     * specified <code>name</code> or <code>null</code> if the   properties of
     * this image are not yet known.
     * @see ImageObserver
     * @see java.awt.Image#UndefinedProperty
     */
    public Object getProperty( String name, ImageObserver observer ) {
        Object property;
        property = super.getProperty( name, observer );
        return property;

        /* return bimg.getProperty(name,observer); */
    }
    
    public DVBBufferedImage getSubimage( int x, int y, int w, int h ) 
                            throws DVBRasterFormatException {
        return getSubimageDVB(x, y, w, h);
    }

    
    
//All other required methods are implemented in java.awt.Image.

}

