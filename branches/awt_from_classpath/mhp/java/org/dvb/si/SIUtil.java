
package org.dvb.si;

/*This class contains SI related utility functions. */

public class SIUtil {


//TODO: implement



/*
This method converts a text string that is coded according to annex A of the DVB-SI speci cation (EN 300 468) to a Java 
String object. The text that must be converted is contained in 'dvbSIText' from index 'offset' to index 'offset+length- 
1' (inclusive). If the text that must be converted is not validly coded according to annex A of the DVB-SI speci cation, 
then the result is unde ned. Parameters: dvbSIText - The byte array that contains the string that must be converted. 
offset - The offset indicates the start of the DVB-SI text in dvbSIText. length - Length of the DVB-SI text in bytes. 
emphasizedPartOnly - If emphasizedPartOnly is true, then only the text that is marked as emphasized (using the character 
emphasis on [0x86] and character emphasis off [0x87] control codes) will be returned. Otherwise, the character emphasis 
codes will be ignored, and all of the converted text will be returned. Returns: The converted text. Throws: 
SIIllegalArgumentException - thrown if offset and/or offset+length-1 is not a valid index in 
dvbSIText. */

public static java.lang.String convertSIStringToJavaString(byte[] dvbSIText, int offset, int length, boolean 
emphasizedPartOnly) {
   return new String(dvbSIText, offset, length);
}


}
