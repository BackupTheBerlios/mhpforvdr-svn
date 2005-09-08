
package java.awt.event;


public interface RcKeyConstants {

   
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

