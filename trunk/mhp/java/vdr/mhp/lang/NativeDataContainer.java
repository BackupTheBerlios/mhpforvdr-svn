
package vdr.mhp.lang;

/*
   Usage is as follows:

   import vdr.mhp.lang.NativeData;
   import vdr.mhp.lang.NativeDataContainer;
   
   NativeData nativeData = NativeDataContainer.createNativeData();
    // or creation on native side
*/

public class NativeDataContainer {

static NativeDataContainer defaultContainer = new NativeDataContainer();

public NativeDataContainer() {
}

public NativeData newNativeData() {
   return new Data();
}

public static NativeData createNativeData() {
   return defaultContainer.newNativeData();
}

native void nativeFinalize(NativeData obj);

class Data implements NativeData {

   // accessed from native code
   private long nativeData = 0;
   // accessed from native code
   private boolean dataIsNull = true;
   // accessed from native code
   private long dataDeleter = 0;
   
   public boolean isNull() {
      return dataIsNull;
   }
   
   public boolean equals(Object other) {
      return (other instanceof Data) &&
             ((Data) other).nativeData == nativeData;
   }
   
   public void finalize() {
      nativeFinalize(this);
   }
   
   public int hashCode() {
      // same as new Long(nativeData).hashCode()
      return (int)(nativeData^(nativeData>>>32));
   }
}

}