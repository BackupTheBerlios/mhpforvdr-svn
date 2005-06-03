
package org.dvb.si;

/*This interface represents an elementary stream of a service. For each running service 
there is a PMT describing the elementary streams of the service. An object that implements 
this interface represents one such elementary stream. Each object that implements the 
PMTElementaryStream interface is identi ed by the combination of the identi ers 
original_network_id, transport_stream_id, service_id, component_tag (or 
elementary_PID). */

public interface PMTElementaryStream extends SIInformation {

/*
Get the component tag identi er. Returns: The component tag. If the elementary stream does not have an associated 
component tag, this method returns -2. */
public int getComponentTag();


/*
Gets a DvbLocator that identi es this elementary stream Returns: The DvbLocator of this elementary 
stream */
public org.davic.net.dvb.DvbLocator getDvbLocator();


/*
Get the elementary PID. Returns: The PID the data of elementary stream is sent on in the transport 
stream. */
public short getElementaryPID();


/*
Get the original network identi cation identi er. Returns: The original network identi 
cation. */
public int getOriginalNetworkID();


/*
Get the service identi cation identi er. Returns: The service identi cation. */
public int getServiceID();


/*
Get the stream type of this elemetary stream. Returns: The stream type (some of the possible values are de ned in the 
PMTStreamType interface). See Also: PMTStreamType */
public byte getStreamType();


/*
Get the transport stream identi cation identi er. Returns: The transport stream identi 
cation. */
public int getTransportStreamID();



}
