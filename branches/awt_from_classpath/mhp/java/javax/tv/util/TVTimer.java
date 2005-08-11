
package javax.tv.util;

/*

A class representing a timer.

 A timer is responsible for managing a set of timer events specified
 by timer specifications.  When the timer event should be sent,
 the timer calls the timer specification's
 <code>notifyListeners()</code> method.

*/
public abstract class TVTimer extends java.lang.Object {

/*
 
 Constructs a TVTimer object. */

//static TVTimer defaultTimer = new DefaultTimer();
 
public TVTimer (){
}


/*
 
 Returns the default timer for the system. There may be one
 TVTimer instance per virtual machine, one per applet, one per
 call to getTimer() , or some other platform dependent
 implementation. 
 Returns: A non-null TVTimer object. 
 
 
 */

public static TVTimer  getTimer (){
   return new DefaultTimer();
}


/*
 
 Begins monitoring a TVTimerSpec.
 
  When the timer specification should go off, the timer will
 call TVTimerSpec.notifyListeners().  
 
  Returns the actual TVTimerSpec that got
 scheduled. If you schedule a specification that implies a
 smaller granularity than this timer can provide, or a repeat
 timer specification that has a smaller repeating interval than
 this timer can provide, the timer should round to the closest
 value and return that value as a  TVTimerSpec  object. An
 interested application can use accessor methods  getMinRepeatInterval()  and  getGranularity()  to obtain
 the Timer's best knowledge of the Timer's limitation on
 granularity and repeat interval. If you schedule an absolute
 specification that should have gone off already, it will go off
 immediately. If the scheduled specification cannot be
 satisfied, the exception  TVTimerScheduleFailedException 
 should be thrown. 
 
 You may schedule a timer specification with multiple timers.
 You may schedule a timer specification with the same timer
 multiple times (in which case it will go off multiple times). If
 you modify a timer specification after it has been scheduled
 with any timer, the results are unspecified.  
 Parameters:  t - The timer specification to begin monitoring. Returns: The real TVTimerSpec that was scheduled. Throws:  TVTimerScheduleFailedException  - is thrown when the scheduled 
      specification cannot be satisfied. 
 
 
 */

public abstract TVTimerSpec  scheduleTimerSpec ( TVTimerSpec t)
                    throws TVTimerScheduleFailedException ;

/*
 
 Removes a timer specification from the set of monitored
 specifications. The descheduling happens as soon as practical,
 but may not happen immediately. If the timer specification has
 been scheduled multiple times with this timer, all the
 schedulings are canceled. 
 Parameters:  t - The timer specification to end monitoring. 
 
 
 */

public abstract void deschedule ( TVTimerSpec t);


/*
 
 Report the minimum interval that this timer can repeat tasks.
 For example, it's perfectly reasonable for a Timer to specify
 that the minimum interval for a repeatedly performed task is
 1000 milliseconds between every run. This is to avoid
 possible system overloading. 
 Returns: The timer's best knowledge of minimum repeat interval
 in milliseconds. Return -1 if this timer doesn't know its repeating
 interval limitation. 
 
 
 */

public abstract long getMinRepeatInterval ();


/*
 
 Report the granularity of this timer, i.e., the length of time between
 "ticks" of this timer. 
 Returns: The timer's best knowledge of the granularity in
 milliseconds. Return -1 if this timer doesn't know its granularity. 
 
 
*/

public abstract long getGranularity ();



}

