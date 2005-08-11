package javax.tv.service;

import javax.tv.service.selection.ServiceContextListener;

public class VDRServiceMulticaster implements ServiceContextListener {


public void receiveServiceContextEvent ( javax.tv.service.selection.ServiceContextEvent evt) {
   ((ServiceContextListener)a).receiveServiceContextEvent(evt);
   ((ServiceContextListener)b).receiveServiceContextEvent(evt);
}




protected java.util.EventListener a;
protected java.util.EventListener b;

protected VDRServiceMulticaster(java.util.EventListener head, java.util.EventListener tail) {
   a = head;
   b = tail;
}


public static ServiceContextListener add(ServiceContextListener listeners, ServiceContextListener newListener) {
   return (ServiceContextListener)addInternal( listeners, newListener);
}

public static ServiceContextListener remove(ServiceContextListener l, ServiceContextListener oldl) {
   return (ServiceContextListener) removeInternal( l, oldl);
}




protected static java.util.EventListener addInternal(java.util.EventListener listeners, java.util.EventListener newListener) {
   if ( listeners == null )      // first time
      return newListener;

   if ( newListener == null )    // strange, but check it (wrong order of args?)
      return listeners;

   return new VDRServiceMulticaster( listeners, newListener);
}

protected static java.util.EventListener removeInternal(java.util.EventListener list, java.util.EventListener remListener) {
   if ( (list == null) || (list == remListener) ) // empty list or only listener
      return null;

   if ( list instanceof VDRServiceMulticaster )
      return ((VDRServiceMulticaster)list).remove( remListener);
   
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