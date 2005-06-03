
package javax.tv.service.navigation;

/*

This class represents values of <code>ServiceComponent</code>
 stream types (e.g., "video", "audio", "subtitles", "data",
 "sections", etc.).

*/
public class StreamType extends java.lang.Object {

/*
 
 Video component. 
 */

public static final StreamType  VIDEO = new StreamType("VIDEO");


/*
 
 Audio component. 
 */

public static final StreamType  AUDIO = new StreamType("AUDIO");


/*
 
 Subtitles component. 
 */

public static final StreamType  SUBTITLES = new StreamType("SUBTITLES");


/*
 
 Data component. 
 */

public static final StreamType  DATA = new StreamType("DATA");


/*
 
 MPEG sections component. 
 */

public static final StreamType  SECTIONS = new StreamType("SECTIONS");


/*
 
 Unknown component. */

public static final StreamType  UNKNOWN = new StreamType("UNKNOWN");


/*
 
 Creates a stream type object. 
 Parameters:  name - The string name of this type (e.g., "VIDEO"). 
 
 */

String name;

protected StreamType (java.lang.String name){
   this.name=name;
}


/*
 
 Provides the string name of the type. For the type objects
 defined in this class, the string name will be identical to the
 class variable name. 
 Overrides:  toString in class java.lang.Object 
 
 
 Returns: The string name of the type. 
 
 
*/

public java.lang.String toString (){
   return name;
}



}

