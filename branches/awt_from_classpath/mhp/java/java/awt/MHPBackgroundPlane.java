package java.awt;

import org.dvb.application.MHPApplication;
import java.awt.MHPScreen;
import vdr.mhp.awt.DFBWindowPeer;

public class MHPBackgroundPlane extends MHPPlane implements java.awt.image.ImageObserver {

Color color=Color.black;
Image image=null;
Rectangle imageRectangle=null;

MHPBackgroundPlane(int x, int y, int width, int height) {
   super(x, y, width, height, null, false, getBackgroundStacking(), MHPScreen.hasBackgroundLayer() ? MHPScreen.getBackgroundLayer() : (MHPScreen.hasVideoLayer() ? MHPScreen.getVideoLayer() : MHPScreen.getMainLayer()) );
   System.out.println("Creating MHPBackgroundPlane");
}

  /** Get the current background color
      @return current background color */
public java.awt.Color getColor() {
   return color;
}

/** Set the background color. This may fail if the device does not support
    variable colors or the caller does not have permission to change it.
    @param newColor new background color */
public void setColor(java.awt.Color color)
            throws org.havi.ui.HPermissionDeniedException,
                   org.havi.ui.HConfigurationException {
   if (!this.color.equals(color) || image != null) {
      this.color=color;
      image=null;
      repaint();
   }
}

public void displayImage(java.awt.Image image) {
   //System.out.println("MHPBackgroundPlane::displayImage, having image "+image+", issuing repaint");
   if (image!=this.image || imageRectangle != null) {
      this.image=image;
      imageRectangle=null;
      repaint();
   }
}

public void displayImage(java.awt.Image image, int x, int y, int w, int h) {
   //System.out.println("MHPBackgroundPlane::displayImage, having image "+image+", "+w+"x"+h+", issuing repaint");
   if (image!=this.image || imageRectangle==null || imageRectangle.x != x || imageRectangle.y != y || imageRectangle.width != width || imageRectangle.height != height) {
      this.image=image;
      imageRectangle = new Rectangle(x,y,w,h);
      repaint();
   }
}

public void displayDripfeed(byte[] data) {
   if ( getPeer() != null )
      displayDripfeed(((DFBWindowPeer) getPeer()).getNativeSurface(), data);
      //the native surface is Release'd in the native code
}
private native void displayDripfeed(long nativeSurface, byte[] data);


public void paint(Graphics g) {
   //System.out.println("MHPBackgroundPlane::paint: Drawing image "+image);
   if (image != null) {
      if (imageRectangle == null) {
         g.drawImage(image, 0, 0, color, this);
      } else {
         g.drawImage(image, imageRectangle.x, imageRectangle.y, imageRectangle.width, imageRectangle.height, color, this);
      }
   } else {
      g.setColor(color);
      g.drawRect(0, 0, width, height);
   }
}

//image observer implementation
public boolean imageUpdate ( Image img, int infoflags, int x, int y, int width, int height ) {
   return true;
}


}
