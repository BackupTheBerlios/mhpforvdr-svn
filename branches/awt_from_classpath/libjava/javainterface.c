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
#include <dlfcn.h> 
 
#include <libait/ait.h>
#include "javainterface.h"

bool JavaInterface::CheckStart() {
   if (!self()->isStarted()) {
      //starts VM and initializes JNI access to ApplicationManager
      return self()->StartJava();
   }
   return true;
}

bool JavaInterface::CheckSystem() {
   return self()->CheckVM();
}

bool JavaInterface::InitializeSystem() {
   return self()->InitializeVM();
}

//See note on memory management of the cApplication::Ptrs in mhp/native/de/application.c
bool JavaInterface::StartApplication(ApplicationInfo::cApplication::Ptr app) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->startApplication.CallMethod(ret, JNI::Int, (void *)new ApplicationInfo::cApplication::Ptr(app)) && ret.TypeInt == 0;
}

bool JavaInterface::StopApplication(ApplicationInfo::cApplication::Ptr app) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->stopApplication.CallMethod(ret, JNI::Int, (void *)new ApplicationInfo::cApplication::Ptr(app)) && ret.TypeInt == 0;
}

bool JavaInterface::StopApplications() {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->stopApplications.CallMethod(ret, JNI::Int) && ret.TypeInt == 0;
}

bool JavaInterface::PauseApplication(ApplicationInfo::cApplication::Ptr app) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->pauseApplication.CallMethod(ret, JNI::Int, (void *)new ApplicationInfo::cApplication::Ptr(app)) && ret.TypeInt == 0;
}

bool JavaInterface::ResumeApplication(ApplicationInfo::cApplication::Ptr app) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->resumeApplication.CallMethod(ret, JNI::Int, (void *)new ApplicationInfo::cApplication::Ptr(app)) && ret.TypeInt == 0;
}


bool JavaInterface::NewApplication(ApplicationInfo::cApplication::Ptr app) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->newApplication.CallMethod(ret, JNI::Int, (void *)new ApplicationInfo::cApplication::Ptr(app)) && ret.TypeInt == 0;
}

bool JavaInterface::ApplicationRemoved(ApplicationInfo::cApplication::Ptr app) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->applicationRemoved.CallMethod(ret, JNI::Int, (void *)new ApplicationInfo::cApplication::Ptr(app)) && ret.TypeInt == 0;
}

bool JavaInterface::ProcessKey(eKeys Key) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->processKey.CallMethod(ret, JNI::Int, (int)Key) && ret.TypeInt == 0;
}

void JavaInterface::CheckAttachCurrentThread() {
   CheckStart();
   self()->CheckAttachThread();
}

void JavaInterface::CheckDetachCurrentThread() {
   //dont start VM if not started or already shut down!
   if (s_self && self()->isStarted())
      self()->CheckDetachThread();
}

bool JavaInterface::ShutdownMHP() {
   if (!s_self || !s_self->isStarted())
      return true;
      
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->shutdown.CallMethod(ret, JNI::Int) && ret.TypeInt == 0;  
}

//shall be called from VDR's main thread
bool JavaInterface::CleanUp() {
   if (!s_self)
      return true;
      
   s_self->CheckDetachThread();
   try {
      s_self->SyncShutdown();
      delete s_self;
      return true;
   } catch (JavaStartException &e) {}
   return false;
}


JavaInterface *JavaInterface::s_self=0;

//self() is moved to javavm-$(VM).c
/*
JavaInterface *JavaInterface::self() {
   if (!s_self)
      s_self=new JavaInterface();
   return s_self;
}
*/

JavaInterface::JavaInterface() {
   s_self=this;
   //we need the methods in a separated struct because they have to be deleted
   //before the VM is shut down, which is done in a function and
   //not in the destructor.
   methods = new StaticMethods;

   jniInitialized = false;
}

JavaInterface::~JavaInterface() {
   s_self=0;
   delete methods;
}

//Calls StartVM and initializes JNI structures to access ApplicationManager
bool JavaInterface::StartJava() {
   StartVM();
   if (!isStarted())
      return false;

   if (jniInitialized)
      return true;

   CheckAttachThread();
   
   char format[50];
   JNI::BaseObject::getSignature(format, JNI::Int, 1, JNI::Long);
   JNI::ReturnType ret;
   if ( !methods->initialize.SetMethod("vdr/mhp/ApplicationManager", "Initialize", format) ||
        !methods->initialize.CallMethod(ret, JNI::Int, (int)&ApplicationInfo::Applications) || ret.TypeInt != 0) {
      esyslog("Failed to initialize Java system: Cannot call method of ApplicationManager");
      jniInitialized=false;
      return false;
   }
   
   //they all have the same signature
   methods->newApplication.SetMethod("vdr/mhp/ApplicationManager", "NewApplication", format);
   methods->applicationRemoved.SetMethod("vdr/mhp/ApplicationManager", "ApplicationRemoved", format);
   methods->startApplication.SetMethod("vdr/mhp/ApplicationManager", "StartApplication", format);
   methods->stopApplication.SetMethod("vdr/mhp/ApplicationManager", "StopApplication", format);
   methods->pauseApplication.SetMethod("vdr/mhp/ApplicationManager", "PauseApplication", format);
   methods->resumeApplication.SetMethod("vdr/mhp/ApplicationManager", "ResumeApplication", format);
   
   JNI::BaseObject::getSignature(format, JNI::Int, 1, JNI::Int);
   methods->processKey.SetMethod("vdr/mhp/ApplicationManager", "ProcessKey", format);
   
   JNI::BaseObject::getSignature(format, JNI::Int, 0);
   methods->shutdown.SetMethod("vdr/mhp/ApplicationManager", "Shutdown", format);
   methods->stopApplications.SetMethod("vdr/mhp/ApplicationManager", "StopApplications", format);

   jniInitialized = true;
   return true;
}

LibraryPreloader::LibraryPreloader() 
 : dlhandle(0)
{
}

LibraryPreloader::~LibraryPreloader() {
   Close();
}

bool LibraryPreloader::Load(const char *library) {
   Close();
   dlhandle=dlopen(library, RTLD_NOW | RTLD_GLOBAL);
   if (!dlhandle) {
      esyslog("MHP: Failed to open dynamic object \"%s\", error message \"%s\".", library, dlerror());
      return false;
   }
   return true;
}

void LibraryPreloader::Close() {
   if (dlhandle) {
      dlclose(dlhandle);
      dlhandle=0;
   }
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
   if (JNIEnvProvider::GetEnv()->ExceptionOccurred()) {
      JNIEnvProvider::GetEnv()->ExceptionDescribe();
      JNIEnvProvider::GetEnv()->ExceptionClear();
      return false;
   }
   return true;
}

JNIEnvProvider *JNIEnvProvider::s_self = 0;

ClassRef::ClassRef() {
   classRef=0;
}

bool ClassRef::SetClass(const char* classname) {
   classRef=JNIEnvProvider::GetEnv()->FindClass(classname);
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
      JNIEnvProvider::GetEnv()->DeleteGlobalRef(classRef);
      classRef=0;
   }
}

bool GlobalClassRef::SetClass(const char* classname) {
   if (!ClassRef::SetClass(classname))
      return false;
   classRef=JNIEnvProvider::GetEnv()->NewGlobalRef(classRef);
   checkException();
   return classRef;
}

bool GlobalClassRef::SetClass(jclass localRef) {
   classRef=JNIEnvProvider::GetEnv()->NewGlobalRef(localRef);
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
      JNIEnvProvider::GetEnv()->DeleteGlobalRef(objectRef);
      objectRef=0;
   }
}

bool GlobalObjectRef::SetObject(jobject localRef) {
   objectRef=JNIEnvProvider::GetEnv()->NewGlobalRef(localRef);
   checkException();
   return objectRef;
}

jclass GlobalObjectRef::GetClass() {
   if (!objectRef)
      return 0;
   jclass clazz=JNIEnvProvider::GetEnv()->GetObjectClass(objectRef);
   checkException();
   return clazz;
}



StaticMethod::StaticMethod() {
   method=0;
}

bool StaticMethod::SetMethod(const char *classname, const char *methodName, const char *signature) {
   if (!classRef.SetClass(classname))
      return false;
   method=JNIEnvProvider::GetEnv()->GetStaticMethodID((jclass)classRef, methodName, signature);
   checkException();
   return method;
}

bool StaticMethod::SetMethod(jclass clazz, const char *methodName, const char *signature) {
   if (!classRef.SetClass(clazz))
      return false;
   method=JNIEnvProvider::GetEnv()->GetStaticMethodID((jclass)classRef, methodName, signature);
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
         JNIEnvProvider::GetEnv()->CallStaticVoidMethodV(classRef, method, ap);
         break;
      case Boolean:
         ret.TypeBoolean=JNIEnvProvider::GetEnv()->CallStaticBooleanMethodV(classRef, method, ap);
         break;
      case Byte:
         ret.TypeByte=JNIEnvProvider::GetEnv()->CallStaticByteMethodV(classRef, method, ap);
         break;
      case Char:
         ret.TypeChar=JNIEnvProvider::GetEnv()->CallStaticCharMethodV(classRef, method, ap);
         break;
      case Short:
         ret.TypeShort=JNIEnvProvider::GetEnv()->CallStaticShortMethodV(classRef, method, ap);
         break;
      case Int:
         ret.TypeInt=JNIEnvProvider::GetEnv()->CallStaticIntMethodV(classRef, method, ap);
         break;
      case Long:
         ret.TypeLong=JNIEnvProvider::GetEnv()->CallStaticLongMethodV(classRef, method, ap);
         break;
      case Double:
         ret.TypeDouble=JNIEnvProvider::GetEnv()->CallStaticDoubleMethodV(classRef, method, ap);
         break;
      case Float:
         ret.TypeFloat=JNIEnvProvider::GetEnv()->CallStaticFloatMethodV(classRef, method, ap);
         break;
      case Object:
      case Array: //in Java, an array is an object
         ret.TypeObject=JNIEnvProvider::GetEnv()->CallStaticObjectMethodV(classRef, method, ap);
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
   method=JNIEnvProvider::GetEnv()->GetMethodID((jclass)classRef, methodName, signature);
   checkException();
   return method;
}

bool InstanceMethod::SetMethod(jclass clazz, const char *methodName, const char *signature) {
   if (!classRef.SetClass(clazz))
      return false;
   method=JNIEnvProvider::GetEnv()->GetMethodID((jclass)classRef, methodName, signature);
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
         JNIEnvProvider::GetEnv()->CallVoidMethodV(object, method, ap);
         break;
      case Boolean:
         ret.TypeBoolean=JNIEnvProvider::GetEnv()->CallBooleanMethodV(object, method, ap);
         break;
      case Byte:
         ret.TypeByte=JNIEnvProvider::GetEnv()->CallByteMethodV(object, method, ap);
         break;
      case Char:
         ret.TypeChar=JNIEnvProvider::GetEnv()->CallCharMethodV(object, method, ap);
         break;
      case Short:
         ret.TypeShort=JNIEnvProvider::GetEnv()->CallShortMethodV(object, method, ap);
         break;
      case Int:
         ret.TypeInt=JNIEnvProvider::GetEnv()->CallIntMethodV(object, method, ap);
         break;
      case Long:
         ret.TypeLong=JNIEnvProvider::GetEnv()->CallLongMethodV(object, method, ap);
         break;
      case Double:
         ret.TypeDouble=JNIEnvProvider::GetEnv()->CallDoubleMethodV(object, method, ap);
         break;
      case Float:
         ret.TypeFloat=JNIEnvProvider::GetEnv()->CallFloatMethodV(object, method, ap);
         break;
      case Object:
      case Array: //in Java, an array is an object
         ret.TypeObject=JNIEnvProvider::GetEnv()->CallObjectMethodV(object, method, ap);
         break;
   }
   va_end(ap);
   return checkException();
}

//inspired by jcl.c from GNU Classpath
bool Exception::Throw(const char *classname, const char *errMsg, ThrowMode mode = ThrowModeTry) {
   checkException();
   ClassRef exc;
   if (!exc.SetClass(classname)) {
      if (mode == ThrowModeConsequent) {
         const char *errExcClass = "java/lang/ClassNotFoundException";
         if (!exc.SetClass(errExcClass)) {
            errExcClass = "java/lang/InternalError";
            if (!exc.SetClass(errExcClass)) {
               fprintf (stderr, "JNI::Exception: Utterly failed to throw exeption ");
               fprintf (stderr, className);
               fprintf (stderr, " with message ");
               fprintf (stderr, errMsg);
               return false;
            }
         }
         JNIEnvProvider::GetEnv()->ThrowNew(className, classname);
      }
      return false;
   }
   return JNIEnvProvider::GetEnv()->ThrowNew(className, errMsg) == 0;
}


}//end of namespace JNI


//Debugging
/*
int main() {
   JavaInterface::StartApplication(0);
   return 0;
}
*/

