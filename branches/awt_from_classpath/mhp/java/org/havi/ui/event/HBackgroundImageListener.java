
package org.havi.ui.event;

/*The listener interface for receiving events related to HBackgroundImage 
objects. */

public interface HBackgroundImageListener extends java.util.EventListener {

/*
Invoked when the data for an HBackgroundImage has been loaded. Parameters: e -the event describing the 
loading */
public void imageLoaded(HBackgroundImageEvent e);


/*
Invoked when loading of an HBackgroundImage fails. Parameters: e -the event describing the 
failure */
public void imageLoadFailed(HBackgroundImageEvent e);



}
