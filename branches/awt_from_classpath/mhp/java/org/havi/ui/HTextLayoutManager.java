
package org.havi.ui;

/*The HTextLayoutManager class manages the layout and rendering on-screen of a "marked-up" 
string. Possible implementations of HTextLayoutManager could enable the following 
behaviors: " Interpreting basic markup, such as changing color or font, and forced line 
breaks. " Providing text alignment, as in the HDefaultTextLayoutManager . " Providing text 
wrapping policies, such as word-wrap. " Providing text orientations, such as 
right-to-left, or top-to-bottom rendering. " Providing specialized support for missing 
characters, or fonts. " Providing specific language support. " Additional text styles, 
such as drop capitals or "shadow" characters. HTextLayoutManager supports passing a 
java.awt.Insets object as argument to the render(String, Graphics, HVisible, Insets)method 
to restrict the area in which text may be rendered.If the insets are zero,the text is 
rendered into the area de  ned by the bounds of the HVisible passed to the render(String, 
Graphics, HVisible, Insets)method. (page 1132) ... */

public interface HTextLayoutManager {

/*
Render the string.The HTextLayoutManager should use the passed HVisible object to determine any additional information 
required to render the string,e.g.Font Color etc. The text should be laid out in the layout area,which is de  ned by the 
bounds of the speci  ed HVisible ,after subtracting the insets.If the insets are null the full bounding rectangle is 
used as the area to render text into. The HTextLayoutManager should not modify the clipping rectangle of the Graphics 
object. Parameters: markedUpString -the string to render. g -the graphics context,including a clipping rectangle which 
encapsulates the area within which rendering is permitted.If a valid insets value is passed to this method then text 
must only be rendered into the bounds of the widget after the insets are subtracted.If the insets value is null then 
text is rendered into the entire bounding area of the HVisible .It is implementation speci  c whether or not the 
renderer takes into account the intersection of the clipping rectangle in each case for optimization 
purposes. */
public void render(java.lang.String markedUpString, java.awt.Graphics g, HVisible v, java.awt.Insets 
insets);



}
