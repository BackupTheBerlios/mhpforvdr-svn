
package org.dvb.si;

/*An interface that can be implemented by objects representing DVB services. Allows 
applications to obtain the textual service identi ers related to a 
service. */

public interface TextualServiceIdentifierQuery {

/*
Returns the textual service identi ers related to this object. Returns: an array of String objects containing the 
textual service identi ers or null if none are present. Since: MHP1.0.1 */
public java.lang.String[] getTextualServiceIdentifiers();



}
