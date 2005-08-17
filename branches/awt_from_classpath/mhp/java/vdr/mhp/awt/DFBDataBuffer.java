
package vdr.mhp.awt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;
import vdr.mhp.io.PathConverter;

//Encapsulates an IDirectFBDataDataBuffer

class DFBDataBuffer {

long nativeData;

private native long createBufferFromFile(byte[] filename) throws IOException;
private native long createBufferFromData(byte[] data, int offset, int len) throws IOException;

//currently streaming buffers are broken in DFB++ and maybe DirectFB as well.
private native long createBufferForStreaming() throws IOException;
private native void putData(long nativeData, byte[] data, int len);
private native void removeRef(long nativeData);

public DFBDataBuffer(String filename) throws IOException {
   nativeData = createBufferFromFile( PathConverter.toNativeString(filename) );
}

public DFBDataBuffer(byte[] data, int offset, int len) throws IOException {
   nativeData = createBufferFromData(data, offset, len);
}

public DFBDataBuffer(InputStream stream) throws IOException {
   //Old code using DirectFB's streaming buffer
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
      static Frame read(InputStream is) {
         byte[] data = new byte[4096];
         int len;
         if ((len = is.read (bytes)) != -1)
            return new Frame(data, len);
         return null;
      }
   }
   Frame f;
   while ((f=Frame.read(is)) != null) {
      Vector.add(f);
   }
   int size = 0;
   for (int i = 0; i < v.size (); i++)
      size += ((Frame)v.elementAt(i)).length;
   byte[] data = new byte[size];
   int pos = 0;
   for (int i = 0; i < v.size (); i++) {
      Frame f = (Frame)v.elementAt(i);
      System.arraycopy(f.data, 0, data, pos, f.length);
      pos += length;
   }
   nativeData = createBufferFromData(data, 0, size);
}

public void dispose() {
   if (nativeData != 0) {
      removeRef(nativeData);
      nativeData = 0;
   }
}

public void finalize() {
   dispose();
}

}