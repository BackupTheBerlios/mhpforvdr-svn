
package org.dvb.si;

/*This interface (together with the SITransportStreamBAT interface) represents a sub-table 
of the Bouquet Association Table (BAT) describing a particular bouquet. Each object that 
implements the SIBouquet interface is identi ed by the identi er 
bouquet_id. */

public interface SIBouquet extends SIInformation {

/*
Get the identi cation. Returns: The bouquet identi cation of this bouquet. */
public int getBouquetID();


/*
This method de nes extra semantics for the SIInformation.getDescriptorTags method. If the BAT sub-table on which this 
SIBouquet object is based consists of multiple sections, then this method returns the descriptor tags in the order they 
appear when concatenating the descriptor loops of the different sections. Overrides: getDescriptorTags() in interface 
SIInformation Returns: The tags of the descriptors actually broadcast for the object (identi ed by their tags). See 
Also: SIInformation, getDescriptorTags() */
public short[] getDescriptorTags();


/*
This method returns the name of this bouquet. The name is extracted from the bouquet_name_descriptor or optionally from 
the multilingual_bouquet_name_descriptor. When thisinformation is not available "" is returned. All control characters 
as de ned in ETR 211 are ignored. For each character the DVB-SI 8 bit character code is mapped to the appropriate 
Unicode representation Returns: The bouquet name of this bouquet. */
public java.lang.String getName();


/*
This method returns the short name (ETR 211) of this bouquet without emphasis marks. The name is extracted from the 
bouquet_name_descriptor or optionally from the multilingual_bouquet_name_descriptor. When this information is not 
available "" is returned. For each character the DVB-SI 8 bit character code is mapped to the appropriate Unicode 
representation. Returns: The short bouquet name of this bouquet. */
public java.lang.String getShortBouquetName();


/*
Get a list of DvbLocators identifying the services that belong to the bouquet. Returns: An array of DvbLocators 
identifying the services See Also: org.davic.net.dvb.DvbLocator, SIService */
public org.davic.net.dvb.DvbLocator[] getSIServiceLocators();


/*
This method de nes extra semantics for the SIInformation.retrieveDescriptors method ( rst prototype). If the BAT 
sub-table on which this SIBouquet object is based consists of multiple sections, then this method returns the requested 
descriptors in the order they appear when concatenating the descriptor loops of the different sections. Overrides: 
retrieveDescriptors(short, Object, SIRetrievalListener) in interface SIInformation Parameters: retrieveMode - Mode of 
retrieval indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if 
available and if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY).appData - An 
object supplied by the application. This object will be delivered to the listener when the request completes. The 
application can use this objects for internal communication purposes. If the application does not need any application 
data, the parameter can be null. listener - SIRetrievalListener that will receive the event informing about the 
completion of the request. Returns: An SIRequest object Throws:SIIllegalArgumentException - thrown if the retrieveMode 
is invalid See Also: SIInformation, retrieveDescriptors(short, Object, SIRetrievalListener) */
public SIRequest retrieveDescriptors(short retrieveMode, java.lang.Object appData, SIRetrievalListener 
listener);


/*
This method de nes extra semantics for the SIInformation.retrieveDescriptors method (second prototype). If the BAT 
sub-table on which this SIBouquet object is based consists of multiple sections, then this method returns the requested 
descriptors in the order they appear when concatenating the descriptor loops of the different sections. Overrides: 
retrieveDescriptors(short, Object, SIRetrievalListener, short[]) in interface SIInformation Parameters: retrieveMode - 
Mode of retrieval indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache 
if available and if not from the stream(FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - 
An object supplied by the application. This object will be delivered to the listener when the request completes. The 
application can use this objects for internal communication purposes. If the application does not need any application 
data, the parameter can be null. listener - SIRetrievalListener that will receive the event informing about the 
completion of the request. someDescriptorTags - A list of tags for descriptors (identi ed by their tags) the application 
is interested in. If the array contains -1 as its one and only element, the application is interested in all 
descriptors. If someDescriptorTags is null, the application is not interested in descriptors. All values that are out of 
the valid range for descriptor tags (i.e. 0...255) are ignored, except for the special meaning of -1 as the only element 
in the array.Returns: An SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is invalid See 
Also: SIInformation, retrieveDescriptors(short, Object, SIRetrievalListener, 
short[]) */
public SIRequest retrieveDescriptors(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] 
someDescriptorTags);


/*
Retrieve information associated with transport streams belonging to the bouquet. The SIIterator that is returned with 
the event when the request completes successfully will contain one or more objects that implement the 
SITransportStreamBAT interface. Parameters: retrieveMode - Mode of retrieval indicating whether the data should be 
retrieved only from the cache (FROM_CACHE_ONLY), from the cache if available and if not from the stream 
(FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY).appData - An object supplied by the application. 
This object will be delivered to the listener when the request completes. The application can use this objects for 
internal communication purposes. If the application does not need any application data, the parameter can be null. 
listener - SIRetrievalListener that will receive the event informing about the completion of the request. 
someDescriptorTags - A list of hints for descriptors (identi ed by their tags) the application is interested in. If the 
array contains -1 as its one and only element, the application is interested in all descriptors. If someDescriptorTags 
is null, the application is not interested in descriptors. All values that are out of the valid range for descriptor 
tags (i.e. 0...255) are ignored, except for the special meaning of -1 as the only element in the array.Returns: An 
SIRequest object Throws: SIIllegalArgumentException - thrown if the retrieveMode is invalid See Also: SIRequest, 
SIRetrievalListener, SITransportStreamBAT, DescriptorTag */
public SIRequest retrieveSIBouquetTransportStreams(short retrieveMode, java.lang.Object appData, SIRetrievalListener 
listener, short[] someDescriptorTags);



}
