
package org.havi.ui.event;

/*This listener is used to monitor when a component,such as an HVideoComponent on-screen 
location is modi  ed.The parameters to the constructors are as follows,in cases where 
parameters are not used, then the constructor should use the default 
values. */

public interface HScreenLocationModifiedListener extends java.util.EventListener {

/*
This method is called when the component's on-screen location is modi  ed. */
public void report(HScreenLocationModifiedEvent gce);



}
