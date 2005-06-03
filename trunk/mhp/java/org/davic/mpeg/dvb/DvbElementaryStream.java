
package org.davic.mpeg.dvb;

/*This class represents one elementary stream within a transport stream as used in 
DVB. */

public class DvbElementaryStream extends org.davic.mpeg.ElementaryStream {

/*
 */
protected int componentTag;

public DvbElementaryStream(org.davic.mpeg.Service service, int pid, int assoc_tag, int componentTag) {
   super(service, pid, assoc_tag);
   this.componentTag=componentTag;
}

/*
Returns: the DVB component tag of this elementary stream, or null if none is 
present. */
public Integer getComponentTag() {
   return new Integer(componentTag);
}


}
