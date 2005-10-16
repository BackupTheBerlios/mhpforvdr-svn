
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;

import javax.tv.xlet.Xlet;
import javax.tv.xlet.XletContext;
import javax.tv.xlet.XletStateChangeException;

import org.dvb.event.*;
import org.davic.resources.*;
import org.davic.net.dvb.*;
import org.dvb.si.*;
import org.dvb.ui.DVBGraphics;
import org.dvb.ui.DVBAlphaComposite;

import org.dvb.application.AppID;
import org.dvb.application.AppsDatabase;
import org.dvb.application.AppProxy;
import org.dvb.application.AppAttributes;
import org.dvb.application.CurrentServiceFilter;

import org.havi.ui.HDefaultTextLayoutManager;
import org.havi.ui.HScene;
import org.havi.ui.HSceneFactory;
import org.havi.ui.HScreen;
import org.havi.ui.HStaticText;
import org.havi.ui.HComponent;
import org.havi.ui.HContainer;

import org.dvb.user.*;
import org.dvb.io.ixc.IxcRegistry;
import javax.tv.util.*;

import javax.tv.service.SIManager;
import javax.tv.service.Service;
import javax.tv.service.SIRequestor;
import javax.tv.service.SIRetrievable;
import javax.tv.service.ServiceNumber;
import javax.tv.service.SIException;
import javax.tv.service.navigation.ServiceList;
import javax.tv.service.navigation.ServiceDetails;
import javax.tv.service.ServiceNumber;
import javax.tv.service.navigation.ServiceIterator;
import javax.tv.service.guide.ProgramEvent;
import javax.tv.service.guide.ProgramEventDescription;
import javax.tv.service.guide.ProgramSchedule;
import javax.tv.service.selection.ServiceContext;
import javax.tv.service.selection.ServiceContextFactory;
import javax.tv.service.selection.InsufficientResourcesException;
import javax.tv.locator.InvalidLocatorException;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;

public class TestXlet implements Xlet {

    XletContext context;
    HScene scene;
    

    public TestXlet() {
    }

    public void initXlet(XletContext xletContext) throws XletStateChangeException {
        System.out.println("begin initXlet");
        context = xletContext;
    }

    public void pauseXlet() {
    }

    public void destroyXlet(boolean flag) throws XletStateChangeException {
       System.out.println("destroyXlet");
       if (scene != null) {
          //scene.setVisible(false);
          //scene.removeAll();
          System.out.println("Calling scene.dispose");
          scene.dispose();
          scene = null;
       }
       context.notifyDestroyed();
    }
    
    public void startXlet() throws XletStateChangeException {
        System.out.println("begin startXlet");
        //testOrgDvbEvent();
        testOrgDvbSi();
        //testOrgDavicDvbLocator();
        //testJavaxTvService();
        //testUI();
        //testPreference();
        //testIXC();
        //testTimers();
        //testXMI();
        //testStackTrace();
        //testJavaIO();
        //testUI_Image();
        //testUI_basic();
        //testUI_negative();
        //testUI_arcs();
        //testUI_KeyEvent();
        //testUI_Font();
        //testOrgDvbApplication();
    }
    
    // helper method
    HScene testComponent(Component comp) {
         if (scene == null) {
            HSceneFactory hsceneFactory = HSceneFactory.getInstance();
            System.out.println("Creating scene");
            scene = hsceneFactory.getFullScreenScene(HScreen.getDefaultHScreen().getDefaultHGraphicsDevice());
            
            comp.setBounds(scene.getBounds());
            comp.setVisible(true);
            scene.add(comp);
            scene.setVisible(true);
         } else {
            comp.setBounds(scene.getBounds());
            comp.setVisible(true);
            scene.add(comp);
         }
         return scene;
    }
    
    // Returns a locator pointing to a channel which will be available (or not, depending on test performed)
    // during the execution of the test using this method
    DvbLocator getTestLocator() throws InvalidLocatorException {
       // ARD on satellite (Astra)
       //return new DvbLocator(1, 1101, 28106);
       // SAT.1 on satellite (Astra)
       //return new org.davic.net.dvb.DvbLocator(133, 33, 46);
       // ARD on Düsseldorf/Ruhrgebiet DVB-T. TID is 6144, testing without TID.
       return new DvbLocator(8468, -1, 256);
    }
    
    
    // --- Here are the unit tests ---
    
    // 4.9.2005: test passed
    void testUI_Font() {
       class FontComponent extends HComponent {
          FontComponent() {
             //setBackground(Color.white);
             System.out.println("FontComponent constructor");
          }
          public void paint(Graphics g) {
             System.out.println("FontComponent paint");
             g.setColor(Color.blue);
             // These four fonts should all result the default font
             Font def = g.getFont();
             Font medium = new Font("SansSerif", Font.BOLD, 36);
             Font large = new Font(null, Font.PLAIN, 100);
             Font unknown = new Font("abcde", Font.BOLD, 14);
             
             String s1 = "Drawn with default font";
             String s2 = "Drawn with medium font";
             int x = 10;
             int y1 = 20, y2 = 56;
             
             g.drawString(s1, x, y1);
             g.setFont(medium);
             g.drawString(s2, x, y2);
             g.setFont(large);
             g.drawString("Large", 200, 200);
             
             FontMetrics m1 = Toolkit.getDefaultToolkit().getFontMetrics(def);
             FontMetrics m2 = Toolkit.getDefaultToolkit().getFontMetrics(medium);
             
             // Test simple layouting (getting the spacing to line above)
             g.setFont(unknown);
             g.drawString("Unknown font", x, y2 + Toolkit.getDefaultToolkit().getFontMetrics(g.getFont()).getAscent() + m2.getDescent() + m2.getLeading());
             
             // Draw rectangle covering the logical extents (including spacing around letters, used to layout)
             // The ink rectangle (including only the pixels touched when drawing) is available by some newer API
             // I think but this is not tested here.
             FontMetrics m=m1;
             int y=y1;
             Color color = Color.red;
             String s = s1;
             for (; m != null;) {
               int width=m.stringWidth(s);
               System.out.println("FontMetrics of medium font: Ascent "+m.getAscent()+", descent "+m.getDescent()+", height "+m.getHeight()+", maxDescent "+m.getMaxDescent()+", width of \'A\' "+m.charWidth('A')+", text width "+width);
               g.setColor(Color.green);
               g.drawRect(x, y-m.getAscent(), width, m.getAscent()+m.getDescent());
               m = ( m == m1 ? m2 : null); color=Color.green; s = s2; y=y2;
             }
          }
       }
       testComponent(new FontComponent());
    }
    
    void testUI_text() {
        /*
       class PrintSomething implements Runnable {
       public void run() {
       System.out.println("Ich bin ein Thread");
       java.awt.Image im=java.awt.Toolkit.getDefaultToolkit().getImage("/usr/local/vdr/apps/HaviExample/foreground.png");
    }
    }
       (new Thread(
       new PrintSomething()
        )).start();
        */
       class TestTextComponent extends HContainer implements KeyListener {
          private HStaticText label;
          private Color[] colors = { Color.black, Color.red, Color.blue };
          private int intColor;
          TestTextComponent() {
             System.out.println("Creating text");
             label = new HStaticText("This text should be yellow", 100, 100, 200, 200, new Font("Tiresias", Font.BOLD, 22), Color.yellow, colors[0], new HDefaultTextLayoutManager());
             add(label);
          }
          public void keyTyped(KeyEvent e) {
          }

          public void keyReleased(KeyEvent e) {
          }

          public void keyPressed(KeyEvent e) {
             intColor++;
             if (intColor == colors.length) {
                intColor = 0;
             }
             label.setBackground(colors[intColor]);
             label.repaint();
          }
       }
       TestTextComponent comp=new TestTextComponent();
       testComponent(comp);
    }
    
    // 31.08.2005 test passed
    void testUI_KeyEvent() {
       class KeyListenerComponent extends HComponent {
       }
       class Listener implements KeyListener {
          String message;
          Listener(String message) {
             this.message=message;
          }
          public void keyTyped(KeyEvent e) {
             System.out.println("KeyListener.keyTyped "+message+": "+e);
          }

          public void keyReleased(KeyEvent e) {
             System.out.println("KeyListener.keyReleased "+message+": "+e);
          }

          public void keyPressed(KeyEvent e) {
             System.out.println("KeyListener.keyPressed "+message+": "+e);
          }
       }
       KeyListenerComponent comp=new KeyListenerComponent();
       HScene s=testComponent(comp);
       comp.addKeyListener(new Listener("from Component"));
       s.addKeyListener(new Listener("from HScene"));
    }
    
    // 30.08.2005: test passed
    void testUI_negative() {
       class SimpleComponent extends HComponent {
          public void paint(Graphics g) {
             for (int i=-100; i<=100; i+=20)
                drawAt(g, i, i);
          }
          void drawAt(Graphics g, int x, int y) {
             int hor = 200;
             int ver = 300;
             g.setColor(Color.white);
             g.drawLine(x, y, x+hor, y);
             g.setColor(Color.yellow);
             g.drawLine(x+hor, y, x+hor, y+ver);
             g.setColor(Color.red);
             g.drawArc(x, y-ver, 2*hor, 2*ver, -90, -90);
          }
      }
       testComponent(new SimpleComponent());
    }
    
    // 30.08.2005: test passed
    void testUI_arcs() {
       class SimpleComponent extends HComponent {
          public void paint(Graphics g) {
             g.setColor(Color.green);
             g.drawArc(0, 0, 100, 100, 90, -270);
             g.setColor(Color.yellow);
             g.drawArc(100, 0, 100, 100, -90, -90);
             g.setColor(Color.red);
             g.drawArc(0, 100, 200, 200, 180, -180);
             g.setColor(Color.blue);
             g.drawArc(200, -200, 2*100, 2*200, -90, -90);
          }
       }
       testComponent(new SimpleComponent());
    }
    
    
    // 29.08.2005: test passed
    void testUI_basic() {
       class SimpleComponent extends HComponent {
          public void paint(Graphics g) {
             g.setColor(Color.black);
             g.drawLine(10, 10, 200, 10);
             g.setColor(Color.yellow);
             g.drawLine(200, 10, 200, 300);
             g.setColor(Color.red);
             g.drawLine(200, 300, 10, 300);
             g.setColor(Color.blue);
             g.drawLine(10, 300, 10, 10);
             g.setColor(new Color(0xF0, 0xF0, 0xF0));
             g.fillRect(210, 310, 50, 50);
          }
       }
       testComponent(new SimpleComponent());
    }
    
    //28.08.2005: test passed
    void testUI_Image() {
       class ImageComponent extends HComponent implements ImageObserver {
          Image imageSpektrum, imageInternet;
          ImageComponent() {
             Toolkit tk=Toolkit.getDefaultToolkit();
             if (tk == null)
                System.out.println("Toolkit is null!");
             imageSpektrum=tk.getImage("farbspektrum.jpg");
             URL url=null;
             // You cannot simply access the internet in MHP, but here it is done
             // to test image loading rather than Internet access.
             try {
                url=new URL("http://www.mhp.org/graphics/mhp-sitewide/logo.gif");
             } catch (java.net.MalformedURLException e) {
                e.printStackTrace();
             }
             imageInternet=tk.getImage(url);
             //imageInternet=imageSpektrum;
          }
          public void paint(Graphics g) {
             //System.out.println("Drawing!");
             g.drawImage(imageSpektrum, 0, 0, this);
             g.drawImage(imageInternet, 400, 300, this);
             Image scaled=imageSpektrum.getScaledInstance(400, 400, 0);
             DVBAlphaComposite transparent=DVBAlphaComposite.getInstance(DVBAlphaComposite.SRC_OVER, 0.5f);
             try {
                ((DVBGraphics) g).setDVBComposite(transparent);
             } catch (org.dvb.ui.UnsupportedDrawingOperationException e) {
                e.printStackTrace();
             }
             g.drawImage(scaled, 50, 50, this);
          }
          public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
             return false;
          }
       }
       testComponent(new ImageComponent());
    }
    
    //1.7.2005: test passed
    void testStackTrace() {
      //This tests inofficial API
      System.out.println("Testing retrieval of current application from stack");
      System.out.println("Current application: "+vdr.mhp.ApplicationManager.getManager().getApplicationFromStack());
    }
    
    //1.7.2005: test passed
    void testJavaIO() {
       System.out.println("Testing dynamic java.io loading and path resolution");
       File f=new File("TestText");
       System.out.println("Can file \"TestText\" be read? "+f.canRead());
       if (f.canRead()) {
         try {
         FileReader fr=new FileReader("TestText");
         char[] ar=new char[25];
         int read=fr.read(ar, 0, ar.length);
         System.out.println("FileReader, read "+read+" characters: "+new String(ar));
         fr.close();
         
         FileInputStream istr=new FileInputStream("TestText");
         byte[] bar=new byte[25];
         read=istr.read(bar, 0, bar.length);
         System.out.println("FileInputStream, read "+read+" characters: "+new String(bar));
         istr.close();
         
         RandomAccessFile raf=new RandomAccessFile("TestText", "r");
         byte[] bar2=new byte[25];
         read=raf.read(bar2);
         System.out.println("RandomAccessFile, read "+read+" characters: "+new String(bar2));
         raf.close();
         
         System.out.println("Now testing writing to home directory. This may not be allowed, raising an exception.");
         FileOutputStream fostr=new FileOutputStream("TestOutput");
         byte[] boar = { 'H', 'a', 'l', 'l', 'o' };
         fostr.write(boar);
         fostr.close();
         
         FileWriter writer=new FileWriter("TestOutput2");
         writer.write("Hello world");
         writer.close();
         
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }
       }
    }

    //18.04.2005: test passed
    void testXMI() {
    
        class XMIComponent extends org.havi.ui.HComponent {
           public void paint(Graphics g) {
               System.out.println("Printing XMIComponent");
               g.setColor(Color.black);
               g.fillRoundRect(10, 10, 90, 90, 15, 18);
               g.setColor(Color.cyan);
               g.drawArc(10, 110, 90, 90, 20, 150);
               g.setColor(Color.green);
               g.drawRoundRect(10, 210, 90, 90, 25, 12);
               g.setColor(Color.red);
               g.fillArc(10, 310, 120, 120, 50, 200);
               g.setColor(Color.yellow);
               int[] x=new int [6];
               int[] y=new int [6];
               x[0]=140;
               y[0]=110;
               x[1]=600;
               y[1]=400;
               x[2]=600;
               y[2]=150;
               x[3]=500;
               y[3]=120;
               x[4]=170;
               y[4]=400;
               x[5]=125;
               y[5]=200;
               g.fillPolygon(x,y,6);
           }
        }
        testComponent(new XMIComponent());
    }
    
   //12.04.2005: test passed
   void testPreference() {
      System.out.println("testPreference: Creating GeneralPreference \"User Name\"");
      GeneralPreference pref = new GeneralPreference("User Name");
      UserPreferenceManager manager = UserPreferenceManager.getInstance();
      manager.read(pref);
      if (pref.hasValue())
         System.out.println("testPreference: (first): "+pref.toString());
      else 
         System.out.println("testPreference: (first): no value");
      pref.add("VDR user");
      try {
         manager.write(pref);
      } catch (org.dvb.user.UnsupportedPreferenceException e) {
         e.printStackTrace();
      } catch (java.io.IOException e) {
         e.printStackTrace();
      }
      manager.read(pref);
      if (pref.hasValue())
         System.out.println("testPreference: (second): "+pref.toString());
      else 
         System.out.println("testPreference: (second): no value");
   }
   
   //12.04.2005: test passed
   void testIXC() {
      class ExampleRemote implements java.rmi.Remote {
         public void doSomething() {}
      }
      
      System.out.println("testIXC: bind()");
      
      try {
         IxcRegistry.bind(context, "MyObject", new ExampleRemote());
      } catch (java.rmi.AlreadyBoundException e) {
         e.printStackTrace();
      }
      
      System.out.println("testIXC: list()");
      String[] list=IxcRegistry.list(context);
      for (int i=0;i<list.length;i++) {
         System.out.println(list[i]);
      }
      
      System.out.println("testIXC: rebind()");
      IxcRegistry.rebind(context, "MyObject", new ExampleRemote());
      if (list.length>0) {
         try {
            System.out.println("testIXC: lookup("+list[0]+")");
            java.rmi.Remote obj=IxcRegistry.lookup(context, list[0]);
            System.out.println("testIXC: returned object is "+obj);
         } catch (java.rmi.NotBoundException e) {
            e.printStackTrace();
         } catch (IllegalArgumentException e) {
            e.printStackTrace();
         }
      }
   }
   
   //12.04.2005: test passed
   void testTimers() {
      class TimerListener implements TVTimerWentOffListener {
         public void timerWentOff(TVTimerWentOffEvent e) {
            System.out.println("TimerListener: timer went off, spec "+e.getTimerSpec()+", timer  "+(TVTimer)e.getSource()+" now is time "+(new java.util.Date())+", this should happen exactly once");
            ((TVTimer)e.getSource()).deschedule(e.getTimerSpec());
         }
      }
      TVTimer timer = TVTimer.getTimer();
      TVTimerSpec spec = new TVTimerSpec();
      java.util.Date now=new java.util.Date();
      spec.addTVTimerWentOffListener(new TimerListener());
      spec.setDelayTime(2000);
      spec.setRepeat(true);
      try {
         timer.scheduleTimerSpec(spec);
      } catch (javax.tv.util.TVTimerScheduleFailedException e) {
         e.printStackTrace();
      }
   }
    
   //27.01.04: Test passed
   void testOrgDvbEvent() {

      class Example implements UserEventListener, ResourceStatusListener, ResourceClient {
         private int myStatus ;
         public Example () {
            EventManager em ;
            UserEventRepository repository;
            em = EventManager.getInstance();

            repository = new UserEventRepository ("R1") ;
            repository.addKey (UserEvent.VK_ENTER);
            repository.addKey (UserEvent.VK_COLORED_KEY_0);
            em.addUserEventListener ((UserEventListener)this, (ResourceClient)this, repository) ;

            repository = new UserEventRepository ("R2") ;
            repository.addKey (UserEvent.VK_0);
            em.addUserEventListener ((UserEventListener)this, repository) ;

            em.addResourceStatusEventListener (this) ;
         } 
         // methods defined by the UserEventListener interface. 
         public void userEventReceived (UserEvent e) { System.out.print("Got Event ");System.out.println(e.getCode());} 

         // Methods defined by the ResourceClient interface. 
         // In the case a cooperative application asks for an user event * exclusively used by me. 
         public boolean requestRelease(ResourceProxy proxy, Object requestData) {
            String name; 

            // let's retrieve the name of the repository, that I have created, and
            name = ((RepositoryDescriptor)proxy).getName() ;
            System.out.print("requestRelease: ");System.out.println(name);
            if ((name.compareTo ("R1") == 0)) {
               // Ok I release this event.
               return true; 
            } else {
               // No I need this event, sorry !
               return false;
            }
         } 

         public void release (ResourceProxy proxy) { System.out.println("Release");}
         public void notifyRelease (ResourceProxy proxy) {System.out.println("NotifyRelease");}
         public void statusChanged (ResourceStatusEvent event) { }
      }
      
      System.out.println("begin testOrgDvbEvent");
      Example ex=new Example();
      Example ex2=new Example();
      UserEvent ue=new UserEvent(this, UserEvent.UEF_KEY_EVENT, 
                                 UserEvent.KEY_PRESSED, UserEvent.VK_ENTER, 0);
      System.out.print("DispatchEvent: ");
      System.out.println( EventManager.getInstance().DispatchEvent(ue));
      
      ue=new UserEvent(this, UserEvent.UEF_KEY_EVENT, 
                       UserEvent.KEY_PRESSED, UserEvent.VK_COLORED_KEY_0, 0);
      System.out.print("DispatchEvent: ");
      System.out.println( EventManager.getInstance().DispatchEvent(ue));
      
      ue=new UserEvent(this, UserEvent.UEF_KEY_EVENT, 
                       UserEvent.KEY_PRESSED, UserEvent.VK_0, 0);
      System.out.print("DispatchEvent: ");
      System.out.println( EventManager.getInstance().DispatchEvent(ue));
  
   }
   
   void testOrgDvbApplication() {
      class OrgDvbAppTester {
         AppProxy proxy;
         AppAttributes att;
         OrgDvbAppTester(int oid, int aid) {
            this(new AppID(oid, aid));
         }
         OrgDvbAppTester(AppID id) {
            System.out.println("Testing application with OID "+id.getOID()+", AID "+id.getAID());
            AppsDatabase db = AppsDatabase.getAppsDatabase();
            proxy = db.getAppProxy(id);
            att = db.getAppAttributes(id);
         }
         void test() {
            System.out.println(proxy == null ? "Did not find AppProxy" : "Found AppProxy");
            if (att == null)
               System.out.println("Did not find AppAttributes");
            else {
               System.out.println("Found AppAttributes: Name "+att.getName());
            }
         }
      }
      // WDR-Ticker, DVB-T
      new OrgDvbAppTester(19, 100).test();
      // non-existant
      new OrgDvbAppTester(100, 100).test();
      
      AppsDatabase db = AppsDatabase.getAppsDatabase();
      CurrentServiceFilter filter = new CurrentServiceFilter();
      Enumeration currentApps = db.getAppIDs(filter);
      while (currentApps.hasMoreElements()) {
         AppID id = (AppID)currentApps.nextElement();
         new OrgDvbAppTester(id).test();
      }
   }
   
   
   //11.04.04: Test passed
   void testOrgDvbSi() {
      class Example implements SIRetrievalListener {
         public void postRetrievalEvent(SIRetrievalEvent event) {
            System.out.println("Retrieved result, "+event.getClass().toString());
            if (event instanceof SISuccessfulRetrieveEvent) {
               SIIterator it=((SISuccessfulRetrieveEvent)event).getResult();
               DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
               switch (((Integer)event.getAppData()).intValue()) {
               case 1:
                  //SINetwork - passed
                  while (it.hasMoreElements()) {
                     SINetwork net=(SINetwork)it.nextElement();
                     System.out.println("Having SINetwork with id "+net.getNetworkID()+", network name \""+net.getName()+"\", short name \""+net.getShortNetworkName()+"\", update time is "+format.format(net.getUpdateTime())+", acquired from transport stream with ID "+net.getDataSource().getTransportStreamId());
                     short[] tags=net.getDescriptorTags();
                     System.out.println("Descriptor tags: ");
                     for (int i=0;i<tags.length;i++)
                        System.out.print(tags[i]+" ");
                     System.out.println();
                     short[] descs=new short[] { -1 };
                     /*
                     try {
                        net.retrieveSITransportStreams(SIInformation.FROM_CACHE_OR_STREAM, new Integer(4), this, descs);
                        net.retrieveDescriptors(SIInformation.FROM_CACHE_OR_STREAM, new Integer(7), this);
                     } catch (SIIllegalArgumentException ex) {
                        ex.printStackTrace();
                     }
                     */
                  }
                  break;
               case 2:
                  //SIService - passed
                  while (it.hasMoreElements()) {
                     SIService ser=(SIService)it.nextElement();
                     System.out.println("Having SIService with ID triple "+ser.getOriginalNetworkID()+" - "                           +ser.getTransportStreamID()+" - "+ser.getServiceID()+", service name \""+ser.getName()+"\", provider name \""+ser.getProviderName()+"\", short service name \""+ser.getShortServiceName()+"\"");
                     System.out.println("Descriptor tags: ");
                     short[] tags=ser.getDescriptorTags();
                     for (int i=0;i<tags.length;i++)
                        System.out.print(tags[i]+" ");
                     System.out.println();
                     short[] descs=new short[] { -1 };
                     //ser.retrievePresentSIEvent(SIInformation.FROM_CACHE_OR_STREAM, new Integer(3), this, descs);
                     //ser.retrieveFollowingSIEvent(SIInformation.FROM_CACHE_OR_STREAM, new Integer(3), this, descs);
                     // retrieve events from 20:15 - 23:45, today
                     Calendar cal = Calendar.getInstance();
                     cal.set(Calendar.HOUR_OF_DAY, 20);
                     cal.set(Calendar.MINUTE, 15);
                     Date begin = cal.getTime();
                     cal.set(Calendar.HOUR_OF_DAY, 23);
                     cal.set(Calendar.MINUTE, 30);
                     Date end = cal.getTime();
                     try {
                        ser.retrieveScheduledSIEvents(SIInformation.FROM_CACHE_OR_STREAM, new Integer(3), this, descs, begin, end);
                     } catch (SIIllegalArgumentException ex) {
                        ex.printStackTrace();
                     }
                  }
                  break;
               case 3:
                  //SIEvent - passed
                  boolean printed = false;
                  int i=0;
                  //synchronized (this) { try { wait(400); } catch (InterruptedException ex) {} }
                  while (it.hasMoreElements()) {
                     SIEvent ev=(SIEvent)it.nextElement();
                     if (!printed) {
                        System.out.println("Having SIEvent with ID "+ev.getOriginalNetworkID()+" - "
                              +ev.getTransportStreamID()+" - "+ev.getServiceID()+" - "+ev.getEventID());
                        printed = true;
                     }
                     System.out.println(" Event "+(++i)+": "+format.format(ev.getStartTime())+", duration "+ev.getDuration()/1000+" seconds: \""+ev.getName()+"\", short name \""+ev.getShortEventName()+"\"");
                  }
                  System.out.println("All events");
                  break;
               case 4:
                  //SITransportStream (NIT) - passed
                  while (it.hasMoreElements()) {
                     SITransportStreamNIT tra=(SITransportStreamNIT)it.nextElement();
                     System.out.println("Having SITransportStreamNIT with triple"+tra.getNetworkID()
                                         +" "+tra.getOriginalNetworkID()+" "+tra.getTransportStreamID());
                  }
                  break;
               case 5:
                  //PMTService - passed
                  while (it.hasMoreElements()) {
                     PMTService ser=(PMTService)it.nextElement();
                     System.out.println("Having PMTService with triple "+ser.getOriginalNetworkID()+" "
                                         +ser.getTransportStreamID()+" "+ser.getServiceID());
                     System.out.println("with PcrPid "+ser.getPcrPid()+" updateTime: "+format.format(ser.getUpdateTime()));
                     
                     short[] descs=new short[] { -1 };
                     /*
                     try {
                        ser.retrievePMTElementaryStreams(SIInformation.FROM_CACHE_OR_STREAM, new Integer(6), this, descs);
                     } catch (SIIllegalArgumentException ex) {
                        ex.printStackTrace();
                     }
                     */
                  }
                  break;
               case 6:
                  //PMTElementaryStream - passed
                  while (it.hasMoreElements()) {
                     PMTElementaryStream str=(PMTElementaryStream)it.nextElement();
                     System.out.println("Having PMTElementaryStream with four "+str.getOriginalNetworkID()+" "
                                         +str.getTransportStreamID()+" "+str.getServiceID()+" "+str.getComponentTag());
                     System.out.println("with type "+str.getStreamType()+" and PID "+str.getElementaryPID());
                  }
                  break;
               case 7:
                  //Descriptor - passed
                  while (it.hasMoreElements()) {
                     Descriptor d=(Descriptor)it.nextElement();
                     System.out.println("Having Descriptor with tag "+d.getTag()+" and length "+d.getContentLength()+
                                        " and bytes 0+1 "+d.getByteAt(0)+d.getByteAt(1));
                  }
                  break;
               }
            }
         }
      }
      System.out.println("Testing org.dvb.si");
      Example ex=new Example();
      SIDatabase[] dbs=SIDatabase.getSIDatabase();
      if (dbs.length == 0) {
         System.out.println("No database available");
         return;
      }
      SIDatabase db = dbs[0];
      short[] descs=new short [] { -1 };
      org.davic.net.dvb.DvbLocator loc;
      try {
         loc=getTestLocator();
      } catch (javax.tv.locator.InvalidLocatorException e) {
         e.printStackTrace();
         return;
      }
      try {
         //db.retrieveSINetworks(SIInformation.FROM_CACHE_OR_STREAM, new Integer(1), ex, -1, descs);
         db.retrieveSIService(SIInformation.FROM_CACHE_OR_STREAM, new Integer(2), ex, loc, descs);
         //db.retrievePMTService(SIInformation.FROM_CACHE_OR_STREAM, new Integer(5), ex, loc, descs);
      } catch (SIIllegalArgumentException e) {
         e.printStackTrace();
      }
   }

   // 27.04.04: Test passed
   void testOrgDavicDvbLocator() {
      System.out.println("testOrgDavicDvbLocator");
      //The test is passed if the external form is identical (1-11) of equivalent in
      //the current context (12-13) to the string used to create the locator
      // and if all invalid locators below throw an exception.
      
      try {
         DvbLocator loc1=new DvbLocator("dvb://'some_description'.3&4&5;45/path/to/files");
         DvbLocator loc2=new DvbLocator("dvb://'some_description';45/path/to/files");
         DvbLocator loc3=new DvbLocator("dvb://'some_description'.3&4&5/path/to/files");
         DvbLocator loc4=new DvbLocator("dvb://123.456.789");
         DvbLocator loc5=new DvbLocator("dvb://123.456.789;42");
         DvbLocator loc6=new DvbLocator("dvb://123.456.789/images/logo.gif");
         DvbLocator loc7=new DvbLocator("dvb://123.456.789.66");
         DvbLocator loc8=new DvbLocator(400, 500);
         int[] t={50, 60};
         DvbLocator loc9=new DvbLocator(400, 500, 600, 700, t, "/hallo/h.class");
         DvbLocator loc10=new DvbLocator("dvb://123"); //non-standard extension!
         DvbLocator loc11=new DvbLocator("dvb://bouquet123"); //non-standard extension!
         //DvbLocator loc12=new DvbLocator("dvb://123..789");
         //DvbLocator loc13=new DvbLocator("dvb:/path/of/channel");
         
         System.out.println(loc1.toExternalForm());
         System.out.println(loc2.toExternalForm());
         System.out.println(loc3.toExternalForm());
         System.out.println(loc4.toExternalForm());
         System.out.println(loc5.toExternalForm());
         System.out.println(loc6.toExternalForm());
         System.out.println(loc7.toExternalForm());
         System.out.println(loc8.toExternalForm());
         System.out.println(loc9.toExternalForm());
         System.out.println(loc10.toExternalForm());
         System.out.println(loc11.toExternalForm());
         //System.out.println(loc12.toExternalForm());
         //System.out.println(loc13.toExternalForm());
      } catch (javax.tv.locator.InvalidLocatorException ex) {
         ex.printStackTrace();
      }
      
      try {
         //an obviously invalid URL
         DvbLocator loc14=new DvbLocator("dvb://dfgl.wepolfw;&,./67.,:'//");
      } catch (javax.tv.locator.InvalidLocatorException ex) {
         try {
            //a more subtly invalid URL
            DvbLocator loc15=new DvbLocator("dvb://123.456.789.66.55;7&4;8///ui'ho'.class"); 
         } catch (javax.tv.locator.InvalidLocatorException ex_) {
            try {
               //a most obviously invalid URL from /dev/random
               DvbLocator loc16=new DvbLocator("(Ö°Êç^¥#¤Ú¡cÐ¤LËÍ<;ZÄkÜïË¬MNQJ{"); 
            } catch (javax.tv.locator.InvalidLocatorException ex__) {
               System.out.println("All invalid URLs threw an exception.");
            }
         }
      }
   }
   
   
   //4.5.2004: Test passed (first, org.dvb.si implementation)
   //10.10.2005: Test passed (VDR based reimplementation)
   void testJavaxTvService() {
      //Note: This test does not test all parts of the javax.tv.service.*.* API
      //Not covered: javax.tv.service.selection.*
         class MyRequestor implements SIRequestor {
            public void notifySuccess ( SIRetrievable [] result) {
               System.out.println("Received request success "+result.length);
               if (result.length > 0) {
                  if (result[0] instanceof ServiceDetails) {
                     ServiceDetails ser=(ServiceDetails)result[0];
                     System.out.println("Service Details for "+ser.getLocator()+", \""+ser.getLongName()+"\": "+ser.getServiceType()+", "+ser.getDeliverySystemType());
                     javax.tv.service.Service service=ser.getService();
                     if (service instanceof ServiceNumber)
                        System.out.println("Stored as service \""+service.getName()+"\", number "+((ServiceNumber)service).getServiceNumber());
                     ProgramSchedule schedule=ser.getProgramSchedule();
                     schedule.retrieveCurrentProgramEvent(this);
                     // retrieve events from 20:15 - 23:45, today
                     Calendar cal = Calendar.getInstance();
                     cal.set(Calendar.HOUR_OF_DAY, 20);
                     cal.set(Calendar.MINUTE, 15);
                     Date begin = cal.getTime();
                     cal.set(Calendar.HOUR_OF_DAY, 23);
                     cal.set(Calendar.MINUTE, 30);
                     Date end = cal.getTime();
                     // will throw an exception if you work too late and it is between 23:30 and 0:00 ;-)
                     try {
                        schedule.retrieveFutureProgramEvents(begin, end, this);
                     } catch (SIException ex) {
                        ex.printStackTrace();
                     }
                  } else if (result[0] instanceof ProgramEvent) {
                     DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
                     if (result.length == 1) {
                        ProgramEvent event=(ProgramEvent)result[0];
                        System.out.println("Event "+event.getLocator()+", \""+event.getName()+"\", from "+format.format(event.getStartTime())+" to "+format.format(event.getEndTime()) );
                        event.retrieveDescription(this);
                        javax.tv.service.Service service=event.getService();
                        if (service instanceof ServiceNumber)
                           System.out.println("Stored as service \""+service.getName()+"\", number " +((ServiceNumber)service).getServiceNumber());
                     } else {
                        System.out.println("Schedule: ");
                        for (int i=0;i<result.length;i++) {
                           ProgramEvent event=(ProgramEvent)result[i];
                           System.out.println(format.format(event.getStartTime())+" - "+format.format(event.getEndTime())+": \""+event.getName()+"\"");
                        }
                     }
                  } else if (result[0] instanceof ProgramEventDescription) {
                     System.out.println("Description of program event: "+((ProgramEventDescription)result[0]).getProgramEventDescription());
                  }
               }
            }
            public void notifyFailure ( javax.tv.service.SIRequestFailureType reason) {
               System.out.println("Received request failure because of "+reason);
            }
         }

      SIManager man=SIManager.createInstance();
      SIRequestor req=new MyRequestor();
      String[] s=man.getSupportedDimensions();
      if (s.length != 0) {
         try {
            System.out.println(man.getRatingDimension(s[0]).getDimensionName());
         } catch (javax.tv.service.SIException ex) {
            ex.printStackTrace();
         }
      }
      
      //TODO: test Transport
      javax.tv.service.transport.Transport[] ts=man.getTransports();
      
      DvbLocator loc=null;
      try {
         loc=getTestLocator();
      } catch (javax.tv.locator.InvalidLocatorException ex) {
         ex.printStackTrace();
      }
      
      try {
         System.out.println("Retrieving service via its locator");
         man.retrieveSIElement(loc, req);
      } catch (javax.tv.locator.InvalidLocatorException ex) {
         ex.printStackTrace();
      } catch (SecurityException ex) {
         ex.printStackTrace();
      }
      
      Service service=null;
      try {
         service=man.getService(loc);
      } catch (javax.tv.locator.InvalidLocatorException ex) {
         ex.printStackTrace();
      } catch (SecurityException ex) {
         ex.printStackTrace();
      }
      
      if (service == null)
         System.out.println("Test service is not available");
      else {
         if (service instanceof ServiceNumber)
            System.out.println("Test service is channel "+((ServiceNumber)service).getServiceNumber()+" \""+service.getName()+"\", "+service.getLocator() );
         else
            System.out.println("Test service is \""+service.getName()+"\" "+service.getLocator()+" - service number unknown!");
      }
      
      ServiceList list = man.filterServices(null);
      System.out.println("ServiceList contains "+list.size()+" elements:");
      
      for (ServiceIterator it = list.createServiceIterator(); it.hasNext(); ) {
         service = it.nextService();
         if (service instanceof ServiceNumber)
            System.out.print(" Service number "+((ServiceNumber)service).getServiceNumber()+": ");
         System.out.println("\""+service.getName()+"\", "+service.getLocator());
      }
      
      ServiceContextFactory factory = ServiceContextFactory.getInstance();
      ServiceContext servicecontext = null;
      try {
         servicecontext = factory.createServiceContext();
      } catch (InsufficientResourcesException ex) {
         ex.printStackTrace();
      } catch (SecurityException ex) {
         ex.printStackTrace();
      }
      
      if (servicecontext == null) {
         System.out.println("Service context is null!");
      } else {
         Service currentService = servicecontext.getService();
         if (currentService == null)
            System.out.println("Current service is null!");
         else {
            System.out.println("Current service is "+service.getName()+" "+service.getLocator()+" service type "+service.getServiceType());
         }
      }
   }
   
   
}
