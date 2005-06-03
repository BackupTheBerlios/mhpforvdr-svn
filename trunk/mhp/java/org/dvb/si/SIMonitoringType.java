
package org.dvb.si;

/*This interface de nes the constants corresponding to the SI information type values in 
SIMonitoringEvent. */

public interface SIMonitoringType {

/*
Constant for the type of SIInformation object: Bouquet */
public static final byte BOUQUET = 2;


/*
Constant for the type of SIInformation object: Network */
public static final byte NETWORK = 1;


/*
Constant for the type of SIInformation object: PMTService */
public static final byte PMT_SERVICE = 4;


/*
Constant for the type of SIInformation object: Present or following event */
public static final byte PRESENT_FOLLOWING_EVENT = 5;


/*
Constant for the type of SIInformation object: Scheduled event */
public static final byte SCHEDULED_EVENT = 6;


/*
Constant for the type of SIInformation object: Service */
public static final byte SERVICE = 3;



}
