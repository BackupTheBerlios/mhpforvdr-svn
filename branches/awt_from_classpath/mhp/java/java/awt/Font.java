package java.awt;

import java.awt.peer.FontPeer;
import java.io.Serializable;

/**
 * XXX: implement serial form!
 */
public class Font implements Serializable {
    public long nativeData; //pointer to an IDirectFBFont
    protected String name;
    protected int style;
    protected int size;
    final public static int PLAIN              = 0;
    final public static int BOLD               = 1;
    final public static int ITALIC             = 2;
    final private static long serialVersionUID = -4206021311591459213L;

    public Font( String fntName, int fntStyle, int fntSize ) {
        Object v;
        String spec;
        name = fntName;
        style = fntStyle;
        size = fntSize;

        //name = name.intern();
	// name.toLowerCase(); // not working!! Dirk (dirkh@prz.tu-berlin.de)

        /* if (name == "Default") { spec = Defaults.FsDefault; }
        else if (name == "Monospaced") { spec = Defaults.FsMonospaced; }
        else if (name == "SansSerif") { spec = Defaults.FsSansSerif; }
        else if (name == "Serif") { spec = Defaults.FsSerif; }
        else if (name == "Dialog") { spec = Defaults.FsDialog; }
        else if (name == "DialogInput") { spec = Defaults.FsDialogInput; }
        else if (name == "ZapfDingbats") { spec = Defaults.FsZapfDingbats; }
        else if (name == "Helvetica") { spec = Defaults.FsSansSerif; }
        else if (name == "TimesRoman") { spec = Defaults.FsSerif; }
        else if (name == "Courier") { spec = Defaults.FsMonospaced; } else {
        spec = name; } */

 // So, fonts required in an MHP box:
 // Arial, Courier, Times. Optionally TimesRoman, Wingdings.
 
        /* In order to get compability to Sun, we must use
        String.equalsIgnoreCase().
        toLowerCase do'nt work. (dirkh@prz.tu-berlin.de) */
        
        //This is the version with Microsoft fonts
        /*if ( name.equalsIgnoreCase( "default" ) ) {
            spec = "arial";
        } else if ( name.equalsIgnoreCase( "monospaced" ) ) {
            spec = "courier";
        } else if ( name.equalsIgnoreCase( "sansserif" ) ) {
            spec = "arial";
        } else if ( name.equalsIgnoreCase( "serif" ) ) {
            spec = "times";
        } else if ( name.equalsIgnoreCase( "dialog" ) ) {
            spec = "arial";
        } else if ( name.equalsIgnoreCase( "dialoginput" ) ) {
            spec = "arial";
        } else if ( name.equalsIgnoreCase( "zapfdingbats" ) ) {
            spec = "wingdings";*/
        //These three shall not change
        /*} else if ( name.equalsIgnoreCase( "helvetica" ) ) {
            spec = "arial";
        } else if ( name.equalsIgnoreCase( "timesroman" ) ) {
            spec = Defaults.FsSerif;
        } else if ( name.equalsIgnoreCase( "courier" ) ) {
            spec = Defaults.FsMonospaced;*/
        /*     
                     //this font, required by MHP, can of course not be provided in a free
                     //implementation because it requires a license fee.
        } else if ( name.equalsIgnoreCase( "tiresias" ) ) { 
            spec = "arial";
        } else {
            spec = name;
        }*/
        
        //This is the version with Bitstream Vera free fonts
        if ( name.equalsIgnoreCase( "default" ) ) {
            spec = "vera";
        } else if ( name.equalsIgnoreCase( "monospaced" ) ) {
            spec = "veramo";
        } else if ( name.equalsIgnoreCase( "sansserif" ) ) {
            spec = "vera";
        } else if ( name.equalsIgnoreCase( "serif" ) ) {
            spec = "verase";
        } else if ( name.equalsIgnoreCase( "dialog" ) ) {
            spec = "vera";
        } else if ( name.equalsIgnoreCase( "dialoginput" ) ) {
            spec = "vera";
        } else if ( name.equalsIgnoreCase( "zapfdingbats" ) ) {
            spec = "verase"; //don't know
        } else if ( name.equalsIgnoreCase( "helvetica" ) ) {
            spec = "vera";
        } else if ( name.equalsIgnoreCase( "timesroman" ) ) {
            spec = "verase";
        } else if ( name.equalsIgnoreCase( "courier" ) ) {
            spec = "veramo";            
                     //this font, required by MHP, can of course not be provided in a free
                     //implementation because it requires a license fee.
        } else if ( name.equalsIgnoreCase( "tiresias" ) ) { 
            spec = "vera";
        } else {
            spec = name;
        }        
        
        nativeData = init( spec.toLowerCase().replace(' ', '_'), style, size );
    }
    
    private native long init(String spec, int style, int size);

    /**
     * @rework
     */
    public static Font decode( String fntSpec ) {
        Font fnt;
        String fontname;
        int fontstyle = PLAIN;
        int fontsize = 12;
        int i, n, l = 0;
        char c;
        fontname = fntSpec;
        if ( ( i = fntSpec.indexOf( '-' ) ) >= 0 ) { // format : <name>[-<style>[-<size>]]
            fontname = fntSpec.substring( 0, i );
            i++;
            if ( fntSpec.regionMatches( true, i, "plain-", 0, 6 ) ) {
                l = 6;
            } else if ( fntSpec.regionMatches( true, i, "bold-", 0, 5 ) ) {
                fontstyle = BOLD;
                l = 5;
            } else if ( fntSpec.regionMatches( true, i, "italic-", 0, 7 ) ) {
                fontstyle = ITALIC;
                l = 7;
            } else if ( fntSpec.regionMatches( true, i,
                "bolditalic-", 0, 11 ) ) {
                    fontstyle = BOLD | ITALIC;
                    l = 11;
            }
            if ( l > 0 ) {
                i += l;
                fontsize = 0;
                for ( n = fntSpec.length(); i < n; i++ ) {
                    c = fntSpec.charAt( i );
                    if ( c >= '0' && c <= '9' ) {
                        fontsize = fontsize * 10 + ( c - '0' );
                    } else {
                        break;
                    }
                }
            }
        }
        fnt = new Font( fontname, fontstyle, fontsize );
        return fnt;
    }

    String encode() {
        String s;
        if ( style == PLAIN ) {
            s = "-plain-";
        } else if ( style == ITALIC ) {
            s = "-italic-";
        } else if ( style == BOLD ) {
            s = "-bold-";
        } else {
            s = "-bolditalic-";
        }
        return ( name + s + size );
    }

    public boolean equals( Object o ) {
        if ( o instanceof Font ) {
            Font fnt = ( Font )o;
            if ( !fnt.name.equals( name ) ) {
                return false;
            }
            if ( fnt.style != style ) {
                return false;
            }
            if ( fnt.size != size ) {
                return false;
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        //return name.hashCode();
        return encode().hashCode();
    }

    protected void finalize() throws Throwable {
        if ( nativeData != 0 ) {
            removeRef( nativeData );
            nativeData = 0;
        }
        super.finalize();
    }

    private native void removeRef(long nativeData);
    
    public String getFamily() {
        return System.getProperty( ( "awt.font." + name.toLowerCase() ), name );
    }

    public static Font getFont( String key ) {
        return getFont( key, null );
    }

    public static Font getFont( String key, Font defFont ) {
        String fSpec;
        if ( ( fSpec = System.getProperty( key ) ) != null ) {
            return decode( fSpec );
        }
        return defFont;
    }

    public String getName() {
        return name;
    }

    public FontPeer getPeer() {
        return null;
    }

    public int getSize() {
        return size;
    }

    public int getStyle() {
        return style;
    }

    public boolean isBold() {
        return ( ( style & BOLD ) != 0 );
    }

    public boolean isItalic() {
        return ( ( style & ITALIC ) != 0 );
    }

    public boolean isPlain() {
        return ( style == 0 );
    }

    public String toString() {
        String s = "";
        if ( style == 0 ) {
            s = "plain";
        } else {
            if ( ( style & BOLD ) != 0 ) {
                s = "bold";
            }
            if ( ( style & ITALIC ) != 0 ) {
                s += "italic";
            }
        }
        return getClass().getName() + " [" + name + ',' + s + ',' + size + ']';
    }
}

