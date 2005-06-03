
package org.dvb.si;

/*This interface groups the common features of SIBouquet, SINetwork, SITransportStream, 
SIService, PMTService, SIEvent, SITime and PMTElementaryStream. Each SIInformation 
instance represents a sub-table (part). Any method accessing descriptors will retrieve 
descriptors from the same sub-table version as the SIInformation instance. When this 
version is no longer available, an SITableUpdatedEvent is 
returned. */

public interface SIInformation extends javax.tv.service.SIRetrievable {
//the SIRetrievable implementation is an internal extension
//for the convergence of the two APIs

//keep FROM_... constants in sync with DvbSi's request.h

/*
Constant for retrieve mode parameter of the retrieve methods. When FROM_CACHE_ONLY mode is speci ed, the data will be 
retrieved only if it is in the cache. Otherwise, SINotInCacheEvent will be delivered to the listener. No stream access 
is done in this case. */
public static final short FROM_CACHE_ONLY = 0;


/*
Constant for retrieve mode parameter of the retrieve methods. When FROM_CACHE_OR_STREAM mode is speci ed, the data will 
be retrieved from cache if it is present in the cache, otherwise it will be retrieved from the 
stream. */
public static final short FROM_CACHE_OR_STREAM = 1;


/*
Constant for retrieve mode parameter of the retrieve methods. When FROM_STREAM_ONLY mode is speci ed, the data will be 
retrieved directly from the stream and no cache access is tried  rst. This mode is meaningful only if the application 
knows that the information is not in the cache or that the information in the cache is no longer valid, but the 
implementation of the SI database may not be aware of the invalidity of the cached data. If the application has got the 
noti cation of the existence of an updated version through the listener mechanism in this API, the implementation of the 
SI database is aware of the version change and the application should specify the FROM_CACHE_OR_STREAM mode to be able 
to retrieve the data faster if the updated version has already been loaded to the cache by the SI database 
implementation. */
public static final short FROM_STREAM_ONLY = 2;


/*
Return true when the information contained in the object that implements this interface was  ltered from an 'actual' 
table or from a table with no 'actual/other' distinction. Returns: true if the information comes from an 'actual' table 
or from a table with no 'actual/other' distiction, otherwise returns false */
public boolean fromActual();


/*
Return the org.davic.mpeg.TransportStream object the information contained in the object that implements that interface 
was  ltered from. Returns: The org.davic.mpeg.TransportStream object the information was  ltered from. See Also: 
org.davic.mpeg.TransportStream */
public org.davic.mpeg.TransportStream getDataSource();


/*
Get the tags of all descriptors that are part of this version of this object. The tags are returned in the same order as 
the descriptors are broadcast. This method returns also the tags of descriptors that were not hinted at and that are not 
necessarily present in the cache. If there are no descriptors associated with this SIInformation object, this method 
returns an empty array whose length is 0. Returns: The tags of the descriptors actually broadcast for the object (identi 
ed by their tags). See Also: DescriptorTag */
public short[] getDescriptorTags();


/*
Return the root of the hierarchy the object that implements this interface belongs to. Returns: The root of the 
hierarchy. */
public SIDatabase getSIDatabase();


/*
Return the time when the information contained in the object that implements this interface was last updated. Returns: 
The date of the last update. */
public java.util.Date getUpdateTime();


/*
This method retrieves all descriptors in the order the descriptors are broadcast. This method is asynchronous and the 
completion of the method will be signalled by an SISuccessfulRetrieveEvent being sent to listener. Any retrieved 
descriptors are found in the SIIterator returned by the getResult method of that event. If descriptors are found then 
this iterator will contain Descriptor objects. If there are no matching descriptors, this iterator will contain no 
objects. Parameters: retrieveMode - Mode of retrieval indicating whether the data should be retrieved only from the 
cache (FROM_CACHE_ONLY), from the cache if available and if not from the stream (FROM_CACHE_OR_STREAM), or always from 
the stream (FROM_STREAM_ONLY). appData - An object supplied by the application. This object will be delivered to the 
listener when the request completes. The application can use this objects for internal communication purposes. If the 
application does not need any application data, the parameter can be null. listener - SIRetrievalListener that will 
receive the event informing about the completion of the request.Returns: An SIRequest object Throws: 
SIIllegalArgumentException - thrown if the retrieveMode is invalid */
public SIRequest retrieveDescriptors(short retrieveMode, java.lang.Object appData, SIRetrievalListener 
listener);


/*
Retrieve a set of descriptors. This method retrieves all or a set of descriptors in the order the descriptors are 
broadcast. The tag values included in the someDescriptorParameters parameter are used for  ltering the descriptors that 
are returned. Only those descriptors whose tag value is included in the someDescriptorParameters array are retrieved, 
unless the someDescriptorParameters array contains -1 as its one and only item in which case all descriptors related to 
this object are retrieved. If the list of tags is a subset of the one hinted to the underlying implementation (in the 
request which created the object on which the method is called), this is likely to increase the ef ciency of the 
(optional) caching mechanismThis method is asynchronous and the completion of the method will be signalled by an 
SISuccessfulRetrieveEvent being sent to listener. Any retrieved descriptors are found in the SIIterator returned by the 
getResult method of that event. If descriptors are found then this iterator will contain Descriptor objects. If there 
are no matching descriptors, this iterator will contain no objects. Parameters: retrieveMode - Mode of retrieval 
indicating whether the data should be retrieved only from the cache (FROM_CACHE_ONLY), from the cache if available and 
if not from the stream (FROM_CACHE_OR_STREAM), or always from the stream (FROM_STREAM_ONLY). appData - An object 
supplied by the application. This object will be delivered to the listener when the request completes. The application 
can use this objects for internal communication purposes. If the application does not need any application data, the 
parameter can be null. listener - SIRetrievalListener that will receive the event informing about the completion of the 
request. someDescriptorTags - - Descriptor tag values of descriptors that are used for  ltering descriptors from the 
descriptors included in the SI table item corresponding to this SIInformation object. If the array contains -1 as its 
one and only element, all descriptors related to this object are retrieved. Returns: An SIRequest object Throws: 
SIIllegalArgumentException - thrown if the retrieveMode is invalid */
public SIRequest retrieveDescriptors(short retrieveMode, java.lang.Object appData, SIRetrievalListener listener, short[] 
someDescriptorTags);



}
