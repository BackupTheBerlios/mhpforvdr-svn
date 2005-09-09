
package org.dvb.event;

import org.davic.resources.*;
import vdr.mhp.ApplicationManager;
import org.dvb.event.chain.*;
import java.util.LinkedList;
import java.util.ListIterator;

/*The event manager allows an application to receive events coming from the user.These events can be sent exclusively to 
an application or can be shared between applications.The Event Manager allows also the application to ask for exclusive 
access to some events,these events being received either from the standard java.awt event mechanism or by the mechanism 
de  ned in this package.The EventManager is a singleton. The right to receive events is considered as the same resource 
regardless of whether it is being handled exclusively or shared.An application successfully obtaining exclusive access 
to an event results in all other applications loosing access to that event,whether the access of those applications was 
shared or exclusive. */


public final class EventManager implements org.davic.resources.ResourceServer {

static EventManager self = new EventManager();
LinkedList listeners = new LinkedList();

FilterChain chain = new FilterChain();
FilterChain exclusiveChain = new FilterChain();


protected EventManager() {
}

/*
Adds the speci  ed resource status listener so that an application can be aware of any changes regarding exclusive 
access to some events. Speci  ed By: org.davic.resources.ResourceServer.addResourceStatusEventListener(org.davic.resources.Res 
ourceStatusListener)in interface org.davic.resources.ResourceServer Parameters: listener -the resource status 
listener. */
public synchronized void addResourceStatusEventListener(org.davic.resources.ResourceStatusListener listener) { 
   listeners.add(listener);
}


/*
An application should use this method to express its intend to have exclusive access to some events, but for these 
events to be received through the java.awt mechanism.The events the application wishes to receive are de  ned by the 
means of the UserEventRepository class.This repository is resolved at the time when this method call is made and adding 
or removing events from the repository after this method call doesn't affect the subscription to those events.An 
exclusive event will be sent to the application if this latest is focused. Parameters: client -resource client. 
userEvents -the user events the application wants to be inform of. Returns: true if the events de  ned in the repository 
have been acquired,false otherwise. Throws: IllegalArgumentException -if the client argument is set to 
null. */
public synchronized boolean addExclusiveAccessToAWTEvent(org.davic.resources.ResourceClient client, UserEventRepository 
userEvents) {
   if (userEvents != null)
      return addExclusiveListener(client, userEvents, null);
   else return false;
}


/*
Adds the speci  ed listener to receive events coming from the user in an exclusive manner.The events the application 
wishes to receive are de  ned by the means of the UserEventRepository class. This repository is resolved at the time 
when this method call is made and adding or removing events from the repository after this method call doesn't affect 
the subscription to those events.The ResourceClient parameter indicates that the application wants to have an exclusive 
access to the user event de  ned in the repository. Parameters: listener -the listener to receive the user events. 
client -resource client. userEvents -a class which contains the user events it wants to be informed of. Returns: true if 
the events de  ned in the repository have been acquired,false otherwise. Throws: IllegalArgumentException -if the client 
argument is set to null. */
public synchronized boolean addUserEventListener(UserEventListener listener, org.davic.resources.ResourceClient client, 
UserEventRepository userEvents) {
   if (listener != null && userEvents != null)
      return addExclusiveListener(client, userEvents, listener);
   else return false;
}


//addUserEventListener and addExclusiveAccessToAWTEvent share the same code, except
//for one line. So if listener != null, this is addUserEventListener, else it is addExclusiveAccessToAWTEvent
boolean addExclusiveListener(org.davic.resources.ResourceClient client, 
UserEventRepository userEvents, UserEventListener listener) {
   if (client==null)
      throw new IllegalArgumentException();
   
   boolean allAcquired=true;
   RepositoryDescriptor descriptor=new RepositoryDescriptor(client, userEvents.RepositoryName);
   UserEventRepository assignedEvents = new UserEventRepository("Unavailable Events");
   for (int i=0; i<userEvents.events.size(); i++) {
      UserEvent e=userEvents.get(i);
      FilterChainElement oldElem=exclusiveChain.findFirstFilter(e);
      if (oldElem != null) {
         //another exclusive filter holds event
         org.davic.resources.ResourceClient oldClient=((ExclusiveFilterElement)oldElem).getRepositoryDescriptor().getClient();
         if (oldClient == client) {
            //the same client - all right
            ((ExclusiveFilterElement)oldElem).addType(e.getType());
            continue;
         } else {
            //different client - negotiate
            if (oldClient.requestRelease(descriptor, null)) {
               oldClient.release(descriptor);
               exclusiveChain.remove(oldElem);
               oldClient.notifyRelease(descriptor);
               //fall through - no continue
            } else {
               allAcquired=false;
               continue;
            }
         }
      }
      //no filter registered yet - do that
      
      assignedEvents.addUserEvent(e);
      FilterChainElement newElem;
      if (listener != null)
         newElem=new ExclusiveUserEventChainElement(e, listener, descriptor);
      else
         newElem=new ExclusiveAWTChainElement(e, descriptor);
      
      exclusiveChain.add(newElem);
   }
   sendResourceStatusEvent(assignedEvents, false);
   return allAcquired;
}


/*
Adds the speci  ed listener to receive events coming from the user.The events the application wishes to receive are de  
ned by the means of the UserEventRepository class.This repository is resolved at the time when this method call is made 
and adding or removing events from the repository after this method call doesn't affect the subscription to those 
events. Parameters: listener -the listener to receive the user events. userEvents -a class which contains the user 
events it wants to be informed of. */
public synchronized void addUserEventListener(UserEventListener listener, UserEventRepository userEvents) {
   for (int i=0; i<userEvents.events.size(); i++) {
      UserEvent e=userEvents.get(i);
      //if an exclusive event is registered, these listeners will never receive their events!
      chain.add(new UserEventChainElement(e, listener));
   }
}

private void sendResourceStatusEvent(UserEventRepository r, boolean isAvailable) {
   ListIterator it = listeners.listIterator(0);
   while (it.hasNext()) {
      // each listener shall receive its own independent repository
      UserEventRepository eventRep = null;
      try {
         eventRep = (UserEventRepository)r.clone();
      } catch (CloneNotSupportedException e) {}
      ResourceStatusListener l = (ResourceStatusListener)it.next();
      if (isAvailable)
         l.statusChanged(new UserEventAvailableEvent(eventRep));
      else
         l.statusChanged(new UserEventUnavailableEvent(eventRep));
   }
}

/*
This method returns the sole instance of the EventManager class.The EventManager class is a singleton. Returns: the 
instance of the EventManager. */
public static EventManager getInstance() {
   return self;
}


/*
The application should use this method to release its exclusive access to user events de  ned by the means of the 
addExclusiveAccessToAWTEvent method. Parameters: client -the client that is no longer interested in events previously 
registered. */
public synchronized void removeExclusiveAccessToAWTEvent(org.davic.resources.ResourceClient client) {
   UserEventRepository assignedEvents = new UserEventRepository("Available Events");
   exclusiveChain.removeGroup(client, assignedEvents);
   // filter out those events to which exclusive access is reserved by the means of the addUserEventListener method
   filterOut(exclusiveChain, assignedEvents);
   sendResourceStatusEvent(assignedEvents, true);
}


/*
Removes the speci  ed listener so that it will no longer receives user events.If it is appropriate (i.e the application 
has asked for an exclusive access),the exclusive access is lost. Parameters: listener -the user event 
listener. */
public synchronized void removeUserEventListener(UserEventListener listener) {
   chain.removeGroup(listener);

   UserEventRepository assignedEvents = new UserEventRepository("Available Events");
   exclusiveChain.removeGroup(listener, assignedEvents);
   // filter out those events to which exclusive access is reserved by the means of the method addExclusiveAccessToAWTEvent
   filterOut(exclusiveChain, assignedEvents);
   sendResourceStatusEvent(assignedEvents, true);
}

private void filterOut(FilterChain chain, UserEventRepository events) {
   for (int i=0; i<events.events.size(); i++) {
      UserEvent e=events.get(i);
      if (chain.findFirstFilter(e) != null)
         events.removeUserEvent(e);
   }
}


/*
Removes the speci  ed resource status listener. Speci  ed By: org.davic.resources.ResourceServer.removeResourceStatusEventListener(org.davic.resources. 
ResourceStatusListener)in interface org.davic.resources.ResourceServer Parameters: listener -the listener to 
remove. */
public synchronized void removeResourceStatusEventListener(org.davic.resources.ResourceStatusListener listener) {
   listeners.remove(listener);
}

/* Called from framework.
   Return true if event should no longer be dispatched  */
public boolean DispatchEvent(UserEvent e) {
   if (exclusiveChain.dispatchEventExclusive(e)) {
      //found exclusive element, dispatching ends here
      return true;
   }
   //dispatch event to any member of the non-exclusive chain which likes it
   chain.dispatchEvent(e);
   return false;
}

/* Called from AWT event dispatching (Component.processKey())
   Return true if event should no longer be dispatched  */
/*public boolean DispatchEvent(UserEvent e) {
   UserEventRepository ue;
   boolean exclaccess[];
   //is event in any repository?
   for (int i=0;i<exclusiveDvbRepositorys.size(); i++) {
      ue=(UserEventRepository)exclusiveDvbRepositorys.get(i);
      exclaccess=ue.isIncluded(e, true);
      if (exclaccess[1] && ue.listener != null) {
         ue.listener.userEventReceived(e);
         return true;
      } else if (exclaccess[0])
         return true; //part of a pressed/released pair, not sent to listener, but not further dispatched
   }
   for (int i=0;i<dvbRepositorys.size(); i++) {
      ue=(UserEventRepository)dvbRepositorys.get(i);
      if (ue.isIncluded(e, false)[1] && ue.listener != null) {
         ue.listener.userEventReceived(e);
      }
   }
   return false;   
}*/


/**** DEBUGGING: Sample code from spec. And yes, they contradicted there own spec, it did not compile.*/

/*class Example implements UserEventListener, ResourceStatusListener, ResourceClient {
 private int myStatus ;
  public Example () {
   EventManager em ;
    UserEventRepository repository ;
     em = EventManager.getInstance () ;
     
      repository = new UserEventRepository ("R1") ;
       repository.addKey (UserEvent.VK_ENTER);
       repository.addKey (UserEvent.VK_COLORED_KEY_0);
        em.addUserEventListener ((UserEventListener)this, (ResourceClient)this, repository) ;
        
       repository = new UserEventRepository ("R2") ;
       repository.addKey (UserEvent.VK_0);
       em.addUserEventListener ((UserEventListener)this, repository) ;
               
       em.addResourceStatusEventListener (this) ;
  } // methods defined by the UserEventListener interface. 
   public void userEventReceived (UserEvent e) { System.out.print("Got Event ");System.out.println(e.getCode());} 
   // Methods defined by the ResourceClient interface. 
    // In the case a cooperative application asks for an user event * exclusively used by me. 
   public boolean requestRelease(ResourceProxy proxy, Object requestData) {
      String name; 
      // let's retrieve the name of the repository, that I have created, and
      name = ((RepositoryDescriptor)proxy).getName() ;
      System.out.print("requestRelease: ");System.out.println(name);
      if ((name.compareTo ("R1") == 0)) {
      // Ok I release this event.
         return true; 
      } else {
         // No I need this event, sorry !
         return false;
      }
  } 
  public void release (ResourceProxy proxy) { System.out.println("Release");}
  public void notifyRelease (ResourceProxy proxy) {System.out.println("NotifyRelease");}
  public void statusChanged (ResourceStatusEvent event) { }
 }

public static void main ( String[] args ) {

   Example ex=getInstance().new Example();
   UserEvent ue=new UserEvent(getInstance(), UserEvent.UEF_KEY_EVENT, 
                                       UserEvent.KEY_PRESSED, UserEvent.VK_ENTER, 0);
   System.out.print("DispatchEvent: ");System.out.println( getInstance().DispatchEvent(ue));
    ue=new UserEvent(getInstance(), UserEvent.UEF_KEY_EVENT, 
                                       UserEvent.KEY_PRESSED, UserEvent.VK_COLORED_KEY_0, 0);
   System.out.print("DispatchEvent: ");System.out.println( getInstance().DispatchEvent(ue));
    ue=new UserEvent(getInstance(), UserEvent.UEF_KEY_EVENT, 
                                       UserEvent.KEY_PRESSED, UserEvent.VK_0, 0);
   System.out.print("DispatchEvent: ");System.out.println( getInstance().DispatchEvent(ue));
  
   ex=getInstance().new Example();
}
*/

}
