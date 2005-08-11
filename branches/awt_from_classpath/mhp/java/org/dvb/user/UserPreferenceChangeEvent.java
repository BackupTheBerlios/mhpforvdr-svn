package org.dvb.user ;

import org.openmhp.util.Out;

//taken from OpenMHP, license is LGPL
/**
* @author tejopa
* @date 13.2.2004
* @status fully implemented
* @module internal
*/

//This class de nes the event sent to appropriate listeners when a user preference has been changed.

public class UserPreferenceChangeEvent extends java.util.EventObject{

   public UserPreferenceChangeEvent (String preferenceName) {
      super(preferenceName);
      //Out.printMe(Out.TRACE);
   }

   public String getName () {
      //Out.printMe(Out.TRACE);
       return (String)getSource();
   }
}

