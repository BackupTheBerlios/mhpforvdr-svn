package org.dvb.dsmcc;

import org.openmhp.util.Out;

/**
* @author tejopa
* @date 13.2.2004
* @status fully implemented
* @module internal
*/
public class NPTRateChangeEvent extends java.util.EventObject {

	NPTRate nptrate;

	public NPTRateChangeEvent( DSMCCStream source, NPTRate rate ){
		super((Object)source);
		nptrate = rate;
		Out.printMe(Out.TRACE);
	}

	public java.lang.Object getSource() {
		Out.printMe(Out.TRACE);
		return super.getSource();
	}

	public NPTRate getRate() {
		Out.printMe(Out.TRACE);
		return nptrate;
	}

}
