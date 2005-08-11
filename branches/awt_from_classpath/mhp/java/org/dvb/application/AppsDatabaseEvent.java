
package org.dvb.application;

/*The AppsDatabaseEvent class indicates either the an entry in the application database has 
changed, or so many changes have occurred.that the database should be considered totally 
new This event shall always be sent after witching to a new service.It is platform 
dependant if and when a new database event is thrown while tuned to the same 
service. */

public class AppsDatabaseEvent extends java.util.EventObject {


private AppID appid;
int id;
/*
Create a new AppsDatabaseEvent object for the entry in the database that changed,or for a new database. Parameters: id 
-the cause of the event appid -the AppId of the entry that changed source -the AppaDatabase 
object. */
public AppsDatabaseEvent(int id, AppID appid, java.lang.Object source) {
   super(source);
   this.appid=appid;
   this.id=id;
}

/*
gets the application ID object for the entry in the database that changed. When the event type is NEW_DATABASE,AppID 
will be null. Returns: application ID representing the application */
public AppID getAppID() {
   return appid;
}

/*
gets the type of the event. Returns: an integer that matches one of the static  elds describing 
events. */
public int getEventId() {
   return id;
}

/*
The addition event id.The APP_ADDED event is generated whenever an entry is added to the AppsDatabase.It is NOT 
generated when the entry already in the AppsDatabase changes. */
public static final int APP_ADDED = 2;

/*
The changed event id.The APP_CHANGED event is generated whenever any of the information about an application changes.It 
is NOT generated when the entry is added to or removed from the AppsDatabase.In such cases,the APP_ADDED or APP_DELETED 
events will be generated instead. */
public static final int APP_CHANGED = 1;

/*
The deletion event id.The APP_DELETED event is generated whenever an entry is removed from the 
AppsDatabase. */
public static final int APP_DELETED = 3;

/*
The new database event id. */
public static final int NEW_DATABASE = 0;


}
