/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <dlfcn.h> 

#include <vdr/config.h>

#include "javavm.h"

//for Kaffe-specific JavaVMInitArgs
#define NONRETURNING
#include <kaffe_jni.h>

ThreadWatch::ThreadWatch() {
   pthread_key_create(&key, NULL);
}

ThreadWatch::~ThreadWatch() {
   pthread_key_delete(key);
}

bool ThreadWatch::Get() {
   return (bool)pthread_getspecific(key);
}

void ThreadWatch::Set(bool value) {
   pthread_setspecific(key, (void *)value);
}


void doNothing() {
}

void doNothingInt(int) {
}

const char *cJavaVM::VDRpluginPath=PLUGINLIBDIR;
void *cJavaVM::preloadedLib = 0;
void *cJavaVM::ownDynamicLib = 0;
bool cJavaVM::error = false;

cJavaVM::cJavaVM() {
   javaExitFunc=&doNothingInt;
   javaAbortFunc=&doNothing;
   //started=false;
   jvm=0;
   jvmEnv=0;
   //doShutdown=false;
   //loadingAborted=false;
   startingThreadPid=0;
   state=Waiting;
}

cJavaVM::~cJavaVM() {
   if (preloadedLib)
      dlclose(preloadedLib);
   if (ownDynamicLib)
      dlclose(ownDynamicLib);
}


//Every thread that calls JNI methods must be "attached" to the VM
//so that it can create its internal per-thread data structures.
//Failure to do so results in a crash! So every static method in 
//JavaInterface calls the method.

void cJavaVM::CheckAttachThread() {
   if (!threadWatch.Get()) {
      if (AttachCurrentThread())
         threadWatch.Set(true);
   }
}


void cJavaVM::CheckDetachThread() {
   if (threadWatch.Get()) {
      if (DetachCurrentThread())
         threadWatch.Set(false);
   }
}


bool cJavaVM::AttachCurrentThread() {
   if (state==Started && jvmEnv) {
      JNIEnv *env;
      jvm->AttachCurrentThread((void **)&env, NULL);
      attachedThreads++;
      return true;
   }
   return false;
}

bool cJavaVM::DetachCurrentThread() {
   if (state==Started && jvmEnv) {
      jvm->DetachCurrentThread();
      attachedThreads--;
      return true;
   }
   return false;
}

void cJavaVM::Init() {
   preloadLibs();
}

bool cJavaVM::CheckSystem() {
   return !error;
}

//StartVM and ShutdownVM are called from the main thread. They control
//the thread from which the VM ist started and shut down, so the actual
//work is done in Action, DoStartVM and DoShutdownVM.

bool cJavaVM::StartVM() {
   if (state != Waiting)
      return false;
      
   state=Starting;
   
   cMutexLock lock(&mutex);
   Start(); //Start the thread
   for (int i=0;i<10;i++) {
      condVar.TimedWait(mutex, 1000);
      if (state >= Started)
         return true;
      if (state == StartingAborted) {
         Cancel(2);
         state=Waiting;
         return false;
      }
   }
   
   return false;
}

void cJavaVM::ShutdownVM(bool synchronous) {
   if (state == Waiting || state == Stopping)
      return;
      
   cMutexLock lock(&mutex);
   state=Stopping;
   condVar.Broadcast();
   
   if (!synchronous)
      return;
      
   //Shutdown is currently not clean. There are some threads left in Java,
   //and kaffe won't return from DestroyJavaVM in this case.
   //This is a TODO for the future, but currently let it be if it does not crash.
   return;
      
   //wait for completion of shutdown
   for (int i=0;i<3;i++) {
      condVar.TimedWait(mutex, 1000);
      if (state != Stopping)
         return;
   }
   printf("In WaitForShutdown: Kaffe did not shut down after 3 seconds\n");
}

//Use a fresh thread as Kaffe main thread.
//DoShutdownVM may never return because of
//some assumptions Kaffe is making.
void cJavaVM::Action() {
   mutex.Lock();
   
   state=StartingJava;
   startingThreadPid=pthread_self();
   if (DoStartVM())
      state=Started;   
   condVar.Broadcast();
   
   while (state==Started)
      condVar.Wait(mutex);
   
   if (state==Stopping)
      DoShutdownVM();
   state=Waiting;
   startingThreadPid=0;
   mutex.Unlock();
}

bool cJavaVM::DoStartVM() {
   attachedThreads=0;

   KaffeVM_Arguments JavaInitArgs;
   JavaInitArgs.version = JNI_VERSION_1_1;
   JNI_GetDefaultJavaVMInitArgs(&JavaInitArgs);

     //Kaffe obviously expects args.classpath to be empty
     //and builds its classpath from bootClasspath
   JavaInitArgs.bootClasspath = ClassPath();
   JavaInitArgs.libraryhome = LibraryHome();
   JavaInitArgs.classhome = ClassHome();   
   
   JavaInitArgs.nativeStackSize = 2*1024*1024;
   //JavaInitArgs.maxHeapSize = 64*1024*1024;
   //JavaInitArgs.minHeapSize = 5*1024*1024;
   //JavaInitArgs.allocHeapSize = 1*1024*1024;
   //JavaInitArgs.verifyMode = 0;   
   JavaInitArgs.exit = javaExitFunc;
   JavaInitArgs.abort = javaAbortFunc;

   JavaInitArgs.disableAsyncGC = 1;
   JavaInitArgs.enableVerboseGC = 1;
   

   printf("Starting JVM...\n");
   adjustLibraryPath();
   preloadLibs();
   
   if (error)
      return false;
      
   try {
      if (JNI_CreateJavaVM(&jvm, (void **)&jvmEnv, &JavaInitArgs) != 0 || state==StartingAborted) {
         fprintf( stderr, "Failed to create JVM\n" );
         error=true; //disable use of plugin for subsequent attempts
      } else {
         threadWatch.Set(true); //main thread is attached by Kaffe
         printf("JVM Created successfully\n");
         return true;
      }
   } catch (JavaStartException &e) {
      //throwing the exception through the starting VM from JavaInterface::jvmAbort doesn't work
      //doesn't find the catch clause here => aborts, so the loadingAborted flag is introduced
      fprintf( stderr, "Failed to create JVM\n" );
   }
   return false;
}

void cJavaVM::AbortStartingThread() {
   //paranoid check
   if (!startingThreadPid == pthread_self())
      return;
   //called from JavaInterface is abort()-replacing function
   state=StartingAborted;
   //set error flag to disable further usage
   error=true;
   isyslog("MHP: Disabling Java system after severe error");
   //wake main thread waiting for startup thread
   condVar.Broadcast();
   //unlock mutex acquired in Action();
   mutex.Unlock();
   //kill this thread
   Cancel();
   //exit should never return
   esyslog("MHP: Aborting thread failed. Don`t know what to do, continuing");
}

void cJavaVM::DoShutdownVM() {
   //mutex is locked
   if (attachedThreads>0) {
      //wait 3 seconds for all threads to detach
      //for (int i=0;i<30 && attachedThreads>0;i++)
         //condVar.TimedWait(mutex, 1000);
      //esyslog("Not all threads detached from VM");
   }
   jvm->DestroyJavaVM();
   jvm=0;
}


//from vdr/plugin.c
#define LIBVDR_PREFIX  "libvdr-"
#define SO_INDICATOR   ".so."

bool cJavaVM::preloadLibs() {
//It took me hours to debug this:
//The Java stack will dlopen libraries for its JNI native code.
//This code accesses code in the plugin (e.g. libait).
//Since VDR dlopens plugins with RTLD_NOW only, we have to reopen the plugin
//with RTLD_NOW | RTLD_GLOBAL because the JNI libraries can't link
//to the plugin's code otherwise.
   error=false;
   if (!ownDynamicLib) {
      char *buffer = NULL;
      //from vdr/plugin.c
      asprintf(&buffer, "%s/%s%s%s%s", VDRpluginPath, LIBVDR_PREFIX, "mhp", SO_INDICATOR, VDRVERSION);
      ownDynamicLib=dlopen(buffer, RTLD_NOW | RTLD_GLOBAL);
      if (!ownDynamicLib) {
         esyslog("MHP: cJavaVM: Failed to open own dynamic object (plugin) under path %s, error message \"%s\"."
                 "\nRemember to give the plugin the same \"-L\" option as VDR!\n", buffer, dlerror());
         free(buffer);
         error=true;
         return false;
      }
      free(buffer);
   }
//It took me hours to debug that:
//kaffe (libkaffevm) will dlopen libnative.so which in turn needs symbols from libkaffevm.so.
//These were not available until I introduced this extra RTDL_GLOBAL dlopen (the plugin is linked against
//libkaffevm, anyway).
   if (!preloadedLib) {
      preloadedLib=dlopen(KAFFE_NATIVE "/libkaffevm.so", RTLD_NOW | RTLD_GLOBAL);
      if (!preloadedLib) {
         esyslog("MHP: cJavaVM: Failed to open libkaffevm.so: %s\n", dlerror());
         error=true;
         return false;
      }
   }
   return true;
}

//The native libraries for Kaffe are located in a non standard directory,
//(LibraryHome()). These libraries are loaded with System.loadLibrary in Java,
//then with libltdl and then dlopen()'ed by Kaffe.
//First idea was to adjust LD_LIBRARY_PATH with setenv, but changes to this
//after program start are ignored by dlopen.
//Next idea was to adjust LTDL_LIBRARY_PATH, used by ltdl, libtool's dlopen
//wrapper currently used by Kaffe. This did not have any effect.
//Next solution is to do nothing. See comment in PROBLEMS file.
void cJavaVM::adjustLibraryPath() {
   /*char *path=getenv("LTDL_LIBRARY_PATH");
   if (!path)
      path="";
   char *newpath;
   asprintf(&newpath, "%s:%s", path, cJavaVM::LibraryHome());
   setenv("LTDL_LIBRARY_PATH", newpath, true);
   free(newpath);*/
}

const char *cJavaVM::ClassPath() {
   return /*".:"*/ 
         MHPJARFILE ":" 
         MHPJARDIR "/jmf.jar:"
         MHPJARDIR "/com_stevesoft_regex.jar:" 
         KAFFE_LIB "/rt.jar:";
}

const char *cJavaVM::LibraryHome() {
   return KAFFE_LIB ":"
          KAFFE_LIB "/i386:"
          KAFFE_DIR "/lib:"
          MHPLIBDIR;
}

const char *cJavaVM::ClassHome() {
   return KAFFE_DIR;
}

namespace JNI {

void BaseObject::getSignature(char *format, Types returnType, int args, ...) {
   va_list ap;
   va_start(ap, args);
   
   int index=0;
   
   format[index++]='(';
   
   Types t;
   for (int i=0;i<args;i++) {
      t=(Types)va_arg(ap, int);
      if (t != Void) {
         format[index++]=(char)t;
         if (t == JNI::Object || t == Array) {
            const char *c=va_arg(ap, const char *);
            strcpy(format+index, c);
            index+=strlen(c);
         }
         if (t == JNI::Object)
            format[index++]=';';
      }
   }
   
   format[index++]=')';
   
   format[index++]=(char)returnType;
   if (returnType == JNI::Object || returnType == Array) {
      const char *c=va_arg(ap, const char *);
      strcpy(format+index, c);
      index+=strlen(c);
   }
   if (returnType == JNI::Object)
      format[index++]=';';
   
   va_end(ap);
   
   format[index++]=0;
}

bool BaseObject::checkException() {
   if (env->ExceptionOccurred()) {
      env->ExceptionDescribe();
      env->ExceptionClear();
      return false;
   }
   return true;
}

JNIEnv *BaseObject::env=0;

ClassRef::ClassRef() {
   classRef=0;
}

bool ClassRef::SetClass(const char* classname) {
   classRef=env->FindClass(classname);
   checkException();
   return classRef;
}

GlobalClassRef::GlobalClassRef() {
}

GlobalClassRef::~GlobalClassRef() {
   DeleteReference();
}

void GlobalClassRef::DeleteReference() {
   if (classRef) {
      env->DeleteGlobalRef(classRef);
      classRef=0;
   }
}

bool GlobalClassRef::SetClass(const char* classname) {
   if (!ClassRef::SetClass(classname))
      return false;
   classRef=(jclass)env->NewGlobalRef(classRef);
   checkException();
   return classRef;
}

bool GlobalClassRef::SetClass(jclass localRef) {
   classRef=(jclass)env->NewGlobalRef(localRef);
   checkException();
   return classRef;
}

GlobalObjectRef::GlobalObjectRef() {
   objectRef=0;
}

GlobalObjectRef::~GlobalObjectRef() {
   DeleteReference();
}

void GlobalObjectRef::DeleteReference() {
   if (objectRef) {
      env->DeleteGlobalRef(objectRef);
      objectRef=0;
   }
}

bool GlobalObjectRef::SetObject(jobject localRef) {
   objectRef=env->NewGlobalRef(localRef);
   checkException();
   return objectRef;
}

jclass GlobalObjectRef::GetClass() {
   if (!objectRef)
      return 0;
   jclass clazz=env->GetObjectClass(objectRef);
   checkException();
   return clazz;
}



StaticMethod::StaticMethod() {
   method=0;
}

bool StaticMethod::SetMethod(const char *classname, const char *methodName, const char *signature) {
   if (!classRef.SetClass(classname))
      return false;
   method=env->GetStaticMethodID((jclass)classRef, methodName, signature);
   checkException();
   return method;
}

bool StaticMethod::SetMethod(jclass clazz, const char *methodName, const char *signature) {
   if (!classRef.SetClass(clazz))
      return false;
   method=env->GetStaticMethodID((jclass)classRef, methodName, signature);
   checkException();
   return method;
}

bool StaticMethod::CallMethod(ReturnType &ret, Types returnType, ...) {
   ret.TypeLong=0;
   if (!method)
      return false;

   va_list ap;
   va_start(ap, returnType);
   switch(returnType) {
      case Void:
         env->CallStaticVoidMethodV(classRef, method, ap);
         break;
      case Boolean:
         ret.TypeBoolean=env->CallStaticBooleanMethodV(classRef, method, ap);
         break;
      case Byte:
         ret.TypeByte=env->CallStaticByteMethodV(classRef, method, ap);
         break;
      case Char:
         ret.TypeChar=env->CallStaticCharMethodV(classRef, method, ap);
         break;
      case Short:
         ret.TypeShort=env->CallStaticShortMethodV(classRef, method, ap);
         break;
      case Int:
         ret.TypeInt=env->CallStaticIntMethodV(classRef, method, ap);
         break;
      case Long:
         ret.TypeLong=env->CallStaticLongMethodV(classRef, method, ap);
         break;
      case Double:
         ret.TypeDouble=env->CallStaticDoubleMethodV(classRef, method, ap);
         break;
      case Float:
         ret.TypeFloat=env->CallStaticFloatMethodV(classRef, method, ap);
         break;
      case Object:
      case Array: //in Java, an array is an object
         ret.TypeObject=env->CallStaticObjectMethodV(classRef, method, ap);
         break;
   }
   va_end(ap);
   return checkException();
}



InstanceMethod::InstanceMethod() {
   method=0;
}

bool InstanceMethod::SetMethod(const char *classname, const char *methodName, const char *signature) {
   if (!classRef.SetClass(classname))
      return false;
   method=env->GetMethodID((jclass)classRef, methodName, signature);
   checkException();
   return method;
}

bool InstanceMethod::SetMethod(jclass clazz, const char *methodName, const char *signature) {
   if (!classRef.SetClass(clazz))
      return false;
   method=env->GetMethodID((jclass)classRef, methodName, signature);
   checkException();
   return method;
}

bool InstanceMethod::CallMethod(jobject object, ReturnType &ret, Types returnType, ...) {
   ret.TypeLong=0;
   if (!method)
      return false;

   va_list ap;
   va_start(ap, returnType);
   switch(returnType) {
      case Void:
         env->CallVoidMethodV(object, method, ap);
         break;
      case Boolean:
         ret.TypeBoolean=env->CallBooleanMethodV(object, method, ap);
         break;
      case Byte:
         ret.TypeByte=env->CallByteMethodV(object, method, ap);
         break;
      case Char:
         ret.TypeChar=env->CallCharMethodV(object, method, ap);
         break;
      case Short:
         ret.TypeShort=env->CallShortMethodV(object, method, ap);
         break;
      case Int:
         ret.TypeInt=env->CallIntMethodV(object, method, ap);
         break;
      case Long:
         ret.TypeLong=env->CallLongMethodV(object, method, ap);
         break;
      case Double:
         ret.TypeDouble=env->CallDoubleMethodV(object, method, ap);
         break;
      case Float:
         ret.TypeFloat=env->CallFloatMethodV(object, method, ap);
         break;
      case Object:
      case Array: //in Java, an array is an object
         ret.TypeObject=env->CallObjectMethodV(object, method, ap);
         break;
   }
   va_end(ap);
   return checkException();
}


}//end of namespace JNI




