
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

class Data implements NativeData {

   // accessed from native code
   private long nativeData = 0;
   // accessed from native code
   private boolean dataIsNull = true;
   
   public boolean isNull() {
      return dataIsNull;
   }
   
   public boolean equals(Object other) {
      return (other instanceof Data) &&
             ((Data) other).nativeData == nativeData;
   }
}

}