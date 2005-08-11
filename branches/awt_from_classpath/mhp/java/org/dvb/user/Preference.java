package org.dvb.user;

import java.util.Hashtable;
//import org.openmhp.util.LinkedList;
import java.util.LinkedList;
import java.util.Iterator;

//Taken from OpenMHP, license is LGPL
/**
* @author tejopa
* @date 7.3.2004
* @status fully implemented
* @module internal
* @HOME
*/

/*This abstract class de nes the Preference object. A Preference maps a name to a list of favourite values.
 The first element in the list is the favourite value for this preference.*/

public abstract class Preference implements Cloneable, java.io.Serializable {

   String name;
   LinkedList values;

   protected Preference(){
      values = new LinkedList();
   }

   public Preference (String n, String value) {
      name = n;
      values = new LinkedList();
      values.add(value);
   }

   public Preference (String n) {
      name = n;
      values = new LinkedList();
   }

   public void add (String value) {
      values.add(value);
   }

   public void add( String v[]){
      for (int i=0;i<v.length;i++) {
         values.add(v[i]);
      }
   }

   public void add (int position, String value) {
      values.add(position,value);
   }

   public String[] getFavourites () {
      String[] result = new String[values.size()];
      Iterator it=values.iterator();
      for (int i=0;it.hasNext();i++) {
         result[i]=(String)it.next();
      }
      /*for (int i=0;i<values.size();i++) {
         result[i] = (String)values.get(i);
      }*/
      return result;
   }

   public String getMostFavourite () {
      return (String)values.get(0);
   }

   public String getName () {
      return name;
   }

   public int getPosition(String value) {
      /*int result = -1;
      for (int i=0;i<values.size();i++) {
         if (value.equals((String)values.get(i))) {
            result = i;
         }
      }
      return result;*/
      return values.indexOf(value);
   }

   public boolean hasValue() {
      /*boolean result = false;
      if (values.size()>0) {
         result = true;
      }
      return result;*/
      return values.size()>0;
   }

   public void remove(String value){
      values.remove(value);
      /*int result = -1;
      for (int i=0;i<values.size();i++) {
         if (value.equals((String)values.get(i))) {
            result = i;
         }
      }
      values.remove(values.get(result));*/
   }

   public void removeAll(){
      values.clear();
   }

   public void setMostFavourite (String value) {
      remove(value);
      values.addFirst(value);
   }

   public String toString() {
      String result = name;
      result+=" [";
      boolean first=true;
      for (Iterator it=values.iterator(); it.hasNext(); ) {
         if (first)
            first=false;
         else
            result+=", ";
         result+=(String)it.next();
      }
      /*for (int i=0;i<values.size();i++) {
         result+=(String)values.get(i)+" ";
      }*/
      result+="]";
      return result;
   }
   
   public boolean equals(Object o) {
      if (o == this)
         return true;
      if (! (o instanceof Preference))
         return false;
      Preference other=(Preference)o;
      return other.name.equals(name) && other.values.equals(values);
   }
   
   public Object clone() {
      Preference clone;
      try {
         clone = (Preference)super.clone();
         clone.values = (LinkedList)values.clone();
      } catch (CloneNotSupportedException e) {
         throw new Error();
      }
      return clone;
   }
   
   public void setDataCloned(Preference p) {
      name = p.name;
      values = (LinkedList)p.values.clone();
   }
   
   //Removes all values not contained in the facility.
   //Removes all values if name of facility does not match.
   void match(Facility f) {
      if (!f.name.equals(name)) {
         values.clear();
         return;
      }
      
      Iterator it=values.iterator();
      while (it.hasNext()) {
         if (!f.contains((String)it.next()))
            it.remove();
      }
   }
}






