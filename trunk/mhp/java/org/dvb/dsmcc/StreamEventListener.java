package org.dvb.dsmcc;

/**
* @author tejopa
* @date 13.2.2004
* @status fully implemented
* @module internal
*/
public interface StreamEventListener extends java.util.EventListener {

	public void receiveStreamEvent(StreamEvent e);

}
