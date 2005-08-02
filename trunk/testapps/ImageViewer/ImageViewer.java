import javax.tv.xlet.Xlet;
import javax.tv.xlet.XletContext;
import javax.tv.xlet.XletStateChangeException;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;

import java.io.File;
import java.io.FilenameFilter;

import org.havi.ui.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class ImageViewer implements Xlet {

XletContext context;
HScene scene;
ImageWidget widget;

class ImageWidget extends HComponent implements KeyListener, FilenameFilter, ImageObserver {

   Timer timer=null;
   String[] files;
   Image image;
   int currentFile;
   boolean running=false;

   ImageWidget() {
   }
   
   void init() {
      try {
         File dir=new File("images");
         files=dir.list(this);
         System.out.print("Having the following images: ");
         for (int i=0;i<files.length;i++)
            System.out.print(files[i]);
         System.out.println("");
      } catch (SecurityException ex) {
         ex.printStackTrace();
      }
   }
   
   private class TimerTaskNext extends TimerTask {
      public void run() {
         ImageWidget.this.loadNext();
      }
   }
   
   void start() {
      if (running)
         return;
      timer=new Timer();
      running=true;
      timer.schedule(new TimerTaskNext(), new Date(), 3000);
   }
   
   void stop() {
      System.out.println("ImageWidget: stop()"+timer);
      if (image != null) {
         image.flush();
         image=null;
      }
      if (timer != null) {
         timer.cancel();
      }
      currentFile=0;
      running=false;
   }
   
   void loadNext() {
      //System.out.println("ImageWidget: loadNext() "+currentFile);
      String imageFile="images"+File.separator+files[currentFile++];
      if (currentFile>=files.length) {
         stop();
      }
      if (image != null) {
         image.flush();
         image=null;
      }
      loadImage(imageFile);
      repaint();
   }
   
   void loadImage(String file) {
      // Create a MediaTracker to tell us when the image has loaded
      MediaTracker tracker = new MediaTracker(this);
      // Then load the image
      image = Toolkit.getDefaultToolkit().getImage(file);

      // add the image to the MediaTracker...
      tracker.addImage(image, 0);

      // ...and wait for it to finish loading
      try {
         tracker.waitForAll();
      }
      catch(InterruptedException e) {
         // Ignore the exception, since there's not a lot we can do.
         image = null;
      }   
   }
   
   public void paint(Graphics graphics) {
      //System.out.println("ImageWidget: paint()");
      
      if (image==null)
         return;
         
      Dimension size = getSize();
      int imageHeight=image.getHeight(this);
      int imageWidth=image.getWidth(this);
      if (imageHeight==-1 || imageWidth==-1)
         return; //images should already be loaded
         
      float widgetDimension=((float)size.width)/((float)size.height);
      float imageDimension=((float)imageWidth)/((float)imageHeight);
      
      if (imageDimension >= widgetDimension) {
         //fit width
         float factor=((float)size.width)/((float)imageWidth);
         int scaledHeight=(int)(factor*((float)imageHeight));
         int halfBlackBorder=(size.height-scaledHeight)/2;
         graphics.drawImage(image, 0, halfBlackBorder, size.width, scaledHeight, null);
      } else {
         //fit height
         float factor=((float)size.height)/((float)imageHeight);
         int scaledWidth=(int)(factor*((float)imageWidth));
         int halfBlackBorder=(size.width-scaledWidth)/2;
         graphics.drawImage(image, halfBlackBorder, 0, scaledWidth, size.height, null);
      }
   }

   //KeyListener
   public void keyTyped(KeyEvent e) {
   }
   
   public void keyReleased(KeyEvent e) {
   }
   
   public void keyPressed(KeyEvent e) {
   }
     
   //FilenameFilter
   public boolean accept(File dir, String name) {
      return name.endsWith(".jpg") 
         || name.endsWith(".jpeg")
         || name.endsWith(".png")
         || name.endsWith(".gif");
   }
   
   //ImageObserver
   public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
      return true;
   }

}

public ImageViewer() {
}

public void initXlet(XletContext xletContext) throws XletStateChangeException {
   context = xletContext;
}

public void startXlet() throws XletStateChangeException {
      // Before we can do anything, we need to create a new HScene so that we
      // can display something on the screen.  First, we get a reference to
      // the HSceneFactory...
      HSceneFactory factory = HSceneFactory.getInstance();

      // ...and then we can get an HScene.
      
      //  First, we create an HSceneTemplate
      HSceneTemplate hst = new HSceneTemplate();

      // Set it to cover the entire screen
      hst.setPreference(
         HSceneTemplate.SCENE_SCREEN_DIMENSION,
            new org.havi.ui.HScreenDimension(1,1),
         HSceneTemplate.REQUIRED);
      hst.setPreference(
         HSceneTemplate.SCENE_SCREEN_LOCATION,
         new org.havi.ui.HScreenPoint(0,0),
         HSceneTemplate.REQUIRED);
      
      // Now actually get the HScene
      scene = factory.getBestScene(hst);


      // Since we didn't specify a size for our HScene when we created it,
      // we need to find its size
      Rectangle rect = scene.getBounds();
      //subtract overscan area
      rect.grow( -( (int)(0.05*((float)(rect.width))) ), -( (int)(0.05*((float)(rect.height))) ) );

      widget=new ImageWidget();
      widget.init();
      // Set the size of our component to fill the HScene, make it
      // visible and set its background colour to be black.
      widget.setBounds(rect);
      widget.setVisible(true);
      //widget.setBackground(Color.black);
      widget.addKeyListener(widget);

      scene.setBackgroundMode(HScene.BACKGROUND_FILL);
      scene.setBackground(Color.black);
      // Add ourselves to the HScene.  While we're adding components to the
      // scene, we should hide it to avoid any nasty flickering, but HScenes 
      // are not visible when they are created.
      scene.add(widget);
      scene.setVisible(true);

      // Give focus to this component
      widget.requestFocus();
      widget.start();
}

public void pauseXlet() {
}

public void destroyXlet(boolean flag) throws XletStateChangeException {
   widget.stop();
   if (scene != null) {
      scene.setVisible(false);
      scene.removeAll();
      HSceneFactory.getInstance().dispose(scene);
      scene = null;
   }
   context.notifyDestroyed();
}


}