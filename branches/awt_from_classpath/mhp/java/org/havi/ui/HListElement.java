
package org.havi.ui;

/*HListElement is a holder for content used with the HListGroup component.It must contain a 
text string,and may also contain a single graphical image. Applications should not 
directly manipulate HListElement objects.They are intended to be used in conjunction with 
an HListGroup which maintains a list of them,and is responsible for their rendering via 
the HListGroupLook class.The parameters to the constructors are as follows,in cases where 
parameters are not used,then the constructor should use the default 
values. */

public class HListElement {

protected java.lang.String label;
protected java.awt.Image icon;


/*
Creates an HListElement object.See the class description for details of constructor parameters and default values. 
Parameters: icon -The icon for this HListElement. label -The label for this 
HListElement. */
public HListElement(java.awt.Image _icon, java.lang.String _label) {
   icon=_icon;
   label=_label;
}

/*
Creates an HListElement object.See the class description for details of constructor parameters and default values. 
Parameters: label -The label for this HListElement. */
public HListElement(java.lang.String _label) {
   label=_label;
   icon=null;
}

/*
Retrieve the icon for this HListElement. Returns: the graphical icon for this HListElement,or null if no icon was 
set. */
public java.awt.Image getIcon() {
   return icon;
}

/*
Retrieve the label for this HListElement. Returns: the text label for this HListElement. */
public java.lang.String getLabel() {
   return label;
}

/*
Set the icon for this HListElement.If icon is null,the HListElement will be in the same state as if no icon was set. 
Parameters: icon -The icon for this HListElement. */
public void setIcon(java.awt.Image _icon) {
   icon=_icon;
}

/*
Set the label for this HListElement. Parameters: label -The label for this HListElement. */
public void setLabel(java.lang.String _label) {
   label=_label;
}


}
