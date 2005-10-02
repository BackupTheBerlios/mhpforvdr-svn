/*

 This file is part of XleTView
 Copyright (C) 2003 Martin Svedén

 This is free software, and you are
 welcome to redistribute it under
 certain conditions;

 See LICENSE document for details.


*/

package org.dvb.net.rc;

import org.davic.resources.*;

import java.io.IOException;

import java.util.Vector;

/**
 * 
 *
 * @version  7.11.03
 * @author 	 Bengt Skogvall
 * @author Martin Sveden
 * @statuscode 4
 */
public class ConnectionRCInterface extends RCInterface implements ResourceProxy, Runnable{

    private boolean connected;
    private Vector listenerObjects;
    private static final ConnectionParameters defaultTarget;
    private ConnectionParameters currentTarget;
    private ResourceClient resourceClient; //only one client...
    private long starttime;
    
    /** To fake the time it takes to make a connection with a modem */
    public static int FAKED_CONNECION_TIME = 1000;

    static{
    	defaultTarget = new ConnectionParameters("12345", "user", "pw");
    }
    
    protected ConnectionRCInterface() {
        connected = false;
        listenerObjects = new Vector();
        currentTarget = defaultTarget;
    }

    public boolean isConnected() {
        return connected;
    }

    public float getSetupTimeEstimate() {
        return 10.0f; //immediate connection
    }

    public void reserve(ResourceClient c, Object requestData) throws PermissionDeniedException {
        if (resourceClient == null) {
            resourceClient = c;
            RCInterfaceManager.getInstance().fireResorceStatusChanged(new RCInterfaceReservedEvent(this));
        }
        else{
        	boolean releaseOk = resourceClient.requestRelease(this, null);
        	/*
        	 * We don't really care about if the ResourceClient
        	 * wants to give up the resource or not, if another
        	 * ResourceClient needs it we give it to that one 
        	 * instead.
        	 */
        	
        	// tell the ResourceClient that it's about to lose the resource
        	resourceClient.release(this);
        	
        	// tell the ResourceClient that it has lost the resource
        	resourceClient.notifyRelease(this);
        	
        	// give the resource to the new ResourceClient
        	resourceClient = c;
        }
        /*
        // not used since we always give permission
        else {
            throw new PermissionDeniedException("ConnectionRCInterface already reserved");
        }
        */
    }

    public void release() {    	
    	if(resourceClient != null){
    		resourceClient = null;        
    		RCInterfaceManager.getInstance().fireResorceStatusChanged(new RCInterfaceReleasedEvent(this));
    	}
    }

    public void connect() throws IOException, PermissionDeniedException {
        Thread th = new Thread(this);
        th.start();
    }

    public void disconnect() throws PermissionDeniedException {
        if(connected){
        	connected = false;
        	fireConnectionEvent(new ConnectionTerminatedEvent(this));
        }
        else{
        	System.out.println("ConnectionRCInterface: was not connected");
        }
    }

    /**
     * Get the current target for connections. 
     *
     * @return the current set of connection target parameters
     * @throws IncompleteTargetException if the current target is not completely configured
     * @throws SecurityException if the application is not allowed to read the current target
     * as defined by the security policy of the platform
     */
    public ConnectionParameters getCurrentTarget() throws IncompleteTargetException {
        return currentTarget;
    }

    public void setTarget(ConnectionParameters target) throws IncompleteTargetException, PermissionDeniedException {
        if (target.getTarget() == null || target.getUsername() == null || target.getPassword() == null) {
            throw new IncompleteTargetException("Incomplete Target");
        }
        currentTarget = target;
    }

    /**
     * Set the target for connections to the default. 
     *
     * @throws PermissionDeniedException if this application does not own the resource
     * @throws SecurityException if the application is not allowed to connect to the default target
     */
    public void setTargetToDefault() throws PermissionDeniedException {
    	currentTarget = defaultTarget;
    }

    public int getConnectedTime() {
        if (connected) {
            return (int) (System.currentTimeMillis() - starttime) / 1000;
        }
        else
            return -1;
    }

    public ResourceClient getClient() {
        return resourceClient;
    }

    public void addConnectionListener(ConnectionListener l) {
        if (!listenerObjects.contains(l)) {
            listenerObjects.add(l);
        }
    }

    public void removeConnectionListener(ConnectionListener l) {
        listenerObjects.remove(l);
    }

    private void fireConnectionEvent(ConnectionRCEvent e) {
        // backwards so we get the last added
        for (int i = listenerObjects.size() - 1; i > -1; i--) {
            ConnectionListener ali = (ConnectionListener) listenerObjects.get(i);
            ali.connectionChanged(e);
        }
    }

    public void run() {
        try {
            Thread.sleep(FAKED_CONNECION_TIME);
        }
        catch (InterruptedException ex) {
            System.out.println("XleTView: Exception in Timer Thread");            
            ex.printStackTrace();
        }
        starttime = System.currentTimeMillis();
        connected = true;
        fireConnectionEvent(new ConnectionEstablishedEvent(this));
    }

}
