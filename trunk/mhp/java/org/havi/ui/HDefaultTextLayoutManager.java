
package org.havi.ui;

/*The HDefaultTextLayoutManager provides the default text rendering mechanism for the 
HStaticText HText and HTextButton classes. The HDefaultTextLayoutManager handles alignment 
and justi  cation of text in both horizontal and vertical directions as speci  ed by the 
current alignment modes set on HVisible .It does not support scaling of text content,and 
the scaling mode of an associated HVisible is ignored. The string passed to the 
render(String, Graphics, HVisible, Insets)method may be multi-line,where each line is 
separated by a "\n"(0x0A).If the string does not  t in the space available, the string 
shall be truncated and an ellipsis ("...")appended to indicate the truncation. The 
HDefaultTextLayoutManager should query the HVisible passed to its render(String, Graphics, 
HVisible, Insets)method to determine the basic font to render text in.If the speci  ed 
font cannot be accessed the default behavior is to replace it with the nearest builtin 
font.Each missing character is replaced with an "!"character. The antialiasing behavior of 
HDefaultTextLayoutManager is platform dependent. */

//simply subclass DVBTextLayoutManager, no need to write yet another class.
public class HDefaultTextLayoutManager extends org.dvb.ui.DVBTextLayoutManager implements HTextLayoutManager {

/*
Creates an HDefaultTextLayoutManager object.See the class description for details of constructor parameters and default 
values. */
public HDefaultTextLayoutManager() {
   super();
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
public void render(java.lang.String markedUpString, java.awt.Graphics g, HVisible v, java.awt.Insets 
insets) {
   super.render(markedUpString, g, v, insets);
}


}
