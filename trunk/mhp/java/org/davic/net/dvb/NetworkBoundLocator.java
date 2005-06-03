package org.davic.net.dvb;

public class NetworkBoundLocator extends DvbLocator implements org.davic.net.TransportDependentLocator  {
/*DVB Locator that is bound to a network. An object of this type identifies uniquely a given entity and the delivery system in which it is carried. For example, a service may be carried in both satellite and terrestrial networks and the DvbLocator identifying that service may be common, but both of them will have a different DvbNetworkBoundLocator. */

private int networkId;

public NetworkBoundLocator(DvbLocator unboundLocator, int networkId) throws javax.tv.locator.InvalidLocatorException {
   super(unboundLocator.getOriginalNetworkId(), unboundLocator.getTransportStreamId(), unboundLocator.getServiceId(),
         unboundLocator.getEventId(), unboundLocator.getComponentTags(), unboundLocator.getFilePath());
   this.networkId=networkId;
}
/*Constructor for a network bound locator Parameters: unboundLocator - an unbound DVB locator networkId - network identifier of the network Throws: InvalidLocatorException when the parameters to construct the locator wouldn't specify a valid locator (e.g. a numeric identifier out of range) */

public int getNetworkId() {
   return networkId;
}
/*Returns the the network_id Returns: network_id*/

}
