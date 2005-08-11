
package org.havi.ui;

/*The HSound class is used to represent an audio clip. */

//TODO: The functionality of this class is unimplemented.

public class HSound {

/*
Creates an HSound object.See the class description for details of constructor parameters and default 
values. */
public HSound() {
}

/*
If the HSound object is playing /looping then it will be stopped.The dispose method then discards all sample resources 
used by the HSound object.This mechanism resets the HSound object to the state before a load()method was 
invoked. */
public void dispose() {
}

/*
Loads data synchronously into an HSound object from an audio sample in the speci  ed  le.If the object already contains 
data,this method shall perform the following sequence: " stop the sample if it is playing or looping. " dispose of the 
old data and any associated resources, as if the dispose() method had been called. " load the new data synchronously. 
Parameters: location -the name of a  le containing audio data in a recognized  le format. Throws: java.io.IOException 
-if the sample cannot be loaded due to an IO problem. java.lang.SecurityException -if the caller does not have suf  
cient rights to access the speci  ed audio sample. */
public void load(java.lang.String location) {
}

/*
Loads data synchronously into an HSound object from an audio sample indicated by a URL.If the object already contains 
data,this method shall perform the following sequence: " stop the sample if it is playing or looping. " dispose of the 
old data and any associated resources, as if the dispose() method had been called. " load the new data synchronously. 
Parameters: contents -a URL referring to the data to load. Throws: java.io.IOException -if the audio sample cannot be 
loaded due to an IO problem. java.lang.SecurityException -if the caller does not have suf  cient rights to access the 
speci  ed audio sample. */
public void load(java.net.URL contents) {
}

/*
Starts the HSound class looping from the beginning of its associated audio data.If the sample data has not been 
completely loaded,this method has no effect. When the audio data has been played in its entirety,then it should be 
played again from the beginning of its associated data,so as to cause a "seamless"continuous (in  nite)audio playback - 
until the next stop,or play method is invoked.Note that the audio data is played back asynchronously,there is no 
mechanism for synchronization with other classes presenting sounds, images,or video. This method may fail "silently"if 
(local)audio facilities are unavailable on the platform. */
public void loop() {
}

/*
Starts the HSound class playing from the beginning of its associated audio data.If the sample data has not been 
completely loaded,this method has no effect. When the audio data has been played in its entirety then no further audible 
output should be made until the next play or loop method is invoked.Note that the audio data is played back 
asynchronously. There is no mechanism for synchronization with other classes presenting sounds,images,or video. This 
method may fail "silently"if (local)audio facilities are unavailable on the 
platform. */
public void play() {
}

/*
Constructs an HSound object from an array of bytes encoded in the same encoding format as when reading this type of 
audio sample data from a  le.If the object already contains data,this method shall perform the following sequence: " 
stop the sample if it is playing or looping. " dispose of the old data and any associated resources, as if the dispose() 
method had been called. " load the new data synchronously. If the byte array does not contain a valid audio sample then 
this method shall throw a java.lang.IllegalArgumentException Parameters: data -the data for the HSound object encoded in 
the speci  ed format for audio sample  les of this type. */
public void set(byte[] data) {
}

/*
Stops the HSound class playing its associated audio data. Note that,if a play or loop method is invoked,after a 
stop,then presentation of the audio data will restart from the beginning of the audio data,rather than from the position 
where the audio data wa stopped. */
public void stop() {
}


}
