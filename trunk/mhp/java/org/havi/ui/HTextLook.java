/*
 * NIST/DASE API Reference Implementation
 * $File: HTextLook.java $
 * Last changed on $Date: 2001/05/31 14:42:10 UTC $
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
 * Standard HTextLook to render an HVisible as text.
 * This look supports different decoration styles (for buttons and labels
 * for example. The decoration style can be selected in the constructor
 * or with setDecorationStyle() to one of the constants of the base class.
 * The HLook simply draws the decorations at the perimeter of the HVisible
 * (see XXXBorderSpacing) usingthe HVisible's foreground color then
 * delegates the actual rendering to the HVisible's HTextLayoutManager.
 * If it has none, do nothing.
 * 
 * <p>Revision information:<br>
 * $Revision: 1.6 $
 *
 */
 
//TODO: Drawing needs some finetuning; Still missing Graphics2D, especially dotted lines

public class HTextLook implements HLook {

  public static final int FIRST_DECORATION = 1;
  
  public static final int LABEL_DECORATION = 1;
  public static final int BUTTON_DECORATION = 2;

  public static final int LAST_DECORATION = 2; // update this

  public static final int DEFAULT_DECORATION = BUTTON_DECORATION;
  
  protected int decorationStyle = DEFAULT_DECORATION;

  
  /** Constructor */
  public HTextLook() {
  }

  /** Constructor with a decoration style !!! NOT FROM API */
  public HTextLook(int style) {
    setDecorationStyle(style);
  }


  /** Horizontal Border, 2 by default */
  private int horizontalBorderSpacing = 2;

  /** Vertical Border, 2 by default */
  private int verticalBorderSpacing = 2;

  /** Static dash array, for speed */
  private static final float[] DASH_ARRAY = {1, 3};

  /** Static stroke to draw dotted line around focused components */
  /*private static final BasicStroke FOCUS_STROKE
    = new BasicStroke(1.0F, BasicStroke.CAP_SQUARE,
                  BasicStroke.JOIN_MITER, 10.0F,
                  DASH_ARRAY, 0.0F);*/

  /** Static stroke to draw solid lines */
  /*private static final BasicStroke SOLID_STROKE = new BasicStroke(1.0F);*/
  
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
    Graphics g = gr;//(Graphics2D)gr;
    

    /* Dimensions of the component */
    Dimension dim = visible.getSize();

    /* Matte compositing done in the update method */
    g.setColor(visible.getForeground());
    

    /* Draw the decorations, 3 pixel-wide */
    switch(decorationStyle) {

    case BUTTON_DECORATION:
      
      /* Draw a border, based on the current state:
         NORMAL_STATE: raised rectangle
         FOCUS_STATE: raised rectangle
         ACTION_STATE: sunken rectangle
         NORMAL_ACTIONED_STATE: same
    */
      
      //g.setStroke(SOLID_STROKE);
      

      switch(state) {
      case HState.NORMAL_STATE:
      case HState.FOCUSED_STATE:
        g.setColor(visible.getForeground());
        g.drawRect(0, 0, dim.width-1, dim.height-1);
        g.setColor(visible.getBackground().darker());
        g.drawRect(1, 1, dim.width-3, dim.height-3);
        g.setColor(visible.getBackground().brighter());
        g.drawLine(2, 2, 2, dim.height-3);
        g.drawLine(2, 2, dim.width-3, 2);
        break;
      case HState.ACTIONED_STATE:
      case HState.ACTIONED_FOCUSED_STATE:
      //case HState.NORMAL_ACTIONED_STATE:
        g.setColor(visible.getForeground());
        g.drawRect(0, 0, dim.width-1, dim.height-1);
        g.setColor(visible.getBackground().darker());
        g.fillRect(1, 1, dim.width-2, dim.height-2);
        g.setColor(visible.getBackground().darker().darker());
        g.drawLine(1, 1, 1, dim.height-2);
        g.drawLine(1, 1, dim.width-2, 1);
        break;
      case HState.DISABLED_STATE:
      case HState.DISABLED_FOCUSED_STATE:
      case HState.DISABLED_ACTIONED_STATE:
      case HState.DISABLED_ACTIONED_FOCUSED_STATE:
      //making up some drawing
        g.setColor(visible.getForeground().brighter());
        g.drawRect(0, 0, dim.width-1, dim.height-1);
        g.setColor(visible.getBackground().darker().darker());
        g.fillRect(1, 1, dim.width-2, dim.height-2);
        g.setColor(visible.getBackground().darker().darker().darker());
        g.drawLine(1, 1, 1, dim.height-2);
        g.drawLine(1, 1, dim.width-2, 1);
        break;
      default:
        // Do nothing: invalid state
      }

      break;

    case LABEL_DECORATION:
    default:

    }


    /* If in a focused state, draw a dotted line at the perimeter */
    
    //missing Graphics2D :-(
    //g.setStroke(FOCUS_STROKE);
    
    g.setColor(visible.getForeground());
    switch(state) {
    case HState.NORMAL_STATE:
      break;
    default:
      g.drawRect(3, 3, dim.width-7, dim.height-7);
    }
    

    
    /* Save the current clip */
    Shape originalClip = g.getClip();
    
    /* Now update the clip area before passing on to the text layout mgr */
    g.clipRect(1+horizontalBorderSpacing, 1+verticalBorderSpacing,
              dim.width-4-2*horizontalBorderSpacing,
              dim.height-4-2*verticalBorderSpacing);

    String text = visible.getTextContent(state);
    g.setColor(visible.getForeground());
    visible.getTextLayoutManager().render(visible.getTextContent(state),
                                          g, visible, getInsets(visible));

    /* Restore the clipping area, as required by the specs */
    g.setClip(originalClip);
      
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

    if(preferred!=null) {

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
      
    /* Now adjust for border decoration */
    // TODO : border size hard coded here
    preferred.height += 10;
    preferred.width += 10;
    
    }

    
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
