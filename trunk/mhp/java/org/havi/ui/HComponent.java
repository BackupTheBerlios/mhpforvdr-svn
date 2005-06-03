
package org.havi.ui;

import org.havi.ui.event.*;
import java.awt.event.*;

/*The HComponent class extends the java.awt.Component class by implementing the HMatteLayer 
interface.The parameters to the constructors are as follows,in cases where parameters are 
not used, then the constructor should use the default values. */

//TODO: How to implement the matte layer? Subclasses will override the paint() method I think.

public abstract class HComponent extends java.awt.Component implements HMatteLayer, HMatteListener, org.dvb.ui.TestOpacity {

/*
The associated matte */
private HMatte matte;


/*
Creates an HComponent object.See the class description for details of constructor parameters and default 
values. */
public HComponent() {
   super();
   matte=null;
}

/*
Creates an HComponent object.See the class description for details of constructor parameters and default 
values. */
public HComponent(int x, int y, int width, int height) {
   super();
   setBounds(x,y,width,height);
}

/*
Get any HMatte currently associated with this component. Speci  ed By: getMatte()in interface HMatteLayer Returns: the 
HMatte currently associated with this component or null if there is no associated 
matte. */
public HMatte getMatte() {
   return matte;
}

/*
Returns true if all the drawing done during the update and paint methods for this speci  c HComponent object is 
automatically double buffered. Overrides: java.awt.Component.isDoubleBuffered()in class java.awt.Component Returns: true 
if all the drawing done during the update and paint methods for this speci  c HComponent object is automatically double 
buffered,or false if drawing is not double buffered.The default value for the double buffering setting is platform-speci 
 c. */
public boolean isDoubleBuffered() {
   //if you change this, also change it in HContainer
   if (getParent() != null)
      return getParent().isDoubleBuffered();
   return super.isDoubleBuffered();
}

/*
Returns true if the entire HComponent area,as given by the java.awt.Component#getBounds method,is fully opaque,i.e.its 
paint method (or surrogate methods)guarantee that all pixels are painted in an opaque Color By default,the return value 
is false The return value should be overridden by subclasses that can guarantee full opacity.The consequences of an 
invalid overridden value are implementation speci  c. Speci  ed By:isOpaque()in interface TestOpacity Returns: true if 
all the pixels within the area given by the java.awt.Component#getBounds method are fully opaque,i.e.its paint method 
(or surrogate methods)guarantee that all pixels are painted in an opaque Color,otherwise 
false */
public boolean isOpaque() {
   return false;
}

/*
This method may be overridden to facilitate the generation of events in the org.havi.ui.event package from 
java.awt.AWTEvents.Events used in this way (e.g.java.awt.event.KeyEvents are NOT consumed and should be propagated to 
the superclass. Subclasses of HComponent must always call the superclass if they override this function. Overrides: 
java.awt.Component.processEvent(java.awt.AWTEvent)in class java.awt.Component Parameters: evt -the java.awt.AWTEvent to 
handle. */

//I still think that the use of three different event libraries in MHP was a bad choice
//I do not know how it all is supposed to work
protected void processEvent(java.awt.AWTEvent evt) {
   switch(evt.getID()) {
   case KeyEvent.KEY_TYPED: //400..402
   case KeyEvent.KEY_PRESSED:
   case KeyEvent.KEY_RELEASED:
      if (this instanceof HKeyboardInputPreferred) {
         java.awt.event.KeyEvent keyevt=(java.awt.event.KeyEvent)evt;
         HKeyEvent e;
         if ( (HRcEvent.RC_FIRST <= keyevt.getKeyCode()) && (keyevt.getKeyCode() <= HRcEvent.RC_LAST) )
            e=new HRcEvent((java.awt.Component)keyevt.getSource(), keyevt.getID(), keyevt.getWhen(), keyevt.getModifiers(), keyevt.getKeyCode());
         else
            e=new HKeyEvent((java.awt.Component)keyevt.getSource(), keyevt.getID(), keyevt.getWhen(), keyevt.getModifiers(), keyevt.getKeyCode());
         ((HKeyboardInputPreferred)this).processHKeyEvent(e);
      }
      break;
   case FocusEvent.FOCUS_GAINED: //1004..1005
   case FocusEvent.FOCUS_LOST:
      if (this instanceof HNavigationInputPreferred) {
         HFocusEvent e=new HFocusEvent((java.awt.Component)evt.getSource(), evt.getID());
         ((HNavigationInputPreferred)this).processHFocusEvent(e);
      }
      break;
   case ContainerEvent.COMPONENT_REMOVED:
      break;
   case ActionEvent.ACTION_PERFORMED: //1001
      if (this instanceof HActionInputPreferred && evt.getSource() instanceof HActionable) {
         HActionEvent e=new HActionEvent((HActionable)evt.getSource(), evt.getID(), ((java.awt.event.ActionEvent)evt).getActionCommand());
         ((HActionInputPreferred) this).processHActionEvent(e);
      }
      break;
      /*if (this instanceof HAdjustmentInputPreferred) {
         HAdjustmentEvent e=new HAdjustmentEvent(e.getSource, e.id);
         
         ((HAdjustmentInputPreferred)this).processHAdjustmentEvent(e);
      }*/
   }
   super.processEvent(evt);
}

/*
Applies an HMatte to this component,for matte compositing.Any existing animated matte must be stopped before this method 
is called or an HMatteException will be thrown. Speci  ed By: setMatte(HMatte)in interface HMatteLayer Parameters: m 
-The HMatte to be applied to this component --note that only one matte may be associated with the component,thus any 
previous matte will be replaced.If m is null,then any matte associated with the component is removed and further calls 
to getMatte()shall return null.The component shall behave as if it had a fully opaque HFlatMatte associated with it (i.e 
anHFlatMatte with the default value of 1.0.) Throws: HMatteException -if the HMatte cannot be associated with the 
component.This can occur: " if the specific matte type is not supported " if the platform does not support any matte 
type " if the component is associated with an already running HFlatEffectMatte or HImageEffectMatte . The exception is 
thrown even if m is null. See Also: HMatte */
public void setMatte(HMatte m) {
    /* If there was already a matte, unregister from it */
    if(matte != null) {
      matte.removeListener(this);
    }
    if (m==matte)
       return;
    matte = m;
   
    if (m != null)
      /* Register to be notified of changes in the matte -- NOT FROM API */
      m.addListener(this);
    /* Update the component to reflect this change */
    matteUpdate(null);
}


  /** Receive an HMatte Event **/
  public void matteUpdate(HMatteEvent e) {
    if(this.isShowing()) {
      this.repaint();
    }
  }

}
