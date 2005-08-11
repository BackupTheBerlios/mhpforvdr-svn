
package org.dvb.si;

/*This interface represents the Time and Date Table (TDT) and the (optional) Time Offset 
Table (TOT). When it represents a TDT table, the retrieveDescriptors and getDescriptorTags 
methods behave as documented in the case when there are no descriptors, because the TDT 
does not contain any descriptors. */

public interface SITime extends SIInformation {

/*
Get the UTC time as coded in the TDT or TOT table. Returns: The UTC as coded in the TDT or TOT 
table. */
public java.util.Date getUTCTime();



}
