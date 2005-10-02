
package vdr.mhp;
import org.dvb.application.*;
import org.dvb.lang.DVBClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import vdr.mhp.lang.NativeData;

/*
   The ApplicationManager is the core class of this implementation.
   All operations are started, controlled and stopped from this class.
   The native plugin will access the static methods via JNI to initiate
   starting and stopping. The ApplicationManager also provides an internal API
   through which all starting and stopping operations from
   the Java side (org.dvb.application package) are routed. This class provides
   the state machine, controls the DSMCC loading on the native side and
   initiates all actual state change operations of applications.
   The ApplicationManager also sets the security manager, important
   properties and provides some general infrastructure for the implementation.
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
      SettingsPolicy.setPolicy(new VDRSettingsPolicy());
      
      System.setSecurityManager(securityManager);
   } catch (Exception e) {
      e.printStackTrace();
   }
}







/* --- Interface called by JNI --- */


/* Parameters:
   appDatabase - pointer to a cApplicationDatabase
*/
public static int Initialize(NativeData appDatabase) {
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
      System.out.println("ApplicationManager.Init(): Caught exception");
      e.printStackTrace();
      return -1;
   }
   getManager().init=true;
   System.out.println("Leaving successfully ApplicationManager.Init()");
   return 0;
}

public static int NewApplication(NativeData nativeData) {
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

public static int ApplicationRemoved(NativeData nativeData) {
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




public static int StartApplication(NativeData nativeData) {
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

public static int StopApplication(NativeData nativeData) {
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

public static int PauseApplication(NativeData nativeData) {
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

public static int ResumeApplication(NativeData nativeData) {
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

//Encapsulation of the native access methods for Mhp::LoadingManager
//and Mhp::RunningManager from mhploading.h/mhpcontrol.h/mhpcontrol.c

static class LoadingManagerInterface {
   static void load(MHPApplication app) {
      load(app.getNativeData());
   }
   private static native void load(NativeData nativeData);
   
   static void stop(MHPApplication app) {
      stop(app.getNativeData());
   }
   private static native void stop(NativeData nativeData);
   
   static boolean isAcquired(MHPApplication app) {
      return isAcquired(app.getNativeData());
   }
   private static native boolean isAcquired(NativeData nativeData);
}

static class RunningManagerInterface {
   static void started(MHPApplication app) {
      started(app.getNativeData());
   }
   private static native void started(NativeData nativeData);
   
   static void stopped(MHPApplication app) {
      stopped(app.getNativeData());
   }
   private static native void stopped(NativeData nativeData);
}

//ThreadGroup for the ApplicationTaskThread from which
//most interaction with (and thus thread creation in)
//applications originates. However, this is not 100% waterproof,
//some other threads will access application code as well.

class ApplicationThreadGroup extends ThreadGroup {

   ApplicationThreadGroup(String s) {
      super(s);
   }
   
   public void uncaughtException(Thread t, Throwable e) {
      System.out.println("ApplicationThreadGroup: uncaughtException!");
      ApplicationManager.reportError(e);
   }

}

//Internal helper class to track state changes, success of the operation, and finally send an event.

class StateChangeNote {

   MHPApplication app;
   int initialState;
   int targetState;
   boolean error = false;
   boolean sent = false;

   StateChangeNote(MHPApplication a, int initialState, int targetState) {
      this.app=a;
      this.initialState=initialState;
      this.targetState=targetState;
   }
   
   void setError() {
      error=true;
   }
   
   void sendNotification() {
      if (!sent && app.hasListeners()) {
         new AppStateChangeEvent(app.getIdentifier(), initialState, targetState, app, error);
      }
      sent=true;
   }

}

//An application task executes a state change from the initial state
//to a requested target states. This class implements the application state machine,
//splits the task in single state transitions
//and orders DVBJApplication to carry out the state transitions.

class ApplicationTask {

   MHPApplication app;
   Object arg;
   StateChangeNote note = null;
   java.util.Stack todo = new java.util.Stack();
   private boolean acquiring = false;
   private int targetState = -1;
   
   //these constants have two slightly different interpretations
   //in the switch in the constructor and as values for todo
   static final int LOAD       =1;
   static final int INIT       =2;
   static final int START      =3;
   static final int PAUSE      =4;
   static final int RESUME     =5;
   static final int STOP       =6;
   private static final int SEND_NOTIFICATION =7;
   
   static final int REMOVE =1;
   static final int APPEND =2;
   static final int APPEND_DELAYED =3;
      
   ApplicationTask(MHPApplication a) {
      app=a;
   }
   
   synchronized void setTask(int procedure) {
      setTask(procedure, null);
   }
   
   synchronized void setTask(int procedure, Object arg) {
      if (procedure==targetState && this.arg==arg)
         return;
      //interruption?
      if (note != null) {
         note.setError();
         note.sendNotification();
      }
      this.arg=arg;
      targetState=procedure;
      fillNote();
      fillTodo();
   }
      
   private void fillNote() {
      switch (targetState) {
      
      //target state LOADED
      case LOAD:
         note = new StateChangeNote(app, app.getState(), DVBJProxy.LOADED);
         break;
         
      //target state PAUSED
      case INIT:
         note = new StateChangeNote(app, app.getState(), AppProxy.PAUSED);
         break;
         
      //target state PAUSED
      case PAUSE:
         note = new StateChangeNote(app, app.getState(), AppProxy.PAUSED);
         break;
         
      //target state STARTED
      case RESUME:
         note = new StateChangeNote(app, app.getState(), AppProxy.STARTED);
         break;
         
      //target state STARTED
      case START:
         note = new StateChangeNote(app, app.getState(), AppProxy.STARTED);
         break;
         
      //target state DESTROYED
      case STOP:
         note = new StateChangeNote(app, app.getState(), AppProxy.DESTROYED);
         break;
      }
   }
      
   private void fillTodo() {
      //this is the implementation of the application lifecycle state machine
      //See page 189 of the spec for a diagram, and see the specification
      //of AppProxy and DVBJProxy for details regarding the allowed states.
      todo.clear();
      switch (targetState) {
      
      //target state LOADED
      //allowed initial states: NOT_LOADED
      //illegal initial state: LOADED, STARTED, PAUSED, DESTROYED
      case LOAD:
         switch (app.getState()) {
         case AppProxy.NOT_LOADED:
            todo.push(new Integer(LOAD));
            break;
         case DVBJProxy.LOADED:
         case AppProxy.PAUSED:
         case AppProxy.STARTED:
         case AppProxy.DESTROYED:
            todo.push(new Integer(SEND_NOTIFICATION));
            note.setError();
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
            break;
         case DVBJProxy.LOADED:
            todo.push(new Integer(INIT));
            break;
         case AppProxy.PAUSED:
         case AppProxy.STARTED:
         case AppProxy.DESTROYED:
            todo.push(new Integer(SEND_NOTIFICATION));
            note.setError();
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
            todo.push(new Integer(SEND_NOTIFICATION));
            note.setError();
            break;
         case AppProxy.STARTED:
            todo.push(new Integer(PAUSE));
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
            todo.push(new Integer(SEND_NOTIFICATION));
            note.setError();
            break;
         case AppProxy.PAUSED:
            todo.push(new Integer(RESUME));
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
            break;
         case DVBJProxy.LOADED:
            todo.push(new Integer(START));
            todo.push(new Integer(INIT));
            break;
         case AppProxy.PAUSED:
            todo.push(new Integer(START));
            break;
         case AppProxy.STARTED:
         case AppProxy.DESTROYED:
            todo.push(new Integer(SEND_NOTIFICATION));
            note.setError();
            break;
         }
         break;
      
      //target state DESTROYED
      //allowed initial states: NOT_LOADED, LOADED, STARTED, PAUSED
      //illegal initial states: DESTROYED
      case STOP:
        switch (app.getState()) {
         case AppProxy.DESTROYED:
            todo.push(new Integer(SEND_NOTIFICATION));
            note.setError();
            break;
         case AppProxy.PAUSED:
         case AppProxy.STARTED:
         case AppProxy.NOT_LOADED:
         case DVBJProxy.LOADED:
            todo.push(new Integer(STOP));
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
      
      //System.out.println("ApplicationTask: Executing task "+task);
      boolean success=true;
      //After having accessed the application code we may not make any assumption about
      //the contents of todo; we call StopApplication ourselves in some cases and alter todo!
      switch (task) {
      //calling the non-API methods!
      case INIT:
         System.out.println("ApplicationTask: Calling doInit on application");
         success=((DVBJApplication)app).doInit();
         if (!success) {
            note.setError();
            vdr.mhp.Osd.LoadingFailed();
            //Stop application if loading fails
            //calling this from here is safe!
            StopApplication(app, true);
         }
         break;
      case START:
         System.out.println("ApplicationTask: Calling doStart on application");
         if (arg==null)
            success=app.doStart();
         else
            success=app.doStart((String[])arg);
         if (!success) {
            vdr.mhp.Osd.StartingFailed();
            //Stop application if starting fails
            StopApplication(app, true);
            note.setError();
         } else 
            notifyStarted();
         break;
      case PAUSE:
         success=app.doPause();
         if (!success)
            note.setError();
         else
            notifyStarted();
         break;
      case RESUME:
         success=app.doResume();
         if (!success)
            note.setError();
         else
            notifyStarted();
         break;
      case STOP:
         System.out.println("ApplicationTask: Calling doStop on application");
         if (acquiring)
            stopAcquiring();
         boolean force = (arg==null) ? true : ((Boolean)arg).booleanValue();
         //Only case where watchdog is activated
         taskThread.watchdog.setBiting(force);
         //may only fail if not forced
         success=app.doStop(force);
         if (!success)
            note.setError();
         else
            notifyStopped();
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
            if (!success)
               note.setError();
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
      case SEND_NOTIFICATION:
         //is sent below
         break;
      }
      
      //send notification when everything is done or an error occurred
      if (todo.isEmpty())
         note.sendNotification();
         
      if (todo.isEmpty())
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
   
   //only set the state, may be called multiple times
   void notifyStarted() {
      ApplicationManager.RunningManagerInterface.started(app);
   }
   
   void notifyStopped() {
      ApplicationManager.RunningManagerInterface.stopped(app);
   }
}

//This Thread manages a list of ApplicationTasks and executes them

class ApplicationTaskThread extends Thread {

   java.util.LinkedList list = new java.util.LinkedList();
   java.util.LinkedList delayedList = new java.util.LinkedList();
   ApplicationTask currentTask =  null;
   WatchdogThread watchdog = null;
   
   Thread thread;
   
   ApplicationTaskThread(ThreadGroup group) {
      super(group, "ApplicationTaskThread");
      watchdog=new WatchdogThread();
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
         //Check for the possibility that the call of this method originates from our
         //very own working thread, i.e. from the "currentTask.Execute()" below,
         //and if it affects the application and the task that is currently executed
         if (currentTask != null && currentTask.app==app) {
            //If we are here, we are in the worker thread, called in some way by 
            //run() and task.Execute()!
            task=currentTask;
         }
         //if no task for the application is found, create one
         if (task==null) {
            if (!running) {
               running=true;
               try {
                  thread = new Thread(this);
                  watchdog.setThread(thread);
                  thread.start();
               } catch (IllegalThreadStateException e) {
                  //If this thread has already been started and is dead now,
                  //some internal error probably occurred, only Errors can cause this.
                  e.printStackTrace();
                  return;
               }
            }
            System.out.println("Appending task");
            task = new ApplicationTask(app);
            task.setTask(procedure, arg);
            list.addLast(task);
            //wake up thread
            notifyAll();
            return;
         }
      }
      //If a task is found, change it according to the new request.
      //Do this outside synchronization on this. Instead, this function is synchronized,
      //and so is Execute, so that there is always a clearly defined state.
      task.setTask(procedure, arg);
   }
   
   //stop thread when all pending tasks are done
   //guaranteed to be called only after MHPApplications.stopAll
   synchronized void completeAndStop() {
      completeAndStop=true;
      notifyAll();
      //do not set watchdog to stop here, it shall guard the stopping process!
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
         if (watchdog != null) {
            watchdog.setThread(null);
            watchdog.completeAndStop();
            try {
               watchdog.join(2000);
            } catch (InterruptedException e) {
               e.printStackTrace();
               return;
            }
         }
      }
   }
   
   private boolean running = false;
   private boolean completeAndStop = false;
   
   public void run() {
      try {
         int action = ApplicationTask.REMOVE;
         while (running) {
         
            synchronized(this) {
            
               if (currentTask != null) {
                  switch (action) {
                  case ApplicationTask.APPEND:
                     //re-add to tail of list
                     list.addLast(currentTask);
                     break;
                  case ApplicationTask.APPEND_DELAYED:
                     //(re-)add to tail of delayedList
                     delayedList.addLast(currentTask);
                     break;
                  case ApplicationTask.REMOVE:
                     //drop task
                     break;
                  }
               }
                  
               //System.out.println("ApplicationTaskThread: list isEmpty? "+list.isEmpty()+" and delayedList "+delayedList.isEmpty());
               if (list.isEmpty()) {
                  currentTask=null;
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
                     currentTask=(ApplicationTask)delayedList.removeFirst();
                  }
               } else
                  currentTask=(ApplicationTask)list.removeFirst();
            }
            
            watchdog.setBiting(false);
            
            if (currentTask != null) {
               //here the actual work is done
               try {
                  action=currentTask.Execute();
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
            
            watchdog.reset();
            
         }
      //these catch clauses are the last resort for internal errors.
      //All Exceptions thrown by application triggered code is caught above
      //All execution is disabled now that the loop is left.
      } catch (Exception e) {
         e.printStackTrace();
      } catch (Throwable x) {
         reportError(x);
      }
      watchdog.setThread(null);
      running=false;
   }
}

class WatchdogThread extends Thread {
   final static int DEFAULT_TIMEOUT = 10 * 1000;
   long timeout = 0;
   long time;
   private boolean running;
   Thread thread = null;
   public void run() {
      time = System.currentTimeMillis();
      while (running) {
         synchronized (this) {
            try {
               wait(1000);
            } catch (InterruptedException e) {
               continue;
            }
            if (timeout != 0 && System.currentTimeMillis()-time > timeout)
               attack();
         }
      }
   }
   
   public synchronized void reset() {
      time = System.currentTimeMillis();
      notify();
   }
   
   public synchronized void setBiting(boolean bite) {
      if (bite)
         timeout=DEFAULT_TIMEOUT;
      else
         timeout=0;
   }
   
   public synchronized void setThread(Thread thread) {
      this.thread=thread;
   }
   
   public synchronized void completeAndStop() {
      running = false;
      notifyAll();
   }
   
   private void attack() {
      // The Spec says Thread.stop() is deprecated, use interrupt() in such case.
      // As well, it is said that if interrupt has no effect, stop() has not either.
      reportError(new RuntimeException("Watchdog timer for TaskThread expired, interrupting thread!"));
      thread.interrupt();
   }
}




}
