/*

 This file is part of XleTView 
 Copyright (C) 2003 Martin Sved�n
 
 This is free software, and you are 
 welcome to redistribute it under 
 certain conditions;

 See LICENSE document for details.

*/


package org.dvb.net;

import java.net.DatagramSocket;


public class DatagramSocketBufferControl {

DatagramSocketBufferControl(){}

public static void setReceiveBufferSize(DatagramSocket d, int size) throws java.net.SocketException {
   d.setReceiveBufferSize(size);
}

public static int getReceiveBufferSize(DatagramSocket d) throws java.net.SocketException {
   return d.getReceiveBufferSize();

}

}

