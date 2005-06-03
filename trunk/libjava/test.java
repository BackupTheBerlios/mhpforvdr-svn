import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.security.ProtectionDomain;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.Principal;
import java.security.Permission;
import java.security.Permissions;
import java.security.PermissionCollection;
import java.security.CodeSource;

public class test {

MySecurityManager securityManager;

class MySecurityManager extends SecurityManager {
   public Class[] getClassContext() {
      return super.getClassContext();
   }
}

public static void main(String []args) {
   test t=new test();
   t.main();
}

void main() {
   System.out.println("Hallo");
   
   try {
   /*
      securityManager=new MySecurityManager();
      
      Class[] cls=securityManager.getClassContext();
      HashSet domains = new HashSet();
      HashSet seenDomains = new HashSet();
      for (int i=0;i<cls.length;i++) {
         Class clazz=cls[i];
        ProtectionDomain domain = clazz.getProtectionDomain();

        if (domain == null)
          continue;
        if (seenDomains.contains(domain))
          continue;
        seenDomains.add(domain);

        // Create a static snapshot of this domain, which may change over time
        // if the current policy changes.
        domains.add(new ProtectionDomain(domain.getCodeSource(),
                                         domain.getPermissions()));
         
         System.out.println("Permissions of "+cls[i]);
         print(cls[i].getProtectionDomain().getPermissions().elements());
         System.out.println("All permission implied? "+cls[i].getProtectionDomain().implies(new java.security.AllPermission()));
         
      }
      
      ProtectionDomain[] result = (ProtectionDomain[]) domains.toArray(new ProtectionDomain[domains.size()]);
   

      try {
         (new AccessControlContext(result)).checkPermission(new java.security.AllPermission());
         //java.security.AccessController.checkPermission(new java.security.AllPermission());
      } catch (java.security.AccessControlException _) {
         System.out.println("AllPermission failed");
      }
   */
      try {
         System.out.println("Checking AccessController for AllPermission");
         java.security.AccessController.checkPermission(new java.security.AllPermission());
      } catch (java.security.AccessControlException _) {
         System.out.println("AccessController: AllPermission failed");
      }
      
      if (System.getSecurityManager()==null) {
         System.out.println("Default security manager is null");
      } else {
      try {
         System.getSecurityManager().checkPermission(new java.security.AllPermission());
      } catch (SecurityException _) {
         System.out.println("SecurityManager: AllPermission failed");
      }
      }
   
      try {
         System.out.println("Checking MySecurityManager for AllPermission");
         new MySecurityManager().checkPermission(new java.security.AllPermission());
      } catch (SecurityException _) {
         System.out.println("MySecurityManager: AllPermission failed");
      }
      //no longer set this as a SecurityManager! Now the newer framework is used.
      //System.setSecurityManager(securityManager);
   } catch (Exception e) {
      e.printStackTrace();
   }
}

void print(Enumeration e) {
   while (e.hasMoreElements()) {
      System.out.println(" "+e.nextElement()); //(java.security.AllPermission * )
   }
}



}