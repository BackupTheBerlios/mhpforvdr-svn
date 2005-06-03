
package org.dvb.application;



/*The AppsDatabase is an abstract view of the currently available applications.The entries 
will be provided by the application manager,and gleaned from the AIT signaling.When the 
service context in which an application is running undergoes service selection,instances 
of AppsDatabase used by that application shall be updated from the new service before an 
AppsDatabaseEvent is sent to the newDatabase method of any registered 
AppsDatabaseEventListeners Externally authorized applications shall not appear unless an 
instance of that application is actually running. A generic launcher may be written which 
uses the database to display information in AppAttributes and uses an AppProxy to launch 
it Methods on classes in this package do not block,they return the information the system 
currently has. Therefore applications should be aware that data may be stale,to within one 
refresh period of the AIT. (page ) ... */


//TODO: getAppAttributes(AppsDatabaseFilter filter), getAppIDs(AppsDatabaseFilter filter)

public class AppsDatabase {

static AppsDatabase self=null;


AppsDatabaseEventListener dbEvListener=null;
long nativeData = 0; //a pointer to an ApplicationInfo::cApplicationsDatabase
//protected Hashtable apps;

//called by the ApplicationManager in the initialization procedure
public static void CreateDatabase(long nativeData) {
   new AppsDatabase(nativeData);
}

AppsDatabase(long nativeData) {
   self=this;
   this.nativeData=nativeData;
   //apps = new Hashtable();
}

long getNativeData() {
   return nativeData;
}

/*
Add a listener to the database so that an application can be informed if the database changes. Parameters: listener -the 
listener to be added. */
public void addListener(AppsDatabaseEventListener listener) {
   dbEvListener=DatabaseMulticaster.add(dbEvListener, listener);
}

//not API
public void NotifyNewApplication(long nativeData) {
   MHPApplication app=MHPApplication.GetApplication(nativeData);
   if (dbEvListener == null || app == null)
      return;
   AppsDatabaseEvent e=new AppsDatabaseEvent(AppsDatabaseEvent.APP_ADDED, app, this);
   if (dbEvListener != null)
      dbEvListener.entryAdded(e);
}

public void NotifyApplicationRemoved(long nativeData) {
   MHPApplication app=MHPApplication.GetApplication(nativeData);
   if (dbEvListener == null || app == null)
      return;
   AppsDatabaseEvent e=new AppsDatabaseEvent(AppsDatabaseEvent.APP_DELETED, app, this);
   if (dbEvListener != null)
      dbEvListener.entryRemoved(e);
   
}



/*
Returns the properties associated with the given ID.Returns null if no such application is available, or if the 
application is externally authorized. Only one AppAttributes object shall be returned in the case where there are 
several applications having the same (organisationId,applicationId)pair.In such a case,the same algorithm as would be 
used to autostart such applications shall be used to decide between the available choices by the implementation. This 
method shall return instances which re  ect the contents of the database at the time the method is called.After an 
AppsDatabaseEvent has been generated,new instances may be returned.After a service selection has taken 
place,applications which survived the service selection may call this method in order to discover the attributes of the 
applications signalled on the new service. Parameters: key -an application ID. Returns: the value to which the key is 
mapped in this dictionary or null if the key is not an application ID, or not mapped to any application currently 
available. */
public AppAttributes getAppAttributes(AppID key) {
   return MHPApplication.GetApplication(key);
}

/*
Returns an enumeration of AppAttributes of the applications available.The Enumeration will contain the set of 
AppAttributes that satisfy the  ltering criteria.For implementations conforming to this version of the speci  
cation,only CurrentServiceFilter  lters may return a non empty Enumeration.If the  lter object is not an instance of 
CurrentServiceFilter or a subclasses then,the method shall return an empty Enumeration. This method shall return 
instances which re  ect the contents of the database at the time the method is called.After an AppsDatabaseEvent has 
been generated,new instances may be returned.After a service selection has taken place,applications which survived the 
service selection may call this method in order to discover the attributes of the applications signalled on the new 
service. No AppAttribute shall be returned for externally authorized applications,even ones which are executing.This 
method will return an empty Enumeration if there are no attributes. Parameters: filter -the  lter to applyReturns: an 
enumeration of the applications attributes. */
public java.util.Enumeration getAppAttributes(AppsDatabaseFilter filter) {
   //IMPLEMENT
   return null;
}

/*
Returns an enumeration of the application ID's available.The Enumeration will contain the set of AppID that match the  
ltering criteria.For implementations conforming to this version of the speci  cation,only CurrentServiceFilter  lters 
may return a non empty Enumeration.If the  lter object is not an instance of CurrentServiceFilter or one of its 
subclasses then,the method shall return an empty Enumeration.No IDs shall be returned for externally authorized 
applications,even ones which are executing.This method will return an empty Enumeration if there are no matching 
applications. Parameters: filter -the  lter to apply Returns: the applications available matching the  ltering 
criteria */
public java.util.Enumeration getAppIDs(AppsDatabaseFilter filter) {
   //IMPLEMENT
   return null;
}

/*
Returns the ApplicationProxy associated with the given ID.Returns null if no such application available. Only one 
AppProxy object shall be returned in the case where there are several applications having the same 
(organisationId,applicationId)pair.In such a case,the same algorithm as would be used to autostart such applications 
shall be used to decide between the available choices by the implementation. Parameters: key -an application ID.null if 
the key is not an application ID,or not mapped to any application available. Returns: the value to which the key is 
mapped in this dictionary; Throws: SecurityException -if the calling application does not have the right to control the 
application associated with the given ID as de  ned by the security policy of the 
platform */
public AppProxy getAppProxy(AppID key) {
   return MHPApplication.GetApplication(key);
}

/*
Returns the singleton system-wide AppsDatabase object. Returns: the singleton AppsDatabase 
object. */
public static AppsDatabase getAppsDatabase() {
   return self;
}

/*
remove a listener on the database. Parameters: listener -the listener to be 
removed. */
public void removeListener(AppsDatabaseEventListener listener) {
   dbEvListener=DatabaseMulticaster.remove(dbEvListener, listener);
}

/*
Returns the number of applications currently available. Returns: the number of applications currently 
available. */
public int size() {
   return getSize(nativeData);
}

native int getSize(long nativeData);
//private native LockDatabase();
//private native UnlockDatabase();


}
