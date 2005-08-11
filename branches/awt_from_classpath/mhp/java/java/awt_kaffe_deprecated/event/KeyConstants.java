package java.awt.event;


//This interface contains the key constants
//because they are used not only by KeyEvent, but also
//by org.havi.ui.event.HRcEvent and org.dvb.event.UserEvent

public interface KeyConstants {

   final public static int KEY_FIRST = 400;
   final public static int KEY_LAST = 402;
   final public static int KEY_TYPED = KEY_FIRST;
   final public static int KEY_PRESSED = 1 + KEY_FIRST;
   final public static int KEY_RELEASED = 2 + KEY_FIRST;
   
   final public static int VK_ENTER = '\n';
   final public static int VK_BACK_SPACE = '\b';
   final public static int VK_TAB = '\t';
   final public static int VK_CANCEL = 0x03;
   final public static int VK_CLEAR = 0x0C;
   final public static int VK_SHIFT = 0x10;
   final public static int VK_CONTROL = 0x11;
   final public static int VK_ALT = 0x12;
   final public static int VK_PAUSE = 0x13;
   final public static int VK_CAPS_LOCK = 0x14;
   final public static int VK_ESCAPE = 0x1B;
   final public static int VK_SPACE = 0x20;
   final public static int VK_PAGE_UP = 0x21;
   final public static int VK_PAGE_DOWN = 0x22;
   final public static int VK_END = 0x23;
   final public static int VK_HOME = 0x24;
   final public static int VK_LEFT = 0x25;
   final public static int VK_UP = 0x26;
   final public static int VK_RIGHT = 0x27;
   final public static int VK_DOWN = 0x28;
   final public static int VK_COMMA = 0x2C;
   final public static int VK_MINUS = 0x2D; // 1.2
   final public static int VK_PERIOD = 0x2E;
   final public static int VK_SLASH = 0x2F;
   final public static int VK_0 = 0x30;
   final public static int VK_1 = 0x31;
   final public static int VK_2 = 0x32;
   final public static int VK_3 = 0x33;
   final public static int VK_4 = 0x34;
   final public static int VK_5 = 0x35;
   final public static int VK_6 = 0x36;
   final public static int VK_7 = 0x37;
   final public static int VK_8 = 0x38;
   final public static int VK_9 = 0x39;
   final public static int VK_SEMICOLON = 0x3B;
   final public static int VK_EQUALS = 0x3D;
   final public static int VK_A = 0x41;
   final public static int VK_B = 0x42;
   final public static int VK_C = 0x43;
   final public static int VK_D = 0x44;
   final public static int VK_E = 0x45;
   final public static int VK_F = 0x46;
   final public static int VK_G = 0x47;
   final public static int VK_H = 0x48;
   final public static int VK_I = 0x49;
   final public static int VK_J = 0x4A;
   final public static int VK_K = 0x4B;
   final public static int VK_L = 0x4C;
   final public static int VK_M = 0x4D;
   final public static int VK_N = 0x4E;
   final public static int VK_O = 0x4F;
   final public static int VK_P = 0x50;
   final public static int VK_Q = 0x51;
   final public static int VK_R = 0x52;
   final public static int VK_S = 0x53;
   final public static int VK_T = 0x54;
   final public static int VK_U = 0x55;
   final public static int VK_V = 0x56;
   final public static int VK_W = 0x57;
   final public static int VK_X = 0x58;
   final public static int VK_Y = 0x59;
   final public static int VK_Z = 0x5A;
   final public static int VK_OPEN_BRACKET = 0x5B;
   final public static int VK_BACK_SLASH = 0x5C;
   final public static int VK_CLOSE_BRACKET = 0x5D;
   final public static int VK_NUMPAD0 = 0x60;
   final public static int VK_NUMPAD1 = 0x61;
   final public static int VK_NUMPAD2 = 0x62;
   final public static int VK_NUMPAD3 = 0x63;
   final public static int VK_NUMPAD4 = 0x64;
   final public static int VK_NUMPAD5 = 0x65;
   final public static int VK_NUMPAD6 = 0x66;
   final public static int VK_NUMPAD7 = 0x67;
   final public static int VK_NUMPAD8 = 0x68;
   final public static int VK_NUMPAD9 = 0x69;
   final public static int VK_MULTIPLY = 0x6A;
   final public static int VK_ADD = 0x6B;
   final public static int VK_SEPARATER = 0x6C; // 1.1 compatibility (misspelled)
   final public static int VK_SEPARATOR = 0x6C; // 1.4
   final public static int VK_SUBTRACT = 0x6D;
   final public static int VK_DECIMAL = 0x6E;
   final public static int VK_DIVIDE = 0x6F;
   final public static int VK_DELETE = 0x7F;
   final public static int VK_NUM_LOCK = 0x90;
   final public static int VK_SCROLL_LOCK = 0x91;
   final public static int VK_F1 = 0x70;
   final public static int VK_F2 = 0x71;
   final public static int VK_F3 = 0x72;
   final public static int VK_F4 = 0x73;
   final public static int VK_F5 = 0x74;
   final public static int VK_F6 = 0x75;
   final public static int VK_F7 = 0x76;
   final public static int VK_F8 = 0x77;
   final public static int VK_F9 = 0x78;
   final public static int VK_F10 = 0x79;
   final public static int VK_F11 = 0x7A;
   final public static int VK_F12 = 0x7B;

   final public static int VK_PRINTSCREEN = 0x9A;
   final public static int VK_INSERT = 0x9B;
   final public static int VK_HELP = 0x9C;
   final public static int VK_META = 0x9D;
   final public static int VK_BACK_QUOTE = 0xC0;
   final public static int VK_QUOTE = 0xDE;

   final public static int VK_FINAL = 0x18;
   final public static int VK_CONVERT = 0x1C;
   final public static int VK_NONCONVERT = 0x1D;
   final public static int VK_ACCEPT = 0x1E;
   final public static int VK_MODECHANGE = 0x1F;
   final public static int VK_KANA = 0x15;
   final public static int VK_KANJI = 0x19;

   
   final public static int VK_UNDEFINED = 0x0;
   final public static char CHAR_UNDEFINED = 0xffff;
   
   
   
   
   //the constants below are > 1.1.8 and thus not required for MHP
/*
   final public static int VK_F13 = 0xF000; // 1.2
   final public static int VK_F14 = 0xF001; // 1.2
   final public static int VK_F15 = 0xF002; // 1.2
   final public static int VK_F16 = 0xF003; // 1.2
   final public static int VK_F17 = 0xF004; // 1.2
   final public static int VK_F18 = 0xF005; // 1.2
   final public static int VK_F19 = 0xF006; // 1.2
   final public static int VK_F20 = 0xF007; // 1.2
   final public static int VK_F21 = 0xF008; // 1.2
   final public static int VK_F22 = 0xF009; // 1.2
   final public static int VK_F23 = 0xF00A; // 1.2
   final public static int VK_F24 = 0xF00B; // 1.2 

   final public static int VK_KP_UP = 0xE0; // 1.2
   final public static int VK_KP_DOWN = 0xE1; // 1.2
   final public static int VK_KP_LEFT = 0xE2; // 1.2
   final public static int VK_KP_RIGHT = 0xE3; // 1.2
   final public static int VK_DEAD_GRAVE = 0x80; // 1.2
   final public static int VK_DEAD_ACUTE = 0x81; // 1.2
   final public static int VK_DEAD_CIRCUMFLEX = 0x82; // 1.2
   final public static int VK_DEAD_TILDE = 0x83; // 1.2
   final public static int VK_DEAD_MACRON = 0x84; // 1.2
   final public static int VK_DEAD_BREVE = 0x85; // 1.2
   final public static int VK_DEAD_ABOVEDOT = 0x86; // 1.2
   final public static int VK_DEAD_DIAERESIS = 0x87; // 1.2
   final public static int VK_DEAD_ABOVERING = 0x88; // 1.2
   final public static int VK_DEAD_DOUBLEACUTE = 0x89; // 1.2
   final public static int VK_DEAD_CARON = 0x8A; // 1.2
   final public static int VK_DEAD_CEDILLA = 0x8B; // 1.2
   final public static int VK_DEAD_OGONEK = 0x8C; // 1.2
   final public static int VK_DEAD_IOTA = 0x8D; // 1.2
   final public static int VK_DEAD_VOICED_SOUND = 0x8E; // 1.2
   final public static int VK_DEAD_SEMIVOICED_SOUND = 0x8F; // 1.2
   final public static int VK_AMPERSAND = 0x96; // 1.2
   final public static int VK_ASTERISK = 0x97; // 1.2
   final public static int VK_QUOTEDBL = 0x98; // 1.2
   final public static int VK_LESS = 0x99; // 1.2
   final public static int VK_GREATER = 0xA0; // 1.2
   final public static int VK_BRACELEFT = 0xA1; // 1.2
   final public static int VK_BRACERIGHT = 0xA2; // 1.2
   final public static int VK_AT = 0x200; // 1.2
   final public static int VK_COLON = 0x201; // 1.2
   final public static int VK_CIRCUMFLEX = 0x202; // 1.2
   final public static int VK_DOLLAR = 0x203; // 1.2
   final public static int VK_EURO_SIGN = 0x204; // 1.2
   final public static int VK_EXCLAMATION_MARK = 0x205; // 1.2
   final public static int VK_INVERTED_EXCLAMATION_MARK = 0x206; // 1.2
   final public static int VK_LEFT_PARENTHESIS = 0x207; // 1.2
   final public static int VK_NUMBER_SIGN = 0x208; // 1.2
   final public static int VK_PLUS = 0x209; // 1.2
   final public static int VK_RIGHT_PARENTHESIS = 0x20A; // 1.2
   final public static int VK_UNDERSCORE = 0x20B; // 1.2
   

   final public static int VK_ALPHANUMERIC = 0xF0; // 1.2
   final public static int VK_KATAKANA = 0xF1; // 1.2
   final public static int VK_HIRAGANA = 0xF2; // 1.2
   final public static int VK_FULL_WIDTH = 0xF3; // 1.2
   final public static int VK_HALF_WIDTH = 0xF4; // 1.2
   final public static int VK_ROMAN_CHARACTERS = 0xF5; // 1.2
   final public static int VK_ALL_CANDIDATES = 0x100; // 1.2
   final public static int VK_PREVIOUS_CANDIDATE = 0x101; // 1.2
   final public static int VK_CODE_INPUT = 0x102; // 1.2
   final public static int VK_JAPANESE_KATAKANA = 0x103; // 1.2
   final public static int VK_JAPANESE_HIRAGANA = 0x104; // 1.2
   final public static int VK_JAPANESE_ROMAN = 0x105; // 1.2
   final public static int VK_KANA_LOCK = 0x106; // 1.2
   final public static int VK_INPUT_METHOD_ON_OFF = 0x107; // 1.2
   final public static int VK_CUT = 0xFFD1; // 1.2
   final public static int VK_COPY = 0xFFCD; // 1.2
   final public static int VK_PASTE = 0xFFCF; // 1.2
   final public static int VK_UNDO = 0xFFCB; // 1.2
   final public static int VK_AGAIN = 0xFFC9; // 1.2
   final public static int VK_FIND = 0xFFD0; // 1.2
   final public static int VK_PROPS = 0xFFCA; // 1.2
   final public static int VK_STOP = 0xFFC8; // 1.2
   final public static int VK_COMPOSE = 0xFF20; // 1.2
   final public static int VK_ALT_GRAPH = 0xFF7E; // 1.2
   

   final public static int KEY_LOCATION_UNKNOWN = 0; // 1.4
   final public static int KEY_LOCATION_STANDARD = 1; // 1.4
   final public static int KEY_LOCATION_LEFT = 2; // 1.4
   final public static int KEY_LOCATION_RIGHT = 3; // 1.4
   final public static int KEY_LOCATION_NUMPAD = 4; // 1.4
   */

   
   
   //the following constants are used by HRcEvent (extending KeyEvent) and UserEvent
   
   /*
The 'balance left'action id -moves the audio balance to the left. */
public static final int VK_BALANCE_LEFT = 452;


/*
The 'balance right'action id -moves the audio balance to the right. */
public static final int VK_BALANCE_RIGHT = 451;


/*
The 'bass boost down'action id -decreases the audio ampli  er bass boost. */
public static final int VK_BASS_BOOST_DOWN = 456;


/*
The 'bass boost up'action id -increases the audio ampli  er bass boost. */
public static final int VK_BASS_BOOST_UP = 455;


/*
The 'channel down'action id. */
public static final int VK_CHANNEL_DOWN = 428;


/*
The 'channel up'action id. */
public static final int VK_CHANNEL_UP = 427;


/*
The 'clear favorite 0'action id. */
public static final int VK_CLEAR_FAVORITE_0 = 437;


/*
The 'clear favorite 1'action id. */
public static final int VK_CLEAR_FAVORITE_1 = 438;


/*
The 'clear favorite 2'action id. */
public static final int VK_CLEAR_FAVORITE_2 = 439;


/*
The 'clear favorite 3'action id. */
public static final int VK_CLEAR_FAVORITE_3 = 440;


/*
Colored key 0 action id. Up to six colored soft keys can be included on a remote control.These are optional,and must be 
identi  ed with a color.If implemented,these keys are to be oriented from left to right,or from top to bottom in 
ascending order. The application can determine how many colored keys are implemented,and what colors are to be used,so 
that the application can match the controls,by using the getRepresentation method in the HRcCapabilities 
class. */
public static final int VK_COLORED_KEY_0 =  403;//red


/*
Colored key 1 action id. */
public static final int VK_COLORED_KEY_1 =  404;//green


/*
Colored key 2 action id. */
public static final int VK_COLORED_KEY_2 = 405; //yellow


/*
Colored key 3 action id. */
public static final int VK_COLORED_KEY_3 = 406; //blue


/*
Colored key 4 action id. */
public static final int VK_COLORED_KEY_4 = 407;


/*
Colored key 5 action id. */
public static final int VK_COLORED_KEY_5 = 408;


/*
The 'device dimmer'action id adjusts illumination of the device. This may be a toggle between two states,or a sequence 
through multiple states. */
public static final int VK_DIMMER = 410;


/*
The 'display swap'action id -swaps displayed video sources. */
public static final int VK_DISPLAY_SWAP = 444;


/*
The 'eject /insert media'action id. */
public static final int VK_EJECT_TOGGLE = 414;


/*
The 'fader front'action id -moves the audio fader to the front. */
public static final int VK_FADER_FRONT = 453;


/*
The 'fader rear'action id -moves the audio fader to the rear. */
public static final int VK_FADER_REAR = 454;


/*
The 'fast forward (media)'action id. */
public static final int VK_FAST_FWD = 417;


/*
The '(send media)to end position'action id. */
public static final int VK_GO_TO_END = 423;


/*
The 'go (send media)to start position'action id. */
public static final int VK_GO_TO_START = 422;


/*
The 'guide'action id -indicates a user request for a program guide (toggle). */
public static final int VK_GUIDE = 458;


/*
The 'info'action id -indicates that the user has requested additional information 
(toggle). */
public static final int VK_INFO = 457;


/*
The 'mute'action id -mute audio output */
public static final int VK_MUTE = 449;


/*
The 'picture in picture toggle'action id -turns picture in picture mode on or off. */
public static final int VK_PINP_TOGGLE = 442;


/*
The 'play (media)'action id. */
public static final int VK_PLAY = 415;


/*
The 'decrease (media)play speed'action id. */
public static final int VK_PLAY_SPEED_DOWN = 419;


/*
The 'set (media)play speed to normal'action id. */
public static final int VK_PLAY_SPEED_RESET = 420;


/*
The 'increase (media)play speed'action id. */
public static final int VK_PLAY_SPEED_UP = 418;


/*
The 'device power'action id turns on or off the delegated device. */
public static final int VK_POWER = 409;


/*
The 'toggle random (media)play'action id. */
public static final int VK_RANDOM_TOGGLE = 426;


/*
The 'recall favorite 0'action id. */
public static final int VK_RECALL_FAVORITE_0 = 433;


/*
The 'recall favorite 1'action id. */
public static final int VK_RECALL_FAVORITE_1 = 434;


/*
The 'recall favorite 2'action id. */
public static final int VK_RECALL_FAVORITE_2 = 435;


/*
The 'recall favorite 3'action id. */
public static final int VK_RECALL_FAVORITE_3 = 436;


/*
The 'record (to media)'action id. */
public static final int VK_RECORD = 416;


/*
The 'select next (media)record speed'action id. */
public static final int VK_RECORD_SPEED_NEXT = 421;


/*
The 'rewind (media)'action id. */
public static final int VK_REWIND = 412;


/*
The 'scan channels toggle'action id -turns channel scanning on or off. */
public static final int VK_SCAN_CHANNELS_TOGGLE = 441;


/*
The 'screen mode next'action id -advances the display screen mode. */
public static final int VK_SCREEN_MODE_NEXT = 445;


/*
The 'split screen toggle'action id -turns split screen on or off. */
public static final int VK_SPLIT_SCREEN_TOGGLE = 443;


/*
The 'stop (media)'action id. */
public static final int VK_STOP = 413;


/*
The 'store current setting as favorite 0'action id. */
public static final int VK_STORE_FAVORITE_0 = 429;


/*
The 'store current setting as favorite 1'action id. */
public static final int VK_STORE_FAVORITE_1 = 430;


/*
The 'store current setting as favorite 2'action id. */
public static final int VK_STORE_FAVORITE_2 = 431;


/*
The 'store current setting as favorite 3'action id. */
public static final int VK_STORE_FAVORITE_3 = 432;


/*
The 'subtitle'action id -indicates a user request for subtitling (toggle). */
public static final int VK_SUBTITLE = 460;


/*
The 'surround mode next'action id -advances audio ampli  er surround mode. */
public static final int VK_SURROUND_MODE_NEXT = 450;


/*
The 'teletext'action id -indicates a user request for a teletext service (toggle). */
public static final int VK_TELETEXT = 459;


/*
The '(send media)to next track'action id. */
public static final int VK_TRACK_NEXT = 425;


/*
The '(send media)to previous track'action id. */
public static final int VK_TRACK_PREV = 424;


/*
The 'video mode next'action id -advances the display video mode. */
public static final int VK_VIDEO_MODE_NEXT = 446;


/*
The 'volume down'action id -decreases audio ampli  er volume. */
public static final int VK_VOLUME_DOWN = 448;


/*
The 'volume up'action id -increases audio ampli  er volume. */
public static final int VK_VOLUME_UP = 447;


/*
The 'device wink'action id is used to indicated that the device should identify itself in some manner, for 
example,audibly or visually. */
public static final int VK_WINK = 411;



}
