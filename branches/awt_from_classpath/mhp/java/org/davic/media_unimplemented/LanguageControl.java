
/*
 * NIST/DASE API Reference Implementation
 * $File: api/javalib/src/org/davic/media/LanguageControl.java $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.davic.media;

/**
 *
 * <p>
 * This interface provides means to control the language settings.
 * See the official API for information (C).
 *
 * <p>Revision information:<br>
 * $Revision: 1.5 $
 * <br>
 * $Date: 2000/03/11 22:43:59 UTC $
 *
 */


import javax.media.Control;
import org.davic.media.LanguageNotAvailableException;
import org.davic.media.NotAuthorizedException;

public interface LanguageControl extends javax.media.Control {
  
  public java.lang.String getCurrentLanguage();
  public java.lang.String[] listAvailableLanguages();
  public java.lang.String selectDefaultLanguage()
    throws NotAuthorizedException;
  public void selectLanguage(java.lang.String lang)
    throws LanguageNotAvailableException, NotAuthorizedException;
  
}

