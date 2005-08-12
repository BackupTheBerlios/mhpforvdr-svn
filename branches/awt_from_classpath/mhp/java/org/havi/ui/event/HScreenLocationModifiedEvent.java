
package org.havi.ui.event;

/*This event is generated by the system when a component is moved on-screen,rather than 
within a container.The parameters to the constructors are as follows,in cases where 
parameters are not used, then the constructor should use the default 
values. */

public class HScreenLocationModifiedEvent extends java.util.EventObject {

/*
Creates an HScreenLocationModifiedEvent object.See the class description for details of constructor parameters and 
default values. Parameters: source -the Component whose on-screen location has been modi  
ed. */
public HScreenLocationModifiedEvent(java.lang.Object source) {
   super(source);
}

/*
Returns the Component whose on-screen location has been modi  ed. Overrides: java.util.EventObject.getSource()in class 
java.util.EventObject Returns: the Component whose on-screen location has been modi  
ed. */
public java.lang.Object getSource() {
   return super.getSource();
}


}