package org.dvb.dsmcc;

import org.openmhp.util.Out;

/**
* @author tejopa
* @date 13.2.2004
* @status fully implemented
* @module internal
*/
public class NPTPresentEvent extends NPTStatusEvent {

	public NPTPresentEvent(DSMCCStream source){
		super(source);
		Out.printMe(Out.TRACE);
	}

}
