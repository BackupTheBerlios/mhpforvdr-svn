
package org.havi.ui.event;

/*The HTextListener interface enables the reception of HTextEvent ,as generated by objects 
implementing HTextValue .The parameters to the constructors are as follows,in cases where 
parameters are not used,then the constructor should use the default 
values. */

public interface HTextListener extends java.util.EventListener {

/*
Called when the caret position of an HTextValue component has moved. Parameters: e -is the HTextEvent generated by the 
object implementing HTextValue . */
public void caretMoved(HTextEvent e);


/*
Called when the textual content of an HTextValue component has changed. Parameters: e -is the HTextEvent generated by 
the object implementing HTextValue . */
public void textChanged(HTextEvent e);



}
