package org.dvb.application;

//This is not an API class!

public class DatabaseMulticaster implements AppsDatabaseEventListener, AppStateChangeEventListener {

protected void multicastAppsDatabaseEvent(AppsDatabaseEvent evt) {
   switch (evt.getEventId()) {
   case AppsDatabaseEvent.APP_ADDED:
      entryAdded(evt);
      break;
   case AppsDatabaseEvent.APP_CHANGED:
      entryChanged(evt);
      break;
   case AppsDatabaseEvent.APP_DELETED:
      entryRemoved(evt);
      break;
   case AppsDatabaseEvent.NEW_DATABASE:
      newDatabase(evt);
      break;
   }
}

public void entryAdded(AppsDatabaseEvent evt) {
   ((AppsDatabaseEventListener)a).entryAdded(evt);
   ((AppsDatabaseEventListener)b).entryAdded(evt);
}

public void entryChanged(AppsDatabaseEvent evt) {
   ((AppsDatabaseEventListener)a).entryChanged(evt);
   ((AppsDatabaseEventListener)b).entryChanged(evt);
}

public void entryRemoved(AppsDatabaseEvent evt) {
   ((AppsDatabaseEventListener)a).entryRemoved(evt);
   ((AppsDatabaseEventListener)b).entryRemoved(evt);
}

public void newDatabase(AppsDatabaseEvent evt) {
   ((AppsDatabaseEventListener)a).newDatabase(evt);
   ((AppsDatabaseEventListener)b).newDatabase(evt);
}

public void stateChange(AppStateChangeEvent evt) {
   ((AppStateChangeEventListener)a).stateChange(evt);
   ((AppStateChangeEventListener)b).stateChange(evt);
}

protected java.util.EventListener a;
protected java.util.EventListener b;

protected DatabaseMulticaster(java.util.EventListener head, java.util.EventListener tail) {
   a = head;
   b = tail;
}


public static AppsDatabaseEventListener add(AppsDatabaseEventListener listeners, AppsDatabaseEventListener newListener) {
   return (AppsDatabaseEventListener)addInternal( listeners, newListener);
}

public static AppsDatabaseEventListener remove(AppsDatabaseEventListener l, AppsDatabaseEventListener oldl) {
   return (AppsDatabaseEventListener) removeInternal( l, oldl);
}



public static AppStateChangeEventListener remove(AppStateChangeEventListener l, AppStateChangeEventListener oldl) {
   return (AppStateChangeEventListener) removeInternal( l, oldl);
}

public static AppStateChangeEventListener add(AppStateChangeEventListener listeners, AppStateChangeEventListener newListener) {
   return (AppStateChangeEventListener)addInternal( listeners, newListener);
}



protected static java.util.EventListener addInternal(java.util.EventListener listeners, java.util.EventListener newListener) {
   if ( listeners == null )      // first time
      return newListener;

   if ( newListener == null )    // strange, but check it (wrong order of args?)
      return listeners;

   return new DatabaseMulticaster( listeners, newListener);
}

protected static java.util.EventListener removeInternal(java.util.EventListener list, java.util.EventListener remListener) {
   if ( (list == null) || (list == remListener) ) // empty list or only listener
      return null;

   if ( list instanceof DatabaseMulticaster )
      return ((DatabaseMulticaster)list).remove( remListener);
   
   return list;
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

}
