// Copied, constant definitions moved to KeyEventKeyConstants
// Rest is unchanged.

/* KeyEvent.java -- event for key presses
   Copyright (C) 1999, 2002, 2004, 2005  Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package java.awt.event;

import gnu.java.awt.EventModifier;

import java.awt.Component;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * This event is generated when a key is pressed or released. There are two
 * categories of key events:
 *
 * <p><em>"Key typed" events</em> are higher-level, and have already
 * compensated for modifiers and keyboard layout to generate a single Unicode
 * character. It may take several key press events to generate one key typed.
 * The <code>getKeyCode</code> method will return <code>VK_UNDEFINED</code>,
 * and <code>getKeyChar</code> will return a valid Unicode character or
 * <code>CHAR_UNDEFINED</code>.
 *
 * <p><em>"Key pressed" and "key released" events</em> are lower-level, and
 * are platform and keyboard dependent. They correspond to the actaul motion
 * on a keyboard, and return a virtual key code which labels the key that was
 * pressed. The <code>getKeyCode</code> method will return one of the
 * <code>VK_*</code> constants (except VK_UNDEFINED), and the
 * <code>getKeyChar</code> method is undefined.
 *
 * <p>Some keys do not generate key typed events, such as the F1 or HELP keys.
 * Not all keyboards can generate all virtual keys, and no attempt is made to
 * simulate the ones that can't be typed. Virtual keys correspond to the
 * keyboard layout, so for example, VK_Q in English is VK_A in French. Also,
 * there are some additional virtual keys to ease handling of actions, such
 * as VK_ALL_CANDIDATES in place of ALT+VK_CONVERT. Do not rely on the value
 * of the VK_* constants, except for VK_ENTER, VK_BACK_SPACE, and VK_TAB.
 *
 * @author Aaron M. Renn (arenn@urbanophile.com)
 * @author Eric Blake (ebb9@email.byu.edu)
 * @see KeyAdapter
 * @see KeyListener
 * @since 1.1
 * @status updated to 1.4
 */
public class KeyEvent extends InputEvent implements KeyConstants
{
  /**
   * Compatible with JDK 1.1+.
   */
  private static final long serialVersionUID = -2352130953028126954L;


  /**
   * The code assigned to the physical keyboard location (as adjusted by the
   * keyboard layout). Use the symbolic VK_* names instead of numbers.
   *
   * @see #getKeyCode()
   * @serial the VK_ code for this key
  */
  private int keyCode;

  /**
   * The Unicode character produced by the key type event. This has no meaning
   * for key pressed and key released events.
   *
   * @see #getKeyChar()
   * @serial the Unicode value for this key
   */
  private char keyChar;

  /**
   * The keyboard location of the key. One of {@link #KEY_LOCATION_UNKNOWN},
   * {@link #KEY_LOCATION_STANDARD}, {@link #KEY_LOCATION_LEFT},
   * {@link #KEY_LOCATION_RIGHT}, or {@link #KEY_LOCATION_NUMPAD}.
   *
   * @see #getKeyLocation()
   * @serial the key location
   * @since 1.4
   */
  private final int keyLocation;

  /**
   * Stores the state of the native event dispatching system, to correctly
   * dispatch in Component#dispatchEventImpl when a proxy is active.
   *
   * XXX Does this matter in Classpath?
   *
   * @serial whether the proxy is active
   */
  private boolean isProxyActive;


  /**
   * Initializes a new instance of <code>KeyEvent</code> with the specified
   * information. Note that an invalid id leads to unspecified results.
   *
   * @param source the component that generated this event
   * @param id the event id
   * @param when the timestamp when the even occurred
   * @param modifiers the modifier keys during the event, in old or new style
   * @param keyCode the integer constant for the virtual key type
   * @param keyChar the Unicode value of the key
   * @param keyLocation the location of the key
   * @throws IllegalArgumentException if source is null, if keyLocation is
   *         invalid, or if (id == KEY_TYPED && (keyCode != VK_UNDEFINED
   *         || keyChar == CHAR_UNDEFINED))
   */
  public KeyEvent(Component source, int id, long when, int modifiers,
                  int keyCode, char keyChar, int keyLocation)
  {
    super(source, id, when, modifiers);
    this.keyCode = keyCode;
    this.keyChar = keyChar;
    this.keyLocation = keyLocation;
    if ((id == KEY_TYPED && (keyCode != VK_UNDEFINED
                             || keyChar == CHAR_UNDEFINED))
        || keyLocation < KEY_LOCATION_UNKNOWN
        || keyLocation > KEY_LOCATION_NUMPAD)
      throw new IllegalArgumentException();
  }

  /**
   * Initializes a new instance of <code>KeyEvent</code> with the specified
   * information. Note that an invalid id leads to unspecified results.
   *
   * @param source the component that generated this event
   * @param id the event id
   * @param when the timestamp when the even occurred
   * @param modifiers the modifier keys during the event, in old or new style
   * @param keyCode the integer constant for the virtual key type
   * @param keyChar the Unicode value of the key
   * @throws IllegalArgumentException if source is null, or if
   *         (id == KEY_TYPED && (keyCode != VK_UNDEFINED
   *         || keyChar == CHAR_UNDEFINED))
   */
  public KeyEvent(Component source, int id, long when, int modifiers,
                  int keyCode, char keyChar)
  {
    this(source, id, when, modifiers, keyCode, keyChar, KEY_LOCATION_UNKNOWN);
  }

  /**
   * Initializes a new instance of <code>KeyEvent</code> with the specified
   * information. Note that an invalid id leads to unspecified results.
   *
   * @param source the component that generated this event
   * @param id the event id
   * @param when the timestamp when the even occurred
   * @param modifiers the modifier keys during the event, in old or new style
   * @param keyCode the integer constant for the virtual key type
   * @throws IllegalArgumentException if source is null, or if
   *         id == KEY_TYPED but keyCode != VK_UNDEFINED
   *
   * @deprecated
   */
  public KeyEvent(Component source, int id, long when, int modifiers,
                  int keyCode)
  {
    this(source, id, when, modifiers, keyCode, '\0', KEY_LOCATION_UNKNOWN);
  }

  /**
   * Returns the key code for the event key.  This will be one of the
   * <code>VK_*</code> constants defined in this class. If the event type is
   * KEY_TYPED, the result will be VK_UNDEFINED.
   *
   * @return the key code for this event
   */
  public int getKeyCode()
  {
    return keyCode;
  }

  /**
   * Sets the key code for this event.  This must be one of the
   * <code>VK_*</code> constants defined in this class.
   *
   * @param keyCode the new key code for this event
   */
  public void setKeyCode(int keyCode)
  {
    this.keyCode = keyCode;
  }

  /**
   * Returns the Unicode value for the event key.  This will be
   * <code>CHAR_UNDEFINED</code> if there is no Unicode equivalent for
   * this key, usually when this is a KEY_PRESSED or KEY_RELEASED event.
   *
   * @return the Unicode character for this event
   */
  public char getKeyChar()
  {
    return keyChar;
  }

  /**
   * Sets the Unicode character for this event to the specified value.
   *
   * @param keyChar the new Unicode character for this event
   */
  public void setKeyChar(char keyChar)
  {
    this.keyChar = keyChar;
  }

  /**
   * Sets the modifier keys to the specified value. This should be a union
   * of the bit mask constants from <code>InputEvent</code>. The use of this
   * method is not recommended, particularly for KEY_TYPED events, which do
   * not check if the modifiers were changed.
   *
   * @param modifiers the new modifier value, in either old or new style
   * @see InputEvent
   *
   * @deprecated
   */
  public void setModifiers(int modifiers)
  {
    this.modifiers = EventModifier.extend(modifiers);
  }

  /**
   * Returns the keyboard location of the key that generated this event. This
   * provides a way to distinguish between keys like left and right shift
   * which share a common key code. The result will be one of
   * {@link #KEY_LOCATION_UNKNOWN}, {@link #KEY_LOCATION_STANDARD},
   * {@link #KEY_LOCATION_LEFT}, {@link #KEY_LOCATION_RIGHT}, or
   * {@link #KEY_LOCATION_NUMPAD}.
   *
   * @return the key location
   * @since 1.4
   */
  public int getKeyLocation()
  {
    return keyLocation;
  }

  /**
   * Returns the text name of key code, such as "HOME", "F1", or "A".
   *
   * XXX Sun claims this can be localized via the awt.properties file - how
   * do we implement that?
   *
   * @return the text name of the key code
   */
  public static String getKeyText(int keyCode)
  {
    switch (keyCode)
      {
      case VK_CANCEL:
        return "Cancel";
      case VK_BACK_SPACE:
        return "Backspace";
      case VK_TAB:
        return "Tab";
      case VK_ENTER:
        return "Enter";
      case VK_CLEAR:
        return "Clear";
      case VK_SHIFT:
        return "Shift";
      case VK_CONTROL:
        return "Ctrl";
      case VK_ALT:
        return "Alt";
      case VK_PAUSE:
        return "Pause";
      case VK_CAPS_LOCK:
        return "Caps Lock";
      case VK_KANA:
        return "Kana";
      case VK_FINAL:
        return "Final";
      case VK_KANJI:
        return "Kanji";
      case VK_ESCAPE:
        return "Escape";
      case VK_CONVERT:
        return "Convert";
      case VK_NONCONVERT:
        return "No Convert";
      case VK_ACCEPT:
        return "Accept";
      case VK_MODECHANGE:
        return "Mode Change";
      case VK_SPACE:
        return "Space";
      case VK_PAGE_UP:
        return "Page Up";
      case VK_PAGE_DOWN:
        return "Page Down";
      case VK_END:
        return "End";
      case VK_HOME:
        return "Home";
      case VK_LEFT:
      case VK_KP_LEFT:
        return "Left";
      case VK_UP:
      case VK_KP_UP:
        return "Up";
      case VK_RIGHT:
      case VK_KP_RIGHT:
        return "Right";
      case VK_DOWN:
      case VK_KP_DOWN:
        return "Down";
      case VK_MINUS:
        return "Minus";
      case VK_MULTIPLY:
        return "NumPad *";
      case VK_ADD:
        return "NumPad +";
      case VK_SEPARATOR:
        return "NumPad ,";
      case VK_SUBTRACT:
        return "NumPad -";
      case VK_DECIMAL:
        return "NumPad .";
      case VK_DIVIDE:
        return "NumPad /";
      case VK_DELETE:
        return "Delete";
      case VK_DEAD_GRAVE:
        return "Dead Grave";
      case VK_DEAD_ACUTE:
        return "Dead Acute";
      case VK_DEAD_CIRCUMFLEX:
        return "Dead Circumflex";
      case VK_DEAD_TILDE:
        return "Dead Tilde";
      case VK_DEAD_MACRON:
        return "Dead Macron";
      case VK_DEAD_BREVE:
        return "Dead Breve";
      case VK_DEAD_ABOVEDOT:
        return "Dead Above Dot";
      case VK_DEAD_DIAERESIS:
        return "Dead Diaeresis";
      case VK_DEAD_ABOVERING:
        return "Dead Above Ring";
      case VK_DEAD_DOUBLEACUTE:
        return "Dead Double Acute";
      case VK_DEAD_CARON:
        return "Dead Caron";
      case VK_DEAD_CEDILLA:
        return "Dead Cedilla";
      case VK_DEAD_OGONEK:
        return "Dead Ogonek";
      case VK_DEAD_IOTA:
        return "Dead Iota";
      case VK_DEAD_VOICED_SOUND:
        return "Dead Voiced Sound";
      case VK_DEAD_SEMIVOICED_SOUND:
        return "Dead Semivoiced Sound";
      case VK_NUM_LOCK:
        return "Num Lock";
      case VK_SCROLL_LOCK:
        return "Scroll Lock";
      case VK_AMPERSAND:
        return "Ampersand";
      case VK_ASTERISK:
        return "Asterisk";
      case VK_QUOTEDBL:
        return "Double Quote";
      case VK_LESS:
        return "Less";
      case VK_PRINTSCREEN:
        return "Print Screen";
      case VK_INSERT:
        return "Insert";
      case VK_HELP:
        return "Help";
      case VK_META:
        return "Meta";
      case VK_GREATER:
        return "Greater";
      case VK_BRACELEFT:
        return "Left Brace";
      case VK_BRACERIGHT:
        return "Right Brace";
      case VK_BACK_QUOTE:
        return "Back Quote";
      case VK_QUOTE:
        return "Quote";
      case VK_ALPHANUMERIC:
        return "Alphanumeric";
      case VK_KATAKANA:
        return "Katakana";
      case VK_HIRAGANA:
        return "Hiragana";
      case VK_FULL_WIDTH:
        return "Full-Width";
      case VK_HALF_WIDTH:
        return "Half-Width";
      case VK_ROMAN_CHARACTERS:
        return "Roman Characters";
      case VK_ALL_CANDIDATES:
        return "All Candidates";
      case VK_PREVIOUS_CANDIDATE:
        return "Previous Candidate";
      case VK_CODE_INPUT:
        return "Code Input";
      case VK_JAPANESE_KATAKANA:
        return "Japanese Katakana";
      case VK_JAPANESE_HIRAGANA:
        return "Japanese Hiragana";
      case VK_JAPANESE_ROMAN:
        return "Japanese Roman";
      case VK_KANA_LOCK:
        return "Kana Lock";
      case VK_INPUT_METHOD_ON_OFF:
        return "Input Method On/Off";
      case VK_AT:
        return "At";
      case VK_COLON:
        return "Colon";
      case VK_CIRCUMFLEX:
        return "Circumflex";
      case VK_DOLLAR:
        return "Dollar";
      case VK_EURO_SIGN:
        return "Euro";
      case VK_EXCLAMATION_MARK:
        return "Exclamation Mark";
      case VK_INVERTED_EXCLAMATION_MARK:
        return "Inverted Exclamation Mark";
      case VK_LEFT_PARENTHESIS:
        return "Left Parenthesis";
      case VK_NUMBER_SIGN:
        return "Number Sign";
      case VK_PLUS:
        return "Plus";
      case VK_RIGHT_PARENTHESIS:
        return "Right Parenthesis";
      case VK_UNDERSCORE:
        return "Underscore";
      case VK_COMPOSE:
        return "Compose";
      case VK_ALT_GRAPH:
        return "Alt Graph";
      case VK_STOP:
        return "Stop";
      case VK_AGAIN:
        return "Again";
      case VK_PROPS:
        return "Props";
      case VK_UNDO:
        return "Undo";
      case VK_COPY:
        return "Copy";
      case VK_PASTE:
        return "Paste";
      case VK_FIND:
        return "Find";
      case VK_CUT:
        return "Cut";
      case VK_COMMA:
      case VK_PERIOD:
      case VK_SLASH:
      case VK_0:
      case VK_1:
      case VK_2:
      case VK_3:
      case VK_4:
      case VK_5:
      case VK_6:
      case VK_7:
      case VK_8:
      case VK_9:
      case VK_SEMICOLON:
      case VK_EQUALS:
      case VK_A:
      case VK_B:
      case VK_C:
      case VK_D:
      case VK_E:
      case VK_F:
      case VK_G:
      case VK_H:
      case VK_I:
      case VK_J:
      case VK_K:
      case VK_L:
      case VK_M:
      case VK_N:
      case VK_O:
      case VK_P:
      case VK_Q:
      case VK_R:
      case VK_S:
      case VK_T:
      case VK_U:
      case VK_V:
      case VK_W:
      case VK_X:
      case VK_Y:
      case VK_Z:
      case VK_OPEN_BRACKET:
      case VK_BACK_SLASH:
      case VK_CLOSE_BRACKET:
        return "" + (char) keyCode;
      case VK_NUMPAD0:
      case VK_NUMPAD1:
      case VK_NUMPAD2:
      case VK_NUMPAD3:
      case VK_NUMPAD4:
      case VK_NUMPAD5:
      case VK_NUMPAD6:
      case VK_NUMPAD7:
      case VK_NUMPAD8:
      case VK_NUMPAD9:
        return "NumPad-" + (keyCode - VK_NUMPAD0);
      case VK_F1:
      case VK_F2:
      case VK_F3:
      case VK_F4:
      case VK_F5:
      case VK_F6:
      case VK_F7:
      case VK_F8:
      case VK_F9:
      case VK_F10:
      case VK_F11:
      case VK_F12:
        return "F" + (keyCode - (VK_F1 - 1));
      case VK_F13:
      case VK_F14:
      case VK_F15:
      case VK_F16:
      case VK_F17:
      case VK_F18:
      case VK_F19:
      case VK_F20:
      case VK_F21:
      case VK_F22:
      case VK_F23:
      case VK_F24:
        return "F" + (keyCode - (VK_F13 - 13));
      default:
        // This is funky on negative numbers, but that's Sun's fault.
        return "Unknown keyCode: 0x" + (keyCode < 0 ? "-" : "")
          + Integer.toHexString(Math.abs(keyCode));
      }
  }

  /**
   * Returns a string describing the modifiers, such as "Shift" or
   * "Ctrl+Button1".
   *
   * XXX Sun claims this can be localized via the awt.properties file - how
   * do we implement that?
   *
   * @param modifiers the old-style modifiers to convert to text
   * @return a string representation of the modifiers in this bitmask
   */
  public static String getKeyModifiersText(int modifiers)
  {
    return getModifiersExText(EventModifier.extend(modifiers
                                                   & EventModifier.OLD_MASK));
  }

  /**
   * Tests whether or not this key is an action key. An action key typically
   * does not fire a KEY_TYPED event, and is not a modifier.
   *
   * @return true if this is an action key
   */
  public boolean isActionKey()
  {
    switch (keyCode)
      {
      case VK_PAUSE:
      case VK_CAPS_LOCK:
      case VK_KANA:
      case VK_FINAL:
      case VK_KANJI:
      case VK_CONVERT:
      case VK_NONCONVERT:
      case VK_ACCEPT:
      case VK_MODECHANGE:
      case VK_PAGE_UP:
      case VK_PAGE_DOWN:
      case VK_END:
      case VK_HOME:
      case VK_LEFT:
      case VK_UP:
      case VK_RIGHT:
      case VK_DOWN:
      case VK_F1:
      case VK_F2:
      case VK_F3:
      case VK_F4:
      case VK_F5:
      case VK_F6:
      case VK_F7:
      case VK_F8:
      case VK_F9:
      case VK_F10:
      case VK_F11:
      case VK_F12:
      case VK_NUM_LOCK:
      case VK_SCROLL_LOCK:
      case VK_PRINTSCREEN:
      case VK_INSERT:
      case VK_HELP:
      case VK_KP_UP:
      case VK_KP_DOWN:
      case VK_KP_LEFT:
      case VK_KP_RIGHT:
      case VK_ALPHANUMERIC:
      case VK_KATAKANA:
      case VK_HIRAGANA:
      case VK_FULL_WIDTH:
      case VK_HALF_WIDTH:
      case VK_ROMAN_CHARACTERS:
      case VK_ALL_CANDIDATES:
      case VK_PREVIOUS_CANDIDATE:
      case VK_CODE_INPUT:
      case VK_JAPANESE_KATAKANA:
      case VK_JAPANESE_HIRAGANA:
      case VK_JAPANESE_ROMAN:
      case VK_KANA_LOCK:
      case VK_INPUT_METHOD_ON_OFF:
      case VK_F13:
      case VK_F14:
      case VK_F15:
      case VK_F16:
      case VK_F17:
      case VK_F18:
      case VK_F19:
      case VK_F20:
      case VK_F21:
      case VK_F22:
      case VK_F23:
      case VK_F24:
      case VK_STOP:
      case VK_AGAIN:
      case VK_PROPS:
      case VK_UNDO:
      case VK_COPY:
      case VK_PASTE:
      case VK_FIND:
      case VK_CUT:
        return true;
      default:
        return false;
      }
  }

  /**
   * Returns a string identifying the event.  This is formatted as the
   * field name of the id type, followed by the keyCode, then the
   * keyChar, modifiers (if any), extModifiers (if any), and
   * keyLocation.
   *
   * @return a string identifying the event
   */
  public String paramString()
  {
    StringBuffer s = new StringBuffer();

    switch (id)
      {
      case KEY_PRESSED:
        s.append("KEY_PRESSED");
        break;
      case KEY_RELEASED:
        s.append("KEY_RELEASED");
        break;
      case KEY_TYPED:
        s.append("KEY_TYPED");
        break;
      default:
        s.append("unknown type");
      }

    s.append(",keyCode=").append(keyCode);

    s.append(",keyText=").append(getKeyText(keyCode));

    s.append(",keyChar=");
    if (isActionKey()
        || keyCode == VK_SHIFT
        || keyCode == VK_CONTROL
        || keyCode == VK_ALT)
      s.append("Undefined keyChar");
    else
      {
        /* This output string must be selected by examining keyChar
         * rather than keyCode, because key code information is not
         * included in KEY_TYPED events.
         */
        if (keyChar == VK_BACK_SPACE
            || keyChar == VK_TAB
            || keyChar == VK_ENTER
            || keyChar == VK_ESCAPE
            || keyChar == VK_DELETE)
          s.append(getKeyText(keyChar));
        else
          s.append("'").append(keyChar).append("'");
      }

    if ((modifiers & CONVERT_MASK) != 0)
      s.append(",modifiers=").append(getModifiersExText(modifiers
                                                        & CONVERT_MASK));
    if (modifiers != 0)
      s.append(",extModifiers=").append(getModifiersExText(modifiers));

    s.append(",keyLocation=KEY_LOCATION_");
    switch (keyLocation)
      {
      case KEY_LOCATION_UNKNOWN:
        s.append("UNKNOWN");
        break;
      case KEY_LOCATION_STANDARD:
        s.append("STANDARD");
        break;
      case KEY_LOCATION_LEFT:
        s.append("LEFT");
        break;
      case KEY_LOCATION_RIGHT:
        s.append("RIGHT");
        break;
      case KEY_LOCATION_NUMPAD:
        s.append("NUMPAD");
      }

    return s.toString();
  }

  /**
   * Reads in the object from a serial stream.
   *
   * @param s the stream to read from
   * @throws IOException if deserialization fails
   * @throws ClassNotFoundException if deserialization fails
   * @serialData default, except that the modifiers are converted to new style
   */
  private void readObject(ObjectInputStream s)
    throws IOException, ClassNotFoundException
  {
    s.defaultReadObject();
    modifiers = EventModifier.extend(modifiers);
  }
} // class KeyEvent
