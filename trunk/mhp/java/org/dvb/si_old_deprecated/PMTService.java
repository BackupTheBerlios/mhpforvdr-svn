
package org.dvb.si;

/*This interface represents a particular service carried by a transport stream. The 
information is retrieved from the PMT table. Each object that implements the PMTService 
interface is identi ed by the combination of the following identi ers: 
original_network_id, transport_stream_id, service_id. */

public interface PMTService extends SIInformation {

/*
Gets a DvbLocator that identi es this service Returns: The DvbLocator of this 
service */
public org.davic.net.dvb.DvbLocator getDvbLocator();


/*
Get the original network identi cation. Returns: The original network identi cation identi 
er. */
public int getOriginalNetworkID();


/*
Get the PCR pid. Returns: The PCR pid. */
public int getPcrPid();


/*
Get the service identi cation. Returns: The service identi cation identi er. */
public int getServiceID();


/*
Get the transport stream identi cation. Returns: The transport stream identi cation identi 
er. */
public int getTransportStreamID();


/*
Retrieve information associated with the elementary streams which compose this service from the Program Map Table (PMT). 
The SIIterator that is returned with the event when the request completes successfully will contain one or more objects 
that implement the PMTElementaryStream interface. If no matching object was found,the appropriate one of the following 
events is sent: ObjectNotInCacheEvent ObjectNotInTableEvent or TableNotFoundEvent. Parameters: retrieveMode - Mode of 
retrieval indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if 
available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - An 
object supplied by the application. This object will be delivered to the listener when the request completes. The 
application can use this objects for internal communication purposes. If the application does not need any application 
data, the parameter can be null.listener - SIRetrievalListener that will receive the event informing about the 
completion of the request. somePMTDescriptorTags - A list of hints for descriptors (identi ed by their tags) the 
application is interested in. If the array contains -1 as its one and only element, the application is interested in all 
descriptors. If somePMTDescriptorTags is null, the application is not interested in descriptors. All non applicable tag 
values are ignored. Returns: An SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is 
invalid See Also: SIRequest, SIRetrievalListener, PMTElementaryStream */
public SIRequest retrievePMTElementaryStreams(short retrieveMode, java.lang.Object appData, SIRetrievalListener 
listener, short[] somePMTDescriptorTags);



}
