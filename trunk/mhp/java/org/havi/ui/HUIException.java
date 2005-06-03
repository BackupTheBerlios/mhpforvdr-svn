
package org.havi.ui;

/*HUIException is a generic exception that indicates that the desired user-interface 
mechanism cannot be performed for some reason.The parameters to the constructors are as 
follows,in cases where parameters are not used,then the constructor should use the default 
values. */

public class HUIException extends java.lang.Exception {

/*
Creates an HUIException object.See the class description for details of constructor parameters and default 
values. */
public HUIException() {
}

/*
Creates an HUIException object with a speci  ed reason string. Parameters: message -the reason why the exception was 
raised */
public HUIException(java.lang.String message) {
}


}
