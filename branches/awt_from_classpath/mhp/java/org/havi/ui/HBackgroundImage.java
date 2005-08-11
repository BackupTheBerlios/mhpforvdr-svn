
package org.havi.ui;
import org.havi.ui.event.HBackgroundImageListener;
import org.havi.ui.event.HBackgroundImageEvent;
/*This class represents a background image.Images of this class can be used as full screen 
backgrounds outside the java.awt framework.The parameters to the constructors are as 
follows,in cases where parameters are not used,then the constructor should use the default 
values. */

public class HBackgroundImage implements java.awt.image.ImageObserver {

java.awt.Image image = null;
HBackgroundImageListener listener=null;
boolean fromfile=true;

/*
Create an HBackgroundImage object from an array of bytes encoded in the same encoding format as when reading this type 
of image data from a  le. If this constructor succeeds then the object will automatically be in the loaded state and 
calling the load(HBackgroundImageListener)method shall immediately generate an HBackgroundImageEvent reporting success. 
If the byte array does not contain a valid image then this constructor shall throw a java.lang.IllegalArgumentException 
Calling the flush()method on an object built with this constructor shall have no effect. Parameters: pixels -the data 
for the HBackgroundImage object encoded in the speci  ed format for image  les of this 
type. */
public HBackgroundImage(byte[] pixels) {
   fromfile=false;
   image=java.awt.Toolkit.getDefaultToolkit().createImage(pixels);
}

/*
Create an HBackgroundImage object.Loading of the data for the object is not required at this time. Parameters: filename 
-the name of the  le to use as the source of data in a platform-speci  c URL 
format. */
public HBackgroundImage(java.lang.String filename) {
   image=java.awt.Toolkit.getDefaultToolkit().getImage(filename);
}

/*
Create an HBackgroundImage object.Loading of the data for the object is not required at this time. Parameters: contents 
-a URL referring to the data to load. */
public HBackgroundImage(java.net.URL contents) {
   image=java.awt.Toolkit.getDefaultToolkit().getImage(contents);
}

/*
Flush all the resources used by this image.This includes any pixel data being cached as well as all underlying system 
resources used to store data or pixels for the image.After calling this method the image is in a state similar to when 
it was  rst created without any load method having been called. When this method is called,the image shall not be in use 
by an application.Resources related to any HBackgroundDevice are not released. */
public void flush() {
   if (image != null) {
      image.flush();
      image=null;
   }
}

/*
Determines the height of the image.This is returned in pixels as de  ned by the format of the image concerned.If this 
information is not known when this method is called then -1 is returned. The image must have been successfully loaded to 
completion before this information is guaranteed to be available.It is implementation speci  c whether this information 
is available before the image is successfully loaded to completion.An image whose loading failed for any reason shall be 
considered as having this information unavailable. Returns: the height of the 
image */
public int getHeight() {
   return image==null ? 0 : image.getHeight(this);
}

/*
Determines the width of the image.This is returned in pixels as de  ned by the format of the image concerned.If this 
information is not known when this method is called then -1 is returned. The image must have been successfully loaded to 
completion before this information is guaranteed to be available.It is implementation speci  c whether this information 
is available before the image is successfully loaded to completion.An image whose loading failed for any reason shall be 
considered as having this information unavailable. Returns: the width of the 
image */
public int getWidth() {
   return image==null ? 0 : image.getWidth(this);
}

/*
Load the data for this object.This method is asynchronous.The completion of data loading is reported through the 
listener provided. Parameters: l -the listener to call when loading of data is 
completed. */
public void load(HBackgroundImageListener l) {
   listener=l;
   if (image != null)
      java.awt.Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, this);
}

//internal
java.awt.Image getImage() {
   return image;
}

//ImageObserver
public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y, int width, int height) {
   if (img==image) {
      if ((infoflags & ALLBITS) != 0)
         if (listener != null)
            listener.imageLoaded(new HBackgroundImageEvent(this, HBackgroundImageEvent.BACKGROUNDIMAGE_LOADED));
      if ((infoflags & ERROR) != 0)
         if (listener != null)
            listener.imageLoaded(new HBackgroundImageEvent(this, fromfile ? HBackgroundImageEvent.BACKGROUNDIMAGE_FILE_NOT_FOUND : HBackgroundImageEvent.BACKGROUNDIMAGE_INVALID));
   }
   return false;
}


}
