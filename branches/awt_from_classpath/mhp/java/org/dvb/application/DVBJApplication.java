package org.dvb.application;


import vdr.mhp.ApplicationManager;
import org.dvb.application.*;
import javax.tv.xlet.*;

public class DVBJApplication extends MHPApplication implements DVBJProxy, XletContext {

DVBJApplication(long nativeData) {
   super(nativeData);
   (new Exception("Stacktrace only: Here a new application is created")).printStackTrace();
}

protected Xlet xlet= null;
protected String[] parameters = null; //do not access directly, use getParameters
protected String[] dvbCallerParameters = null; //only used in a special case

static protected MHPApplication GetApplication(AppID id) {
   MHPApplication app;
   if ( (app=(MHPApplication)apps.get(id)) == null ) {
      long nD=getApplicationPointer(AppsDatabase.getAppsDatabase().getNativeData(), id.getOID(), id.getAID());
      if (nD != 0) {
         app=new DVBJApplication(nD);
         apps.put(id, app);
      } else
         app=null;
   }
   System.out.println("DVBJApplication: Requesting application app ID "+id.getAID()+", org ID "+id.getOID()+", returning "+app);
   return app;
}

static protected MHPApplication GetLocalApplication(AppID id, long nativeData) {
   MHPApplication app;
   if ( (app=(MHPApplication)apps.get(id)) == null ) {
      if (nativeData != 0) {
         app=new DVBJApplication(nativeData);
         apps.put(id, app);
      } else
         app=null;
   }
   return app;
}


private static native long getApplicationPointer(long nativeData /*database!*/, int oid, int aid);


String getBaseDir() {
   return new String(baseDir(nativeData));
}

private native byte[] baseDir(long nativeData);

String getClassPath() {
   return new String(classPath(nativeData));
}

private native byte[] classPath(long nativeData);

String getInitialClass() {
   return new String(initialClass(nativeData));
}

private native byte[] initialClass(long nativeData);

public String getCarouselBasePath() {
   return getCarouselRoot()+"/"+getBaseDir();
}

String[] getParameters() {
   if (parameters != null)
      return parameters;
   int num=numberOfParameters(nativeData);
   parameters=new String[num];
   for (int i=0;i<num;i++) {
      parameters[i]=new String(parameter(nativeData, i));
   }
   return parameters;
}

private native int numberOfParameters(long nativeData);
private native byte[] parameter(long nativeData, int index);

public java.lang.Object getProperty(java.lang.String index) {
   Object ret=super.getProperty(index);
   if (ret!=null)
      return ret;
      
   if (index.equals("dvb.j.location.base")) {
      return getBaseDir();
   } else if (index.equals("dvb.j.location.cpath.extension")) {
      //TODO, p. 261
      return new String[0];
   }
   return null;
}



/* The following functions are called from the ApplicationManager only. */

public boolean doStart() {
   //if (state != PAUSED)
   //   return;
   if (xlet == null)
      return false;
   try {
      xlet.startXlet();
      dvbCallerParameters=null;
   } catch (XletStateChangeException e) {
      return false;
   } catch (Throwable e) {
      e.printStackTrace();
      return false;
   }
   state=STARTED;
   return true;
}

public boolean doStart(java.lang.String[] args) {
   //if (state != PAUSED)
   //   return;
   if (xlet == null)
      return false;
   try {
      xlet.startXlet();
      dvbCallerParameters=args;
   } catch (XletStateChangeException e) {
      return false;
   } catch (Throwable e) {
      e.printStackTrace();
      return false;
   }
   state=STARTED;
   return true;
}

public boolean doPause() {
   //if (state != STARTED)
   //   return;
   if (xlet == null)
      return false;
   try {
      xlet.pauseXlet();
   }  catch (Throwable e) {
      e.printStackTrace();
      return false;
   }
   state=PAUSED;
   return true;
}

public boolean doResume() {
   //if (state != PAUSED)
   //   return;
   if (xlet == null)
      return false;
   try {
      xlet.startXlet();
   } catch (XletStateChangeException e) {
      return false;
   } catch (Throwable e) {
      e.printStackTrace();
      return false;
   }
   state=STARTED;
   return true;
}

public boolean doStop(boolean force) {
   //if (state != STARTED && state != PAUSED && state != LOADED)
   //   return;
   //This also allows to stop non-loaded applications.
   if (xlet != null) {
      try {
         xlet.destroyXlet(force);
      } catch (XletStateChangeException e) {
         if (!force)
            return false;
      } catch (Throwable e) {
         e.printStackTrace();
         return false;
      }
   }
   //it is very important to remove destroyed applications from the hashtable
   //the objects must not be started again
   setDestroyed();
   return true;
}

public void doEmergencyStop() {
   if (!doStop(true))
      setDestroyed();
}

void setDestroyed() {
   xlet=null;
   state=DESTROYED;
   apps.remove(getIdentifier());
}

public boolean doLoad() {
   state=LOADED;
   return true;
}

public boolean doInit() {
   //if (state != LOADED && state != NOT_LOADED)
   //   return;
   if (LoadXlet()) {
      try {
         xlet.initXlet(this);
      } catch (XletStateChangeException e) {
         return false;
      } catch (Throwable e) {
         e.printStackTrace();
         return false;
      }
      state=PAUSED;
      return true;
   }
   return false;
}

boolean LoadXlet() {
   if (xlet != null)
      return true;
   //TODO: honor the application's classPath appropriately (spec. page 231)
   java.net.URL url;
   try {
      url=new java.net.URL("file:"+getCarouselBasePath()+"/");
   } catch (java.net.MalformedURLException e) {
      e.printStackTrace();
      return false;
   }
   
   java.net.URL[] urls=new java.net.URL[1];
   urls[0]=url;
   org.dvb.lang.DVBClassLoader loader=new org.dvb.lang.DVBClassLoader(urls, this);
   try {
      //so, this can generate a lot of exceptions...
      xlet=(Xlet)Class.forName(getInitialClass(), true, loader).newInstance();
      return xlet != null;
   } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return false;
   } catch (ClassCastException e) {
      e.printStackTrace();
      return false;
   } catch (IllegalAccessException e) {
      e.printStackTrace();
      return false;
   } catch (InstantiationException e) {
      e.printStackTrace();
      return false;
   } /*catch (NoSuchMethodException e) {
      e.printStackTrace();
      return false;
   } catch (NoSuchFieldException e) {
      e.printStackTrace();
      return false;
   } */catch (RuntimeException e) {
      e.printStackTrace();
      return false;
   } catch (ExceptionInInitializerError e) {
      e.printStackTrace();
      return false;
   } /*catch (SecurityException e) {
      e.printStackTrace();
      return false;
   }*/
}


/* -------------  DVBJProxy interface-----------------*/

/*
Requests the application manager calls the initXlet method on the application. This method is asynchronous and its 
completion will be noti  ed by an AppStateChangedEvent.In case of failure,the hasFailed method of the 
AppStateChangedEvent will return true.Calls to this method shall only succeed if the application is in the NOT_LOADED or 
LOADED states.If the application is in the NOT_LOADED state,the application will move through the LOADED state into the 
PAUSED state before calls to this method complete. In all cases,an AppStateChangeEvent will be sent,whether the call was 
successful or not. Throws: SecurityException -if the application is not entitled to load this application.being able to 
load an application requires to be entitled to start it. */
public void init() {
   //if (state != LOADED && state != NOT_LOADED)
   //   return;
   ApplicationManager.getManager().InitApplication(this);
}


/*
Provides a hint to preload at least the initial class of the application into local storage,resources permitting.This 
does not require loading of classes into the virtual machine or creation of a new logical virtual machine which are 
implications of the init method.This method is asynchronous and its completion will be noti  ed by an 
AppStateChangedEvent In case of failure,the hasFailed method of the AppStateChangedEvent will return true.Calls to this 
method shall only succeed if the application is in the NOT_LOADED state.In all cases,an AppStateChangeEvent will be 
sent,whether the call was successful or not. Throws: SecurityException -if the application is not entitled to load this 
application.being able to load an application requires to be entitled to start 
it. */
public void load() {
   //if (state != NOT_LOADED)
   //   return;
   ApplicationManager.getManager().LoadApplication(this);
}

//overloaded from AppProxy, MHPApplication
/*
Request that the application manager start the application bound to this information structure. The application will be 
started.This method will throw a security exception if the application does not have the authority to start 
applications.Calls to this method shall only succeed if the application is in the not loaded or paused states.If the 
application was not loaded at the moment of this call, then the application will be started.In the case of a DVB-J 
application,it will be initialized and then started by the Application Manager,hence causing the Xlet to go from 
NotLoaded to Paused and then from Paused to Active.If the application was in the Paused state at the moment of the call 
and had never been in the Active state,then the application will be started.If the application represented by this 
AppProxy is a DVB-J application,calling this method will,if successful,result in the startXlet method being called on 
the Xlet making up the DVB-J application. This method is asynchronous and its completion will be noti  ed by an 
AppStateChangedEvent.In case of failure,the hasFailed method of the AppStateChangedEvent will return 
true. */
public void start() {
   /*if (state == NOT_LOADED || state == LOADED) {
      ApplicationManager.getManager().InitApplication(this);
      ApplicationManager.getManager().StartApplication(this);
   }
   if (state == PAUSED) {
      ApplicationManager.getManager().StartApplication(this);
   }*/
   ApplicationManager.getManager().StartApplication(this);
}

public void start(java.lang.String[] args) {
   /*if (state == NOT_LOADED || state == LOADED) {
      ApplicationManager.getManager().InitApplication(this);
      ApplicationManager.getManager().StartApplication(this, args);
   }
   if (state == PAUSED) {
      ApplicationManager.getManager().StartApplication(this, args);
   }*/
   ApplicationManager.getManager().StartApplication(this, args);
}










/* -------------  XletContext interface-----------------*/



/*
 
 Used by an application to notify its manager that it
 has entered into the
 Destroyed state. The application manager will not
 call the Xlet's destroy method, and all resources
 held by the Xlet will be considered eligible for reclamation. 
 Before calling this method,
 the Xlet must have performed the same operations
 (clean up, releasing of resources etc.) it would have if the
 Xlet.destroyXlet() had been called. 
 */

public void notifyDestroyed () {
   if (state == DESTROYED)
      return;
   setDestroyed();
   ApplicationManager.getManager().NotifyXletDestroyedItself(this);
}


/*
 
 Notifies the manager that the Xlet does not want to be active and has
 entered the Paused state. Invoking this method will
 have no effect if the Xlet is destroyed, or if it has not
 yet been started. */

public void notifyPaused () {
   if (state != STARTED)
      return;
   state=PAUSED;
   ApplicationManager.getManager().NotifyXletPausedItself(this);
}


/*
 
 Provides an Xlet with a mechanism to retrieve named
 properties from the XletContext. 
 Parameters:  key - The name of the property. Returns: A reference to an object representing the property.
 null is returned if no value is available for key. 
 
 
 */

public java.lang.Object getXletProperty (java.lang.String key) {
   if (key.equals(XletContext.ARGS)) {
      //return arguments
      return getParameters();
   }
   else if (key.equals("dvb.org.id")) {
      //an Integer? a String?? spec says nothing!
      return new Integer(getOID());
   }
   else if (key.equals("dvb.app.id")) {
      return new Integer(getAID());
   }
   else if (key.equals("dvb.caller.parameters")) {
      //the parameters passed to this application if it was
      // started by a mechanism other than application signalling.
      // (i.e. AppProxy.start(java.lang.String[] args))
      return dvbCallerParameters;
   }
   return null;
}


/*
 
 Provides the Xlet with a mechanism to indicate that it is
 interested in entering the Active state. Calls to this
 method can be used by an application manager to determine which
 Xlets to move to Active state. Any subsequent call to
 Xlet.startXlet() as a result of this method will
 be made via a different thread than the one used to call
 resumeRequest() . 
 See Also:   Xlet.startXlet()  
 
 
*/

public void resumeRequest () {
   //IMPLEMENT
}

}
