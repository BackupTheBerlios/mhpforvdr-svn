
package vdr.mhp.awt;

import java.io.IOException;

import vdr.mhp.io.PathConverter;


class DFBImageProvider {

long nativeData;

int width, height;
boolean valid = false;

static {
   initStaticState();
}

private static native void initStaticState();
private native long createImageProviderFromFile(byte[] filename) throws IllegalArgumentException;
private native long createImageProviderFromDataBuffer(long nativeBufferData) throws IllegalArgumentException;
private native void renderTo(long nativeProviderData, long nativeImageData);
private native void removeRef(long nativeData);

public DFBImageProvider(String filename) throws IOException {
   nativeData = createImageProviderFromFile( PathConverter.toNativeString(filename) );
}

public DFBImageProvider(MHPDataBuffer data) throws IllegalArgumentException, IOException {
   nativeData = createImageProviderFromDataBuffer( data.nativeData );
}

public int getWidth() {
   if (!valid)
      throw new IllegalStateException();
   return width;
}

public int getHeight() {
   if (!valid)
      throw new IllegalStateException();
   return height;
}

public MHPImage createImage() {
   MHPImage image = createImageObject();
   renderTo(image);
   return image;
}

//created an _empty_ image with the appropriate dimensions
MHPImage createImageObject() {
   if (!valid)
      throw new IllegalStateException();
   return new MHPImage(width, height);
}

void renderTo(MHPImage image) {
   if (!valid)
      throw new IllegalStateException();
   if (image.width != width || image.height != height || image.nativeData == 0)
      throw new IllegalArgumentException();
   renderTo(nativeData, image.nativeData);
}

//callback from native side
private void setProperties(boolean valid, int width, int height) {
   this.valid=valid;
   this.width=width;
   this.height=height;
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