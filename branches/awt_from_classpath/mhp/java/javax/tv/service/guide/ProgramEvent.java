
package javax.tv.service.guide;

/*

<code>ProgramEvent</code> represents collection of elementary
 streams with a common time base, an associated start time, and an
 associated end time. An event is equivalent to the common industry
 usage of "TV program." <p>


 The Event Information Table (EIT) contains information (titles, start
 times, etc.) for events on defined services. An event is, in most cases,
 a typical TV program, however its definition may be extended to include
 particular data broadcasting sessions and other information segments.<p>
 
 A <code>ProgramEvent</code> object may optionally implement the
 <code>CAIdentification</code> interface. Note that all time values
 are in UTC time. 
*/
public interface ProgramEvent extends javax.tv.service.SIElement {

/*
 
 Returns the start time of this program event. The start time is in UTC
 time. 
 
 
 
 Returns: This program's start time (UTC). 
 
 
 */

public java.util.Date getStartTime ();


/*
 
 Returns the end time of this program event. The end time is in UTC time. 
 
 
 
 Returns: This program's end time (UTC). 
 
 
 */

public java.util.Date getEndTime ();


/*
 
 Returns the duration of this program event in seconds. 
 
 
 
 Returns: This program's duration in seconds. 
 
 
 */

public long getDuration ();


/*
 
 Returns the program event title. This information may be obtained in
 the ATSC EIT table or the DVB Short Event Descriptor. 
 
 
 
 Returns: A string representing this program's title, or an empty
 string if the title is unavailable. 
 
 
 */

public java.lang.String getName ();


/*
 
 Retrieves a textual description of the event. This method
 delivers its results asynchronously. 
 
 
 
 Parameters:  requestor - The SIRequestor to be notified
 when this retrieval operation completes. Returns: An SIRequest object identifying this
 asynchronous retrieval request. See Also:   ProgramEventDescription  
 
 
 */

public javax.tv.service.SIRequest  retrieveDescription ( javax.tv.service.SIRequestor requestor);


/*
 
 Reports content advisory information associated with this program for
 the local rating region. 
 
 
 
 Returns: A ContentRatingAdvisory object describing the
 rating of this ProgramEvent or null if
 no rating information is available. 
 
 
 */

public ContentRatingAdvisory  getRating ();


/*
 
 Reports the Service this program event is associated with. 
 
 
 
 Returns: The Service this program event is delivered on. 
 
 
 */

public javax.tv.service.Service  getService ();


/*
 
 Retrieves an array of service components which are part of this
 ProgramEvent . Service component information may not
 always be available. If the ProgramEvent is current,
 this method will provide only service components associated with
 the Service to which the ProgramEvent 
 belongs. If the ProgramEvent is not current, no
 guarantee is provided that all or even any of its service
 components will be available. */

public javax.tv.service.SIRequest  retrieveComponents ( javax.tv.service.SIRequestor requestor);



}

