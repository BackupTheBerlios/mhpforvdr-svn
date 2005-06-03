
package javax.tv.util;

/*

A listener interested in timer specifications going off.

*/
public interface TVTimerWentOffListener {

/*
 
 Notifies the listener that a timer specification went off. 
 Parameters:  e - The event specifying which timer and which timer specification
 went off. 
 
 
*/

public void timerWentOff ( TVTimerWentOffEvent e);



}

