
package javax.tv.service.navigation;

/*

This interface represents an abstraction of an elementary
 stream. It provides information about individual components of a
 service.  Generally speaking, a service component carries content
 such as media (e.g. as audio or video) or data.  Content within a
 <code>ServiceComponent</code> may include <code>Xlet</code>s.

*/
public interface ServiceComponent extends javax.tv.service.SIElement {

/*
 
 Returns a name associated with this component. The Component Descriptor
 (DVB) or Component Name Descriptor (ATSC) may be used if present. A
 generic name (e.g., "video", "first audio", etc.) may be used otherwise. 
 
 
 
 Returns: A string representing the component name or an empty string
 if no name can be associated with this component. 
 
 
 */

public java.lang.String getName ();


/*
 
 Identifies the language used for the elementary stream. The
 associated language is indicated using a language code. This is
 typically a three-character language code as specified by ISO
 639.2/B, but the code may be system-dependent. 
 
 
 
 Returns: A string representing a language code defining the
 language associated with this component. An empty string is
 returned when there is no language associated with this component. 
 
 
 */

public java.lang.String getAssociatedLanguage ();


/*
 
 Provides the stream type of this component. (For example, "video",
 "audio", etc.) 
 
 
 
 Returns: Stream type of this component. 
 
 
 */

public javax.tv.service.navigation.StreamType  getStreamType ();


/*
 
 Provides the Service object to which this
 ServiceComponent belongs. The result may be
 null if the Service cannot be determined. 
 
 
 
 Returns: The Service to which this
 ServiceComponent belongs. 
 
 
*/

public javax.tv.service.Service  getService ();



}

