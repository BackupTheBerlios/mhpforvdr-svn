/***************************************************************************
 *       Copyright (c) 2003-2005 by Marcel Wiesweg                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef JNI_INTERFACE_H
#define JNI_INTERFACE_H

#include <stdint.h>

#ifdef SABLEVM_JNI
   #include "jni-sablevm.h"
#else
   #include <jni.h>
#endif

/* This namespace contains some classes to abstract the JNI so
   that you can use it without having to know about its nasty details.
   
   Please note that JNI still is very sensitive. Always check for errors
   at the functions here returning bool, and double check your functions
   and their signatures.
*/

namespace JNI {

enum Types { Void = 'V', Boolean = 'Z', Byte = 'B', Char = 'C',
             Short = 'S', Int = 'I', Long = 'J', Float = 'F', Double = 'D',
             Object = 'L', Array = '[' }; //last two require an additional specifier
union ReturnType {
   jobject TypeObject;
   bool TypeBoolean;
   int8_t TypeByte;
   uint16_t TypeChar;
   int16_t TypeShort;
   int32_t TypeInt;
   int64_t TypeLong;
   float TypeFloat;
   double TypeDouble;
};

enum ExceptionHandling { ClearExceptions, DoNotClearExceptions };

class JNIEnvProvider {
public:
   //to set JNIEnv when JNI API is used from a JNI function, called from Java code,
   //with the thread possibly created by Java (thread need not be attached to VM).
   static void SetJavaEnv(JNIEnv *env) { return s_self->setJavaEnv(env); }
   static JNIEnv *GetEnv() { return s_self->env(); }
protected:
   JNIEnvProvider() { s_self = this; }
   virtual ~JNIEnvProvider() { s_self=0; }
   virtual JNIEnv *env() = 0;
   virtual void setJavaEnv(JNIEnv *env) = 0;
private:
   static JNIEnvProvider *s_self;
};

class BaseObject {
public:
   BaseObject();
      // Returns a function signature, accepts enum Types.
      // numArgs is the number of arguments of the Java function.
      // 'Class' requires the class name, 'Array' the type as a second specifier.
      // This second specifier shall be a string given as the following argument
      // and _not_ be counted by numArgs.
      // If returnType is either 'Class' or 'Array', then the very last argument
      // is the necessary second specifier. This argument shall _not_ be counted
      // by numArgs.
      // "buffer" must be sufficiently large.
      // Arrays of class objects are not elegantly supported, the second specifier must be "Lorg/my/Example;
      //
      // Two Examples: public int doIt(int arg1, bool arg2);
      //               getSignature(buf, JNI::Int, 2, JNI::Int, JNI::Boolean);
      //               myInstanceMethod.SetMethod("org/my/Example", "doIt", buf);
      // 
      //               public static String doThat(int arg1, java.util.Date arg2, int[] arg3)
      //               getSignature(buf, JNI::Object, 3, JNI::Int, JNI::Object, 
      //                            "java/util/Data", JNI::Array, JNI::Int, "java/lang/String");
      //               myStaticMethod.SetMethod("org/my/Example", "doThat", buf);
      //               myStaticMethod.CallMethod(myReturnType, JNI::Object);
   static void getSignature(char *buffer, Types returnType, int numArgs, ...);
   static void getSignature(char *buffer, Types returnType, int numArgs, va_list args);
      //Get signature for a constructor. This is equvivalent to calling getSignature with returnType JNI::Void
   static void getConstructorSignature(char *buffer, int numArgs, ...);
   static bool checkException();
   
   // Per default, exceptions (stacktrace written to output) are cleared after each call
   // and only the return value indicates an error: ClearExceptions.
   // If the handling is set to DoNotClearExceptions, ExceptionClear() is not called and an exception may be pending.
   void SetExceptionHandling(ExceptionHandling eh) { exceptionHandling = eh; }
private:
   ExceptionHandling exceptionHandling;
};

class ClassRef : public BaseObject {
public:
   ClassRef();
   operator jclass() const { return classRef; }
   //fully-qualified class name, e.g. "java/lang/String"
   bool SetClass(const char* classname);
protected:
   jclass classRef;   
};

class GlobalObjectRef : public BaseObject {
public:
   GlobalObjectRef();
   ~GlobalObjectRef();
   operator jobject() const { return objectRef; }
   jclass GetClass();
   //a valid local reference
   bool SetObject(jobject localRef);
   void DeleteReference();
protected:
   jobject objectRef;
};

class GlobalClassRef : public ClassRef {
public:
   GlobalClassRef();
   ~GlobalClassRef();
   bool SetClass(const char* classname);
   bool SetClass(jclass localRef);
   void DeleteReference();
};

class InstanceMethod : public BaseObject {
public:
   InstanceMethod();
   bool SetMethod(const char *classname, const char *methodName, const char *signature);
   bool SetMethod(jclass clazz, const char *methodName, const char *signature);
   //calls method set before, which has return type returnType
   //returnValue is valid only if function return true
   bool CallMethod(jobject object, ReturnType &returnValue, Types returnType, ...);
   bool CallMethod(jobject object, ReturnType &returnValue, Types returnType, va_list args);
protected:
   jmethodID method;
   GlobalClassRef classRef;
};

class StaticMethod : public BaseObject {
public:
   StaticMethod();
   bool SetMethod(const char *classname, const char *methodName, const char *signature);
   bool SetMethod(jclass clazz, const char *methodName, const char *signature);
   //calls method set before, which has return type returnType
   //returnValue is valid only if function return true
   bool CallMethod(ReturnType &returnValue, Types returnType, ...);
protected:
   jmethodID method;
   GlobalClassRef classRef;
};

class Constructor : protected InstanceMethod {
public:
   Constructor();
   bool SetConstructor(const char *classname, const char *signature);
   bool SetConstructor(jclass clazz, const char *signature);
   //Creates a new object
   bool NewObject(jobject &newObj, ...);
   bool NewObject(jobject &newObj, va_list args);
   //Make method from BaseObject available (protected inheritance)
   void SetExceptionHandling(ExceptionHandling eh) { InstanceMethod::SetExceptionHandling(eh); }
protected:
   jmethodID method;
   GlobalClassRef classRef;
};

class Exception : public BaseObject {
public:
   Exception();
   enum ThrowMode { ThrowModeTry, ThrowModeConsequent };
   //Throws a new exception instance of given class with given error message.
   //If ThrowMode is Consequent, in case of failure a ClassNotFoundException and
   //ultimately an internal error is sent. If mode is Try, just try to throw given class
   //and nothing more. In any case, return value indicates whether the requested attempt was successful.
   bool Throw(const char *classname, const char *errMsg, ThrowMode mode = ThrowModeTry);
};

}//end of namespace JNI


#endif

