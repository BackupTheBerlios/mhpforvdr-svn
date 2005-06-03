
package vdr.mhp;

public class Syslog {
    public native static synchronized void esyslog(String s);
    public native static synchronized void dsyslog(String s);    
}