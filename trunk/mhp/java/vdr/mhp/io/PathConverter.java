package vdr.mhp.io;

import vdr.mhp.ApplicationManager;
import org.dvb.application.MHPApplication;
import java.io.File;
import java.net.URI;


public class PathConverter {

protected PathConverter() {
}

public static String convert(String path) {
   //System.out.println("PathConvert.convert: "+path+", "+path.startsWith(File.separator)+" "+getAppBasePrefix()+path);
   if (path.startsWith(File.separator))
      return path; //absolute file name
   else //relative filename: turn into absolute file name pointing to base dir
      return getAppBasePrefix()+path;
}

/*
This method is for the type of File constructor where a "parent" path
and a "child" path is given. Typically parent is the directory and child 
the filename, but parent may be null as well, in which case this
constructor is equivalent with the single String form.
The value returned will replace the parent string, while the child string will be unchanged.
*/
public static String convert(String parent, String child) {
   //TODO: There are some special cases in the spec, don't know if important
/*
"If parent is null then the new File instance is created as if by invoking the single-argument
 File constructor on the given child pathname string. 
 Otherwise the parent pathname string is taken to denote a directory, and the child pathname string
 is taken to denote either a directory or a file. If the child pathname string is absolute then it
 is converted into a relative pathname in a system-dependent way. If parent is the empty string
 then the new File instance is created by converting child into an abstract pathname and resolving 
 the result against a system-dependent default directory. Otherwise each pathname string is converted 
 into an abstract pathname and the child abstract pathname is resolved against the parent."
*/ 
   if (child==null)
      return parent; //invalid, keep out
   if (parent==null) {
      if (child.startsWith(File.separator))
         return parent; //null
      else
         return getAppBasePrefix();
   } else if (parent.equals("")) {
      return getAppBasePrefix();
   } else {
      return convert(parent);
   }
}

public static URI convert(URI path) {
   //TODO!
   System.out.println("TODO: Implement vdr.mhp.io.PathConverter.convert(URI). Stack trace follows");
   new Exception().printStackTrace();
   return path;
}

static String getAppBasePrefix() {
   MHPApplication app=ApplicationManager.getManager().getApplicationFromStack();
   if (app==null)
      return "";
   else
      return app.getCarouselBasePath()+File.separator;
}


}