package org.dvb.dsmcc;

import org.openmhp.util.Out;

/**
* @author tejopa
* @date 13.2.2004
* @status fully implemented
* @module internal
*/
public abstract class NPTStatusEvent extends java.util.EventObject {

	public NPTStatusEvent(DSMCCStream source) {
		super((Object)source);
		Out.printMe(Out.TRACE);
	}

	public Object getSource(){
		Out.printMe(Out.TRACE);
		return super.getSource();
	}
}
