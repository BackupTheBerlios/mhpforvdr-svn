package java.awt;

import vdr.mhp.awt.MHPImage;

public class MHPBackgroundLayer {

// keep in sync with enum on native side
static public final int MODE_DONTCARE = 0;
static public final int MODE_COLOR = 1;
static public final int MODE_IMAGESTRETCH = 2;
static public final int MODE_IMAGETILE = 3;

static final Color defaultColor = new Color(0xCD, 0xCD, 0xCD);

int mode;
Color color;
Image image;

long nativeLayer;

private native void setLayerBackgroundMode(long nativeLayer, int mode);
private native void setLayerBackgroundColor(long nativeLayer, int r, int g, int b, int a);
private native void setLayerBackgroundImage(long nativeLayer, long nativeSurface);

private static MHPBackgroundLayer layer = null;

public static MHPBackgroundLayer getBackgroundLayer() {
   if (layer == null)
      layer = new MHPBackgroundLayer();
   return layer;
}

// called by MHPScreen
static void setDefaultBackgroundConfiguration() {
   getBackgroundLayer();
}

protected MHPBackgroundLayer() {
   layer = this;
   if (MHPScreen.hasBackgroundLayer())
      nativeLayer = MHPScreen.getBackgroundLayer();
   else if (MHPScreen.hasVideoLayer())
      nativeLayer = MHPScreen.getVideoLayer();
   else
      nativeLayer = MHPScreen.getMainLayer();
   
   mode = MODE_COLOR;
   color = defaultColor;
   image = null;
   setMode();
}

public void setColor(Color color) {
   this.color = color;
   this.mode = MODE_COLOR;
   setMode();
}

public void setImageStretched(Image image) {
   this.image = image;
   this.mode = MODE_IMAGESTRETCH;
   setMode();
}

public void setImageTiled(Image image) {
   this.image = image;
   this.mode = MODE_IMAGETILE;
   setMode();
}

// The specified image is displayed at the specified part of the layer,
// the rest is filled with the background color.
public void setImageStretched(Image image, int x, int y, int width, int height) {
   // Need to draw to new image
   Dimension size = MHPScreen.getResolution();
   MHPImage drawImage = new MHPImage(size.width, size.height);
   Graphics g = drawImage.getGraphics();
   g.setColor(color);
   g.fillRect(0, 0, size.width, size.height);
   g.drawImage(image, x, y, width, height, null);
   
   setImageStretched(drawImage);
}

// If mode is one of the image modes, the image must be set before the mode is set!
// This must be taken care for here, it is not enforced in native code!
private void setMode() {
   if (color != null)
      setLayerBackgroundColor(nativeLayer, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
   if (image != null)
      setLayerBackgroundImage(nativeLayer, ((MHPImage) image).getNativeSurface());
   setLayerBackgroundMode(nativeLayer, mode);
}

public Color getColor() {
   return color;
}

public Image getImage() {
   return image;
}




}

