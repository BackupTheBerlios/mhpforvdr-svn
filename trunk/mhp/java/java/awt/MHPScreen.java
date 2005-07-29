package java.awt;

import java.awt.image.ColorModel;
import java.security.AccessController;
import java.security.PrivilegedAction;


//At once the heart of the graphics implementation,
//on the other hand a class that doesn't do much

//Instantiation happens in form of the derived class org.havi.ui.HScreen

public class MHPScreen {

public static final Dimension FourToThree=new Dimension(4,3);
public static final Dimension SixteenToNine=new Dimension(16,9);
static Dimension aspectRatio;
static Dimension deviceResolution;


static {
   //don't catch any exception, if an exception is thrown,
   //let it go toplevel, since the whole thing won't work without this lib.
   AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                System.loadLibrary("mhpjni_directfbawt");
                return null;
            }
        });
   Toolkit.initToolkit();
}

//called by ApplicationMananger
public static void InitializeDisplaySystem() {
   //do not create an instance of MHPScreen here - this is done by HScreen
   //(and not necessary since MHPScreen is all static).
   int ratio=aspectRatio();
   if (ratio==0)
      aspectRatio=FourToThree;
   else if (ratio==1)
      aspectRatio=SixteenToNine; //probably not well supported?
   
   deviceResolution=new Dimension(getDeviceResolutionX(), getDeviceResolutionY());
}

//called by ApplicationMananger
public static void CleanUpDisplaySystem() {
}

protected MHPScreen() {
   //HAVI's HScreen inherits MHPScreen and instantiates
}

private static native int aspectRatio();
private static native int getDeviceResolutionX();
private static native int getDeviceResolutionY();


/*** Public internal API ***/

public static int getResolutionX() {
   return deviceResolution.width;
}

public static int getResolutionY() {
   return deviceResolution.height;
}

public static Dimension getResolution() {
   return deviceResolution;
}

public static Dimension getAspectRatio() {
   return aspectRatio;
}

public static boolean isFourToThree() {
   return aspectRatio==FourToThree;
}

public static boolean isSixteenToNine() {
   return aspectRatio==SixteenToNine;
}

//on TV, a pixel is wider than high!
public static float getPixelAspectRatio() {
   float resolutionNumber=((float)getResolutionX())/((float)getResolutionY()); // 720/576 = 5/4
   float ratioNumber=((float)aspectRatio.getWidth())/((float)aspectRatio.getHeight()); // 4/3 or 16/9
   return ratioNumber/resolutionNumber;
   // 4/3  / (5/4) = 16/15
   // 16/9 / (5/4) = 64/45
}

public static MHPBackgroundPlane createBackgroundPlane(int x, int y, int width, int height) {
   return new MHPBackgroundPlane(x, y, width, height);
}

public static MHPVideoPlane createVideoPlane(int x, int y, int width, int height) {
   return new MHPVideoPlane(x, y, width, height);
}

   //shall be called by the class representing a native window - MHPPlane
public static void checkEventDispatching() {
   Toolkit.startDispatch();
}

    //actual creation methods - not official API
public static Graphics createClippedGraphics(Component comp) {
   return MHPNativeGraphics.createClippedGraphics(comp);
}

public static Graphics getImageGraphics(java.awt.Image img) {
   return MHPNativeGraphics.getImageGraphics(img);
}

public static void postPaintEvent ( int id, Component c, int x, int y, int width, int height ) {
   Toolkit.eventQueue.postPaintEvent( id, c, x, y, width, height);
}

public static ColorModel getColorModel() {
   //ARGB
   return new java.awt.image.DirectColorModel(32, 0xFF000000, 0x00FF0000, 0x0000FF00, 0x000000FF);
}

public static int getDotsPerInch() {
   //TODO
   return -1;
}

public static void sync() {
   //finish all drawing/blitting functions
   waitIdle();
}
private static native void waitIdle();

}
