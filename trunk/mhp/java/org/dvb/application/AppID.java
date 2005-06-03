
package org.dvb.application;

/*The AppID is a representation of the unique identi  er for applications. Its string form 
is the Hex representation of the 48 bit number. */

public class AppID {

private int oid;
private int aid;

/*
Create a new AppID based on the given integers.There is no range checking on these numbers. Parameters: oid -the 
globally unique organization number. aid -the unique count within the organization. */
public AppID(int _oid, int _aid) {
   oid=_oid;
   aid=_aid;
}

/*
method returns the integer value of the application count supplied in the constructor Returns: the integer value of the 
application count supplied in the constructor */
public int getAID() {
   return aid;
}

/*
This method returns the integer value of the organization number supplied in the constructor. Returns: the integer value 
of the organization number supplied in the constructor. */
public int getOID() {
   return oid;
}

/*
This method returns a string containing the Hex representation of the 48 bit number. Overrides: 
java.lang.Object.toString()in class java.lang.Object Returns: a string containing the Hex representation of the 48 bit 
number. */
public java.lang.String toString() {
   long fourtyEightBit = ((long)oid << 48) | aid; //long is 64bit in Java
   return Long.toHexString(fourtyEightBit);
}

public boolean equals(Object obj) {
   return (obj instanceof AppID) && ((AppID)obj).oid==oid && ((AppID)obj).aid==aid;
}

public int hashCode() {
   //add upper 16bits and lower 16bits of oid, push into upper 16bits
   //of return value, put 16-bit aid into lower 16 bits
   return ( ((oid >> 16) + (oid & 0xFFFF)) << 16 ) & (aid & 0xFFFF);
}


}
