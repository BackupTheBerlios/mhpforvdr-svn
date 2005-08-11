package org.dvb.dsmcc;

import org.openmhp.util.Out;

/**
* @author tejopa
* @date 13.2.2004
* @status fully implemented
* @module internal
*/
public class StreamEvent extends java.util.EventObject {

	private String eventname;
	private int eventid;
	private long eventnpt;
	private byte[] eventdata;

	public StreamEvent(DSMCCStreamEvent source, long npt, String name, int id, byte[] data){
		super(source);
		eventnpt = npt;
		eventname = name;
		eventid = id;
		eventdata = data;
		Out.printMe(Out.TRACE);
	}

	public java.lang.Object getSource() {
		Out.printMe(Out.TRACE);
		return super.getSource();
	}

	public String getEventName(){
		Out.printMe(Out.TRACE);
		return eventname;
	}

	public int getEventId(){
		Out.printMe(Out.TRACE);
		return eventid;
	}

	public long getEventNPT(){
		Out.printMe(Out.TRACE);
		return eventnpt;
	}

	public byte[] getEventData(){
		Out.printMe(Out.TRACE);
		return eventdata;
	}

}
