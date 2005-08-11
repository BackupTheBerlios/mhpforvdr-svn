package org.dvb.dsmcc;

import java.io.*;
import org.openmhp.util.Out;

/**
* @author tejopa
* @date 12.2.2004
* @status partially implemented
* @module internal
*/
public class DSMCCStreamEvent extends DSMCCStream {

	public DSMCCStreamEvent(DSMCCObject o) throws NotLoadedException, IllegalObjectTypeException	{
		super (o);
		Out.printMe(Out.TODO,"check from specs");
	}

	public DSMCCStreamEvent(String s) throws IOException, IllegalObjectTypeException	{
		super(s);
		Out.printMe(Out.TODO,"check from specs");
	}

	public DSMCCStreamEvent(String p, String n) throws IOException, IllegalObjectTypeException {
		super(p, n);
		Out.printMe(Out.TODO,"check from specs");
	}

	public synchronized int subscribe(String eventName, StreamEventListener l) throws UnknownEventException, InsufficientResourcesException	{
		Out.printMe(Out.TODO,"returns 0, check from specs");
		return 0;
	}

	public synchronized void unsubscribe(int eventId, StreamEventListener l) throws UnknownEventException{
		Out.printMe(Out.TODO,"check from specs");
	}

	public synchronized void unsubscribe(String eventName, StreamEventListener l) throws UnknownEventException{
		Out.printMe(Out.TODO,"check from specs");
	}

	public String [] getEventList()	{
		Out.printMe(Out.TODO,"check from specs");
		return null;
	}

}

