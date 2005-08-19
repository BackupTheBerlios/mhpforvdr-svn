package org.dvb.ui;

/**
 * This <code>DVBAlphaComposite</code> class implements the basic alpha
 * compositing rules for combining source and destination pixels to achieve
 * blending and transparency effects with graphics, images and video.
 * The rules implemented by this class are a subset of the Porter-Duff
 * rules described in T. Porter and T. Duff, "Compositing Digital Images",
 * SIGGRAPH 84, 253-259. <p> If any input does not have an alpha channel, an
 * alpha value of 1.0, which is completely opaque, is assumed for all pixels.
 *  A constant alpha value can also be specified to be multiplied with the
 * alpha value of the source pixels. <p>
 * The following abbreviations are used in the description of the rules: <ul>
 * <li>Cs = one of the color components of the source pixel.
 * <li>Cd = one of the color components of the destination pixel.
 * <li>As = alpha component of the source pixel.
 * <li>Ad = alpha component of the destination pixel.
 * <li>Fs = fraction of the source pixel that contributes to the output.
 * <li>Fd = fraction of the input destination pixel that contributes to the
 * output. </ul> <p> The color and alpha components produced by the
 * compositing operation are calculated as follows: <pre>
 * 	Cd = Cs*Fs + Cd*Fd
 * 	Ad = As*Fs + Ad*Fd
 *</pre> where Fs and Fd are specified by each rule.  The above equations assume
 * that both source and destination pixels have the color components
 * premultiplied by the alpha component.  Similarly, the equations expressed
 * in the definitions of compositing rules below assume premultiplied alpha.
 * <p> The alpha resulting from the compositing operation is stored
 * in the destination if the destination has an alpha channel.
 * Otherwise, the resulting color is divided by the resulting
 * alpha before being stored in the destination and the alpha is discarded.
 * If the alpha value is 0.0, the color values are set to 0.0.
 * @since MHP 1.0
 */
public final class DVBAlphaComposite {
    /**
     * Porter-Duff Clear rule. Both the color and the alpha of the destination
     * are cleared. Neither the source nor the destination is used as input.
     * <p> Fs = 0 and Fd = 0, thus: <pre>
   * 	Cd = 0
   * 	Ad = 0
   *</pre> <p> <b>Note that this operation is a fast drawing operation</b>
     * This operation is the same as using a source with alpha= 0
     * and the SRC rule
     * @since MHP 1.0
     */
  // public static final int	CLEAR		= AlphaComposite.CLEAR;
    public static final int CLEAR = 1;

    /**
     * Porter-Duff Source rule. The source is copied to the destination.
     * The destination is not used as input. <p> Fs = 1 and Fd = 0, thus: <pre>
   * 	Cd = Cs
   * 	Ad = As
   *</pre> <p> <b>Note that this is a fast drawing routine</b>
     * @since MHP 1.0
     */
  //public static final int	SRC		= AlphaComposite.SRC;
    public static final int SRC = 2;

    /**
     * Porter-Duff Destination In Source rule.
     * The part of the destination lying inside of the source
     * replaces the destination. <p> Fs = 0 and Fd = As, thus: <pre>
   * 	Cd = Cd*As
   * 	Ad = Ad*As
   *</pre> <p> <b>Note that this operation is faster than e.g. SRC_OVER but
     * slower as SRC</b>
     * @since MHP 1.0
     */
  // public static final int	DST_IN		= AlphaComposite.DST_IN;
    public static final int DST_IN = 6;

    /**
     * Porter-Duff Destination Held Out By Source rule.
     * The part of the destination lying outside of the source
     * replaces the destination. <p> Fs = 0 and Fd = (1-As), thus: <pre>
   * 	Cd = Cd*(1-As)
   * 	Ad = Ad*(1-As)
   *</pre> <p> <b>Note that this operation is faster than e.g. SRC_OVER but
     * slower as SRC</b>
     * @since MHP 1.0
     */
  // public static final int	DST_OUT		= AlphaComposite.DST_OUT;
    public static final int DST_OUT = 8;

    /**
     * Porter-Duff Destination Over Source rule.
     * The destination is composited over the source and
     * the result replaces the destination. <p>
     * Fs = (1-Ad) and Fd = 1, thus: <pre>
   * 	Cd = Cs*(1-Ad) + Cd
   * 	Ad = As*(1-Ad) + Ad
   *</pre> <p> <b>Note that this can be a very slow drawing operation</b>
     * @since MHP 1.0
     */
  //  public static final int	DST_OVER	= AlphaComposite.DST_OVER;
    public static final int DST_OVER = 4;

    /**
     * Porter-Duff Source In Destination rule.
     * The part of the source lying inside of the destination replaces
     * the destination. <p> Fs = Ad and Fd = 0, thus: <pre>
   * 	Cd = Cs*Ad
   * 	Ad = As*Ad
   *</pre> <p> <b>Note that this operation is faster than e.g. SRC_OVER but
     * slower as SRC</b>
     * @since MHP 1.0
     */
  // public static final int	SRC_IN		= AlphaComposite.SRC_IN;
    public static final int SRC_IN = 5;

    /**
     * Porter-Duff Source Held Out By Destination rule.
     * The part of the source lying outside of the destination
     * replaces the destination. <p> Fs = (1-Ad) and Fd = 0, thus: <pre>
   * 	Cd = Cs*(1-Ad)
   * 	Ad = As*(1-Ad)
   *</pre> <p> <b>Note that this operation is faster than e.g. SRC_OVER but
     * slower as SRC</b>
     * @since MHP 1.0
     */
  // public static final int	SRC_OUT		= AlphaComposite.SRC_OUT;
    public static final int SRC_OUT = 7;

    /**
     * Porter-Duff Source Over Destination rule.
     * The source is composited over the destination. <p>
     * Fs = 1 and Fd = (1-As), thus: <pre>
   * 	Cd = Cs + Cd*(1-As)
   * 	Ad = As + Ad*(1-As)
   *</pre> <p> <b>Note that this can be a very slow drawing operation</b>
     * @since MHP 1.0
     */
  // public static final int	SRC_OVER	= AlphaComposite.SRC_OVER;
    public static final int SRC_OVER = 3;

    /**
     * <code>DVBAlphaComposite</code> object that implements the opaque CLEAR
     * rule with an alpha of 1.0f.
     * @see #CLEAR
     * @since MHP 1.0
     */
    public static final DVBAlphaComposite Clear =
        new DVBAlphaComposite( CLEAR );

    /**
     * <code>DVBAlphaComposite</code> object that implements the opaque SRC
     * rule with an alpha of 1.0f.
     * @see #SRC
     * @since MHP 1.0
     */
    public static final DVBAlphaComposite Src = new DVBAlphaComposite( SRC );

    /**
     * <code>DVBAlphaComposite</code> object that implements the opaque
     * SRC_OVER rule with an alpha of 1.0f.
     * @see #SRC_OVER
     * @since MHP 1.0
     */
    public static final DVBAlphaComposite SrcOver =
        new DVBAlphaComposite( SRC_OVER );

    /**
     * <code>DVBAlphaComposite</code> object that implements the opaque
     * DST_OVER rule with an alpha of 1.0f.
     * @see #DST_OVER
     * @since MHP 1.0
     */
    public static final DVBAlphaComposite DstOver =
        new DVBAlphaComposite( DST_OVER );

    /**
     * <code>DVBAlphaComposite</code> object that implements the opaque SRC_IN
     * rule with an alpha of 1.0f.
     * @see #SRC_IN
     * @since MHP 1.0
     */
    public static final DVBAlphaComposite SrcIn =
        new DVBAlphaComposite( SRC_IN );

    /**
     * <code>DVBAlphaComposite</code> object that implements the opaque DST_IN
     * rule with an alpha of 1.0f.
     * @see #DST_IN
     * @since MHP 1.0
     */
    public static final DVBAlphaComposite DstIn =
        new DVBAlphaComposite( DST_IN );

    /**
     * <code>DVBAlphaComposite</code> object that implements the opaque
     * SRC_OUT rule with an alpha of 1.0f.
     * @see #SRC_OUT
     * @since MHP 1.0
     */
    public static final DVBAlphaComposite SrcOut =
        new DVBAlphaComposite( SRC_OUT );

    /**
     * <code>DVBAlphaComposite</code> object that implements the opaque
     * DST_OUT rule with an alpha of 1.0f.
     * @see #DST_OUT
     * @since MHP 1.0
     */
    public static final DVBAlphaComposite DstOut =
        new DVBAlphaComposite( DST_OUT );
    private static final int MIN_RULE = CLEAR;
    private static final int MAX_RULE = DST_OUT;
    float extraAlpha;
    int rule;

    private DVBAlphaComposite( int rule ) {
        this( rule, 1.0f );
    }

    private DVBAlphaComposite( int rule, float alpha ) {
        if ( alpha < 0.0f || alpha > 1.0f ) {
            throw new IllegalArgumentException( "alpha value out of range" );
        }
        if ( rule < MIN_RULE || rule > MAX_RULE ) {
            throw new IllegalArgumentException( "unknown composite rule" );
        }
        this.rule = rule;
        this.extraAlpha = alpha;
    }

    /**
     * Tests if the specified {@link Object} is equal to this
     * <code>DVBAlphaComposite</code> object.
     * @param obj the <code>Object</code> to test for equality
     * @return <code>true</code> if <code>obj</code> equals this
     * <code>DVBAlphaComposite</code>; <code>false</code> otherwise.
     * @since MHP 1.0
     */
    public boolean equals( Object obj ) {
        if ( !( obj instanceof DVBAlphaComposite ) ) {
            return false;
        }
        DVBAlphaComposite ac = ( DVBAlphaComposite )obj;
        if ( rule != ac.rule ) {
            return false;
        }
        if ( extraAlpha != ac.extraAlpha ) {
            return false;
        }
        return true;
    }

    /**
     * Returns the alpha value of this<code>DVBAlphaComposite</code>.  If this
     * <code>DVBAlphaComposite</code> does not have an alpha value,
     * 1.0 is returned.
     * @return the alpha value of this <code>DVBAlphaComposite</code>.
     * @since MHP 1.0
     */
    public float getAlpha() {
        return extraAlpha;
    }
    
    //internal API
    public int getIntegerAlpha() {
       return multiplyAlpha(255);
    }
    
    //internal API
    public int multiplyAlpha(int otherIntegerAlpha) {
      if (extraAlpha==1.0f)
         return otherIntegerAlpha;
      else
         return (int)( ((float)otherIntegerAlpha) * extraAlpha );
    }
    
    /**
     * Creates an <code>DVBAlphaComposite</code> object with the
     * specified rule.
     * @param rule the compositing rule
     * @since MHP 1.0
     */
    public static DVBAlphaComposite getInstance( int rule ) {
        //return static instances now for alpha=1.0
        switch ( rule ) {
            case CLEAR:
                return Clear;
            case SRC:
                return Src;
            case SRC_OVER:
                return SrcOver;
            case DST_OVER:
                return DstOver;
            case SRC_IN:
                return SrcIn;
            case DST_IN:
                return DstIn;
            case SRC_OUT:
                return SrcOut;
            case DST_OUT:
                return DstOut;
            default:
                throw new IllegalArgumentException( "unknown composite rule" );
        }
    }

    /**
     * Creates an <code>DVBAlphaComposite</code> object with the specified
     * rule and the constant alpha to multiply with the alpha of the source.
     * The source is multiplied with the specified alpha before being composited
     * with the destination.
     * @param rule the compositing rule
     * @param alpha the constant alpha to be multiplied with the alpha of
     * the source. <code>alpha</code> must be a floating point number in the
     * inclusive range [0.0,&nbsp;1.0].
     * @since MHP 1.0
     */
    public static DVBAlphaComposite getInstance( int rule, float alpha ) {
        if ( alpha == 1.0f ) {
            return getInstance( rule );
        }
        //sanity checks are done in constructor
        return new DVBAlphaComposite( rule, alpha );
    }

    /**
     * Returns the compositing rule of this <code>DVBAlphaComposite</code>.
     * @return the compositing rule of this <code>DVBAlphaComposite</code>.
     * @since MHP 1.0
     */
    public int getRule() {
        return rule;
    }
}

