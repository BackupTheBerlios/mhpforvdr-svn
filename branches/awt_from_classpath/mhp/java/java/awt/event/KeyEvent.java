package java.awt.event;

import java.awt.Component;
import java.awt.Event;

/**
 *
 * Copyright (c) 1998
 *   Transvirtual Technologies Inc.  All rights reserved.
 *
 * See the file "license.terms" for information on usage and redistribution
 * of this file.
 * @author P.C.Mehlitz
 */
public class KeyEvent
  extends InputEvent
  implements KeyConstants
{
   protected int keyCode;
   protected char keyChar;
   private static final long serialVersionUID = -2352130953028126954L;
   
   

public KeyEvent ( Component src, int evtId, long time, int mods, int kCode) {
   super( src, evtId);
   
   when = time;
   modifiers = mods;
   keyCode = kCode;
   keyChar = (char)kCode;
//   keyLocation = KEY_LOCATION_UNKNOWN;
}

public KeyEvent ( Component src, int evtId, long time, int mods, int kCode, char kChar ) {
   super( src, evtId);
   
   when = time;
   modifiers = mods;
   keyCode = kCode;
   keyChar = kChar;
//   keyLocation = KEY_LOCATION_UNKNOWN;
}

public KeyEvent ( Component src, int evtId, long time, int mods, int kCode, char kChar, int kLoc ) {
   super( src, evtId);
   
   when = time;
   modifiers = mods;
   keyCode = kCode;
   keyChar = kChar;
//   keyLocation = kLoc;
}

//not API
public static boolean isRcCode(int keyCode) {
   return (keyCode >= org.havi.ui.event.HRcEvent.RC_FIRST) && (keyCode <= org.havi.ui.event.HRcEvent.RC_LAST);
} 

public boolean isRcCode() {
   return isRcCode(keyCode);
}

public char getKeyChar() {
   return keyChar;
}

public int getKeyCode() {
   return keyCode;
}

public static String getKeyModifiersText ( int modifiers ) {
   StringBuffer sb = new StringBuffer();
   int i = 0;

   if ( (modifiers & META_MASK) != 0 ){
      i++;
      sb.append ("Meta");
   }

   if ( (modifiers & CTRL_MASK) != 0 ) {
      if ( i++ > 0 ) sb.append ('+');
      sb.append ("Ctrl");
   }

   if ( (modifiers & ALT_MASK) != 0 ) {
      if ( i++ > 0 ) sb.append ('+');
      sb.append ("Alt");
   }

   if ( (modifiers & InputEvent.SHIFT_MASK) != 0 ){
      if ( i++ > 0 ) sb.append ('+');
      sb.append ("Shift");
   }

   return sb.toString();
}

public static String getKeyText ( int keyCode ) {
   if ( (keyCode >= VK_0 && keyCode <= VK_9) || (keyCode >= VK_A && keyCode <= VK_Z) ||
       keyCode == ',' || keyCode == '.' || keyCode == '/' || keyCode == ';' ||
        keyCode == '=' || keyCode == '[' || keyCode == '\\' || keyCode == ']' )
      return String.valueOf((char)keyCode);

   switch(keyCode) {
   case VK_ENTER:      return "Enter";
   case VK_BACK_SPACE: return "Backspace";
   case VK_TAB:        return "Tab";
   case VK_CANCEL:     return "Cancel";
   case VK_CLEAR:      return "Clear";
   case VK_SHIFT:      return "Shift";
   case VK_CONTROL:    return "Control";
   case VK_ALT:        return "Alt";
   case VK_PAUSE:      return "Pause";
   case VK_CAPS_LOCK:  return "Caps Lock";
   case VK_ESCAPE:     return "Escape";
   case VK_SPACE:      return "Space";
   case VK_PAGE_UP:    return "Page Up";
   case VK_PAGE_DOWN:  return "Page Down";
   case VK_END:        return "End";
   case VK_HOME:       return "Home";
   case VK_LEFT:       return "Left";
   case VK_UP:         return "Up";
   case VK_RIGHT:      return "Right";
   case VK_DOWN:       return "Down";

  /* case VK_KP_LEFT:    return "Left";
   case VK_KP_UP:      return "Up";
   case VK_KP_RIGHT:   return "Right";
   case VK_KP_DOWN:    return "Down";*/

   case VK_MULTIPLY:   return "NumPad *";
   case VK_ADD:        return "NumPad +";
   case VK_SEPARATER:  return "NumPad ,";
   case VK_SUBTRACT:   return "NumPad -";
   case VK_DECIMAL:    return "NumPad .";
   case VK_DIVIDE:     return "NumPad /";

   case VK_F1:         return "F1";
   case VK_F2:         return "F2";
   case VK_F3:         return "F3";
   case VK_F4:         return "F4";
   case VK_F5:         return "F5";
   case VK_F6:         return "F6";
   case VK_F7:         return "F7";
   case VK_F8:         return "F8";
   case VK_F9:         return "F9";
   case VK_F10:        return "F10";
   case VK_F11:        return "F11";
   case VK_F12:        return "F12";
   // JDK 1.2+
 /*  case VK_F13:        return "F13";
   case VK_F14:        return "F14";
   case VK_F15:        return "F15";
   case VK_F16:        return "F16";
   case VK_F17:        return "F17";
   case VK_F18:        return "F18";
   case VK_F19:        return "F19";
   case VK_F20:        return "F20";
   case VK_F21:        return "F21";
   case VK_F22:        return "F22";
   case VK_F23:        return "F23";
   case VK_F24:        return "F24";*/

   case VK_DELETE:     return "Delete";
   case VK_NUM_LOCK:   return "Num Lock";
   case VK_SCROLL_LOCK: return "Scroll Lock";
   case VK_PRINTSCREEN: return "Print Screen";
   case VK_INSERT:     return "Insert";
   case VK_HELP:       return "Help";
   case VK_META:       return "Meta";
   case VK_BACK_QUOTE: return "Back Quote";
   case VK_QUOTE:      return "Quote";

   case VK_FINAL:      return "Final";
   case VK_CONVERT:    return "Convert";
   case VK_NONCONVERT: return "No Convert";
   case VK_ACCEPT:     return "Accept";
   case VK_MODECHANGE: return "Mode Change";
   case VK_KANA:       return "Kana";
   case VK_KANJI:      return "Kanji";

   // Java2 additions
 /*  case VK_AGAIN:      return "Again";
   case VK_ALL_CANDIDATES:   return "All Candidates";
   case VK_ALPHANUMERIC:   return "Alphanumeric";
   case VK_ALT_GRAPH:   return "Alt Graph";
   case VK_AMPERSAND:   return "Ampersand";
   case VK_ASTERISK:   return "Asterisk";
   case VK_AT:      return "At";
   case VK_BRACELEFT:   return "Left Brace";
   case VK_BRACERIGHT:   return "Right Brace";
   case VK_CIRCUMFLEX:   return "Circumflex";
   case VK_CODE_INPUT:   return "Code Input";
   case VK_COLON:      return "Colon";
   case VK_COMPOSE:   return "Compose ";
   case VK_COPY:      return "Copy";
   case VK_CUT:      return "Cut";
   case VK_DEAD_ABOVEDOT:   return "Dead Above Dot";
   case VK_DEAD_ABOVERING:   return "Dead Above Ring";
   case VK_DEAD_ACUTE:   return "Dead Acute";
   case VK_DEAD_BREVE:   return "Dead Breve";
   case VK_DEAD_CARON:   return "Dead Caron";
   case VK_DEAD_CEDILLA:   return "Dead Cedilla";
   case VK_DEAD_CIRCUMFLEX: return "Dead Circumflex";
   case VK_DEAD_DIAERESIS:   return "Dead Diaeresis";
   case VK_DEAD_DOUBLEACUTE: return "Dead Double Acute";
   case VK_DEAD_GRAVE:   return "Dead Grave";
   case VK_DEAD_IOTA:   return "Dead Iota";
   case VK_DEAD_MACRON:   return "Dead Macron";
   case VK_DEAD_OGONEK:   return "Dead Ogonek";
   case VK_DEAD_SEMIVOICED_SOUND: return "Dead Semivoiced Sound";
   case VK_DEAD_TILDE:   return "Dead Tilde";
   case VK_DEAD_VOICED_SOUND: return "Dead Voiced Sound";
   case VK_DOLLAR:      return "Dollar";
   case VK_EURO_SIGN:   return "Euro";
   case VK_EXCLAMATION_MARK: return "Exclamation Mark";
   case VK_FIND:      return "Find";
   case VK_FULL_WIDTH:   return "Full-Width";
   case VK_GREATER:   return "Greater";
   case VK_HALF_WIDTH:   return "Half-Width";
   case VK_HIRAGANA:   return "Hiragana";
   case VK_INPUT_METHOD_ON_OFF: return "Input Method On/Off";
   case VK_INVERTED_EXCLAMATION_MARK: return "Inverted Exclamation Mark";
   case VK_JAPANESE_HIRAGANA: return "Japanese Hiragana";
   case VK_JAPANESE_KATAKANA: return "Japanese Katakana";
   case VK_JAPANESE_ROMAN:   return "Japanese Roman";
   case VK_KANA_LOCK:   return "Kana Lock";
   case VK_KATAKANA:   return "Katakana";
   case VK_LEFT_PARENTHESIS: return "Left Parenthesis";
   case VK_LESS:      return "Less";
   case VK_MINUS:      return "Minus";
   case VK_NUMBER_SIGN:   return "Number Sign";
   case VK_PASTE:      return "Paste";
   case VK_PLUS:      return "Plus";
   case VK_PREVIOUS_CANDIDATE: return "Previous Candidate";
   case VK_PROPS:      return "Props";
   case VK_QUOTEDBL:   return "Double Quote";
   case VK_RIGHT_PARENTHESIS: return "Right Parenthesis";
   case VK_ROMAN_CHARACTERS: return "Roman Characters";
   case VK_STOP:      return "Stop";
   case VK_UNDERSCORE:   return "Underscore";
   case VK_UNDO:      return "Undo";*/
   }

   if ( keyCode >= VK_NUMPAD0 && keyCode <= VK_NUMPAD9 ) {
      return "NumPad-" + (char)(keyCode - VK_NUMPAD0 + '0');
   }

   return "Unknown keyCode: 0x" + Integer.toString(keyCode, 16);
}

protected Event initOldEvent ( Event e ) {
   if ( keyChar == 0 )
      return null;

   e.target = source;
   e.id = id;
   
   e.when = when;
   e.modifiers = modifiers;
   e.key = keyChar;

   // we need to set x & y coordinates too,
   // since some events may be cached.
   e.x = ((Component) getSource()).getX();
   e.y = ((Component) getSource()).getY();

   return e;
}

public boolean isActionKey () {
   int kc = keyCode;

   // Pop out most common alphanumerics first
   if ( kc == VK_SPACE ) return false; // Space-bar
   if ( (kc > VK_DOWN) && (kc < VK_F1) ) return false; // Alphanum
   if ( kc < VK_PAUSE ) return false; // Newline
   if ( (kc > VK_KANJI) && (kc < VK_CONVERT) ) return false; // Escape
   if ( (kc > VK_KANA) && (kc < VK_FINAL) ) return false; // None
   if ( kc <= VK_F12 ) return true; // Everything else common

   if ( (kc >= VK_NUM_LOCK) && (kc <= VK_SCROLL_LOCK) ) return true;
   if ( (kc >= VK_PRINTSCREEN) && (kc <= VK_HELP) ) return true;
   /*if ( (kc >= VK_KP_UP) && (kc <= VK_KP_RIGHT) ) return true;
   if ( (kc >= VK_ALPHANUMERIC) && (kc <= VK_ROMAN_CHARACTERS) ) return true;
   if ( (kc >= VK_ALL_CANDIDATES) && (kc <= VK_INPUT_METHOD_ON_OFF) ) return true;
   if ( (kc >= VK_F13) && (kc <= VK_F24) ) return true;
   if ( (kc >= VK_STOP) && (kc <= VK_UNDO) ) return true;
   if ( kc == VK_COPY ) return true;
   if ( (kc >= VK_PASTE) && (kc <= VK_CUT) ) return true;*/
   if ( (kc >= org.havi.ui.event.HRcEvent.RC_FIRST) && (kc <= org.havi.ui.event.HRcEvent.RC_LAST) ) return true;

   return false;
}

public String paramString () {
   String s;
   int kc = keyCode;
   int k = keyChar;
   
   switch(id) {
   case KEY_PRESSED:      s = "KEY_PRESSED";      break;
   case KEY_RELEASED:   s = "KEY_RELEASED";      break;
   case KEY_TYPED:        s = "KEY_TYPED";        break;
   default:              s = "unknown type";
   }

   s += ",keyCode=" + keyCode;
   
   if ( isActionKey() || kc == VK_ENTER || kc == VK_BACK_SPACE || 
        kc == VK_TAB || kc == VK_ESCAPE || kc == VK_DELETE ||
        (kc >= VK_NUMPAD0 && kc <= VK_NUMPAD9) ) {
      s += ',' + getKeyText( kc);
   }
   else if ( k == '\n' || k == '\b' || k == '\t' || k == VK_ESCAPE || k == VK_DELETE) {
      s += ',' + getKeyText( k);
   }
   else {
      s += ",keyChar='" + keyChar + "'";
   }
   
   if ( modifiers > 0 ) {
      s += ",modifiers=" + getKeyModifiersText( modifiers);
   }
   
   return s;
}

/*public int getKeyLocation() {
//   return keyLocation;
   return KEY_LOCATION_UNKNOWN;
}*/

public void setKeyChar ( char kChar ) {
   keyChar = kChar;
}

public void setKeyCode ( int kCode ) {
   keyCode = kCode;
}

public void setModifiers ( int mods ) {
   modifiers = mods;
}
}
