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
   static void RegisterForDeletion(DeletableObject *obj);
   static void RemoveForDeletion(DeletableObject *obj);
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
      // First argument is the return type of the method.
      // numArgs is the number of arguments of the Java function.
      // The arguments of the method are then appended.
      // 'Class' requires the class name, 'Array' the type as a second specifier.
      // 'Array' requires a the class name as a third specifier if it is an array of objects.
      // These class name specifiers shall be a string given as the following argument.
      // The additional specifiers shall _not_ be counted by numArgs.
      // If returnType is either 'Class' or 'Array', then the additionally required specifiers
      // shall be given as the very last arguments. These arguments shall _not_ be counted
      // by numArgs.
      // Attention: Due to some magic in variadic function calls, if your argument list ends with a
      // string literal, and this arguments is the only variadic argument, you must end the list with
      // another arguments such as (char *)NULL! This arguments shall not be counted and will not be read.
      //
      // Examples:     public int doIt(int arg1, bool arg2);
      //               const char *sig = getSignature(JNI::Int, 2, JNI::Int, JNI::Boolean);
      //               myInstanceMethod.SetMethod("org/my/Example", "doIt", sig);
      //               delete[] sig;
      //
      //               //There are convenience methods with the same semantics, see below
      //               public static String doThat(int arg1, java.util.Date arg2, int[] arg3)
      //               myStaticMethod.SetMethodWithArguments("org/my/Example", "doThat",
      //                            JNI::Object, 3, JNI::Int, JNI::Object, 
      //                            "java/util/Data", JNI::Array, JNI::Int, "java/lang/String"););
      //               myStaticMethod.CallMethod(myReturnType, JNI::Object);
      //
      //               public String[] doSomethingWithArray(Object[] args)
      //               const char *sig = getSignature(JNI::Array, 1, JNI::Array, JNI::Object, "java/lang/object",
      //                                              JNI::Object, "java/lang/String")
      //               myThirdMethod.SetMethod("org/my/Example", "doSomethingWithArray", sig);
      //               delete[] sig;
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

class Field : public BaseObject {
public:
   Field();
   ~Field();
   // For fields of primitive type
   bool SetField(const char *classname, const char *fieldName, Types fieldType);
   bool SetField(jclass clazz, const char *fieldName, Types fieldType);
   // For object or array fields
   bool SetField(const char *classname, const char *fieldName, Types fieldType, const char *type);
   bool SetField(jclass clazz, const char *fieldName, Types fieldType, const char *type);
   bool GetValue(jobject obj, ReturnType &ret);
   bool SetValue(jobject obj, ReturnType value);
protected:
   bool SetField(const char *fieldName, Types fieldType);
   bool SetField(const char *fieldName, Types fieldType, const char *signature);
   bool SetField(const char *fieldName, const char *signature);
   jfieldID field;
   GlobalClassRef classRef;
   Types fieldType;
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

// A wrapper for certain String operations
class String : public BaseObject {
public:
   // From a C-style string in native encoding
   String(const char *cstring);
   // From a java.lang.String object
   String(jstring javastring);
   ~String();
   // Convert to a java.lang.String object. Object returned is a local reference.
   jstring toJavaString();
   // Convert to UTF8 characters.
   // Returned value is valid as long as this JNI::String object exists.
   const char *toUTF8();
   // Convert to C-Style characters in local encoding.
   // Returned value is valid as long as this JNI::String object exists.
   const char *toCString();
   // Returns the number of Unicode characters in this string.
   // Since in UTF8, a character may be represented by up to 3 bytes, this
   // value will be different if the string contains characters not in
   // the standard ASCII range from 0 to 127.
   int getCharactersLength();
   // Returns the number of bytes of the UTF8 representation of this String.
   // This number will be larger than getCharacterLength() if the string
   // contains characters not in the standard ASCII range from 0 to 127.
   int getUTF8BytesLength();
   
   operator jstring() { return toJavaString(); }
   // Do not provide operator const char*, is ambiguous
   
   //internal initialization
   static bool Initialize();
protected:
   jstring javastring;
   const char *cstring;
   const char *utf8;
   bool ownsCString;
   static Constructor javaLangStringByteArray;
   static InstanceMethod javaLangStringGetBytes;
};

// A utility to wrap native data (pointers) and stores them on Java side.
// This code here is implementation independent, there must be an implementation
// which sets itself with SetImplementation().
class NativeData : public BaseObject {
public:
   class Deleter {
   public:
      virtual void Delete(void *nativeData) = 0;
   };
   template <class T> class ReferenceDeleter : public Deleter {
      virtual void Delete(void *nativeData)
        { delete (T *)nativeData; }
   };
   class Implementation {
   public:
      virtual void Set(jobject obj, void *nativeData, bool isNull, Deleter *deleter) = 0;
      virtual void *Get(jobject obj) = 0;
      virtual jobject Create() = 0;
      virtual void Finalize(jobject obj) = 0;
   };
   static void SetImplementation(Implementation *impl);
   operator jobject() { return obj; }
   static void Finalize(jobject obj);
protected:
   NativeData(jobject data);
   void Set(void *nativeData, bool isNull, Deleter *deleter=0);
   void *Get();
   static jobject Create();
   
   static Implementation *impl;
   jobject obj;
};

// Wraps simple pointers for storage on Java side
template <typename T> class PointerNativeData : public NativeData {
public:
   PointerNativeData(jobject obj) : NativeData(obj) {}
   PointerNativeData(T *p) : NativeData(Create()) { Set(p); }
   void Set(T *p) { NativeData::Set((void *)p, p == 0); }
   T *Get() { return (T *)NativeData::Get(); }
   PointerNativeData<T> &operator=(T *p)
   {
      Set(p);
      return *this;
   }
   operator T *() { return Get(); }
};

// Wraps classes which are usually passed by reference and value.
// Internally, such an object is created on the heap and stored as a pointer.
template <typename T> class ReferenceNativeData : public NativeData {
public:
   ReferenceNativeData(jobject obj) : NativeData(obj) {}
   ReferenceNativeData(T &r, bool isNull = false) : NativeData(Create()) { Set(r, isNull); }
   void Set(T &ref, bool isNull = false) { Set(ref, isNull, 0); }
   T &Get() { return *(T *)NativeData::Get(); }
   ReferenceNativeData<T> &operator=(T &r)
   {
      Set(r);
      return *this;
   }
   operator T &() { return Get(); }
protected:
   void Set(T &ref, bool isNull, Deleter *deleter)
   {
      T *p = new T(ref);
      NativeData::Set((void *)p, isNull, deleter);
   }
};

// ReferenceNativeData which frees the memory on the heap, whereas it is leaked by
// ReferenceNativeData. A template-parameter specific class needs to be passed
// as a second template parameter, and this class shall have a public member "deleter"
// which is a Deleter object specific for the first template parameter.
template <typename T, class D> class ReferenceDeleterNativeData : public ReferenceNativeData<T> {
public:
   ReferenceDeleterNativeData(jobject obj) : ReferenceNativeData<T>(obj) {}
   ReferenceDeleterNativeData(T &r, bool isNull = false) : ReferenceNativeData<T>(r, isNull) {}
   void Set(T &ref, bool isNull = false) { Set(ref, isNull, D::deleter); }
};

}//end of namespace JNI


#endif

