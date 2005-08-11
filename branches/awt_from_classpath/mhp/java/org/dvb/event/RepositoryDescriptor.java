
package org.dvb.event;

/*An instance of this class will be sent to clients of the DVB event API to notify them (through the interface 
org.davic.resources.ResourceClient)when they are about to lose,or have lost,access to an event source.This object can be 
used by the application to get the name of the repository from which it will no longer be able to receive 
events. */

public class RepositoryDescriptor implements org.davic.resources.ResourceProxy {

private String RepositoryName;
private org.davic.resources.ResourceClient client;


public RepositoryDescriptor(org.davic.resources.ResourceClient rc, java.lang.String name) {
   RepositoryName=name;
   client=rc;
}

/*
Return the object which asked to be noti  ed about withdrawl of the event source.This is the object passed as the 
ResoourceClient to whichever of the various 'add'methods on ResourceManager was used by the application to express 
interest in this repository. Speci  ed By: org.davic.resources.ResourceProxy.getClient()in interface 
org.davic.resources.ResourceProxy Returns: the object which asked to be noti  ed about withdrawl of the event 
source */
public org.davic.resources.ResourceClient getClient() {
   return client;
}

/*
Returns the name of the repository to which the lost user event belongs. Returns: String the name of the 
repository. */
public java.lang.String getName() {
   return RepositoryName;
}


}
