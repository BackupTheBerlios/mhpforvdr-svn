package org.openmhp.util;



/**
* Class for output.
*
* @author tejopa
* @date 17.3.2004
* @status fully implemented
* @module internal
*/
public class Out {

	public static final int TRACE = 0;
	public static final int DEBUG = 1;
	public static final int FIXME = 2;
	public static final int TODO  = 3;
	public static final int ERROR = 4;
	
	
	private static boolean traceOn = true;
	private static boolean debugOn = true;
	private static boolean fixmeOn = true;
	private static boolean todoOn = true;
	private static boolean errorOn = true;

	public Out() { }

	public static void setTrace(boolean b) {	
		traceOn = b;
	}
	
	public static void setDebug(boolean b) {	
		debugOn = b;
	}
	
	public static void setFixme(boolean b) {	
		fixmeOn = b;
	}
	
	public static void setTodo(boolean b)  {	
		todoOn = b;
	}
	
	public static void setError(boolean b) { 	
		errorOn = b;
	}

	public static void printMe(int type) {
		printMe(type,"");	
	}

	/**
	* Method prints the class name and the method name from where this
	* method was called. Argument describes the type of the message.
	*/
	public static void printMe(int type, String message) {
		boolean print = true;
		try {
			throwException();
		}
		catch (Exception e) {
			StackTraceElement[] elem = e.getStackTrace();

			int i = 0;
			
			StackTraceElement caller = elem[i];
			
			while (elem[i].getClassName().indexOf("util.Out")!=-1) {
				i++;
				caller = elem[i];
			}
			
			String prefix = "";
			switch (type) {
				case TRACE:	prefix = "<MHP RE><TRACE> : ";
						 	print = traceOn;
						 	break;
				case DEBUG:	prefix = "<MHP RE><DEBUG> : ";
						 	print = debugOn;
						 	break;
				case FIXME:	prefix = "<MHP RE><FIXME>** : ";
						 	print = fixmeOn;
						 	break;
				case TODO:	prefix = "<MHP RE><TODO>*** : ";
						 	print = todoOn;
						 	break;
				case ERROR:	prefix = "<MHP RE><ERROR>**** : ";
						 	print = errorOn;
						 	break;
				default: 	prefix = "<MHP RE> : ";
						 	break;
			}
			if (print) {
				System.out.println(prefix+caller.getClassName()+" : "+caller.getMethodName()+" : "+message);
			}		
		}
	}

	private static void throwException() throws Exception {	throw new Exception("debug exception");	}

	public static void println(Object o, String mess) {
		if (debugOn) {
			System.out.println("<MHP RE> : "+o.getClass().toString()+" : "+mess+" "+Thread.currentThread().toString());
		}
	}

	public static void error(Object o, String mess) {
		if (errorOn) {
			System.out.println("<MHP RE> : ERROR START --------------------------------------------------------------------");
			System.out.println("<MHP RE> : "+o.getClass().toString()+" : "+mess+" "+Thread.currentThread().toString());
			System.out.println("<MHP RE> : ERROR END ----------------------------------------------------------------------");	}
	}

}