
package org.havi.ui;
import java.awt.Image;
import java.util.Hashtable;

/*The HVisible class is the base class for all non-interactive components. If a layout 
manager is associated with the Container into which a HVisible component is placed,the 
size and location of the component will be controlled by the layout manager. HVisible 
provides the following features for the use of subclasses: " support for interaction 
states. " a mechanism to associate the component with a pluggable HLook class to which all 
drawing is delegated. " support for state-related content which is drawn by the associated 
HLook . " support for scalable and alignable content. " support for private look data. " 
support for preferred sizes when used with a layout manager. " control over the background 
painting behavior. " a pluggable text layout management mechanism. Some of these features 
are discussed in more detail below. (page 1173) ... */

public class HVisible extends HComponent implements HState {


// _CHANGE constants


/*
A constant which indicates the  rst change value for use with the hinting mechanism. */
public static final int FIRST_CHANGE = 0;


/*
A constant which indicates the last de  ned value for use with the hinting mechanism. */
public static final int LAST_CHANGE = 19;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the value of an HAdjustmentValue component has changed.The value for this hint is a java.lang.Integer 
which contains the old index. */
public static final int ADJUSTMENT_VALUE_CHANGE = 13;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the animated content has changed. The value for this hint is an array,java.lang.Object[9]which contains 
the state for which the content changed (a java.lang.Integer and the old content (a java.awt.Image[]for all 8 
states. */
public static final int ANIMATE_CONTENT_CHANGE = 2;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the caret position has changed.The value for this hint is a java.lang.Integer which has the value of the 
old caret position. */
public static final int CARET_POSITION_CHANGE = 5;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the miscellaneous content has changed.The value for this hint is an array,java.lang.Object[9]which 
contains the state for which the content changed (a java.lang.Integer and the old content (a java.lang.Object for all 8 
states. */
public static final int CONTENT_CHANGE = 3;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the echo character has changed.The value for this hint is a java.lang.Character which has the value of 
the old echo character. */
public static final int ECHO_CHAR_CHANGE = 6;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the editing mode has changed.The value for this hint is a java.lang.Boolean which has the value of the 
old edit mode. */
public static final int EDIT_MODE_CHANGE = 7;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the graphical content has changed. The value for this hint is an array,java.lang.Object[9]which contains 
the state for which the content changed (a java.lang.Integer and the old content (a java.awt.Image for all 8 
states. */
public static final int GRAPHIC_CONTENT_CHANGE = 1;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the value of an HItemValue component has changed.The value for this hint is a java.lang.Integer which 
contains the old value. */
public static final int ITEM_VALUE_CHANGE = 12;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the content of an HListGroup component has changed.The value for this hint is a java.lang.Integer which 
contains the old content. */
public static final int LIST_CONTENT_CHANGE = 14;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the iconsize of an HListGroup component has changed.The value for this hint is a java.lang.Integer which 
contains the old size. */
public static final int LIST_ICONSIZE_CHANGE = 15;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the labelsize of an HListGroup component has changed.The value for this hint is a java.lang.Integer which 
contains the old size. */
public static final int LIST_LABELSIZE_CHANGE = 16;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the multiselection setting of an HListGroup component has changed.The value for this hint is a 
java.lang.Integer which contains the old setting. */
public static final int LIST_MULTISELECTION_CHANGE = 17;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the scrollposition of an HListGroup component has changed.The value for this hint is a java.lang.Integer 
which contains the old position. */
public static final int LIST_SCROLLPOSITION_CHANGE = 18;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the range of an HAdjustmentValue component has changed.The value for this hint is an array, 
java.lang.Object[2]which contains the old minimum and maximum extents of the range as 
java.lang.Integer */
public static final int MIN_MAX_CHANGE = 8;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the orientation of an HOrientable component has changed.The value for this hint is a java.lang.Integer 
which has the value of the old orientation. */
public static final int ORIENTATION_CHANGE = 10;

/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the interaction state has changed. The value for this hint is a java.lang.Integer which has the value of 
the old state. */
public static final int STATE_CHANGE = 4;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the text content has changed.The value for this hint is an array,java.lang.Object[9]which contains the 
state for which the content changed (a java.lang.Integer and the old content (a java.lang.String for all 8 
states. */
public static final int TEXT_CONTENT_CHANGE = 0;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the value of an HTextValue component has changed.The value for this hint is a java.lang.Integer which 
contains the old value. */
public static final int TEXT_VALUE_CHANGE = 11;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that the thumb offsets of an HAdjustmentValue component have changed.The value for this hint is an array, 
java.lang.Object[2]which contains the old minimum and maximum thumb offsets as java.lang.Integer */
public static final int THUMB_OFFSETS_CHANGE = 9;


/*
A constant for use with the hinting mechanism (see the widgetChanged(HVisible, HChangeData[])method in HLook ).This hint 
indicates that some unspeci  ed change has occurred.The value for this hint is a java.lang.Integer which also has the 
value UNKNOWN_CHANGE. */
public static final int UNKNOWN_CHANGE = 19;





// HALIGN_  constants

/*
A constant for use with the setHorizontalAlignment(int)method of HVisible which indicates that content should be 
centered horizontally. */
public static final int HALIGN_CENTER = 1;


/*
A constant for use with the setHorizontalAlignment(int)method of HVisible which indicates that content should be fully 
justi  ed (horizontally) */
public static final int HALIGN_JUSTIFY = 3;


/*
A constant for use with the setHorizontalAlignment(int)method of HVisible which indicates that content should be left 
aligned. */
public static final int HALIGN_LEFT = 0;


/*
A constant for use with the setHorizontalAlignment(int)method of HVisible which indicates that content should be right 
aligned. */
public static final int HALIGN_RIGHT = 2;


// _FILL constants


/*
A constant for use with the setBackgroundMode(int)method of HVisible which indicates that an associated HLook should  ll 
the bounding rectangle of the HVisible with its current background color before drawing any content.Any previous content 
will be erased during the repainting of the HVisible . */
public static final int BACKGROUND_FILL = 1;


/*
A constant for use with the setBackgroundMode(int)method of HVisible which indicates that an associated HLook should not 
 ll the bounding rectangle of the HVisible with its current background color before drawing any content.Therefore any 
previous content will NOT necessarily be erased during the repainting of the HVisible */
public static final int NO_BACKGROUND_FILL = 0;



// NO_DEFAULT constants


/*
A constant for use with the setDefaultSize(Dimension)and getDefaultSize()methods of HVisible which indicates that no 
default height is desired for the HVisible . */
public static final int NO_DEFAULT_HEIGHT = -1;


/*
A constant for use with the setDefaultSize(Dimension)and getDefaultSize()methods of HVisible which indicates that no 
default width is desired for the HVisible . */
public static final int NO_DEFAULT_WIDTH = -1;



/*
A constant for use with the setDefaultSize(Dimension)and getDefaultSize()methods of HVisible which indicates that no 
default size is desired for the HVisible . */
                                                         //simply check with ==
public static final java.awt.Dimension NO_DEFAULT_SIZE = new java.awt.Dimension(NO_DEFAULT_WIDTH, NO_DEFAULT_HEIGHT);


// RESIZE_ constants

/*
A constant for use with the setResizeMode(int)method of HVisible which indicates that content should be scaled to  t the 
component.Aspect ratios of the content need not be preserved. */
public static final int RESIZE_ARBITRARY = 2;


/*
A constant for use with the setResizeMode(int)method of HVisible which indicates that content should not be scaled to  t 
the component. */
public static final int RESIZE_NONE = 0;


/*
A constant for use with the setResizeMode(int)method of HVisible which indicates that content should be scaled to  t the 
component while preserving the aspect ratio of the content.Areas of the component that are not  lled by the content will 
be look dependent. */
public static final int RESIZE_PRESERVE_ASPECT = 1;


// VALIGN_ constants


/*
A constant for use with the setVerticalAlignment(int)method of HVisible which indicates that content should be 
vertically aligned to the bottom of the component. */
public static final int VALIGN_BOTTOM = 8;


/*
A constant for use with the setVerticalAlignment(int)method of HVisible which indicates that content should be centered 
vertically. */
public static final int VALIGN_CENTER = 4;


/*
A constant for use with the setVerticalAlignment(int)method of HVisible which indicates that content should be fully 
justi  ed (vertically). */
public static final int VALIGN_JUSTIFY = 12;


/*
A constant for use with the setVerticalAlignment(int)method of HVisible which indicates that content should be 
vertically aligned to the top of the component. */
public static final int VALIGN_TOP = 0;

protected HMatte matte=null;
protected HTextLayoutManager textManager=new HDefaultTextLayoutManager();
protected int backgroundMode=NO_BACKGROUND_FILL;
protected HLook look = null;
protected java.awt.Dimension defaultDim=NO_DEFAULT_SIZE;
protected int state = NORMAL_STATE;
protected int align = HALIGN_LEFT;
protected int valign = VALIGN_CENTER;
protected int resizeMode = RESIZE_ARBITRARY;

/*protected String textContent[];
protected java.awt.Image[] animateContent[]; //array of arrays
protected Object content[];
protected java.awt.Image graphicContent[];*/

protected java.util.HashMap hashmap;



/*
Creates an HVisible component with no HLook .See the class description for details of constructor parameters and default 
values. */
public HVisible() {
   Initialize();
}

/*
Creates an HVisible component with the given HLook .See the class description for details of constructor parameters and 
default values. Parameters: hlook -The HLook associated with the HVisible component. */
public HVisible(HLook hlook) {
   Initialize();
   look=hlook;
}

/*
Creates an HVisible component with the given HLook and the speci  ed location and size.See the class description for 
details of constructor parameters and default values. Parameters: hlook -The HLook associated with the HVisible 
component. x -the x-coordinate of the HVisible component within its Container. y -the y-coordinate of the HVisible 
component within its Container. width -the width of the HVisible component in pixels. height -the height of the HVisible 
component in pixels. */
public HVisible(HLook hlook, int x, int y, int width, int height) {
   super(x,y, width, height);
   Initialize();
   look=hlook;
}

//internal helper
private void FillArrayWithEightNull(Object[] ar) {
   for (int i=0;i<8;i++)
      ar[i]=null;
}

private void Initialize() {
/*   textContent=new String[8];
   animateContent=new java.awt.Image[8][];
   content = new Object[8];
   graphicContent = new java.awt.Image[8];
   
   FillArrayWithEightNull(textContent);
   FillArrayWithEightNull(animateContent); //array of arrays
   FillArrayWithEightNull(content);
   FillArrayWithEightNull(graphicContent);*/
   hashmap=new java.util.HashMap();
}

/*
Gets the animate content for this component. Parameters: state -The state for which content is to be retrieved.Note that 
content is set on the XXX_STATE constants de  ned in HState ,and not on the XXX_STATE_BIT constants.A 
java.lang.IllegalArgumentException will be thrown if a STATE_BIT rather than a STATE is passed. Returns: The animate 
content associated with the speci  ed state.If no animate content has been set for the speci  ed state,then null is 
returned. */
/*public java.awt.Image[] getAnimateContent(int _state) {
   if (!checkState(_state))
      throw new IllegalArgumentException("Illegal state");
   return (java.awt.Image[])getObjectFromArray(animateContent, _state);
}*/

/*
Get the background mode of this HVisible .The return value speci  es how the look should draw the background (i.e.a 
rectangle  lling the bounds of the HVisible ). Returns: one of NO_BACKGROUND_FILL or BACKGROUND_FILL 
. */
public int getBackgroundMode() {
   return backgroundMode;
}

/*
Gets the content for this component. Parameters: state -The state for which content is to be retrieved.Note that content 
is set on the XXX_STATE constants de  ned in HState ,and not on the XXX_STATE_BIT constants.A 
java.lang.IllegalArgumentException will be thrown if a STATE_BIT rather than a STATE is passed. Returns: The content 
associated with the speci  ed state.If no content has been set for the speci  ed state,then null is 
returned. */
/*public java.lang.Object getContent(int _state) {
   if (!checkState(_state))
      throw new IllegalArgumentException("Illegal state");
   return getObjectFromArray(content, _state);
}*/

/*
Returns the default preferred size to be used for this component when a layout manager is in use.If no default size has 
been set using the setDefaultSize(Dimension) method NO_DEFAULT_SIZE is returned. If the parent Container into which the 
HVisible is placed has no layout manager the default preferred size has no effect. The default size of a component is to 
be interpreted as the area in which the component can be rendered,excluding look-speci  c borders. Returns: the default 
preferred size to be used for this component when a layout manager is in use. */
public java.awt.Dimension getDefaultSize() {
   return defaultDim;
}

/*
Gets the graphic content for this component. Parameters: state -The state for which content is to be retrieved.Note that 
content is set on the XXX_STATE constants de  ned in HState ,and not on the XXX_STATE_BIT constants.A 
java.lang.IllegalArgumentException will be thrown if a STATE_BIT rather than a STATE is passed. Returns: The graphical 
content associated with the speci  ed state.If no graphical content has been set for the speci  ed state,then null is 
returned. */
/*public java.awt.Image getGraphicContent(int _state) {
   if (!checkState(_state))
      throw new IllegalArgumentException("Illegal state");
   return (java.awt.Image)getObjectFromArray(animateContent, _state);
}*/

/*
Get the horizontal alignment of any state-based content rendered by an associated HLook .If content is not used in the 
rendering of this HVisible the value returned shall be valid,but has no affect on the rendered representation. Returns: 
the current horizontal alignment mode,one of HALIGN_LEFT ,HALIGN_CENTER , HALIGN_RIGHT or HALIGN_JUSTIFY 
. */
public int getHorizontalAlignment() {
   return align;
}

/*
Return the interaction state the component is currently in. Returns: the interaction state the component is currently 
in. See Also: HState */
public int getInteractionState() {
   return state;
}

/*
Gets the HLook for this component. Returns: the HLook that is being used by this component -if no HLook has been 
set,then returns null. */
public HLook getLook() {
   return look;
}

/*
Retrieve a look-speci  c private data object.Instances of HLook may use this method to retrieve private data (e.g.layout 
hints,cached images etc.)from the HVisible.Use of this mechanism is an implementation option.If this mechanism is not 
used by an implementation,or no data has been set for the speci  ed key this method returns null Parameters: key -an 
object which uniquely identi  es the type of look for which the private data is to be retrieved.Keys need not be unique 
across different instances of the same look class. Returns: a private data object as previously set using 
setLookData(Object, Object),or null See Also: HLook setLookData(Object, Object) */
public java.lang.Object getLookData(java.lang.Object key) {
   return hashmap.get(key);
}

/*
Gets the maximum size of the HVisible .The getMaximumSize method of the HLook that is associated with this HVisible will 
be called to calculate the dimensions. Overrides: java.awt.Component.getMaximumSize()in class java.awt.Component 
Returns: A dimension object indicating this HVisible 's maximum size -if no HLook has been associated with the HVisible 
,then the current HVisible dimensions as determined with getSize will be returned. See Also: 
getMaximumSize(HVisible) */
public java.awt.Dimension getMaximumSize() {
   if (look != null)
      return look.getMaximumSize(this);
   else
      return getSize();
}

/*
Gets the minimum size of the HVisible .The getMinimumSize method of the HLook that is associated with this HVisible will 
be called to calculate the dimensions. Overrides: java.awt.Component.getMinimumSize()in class java.awt.Component 
Returns: A dimension object indicating this HVisible 's minimum size -if no HLook has been associated with the HVisible 
,then the current HVisible dimensions as determined with getSize will be returned. See Also: 
getMinimumSize(HVisible) */
public java.awt.Dimension getMinimumSize() {
   if (look != null)
      return look.getMinimumSize(this);
   else
      return getSize();
}

/*
Gets the preferred size of the HVisible .The getPreferredSize method of the HLook that is associated with this HVisible 
will be called to calculate the dimensions. Overrides: java.awt.Component.getPreferredSize()in class java.awt.Component 
Returns: A dimension object indicating this HVisible 's preferred size -if no HLook has been associated with the 
HVisible ,then the current HVisible dimensions as determined with getSize will be 
returned. */
public java.awt.Dimension getPreferredSize() {
   if (look != null)
      return look.getPreferredSize(this);
   else
      return getSize();
}

/*
Get the scaling mode for scaling any state-based content rendered by an associated HLook .If content is not used in the 
rendering of this HVisible the value returned shall be valid,but has no affect on the rendered representation. Returns: 
the current scaling mode,one of RESIZE_NONE ,RESIZE_PRESERVE_ASPECT or RESIZE_ARBITRARY */
public int getResizeMode() {
   return resizeMode;
}

/*
Gets the text content for this component. Parameters: state -The state for which content is to be retrieved.Note that 
content is set on the XXX_STATE constants de  ned in HState ,and not on the XXX_STATE_BIT constants.A 
java.lang.IllegalArgumentException will be thrown if a STATE_BIT rather than a STATE is passed. Returns: The text 
content associated with the speci  ed state.If no text content has been set for the speci  ed state,then null is 
returned. */
/*public java.lang.String getTextContent(int _state) {
   if (!checkState(_state))
      throw new IllegalArgumentException("Illegal state");
   return (String)getObjectFromArray(textContent, _state);
}*/

/*
Gets the text layout manager that is being used to layout this text.Returns:The HTextLayoutManager that is being used by 
this component. */
public HTextLayoutManager getTextLayoutManager() {
   return textManager;
}

/*
Get the vertical alignment of any state-based content rendered by an associated HLook .If content is not used in the 
rendering of this HVisible the value returned shall be valid,but has no affect on the rendered representation. Returns: 
the current vertical alignment mode,one of VALIGN_TOP ,VALIGN_CENTER , VALIGN_BOTTOM or 
VALIGN_JUSTIFY. */
public int getVerticalAlignment() {
   return valign;
}

/*
By default an HVisible component is not focus-traversable. Overrides: java.awt.Component.isFocusTraversable()in class 
java.awt.Component Returns: false See Also: java.awt.Component.isFocusTraversable() */
public boolean isFocusTraversable() {
   return false;
}

/*
Returns true if the entire HVisible area,as given by the java.awt.Component#getBounds method, is fully opaque,i.e.its 
HLook guarantees that all pixels are painted in an opaque Color. This method will call the isOpaque(HVisible)method of 
an associated HLook if one is set.If no HLook is associated this method returns false. The default return value is 
implementation speci  c and depends on the background painting mode of the given HVisible .The consequences of an 
invalid overridden value are implementation speci  c. Speci  ed By: isOpaque()in interface TestOpacity Overrides: 
isOpaque()in class HComponentReturns: true if all the pixels with the java.awt.Component#getBounds method are fully 
opaque,i.e.its associated HLook guarantees that all pixels are painted in an opaque 
Color. */
public boolean isOpaque() {
   if (look != null)
      return look.isOpaque(this);
   else
      return false;
}

/*
Draws the current state of the component,by calling the showLook(Graphics, HVisible, int)method of the associated HLook 
.If no HLook is associated with the component,(i.e.the HVisible was created with a null HLook or the look has been set 
to null using setLook(HLook))then the paint method should do nothing.This mechanism may be used for components that wish 
to extend HVisible ,and override the paint method,without supporting the HLook 
interface. */
public void paint(java.awt.Graphics g) {
   if (look != null)
      look.showLook(getGraphics(), this, state);
}

/*
Sets an array of graphical content (primarily used for animation),per state.Different (single array of)content can be 
associated with the different states of a component. Note that the content is not copied,merely its object reference. If 
the HVisible has an associated HLook ,then it should repaint itself. Parameters: imageArray -An array of images that 
make up the animation.If the array is null,then any currently assigned content shall be removed for the speci  ed state. 
state -The state of the component for which this content should be displayed.Note that content is set on the XXX_STATE 
constants de  ned in HState ,and not on the XXX_STATE_BIT constants.A java.lang.IllegalArgumentException will be thrown 
if a STATE_BIT rather than a STATE is passed. */
/*public void setAnimateContent(java.awt.Image[] imageArray, int _state) {
   if (!checkState(_state))
      throw new IllegalArgumentException("Illegal state");
   animateContent[stateToIndex(_state)]=imageArray;
}*/

/*
Set the background drawing mode.The value speci  es how the look should draw the background (i.e.a rectangle  lling the 
bounds of the HVisible ). Parameters: mode -one of NO_BACKGROUND_FILL or BACKGROUND_FILL */
public void setBackgroundMode(int mode) {
   backgroundMode=mode;
}

/*
Sets a single piece of content for this component,per state.Different (single pieces of)content can be associated with 
the different states of a component. Note that the content is not copied,merely its object reference. If the HVisible 
has an associated HLook ,then it should repaint itself. Parameters: object -The content.If the content is null,then any 
currently assigned content shall be removed for the speci  ed state. state -The state of the component for which this 
content should be displayed.Note that content is set on the XXX_STATE constants de  ned in HState ,and not on the 
XXX_STATE_BIT constants.A java.lang.IllegalArgumentException will be thrown if a STATE_BIT rather than a STATE is 
passed. */
/*public void setContent(java.lang.Object object, int _state) {
   if (!checkState(_state))
      throw new IllegalArgumentException("Illegal state");
   content[stateToIndex(_state)]=object;
}*/

/*
Set the preferred default size for this component when a layout manager is in use.If the parent Container into which the 
HVisible is placed has no layout manager this method has no effect.Note that the size set with this method is not a 
guaranteed size;if set it will be passed to the layout manager through the getPreferredSize(HVisible)method.The default 
size of a component is to be interpreted as the area in which the component can be rendered,excluding look-speci  c 
borders. Valid arguments include NO_DEFAULT_SIZE ,and Dimensions containing NO_DEFAULT_WIDTH or NO_DEFAULT_HEIGHT 
Parameters: defaultSize -speci  es the default preferred size.If this parameter is null a java.lang.NullPointerException 
will be thrown. If this parameter or speci  es a size smaller than an implementation-de  ned minimum size a 
java.lang.IllegalArgumentException will be thrown. */
public void setDefaultSize(java.awt.Dimension defaultSize) {
   if (defaultSize == null)
      throw new NullPointerException();
   if (defaultSize == NO_DEFAULT_SIZE || defaultSize.width == NO_DEFAULT_WIDTH || defaultSize.height == NO_DEFAULT_HEIGHT)
      defaultDim=NO_DEFAULT_SIZE;
   else
      defaultDim=defaultSize;
}

/*
Sets a single piece of graphical content for this component,per state.Different (single pieces of) content can be 
associated with the different states of a component. Note that the content is not copied,merely its object reference. If 
the HVisible has an associated HLook ,then it should repaint itself. Parameters: image -The content.If the content is 
null,then any currently assigned content shall be removed for the speci  ed state. state -The state of the component for 
which this content should be displayed.Note that content is set on the XXX_STATE constants de  ned in HState ,and not on 
the XXX_STATE_BIT constants.A java.lang.IllegalArgumentException will be thrown if a STATE_BIT rather than a STATE is 
passed. */
/*public void setGraphicContent(java.awt.Image image, int _state) {
   if (!checkState(_state))
      throw new IllegalArgumentException("Illegal state");
   graphicContent[stateToIndex(_state)]=image;
}*/

/*
Set the horizontal alignment of any state-based content rendered by an associated HLook .If content is not used in the 
rendering of this HVisible calls to this method shall change the current alignment mode,but this will not affect the 
rendered representation. Parameters: halign -the new horizontal alignment mode,one of HALIGN_LEFT ,HALIGN_CENTER , 
HALIGN_RIGHT or HALIGN_JUSTIFY */
public void setHorizontalAlignment(int _halign) {
   align=_halign;
}

/*
Set the interaction state for this component.This method is provided for the use by subclasses of HVisible to change the 
interaction state of the HVisible .Subclasses MUST NOT manipulate the state by any other mechanism. Attempts to set 
states which are not valid for the subclass will cause an java.lang.IllegalArgumentException to be thrown.See the class 
descriptions of each component for the de  nitions of which states are valid.Parameters: state -the interaction state 
for this component.A java.lang.IllegalArgumentException will be thrown if a STATE_BIT rather than a STATE is passed. See 
Also: HState */
protected void setInteractionState(int _state) {
   if ( !checkState(_state)
         || (_state != NORMAL_STATE && _state != DISABLED_STATE) ) //this constraint goes for plain (unsubclassed) HVisibles
      throw new IllegalArgumentException("Illegal state");
   state=state;
}

/*
Sets the HLook for this component. Parameters: hlook -The HLook that is to be used for this component.Note that this 
parameter may be null, in which case the component will not draw itself until a look is set. Throws: 
HInvalidLookException -If the Look is not compatible with this type of component,for example a graphic look being set on 
a text component,an HInvalidLookException is thrown. Note that HVisible itself will never throw this exception,but it 
may be thrown by a subclass which has overridden this method. */
public void setLook(HLook hlook) throws HInvalidLookException {
   look=hlook;
}

/*
Set a look-speci  c private data object.Instances of HLook may use this method to set private data (e.g.layout 
hints,cached images etc.)on the HVisible.Use of this mechanism is an implementation option.If this mechanism is not used 
by an implementation,this method will have no effect and calls to getLookData(Object)shall return null Parameters: key 
-an object which uniquely identi  es the type of look for which the private data is to be retrieved.Keys need not be 
unique across different instances of the same look class. data -a private data object,or null to remove any current 
object set on this HVisible. See Also: HLook getLookData(Object) */
public void setLookData(java.lang.Object key, java.lang.Object data) {
   if (key == null || data == null)
      hashmap.clear();
   else
      hashmap.put(key, data);
}

/*
Set the scaling mode for scaling any state-based content rendered by an associated HLook .If content is not used in the 
rendering of this HVisible calls to this method shall change the current alignment mode,but this will not affect the 
rendered representation. Parameters: resize -the new scaling mode,one of RESIZE_NONE ,RESIZE_PRESERVE_ASPECT or 
RESIZE_ARBITRARY */
public void setResizeMode(int resize) {
   resizeMode=resize;
}

/*
Sets a single piece of text content for this component,per state.Different (single pieces of)content can be associated 
with the different states of a component.Note that unlike setGraphicContent(Image, int),setAnimateContent(Image[], 
int)andsetContent(Object, int),the content is copied as it is not possible to store a reference to a java.lang.String If 
the HVisible has an associated HLook ,then it should repaint itself. Parameters: string -The content.If the content is 
null,then any currently assigned content shall be removed for the speci  ed state. state -The state of the component for 
which this content should be displayed.Note that content is set on the XXX_STATE constants de  ned in HState ,and not on 
the XXX_STATE_BIT constants.A java.lang.IllegalArgumentException will be thrown if a STATE_BIT rather than a STATE is 
passed. */
/*public void setTextContent(java.lang.String string, int _state) {
   if (!checkState(_state))
      throw new IllegalArgumentException("Illegal state");
   textContent[stateToIndex(_state)]=new String(string);
}*/

/*
Sets the text layout manager that should be used to layout the text for this component. Parameters: manager -the 
HTextLayoutManager to be used by this component. */
public void setTextLayoutManager(HTextLayoutManager manager) {
   textManager=manager;
}

/*
Set the vertical alignment of any state-based content rendered by an associated HLook .If content is not used in the 
rendering of this HVisible calls to this method shall change the current alignment mode,but this will not affect the 
rendered representation. Parameters: valign -the new vertical alignment mode,one of VALIGN_TOP ,VALIGN_CENTER , 
VALIGN_BOTTOM or VALIGN_JUSTIFY . */
public void setVerticalAlignment(int _valign) {
   valign=_valign;
}

/*
The update()method in HVisible overrides that in Component and does not clear the background of the component,it simply 
modi  es the current Color of the Graphics object to match that of the components background Color,and calls the 
paint()method. Overrides: java.awt.Component.update(java.awt.Graphics)in class java.awt.Component Parameters: g -the 
graphics context to use for updating. */
public void update(java.awt.Graphics g) {
   g.setColor(getBackground());
   paint(g);
}
/*
//internal helper
private int stateToIndex(int state) {
   switch (state) {
   case NORMAL_STATE:
      return 0;
   case ACTIONED_STATE:
      return 1;
   case DISABLED_STATE:
      return 2;
   case FOCUSED_STATE:
      return 3;
   case ACTIONED_FOCUSED_STATE:
      return 4;
   case DISABLED_ACTIONED_STATE:
      return 5;
   case DISABLED_FOCUSED_STATE:
      return 6;
   case DISABLED_ACTIONED_FOCUSED_STATE:
      return 7;
   default:
      return 0;
   }
}*/

//internal helper
//returns true if _state is a valid HState constant
private boolean checkState(int _state) {
   return ( (_state >= FIRST_STATE) && (_state <= LAST_STATE) );
}
/*
//returns state data or nearest matching data, according to the spec
private Object getObjectFromArray( Object ar[], int state) {
   int index=stateToIndex(state);
   switch (state) {
   default:
   case NORMAL_STATE:
      return ar[index];
   case ACTIONED_STATE:
      return ar[index]!=null ? ar[index] : getObjectFromArray(ar,FOCUSED_STATE);
   case DISABLED_STATE:
      return ar[index]!=null ? ar[index] : getObjectFromArray(ar,NORMAL_STATE);
   case FOCUSED_STATE:
      return ar[index]!=null ? ar[index] : getObjectFromArray(ar,NORMAL_STATE);
   case ACTIONED_FOCUSED_STATE:
      return ar[index]!=null ? ar[index] : getObjectFromArray(ar,FOCUSED_STATE);
   case DISABLED_ACTIONED_STATE:
      return ar[index]!=null ? ar[index] : getObjectFromArray(ar,ACTIONED_STATE);
   case DISABLED_FOCUSED_STATE:
      return ar[index]!=null ? ar[index] : getObjectFromArray(ar,DISABLED_STATE);
   case DISABLED_ACTIONED_FOCUSED_STATE:
      return ar[index]!=null ? ar[index] : getObjectFromArray(ar,DISABLED_STATE);
   }
}*/

  /** Hash table for textual content */
  private Hashtable textContentTable = new Hashtable();
  
  /** Hash table for graphic content */
  private Hashtable graphicContentTable = new Hashtable();

  /** Hash table for animated content (array of images) */
  private Hashtable animateContentTable = new Hashtable();

  /** Hash table for generic content */
  private Hashtable userContentTable = new Hashtable();

  
  /** Associate a textual content element to a specific state.
      This also triggers an update of the widget.
      @param string string to associate with <code>state</code>; if null,
                    remove the current content. Stored as a reference
      @param state state from HState */

  public void setTextContent(String string, int state) {

    genericSetContent(textContentTable, string, state);
    /* Update the display. */
    this.repaint();
  }


  
  /** Associate a graphical content element to a specific state.
      This also triggers an update of the widget.
      @param image image to associate with <code>state</code>; if null,
                    remove the current content. Store as a reference.
      @param state state from HState */

  public void setGraphicContent(Image image, int state) {
    
    genericSetContent(graphicContentTable, image, state);
    /* Update the display. */
    this.repaint();
  }
    
  
  /** Associate animated content element to a specific state.
      This also triggers an update of the widget.
      @param imageArray animation list to associate with <code>state</code>;
      if null, remove the current content. Store as a reference.
      @param state state from HState */

  public void setAnimateContent(Image[] imageArray, int state) {
    
    genericSetContent(animateContentTable, imageArray, state);
    /* Update the display. */
    this.repaint();
    
  }
  
  
  
  /** Associate random content element to a specific state.
      This also triggers an update of the widget.
      @param object object to associate with <code>state</code>; if null,
                    remove the current content. Store as a reference.
                    @param state state from HState */

  public void setContent(Object object, int state) {

    //Graphics g;

    genericSetContent(userContentTable, object, state);
    /* Update the display. */
    this.repaint();

  }


  /** Generic association to a specific state.
      @param hash   target hash table
      @param object object to associate with <code>state</code>; if null,
                    remove the current content. Store as a reference.
      @param state  state from HState, if ALL_STATES, set/unset for all */

  private void genericSetContent(Hashtable hash, Object object, int state) {

    Integer stateInt = new Integer(state);
    
    if(state == HState.ALL_STATES) {
      hash.clear();
    }
    if(object==null) {
      hash.remove(stateInt);
    } else {
      hash.put(stateInt, object);
    }
    
  }

  
  /** Retrieve textual content for a specific state
      @param state state to query
      @return textual content or null if none */
  public String getTextContent(int state) {

      return (String)genericGetContent(textContentTable, state);

  }


  /** Retrieve graphical content for a specific state
      @param state state to query
      @return graphical content or null if none */
  public Image getGraphicContent(int state) {

    return (Image)genericGetContent(graphicContentTable, state);
    
  }


    /** Retrieve animated content for a specific state
      @param state state to query
      @return animated content or null if none */
  public Image[] getAnimateContent(int state) {

    return (Image[])genericGetContent(animateContentTable, state);
    
  }


  /** Retrieve user-defined content for a specific state
      @param state state to query
      @return content or null if none */
  public Object getContent(int state) {

    return genericGetContent(userContentTable, state);
    
  }
  
  /** Generic association retrieval for a specific state.
      @param hash   source hash table
      @param state  state from HState */
  private Object genericGetContent(Hashtable hash, int state) {
    Integer stateInt = new Integer(state);
    Object value = hash.get(stateInt);
    if( value == null ) {
      stateInt = new Integer(HState.ALL_STATES);
      return hash.get(stateInt);
    } else {
      return value;
    }
  }


}
