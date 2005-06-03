
package org.davic.mpeg.dvb;

/*This class represents an MPEG-2 Transport Stream as used in DVB with its associated 
Service Information (SI) as known to a decoder. */

public class DvbTransportStream extends org.davic.mpeg.TransportStream {

protected int onid;
protected int networkId;

public DvbTransportStream(int tid, int onid, int networkId) {
   super(tid);
   this.onid=onid;
   this.networkId=networkId;
}


/*
the original_network_id of this transport stream. */
public int getOriginalNetworkId() {
   return onid;
}

/*
the network_id of the network from which this MPEG-2 TS is accessed. */
public int getNetworkId() {
   return networkId;
}


}
