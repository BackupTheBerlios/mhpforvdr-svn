/*
 * NIST/DASE API Reference Implementation
 * $File: NullLayout.java $
 * Last changed on $Date: 2001/05/11 15:44:33 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Container;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Dimension;
import java.awt.Point;

/**
 * AWT Layout Manager that does nothing (simply relies on the current
 * geometry of the components in the container).
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 */

public class NullLayout implements LayoutManager {


  /** Add a component to this layout. Not used here.
      @param name name of the new component
      @param comp component to add */
  public void addLayoutComponent(String name, Component comp) {
  }

  /** Remove a component to this layout. Not used here.
      @param comp component to remove */
  public void removeLayoutComponent(Component comp) {
  }

  /** Return the preferred dimension for this Container based on its content.
      All components are stacked on top of each other, aligned at (0,0).
      The preferred size is simply the largest of all preferred sizes,
      plus any inset.
      @param parent Container to evaluate
      @return the preferred size of this container based on this layout
      specifications. */
  public Dimension preferredLayoutSize(Container parent) {
    
    /* Step one: find out the largest bounding box */
    Dimension maxSize = new Dimension(0,0);
    Component[] allComponents = parent.getComponents();
    Dimension currentSize;
    Point currentLocation;
    
    for(int i=0; i < allComponents.length; i++) {
      currentSize = allComponents[i].getSize();
      currentLocation = allComponents[i].getLocation();
      if( (currentSize.height+currentLocation.y) > maxSize.height ) {
        maxSize.height = (currentSize.height+currentLocation.y);
      }
      if( (currentSize.width+currentLocation.x) > maxSize.width ) {
        maxSize.width = (currentSize.width+currentLocation.x);
      }
    }
    return maxSize;
  }

  /** Return the minimum dimension for this Container based on its content.
      Since their is no layout done at all, same as preferred size
      @param parent Container to evaluate
      @return the minimum size of this container based on this layout
      specifications. */
  public Dimension minimumLayoutSize(Container parent) {
    return preferredLayoutSize(parent);
  }

  /** Actually lays out the components in the container:
      All components are stacked on top of each other, with whatever geometry
      they were initialized with (No sizing, not relocation).
      In other words: do nothing.
      @param parent container to lay out */
  public void layoutContainer(Container parent) {
  }

}
