/*
 * NIST/DASE API Reference Implementation
 * $File: HScene.java $
 * Last changed on $Date: 2001/06/15 21:19:50 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.MHPScreen;
import java.awt.MHPPlane;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.AWTEventMulticaster;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;


/**
 * Implementation of HScene, the base window for all HAVi application.
 * (equivalent to a Frame for AWT). HScenes cannot be instanciated directly,
 * but via HSceneFactory.
 * <br>See (C) official HaVi documentation for reference.
 * <p>
 * <p>Revision information:<br>
 * $Revision: 1.5 $
 *
 */
 
//Note that this class directly inherits MHPPlane, and knows this.
//Most necessary changes are implemented in MHPPlane


public class HScene extends MHPPlane implements HComponentOrdering {


public static final int BACKGROUND_FILL = 1;
public static final int NO_BACKGROUND_FILL = 0;
public static final int IMAGE_CENTER = 2;
public static final int IMAGE_NONE = 0;
public static final int IMAGE_STRETCH = 1;
public static final int IMAGE_TILE = 3;
  /** Set if shortcuts are enabled for this HScene. True by default. */
  boolean shortcutsEnabled = true;
  java.awt.Image backgroundImage = null;
  int backgroundMode = NO_BACKGROUND_FILL;
  int renderMode = IMAGE_NONE;
  
  private java.awt.Color transparent=new java.awt.Color(0,0,0,0);


  /*public HScene() {
    super();
  }
  */
  /** Constructor with a requested geometry (relative to parent). NOT FROM API.
      Note that the actual geometry depends on the layout manager
      @param x horizontal position relative to parent
      @param y vertical position relative to parent
      @param width requested width
      @param height requested height */
  HScene(int x, int y, int width, int height, org.dvb.application.MHPApplication app) {
    super(x, y, width, height, app);
  }
  
  public void dispose() {
    HSceneFactory.getInstance().dispose(this);
    // there is a slight clash in naming, dispose is redefined by Havi and is used internally
    // by AWT. Call super class anyway, should be all right.
    super.dispose();
  }

  
// Leave to Window
  /*
  Returns the child component of this HScene which has focus if and only 
  if this HScene is active. Returns: the component with focus,or null 
  if no children have focus assigned to them.
  */
  /*
  public java.awt.Component getFocusOwner() {
  }  
  */
  
  /** Request that the HScene be made visible, that is request focus from WM
      and realize and its subcomponent.
      @param show visibility state
  */
/*
  public void setVisible(boolean show) {
    if(show) {
      this.requestFocus();
    }
    super.setVisible(show);
  }
*/

  /** Query whether the container is currently visible. Since there is
      no WM implemented yet, simply returns the visibility status of the
      parent Component */
/*
  public boolean isVisible() {
    return super.isVisible();
  }
*/
  
  /*
  Get the rendering mode of any background image associated with this HScene . 
  Returns: the rendering mode,one of IMAGE_NONE ,IMAGE_STRETCH ,IMAGE_CENTER or IMAGE_TILE .
  */
  public int getRenderMode() {
     return renderMode;
  }


  /** Overridden paint method so that it paints its children, but not
      itself.
      @param g Graphics context to use for this operation */   
public void paint(Graphics g) {
    org.dvb.ui.DVBGraphics dvbG=(org.dvb.ui.DVBGraphics)g;
    
    dvbG.enterBuffered();
    
    Rectangle bounds=getBounds();
    java.awt.Color color=dvbG.getColor();
    org.dvb.ui.DVBAlphaComposite comp=dvbG.getDVBComposite();
    try { dvbG.setDVBComposite(org.dvb.ui.DVBAlphaComposite.Src); } catch (org.dvb.ui.UnsupportedDrawingOperationException _) {}
    
    if (backgroundMode==BACKGROUND_FILL) {
       dvbG.setColor(getBackground());
       dvbG.fillRect(0, 0, bounds.width, bounds.height);
    } else {
       dvbG.setColor(transparent);
       dvbG.fillRect(0, 0, bounds.width, bounds.height);
    }
    
    if (renderMode != IMAGE_NONE && backgroundImage != null) {
       //TODO: Scale Image, respect renderMode
       dvbG.drawImage(backgroundImage, 0, 0, bounds.width, bounds.height, getBackground(), this);
    }
    
    //restore settings
    dvbG.setColor(color);
    try { dvbG.setDVBComposite(comp); } catch (org.dvb.ui.UnsupportedDrawingOperationException _) {}
    
    //paint children
    super.paint(g);
    
    dvbG.leaveBuffered();
}
  
  
public java.awt.Image getBackgroundImage() {
   return backgroundImage;
}
 
/*
Set an image which shall be painted in the background of the HScene ,
after the background has been drawn according to the current mode set 
with setBackgroundMode(int),but before any children are drawn.
The image is rendered according to the current render mode set with 
setRenderMode(int). Note that the use of a background image in this 
way may affect the return value of the isOpaque() method,depending on 
the image and the current rendering mode. 
Parameters: image -the image to be used as a background.If this parameter 
is null any current image is removed.Note that depending on the current 
render mode any image set may not actually be rendered. See Also: setRenderMode(int)
*/ 
public void setBackgroundImage(java.awt.Image image) {
   backgroundImage=image;
}  

/*
Set the background mode of this HScene .
The value speci  es whether the paint method should draw the background 
(i.e.a rectangle  lling the bounds of the HScene ). Note that the background 
mode will affect the return value of the isOpaque()method,depending on the 
value of the mode parameter.A  ll mode of BACKGROUND_FILL implies that isOpaque() 
must return true Parameters: mode -one of NO_BACKGROUND_FILL or BACKGROUND_FILL .
If mode is not a valid value,an IllegalArgumentException will be thrown.
*/
public void setBackgroundMode(int mode) {
   backgroundMode=mode;
}

public boolean isOpaque() {
   return backgroundMode==BACKGROUND_FILL 
         || (backgroundImage != null 
               && (renderMode == IMAGE_STRETCH || renderMode == IMAGE_TILE) );
}

/*
Set the rendering mode of any background image associated with this HScene .
 Note that the minimum requirement is to support only the IMAGE_NONE mode.
 Support of the other modes is platform and implementation speci  c.
  Parameters: mode -the rendering mode,one of IMAGE_NONE ,IMAGE_STRETCH ,
  IMAGE_CENTER or IMAGE_TILE . Returns: true if the mode was set successfully,
  false if the mode is not supported by the platform.
*/
public boolean setRenderMode(int mode) {
   switch (mode) {
   case IMAGE_NONE:
   case IMAGE_STRETCH:
   case IMAGE_CENTER:
      renderMode=mode;   
      return true;
   case IMAGE_TILE:
   default:
      return false;
   }
}  

// The spec requires this WindowEvent infrastructure like java.awt.Window.
// Now, MHPPlane is a Window, but this is not specified!
// Leave this to Window

  /** Private list of window listeners */
  // TODO Fire these events accordingly
  //private WindowListener windowListeners = null;
  
  /** Add a listener to receive WindowEvents
      @param wl object implementing WindowListener to notify */
  /*public void addWindowListener(WindowListener wl) {
    windowListeners = AWTEventMulticaster.add(windowListeners, wl);
  }*/

  /** Remove a WindowEvent listener
      @param wl object implementing WindowListener to remove */
  /*public void removeWindowListener(WindowListener wl) {
    windowListeners = AWTEventMulticaster.remove(windowListeners, wl);
  } 


protected void processWindowEvent(java.awt.event.WindowEvent event) {
   if ( windowListeners != null ) {
      switch ( event.getID() ) {
      case WindowEvent.WINDOW_OPENED:
         windowListeners.windowOpened( event);
         break;
      case WindowEvent.WINDOW_CLOSING:
         windowListeners.windowClosing( event);
         break;
      case WindowEvent.WINDOW_CLOSED:
         windowListeners.windowClosed( event);
         break;
      case WindowEvent.WINDOW_ICONIFIED:
         windowListeners.windowIconified( event);
         break;
      case WindowEvent.WINDOW_DEICONIFIED:
         windowListeners.windowDeiconified( event);
         break;
      case WindowEvent.WINDOW_ACTIVATED:
            windowListeners.windowActivated( event);
         break;
      case WindowEvent.WINDOW_DEACTIVATED:
            windowListeners.windowDeactivated( event);
         break;
      }
   }
}
*/
  
  
  /** Shortcut Hash table */
  private Hashtable shortcutTable = new Hashtable();

  
  /** Install a new shortcut: A KeyEvent/HRcEvent will action an HActionable.
      @param keyCode the keycode associated to this shortcut. If a shortcut
          is already associated with this key, it will be removed.
          As required by the specs, VK_UNDEFINED events are ignored.
      @param comp target HActionable to be actioned (with a VK_ACTION event)
          If comp is not a subcomponent of this HScene, the shortcut is ignored
  */
  public void addShortcut(int keyCode,
                          HActionable comp) {

    if(keyCode == KeyEvent.VK_UNDEFINED) {
      return;
    }

    /* Safety check: make sure that comp is a subcomponent */
    if( ! this.isAncestorOf((Component)comp) ) {
      System.out.println("HScene: attempt to associate a shortcut with a Component not in this HScene => ignored");
      return;
    }

    synchronized(shortcutTable) {
      shortcutTable.put(new Integer(keyCode), comp);
    }

  }

  /** Remove an existing shortcut. Do nothing if not present.
      @param keyCode key code of the shortcut to remove */
  public void removeShortcut(int keyCode) {
    synchronized(shortcutTable) {
      shortcutTable.remove(new Integer(keyCode));
    }
  }


  /** Enable/disable  all keyboard/RC shortcuts. They are not removed.
      @param enable request shortcut state */
  public void enableShortcuts(boolean enable) {
    shortcutsEnabled = enable;
  }

  /** Query the current shortcut status
      @return true if shortcuts are enabled */
  public boolean isEnableShortcuts() {
    return shortcutsEnabled;
  }


  /** Return the shortcut currently associated with the specified HActionable.
      @param comp HActionable whose shortcut is requested
      @return key code of the shortcut or KeyEvent.VK_UNDEFINED if none. */
  public int getShortcutKeycode(HActionable comp) {

    synchronized(shortcutTable) {
      
      if(!shortcutTable.contains(comp)) {
        return KeyEvent.VK_UNDEFINED;
      }
      
      /* Find the component */
      Object key = null;
      for (Enumeration e = shortcutTable.keys() ; e.hasMoreElements() ;) {
        key = e.nextElement();
        if( shortcutTable.get(key) == comp ) {
          return ((Integer)key).intValue();
        }
      }

      /* Note: this should never happen */
      return KeyEvent.VK_UNDEFINED;

    }
  }

  /** Return an array of all currently allocated shortcuts
      @return array of keycodes */
  public int[] getAllShortcutKeycodes() {

    int[] keyCodes;
   
    synchronized(shortcutTable) {

      keyCodes = new int[shortcutTable.keySet().size()];
      
      int i=0;
      for (Enumeration e = shortcutTable.keys() ; e.hasMoreElements() ;) {
        keyCodes[i++] = ((Integer)e.nextElement()).intValue();
      }

    }
    return keyCodes;
    
  }
  
  /*
  Retrieve the HActionable associated with the speci  ed shortcut key.
  Parameters: keyCode -the shortcut key code to be queried for an associated HActionable . 
  Returns: the HActionable associated with the speci  ed key if keyCode is a valid shortcut 
  key for this HScene ,null otherwise.
  */
  public HActionable getShortcutComponent(int keyCode) {
    synchronized(shortcutTable) {
      return (HActionable)shortcutTable.get(new Integer(keyCode));
    }   
  }


  public HScreenRectangle getPixelCoordinatesHScreenRectangle(Rectangle r) {

    /* Returns an HScreenRectangle which corresponds to the graphics (AWT)
      pixel area specified by the parameter in this HScene (i.e. within the
      HScene's coordinate space).
      Parameters:
        r - the AWT pixel area within this HScene (i.e. within the HScene's
      coordinate space), specified as an java.awt.Rectangle.
      Returns: an HScreenRectangle which corresponds to the graphics (AWT)
      pixel area specified by the parameter in this HScene (i.e. within the
      HScene's coordinate space).
    */
    Rectangle bounds=getBounds();
    Dimension pixelResolution=MHPScreen.getResolution();
    return new HScreenRectangle((float)bounds.x/((float)pixelResolution.width),
                                 (float)bounds.y/((float)pixelResolution.height),
                                 (float)bounds.width/((float)pixelResolution.width),
                                 (float)bounds.height/((float)pixelResolution.height) );
  }


  // TODO:  Not implemented
  /**
   * Return an HSceneTemplate that describes this HScene.
   * !!! Only partially implemented. The piece of information stored in
   * the template is the SCENE_PIXEL_RECTANGLE.
   */      
  public HSceneTemplate getSceneTemplate() {
    HSceneTemplate template = new HSceneTemplate();

    /* Graphics configuration */

    /* Resolution */
    
    /* Scene geometry in pixels. */
    template.setPreference(HSceneTemplate.SCENE_PIXEL_DIMENSION,
            new Rectangle(this.getLocation(), this.getSize()),
            HSceneTemplate.REQUIRED);
    
    /* Scene geometry as an HScreenRectangle (display independant) */
    
    return template;

  }







   /*** HComponentOrdering ***/

//This code is duplicated from HContainer because HScene inherits java.awt.Container via MHPPlane.
//IMO here multiple inheritance would really be useful.


/*
Adds a java.awt.Component to this HContainer directly behind a previously added java.awt.Component If component has
already been added to this container,then addAfter moves component behind front If front and component are the same
component which was already added to this container,addAfter does not change the ordering of the components and returns
component This method affects the Z-order of the java.awt.Component children within the HContainer , and may also
implicitly change the numeric ordering of those children. Speci  ed By: addAfter(Component, Component)in interface
HComponentOrdering Parameters: component -is the java.awt.Component to be added to the HContainer front -is the
java.awt.Component which component will be placed behind,i.e.front will be directly in front of the added
java.awt.Component Returns:If the java.awt.Component is successfully added,then it will be returned from this call.If
the java.awt.Component is not successfully added,e.g.front is not a java.awt.Component currently added to the HContainer
,then null will be returned. This method must be implemented in a thread safe
manner. */
public synchronized java.awt.Component addAfter(java.awt.Component component, java.awt.Component
front) {
    add(component);
    if( pushBehind(component, front) == false ) {
      remove(component);
      return null;
    } else {
      return component;
    }

}

/*
Adds a java.awt.Component to this HContainer directly in front of a previously added java.awt.Component If component has
already been added to this container,then addBefore moves component in front of behind If behind and component are the
same component which was already added to this container,addBefore does not change the ordering of the components and
returns component This method affects the Z-order of the java.awt.Component children within the HContainer , and may
also implicitly change the numeric ordering of those children. Speci  ed By: addBefore(Component, Component)in interface
HComponentOrdering Parameters: component -is the java.awt.Component to be added to the HContainer behind -is the
java.awt.Component which component will be placed in front of,i.e. behind will be directly behind the added
java.awt.Component Returns: If the java.awt.Component is successfully added,then it will be returned from this call.If the
java.awt.Component is not successfully added,e.g.behind is not a java.awt.Component currently added to the HContainer
,then null will be returned. This method must be implemented in a thread safe 
manner.*/
public synchronized java.awt.Component addBefore(java.awt.Component component, java.awt.Component
behind) {
   add(component);
   if( popInFrontOf(component, behind) == false ) {
      remove(component);
      return null;
   } else {
      return component;
   }

}

/*
Moves the speci  ed java.awt.Component one component nearer in the Z-order,i.e.wapping it with the java.awt.Component
that was directly in front of it. If component is already at the front of the Z-order,the order is unchanged and pop
returns true Speci  ed By: pop(Component)in interface HComponentOrdering Parameters: component -The java.awt.Component
to be moved. Returns: returns true on success,false on failure,for example if the java.awt.Component has yet to be added
to the HContainer . */
public synchronized boolean pop(java.awt.Component component) {

    int index = getComponentIndex(component);
    if(index==-1) {
      return false;
    }

    /* If already first, do nothing */
    if(index==0) {
      return true;
    }

    remove(component);
    add(component, index-1);
    return true;
}

/*
Puts the speci  ed java.awt.Component in front of another java.awt.Component in the Z-order of this HContainer . If move
and behind are the same component which has been added to the container popInFront does not change the Z-order and
returns true Speci  ed By: popInFrontOf(Component, Component)in interface HComponentOrdering Parameters: move -The
java.awt.Component to be moved directly in front of the "behind"Component in the Z-order of this HContainer . behind
-The java.awt.Component which the "move"Component should be placed directly in front of. Returns: returns true on
success,false on failure,for example when either java.awt.Component has yet to be added to the HContainer .If this
method fails,the Z-order is unchanged. */
public synchronized boolean popInFrontOf(java.awt.Component move, java.awt.Component behind) {

    /* Step one: locate move and behind */
    int moveIndex = getComponentIndex(move);
    int behindIndex = getComponentIndex(behind);

    if( (moveIndex==-1) || (behindIndex==-1) ) {
      /* Not found */
      return false;
    }
    remove(move);
    /* WATCH OUT HERE: behindIndex may have changed !!! */
    behindIndex = getComponentIndex(behind);
    add(move, behindIndex);
    return true;

}

/*
Brings the speci  ed java.awt.Component to the "front"of the Z-order in this HContainer . If component is already at the
front of the Z-order,the order is unchanged and popToFront returns true Speci  ed By: popToFront(Component)in interface
HComponentOrdering Parameters: component -The java.awt.Component to bring to the "front"of the Z-order of this
HContainer . Returns: returns true on success,false on failure,for example when the java.awt.Component has yet to be
added to the HContainer .If this method fails,the Z-order is unchanged. */
public synchronized boolean popToFront(java.awt.Component component) {
    int index = getComponentIndex(component);

    if(index==-1) {
      return false;
    }

    remove(component);
    add(component, 0);
    return true;
}

/*
Moves the speci  ed java.awt.Component one component further away in the Z-order,i.e. wapping it with the
java.awt.Component that was directly behind it. If component is already at the back of the Z-order,the order is
unchanged and push returns true Speci  ed By: push(Component)in interface HComponentOrdering Parameters: component -The
java.awt.Component to be moved. Returns: returns true on success,false on failure,for example if the java.awt.Component
has yet to be added to the HContainer . */
public synchronized boolean push(java.awt.Component component) {

    int index = getComponentIndex(component);
    if(index==-1) {
      return false;
    }

    /* If already last, do nothing */
    if( index== (getComponentCount()-1) ) {
      return true;
    }

    remove(component);
    add(component, index+1);
    return true;
}

/*
Puts the speci  ed java.awt.Component behind another java.awt.Component in the Z-order of this HContainer . If move and
front are the same component which has been added to the container pushBehind does not change the Z-order and returns
true Speci  ed By: pushBehind(Component, Component)in interface HComponentOrdering Parameters: move -The
java.awt.Component to be moved directly behind the "front"Component in the Z- order of this HContainer . front -The
java.awt.Component which the "move"Component should be placed directly behind. Returns: returns true on success,false on
failure,for example when either java.awt.Component has yet to be added to the HContainer
. */
public synchronized boolean pushBehind(java.awt.Component move, java.awt.Component front) {

    /* Step one: locate move and front */
    int moveIndex = getComponentIndex(move);
    int frontIndex = getComponentIndex(front);

    if( (moveIndex==-1) || (frontIndex==-1) ) {
      /* Not found */
      return false;
    }
    remove(move);
    frontIndex = getComponentIndex(front);
    add(move, frontIndex+1);
    return true;
}

/*
Place the speci  ed java.awt.Component at the "back"of the Z-order in this HContainer . If component is already at the
back the Z-order is unchanged and pushToBack returns true Speci  ed By: pushToBack(Component)in interface
HComponentOrdering Parameters: component -The java.awt.Component to place at the "back"of the Z-order of this HContainer
. Returns: returns true on success,false on failure,for example when the java.awt.Component has yet to be added to the
HContainer .If the component was not added to the container pushToBack does not change the 
Z-order. */
public synchronized boolean pushToBack(java.awt.Component component) {

    int index = getComponentIndex(component);

    if(index==-1) {
      return false;
    }

    remove(component);
    add(component, -1);
    return true;
}
  /** Utility method to return the numeric index of a component within
      this container.
      @param component component to locate
      @return index of <code>component</code> in the internal Component array
              or -1 if not found
  */
private int getComponentIndex(Component component) {
    Component[] current = getComponents();
    int i;

    for(i=0; i < current.length; i++) {
      if(current[i]==component) {
        break;
      }
    }
    if(i==current.length) {
      /* Not found */
      return -1;
    } else {
      return i;
    }
  }



}
