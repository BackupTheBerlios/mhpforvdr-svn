
package org.dvb.lang;

import java.io.File;
import java.io.RandomAccessFile;
import java.security.ProtectionDomain;
import java.security.AccessController;
import java.security.AccessControlException;
import java.security.Principal;
import java.security.Permission;
import java.security.Permissions;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.net.URLClassLoader;
import java.io.FilePermission;
import java.net.SocketPermission;
import java.util.PropertyPermission;
import java.lang.RuntimePermission;
import java.security.SecurityPermission;
import java.net.URL;
import org.dvb.application.MHPApplication;
import javax.tv.service.ReadPermission;
import javax.tv.service.selection.SelectPermission;
import javax.tv.service.selection.ServiceContextPermission;
import org.dvb.application.AppsControlPermission;
import org.dvb.media.DripFeedPermission;
import org.dvb.net.ca.CAPermission;
import org.dvb.user.UserPreferencePermission;

/*This class loader is used to load classes and resources from a search path of URLs referring to locations where Java 
class  les may be stored. The classes that are loaded are by default only allowed to load code through the parent 
classloader,or from the URLs speci  ed when the DVBClassLoader was created. */


//TODO 
//"The valid types of locator for use with DVBClassLoader shall
// include the following:  org.davic.net.dvb.DVBLocator" (page 246)
//So, they may point to a Object carousel -- implement if needed
// BUT - org.davic.net.dvb.DVBLocator does not inherit java.net.URL! What's that?

//TODO: The spec requires to call checkCreateClassLoader() 
//in the constructor if a security manager exists.

public class DVBClassLoader extends URLClassLoader {

//java.net.URL[] urls;
MHPApplication app;
//ProtectionDomain protectionDomain;

/*
//this permission is not used as a permission,
//but abused to retrieve the calling MHP application from stack
static class ApplicationTracePermission extends Permission {
   MHPApplication app;
   
   ApplicationTracePermission(MHPApplication app) {
      super(null);
      this.app=app;
   }
   
   ApplicationTracePermission() {
      super(null);
   }
   
   MHPApplication getApplication() {
      return app;
   }
   
   public boolean implies(Permission p) {
      System.out.println("ApplicationTracePermission.implies"+p);
      if (p instanceof ApplicationTracePermission) {
         //here we do not check for a permission, but rather modify the calling permission
         ApplicationTracePermission o=(ApplicationTracePermission)p;
         if (o.app == null) {
            o.app = app;
            return true;
         } else //strange
            return o.app == app;
      }
      return false;
   }
   
   public String getActions() {
      return null;
   }
   
   public boolean equals(Object obj) {
      if (obj instanceof ApplicationTracePermission)
         return ((ApplicationTracePermission)obj).app == app;
      return false;
   }
   
   public int hashCode() {
      return app==null ? 0 : app.hashCode();
   }
}
*/

//not API
//use this constructor or the next only from inside the implementation
public DVBClassLoader(java.net.URL[] URLs, org.dvb.application.MHPApplication app) {
   this(URLs, null, app);
}

//not API
public DVBClassLoader(java.net.URL[] URLs, java.lang.ClassLoader parent, org.dvb.application.MHPApplication app) {
   super(URLs, parent);
   System.out.println("Creating DVB class loader");
   this.app=app;
}

//only to be used by MHP application code, not by the implementation!
/*
Constructs a new DVBClassLoader for the given URLs.The URLs will be searched in the order speci  ed for classes and 
resources. If there is a security manager,this method  rst calls the security manager's checkCreateClassLoader method to 
ensure creation of a class loader is allowed. Parameters: URLs -the URLs from which to load classes and resources 
Throws: SecurityException -if a security manager exists and its checkCreateClassLoader method doesn't allow creation of 
a class loader. See Also: SecurityManager */
public DVBClassLoader(java.net.URL[] URLs)  throws SecurityException {
   this(URLs, (ClassLoader)null);
}

//only to be used by MHP application code, not by the implementation!
/*
Constructs a new DVBClassLoader for the given URLs.The URLs will be searched in the order speci  ed for classes and 
resources. If there is a security manager,this method  rst calls the security manager's checkCreateClassLoader method to 
ensure creation of a class loader is allowed. Parameters: URLs -the URLs from which to load classes and resources parent 
-the parent classloader for delegation */
public DVBClassLoader(java.net.URL[] URLs, java.lang.ClassLoader parent)  throws SecurityException {
   super(URLs, parent);
   ClassLoader loader=getClass().getClassLoader();
   if (!(loader instanceof org.dvb.lang.DVBClassLoader) || (((org.dvb.lang.DVBClassLoader)loader).getApplication() == null))
      throw new SecurityException("from DVBClassLoader constructor");
   this.app=((org.dvb.lang.DVBClassLoader)loader).getApplication();
}

/*
void checkAllowed() throws SecurityException {
   SecurityManager current = System.getSecurityManager();
   if (current != null && current instanceof vdr.mhp.MHPSecurityManager) {
      ((vdr.mhp.MHPSecurityManager)current).checkNoSubclassOfDVBClassLoader(getClass());
   }
}
*/

public org.dvb.application.MHPApplication getApplication() {
   return app;
}


/*
Finds and loads the class with the speci  ed name from the URL search path.Any URLs are searched until the class is 
found. Parameters: name -the name of the class. Returns: the resulting class. Throws: ClassNotFoundException -if the 
named class could not be found. */
public java.lang.Class findClass(java.lang.String name) throws ClassNotFoundException{
   System.out.println("DvbClassLoader: findClass "+name);
   //TODO?? support dvb:// urls
   return super.findClass(name);
   /*for (int i=0;i<urls.length;i++) {
      File dir;
      if (urls[i].getProtocol().equals("file"))
         dir = new File(urls[i].getFile());
      else //see above for comment on dvb://
         //throw new ClassNotFoundException("DVBClassLoader - unknown URL protocol".concat(name));
         continue;
      if (name.indexOf('/') != -1)
         throw new ClassNotFoundException("DVBClassLoader - Invalid class name "+name);
      int lastDot=name.lastIndexOf('.');
      File clazz;
      if (lastDot != -1)
         dir=new File(dir, name.substring(0, lastDot+1).replace('.', '/'));
      //System.out.println("Searching in dir "+dir.toString());
      clazz=new File(dir, name.substring(lastDot+1) + ".class");
      System.out.println("DVBClassLoader: constructed path "+clazz.getPath()+". Is it a file, can it be read? "+clazz.isFile()+", "+clazz.canRead());
      if (clazz.isFile() && clazz.canRead()) {
         try {
            RandomAccessFile f=new RandomAccessFile(clazz, "r");
            byte[] b=new byte[(int)f.length()];
            f.readFully(b);
            //System.out.println("Calling defineClass");
            return defineClass(name, b, 0, b.length);
         } catch (java.io.IOException ex) { ex.printStackTrace();  }
      }
   }
   throw new ClassNotFoundException("DVBClassLoader - Did not find class: " + name);
   */
}

protected PermissionCollection getPermissions(CodeSource source)
{
   System.out.println("DVBClassLoader.getPermissions()");
   PermissionCollection collection=new Permissions();
   collection.add(new FilePermission("*", "read"));
   collection.add(new SocketPermission("*", "accept,connect,listen"));
   collection.add(new PropertyPermission("*", "read"));
   collection.add(new ServiceContextPermission("*", "own"));
   collection.add(new SelectPermission("*", "own"));
   collection.add(new UserPreferencePermission("*"));
   //no RuntimePermission
   //TODO: appropriate SecurityPermissions
   //TODO: more refined FilePermission
   //currently unused by implementation: CAPermission, DripFeedPermission, AppsControlPermission

    // Now add any extra permissions depending on the URL location.
    URL url = source.getLocation();
    String protocol = url.getProtocol();
    if (protocol.equals("file"))
      {
        String file = url.getFile();

        // If the file end in / it must be an directory.
        if (file.endsWith("/") || file.endsWith(File.separator))
          {
            // Grant permission to read everything in that directory and
            // all subdirectories.
            collection.add(new FilePermission(file + "-", "read"));
          }
        else
          {
            // It is a 'normal' file.
            // Grant permission to access that file.
            collection.add(new FilePermission(file, "read"));
          }
      }
    else
      {
        // Grant permission to connect to and accept connections from host
        String host = url.getHost();
        if (host != null)
          collection.add(new SocketPermission(host, "connect,accept"));
      }

   //collection.add(new ApplicationTracePermission(app));
   return collection;
}

/*
//overriden from SecureClassLoader
//one ProtectionDomain per MHP application, not per CodeSource
protected final Class defineClass(String name, byte[] b, int off, int len, CodeSource cs)
{
   if (cs != null) {
      if (protectionDomain == null ) {
         protectionDomain = new ProtectionDomain(cs, getPermissions(cs), this, null);
      }
      return super.defineClass(name, b, off, len, protectionDomain);
   } else
      return super.defineClass(name, b, off, len);
}
*/

//Usually, this class would only need to override findClass.
//However, we must replace a few classes in java.io with stub implementations
//that check for relative file paths, resolve them to the base directory
//of the MHP application, and otherwise just use the classpath implementation.
//This interception in class loading is done here.
public Class loadClass(String name) throws ClassNotFoundException
{
  return loadClass(name, false);
}

static String[] javaIOReplace = { "java.io.File",
                                  "java.io.FileInputStream",
                                  "java.io.FileOutputStream",
                                  "java.io.FileReader",
                                  "java.io.FileWriter",
                                  "java.io.RandomAccessFile"
                                };

protected Class loadClass(String name, boolean resolve)
  throws ClassNotFoundException
{
   if (name.startsWith("java.io")) {
      for (int i=0; i<javaIOReplace.length; i++) {
         if (name.equals(javaIOReplace[i])) {
            //return classes from vdr.mhp.io for those from java.io
            return super.loadClass("vdr.mhp"+name.substring(4), resolve);
         }
      }
   }
   return super.loadClass(name, resolve);
}


/*
Creates a new instance of DVBClassLoader for the speci  ed URLs.If a security manager is installed,the loadClass method 
of the DVBClassLoader returned by this method will invoke the SecurityManager.checkPackageAccess method before loading 
the class. Parameters: URLs -the URLs to search for classes and resources. Returns: the resulting class 
loader */
public static DVBClassLoader newInstance(java.net.URL[] URLs) {
   return new DVBClassLoader(URLs);
}

/*
Creates a new instance of DVBClassLoader for the speci  ed URLs.If a security manager is installed,the loadClass method 
of the DVBClassLoader returned by this method will invoke the SecurityManager.checkPackageAccess method before loading 
the class. Parameters: URLs -the URLs to search for classes and resources. parent -the parent class loader for 
delegation. Returns: the resulting class loader */
public static DVBClassLoader newInstance(java.net.URL[] URLs, java.lang.ClassLoader parent) {
   return new DVBClassLoader(URLs, parent);
}

/*
static public MHPApplication getApplicationFromStack() {
   //I do not know how fragile this is
   ApplicationTracePermission p = new ApplicationTracePermission();
   try {
      AccessController.checkPermission(p);
   } catch (AccessControlException e) {
      System.out.println("getApplicationFromStack: got exception, returning null");
      return null;
   }
   System.out.println("getApplicationFromStack: got Permission");
   return p.getApplication();
}
*/

}
