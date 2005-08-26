package java.awt;

import java.awt.event.KeyEvent;
import java.util.Date;
import org.dvb.application.MHPApplication;
import vdr.mhp.awt.DFBWindowPeer;
import vdr.mhp.awt.MHPToolkit;
import java.awt.MHPPlane;
import java.awt.MHPScreen;

/* In MHP there are no heavy-weight classes like Window or Frame.
   All classes are derived from Component or Container, thus light-weight.
   The only heavy-weight class is HScene, so it is on TV what
   Window/Frame are on the computer screen.
   An MHPPlane is a native DirectFB window.
   To fit in AWT's hierarchy and to satisfy several assumptions (in
   Classpath's implementation and the spec), MHPPlane is a Frame,
   which is defined to be top-level, rather than only a Window.
*/

public class MHPPlane extends Frame {

//the stacking classes are used as follows for a 1.one layer, 2. two layer, 3. three layer configuration
static final int STACKING_LOWER  = 0; //used for 1. background plane 2. background plane 3. unused
static final int STACKING_MIDDLE = 1; //used for 1. video plane 2. video, graphics plane on different layers 3. all planes
static final int STACKING_UPPER  = 2; //used for 1. graphics planes 2. unused 3. unused

long nativeLayer=0; //pointer to an IDirectFBDisplayLayer
boolean withEventThread=true;
MHPApplication application;

MHPPlane(int x, int y, int width, int height, MHPApplication application, boolean withEventThread, int stacking, long layer) {
   //set bounds
   this.x=x;
   this.y=y;
   this.height=height;
   this.width=width;
   
   this.nativeLayer=layer;
   this.application=application;
   this.withEventThread=withEventThread;
   System.out.println("Creating MHP plane at "+x+"x"+y+" with size "+width+"x"+height+", layer is null ? "+(layer==0));
   addNotify();
   setStackingClass(stacking);
}
protected static int getGraphicsStacking() {
   return MHPScreen.isOneLayerConfiguration() ? STACKING_UPPER : STACKING_MIDDLE;
}

protected static int getVideoStacking() {
   return STACKING_MIDDLE;
}

protected static int getBackgroundStacking() {
   return MHPScreen.isThreeLayerConfiguration() ? STACKING_MIDDLE : STACKING_LOWER;
}

MHPPlane(int x, int y, int width, int height, MHPApplication application, boolean withEventThread, int stacking) {
   this(x, y, width, height, application, withEventThread, stacking, MHPScreen.getMainLayer());
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

public static org.dvb.application.MHPApplication getApplication(Component c) {
   MHPPlane toplevel = getMHPPlane(c);
   return toplevel != null ? toplevel.getApplication() : null;
}

public static MHPPlane getMHPPlane(Component component) {
   Component c;
   for ( c = component; !(c instanceof MHPPlane) && c != null; c = c.getParent() );
   return (MHPPlane)c;
}


public void addNotify () {
   if (peer == null) {
      peer = ((MHPToolkit) getToolkit()).createDFBWindowPeer(this);
      ((DFBWindowPeer) peer).create(x, y, width, height, nativeLayer, withEventThread);
   }
   super.addNotify();
}

public void setBoundsCallback (int x, int y, int w, int h) {
   // public here because peer needs it
   super.setBoundsCallback(x, y, w, h);
}


/*
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
*/

/*
// taken from Window.java
public void dispose()
  {
    hide();

    synchronized (getTreeLock ())
      {
        *
	Iterator e = ownedWindows.iterator();
	while(e.hasNext())
	  {
	    Window w = (Window)(((Reference) e.next()).get());
	    if (w != null)
	      w.dispose();
	    else
	      // Remove null weak reference from ownedWindows.
	      e.remove();
	  }
        *

	for (int i = 0; i < ncomponents; ++i)
	  component[i].removeNotify();
	this.removeNotify();

      *
        // Post a WINDOW_CLOSED event.
        WindowEvent we = new WindowEvent(this, WindowEvent.WINDOW_CLOSED);
        getToolkit().getSystemEventQueue().postEvent(we);
        *
      }
  }
*/
/*
// taken from Window.java
public void show() {
   //System.out.println("MHPPlane.show() "+this);
   if ( peer == null ){
      addNotify();
   }

   validate();
   super.show();
   setOpacity(0xFF);
   raiseToTop();

   if (!shown)
      {
        FocusTraversalPolicy policy = getFocusTraversalPolicy ();
        Component initialFocusOwner = null;

        if (policy != null)
          initialFocusOwner = policy.getInitialComponent (this);

        if (initialFocusOwner != null)
          initialFocusOwner.requestFocusInWindow ();

        shown = true;
      }
}
*/
/*
public void hide() {
   //System.out.println("MPPlane.hide()");
   setOpacity(0x0);
   super.hide();
}
*/

/*
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
*/


public int getOpacity() {
   if (peer != null)
      return ((DFBWindowPeer) peer).getOpacity();
   return 0;
}

public void setOpacity(int opacity) {
   if (peer != null)
      ((DFBWindowPeer) peer).setOpacity(opacity);
}

//the returned IDirectedFBSurface must be Release'd!
/*long getNativeSurface() {
   return nativeData == 0 ? 0 : getSurface(nativeData);
}
private native long getSurface(long nativeData);
*/
void setStackingClass(int stacking) {
   if (peer != null)
      ((DFBWindowPeer) peer).setStackingClass(stacking);
}

//These functions work on the native DirectFB window stack.
//Please note that they take the stacking class into account,
//so HScenes can be moved around freely without danger of putting them behind
//a background plane
void raise() {
   if (peer != null)
      ((DFBWindowPeer) peer).raise();
}

void lower() {
   if (peer != null)
      ((DFBWindowPeer) peer).lower();
}

void raiseToTop() {
   if (peer != null)
      ((DFBWindowPeer) peer).raiseToTop();
}

void lowerToBottom() {
   if (peer != null)
      ((DFBWindowPeer) peer).lowerToBottom();
}

void putAtop(MHPPlane other) {
   DFBWindowPeer otherPeer = (DFBWindowPeer) other.getPeer();
   if (peer != null && otherPeer != null)
      ((DFBWindowPeer) peer).putAtop(otherPeer);
}

void putBelow(MHPPlane other) {
   DFBWindowPeer otherPeer = (DFBWindowPeer) other.getPeer();
   if (peer != null && otherPeer != null)
      ((DFBWindowPeer) peer).putBelow(otherPeer);
}

/*void lowerToBottom() {
   lowerToBottom(nativeData);
}
private native void lowerToBottom(long nativeData);

void putAtop(MHPPlane lower) {
   putAtop(nativeData, lower.nativeData);
}
private native void putAtop(long nativeData, long lowerNativeData);*/




}
