
package org.havi.ui;

import java.awt.Container;
import java.awt.Component;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.FlowLayout;
import org.havi.ui.event.HMatteListener;
import org.havi.ui.event.HMatteEvent;
import java.awt.image.BufferedImage;

/*The HContainer class extends the java.awt.Container class by implementing the HMatteLayer 
interface and providing additional Z-ordering capabilities,which are required since 
components in the HAVi user-interface are explicitly allowed to overlap each other. Note 
that these Z-ordering capabilities (addBefore, addAfter, pop, popInFrontOf, popToFront, 
push, pushBehind and pushToBack must be implemented by (implicitly) reordering the child 
Components within the HContainer ,so that the standard AWT convention that the Z-order is 
de  ned as the order in which Components are added to a given Container is maintained.For 
example,one implementation of popToFront might be to make the speci  ed Component become 
the  rst Component added to the parent Container by removing all Components from that 
Container,adding the speci  ed Container  rst,and then adding the remaining Components in 
their current relative order to that Container.The parameters to the constructors are as 
follows,in cases where parameters are not used,then the constructor should use the default 
values. */

public class HContainer extends java.awt.Container implements HMatteLayer, HMatteListener, HComponentOrdering, org.dvb.ui.TestOpacity {

HMatte matte;
boolean grouped;

  /** Default matte. By default, HContainers are transparent. */
  //private static final HMatte DEFAULT_HCONTAINER_MATTE = new HFlatMatte(0.0F);
  /* Static default layout */
  private static final LayoutManager defaultLayoutManager = new NullLayout();
  
  /** Constructor with no argument.
      Note that matte is initialized to an transparent FlatMatte.
      As required by the HAVi specs, the default layout is null
      (as opposed to FlowLayout for most AWT containers. */
/*
Creates an HContainer object.See the class description for details of constructor parameters and default
values. */
public HContainer() {
    // WARNING setting a null layout prevents the container from laying
    // itself out properly (esp. in terms of preferred/minimum sizes).
    // For now, break the API by using FlowLayout.
    // TODO: In the future, create a no-layout layout
    // setLayout(null);
   this.setMatte(null);
   grouped=false;
   setLayout(defaultLayoutManager);
}

/*
Creates an HContainer object.See the class description for details of constructor parameters and default
values. */
public HContainer(int x, int y, int width, int height) {
   this();
   setBounds(x,y,width, height);
}


/*
Get any HMatte currently associated with this component. Speci  ed By: getMatte()in interface HMatteLayer Returns: the
HMatte currently associated with this component or null if there is no associated
matte. */
public HMatte getMatte() {
   return matte;
}

/*
Groups the HContainer and its components.If the container is already grouped this method has no effect See Also: 
ungroup(),isGrouped() */
public void group() {
   grouped=true;
}

/*
Returns true if all the drawing done during the update and paint methods for this speci  c HContainer object is 
automatically double buffered. Overrides: java.awt.Component.isDoubleBuffered()in class java.awt.Component Returns: true 
if all the drawing done during the update and paint methods for this speci  c HComponent object is automatically double 
buffered,or false if drawing is not double buffered.The default value for the double buffering setting is platform-speci 
 c. */
public boolean isDoubleBuffered() {
   return true; //implemented in this.paint
}

/*
Tests whether the HContainer and its components are grouped.By default the container is not grouped with its components. 
Returns: returns true if the HContainer and its components are grouped,false otherwise. See Also: 
group(),ungroup() */
public boolean isGrouped() {
   return grouped;
}

/*
Returns true if the entire HContainer area,as given by the java.awt.Component#getBounds method,is fully opaque,i.e.its
paint method (or surrogate methods)guarantee that all pixels are painted in an opaque Color By default,the return value
is false The return value should be overridden by subclasses that can guarantee full opacity.The consequences of an
invalid overridden value are implementation speci  c. Speci  ed By: isOpaque()in interface TestOpacity Returns: true if
all the pixels within the area given by the java.awt.Component#getBounds method are fully opaque,i.e.its paint method
(or surrogate methods)guarantee that all pixels are painted in an opaque Color,otherwise
false */
public boolean isOpaque() {
   return false;
}


/*
Applies an HMatte to this component,for matte compositing.Any existing animated matte must be stopped before this method 
is called or an HMatteException will be thrown. Speci  ed By: setMatte(HMatte)in interface HMatteLayer Parameters: m 
-The HMatte to be applied to this component --note that only one matte may be associated with the component,thus any 
previous matte will be replaced.If m is null,then any matte associated with the component is removed and further calls 
to getMatte()shall return null.The component shall behave as if it had a fully opaque HFlatMatte associated with it (i.e 
an HFlatMatte with the default value of 1.0.) Throws: HMatteException -if the HMatte cannot be associated with the 
component.This can occur:" if the specific matte type is not supported " if the platform does not support any matte type 
" if the component is associated with an already running HFlatEffectMatte or HImageEffectMatte . The exception is thrown 
even if m is null. See Also: HMatte */
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

  /* **************** HMatteListener Interface ****************** */

  /** Receive an HMatte Event **/
  public void matteUpdate(HMatteEvent e) {
    if(this.isShowing()) {
      this.repaint();
    }
  }

/*
Ungroups the HContainer and its components.If the container is already ungrouped,this method has no effect. See Also: 
group(),isGrouped() */
public void ungroup() {
   grouped=false;
}


  /** Overrides the standard Component update method:
      <ul><li>Do not clear the background.
      @param g Graphics to use */
  public void update(Graphics g) {

    this.paint(g);    
    
  }

  public void paint(Graphics g) {
    org.dvb.ui.DVBGraphics dvbG=(org.dvb.ui.DVBGraphics)g;
    dvbG.enterBuffered();
    if (grouped) {
       super.paint(g);
       if (getMatte() != null)
         getMatte().compose((org.dvb.ui.DVBGraphics)dvbG.create(), getBounds() );
    } else {
       if (getMatte() != null)
          getMatte().compose((org.dvb.ui.DVBGraphics)dvbG.create(), getBounds() );
       super.paint(g);
    }
    dvbG.leaveBuffered();
  
//old NIST Java implementation
    /* Create a copy of the Graphics context in which regular rendering
       will take place. This will avoid flickering as well. */
    /*BufferedImage  offscreen
      = new BufferedImage(this.width, this.height,
                          BufferedImage.TYPE_INT_ARGB);
    Graphics off = offscreen.getGraphics();

    if (off != null) {
      try {
        off.setClip(g.getClip());
        
        off.setColor(getBackground());
        off.fillRect(0,0, this.width, this.height);
        off.setColor(getForeground());
        
        // Now perform the composition.
        // If the container is grouped, apply the matte to itself and all
        // rendered subcomponents, other only to the areas exposed 
        if(grouped) {
          super.paint(off);
          getMatte().compose(offscreen, (g.getClip()).getBounds() );
        } else {
          getMatte().compose(offscreen, (g.getClip()).getBounds() );
          super.paint(off);
        }
        
        //HToolkit.showBorder: this was a public static variable, default=false
        if(false ) {
          off.setColor(Color.green);
          off.drawRect(0, 0, this.width, this.height);
        }
        
        g.drawImage(offscreen, 0, 0, this);
      } finally {
        off.dispose();
      }
    }*/
  }




   /*** HComponentOrdering ***/

//This code is duplicated in HContainer!


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
