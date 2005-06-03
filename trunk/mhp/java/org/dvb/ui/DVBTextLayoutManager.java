
package org.dvb.ui;

import java.util.StringTokenizer;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.FontMetrics;
import org.havi.ui.HVisible;

/*The DVBTextLayoutManager provides a text rendering layout mechanism for the 
org.havi.ui.HStaticText org.havi.ui.HText and org.havi.ui.HTextButton classes. The 
semantics of the rendering behaviour and the settings are speci  ed in the "Text 
presentation"annex of this speci  cation.The DVBTextLayoutManager renders the text 
according to the semantics described in that annex. */


//TODO: Luckily NIST provided an implementation :-)
//However, their constants are different, thus alignment needs fixing.




public class DVBTextLayoutManager implements org.havi.ui.HTextLayoutManager {

/*
The text should be centered horizontally. */
public static final int HORIZONTAL_CENTER = 3;

/*
The text should be horizontally to the horizontal end side (e.g.when start corner is upper left and line orientation 
horizontal,meaning text that is read left to right from top to bottom,this implies alignment to 
right). */
public static final int HORIZONTAL_END_ALIGN = 2;

/*
The text should be aligned horizontally to the horizontal start side (e.g.when start corner is upper left and line 
orientation horizontal,meaning text that is read left to right from top to bottom,this implies alignment to 
left). */
public static final int HORIZONTAL_START_ALIGN = 1;

/*
Horizontal line orientation. */
public static final int LINE_ORIENTATION_HORIZONTAL = 10;

/*
Vertical line orientation. */
public static final int LINE_ORIENTATION_VERTICAL = 11;

/*
Lower left text start corner. */
public static final int START_CORNER_LOWER_LEFT = 22;

/*
Lower right text start corner. */
public static final int START_CORNER_LOWER_RIGHT = 23;

/*
Upper left text start corner. */
public static final int START_CORNER_UPPER_LEFT = 20;

/*
Upper right text start corner. */
public static final int START_CORNER_UPPER_RIGHT = 21;

/*
The text should be centered vertically. */
public static final int VERTICAL_CENTER = 6;

/*
The text should be aligned vertically to the vertical end side (e.g.when start corner is upper left and line orientation 
horizontal,meaning text that is read left to right from top to bottom,this implies alignment to bottom). This is is de  
ned by the section "Vertical limits"in the "Text presentation"annex of this speci  
cation. */
public static final int VERTICAL_END_ALIGN = 5;

/*
The text should be aligned vertically to the vertical start side (e.g.when start corner is upper left and line 
orientation horizontal,meaning text that is read left to right from top to bottom,this implies alignment to top). This 
is is de  ned by the section "Vertical limits"in the "Text presentation"annex of this speci  
cation. */
public static final int VERTICAL_START_ALIGN = 4;

protected int horizontalAlign;
protected int verticalAlign;
protected int lineOrientation;
protected int startCorner;
protected boolean wrap;
protected int linespace;
protected int letterspace;
protected int horizontalTabSpace;
protected java.awt.Insets insets;

private TextOverflowListener overflowListener;

/** Line spacing in pixels (not in API) */
private static final int LINE_SPACING = 1;



/*
Constructs a DVBTextLayoutManager object with default parameters (HORIZONTAL_START_ALIGN,
VERTICAL_START_ALIGN,LINE_ORIENTATION_HORIZONTAL, 
START_CORNER_UPPER_LEFT,wrap =true,linespace =(point size of the default font for HVisible)+7,
letterspace 
=0,horizontalTabSpace =56) */
public DVBTextLayoutManager() {
   horizontalAlign=HORIZONTAL_START_ALIGN;
   verticalAlign=VERTICAL_START_ALIGN;
   lineOrientation=LINE_ORIENTATION_HORIZONTAL;
   startCorner=START_CORNER_UPPER_LEFT;
   wrap=true;
   linespace=12+7;
   letterspace=0;
   horizontalTabSpace=56;
   insets=new java.awt.Insets(0,0,0,0);
   
   overflowListener=null;
}

/*
Constructs a DVBTextLayoutManager object. Parameters: horizontalAlign -Horizontal alignment setting verticalAlign 
-Vertical alignment setting lineOrientation -Line orientation setting startCorner -Starting corner setting wrap -Text 
wrapping setting linespace -Line spacing setting expressed in points letterspace -Letterspacing adjustment relative to 
the default letterspacing.Expressed in units of 1/256th point as the required increase in the spacing between 
consecutive characters. May be either positive or negative. horizontalTabSpace -Horizontal tabulation setting in 
points */
public DVBTextLayoutManager(int _horizontalAlign, int _verticalAlign, int _lineOrientation,
 int _startCorner, boolean _wrap, 
int _linespace, int _letterspace, int _horizontalTabSpace) {

   horizontalAlign=_horizontalAlign;
   verticalAlign=_verticalAlign;
   lineOrientation=_lineOrientation;
   startCorner=_startCorner;
   wrap=_wrap;
   linespace=_linespace;
   letterspace=_letterspace;
   horizontalTabSpace=_horizontalTabSpace;
   insets=new java.awt.Insets(0,0,0,0);
   
   overflowListener=null;
}

/*
Register a TextOver  owListener that will be noti  ed if the text string does not  t in the component when rendering. 
Parameters: l -a listener object */
public void addTextOverflowListener(TextOverflowListener l) {
   overflowListener=TextOverflowMulticaster.add(overflowListener, l);
}

/*
Get the horizontal alignment. Returns: Horizontal alignment setting */
public int getHorizontalAlign() {
   return horizontalAlign;
}

/*
Get the horizontal tabulation spacing. Returns: the horizontal tabulation spacing */
public int getHorizontalTabSpacing() {
   return horizontalTabSpace;
}

/*
Returns the insets that this text layout manager uses.When rendering text,it leaves empty margins of the size de  ned by 
the insets.The actual area used for the text is the area of the component decreased by the mount of insets at each 
edge.The deafult insets,if not set explicitly useing setInsets,are 0 at each edge,i.e.no margins. Returns: Insets used 
by this text layout manager. */
public java.awt.Insets getInsets() {
   return insets;
}

/*
Get the letter space setting. Returns: letter space setting */
public int getLetterSpace() {
   return letterspace;
}

/*
Get the line orientation. Returns: Line orientation setting */
public int getLineOrientation() {
   return lineOrientation;
}

/*
Get the line space setting. Returns: line space setting or -1,if the default line spacing is determined from the size of 
the default font is used. */
public int getLineSpace() {
   return linespace;
}

/*
Get the starting corner. Returns: Starting corner setting */
public int getStartCorner() {
   return startCorner;
}

/*
Get the text wrapping setting. Returns:text wrapping setting */
public boolean getTextWrapping() {
   return wrap;
}

/*
Get the vertical alignment. Returns: Vertical alignment setting */
public int getVerticalAlign() {
   return verticalAlign;
}

/*
Removes a TextOver  owListener that has been registered previously. Parameters: l -a listener 
object */
public void removeTextOverflowListener(TextOverflowListener l) {
   overflowListener=TextOverflowMulticaster.remove(overflowListener, l);
}

/*
Render the string.The HTextLayoutManager should use the passed HVisible object to determine any additional information 
required to render the string,e.g.Font Color etc. The text should be laid out in the layout area,which is de  ned by the 
bounds of the speci  ed HVisible ,after subtracting the insets.If the insets are null the full bounding rectangle is 
used as the area to render text into. The HTextLayoutManager should not modify the clipping rectangle of the Graphics 
object. Speci  ed By: render(String, Graphics, HVisible, Insets)in interface HTextLayoutManager Parameters: 
markedUpString -the string to render. g -the graphics context,including a clipping rectangle which encapsulates the area 
within which rendering is permitted.If a valid insets value is passed to this method then text must only be rendered 
into the bounds of the widget after the insets are subtracted.If the insets value is null then text is rendered into the 
entire bounding area of the HVisible .It is implementation speci  c whether or not the renderer takes into account the 
intersection of the clipping rectangle in each case for optimization purposes.v -the HVisible into which to render. 
insets -the insets to determine the area in which to layout the text,or null */
public void render(java.lang.String markedUpString, java.awt.Graphics g,
                   HVisible v, java.awt.Insets paramInsets) {
   /*Rectangle componentField=v.getBounds();
   Rectangle textField;
   if (paramInsets == null)
      //subtract only my personal insets
      textField=new Rectangle(componentField.x+insets.left,
                              componentField.y-insets.top,
                              componentField.width-insets.right,
                              componentField.height-insets.bottom);
   else //subtract given and own insets                                        
      textField=new Rectangle(componentField.x+insets.left+paramInsets.left,
                              componentField.y-insets.top-paramInsets.top,
                              componentField.width-insets.right-paramInsets.right,
                              componentField.height-insets.bottom-paramInsets.bottom);
   if (textField.isEmpty())
      return;

   Color c=v.getForeground();
   Font  f=v.getFont();
   g.setColor(c);
   g.setFont(f);
   FontMetrics fm=g.getFontMetrics();
   
   float wrappingWidth = textField.width;
   java.awt.Point pen=new java.awt.Point(textField.x, textField.y);
   int position=0;

   System.out.println("DVBTextLayoutManager: Starting loop");
   while (position < markedUpString.length()) {
      int lineCharNumber=markedUpString.length()-position;
      int oldNumber=0;
      while ( lineCharNumber - oldNumber > 1) {
         int tempNumber=lineCharNumber;
         int diff=Math.abs(oldNumber-lineCharNumber)/2;
         if (diff==0)
            diff=1;
         if (fm.stringWidth(markedUpString.substring(position, position+lineCharNumber)) > wrappingWidth) {
            lineCharNumber-=diff;
            oldNumber=tempNumber;
         } else {
            lineCharNumber+=diff;
         }
         oldNumber=tempNumber;
      }

      String line=markedUpString.substring(position, position+lineCharNumber);
      position+=lineCharNumber;
      
      pen.y += (fm.getAscent());
      float dx = /*layout.isLeftToRight()* /true ?
          0 : (wrappingWidth - fm.getMaxAdvance());
      
      if (pen.y+fm.getDescent() > textField.x+textField.width)
         break; //line goes deeper than bottom of textField
                //according to spec, do not draw crippled characters

      g.drawString(line, pen.x + (int)dx, pen.y);
      pen.y += fm.getDescent() + fm.getLeading();
   }   
   System.out.println("DVBTextLayoutManager: Loop ended");/*
   
   
   
   //NIST
   
  /** Rendering method from the TextLayoutManager interface.
      The specs require that if the font specified in the Graphics is not
      availabe, then a close match should be used and missing characters
      should be replaced by '!' (rely on AWT in this implementation).
      <br><i>Note:</i> Justification not supported in this version. 
      @param markedUpString the string to display. '\n'=0x0A for new lines.
      @param g Graphics to use for rendering
      @param v target visible (the method queries this object's properties) */
    
    // TODO: Implement justification

    /* For safety */
    if(markedUpString==null) {
      return;
    }
    
    Font fnt = v.getFont();
    FontMetrics fntMetrics = v.getFontMetrics(fnt);
    Dimension dim = v.getSize();
    
    g.setFont(fnt);
    g.setColor(v.getForeground());

    /* Break the string into lines */
    StringTokenizer st = new StringTokenizer(markedUpString, "\n", false);
    int lineCount = st.countTokens();

    /* Currrent 'pen' position */
    int textY;
    int textX;

    /* Query vertical and horizontal border spacings from the
       visible's text look */
    int verticalSpacing = 0;
    int horizontalSpacing = 0;
    if( v.getLook() instanceof org.havi.ui.HTextLook ) {
      java.awt.Insets lookInsets= (v.getLook()).getInsets(v);
      //verticalSpacing = (v.getLook()).getVerticalBorderSpacing();
      //horizontalSpacing = (v.getLook()).getHorizontalBorderSpacing();
      verticalSpacing = lookInsets.left+lookInsets.right/2;
      horizontalSpacing = lookInsets.top+lookInsets.bottom/2;
    }
    
  //A note to the constants: NIST/ATSC and MHP have different alignment constants.
  //While NIST's constants (commented out) are straightforward, 
  //MHP has three levels: Orientation, Start Corner, Alignment where the "straightforward"
  //result depends on all three, e.g. Orientation is LINE_ORIENTATION_HORIZONTAL and
  //start corner START_CORNER_UPPER_LEFT (as with this text), then
  //left alignment is HORIZONTAL_START_ALIGN:
  // Horizontal to orientation, beginning at start corner
  
  //At the moment, only left-to-right-then-top-to-bottom text is taken care of.
    
     /* Decide where the text should start vertically */
     switch(verticalAlign) {
     //case TOP_ALIGN:
     case VERTICAL_START_ALIGN:
       textY = verticalSpacing + 1;
       break;
     //case BOTTOM_ALIGN:
     case VERTICAL_END_ALIGN:
     
       textY = dim.height -
         (fntMetrics.getHeight()+LINE_SPACING)*lineCount + LINE_SPACING
         - verticalSpacing;
       break;
     //case CENTER_VERTICAL:
     case VERTICAL_CENTER:
     //case VERTICAL_JUSTIFY:
     default:
       textY = ( dim.height - (fntMetrics.getHeight()+LINE_SPACING)*lineCount
                                                          + LINE_SPACING ) / 2;
       break;
     }
     /* Adjust textY to indicate the baseline location */
     textY += fntMetrics.getAscent();

     // System.out.println("Rendering \"" + markedUpString + "\" at y=" + textY);
     
     /* Now go ahead and render */
     while (st.hasMoreTokens()) {

       String line = st.nextToken();

       switch(horizontalAlign) {
       //case LEFT_ALIGN:
       case HORIZONTAL_START_ALIGN:
         textX = horizontalSpacing + 1;
         break;
       //case RIGHT_ALIGN:
       case HORIZONTAL_END_ALIGN:
         textX = dim.width - fntMetrics.stringWidth(line) - horizontalSpacing;
         break;
       //case CENTER_HORIZONTAL:
       //case HORIZONTAL_JUSTIFY:   
       case HORIZONTAL_CENTER:
       default:
         textX = ( dim.width - fntMetrics.stringWidth(line) ) / 2;
         break;
       }

       /* Draw */
       g.drawString(line, textX, textY);

       // System.out.println(" Rendered \"" +
       //                   line + "\" at " + textX + "," + textY);
       
       /* Move the pen to next line */
       textY += fntMetrics.getHeight()+LINE_SPACING;

     } // line while

   
}

/*
Set the horizontal alignment. Parameters: horizontalAlign -Horizontal alignment 
setting */
public void setHorizontalAlign(int _horizontalAlign) {
   horizontalAlign=_horizontalAlign;
}

/*
Set the horizontal tabulation spacing. Parameters: horizontalTabSpace -tab spacing in 
points */
public void setHorizontalTabSpacing(int _horizontalTabSpace) {
   horizontalTabSpace=_horizontalTabSpace;
}

/*
Sets the insets that should be used by this text layout manager.The text is rendered to the area de  ned by the area of 
the component decreased by the amount of insets at each edge.If this method is not called,the default insets are 0 at 
each edge. Parameters: insets -Insets that should be used */
public void setInsets(java.awt.Insets _insets) {
   insets=_insets;
}

/*
Set the letter space setting. Parameters: letterSpace -letter space setting */
public void setLetterSpace(int _letterSpace) {
   letterspace=_letterSpace;
}

/*
Set the line orientation. Parameters: lineOrientation -Line orientation setting */
public void setLineOrientation(int _lineOrientation) {
   lineOrientation=_lineOrientation;
}

/*
Set the line space setting.Using -1 as the line space setting shall cause the line spacing to be determined from the 
size of the default font. Parameters: lineSpace -line space setting */
public void setLineSpace(int _lineSpace) {
   linespace=_lineSpace;
}

/*
Set the starting corner. Parameters: startCorner -Starting corner setting */
public void setStartCorner(int _startCorner) {
   startCorner=_startCorner;
}

/*
Set the text wrapping setting Parameters: wrap -Text wrapping setting */
public void setTextWrapping(boolean _wrap) {
   wrap=_wrap;
}

/*
Set the vertical alignment. Parameters: verticalAlign -Vertical alignment setting */
public void setVerticalAlign(int _verticalAlign) {
   verticalAlign=_verticalAlign;
}

  /** Return the preferred size (ie the bounding box for the overall text).
      <br>Note: this is an implementation method that is not part of the
      specs.
      @param markedUpString string to be rendered
      @param visible target HVisible
      @return a Dimension object containing the dimensions of the string's
              bounding box.
  */
public Dimension getPreferredSize(String markedUpString, HVisible visible) {

    FontMetrics fntMetrics = visible.getFontMetrics(visible.getFont());
    String line;
    
    Dimension preferred = new Dimension();

    /* Break the string into lines */
    StringTokenizer st = new StringTokenizer(markedUpString, "\n", false);
    int lineCount = st.countTokens();
    
    preferred.height =
      (fntMetrics.getHeight()+LINE_SPACING)*lineCount - LINE_SPACING;


    
     /* Now find the widest line and update preferred accordingly */
    
    while (st.hasMoreTokens()) {

       line = st.nextToken();

       if(fntMetrics.stringWidth(line) > preferred.width) {
         preferred.width = fntMetrics.stringWidth(line);
       }

    }

    return preferred;
    
}



}
