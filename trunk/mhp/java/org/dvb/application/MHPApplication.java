package org.dvb.application;


import java.util.Hashtable;
import java.util.Iterator;
import javax.tv.xlet.*;
import org.dvb.application.*;
import vdr.mhp.ApplicationManager;
import vdr.mhp.lang.NativeData;
import javax.tv.service.Service;
import javax.tv.service.VDRService;


//TODO: implement creating AppStateChangeEvents (in DVBJApplication I think)

public abstract class MHPApplication extends AppID implements AppProxy, AppAttributes {

AppStateChangeEventListener AppSCEListener = null;
int state = NOT_LOADED;
static Hashtable apps = new Hashtable();
VDRService service = null;

NativeData nativeData;

public MHPApplication(NativeData nativeData) {
   super(getOid(nativeData), getAid(nativeData));
   this.nativeData=nativeData;
}

 //implemented by DVBJApplication
static protected MHPApplication GetApplication(AppID id) {
   return DVBJApplication.GetApplication(id);
}

static public MHPApplication GetApplication(NativeData nativeData) {
   switch (type(nativeData)) {
   case AppAttributes.DVB_J_application:
      return DVBJApplication.GetApplication(new AppID(getOid(nativeData), getAid(nativeData)));
   case AppAttributes.LOCAL_DVB_J_application:
      return DVBJApplication.GetLocalApplication(new AppID(getOid(nativeData), getAid(nativeData)), nativeData);
   case AppAttributes.DVB_HTML_application:
   case AppAttributes.LOCAL_DVB_HTML_application:
      return null; //not implemented
   }
   return null;
}


/*static void RemoveEntry(AppID key) {
   apps.remove(key);
}*/

//private abstract static int getApplicationPointer(int oid, int aid);

private static native int getAid(NativeData nativeData);
private static native int getOid(NativeData nativeData);

//get the path under which the Object Carousel is stored
public String getCarouselRoot() {
   return carouselRoot(nativeData);
}

private native String carouselRoot(NativeData nativeData);

//path used as a base directory for relative path names (overridden by DVBJApplication)
public String getCarouselBasePath() {
   return getCarouselRoot();
}

public NativeData getNativeData() {
   return nativeData;
}

public NativeData getNativeChannel() {
    return channel(nativeData);
}

// private native NativeData channel(NativeData nativeData, NativeData nativeChannel);
private native NativeData channel(NativeData nativeData);


/* Interface for ApplicationManager */
public abstract boolean doStart();
public abstract boolean doStart(java.lang.String[] args);
public abstract boolean doPause();
public abstract boolean doResume();
public abstract boolean doStop(boolean force);

//For ApplicationManager only!
static public void stopAll() {
   //clone the Hashtable because it will be modified 
   //from another thread when app is stopped
   Object[] c=apps.values().toArray();
   for (int i=0;i<c.length;i++) {
      ((MHPApplication)c[i]).stop(true);
   }
}

//For ApplicationManager only!
static public void clearList() {
   apps.clear();
}



/*---------------- AppProxy interface --------------------*/


/*
Add a listener to the application proxy so that an application can be informed if the application changes state. 
Parameters: listener -the listener to be added. */
public void addAppStateChangeEventListener(AppStateChangeEventListener listener) {
   AppSCEListener=DatabaseMulticaster.add(AppSCEListener, listener);
}


/*
Remove a listener on the database. Parameters: listener -the listener to be 
removed. */
public void removeAppStateChangeEventListener(AppStateChangeEventListener listener) {
   AppSCEListener=DatabaseMulticaster.remove(AppSCEListener, listener);
}

//not official API
public boolean hasListeners() {
   return AppSCEListener==null;
}

//not official API
public void sendAppStateChangeEvent(AppStateChangeEvent event) {
   if (AppSCEListener != null)
      AppSCEListener.stateChange(event);
}


/*
Return the current state of the application. Returns: the state of the application. */
public int getState() {
   return state;
}


/*
Request that the application manager pause the application bound to this information structure. The application will be 
paused.Calls to this method shall fail if the application is not in the active state.If the application represented by 
this AppProxy is a DVB-J application,calling this method will, if successful,result in the pauseXlet method being called 
on the Xlet making up the DVB-J application. Throws: SecurityException -if the application is not entitled to pause this 
application.Note that if an application is entitled to stop an application,it is also entitled to pause it:having the 
right to stop an application is logically equivalent to having the right to pause 
it. */
public void pause() {
   //if (state != STARTED)
   //   return;
   ApplicationManager.getManager().PauseApplication(this);
}


/*
Request that the application manager resume the execution of the application.The application will be started.This method 
will throw a security exception if the application does not have the authority to resume the application.Calls to this 
method shall fail if the application is not in the paused state. This method is asynchronous and its completion will be 
noti  ed by an AppStateChangedEvent.In case of failure,the hasFailed method of the AppStateChangedEvent will return 
true.If the application represented by this AppProxy is a DVB-J application,calling this method will,if 
successful,result in the startXlet method being called on the Xlet making up the DVB-J application. Throws: 
SecurityException -if the application is not entitled to resume this application. */
public void resume() {
   //if (state != PAUSED)
   //   return;
   ApplicationManager.getManager().ResumeApplication(this);
}


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
//overloaded by DVBJApplication
public void start() {
   //if (state != PAUSED && state != NOT_LOADED)
   //   return;
   ApplicationManager.getManager().PauseApplication(this);
}


/*
Request that the application manager start the application bound to this information structure passing to that 
application the speci  ed parameters.The application will be started.This method will throw a security exception if the 
application does not have the authority to start applications.Calls to this method shall only succeed if the application 
is in the not loaded or paused states.If the application was not loaded at the moment of this call, then the application 
will be started.In the case of a DVB-J application,it will be initialized and then started by the Application 
Manager,hence causing the Xlet to go from NotLoaded to Paused and then from Paused to Active.If the application was in 
the Paused state at the moment of the call and had never been in the Active state,then the application will be 
started.If the application represented by this AppProxy is a DVB-J application,calling this method will,if 
successful,result in the startXlet method being called on the Xlet making up the DVB-J application. This method is 
asynchronous and its completion will be noti  ed by an AppStateChangedEvent.In case of failure,the hasFailed method of 
the AppStateChangedEvent will return true. Parameters: args -the parameters to be passed into the application being 
started Throws: SecurityException -if the application is not entitled to start this 
application. */
public void start(java.lang.String[] args) {
   //if (state != PAUSED && state != NOT_LOADED)
   //   return;
   ApplicationManager.getManager().PauseApplication(this);
}


/*
Request that the application manager stop the application bound to this information structure. The application will be 
stopped.A call to this method shall fail if the application was already in the destroyed state.This method call will 
stop the application if it was in any other state before the call.If the application is in the NOT_LOADED state then it 
shall move directly to the DESTROYED state with no other action being taken.If the application represented by this 
AppProxy is a DVB-J application and is not in the DESTROYED state then calling this method will,if successful,result in 
the destroyXlet method being called on the Xlet making up the DVB-J application with the same value for the parameter as 
passed to this method. This method is asynchronous and its completion will be noti  ed by an AppStateChangedEvent.In 
case of failure,the hasFailed method of the AppStateChangedEvent will return true. Parameters: forced -if true then do 
not ask the application but forcibly terminate it,if false give the application an opportunity to refuse. Throws: 
SecurityException -if the application is not entitled to stop this application. */
public void stop(boolean forced) {
   /*if (state == NOT_LOADED) {
      state=DESTROYED;
      return;
   }
   if (state == DESTROYED)
      return;*/
   ApplicationManager.getManager().StopApplication(this, forced);
}











/* -------------  AppAttributes interface-----------------*/


/*
This method returns an object encapsulating the information about the icon(s)for the application. Returns: the 
information related to the icons that are attached to the application or null if no icon information is 
available */
public AppIcon getAppIcon() {
   return new AppIcon();
}


/*
This method returns the application identi  er.depending on the Returns: the application identi  
er */
public AppID getIdentifier() {
   return new AppID(getOid(nativeData), getAid(nativeData));
}

//private native int getOid(NativeData nativeData);
//private native int getAid(NativeData nativeData);

/*
This method determines whether the application is bound to a single service. Returns: true if the application is bound 
to a single service,false otherwise. */
public boolean getIsServiceBound() {
   return isServiceBound(nativeData);
}

private native boolean isServiceBound(NativeData nativeData);

/*
This method returns the name of the application.If the default language (as speci  ed in user preferences)is in the set 
of available language /name pairs then the name in that language shall be returned.Otherwise this method will return a 
name which appears in that set on a "best-effort basis". Returns: the name of the 
application */
public String getName() {
   return name(nativeData);
}

private native String name(NativeData nativeData);


/*
This method returns the name of the application in the language which is speci  ed by the parameter passed as an 
argument.If the language speci  ed is not in the set of available language /name pairs then an exception shall be 
thrown. Parameters: iso639code -the speci  ed language,encoded as per ISO 639. Returns: returns the name of the 
application in the speci  ed language Throws: LanguageNotAvailableException -if the name is not available in the 
language speci  ed */
public java.lang.String getName(java.lang.String iso639code) throws LanguageNotAvailableException{
   String name=nameForLanguage(nativeData, iso639code);
   if (name == null)
      throw new LanguageNotAvailableException();
   return name;
}

private native String nameForLanguage(NativeData nativeData, String iso639code);


/*
This method returns all the available names for the application together with their ISO 639 language code. Returns:the 
possible names of the application,along with their ISO 639 language code.The  rst string in each sub-array is the ISO 
639 language code.The second string in each sub-array is the corresponding application 
name. */
public java.lang.String[][] getNames() {
   System.err.println("MHPApplication.getNames(): Returning array of length null. Implement me (trivial)");
   return new String[0][0];
   //return names(nativeData);
}

//private native String[][] names(NativeData nativeData);

/*
This method returns the priority of the application. Returns: the priority of the 
application. */
public int getPriority() {
   return priority(nativeData);
}

private native int priority(NativeData nativeData);


/*
This method returns those minimum pro  les required for the application may execute.Pro  le names shall be encoded using 
the same encoding speci  ed elsewhere in this speci  cation as input for use with the java.lang.System.getProperty 
method to query if a pro  le is supported by this platform. For example,for implementations conforming to the  rst 
version of the speci  cation,the translation from AIT signaling values to strings shall be as follows: " '1' in the 
signaling will be translated into 'mhp.profile.enhanced_broadcast' " '2' in the signaling will be translated into 
'mhp.profile.interactive_broadcast' Only pro  les known to this particular MHP terminal shall be returned.Hence the 
method can return an array of size zero where all the pro  les on which an application can execute an application are 
unknown. Returns: an array of Strings,each String describing a pro  le. */
public java.lang.String[] getProfiles() {
   System.err.println("MHPApplication.getProfiles(): Returning array of length null. Implement me (trivial)");
   return new String[0];
   //return profiles(nativeData);
}

//private native String[] profiles(NativeData nativeData);


/*
The following method is included for properties that do not have explicit property accessors.The naming of properties 
and their return values are described in the main body of this speci  cation. (p.261) Parameters: index -a property name 
Returns: either the return value corresponding to the property name or null if the property name is 
unknown */
public java.lang.Object getProperty(java.lang.String index) {
   if (index.equals("dvb.transport.oc.component.tag")) {
      return Integer.toString(componentTag(nativeData));
   }
   return null;
}

private native int componentTag(NativeData nativeData);
//private native String property(NativeData nativeData, String index);


/*
This method returns the locator of the Service describing the application.For an application transmitted on a remote 
connection,the returned locator shall be the service for that remote connection.For applications not transmitted on a 
remote connection,the service returned shall be the currently selected service of the service context within which the 
application calling the method is running. Returns: the locator of the Service describing the 
application. */
public org.davic.net.Locator getServiceLocator() {
   //all locators in this implementation are actually org.davic.net.dvb.DvbLocators
   return (org.davic.net.Locator)getService().getLocator();
}

// internal API
// Follows specification of getServiceLocator() above
// Shall not return null.
public javax.tv.service.VDRService getService() {
   if (service == null) {
      NativeData channel = getNativeChannel();
      if (channel.isNull())
         service = VDRService.getServiceForNativeChannel(channel);
      else
         service = VDRService.getCurrentService();
   }
   return service;
}


/*
This method returns the type of the application (as registered by DVB). Returns: the type of the application (as 
registered by DVB). */
public int getType() {
   return type(nativeData);
}

private static native int type(NativeData nativeData);


/*
This method returns an array of integers containing the version number of the speci  cation required to run this 
application at the speci  ed pro  le. Parameters: profile -a pro  le encoded as described in the main body of this speci 
 cation for use with java.lang.System.getProperty Returns: an array of integers,containing the major,minor and micro 
values (in that order)required for the speci  ed pro  le. Throws: IllegalProfileParameterException -thrown if the pro  
le speci  ed is not one of the minimum pro  les required for the application to 
execute. */
public int[] getVersions(java.lang.String profile) {
   int [] ret=new int[3];
   ret[0]=1;
   ret[1]=0;
   ret[2]=2; //1.0.2
   System.err.println("MHPApplication.getVersions(): Returning 1.0.2 hardcoded");
   return ret;
}


/*
This method determines whether the application is startable or not.An Application is not startable if any of the 
following apply. " The application is transmitted on a remote connection. " The caller of the method does not have the 
Permissions to start it. " At the moment when the method is called, the implementation has detected that this 
application is not available any more. */
public boolean isStartable() {
   return startable(nativeData);
}

private native boolean startable(NativeData nativeData);




}
