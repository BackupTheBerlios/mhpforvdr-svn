/*
 * NIST/DASE API Reference Implementation
 * $File: HGraphicLook.java $
 * Last changed on $Date: 2001/01/31 17:43:28 UTC $
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
import java.awt.Image;

/**
 * See (C) official HaVi documentation for reference
 * <p>
 * Standard HGraphicLook to render an HVisible as graphics (with its
 * association GraphiContent).
 * This look supports different decoration styles (for static and actionable
 * icons for example). The decoration style can be selected in the
 * constructor or with setDecorationStyle() to one of the constants of the base
 * class.  The HLook simply draws the decorations at the perimeter of the
 * HVisible (see XXXBorderSpacing) using the HVisible's foreground color then
 * renders the graphic content. If it has none, do nothing.
 * 
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 * */
 
 
//TODO: Drawing needs some finetuning; Still missing Graphics2D, especially dotted lines


public class HGraphicLook implements HLook {
  
  
  public static final int FIRST_DECORATION = 1;
  
  public static final int ICON_DECORATION = 1;
  public static final int BUTTON_DECORATION = 2;
  
  public static final int LAST_DECORATION = 2; // update this
  
  public static final int DEFAULT_DECORATION = BUTTON_DECORATION;
  
  protected int decorationStyle = DEFAULT_DECORATION;
  


  /** Horizontal Border, 2 by default (API) */
  private int horizontalBorderSpacing = 2;
  
  /** Vertical Border, 2 by default (API) */
  private int verticalBorderSpacing = 2;
  
  /** Scaling mode, NO_SCALING  by default (API) */
  private int resizeMode = NO_SCALING;
  

  /** Render without scaling to fit the current geometry */
  public static final int NO_SCALING = 100;

  /** Render to fit as well as possible in the current geometry while
      preserving the original aspect ratio.
      In this implementation, the areas not covered by the graphic content
      are fill with the background color */
  public static final int SCALE_PRESERVE = 101;
  
  /** Stretch to fit, regardless of the aspect ratio */
  public static final int SCALE_ARBITRARY = 102;
  
  /** Private dash array for decoration rendering */
  private static final float[] dashArray = {1, 3};
  
  /** Constructor */
  public HGraphicLook() {
    super();
  }
  
  /** Constructor with a decoration style !!! NOT FROM API */
  public HGraphicLook(int style) {
    setDecorationStyle(style);
  }
  
  
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

    /* Dimensions of the component */
    Dimension dim = visible.getSize();

    /* Matte compositing done in the update method */

    g.setColor(visible.getForeground());
    
    /* Draw the decorations, 3 pixel-wide */

    /* If in a focused state, draw a dotted line at the perimeter */
    /*g.setStroke(new BasicStroke(1.0F, BasicStroke.CAP_SQUARE,
                                BasicStroke.JOIN_MITER, 10.0F,
                                dashArray, 0.0F));*/
    
    switch(state) {
    case HState.NORMAL_STATE:
      break;
    default:
      g.drawRect(1, 1, dim.width-3, dim.height-3);
    }
    
    
    switch(decorationStyle) {

    case BUTTON_DECORATION:
      
      /* Draw a border, based on the current state:
         NORMAL_STATE: flat rectangle 1 pixel
         FOCUS_STATE: flat rectangle, 1 pixel
         ACTION_STATE: flat rectangle, 2 pixels
         NORMAL_ACTIONED_STATE: inverted
    */
      
      //g.setStroke(new BasicStroke(1.0F));
      
      g.drawRect(3, 3, dim.width-7, dim.height-7);

      switch(state) {
      case HState.NORMAL_STATE:
        break;
      case HState.FOCUSED_STATE:
        break;
      case HState.ACTIONED_STATE:
      case HState.ACTIONED_FOCUSED_STATE:
        g.drawRect(4, 4, dim.width-9, dim.height-9);
        break;
      default:
        // Do nothing: invalid state
      }

      break;

    case ICON_DECORATION:
    default:

      /* No border without focus. Dotted line with focus. */
      /*g.setStroke(new BasicStroke(1.0F, BasicStroke.CAP_SQUARE,
                                  BasicStroke.JOIN_MITER, 10.0F,
                                  dashArray, 0.0F));*/

      switch(state) {
      case HState.NORMAL_STATE:
        break;
      default:
        g.drawRect(1, 1, dim.width-3, dim.height-3);
      }

    }

    /* Save the current clip */
    Shape originalClip = gr.getClip();
    
    /* Now update the clip area before rendering */
    g.clipRect(3+horizontalBorderSpacing, 3+verticalBorderSpacing,
              dim.width-7-2*horizontalBorderSpacing,
              dim.height-7-2*verticalBorderSpacing);

    Image content = visible.getGraphicContent(state);

    if(content != null) {

      /* Render the image centered in the component
         Sign up the parent visible as an ImageObserver so that it repaints
         when the image is done loading. */
      // TODO: honor the getAlignment[XY] property of Components
      g.drawImage(content,
                  (dim.width - content.getWidth(null)) / 2 ,
                  (dim.height - content.getHeight(null)) / 2,
                  visible);
    }
    
    /* Restore the clipping area, as required by the specs */
    gr.setClip(originalClip);
    
  }


  /** Return the preferred size of the HVisible as rendered by this look.
      This include the image size for the current state plus any decoration.
      If no content is available, return the current size
      @param visible HVisible to evaluate
      @return preferred size */
  
  public Dimension getPreferredSize(HVisible visible) {

    System.out.println("getPreferredSize: current size = "
                       + visible.getSize());
    Image content = visible.getGraphicContent(visible.getInteractionState());

    /* getXXX returns -1 if the image geometry is not yet known */
    if( (content==null) ) {
      return visible.getSize();
    }
    if( (content.getWidth(null) == -1) || (content.getHeight(null) == -1) ) {
      visible.prepareImage(content, visible);
      return visible.getSize();
    } else {
      //TODO Resolve this problem (size not known yet at layout time)
      return visible.getSize();
      // TODO: decoration size hard-coded here at 3 pixels
      /*  return new Dimension(content.getWidth(null)+2*horizontalBorderSpacing+6,
          content.getHeight(null)+2*verticalBorderSpacing+6); */
    }

  }

  /** Return the maximum preferred size. If content is not available, return
      the current size. For now, just return the max of current size
      or preferred size.
      @param visible HVisible to evaluate
      @return maximum size */
  public Dimension getMaximumSize(HVisible visible) {

    System.out.println("getMaximumSize: current size = "
                       + visible.getSize());

    Dimension c = visible.getSize();
    Dimension p = getPreferredSize(visible);
    if( ( c.height < p.height ) || ( c.width < p.width ) ) {
      // TODO: fix this
      // return p;
      return c;
    } else {
      return c;
    }
  }


  /** Return the minimum preferred size. If content is not available, return
      the current size.
      For now (//TODO) simply return getPreferredSize()
      @param visible HVisible to evaluate
      @return minimum size */
  public Dimension getMinimumSize(HVisible visible) {
    System.out.println("getMinimumSize: current size = "
                       + visible.getSize());
    return getPreferredSize(visible);
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


  /** Set the resize mode to either on of NO_SCALING, SCALE_PRESERVE
      or SCALE_ARBITRARY.
      @param mode requested mode
      @return false if new mode is not supported */
  public boolean setResizeMode(int mode) {
    // TODO : support scaling
    if(mode==HGraphicLook.NO_SCALING) {
      resizeMode = mode;
      return true;
    } else {
      return false;
    }
  }

  /** Return the current resize mode
      @return current resize mode */
  public int getResizeMode() {
    return resizeMode;
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

}
