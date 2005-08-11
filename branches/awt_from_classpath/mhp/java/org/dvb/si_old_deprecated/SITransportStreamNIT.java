
package org.dvb.si;

/*This interface represents information about transport streams that has been retrieved from 
a NIT table. All descriptor accessing methods return descriptors retrieved from a NIT 
table. Methods in SIDatabase and SINetwork for retrieving transport streams return objects 
that implement this interface. */

public interface SITransportStreamNIT extends SITransportStream {

/*
Get the identi cation of the network this transport stream is part of. Returns: The network identi cation identi 
er. */
public int getNetworkID();



}
