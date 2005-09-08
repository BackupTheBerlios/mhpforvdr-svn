package java.awt;

import java.awt.event.KeyEvent;
import java.util.Date;
import org.dvb.application.MHPApplication;

/* In MHP there are no heavy-weight classes like Window or Frame.
   All classes are derived from Component or Container, thus light-weight.
   The only heavy-weight class is HScene, so it is on TV what
   Window/Frame are on the computer screen.
   An MHPPlane is a native DirectFB window.
*/

public class MHPPlane extends Container {

//the stacking classes are used as follows for a 1.one layer, 2. two layer, 3. three layer configuration
static final int STACKING_LOWER  = 0; //used for 1. background plane 2. background plane 3. unused
static final int STACKING_MIDDLE = 1; //used for 1. video plane 2. video, graphics plane on different layers 3. all planes
static final int STACKING_UPPER  = 2; //used for 1. graphics planes 2. unused 3. unused

long nativeData = 0; //pointer to an IDirectFBWindow
long nativeEventBuffer=0;//pointer to an IDirectFBEventBuffer
long nativeLayer=0; //pointer to an IDirectFBDisplayLayer
EventThread eventThread;
boolean withEventThread=true;
MHPApplication application;

MHPPlane(int x, int y, int width, int height, MHPApplication application, boolean withEventThread, int stacking, long layer) {
   this.x=x;
   this.y=y;
   this.height=height;
   this.width=width;
   this.nativeLayer=layer;
   this.application=application;
   this.withEventThread=withEventThread;
   System.out.println("Creating MHP plane at "+x+"x"+y+" with size "+width+"x"+height+", layer is null ? "+(layer==0));
   // windows aren't visible per se, but they are exposed, colored and fontified
   flags = (IS_PARENT_SHOWING | IS_BG_COLORED | IS_FG_COLORED | IS_FONTIFIED);
   //Exception e=new Exception();e.printStackTrace();
   addNotify();
   setStackingClass(stacking);
}
static native long getMainLayer();
static native long getVideoLayer();
static native boolean hasVideoLayer();
static native long getBackgroundLayer();
static native boolean hasBackgroundLayer();

//the standard software approach, all composing done in software.
public static boolean isOneLayerConfiguration() {
   return !hasVideoLayer() && !hasBackgroundLayer();
}

//Case for a graphics and a video plane.
//I think it's best to put background+video in the lower and graphics in the upper layer.
//Possibly background is not supported.
public static boolean isTwoLayerConfiguration() {
   return hasVideoLayer() && !hasBackgroundLayer();
}

//the ideal configuration, blending the three planes done in hardware
public static boolean isThreeLayerConfiguration() {
   return hasVideoLayer() && hasBackgroundLayer();
}

protected static int getGraphicsStacking() {
   return isOneLayerConfiguration() ? STACKING_UPPER : STACKING_MIDDLE;
}

protected static int getVideoStacking() {
   return STACKING_MIDDLE;
}

protected static int getBackgroundStacking() {
   return isThreeLayerConfiguration() ? STACKING_MIDDLE : STACKING_LOWER;
}

MHPPlane(int x, int y, int width, int height, MHPApplication application, boolean withEventThread, int stacking) {
   this(x, y, width, height, application, withEventThread, stacking, getMainLayer());
}

//default constructor for graphics windows (HScenes)
protected MHPPlane(int x, int y, int width, int height, MHPApplication application) {
   this(x, y, width, height, application, true, getGraphicsStacking());
}

//internal API
//Returns application that created this object in the case of a HScene
//or null in the case of a background/video plane.
public org.dvb.application.MHPApplication getApplication() {
   return application;
}

public void addNotify () {
   //do the actual native creation
   nativeData=createDFBWindow(nativeLayer,x,y,width, height);
   ////it seems that Component per default have this flag set - but a HScene is not visible in the first place
   //flags &= ~IS_VISIBLE; 
   if (nativeData == 0)
      throw new NullPointerException("Native DFB window is null");
   if (withEventThread) {
      MHPScreen.checkEventDispatching(); //make sure dispatching thread started
      nativeEventBuffer=attachEventBuffer(nativeData);
      if (nativeEventBuffer!=0)
         eventThread=new EventThread(this, nativeEventBuffer);
   }
   super.addNotify();
}
private native long createDFBWindow(long layer, int x, int y, int width, int height);
private native long attachEventBuffer(long nativeData);


public void requestFocus () {
   //System.out.println("MHPPlane.requestFocus: is visible, don't call show? "+isVisible());
   //according to spec, first check if window is visible and call show() if not
   if (!isVisible())
      show();
   requestFocus(nativeData);
   AWTEvent.activeWindow=this;
}
private native void requestFocus(long nativeData);

public void dispose() {
   removeNotify();
}

public void removeNotify() {
   super.removeNotify();
   destroy(nativeData);
}
private native void destroy(long nativeData);

public void finalize() throws Throwable {
   if ((flags & IS_ADD_NOTIFIED)!=0)
      removeNotify();
   removeRefs(nativeData, nativeEventBuffer);
   nativeData = 0;
   nativeEventBuffer=0;
   super.finalize();
}
private native void removeRefs(long nativeWindow, long nativeEventBuffer);

public void show() {
   //System.out.println("MHPPlane.show() "+this);
   
   //code for this function taken and adapted from AWT's Window.java
   if ( nativeData == 0 ){
      addNotify();
   }

   // this happens to be one of the automatic validation points, and it should
   // cause a layout *before* we get visible
   validate();

   if ( (flags & IS_VISIBLE) != 0 ) {
      raiseToTop();
   }
   else {
      super.show();
      setOpacity(0xff);
      
      //we must trigger an initial painting here
      repaint();

      // the spec says that WINDOW_OPENED is delivered the first time a Window
      // is shown, and JDK sends this after it got shown, so this is the place
      /*if ( (flags & IS_OPENED) == 0 ) {
         flags |= IS_OPENED;

         if ( (wndListener != null) || (eventMask & AWTEvent.WINDOW_EVENT_MASK) != 0 ){
            AWTEvent.sendEvent( WindowEvt.getEvent( this,WindowEvent.WINDOW_OPENED), false);
         }
      }*/
   }
}

public void hide() {
   //System.out.println("MPPlane.hide()");
   setOpacity(0x0);
   super.hide();
}

//in MHP, we have to deal with transparency,
//so we follow the algorithm defined in the spec, chapter 13.3.2
public void repaint ( long ms, int x, int y, int width, int height ) {
   //System.out.println(" MHPPlane.repaint(): "+isVisible()+" "+x+"x"+y+", "+width+"x"+height);
   if ( isVisible() ){
   
      if ( x < 0 ) x = 0;
      if ( y < 0 ) y = 0;
      if ( (x + width) > getWidth() )
         width = getWidth() - x;
      if ( (y + height) > getHeight() )
         height = getHeight() - y;
   
      java.awt.MHPScreen.postPaintEvent( java.awt.event.PaintEvent.UPDATE, this, x, y, width, height);
   }
}

void processPaintEvent ( int id, int ux, int uy, int uw, int uh ) {
   System.out.println(" MHPPlane.processPaintEvent(): "+isVisible()+" "+ux+"x"+uy+", "+uw+"x"+uh);
   org.dvb.ui.DVBGraphics g = (org.dvb.ui.DVBGraphics)java.awt.MHPScreen.createClippedGraphics(this);
   //g.setClip(ux,uy,uw,uh);
   if ( g != null ){
      if ( id == java.awt.event.PaintEvent.UPDATE ) {
         update( g);
      }
      else {
         paint( g);
      }
      g.dispose();
   }
}


// Overrides the standard Container update method:
// Do not clear the background.
public void update(Graphics g) {
   flags |= IS_IN_UPDATE;

   this.paint( g);

   flags &= ~IS_IN_UPDATE;
}



public int getOpacity() {
   return ( (flags & IS_ADD_NOTIFIED) != 0 ) ? getOpacity(nativeData) : 0;
}
private native int getOpacity(long nativeData);

public void setOpacity(int opacity) {
   if ( 0x00 <= opacity && opacity <= 0xFF )
      setOpacity(nativeData, opacity);
}
private native void setOpacity(long nativeData, int opacity);

//the returned IDirectedFBSurface must be Release'd!
long getNativeSurface() {
   return nativeData == 0 ? 0 : getSurface(nativeData);
}
private native long getSurface(long nativeData);

void setStackingClass(int stacking) {
   setStackingClass(nativeData, stacking);
}
private native void setStackingClass(long nativeData, int stacking);

//These functions work on the native DirectFB window stack.
//Please note that they take the stacking class into account,
//so HScenes can be moved around freely without danger of putting them behind
//a background plane
void raise() {
   if (nativeData != 0)
      raise(nativeData);
}
private native void raise(long nativeData);

void lower() {
   if (nativeData != 0)
      lower(nativeData);
}
private native void lower(long nativeData);

void raiseToTop() {
   if (nativeData != 0)
      raiseToTop(nativeData);
}
private native void raiseToTop(long nativeData);

void lowerToBottom() {
   if (nativeData != 0)
      lowerToBottom(nativeData);
}
private native void lowerToBottom(long nativeData);

void putAtop(MHPPlane other) {
   if (nativeData != 0 && other.nativeData != 0)
      putAtop(nativeData, other.nativeData);
}
private native void putAtop(long nativeData, long otherNativeData);

void putBelow(MHPPlane other) {
   if (nativeData != 0 && other.nativeData != 0)
      putBelow(nativeData, other.nativeData);
}
private native void putBelow(long nativeData, long otherNativeData);

/*void lowerToBottom() {
   lowerToBottom(nativeData);
}
private native void lowerToBottom(long nativeData);

void putAtop(MHPPlane lower) {
   putAtop(nativeData, lower.nativeData);
}
private native void putAtop(long nativeData, long lowerNativeData);*/



/** Event handling **/


interface DFBEventConstants {
   final static int DWET_POSITION       = 0x00000001;  /* window has been moved by
                                         window manager or the
                                         application itself */
   final static int DWET_SIZE           = 0x00000002;  /* window has been resized
                                         by window manager or the
                                         application itself */
   final static int DWET_CLOSE          = 0x00000004;  /* closing this window has been
                                         requested only */
   final static int DWET_DESTROYED      = 0x00000008;  /* window got destroyed by global
                                         deinitialization function or
                                         the application itself */
   final static int DWET_GOTFOCUS       = 0x00000010;  /* window got focus */
   final static int DWET_LOSTFOCUS      = 0x00000020;  /* window lost focus */

   final static int DWET_KEYDOWN        = 0x00000100;  /* a key has gone down while
                                         window has focus */
   final static int DWET_KEYUP          = 0x00000200;  /* a key has gone up while
                                         window has focus */

   final static int DWET_BUTTONDOWN     = 0x00010000;  /* mouse button went down in
                                         the window */
   final static int DWET_BUTTONUP       = 0x00020000;  /* mouse button went up in
                                         the window */
   final static int DWET_MOTION         = 0x00040000;  /* mouse cursor changed its
                                         position in window */
   final static int DWET_ENTER          = 0x00080000;  /* mouse cursor entered
                                         the window */
   final static int DWET_LEAVE          = 0x00100000;  /* mouse cursor left the window */

   final static int DWET_WHEEL          = 0x00200000;  /* mouse wheel was moved while
                                         window has focus */

   final static int DWET_POSITION_SIZE  = DWET_POSITION | DWET_SIZE;/* initially sent to
                                                      window when it's
                                                      created */
                                                      
   final static int DIMM_SHIFT     = 1<<1;    /* Shift key is pressed */
   final static int DIMM_CONTROL   = 1<<2;  /* Control key is pressed */
   final static int DIMM_ALT       = 1<<3;      /* Alt key is pressed */
   final static int DIMM_ALTGR     = 1<<4;    /* AltGr key is pressed */
   final static int DIMM_META      = 1<<5;     /* Meta key is pressed */
   
}

//this class translates the relevant native DirectFB events
//to Java AWTEvents. These are posted to the Java eventQueue.
//The EventDispatchThread will take them from there and give
//them to the MHPEventFilter.
class EventThread extends Thread implements DFBEventConstants {

   long nativeData;
   long nativeEvent; //one DFBEvent being recycled
   MHPPlane plane;
   private boolean running = false;
   int[] eventData=new int[15];
   long midnight;


   EventThread(MHPPlane p, long nativeEventBuffer) {
      plane=p;
      nativeData=nativeEventBuffer;
      nativeEvent=allocateEvent();
      
      Date today=new Date();
      today.setHours(0);
      today.setMinutes(0);
      today.setSeconds(0);
      midnight=today.getTime();
      
      running=true;
      start();
   }
   private native long allocateEvent();
   
   public void finalize() {
      deleteEvent(nativeEvent);
   }
   private native void deleteEvent(long nativeEvent);
   
   public void run() {
      AWTEvent e;
      try {
         while (running) {
            e=getNextEvent();
            if (e != null)
               Toolkit.eventQueue.postEvent(e);
         }
         running=false;
      } catch (Exception ex) {
         ex.printStackTrace();
      } catch (Throwable x) {
         vdr.mhp.ApplicationManager.reportError(x);
      }
   }
   
   AWTEvent getNextEvent() {
      //System.out.println("Waiting for DFBEvent");
      waitForEvent(nativeData);
      //System.out.println("Waited for DFBEvent");
      if (getEvent(nativeData, nativeEvent)) {
         fillEventInformation(nativeEvent, eventData);
         return createAWTEvent();
      }
      return null;
   }
   private native void waitForEvent(long nativeData);
      //returns true if event is window event (should always be, I think)
   private native boolean getEvent(long nativeData, long nativeEvent);
   private native void fillEventInformation(long nativeEvent, int[] eventData);
   /* the array is filled as follows:
   data[0]=e->type;               type of event, DFBEventConstants
   data[1]=e->x;                  x position of window or coordinate within window
   data[2]=e->y;                  y position of window or coordinate within window
   data[3]=e->cx;                 x cursor position
   data[4]=e->cy;                 y ~
   data[5]=e->step;               wheel step
   data[6]=e->w;                  width of window
   data[7]=e->h;                  height ~
   data[8]=e->key_id;             basic modifier independant mapping
   data[9]=e->key_symbol;         advanced, unicode compatible, modifier independant mapping
   data[10]=e->modifiers;         pressed modifiers
   data[11]=e->button;            button being pressed or released
   data[12]=e->buttons;           mask of currently pressed buttons
   data[13]=e->timestamp.tv_sec;  timestamp, seconds of day
   data[14]=e->timestamp.tv_usec; timestamp, microseconds of day
   */

   //creates an AWT event from DirectFB eventData
   AWTEvent createAWTEvent() {
      //System.out.println("createAWTEvent: "+eventData[0]);
      switch (eventData[0]) {
         case DWET_KEYDOWN:
            //kaffe's key codes are unicode compatible, so DFB key_symbol == Java keyCode == Java keyChar
            return new KeyEvent(findKeyTarget(), KeyEvent.KEY_PRESSED, getMillis(eventData[13], eventData[14]), 
                                getModifierMask(eventData[10]), eventData[9], (char)eventData[9]);
         case DWET_KEYUP:
            return new KeyEvent(findKeyTarget(), KeyEvent.KEY_RELEASED, getMillis(eventData[13], eventData[14]), 
                                getModifierMask(eventData[10]), eventData[9], (char)eventData[9]);
         case DWET_BUTTONDOWN:
         case DWET_BUTTONUP:
         case DWET_MOTION:
         default:
            return null;
      }
   }
   
   Component findKeyTarget() {
      if (MHPEventFilter.getFocusComponent() != null) {
         return MHPEventFilter.getFocusComponent();
      } else {
         plane.requestFocus();
         return plane;
      }
   }
   
   //converts time-since-midnight to time-since-Epoch
   long getMillis(int secs, int usecs) {
      long ret=midnight+secs*1000 + (usecs/1000);
      if (System.currentTimeMillis() < ret) //new day
         midnight+=24*60*60*1000;
      return ret;
   }
   
   int getModifierMask(int DFBmask) {
      int mask=0;
      if ((DFBmask & DIMM_SHIFT)!=0)
         mask |= KeyEvent.SHIFT_MASK;
      if ((DFBmask & DIMM_CONTROL)!=0)
         mask |= KeyEvent.CTRL_MASK;
      if ((DFBmask & DIMM_ALT)!=0)
         mask |= KeyEvent.ALT_MASK;
      if ((DFBmask & DIMM_META)!=0)
         mask |= KeyEvent.META_MASK;
      return mask;
   }
   
}


}
