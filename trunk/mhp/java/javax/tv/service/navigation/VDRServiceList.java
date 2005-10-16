
package javax.tv.service.navigation;

import java.util.LinkedList;
import java.util.Iterator;
import javax.tv.service.Service;
import javax.tv.service.VDRService;
import org.davic.net.dvb.DvbLocator;
import vdr.mhp.lang.NativeData;

/*

<code>ServiceList</code> represents an ordered list of
 <code>Service</code> objects based on a specific grouping rule
 defined by a <code>ServiceFilter</code>.  The objects in a
 <code>ServiceList</code> are numbered from 0 to <code>size()
 -1</code>.

 A <code>ServiceList</code> is <i>immutable</i>.  In other words,
 once a <code>ServiceList</code> instance is created, the elements
 in the list and their order will never change.  All classes that
 implement the <code>ServiceList</code> interface are required to
 maintain this property.

*/
public class VDRServiceList implements ServiceList {

LinkedList list;

static {
   initStaticState();
}

private native static void initStaticState();
private native void listServices(ListBuilder builder);

class ListBuilder {
   ServiceFilter filter;
   ListBuilder(ServiceFilter filter) {
      this.filter=filter;
   }
   void nextService(NativeData nativeData) {
      Service service = VDRService.getService(nativeData);
      nextService(service);
   }
   void nextService(Service service) {
      if (filter==null || filter.accept(service))
         list.add(service);
   }
}

public VDRServiceList(ServiceFilter filter) {
   list = new LinkedList();
   listServices(new ListBuilder(filter));
}

VDRServiceList(ServiceFilter filter, LinkedList source) {
   list = new LinkedList();
   ListBuilder builder = new ListBuilder(filter);
   for (Iterator it = source.iterator(); it.hasNext(); ) {
      builder.nextService((Service)it.next());
   }
}

class NameComparator implements java.util.Comparator {
   public boolean equals(Object o) {
      return o instanceof NameComparator;
   }
   
   public int compare(Object o1, Object o2) {
      return ((VDRService)o1).getName().compareTo(((VDRService)o2).getName());
   }
}

class NumberComparator implements java.util.Comparator {
   public boolean equals(Object o) {
      return o instanceof NumberComparator;
   }
   
   public int compare(Object o1, Object o2) {
      return ((VDRService)o1).getServiceNumber()-((VDRService)o2).getServiceNumber();
   }
}

/*
 
 Generates a new ServiceList containing the
 same elements as the current list, sorted in ascending
 order by service name. 
 Returns: A ServiceList sorted by service name. See Also:   Service.getName()  
 
 
 */

public ServiceList  sortByName () {
   VDRServiceList vsl = new VDRServiceList(null, list);
   try {
      java.util.Collections.sort(vsl.list, new NameComparator());
   } catch (ClassCastException e) {
      e.printStackTrace();
   }
   return vsl;
}


/*
 
 Generates a new ServiceList containing the
 same elements as the current list, sorted in ascending
 order by service number. 
 Returns: A ServiceList sorted by service number. Throws:  SortNotAvailableException  - If any of the
 Service objects in this ServiceList 
 do not implement the ServiceNumber interface. See Also:   ServiceNumber  
 
 
 */

public ServiceList  sortByNumber ()
             throws SortNotAvailableException {
   VDRServiceList vsl = new VDRServiceList(null, list);
   try {
      java.util.Collections.sort(vsl.list, new NumberComparator());
   } catch (ClassCastException e) {
      e.printStackTrace();
   }
   return vsl;
}


/*
 
 Reports the Service corresponding to the specified
 locator if it is a member of this list. 
 Parameters:  locator - Specifies the Service to be searched for. Returns: The Service corresponding to
 locator , or null if the
 Service is not a member of this list. Throws:  InvalidLocatorException  - If locator does not
 reference a valid Service . 
 
 
 */

public javax.tv.service.Service  findService ( javax.tv.locator.Locator locator)
          throws javax.tv.locator.InvalidLocatorException
{
   javax.tv.locator.Locator[] loc = { locator };
   LocatorFilter filter=new LocatorFilter(loc);
   for (java.util.Iterator it=list.iterator(); it.hasNext(); ) {
      Service s=(Service)it.next();
      if (filter.accept(s))
         return s;
   }
   return null;
}


/*
 
 Creates a new ServiceList object that is a subset of
 this list, based on the conditions specified by a
 ServiceFilter object. This method may be used to
 generate increasingly specialized lists of Service 
 objects based on multiple filtering criteria. If the filter is
 null , the resulting ServiceList will be
 a duplicate of this list.  */

public ServiceList  filterServices ( ServiceFilter filter) {
   return new VDRServiceList(filter, list);
}


class VDRServiceListIterator implements ServiceIterator {

   java.util.ListIterator it;

   VDRServiceListIterator() {
      toBeginning();
   }

   public void toBeginning() {
      it = list.listIterator();
   }
   
   public void toEnd() {
      if (!list.isEmpty())
         it = list.listIterator(list.size()-1);
      else
         it = list.listIterator();
   }
   
   public javax.tv.service.Service nextService() {
      return (Service)it.next();
   }
   
   public javax.tv.service.Service previousService() {
      return (Service)it.previous();
   }
   
   public boolean hasNext() {
      return it.hasNext();
   }
   
   public boolean hasPrevious() {
      return it.hasPrevious();
   }
   
}

/*
 
 Generates an iterator on the Service elements
 in this list. 
 Returns: A ServiceIterator on the
 Service s in this list. 
 
 
 */

public ServiceIterator  createServiceIterator () {
   return new VDRServiceListIterator();
}


/*
 
 Tests if the indicated Service object is contained
 in the list. 
 Parameters:  service - The Service object for which to search. Returns:  true if the specified Service 
 is member of the list; false otherwise. 
 
 
 */

public boolean contains (javax.tv.service.Service service) {
   return list.contains(service);
}


/*
 
 Reports the position of the first occurrence of the
 indicated Service object in the list. 
 Parameters:  service - The Service object for which to search. Returns: The index of the first occurrence of the
 service , or -1 if service 
 is not contained in the list. 
 
 
 */

public int indexOf (javax.tv.service.Service service) {
   return list.indexOf(service);
}


/*
 
 Reports the number of Service objects in the list. 
 Returns: The number of Service objects in the list. 
 
 
 */

public int size () {
   return list.size();
}


/*
 
 Reports the Service at the specified index position. 
 Parameters:  index - A position in the ServiceList . Returns: The Service at the specified index. Throws:  java.lang.IndexOutOfBoundsException - If index < 0 or
 index > size()-1 . 
 
 
 */

public javax.tv.service.Service  getService (int index) throws IndexOutOfBoundsException {
   return (Service)list.get(index);
}


/*
 
 Compares the specified object with this ServiceList 
 for equality. Returns true if and only if the
 specified object is also a ServiceList , both lists
 have the same size, and all corresponding pairs of elements in
 the two lists are equal. (Two elements e1 and e2 are equal if
 (e1==null ? e2==null : e1.equals(e2)).) In other words, two lists
 are defined to be equal if they contain the same elements in the
 same order. 
 Overrides:  equals in class java.lang.Object 
 
 
 Parameters:  o - The object to be compared for equality with this list. Returns:  true if the specified object is equal to
 this list; false otherwise. 
 
 
 */

public boolean equals (java.lang.Object o) {
   if (!(o instanceof VDRServiceList))
      return false;
   return list.equals(((VDRServiceList)o).list);
}


/*
 
 Provides the hash code value for this ServiceList .
 Two ServiceList objects that are equal will have
 the same hash code. 
 Overrides:  hashCode in class java.lang.Object 
 
 
 Returns: The hash code value of this ServiceList . 
 
 
*/

public int hashCode () {
   return list.hashCode();
}



}

