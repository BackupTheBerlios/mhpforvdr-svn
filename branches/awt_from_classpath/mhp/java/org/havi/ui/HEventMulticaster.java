/* Large parts taken from Kaffe, which is 
 * Copyright (c) 1998 Transvirtual Technologies, Inc.
 * The rest is (c) Marcel Wiesweg, 2003 */



package org.havi.ui;

import org.havi.ui.event.*;

/*The HEventMulticaster class is intended to handle event dispatching for the following HAVi 
events: " HBackgroundImageEvent " HScreenConfigurationEvent " HScreenLocationModifiedEvent 
" HActionEvent " HFocusEvent " HItemEvent " HTextEvent " HKeyEvent " HAdjustmentEvent " 
java.awt.event.WindowEvent " org.davic.resources.ResourceStatusEvent It is an 
implementation option for this class to insert other classes in the inheritance tree (for 
example java.awt.AWTEventMulticaster).It is allowed that this may result in 
HEventMulticaster inheriting additional methods beyond those speci  ed here. Note:the 
org.davic.resources.ResourceStatusListener speci  cation does not require EventListener to 
be present.In a HAVi UI implementation,ResourceStatusListener shall extend EventListener. 
The parameters to the constructors are as follows,in cases where parameters are not 
used,then the constructor should use the default values. */

//TODO: in HScreenConfigurationListener add(HScreenConfigurationListener listeners, HScreenConfigurationListener newListener, 
//      HScreenConfigTemplate tb), the HScreenConfigTemplate is ignored

public class HEventMulticaster implements HBackgroundImageListener, HScreenConfigurationListener,
                                           HScreenLocationModifiedListener, java.awt.event.WindowListener,
                                            HActionListener, HAdjustmentListener, HFocusListener,
                                             HItemListener, HTextListener, HKeyListener,
                                              org.davic.resources.ResourceStatusListener {

/*
 */
protected final java.util.EventListener a;


/*
 */
protected final java.util.EventListener b;


/*
Creates an event multicaster instance which chains listener-a with listener-b. Parameters: a -listener-a b 
-listener-b */
protected HEventMulticaster(java.util.EventListener head, java.util.EventListener tail) {
   a = head;
   b = tail;
}

/*
Handles the ActionEvent by invoking the actionPerformed methods on listener-a and listener-b. Speci  ed By: 
java.awt.event.ActionListener.actionPerformed(java.awt.event.ActionEvent)in interface java.awt.event.ActionListener 
Parameters: e -the ActionEvent event */
public void actionPerformed(java.awt.event.ActionEvent e) {
   ((java.awt.event.ActionListener)a).actionPerformed( e);
   ((java.awt.event.ActionListener)b).actionPerformed( e);
}

/*
Adds HActionListener -a with HActionListener -b and returns the resulting multicast listener. Parameters: a 
-HActionListener-a b -HActionListener-b */
public static HActionListener add(HActionListener listeners, HActionListener newListener) {
   return (HActionListener)addInternal( listeners, newListener);
}

/*
Adds HAdjustmentListener -a with HAdjustmentListener -b and returns the resulting multicast listener. Parameters: a 
-HAdjustmentListener-a b -HAdjustmentListener-b */
public static HAdjustmentListener add(HAdjustmentListener listeners, HAdjustmentListener newListener) {
   return (HAdjustmentListener)addInternal( listeners, newListener);
}

/*
Adds HBackgroundImageListener -a with HBackgroundImageListener -b and returns the resulting multicast listener. 
Parameters: a -HBackgroundImageListener-a b -HBackgroundImageListener-b */
public static HBackgroundImageListener add(HBackgroundImageListener listeners, HBackgroundImageListener newListener) {
   return (HBackgroundImageListener)addInternal( listeners, newListener);
}

/*
Adds HFocusListener -a with HFocusListener -b and returns the resulting multicast listener. Parameters: a 
-HFocusListener-a b -HFocusListener-b */
public static HFocusListener add(HFocusListener listeners, HFocusListener newListener) {
   return (HFocusListener)addInternal( listeners, newListener);
}

/*
Adds HItemListener -a with HItemListener -b and returns the resulting multicast listener. Parameters: a -HItemListener-a 
b -HItemListener-b */
public static HItemListener add(HItemListener listeners, HItemListener newListener) {
   return (HItemListener)addInternal( listeners, newListener);
}

/*
Adds HKeyListener -a with HKeyListener -b and returns the resulting multicast 
listener. */
public static HKeyListener add(HKeyListener listeners, HKeyListener newListener) {
   return (HKeyListener)addInternal( listeners, newListener);
}

/*
Adds HScreenConfigurationListener -a with HScreenConfigurationListener -b and returns the resulting multicast 
listener. */
public static HScreenConfigurationListener add(HScreenConfigurationListener listeners, HScreenConfigurationListener newListener) {
   return (HScreenConfigurationListener)addInternal( listeners, newListener);
}

/*
Adds HScreenConfigurationListener -a with HScreenConfigurationListener -b,which is noti  ed when the HScreenDevice con  
guration is modi  ed so that it is no longer compatible with the HScreenConfigTemplate tb.It returns the resulting 
multicast listener. Parameters: a -HScreenCon  gurationListener-a b -HScreenCon  gurationListener-b tb -HScreenCon  
gTemplate associated with HScreenCon  gurationListener-b */
public static HScreenConfigurationListener add(HScreenConfigurationListener listeners, HScreenConfigurationListener newListener, 
HScreenConfigTemplate tb) {
   return (HScreenConfigurationListener)addInternal( listeners, newListener);
}

/*
Adds HScreenLocationModifiedListener -a with HScreenLocationModifiedListener - b and returns the resulting multicast 
listener. Parameters: a -HScreenLocationModi  edListener-a b -HScreenLocationModi  
edListener-b */
public static HScreenLocationModifiedListener add(HScreenLocationModifiedListener listeners, 
                                                   HScreenLocationModifiedListener newListener) {
   return (HScreenLocationModifiedListener)addInternal( listeners, newListener);
}

/*
Adds HTextListener -a with HTextListener -b and returns the resulting multicast listener. Parameters: a -HTextListener-a 
b -HTextListener-b */
public static HTextListener add(HTextListener listeners, HTextListener newListener) {
   return (HTextListener)addInternal( listeners, newListener);
}

/*
Adds ResourceStatusListener-a with listener-b and returns the resulting multicast listener.In a HAVi UI 
implementation,ResourceStatusListener shall extend EventListener. */
public static org.davic.resources.ResourceStatusListener add(org.davic.resources.ResourceStatusListener listeners, 
org.davic.resources.ResourceStatusListener newListener) {
   return (org.davic.resources.ResourceStatusListener)addInternal( listeners, newListener);
}

/*
Adds WindowListener-a with WindowListener-b and returns the resulting multicast listener. Parameters: a 
-WindowListener-a b -WindowListener-b */
public static java.awt.event.WindowListener add(java.awt.event.WindowListener listeners, java.awt.event.WindowListener newListener) {
   return (java.awt.event.WindowListener)addInternal( listeners, newListener);
}

/*
Returns the resulting multicast listener from adding listener-a and listener-b together.If listener-a is null,it returns 
listener-b;If listener-b is null,it returns listener-a If neither are null,then it creates and returns a new 
HEventMulticaster instance which chains a with b. */
protected static java.util.EventListener addInternal(java.util.EventListener listeners, java.util.EventListener newListener) {
   if ( listeners == null )      // first time
      return newListener;

   if ( newListener == null )    // strange, but check it (wrong order of args?)
      return listeners;

   // Note that we don't check against multiple adds of the same listener. Would be
   // reasonable, but the spec doesn't clarify this, and Suns impl obviously allows it

   return new HEventMulticaster( listeners, newListener);
}

/*
Handles the HTextEvent by invoking the caretMoved(HTextEvent)methods on listener-a and listener-b. Speci  ed By: 
caretMoved(HTextEvent)in interface HTextListener */
public void caretMoved(HTextEvent e) {
   ((HTextListener)a).caretMoved( e);
   ((HTextListener)b).caretMoved( e);
}

/*
Handles the HItemEvent by invoking the currentItemChanged(HItemEvent)methods on listener-a and listener-b. Speci  ed By: 
currentItemChanged(HItemEvent)in interface HItemListener */
public void currentItemChanged(HItemEvent e) {
   ((HItemListener)a).currentItemChanged( e);
   ((HItemListener)b).currentItemChanged( e);
}

/*
Handles the FocusEvent by invoking the focusGained methods on listener-a and listener-b. Speci  ed By: 
java.awt.event.FocusListener.focusGained(java.awt.event.FocusEvent)in interface 
java.awt.event.FocusListener */
public void focusGained(java.awt.event.FocusEvent e) {
   ((java.awt.event.FocusListener)a).focusGained( e);
   ((java.awt.event.FocusListener)b).focusGained( e);
}

/*
Handles the FocusEvent by invoking the focusLost methods on listener-a and listener-b. Speci  ed By: 
java.awt.event.FocusListener.focusLost(java.awt.event.FocusEvent)in interface 
java.awt.event.FocusListener */
public void focusLost(java.awt.event.FocusEvent e) {
   ((java.awt.event.FocusListener)a).focusLost( e);
   ((java.awt.event.FocusListener)b).focusLost( e);
}

/*
Handles the HBackgroundImageEvent by invoking the imageLoaded(HBackgroundImageEvent)methods on listener-a and 
listener-b. */
public void imageLoaded(HBackgroundImageEvent e) {
   ((HBackgroundImageListener)a).imageLoaded( e);
   ((HBackgroundImageListener)b).imageLoaded( e);
}

/*
Handles the HBackgroundImageEvent by invoking the imageLoadFailed(HBackgroundImageEvent)methods on listener-a and 
listener-b. */
public void imageLoadFailed(HBackgroundImageEvent e) {
   ((HBackgroundImageListener)a).imageLoadFailed( e);
   ((HBackgroundImageListener)b).imageLoadFailed( e);
}

/*
Handles the HKeyEvent by invoking the keyPressed methods on listener-a and listener-b. Speci  ed By: 
java.awt.event.KeyListener.keyPressed(java.awt.event.KeyEvent)in interface java.awt.event.KeyListener */
public void keyPressed(java.awt.event.KeyEvent e) {
   ((HKeyListener)a).keyPressed( e);
   ((HKeyListener)b).keyPressed( e);
}

/*
Handles the HKeyEvent by invoking the keyReleased methods on listener-a and listener-b. Speci  ed By: 
java.awt.event.KeyListener.keyReleased(java.awt.event.KeyEvent)in interface 
java.awt.event.KeyListener */
public void keyReleased(java.awt.event.KeyEvent e) {
   ((HKeyListener)a).keyReleased( e);
   ((HKeyListener)b).keyReleased( e);
}

/*
Handles the HKeyEvent by invoking the keyTyped methods on listener-a and listener-b. Speci  ed By: 
java.awt.event.KeyListener.keyTyped(java.awt.event.KeyEvent)in interface java.awt.event.KeyListener Parameters: e -the 
HKeyEvent event */
public void keyTyped(java.awt.event.KeyEvent e) {
   ((HKeyListener)a).keyTyped( e);
   ((HKeyListener)b).keyTyped( e);
}

/*
Removes a listener from this multicaster and returns the resulting multicast 
listener. */
protected java.util.EventListener remove(java.util.EventListener remListener) {
  // check if this refers to our own fields
   if ( remListener == a )
      return b;
   if ( remListener == b )
      return a;
   
   // nope, recursive descent
   java.util.EventListener l1, l2;
   l1 = removeInternal( a, remListener);
   l2 = removeInternal( b, remListener);
   
   // neither a nor b (subtree) had it, so there's nothing to remove at all
   if ( (l1 == a) && (l2 == b) )
      return this;
      
   // Ok, it was in our subtrees, construct a new cell from the mod subtree
   return addInternal( l1, l2);
}

/*
Removes the old HActionListener from HActionListener -l and returns the resulting multicast 
listener. */
public static HActionListener remove(HActionListener l, HActionListener oldl) {
   return (HActionListener) removeInternal( l, oldl);
}

/*
Removes the old HAdjustmentListener from HAdjustmentListener -l and returns the resulting multicast listener. 
Parameters: l -HAdjustmentListener-l oldl -the HAdjustmentListener being removed */
public static HAdjustmentListener remove(HAdjustmentListener l, HAdjustmentListener 
oldl) {
   return (HAdjustmentListener) removeInternal( l, oldl);
}

/*
Removes the old HBackgroundImageListener from HBackgroundImageListener -l and returns the resulting multicast listener. 
Parameters: l -HBackgroundImageListener-l oldl -the HBackgroundImageListener being 
removed */
public static HBackgroundImageListener remove(HBackgroundImageListener l, HBackgroundImageListener 
oldl) {
   return (HBackgroundImageListener) removeInternal( l, oldl);
}

/*
Removes the old HFocusListener from HFocusListener -l and returns the resulting multicast listener. Parameters: l 
-HFocusListener-l oldl -the HFocusListener being removed */
public static HFocusListener remove(HFocusListener l, HFocusListener oldl) {
   return (HFocusListener) removeInternal( l, oldl);
}

/*
Removes the old HItemListener from HItemListener -l and returns the resulting multicast listener. Parameters: l 
-HItemListener-l oldl -the HItemListener being removed */
public static HItemListener remove(HItemListener l, HItemListener oldl) {
   return (HItemListener) removeInternal( l, oldl);
}

/*
Removes the old HKeyListener from HKeyListener -l and returns the resulting multicast 
listener. */
public static HKeyListener remove(HKeyListener l, HKeyListener oldl) {
   return (HKeyListener) removeInternal( l, oldl);
}

/*
Removes the old HScreenConfigurationListener from HScreenConfigurationListener -l and returns the resulting multicast 
listener. */
public static HScreenConfigurationListener remove(HScreenConfigurationListener l, HScreenConfigurationListener 
oldl) {
   return (HScreenConfigurationListener) removeInternal( l, oldl);
}

/*
Removes the old HScreenLocationModifiedListener from HScreenLocationModifiedListener -l and returns the resulting 
multicast listener. */
public static HScreenLocationModifiedListener remove(HScreenLocationModifiedListener l, HScreenLocationModifiedListener 
oldl) {
   return (HScreenLocationModifiedListener) removeInternal( l, oldl);
}

/*
Removes the old HTextListener from HTextListener -l and returns the resulting multicast listener. Parameters: l 
-HTextListener-l oldl -the HTextListener being removed */
public static HTextListener remove(HTextListener l, HTextListener oldl) {
   return (HTextListener) removeInternal( l, oldl);
}

/*
Removes the old ResourceStatusListener from ResourceStatusListener-l and returns the resulting multicast listener.In a 
HAVi UI implementation,ResourceStatusListener shall extend EventListener. */
public static org.davic.resources.ResourceStatusListener remove(org.davic.resources.ResourceStatusListener l, 
org.davic.resources.ResourceStatusListener oldl) {
   return (org.davic.resources.ResourceStatusListener) removeInternal( l, oldl);
}

/*
Removes the old WindowListener from WindowListener-l and returns the resulting multicast 
listener. */
public static java.awt.event.WindowListener remove(java.awt.event.WindowListener l, java.awt.event.WindowListener 
oldl) {
   return (java.awt.event.WindowListener) removeInternal( l, oldl);
}

/*
Returns the resulting multicast listener after removing the old listener from listener-l.If listener-l equals the old 
listener OR listener-l is null,returns null.Else if listener-l is an instance of HEventMulticaster,then it removes the 
old listener from it.Else,returns listener l. Parameters: l -the listener being removed from oldl -the listener being 
removed */
protected static java.util.EventListener removeInternal(java.util.EventListener list, java.util.EventListener 
remListener) {
   if ( (list == null) || (list == remListener) ) // empty list or only listener
      return null;

   if ( list instanceof HEventMulticaster )
      return ((HEventMulticaster)list).remove( remListener);
   
   return list;
}

/*
Handles the HScreenConfigurationEvent by invoking the report(HScreenConfigurationEvent)methods on listener-a and 
listener-b. Speci  ed By: report(HScreenConfigurationEvent)in interface HScreenConfigurationListener */
public void report(HScreenConfigurationEvent e) {
   ((HScreenConfigurationListener)a).report( e);
   ((HScreenConfigurationListener)b).report( e);
}

/*
Handles the HScreenLocationModifiedEvent by invoking the report(HScreenLocationModifiedEvent)methods on listener-a and 
listener-b. */
public void report(HScreenLocationModifiedEvent e) {
   ((HScreenLocationModifiedListener)a).report( e);
   ((HScreenLocationModifiedListener)b).report( e);
}

/*
Handles the HItemEvent by invoking the selectionChanged(HItemEvent)methods on listener-a and 
listener-b. */
public void selectionChanged(HItemEvent e) {
   ((HItemListener)a).selectionChanged( e);
   ((HItemListener)b).selectionChanged( e);
}

/*
Handles the ResourceStatusEvent by invoking the statusChanged methods on listener-a and 
listener-b. */
public void statusChanged(org.davic.resources.ResourceStatusEvent e) {
   ((org.davic.resources.ResourceStatusListener)a).statusChanged( e);
   ((org.davic.resources.ResourceStatusListener)b).statusChanged( e);
}

/*
Handles the HTextEvent by invoking the textChanged(HTextEvent)methods on listener-a and 
listener-b. */
public void textChanged(HTextEvent e) {
   ((HTextListener)a).textChanged( e);
   ((HTextListener)b).textChanged( e);
}

/*
Handles the HAdjustmentEvent by invoking the valueChanged(HAdjustmentEvent) methods on listener-a and 
listener-b. */
public void valueChanged(HAdjustmentEvent e) {
   ((HAdjustmentListener)a).valueChanged( e);
   ((HAdjustmentListener)b).valueChanged( e);
}

/*
Handles the windowActivated event by invoking the windowActivated methods on listener-a and listener-b. Speci  ed By: 
java.awt.event.WindowListener.windowActivated(java.awt.event.WindowEvent)in interface 
java.awt.event.WindowListener */
public void windowActivated(java.awt.event.WindowEvent e) {
   ((java.awt.event.WindowListener)a).windowActivated( e);
   ((java.awt.event.WindowListener)b).windowActivated( e);
}

/*
Handles the windowClosed event by invoking the windowClosed methods on listener-a and listener- b. Speci  ed By: 
java.awt.event.WindowListener.windowClosed(java.awt.event.WindowEvent)in interface 
java.awt.event.WindowListener */
public void windowClosed(java.awt.event.WindowEvent e) {
   ((java.awt.event.WindowListener)a).windowClosed( e);
   ((java.awt.event.WindowListener)b).windowClosed( e);
}

/*
Handles the windowClosing event by invoking the windowClosing methods on listener-a and listener- b. Speci  ed By: 
java.awt.event.WindowListener.windowClosing(java.awt.event.WindowEvent)in interface 
java.awt.event.WindowListener */
public void windowClosing(java.awt.event.WindowEvent e) {
   ((java.awt.event.WindowListener)a).windowClosing( e);
   ((java.awt.event.WindowListener)b).windowClosing( e);
}

/*
Handles the windowDeactivated event by invoking the windowDeactivated methods on listener-a and listener-b. Speci  ed 
By: java.awt.event.WindowListener.windowDeactivated(java.awt.event.WindowEvent)in interface 
java.awt.event.WindowListener */
public void windowDeactivated(java.awt.event.WindowEvent e) {
   ((java.awt.event.WindowListener)a).windowDeactivated( e);
   ((java.awt.event.WindowListener)b).windowDeactivated( e);
}

/*
Handles the windowDeiconi  ed event by invoking the windowDeiconi  ed methods on listener-a and listener-b. Speci  ed 
By: java.awt.event.WindowListener.windowDeiconi  ed(java.awt.event.WindowEvent)in interface 
java.awt.event.WindowListener Parameters: e -the window event */
public void windowDeiconified(java.awt.event.WindowEvent e) {
   ((java.awt.event.WindowListener)a).windowDeiconified( e);
   ((java.awt.event.WindowListener)b).windowDeiconified( e);
}

/*
Handles the windowIconi  ed event by invoking the windowIconi  ed methods on listener-a and listener-b. Speci  ed By: 
java.awt.event.WindowListener.windowIconi  ed(java.awt.event.WindowEvent)in interface java.awt.event.WindowListener 
Parameters: e -the window event */
public void windowIconified(java.awt.event.WindowEvent e) {
   ((java.awt.event.WindowListener)a).windowIconified( e);
   ((java.awt.event.WindowListener)b).windowIconified( e);
}

/*
Handles the windowOpened event by invoking the windowOpened methods on listener-a and listener-b. Speci  ed By: 
java.awt.event.WindowListener.windowOpened(java.awt.event.WindowEvent)in interface 
java.awt.event.WindowListener */
public void windowOpened(java.awt.event.WindowEvent e) {
   ((java.awt.event.WindowListener)a).windowOpened( e);
   ((java.awt.event.WindowListener)b).windowOpened( e);
}


}
