package org.dvb.dsmcc;

/**
* @author tejopa
* @date 13.2.2004
* @status fully implemented
* @module internal
*/
public interface NPTListener extends java.util.EventListener {

	public void receiveRateChangedEvent( NPTRateChangeEvent e);

	public void receiveNPTStatusEvent( NPTStatusEvent e);
}
