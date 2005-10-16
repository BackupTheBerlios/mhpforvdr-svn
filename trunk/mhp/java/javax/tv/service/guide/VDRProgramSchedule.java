
package javax.tv.service.guide;

import vdr.mhp.lang.NativeData;
import org.davic.net.dvb.DvbLocator;
import javax.tv.service.SIManager;
import javax.tv.service.VDRService;
import javax.tv.service.SIException;
import javax.tv.service.SIRequestFailureType;
import javax.tv.service.navigation.ServiceComponent;
import javax.tv.service.navigation.StreamType;
import javax.tv.locator.InvalidLocatorException;
import java.util.LinkedList;
import java.util.Date;


/*

This interface represents a collection of program events for a given
 service ordered by time. It provides the current, next and future
 program events.<p>

 Note that all time values are in UTC time.

*/
public class VDRProgramSchedule implements ProgramSchedule {

VDRService service;

static {
   initStaticState();
}

private native static void initStaticState();
// all time values in seconds
private native boolean currentEvent(NativeData nativeServiceData, EventListBuilder builder);
private native boolean futureEvent(NativeData nativeServiceData, long time, EventListBuilder builder);
private native boolean futureEvents(NativeData nativeServiceData, long begin, long end, EventListBuilder builder);
private native boolean nextEvent(NativeData nativeServiceData, EventListBuilder builder);
private native boolean event(NativeData nativeServiceData, int eventID, EventListBuilder builder);

private native boolean fillEventDescription(NativeData nativeServiceData, int eventID, VDRProgramEvent event);
private native boolean components(NativeData nativeServiceData, int eventID, ComponentListBuilder builder);

public VDRProgramSchedule(VDRService service) {
   this.service=service;
}

class EventListBuilder {
   LinkedList list = new LinkedList();
   
   // all time values in seconds
   void nextEvent(int eventID, long startTime, long endTime, long duration, long updateTime, String name) {
      list.add(new VDRProgramEvent(eventID, new Date(startTime * 1000), new Date(endTime * 1000), duration, new Date(updateTime *1000), name));
   }
   
   VDRProgramEvent[] getArray() {
      VDRProgramEvent[] array = new VDRProgramEvent[list.size()];
      return (VDRProgramEvent[])list.toArray(array);
   }
}

class ComponentListBuilder {
   LinkedList list = new LinkedList();
   
   // all time values in seconds
   void nextComponent(int componentTag, int stream_content, int content_type, String language, String name, long updateTime) {
      StreamType type;
      // Mapping according to Annex O of the MHP specification.
      // Constants can be looked up in ETSI EN 300 468, Table 26
      switch(stream_content) {
         case 0x01:
            type=StreamType.VIDEO;
            break;
         case 0x02:
            type=StreamType.AUDIO;
            break;
         case 0x03:
            switch (content_type) {
               case 0x01:
               case 0x10: case 0x11: case 0x12: case 0x13:
               case 0x20: case 0x21: case 0x22: case 0x23:
                  type=StreamType.SUBTITLES;
                  break;
               case 0x02:
                  type=StreamType.DATA;
                  break;
               default:
                  type=StreamType.UNKNOWN;
                  break;
            }
            break;
            // not according to table in annex:
         // case 0x04:
         //   type=StreamType.AUDIO; // AC-3
         //   break;
         default:
            type=StreamType.UNKNOWN;
      }
      list.add(new VDRProgramEventServiceComponent(componentTag, type, language, name, new Date(updateTime *1000)));
   }
   
   VDRProgramEventServiceComponent[] getArray() {
      VDRProgramEventServiceComponent[] array = new VDRProgramEventServiceComponent[list.size()];
      return (VDRProgramEventServiceComponent[])list.toArray(array);
   }
}

/*
 
 Retrieves the current ProgramEvent . The resulting
 ProgramEvent is available for immediate viewing. */

public javax.tv.service.SIRequest  retrieveCurrentProgramEvent ( javax.tv.service.SIRequestor requestor) {
   EventListBuilder builder = new EventListBuilder();
   if (currentEvent(service.getNativeData(), builder))
      return SIManager.deliverRequest(requestor, builder.getArray());
   else
      return SIManager.deliverRequest(requestor, SIRequestFailureType.DATA_UNAVAILABLE);
}


/*
 
 Retrieves the program event for the specified time. The
 specified time will fall between the resulting program event's
 start time (inclusive) and end time (exclusive). */

public javax.tv.service.SIRequest  retrieveFutureProgramEvent (java.util.Date time,
      javax.tv.service.SIRequestor requestor)
      throws javax.tv.service.SIException 
{
   EventListBuilder builder = new EventListBuilder();
   if (futureEvent(service.getNativeData(), time.getTime() / 1000,  builder))
      return SIManager.deliverRequest(requestor, builder.getArray());
   else
      return SIManager.deliverRequest(requestor, SIRequestFailureType.DATA_UNAVAILABLE);
}


/*
 
 Retrieves all known program events on this service for the
 specified time interval. A program event pe is
 retrieved by this method if the time interval from
 pe.getStartTime() (inclusive) to
 pe.getEndTime() (exclusive) intersects the time
 interval from begin (inclusive) to end 
 (exclusive) specified by the input parameters. */

public javax.tv.service.SIRequest  retrieveFutureProgramEvents (java.util.Date begin,
      java.util.Date end,
      javax.tv.service.SIRequestor requestor)
      throws javax.tv.service.SIException
{
   if (end.before(begin) || end.before(new Date()))
      throw new SIException("Invalid value for end");
   EventListBuilder builder = new EventListBuilder();
   if (futureEvents(service.getNativeData(), begin.getTime() / 1000, end.getTime() / 1000, builder))
      return SIManager.deliverRequest(requestor, builder.getArray());
   else
      return SIManager.deliverRequest(requestor, SIRequestFailureType.DATA_UNAVAILABLE);
}

/*
 
 Retrieves a event which follows the specified event. */

public javax.tv.service.SIRequest  retrieveNextProgramEvent ( ProgramEvent event,
      javax.tv.service.SIRequestor requestor)
      throws javax.tv.service.SIException
{
   EventListBuilder builder = new EventListBuilder();
   if (nextEvent(service.getNativeData(), builder))
      return SIManager.deliverRequest(requestor, builder.getArray());
   else
      return SIManager.deliverRequest(requestor, SIRequestFailureType.DATA_UNAVAILABLE);
}


/*
 
 Retrieves a program event matching the locator. Note that
 the event must be part of this schedule. 
 
 This method returns data asynchronously. 
 Parameters:  locator - Locator referencing the ProgramEvent 
 of interest. requestor - The SIRequestor to be notified
 when this retrieval operation completes. Returns: An SIRequest object identifying this
 asynchronous retrieval request. Throws:  InvalidLocatorException  - If locator does not
 reference a valid ProgramEvent in this
 ProgramSchedule . java.lang.SecurityException - If the caller does not have
 javax.tv.service.ReadPermission(locator) . See Also:   ProgramEvent , 
 ReadPermission  
 
 
 */

public javax.tv.service.SIRequest  retrieveProgramEvent ( javax.tv.locator.Locator locator,
      javax.tv.service.SIRequestor requestor)
      throws javax.tv.locator.InvalidLocatorException ,
                   java.lang.SecurityException
{
   if (!(locator instanceof DvbLocator))
      throw new InvalidLocatorException(locator, "Unsupported locator class");
   DvbLocator loc = (DvbLocator)locator;
   if (!loc.provides(DvbLocator.EVENT))
      throw new InvalidLocatorException(locator, "Locator does not reference an event");
   if (!service.sameService(loc))
      throw new InvalidLocatorException(locator, "Event is not included in this schedule, wrong service");

   EventListBuilder builder = new EventListBuilder();
   if (event(service.getNativeData(), loc.getEventId(), builder))
      return SIManager.deliverRequest(requestor, builder.getArray());
   else
      return SIManager.deliverRequest(requestor, SIRequestFailureType.DATA_UNAVAILABLE);
}


/*
 
 Registers a ProgramScheduleListener to be notified of
 changes to program events on this ProgramSchedule .
 Subsequent changes will be indicated through instances of
 ProgramScheduleEvent , with this
 ProgramSchedule as the event source and an
 SIChangeType of ADD ,
 REMOVE , MODIFY , or
 CURRENT_PROGRAM_EVENT . Only changes to
 ProgramEvent instances p for which the
 caller has
 javax.tv.service.ReadPermission(p.getLocator()) will
 be reported. */

public void addListener ( ProgramScheduleListener listener) {
   //TODO
   System.out.println("VDRProgramSchedule.addListener: implement me");
}


/*
 
 Unregisters a ProgramScheduleListener . If the
 specified ProgramScheduleListener is not registered, no
 action is performed. 
 Parameters:  listener - A previously registered listener. 
 
 
 */

public void removeListener ( ProgramScheduleListener listener) {
   //TODO
   System.out.println("VDRProgramSchedule.removeListener: implement me");
}


/*
 
 Reports the transport-dependent locator referencing the service to
 which this ProgramSchedule belongs. Note that
 applications may use this method to establish the identity of
 a ProgramSchedule after it has changed. 
 Returns: The transport-dependent locator referencing the service to
 which this ProgramSchedule belongs. See Also:   ProgramScheduleEvent.getProgramSchedule()  
 
 
*/

public javax.tv.locator.Locator  getServiceLocator () {
   return service.getLocator();
}


class VDRProgramEvent implements ProgramEvent {
// Optionally implemented interfaces: CAIdentification is currently not implemented

   int eventID;
   Date startTime;
   Date endTime;
   long duration; // in seconds
   Date updateTime;
   String name;
   String description = null;
   
   VDRProgramEvent(int eventID, Date startTime, Date endTime, long duration, Date updateTime, String name) {
      this.eventID = eventID;
      this.startTime = startTime;
      this.endTime = endTime;
      this.duration = duration;
      this.updateTime = updateTime;
      this.name = name;
   }
   /*

   Returns the time when this object was last updated from data in
   the broadcast. 
   Returns: The date of the last update in UTC format, or null 
   if unknown. 
 
 
*/

   public java.util.Date getUpdateTime () {
      return updateTime;
   }

/*
 
   Reports the Locator of this SIElement . 
 
 
 
   Returns: Locator The locator referencing this
   SIElement 
 
 
 */

   public javax.tv.locator.Locator  getLocator () {
      try {
         return new org.davic.net.dvb.DvbLocator(service.getOriginalNetworkId(), service.getTransportStreamId(), service.getServiceId(), eventID);
      } catch (javax.tv.locator.InvalidLocatorException e) {
         e.printStackTrace();
         return null;
      }
   }

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

   public boolean equals (java.lang.Object obj) {
      if (!(obj instanceof VDRProgramEvent))
         return false;
      
      VDRProgramEvent other=(VDRProgramEvent)obj;   
      return eventID == other.eventID
            && startTime.equals(other.startTime)
            && endTime.equals(other.endTime)
            && duration == other.duration
            && getLocator().equals(other.getLocator());
   }


/*
   Reports the hash code value of this SIElement . Two
   SIElement objects that are equal will have identical
   hash codes. 
   Overrides:  hashCode in class java.lang.Object 
 
 
   Returns: The hash code value of this SIElement . 
 */

   public int hashCode () {
      return getLocator().hashCode();
   }


/*
   Reports the SI format in which this SIElement was
   delivered. 
   Returns: The SI format in which this SI element was delivered. 
*/

   public javax.tv.service.ServiceInformationType  getServiceInformationType () {
      return javax.tv.service.ServiceInformationType.DVB_SI;
   }


   /*
   Returns the program event title. This information may be obtained in the ATSC EIT table or the DVB Short Event Descriptor.
   Returns:
   A string representing this program's title, or an empty string if the title is unavailable.
   */
   public java.lang.String getName() {
      return name;
   }


/*
   Returns the start time of this program event. The start time is in UTC time.
   Returns:
   This program's start time (UTC).
   */
   public java.util.Date getStartTime() {
      return startTime;
   }
   
   /*
   Returns the duration of this program event in seconds.
   Returns:
   This program's duration in seconds.
   */
   public long getDuration() {
      return duration;
   }

   /*
 
   Returns the end time of this program event. The end time is in UTC time. 
 
 
 
   Returns: This program's end time (UTC). 
 
 
 */

   public java.util.Date getEndTime () {
      return endTime;
   }

/*
 
   Retrieves a textual description of the event. This method
   delivers its results asynchronously. 
 
 
 
   Parameters:  requestor - The SIRequestor to be notified
   when this retrieval operation completes. Returns: An SIRequest object identifying this
   asynchronous retrieval request. See Also:   ProgramEventDescription  
 
 
 */

   public javax.tv.service.SIRequest retrieveDescription ( javax.tv.service.SIRequestor requestor) {
      // Description may be a long text, retrieve it only on demand.
      boolean success = false;
      synchronized (this) {
         success = fillEventDescription(service.getNativeData(), eventID, this);
      }
      if (success) {
         // This object cannot implement ProgramEventDescription - spec does not allow that,
         // application will probably test with instanceof ProgramEvent and then ProgramEventDescription
         // (imagine what happens)
         return SIManager.deliverRequest(requestor, new VDRProgramEventDescription(this));
      } else
         return SIManager.deliverRequest(requestor, SIRequestFailureType.DATA_UNAVAILABLE);
   }


/*
 
   Reports content advisory information associated with this program for
   the local rating region. 
 
 
 
   Returns: A ContentRatingAdvisory object describing the
   rating of this ProgramEvent or null if
   no rating information is available. 
 
 
 */

   public javax.tv.service.guide.ContentRatingAdvisory  getRating () {
      return null;
   //TODO:
   //return new javax.tv.service.DVBParentalRating(getRating(nativeData));
   //getRating(nativeData) shall return the rating from the parental
   //rating descriptor, 0 otherwise
   }


/*
 
   Reports the Service this program event is associated with. 
 
 
 
   Returns: The Service this program event is delivered on. 
 
 
 */

   public javax.tv.service.Service  getService () {
      return service;
   }

/*
 
   Retrieves an array of service components which are part of this
   ProgramEvent . Service component information may not
   always be available. If the ProgramEvent is current,
   this method will provide only service components associated with
   the Service to which the ProgramEvent 
   belongs. If the ProgramEvent is not current, no
   guarantee is provided that all or even any of its service
   components will be available. */

   public javax.tv.service.SIRequest  retrieveComponents ( javax.tv.service.SIRequestor requestor) {
      ComponentListBuilder builder = new ComponentListBuilder();
      if (components(service.getNativeData(), eventID, builder))
         return SIManager.deliverRequest(requestor, builder.getArray());
      else
         return SIManager.deliverRequest(requestor, SIRequestFailureType.DATA_UNAVAILABLE);
   }


}

class VDRProgramEventDescription implements ProgramEventDescription {

   VDRProgramEvent event;

   VDRProgramEventDescription(VDRProgramEvent event) {
      this.event = event;
   }
   /*
   Provides a textual description of the ProgramEvent.
   Returns:
   A textual description of the ProgramEvent, or an empty string if no description is available.
   */
   public java.lang.String getProgramEventDescription() {
      return event.description;
   }

   public java.util.Date getUpdateTime () {
      return event.updateTime;
   }

}

class VDRProgramEventServiceComponent implements ServiceComponent {

   int componentTag;
   StreamType type;
   String language;
   String name;
   Date updateTime;
   
   VDRProgramEventServiceComponent(int componentTag, StreamType type, String language, String name, Date updateTime) {
      this.componentTag=componentTag;
      this.type=type;
      this.language=language;
      this.name=name;
      this.updateTime=updateTime;
   }
/*
 
   Returns a name associated with this component. The Component Descriptor
   (DVB) or Component Name Descriptor (ATSC) may be used if present. A
   generic name (e.g., "video", "first audio", etc.) may be used otherwise. 
 
 
 
   Returns: A string representing the component name or an empty string
   if no name can be associated with this component. 
 
 
 */

   public java.lang.String getName () {
      return name;
   }


/*
 
   Identifies the language used for the elementary stream. The
   associated language is indicated using a language code. This is
   typically a three-character language code as specified by ISO
   639.2/B, but the code may be system-dependent. 
 
 
 
   Returns: A string representing a language code defining the
   language associated with this component. An empty string is
   returned when there is no language associated with this component. 
 
 
 */

   public java.lang.String getAssociatedLanguage () {
      return language;
   }


/*
 
   Provides the stream type of this component. (For example, "video",
   "audio", etc.) 
 
 
 
   Returns: Stream type of this component. 
 
 
 */

   public javax.tv.service.navigation.StreamType  getStreamType () {
      return type;
   }


/*
 
   Provides the Service object to which this
   ServiceComponent belongs. The result may be
   null if the Service cannot be determined. 
 
 
 
   Returns: The Service to which this
   ServiceComponent belongs. 
 
 
*/

   public javax.tv.service.Service  getService () {
      return service;
   }

/*
 
   Returns the time when this object was last updated from data in
   the broadcast. 
   Returns: The date of the last update in UTC format, or null 
   if unknown. 
 
 
*/

   public java.util.Date getUpdateTime () {
      return updateTime;
   }
/*
 
   Reports the Locator of this SIElement . 
 
 
 
   Returns: Locator The locator referencing this
   SIElement 
 
 
 */

   public javax.tv.locator.Locator  getLocator () {
      try {
         // include eventID? Don't know.
         return new org.davic.net.dvb.DvbLocator(service.getOriginalNetworkId(), service.getTransportStreamId(), service.getServiceId(), -1, componentTag);
      } catch (javax.tv.locator.InvalidLocatorException e) {
         e.printStackTrace();
         return null;
      }
   }

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

   public boolean equals (java.lang.Object obj) {
      if (!(obj instanceof VDRProgramEventServiceComponent))
         return false;
      
      VDRProgramEventServiceComponent other=(VDRProgramEventServiceComponent)obj;
      return getLocator().equals(other.getLocator());
   }


/*
   Reports the hash code value of this SIElement . Two
   SIElement objects that are equal will have identical
   hash codes. 
   Overrides:  hashCode in class java.lang.Object 
 
 
   Returns: The hash code value of this SIElement . 
 */

   public int hashCode () {
      return getLocator().hashCode();
   }


/*
   Reports the SI format in which this SIElement was
   delivered. 
   Returns: The SI format in which this SI element was delivered. 
*/

   public javax.tv.service.ServiceInformationType  getServiceInformationType () {
      return javax.tv.service.ServiceInformationType.DVB_SI;
   }


}


}

