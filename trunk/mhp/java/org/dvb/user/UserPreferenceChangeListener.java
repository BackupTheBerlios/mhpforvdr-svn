package org.dvb.user ;

//taken from OpenMHP, license is LGPL
/**
* @author tejopa
* @date 13.2.2004
* @status fully implemented
* @module internal
*/


//An application wishing to be informed of any change to a user preference implements this interface.public

interface UserPreferenceChangeListener {

   public void receiveUserPreferenceChangeEvent (UserPreferenceChangeEvent e);

}
