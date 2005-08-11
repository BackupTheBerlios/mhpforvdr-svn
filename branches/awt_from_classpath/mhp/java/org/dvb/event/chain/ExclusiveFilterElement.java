package org.dvb.event.chain;

import org.dvb.event.RepositoryDescriptor;
import org.dvb.event.UserEvent;


public interface ExclusiveFilterElement {

public RepositoryDescriptor getRepositoryDescriptor();

public void dispatch(UserEvent e);

public void addType(int type);

}