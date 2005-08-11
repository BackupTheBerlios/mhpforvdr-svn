
package org.davic.resources;

/*This class is the parent class for events reporting changes in the status of 
resources. */

public class ResourceStatusEvent extends Object {

/*
This constructs a resource status event relating to the specified resource. The precise class of the object will depend 
on the individual API using the resource notification API. Parameters: source - the object (resource) whose status 
changed */
public ResourceStatusEvent(Object _source) {
   source=_source;
}

/*
Returns: the object whose status changed */
public Object getSource() {
   return source;
}

private Object source;


}
