/*
 * NIST/DASE API Reference Implementation
 * $File: HGraphicButton.java $
 * Last changed on $Date: 2001/06/15 21:18:45 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Image;
import java.util.Vector;
import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import org.havi.ui.event.HActionEvent;
import org.havi.ui.event.HActionListener;
import org.havi.ui.HEventMulticaster;

/**
 * See (C) official HaVi documentation for reference
 * <p>
 * HGraphicButton is a widget that provides a basic button with a bitmap.
 * This HVisible extends HIcon to make it actionable.
 * It has three states: NORMAL_STATE and FOCUSED_STATE and ACTIONED_STATE
 * <p>Revision information:<br>
 * $Revision: 1.4 $
 *
 */

public class HGraphicButton extends HIcon
  implements HActionable, KeyListener, MouseListener {

  /* Inherited for Component: x, y, width, height, font
     Inherited from HVisible: tlm default to DefaultTextLayoutManager
                              state, hlook
     Inherited from HStaticIcon: defaultLook.
     Inherited from HIcon: sounds for FOCUS and NORMAL. focus state.
     Graphic content is stored with setGraphicContent from HVisible */

  /** Sound to play when this component is actionned. Default = none */ 
  protected HSound actionSound = null;

  /** Constructor with no parameters. Delegate to parent */
  public HGraphicButton() {
    super();
    initialize();
  }
    

  /** Constructor with initial image and geometry.
      @param image image to associate with all interaction states
      @param x initial horizontal position (subject to layout)
      @param y initial vertical position (subject to layout)
      @param width initial width (subject to layout)
      @param height initial height (subject to layout) */
  public HGraphicButton(Image image,
                        int x, int y, int width, int height) {
    super(image, x, y, width, height);
    initialize();
    /* By default, the image for ACTIONED_STATE is the same */
    setGraphicContent(image, HState.ACTIONED_STATE);
  }

  /** Constructor with images for each interaction state a initial geometry
      @param imageNormal image to associate with NORMAL_STATE
      @param imageFocused image to associate with FOCUSED_STATE
      @param imageActioned image to associate with ACTIONED_STATE
      @param x initial horizontal position (subject to layout)
      @param y initial vertical position (subject to layout)
      @param width initial width (subject to layout)
      @param height initial height (subject to layout) */
  public HGraphicButton(Image imageNormal,
                        Image imageFocused,
                        Image imageActioned,
                        int x, int y, int width, int height) {
    super(imageNormal, x, y, width, height);
    initialize();
    setGraphicContent(imageFocused, HState.FOCUSED_STATE);
    setGraphicContent(imageActioned, HState.ACTIONED_STATE);
  }


  /** Constructor with graphic content.
      @param image image to associate with all interaction states */
  public HGraphicButton(Image image) {
    super(image);
    initialize();
    /* By default, the image for ACTIONED_STATE is the same */
    setGraphicContent(image, HState.ACTIONED_STATE);
  }


  /** Constructor with images for each interaction state
      @param imageNormal image to associate with NORMAL_STATE
      @param imageFocused image to associate with FOCUSED_STATE
      @param imageActioned image to associate with ACTIONED_STATE */
  public HGraphicButton(Image imageNormal,
                        Image imageFocused,
                        Image imageActioned) {
    super(imageNormal);
    initialize();
    setGraphicContent(imageFocused, HState.FOCUSED_STATE);
    setGraphicContent(imageActioned, HState.ACTIONED_STATE);
  }

  /** Set defaults and register listeners */
  private void initialize() {
    try {
      setLook(getDefaultLook());
    } catch(HInvalidLookException e) {
      // Just ignore, this cannot happen anyway
    }
    registerListeners();
  }
  
  // Inherited from HIcon
  // public void setMove(int keyCode,  HNavigable target)

  // Inherited from HIcon
  // public HNavigable getMove(int keyCode)

  // Inherited from HIcon
  // public void setFocusTraversal(HNavigable up, ...)

  // Inherited from HIcon
  // public boolean isSelected()

  // Inherited from HIcon
  // public boolean isFocusTraversable()



  // Inherited from HIcon
  // public void setGainFocusSound(HSound sound)
  // public void setLoseFocusSound(HSound sound)
  // public HSound getGainFocusSound()
  // public HSound getLoseFocusSound()

    private HActionListener actionListeners = null;
  
  /** register a standard action listener to receive ActionEvents from
      this component.
      @param l action listener to add
  */
  public void addHActionListener(HActionListener l) {
    actionListeners = HEventMulticaster.add(actionListeners, l);        
  }

  /** Remove a previously registered action listener.
      If not present, return silently.
      @param l listener to be removed */
  public void removeHActionListener(HActionListener l) {
    actionListeners = HEventMulticaster.remove(actionListeners, l);    
  }

public void processHActionEvent(org.havi.ui.event.HActionEvent evt) {
   setActionCommand(evt.getActionCommand());
}


  /** action command name. See setActionCommand() */
  private String actionCommandName = "ACTION";
  
  /** Set the name of the command that will be reported in the ActionEvent
      @param command name of the command */
  public void setActionCommand(String command) {
    actionCommandName = command;
  }

  /** Return the name of the command that is reported in  ActionEvent */
  public String getActionCommand() {
    return actionCommandName;
  }


  /** Associate an HSound to play when the widget is actionned.
      Note that the specs make no guarantee that the sound will be played
      completely (or at all for HSounds if it hasn't fully loaded yet).
      @param sound new sound
  */
  public void setActionSound(HSound sound) {
    this.actionSound = sound;
  }

  /** Return the current sound associated with action event */
  public HSound getActionSound() {
    return this.actionSound;
  }


  /** Default look for all HGraphicButton widgets */
  private static HGraphicLook defaultLook =
    new HGraphicLook(HGraphicLook.BUTTON_DECORATION);
  
  /** Set a new default look for the HGraphicButton class.
      Note that this is not inherited directly from HIcon because
      this default should be local to HGraphicButton, not to its parent.
      @param hlook new look
      @exception HInvalidLookException if the new look is not a valid HLook
  */
  public static void setDefaultLook(HGraphicLook hlook)
    throws HInvalidLookException {
    if ( hlook instanceof HGraphicLook ) {  // silly...
      defaultLook = hlook;
    } else {
      throw new HInvalidLookException(
"New look is not an HGraphicLook and would not render HGraphicButton properly");
    }
  }

  /** Return the current default look for this class
      @return default look */
  public static HGraphicLook getDefaultLook() {
    return defaultLook;
  }




  /* ******** Implementation methods (not part of the API  ******** */

  /** Register to receive Mouse and Key events */
  private void registerListeners() {
    this.addMouseListener(this);
    this.addKeyListener(this);
  }

  
  /* ******** KeyListener interface  ******** */

  /** Process key events */
  public void keyTyped(KeyEvent e) {
    super.keyTyped(e);
  }

  /** Process KeyPress events (from KeyListener interface) -- ignored */
  public void keyPressed(KeyEvent e) {
    super.keyPressed(e);
    switch(e.getKeyCode()) {
    case KeyEvent.VK_ENTER:
    case KeyEvent.VK_SPACE:
      /* To avoid flickering */
      if(getInteractionState() != HState.ACTIONED_STATE) {
        setInteractionState(HState.ACTIONED_STATE);
      }
      break;
    }
  }

  /** Process KeyRelease events (from KeyListener interface) -- ignored */
  public void keyReleased(KeyEvent e){
    super.keyReleased(e);
    switch(e.getKeyCode()) {
    case KeyEvent.VK_ENTER:
    case KeyEvent.VK_SPACE:
      /* Multiple events can be fired for the same "action" */
      if(getInteractionState() != HState.FOCUSED_STATE) {
        setInteractionState(HState.FOCUSED_STATE);
        doAction();
      }
      break;
    }
  }
  

  /* ******** MouseListener interface ******** */
  
  public void mouseClicked(MouseEvent e) {
    super.mouseClicked(e);
  }

  public void mouseEntered(MouseEvent e)  {
    super.mouseEntered(e);
  }

  public void mouseExited(MouseEvent e)  {
    super.mouseExited(e);
  }

  public void mousePressed(MouseEvent e)  {
    super.mousePressed(e);
    setInteractionState(HState.ACTIONED_STATE);
    System.out.println(e);
  }

  public void mouseReleased(MouseEvent e)  {
    super.mouseReleased(e);
    setInteractionState(HState.FOCUSED_STATE);
    doAction();
    System.out.println(e);
  }

  /** Event sequence number */
  private int sequence = 0;

  /** Fire the action events */
  private void doAction() {
    if(actionSound != null) {
      actionSound.play();
    }
    if (actionListeners != null) {
      actionListeners.actionPerformed(new ActionEvent(this, sequence++,
                                                      actionCommandName));
    }
  }
}
  
