package org.dvb.event.chain;
import org.dvb.event.UserEvent;


public class FilterChain {

public FilterChain() {
}

FilterChainElement head = null;
FilterChainElement tail = null;
int length=0;

//taken from Kaffe, java.util.LinkedList
public void add(FilterChainElement e) {
      if (length == 0) {
         head = tail = e;
      } else {
         e.prev = tail;
         tail.next = e;
         tail = e;
      }
      length++;
}

// Remove the element "e", returning the next element
public FilterChainElement remove(FilterChainElement e) {
   if (e.prev == null) {
      removeFirst();
      return head;
   } else if (e.next == null) {
      removeLast();
      return null;
   } else {
      e.prev.next = e.next;
      e.next.prev = e.prev;
      length--;
      return e.next;
   }
}

public void removeFirst() {
   if (length == 0) {
      return;
   }
   head = head.next;
   if (head == null) {
      tail = null;
   } else {
      head.prev = null;
   }
   length--;
}

public void removeLast() {
   if (length == 0) {
      return;
   }
   tail = tail.prev;
   if (tail == null) {
      head = null;
   } else {
      tail.next = null;
   }
   length--;
}


public void removeGroup(Object o) {
   FilterChainElement e = head;
   while (e != null) {
      if (e.belongsTo(o))
         e=remove(e);
      else
         e=e.next;
   }
}

public FilterChainElement findFirstFilter(UserEvent event) {
   for (FilterChainElement e = head; e != null; e = e.next)
      if (e.includes(event))
         return e;
   return null;
}

public void dispatchEvent(UserEvent event) {
   for (FilterChainElement e = head; e != null; e = e.next) {
      if (e.includes(event))
         e.dispatch(event);
   }
}

public boolean dispatchEventExclusive(UserEvent event) {
   FilterChainElement e=findFirstFilter(event);
   if (e != null) {
      e.dispatch(event);
      return true;
   }
   return false;
}




}
