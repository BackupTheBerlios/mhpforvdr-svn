package org.dvb.user ;

//Taken and adapted from OpenMHP, license is LGPL
/**
* @author tejopa
* @date 7.3.2004
* @status not implemented
* @module internal
* @HOME
*/

/*
A facility maps a preference's name to a single value or to an array of values.
A facility enables an application to de ne the list of values supported for a
speci ed preference. For example, if an application is available in English or
French then it can create a Facility ("User Language", {"English", "French"}).
When the application will retrieve the "User Language" from the general
preference it will specify the associated facility in order to get a Preference
which will contain a set a values compatible with those supported by the
application.
*/

public class Facility {

String values[];
String name;

public Facility (String preference, String value) {
   name=preference;
   values=new String[1];
   values[0]=value;
}

public Facility (String preference, String values[]) {
   this.name=preference;
   this.values=values;
}

boolean contains(String value) {
   for (int i=0;i<values.length;i++)
      if (values[i].equals(value))
         return true;
   return false;
}

}
