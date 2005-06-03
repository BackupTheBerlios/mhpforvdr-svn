/*
 * NIST/DASE API Reference Implementation
 * $File: PushSourceStream2.java $
 * Last changed on $Date: 2001/02/21 19:05:03 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package javax.tv.media.protocol;
import java.io.IOException;
import javax.media.protocol.PushSourceStream;

/**
 *
 * <p>
 * This interface identifies a SourceStream that pushes asynchronous data.
 * See API for details (C).
 *
 * <p>Revision information:<br>
 * $Revision: 1.4 $
 *
 */    


public interface PushSourceStream2 extends PushSourceStream {

  public int readStream(byte[] buffer, int offset, int length)
    throws IOException, DataLostException, ArrayIndexOutOfBoundsException;

}

