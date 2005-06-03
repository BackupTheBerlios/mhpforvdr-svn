package org.dvb.dsmcc;

import java.io.FileNotFoundException;
import java.io.InterruptedIOException;
import java.net.URL;
import javax.tv.locator.InvalidLocatorException;
import org.davic.net.*;

import org.openmhp.util.Out;

//Taken from OpenMHP, license in LGPL
/**
* @author tejopa
* @date 7.3.2004
* @status partially implemented
* @module internal
* @HOME
*/
public class ServiceDomain {

   private boolean attached = false;
   private DSMCCObject mountPoint = new DSMCCObject("\\");

    public ServiceDomain()    {      Out.println(this,"creation");    }

    public void attach(Locator locator, int i) throws ServiceXFRException, InterruptedIOException, MPEGDeliveryException    {
      Out.println(this,"attach 1 success");
       attached = true;
    }

    public void attach(Locator locator) throws DSMCCException, InterruptedIOException, MPEGDeliveryException    {
      Out.println(this,"attach 2 success : "+locator.toString());
      if (locator.toString()!=null) {
         mountPoint = new DSMCCObject(locator.toString());
         if (!mountPoint.exists()) {
            attached = false;
            throw new DSMCCException("An error has occurred during the attachment. For example, the locator does not point to a component carrying a DSI of an Object Carousel or to a service containing a single carousel");
         }
      }
      else {
         Out.error(this,"locator was null, assume attached");
      }
      attached = true;
    }

    public void attach(byte abyte0[]) throws DSMCCException, InterruptedIOException, InvalidAddressException, MPEGDeliveryException    {
      Out.println(this,"attach 3 success");
      attached = true;
    }

    public void detach() throws NotLoadedException    {
      Out.println(this,"detach success");
      attached = false;
    }

    public byte[] getNSAPAddress() throws NotLoadedException    {
      Out.println(this,"getNSAPAddress returns NULL");
        return null;
    }

    public static URL getURL(Locator locator) throws NotLoadedException, InvalidLocatorException, FileNotFoundException {
      Out.println(new Object(),"org.dvb.dsmcc.ServiceDomain getURL return NULL");
        URL url = null;
        try {
           url = new URL(locator.toString());
           return url;
        }
        catch (Exception e) {
           throw new InvalidLocatorException(locator);
        }
    }

    public DSMCCObject getMountPoint()    {
      Out.println(this,"getMountPoint() : "+mountPoint.toString());
      return mountPoint;
    }

    public boolean isNetworkConnectionAvailable()    {
      Out.println(this,"isNetworkConnectionAvailable returns FALSE");
        return attached;
    }

    public boolean isAttached()    {
      Out.println(this,"isAttached returns "+attached);
        return attached;
    }

    public Locator getLocator()    {
      Out.println(this,"getLocator returns NULL");
        return null;
    }
}