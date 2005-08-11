
package org.davic.mpeg.sections;

/*
 The SectionFilterListener interface is implemented by 
 classes which wish to be informed about events relating 
 to section filters. 
*/

public interface SectionFilterListener {
public abstract void sectionFilterUpdate(SectionFilterEvent event) 

/*When a section filter event 
happens, the sectionFilterUpdate method of all listeners connected to the 
originating object will be called. */

}