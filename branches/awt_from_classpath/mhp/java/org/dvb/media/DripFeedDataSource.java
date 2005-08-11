
package org.dvb.media;
import java.util.Vector;

/*This class allows to create a source for a JMF player to be able to feed the decoder 
progressively with parts of a clip (e.g. I or P MPEG-2 frame) according to the drip-fed 
mode format de ned in the MHP content format chapter. To start using the drip-feed mode, 
the application needs to instantiate a player representing a MPEG-2 video decoder and have 
its source be a DripFeedDataSource instance. A DripFeedDataSource instance can be obtained 
by calling the default constructor of the class. A player that will be bound to a MPEG-2 
video decoder (when realized) can be created with the following special URL (locator): 
"dripfeed://". It is also possible to use a decoder that was instantiated to play a 
broadcast MPEG-2 stream. After having the DripFeedDataSource connected to a Player 
representing a MPEG-2 video decoder, the following rules applies: - If the feed method is 
called when the player is in the "prefetched" state the image will be stored so that when 
the player goes in the "started" state it will be automatically displayed. - If the feed 
method is called when the player is in the "started" mode, the frame shall be displayed 
immediately. In particular it is not required to feed a second frame to the decoder to 
display the  rst frame. - If the feed method is called when the player is in any other 
state (or if the DripFeedDataSource is not connected to a player), it will be ignored by 
the platform implementation. */

public class DripFeedDataSource extends javax.media.protocol.DataSource {

/*
Constructor. A call to the constructor will throw a security exception if the application is not granted the right to 
use this mode. */
public DripFeedDataSource() {
}

static {
   Vector list=javax.media.PackageManager.getContentPrefixList();
   list.add("org.dvb");
   javax.media.PackageManager.setContentPrefixList(list);
   javax.media.PackageManager.commitContentPrefixList();
}



/*
This method shall not be used and has no effect. This source is considered as always connected. Overrides: 
javax.media.protocol.DataSource.connect() in class javax.media.protocol.DataSource Throws: IOException - never thrown in 
this sub-class */
public void connect() {
   //does nothing
}

/*
This method shall not be used and has no effect. This source is considered as always connected. Overrides: 
javax.media.protocol.DataSource.disconnect() in class javax.media.protocol.DataSource */
public void disconnect() {
   //does nothing
}

/*
This method allows an application to feed the decoder progressively with parts of a clip (e.g. I or P MPEG-2 frame) 
according to the drip-fed mode format de ned in the MHP content format chapter. The feed method shall not be called more 
often than every 500ms. If this rule is not respected, display is not guaranteed. While in the prefetch state the drip 
feed data source is only required to corrrectly process a single invocation of this method where the data consists only 
of a single I frame. Possible additional invocations while in the prefetch state shall have implementation speci c 
results. Parameters: clip_part - Chunk of bytes compliant with the drip-fed mode format de ned in the MHP content format 
chapter (i.e. one MPEG-2 frame with optional synctactic MPEG-2 elements). */
public synchronized void feed(byte[] clip_part) {
   cache.add(clip_part);
   notify();
}

//not API
public synchronized byte[] getNext() {
   try {
      wait(100);
   } catch (InterruptedException _) {
   }
   try {
      return (byte[])cache.removeFirst();
   } catch (java.util.NoSuchElementException _) {
      return null;
   }
}

java.util.LinkedList cache=new java.util.LinkedList();

/*
This method shall return the content type for mpeg-2 video "drips" Overrides: 
javax.media.protocol.DataSource.getContentType() in class javax.media.protocol.DataSource Returns: the content type for 
MPEG-2 video drips */
public java.lang.String getContentType() {
   return "dripfeed";
}

/*
Obtain the object that implements the speci ed Class or Interface. The full class or interface name must be used. If the 
control is not supported then null is returned. Overrides: javax.media.protocol.DataSource.getControl(java.lang.String) 
in class javax.media.protocol.DataSource */
public java.lang.Object getControl(java.lang.String controlType) {
   return null;
}

/*
Obtain the collection of objects that control this object. If no controls are supported, a zero length array is 
returned. Overrides: javax.media.protocol.DataSource.getControls() in class javax.media.protocol.DataSource Returns: the 
collection of object controls */
public java.lang.Object[] getControls() {
   return new Object[0];
}

/*
This method shall not be used and has no effect. Overrides: javax.media.protocol.DataSource.getDuration() in class 
javax.media.protocol.DataSource Returns: DURATION_UNKNOWN. */
public javax.media.Time getDuration() {
   return javax.media.Duration.DURATION_UNKNOWN;
}

/*
This method is not used and shall return null. Returns: always returns null in this 
sub-class */
public javax.media.protocol.PullSourceStream[] getStreams() {
   return null;
}

/*
This method shall not be used and has no effect. This source is considered as always started. Overrides: 
javax.media.protocol.DataSource.start() in class javax.media.protocol.DataSource Throws: IOException - never thrown in 
this sub-class */
public void start() {
   //does nothing
}

/*
This method shall not be used and has no effect. This source is considered as always started. Overrides: 
javax.media.protocol.DataSource.stop() in class javax.media.protocol.DataSource Throws: IOException - never thrown in 
this sub-class */
public void stop() {
   //does nothing
}


}
