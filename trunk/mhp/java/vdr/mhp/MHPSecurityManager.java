package vdr.mhp;

import java.security.Permission;

//This class is no longer set as a SecurityManager
//Only to provide a portable implementation for getApplicationFromStack
//TODO: remove this, find a better way
//but: See DVBClassLoader for failed attempts to achieve this 
//with the AccessController framework

public class MHPSecurityManager extends SecurityManager {

ThreadGroup systemThreadGroup;

MHPSecurityManager(ThreadGroup systemGroup) {
   systemThreadGroup=systemGroup;
}

public void checkPermission(Permission perm) {
   //TODO!!!!!!!!!!!!!!!!!
   //This currently effectively DISABLES ALL SECURITY CHECKS!
   //Remove this immediately as soon as SableVM has implemented VMAccessController!
}


/*
public void checkAccept(String host, int port) {
   //throw new SecurityException();
}

public void checkAccess(Thread g) {

}

public void checkAccess(ThreadGroup g) {
}

public void checkAwtEventQueueAccess() {

}

public void checkConnect(String host, int port) {

}

public void checkConnect(String host, int port, Object context) {

}


public void checkCreateClassLoader() {
   //DVBClassLoader is allowed, of course, but no other class loaders.
   if (classDepth("kaffe.lang.PrimordialClassLoader") == -1 &&
        classDepth("org.dvb.lang.DVBClassLoader") == -1)
      throw new SecurityException();
}


public void checkDelete(String file) {

}

public void checkExec(String cmd) {

}

public void checkExit(int status) {

}

public void checkLink(String lib) {

}

public void checkListen(int port) {

}

public void checkMemberAccess ( Class clazz, int which ) {

}

public void checkMulticast(java.net.InetAddress maddr) {

}

public void checkMulticast(java.net.InetAddress maddr, byte ttl) {

}

public void checkPackageAccess(String pkg) {

}

public void checkPackageDefinition(String pkg) {
}


public void checkPermission(Permission perm) {
   if (perm instanceof RuntimePermission) {
      if (perm.getName().equals("setSecurityManager")
       || perm.getName().equals("createSecurityManager")
       || ( perm.getName().equals("createClassLoader") && classDepth("org.dvb.lang.DVBClassLoader") == -1 )
         )
         throw new SecurityException();
   }
}


public void checkPermission(Permission perm, Object context) {

}

public void checkPrintJobAccess() {

}

void checkPropertyAccess(String key, String def) {

}

public void checkRead(java.io.FileDescriptor fd) {

}

public void checkRead(String file) {

}

public void checkRead(String file, Object context) {

}

public void checkSecurityAccess(String action) {

}

public void checkSetFactory() {

}

public void checkSystemClipboardAccess() {

}

public boolean checkTopLevelWindow(Object window) {
   return true;
}

public void checkWrite(java.io.FileDescriptor fd) {

}

public void checkWrite(String file) {

}
*/

//Only works when an MHP application triggered the call
//somewhere down the stack.
public org.dvb.application.MHPApplication getApplicationFromStack() {
   Class[] cls=getClassContext();
   System.out.println("Class context:");
   for (int i=0; i< cls.length; i++) {
      ClassLoader loader=cls[i].getClassLoader();
      System.out.println(" "+cls[i]+", "+ (loader==null ? "bootstrap loader" : (loader+" "+loader.getClass())) );
   }
   for (int i=0; i< cls.length; i++) {
      ClassLoader loader=cls[i].getClassLoader();
      if (loader instanceof org.dvb.lang.DVBClassLoader)
         return ((org.dvb.lang.DVBClassLoader)loader).getApplication();
   }
   return null;
   
   //currentClassLoader cannot be used because if the code has AllPermission,
   //it will return null in newer Java versions.
   //This behavior is OK for security, but here we need the class loader for a different purpose.
   
   /*
   ClassLoader loader=currentClassLoader();
   System.out.println("loader is "+loader);
   if (loader == null)
      return null;
   if (loader instanceof org.dvb.lang.DVBClassLoader)
      return ((org.dvb.lang.DVBClassLoader)loader).getApplication();
   else
      throw new SecurityException("Unknown class loader detected");
   */
}

/*
//internal API
//org.dvb.lang.DVBClassLoader is the only classloader MHP applications are allowed
//to create. However, they may not subclass it because then they could override
//its methods. So its constructor calls this check.
public void checkNoSubclassOfDVBClassLoader(Class possibleDvbClassLoaderChildClass) {
   Class[] classes = getClassContext();
   Class compare=org.dvb.lang.DVBClassLoader.class;
   for (int i=0;i<classes.length;i++) {
      //check if class has DVBClassLoader as parent and is not DVBClassLoader
      if (compare.isAssignableFrom(possibleDvbClassLoaderChildClass)
            && !possibleDvbClassLoaderChildClass.getName().equals("org.dvb.lang.DVBClassLoader") )
         throw new SecurityException("from checkNoSubclassOfDVBClassLoader");
   }
}
*/

//not API
/*public ClassLoader getCallersClassLoader() {
   Class[] classes = getClassContext();
   ret*urn classes[1].
}*/


}
