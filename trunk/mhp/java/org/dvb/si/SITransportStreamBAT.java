
package org.dvb.si;

/*This interface represents information about transport streams that has been retrieved from 
a BAT table. All descriptor accessing methods return descriptors retrieved from a BAT 
table. Methods in SIBouquet for retrieving transport streams return objects that implement 
this interface. */

public interface SITransportStreamBAT extends SITransportStream {

/*
Get the identi cation of the bouquet this transport stream is part of. Returns: The bouquet identi cation identi 
er. */
public int getBouquetID();


}
