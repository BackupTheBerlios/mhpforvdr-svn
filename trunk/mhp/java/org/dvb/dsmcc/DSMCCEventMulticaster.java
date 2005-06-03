package org.dvb.dsmcc;


//This is the usual EventMulticaster implementation.
//See org.havi.ui.HEventMulticaster for comments on the implementation

public class DSMCCEventMulticaster implements ObjectChangeEventListener {

protected final java.util.EventListener a;
protected final java.util.EventListener b;

protected DSMCCEventMulticaster(java.util.EventListener head, java.util.EventListener tail) {
   a = head;
   b = tail;
}


public void receiveObjectChangeEvent(ObjectChangeEvent e) {
   ((ObjectChangeEventListener)a).receiveObjectChangeEvent(e);
   ((ObjectChangeEventListener)b).receiveObjectChangeEvent(e);
}


public static ObjectChangeEventListener add(ObjectChangeEventListener listeners, ObjectChangeEventListener newListener) {
   return (ObjectChangeEventListener)addInternal(listeners, newListener);
}

public static ObjectChangeEventListener remove(ObjectChangeEventListener listeners, ObjectChangeEventListener newListener) {
   return (ObjectChangeEventListener)removeInternal(listeners, newListener);
}


protected static java.util.EventListener addInternal(java.util.EventListener listeners, java.util.EventListener newListener) {
   if ( listeners == null )      // first time
      return newListener;

   if ( newListener == null )    // strange, but check it (wrong order of args?)
      return listeners;

   // Note that we don't check against multiple adds of the same listener. Would be
   // reasonable, but the spec doesn't clarify this, and Suns impl obviously allows it

   return new DSMCCEventMulticaster( listeners, newListener);
}

protected static java.util.EventListener removeInternal(java.util.EventListener list, java.util.EventListener 
remListener) {
   if ( (list == null) || (list == remListener) ) // empty list or only listener
      return null;

   if ( list instanceof DSMCCEventMulticaster )
      return ((DSMCCEventMulticaster)list).remove( remListener);
   
   return list;
}

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



}