/*
 * NIST/DASE API Reference Implementation
 * $File: HTextButton.java $
 * Last changed on $Date: 2001/06/15 21:21:26 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Color;
import java.awt.Font;
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
 * Text is a widget that provides basic text-based buttons.
 * This HVisible extends HText to make it actionable.
 * It has three states: NORMAL_STATE and FOCUSED_STATE and ACTIONED_STATE
 * <p>Revision information:<br>
 * $Revision: 1.6 $
 *
 */


public class HTextButton extends HText
  implements HActionable, HActionInputPreferred, KeyListener, MouseListener {

  /* Inherited for Component: x, y, width, height, font
     Inherited from HVisible: tlm default to DefaultTextLayoutManager
                              state, hlook
     Inherited from HStaticText: defaultLook.
     Inherited from HText: sounds for FOCUS and NORMAL. focus state.
     Text is stored with setTextContent from HVisible */

  /** Sound to play when this component is actionned. Default = none */ 
  protected HSound actionSound = null;

  /** Constructor with no parameters. Delegate to parent */
  public HTextButton() {
    super();
    initialize();
  }
  

  /** Constructor with initial text and geometry. Handled by parent */
  public HTextButton(String text,
               int x, int y, int width, int height) {
    super(text, x, y, width, height);
    initialize();
    /* By default, the text for ACTIONED_STATE is the same */
    setTextContent(text, HState.ACTIONED_STATE);
  }


  /** Constructor with initial text and geometry and rendering options.
      Handled by parent */
  public HTextButton(String text,
               int x, int y, int width, int height,
               Font font, Color foreground, Color background,
               HTextLayoutManager tlm) {
    super(text, x, y, width, height, font, foreground, background, tlm);
    initialize();
    setTextContent(text, HState.ACTIONED_STATE);
  }

  /** Constructor with initial text. Handled by parent */
  public HTextButton(String text) {
    super(text);
    initialize();
    setTextContent(text, HState.ACTIONED_STATE);
  }

  
  /** Constructor with initial text and rendering options.
      Handled by parent */
  public HTextButton(String text,
               Font font, Color foreground, Color background,
               HTextLayoutManager tlm) {
    super(text, font, foreground, background, tlm);
    initialize();
    setTextContent(text, HState.ACTIONED_STATE);
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
  

  // Inherited from HText
  // public void setMove(int keyCode,  HNavigable target)

  // Inherited from HText
  // public HNavigable getMove(int keyCode)

  // Inherited from HText
  // public void setFocusTraversal(HNavigable up, ...)

  // Inherited from HText
  // public boolean isSelected()

  // Inherited from HText
  // public boolean isFocusTraversable()



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


  /** Default look for all HTextButton widgets */
  private static HTextLook defaultLook =
    new HTextLook(HTextLook.BUTTON_DECORATION);
  
  /** Set a new default look for the HTextButton class.
      Note that this is not inherited directly from HStaticText because
      this default should be local to HText, not to its parent.
      @param hlook new look
      @exception HInvalidLookException if the new look is not an HLook
  */
  public static void setDefaultLook(HTextLook hlook)
    throws HInvalidLookException {
    if ( hlook instanceof HTextLook ) {
      defaultLook = hlook;
    } else {
      throw new HInvalidLookException(
"New look is not an HTextLook and would not render HTextButton properly");
    }
  }

  /** Return the current default look for this class
      @return default look */
  public static HTextLook getDefaultLook() {
    return defaultLook;
  }


  /* ******** Actual implementation  ******** */

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
    // System.out.println(e);
  }

  public void mouseEntered(MouseEvent e)  {
    super.mouseEntered(e);
    // System.out.println(e);
  }

  public void mouseExited(MouseEvent e)  {
    super.mouseExited(e);
    // System.out.println(e);
  }

  public void mousePressed(MouseEvent e)  {
    super.mousePressed(e);
    requestFocus();
    setInteractionState(HState.ACTIONED_STATE);
    // System.out.println(e);
  }

  public void mouseReleased(MouseEvent e)  {
    super.mouseReleased(e);
    setInteractionState(HState.FOCUSED_STATE);
    doAction();
    // System.out.println(e);
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
