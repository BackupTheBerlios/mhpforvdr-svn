
package org.dvb.si;

/*This class represents a descriptor within a sub-table. A descriptor consist of three  
elds: a tag, a contentLength and the content. The tag uniquely identi es the descriptor 
type. The content length indicates the number of bytes in the content. The content 
consists of an array of bytes of length content length. The data represented by the 
content is descriptor type dependent. */

public class Descriptor {

byte[] content;
short tag;

public Descriptor(short tag, byte[] content) {
   this.content=content;
   this.tag=tag;
}

//copies array
public Descriptor(short tag, byte[] content, int length) {
   this.content=new byte[length];
   this.tag=tag;
   System.arraycopy(content, 0, this.content, 0, length);
}

/*
Get a particular byte within the descriptor content Parameters: index - index to the descriptor content. Value 0 
corresponds to the  rst byte after the length  eld. Returns: The required byte Throws: IndexOutOfBoundsException - if 
index < 0 or index >= ContentLength */
public byte getByteAt(int index) throws IndexOutOfBoundsException {
   return content[index];
}

/*
Get a copy of the content of this descriptor (everything after the length  eld). Returns: a copy of the content of the 
descriptor */
public byte[] getContent() {
   byte[] copy=new byte[content.length];
   System.arraycopy(content, 0, copy, 0, content.length);
   return copy;
}

/*
ETSI This method returns the length of the descriptor content as coded in the length  eld of this descriptor. 495 ETSI 
TS 102 812 V1.1.1 (2001-11) Returns: The length of the descriptor content. */
public short getContentLength() {
   return (short)content.length;
}

/*
Get the descriptor tag Returns: The descriptor tag (the most common values are de ned in the DescriptorTag interface) 
See Also: DescriptorTag */
public short getTag() {
   return tag;
}


}
