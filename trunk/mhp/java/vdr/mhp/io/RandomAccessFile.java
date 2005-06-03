/* RandomAccessFile.java -- Class supporting random file I/O
   Copyright (C) 1998, 1999, 2001, 2002, 2003, 2004, 2005  Free Software Foundation, Inc.

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


package java.io;

import java.nio.channels.FileChannel;

/* Written using "Java Class Libraries", 2nd edition, ISBN 0-201-31002-3
 * "The Java Language Specification", ISBN 0-201-63451-1
 * Status: Believe complete and correct to 1.1.
 */


public class RandomAccessFile implements DataOutput, DataInput
{
  private java.io.RandomAccessFile file;
  
  public RandomAccessFile (java.io.File fil, String mode)
    throws FileNotFoundException
  {
    file = new java.io.RandomAccessFile(fil, mode);
  }

  
  public RandomAccessFile (String fileName, String mode)
    throws FileNotFoundException
  {
    file = new java.io.RandomAccessFile(fileName, mode);
  }

  
  public void close () throws IOException
  {
    file.close();
  }

  
  public final FileDescriptor getFD () throws IOException
  {
    return file.getFD();
  }

  
  public long getFilePointer () throws IOException
  {
    return file.getFilePointer();
  }

  
  public void setLength (long newLen) throws IOException
  {
    file.setLength(newLen);
  }

  
  public long length () throws IOException
  {
    return file.length();
  }

  
  public int read () throws IOException
  {
    return file.read();
  }

  
  public int read (byte[] buffer) throws IOException
  {
    return file.read (buffer);
  }

  
  public int read (byte[] buffer, int offset, int len) throws IOException
  {
    return file.read (buffer, offset, len);
  }

  
  public final boolean readBoolean () throws IOException
  {
    return file.readBoolean ();
  }

  
  public final byte readByte () throws IOException
  {
    return file.readByte ();
  }

  
  public final char readChar () throws IOException
  {
    return file.readChar();
  }

  
  public final double readDouble () throws IOException
  {
    return file.readDouble ();
  }

  
  public final float readFloat () throws IOException
  {
    return file.readFloat();
  }

  
  public final void readFully (byte[] buffer) throws IOException
  {
    file.readFully(buffer);
  }

  
  public final void readFully (byte[] buffer, int offset, int count)
    throws IOException
  {
    file.readFully (buffer, offset, count);
  }

  
  public final int readInt () throws IOException
  {
    return file.readInt();
  }

  
  public final String readLine () throws IOException
  {
    return file.readLine ();
  }

  
  public final long readLong () throws IOException
  {
    return file.readLong();
  }

  
  public final short readShort () throws IOException
  {
    return file.readShort();
  }

  
  public final int readUnsignedByte () throws IOException
  {
    return file.readUnsignedByte();
  }

  
  public final int readUnsignedShort () throws IOException
  {
    return file.readUnsignedShort();
  }

  
  public final String readUTF () throws IOException
  {
    return file.readUTF();
  }

  
  public void seek (long pos) throws IOException
  {
    file.seek(pos);
  }

  
  public int skipBytes (int numBytes) throws IOException
  {
    return file.skipBytes(numBytes);
  }

  
  public void write (int oneByte) throws IOException
  {
    file.write(oneByte);
  }

  
  public void write (byte[] buffer) throws IOException
  {
    file.write(buffer);
  }

  
  public void write (byte[] buffer, int offset, int len) throws IOException
  {
    file.write (buffer, offset, len);
  }

  
  public final void writeBoolean (boolean val) throws IOException
  {
    file.writeBoolean(val);
  }

  
  public final void writeByte (int val) throws IOException
  {
    file.writeByte(val);
  }

  
  public final void writeShort (int val) throws IOException
  {
    file.writeShort(val);
  }

  
  public final void writeChar (int val) throws IOException
  {
    file.writeChar(val);
  }

  
  public final void writeInt (int val) throws IOException
  {
    file.writeInt(val);
  }

  
  public final void writeLong (long val) throws IOException
  {
    file.writeLong(val);
  }

  
  public final void writeFloat (float val) throws IOException
  {
    file.writeFloat(val);
  }

  
  public final void writeDouble (double val) throws IOException
  {
    file.writeDouble(val);
  }

  
  public final void writeBytes (String val) throws IOException
  {
    file.writeBytes(val);
  }
  
  
  public final void writeChars (String val) throws IOException
  {
    file.writeChars(val);
  }
  
  
  public final void writeUTF (String val) throws IOException
  {
    file.writeUTF(val);
  }
  
  
  public final FileChannel getChannel ()
  {
    return file.getChannel();
  }
}
