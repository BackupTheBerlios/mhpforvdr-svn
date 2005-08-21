/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include <vector>

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
   return self()->methods->startApplication.CallMethod(ret, (void *)new ApplicationInfo::cApplication::Ptr(app)) && ret.TypeInt == 0;
}

bool JavaInterface::StopApplication(ApplicationInfo::cApplication::Ptr app) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->stopApplication.CallMethod(ret, (void *)new ApplicationInfo::cApplication::Ptr(app)) && ret.TypeInt == 0;
}

bool JavaInterface::StopApplications() {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->stopApplications.CallMethod(ret) && ret.TypeInt == 0;
}

bool JavaInterface::PauseApplication(ApplicationInfo::cApplication::Ptr app) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->pauseApplication.CallMethod(ret, (void *)new ApplicationInfo::cApplication::Ptr(app)) && ret.TypeInt == 0;
}

bool JavaInterface::ResumeApplication(ApplicationInfo::cApplication::Ptr app) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->resumeApplication.CallMethod(ret, (void *)new ApplicationInfo::cApplication::Ptr(app)) && ret.TypeInt == 0;
}


bool JavaInterface::NewApplication(ApplicationInfo::cApplication::Ptr app) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->newApplication.CallMethod(ret, (void *)new ApplicationInfo::cApplication::Ptr(app)) && ret.TypeInt == 0;
}

bool JavaInterface::ApplicationRemoved(ApplicationInfo::cApplication::Ptr app) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->applicationRemoved.CallMethod(ret, (void *)new ApplicationInfo::cApplication::Ptr(app)) && ret.TypeInt == 0;
}

bool JavaInterface::ProcessKey(eKeys Key) {
   JNI::ReturnType ret;
   self()->CheckAttachThread();
   return self()->methods->processKey.CallMethod(ret, (int)Key) && ret.TypeInt == 0;
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
   return self()->methods->shutdown.CallMethod(ret) && ret.TypeInt == 0;  
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
   
   const char *format=JNI::BaseObject::getSignature(JNI::Int, 1, JNI::Long);
   JNI::ReturnType ret;
   if ( !methods->initialize.SetMethod("vdr/mhp/ApplicationManager", "Initialize", JNI::Int, format) ||
        !methods->initialize.CallMethod(ret, (int)&ApplicationInfo::Applications) || ret.TypeInt != 0) {
      esyslog("Failed to initialize Java system: Cannot call method of ApplicationManager");
      jniInitialized=false;
      return false;
   }
   
   //they all have the same signature
   methods->newApplication.SetMethod("vdr/mhp/ApplicationManager", "NewApplication", JNI::Int, format);
   methods->applicationRemoved.SetMethod("vdr/mhp/ApplicationManager", "ApplicationRemoved", JNI::Int, format);
   methods->startApplication.SetMethod("vdr/mhp/ApplicationManager", "StartApplication", JNI::Int, format);
   methods->stopApplication.SetMethod("vdr/mhp/ApplicationManager", "StopApplication", JNI::Int, format);
   methods->pauseApplication.SetMethod("vdr/mhp/ApplicationManager", "PauseApplication", JNI::Int, format);
   methods->resumeApplication.SetMethod("vdr/mhp/ApplicationManager", "ResumeApplication", JNI::Int, format);
   delete[] format;
   
   methods->processKey.SetMethodWithArguments("vdr/mhp/ApplicationManager", "ProcessKey", JNI::Int, 1, JNI::Int);
   
   format=JNI::BaseObject::getSignature(JNI::Int, 0);
   methods->shutdown.SetMethod("vdr/mhp/ApplicationManager", "Shutdown", JNI::Int, format);
   methods->stopApplications.SetMethod("vdr/mhp/ApplicationManager", "StopApplications", JNI::Int, format);
   delete[] format;

   JNI::Exception::Initialize();
   
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

BaseObject::BaseObject()
   : exceptionHandling(ClearExceptions)
{
}

BaseObject::~BaseObject() {
}

const char *BaseObject::getSignature(Types returnType, int args, ...) {
   va_list ap;
   va_start(ap, args);
   const char *sig=getSignature(returnType, args, ap);
   va_end(ap);
   return sig;
}

const char *BaseObject::getConstructorSignature(int args, ...) {
   va_list ap;
   va_start(ap, args);
   const char *sig=getSignature(JNI::Void, args, ap);
   va_end(ap);
   return sig;
}

// Attempt at a string optimized for appending
class StringBuffer : public std::vector<char> {
public:
   StringBuffer(int n = 10) : std::vector<char>(n) {}
   StringBuffer &operator+=(char c) {
      push_back(c);
      return *this;
   }
   StringBuffer &operator+=(const char *str) {
      int len=strlen(str);
      for (int i=0; i<len; i++)
         push_back(str[i]);
      return *this;
   }
   char *getCharArray() {
      int len=size();
      char *a=new char[len];
      for (int i=0; i<len; i++)
         a[i]=operator[](i);
      return a;
   }
};

const char *BaseObject::getSignature(Types returnType, int args, va_list ap) {
   StringBuffer format(2*args);
   
   format+='(';
   
   Types t;
   for (int i=0;i<args;i++) {
      t=(Types)va_arg(ap, int);
      if (t != Void) {
         format+=(char)t;
         if (t == JNI::Object || t == Array) {
            const char *c=va_arg(ap, const char *);
            format+=c;
         }
         if (t == JNI::Object)
            format+=';';
      }
   }
   format+=')';
   
   format+=(char)returnType;
   if (returnType == JNI::Object || returnType == Array) {
      const char *c=va_arg(ap, const char *);
      format+=c;
   }
   if (returnType == JNI::Object)
      format+=';';
   
   format+='\0';
   
   return format.getCharArray();
   /*
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
   
   format[index++]=0;
   */
}

bool BaseObject::checkException(ExceptionHandling exceptionHandling) {
   if (JNIEnvProvider::GetEnv()->ExceptionOccurred()) {
      JNIEnvProvider::GetEnv()->ExceptionDescribe();
      if (exceptionHandling==ClearExceptions)
         JNIEnvProvider::GetEnv()->ExceptionClear();
      return false;
   }
   return true;
}

bool BaseObject::checkException() {
   return checkException(exceptionHandling);
}

DeletableObject::~DeletableObject() {
   RemoveForDeletion();
   Delete();
}

void DeletableObject::RegisterForDeletion() {
   ShutdownManager::RegisterForDeletion(this);
}

void DeletableObject::RemoveForDeletion() {
   ShutdownManager::RemoveForDeletion(this);
}

JNIEnvProvider *JNIEnvProvider::s_self = 0;
ShutdownManager *ShutdownManager::s_self = 0;

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
   Delete();
}

void GlobalClassRef::Delete() {
   if (classRef) {
      JNIEnvProvider::GetEnv()->DeleteGlobalRef(classRef);
      classRef=0;
   }
}

bool GlobalClassRef::SetClass(const char* classname) {
   RegisterForDeletion();
   if (!ClassRef::SetClass(classname))
      return false;
   classRef=JNIEnvProvider::GetEnv()->NewGlobalRef(classRef);
   checkException();
   return classRef;
}

bool GlobalClassRef::SetClass(jclass localRef) {
   RegisterForDeletion();
   classRef=JNIEnvProvider::GetEnv()->NewGlobalRef(localRef);
   checkException();
   return classRef;
}

GlobalObjectRef::GlobalObjectRef() {
   objectRef=0;
}

GlobalObjectRef::~GlobalObjectRef() {
   Delete();
}

void GlobalObjectRef::Delete() {
   if (objectRef) {
      JNIEnvProvider::GetEnv()->DeleteGlobalRef(objectRef);
      objectRef=0;
   }
}

bool GlobalObjectRef::SetObject(jobject localRef) {
   RegisterForDeletion();
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




Method::Method()
   : method(0), returnType(JNI::Void)
{
}

void Method::Delete() {
   method=0;
}

bool Method::SetMethodWithArguments(const char *classname, const char *methodName, Types returnType, int numArgs, ...) {
   va_list ap;
   va_start(ap, numArgs);
   bool success=SetMethod(classname, methodName, returnType, ap);
   va_end(ap);
   return success;
}

bool Method::SetMethodWithArguments(jclass clazz, const char *methodName, Types returnType, int numArgs, ...) {
   va_list ap;
   va_start(ap, numArgs);
   bool success=SetMethod(clazz, methodName, returnType, ap);
   va_end(ap);
   return success;
}

bool Method::SetMethodWithArguments(const char *classname, const char *methodName, Types returnType, int numArgs, va_list args) {
   const char *signature=getSignature(returnType, numArgs, args);
   bool success=SetMethod(classname, methodName, returnType, signature);
   delete[] signature;
   return success;
}

bool Method::SetMethodWithArguments(jclass clazz, const char *methodName, Types returnType, int numArgs, va_list args) {
   const char *signature=getSignature(returnType, numArgs, args);
   bool success=SetMethod(clazz, methodName, returnType, signature);
   delete[] signature;
   return success;
}

StaticMethod::StaticMethod() {
}

bool StaticMethod::SetMethod(const char *classname, const char *methodName, Types rt, const char *signature) {
   if (!classRef.SetClass(classname))
      return false;
   returnType=rt;
   method=JNIEnvProvider::GetEnv()->GetStaticMethodID((jclass)classRef, methodName, signature);
   checkException();
   return method;
}

bool StaticMethod::SetMethod(jclass clazz, const char *methodName, Types rt, const char *signature) {
   if (!classRef.SetClass(clazz))
      return false;
   returnType=rt;
   method=JNIEnvProvider::GetEnv()->GetStaticMethodID((jclass)classRef, methodName, signature);
   checkException();
   return method;
}

bool StaticMethod::CallMethod(ReturnType &ret, ...) {
   va_list ap;
   va_start(ap, ret);
   bool success = CallMethod(ret, ap);
   va_end(ap);
   return success;
}

bool StaticMethod::CallMethod(ReturnType &ret, va_list ap) {
   ret.TypeLong=0;
   if (!method)
      return false;

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
   return checkException();
}


InstanceMethod::InstanceMethod() {
}

// The following functions seem to be duplicated from StaticMethod,
// but if you have a closer look, it is not identical.

bool InstanceMethod::SetMethod(const char *classname, const char *methodName, Types rt, const char *signature) {
   if (!classRef.SetClass(classname))
      return false;
   returnType=rt;
   method=JNIEnvProvider::GetEnv()->GetMethodID((jclass)classRef, methodName, signature);
   checkException();
   return method;
}

bool InstanceMethod::SetMethod(jclass clazz, const char *methodName, Types rt, const char *signature) {
   if (!classRef.SetClass(clazz))
      return false;
   returnType=rt;
   method=JNIEnvProvider::GetEnv()->GetMethodID((jclass)classRef, methodName, signature);
   checkException();
   return method;
}

bool InstanceMethod::CallMethod(jobject object, ReturnType &ret, ...) {
   va_list ap;
   va_start(ap, ret);
   bool success = CallMethod(object, ret, ap);
   va_end(ap);
   return success;
}

bool InstanceMethod::CallMethod(jobject object, ReturnType &ret, va_list ap) {
   ret.TypeLong=0;
   if (!method)
      return false;

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
   return checkException();
}


Constructor::Constructor() {
}

bool Constructor::SetConstructorWithArguments(const char *classname, int numArgs, ...) {
   va_list ap;
   va_start(ap, numArgs);
   bool success=InstanceMethod::SetMethodWithArguments(classname, "<init>", JNI::Void, numArgs, ap);
   va_end(ap);
   return success;
}

bool Constructor::SetConstructorWithArguments(jclass clazz, int numArgs, ...) {
   va_list ap;
   va_start(ap, numArgs);
   bool success=InstanceMethod::SetMethodWithArguments(clazz, "<init>", JNI::Void, numArgs, ap);
   va_end(ap);
   return success;
}

bool Constructor::SetConstructor(const char *classname, const char *signature) {
   return InstanceMethod::SetMethod(classname, "<init>", JNI::Void, signature);
}

bool Constructor::SetConstructor(jclass clazz, const char *signature) {
   return InstanceMethod::SetMethod(clazz, "<init>", JNI::Void, signature);
}

bool Constructor::NewObject(jobject &newObj, ...) {
   va_list ap;
   va_start(ap, newObj);
   bool success = NewObject(newObj, ap);
   va_end(ap);
   return success;
}

bool Constructor::NewObject(jobject &newObj, va_list args) {
   if (!method)
      return false;
   newObj=JNIEnvProvider::GetEnv()->NewObjectV(classRef, method, args);
   return checkException();
}

bool Exception::SetClass(const char *classname){
   return classRef.SetClass(classname);
}

bool Exception::Throw(const char *errMsg) {
   if (!classRef)
      return false;
   return JNIEnvProvider::GetEnv()->ThrowNew(classRef, errMsg) == 0;
}

//inspired by jcl.c from GNU Classpath
bool Exception::Throw(const char *classname, const char *errMsg, ThrowMode mode) {
   checkException(ClearExceptions);
   ClassRef exc;
   if (!exc.SetClass(classname)) {
      if (mode == ThrowModeConsequent) {
         checkException(ClearExceptions);
         if (!exc.SetClass("java/lang/ClassNotFoundException")) {
            if (!exc.SetClass("java/lang/InternalError")) {
               checkException(ClearExceptions);
               fprintf (stderr, "JNI::Exception: Utterly failed to throw exeption ");
               fprintf (stderr, classname);
               fprintf (stderr, " with message ");
               fprintf (stderr, errMsg);
               return false;
            }
            JNIEnvProvider::GetEnv()->ThrowNew(exc, "Class java/lang/ClassNotFoundException not found");
         }
         JNIEnvProvider::GetEnv()->ThrowNew(exc, classname);
      }
      return false;
   }
   return JNIEnvProvider::GetEnv()->ThrowNew(exc, errMsg) == 0;
}

Exception Exception::javaLangIllegalArgumentException;
Exception Exception::javaLangIllegalStateException;
Exception Exception::javaLangNullPointerException;
Exception Exception::javaLangRuntimeException;
Exception Exception::javaIoIOException;

bool Exception::Initialize() {
   bool success = true;
   success = success && javaLangIllegalArgumentException.SetClass("java/lang/IllegalArgumentException");
   success = success && javaLangIllegalStateException.SetClass("java/lang/IllegalArgumentException");
   success = success && javaLangNullPointerException.SetClass("java/lang/NullPointerException");
   success = success && javaLangRuntimeException.SetClass("java/lang/RuntimeException");
   success = success && javaIoIOException.SetClass("java/io/IOException");
   return success;
}

bool Exception::Throw(PredefinedException e, const char *errMsg) {
   switch(e) {
      case JavaLangIllegalArgumentException:
         return javaLangIllegalArgumentException.Throw(errMsg);
      case JavaLangIllegalStateException:
         return javaLangIllegalStateException.Throw(errMsg);
      case JavaLangNullPointerException:
         return javaLangNullPointerException.Throw(errMsg);
      case JavaLangRuntimeException:
         return javaLangRuntimeException.Throw(errMsg);
      case JavaIoIOException:
         return javaIoIOException.Throw(errMsg);
      default:
         return false;
   }
}


}//end of namespace JNI


//Debugging
/*
int main() {
   JavaInterface::StartApplication(0);
   return 0;
}
*/

