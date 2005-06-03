
package org.dvb.event;

/*This class de  nes a repository which contains all the user events de  ned in the UserEvent class.For example,this 
pre-de  ned repository could be used by an application,which requires a pin code from the user,in order to prevent 
another applications from receiving events. */

//TODO: When UserEventRepository honors modifiers, this class should, too.


public class OverallRepository extends UserEventRepository {


/*
The constructor for the repository. */
public OverallRepository() {
   super("OverallRepository");
   for (int i=0;i<keycodes.length; i++)
      addKey(keycodes[i]);
}

final static private int[] keycodes = 
{ 
UserEvent.VK_ENTER, 
UserEvent.VK_BACK_SPACE, 
UserEvent.VK_TAB, 
UserEvent.VK_CANCEL, 
UserEvent.VK_CLEAR, 
UserEvent.VK_SHIFT, 
UserEvent.VK_CONTROL, 
UserEvent.VK_ALT, 
UserEvent.VK_PAUSE, 
UserEvent.VK_CAPS_LOCK, 
UserEvent.VK_ESCAPE, 
UserEvent.VK_SPACE, 
UserEvent.VK_PAGE_UP, 
UserEvent.VK_PAGE_DOWN, 
UserEvent.VK_END, 
UserEvent.VK_HOME, 
UserEvent.VK_LEFT, 
UserEvent.VK_UP, 
UserEvent.VK_RIGHT, 
UserEvent.VK_DOWN, 
UserEvent.VK_COMMA, 
UserEvent.VK_PERIOD, 
UserEvent.VK_SLASH, 
UserEvent.VK_0, 
UserEvent.VK_1, 
UserEvent.VK_2, 
UserEvent.VK_3, 
UserEvent.VK_4, 
UserEvent.VK_5, 
UserEvent.VK_6, 
UserEvent.VK_7, 
UserEvent.VK_8, 
UserEvent.VK_9, 
UserEvent.VK_SEMICOLON, 
UserEvent.VK_EQUALS, 
UserEvent.VK_A, 
UserEvent.VK_B, 
UserEvent.VK_C, 
UserEvent.VK_D, 
UserEvent.VK_E, 
UserEvent.VK_F, 
UserEvent.VK_G, 
UserEvent.VK_H, 
UserEvent.VK_I, 
UserEvent.VK_J, 
UserEvent.VK_K, 
UserEvent.VK_L, 
UserEvent.VK_M, 
UserEvent.VK_N, 
UserEvent.VK_O, 
UserEvent.VK_P, 
UserEvent.VK_Q, 
UserEvent.VK_R, 
UserEvent.VK_S, 
UserEvent.VK_T, 
UserEvent.VK_U, 
UserEvent.VK_V, 
UserEvent.VK_W, 
UserEvent.VK_X, 
UserEvent.VK_Y, 
UserEvent.VK_Z, 
UserEvent.VK_OPEN_BRACKET, 
UserEvent.VK_BACK_SLASH, 
UserEvent.VK_CLOSE_BRACKET, 
UserEvent.VK_NUMPAD0, 
UserEvent.VK_NUMPAD1, 
UserEvent.VK_NUMPAD2, 
UserEvent.VK_NUMPAD3, 
UserEvent.VK_NUMPAD4, 
UserEvent.VK_NUMPAD5, 
UserEvent.VK_NUMPAD6, 
UserEvent.VK_NUMPAD7, 
UserEvent.VK_NUMPAD8, 
UserEvent.VK_NUMPAD9, 
UserEvent.VK_MULTIPLY, 
UserEvent.VK_ADD, 
UserEvent.VK_SEPARATER, 
UserEvent.VK_SUBTRACT, 
UserEvent.VK_DECIMAL, 
UserEvent.VK_DIVIDE, 
UserEvent.VK_F1, 
UserEvent.VK_F2, 
UserEvent.VK_F3, 
UserEvent.VK_F4, 
UserEvent.VK_F5, 
UserEvent.VK_F6, 
UserEvent.VK_F7, 
UserEvent.VK_F8, 
UserEvent.VK_F9, 
UserEvent.VK_F10, 
UserEvent.VK_F11, 
UserEvent.VK_F12, 
UserEvent.VK_DELETE, 
UserEvent.VK_NUM_LOCK, 
UserEvent.VK_SCROLL_LOCK, 
UserEvent.VK_PRINTSCREEN, 
UserEvent.VK_INSERT, 
UserEvent.VK_HELP, 
UserEvent.VK_META, 
UserEvent.VK_BACK_QUOTE, 
UserEvent.VK_QUOTE, 
UserEvent.VK_FINAL, 
UserEvent.VK_CONVERT, 
UserEvent.VK_NONCONVERT, 
UserEvent.VK_ACCEPT, 
UserEvent.VK_MODECHANGE, 
UserEvent.VK_KANA, 
UserEvent.VK_KANJI, 
UserEvent.VK_UNDEFINED, 
UserEvent.VK_COLORED_KEY_0, 
UserEvent.VK_COLORED_KEY_1, 
UserEvent.VK_COLORED_KEY_2, 
UserEvent.VK_COLORED_KEY_3 };
   


}
