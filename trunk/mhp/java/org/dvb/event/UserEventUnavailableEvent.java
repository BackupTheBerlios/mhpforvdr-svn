package org.dvb.event;

import org.davic.resources.ResourceStatusEvent;

/**
* @author tejopa
* @date 13.2.2004
* @status fully implemented
* @module internal
*/
public class UserEventUnavailableEvent extends ResourceStatusEvent {

public UserEventUnavailableEvent(Object s) {
   super(s);
}

public Object getSource(){
        return super.getSource();
}

}

