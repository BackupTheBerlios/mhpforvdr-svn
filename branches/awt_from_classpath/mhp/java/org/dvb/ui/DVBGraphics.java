package org.dvb.ui;

/* Last change:  IB   21 Jan 100    1:05 pm */
import java.awt.Color;
import java.awt.Graphics;

/**
 * The <code>DVBGraphics</code> class is a adapter class to support alpha
 * compositing in an MHP device. Most methods directly delegate to
 * java.awt.Graphics other methods could
 * delegate to the appropriate methods in java.awt.Graphics2D where available
 * or could be implemented in native code
 * This class inherits from java.awt.Graphics in implementations using the
 * JDK1.1. In implementations using the JDK1.2 DVBGraphics inherits from
 * java.awt.Graphics2D. <b>In MHP devices all Graphics Objects are DVBGraphics
 * objects.</b> Thus one can get a DVBGraphics by casting a given Graphics
 * object. The normal compositing rule used is <b>DVBAlphaComposite.SCR. This
 * is the fastes rule because there is no computation. Note this is not the
 * default behaviour of other graphics libraries.
 * When drawing pictures with an alpha channel the transparent part will be
 * transparent to the video in the background. Programmers should set the rule
 * to DVBAlphaComposite.SRC_OVER when drawing images or other shapes which
 * shall be transparent to the graphics in the background </B>
 * @see     java.awt.Graphics
 * @since       MHP1.0
 */
public abstract class DVBGraphics extends Graphics {
    /**
     * Constructs a new <code>DVBGraphics</code> object.  This constructor is
     * the default contructor for a graphics context. <p>
     * Since <code>DVBGraphics</code> is an abstract class, applications
     * cannot call this constructor directly. DVBGraphics contexts are
     * obtained from other DVBGraphics contexts or are created by casting
     * java.awt.Graphics to DVBGraphics.
     * @see        java.awt.Graphics#create()
     * @see        java.awt.Component#getGraphics
     * @since MHP 1.0
     */
    protected DVBGraphics() {
    }
    
    //implement double buffering - not official API, used by HContainer/HScene
    public abstract void enterBuffered();
    public abstract void leaveBuffered();
    
    /**
     *  Returns all available Porter-Duff Rules for this specific Graphics
     * context. E.g. a devices could support the SRC_OVER rule when using a
     * destination which does not has Alpha or were the
     * alpha is null while this rule is not available when drawing on a
     * graphic context were the destination has alpha.
     * Which rules are supported for the different graphics objects is defined
     * in the Minimum Platform Capabilities of the MHP spec.
     * @since MHP 1.0
     */
    public abstract DVBAlphaComposite[] getAvailableCompositeRules();

    /**
     *  Returns the best match for the specified Color as a DVBColor
     *	@return - the best match for the specified Color.
     * @since MHP 1.0	
     */
    public DVBColor getBestColorMatch( Color c ) {
        return new DVBColor(c); //I do not know if this is better, but I don't like the "null" from Convergence
        //return null;
    }

    /**
     * Gets this graphics context's current color. This will return a DVBColor
     * cast to java.awt.Color.
     * @return    this graphics context's current color.
     * @see       DVBColor
     * @see       java.awt.Color
     * @see       #setColor
     * @since MHP 1.0
     */
    public abstract Color getColor();

    /**
     * Returns the current <code>DVBAlphaComposite</code> in the
     * <code>DVBGraphics</code> context.
     * This method could delegate to a java.awt.Graphics2D object
     * where available
     * @return the current <code>DVBGraphics</code>
     * <code>DVBAlphaComposite</code>, which defines a compositing style.
     * @see #setDVBComposite
     * @since MHP 1.0
     */
    public abstract DVBAlphaComposite getDVBComposite();

    /**
     * Returns the Sample Model (DVBBufferedImage.TYPE_BASE,
     * DVBBufferedImage.TYPE_ADVANCED) which is used in the on/off screen
     * buffer this graphics object draws into.
     * @return the type of the Sample Model
     * @see org.dvb.ui.DVBBufferedImage
     * @since MHP 1.0
     */
    public int getType() {
        return 0;
    }

    /**
     * Sets this graphics context's current color to the specified color. All
     * subsequent graphics operations using this graphics context use this
     * specified color. Note that color c can be a DVBColor
     * @param     c   the new rendering color.
     * @see       java.awt.Color
     * @see       DVBColor
     * @see       org.dvb.ui.DVBGraphics#getColor
     * @since MHP 1.0
     */
    public abstract void setColor( Color c );

    /**
     * Sets the <code>DVBAlphaComposite</code> for the
     * <code>DVBGraphics</code> context. The <code>DVBAlphaComposite</code> is
     * used in all drawing methods such as
     * <code>drawImage</code>, <code>drawString</code>, <code>draw</code>,
     * and <code>fill</code>.  It specifies how new pixels are to be combined
     * with the existing pixels on the graphics device during the rendering
     * process. <p> This method could delegate to a Graphics2D object or to an
     * native implementation
     * @param comp the <code>DVBAlphaComposite</code> object to be
     * used for rendering
     * @throw UnsupportedDrawingOperationException when the requested
     * Porter-Duff rule is not supported by this graphics context
     * @see java.awt.Graphics#setXORMode
     * @see java.awt.Graphics#setPaintMode
     * @see org.dvb.ui.DVBAlphaComposite
     * @since MHP 1.0
     */
    public abstract void setDVBComposite( DVBAlphaComposite comp )
        throws UnsupportedDrawingOperationException;

    /**
     * Returns a <code>String</code> object representing this
     * <code>DVBGraphics</code> object's value.
     * @return       a string representation of this graphics context.
     * @since MHP 1.0
     */
    public String toString() {
        return getClass().getName() + "[font=" + getFont() + ",color=" +
            getColor() + "]";
    }
}

