
package javax.tv.service.navigation;

/*

<code>ServiceIterator</code> permits iteration over an ordered
 list of <code>Service</code> objects.  Applications may use the
 <code>ServiceIterator</code> interface to browse a
 <code>ServiceList</code> forward or backward.<p>

 Upon initial usage, <code>hasPrevious()</code> will return
 <code>false</code> and <code>nextService()</code> will return the
 first <code>Service</code> in the list, if present.

*/
public interface ServiceIterator {

/*
 
 Resets the iterator to the beginning of the list, such that
 hasPrevious() returns false and
 nextService() returns the first Service 
 in the list (if the list is not empty). 
 */

public void toBeginning ();


/*
 
 Sets the iterator to the end of the list, such that
 hasNext() returns false and
 previousService() returns the last Service 
 in the list (if the list is not empty). 
 */

public void toEnd ();


/*
 
 Reports the next Service object in the list. This
 method may be called repeatedly to iterate through the list. 
 Returns: The Service object at the next position in
 the list. Throws:  java.util.NoSuchElementException - If the iteration has no next
 Service . 
 
 
 */

public javax.tv.service.Service  nextService ();


/*
 
 Reports the previous Service object in the list.
 This method may be called repeatedly to iterate through the list
 in reverse order. 
 Returns: The Service object at the previous position
 in the list. Throws:  java.util.NoSuchElementException - If the iteration has no previous
 Service . 
 
 
 */

public javax.tv.service.Service  previousService ();


/*
 
 Tests if there is a Service in the next position in
 the list. 
 Returns:  true if there is a Service in
 the next position in the list; false otherwise. 
 
 
 */

public boolean hasNext ();


/*
 
 Tests if there is a Service in the previous
 position in the list. 
 Returns:  true if there is a Service in
 the previous position in the list; false otherwise. 
 
 
*/

public boolean hasPrevious ();



}

