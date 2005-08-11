
package javax.tv.util;

/*

A class representing a timer specification.  A timer specification
 declares when a <code>TVTimerWentOffEvent</code> should be sent.
 These events are sent to the listeners registered on the
 specification.</p>

 <p>A <code>TVTimerSpec</code> may be <b>absolute</b> or <b>delayed</b>.
 Absolute specifications go off at the specified time.  Delayed
 specifications go off after waiting the specified amount of time.</p>

 <p>Delayed specifications may be repeating or non-repeating.
 Repeating specifications automatically reschedule themselves after
 going off.</p>

 <p>Repeating specifications may be regular or non-regular.  Regular
 specifications attempt to go off at fixed intervals of time,
 irrespective of system load or how long it takes to notify the
 listeners.  Non-regular specifications wait the specified amount of
 time after all listeners have been called before going off again.</p>

 <p>For example, you could create a repeating specification that
 goes off every 100 milliseconds.  Furthermore, imagine that it
 takes 5 milliseconds to notify the listeners every time it goes
 off.  If the specification is regular, the listeners will be
 notified after 100 milliseconds, 200 milliseconds, 300
 milliseconds, and so on.  If the specification is non-regular, the
 listeners will be notified after 100 milliseconds, 205
 milliseconds, 310 milliseconds, and so on.</p>

*/
public class TVTimerSpec extends java.lang.Object {

/*
 
 Creates a timer specification. It initially is absolute,
 non-repeating, regular specification set to go off at time 0. */

boolean absolute;
boolean repeat;
boolean regular;
long time; 

public TVTimerSpec (){
   absolute=true;
   repeat=false;
   regular=true;
   time = 0;
}


/*
 
 Sets this specification to be absolute or delayed. 
 Parameters:  absolute - Flag to indicate that this specification is
 either absolute or delayed. If true , the
 specification is absolute; otherwise, it is delayed. 
 
 
 */

public void setAbsolute (boolean absolute){
   this.absolute=absolute;
}


/*
 
 Checks if this specification is absolute. 
 Returns:  true if this specification is absolute;
 false if it is delayed. 
 
 
 */

public boolean isAbsolute (){
   return absolute;
}


/*
 
 Sets this specification to be repeating or non-repeating. 
 Parameters:  repeat - Flag to indicate that this specification is
 either repeating or non-repeating. If true , the
 specification is repeating; otherwise, it is non-repeating. 
 
 
 */

public void setRepeat (boolean repeat){
   this.repeat=repeat;
}


/*
 
 Checks if this specification is repeating. 
 Returns:  true if this specification is repeating;
 false if it is non-repeating. 
 
 
 */

public boolean isRepeat (){
   return repeat;
}


/*
 
 Sets this specification to be regular or non-regular. 
 Parameters:  regular - Flag to indicate that this specification is
 either regular or non-regular. If true , the
 specification is regular; otherwise, it is non-regular. 
 
 
 */

public void setRegular (boolean regular){
   this.regular=regular;
}


/*
 
 Checks if this specification is regular. 
 Returns:  true if this specification is regular;
 false if it is non-regular. 
 
 
 */

public boolean isRegular (){
   return regular;
}


/*
 
 Sets when this specification should go off. For absolute
 specifications, this is a time in milliseconds since midnight,
 January 1, 1970 UTC. For delayed specifications, this is a
 delay time in milliseconds. 
 Parameters:  time - The time when this specification should go off. 
 
 
 */

public void setTime (long time){
   this.time=time;
}


/*
 
 Returns the absolute or delay time when this specification
 will go off. 
 Returns: The time when this specification will go off. 
 
 
 */

public long getTime (){
   return time;
}



/*
 
 Sets this specification to go off at the given absolute time.
 This is a convenience function equivalent to
 setAbsolute(true) , setTime(when) ,
 setRepeat(false) . 
 Parameters:  when - The absolute time for the specification to go off. 
 
 
 */

public void setAbsoluteTime (long when){
   setAbsolute(true);
   setTime(when);
   setRepeat(false);
}


/*
 
 Sets this specification to go off after the given delay time.
 This is a convenience function equivalent to
 setAbsolute(false) , setTime(delay) ,
 setRepeat(false) . 
 Parameters:  delay - The relative time for the specification to go off. 
 
 
 */

public void setDelayTime (long delay){
   setAbsolute(false);
   setTime(delay);
   setRepeat(false);
}


/*
 
 Calls all listeners registered on this timer specification.
 This function is primarily for the benefit of those writing
 implementations of TVTimers. 
 Parameters:  source - The TVTimer that decided that this specification
 should go off. 
 
 
*/

  private java.util.ArrayList listeners = new java.util.ArrayList();

public void notifyListeners ( TVTimer source){
   /*if (regular) {
      Thread notifyThread = new NotifyThread(new TVTimerWentOffEvent(source, this));
      notifyThread.start();
   } else {
      TVTimerWentOffEvent event = new TVTimerWentOffEvent(source, this);
      synchronized(listeners) {
        for(int i=0; i < listeners.size(); i++) {
          ((TVTimerWentOffListener)(listeners.get(i))).timerWentOff(event);
        }
      }   
   }*/
   TVTimerWentOffEvent event = new TVTimerWentOffEvent(source, this);
   synchronized(listeners) {
      for(int i=0; i < listeners.size(); i++) {
         ((TVTimerWentOffListener)(listeners.get(i))).timerWentOff(event);
      }
   }
}

/*
 
 Registers a listener with this timer specification. 
 Parameters:  l - The listener to add. 
 
 
 */

public void addTVTimerWentOffListener ( TVTimerWentOffListener l){
   synchronized(listeners) {
      listeners.add(l);
   }
}


/*
 
 Removes a listener to this timer specification. Silently does nothing
 if the listener was not listening on this specification. 
 Parameters:  l - The listener to remove. 
 
 
 */

public void removeTVTimerWentOffListener ( TVTimerWentOffListener l){
   synchronized(listeners) {
      /* listener may have been registered several times */
      while(listeners.remove(l)) {}
   }
}

//not needed, taken care for by implementation!
/*private class NotifyThread extends Thread {
    private TVTimerWentOffEvent event;
    public NotifyThread(TVTimerWentOffEvent e) {
      super();
      this.event = e;
      setName("TVTimerSpec listeners notify");
    }
    public void run() {
      synchronized(listeners) {
        for(int i=0; i < listeners.size(); i++) {
          ((TVTimerWentOffListener)(listeners.get(i))).timerWentOff(event);
        }
      }
    }
}*/

}

