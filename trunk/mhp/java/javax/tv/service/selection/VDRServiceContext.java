
package javax.tv.service.selection;

import javax.tv.service.Service;
import javax.tv.service.VDRService;
import javax.tv.service.VDRServiceMulticaster;
import org.dvb.application.MHPApplication;

/*

A <code>ServiceContext</code> represents an environment in which
 services are presented in a broadcast receiver.  Applications may
 use <code>ServiceContext</code> objects to select new services to
 be presented.  Content associated with a selected service is
 presented by one or more <code>ServiceContentHandler</code> objects
 managed by the <code>ServiceContext</code>.<p>

 A <code>ServiceContext</code> can exist in four states -
 <em>presenting</em>, <em>not presenting</em>, <em>presentation
 pending</em> and <em>destroyed</em>. The initial state is
 <em>not presenting</em>. <p>

 The <code>select()</code> method can be called from any state
 except <em>destroyed</em>. Assuming no exception is thrown, the
 service context then enters the <em>presentation pending</em>
 state. No event is generated on this state transition. If a call to
 <code>select()</code> completes successfully, either a
 <code>NormalContentEvent</code> or an
 <code>AlternativeContentEvent</code> is generated and the
 <code>ServiceContext</code> moves into the <em>presenting</em>
 state.<p>

 If the selection operation fails, a
 <code>SelectionFailedEvent</code> is generated.  If the
 <code>select()</code> method is called during the <em>presentation
 pending</em> state, a <code>SelectionFailedEvent</code> with reason
 code <code>INTERRUPTED</code> is generated, and the
 <code>ServiceContext</code> will proceed in the <em>presentation
 pending</em> state for the most recent <code>select()</em> call.
 Otherwise, if the state before the failed select operation was
 <em>not presenting</em>, the <code>ServiceContext</code> will
 return to that state and a <code>PresentationTerminatedEvent</code>
 be generated. If the state before the failed select operation was
 <em>presenting</em>, it will attempt to return to that previous
 state, which can result in a <code>NormalContentEvent</code> or
 <code>AlternativeContentEvent</code> if this is possible, or a
 <code>PresentationTerminatedEvent</code> if it is not possible.<p>

 The <em>not presenting</em> state is entered due to service
 presentation being stopped which is reported by a
 <code>PresentationTerminatedEvent</code>. The stopping of service
 presentation can be initiated by an application calling the
 <code>stop()</code> method or because some change in the environment
 makes continued presentation impossible.<p>

 The <em>destroyed</em> state is entered by calling the
 <code>destroy()</code> method, and is signaled by a
 <code>ServiceContextDestroyedEvent</code>. Once this state is
 entered, the <code>ServiceContext</code> can no longer be used for
 any purpose.  A destroyed <code>ServiceContext</code> will be
 eligible for garbage collection once all references to it by any
 applications have been removed.<p>

 Note that the ability to select a service for presentation does not
 imply exclusive rights to the resources required for that
 presentation.  Subsequent attempts to select the same service may
 fail.<p>

 Applications may also use this interface to register for events
 associated with <code>ServiceContext</code> state changes.<p>

*/
class VDRServiceContext implements ServiceContext {


static final int NOT_PRESENTING       = 0;
static final int PRESENTATION_PENDING = 1;
static final int PRESENTING           = 2;
static final int DESTROYED            = 3;

static {
   initializeStatus();
}
private static native void initializeStatus();

static VDRServiceContext context = null;

static synchronized VDRServiceContext getContext() {
   if (context == null)
      context=new VDRServiceContext();
   return context;
}

private static synchronized void removeCurrent() {
   context=null;
}



int state = NOT_PRESENTING;
ServiceContextListener sclistener = null;
boolean destructionPending = false;

private VDRServiceContext() {
   if (isPresenting())
      state=PRESENTING;
}

private native boolean isPresenting();

/*
 
 Selects a service to be presented in this
 ServiceContext . If the ServiceContext 
 is already presenting content, the new selection replaces the
 content being presented. If the ServiceContext is
 not presenting, successful conclusion of this operation results in
 the ServiceContext entering the presenting 
 state. */

public void select (Service selection) 
   throws SecurityException, IllegalStateException
{
//operation completes asynchronously
   synchronized (this) {
      switch (state) {
      case PRESENTATION_PENDING:
      case NOT_PRESENTING:
      case PRESENTING:
         SecurityManager s = System.getSecurityManager();
         if (s != null) {
            s.checkPermission(new SelectPermission(selection.getLocator(), "own"));
         }
         if (state == PRESENTATION_PENDING && sclistener != null)
            sclistener.receiveServiceContextEvent(new SelectionFailedEvent(this, SelectionFailedEvent.INTERRUPTED));
         doSelect((VDRService)selection);
         break;
      case DESTROYED:
         throw new IllegalStateException();
      }
   }
}

private void doSelect(VDRService selection) {
   doSelect(selection.getNativeData());
}

private native void doSelect(long nativeData);


//Keep these constants in sync with Service::ContextStatus::Message in libservice/servicecontext.h
static final int SUCCESS_NORMAL_CONTENT      = 0;
static final int SUCCESS_ALTERNATIVE_CONTENT = 1;
static final int ACCESS_WITHDRAWN            = 2;
static final int RESOURCES_REMOVED           = 3;
static final int SERVICE_VANISHED            = 4;
static final int TUNED_AWAY                  = 5;
static final int USER_STOP                   = 6;
static final int CA_REFUSAL                  = 7;
static final int CONTENT_NOT_FOUND           = 8;
static final int INSUFFICIENT_RESOURCES      = 9;
static final int MISSING_HANDLER             = 10;
static final int TUNING_FAILURE              = 11;

//called from native code
void serviceEvent(int event) {
   synchronized (this) {
      if (state==DESTROYED && !destructionPending)
         return;
      switch (event) {
      case SUCCESS_NORMAL_CONTENT:
         state=PRESENTING;
         sendEvent(new NormalContentEvent(this));
         break;
      case SUCCESS_ALTERNATIVE_CONTENT:
         state=PRESENTING;
         sendEvent(new AlternativeContentEvent(this));
        break;
        
      case ACCESS_WITHDRAWN:
         sendEvent(new PresentationTerminatedEvent(this, PresentationTerminatedEvent.ACCESS_WITHDRAWN));
         break;
      case RESOURCES_REMOVED:
         sendEvent(new PresentationTerminatedEvent(this, PresentationTerminatedEvent.RESOURCES_REMOVED));
         break;
      case SERVICE_VANISHED:
         sendEvent(new PresentationTerminatedEvent(this, PresentationTerminatedEvent.SERVICE_VANISHED));
         break;
      case TUNED_AWAY:
         sendEvent(new PresentationTerminatedEvent(this, PresentationTerminatedEvent.TUNED_AWAY));
         break;
      case USER_STOP:
         sendEvent(new PresentationTerminatedEvent(this, PresentationTerminatedEvent.USER_STOP));
         //if stop() was called from destroy()
         if (destructionPending) {
            destructionPending=false;
            doDestroy();
         }
         break;
         
      case CA_REFUSAL:
         state=NOT_PRESENTING;
         sendEvent(new SelectionFailedEvent(this, SelectionFailedEvent.CA_REFUSAL));
         break;
      case CONTENT_NOT_FOUND:
         state=NOT_PRESENTING;
         sendEvent(new SelectionFailedEvent(this, SelectionFailedEvent.CONTENT_NOT_FOUND));
         break;
      case INSUFFICIENT_RESOURCES:
         state=NOT_PRESENTING;
         sendEvent(new SelectionFailedEvent(this, SelectionFailedEvent.INSUFFICIENT_RESOURCES));
         break;
      case MISSING_HANDLER:
         state=NOT_PRESENTING;
         sendEvent(new SelectionFailedEvent(this, SelectionFailedEvent.MISSING_HANDLER));
         break;
      case TUNING_FAILURE:
         state=NOT_PRESENTING;
         sendEvent(new SelectionFailedEvent(this, SelectionFailedEvent.TUNING_FAILURE));
         break;
      }
   }
}

private void sendEvent(ServiceContextEvent e) {
   if (sclistener != null)
      sclistener.receiveServiceContextEvent(e);
}

private static void nativeServiceEvent(int event) {
}


/*
 
 Selects content by specifying the parts of a service to be
 presented. If the ServiceContext is
 already presenting content, the new selection replaces the
 content being presented. If the
 ServiceContext is not presenting,
 successful conclusion of this operation results in the
 ServiceContext entering the
 presenting state. */

public void select ( javax.tv.locator.Locator [] components)
      throws javax.tv.locator.InvalidLocatorException,
          InvalidServiceComponentException,
          SecurityException
{
//operation completes asynchronously
   //TODO: interpret locators, call other function
   //Use VDRService to obtain an appropriate javax.tv.service.Service (TODO!)
}


/*
 
 Causes the ServiceContext to stop presenting content
 and enter the not presenting state. Resources used
 in the presentation will be released, associated
 ServiceContentHandlers will cease presentation
 ( ServiceMediaHandlers will no longer be in the
 started state), and a
 PresentationTerminatedEvent will be posted. */

public void stop ()
     throws SecurityException, IllegalStateException
{
//operation completes asynchronously
   synchronized (this) {
      switch (state) {
      case PRESENTATION_PENDING:
      case PRESENTING:
         SecurityManager s = System.getSecurityManager();
         if (s != null) {
            s.checkPermission(new ServiceContextPermission("stop", "own"));
         }
         doStop();
         break;
      case NOT_PRESENTING:
         return;
      case DESTROYED:
         throw new IllegalStateException();
      }
   }
}

private native void doStop();


/*
 
 Causes the ServiceContext to release all resources
 and enter the destroyed state. This method indicates
 that the the ServiceContext must cease further
 activity, and that it will no longer be used. After completion
 of this method, ServiceMediaHandler instances
 associated with this ServiceContext will have become
 unrealized or will have been closed.*/

public void destroy ()
       throws SecurityException
{
//operation completes asynchronously
   synchronized (this) {
      switch (state) {
      case PRESENTATION_PENDING:
      case PRESENTING:
      case NOT_PRESENTING:
         SecurityManager s = System.getSecurityManager();
         if (s != null) {
            s.checkPermission(new ServiceContextPermission("destroy", "own"));
         }
         if (state != NOT_PRESENTING) {
            //stop asynchronously
            destructionPending=true;
            stop();
            //set state here so that no other operation may interfere
            state=DESTROYED;
         } else {
            //proceed synchronously
            doDestroy();
         }
         break;
      case DESTROYED:
         break;
      }
   }
}

private void doDestroy() {
   removeCurrent();
   synchronized (this) {
      state = DESTROYED;
      if (sclistener != null)
         sclistener.receiveServiceContextEvent(new ServiceContextDestroyedEvent(this));
   }
}


/*
 
 Reports the current collection of ServiceContentHandlers. A
 zero-length array is returned if the ServiceContext 
 is in the not presenting or presentation
 pending states. 
 Returns: The current ServiceContentHandler instances. Throws:  java.lang.SecurityException - If the caller owns this
 ServiceContext but does not have
 ServiceContextPermission("getServiceContentHandlers",
 "own") , or if the caller does not own this
 ServiceContext and does not have
 SelectPermission("getServiceContentHandlers", "*") . java.lang.IllegalStateException - If the ServiceContext 
 has been destroyed. 
 
 
 */

public ServiceContentHandler [] getServiceContentHandlers ()
                         throws SecurityException, IllegalStateException
{
   synchronized (this) {
      switch (state) {
      case NOT_PRESENTING:
      case PRESENTATION_PENDING:
         break;
      case PRESENTING:
         SecurityManager s = System.getSecurityManager();
         if (s != null) {
            s.checkPermission(new ServiceContextPermission("getServiceContentHandlers", "own"));
         }
         //implement
         return new ServiceContentHandler[] {};
         //ServiceContentHandler[] ret=new ServiceContentHandler[1];
         //ret[0]=new org.dvb.media.content.vdrdvb.Player();
         //return ret;
      case DESTROYED:
         throw new IllegalStateException();
      }
   }
   return new ServiceContentHandler[0];
}


/*
 
 Reports the Service being presented in this
 ServiceContext . If the ServiceContext 
 is currently presenting a service, the Service 
 returned will be a network-dependent representation of the
 Service indicated in the last successful
 select() method call. If the
 ServiceContext is not in the presenting 
 state then null is returned. 
 Returns: The service currently being presented. Throws:  java.lang.IllegalStateException - If the ServiceContext 
 has been destroyed. 
 
 
 */

public Service  getService () throws IllegalStateException {
   switch (state) {
   case NOT_PRESENTING:
   case PRESENTATION_PENDING:
      break;
   case PRESENTING:
      return VDRService.getServiceForNativeChannel(getNativeService());
   case DESTROYED:
      throw new IllegalStateException();
   }
   return null;
}

private native long getNativeService();


/*
 
 Subscribes a listener to receive events related to this
 ServiceContext . If the specified listener is currently
 subscribed, no action is performed. 
 Parameters:  listener - The ServiceContextListener to subscribe. Throws:  java.lang.IllegalStateException - If the ServiceContext has been
 destroyed. See Also:   ServiceContextEvent  
 
 
 */
 

public void addListener ( ServiceContextListener listener) {
   switch (state) {
   case NOT_PRESENTING:
   case PRESENTATION_PENDING:
   case PRESENTING:
      synchronized (this) {
         sclistener=VDRServiceMulticaster.add(sclistener, listener);
      }
      break;
   case DESTROYED:
      throw new IllegalStateException();
   }
}


/*
 
 Unsubscribes a listener from receiving events related to this
 ServiceContext . If the specified listener is not currently
 subscribed, no action is performed. 
 Parameters:  listener - The ServiceContextListener to unsubscribe. Throws:  java.lang.IllegalStateException - If the ServiceContext has been
 destroyed. 
 
 
*/

public void removeListener ( ServiceContextListener listener) {
   switch (state) {
   case NOT_PRESENTING:
   case PRESENTATION_PENDING:
   case PRESENTING:
      synchronized (this) {
         sclistener=VDRServiceMulticaster.remove(sclistener, listener);
      }
      break;
   case DESTROYED:
      throw new IllegalStateException();
   }
}



}

