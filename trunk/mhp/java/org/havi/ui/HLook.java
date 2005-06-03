
package org.havi.ui;

/*The HLook interface de  nes the "look"of a component and may be regarded as a mechanism to allow a "pluggable"paint 
method to be attached to the component.Instead of having to subclass the entire component to change its look,it is 
possible to simply implement an HLook that will render the component "look"and then associate this HLook implementation 
with the component. Borders An implementation of HLook will also include code to draw implementation-speci  c 
borders.The application or component can query the reserved space for an HLook with the getInsets(HVisible)method. Since 
the border area is included in the overall size of the component,the border effectively constrains the area available 
for rendering content to the rectangle which has an upper-left corner location of (insets.left, insets.top),and has a 
size of width - (insets.left + insets.right)by height - (insets.top + insets.bottom). Invocation Mechanism The 
showLook(Graphics, HVisible, int)method of the HLook interface will be called by the havi.ui framework in response to 
the paint method of the HVisible being called by the AWT lightweight component framework.Applications should simply 
invoke the component repaint method as in normal AWT,rather than calling the showLook(Graphics, HVisible, int)method 
directly.The conditions under which the showLook(Graphics, HVisible, int)method shall be invoked include the following: 
" If the class implements one of the following interfaces, the showLook(Graphics, HVisible, int) method shall be invoked 
when the trigger events specified in the table below are processed: (continue page 967 of the 
spec) */

public interface HLook extends java.lang.Cloneable {

/*
Determines the insets of this HLook ,which indicate the size of the border.This area is reserved for the HLook to use 
for drawing borders around the associated HVisible . Parameters: hvisible -HVisible to which this HLook is 
attached. */
public java.awt.Insets getInsets(HVisible visible);


/*
Gets the maximum size of the HVisible component when drawn with this HLook . This size may be determined in several ways 
depending on the information available to the look. These steps are performed in order and the  rst available result is 
returned.For the purposes of this algorithm HLook classes that do not use content (e.g.HRangeLook )are treated as if no 
content was present. */
public java.awt.Dimension getMaximumSize(HVisible hvisible);


/*
Gets the minimum size of the HVisible component when drawn with this HLook . This size may be determined in several ways 
depending on the information available to the look. These steps are performed in order and the  rst available result is 
returned.For the purposes of this algorithm HLook classes that do not use content (e.g.HRangeLook )are treated as if no 
content was present. The extra space required for border decoration can be determined from the 
getInsets(HVisible)method. 1. If the HLook supports the scaling of its content (e.g. an HGraphicLook ) and content is 
set then the return value is the size of the smallest piece of content plus any additional dimensions that the HLook 
requires for border decoration etc. 2. If the HLook does not support scaling of content or no scaling is requested, and 
content is set then the return value is the size of the largest piece of content plus any additional dimensions that the 
HLook requires for border decoration etc. 3. If no content is available but a default preferred size has been set using 
setDefaultSize(Dimension) has been called to set then the return value is this value (as obtained with getDefaultSize() 
) plus any additional dimensions that the HLook requires for border decora-tion etc. 4. If there is no content or 
default size set then the return value is an implementation-specific minimum size plus any additional dimensions that 
the HLook requires for border decoration etc. Parameters: hvisible -HVisible to which this HLook is attached. Returns: A 
dimension object indicating this HLook minimum size. */
public java.awt.Dimension getMinimumSize(HVisible hvisible);


/*
Gets the preferred size of the HVisible component when drawn with this HLook . This size may be determined in several 
ways depending on the information available to the look. These steps are performed in order and the  rst available 
result is returned.For the purposes of this algorithm HLook classes that do not use content (e.g.HRangeLook )are treated 
as if no content was present. The extra space required for border decoration can be determined from the 
getInsets(HVisible)method. 1. If a default preferred size has been set for this HVisible (using 
setDefaultSize(Dimension) ) then the return value is this size (obtained with getDefaultSize() ) plus any additional 
dimensions that the HLook requires for border decoration etc. 2. If this HLook does not support scaling of content or no 
scaling is requested, and content is present then the return value is the size of the largest piece of content plus any 
additional dimensions that the HLook requires for border decoration etc. 3. If this HLook supports the scaling of its 
content (e.g. an HGraphicLook ) and content is set then the return value is the current size of the HVisible as returned 
by getSize ). 4. If there is no content and no default size set then the return value is the current size of the 
HVisible as returned by getSize ). Parameters: hvisible -HVisible to which this HLook is attached. Returns: A dimension 
object indicating the preferred size of the HVisible when drawn with this HLook */
public java.awt.Dimension getPreferredSize(HVisible hvisible);


/*
Returns true if the entire painted area of the HVisible when using this look is fully opaque,i.e.the showLook(Graphics, 
HVisible, int)method guarantees that all pixels are painted in an opaque Color. The default value is implementation 
speci  c and depends on the background painting mode of the given HVisible .The consequences of an invalid overridden 
value are implementation speci  c. Parameters: visible -the visible to test Returns: true if all the pixels with the 
java.awt.Component#getBounds method of an HVisible using this look are fully opaque,i.e.the showLook(Graphics, HVisible, 
int)method guarantees that all pixels are painted in an opaque Color,otherwise 
false. */
public boolean isOpaque(HVisible visible);


/*
The showLook(Graphics, HVisible, int)method is responsible for repainting the entire HVisible component,(including any 
content set on the component,and the component background),subject to the clipRect of the Graphics object passed to it. 
The showLook(Graphics, HVisible, int)method should not modify the clipRect of the Graphics object that is passed to it. 
For looks which draw content (e.g.HTextLook ,HGraphicLook and HAnimateLook ),if no content is associated with the 
component,the showLook(Graphics, HVisible, int)method paints the component with its current background Color according 
to the setBackgroundMode(int)method of HVisible and draws any (implementation-speci  c) borders.Note that by default the 
background mode is set so as to not paint a background. Furthermore on platforms which support transparent colors the 
background Color may be partially or completely transparent. Any resources explicitly associated with an HLook should be 
loaded by the HLook during its creation,etc.,or via its setXXX()methods.Note that the "standard"looks don't load content 
by default. This method is called from the paint(Graphics)method of HVisible and must never be called from 
elsewhere.Components wishing to redraw themselves should call their repaint method in the usual way. Parameters: g -the 
graphics context. visible -the visible. state -the state parameter indicates the state of the visible,allowing the look 
to render the appropriate content for that state.Note that some components (e.g.HStaticRange,HRange, HRangeValue)do not 
use state-based content). */
public void showLook(java.awt.Graphics g, HVisible visible, int state);


/*
Called by the HVisible whenever its content,state,or any other data changes.See the class description of HVisible for 
more information about the changes parameter. The implementation of this method should work out which graphical areas of 
the HVisible have changed and make any relevant calls to trigger the repainting of those areas. A minimum implementation 
of this method could simply call visible.repaint() Parameters: visible -the HVisible which has changed changes -an array 
containing hint data and associated hint objects.If this argument is null a full repaint will be 
triggered. */
public void widgetChanged(HVisible visible, HChangeData[] changes);



}
