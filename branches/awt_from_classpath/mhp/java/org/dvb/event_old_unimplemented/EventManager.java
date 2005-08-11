
package org.dvb.event;
import org.davic.resources.*;
import java.util.Vector;
import java.util.Vector;

/*The event manager allows an application to receive events coming from the user.These events can be sent exclusively to 
an application or can be shared between applications.The Event Manager allows also the application to ask for exclusive 
access to some events,these events being received either from the standard java.awt event mechanism or by the mechanism 
de  ned in this package.The EventManager is a singleton. The right to receive events is considered as the same resource 
regardless of whether it is being handled exclusively or shared.An application successfully obtaining exclusive access 
to an event results in all other applications loosing access to that event,whether the access of those applications was 
shared or exclusive. */

//TODO: Exclusive access request via AWT is currently ignored.
//      There will be many bugs in the loops.
//      ResourceStatusListeners are currently ignored

//80% 

public final class EventManager implements org.davic.resources.ResourceServer {

protected Vector dvbRepositorys;
protected Vector exclusiveDvbRepositorys;
protected Vector awtClients; //always exclusive
protected Vector  listeners;
static private EventManager self = null;

protected EventManager() {
   dvbRepositorys=new Vector();
   awtClients=new Vector();
   exclusiveDvbRepositorys=new Vector();
   listeners=new Vector();
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
public boolean addExclusiveAccessToAWTEvent(org.davic.resources.ResourceClient client, UserEventRepository 
userEvents) {
   if (client==null)
      throw new IllegalArgumentException();
   //removeExclusiveAccessToAWTEvent(client);
   //UserEventRepository ue=userEvents.clone();
   //awtClients.put(client, ue);
   return true;
}


/*
Adds the speci  ed resource status listener so that an application can be aware of any changes regarding exclusive 
access to some events. Speci  ed By: org.davic.resources.ResourceServer.addResourceStatusEventListener(org.davic.resources.Res 
ourceStatusListener)in interface org.davic.resources.ResourceServer Parameters: listener -the resource status 
listener. */
public void addResourceStatusEventListener(org.davic.resources.ResourceStatusListener 
listener) { 
   listeners.add(listener);
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
public boolean addUserEventListener(UserEventListener listener, org.davic.resources.ResourceClient client, 
UserEventRepository userEvents) {
   if (client==null)
      throw new IllegalArgumentException();
      
   UserEventRepository rep=getRepositoryForListener(exclusiveDvbRepositorys, listener, client);
   UserEventRepository exclrep;
   UserEvent uee;
   boolean skip=false, ret=true;
   
   eventloop:
   for (int i=0; i<userEvents.events.size(); i++) {
      uee=(UserEvent)userEvents.events.get(i);
      exclloop:
      for (int u=0; u<exclusiveDvbRepositorys.size(); u++) {
         exclrep=(UserEventRepository)exclusiveDvbRepositorys.get(u);
         if ( (exclrep != rep) && exclrep.isIncluded(uee, true)[0]) {
            skip=askForRelease(exclrep, exclrep.client, rep, rep.client);
            ret= ret && !skip; //could not assign all events
            if (!skip) { //client agreed with releasing the resource
               notifyRelease(true,exclrep, exclrep.client, rep, rep.client);
               exclrep.removeUserEventExclusive(uee);
               notifyRelease(false,exclrep, exclrep.client, rep, rep.client);
            }
            break exclloop;
         }
      }
      if (!skip) {
         if (rep.isIncluded(uee,false)[1])
            continue eventloop;
         rep.addUserEvent(uee);
      }
   }
   
   return ret;
   
   
   /*UserEventRepository ue=null, uetemp;
   boolean ret;
   firstloop:
   for (int i=0; i<exclusiveDvbRepositorys.size(); i++) {
      uetemp=(UserEventRepository)exclusiveDvbRepositorys.get(i);
      for (int u=0;u<userEvents.events.size();u++) {
         if (uetemp.isIncluded(userEvents.events.get(u))) {
            //ask client for permission
            RepositoryDescriptor rd=new RepositoryDescriptor(client, uetemp.RepositoryName);
            if (uetemp.client != null && uetemp.client.requestRelease(rd, null))
               
         }
      }
      if ( uetemp.listener == listener)
         ue=uetemp;
   }
   if (ue == null) {
      ue=new UserEventRepository("");
      exclusiveDvbRepositorys.add(ue);
   }
   ue.addAll(userEvents);  
   ue.exclusive=true;
   ue.client=client; 
   ue.listener=listener;
   return true;*/
   //UserEventRepository ue=userEvents.clone();
   //awtClients.put(client, ue);
}


/*
Adds the speci  ed listener to receive events coming from the user.The events the application wishes to receive are de  
ned by the means of the UserEventRepository class.This repository is resolved at the time when this method call is made 
and adding or removing events from the repository after this method call doesn't affect the subscription to those 
events. Parameters: listener -the listener to receive the user events. userEvents -a class which contains the user 
events it wants to be informed of. */
public void addUserEventListener(UserEventListener listener, UserEventRepository userEvents) { 
   UserEventRepository rep=getRepositoryForListener(dvbRepositorys, listener, null);
   UserEventRepository exclrep;
   UserEvent uee;
   boolean skip=false;
   
   eventloop:
   for (int i=0; i<userEvents.events.size(); i++) {
      uee=(UserEvent)userEvents.events.get(i);
      exclloop:
      for (int u=0; u<dvbRepositorys.size(); u++) {
         exclrep=(UserEventRepository)dvbRepositorys.get(u);
         if (exclrep.isIncluded(uee, true)[0]) {
            skip=true;
            //ret=false; //could not assign all events
            break exclloop;
         }
      }
      if (!skip) {
         if (rep.isIncluded(uee,false)[1])
            continue eventloop;
         rep.addUserEvent(uee);
      }
   }
   
   //return ret;
   /*UserEventRepository ue=null;
   for (int i=0; i<dvbRepositorys.size(); i++) {
      if ( ((UserEventRepository)dvbRepositorys.get(i)).listener == listener)
         ue=(UserEventRepository)(dvbRepositorys.get(i));
   }
   if (ue == null) {
      ue=new UserEventRepository("");
      dvbRepositorys.add(ue);
   }
   ue.addAll(userEvents);  
   ue.exclusive=true;
   ue.client=null; 
   ue.listener=listener;*/
}

/*Finds the repository for the listener in the given vector and creates a new one if needed */
protected UserEventRepository getRepositoryForListener(Vector repList, UserEventListener listener, org.davic.resources.ResourceClient client) {
   UserEventRepository ret;
   for (int i=0; i<repList.size(); i++) {
      ret=(UserEventRepository)repList.get(i);
      if (ret.listener == listener)
         return ret;
   }
   ret=new UserEventRepository("internal");
   ret.listener=listener;
   ret.client=client;
   repList.add(ret);
   return ret;
}

/*asks client whether he is ready to release the resource */
protected boolean askForRelease(UserEventRepository oldrep, org.davic.resources.ResourceClient oldclient,
                                 UserEventRepository newrep, org.davic.resources.ResourceClient newclient) {
   //i am not sure whether to pass oldclient or newclient. The spec says
   //"the object which asked to be notified about withdrawal of 
   //the underlying physical resource from a resource proxy." So what?
   RepositoryDescriptor rd=new RepositoryDescriptor(oldclient, oldrep.RepositoryName);
   return oldclient.requestRelease(rd, null);
}

/*tells client resource resource will be released( pre=true) or has been released (pre=false) */
protected void notifyRelease(boolean pre, UserEventRepository oldrep, org.davic.resources.ResourceClient oldclient,
                                 UserEventRepository newrep, org.davic.resources.ResourceClient newclient) {
   //i am not sure whether to pass oldclient or newclient. The spec says
   //"the object which asked to be notified about withdrawal of 
   //the underlying physical resource from a resource proxy." So what?
   RepositoryDescriptor rd=new RepositoryDescriptor(oldclient, oldrep.RepositoryName);
   if (pre)
      oldclient.release(rd);
   else
      oldclient.notifyRelease(rd);
}

/*
This method returns the sole instance of the EventManager class.The EventManager class is a singleton. Returns: the 
instance of the EventManager. */
public static EventManager getInstance() {
   if (self==null)
      self = new EventManager();
   return self;
}


/*
The application should use this method to release its exclusive access to user events de  ned by the means of the 
addExclusiveAccessToAWTEvent method. Parameters: client -the client that is no longer interested in events previously 
registered. */
public void removeExclusiveAccessToAWTEvent(org.davic.resources.ResourceClient 
client) { 
}


/*
Removes the speci  ed resource status listener. Speci  ed By: org.davic.resources.ResourceServer.removeResourceStatusEventListener(org.davic.resources. 
ResourceStatusListener)in interface org.davic.resources.ResourceServer Parameters: listener -the listener to 
remove. */
public void removeResourceStatusEventListener(org.davic.resources.ResourceStatusListener listener) { 
   for (int i=0;i<listeners.size(); i++) {
      if (listeners.get(i)==listener)
         listeners.remove(i);
   }
}


/*
Removes the speci  ed listener so that it will no longer receives user events.If it is appropriate (i.e the application 
has asked for an exclusive access),the exclusive access is lost. Parameters: listener -the user event 
listener. */
public void removeUserEventListener(UserEventListener listener) {
   for (int i=0; i<exclusiveDvbRepositorys.size(); i++) {
      if ( ((UserEventRepository)exclusiveDvbRepositorys.get(i)).listener == listener)
         exclusiveDvbRepositorys.remove(i);
   }
   for (int i=0; i<dvbRepositorys.size(); i++) {
      if ( ((UserEventRepository)dvbRepositorys.get(i)).listener == listener)
         dvbRepositorys.remove(i);
   }
}

/* Called from AWT event dispatching (Component.processKey())
   Return true if event should no longer be dispatched  */
public boolean DispatchEvent(UserEvent e) {
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
}


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
