
package org.davic.mpeg;

/*This class represents one elementary stream within a transport stream. If an elementary 
stream belongs to multiple services then it will be represented by multiple instances one 
instance for each parent service. */

public abstract class ElementaryStream {

protected Service service;
protected int pid;
protected int assoc_tag;

public ElementaryStream(Service service, int pid, int assoc_tag) {
   this.service=service;
   this.pid=pid;
   this.assoc_tag=assoc_tag;
}
/*
Returns: a reference to the Service object that represents the service in which this Elementary Stream is 
contained. */
public Service getService() {
   return service;
}

/*
Returns: the PID value of MPEG-2 Transport Stream packets that carry this elementary 
stream. */
public int getPID() {
   return pid;
}

/*
Returns: the DSM-CC association tag of this elementary stream, or null if none is 
present. */
public Integer getAssociationTag() {
   return new Integer(assoc_tag);
}


}
