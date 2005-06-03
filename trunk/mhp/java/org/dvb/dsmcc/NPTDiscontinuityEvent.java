package org.dvb.dsmcc;

import org.openmhp.util.Out;

/**
* @author tejopa
* @date 13.2.2004
* @status fully implemented
* @module internal
*/
public class NPTDiscontinuityEvent extends NPTStatusEvent {

	private long first;
	private long last;

	public NPTDiscontinuityEvent( DSMCCStream source, long before, long after )	{
		super(source);
		first = before;
		last = after;
		Out.printMe(Out.TRACE);
	}

	public long getLastNPT() {
		Out.printMe(Out.TRACE);
		return last;
	}

	public long getFirstNPT() {
		Out.printMe(Out.TRACE);
		return first;
	}

}
