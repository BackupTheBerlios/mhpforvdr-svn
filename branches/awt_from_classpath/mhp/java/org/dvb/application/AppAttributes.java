
package org.dvb.application;

/*The AppAttributes class is a mapping of various information about a registered 
application.For applications which are signalled in an AIT,the mapping between the values 
returned by methods in this class and the  elds and descriptors of the AIT shall be as 
speci  ed in the main body of this speci  cation. Instances of objects implementing this 
interface are immutable and populated before the instance is  rst returned to an 
application. */

public interface AppAttributes {

/*
The DVB registered value for all DVB-HTML applications. */
public static final int DVB_HTML_application = 2;


/*
The DVB registered value for all DVB-J applications. */
public static final int DVB_J_application = 1;

//my own homebrew values for local applications
public static final int LOCAL_DVB_J_application = 0xffff1;
public static final int LOCAL_DVB_HTML_application = 0xffff2;


/*
This method returns an object encapsulating the information about the icon(s)for the application. Returns: the 
information related to the icons that are attached to the application or null if no icon information is 
available */
public AppIcon getAppIcon();


/*
This method returns the application identi  er.depending on the Returns: the application identi  
er */
public AppID getIdentifier();


/*
This method determines whether the application is bound to a single service. Returns: true if the application is bound 
to a single service,false otherwise. */
public boolean getIsServiceBound();


/*
This method returns the name of the application.If the default language (as speci  ed in user preferences)is in the set 
of available language /name pairs then the name in that language shall be returned.Otherwise this method will return a 
name which appears in that set on a "best-effort basis". Returns: the name of the 
application */
public java.lang.String getName();


/*
This method returns the name of the application in the language which is speci  ed by the parameter passed as an 
argument.If the language speci  ed is not in the set of available language /name pairs then an exception shall be 
thrown. Parameters: iso639code -the speci  ed language,encoded as per ISO 639. Returns: returns the name of the 
application in the speci  ed language Throws: LanguageNotAvailableException -if the name is not available in the 
language speci  ed */
public java.lang.String getName(java.lang.String iso639code) throws LanguageNotAvailableException;


/*
This method returns all the available names for the application together with their ISO 639 language code. Returns:the 
possible names of the application,along with their ISO 639 language code.The  rst string in each sub-array is the ISO 
639 language code.The second string in each sub-array is the corresponding application 
name. */
public java.lang.String[][] getNames();


/*
This method returns the priority of the application. Returns: the priority of the 
application. */
public int getPriority();


/*
This method returns those minimum pro  les required for the application may execute.Pro  le names shall be encoded using 
the same encoding speci  ed elsewhere in this speci  cation as input for use with the java.lang.System.getProperty 
method to query if a pro  le is supported by this platform. For example,for implementations conforming to the  rst 
version of the speci  cation,the translation from AIT signaling values to strings shall be as follows: " '1' in the 
signaling will be translated into 'mhp.profile.enhanced_broadcast' " '2' in the signaling will be translated into 
'mhp.profile.interactive_broadcast' Only pro  les known to this particular MHP terminal shall be returned.Hence the 
method can return an array of size zero where all the pro  les on which an application can execute an application are 
unknown. Returns: an array of Strings,each String describing a pro  le. */
public java.lang.String[] getProfiles();


/*
The following method is included for properties that do not have explicit property accessors.The naming of properties 
and their return values are described in the main body of this speci  cation. Parameters: index -a property name 
Returns: either the return value corresponding to the property name or null if the property name is 
unknown */
public java.lang.Object getProperty(java.lang.String index);


/*
This method returns the locator of the Service describing the application.For an application transmitted on a remote 
connection,the returned locator shall be the service for that remote connection.For applications not transmitted on a 
remote connection,the service returned shall be the currently selected service of the service context within which the 
application calling the method is running. Returns: the locator of the Service describing the 
application. */
public org.davic.net.Locator getServiceLocator();


/*
This method returns the type of the application (as registered by DVB). Returns: the type of the application (as 
registered by DVB). */
public int getType();


/*
This method returns an array of integers containing the version number of the speci  cation required to run this 
application at the speci  ed pro  le. Parameters: profile -a pro  le encoded as described in the main body of this speci 
 cation for use with java.lang.System.getProperty Returns: an array of integers,containing the major,minor and micro 
values (in that order)required for the speci  ed pro  le. Throws: IllegalProfileParameterException -thrown if the pro  
le speci  ed is not one of the minimum pro  les required for the application to 
execute. */
public int[] getVersions(java.lang.String profile);


/*
This method determines whether the application is startable or not.An Application is not startable if any of the 
following apply. " The application is transmitted on a remote connection. " The caller of the method does not have the 
Permissions to start it. " At the moment when the method is called, the implementation has detected that this 
application is not available any more. */
public boolean isStartable();



}
