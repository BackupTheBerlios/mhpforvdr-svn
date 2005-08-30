
package org.dvb.media.content.dripfeed;


import javax.media.Time;
import javax.media.Control;

// TODO:
// I do not know why this class uses the HBackgroundDevice. That's wrong.
// All this media stuff is unimplemented, the class is only an example.

/* A player implementation for MPEG-2 drip, org.dvb.media.DripFeedDataSource
   The strange package name is required in order to be found by javax.media.Manager
*/

public class Player implements javax.media.Player {

javax.media.ControllerListener listener = null;
int state=Unrealized;
int targetState=Unrealized;
org.havi.ui.HBackgroundDevice bg=null;
/*long nativeData = 0;

private native long initialize();
private native void freeResources(long nativeData);
private native void playData(long nativeData, byte[] data);*/

/* Player interface */

public void removeController(javax.media.Controller controller) {
}

public void addController(javax.media.Controller controller) throws javax.media.IncompatibleTimeBaseException {
}

public java.awt.Component getControlPanelComponent() {
   return null;
}

public java.awt.Component getVisualComponent() {
   return null;
}

public synchronized void start() {
   if (state==Started)
      sendAppropriateEvent(state, state);
   targetState=Started;
   new Thread(eventSender).start();
}

public javax.media.GainControl getGainControl() {
   return null;
}



//This implements the state machine for all advancing state changes.
//It is called in asynchronous context, while all "regressive" changes
//are handled synchronously (stop(), deallocate(), close())
void changeState() {
   if (targetState > state) {
      switch(state) {
      case Unrealized:
      case Realizing:
         state=Realized;
         //Allocate native data, free'd in deallocate()
         //nativeData=initialize();
         bg=org.havi.ui.HScreen.getDefaultHScreen().getDefaultHBackgroundDevice();
         //if (!bg) //send appropriate error event
         sendAppropriateEvent(Realized, Realizing);
         changeState();
         break;
      case Realized:
      case Prefetching:
         state=Prefetched;
         sendAppropriateEvent(Prefetched, Prefetching);
         changeState();
         break;
      case Prefetched:
         doStart();
         state=Started;
         sendAppropriateEvent(Started, Prefetched);
         break;
      }
   }
}

void sendAppropriateEvent(int kindOf, int previous) {
   javax.media.ControllerEvent e=null;
   switch(kindOf) {
   case Prefetched:
      e=new javax.media.PrefetchCompleteEvent(this, previous, state, targetState);
      break;
   case Realized:
      e=new javax.media.RealizeCompleteEvent(this, previous, state, targetState);
      break;
   case Started:
      e=new javax.media.StartEvent(this, previous, state, targetState, new Time(0), new Time(0));
      break;
   case -1:
      e=new javax.media.StopEvent(this, previous, state, targetState, new Time(0));
      break;
   case -2:
      e=new javax.media.DeallocateEvent(this, previous, state, targetState, new Time(0));
      break;
   case -3:
      e=new javax.media.ControllerClosedEvent(this);
      break;
   default:
      return;
   }
   if (listener != null)
      listener.controllerUpdate(e);
}

class EventSender implements Runnable {
EventSender(Player p) {
   this.p=p;
}
Player p;
public void run() {
   try {
      synchronized (p) {
         p.changeState();
      }
   } catch (Exception ex) {
      System.err.println("Error in org.dvb.media.content.dripfeed.Player.EventSender.run()");
   } catch (Throwable t) {
      vdr.mhp.ApplicationManager.reportError(t);
   }
}
};
EventSender eventSender=new EventSender(this);


void doStart() {
   if (running)
      return;
   running=true;
   thread.start();
}

boolean running=false;

class PlayerThread extends Thread {
   PlayerThread(Player p) {
      this.p=p;
   }
   Player p;
   
   public void run() {
      try {
         byte data[]=null;
         if (source==null) {
            p.running=false;
            return;
         }
         while (p.running) {
            data=p.source.getNext();
            if (data != null) {
               p.PlayData(data);
            }
         }
      } catch (Exception ex) {
         System.err.println("Error in org.dvb.media.content.dripfeed.Player.PlayerThread.run()");
      } catch (Throwable t) {
         vdr.mhp.ApplicationManager.reportError(t);
      }
   }
   
};

PlayerThread thread=new PlayerThread(this);

void PlayData(byte[] data) {
   //service kindly provided by HBackgroundDevice, actual work done in MHPBackgroundPlane
   //bg.displayDripfeed(data);
}


/* Clock interface */

//there isn't much sense in supporting a time model for a drip feed.

public void setStopTime(Time stopTime) {
}

public Time getStopTime() {
   return javax.media.Clock.RESET;
}

public void setMediaTime(Time now) {
}

public Time getMediaTime() {
   return new Time(0);
}

public long getMediaNanoseconds() {
   return 0;
}

public Time getSyncTime() {
   return getMediaTime();
}


public float getRate() {
   return 1.0f;
}

public float setRate(float factor) {
   return getRate();
}


public void syncStart(Time at) {
   start();
}

//operates synchronously as I see it.
public synchronized void stop() {
   if (state > Prefetched) {
      if (running) {
         running=false;
         try {
            thread.join(1000);
         } catch (InterruptedException _) {
         }
         thread.destroy();
      }
      int oldState=state;
      targetState=Prefetched;
      state=Prefetched;
      sendAppropriateEvent(-1, oldState);
   } 
   if (state == Prefetching) {
      int oldState=state;
      targetState=Realized;
      state=Realized;
      sendAppropriateEvent(-1, oldState);
   }
}

public javax.media.Time mapToTimeBase(javax.media.Time time) throws javax.media.ClockStoppedException {
   return time;
}

public javax.media.TimeBase getTimeBase() {
   return null;
}

public void setTimeBase(javax.media.TimeBase timebase) throws javax.media.IncompatibleTimeBaseException {
}





/* MediaHandler interface */

org.dvb.media.DripFeedDataSource source=null;

public void setSource(javax.media.protocol.DataSource s) 
  throws java.io.IOException, javax.media.IncompatibleSourceException {
   if (!(s instanceof org.dvb.media.DripFeedDataSource))
      throw new javax.media.IncompatibleSourceException("unknown source");
   if (state>=Prefetching)
      return;
   source=(org.dvb.media.DripFeedDataSource)s;
}





/* Controller interface */

public void addControllerListener(javax.media.ControllerListener l) {
   org.dvb.media.MediaMulticaster.add(listener, l);
}

public void removeControllerListener(javax.media.ControllerListener l) {
   org.dvb.media.MediaMulticaster.remove(listener, l);
}

public synchronized void realize() {
   if (state>=Realized)
      sendAppropriateEvent(state, state);
   targetState=Realized;
   new Thread(eventSender).start();
}

public synchronized void prefetch() {
   if (state>=Prefetched)
      sendAppropriateEvent(state, state);
   targetState=Prefetched;
   new Thread(eventSender).start();
}

//deallocate operates synchronously. It is illegal to use it on a not-stopped player.
public synchronized void deallocate() {
   if (state==Started)
      throw new javax.media.ClockStartedError("Calling deallocate on a running dripfeed player");
   if (state >= Realized) {
      int oldState=state;
      targetState=Realized;
      
      //freeResources(nativeData);
      //nativeData = 0;
      
      state=Realized;
      sendAppropriateEvent(-2, oldState);
   }
   if (state == Realizing) {
      int oldState=state;
      targetState=Unrealized;
      
      //freeResources(nativeData);
      //nativeData = 0;
      
      state=Unrealized;
      sendAppropriateEvent(-2, oldState);
   }
}

public synchronized void close() {
   if (state==Started)
      return; //should send error event
   int oldState=state;
   state=Unrealized;
   sendAppropriateEvent(-3, oldState);
}

public int getState() {
   return state;
}

public int getTargetState() {
   return targetState;
}

public Control[] getControls() {
   return new Control[0];
}

public Control getControl(String forName) {
   return null;
}

public javax.media.Time getStartLatency() {
   return LATENCY_UNKNOWN;
}




/* Duration interface */

public javax.media.Time getDuration() {
   return javax.media.Duration.DURATION_UNKNOWN;
}


}
