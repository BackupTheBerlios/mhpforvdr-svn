
package javax.tv.service.selection;

/*

A <code>ServiceContentHandler</code> represents a mechanism for
 presenting, processing or playing portions of a service.  A single
 <code>ServiceContentHandler</code> may handle one or more
 constituent parts of a service, as represented by one or more
 locators to those parts.  Each locator reported by a
 <code>ServiceContentHandler</code> refers either to an individual
 service component or to content within a service component (such as
 an Xlet).

*/
public interface ServiceContentHandler {

/*
 
 Reports the portions of the service on which this handler operates. 
 Returns: An array of locators representing the portions of the
 service on which this handler operates. See Also:   ServiceContext.select(Locator[] components)  
 
 
*/

public javax.tv.locator.Locator [] getServiceContentLocators ();



}

