package org.davic.net.dvb;

import org.davic.net.Locator;
import javax.tv.locator.InvalidLocatorException;


//This is the class where most of the work is being done.

/*
Forms of an URL:
dvb://<original_network_id>.[<transport_stream_id>][.<service_id>[.<compone
nt_tag>{&<component_ tag>}][;<event_id>]]{/<path_segments>} or 
dvb://'textual_service_identifier>'[.<component_tag>{&<component_tag>}][;<event_i
d>]]{/<path_ segments>}*/

// dvb://60.23.20040.3&4&5;45/path/to/files
// dvb://'some_description'.3&4&5;45/path/to/files

/*
 Non-standard internal extension:
   dvb://123             for a network with id 0x123
   dvb://bouquet123      for a bouquet with id 0x123
*/
public class DvbLocator extends org.davic.net.Locator {
/*DVB Locator that encapsulates a DVB URL into an object */

static final int HAS_ONID = 1;
static final int HAS_TID = 2;
static final int HAS_SID = 4;
static final int HAS_EID = 8;
static final int HAS_CTAGS = 16;
static final int HAS_PATH = 32;
static final int HAS_BOUQUETID = 64; //non-standard extension
static final int HAS_TEXTUAL_ID = HAS_ONID | HAS_TID | HAS_SID;

public static final int INVALID = 0;
public static final int NETWORK = HAS_ONID; //non-standard extension
public static final int BOUQUET = HAS_BOUQUETID; //non-standard extension
public static final int TRANSPORT_STREAM = HAS_ONID | HAS_TID;
public static final int SERVICE = TRANSPORT_STREAM | HAS_SID;
public static final int COMPONENTS = SERVICE | HAS_CTAGS;
public static final int EVENT = SERVICE | HAS_EID;
public static final int EVENT_AND_COMPONENTS = SERVICE | EVENT | COMPONENTS;
public static final int CAROUSEL_PATH = SERVICE | HAS_PATH;
public static final int CAROUSEL_PATH_AND_COMPONENTS = COMPONENTS | CAROUSEL_PATH;
public static final int CAROUSEL_PATH_AND_EVENT = EVENT | CAROUSEL_PATH;
public static final int CAROUSEL_PATH_AND_EVENT_AND_COMPONENTS = EVENT_AND_COMPONENTS | CAROUSEL_PATH;

protected int original_network_id=-1;
protected int bouquet_id=-1;
protected int transport_stream_id=-1;
protected int service_id=-1;
protected int event_id=-1;
protected int[] component_tags=null;
protected String path=null;

protected String textual_service_identifier=null;

protected int flags = INVALID;

//internal API extension:
//Returns true if locator describes a target given type. The descriptor may be more specific.
public boolean provides(int type) {
   return (flags & type) == type;
}

//internal API extension:
//Returns true if locator describes a target given type. The descriptor is not more specific.
public boolean is(int type) {
   return flags == type;
}

//internal API extension:
//Returns one of the predefined types, if the locator has the data.
public int getType() {
   if (flags == TRANSPORT_STREAM
       || flags == NETWORK //non-standard extension
       || flags == BOUQUET //non-standard extension
       || flags == SERVICE
       || flags == COMPONENTS
       || flags == EVENT
       || flags == EVENT_AND_COMPONENTS
       || flags == CAROUSEL_PATH
       || flags == CAROUSEL_PATH_AND_COMPONENTS
       || flags == CAROUSEL_PATH_AND_EVENT
       || flags == CAROUSEL_PATH_AND_EVENT_AND_COMPONENTS
      )
      return flags;
   else
      return INVALID;
}


public java.lang.String toExternalForm () {
   StringBuffer url=new StringBuffer();
   url.append("dvb://");
   
   switch (getType()) {
   case TRANSPORT_STREAM:
      url.append(Integer.toHexString(original_network_id));
      url.append(".");
      url.append(Integer.toHexString(transport_stream_id));
      break;
   case SERVICE:
   case COMPONENTS:
   case EVENT:
   case EVENT_AND_COMPONENTS:
   case CAROUSEL_PATH:
   case CAROUSEL_PATH_AND_COMPONENTS:
   case CAROUSEL_PATH_AND_EVENT:
   case CAROUSEL_PATH_AND_EVENT_AND_COMPONENTS:
      appendServiceEtc(url);
      break;
   case NETWORK:
      url.append(Integer.toHexString(original_network_id));//non-standard extension
      break;
   case BOUQUET:
      url.append("bouquet");
      url.append(Integer.toHexString(bouquet_id));//non-standard extension
      break;
   default:
      break;
   }
      
   return url.toString();
}

void appendServiceEtc(StringBuffer url) {
   appendServiceWithoutEvent(url);
   appendComponentTag(url);
   appendEventConstraint(url);
   appendPath(url);
}

void appendServiceWithoutEvent(StringBuffer url) {
   if (textual_service_identifier==null) {      
      url.append(Integer.toHexString(original_network_id));
      url.append(".");

      if (transport_stream_id != -1) {
         //I learned that some implementation leave out the TID,
         //but since MHP 1.0.3 it is supposed to be required.
         url.append(Integer.toHexString(transport_stream_id));
      }
      
      url.append(".");
      url.append(Integer.toHexString(service_id));
   } else {
      //second form
      url.append("'");
      url.append(textual_service_identifier);
      url.append("'");
   }

}

void appendPath(StringBuffer url) {
   if (path != null)
      url.append("/"+path);
}

void appendEventConstraint(StringBuffer url) {
   if (event_id != -1) {
      url.append(";");
      url.append(Integer.toHexString(event_id));
   }  
}

void appendComponentTag(StringBuffer buf) {
   if (component_tags != null && component_tags.length>0) {
      buf.append(".");
      buf.append(Integer.toHexString(component_tags[0]));
      for (int i=1;i<component_tags.length;i++) {
         buf.append("&");
         buf.append(Integer.toHexString(component_tags[i]));
      }
   }
}



public DvbLocator(String url) throws InvalidLocatorException {
   super(url);
   
   int index=0;
   int length=url.length();
   
   //This implementation tries to follow the formal dvb URL definition
   //of chapter 14.1 of the spec.
   if (url.startsWith("dvb:/")) {
      index+=5;
      //dvb_hier_part
      if (url.startsWith("/", index)) {
         //dvb_net_path
         index++;
         int nextSlash=url.indexOf("/", index);
         String entity;
         if (nextSlash==-1)
            entity=url.substring(index);
         else {
            entity=url.substring(index, nextSlash);
            path=url.substring(nextSlash+1);
         }
         //dvb_entity
        index=0; //index is now on entity
         
         char nextChar;
         try {
            nextChar=entity.charAt(index);
         } catch (StringIndexOutOfBoundsException _) {
            throw new InvalidLocatorException(this, "Unexpected end scanning for type of dvb_entity");
         }
         
         if (nextChar == '\'') {
            //dvb_service or dvb_service_component 
            //where dvb_service_without_event is a textual_service_identifier
            index++;
            int nextQuotingChar=entity.indexOf("\'", index);
            if (nextQuotingChar==-1) {
               throw new InvalidLocatorException(this, "Could not find end of textual_service_identifier");
            }
            textual_service_identifier=entity.substring(index, nextQuotingChar);
            index=nextQuotingChar+1;
            
            int lastSemicolon=entity.lastIndexOf(";");
            String components;
            if (lastSemicolon== -1 || lastSemicolon<index) {
               components=entity.substring(index);
            } else {
               try {
                  event_id=Integer.parseInt(entity.substring(lastSemicolon+1), 16);
               } catch (NumberFormatException _) {
                  throw new InvalidLocatorException(this, "Component tag found in dvb_event_constraint not parsable");
               }
               components=entity.substring(index, lastSemicolon);
            }
            
            parseComponents(components, 0);
                  
         } else {
            //dvb_transport_stream, or dvb_service or dvb_service_component with no textual_service_identifier
            int nextDot=entity.indexOf(".", index);
            if (nextDot==-1) {
               if (entity.length() == 0)
                  throw new InvalidLocatorException(this, "No original network ID found in dvb_entity");
               //non-standard extension
               if (entity.startsWith("bouquet")) { 
                  index+=7;
                  try {
                     bouquet_id=Integer.parseInt(entity.substring(index), 16);
                  } catch (NumberFormatException _) {
                     throw new InvalidLocatorException(this, "Bouquet ID found in dvb_entity not parsable");
                  }
               //non-standard extension
               } else {
                  try {
                     original_network_id=Integer.parseInt(entity.substring(index), 16);
                  } catch (NumberFormatException _) {
                     throw new InvalidLocatorException(this, "Original network ID found in dvb_entity not parsable");
                  }
               }
            } else {
               try {
                  original_network_id=Integer.parseInt(entity.substring(index,nextDot), 16);
               } catch (NumberFormatException _) {
                  throw new InvalidLocatorException(this, "Original network ID found in dvb_entity not parsable");
               }
               index=nextDot+1;
   
               nextDot=entity.indexOf(".", index);
               if (nextDot==-1) {
                  //dvb_transport_stream
                  try {
                     transport_stream_id=Integer.parseInt(entity.substring(index), 16);
                  } catch (NumberFormatException _) {
                     throw new InvalidLocatorException(this, "Transport stream ID found in dvb_entity not parsable");
                  }
               } else {
                  //dvb_service or dvb_service_component with no textual_service_identifier
                  
                  //parsing dvb_service_without_event with no textual_service_identifier
                  if (nextDot-index==1) {
                     transport_stream_id=currentTransportStreamId();
                  } else {
                     try {
                        transport_stream_id=Integer.parseInt(entity.substring(index, nextDot), 16);
                     } catch (NumberFormatException _) {
                        throw new InvalidLocatorException(this, "Transport stream ID found in dvb_service_without_event not parsable");
                     }
                  }
                  index=nextDot+1;
                  
                  int lastSemicolon=entity.lastIndexOf(";");
                  String service;
                  if (lastSemicolon== -1 || lastSemicolon<index) {
                     service=entity;
                  } else {
                     try {
                        event_id=Integer.parseInt(entity.substring(lastSemicolon+1), 16);
                     } catch (NumberFormatException _) {
                        throw new InvalidLocatorException(this, "Event ID found in dvb_event_constraint not parsable");
                     }
                     service=entity.substring(0, lastSemicolon);
                  }
                  //index=0; //index is now on service
                  
                  nextDot=service.indexOf(".", index);
                  if (nextDot==-1) {
                     //dvb_service
                     try {
                        service_id=Integer.parseInt(service.substring(index), 16);
                     } catch (NumberFormatException _) {
                        throw new InvalidLocatorException(this, "Service ID found in dvb_service_without_event not parsable");
                     }
                  } else {
                     //dvb_service_component
                     try {
                        service_id=Integer.parseInt(service.substring(index, nextDot), 16);
                     } catch (NumberFormatException _) {
                        throw new InvalidLocatorException(this, "Service ID found in dvb_service_without_event not parsable");
                     }
                     index=nextDot+1;
                     parseComponents(service, nextDot);
                  }
               }
            }
         }
      } else {
         //dvb_abs_path
         //This means that URL specifies current service.
         //So fill in current service data.
         original_network_id=currentOriginalNetworkId();
         transport_stream_id=currentTransportStreamId();
         service_id=currentServiceId();
         path=url.substring(index);
      }
   } else {
      throw new InvalidLocatorException(this, "Currently only \"dvb://\" locators supported");
   }
   determineKind();
}
/* Constructor for the DVB locator Parameters: url - URL string Throws: 
InvalidLocatorException when the parameters to construct the locator wouldn't 
specify a valid locator (e.g. a numeric identifier out of range) */

void parseComponents(String components, int index) throws InvalidLocatorException {
   char nextChar;
   if (components.length() == 0) {
      component_tags=null;
   } else {
      try {
         nextChar=components.charAt(index);
      } catch (StringIndexOutOfBoundsException _) {
         return; //must not happen, see above
      }
      if (nextChar != '.') {
         throw new InvalidLocatorException(this, "Invalid element after service specification");
      }
      index++; //skip "."
      int tempIndex=index;
      //unless the remaining length is 0, there is at least one tag
      int countOfTags=1;
      while ( (tempIndex=components.indexOf("&", tempIndex)) != -1) {
         countOfTags++;
         tempIndex++; //indexOf does not throw a IndexOutOfBoundsException
      }
      if (countOfTags==0) {
         
      } else {
         component_tags=new int[countOfTags];
         tempIndex=index;
         for (int i=0;i<countOfTags;i++) {
            tempIndex=components.indexOf("&", tempIndex);
            try {
               component_tags[i]=Integer.parseInt(components.substring(index, tempIndex==-1 ? components.length() : tempIndex), 16);
            } catch (NumberFormatException _) {
               throw new InvalidLocatorException(this, "Event ID found in component_tag_set not parsable");
            }
            index=(++tempIndex);
         }
      }
   }
}

void determineKind() {
   flags=0;
   if (original_network_id != -1)
      flags |= HAS_ONID;
   if (bouquet_id != -1)
      flags |= HAS_BOUQUETID;
   if (transport_stream_id != -1)
      flags |= HAS_TID;
   if (service_id != -1)
      flags |= HAS_SID;
   if (event_id != -1)
      flags |= HAS_EID;
   if (component_tags != null)
      flags |= HAS_CTAGS;
   if (path != null)
      flags |= HAS_PATH;
   if (textual_service_identifier != null)
      flags |= HAS_TEXTUAL_ID;
}

int currentOriginalNetworkId() {
   return javax.tv.service.VDRService.getCurrentService().getOriginalNetworkId();
}

int currentTransportStreamId() {
   return javax.tv.service.VDRService.getCurrentService().getTransportStreamId();
}

int currentServiceId() {
    return javax.tv.service.VDRService.getCurrentService().getServiceId();
}


//non-standard extension: Network or bouquet
public DvbLocator(int id, boolean isNid) throws InvalidLocatorException {
   if (id<0)
      throw new InvalidLocatorException(this, "Invalid original network/bouquet ID "+id);
   if (isNid) {
      original_network_id=id;
      flags=NETWORK;
   } else {
      bouquet_id=id;
      flags=BOUQUET;
   }
}

//transport stream
public DvbLocator(int onid, int tsid) throws InvalidLocatorException {
   if (onid<0)
      throw new InvalidLocatorException(this, "Invalid original network ID "+onid);
   if (tsid<0)
      throw new InvalidLocatorException(this, "Invalid transport stream ID "+tsid);
   original_network_id=onid;
   transport_stream_id=tsid;
   flags=TRANSPORT_STREAM;
}
/*Constructor 
for the DVB locator corresponding to the URL form "dvb://onid.tsid" Parameters: 
onid - original network identifier tsid - transport stream identifier Throws: 
InvalidLocatorException180 when the parameters to construct the locator wouldn't 
specify a valid locator (e.g. a numeric identifier out of range) */

//service
public DvbLocator(int onid, int tsid, int serviceid) throws InvalidLocatorException {
   if (onid<0)
      throw new InvalidLocatorException(this, "Invalid original network ID "+onid);
   if (tsid<0)
      throw new InvalidLocatorException(this, "Invalid transport stream ID "+tsid);
   if (serviceid<0)
      throw new InvalidLocatorException(this, "Invalid service ID "+serviceid);
   original_network_id=onid;
   transport_stream_id=tsid;
   service_id=serviceid;
   flags=SERVICE;
}

/*Constructor for the DVB locator corresponding to the URL form 
"dvb://onid.tsid.serviceid" Parameters: onid - original network identifier tsid 
- transport stream identifier (if -1, the locator does not include a 
transport_stream_id) serviceid - service identifier Throws: 
InvalidLocatorException when the parameters to construct the locator wouldn't 
specify a valid locator (e.g. a numeric identifier out of range) */

//event?
public DvbLocator(int onid, int tsid, int serviceid, int eventid) throws 
InvalidLocatorException{
   if (onid<0)
      throw new InvalidLocatorException(this, "Invalid original network ID "+onid);
   if (tsid<0)
      throw new InvalidLocatorException(this, "Invalid transport stream ID "+tsid);
   if (serviceid<0)
      throw new InvalidLocatorException(this, "Invalid service ID "+serviceid);
   if (eventid<0)
      throw new InvalidLocatorException(this, "Invalid event ID "+eventid);
   original_network_id=onid;
   transport_stream_id=tsid;
   service_id=serviceid;
   event_id=eventid;
   flags=EVENT;
}
/*Constructor for the DVB locator corresponding to the 
URL form "dvb://onid.tsid.serviceid;eventid" Parameters: onid - original network 
include a transport_stream_id) serviceid - service identifier eventid - 
eveidentifier Throws: InvalidLocatorException when the parameters to construct 
thlocator wouldn't specify a valid locator (e.g. a numeric identifier out of     
 range) */
 
//stream?
 public DvbLocator(int onid, int tsid, int serviceid, int eventid, int componenttag)
  throws InvalidLocatorException {
   if (onid<0)
      throw new InvalidLocatorException(this, "Invalid original network ID "+onid);
   if (tsid<0)
      throw new InvalidLocatorException(this, "Invalid transport stream ID "+tsid);
   if (serviceid<0)
      throw new InvalidLocatorException(this, "Invalid service ID "+serviceid);
   if (eventid<0)
      throw new InvalidLocatorException(this, "Invalid event ID "+eventid);
   if (componenttag<0)
      throw new InvalidLocatorException(this, "Invalid component tag "+componenttag);
   original_network_id=onid;
   transport_stream_id=tsid;
   service_id=serviceid;
   event_id=eventid;
   component_tags=new int[1];
   component_tags[0]=componenttag;
   flags=EVENT_AND_COMPONENTS;
}
  /*Constructor for the DVB 
locatoor "dvb://onid.tsid.serviceid.componenttag" Parameters: onid - original 
network  identifier tsid - transport stream identifier (if -1, the locator does 
not      include a transport_stream_id) serviceid - service identifier eventid - 
event   identifier (if -1, the locator does not include an event id) 
componenttag -component tag Throws: InvalidLocatorException when the parameters 
to constructthe locator wouldn't specify a valid locator (e.g. a numeric 
identifier out range)*/

//streams?
public DvbLocator(int onid, int tsid, int serviceid, int 
eventid, int  componenttag[]) throws InvalidLocatorException {
   if (onid<0)
      throw new InvalidLocatorException(this, "Invalid original network ID "+onid);
   if (tsid<0)
      throw new InvalidLocatorException(this, "Invalid transport stream ID "+tsid);
   if (serviceid<0)
      throw new InvalidLocatorException(this, "Invalid service ID "+serviceid);
   if (eventid<0)
      throw new InvalidLocatorException(this, "Invalid event ID "+eventid);
   original_network_id=onid;
   transport_stream_id=tsid;
   service_id=serviceid;
   event_id=eventid;
   component_tags=componenttag;
   flags=EVENT_AND_COMPONENTS;
}
/* Constructor 
for the DVB    locator corresponding to the URL form                             
            "dvb://onid.tsid.serviceid.componenttag{&componenttag};eventid" or   
       "dvb://onid.tsid.serviceid.componenttaoriginal network identifier tsid - 
transport stream identifier (if locator does not include a transport_stream_id) 
serviceid - service identifeventid - event identifier (if -1, the locator does 
not include an event idcomponenttags - an array of component tags Throws: 
InvalidLocatorException whenthe parameters to construct the locator wouldn't 
specify a valid locator (e.g */

public DvbLocator(int onid, int tsid, int serviceid, int eventid, int componenttags[],
 String filePath) throws InvalidLocatorException {
   if (onid<0)
      throw new InvalidLocatorException(this, "Invalid original network ID "+onid);
   if (tsid<0)
      throw new InvalidLocatorException(this, "Invalid transport stream ID "+tsid);
   if (serviceid<0)
      throw new InvalidLocatorException(this, "Invalid service ID "+serviceid);
   if (eventid<0)
      throw new InvalidLocatorException(this, "Invalid event ID "+eventid);
   original_network_id=onid;
   transport_stream_id=tsid;
   service_id=serviceid;
   event_id=eventid;
   component_tags=componenttags;
   path=filePath;
   flags=CAROUSEL_PATH_AND_EVENT_AND_COMPONENTS;
}
/*Constructor for the DVB locator corresponding to the URL form 
"dvb://onid.tsid.serviceid.componenttag{&componenttag};eventid/filepath"
 or "dvb://onid.tsid.serviceid.componenttag{&componenttag}/filepath"
 Parameters:
  onid - original network identifier 
  tsid - transport stream identifier (if -1, the locator does not include a transport_stream_id) 
  serviceid - service identifier 
  eventid - event identifier (if -1, the locator does not include an event id) 
  componenttags - array of component tags (if null, the locator does not include any component tags) 
  the - file path string including the slash character in the beginning 
  
  Throws: InvalidLocatorException when the parameters to construct the 
  locator wouldn't specify a valid locator (e.g. a numeric identifier out of range) */
  
  
public int getOriginalNetworkId() {
   return original_network_id;
}
/*Returns the  
original_network_id Returns:original_network_id */ 

//for type NETWORK only, non-standard extension
public int getNetworkId() {
   return getOriginalNetworkId();
}

//for type BOUQUET, non-standard extension
public int getBouquetId() {
   return bouquet_id;
}


public int getTransportStreamId() {
   return transport_stream_id;
}
/*Returns the transport_stream_id Returns:              
transport_stream_id, -1 if not present */


public int getServiceId() {
   return service_id;
}
/*Rthe 
service_id Returns: service_id, -1 if not present */


public int[] getComponentTags() {
   return (component_tags==null ? new int[0] : component_tags);
}
/*Returns an array of the component_tags Returns: an 
arrcontaining the component_tags, the length of the array will belocator does 
not identify component_tags */


public int getEventId() {
   return event_id;
}
/*the event_id Returns: 
event_id, -1 if not present */


public String getFilePath(){
   return path;
}
/*Returns the file 
name path part of the locator Returns: the path including the slash character in 
the beginning. If the locator does not include a path string, this method will 
return null.*/


// -- DEBUGGING --- //

public static void main ( String[] args ) {
      try {
         DvbLocator loc1=new DvbLocator("dvb://'some_description'.3&4&5;45/path/to/files");
         DvbLocator loc2=new DvbLocator("dvb://'some_description';45/path/to/files");
         DvbLocator loc3=new DvbLocator("dvb://'some_description'.3&4&5/path/to/files");
         DvbLocator loc4=new DvbLocator("dvb://123.456.789");
         DvbLocator loc5=new DvbLocator("dvb://123.456.789;42");
         DvbLocator loc6=new DvbLocator("dvb://123.456.789/images/logo.gif");
         DvbLocator loc7=new DvbLocator("dvb://123.456.789.66");
         DvbLocator loc8=new DvbLocator(400, 500);
         int[] t={50, 60};
         DvbLocator loc9=new DvbLocator(400, 500, 600, 700, t, "/hallo/h.class");
         DvbLocator loc10=new DvbLocator("dvb://123");
         DvbLocator loc11=new DvbLocator("dvb://bouquet123");
         //DvbLocator loc12=new DvbLocator("dvb://123..789");
         //DvbLocator loc13=new DvbLocator("dvb:/path/of/channel");
         
         System.out.println(loc1.toExternalForm());
         System.out.println(loc2.toExternalForm());
         System.out.println(loc3.toExternalForm());
         System.out.println(loc4.toExternalForm());
         System.out.println(loc5.toExternalForm());
         System.out.println(loc6.toExternalForm());
         System.out.println(loc7.toExternalForm());
         System.out.println(loc8.toExternalForm());
         System.out.println(loc9.toExternalForm());
         System.out.println(loc10.toExternalForm());
         System.out.println(loc11.toExternalForm());
         //System.out.println(loc12.toExternalForm());
         //System.out.println(loc13.toExternalForm());
      } catch (javax.tv.locator.InvalidLocatorException ex) {
         ex.printStackTrace();
      }
      
      try {
         DvbLocator loc14=new DvbLocator("dvb://dfgl.wepolfw;&,./67.,:'//"); //an obviously invalid URL
      } catch (javax.tv.locator.InvalidLocatorException ex) {
         try {
            DvbLocator loc15=new DvbLocator("dvb://123.456.789.66.55;7&4;8///ui'ho'.class"); //a more subtly invalid URL
         } catch (javax.tv.locator.InvalidLocatorException ex_) {
            try {
               DvbLocator loc16=new DvbLocator("(Ö°Êç^¥#¤Ú¡cÐ¤LËÍ<;ZÄkÜïË¬MNQJ{"); //a most obviously invalid URL
            } catch (javax.tv.locator.InvalidLocatorException ex__) {
               System.out.println("All invalid URLs threw an exception.");
            }
         }
      }
}



}
