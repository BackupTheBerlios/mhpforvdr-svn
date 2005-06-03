/*
 * NIST/DASE API Reference Implementation
 * $File: DataLostException.java $
 * Last changed on $Date: 2001/02/21 19:06:04 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package javax.tv.media.protocol;

/**
 *
 * <p>
 * This class represents an exception generated when the data for a 
 * particular operation has been lost.
 * <p> See the offical (copyrighted) JavaTV API for details.
 *
 * <p>Revision information:<br>
 * $Revision: 1.4 $
 *
 */    

import java.io.IOException;

public class DataLostException extends IOException {

  public DataLostException() {
    super();
  }


  public DataLostException(String reason) {
    super(reason);
  }

}




