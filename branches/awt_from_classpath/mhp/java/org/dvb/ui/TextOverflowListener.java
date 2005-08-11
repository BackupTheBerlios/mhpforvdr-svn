
package org.dvb.ui;

import org.havi.ui.HVisible;

/*The TextOver  owListener is an interface that an application may implement and register in 
the DVBTextLayoutManager.This listener will be noti  ed if the text string does not  t 
within the component when rendering it. */

public interface TextOverflowListener {

/*
This method is called by the DVBTextLayoutManager if the text does not  t within the component Parameters: 
markedUpString -the string that was rendered v -the HVisible object that was being rendered overflowedHorizontally -true 
if the text over  ew the bounds of the component in the horizontal direction;otherwise false overflowedVertically -true 
if the text over  ew the bounds of the component in the vertical direction;otherwise 
false */
public void notifyTextOverflow(java.lang.String markedUpString, HVisible v, boolean overflowedHorizontally, boolean 
overflowedVertically);



}
