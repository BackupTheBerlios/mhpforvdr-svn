package java.awt;

import java.awt.ImageNativeProducer;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.awt.peer.WindowPeer;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Category;

import kaffe.util.log.LogClient;
import kaffe.util.log.LogStream;

/* $Id: Toolkit.java,v 1.1 2002/09/25 12:52:07 dok Exp $ */

/**
 * Toolkit - used to be an abstract factory for peers, but since we don't have
 * peers, it is just a simple anchor for central mechanisms (like System-
 * EventQueue etc.) and a wrapper for all native methods. Of course, it is a
 * singleton. Copyright (c) 1998 Transvirtual Technologies, Inc.  All rights
 * reserved. See the file "license.terms" for information on usage and
 * redistribution of this file.
 * @author P.C.Mehlitz
 */
class FlushThread extends Thread {
    boolean stop;
    int flushInterval;

    FlushThread( int interval ) {
        super( "AWT-Flusher" );
        flushInterval = interval;
        setPriority( Thread.MIN_PRIORITY + 1 );
    }

    public void run() {
        while ( !stop ) {
            Toolkit.tlkFlush();
            try {
                Thread.sleep( flushInterval );
            } catch ( Exception x ) {
            }
        }
    }

    void stopFlushing() {
        stop = true;
    }
}


class NativeCollector extends Thread {
    NativeCollector() {
        super( "AWT-native" );
    }

    public void run() {
    // this does not return until the we shut down the system, since it
    // consititutes the native dispatcher loop. Don't be confused about
    // tlkInit being a sync method. It gives up the lock in the native
    // layer before falling into its dispatcher loop
        try {
            if ( !Toolkit.tlkInit( System.getProperty( "awt.display" ) ) ) {
                throw new AWTError( "native layer init failed" );
            }
        } catch ( Throwable x ) {
            x.printStackTrace();
        }
    }
}


public class Toolkit {
    static Category CAT = Category.getInstance( Toolkit.class );
    static Toolkit singleton;
    static Dimension screenSize;
    static int resolution;
    public static EventQueue eventQueue;
    static EventDispatchThread eventThread;
    static NativeClipboard clipboard;
    static ColorModel colorModel;

    static LightweightPeer lightweightPeer = new LightweightPeer() {
    };

    static WindowPeer windowPeer = new WindowPeer() {
    };
    static FlushThread flushThread;
    static NativeCollector collectorThread;
    public static int flags;
    public final static int FAILED                 = -1;
    public final static int IS_BLOCKING            = 1;
    public final static int IS_DISPATCH_EXCLUSIVE  = 2;
    public final static int NEEDS_FLUSH            = 4;
    public final static int NATIVE_DISPATCHER_LOOP = 8;
    public final static int EXTERNAL_DECO   = 16;

    static {
        CAT.debug( "Toolkit static block called" );
        System.loadLibrary( "kawt" );
        flags = tlkProperties();
        if ( ( flags & NATIVE_DISPATCHER_LOOP ) == 0 ) {
            if ( !tlkInit( System.getProperty( "awt.display" ) ) ) {
                throw new AWTError( "native layer initialization failed" );
            }
            initToolkit();
        } else {
      // Not much we can do here, we have to delegate the native init
      // to a own thread since tlkInit() doesn't return. Wait for this
      // thread to flag that initialization has been completed
            collectorThread = new NativeCollector();
            collectorThread.start();
            try {
                synchronized( Toolkit.class ) {
                    while ( singleton == null ) {
                        Toolkit.class.wait();
                    }
                }
            } catch ( Exception x ) {
                x.printStackTrace();
            }
        }
    }

    public Toolkit() {
    }

    public void beep() {
        tlkBeep();
    }

    public native static synchronized void cbdFreeClipboard( int cbdData );

    public native static synchronized Transferable
        cbdGetContents( int cbdData );

    public native static synchronized int cbdInitClipboard();

    public native static synchronized boolean cbdSetOwner( int cbdData );

    public int checkImage( Image image, int width, int height,
        ImageObserver observer ) {
            return ( image.checkImage( width, height, observer, false ) );
    }

    public native static synchronized long clrBright( int rgbValue );

    public native static synchronized long clrDark( int rgbValue );

    public native static synchronized ColorModel clrGetColorModel();

    public native static synchronized int clrGetPixelValue( int rgb );

    public native static synchronized int clrSetSystemColors( int[] sysClrs );

    public Image createImage( ImageProducer producer ) {
        return new Image( producer );
    }

    public Image createImage( byte[] imageData ) {
        return createImage( imageData, 0, imageData.length );
    }

    public Image createImage( byte[] imagedata, int imageoffset,
        int imagelength ) {
            return new Image( imagedata, imageoffset, imagelength );
    }

    ComponentPeer createLightweight( Component c ) {
    // WARNING! this is just a dummy to enable checks like
    // "..getPeer() != null.. or ..peer instanceof LightweightPeer..
    // see createWindow()
        return lightweightPeer;
    }

    static void createNative( Component c ) {
        WMEvent e = null;
        synchronized( Toolkit.class ) {
      // even if this could be done in a central location, we defer this
      // as much as possible because it might involve polling (for non-threaded
      // AWTs), slowing down the startup time
            if ( eventThread == null ) {
                startDispatch();
            }
        }
    // do we need some kind of a context switch ?
        if ( ( flags & IS_DISPATCH_EXCLUSIVE ) != 0 ) {
            if ( ( flags & NATIVE_DISPATCHER_LOOP ) != 0 ) {
                if ( Thread.currentThread() != collectorThread ) {
	  // this is beyond our capabilities (there is no Java message entry we can call
	  // in the native collector), we have to revert to some native mechanism
                    e = WMEvent.getEvent( c, WMEvent.WM_CREATE );
                    evtSendWMEvent( e );
                }
            } else {
                if ( Thread.currentThread() != eventThread ) {
	  // we can force the context switch by ourselves, no need to go native
                    e = WMEvent.getEvent( c, WMEvent.WM_CREATE );
                    eventQueue.postEvent( e );
                }
            }
      // Ok, we have a request out there, wait for it to be served
            if ( e != null ) {
	// we should check for nativeData because the event might
	// already be processed (depending on the thread system)
                while ( c.getNativeData() == 0 ) {
                    synchronized( e ) {
                        try {
                            e.wait();
                        } catch ( InterruptedException x ) {
                        }
                    }
                }
                return;
            }
        } else {
      // no need to switch threads, go native right away
            c.createNative();
        }
    }

    protected WindowPeer createWindow( Window w ) {
    // WARNING! this is just a dummy to enable checks like
    // "..getPeer() != null.. or ..peer instanceof LightweightPeer..
    // it is NOT a real peer support. The peer field just exists to
    // check if a Component already passed its addNotify()/removeNotify().
    // This most probably will be removed once the "isLightweightComponent()"
    // method gets official (1.2?)
        return windowPeer;
    }

    static void destroyNative( Component c ) {
        WMEvent e = null;
    // do we need some kind of a context switch ?
        if ( ( flags & IS_DISPATCH_EXCLUSIVE ) != 0 ) {
            if ( ( flags & NATIVE_DISPATCHER_LOOP ) != 0 ) {
                if ( Thread.currentThread() != collectorThread ) {
	  // this is beyond our capabilities (there is no Java message entry we can call
	  // in the native collector), we have to revert to some native mechanism
                    e = WMEvent.getEvent( c, WMEvent.WM_DESTROY );
                    evtSendWMEvent( e );
                }
            } else {
                if ( Thread.currentThread() != eventThread ) {
	  // we can force the context switch by ourselves, no need to go native
                    e = WMEvent.getEvent( c, WMEvent.WM_DESTROY );
                    eventQueue.postEvent( e );
                }
            }
      // Ok, we have a request out there, wait for it to be served
            if ( e != null ) {
	// we should check for nativeData because the event might
	// already be processed (depending on the thread system)
                while ( c.getNativeData() != 0 ) {
                    synchronized( e ) {
                        try {
                            e.wait();
                        } catch ( InterruptedException x ) {
                        }
                    }
                }
                return;
            }
        } else {
      // no need to switch threads, go native right away
            c.destroyNative();
        }
    }

    public native static synchronized AWTEvent evtGetNextEvent();

    public native static synchronized Component[] evtInit();

    public native static synchronized AWTEvent evtPeekEvent();

    public native static synchronized AWTEvent evtPeekEventId( int eventId );

    public native static synchronized int evtRegisterSource( int wndData );

    public native static synchronized void evtSendWMEvent( WMEvent e );

    public native static synchronized int evtUnregisterSource( int wndData );

    public native static synchronized void evtWakeup();

    public native static synchronized int fntBytesWidth
        ( int fmData, byte[] data, int off, int len );

    public native static synchronized int fntCharWidth( int fmData, char c );

    public native static synchronized int fntCharsWidth
        ( int fmData, char[] data, int off, int len );

    public native static synchronized void fntFreeFont( int fontData );

    public native static synchronized void fntFreeFontMetrics( int fmData );

    public native static synchronized int fntGetAscent( int fmData );

    public native static synchronized int fntGetDescent( int fmData );

    public native static synchronized int fntGetFixedWidth( int fmData );

    public native static synchronized int fntGetHeight( int fmData );

    public native static synchronized int fntGetLeading( int fmData );

    public native static synchronized int fntGetMaxAdvance( int fmData );

    public native static synchronized int fntGetMaxAscent( int fmData );

    public native static synchronized int fntGetMaxDescent( int fmData );

    public native static synchronized int[] fntGetWidths( int fmData );

    public native static synchronized int fntInitFont
        ( String fntSpec, int style, int size );

    public native static synchronized int fntInitFontMetrics( int fontData );

    public native static synchronized boolean fntIsWideFont( int fmData );

    public native static synchronized int fntStringWidth
        ( int fmData, String s );

    public ColorModel getColorModel() {
        if ( colorModel == null ) {
            colorModel = clrGetColorModel();
        }
        return colorModel;
    }

    public static Toolkit getDefaultToolkit() {
        return singleton;
    }

    public String[] getFontList() {
        String[] list = { "Default", "Monospaced",
            "SansSerif", "Serif",
            "Dialog", "DialogInput",
            "ZapfDingbats" };
        return list;
    }

    public FontMetrics getFontMetrics( Font font ) {
        return FontMetrics.getFontMetrics( font );
    }

    public Image getImage( String filename ) {
        File f = new File( filename );
    // Hmm, that's a inconsistency with getImage(URL). The doc isn't very
    // helpful, here (class doc says nothing, book tells us it should return
    // null in case it's not there - but that is not known a priori for URLs).
    // JDK never returns a null object, so we don't do, either
        return ( f.exists() ) ? new Image( f ) : Image.getUnknownImage();
    }

    public Image getImage( URL url ) {
    // how can we return 'null' before we start to produce this thing (which
    // isn't supposed to happen here)? We are NOT going to open the URLConnection
    // twice just to check if it is there
        return new Image( url );
    }

    public int getMenuShortcutKeyMask() {
        return InputEvent.CTRL_MASK;
    }

    public PrintJob getPrintJob
        ( Frame frame, String jobtitle, Properties props ) {
            return new PSPrintJob( frame, jobtitle, props );
    }

    public static String getProperty( String key, String defaultValue ) {
        return null;
    }

    public Dimension getScreenSize() {
        return screenSize;
    }

    public Clipboard getSystemClipboard() {
        if ( clipboard == null ) {
            clipboard = new NativeClipboard();
        }
        return clipboard;
    }

    public EventQueue getSystemEventQueue() {
        return eventQueue;
    }

    public native static synchronized void imgComplete
        ( int imgData, int status );

    public native static synchronized int
        imgCreateFromData( byte[] buf, int offset, int len );

    public native static synchronized int imgCreateFromFile( String gifPath );

    public native static synchronized int imgCreateImage( int w, int h );

    public native static synchronized int
        imgCreateScaledImage( int imgData, int w, int h );

    public native static synchronized int
        imgCreateScreenImage( int w, int h );

    public native static synchronized void imgFreeImage( int imgData );

    public native static synchronized int imgGetHeight( int imgData );

    public native static synchronized int imgGetLatency( int imgData );

    public native static synchronized int imgGetNextFrame( int imgData );

    public native static synchronized int imgGetWidth( int imgData );

    public native static synchronized boolean imgIsMultiFrame( int imgData );

    public native static synchronized void
        imgProduceImage( ImageNativeProducer prod, int imgData );

    public native static synchronized int
        imgSetFrame( int imgData, int frame );

    public native static synchronized void
        imgSetIdxPels( int imgData, int x, int y, int w, int h, int[] rgbs,
        byte[] pels, int trans, int off, int scans );

    public native static synchronized void
        imgSetRGBPels( int imgData, int x, int y, int w, int h, int[] rgbs,
        int off, int scans );

    static synchronized void initToolkit() {
    // this is called when the native layer has been initialized, and it is safe
    // to query native settings / rely on native functionality
        screenSize = new Dimension( tlkGetScreenWidth(),
            tlkGetScreenHeight() );
        CAT.debug( "initToolkit: screen size " + tlkGetScreenWidth() + " x " +
            tlkGetScreenHeight() );
        resolution = tlkGetResolution();
        CAT.debug( "resolution " + tlkGetResolution() );
    // we do this here to keep the getDefaultToolkit() method as simple
    // as possible (since it might be called frequently). This is a
    // deviation from the normal Singleton (which initializes the singleton
    // instance upon request)
        singleton = new Toolkit();
        eventQueue = new EventQueue();

        /**
         *       if ( Defaults.ConsoleClass != null ){
         * // since we have to defer the ConsoleWindow until the native
         * Toolkit is propperly // initialized, it seems to be a good idea to
         * defuse any output to the standard streams
         * // (which might cause SEGFAULTS on some systems (e.g. DOS)
         * System.setOut( new PrintStream( NullOutputStream.singleton));
         * System.setErr( System.out); }
         */
        if ( ( flags & NATIVE_DISPATCHER_LOOP ) != 0 ) {
      // let the world know we are ready to take over, native-wise
            Toolkit.class.notify();
        }
    }

    protected void loadSystemColors( int[] sysColors ) {
        clrSetSystemColors( sysColors );
    }

    public boolean prepareImage
        ( Image image, int width, int height, ImageObserver observer ) {
            return ( Image.loadImage( image, width, height, observer ) );
    }

    static void redirectStreams() {
        try {
            LogClient lv = ( LogClient )
                Class.forName( Defaults.ConsoleClass ).newInstance();
            LogStream ls = new LogStream( 30, lv );
            lv.enable();
            System.setOut( new PrintStream( ls ) );
            System.setErr( System.out );
            System.out.println( "Java console enabled" );
        } catch ( Exception x ) {
            System.err.println( "unable to redirect out, err" );
            x.printStackTrace();
        }
    }

    static ThreadGroup getTopLevelThreadGroup() {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        while ( tg.getParent() != null ) {
            tg = tg.getParent();
        }
        return tg;
    }

    static synchronized void startDispatch() {
        if ( eventThread == null ) {
      // CIM: We want the EventDispatchThread of the AWT be part of the
      // top-level ThreadGroup
            eventThread = new EventDispatchThread( getTopLevelThreadGroup(),
                eventQueue );
      //eventThread = new EventDispatchThread( eventQueue);
            eventThread.start();
      // we defer the Console creation / output redirection up to this point, since we otherwise
      // might get all sort of trouble because of a incompletely initialized native layer / Toolkit
            if ( Defaults.ConsoleClass != null ) {
                redirectStreams();
            }
        }
        if ( ( ( flags & NEEDS_FLUSH ) != 0 ) && ( flushThread == null ) ) {
            flushThread = new FlushThread( Defaults.GraFlushRate );
            flushThread.start();
        }
    }

    static synchronized void stopDispatch() {
        if ( eventThread != null ) {
            eventThread.stopDispatching();
            eventThread = null;
        }
        if ( flushThread != null ) {
            flushThread.stopFlushing();
            flushThread = null;
        }
    }

    public void sync() {
        tlkSync();
    }

    static void terminate() {
        if ( clipboard != null ) {
            clipboard.dispose();
        }
        stopDispatch();
        tlkTerminate();
    }

    public native static synchronized void tlkBeep();

    public native static synchronized void tlkDisplayBanner( String banner );

    public native static synchronized void tlkFlush();

    public native static synchronized int tlkGetResolution();

    public native static synchronized int tlkGetScreenHeight();

    public native static synchronized int tlkGetScreenWidth();

    public native static boolean tlkInit( String displayName );

    public native static synchronized int tlkProperties();

    public native static synchronized void tlkSync();

    public native static synchronized void tlkTerminate();

    public native static synchronized String tlkVersion();

    public native static synchronized int wndCreateDialog
        ( int ownerData, String title, int x, int y, int width, int height,
        int cursorType, int bgColor, int bgAlpha, boolean isResizable );

    public native static synchronized int wndCreateFrame
        ( String title, int x, int y, int width, int height, int cursorType,
        int bgColor, int bgAlpha, boolean isResizable );

    public native static synchronized int wndCreateWindow
        ( int ownerData, int x, int y, int width, int height, int cursorType,
        int bgColor, int bgAlpha );

    public native static synchronized void wndDestroyWindow( int wndData );

    public native static synchronized void wndRepaint
        ( int wndData, int x, int y, int width, int height );

    public native static synchronized void wndRequestFocus( int wndData );

    public native static synchronized void wndSetBounds
        ( int wndData, int x, int y, int width, int height,
        boolean isResizable );

    public native static synchronized void wndSetCursor
        ( int wndData, int cursorType );

    public native static synchronized int wndSetDialogInsets
        ( int top, int left, int bottom, int right );

    public native static synchronized int wndSetFrameInsets
        ( int top, int left, int bottom, int right );

    public native static synchronized void wndSetIcon
        ( int wndData, int iconData );

    public native static synchronized void wndSetResizable
        ( int wndData, boolean isResizable, int x, int y,
        int width, int height );

    public native static synchronized void wndSetTitle
        ( int wndData, String title );

    public native static synchronized void wndSetVisible
        ( int wndData, boolean showIt );

    public native static synchronized void wndToBack( int wndData );

    public native static synchronized void wndToFront( int wndData );
}

