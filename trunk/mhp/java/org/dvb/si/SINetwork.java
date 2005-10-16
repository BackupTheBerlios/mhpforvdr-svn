
package org.dvb.si;

/*This interface (together with the SITransportStreamNIT interface) represents a sub-table 
of the Network Information Table (NIT) describing a particular network. Each object that 
implements the SINetwork interface is identi ed by the identi er 
network_id. */

public interface SINetwork extends SIInformation {

/*
This method de nes extra semantics for the SIInformation.getDescriptorTags method. If the NIT subtable on which this 
SINetwork object is based consists of multiple sections, then this method returns the descriptor tags in the order they 
appear when concatenating the descriptor loops of the different sections. Overrides: getDescriptorTags() in interface 
SIInformation Returns: The tags of the descriptors actually broadcast for the object (identi ed by their 
tags). */
public short[] getDescriptorTags();


/*
This method returns the name of this network. The name is extracted from the network_name_descriptor or optionally from 
the multilingual_network_name_descriptor. When this information is not available "" is returned. All control characters 
as de ned in ETR 211 are ignored. For each character the DVB-SI 8 bit character code is mapped to the appropriate 
Unicode representation. Returns: The network name of this network. */
public java.lang.String getName();


/*
Get the identi cation of this network. Returns: The network identi cation identi 
er. */
public int getNetworkID();


/*
This method returns the short name (ETR 211) of this network without emphasis marks. The name is extracted from the 
network_name_descriptor or optionally from the multilingual_network_name_descriptor. When this information is not 
available "" is returned. For each character the DVB-SI 8 bit character code is mapped to the appropriate Unicode 
representation. Returns: The short network name of this network. */
public java.lang.String getShortNetworkName();


/*
This method de nes extra semantics for the SIInformation.retrieveDescriptors method ( rst prototype). If the NIT 
sub-table on which this SINetwork object is based consists of multiple sections, then this method returns the requested 
descriptors in the order they appear when concatenating the descriptor loops of the different sections. Overrides: 
retrieveDescriptors(short, Object, SIRetrievalListener) in interface SIInformation Parameters: retrieveMode - Mode of 
retrieval indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if 
available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY).appData - An 
object supplied by the application. This object will be delivered to the listener when the request completes. The 
application can use this objects for internal communication purposes. If the application does not need any application 
data, the parameter can be null. listener - SIRetrievalListener that will receive the event informing about the 
completion of the request. Returns: An SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode 
is invalid */
public SIRequest retrieveDescriptors(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener) throws SIIllegalArgumentException;


/*
This method de nes extra semantics for the SIInformation.retrieveDescriptors method (second prototype). If the NIT 
sub-table on which this SINetwork object is based consists of multiple sections, then this method returns the requested 
descriptors in the order they appear when concatenating the descriptor loops of the different sections. Overrides: 
retrieveDescriptors(short, Object, SIRetrievalListener, short[]) in interface SIInformation Parameters: retrieveMode - 
Mode of retrieval indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache 
if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - 
An object supplied by the application. This object will be delivered to the listener when the request completes. The 
application can use this objects for internal communication purposes. If the application does not need any application 
data, the parameter can be null. listener - SIRetrievalListener that will receive the event informing about the 
completion of the request. */
public SIRequest retrieveDescriptors(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] someDescriptorTags) throws SIIllegalArgumentException;


/*This method de nes extra semantics for the SIInformation.retrieveDescriptors method (second prototype).
 If the NIT sub-table on which this SINetwork object is based consists of multiple sections,
 then this method returns the requested descriptors in the order they appear when concatenating 
 the descriptor loops of the different sections. Overrides: retrieveDescriptors(short, Object, 
 SIRetrievalListener, short[]) in interface SIInformation Parameters: retrieveMode - Mode of retrieval 
 indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache 
 if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). 
 appData - An object supplied by the application. This object will be delivered to the listener when 
 the request completes. The application can use this objects for internal communication purposes. 
 If the application does not need any application data, the parameter can be null. 
 listener - SIRetrievalListener that will receive the event informing about the completion of the request.
someDescriptorTags - A list of tags for descriptors (identi ed by their tags) the application is interested in. If the 
array contains -1 as its one and only element, the application is interested in all descriptors. If someDescriptorTags 
is null, the application is not interested in descriptors. All values that are out of the valid range for descriptor 
tags (i.e. 0...255) are ignored, except for the special meaning of -1 as the only element in the array. Returns: An 
SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is 
invalid
 */
public SIRequest retrieveSITransportStreams(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] someDescriptorTags) throws SIIllegalArgumentException;



}
