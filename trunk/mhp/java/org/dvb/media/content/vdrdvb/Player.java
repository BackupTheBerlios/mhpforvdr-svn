
package org.dvb.media.content.vdrdvb;


import javax.media.Time;
import javax.media.Control;

/* A dummy player for normal TV display
*/

public class Player implements javax.media.Player, javax.tv.service.selection.ServiceContentHandler {

/* ContentHandler interface */

public javax.tv.locator.Locator[] getServiceContentLocators() {
   return null;
}

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

public void start() {
}

public javax.media.GainControl getGainControl() {
   return null;
}





/* Clock interface */

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
   return new Time(0);
}


public float getRate() {
   return 0;
}

public float setRate(float factor) {
   return 0;
}


public void syncStart(Time at) {
}

public void stop() {
}

public javax.media.Time mapToTimeBase(javax.media.Time time) throws javax.media.ClockStoppedException {
   return new Time(0);
}

public javax.media.TimeBase getTimeBase() {
   return null;
}

public void setTimeBase(javax.media.TimeBase timebase) throws javax.media.IncompatibleTimeBaseException {
}





/* MediaHandler interface */


public void setSource(javax.media.protocol.DataSource source) 
  throws java.io.IOException, javax.media.IncompatibleSourceException {
}





/* Controller interface */

public void addControllerListener(javax.media.ControllerListener listener) {
}

public void removeControllerListener(javax.media.ControllerListener listener) {
}

public void realize() {
}

public void prefetch() {
}

public void deallocate() {
}

public void close() {
}

public int getState() {
   return Started;
}

public int getTargetState() {
   return Started;
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
