
package vdr.mhp.awt;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.net.URL;
import java.util.Vector;
import vdr.mhp.io.PathConverter;

//Encapsulates an IDirectFBDataBuffer

class DFBDataBuffer {

// this does not point to an IDirectFBDataBuffer, but a private native structure.
// so private, not default access
private long nativeData;

private native long createBufferFromFile(byte[] filename) throws IOException;
private native long createBufferFromData(byte[] data, int offset, int len) throws ArrayIndexOutOfBoundsException;

//currently streaming buffers are broken in DFB++ and maybe DirectFB as well.
private native long createBufferForStreaming() throws IOException;
private native void putData(long nativeData, byte[] data, int len);
private native void removeRef(long nativeData);

private native long nativeBufferData(long nativeData);

public DFBDataBuffer(String filename) throws IOException {
   nativeData = createBufferFromFile( PathConverter.toNativeString(PathConverter.convert(filename)) );
}

public DFBDataBuffer(byte[] data, int offset, int len) throws IOException {
   nativeData = createBufferFromData(data, offset, len);
}

public DFBDataBuffer(InputStream stream) throws IOException {
   // Old code using DirectFB's streaming buffer
   // Access to DirectFB's streaming buffer via DFB++ is broken. See native code.
   /*
   nativeData = createBufferForStreaming();
   byte bytes[] = new byte[4096];
   int len = 0;
   while ((len = is.read (bytes)) != -1)
      putData(nativeData, bytes, len);
   */
   //This is a hack. Remove it as soon as the above code works.
   Vector v = new Vector();
   class Frame {
      byte[]data;
      int length;
      Frame(byte[] data, int length) {
         this.data=data;
         this.length=length;
      }
   }
   int len;
   do {
      byte[] data = new byte[4096];
      if ((len = stream.read (data)) != -1)
         v.add(new Frame(data, len));
   } while (len != -1);
   Frame f;
   int size = 0;
   for (int i = 0; i < v.size (); i++)
      size += ((Frame)v.elementAt(i)).length;
   byte[] data = new byte[size];
   int pos = 0;
   for (int i = 0; i < v.size (); i++) {
      f = (Frame)v.elementAt(i);
      System.arraycopy(f.data, 0, data, pos, f.length);
      pos += f.length;
   }
   nativeData = createBufferFromData(data, 0, size);
}

long getNativeData() {
   return nativeBufferData(nativeData);
}

public synchronized void dispose() {
   if (nativeData != 0) {
      removeRef(nativeData);
      nativeData = 0;
   }
}

public void finalize() {
   dispose();
}

}