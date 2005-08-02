
package vdr.mhp;
import org.dvb.application.*;
import org.dvb.lang.DVBClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

/*
   The ApplicationManager is the core class of this implementation.
   All operations are started, controlled and ended from this class, although
   the work is done elsewhere.
   The native plugin will call the static methods of this class via JNI.
   The ApplicationManager also sets the security manager.
*/




public class ApplicationManager {

static {
   System.loadLibrary("mhpjni_dvb");
}

//return values in this class:
// 0 -- all right    1 -- "normal" error condition   -1 -- terminate VM

//the application manager catches all exceptions at its toplevel threads
// and its interface called by JNI (all public static methods)

static ApplicationManager self=new ApplicationManager();

public static ApplicationManager getManager() {
   return self;
}

ApplicationTaskThread taskThread;
boolean init=false;
MHPSecurityManager securityManager;
ApplicationThreadGroup applicationThreadGroup;

 /* It would be nice to have all application-created threads in the dedicated
    thread group. However, there is the libdvbsi DispatchThread which must be
    created in native space and directly calls Java via JNI.
    Threads created in that context will not belong to the dedicated group,
    all others will: The other entry points are the ApplicationTaskThread and
    the event dispatching thread. The latter is created when an MHPPlane is addNotified,
    so this is done in application's context.
 */
ApplicationManager() {
   try {
      applicationThreadGroup=new ApplicationThreadGroup("ApplicationGroup");
      taskThread=new ApplicationTaskThread(applicationThreadGroup);
      securityManager=new MHPSecurityManager(Thread.currentThread().getThreadGroup());
      
      System.setSecurityManager(securityManager);
   } catch (Exception e) {
      e.printStackTrace();
   }
}







/* --- Interface called by JNI --- */


/* Parameters:
   appDatabase - pointer to a cApplicationDatabase
*/
public static int Initialize(long appDatabase) {
   try {   
      if (getManager().init)
         return 0;
      System.out.println("ApplicationManager.Init()");
      
      //initialize java.awt/org.havi.ui
      java.awt.MHPScreen.InitializeDisplaySystem();
   
      //initialize org.dvb.application
      AppsDatabase.CreateDatabase(appDatabase);
            
      //set some properties for System.getProperty()
      setupProperties();
   } catch (Exception e) {
      e.printStackTrace();
      return -1;
   }
   getManager().init=true;
   return 0;
}

public static int NewApplication(long nativeData) {
   try {
      if (!getManager().init)
         return 1;
      System.out.println("ApplicationManager.NewApplication()");
      AppsDatabase.getAppsDatabase().NotifyNewApplication(nativeData);
   } catch (Exception e) {
      e.printStackTrace();
      return -1;
   }
   return 0;
}

public static int ApplicationRemoved(long nativeData) {
   try {
      if (!getManager().init)
         return 1;
      System.out.println("ApplicationManager.ApplicationRemoved()");
      AppsDatabase.getAppsDatabase().NotifyApplicationRemoved(nativeData);
   } catch (Exception e) {
      e.printStackTrace();
      return -1;
   }
   return 0;
}




public static int StartApplication(long nativeData) {
   try {
      System.out.println("ApplicationManager.StartApplication()");
      if (!getManager().init) {
         System.out.println("manager not init'ed");
         return 1;
      }
      MHPApplication app=MHPApplication.GetApplication(nativeData);
      if (app==null) {
         System.out.println("did not find application");
         return 1;
      }
      app.start();
      System.out.println("Found application for native Data, starting");
      /** What happens? DVBJApplication calls this.StartApplication,
          which posts a task to the ApplicationTaskThread. The task
          will take all action necessary, including loading, initializing
          and actually starting the Xlet.
      */
   } catch (Exception e) {
      e.printStackTrace();
      return -1;
   }
   return 0;
}

public static int StopApplication(long nativeData) {
   try {
      System.out.println("ApplicationManager.StopApplication()");
      if (!getManager().init)
         return 1;
      MHPApplication app=MHPApplication.GetApplication(nativeData);
      if (app==null)
         return 1;
      app.stop(true);
   } catch (Exception e) {
      e.printStackTrace();
      return -1;
   }
   return 0;
}

public static int StopApplications() {
   try {
      System.out.println("ApplicationManager.StopApplications()");
      if (!getManager().init)
         return 1;
      MHPApplication.stopAll();
   } catch (Exception e) {
      e.printStackTrace();
      return -1;
   }
   return 0;
}

public static int PauseApplication(long nativeData) {
   try {
      System.out.println("ApplicationManager.PauseApplication()");
      if (!getManager().init)
         return 1;
      MHPApplication app=MHPApplication.GetApplication(nativeData);
      if (app==null)
         return 1;
      app.pause();
   } catch (Exception e) {
      e.printStackTrace();
      return -1;
   }
   return 0;
}

public static int ResumeApplication(long nativeData) {
   try {
      System.out.println("ApplicationManager.ResumeApplication()");
      if (!getManager().init)
         return 1;
      MHPApplication app=MHPApplication.GetApplication(nativeData);
      if (app==null)
         return 1;
      app.resume();
   } catch (Exception e) {
      e.printStackTrace();
      return -1;
   }
   return 0;
}

public static int ProcessKey(int eKey) {
   try {
      //System.out.println("ApplicationManager.ProcessKey()");
      if (!getManager().init)
         return 1;
      java.awt.VDREventDispatcher.dispatchKey(eKey);
   } catch (Exception e) {
      e.printStackTrace();
      return -1;
   }
   return 0;
}

//Perform clean-ups before VDR quits - this function shall only
//be called once during the lifetime of the VM/the whole VDR process
public static int Shutdown() {
   try {
      ApplicationManager m=getManager();
      //System.out.println("ApplicationManager.ProcessKey()");
      if (!m.init)
         return 1;
      //All applications shall already be stopped, but be sure anyway
      MHPApplication.stopAll();
      //wait for completion of taskThread
      m.taskThread.completeAndStop();
      m.taskThread.waitForShutdown();
      
      org.dvb.user.UserPreferenceManager.store();
      //remove references
      MHPApplication.clearList();
      //run garbage collector
      System.gc();
      //print debug output
      m.applicationThreadGroup.list();
   } catch (Exception e) {
      e.printStackTrace();
      return -1;
   }
   return 0;
}



/* --- Interface to MHPApplication --- */

public void NotifyXletDestroyedItself(MHPApplication app) {
}

public void NotifyXletPausedItself(MHPApplication app) {
}

public void StartApplication(MHPApplication app) {
   taskThread.requestStateChange(app, ApplicationTask.START);
}

public void StartApplication(MHPApplication app, java.lang.String[] args) {
   taskThread.requestStateChange(app, ApplicationTask.START, args);
}

public void PauseApplication(MHPApplication app) {
   taskThread.requestStateChange(app, ApplicationTask.PAUSE);
}

public void ResumeApplication(MHPApplication app) {
   taskThread.requestStateChange(app, ApplicationTask.RESUME);
}

/*
forced -if true then do not ask the application but forcibly terminate it,
if false give the application an opportunity to refuse.
*/
public void StopApplication(MHPApplication app, boolean force) {
   taskThread.requestStateChange(app, ApplicationTask.STOP, new Boolean(force));
}

public void InitApplication(DVBJApplication app) {
   taskThread.requestStateChange(app, ApplicationTask.INIT);
}

public void LoadApplication(DVBJApplication app) {
   taskThread.requestStateChange(app, ApplicationTask.LOAD);
}



/* --- Internal public API --- */

//this is meant to report serious incidents such as an Error (not an Exception)
//caught in a top-level catch clause.
public static void reportError(Throwable x) {
   //let's be careful here, anything might have happened
   try {
      System.out.println("ApplicationManager.reportError called");
   } catch (Throwable nx) {
      Runtime.getRuntime().exit(1);
   }
   try {
      x.printStackTrace();
   } catch (Throwable nx) {
      Runtime.getRuntime().exit(1);
   }
   try {
      vdr.mhp.Syslog.esyslog("MHP: ApplicationManger: An error was reported. "+
                            "Please check your installation, especially if the libraries are installed correctly. "+
                            "More information is output on the standard error output (STDERR), probably on the "+
                            "console your instance of VDR runs in." );
   } catch (Throwable nx) {
   }
}

public org.dvb.application.MHPApplication getApplicationFromStack() {
   return securityManager.getApplicationFromStack();
   //return DVBClassLoader.getApplicationFromStack();
}



/* --- Internal code --- */

static void setupProperties() {
   //see section 11.9.3, page 270 for description
   //keep in sync with mhp/implementation.h
   System.setProperty("mhp.profile.enhanced_broadcast", "YES");
   System.setProperty("mhp.profile.interactive_broadcast", "NO");
   System.setProperty("mhp.profile.internet_access", "NO");
   System.setProperty("mhp.eb.version.major", Integer.toString(1));
   System.setProperty("mhp.eb.version.minor", Integer.toString(0));
   System.setProperty("mhp.eb.version.micro", Integer.toString(2));
   //These are unsupported and shall be left null
   /*
   System.setProperty("mhp.ib.version.major", );
   System.setProperty("mhp.ib.version.minor", );
   System.setProperty("mhp.ib.version.micro", );
   System.setProperty("mhp.ia.version.major", );
   System.setProperty("mhp.ia.version.minor", );
   System.setProperty("mhp.ia.version.micro", );
   */
}

static class LoadingManagerInterface {
   static void load(MHPApplication app) {
      load(app.getNativeData());
   }
   private static native void load(long nativeData);
   
   static void stop(MHPApplication app) {
      stop(app.getNativeData());
   }
   private static native void stop(long nativeData);
   
   static boolean isAcquired(MHPApplication app) {
      return isAcquired(app.getNativeData());
   }
   private static native boolean isAcquired(long nativeData);
}

class ApplicationThreadGroup extends ThreadGroup {

   ApplicationThreadGroup(String s) {
      super(s);
   }
   
   public void uncaughtException(Thread t, Throwable e) {
      System.out.println("ApplicationThreadGroup: uncaughtException!");
      ApplicationManager.reportError(e);
   }

}

class ApplicationTask {

   MHPApplication app;
   Object args;
   AppStateChangeEvent event;
   java.util.Stack todo = new java.util.Stack();
   private boolean acquiring = false;
   
   //these constants have two slightly different interpretations
   //in the switch in the constructor and as values for todo
   static final int LOAD       =1;
   static final int INIT       =2;
   static final int START      =3;
   static final int PAUSE      =4;
   static final int RESUME     =5;
   static final int STOP       =6;
   private static final int SEND_ERROR =7;
   
   static final int REMOVE =1;
   static final int APPEND =2;
   static final int APPEND_DELAYED =3;
      
   ApplicationTask(MHPApplication a, int procedure, Object arg) {
      app=a;
      args=arg;
      fillTodo(procedure);
   }
      
   synchronized void fillTodo(int procedure) {
      //this is the implementation of the application lifecycle state machine
      //See page 189 of the spec for a diagram, and see the specification
      //of AppProxy and DVBJProxy for details regarding the allowed states.
      todo.clear();
      switch (procedure) {
      
      //target state LOADED
      //allowed initial states: NOT_LOADED
      //illegal initial state: LOADED, STARTED, PAUSED, DESTROYED
      case LOAD:
         switch (app.getState()) {
         case AppProxy.NOT_LOADED:
            todo.push(new Integer(LOAD));
            if (app.hasListeners())
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), DVBJProxy.LOADED, app, false);
            break;
         case DVBJProxy.LOADED:
         case AppProxy.PAUSED:
         case AppProxy.STARTED:
         case AppProxy.DESTROYED:
            if (app.hasListeners()) {
               todo.push(new Integer(SEND_ERROR));
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), DVBJProxy.LOADED, app, true);
            }
            break;
         }
         break;
         
      //target state PAUSED
      //allowed initial states: NOT_LOADED, LOADED
      //illegal initial states: PAUSED, STARTED, DESTROYED
      case INIT:
         switch (app.getState()) {
         case AppProxy.NOT_LOADED:
            todo.push(new Integer(INIT));
            todo.push(new Integer(LOAD));
            if (app.hasListeners())
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.PAUSED, app, false);
            break;
         case DVBJProxy.LOADED:
            todo.push(new Integer(INIT));
            if (app.hasListeners())
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.PAUSED, app, false);
            break;
         case AppProxy.PAUSED:
         case AppProxy.STARTED:
         case AppProxy.DESTROYED:
            if (app.hasListeners()) {
               todo.push(new Integer(SEND_ERROR));
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.PAUSED, app, true);
            }
            break;
         }
         break;
         
      //target state PAUSED
      //allowed initial states: STARTED
      //illegal initial states: PAUSED, NOT_LOADED, LOADED, DESTROYED
      case PAUSE:
         switch (app.getState()) {
         case AppProxy.NOT_LOADED:
         case AppProxy.DESTROYED:
         case AppProxy.PAUSED:
         case DVBJProxy.LOADED:
            if (app.hasListeners()) {
               todo.push(new Integer(SEND_ERROR));
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.PAUSED, app, true);
            }
            break;
         case AppProxy.STARTED:
            todo.push(new Integer(PAUSE));
            if (app.hasListeners())
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.PAUSED, app, false);
            break;
         }
         break;
         
      //target state STARTED
      //allowed initial states: PAUSED
      //illegal initial states: STARTED, NOT_LOADED, LOADED, DESTROYED
      case RESUME:
         switch (app.getState()) {
         case AppProxy.NOT_LOADED:
         case DVBJProxy.LOADED:
         case AppProxy.STARTED:
         case AppProxy.DESTROYED:
            if (app.hasListeners()) {
               todo.push(new Integer(SEND_ERROR));
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.STARTED, app, true);
            }
            break;
         case AppProxy.PAUSED:
            todo.push(new Integer(RESUME));
            if (app.hasListeners())
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.STARTED, app, false);
            break;
         }
         break;
         
      //target state STARTED
      //allowed initial states: NOT_LOADED, LOADED, PAUSED
      //illegal initial states: STARTED, DESTROYED
      case START:
         switch (app.getState()) {
         case AppProxy.NOT_LOADED:
            //remember that todo is a last-in-first-out stack
            todo.push(new Integer(START));
            todo.push(new Integer(INIT));
            todo.push(new Integer(LOAD));
            if (app.hasListeners())
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.PAUSED, app, false);
            break;
         case DVBJProxy.LOADED:
            todo.push(new Integer(START));
            todo.push(new Integer(INIT));
            if (app.hasListeners())
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.PAUSED, app, false);
            break;
         case AppProxy.PAUSED:
            todo.push(new Integer(START));
            if (app.hasListeners())
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.PAUSED, app, false);
            break;
         case AppProxy.STARTED:
         case AppProxy.DESTROYED:
            if (app.hasListeners()) {
               todo.push(new Integer(SEND_ERROR));
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.STARTED, app, true);
            }
            break;
         }
         break;
      
      //target state DESTROYED
      //allowed initial states: NOT_LOADED, LOADED, STARTED, PAUSED
      //illegal initial states: DESTROYED
      case STOP:
         switch (app.getState()) {
         case AppProxy.DESTROYED:
            if (app.hasListeners()) {
               todo.push(new Integer(SEND_ERROR));
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.DESTROYED, app, true);
            }
            break;
         case AppProxy.PAUSED:
         case AppProxy.STARTED:
         case AppProxy.NOT_LOADED:
         case DVBJProxy.LOADED:
            todo.push(new Integer(STOP));
            if (app.hasListeners())
               event=new AppStateChangeEvent(app.getIdentifier(), app.getState(), AppProxy.DESTROYED, app, false);
            break;
         }
         break;
      }
   }
   
   synchronized int Execute() {
      int task;
      try {
         task=((Integer)todo.pop()).intValue();
      } catch (java.util.EmptyStackException e) {
         return REMOVE;
      }
      
      System.out.println("ApplicationTask: Executing task "+task);
      boolean success=false;
      switch (task) {
      //calling the non-API methods!
      case INIT:
         System.out.println("ApplicationTask: Calling doInit on application");
         if (!(success=((DVBJApplication)app).doInit()))
            vdr.mhp.Osd.LoadingFailed();
         break;
      case START:
         System.out.println("ApplicationTask: Calling doStart on application");
         if (args==null)
            success=app.doStart();
         else
            success=app.doStart((String[])args);
         if (!success)
            vdr.mhp.Osd.StartingFailed();
         break;
      case PAUSE:
         success=app.doPause();
         break;
      case RESUME:
         success=app.doResume();
         break;
      case STOP:
         System.out.println("ApplicationTask: Calling doStop on application");
         if (acquiring)
            stopAcquiring();
         boolean force = (args==null) ? true : ((Boolean)args).booleanValue();
         success=app.doStop(force);
         break;
      case LOAD:
         //this one is a bit special: we do everything from the ApplicationManager,
         //MHPApplication does not do real work.
         if (!acquiring)
            startAcquiring();
         //poll if loading the carousel finished
         //System.out.print("ApplicationTask, loading: is app acquired?");
         if (ApplicationManager.LoadingManagerInterface.isAcquired(app)) {
            //just set the state to LOADED
            //System.out.println(" Yes");
            success=((DVBJApplication)app).doLoad();
         } else {
            //again push load on top of todo
            //System.out.println(" No, push to delayedList");
            todo.push(new Integer(LOAD));
            //return this value to indicate that the task
            //shall be executed again, but only after a delay
            //to avoid busy waiting
            return APPEND_DELAYED;
         }
         break;
      case SEND_ERROR:
         success=true;
         //is sent below
         break;
      }
      
      //send event when everything is done
      if (todo.empty() && event!=null) {
         app.sendAppStateChangeEvent(event);
      }
      
      if (todo.empty() || !success)
         return REMOVE;
      else
         return APPEND;
   }
   
   void startAcquiring() {
      System.out.println("Starting acquire from Java");
      acquiring=true;     
      ApplicationManager.LoadingManagerInterface.load(app);
   }
   
   void stopAcquiring() {
      System.out.println("Stopping acquire from Java");
      ApplicationManager.LoadingManagerInterface.stop(app);
      acquiring=false;
   }
}


class ApplicationTaskThread extends Thread {

   java.util.LinkedList list = new java.util.LinkedList();
   java.util.LinkedList delayedList = new java.util.LinkedList();
   
   Thread thread;
   
   ApplicationTaskThread(ThreadGroup group) {
      super(group, "ApplicationTaskThread");
   }
   
   void requestStateChange(MHPApplication app, int procedure) {
      requestStateChange(app, procedure, null);
   }
   
   void requestStateChange(MHPApplication app, int procedure, Object arg) {
      ApplicationTask task = null;
      synchronized (this) {
         //We must make sure that there is not more than one task per application,
         //so first search through delayed list
         for (java.util.Iterator it=delayedList.iterator(); it.hasNext() && task==null; ) {
            ApplicationTask t = (ApplicationTask)it.next();
            if (t.app==app) {
               task=t;
               //move from delayedList to normal list
               it.remove();
               list.addLast(task);
            }
         }
         //then look in normal list
         for (java.util.Iterator it=list.iterator(); it.hasNext() && task==null; ) {
            ApplicationTask t = (ApplicationTask)it.next();
            if (t.app==app) {
               task=t;
            }
         }
         //if no task for the application is found, create one
         if (task==null) {
            if (!running) {
               running=true;
               try {
                  thread = new Thread(this);
                  thread.start();
               } catch (IllegalThreadStateException e) {
                  //If this thread has already been started and is dead now,
                  //some internal error probably occurred, only Errors can cause this.
                  e.printStackTrace();
                  return;
               }
            }
            System.out.println("Appending task");
            list.addLast(new ApplicationTask(app, procedure, arg));
            notifyAll();
            return;
         }
      }
      //If a task is found, change it according to the new request.
      //Do this outside synchronization on this. Instead, this function is synchronized,
      //and so is Execute, so that there is always a clearly defined state.
      task.fillTodo(procedure);
   }
   
   //stop thread when all pending tasks are done
   //guaranteed to be called only after MHPApplications.stopAll
   synchronized void completeAndStop() {
      completeAndStop=true;
   }
   
   void waitForShutdown() {
      if (completeAndStop && thread != null) {
         try {
            thread.join(2000);
         } catch (InterruptedException e) {
            e.printStackTrace();
            return;
         }
         thread=null;
      }
   }
   
   private boolean running = false;
   private boolean completeAndStop = false;
   
   public void run() {
      try {
         ApplicationTask task=null;
         int action = ApplicationTask.REMOVE;
         while (running) {
         
            synchronized(this) {
            
               if (task != null) {
                  switch (action) {
                  case ApplicationTask.APPEND:
                     //re-add to tail of list
                     list.addLast(task);
                     break;
                  case ApplicationTask.APPEND_DELAYED:
                     //(re-)add to tail of delayedList
                     delayedList.addLast(task);
                     break;
                  case ApplicationTask.REMOVE:
                     //drop task
                     break;
                  }
               }
                  
               //System.out.println("ApplicationTaskThread: list isEmpty? "+list.isEmpty()+" and delayedList "+delayedList.isEmpty());
               if (list.isEmpty()) {
                  task=null;
                  if (completeAndStop)
                     break;
                  try {
                     wait(1000);
                  } catch (InterruptedException e) {
                     //requestStateChange will notifyAll() on this and interrupt the sleep
                     continue;
                  }
                  //check delayed list only after waiting
                  if (delayedList.isEmpty())
                     continue;
                  else {
                     task=(ApplicationTask)delayedList.removeFirst();
                  }
               } else
                  task=(ApplicationTask)list.removeFirst();
            }
            
            if (task != null) {
               //here the actual work is done
               try {
                  action=task.Execute();
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
            
         }
      //these catch clauses are the last resort for internal errors.
      //All Exceptions thrown by application triggered code is caught above
      //All execution is disabled now that the loop is left.
      } catch (Exception e) {
         e.printStackTrace();
      } catch (Throwable x) {
         reportError(x);
      }
      running=false;
   }
}




}
