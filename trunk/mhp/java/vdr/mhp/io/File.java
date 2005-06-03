/* File.java -- Class representing a file on disk
   Copyright (C) 1998, 1999, 2000, 2001, 2003, 2004, 2005
   Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.
 
GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package vdr.mhp.io;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.Serializable;
import java.io.IOException;
import java.io.FileFilter;
import java.io.FilenameFilter;

/* Written using "Java Class Libraries", 2nd edition, ISBN 0-201-31002-3
 * "The Java Language Specification", ISBN 0-201-63451-1
 * Status:  Complete to version 1.3.
 */


public class File implements Serializable, Comparable
{
  //the actual object
  private java.io.File file;
  
  public static final String separator = java.io.File.separator;

  
  public static final char separatorChar = java.io.File.separatorChar;
  
  
  public static final String pathSeparator = java.io.File.pathSeparator;
  
  
  public static final char pathSeparatorChar = java.io.File.pathSeparatorChar;

  
  public boolean canRead()
  {
   return file.canRead();
  }

  
  public boolean canWrite()
  {
   return file.canWrite();
  }

  
  public boolean createNewFile() throws IOException
  {
   return file.createNewFile();
  }
  
  public boolean delete()
  {
   return file.delete();
  }

  
  public boolean equals(Object obj)
  {
   return file.equals(obj);
  }

  
  public boolean exists()
  {
   return file.exists();
  }

  
  public File(String name)
  {
   file=new java.io.File(name);
  }
 
   
  public File(String dirPath, String name)
  {
   file=new java.io.File(name);
  }

  
  public File(java.io.File directory, String name)
  {
   file=new java.io.File(name);
  }

  
  public File(URI uri)
  {
   file=new java.io.File(uri);
  }

  
  public String getAbsolutePath()
  {
   return file.getAbsolutePath();
  }

  
  public java.io.File getAbsoluteFile()
  {
   return file.getAbsoluteFile();
  }

  
  public String getCanonicalPath() throws IOException
  {
   return file.getCanonicalPath();
  }

  
  public java.io.File getCanonicalFile() throws IOException
  {
   return file.getCanonicalFile();
  }

  
  public String getName()
  {
   return file.getName();
  }

  
  public String getParent()
  {
   return file.getParent();
  }

  
  public java.io.File getParentFile()
  {
   return file.getParentFile();
  }

  
  public String getPath()
  {
   return file.getPath();
  }

  
  public int hashCode()
  {
   return file.hashCode();
  }

  
  public boolean isAbsolute()
  {
   return file.isAbsolute();
  }

  
  public boolean isDirectory()
  {
    return file.isDirectory();
 }

  
  public boolean isFile()
  {
   return file.isFile();
  }

  
  public boolean isHidden()
  {
   return file.isHidden();
  }

  
  public long lastModified()
  {
   return file.lastModified();
  }

  
  public long length()
  {
   return file.length();
  }

  
  public String[] list(FilenameFilter filter)
  {
   return file.list(filter);
  }

  
  public String[] list()
  {
   return file.list();
  }

  
  public java.io.File[] listFiles()
  {
   return file.listFiles();
  }
  
  
  public java.io.File[] listFiles(FilenameFilter filter)
  {
   return file.listFiles();
  }

  
  public java.io.File[] listFiles(FileFilter filter)
  {
   return file.listFiles();
  }

  
  public String toString()
  {
   return file.toString();
  }

  
  public URI toURI()
  {
   return file.toURI();
  }

  
  public URL toURL() throws MalformedURLException
  {
   return file.toURL();
  }


  
  public boolean mkdir()
  {
   return file.mkdir();
  }

  
  public boolean mkdirs()
  {
   return file.mkdirs();
  }

  
  public static java.io.File createTempFile(String prefix, String suffix,
				    java.io.File directory)
    throws IOException
  {
   return java.io.File.createTempFile(prefix, suffix, directory);
  }

  
  public boolean setReadOnly()
  {
   return file.setReadOnly();
  }

  
  public static java.io.File[] listRoots()
  {
   return java.io.File.listRoots();
  }

  
  public static java.io.File createTempFile(String prefix, String suffix)
    throws IOException
  {
   return java.io.File.createTempFile(prefix, suffix);  
  }

  
  public int compareTo(java.io.File other)
  {
   return file.compareTo(other);
  }

  
  public int compareTo(Object obj)
  {
   return file.compareTo(obj);
  }

  
  public synchronized boolean renameTo(java.io.File dest)
  {
   return file.renameTo(dest);
  }

  
  public boolean setLastModified(long time) 
  {
   return file.setLastModified(time);
  }

  public void deleteOnExit()
  {
   file.deleteOnExit();
  }

  
} // class File

