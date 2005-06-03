
package javax.tv.service;

/*

<code>SIChangeEvent</code> objects are sent to
 <code>SIChangeListener</code> instances to signal detected changes
 in the SI database.<p>

 Note that while the SI database may detect changes, notification of
 which specific <code>SIElement</code> has changed is not guaranteed.
 The entity reported by the method <code>getSIElement()</code> will
 be either:
 <ul>
 <li>The specific SI element that changed, or<p>
 <li>An SI element that contains, however indirectly, the specific SI
 element that changed, or<p>
 <li><code>null</code>, if the specific changed element is unknown.
 </ul>
 
 The level of specificity provided by the change mechanism is
 entirely dependent on the capabilities and current resources of the
 implementation.

 <code>SIChangeEvent</code> instances also report the kind of change
 that occurred to the SI element, via the method
 <code>getChangeType()</code>:
 <ul>

 <li>An <code>SIChangeType</code> of <code>ADD</code> indicates that
 the reported SI element is new in the database.<p>

 <li>An <code>SIChangeType</code> of <code>REMOVE</code> indicates
 that the reported SI element is defunct and no longer cached by the
 database.  The results of subsequent method invocations on the
 removed SIElement are undefined.<p>

 <li>An <code>SIChangeType</code> of <code>MODIFY</code> indicates
 that the data encapsulated by the reported SI element has changed.
 
 </ul>

 In the event that the SIElement reported by this event is not
 the actual element that changed in the broadcast (i.e. it is
 instead a containing element or <code>null</code>), the
 <code>SIChangeType</code> will be <code>MODIFY</code>.
 Individual SI element changes are reported only once, i.e.,
 a change to an SI element is not also reported as a change
 to any containing (or "parent") SI elements.

*/
public abstract class SIChangeEvent extends java.util.EventObject {

/*
 
 Constructs an SIChangeEvent object. 
 Parameters:  source - The entity in which the change occurred. type - The type of change that occurred. e - The SIElement that changed, or
 null if this is unknown. 
 
 */

SIChangeType type;
SIElement element;

public SIChangeEvent (java.lang.Object source,
           SIChangeType type,
           SIElement e){
   super(source);
   this.type=type;
   this.element=e;
}


/*
 
 Reports the SIElement that changed. 
 
 This method may return null , since it is not
 guaranteed that the SI database can or will determine which
 element in a particular table changed. 
 Returns: The SIElement that changed, or
 null if this is unknown. 
 
 
 */

public SIElement  getSIElement (){
   return element;
}


/*
 
 Indicates the type of change that occurred. 
 Returns: The type of change that occurred. 
 
 
*/

public SIChangeType  getChangeType (){
   return type;
}



}

