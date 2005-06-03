
package org.havi.ui;

/*This interface is implemented for all HAVi component containers that support the manipulation of the z- ordering of 
their children. All Known Implementing Classes: HContainer HScene */

public interface HComponentOrdering {

/*
Adds a java.awt.Component to this HComponentOrdering directly behind a previously added java.awt.Component If component 
has already been added to this container,then addAfter moves component behind front If front and component are the same 
component which was already added to this container,addAfter does not change the ordering of the components and returns 
component This method affects the Z-order of the java.awt.Component children within the HComponentOrdering ,and may also 
implicitly change the numeric ordering of those children. Parameters: component -is the java.awt.Component to be added 
to the HComponentOrdering front -is the java.awt.Component which component will be placed behind,i.e.front will be 
directly in front of the added java.awt.Component Returns: If the java.awt.Component is successfully added,then it will 
be returned from this call.If the java.awt.Component is not successfully added,e.g.front is not a java.awt.Component 
currently added to the HComponentOrdering ,then null will be returned. This method must be implemented in a thread safe 
manner. */
public java.awt.Component addAfter(java.awt.Component component, java.awt.Component 
front);


/*
Adds a java.awt.Component to this HComponentOrdering directly in front of a previously added java.awt.Component. If 
component has already been added to this container,then addBefore moves component in front of behind If behind and 
component are the same component which was already added to this container,addBefore does not change the ordering of the 
components and returns component This method affects the Z-order of the java.awt.Component children within the 
HComponentOrdering ,and may also implicitly change the numeric ordering of those children. Parameters: component -is the 
java.awt.Component to be added to the HComponentOrdering behind -is the java.awt.Component which component will be 
placed in front of,i.e. behind will be directly behind the added java.awt.Component Returns: If the java.awt.Component 
is successfully added,then it will be returned from this call.If the java.awt.Component is not successfully 
added,e.g.behind is not a java.awt.Component currently added to the HComponentOrdering ,then null will be returned. This 
method must be implemented in a thread safe manner. */
public java.awt.Component addBefore(java.awt.Component component, java.awt.Component 
behind);


/*
Moves the speci  ed java.awt.Component one component nearer in the Z-order,i.e.wapping it with the java.awt.Component 
that was directly in front of it. If component is already at the front of the Z-order,the order is unchanged and pop 
returns true Parameters: component -The java.awt.Component to be moved. Returns: returns true on success,false on 
failure,for example if the java.awt.Component has yet to be added to the HComponentOrdering */
public boolean pop(java.awt.Component component);


/*
Puts the speci  ed java.awt.Component in front of another java.awt.Component in the Z-order of this HComponentOrdering . 
If move and behind are the same component which has been added to the container popInFront does not change the Z-order 
and returns true Parameters: move -The java.awt.Component to be moved directly in front of the "behind"Component in the 
Z-order of this HComponentOrdering . behind -The java.awt.Component which the "move"Component should be placed directly 
in front of. Returns: returns true on success,false on failure,for example when either java.awt.Component has yet to be 
added to the HComponentOrdering .If this method fails,the Z-order is unchanged. */
public boolean popInFrontOf(java.awt.Component move, java.awt.Component behind);


/*
Brings the speci  ed java.awt.Component to the "front"of the Z-order in this HComponentOrdering . If component is 
already at the front of the Z-order,the order is unchanged and popToFront returns true Parameters: component -The 
java.awt.Component to bring to the "front"of the Z-order of this HComponentOrdering . Returns: returns true on 
success,false on failure,for example when the java.awt.Component has yet to be added to the HComponentOrdering .If this 
method fails,the Z-order is unchanged. */
public boolean popToFront(java.awt.Component component);


/*
Moves the speci  ed java.awt.Component one component further away in the Z-order,i.e. wapping it with the 
java.awt.Component that was directly behind it. If component is already at the back of the Z-order,the order is 
unchanged and push returns true Parameters: component -The java.awt.Component to be moved. Returns: returns true on 
success,false on failure,for example if the java.awt.Component has yet to be added to the 
HComponentOrdering */
public boolean push(java.awt.Component component);


/*
Puts the speci  ed java.awt.Component behind another java.awt.Component in the Z-order of this HComponentOrdering . If 
move and front are the same component which has been added to the container pushBehind does not change the Z-order and 
returns true Parameters: move -The java.awt.Component to be moved directly behind the "front"Component in the Z- order 
of this HComponentOrdering . front -The java.awt.Component which the "move"Component should be placed directly behind. 
Returns: returns true on success,false on failure,for example when either java.awt.Component has yet to be added to the 
HComponentOrdering */
public boolean pushBehind(java.awt.Component move, java.awt.Component front);


/*
Place the speci  ed java.awt.Component at the "back"of the Z-order in this HComponentOrdering . If component is already 
at the back the Z-order is unchanged and pushToBack returns true Parameters: component -The java.awt.Component to place 
at the "back"of the Z-order of this HComponentOrdering. Returns: returns true on success,false on failure,for example 
when the java.awt.Component has yet to be added to the HComponentOrdering .If the component was not added to the 
container pushToBack does not change the Z-order. */
public boolean pushToBack(java.awt.Component component);



}
