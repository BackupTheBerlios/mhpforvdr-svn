/*

 This file is part of XleTView 
 Copyright (C) 2003 Martin Svedén
 
 This is free software, and you are 
 welcome to redistribute it under 
 certain conditions;

 See LICENSE document for details.

*/


package org.dvb.media;

import javax.tv.locator.InvalidLocatorException;
import javax.tv.service.selection.InvalidServiceComponentException;
import javax.tv.service.selection.InsufficientResourcesException;
import javax.tv.locator.Locator;

public interface DVBMediaSelectControl extends javax.tv.media.MediaSelectControl {

public void selectServiceMediaComponents(Locator l) throws
                        InvalidLocatorException,InvalidServiceComponentException,
                        InsufficientResourcesException;

}

