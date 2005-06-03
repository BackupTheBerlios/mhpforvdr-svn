
package org.dvb.si;

/*Objects of this class are sent to listener objects of the using application to notify that 
a change in the monitored information has happened. */

public class SIMonitoringEvent extends java.util.EventObject {

byte objectType;
int networkId;
int bouquetId;
int originalNetworkId;
int transportStreamId;
int serviceId;
java.util.Date startTime;
java.util.Date endTime;
/*
Constructor for the event object Parameters: source - the SIDatabase object which is the source of the event objectType 
- type of the SIInformation object (constants in SIMonitoringType) networkId - networkId bouquetId - bouquetId 
originalNetworkId - originalNetworkId transportStreamId - transportStreamId serviceId - serviceId startTime - start time 
of event schedule period endTime - end time of event schedule period */
public SIMonitoringEvent(SIDatabase source, byte objectType, int networkId, int bouquetId, int originalNetworkId, int 
transportStreamId, int serviceId, java.util.Date startTime, java.util.Date endTime) {
   super(source);
   this.objectType=objectType;
   this.networkId=networkId;
   this.bouquetId=bouquetId;
   this.originalNetworkId=originalNetworkId;
   this.transportStreamId=transportStreamId;
   this.serviceId=serviceId;
   this.startTime=startTime;
   this.endTime=endTime;
}

/*
Returns the bouquetId of the bouquet. This method is only applicable if the SIInformation type returned with the 
getSIInformationType method is BOUQUET. Returns: the bouquetId or -2 if not applicable for this 
event */
public int getBouquetID() {
   return bouquetId;
}

/*
Returns the end time of the schedule period whose event information has changed. This method is only applicable if the 
SIInformation type returned with the getSIInformationType method is SCHEDULED_EVENT. Returns: the end time or null if 
not applicable for this event */
public java.util.Date getEndTime() {
   return endTime;
}

/*
Returns the networkId of the network. This method is only applicable if the SIInformation type returned with the 
getSIInformationType method is NETWORK. Returns: the networkId or -2 if not applicable for this 
event */
public int getNetworkID() {
   return networkId;
}

/*
Returns the originalNetworkId of the SIInformation objects This method is only applicable if the SIInformation type 
returned with the getSIInformationType method is SERVICE, PMT_SERVICE, PRESENT_FOLLOWING_EVENT or SCHEDULED_EVENT. 
Returns: the originalNetworkId or -2 if not applicable for this event */
public int getOriginalNetworkID() {
   return originalNetworkId;
}

/*
Returns the serviceId of the SIInformation objects This method is only applicable if the SIInformation type returned 
with the getSIInformationType method is PMT_SERVICE, PRESENT_FOLLOWING_EVENT or SCHEDULED_EVENT. Returns: the serviceId 
or -2 if not applicable for this event */
public int getServiceID() {
   return serviceId;
}

/*
Get the SIInformation type of the information that has changed Returns: The SIInformation type (the possible values are 
de ned in the SIMonitoringType interface). */
public byte getSIInformationType() {
   return objectType;
}

/*
Gets the SIDatabase instance that is sending the event. Overrides: java.util.EventObject.getSource() in class 
java.util.EventObject Returns: the SIDatabase instance that is the source of this 
event. */
public java.lang.Object getSource() {
   return super.getSource();
}

/*
Returns the start time of the schedule period whose event information has changed. This method is only applicable if the 
SIInformation type returned with the getSIInformationType method is SCHEDULED_EVENT. Returns: the start time or null if 
not applicable for this event */
public java.util.Date getStartTime() {
   return startTime;
}

/*
Returns the transportStreamId of the SIInformation objects This method is only applicable if the SIInformation type 
returned with the getSIInformationType method is SERVICE, PMT_SERVICE, PRESENT_FOLLOWING_EVENT or SCHEDULED_EVENT. 
Returns: the transportStreamId or -2 if not applicable for this event */
public int getTransportStreamID() {
   return transportStreamId;
}


}
