/*

 This file is part of XleTView 
 Copyright (C) 2003 Martin Svedén
 
 This is free software, and you are 
 welcome to redistribute it under 
 certain conditions;

 See LICENSE document for details.

*/


package org.dvb.net.rc;

/**
 * 
 * 
 * @author Martin Sveden
 * @statuscode 2
 */
public class RCPermission extends java.security.BasicPermission {

public RCPermission(String name){
   super(name);
}

public RCPermission(String name, String actions){
   super(name,actions);
}

public boolean implies(java.security.Permission p) {
   if (!(p instanceof RCPermission))
      return false;
   RCPermission other = (RCPermission)p;
   String name = getName();
   if (name.endsWith("*")) {
      String otherName = other.getName();
      if (name.length()-1 > otherName.length())
         return false;
      return name.startsWith(otherName.substring(0, name.length()-1));
   } else 
      return name.equals(other.getName());
}

}
