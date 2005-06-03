/*
 * NIST/DASE API Reference Implementation
 * $File: HRangeLook.java $
 * Last changed on $Date: 2001/05/01 14:09:25 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */



package org.havi.ui;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;
//import java.awt.Graphics2D;
//import java.awt.BasicStroke;
import java.awt.Shape;
import java.util.StringTokenizer;
import java.awt.Font;
import java.awt.FontMetrics;

/**
 * See (C) official HaVi documentation for reference
 * <p>
 * Standard HRangeLook to render an HStaticRange objects and subclasses,
 * based on its orientation, 
 * This look supports different decoration styles.
 * The decoration style can be selected in the constructor
 * or with setDecorationStyle() to one of the constants of the base class.
 * The HLook simply draws the decorations at the perimeter of the HVisible
 * (see XXXBorderSpacing) using the HVisible's foreground color.
 * 
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 */

public class HRangeLook implements HLook {

  public static final int FIRST_DECORATION = 1;
  
  public static final int STATIC_DECORATION = 1;

  public static final int LAST_DECORATION = 1; // update this

  public static final int DEFAULT_DECORATION = STATIC_DECORATION;
  
  protected int decorationStyle = DEFAULT_DECORATION;

  
  /** Constructor */
  public HRangeLook() {
  }

  /** Constructor with a decoration style !!! NOT FROM API */
  public HRangeLook(int style) {
    setDecorationStyle(style);
  }


  /** Horizontal Border, 2 by default */
  private int horizontalBorderSpacing = 2;

  /** Vertical Border, 2 by default */
  private int verticalBorderSpacing = 2;

  /** Static dash array, for speed */
  private static final float[] DASH_ARRAY = {1, 3};

  /** Static stroke to draw dotted line around focused components */
 /* private static final BasicStroke FOCUS_STROKE
    = new BasicStroke(1.0F, BasicStroke.CAP_SQUARE,
                  BasicStroke.JOIN_MITER, 10.0F,
                  DASH_ARRAY, 0.0F);*/

  /** Static stroke to draw solid lines */
 /* private static final BasicStroke SOLID_STROKE = new BasicStroke(1.0F);*/
  
  
  /* Unlike a regular AWT component, an HVisible delegates all rendering
     (paint method) to its HLook via the showLook() method,
     including the background, using the provided Graphics's ClipRect.
     @param g Graphics to use for rendering
     @param visible target HVisible
     @param state state for which <code>visible</code> is to be rendered
  */
  
  public void showLook(Graphics gr,
                       HVisible visible,
                       int state) {

    //Graphics2D g = (Graphics2D)gr;
    Graphics g=gr;

    HStaticRange hrange = null;


    /* Dimensions of the component */
    Dimension dim = visible.getSize();

    /* Matte compositing done in the update method */
    g.setColor(visible.getForeground());

    /* Sanity check */
    if( ! (visible instanceof HStaticRange) ) {
      System.err.println("HAVi: invalid object type passed to HRangeLook");
      //g.setStroke(SOLID_STROKE);
      g.drawRect(0,0, dim.width - 1, dim.height - 1);
      g.drawLine(0,0, dim.width - 1, dim.height - 1);
      g.drawLine(dim.width-1, 0, 0, dim.height-1);
      return;
    }

    hrange = (HStaticRange)visible;

    /* If in a focused state, draw a otted line at the perimeter */
    
    //g.setStroke(FOCUS_STROKE);
    g.setColor(visible.getForeground());
    switch(state) {
    case HState.NORMAL_STATE:
      break;
    default:
      g.drawRect(3, 3, dim.width-7, dim.height-7);
    }
    
    /* Draw a box */
    //g.setStroke(SOLID_STROKE);
    g.drawRect(3+horizontalBorderSpacing, 3+verticalBorderSpacing,
              dim.width-7-2*horizontalBorderSpacing,
              dim.height-7-2*verticalBorderSpacing);
    /* Show the current value */
    // TODO CLEAN THIS UP (Yuck!!!)
    switch(hrange.getOrientation()) {
    default:
    case HStaticRange.ORIENT_RIGHT_TO_LEFT:
    case HStaticRange.ORIENT_LEFT_TO_RIGHT:
      int tW = (int)((float)(dim.width-7-2*horizontalBorderSpacing) * ( (float)(hrange.getValue()-hrange.getMinValue()) / (float)(hrange.getMaxValue() - hrange.getMinValue()) ));
      System.out.println(tW + " (value: " + hrange.getValue() + ")");
      g.fillRect(3+horizontalBorderSpacing, 3+verticalBorderSpacing,
                 tW, dim.height-7-2*verticalBorderSpacing);
      break;
    case HStaticRange.ORIENT_BOTTOM_TO_TOP:
    case HStaticRange.ORIENT_TOP_TO_BOTTOM:
      int tH = (int)((float)(dim.height-7-2*verticalBorderSpacing) * ( (float)(hrange.getValue()-hrange.getMinValue()) / (float)(hrange.getMaxValue() - hrange.getMinValue()) ));
      System.out.println(tH + " (value: " + hrange.getValue() + ")");
      g.fillRect(3+horizontalBorderSpacing, 3+verticalBorderSpacing,
                 dim.width-7-2*horizontalBorderSpacing, tH);
      break;
    }
      
    /* Save the current clip *
    Shape originalClip = g.getClip();
    
    /* Now update the clip area before passing on to the text layout mgr *
    g.clipRect(3+horizontalBorderSpacing, 3+verticalBorderSpacing,
              dim.width-7-2*horizontalBorderSpacing,
              dim.height-7-2*verticalBorderSpacing);

    String text = visible.getTextContent(state);
    g.setColor(visible.getForeground());
    visible.getTextLayoutManager().render(visible.getTextContent(state),
                                          g, visible);

    /* Restore the clipping area, as required by the specs *
    g.setClip(originalClip);
    */
      
  }


  /** Evaluate the optimal size, based on the content and border
      If no content, fallback to the current size of visible
      @param visible target HVisible
      @return preferred size
  */
  public Dimension getPreferredSize(HVisible visible) {

    /* Get the maximum of all allowable states */
    Dimension preferred = null;
    Dimension current = null;

    for(int i=HState.FIRST_STATE; i<=HState.LAST_STATE; i++) {
      current = getPreferredSize(visible, i);
      if(current != null) {
        if(preferred == null) {
          preferred = current;
        } else {
          preferred = getDimensionUnion(preferred, current);
        }
      }
    }
    if( preferred == null ) {
      return visible.getSize();
    } else {
      return preferred;
    }
  }


  /** Evaluate the optimal size for a given state, based on the content and
      border. If no content, return null (!! DEPARTS FROM API !!)
      Note that this two-parameter method is not part of the API (private).
      @param visible target HVisible
      @param state state in which the dimension should be computed
      @return preferred size
  */
  private Dimension getPreferredSize(HVisible visible, int state) {

    if(visible.getTextContent(state) == null) {
      return visible.getSize();
    }
    
    Dimension preferred = null;
    
    /* First query layout manager, if possible */
    if(visible.getTextLayoutManager() instanceof org.dvb.ui.DVBTextLayoutManager) {
      preferred
        = ((org.dvb.ui.DVBTextLayoutManager)
           (visible.getTextLayoutManager())).getPreferredSize(
             visible.getTextContent(state), visible);
    }

    if(preferred==null) {

      /* If not available, guess from the string */
      FontMetrics fntMetrics = visible.getFontMetrics(visible.getFont());
      String line;
    
      /* Break the string into lines */
      StringTokenizer st
        = new StringTokenizer(visible.getTextContent(state),
                              "\n", false);
      int lineCount = st.countTokens();

      // Hard-coded line-spacing here (2)
      preferred.height =
        (fntMetrics.getHeight()+2)*lineCount - 2;
      
      /* Now find the widest line and update preferred accordingly */
      while (st.hasMoreTokens()) {
        line = st.nextToken();
        if(fntMetrics.stringWidth(line) > preferred.width) {
          preferred.width = fntMetrics.stringWidth(line);
        }
      }
      
    }

    /* Now adjust for border decoration */
    // TODO : border size hard coded here
    preferred.height += 10;
    preferred.width += 10;
    
    return preferred;

  }

  /** Evaluate the maximum size, based on the content and border
      If no content, fallback to the current size of visible
      @param visible target HVisible
      @return preferred size
  */
  public Dimension getMaximumSize(HVisible visible) {
    /* Return the minimum of current and preferred size */
    return visible.getSize();
  }

  

  /** Evaluate the minimum size, based on the content and border
      If no content, fallback to the current size of visible
      @param visible target HVisible
      @return preferred size
  */
  public Dimension getMinimumSize(HVisible visible) {
    /* Return the maximum of current and preferred size */
    return getDimensionUnion(visible.getPreferredSize(), visible.getSize());
  }


  /** Set the horizontal spacing between the border decoration and the
      HVisible rendition.
      @param width new spacing
  */
  public void setHorizontalBorderSpacing(int width) {
    if(width<0) {
      horizontalBorderSpacing = 0;
    } else {
      horizontalBorderSpacing = width;
    }
  }

  /** Return the current horizontal spacing.
      @return current horizontal border spacing
  */
  public int getHorizontalBorderSpacing() {
    return horizontalBorderSpacing;
  }

  /** Set the vertical spacing between the border decoration and the
      HVisible rendition.
      @param width new spacing
  */
  public void setVerticalBorderSpacing(int width) {
    if(width<0) {
      verticalBorderSpacing = 0;
    } else {
      verticalBorderSpacing = width;
    }
  }

  /** Return the current vertical spacing.
      @return current vertical border spacing
  */
  public int getVerticalBorderSpacing() {
    return verticalBorderSpacing;
  }


  public java.awt.Insets getInsets(HVisible visible) {
    return new java.awt.Insets(getVerticalBorderSpacing(),
                               getHorizontalBorderSpacing(),
                               getVerticalBorderSpacing(),
                               getHorizontalBorderSpacing());
  }

  public boolean isOpaque(HVisible visible) {
    return true;
  }
  
  //TODO: optimize
  public void widgetChanged(HVisible visible, HChangeData[] changes) {
    visible.repaint();
  }
  
/* ****************** NOT FROM API ******************** */
  
  /** Set the decoration style (eg. button or label)
      @param style new style
      @return new value (may not reflect the request, if invalid) */
  public int setDecorationStyle(int style) {
    if((style<=LAST_DECORATION) && (style>=FIRST_DECORATION)) {
      decorationStyle = style;
    }
    return decorationStyle;
  }
      
  /** Return the current decoration style (eg. button or label)
      @return current value */
  public int getDecorationStyle() {
    return decorationStyle;
  }

  /** Little utility function to return the dimension of a box that
      would contain both Dimension objects (union) */
  private Dimension getDimensionUnion(Dimension a, Dimension b) {
    Dimension m = new Dimension();
    m.height = ( a.height>b.height ?  a.height : b.height );
    m.width = ( a.width>b.width ?  a.width : b.width );
    return m;
  }

}
