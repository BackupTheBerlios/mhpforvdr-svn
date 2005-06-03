
package org.havi.ui;

/*The HVersion interface de  nes some versioning constants that are accessible by using the java.lang.System method 
getProperty,with the appropriate property name. Note that it is a valid implementation to return empty strings for the 
implementation,vendor and name strings. */

public interface HVersion {

/*
A string constant describing the HAVi implementation name,as returned via java.lang.System.getProperty(havi.implementation.name). */
public static final java.lang.String HAVI_IMPLEMENTATION_NAME = "org.havi.ui.HVersion.HAVI_IMPLEMENTATION_NAME";


/*
A string constant describing the HAVi implementation vendor,as returned via java.lang.System.getProperty(havi.implementation.vendor). */
public static final java.lang.String HAVI_IMPLEMENTATION_VENDOR = "org.havi.ui.HVersion.HAVI_IMPLEMENTATION_VENDOR";


/*
A string constant describing the HAVi implementation version,as returned via java.lang.System.getProperty(havi.implementation.version). */
public static final java.lang.String HAVI_IMPLEMENTATION_VERSION = "org.havi.ui.HVersion.HAVI_IMPLEMENTATION_VERSION";


/*
A string constant describing the HAVi speci  cation name,as returned via java.lang.System.getProperty(havi.speci  
cation.name). */
public static final java.lang.String HAVI_SPECIFICATION_NAME = "org.havi.ui.HVersion.HAVI_SPECIFICATION_NAME";


/*
A string constant describing the HAVi speci  cation vendor,as returned via java.lang.System.getProperty(havi.speci  
cation.vendor). */
public static final java.lang.String HAVI_SPECIFICATION_VENDOR = "org.havi.ui.HVersion.HAVI_SPECIFICATION_VENDOR";


/*
A string constant describing the HAVi speci  cation version,as returned via java.lang.System.getProperty(havi.speci  
cation.version). */
public static final java.lang.String HAVI_SPECIFICATION_VERSION = "org.havi.ui.HVersion.HAVI_SPECIFICATION_VERSION";



}
