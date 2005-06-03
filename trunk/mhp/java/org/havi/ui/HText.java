/*
 * NIST/DASE API Reference Implementation
 * $File: HText.java $
 * Last changed on $Date: 2001/05/25 21:08:35 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;

/**
 * See (C) official HaVi documentation for reference
 * <p>
 * Text is a widget that provides basic text display (aka label).
 * This HVisible extends HStaticText but is focus traversible.
 * It has two states: NORMAL_STATE and FOCUSED_STATE.
 * <p>Revision information:<br>
 * $Revision: 1.4 $
 *
 */

//TODO: I assume processHFocusEvent should do something
 
 
public class HText extends HStaticText
  implements HNavigable, FocusListener, KeyListener {

  /* Inherited for Component: x, y, width, height, font
     Inherited from HVisible: tlm default to DefaultTextLayoutManager
                              state, hlook
     Inherited from HStaticText: defaultLook.
     Text is stored with setTextContent from HVisible */

  /** Sound to play when this component gaines focus. Default = none */ 
  protected HSound gainFocusSound = null;
  /** Sound to play when this component loses focus. Default = none */ 
  protected HSound loseFocusSound = null;

  /** Constructor with no parameters. Delegate to parent */
  public HText() {
    super();
    initialize();
  }

  /** Constructor with initial text and geometry. Handled by parent */
  public HText(String text,
               int x, int y, int width, int height) {
    super(text, x, y, width, height);
    initialize();
    /* By default, the text for FOCUSED_STATE is the same */
    setTextContent(text, HState.FOCUSED_STATE);
  }


  /** Constructor with initial text and geometry and rendering options.
      Handled by parent */
  public HText(String text,
               int x, int y, int width, int height,
               Font font, Color foreground, Color background,
               HTextLayoutManager tlm) {
    super(text, x, y, width, height, font, foreground, background, tlm);
    initialize();
    setTextContent(text, HState.FOCUSED_STATE);
  }

  /** Constructor with initial text. Handled by parent */
  public HText(String text) {
    super(text);
    initialize();
    setTextContent(text, HState.FOCUSED_STATE);
  }

  
  /** Constructor with initial text and rendering options.
      Handled by parent */
  public HText(String text,
               Font font, Color foreground, Color background,
               HTextLayoutManager tlm) {
    super(text, font, foreground, background, tlm);
    initialize();
    setTextContent(text, HState.FOCUSED_STATE);
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

  
  /** Default look for all HText widgets */
  private static HTextLook defaultLook
    = new HTextLook(HTextLook.LABEL_DECORATION);
  
  /** Set a new default look for the HText class.
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
"New look is not an HTextLook and would not render HText properly");
    }
  }

  /** Return the current default look for this class
      @return default look */
  public static HTextLook getDefaultLook() {
    return defaultLook;
  }

  
  /* ** HNavigationInputPreferred Interface ** */
  
public int[] getNavigationKeys() {
   int[] ret=new int[4];
   ret[0]=KeyEvent.VK_UP;
   ret[1]=KeyEvent.VK_DOWN;
   ret[2]=KeyEvent.VK_LEFT;
   ret[3]=KeyEvent.VK_RIGHT;
   return ret;
}

public void processHFocusEvent(org.havi.ui.event.HFocusEvent evt) {
   //this is done by other listeners?
}


  /* *****************     HNavigable interface ****************** */

  /** Navigation map for this component */
  HashMap navigationMap = new HashMap();
  
  /** Map navigation keyCodes to another widget.
      @param keyCode VK event to map.
      @param target target HNavigable
  */
  public void setMove(int keyCode,
                      HNavigable target) {
    navigationMap.put(new Integer(keyCode), target);
  }

  /** Return the current target HNavigable associated with a navigation event
      @param keyCode VK event.
  */
  public HNavigable getMove(int keyCode) {
    return (HNavigable)navigationMap.get(new Integer(keyCode));
  }

  /** Utility method to map all four basic navigation events all at once.
      @param up Target for VK_UP
      @param down Target for VK_DOWN
      @param left Target for VK_LEFT
      @param right Target for VK_RIGHT */
  public void setFocusTraversal(HNavigable up,
                                HNavigable down,
                                HNavigable left,
                                HNavigable right) {

    setMove(KeyEvent.VK_UP, up);
    setMove(KeyEvent.VK_DOWN, down);
    setMove(KeyEvent.VK_LEFT, left);
    setMove(KeyEvent.VK_RIGHT, right);

  }


  /** Current focus state */
  private boolean hasFocus = false;

  /** Current focus state
      @return true if component has focus */
  public boolean isSelected() {
    return hasFocus;
  }

  /** Associate an HSound to play when the widget gains focus.
      Note that the specs make no guarantee that the sound will be played
      completely (or at all for HSounds if it hasn't fully loaded yet).
      In particular, the sound must stop as soon as the component looses
      focus.
      @param sound new sound
  */
  public void setGainFocusSound(HSound sound) {
    this.gainFocusSound = sound;
  }

  /** Associate an HSound to play when the widget gains focus.
      Note that the specs make no guarantee that the sound will be played
      completely (or at all for HSounds if it hasn't fully loaded yet).
      @param sound new sound
  */
  public void setLoseFocusSound(HSound sound) {
    this.loseFocusSound = sound;
  }

  /** Return the current sound associated with FOCUS_GAINED */
  public HSound getGainFocusSound() {
    return this.gainFocusSound;
  }

  /** Return the current sound associated with FOCUS_LOST */
  public HSound getLoseFocusSound() {
    return this.loseFocusSound;
  }


  /** Delegated to the underlying AWT Component implementation */
  public void requestFocus() {
    super.requestFocus();
  }

  /** True by default for HNavigable */
  public boolean isFocusTraversable() {
    return true;
  }


  /* ******** Actual implementation of the navigation scheme ******** */

  /** Register to receive Focus and Key events */
  private void registerListeners() {
    this.addFocusListener(this);
    this.addKeyListener(this);
    requestFocus();
  }

  /** Process FOCUS_GAINED events (from FocusListener interface) */
  public void focusGained(FocusEvent e) {
    this.hasFocus = true;
    setInteractionState(HState.FOCUSED_STATE);
    if(gainFocusSound != null) {
      gainFocusSound.play();
    }
  }

  /** Process FOCUS_LOST events (from FocusListener interface) */
  public void focusLost(FocusEvent e) {
    this.hasFocus = false;
    setInteractionState(HState.NORMAL_STATE);
    if(loseFocusSound != null) {
      loseFocusSound.play();
    }
  }

  /** Process key events (from KeyListener interface) in search of
      navigation events (VK_UP,DOWN,etc.) */
  public void keyPressed(KeyEvent e) {
    Integer keyCode = new Integer(e.getKeyCode());

    // System.out.println("Keyboard event: " + e);

    Object o;
    if ( (o=navigationMap.get(keyCode)) != null ) {
      //ATSC/NIST had the method requestFocus() in HNavigable, MHP has not.
      //I do not want it to HNavigable because of possible AbstractMethodErrors.
      //So do it this way. Well, there won't be HNavigables which do not inherit Component...
      if (o instanceof java.awt.Component)
         ((java.awt.Component)o).requestFocus();
      //((HNavigable)o).requestFocus();
    } else {
      /* If no navigation bindings have been declared, fall back to the
         default scheme: down and right mean next, up and left mean previous */
      // TODO: I don't know of a way to access the previous component.
      // For now, alway go forward
      if( (e.getKeyCode() == KeyEvent.VK_UP) ||
          (e.getKeyCode() == KeyEvent.VK_DOWN) ||
          (e.getKeyCode() == KeyEvent.VK_LEFT) ||
          (e.getKeyCode() == KeyEvent.VK_RIGHT) ) {
        // System.out.println("Transfering focus to next component");
        this.transferFocus();
      }
    }

    
  }

  /** Process KeyPress events (from KeyListener interface) -- ignored */
  public void keyTyped(KeyEvent e) {
    // System.out.println(e);
  }

  /** Process KeyRelease events (from KeyListener interface) -- ignored */
  public void keyReleased(KeyEvent e){
    // System.out.println(e);
  }
  

  public void mouseClicked(MouseEvent e) {
    // System.out.println(e);
  }

  public void mouseEntered(MouseEvent e)  {
    // System.out.println(e);
  }

  public void mouseExited(MouseEvent e)  {
    // System.out.println(e);
  }

  public void mousePressed(MouseEvent e)  {
    // System.out.println(e);
  }

  public void mouseReleased(MouseEvent e)  {
    // System.out.println(e);
  }

}
