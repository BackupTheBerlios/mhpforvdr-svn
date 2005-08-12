
package vdr.mhp.awt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import vdr.mhp.io.PathConverter;

//Encapsulates an IDirectFBDataDataBuffer

class DFBDataBuffer {

long nativeData;

private native long createBufferFromFile(byte[] filename) throws IOException;
private native long createBufferFromData(byte[] data, int offset, int len) throws IOException;
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
   nativeData = createBufferForStreaming();
   byte bytes[] = new byte[4096];
   int len = 0;
   while ((len = is.read (bytes)) != -1)
      putData(nativeData, bytes, len);
}

public void flush() {
   if (nativeData != 0) {
      removeRef(nativeData);
      nativeData = 0;
   }
}

public void finalize() {
   flush();
}

}