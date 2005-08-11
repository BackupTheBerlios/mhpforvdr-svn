/*
 * NIST/DASE API Reference Implementation
 * MediaSelectControl.java
 * Last changed on $Date: 2001/02/26 18:02:13 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package javax.tv.media;
import javax.media.Control;
import javax.tv.locator.InvalidLocatorException;
import javax.tv.service.selection.InvalidServiceComponentException;
import javax.tv.service.selection.InsufficientResourcesException;
import javax.tv.locator.Locator;

/**
 *
 * <p>
 * This interface represents a way to select a service on a player.
 * For details, see the official JavaTV API.
 *
 * <p>Revision information:<br>
 * $Revision: 1.7 $
 *
 */    

public interface MediaSelectControl extends Control {

  public void select(Locator component)
    throws InvalidLocatorException, InvalidServiceComponentException,
           InsufficientResourcesException, SecurityException;

  public void select(Locator[] components)
    throws InvalidLocatorException, InvalidServiceComponentException,
           InsufficientResourcesException, SecurityException;

  public void add(Locator component)
    throws InvalidLocatorException, InvalidServiceComponentException,
           InsufficientResourcesException, SecurityException;

  public void remove(Locator component)
    throws InvalidLocatorException, InvalidServiceComponentException,
           SecurityException;

  public void replace(Locator fromComponent, Locator toComponent)
    throws InvalidLocatorException, InvalidServiceComponentException,
           InsufficientResourcesException, SecurityException;

  public void addMediaSelectListener(MediaSelectListener listener);

  public void removeMediaSelectListener(MediaSelectListener listener);

  public Locator[] getCurrentSelection();

}
