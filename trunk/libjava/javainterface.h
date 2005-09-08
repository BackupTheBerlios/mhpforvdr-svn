/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

 
#ifndef JAVA_INTERFACE_H
#define JAVA_INTERFACE_H

#include <vdr/keys.h>
#include <vdr/thread.h>
#include <libait/applications.h>
#include "jniinterface.h"


class JavaStartException {
};

//generic wrapper for pthread_getspecific
template <typename T> class ThreadWatch {
public:
   ThreadWatch() { pthread_key_create(&key, NULL); }
   ~ThreadWatch() { pthread_key_delete(key); }
   T Get() { return (T)pthread_getspecific(key); }
   void Set(T value) { pthread_setspecific(key, (void *)value); }
private:
   pthread_key_t key;
};

// Three implementations for a JNIEnvProvider

// For a VM which returns the same env pointer for all threads
class SimpleJNIEnvProvider : public JNI::JNIEnvProvider {
public:
   SimpleJNIEnvProvider() : jnienv(0) {}
protected:
   virtual JNIEnv *env() { return jnienv; }
   virtual void setJavaEnv(JNIEnv *env) { jnienv=env; }
   JNIEnv *jnienv;
};

// For a VM which returns a specific env pointer for each attached thread.
// The threads created on Java side, the pointer must be set by the JNI code
// using the JNI interface.
// This is currently NOT the case for large parts of the JNI code
// of this implementation.
class PerThreadJNIEnvProvider : public JNI::JNIEnvProvider, public ThreadWatch<JNIEnv *> {
public:
   PerThreadJNIEnvProvider() {}
protected:
   virtual JNIEnv *env() { return Get(); }
   virtual void setJavaEnv(JNIEnv *env) { Set(env); }
};

// For a VM which returns a specific env pointer for each attached thread.
// The VM must additionally (complying with the spec) return the pointer
// in the JNI function AttachCurrentThread even of the thread is
// already attached, and the JavaInterface implementation here must set
// the JNIEnv for this provider in its CheckAttachThread function.
class PerThreadAutomagicJNIEnvProvider : public PerThreadJNIEnvProvider {
public:
   PerThreadAutomagicJNIEnvProvider() {}
   JNIEnv *GetEnvNoMagic() { return Get(); }
protected:
   virtual JNIEnv *env();
};

//utility to dlopen a library with RTDL_GLOBAL flag
class LibraryPreloader {
public:
   LibraryPreloader();
   ~LibraryPreloader();
   bool Load(const char *library);
protected:
   void Close();
   void *dlhandle;
};

//This is the interface to access Java code
//This is pure JNI, there shall be no VM-specific code 
// - VM specific code is in the subclasses in javavm-$(VM).c
//The static method self() of this class will be implemented there as well.
class JavaInterface {
public:
   //check for severe errors preventing the use of the system
   static bool CheckSystem();
   //check that the VM has cleanly started
   static bool CheckStart();
   //interface to ApplicationManager (Java) via JNI
   static bool StartApplication(ApplicationInfo::cApplication::Ptr app);
   static bool StopApplication(ApplicationInfo::cApplication::Ptr app);
   static bool StopApplications();
   static bool PauseApplication(ApplicationInfo::cApplication::Ptr app);
   static bool ResumeApplication(ApplicationInfo::cApplication::Ptr app);
   
   static bool NewApplication(ApplicationInfo::cApplication::Ptr app);
   static bool ApplicationRemoved(ApplicationInfo::cApplication::Ptr app);
   
   static bool ProcessKey(eKeys Key);
   
   //Performs static shutdown operations in Java. 
   //All MHP applications have been stopped before calling this.
   //Prepares for CleanUp().
   static bool ShutdownMHP();
   //Destroys the JavaVM.
   static bool CleanUp();
   //do basic initialization
   static bool InitializeSystem();
   
   
   //libjava interface
   
   //any thread that calls one of the above JNI wrapper functions
   //for the first time is automatically attached to the VM.
   //Calling CheckAttachCurrentThread directly is thus not necessary.
   //However, any thread that uses the JNI interface shall
   //call CheckDetachCurrentThread() when leaving its thread function.
   //It is safe to call these functions multiple times or 
   //to try to detach a not-attached thread.
   static void CheckAttachCurrentThread();
   static void CheckDetachCurrentThread();
   
protected:
   static JavaInterface *self();
   static JavaInterface *s_self;
   
   //to be implemented by subclasses:
   //shut down VM synchronously (return after completed shutdown)
   virtual void SyncShutdown() = 0;
   //return whether VM has been created
   virtual bool isStarted() = 0;
   //check that current thread is attached to VM,
   //and make sure the JNIEnvProvider returns the JNIEnv for this thread
   virtual void CheckAttachThread() = 0;
   virtual void CheckDetachThread() = 0;
   //actually start VM, and create a JNIEnvProvider
   virtual bool StartVM() = 0;
   //basic initialization, called from plugin's Start() called by VDR
   virtual bool InitializeVM() = 0;
   //check that everything is sane and no critical errors occurred.
   //A return value of false will prevent further use of the plugin.
   virtual bool CheckVM() = 0;
   
   JavaInterface();
   virtual ~JavaInterface();
   
   //Call StartVM and initialize JNI objects
   //to access Java part of implementation.
   //Nothing VM specific here, so no virtual function.
   bool StartJava();
   bool isJavaStarted() { return isStarted() && jniInitialized; }
   
   struct StaticMethods {
      JNI::StaticMethod initialize;
      JNI::StaticMethod newApplication;
      JNI::StaticMethod applicationRemoved;
      JNI::StaticMethod startApplication;
      JNI::StaticMethod stopApplication;
      JNI::StaticMethod stopApplications;
      JNI::StaticMethod pauseApplication;
      JNI::StaticMethod resumeApplication;
      JNI::StaticMethod processKey;
      JNI::StaticMethod shutdown;
   };
   StaticMethods *methods;
private:
   bool jniInitialized;
   bool jniError;
};


#endif
