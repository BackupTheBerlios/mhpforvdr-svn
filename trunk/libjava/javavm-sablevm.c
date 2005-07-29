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
#include <signal.h> 

#include <vdr/config.h>
#include <vdr/thread.h> 

#include "javainterface.h"



class cJavaVM : public cThread {
public:
   enum State { Waiting, Starting, StartingJava, StartingAborted, Started, Stopping };
   cJavaVM();
   ~cJavaVM();
   bool StartVM();
   void ShutdownVM(bool synchronous);
   void CheckAttachThread();
   void CheckDetachThread();
   bool CheckSystem();
   //void SetVDRPluginPath(const char *path) { VDRpluginPath=path; } //path to VDR plugins
   void Init(); //global init
   //static const char *ClassPath();
   //static const char *ClassHome();
   //static const char *LibraryHome();
protected:
   bool AttachCurrentThread();
   bool DetachCurrentThread();
   int attachedThreads;
   JavaVM *jvm;
   JNIEnv *jvmEnv;
   PerThreadJNIEnvProvider provider;
   
   void (*javaExitFunc)(jint);
   void (*javaAbortFunc)(void);
   
   State state;

   virtual void Action(void);
   bool DoStartVM();
   void DoShutdownVM();
   void AbortStartingThread();
private:
   //static void adjustLibraryPath();
   bool preloadLibs();
   //LibraryPreloader preloadedLib;
   bool error;
   pthread_t startingThreadPid;
   //bool doShutdown;
   //bool executed;
   cMutex mutex;
   cCondVar condVar;
};

/*
#define CLASSPATH    MHPJARFILE ":"  \
                     SABLEVM_LIB "/libclasspath.jar" \
                     SABLEVM_LIB "/resources.jar" \
                     MHPJARDIR "/jmf.jar"
#define LIBRARYHOME  SABLEVM_NATIVE ":" \
                     MHPLIBDIR
*/

//The boot class path needs to contain all classes an entry any this path depends on.
//This means e.g. the ASM jar file cannot be included in the SYSTEM_CLASS_PATH
//because central classes in MHPJARFILE depend on them.

#define BOOT_CLASS_PATH      MHPJARFILE ":"  \
                             SABLEVM_LIB "/libclasspath.jar" \
                             SABLEVM_LIB "/resources.jar" \
                             GLOBAL_EXTRA_CLASSPATH
#define SYSTEM_CLASS_PATH
#define BOOT_LIBRARY_PATH    SABLEVM_NATIVE
#define SYSTEM_LIBRARY_PATH  MHPLIBDIR

void doNothing() {
}

void doNothingInt(int) {
}

/*
const char *cJavaVM::VDRpluginPath=PLUGINLIBDIR;
void *cJavaVM::preloadedLib = 0;
void *cJavaVM::ownDynamicLib = 0;
bool cJavaVM::error = false;
*/

cJavaVM::cJavaVM() {
   javaExitFunc=&doNothingInt;
   javaAbortFunc=&doNothing;
   //started=false;
   jvm=0;
   jvmEnv=0;
   //doShutdown=false;
   //loadingAborted=false;
   state=Waiting;
}

cJavaVM::~cJavaVM() {
}


//Every thread that calls JNI methods must be "attached" to the VM
//so that it can create its internal per-thread data structures.
//Failure to do so results in a crash! So every static method in 
//JavaInterface calls the method.

void cJavaVM::CheckAttachThread() {
   if (!provider.GetEnv()) {
      AttachCurrentThread();
   }
}


void cJavaVM::CheckDetachThread() {
   if (!provider.GetEnv()) {
      if (DetachCurrentThread());
   }
}


bool cJavaVM::AttachCurrentThread() {
   if (state==Started && jvmEnv) {
      JNIEnv *env;
      if (jvm->AttachCurrentThread((void **)&env, NULL) != JNI_OK)
         return false;
      attachedThreads++;
      provider.SetEnvForCurrentThread(env);
      return true;
   }
   return false;
}

bool cJavaVM::DetachCurrentThread() {
   if (state==Started && jvmEnv) {
      if (jvm->DetachCurrentThread() != JNI_OK)
         return false;
      attachedThreads--;
      provider.SetEnvForCurrentThread(0);
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
   if (state == Started)
      return true;
   if (state != Waiting || error)
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
   if (DoStartVM())
      state=Started;   
   condVar.Broadcast();
   
   while (state==Started)
      condVar.Wait(mutex);
   
   if (state==Stopping)
      DoShutdownVM();
   state=Waiting;
   mutex.Unlock();
}

bool cJavaVM::DoStartVM() {
   attachedThreads=0;

   JavaVMInitArgs JavaInitArgs;
   JavaInitArgs.version = JNI_VERSION_1_2;
   JavaInitArgs.ignoreUnrecognized = JNI_FALSE;
   JavaVMOption options[7];
   
   //SableVM follows Classpath convention that there is a bootstrap class loader
   //provided by the VM and a system class loader which reads 
   //the java.class.path and the java.library.path property. 
   //We need to override some classes (AWT) from
   //SableVM's classpath, and we allow users to use a different sablevm-classpath path.
   //So we need the SableVM-specific prepend option.
   //The other components can be loaded by the system class loader.
   //The same applies to the library path: Allow users to specifiy classpath
   
   options[0].optionString="-Dsablevm.boot.class.path.prepend=" BOOT_CLASS_PATH; // ":/home/marcel/freshmeat/vdr/vdr-1.3.23/PLUGINS/src/mhp/mhp/java/testjar.jar:/home/marcel/freshmeat/vdr/vdr-1.3.23/PLUGINS/src/mhp/mhp/java/testzip.zip";
   options[1].optionString="-Djava.class.path=" SYSTEM_CLASS_PATH;
   options[2].optionString="-Dsablevm.boot.library.path=" BOOT_LIBRARY_PATH;
   options[3].optionString="-Djava.library.path=" SYSTEM_LIBRARY_PATH;
   options[4].optionString="exit";
   options[4].extraInfo=(void *)javaExitFunc;
   options[5].optionString="abort";
   options[5].extraInfo=(void *)javaAbortFunc;
   options[6].optionString="-verbose:gc";
   
   JavaInitArgs.nOptions = 7;
   JavaInitArgs.options  = options;
   //JavaInitArgs.libraryhome = LibraryHome();
   //JavaInitArgs.classhome = ClassHome();   
   
   //JavaInitArgs.nativeStackSize = 1*1024*1024;
   //JavaInitArgs.javaStackSize = 1*1024*1024;
   //JavaInitArgs.maxHeapSize = 15*1024*1024;
   //JavaInitArgs.minHeapSize = 5*1024*1024;
   //JavaInitArgs.allocHeapSize = 1*1024*1024;
   //JavaInitArgs.verifyMode = 1;   
   

   printf("Starting JVM...\n");
   //adjustLibraryPath();
   
   if (error)
      return false;
      
   try {
      if (JNI_CreateJavaVM(&jvm, (void **)&jvmEnv, &JavaInitArgs) != 0 || state==StartingAborted) {
         fprintf( stderr, "Failed to create JVM\n" );
         error=true; //disable use of plugin for subsequent attempts
      } else {
         provider.SetEnvForCurrentThread(jvmEnv); //main thread is attached by VM
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



bool cJavaVM::preloadLibs() {
   //This lib is linked to the plugin the normal -l way, and since the plugin is loaded
   //without RTDL_GLOBAL, need to reload the library as well (plugin is reloaded in mhp.c)
   error=false;
   /*if (!preloadedLib.Load(SABLEVM_SODIR "/libsablevm.so")) {
      error=true;
      return false;
   }*/
   return true;
}

/*void cJavaVM::adjustLibraryPath() {
   char *path=getenv("KAFFELIBRARYPATH");
   if (!path)
      path="";
   char *newpath;
   asprintf(&newpath, "%s:%s", path, cJavaVM::LibraryHome());
   setenv("KAFFELIBRARYPATH", newpath, true);
   free(newpath);
}

const char *cJavaVM::ClassPath() {
   return
         MHPJARFILE ":" 
         SABLEVM_LIB "/libclasspath.jar" ":"
         SABLEVM_LIB "/resources.jar" ":"
         MHPJARDIR "/jmf.jar";
}

const char *cJavaVM::LibraryHome() {
   return SABLEVM_NATIVE ":"
          MHPLIBDIR;
}

const char *cJavaVM::ClassHome() {
   return KAFFE_DIR;
}*/


class JavaInterfaceSableVM : public JavaInterface, private cJavaVM {
public:
   JavaInterfaceSableVM();
protected:
   static void jvmExit(jint exitCode);
   static void jvmAbort();
   virtual void SyncShutdown();
   //redirect virtual methods to cJavaVM
   virtual bool isStarted() { return state==Started; }
   virtual bool StartVM() { return cJavaVM::StartVM(); }
   virtual void CheckAttachThread() { return cJavaVM::CheckAttachThread(); }
   virtual void CheckDetachThread() { return cJavaVM::CheckDetachThread(); }
   virtual bool InitializeVM();
   virtual bool CheckVM() { return cJavaVM::CheckSystem(); }
   static JavaInterfaceSableVM *asVM() { return ((JavaInterfaceSableVM*)s_self); }
};

void JavaInterfaceSableVM::SyncShutdown() {
   //do this in the right order - when the VM is shut down, the methods cannot be released
   try {
      delete methods;
      methods=0;
      ShutdownVM(true);
   } catch (JavaStartException &e) {}
}

void JavaInterfaceSableVM::jvmExit(jint exitCode) {
   jvmAbort();
}

void JavaInterfaceSableVM::jvmAbort() {
   if (s_self) {
      switch (asVM()->state) {
      case Waiting:
      case Starting:
      case Stopping:
      case Started:
         esyslog("MHP: Kaffe called abort. At this time, it can actually not be due to a broken installation, "
                 "rather to a segfault anywhere within VDR (including, and most likely, the MHP plugin). "
                 "The process will be aborted NOW.");
         //restore default SIGABRT handler
         signal(SIGABRT, SIG_DFL);
         abort();
      case StartingJava:
         {
         esyslog("MHP: Unusual error condition in Java stack while starting up the VM. Trying to recover.");
         asVM()->state=StartingAborted;
         //Let cJavaVM try to unlock its mutexes and exit this thread if possible
         asVM()->AbortStartingThread();
         break;
         }
      case StartingAborted:
         esyslog("MHP: Abort called again from Java stack, starting was already aborted. Continuing to recover.");
      }
   }
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


JavaInterfaceSableVM::JavaInterfaceSableVM() {
   javaExitFunc=(jvmExit);
   javaAbortFunc=(jvmAbort);
}

bool JavaInterfaceSableVM::InitializeVM() {
   cJavaVM::Init();
   return true;
}

//from class JavaInterface, not JavaInterfaceSableVM!
JavaInterface *JavaInterface::self() {
   if (!s_self)
      s_self=new JavaInterfaceSableVM();
   return s_self;
}



