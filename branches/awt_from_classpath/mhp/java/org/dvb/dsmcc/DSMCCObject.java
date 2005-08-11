
package org.dvb.dsmcc;

import java.io.File;
import vdr.mhp.io.PathConverter;
import vdr.mhp.ApplicationManager;
import org.dvb.application.MHPApplication;

/*A DSMCCObject is an object which belongs to a DSMCC ServiceDomain. As soon as a 
ServiceDomain has been attached to the  le system hierarchy, DSMCCObject objects can be 
created to access the ServiceDomain objects. Paths used to create a DSMCCObject can be 
either absolute or relative as de ned in detail in the main body of this speci cation. To 
access the content of the object: " For a Directory, the method list of the java.io.File 
class has to be used to get the entries of the directory. " For a Stream object, the class 
DSMCCStream has to be used. " For a File, the java.io.FileInputStream class or the 
java.io.RandomAccessFile has to be used. NB: " Obviously, for the Object Carousel, the 
write mode of java.io.RandomAccessFile is not allowed. */

public class DSMCCObject extends java.io.File {

//path in the context of the object carousel, regardless of the cache directory
File carouselFile;
ObjectChangeEventListener listeners = null;
long nativeData = 0; //a DSMCCObjectListener *
//org.dvb.application.MHPApplication app = null;

/*
Constant to indicate that the data for an object shall only be retrieved where it is already in cache and meets the 
requirements of cache priority signaling. Where data is not in the cache, or the contents don't meet the requirements of 
the of cache priority signaling, attempts to load a DSMCCObject shall fail. */
public static final int FROM_CACHE = 1;


/*
Constant to indicate that the data for an object shall be automatically be retrieved from the network where the data is 
not already cached. Note that this method does not modify the caching policy controlled by the signaling in the OC. So, 
if the data is signalled as requiring transparent caching then data will be retrieved from the network if 
required. */
public static final int FROM_CACHE_OR_STREAM = 2;


/*
Constant to indicate that the data for an object shall always be retrieved from the network even if the data has already 
been cached. */
public static final int FROM_STREAM_ONLY = 3;


/*
Create a DSMCCObject object. Parameters: dir - the directory object. name - the  
lename. */
public DSMCCObject(DSMCCObject dir, java.lang.String name) {
   super(dir, name);
   carouselFile=new File(dir.carouselFile, name);
}

/*
Create a DSMCCObject object. Parameters: path - the path to the  le. */
public DSMCCObject(java.lang.String path) {
   super(PathConverter.convert(path));
   carouselFile=new File(path);
}

/*
Create a DSMCCObject object. Parameters: path - the directory Path. name - the  
lename. */
public DSMCCObject(java.lang.String path, java.lang.String name) {
   super(PathConverter.convert(path, name), name);
   carouselFile=new File(path+"/"+path);
}

/*
static java.io.File getAppBaseDir() {
   org.dvb.application.MHPApplication app=getApp();
   if (app==null)
      return new File("");
   else
      return new File(app.getCarouselBasePath());
}

static org.dvb.application.MHPApplication getApp() {
   return vdr.mhp.ApplicationManager.getManager().getApplicationFromStack();
}
*/

/*
This method is used to abort a load in progress. It can be used to abort either a synchronousLoad or an 
asynchronousLoad. Throws:NothingToAbortException - There is no loading in progress. */
public void abort() {
}

/*
Subscribes an ObjectChangeEventListener to receive noti cations of version changes of DSMCCObject. This listener shall 
never be  red until after the object has successfully entered the loaded state for the  rst time. Hence objects which 
never successfully enter the loaded state (e.g. because the object cannot be found) shall never have this listener  re. 
Once an object has successfully entered the loaded state once, this event shall continue to be  red when changes are 
detected by the MHP regardless of further transitions in or out of the loaded state. Parameters: listener - the 
ObjectChangeEventListener to be noti ed . */
public void addObjectChangeEventListener(ObjectChangeEventListener listener) {
   System.out.println("DSMCCObject.addObjectChangeEventListener "+listener);
   listeners=DSMCCEventMulticaster.add(listeners, listener);
   MHPApplication app=ApplicationManager.getManager().getApplicationFromStack();
   if (app==null) {
      System.out.println("No application found in addObjectChangeEventListener, stack trace follows:");
      new Exception().printStackTrace();
      return;
   }
   if (nativeData == 0)
      nativeData=createListener(app.getNativeData(), (carouselFile.getPath()+'\0').getBytes());
}

/*
Unsubscribes an ObjectChangeEventListener to receive noti cations of version changes of DSMCCObject. Parameters: 
listener - a previously registered ObjectChangeEventListener. */
public void removeObjectChangeEventListener(ObjectChangeEventListener listener) {
   System.out.println("DSMCCObject.removeObjectChangeEventListener "+listener);
   listeners=DSMCCEventMulticaster.add(listeners, listener);
   if (listeners == null) {
      removeListener(nativeData);
      nativeData = 0;
   }
}

private native long createListener(long nativeApp, byte[] path);
private native void removeListener(long nativeData);

/*
This method is used to asynchronously load a carousel object. For each call to this method which returns without 
throwing an exception, one of the following events will be sent to the application (by a listener mechanism) as soon as 
the loading is done or if an error has occurred: SuccessEvent, InvalidFormatEvent, InvalidPathNameEvent, 
MPEGDeliveryErrorEvent, ServerDeliveryErrorEvent, ServiceXFRErrorEvent, NotEntitledEvent Parameters: l - an 
AsynchronousLoadingEventListener to receive events related to asynchronous loading. Throws: InvalidPathNameException - 
the object can not be found. */
public void asynchronousLoad(AsynchronousLoadingEventListener l) {
   System.out.println("DSMCCObject.asynchronousLoad, can read? "+canRead());
   AsynchronousLoadingEvent e;
   if (canRead())
      e=new SuccessEvent(this);
   else
      e=new InvalidPathnameEvent(this);
   //do this from a different thread?
   l.receiveEvent(e);
}

/*
This method will return the lists of certi cate chains that can authenticate the DSMCCObject. If the DSMCCObject is not 
loaded, this method will return null. If the DSMCCObject is loaded but not authenticated this method will return an 
outer array of size zero. Returns: a two-dimensional array of X.509 certi cates, where the  rst index of the array 
determines a certi cate chain and the second index identi es the certi cate within the chain. Within one certi cate 
chain the leaf certi cate is  rst followed by any intermediate certi cate authorities in the order of the chain with the 
root CA certi cate as the last item. */
//public java.security.cert.X509Certificate[][] getSigners() {
//}

/*
Returns a URL identifying this carousel object. If the directory entry for the object has not been loaded then null 
shall be returned. Returns: a URL identifying the carousel object or null */
public java.net.URL getURL() {
   try {
      return toURL();
   } catch (java.net.MalformedURLException e) {
      e.printStackTrace();
      return null;
   }
}

/*
Returns a boolean indicating whether or not the DSMCCObject has been loaded. Returns: true if the  le is already loaded, 
false otherwise. */
public boolean isLoaded() {
   System.out.println("DSMCCObject.isLoaded, returning "+canRead());
   return canRead();
}

/*
Returns a boolean indicating if the kind of the object is known. (The kind of an object is known if the directory 
containing it is loaded). Returns: true if the type of the object is known, false 
otherwise. */
public boolean isObjectKindKnown() {
   return true;
}

/*
Returns a boolean indicating whether or not the DSMCCObject is a DSMCC Stream object. Returns: true if the  le is a 
stream, false if the object is not a stream or if the object kind is unknown. */
public boolean isStream() {
   return false;
}

/*
Returns a boolean indicating whether or not the DSMCCObject is a DSMCC StreamEvent object. NB: If isStreamEvent is true 
then isStream is true also. Returns: true if the  le is a stream event, false if the object is not a stream event or if 
the object kind is unknown. */
public boolean isStreamEvent() {
   return false;
}

/*
Asynchronous loading of the directory entry information. Calling this is equivalent of calling the method 
asynchronousLoad on the parent directory of a DSMCCObject. Parameters: l - a listener which will be called when the 
loading is done. Throws: InvalidPathNameException - if the object cannot be 
found. */
public void loadDirectoryEntry(AsynchronousLoadingEventListener l) {
}

/*
Calling this method will issue a hint to the MHP for pre-fetching the object data for that DSMCC object into 
cache.Parameters: dir - the directory object in which to pre-fetch the data. path - the relative path name of object to 
pre-fetch, starting from the directory object passes as parameter. priority - the relative priority of this pre-fetch 
request (higher = more important) Returns: true if the MHP supports pre-fetching (i.e. will try to process the request) 
and false otherwise. Note that a return value of 'true' is only an indication that the MHP receiver supports 
pre-fetching. It is not a guarantee that the requested data will actually be loaded into cache as the receiver may 
decide to drop the request in order to make resources available for regular load 
requests. */
public static boolean prefetch(DSMCCObject dir, java.lang.String path, byte 
priority) {
   return false;
}

/*
Calling this method will issue a hint to the MHP for pre-fetching the object data for that DSMCC object into cache. 
Parameters: path - the absolute pathname of the object to pre-fetch. priority - the relative priority of this pre-fetch 
request (higher = more important) Returns: true if the MHP supports pre-fetching (i.e. will try to process the request) 
and false otherwise. Note that a return value of 'true' is only an indication that the MHP receiver supports 
pre-fetching. It is not a guarantee that the requested data will actually be loaded into cache as the receiver may 
decide to drop the request in order to make resources available for regular load 
requests. */
public static boolean prefetch(java.lang.String path, byte priority) {
   return false;
}

/*
Set the retrieval mode for a DSMCCObject. The default retrieval mode is FROM_CACHE_OR_STREAM. The retrieval mode state 
is sampled when the object is loaded (whether explicitly or as described in "Constraints on the java.io.File methods for 
broadcast carousels"). Changing the retrieval mode for a loaded object has no effect until the object is unloaded and 
loaded again. Parameters: retrieval_mode - the retrieval mode to be used for the object speci ed as one of the public 
static  nal constants in this class. Throws: IllegalArgumentException - if the retrieval_mode speci ed is not one listed 
de ned for use with this method. */
public void setRetrievalMode(int retrieval_mode) {
   System.out.println("DSMCCObject.setRetrievalMode(): "+retrieval_mode);
}

/*
This method is used to load a DSMCCObject. This method blocks until the  le is loaded. It can be aborted from another 
thread with the abort method. In this case the InterruptedIOException is thrown. If the IOR of the object itself or one 
of its parent directories is a Lite Option Pro le Body, the MHP implementation will not attempt to resolve it: a 
ServiceXFRException is thrown to indicate to the application where the DSMCCObject is actually located. Throws: 
InterruptedIOException - the loading has been aborted. InvalidPathNameException - the Object can not be found. 
NotEntitledException - the stream carrying the object is scrambled and the user has no entitlements to descramble the 
stream. ServiceXFRException - the IOR of the object or one of its parent directories is a Lite Option Pro le Body. 
InvalidFormatException - an inconsistent DSMCC message has been received. MPEGDeliveryException - an error has occurred 
while loading data from MPEG stream such as a timeout ServerDeliveryException - when an MHP terminal cannot communicate 
with the server for  les delivered over a bi-directional IP connection. */
public void synchronousLoad() throws InvalidPathNameException {
   System.out.println("DSMCCObject.synchronousLoad(), can read? "+canRead());
   if (!canRead())
      throw new InvalidPathNameException();
}

/*
When calling this method, the applications gives a hint to the MHP that if this object is not consumed by another 
application/thread, the system can free all the resources allocated to this object. It is worth noting that if other 
clients use this object (e.g. a  le input stream is opened on this object or if the corresponding stream or stream event 
is being consumed) the system resources allocated to this object will not be freed. Throws: NotLoadedException - the 
carousel object is not loaded. */
public void unload() {
   System.out.println("DSMCCObject.unload()");
}

static class EventThread extends Thread {
   private boolean running = false;
   
   public void CheckStart() {
      if (!running) {
         running=true;
         start();
      }
   }
   
   public void run() {
      while (running) {
         //waiting is done on the native side
         long nativeEvent = nextEvent();
         if (nativeEvent != 0) {
            int version=getVersion(nativeEvent);
            DSMCCObject obj=(DSMCCObject)getRef(nativeEvent);
            deleteEvent(nativeEvent);
            
            if (obj.listeners != null)
               obj.listeners.receiveObjectChangeEvent(new ObjectChangeEvent(obj, version));
         }
      }
   }
   
   private native long nextEvent();
   private native int getVersion(long nativeEvent);
   private native Object getRef(long nativeEvent);
   private native void deleteEvent(long nativeEvent);
   
}


}
