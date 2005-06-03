package org.dvb.user;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Hashtable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//Definitions taken from OpenMHP
//Implementation is not taken from OpenMHP, but written from scratch.

/**
* @author tejopa
* @date 7.3.2004
* @module internal
* @HOME
*/

/*
The UserPreferenceManager class gives access to the user preference settings.
This class provides a set of methods that allow an application to read or save
user settings. It also provides a mechanism to notify applications when a
preference has been modi ed. The value of a user setting,retrieved with the read
method, is a copy of the value that is stored in the receiver. The write method,
if authorized, overwrites the stored value.
*/

public class UserPreferenceManager {

static UserPreferenceManager manager;

LinkedList listeners = new LinkedList();
LinkedList settings;
String file;

/*final static String UserLanguage = "User Language";
final static String ParentalRating = "Parental Rating";
final static String UserName = "User Name";
final static String UserAddress = "UserAddress";
final static String UserEmail = "User @";
final static String CountryCode = "Country Code";
final static String DefaultFontSize = "Default Font Size";*/

private UserPreferenceManager() {
   file=new String(getSettingsFilePath());
   initSettings();
}

private native byte[] getSettingsFilePath();
private native byte[] getVDRLanguage();

//For simplicity, we use Java serialization instead of a good old
//plain-ASCII settings file. Change this if you feel like it and have time.
private void initSettings() {
   //load stored data
   try {
      FileInputStream str = new FileInputStream(file);
      ObjectInputStream p = new ObjectInputStream(str);
      settings = (LinkedList)p.readObject();
      p.close();
      str.close();
   } catch (java.io.FileNotFoundException e) {
      //no problem if file did not exist
   } catch (java.io.IOException e) {
      e.printStackTrace();
   } catch (ClassNotFoundException e) {
      e.printStackTrace();
   }
   
   //set up defaults if file did not exist
   if (settings==null) {
      settings = new LinkedList();
      insertDefaults();
   }
}

private void insertDefaults() {
   GeneralPreference pref = new GeneralPreference(GeneralPreference.UserLanguage);
   pref.add(new String(getVDRLanguage()));
   settings.add(pref);
   pref = new GeneralPreference(GeneralPreference.DefaultFontSize);
   pref.add(Integer.toString(26));
   settings.add(pref);
}

private void storeSettings() {
   if (settings==null || settings.size()==0)
      return;
   synchronized (settings) {
      try {
         FileOutputStream str = new FileOutputStream(file);
         ObjectOutputStream p = new ObjectOutputStream(str);
         p.writeObject(settings);
         p.close();
         str.close();
      } catch (java.io.IOException e) {
         e.printStackTrace();
      }
   }
}

public static void store() {
   if (manager!=null) {
      manager.storeSettings();
   }
}

/*
Return an instance of the UserPreferenceManager for this application. Repeated
calls to this method by the same application shall return the same instance.
Returns: an instance of UserPreferenceManager
*/
public static UserPreferenceManager getInstance(){ 
   if (manager==null)
      manager = new UserPreferenceManager();
   return manager;
}

/*
Allows an application to read a speci ed user preference. Parameters: p - an
object representing the preference to read. Throws: SecurityException - if the
calling application is denied access to this preference
*/
public void read (Preference p) throws SecurityException {
   synchronized(settings) {
      Iterator it=settings.iterator();
      Preference set;
      while (it.hasNext()) {
         set=(Preference)it.next();
         if (p.name.equals(set.name)) {
            checkReadAccess(set.name);
            p.setDataCloned(set);
            return;
         }
      }
   }
}

/*
Allows an application to read a speci ed user preference taking into account the
facility de ned by the application. If the intersection between the two sets of
values is empty then the preference will have no value. If there is a mis-match
between the name of the preference used when constructing the facility and the
name of the preference used in this method then the preference will have no
value. Parameters: p - an object representing the preference to read. facility -
the preferred values the application for the preference Throws:
SecurityException - if the calling application is denied access to this
preference
*/
public void read(Preference p, Facility facility) throws SecurityException {
   read(p);
   p.match(facility);
}

/*
"Access to the following settings/preferences is always granted: "User Language",
"Parental Rating", "Default Font Size" and "Country Code"."
Otherwise, check for permission
*/
void checkReadAccess(String preferenceName) {
   if (!preferenceName.equals("User Language") &&
       !preferenceName.equals("Parental Rating") &&
       !preferenceName.equals("Default Font Size") &&
       !preferenceName.equals("Country Code") ) {
      SecurityManager s = System.getSecurityManager();
      if (s != null) {
         s.checkPermission(new UserPreferencePermission("read"));
      }
   }
}

/*
Saves the speci ed user preference. If this method succeeds then it will change
the value of this preference for all future MHP applications. Parameters: p -
the preference to save. Throws: UnsupportedPreferenceException - if the
preference provided is not a standardized preference as de ned for use with
GeneralPreference. java.lang.SecurityException - if the application does not
have permission to call this method IOException - if saving the preference fails
for other reasons
*/
public void write (Preference p) throws UnsupportedPreferenceException, IOException, SecurityException {
   SecurityManager s = System.getSecurityManager();
   if (s != null) {
      s.checkPermission(new UserPreferencePermission("write"));
   }
   
   synchronized(settings) {
      Iterator it=settings.iterator();
      Preference old;
      while (it.hasNext()) {
         old=(Preference)it.next();
         if (p.name.equals(old.name)) {
            if (p.values.equals(old.values))
               return;
            it.remove();
            break;
         }
      }
      settings.add(p.clone());
   }
   if (listeners.size() > 0)
      notifyListeners(new UserPreferenceChangeEvent(p.name));
}

/*
Adds a listener for changes in user preferences. Parameters: l - the listener to
add.
*/
public void addUserPreferenceChangeListener (UserPreferenceChangeListener l) {
   synchronized(listeners) {
      listeners.add(l);
   }
}

/*
Removes a listener for changes in user preferences. Parameters: l - the listener
to remove.
*/
public void removeUserPreferenceChangeListener(UserPreferenceChangeListener l) {
   synchronized(listeners) {
      listeners.remove(l);
   }
}

void notifyListeners(UserPreferenceChangeEvent event) {
   synchronized(listeners) {
      Iterator it=listeners.iterator();
      while (it.hasNext()) {
         ((UserPreferenceChangeListener)it.next()).receiveUserPreferenceChangeEvent(event);
      }
   }
}

}

