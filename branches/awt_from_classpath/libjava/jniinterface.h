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
enum ThrowMode { ThrowModeTry, ThrowModeConsequent };
enum PredefinedException {
   JavaLangIllegalArgumentException,
   JavaLangIllegalStateException,
   JavaLangNullPointerException,
   JavaLangRuntimeException,
   JavaIoIOException
};

class JNIEnvProvider {
public:
   // to set JNIEnv when JNI API is used from a JNI function, called from Java code,
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

class DeletableObject {
public:
   virtual ~DeletableObject();
   // Called when VM is shut down. Shall be implemented by all subclasses which need
   // to remove any sort of references/IDs with JNI calls. At destruction time, 
   // the VM may be shut down and JNI no longer available.
   // As well, this is called from destructor.
   // This method shall be implemented so that it may be called multiple times,
   // where only the first call has the specified effect, subsequent calls doing nothing.
   virtual void Delete() {}
protected:
   // Causes Delete() to be called immediately before the VM is shut down
   void RegisterForDeletion();
   // Automatically called from destructor
   void RemoveForDeletion();
};

class ShutdownManager {
public:
   static void RegisterForDeletion(DeletableObject *obj) { return s_self->RegisterForDeletion(obj); }
   static void RemoveForDeletion(DeletableObject *obj) { return s_self->RemoveForDeletion(obj); }
protected:
   ShutdownManager() { s_self = this; }
   virtual ~ShutdownManager() { s_self = 0; }
   virtual void registerForDeletion(DeletableObject *obj) = 0;
   virtual void removeForDeletion(DeletableObject *obj) = 0;
private:
   static ShutdownManager *s_self;
};

class BaseObject {
public:
   BaseObject();
   virtual ~BaseObject();
      // Returns a function signature, accepts enum Types.
      // The returned array is allocated with new[] and must be delete[]'ed by the caller.
      // numArgs is the number of arguments of the Java function.
      // 'Class' requires the class name, 'Array' the type as a second specifier.
      // This second specifier shall be a string given as the following argument
      // and _not_ be counted by numArgs.
      // If returnType is either 'Class' or 'Array', then the very last argument
      // is the necessary second specifier. This argument shall _not_ be counted
      // by numArgs.
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
   static const char *getSignature(Types returnType, int numArgs, ...);
   static const char *getSignature(Types returnType, int numArgs, va_list args);
      //Get signature for a constructor. This is equvivalent to calling getSignature with returnType JNI::Void
   static const char *getConstructorSignature(int numArgs, ...);
   
   static bool checkException(ExceptionHandling eh);
   bool checkException();
   
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

class GlobalObjectRef : public BaseObject, public DeletableObject {
public:
   GlobalObjectRef();
   ~GlobalObjectRef();
   operator jobject() const { return objectRef; }
   jclass GetClass();
   //a valid local reference
   bool SetObject(jobject localRef);
   virtual void Delete();
protected:
   jobject objectRef;
};

class GlobalClassRef : public ClassRef, public DeletableObject {
public:
   GlobalClassRef();
   ~GlobalClassRef();
   bool SetClass(const char* classname);
   bool SetClass(jclass localRef);
   virtual void Delete();
};

class Method : public BaseObject, public DeletableObject {
public:
   Method();
   // Calling these four methods is equivalent to obtaining the signature with getSignature
   // and then calling one the two other methods.
   // For the arguments returnType, numArgs, and the variable argument list
   // see the documentation for getSignature above.
   virtual bool SetMethodWithArguments(const char *classname, const char *methodName, Types returnType, int numArgs, ...);
   virtual bool SetMethodWithArguments(jclass clazz, const char *methodName, Types returnType, int numArgs, ...);
   virtual bool SetMethodWithArguments(const char *classname, const char *methodName, Types returnType, int numArgs, va_list args);
   virtual bool SetMethodWithArguments(jclass clazz, const char *methodName, Types returnType, int numArgs, va_list args);
   
   virtual bool SetMethod(const char *classname, const char *methodName, Types returnType, const char *signature) = 0;
   virtual bool SetMethod(jclass clazz, const char *methodName, Types returnType, const char *signature) = 0;
   virtual void Delete();
protected:
   jmethodID method;
   GlobalClassRef classRef;
   Types returnType;
};

class InstanceMethod : public Method {
public:
   InstanceMethod();
   virtual bool SetMethod(const char *classname, const char *methodName, Types returnType, const char *signature);
   virtual bool SetMethod(jclass clazz, const char *methodName, Types returnType, const char *signature);
   //calls method set before, which has return type returnType
   //returnValue is valid only if function return true
   bool CallMethod(jobject object, ReturnType &returnValue, ...);
   bool CallMethod(jobject object, ReturnType &returnValue, va_list args);
};

class StaticMethod : public Method {
public:
   StaticMethod();
   virtual bool SetMethod(const char *classname, const char *methodName, Types returnType, const char *signature);
   virtual bool SetMethod(jclass clazz, const char *methodName, Types returnType, const char *signature);
   //calls method set before, which has return type returnType
   //returnValue is valid only if function return true
   bool CallMethod(ReturnType &returnValue, ...);
   bool CallMethod(ReturnType &returnValue, va_list args);
};

class Constructor : protected InstanceMethod {
public:
   Constructor();
   bool SetConstructorWithArguments(const char *classname, int numArgs, ...);
   bool SetConstructorWithArguments(jclass clazz, int numArgs, ...);
   bool SetConstructor(const char *classname, const char *signature);
   bool SetConstructor(jclass clazz, const char *signature);
   //Creates a new object
   bool NewObject(jobject &newObj, ...);
   bool NewObject(jobject &newObj, va_list args);
   //Make method from BaseObject available (protected inheritance)
   void SetExceptionHandling(ExceptionHandling eh) { InstanceMethod::SetExceptionHandling(eh); }
};

class Exception : public BaseObject {
public:
   Exception();
   bool SetClass(const char *classname);
   // This method is well suited for use with a preinitialized Exception object
   bool Throw(const char *errMsg);
   static bool Throw(PredefinedException e, const char *errMsg);
   
   // Combines SetClass(classname) and then Throw(errMsg, mode) - with error checking!
   // This call is well suited to be called when an error state is reached and the Exception variable
   // has not yet been created/initialized.
   // This call is not affected by a call to SetClass before.
   // If ThrowMode is Consequent, in case of failure of loading an exception class a ClassNotFoundException and
   // ultimately an internal error is sent. If mode is Try, just try to throw given class
   // and nothing more. In any case, return value indicates whether the requested attempt was successful.
   static bool Throw(const char *classname, const char *errMsg, ThrowMode mode = ThrowModeTry);
   
   //internal initialization
   static bool Initialize();
protected:
   static Exception javaLangIllegalArgumentException;
   static Exception javaLangIllegalStateException;
   static Exception javaLangNullPointerException;
   static Exception javaLangRuntimeException;
   static Exception javaIoIOException;
   GlobalClassRef classRef;
};

}//end of namespace JNI


#endif

