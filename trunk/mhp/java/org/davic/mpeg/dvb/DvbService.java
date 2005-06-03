
package org.davic.mpeg.dvb;

/*This class represents one service within a transport stream as used in 
DVB. */

public class DvbService extends org.davic.mpeg.Service {

public DvbService (org.davic.mpeg.TransportStream trstr, int servId) {
   super(trstr, servId);
}

/*
Parameters: componentTag - the value of the component tag that is associated with the elementary stream. Returns: a 
reference to the DvbElementaryStream object that represents the Elementary Stream that is associated with the provided 
component tag. Null is returned if the specified component tag is not present within this service or if the required 
information is not available. */
public DvbElementaryStream retrieveDvbElementaryStream(int componentTag) {
   return null;
}


}
