package org.havi.ui;

import java.awt.Dimension;
import org.davic.resources.*;
import org.havi.ui.event.*;

import java.util.LinkedList;

//Code from OpenMHP, license is LGPL
//made thread safe
//See HScreenDevice.java_nist_deprecated for the NIST implementation
 /**
 * @author tejopa
 * @date 11.3.2004 
 * @status partially implemented
 * @module internal, video, graphics
 * @TODO map through adaptation layer: aspect ratio, id, reserving
 */
public abstract class HScreenDevice implements ResourceProxy, ResourceServer {

	private LinkedList screenConfigurationListeners;
	private LinkedList resourceStatusListeners;
    private ResourceClient currentResourceClient = null;
    private HScreenConfigTemplate hScreenConfigTemplate;

    public HScreenDevice()    {    }

    public synchronized void addResourceStatusEventListener(ResourceStatusListener l) {
		if (resourceStatusListeners==null) {
			resourceStatusListeners = new LinkedList();
		}
		resourceStatusListeners.add(l);
    }

    public synchronized void addScreenConfigurationListener(HScreenConfigurationListener l) {
		if (screenConfigurationListeners==null) {
			screenConfigurationListeners = new LinkedList();	
		}
		screenConfigurationListeners.add(l);
    }

    public synchronized void addScreenConfigurationListener(HScreenConfigurationListener l, HScreenConfigTemplate hscreenconfigtemplate) {
        addScreenConfigurationListener(l);
   		//Out.printMe(Out.TODO,"check template and create event if needed, add template to some structure");
        hScreenConfigTemplate = hscreenconfigtemplate;
    }

    public ResourceClient getClient() {
        return currentResourceClient;
    }

    // Device dependent
    public abstract java.awt.Dimension getScreenAspectRatio();


    // Device dependent
    public abstract String getIDstring();
    /*public String getIDstring()    {
		Out.printMe(Out.TODO);
		return "this is not a unique HScreenDevice ID string";
	}

    public Dimension getScreenAspectRatio() {
		Out.printMe(Out.TODO);
        return new Dimension(720,576);
    }*/

    public synchronized void releaseDevice()    {
        if (resourceStatusListeners!=null) {
            HScreenDeviceReleasedEvent e = new HScreenDeviceReleasedEvent(currentResourceClient);
        	for (int i=0;i<resourceStatusListeners.size();i++) {
        		((ResourceStatusListener)resourceStatusListeners.get(i)).statusChanged(e);	
        	}
        }
        currentResourceClient = null;
    }

    public synchronized void removeResourceStatusEventListener(ResourceStatusListener l)     {
		if (resourceStatusListeners!=null) {
			resourceStatusListeners.remove(l);
		}
    }

    public synchronized void removeScreenConfigurationListener(HScreenConfigurationListener l) {
		if (screenConfigurationListeners!=null) {
			screenConfigurationListeners.remove(l);
		}

    }

    public synchronized boolean reserveDevice(ResourceClient client)    {
        currentResourceClient = client;
        if (resourceStatusListeners!=null) {
            HScreenDeviceReleasedEvent e = new HScreenDeviceReleasedEvent(currentResourceClient);
        	for (int i=0;i<resourceStatusListeners.size();i++) {
        		((ResourceStatusListener)resourceStatusListeners.get(i)).statusChanged(e);	
        	}
        }
    	return true;
    }

}

