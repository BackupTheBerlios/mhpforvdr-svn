
package org.havi.ui.event;

/*This listener is used to monitor when the con  guration of an HScreenDevice is modi  
ed.The parameters to the constructors are as follows,in cases where parameters are not 
used,then the constructor should use the default values. */

public interface HScreenConfigurationListener extends java.util.EventListener {

/*
This method is called when the con  guration of an HScreenDevice is modi  ed. */
public void report(HScreenConfigurationEvent gce);



}
