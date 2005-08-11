package org.dvb.user;

import org.openmhp.util.Out;

//Taken from OpenMHP, license is LGPL
/**
* @author tejopa
* @date 11.2.2004
* @status fully implemented
* @module internal
*/

//Thrown when a non-supported preference is used.

public class UnsupportedPreferenceException extends java.lang.Exception {

   public UnsupportedPreferenceException()   {
      super();
      //Out.printMe(Out.TRACE);
   }

   public UnsupportedPreferenceException(String s)   {
      super(s);
      //Out.printMe(Out.TRACE);
   }
}
