
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
import java.net.URL;

import org.dvb.application.MHPApplication;

import java.io.FilePermission;
import java.net.SocketPermission;
import java.util.PropertyPermission;
import java.lang.RuntimePermission;
import java.security.SecurityPermission;
import javax.tv.service.ReadPermission;
import javax.tv.service.selection.SelectPermission;
import javax.tv.service.selection.ServiceContextPermission;
import org.dvb.application.AppsControlPermission;
import org.dvb.media.DripFeedPermission;
import org.dvb.net.ca.CAPermission;
import org.dvb.user.UserPreferencePermission;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import java.io.FileOutputStream;
import java.io.InputStream;
//import java.lang.reflect.Method;

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
   super(URLs);
   System.out.println("Creating DVB class loader");
   this.app=app;
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

// Usually, this class would only need to override findClass.
// However, we must replace a few classes in java.io with stub implementations
// that check for relative file paths, resolve them to the base directory
// of the MHP application, and otherwise just use the classpath implementation.
// This interception in class loading is done here.

// Problem: This only works if the Xlet code itself contains the reference to one of these classes.
// If it loads another class, passes a file path, and this class (loaded by bootstrap loader),
// creates a File object, the unchanged File class will be used.
// So the constructor of the first class which passes the filename needs to be added to this list here.
// Second possible problem: I think if a class is loaded twice, its static fields will be loaded twice
// as well. This might cause any sort of problem, so at least heavyweight classes which contain
// important static fields cannot be modified.
// Is there any other, more elegant solution to this relative-pathname-dilemma?

// In any case, every class which is part of this implementation and takes a pathname shall itself
// convert this path with vdr.mhp.io.PathConverter.


public Class loadClass(String name) throws ClassNotFoundException
{
  return loadClass(name, false);
}


//The code of the next method and the following inner classes are taken from / inspired by:

/***
 * ASM examples: examples showing how ASM can be used
 * Copyright (c) 2000-2005 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

static InstructionWriter[] javaIOFunctions =
  {
   new InstructionWriter("java/io/File", "<init>", "(Ljava/lang/String;)V", InstructionWriter.CONVERTER_STRING),
   new InstructionWriter("java/io/File", "<init>", "(Ljava/net/URI;)V", InstructionWriter.CONVERTER_URI),
   new InstructionWriter("java/io/File", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", InstructionWriter.CONVERTER_STRING_STRING),
   new InstructionWriter("java/io/FileInputStream", "<init>", "(Ljava/lang/String;)V", InstructionWriter.CONVERTER_STRING),
   new InstructionWriter("java/io/FileOutputStream", "<init>", "(Ljava/lang/String;)V", InstructionWriter.CONVERTER_STRING),
   new InstructionWriter("java/io/FileOutputStream", "<init>", "(Ljava/lang/String;Z)V", InstructionWriter.CONVERTER_STRING),
   new InstructionWriter("java/io/FileReader", "<init>", "(Ljava/lang/String;)V", InstructionWriter.CONVERTER_STRING),
   new InstructionWriter("java/io/FileWriter", "<init>", "(Ljava/lang/String;)V", InstructionWriter.CONVERTER_STRING),
   new InstructionWriter("java/io/FileWriter", "<init>", "(Ljava/lang/String;Z)V", InstructionWriter.CONVERTER_STRING),
   new InstructionWriter("java/io/RandomAccessFile", "<init>", "(Ljava/lang/String;)V", InstructionWriter.CONVERTER_STRING),
  };

boolean checkReplaceClass(String name) {
   for (int i=0; i<javaIOFunctions.length; i++) {
      if (javaIOFunctions[i].checkClass(name))
         return true;
   }
   return false;
}

InstructionWriter checkReplaceMethod(String clazz, String name, String signature) {
   for (int i=0; i<javaIOFunctions.length; i++) {
      if (javaIOFunctions[i].checkClass(clazz) && javaIOFunctions[i].checkMethod(name, signature))
         return javaIOFunctions[i];
   }
   return null;
}


protected Class loadClass(String name, boolean resolve)
  throws ClassNotFoundException
{
   synchronized(this) {
   
   String bytecodeName = name.replace('.','/');
   //check if class is in the list
   if (checkReplaceClass(bytecodeName)) {
      //Have we already loaded this class?
      Class c = findLoadedClass(name);
      if (c == null) {
         System.out.println("DVBClassLoader: Dynamically modifying class "+name);
         //new Exception().printStackTrace();
         /*
         for (int i=0; i<javaIOFunctions.length; i++) {
            if (javaIOReplace[i].checkClass(name)) {
               return super.loadClass("vdr.mhp"+name.substring(4), resolve);
         */
      
         // gets an input stream to read the bytecode of the class
         String resource = bytecodeName + ".class";
         //System.out.println("Resource is "+resource);
         InputStream is = getResourceAsStream(resource);
         byte[] b;
      
         // adapts the class on the fly
         try {
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(false);
            ClassVisitor cv = new JavaIOClassAdapter(cw);
            cr.accept(cv, false);
            b = cw.toByteArray();
            
            FileOutputStream str = new FileOutputStream(name);
            str.write(b);
            str.close();
            //System.out.println("Having class in byte array with length "+b.length);
         } catch (Exception e) {
            e.printStackTrace();
            throw new ClassNotFoundException(name, e);
         }
         
         //System.out.println("Now defining class");
         // the adapted class should have the same ProtectionDomain as the unchanged class
         // Remember to call super.loadClass(String, bool) - super.loadClass(String) will probably create an endless loop
         return defineClass(name, b, 0, b.length, super.loadClass(name, resolve).getProtectionDomain());
      } else
         return c;
   }
   }
   return super.loadClass(name, resolve);
}


class JavaIOClassAdapter extends ClassAdapter implements Opcodes {

   String owner;

   public JavaIOClassAdapter(ClassVisitor cv) {
      super(cv);
   }

  public void visit (
    final int version,
    final int access,
    final String name,
    final String signature,
    final String superName,
    final String[] interfaces)
  {
    owner = name;
    super.visit(version, access, name, signature, superName, interfaces);
    //System.out.println("ClassAdapter.visit "+name+signature);
  }

  public MethodVisitor visitMethod (
    final int access,
    final String name,
    final String desc,
    final String signature,
    final String[] exceptions)
  {
    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    if( mv==null)
       return null;
    InstructionWriter writer = checkReplaceMethod(owner, name, desc);
    //System.out.println("ClassAdapter.visitMethod "+name+" "+desc+" "+signature+", "+((access & ACC_PUBLIC) != 0)+" "+writer+", "+mv);
    if ( writer != null && (access & ACC_PUBLIC) != 0 )
      return new JavaIOCodeAdapter(mv, writer);
    return mv;
  }
}

class JavaIOCodeAdapter extends MethodAdapter implements Opcodes {

   private InstructionWriter writer;

   public JavaIOCodeAdapter (MethodVisitor mv, InstructionWriter writer) {
     super(mv);
     this.writer = writer;
     //System.out.println("New JavaIOCodeAdapter for InstructionWriter for "+writer.clazz+"."+writer.method+" "+writer.signature);
   }

   public void visitCode() {
      //add code to beginning of method
      writer.write(mv);
   }
   
  /*
  public void visitFieldInsn (
    final int opcode,
    final String owner,
    final String name,
    final String desc)
  {
    if (owner.equals(this.owner)) {
      if (opcode == GETFIELD) {
        // replaces GETFIELD f by INVOKESPECIAL _getf
        String gDesc = "()" + desc;
        visitMethodInsn(INVOKESPECIAL, owner, "_get" + name, gDesc);
        return;
      } else if (opcode == PUTFIELD) {
        // replaces PUTFIELD f by INVOKESPECIAL _setf
        String sDesc = "(" + desc + ")V";
        visitMethodInsn(INVOKESPECIAL, owner, "_set" + name, sDesc);
        return;
      }
    }
    super.visitFieldInsn(opcode, owner, name, desc);
  }
  */
}

static class InstructionWriter implements Opcodes {

   final static int CONVERTER_STRING = 0;
   final static int CONVERTER_URI = 1;
   final static int CONVERTER_STRING_STRING = 2;

   private static String[][] pathConverterFunctions =
   {
      { "vdr/mhp/io/PathConverter", "convert", "(Ljava/lang/String;)Ljava/lang/String;" },
      { "vdr/mhp/io/PathConverter", "convert", "(Ljava/net/URI;)Ljava/net/URI;" },
      { "vdr/mhp/io/PathConverter", "convert", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;" },
   };
   
   
   String clazz;
   String method;
   String signature;
   int converterFunction;

   InstructionWriter(String clazz, String method, String signature, int converterFunction) {
      this.clazz=clazz;
      this.method=method;
      this.signature=signature;
      this.converterFunction=converterFunction;
   }
   
   boolean checkClass(String className) {
      return clazz.equals(className);
   }
   
   boolean checkMethod(String name, String signature) {
      return method.equals(name) && this.signature.equals(signature);
   }
   
   void write(MethodVisitor mv) {
      /*
      This function is called immediately at the beginning of the function
      and adds the very first instructions. These instructions are very simple:
      Local variable 1 is the first argument passed to the function.
      This variable (in the second case the second argument as well)
      is pushed on the stack and the corresponding function from PathConverter is called,
      which returns the first argument. This return value is then written into the local variable.
      */
      
      String[] func=pathConverterFunctions[converterFunction];
      switch (converterFunction) {
      case CONVERTER_STRING:
      case CONVERTER_URI:
         mv.visitVarInsn(ALOAD, 1);
         mv.visitMethodInsn(INVOKESTATIC, func[0], func[1], func[2]);
         mv.visitVarInsn(ASTORE, 1);
         break;
      case CONVERTER_STRING_STRING:
         mv.visitVarInsn(ALOAD, 1);
         mv.visitVarInsn(ALOAD, 2);
         mv.visitMethodInsn(INVOKESTATIC, func[0], func[1], func[2]);
         mv.visitVarInsn(ASTORE, 1);
         break;
      }
   }
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
the class. Parameters: 
URLs -the URLs to search for classes and resources. parent -the parent class loader for 
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
