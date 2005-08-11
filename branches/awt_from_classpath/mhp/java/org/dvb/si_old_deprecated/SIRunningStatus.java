
package org.dvb.si;

/*This interface de nes the constants corresponding to the running status values for 
services and events. */

public interface SIRunningStatus {

//the values are actually specified in TR 101 211

/*
Constant value for the running status as speci ed in EN 300 468 */
public static final byte NOT_RUNNING = 0x02;


/*
Constant value for the running status as speci ed in EN 300 468 */
public static final byte PAUSING = 0x03;


/*
Constant value for the running status as speci ed in EN 300 468 */
public static final byte RUNNING = 0x01;


/*
Constant value for the running status as speci ed in EN 300 468 */
public static final byte STARTS_IN_A_FEW_SECONDS = 0x04;


/*
Constant value for the running status as speci ed in EN 300 468 */
public static final byte UNDEFINED = 0x00;



}
