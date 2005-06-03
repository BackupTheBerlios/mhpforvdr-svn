package org.dvb.media;

//This is not an API class!

public class MediaMulticaster implements javax.media.ControllerListener  {

public void controllerUpdate(javax.media.ControllerEvent event) {
   ((javax.media.ControllerListener)a).controllerUpdate(event);
   ((javax.media.ControllerListener)b).controllerUpdate(event);
}

protected javax.media.ControllerListener a;
protected javax.media.ControllerListener b;

protected MediaMulticaster(javax.media.ControllerListener head, javax.media.ControllerListener tail) {
   a = head;
   b = tail;
}


public static javax.media.ControllerListener remove(javax.media.ControllerListener l, javax.media.ControllerListener oldl) {
   return (javax.media.ControllerListener) removeInternal( l, oldl);
}

public static javax.media.ControllerListener add(javax.media.ControllerListener listeners, javax.media.ControllerListener newListener) {
   return (javax.media.ControllerListener)addInternal( listeners, newListener);
}



protected static javax.media.ControllerListener addInternal(javax.media.ControllerListener listeners, javax.media.ControllerListener newListener) {
   if ( listeners == null )      // first time
      return newListener;

   if ( newListener == null )    // strange, but check it (wrong order of args?)
      return listeners;

   return new MediaMulticaster( listeners, newListener);
}

protected static javax.media.ControllerListener removeInternal(javax.media.ControllerListener list, javax.media.ControllerListener remListener) {
   if ( (list == null) || (list == remListener) ) // empty list or only listener
      return null;

   if ( list instanceof MediaMulticaster )
      return ((MediaMulticaster)list).remove( remListener);
   
   return list;
}

/*
Removes a listener from this multicaster and returns the resulting multicast 
listener. */
protected javax.media.ControllerListener remove(javax.media.ControllerListener remListener) {
  // check if this refers to our own fields
   if ( remListener == a )
      return b;
   if ( remListener == b )
      return a;
   
   // nope, recursive descent
   javax.media.ControllerListener l1, l2;
   l1 = removeInternal( a, remListener);
   l2 = removeInternal( b, remListener);
   
   // neither a nor b (subtree) had it, so there's nothing to remove at all
   if ( (l1 == a) && (l2 == b) )
      return this;
      
   // Ok, it was in our subtrees, construct a new cell from the mod subtree
   return addInternal( l1, l2);
}

}
