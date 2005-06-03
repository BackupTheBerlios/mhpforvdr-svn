
package org.havi.ui.event;

/*This event is sent to all registered HScreenConfigurationListener when an HScreenDevice 
modi  es its con  guration.The parameters to the constructors are as follows,in cases 
where parameters are not used,then the constructor should use the default 
values. */

public class HScreenConfigurationEvent extends java.util.EventObject {

/*
Construct an HScreenConfigurationEvent Parameters: source -the HScreenDevice whose con  guration 
changed */
public HScreenConfigurationEvent(java.lang.Object source) {
   super(source);
}


}
