package org.dvb.ui;

/**
 * This exception is thrown for some invalid operations on 
 * instances of DVBBufferedImage. The precise conditions are 
 * defined in the places where this exception is thrown.
 */

public class DVBRasterFormatException extends java.lang.Exception
{
    /**
     * Constructs a <code>DVBRasterFormatException</code> with
     * <code>null</code> as its error detail message.
     */
    public DVBRasterFormatException() {
        super();
    }

    /**
     * Constructs an instance of <code>DVBRasterFormatException</code> with 
     * the specified detail message.
     */
    public DVBRasterFormatException(java.lang.String s){
	super( s );
    }
}

    
