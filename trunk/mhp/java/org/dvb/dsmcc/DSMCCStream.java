package org.dvb.dsmcc;

import java.io.IOException;

/**
* @author tejopa
* @date 7.3.2004
* @status not implemented
* @module internal
* @HOME
*/
public class DSMCCStream {


	public DSMCCStream(DSMCCObject aDSMCCObject) throws NotLoadedException, IllegalObjectTypeException{
	}

	public DSMCCStream(String path) throws IOException, IllegalObjectTypeException{
	}

	public DSMCCStream(String path, String name) throws IOException, IllegalObjectTypeException	{
	}


	public long getDuration() {
		return (long)0;
	}



	public long getNPT() throws MPEGDeliveryException{
		return (long)0;
	}

	public org.davic.net.Locator getStreamLocator()	{
		return null;
	}

	public boolean isMPEGProgram(){
		return true;
    }

	public boolean isAudio(){
		return false ;
    }

	public boolean isVideo(){
		return false;
    }

	public boolean isData() { return true; }

	public NPTRate getNPTRate() throws MPEGDeliveryException { return null; }

	public void addNPTListener(NPTListener l){};

	public void removeNPTListener(NPTListener l) {};
}
