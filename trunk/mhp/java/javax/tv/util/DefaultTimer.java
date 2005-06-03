package javax.tv.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//MHP uses Java 1.2.2 at maximum. java.util.Timer exists only since
//Java 1.3, so it is not officially available for MHP.
//Implementing this substituting class from javax.tv.util, we simply
//use java.util.Timer because we know it is implemented by Kaffe/GNU Classpath

public class DefaultTimer extends TVTimer {

Timer timer=new Timer();

//These are supposed to be best-knowledge values.
//Since we do not quite know it, lets just put in the minimum requirements.
long granularity=10; //MHP requires <= 10;
long minInterval=40; //MHP requires <= 40

DefaultTimer() {
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
java.util.LinkedList list = new java.util.LinkedList();
 
class TVTimerSpecAdapter extends TimerTask {
   TVTimerSpec t;
   
   TVTimerSpecAdapter(TVTimerSpec t) {
      this.t=t;
      synchronized (DefaultTimer.this) {
         DefaultTimer.this.list.add(this);
      }
   }
   
   public void run() {
      t.notifyListeners(DefaultTimer.this);
   }
}

public TVTimerSpec  scheduleTimerSpec ( TVTimerSpec t)
                    throws TVTimerScheduleFailedException {
   try {
      if (t.isAbsolute()) {
         //one-time, absolute date
         timer.schedule(new TVTimerSpecAdapter(t), new Date(t.getTime()));
      } else {
         if (t.isRepeat()) {
            if (t.isRegular()) {
               //repeating, fixed-rate (delay regardless of time since return from execute)
               //100, 200, 300, ...
               timer.scheduleAtFixedRate(new TVTimerSpecAdapter(t), t.getTime(), t.getTime());
            } else {
               //repeating, fixed-delay (delay relative to return from last execute)
               //100, 205, 310, ...
               timer.schedule(new TVTimerSpecAdapter(t), t.getTime(), t.getTime());
            }
         } else {
            //one-time, relative date
            timer.schedule(new TVTimerSpecAdapter(t), t.getTime());
         }
      }
   } catch (IllegalArgumentException e) {
      throw new TVTimerScheduleFailedException();
   } catch (IllegalStateException e) {
      throw new TVTimerScheduleFailedException();
   }
   return t;
}

/*
 
 Removes a timer specification from the set of monitored
 specifications. The descheduling happens as soon as practical,
 but may not happen immediately. If the timer specification has
 been scheduled multiple times with this timer, all the
 schedulings are canceled. 
 Parameters:  t - The timer specification to end monitoring. 
 
 
 */

public void deschedule ( TVTimerSpec t) {
   synchronized (this) {
      java.util.Iterator it=list.iterator();
      TVTimerSpecAdapter task;
      while (it.hasNext()) {
         task=(TVTimerSpecAdapter)it.next();
         if (task.t==t) {
            task.cancel();
            it.remove();
         }
      }
   }
}


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

public long getMinRepeatInterval () {
   return minInterval;
}


/*
 
 Report the granularity of this timer, i.e., the length of time between
 "ticks" of this timer. 
 Returns: The timer's best knowledge of the granularity in
 milliseconds. Return -1 if this timer doesn't know its granularity. 
 
 
*/

public long getGranularity () {
   return granularity;
}

}