package org.dvb.dsmcc;

import org.openmhp.util.Out;

/**
* @author tejopa
* @date 13.2.2004
* @status fully implemented
* @module internal
*/
public class NPTRemovedEvent extends NPTStatusEvent {

	public NPTRemovedEvent(DSMCCStream source){
		super(source);
		Out.printMe(Out.TRACE);
	}

}
