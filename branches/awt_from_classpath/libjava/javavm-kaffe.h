/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

 
#ifndef JAVA_VM_H
#define JAVA_VM_H

#include <jni.h>
#include <stdint.h>
#include <vdr/thread.h> 

class ThreadWatch {
public:
   ThreadWatch();
   ~ThreadWatch();
   bool Get();
   void Set(bool value);
private:
   pthread_key_t key;
};

class cJavaVM : public cThread {
public:
   enum State { Waiting, Starting, StartingJava, StartingAborted, Started, Stopping };
   cJavaVM();
   ~cJavaVM();
   bool StartVM();
   void ShutdownVM(bool synchronous);
   void CheckAttachThread();
   void CheckDetachThread();
   static bool CheckSystem();
   static void SetVDRPluginPath(const char *path) { VDRpluginPath=path; } //path to VDR plugins
   static void Init(); //global init
   static const char *ClassPath();
   static const char *ClassHome();
   static const char *LibraryHome();
protected:
   bool AttachCurrentThread();
   bool DetachCurrentThread();
   int attachedThreads;
   JavaVM *jvm;
   JNIEnv *jvmEnv;
   //bool started;
   ThreadWatch threadWatch;
   
   class JavaStartException {
   };
   
   void (*javaExitFunc)(jint);
   void (*javaAbortFunc)(void);
   //bool loadingAborted;
   
   State state;

   virtual void Action(void);
   bool DoStartVM();
   void DoShutdownVM();
   void AbortStartingThread();
private:
   static void adjustLibraryPath();
   static bool preloadLibs();
   static void *preloadedLib;
   static void *ownDynamicLib;
   static const char *VDRpluginPath;
   static bool error;
   pthread_t startingThreadPid;
   //bool doShutdown;
   //bool executed;
   cMutex mutex;
   cCondVar condVar;
};


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

class BaseObject {
public:
   BaseObject() {}
   static void SetJNIEnv(JNIEnv *e) { env=e; }
      //Returns a function signature, accepts enum Types.
      //numArgs is the number of arguments of the Java function.
      //'Class' requires the class name, 'Array' the type as a second specifier.
      //This second specifier shall be a string given as the following argument
      //and _not_ be counted by numArgs.
      //If returnType is either 'Class' or 'Array', then the very last argument
      //is the necessary second specifier. This argument shall _not_ be counted
      //by numArgs.
      //"buffer" must be sufficiently large.
   static void getSignature(char *buffer, Types returnType, int numArgs, ...);
   static bool checkException();
      
protected:
   static JNIEnv *env;
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

}//end of namespace JNI



#endif
