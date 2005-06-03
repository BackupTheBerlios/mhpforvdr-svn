
package org.davic.resources;

/*This interface should be implemented by objects wishing to be informed about changes in status of particular 
resources. */

public interface ResourceStatusListener extends java.util.EventListener{

/*
This method is called by a ResourceServer when a resource changes status. Parameters: event - the change in status which 
happened. */
public abstract void statusChanged(ResourceStatusEvent event);



}
