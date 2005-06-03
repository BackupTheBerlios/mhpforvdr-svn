
package org.dvb.ui;

import org.havi.ui.HVisible;

/*The TextOver  owListener is an interface that an application may implement and register in 
the DVBTextLayoutManager.This listener will be noti  ed if the text string does not  t 
within the component when rendering it. */

public class TextOverflowMulticaster implements TextOverflowListener {

protected TextOverflowListener a;
protected TextOverflowListener b;

protected TextOverflowMulticaster(TextOverflowListener head, TextOverflowListener tail) {
   a = head;
   b = tail;
}

public static TextOverflowListener add(TextOverflowListener listeners, TextOverflowListener newListener) {
   return addInternal( listeners, newListener);
}

public static TextOverflowListener remove(TextOverflowListener l, TextOverflowListener oldl) {
   return (TextOverflowListener) removeInternal( l, oldl);
}

/*
This method is called by the DVBTextLayoutManager if the text does not  t within the component Parameters: 
markedUpString -the string that was rendered v -the HVisible object that was being rendered overflowedHorizontally -true 
if the text over  ew the bounds of the component in the horizontal direction;otherwise false overflowedVertically -true 
if the text over  ew the bounds of the component in the vertical direction;otherwise 
false */
public void notifyTextOverflow(java.lang.String markedUpString, HVisible v, boolean overflowedHorizontally, boolean 
overflowedVertically) {
   a.notifyTextOverflow(markedUpString, v, overflowedHorizontally, overflowedVertically);
   b.notifyTextOverflow(markedUpString, v, overflowedHorizontally, overflowedVertically);
}

protected static TextOverflowListener addInternal(TextOverflowListener listeners, TextOverflowListener newListener) {
   if ( listeners == null )      // first time
      return newListener;

   if ( newListener == null )    // strange, but check it (wrong order of args?)
      return listeners;

   // Note that we don't check against multiple adds of the same listener. Would be
   // reasonable, but the spec doesn't clarify this, and Suns impl obviously allows it

   return new TextOverflowMulticaster( listeners, newListener);
}

protected static TextOverflowListener removeInternal(TextOverflowListener list, TextOverflowListener remListener) {
   if ( (list == null) || (list == remListener) ) // empty list or only listener
      return null;

   if ( list instanceof TextOverflowMulticaster )
      return ((TextOverflowMulticaster)list).remove( remListener);
   
   return list;
}

/*
Removes a listener from this multicaster and returns the resulting multicast 
listener. */
protected TextOverflowListener remove(TextOverflowListener remListener) {
  // check if this refers to our own fields
   if ( remListener == a )
      return b;
   if ( remListener == b )
      return a;
   
   // nope, recursive descent
   TextOverflowListener l1, l2;
   l1 = removeInternal( a, remListener);
   l2 = removeInternal( b, remListener);
   
   // neither a nor b (subtree) had it, so there's nothing to remove at all
   if ( (l1 == a) && (l2 == b) )
      return this;
      
   // Ok, it was in our subtrees, construct a new cell from the mod subtree
   return addInternal( l1, l2);
}

}
