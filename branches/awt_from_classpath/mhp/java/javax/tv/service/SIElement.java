
package javax.tv.service;

/*

The base interface of elements provided by the SI database.
 <code>SIElement</code> objects represent immutable <em>copies</em>
 of the service information data contained in the SI database.  If
 the information represented by an <code>SIElement</code> <em>E</em>
 changes in the database, <em>E</em> will not be changed.  The value
 of the <code>SIElement</code>'s locator (obtained by the
 <code>getLocator()</code> method) will remain unchanged in this
 case; the locator may be used to retrieve a copy of the SI element
 with the new data.  Two <code>SIElement</code> objects retrieved
 from the SI database using the same input <code>Locator</code> at
 different times will report <code>Locator</code> objects that are
 equal according to <code>Locator.equal()</code>.  However, the
 <code>SIElement</code> objects themselves will not be
 <code>equal()</code> if the corresponding data changed in the SI
 database between the times of their respective retrievals.

*/
public interface SIElement extends javax.tv.service.SIRetrievable {

/*
 
 Reports the Locator of this SIElement . 
 
 
 
 Returns: Locator The locator referencing this
 SIElement 
 
 
 */

public javax.tv.locator.Locator  getLocator ();


/*
 
 Tests two SIElement objects for equality. Returns
 true if and only if:
 
  obj 's class is the
 same as the class of this SIElement , and 
  obj 's Locator is equal to
 the Locator of this object (as reported by
 SIElement.getLocator() , and 
  obj and this object encapsulate identical data.
  
 Overrides:  equals in class java.lang.Object 
 
 
 Parameters:  obj - The object against which to test for equality. Returns:  true if the two SIElement objects
 are equal; false otherwise. 
 
 
 */

public boolean equals (java.lang.Object obj);


/*
 
 Reports the hash code value of this SIElement . Two
 SIElement objects that are equal will have identical
 hash codes. 
 Overrides:  hashCode in class java.lang.Object 
 
 
 Returns: The hash code value of this SIElement . 
 
 
 */

public int hashCode ();


/*
 
 Reports the SI format in which this SIElement was
 delivered. 
 
 
 
 Returns: The SI format in which this SI element was delivered. 
 
 
*/

public ServiceInformationType  getServiceInformationType ();



}

