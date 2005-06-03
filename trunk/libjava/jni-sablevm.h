/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This source file has been released into the public domain by    *
 * the developers of SableVM (http://www.sablevm.org/).            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

#ifndef SVM_JNI_H
#define SVM_JNI_H

#include <stdarg.h>

/* If you'd like to deduce real C++ type definitions for JNI types,
   from the published specification (see hints on page 166 of ISBN
   0-201-32577-2), please feel free to do so and contribute it
   back. */

#ifdef __cplusplus
extern "C"
{
#endif

#include "jni_system_specific.h"

  /* constants */

#define JNI_FALSE 0
#define JNI_TRUE 1

#define JNI_COMMIT 1
#define JNI_ABORT 2

#define JNI_VERSION_1_1 0x00010001
#define JNI_VERSION_1_2 0x00010002
#define JNI_VERSION_1_4 0x00010004

#define JNI_OK 0
#define JNI_ERR (-1)
#define JNI_EDETACHED (-2)
#define JNI_EVERSION  (-3)

  /* _VIRTUAL_MACHINE should only be defined when including this file
     into the source code of a virtual machine.  This enables the
     virtual machine to provide its own "non-opaque" definitions. */
#ifndef _VIRTUAL_MACHINE

  /* opaque types */
#ifdef __cplusplus
  class _jobject {};
  typedef _jobject *jobject;
#else

#endif

  typedef struct _jobject *jobject;
  typedef struct _jfieldID *jfieldID;
  typedef struct _jmethodID *jmethodID;

#endif				/* NOT _VIRTUAL_MACHINE */

  typedef _svmt_u8 jboolean;
  typedef _svmt_s8 jbyte;
  typedef _svmt_u16 jchar;
  typedef _svmt_s16 jshort;
  typedef _svmt_s32 jint;
  typedef _svmt_s64 jlong;
  typedef _svmt_f32 jfloat;
  typedef _svmt_d64 jdouble;

  typedef struct JNINativeInterface_struct JNINativeInterface;
  typedef struct JNIInvokeInterface_struct JNIInvokeInterface;

  /* size type */

  typedef jint jsize;

#ifdef __cpluscplus

  class _jclass : public _jobject {};
  class _jstring : public _jobject {};

#ifndef _VIRTUAL_MACHINE
    
   class _jarray : public _jobject {};

#endif

  class _jthrowable : public _jobject {};

  class _jobjectArray : public _jarray {};
  class _jbooleanArray : public _jarray {};
  class _jbyteArray : public _jarray {};
  class _jcharArray : public _jarray {};
  class _jshortArray : public _jarray {};
  class _jintArray : public _jarray {};
  class _jlongArray : public _jarray {};
  class _jfloatArray : public _jarray {};
  class _jdoubleArray : public _jarray {};

  typedef _jclass *jclass;
  typedef _jstring *jstring;
  typedef _jarray *jarray;
  typedef _jthrowable *jthrowable;
  typedef _jobjectArray *jobjectArray;
  typedef _jbooleanArray *jbooleanArray;
  typedef _jbyteArray *jbyteArray;
  typedef _jcharArray *jcharArray;
  typedef _jshortArray *jshortArray;
  typedef _jintArray *jintArray;
  typedef _jlongArray *jlongArray;
  typedef _jfloatArray *jfloatArray;
  typedef _jdoubleArray *jdoubleArray;

#else /* NOT __cplusplus */
  /* class types */

  typedef jobject jclass;
  typedef jobject jstring;

#ifndef _VIRTUAL_MACHINE

  typedef jobject jarray;

#endif				/* NOT _VIRTUAL_MACHINE */

  typedef jobject jthrowable;
  typedef jobject jweak;

  typedef jarray jobjectArray;
  typedef jarray jbooleanArray;
  typedef jarray jbyteArray;
  typedef jarray jcharArray;
  typedef jarray jshortArray;
  typedef jarray jintArray;
  typedef jarray jlongArray;
  typedef jarray jfloatArray;
  typedef jarray jdoubleArray;

#endif /* __cplusplus */

  typedef jobject jweak;

  /* value type */

  typedef union jvalue_union jvalue;

  union jvalue_union
  {
    jboolean z;
    jbyte b;
    jchar c;
    jshort s;
    jint i;
    jlong j;
    jfloat f;
    jdouble d;
    jobject l;
  };

  /* JNIEnv type */

#ifdef __cplusplus
  struct JNIEnv_;
  typedef JNIEnv_ JNIEnv;
#else
  typedef const JNINativeInterface *JNIEnv;
#endif

  /* JNINativeMethod type */

  typedef struct JNINativeMethod_struct JNINativeMethod;

  struct JNINativeMethod_struct
  {
    char *name;
    char *signature;
    void *fnPtr;
  };

  /* JavaVM type */

#ifdef __cplusplus
  struct JavaVM_;
  typedef JavaVM_ JavaVM;
#else
  typedef const JNIInvokeInterface *JavaVM;
#endif

  /* initialization structures */

  typedef struct JavaVMOption_struct JavaVMOption;

  struct JavaVMOption_struct
  {
    char *optionString;
    void *extraInfo;
  };

  typedef struct JavaVMInitArgs_struct JavaVMInitArgs;

  struct JavaVMInitArgs_struct
  {
    jint version;
    jint nOptions;
    JavaVMOption *options;
    jboolean ignoreUnrecognized;
  };

  typedef struct JavaVMAttachArgs_struct JavaVMAttachArgs;

  struct JavaVMAttachArgs_struct
  {
    jint version;
    char *name;
    jobject group;
  };

  /* JNINativeInterface type */

/* *INDENT-OFF* */
struct JNINativeInterface_struct
{
  void *null_0;
  void *null_1;
  void *null_2;
  void *null_3;
  jint (JNICALL *GetVersion) (JNIEnv *env);	/* 4 */
  jclass (JNICALL *DefineClass) (JNIEnv *env, const char *name,
				  jobject loader, const jbyte *buf, jsize bufLen);	/* 5 */
  jclass (JNICALL *FindClass) (JNIEnv *env, const char *name);	/* 6 */
  jmethodID (JNICALL *FromReflectedMethod) (JNIEnv *env, jobject method);	/* 7 */
  jfieldID (JNICALL *FromReflectedField) (JNIEnv *env, jobject field);	/* 8 */
  jobject (JNICALL *ToReflectedMethod) (JNIEnv *env, jclass cls,
					 jmethodID methodID, jboolean isStatic);	/* 9 */
  jclass (JNICALL *GetSuperclass) (JNIEnv *env, jclass clazz);	/* 10 */
  jboolean (JNICALL *IsAssignableFrom) (JNIEnv *env, jclass clazz1, jclass clazz2);	/* 11 */
  jobject (JNICALL *ToReflectedField) (JNIEnv *env, jclass cls,
					jfieldID fieldID, jboolean isStatic);	/* 12 */
  jint (JNICALL *Throw) (JNIEnv *env, jthrowable obj);	/* 13 */
  jint (JNICALL *ThrowNew) (JNIEnv *env, jclass clazz, const char *message);	/* 14 */
  jthrowable (JNICALL *ExceptionOccurred) (JNIEnv *env);	/* 15 */
  void (JNICALL *ExceptionDescribe) (JNIEnv *env);	/* 16 */
  void (JNICALL *ExceptionClear) (JNIEnv *env);	/* 17 */
  void (JNICALL *FatalError) (JNIEnv *env, const char *msg);	/* 18 */
  jint (JNICALL *PushLocalFrame) (JNIEnv *env, jint capacity);	/* 19 */
  jobject (JNICALL *PopLocalFrame) (JNIEnv *env, jobject result);	/* 20 */
  jobject (JNICALL *NewGlobalRef) (JNIEnv *env, jobject obj);	/* 21 */
  void (JNICALL *DeleteGlobalRef) (JNIEnv *env, jobject gref);	/* 22 */
  void (JNICALL *DeleteLocalRef) (JNIEnv *env, jobject lref);	/* 23 */
  jboolean (JNICALL *IsSameObject) (JNIEnv *env, jobject ref1, jobject ref2);	/* 24 */
  jobject (JNICALL *NewLocalRef) (JNIEnv *env, jobject ref);	/* 25 */
  jint (JNICALL *EnsureLocalCapacity) (JNIEnv *env, jint capacity);	/* 26 */
  jobject (JNICALL *AllocObject) (JNIEnv *env, jclass clazz);	/* 27 */
  jobject (JNICALL *NewObject) (JNIEnv *env, jclass clazz,
				 jmethodID methodID, ...);	/* 28 */
  jobject (JNICALL *NewObjectV) (JNIEnv *env, jclass clazz,
				  jmethodID methodID, va_list args);	/* 29 */
  jobject (JNICALL *NewObjectA) (JNIEnv *env, jclass clazz,
				  jmethodID methodID, jvalue *args);	/* 30 */
  jclass (JNICALL *GetObjectClass) (JNIEnv *env, jobject obj);	/* 31 */
  jboolean (JNICALL *IsInstanceOf) (JNIEnv *env, jobject obj, jclass clazz);	/* 32 */
  jmethodID (JNICALL *GetMethodID) (JNIEnv *env, jclass clazz,
				     const char *name, const char *sig);	/* 33 */
  jobject (JNICALL *CallObjectMethod) (JNIEnv *env, jobject obj,
					jmethodID methodID, ...);	/* 34 */
  jobject (JNICALL *CallObjectMethodV) (JNIEnv *env, jobject obj,
					 jmethodID methodID, va_list args);	/* 35 */
  jobject (JNICALL *CallObjectMethodA) (JNIEnv *env, jobject obj,
					 jmethodID methodID, jvalue *args);	/* 36 */
  jboolean (JNICALL *CallBooleanMethod) (JNIEnv *env, jobject obj,
					  jmethodID methodID, ...);	/* 37 */
  jboolean (JNICALL *CallBooleanMethodV) (JNIEnv *env, jobject obj,
					   jmethodID methodID, va_list args);	/* 38 */
  jboolean (JNICALL *CallBooleanMethodA) (JNIEnv *env, jobject obj,
					   jmethodID methodID, jvalue *args);	/* 39 */
  jbyte (JNICALL *CallByteMethod) (JNIEnv *env, jobject obj,
				    jmethodID methodID, ...);	/* 40 */
  jbyte (JNICALL *CallByteMethodV) (JNIEnv *env, jobject obj,
				     jmethodID methodID, va_list args);	/* 41 */
  jbyte (JNICALL *CallByteMethodA) (JNIEnv *env, jobject obj,
				     jmethodID methodID, jvalue *args);	/* 42 */
  jchar (JNICALL *CallCharMethod) (JNIEnv *env, jobject obj,
				    jmethodID methodID, ...);	/* 43 */
  jchar (JNICALL *CallCharMethodV) (JNIEnv *env, jobject obj,
				     jmethodID methodID, va_list args);	/* 44 */
  jchar (JNICALL *CallCharMethodA) (JNIEnv *env, jobject obj,
				     jmethodID methodID, jvalue *args);	/* 45 */
  jshort (JNICALL *CallShortMethod) (JNIEnv *env, jobject obj,
				      jmethodID methodID, ...);	/* 46 */
  jshort (JNICALL *CallShortMethodV) (JNIEnv *env, jobject obj,
				       jmethodID methodID, va_list args);	/* 47 */
  jshort (JNICALL *CallShortMethodA) (JNIEnv *env, jobject obj,
				       jmethodID methodID, jvalue *args);	/* 48 */
  jint (JNICALL *CallIntMethod) (JNIEnv *env, jobject obj,
				  jmethodID methodID, ...);	/* 49 */
  jint (JNICALL *CallIntMethodV) (JNIEnv *env, jobject obj,
				   jmethodID methodID, va_list args);	/* 50 */
  jint (JNICALL *CallIntMethodA) (JNIEnv *env, jobject obj,
				   jmethodID methodID, jvalue *args);	/* 51 */
  jlong (JNICALL *CallLongMethod) (JNIEnv *env, jobject obj,
				    jmethodID methodID, ...);	/* 52 */
  jlong (JNICALL *CallLongMethodV) (JNIEnv *env, jobject obj,
				     jmethodID methodID, va_list args);	/* 53 */
  jlong (JNICALL *CallLongMethodA) (JNIEnv *env, jobject obj,
				     jmethodID methodID, jvalue *args);	/* 54 */
  jfloat (JNICALL *CallFloatMethod) (JNIEnv *env, jobject obj,
				      jmethodID methodID, ...);	/* 55 */
  jfloat (JNICALL *CallFloatMethodV) (JNIEnv *env, jobject obj,
				       jmethodID methodID, va_list args);	/* 56 */
  jfloat (JNICALL *CallFloatMethodA) (JNIEnv *env, jobject obj,
				       jmethodID methodID, jvalue *args);	/* 57 */
  jdouble (JNICALL *CallDoubleMethod) (JNIEnv *env, jobject obj,
					jmethodID methodID, ...);	/* 58 */
  jdouble (JNICALL *CallDoubleMethodV) (JNIEnv *env, jobject obj,
					 jmethodID methodID, va_list args);	/* 59 */
  jdouble (JNICALL *CallDoubleMethodA) (JNIEnv *env, jobject obj,
					 jmethodID methodID, jvalue *args);	/* 60 */
  void (JNICALL *CallVoidMethod) (JNIEnv *env, jobject obj,
				   jmethodID methodID, ...);	/* 61 */
  void (JNICALL *CallVoidMethodV) (JNIEnv *env, jobject obj,
				    jmethodID methodID, va_list args);	/* 62 */
  void (JNICALL *CallVoidMethodA) (JNIEnv *env, jobject obj,
				    jmethodID methodID, jvalue *args);	/* 63 */
  jobject (JNICALL *CallNonvirtualObjectMethod) (JNIEnv *env, jobject obj,
						  jclass clazz,
						  jmethodID methodID, ...);	/* 64 */
  jobject (JNICALL *CallNonvirtualObjectMethodV) (JNIEnv *env, jobject obj,
						   jclass clazz,
						   jmethodID methodID, va_list args);	/* 65 */
  jobject (JNICALL *CallNonvirtualObjectMethodA) (JNIEnv *env, jobject obj,
						   jclass clazz,
						   jmethodID methodID, jvalue *args);	/* 66 */
  jboolean (JNICALL *CallNonvirtualBooleanMethod) (JNIEnv *env, jobject obj,
						    jclass clazz,
						    jmethodID methodID, ...);	/* 67 */
  jboolean (JNICALL *CallNonvirtualBooleanMethodV) (JNIEnv *env, jobject obj,
						     jclass clazz,
						     jmethodID methodID, va_list args);	/* 68 */
  jboolean (JNICALL *CallNonvirtualBooleanMethodA) (JNIEnv *env, jobject obj,
						     jclass clazz,
						     jmethodID methodID, jvalue *args);	/* 69 */
  jbyte (JNICALL *CallNonvirtualByteMethod) (JNIEnv *env, jobject obj,
					      jclass clazz,
					      jmethodID methodID, ...);	/* 70 */
  jbyte (JNICALL *CallNonvirtualByteMethodV) (JNIEnv *env, jobject obj,
					       jclass clazz,
					       jmethodID methodID, va_list args);	/* 71 */
  jbyte (JNICALL *CallNonvirtualByteMethodA) (JNIEnv *env, jobject obj,
					       jclass clazz,
					       jmethodID methodID, jvalue *args);	/* 72 */
  jchar (JNICALL *CallNonvirtualCharMethod) (JNIEnv *env, jobject obj,
					      jclass clazz,
					      jmethodID methodID, ...);	/* 73 */
  jchar (JNICALL *CallNonvirtualCharMethodV) (JNIEnv *env, jobject obj,
					       jclass clazz,
					       jmethodID methodID, va_list args);	/* 74 */
  jchar (JNICALL *CallNonvirtualCharMethodA) (JNIEnv *env, jobject obj,
					       jclass clazz,
					       jmethodID methodID, jvalue *args);	/* 75 */
  jshort (JNICALL *CallNonvirtualShortMethod) (JNIEnv *env, jobject obj,
						jclass clazz,
						jmethodID methodID, ...);	/* 76 */
  jshort (JNICALL *CallNonvirtualShortMethodV) (JNIEnv *env, jobject obj,
						 jclass clazz,
						 jmethodID methodID, va_list args);	/* 77 */
  jshort (JNICALL *CallNonvirtualShortMethodA) (JNIEnv *env, jobject obj,
						 jclass clazz,
						 jmethodID methodID, jvalue *args);	/* 78 */
  jint (JNICALL *CallNonvirtualIntMethod) (JNIEnv *env, jobject obj,
					    jclass clazz, jmethodID methodID, ...);	/* 79 */
  jint (JNICALL *CallNonvirtualIntMethodV) (JNIEnv *env, jobject obj,
					     jclass clazz, jmethodID methodID,
					     va_list args);	/* 80 */
  jint (JNICALL *CallNonvirtualIntMethodA) (JNIEnv *env, jobject obj,
					     jclass clazz, jmethodID methodID,
					     jvalue *args);	/* 81 */
  jlong (JNICALL *CallNonvirtualLongMethod) (JNIEnv *env, jobject obj,
					      jclass clazz,
					      jmethodID methodID, ...);	/* 82 */
  jlong (JNICALL *CallNonvirtualLongMethodV) (JNIEnv *env, jobject obj,
					       jclass clazz,
					       jmethodID methodID, va_list args);	/* 83 */
  jlong (JNICALL *CallNonvirtualLongMethodA) (JNIEnv *env, jobject obj,
					       jclass clazz,
					       jmethodID methodID, jvalue *args);	/* 84 */
  jfloat (JNICALL *CallNonvirtualFloatMethod) (JNIEnv *env, jobject obj,
						jclass clazz,
						jmethodID methodID, ...);	/* 85 */
  jfloat (JNICALL *CallNonvirtualFloatMethodV) (JNIEnv *env, jobject obj,
						 jclass clazz,
						 jmethodID methodID, va_list args);	/* 86 */
  jfloat (JNICALL *CallNonvirtualFloatMethodA) (JNIEnv *env, jobject obj,
						 jclass clazz,
						 jmethodID methodID, jvalue *args);	/* 87 */
  jdouble (JNICALL *CallNonvirtualDoubleMethod) (JNIEnv *env, jobject obj,
						  jclass clazz,
						  jmethodID methodID, ...);	/* 88 */
  jdouble (JNICALL *CallNonvirtualDoubleMethodV) (JNIEnv *env, jobject obj,
						   jclass clazz,
						   jmethodID methodID, va_list args);	/* 89 */
  jdouble (JNICALL *CallNonvirtualDoubleMethodA) (JNIEnv *env, jobject obj,
						   jclass clazz,
						   jmethodID methodID, jvalue *args);	/* 90 */
  void (JNICALL *CallNonvirtualVoidMethod) (JNIEnv *env, jobject obj,
					     jclass clazz, jmethodID methodID, ...);	/* 91 */
  void (JNICALL *CallNonvirtualVoidMethodV) (JNIEnv *env, jobject obj,
					      jclass clazz,
					      jmethodID methodID, va_list args);	/* 92 */
  void (JNICALL *CallNonvirtualVoidMethodA) (JNIEnv *env, jobject obj,
					      jclass clazz,
					      jmethodID methodID, jvalue *args);	/* 93 */
  jfieldID (JNICALL *GetFieldID) (JNIEnv *env, jclass clazz,
				   const char *name, const char *sig);	/* 94 */
  jobject (JNICALL *GetObjectField) (JNIEnv *env, jobject obj, jfieldID fieldID);	/* 95 */
  jboolean (JNICALL *GetBooleanField) (JNIEnv *env, jobject obj, jfieldID fieldID);	/* 96 */
  jbyte (JNICALL *GetByteField) (JNIEnv *env, jobject obj, jfieldID fieldID);	/* 97 */
  jchar (JNICALL *GetCharField) (JNIEnv *env, jobject obj, jfieldID fieldID);	/* 98 */
  jshort (JNICALL *GetShortField) (JNIEnv *env, jobject obj, jfieldID fieldID);	/* 99 */
  jint (JNICALL *GetIntField) (JNIEnv *env, jobject obj, jfieldID fieldID);	/* 100 */
  jlong (JNICALL *GetLongField) (JNIEnv *env, jobject obj, jfieldID fieldID);	/* 101 */
  jfloat (JNICALL *GetFloatField) (JNIEnv *env, jobject obj, jfieldID fieldID);	/* 102 */
  jdouble (JNICALL *GetDoubleField) (JNIEnv *env, jobject obj, jfieldID fieldID);	/* 103 */
  void (JNICALL *SetObjectField) (JNIEnv *env, jobject obj, jfieldID fieldID,
				   jobject value);	/* 104 */
  void (JNICALL *SetBooleanField) (JNIEnv *env, jobject obj,
				    jfieldID fieldID, jboolean value);	/* 105 */
  void (JNICALL *SetByteField) (JNIEnv *env, jobject obj, jfieldID fieldID,
				 jbyte value);	/* 106 */
  void (JNICALL *SetCharField) (JNIEnv *env, jobject obj, jfieldID fieldID,
				 jchar value);	/* 107 */
  void (JNICALL *SetShortField) (JNIEnv *env, jobject obj, jfieldID fieldID,
				  jshort value);	/* 108 */
  void (JNICALL *SetIntField) (JNIEnv *env, jobject obj, jfieldID fieldID,
				jint value);	/* 109 */
  void (JNICALL *SetLongField) (JNIEnv *env, jobject obj, jfieldID fieldID,
				 jlong value);	/* 110 */
  void (JNICALL *SetFloatField) (JNIEnv *env, jobject obj, jfieldID fieldID,
				  jfloat value);	/* 111 */
  void (JNICALL *SetDoubleField) (JNIEnv *env, jobject obj, jfieldID fieldID,
				   jdouble value);	/* 112 */
  jmethodID (JNICALL *GetStaticMethodID) (JNIEnv *env, jclass clazz,
					   const char *name, const char *sig);	/* 113 */
  jobject (JNICALL *CallStaticObjectMethod) (JNIEnv *env, jclass clazz,
					      jmethodID methodID, ...);	/* 114 */
  jobject (JNICALL *CallStaticObjectMethodV) (JNIEnv *env, jclass clazz,
					       jmethodID methodID, va_list args);	/* 115 */
  jobject (JNICALL *CallStaticObjectMethodA) (JNIEnv *env, jclass clazz,
					       jmethodID methodID, jvalue *args);	/* 116 */
  jboolean (JNICALL *CallStaticBooleanMethod) (JNIEnv *env, jclass clazz,
						jmethodID methodID, ...);	/* 117 */
  jboolean (JNICALL *CallStaticBooleanMethodV) (JNIEnv *env, jclass clazz,
						 jmethodID methodID, va_list args);	/* 118 */
  jboolean (JNICALL *CallStaticBooleanMethodA) (JNIEnv *env, jclass clazz,
						 jmethodID methodID, jvalue *args);	/* 119 */
  jbyte (JNICALL *CallStaticByteMethod) (JNIEnv *env, jclass clazz,
					  jmethodID methodID, ...);	/* 120 */
  jbyte (JNICALL *CallStaticByteMethodV) (JNIEnv *env, jclass clazz,
					   jmethodID methodID, va_list args);	/* 121 */
  jbyte (JNICALL *CallStaticByteMethodA) (JNIEnv *env, jclass clazz,
					   jmethodID methodID, jvalue *args);	/* 122 */
  jchar (JNICALL *CallStaticCharMethod) (JNIEnv *env, jclass clazz,
					  jmethodID methodID, ...);	/* 123 */
  jchar (JNICALL *CallStaticCharMethodV) (JNIEnv *env, jclass clazz,
					   jmethodID methodID, va_list args);	/* 124 */
  jchar (JNICALL *CallStaticCharMethodA) (JNIEnv *env, jclass clazz,
					   jmethodID methodID, jvalue *args);	/* 125 */
  jshort (JNICALL *CallStaticShortMethod) (JNIEnv *env, jclass clazz,
					    jmethodID methodID, ...);	/* 126 */
  jshort (JNICALL *CallStaticShortMethodV) (JNIEnv *env, jclass clazz,
					     jmethodID methodID, va_list args);	/* 127 */
  jshort (JNICALL *CallStaticShortMethodA) (JNIEnv *env, jclass clazz,
					     jmethodID methodID, jvalue *args);	/* 128 */
  jint (JNICALL *CallStaticIntMethod) (JNIEnv *env, jclass clazz,
					jmethodID methodID, ...);	/* 129 */
  jint (JNICALL *CallStaticIntMethodV) (JNIEnv *env, jclass clazz,
					 jmethodID methodID, va_list args);	/* 130 */
  jint (JNICALL *CallStaticIntMethodA) (JNIEnv *env, jclass clazz,
					 jmethodID methodID, jvalue *args);	/* 131 */
  jlong (JNICALL *CallStaticLongMethod) (JNIEnv *env, jclass clazz,
					  jmethodID methodID, ...);	/* 132 */
  jlong (JNICALL *CallStaticLongMethodV) (JNIEnv *env, jclass clazz,
					   jmethodID methodID, va_list args);	/* 133 */
  jlong (JNICALL *CallStaticLongMethodA) (JNIEnv *env, jclass clazz,
					   jmethodID methodID, jvalue *args);	/* 134 */
  jfloat (JNICALL *CallStaticFloatMethod) (JNIEnv *env, jclass clazz,
					    jmethodID methodID, ...);	/* 135 */
  jfloat (JNICALL *CallStaticFloatMethodV) (JNIEnv *env, jclass clazz,
					     jmethodID methodID, va_list args);	/* 136 */
  jfloat (JNICALL *CallStaticFloatMethodA) (JNIEnv *env, jclass clazz,
					     jmethodID methodID, jvalue *args);	/* 137 */
  jdouble (JNICALL *CallStaticDoubleMethod) (JNIEnv *env, jclass clazz,
					      jmethodID methodID, ...);	/* 138 */
  jdouble (JNICALL *CallStaticDoubleMethodV) (JNIEnv *env, jclass clazz,
					       jmethodID methodID, va_list args);	/* 139 */
  jdouble (JNICALL *CallStaticDoubleMethodA) (JNIEnv *env, jclass clazz,
					       jmethodID methodID, jvalue *args);	/* 140 */
  void (JNICALL *CallStaticVoidMethod) (JNIEnv *env, jclass clazz,
					 jmethodID methodID, ...);	/* 141 */
  void (JNICALL *CallStaticVoidMethodV) (JNIEnv *env, jclass clazz,
					  jmethodID methodID, va_list args);	/* 142 */
  void (JNICALL *CallStaticVoidMethodA) (JNIEnv *env, jclass clazz,
					  jmethodID methodID, jvalue *args);	/* 143 */
  jfieldID (JNICALL *GetStaticFieldID) (JNIEnv *env, jclass clazz,
					 const char *name, const char *sig);	/* 144 */
  jobject (JNICALL *GetStaticObjectField) (JNIEnv *env, jclass clazz,
					    jfieldID fieldID);	/* 145 */
  jboolean (JNICALL *GetStaticBooleanField) (JNIEnv *env, jclass clazz,
					      jfieldID fieldID);	/* 146 */
  jbyte (JNICALL *GetStaticByteField) (JNIEnv *env, jclass clazz, jfieldID fieldID);	/* 147 */
  jchar (JNICALL *GetStaticCharField) (JNIEnv *env, jclass clazz, jfieldID fieldID);	/* 148 */
  jshort (JNICALL *GetStaticShortField) (JNIEnv *env, jclass clazz, jfieldID fieldID);	/* 149 */
  jint (JNICALL *GetStaticIntField) (JNIEnv *env, jclass clazz, jfieldID fieldID);	/* 150 */
  jlong (JNICALL *GetStaticLongField) (JNIEnv *env, jclass clazz, jfieldID fieldID);	/* 151 */
  jfloat (JNICALL *GetStaticFloatField) (JNIEnv *env, jclass clazz, jfieldID fieldID);	/* 152 */
  jdouble (JNICALL *GetStaticDoubleField) (JNIEnv *env, jclass clazz,
					    jfieldID fieldID);	/* 153 */
  void (JNICALL *SetStaticObjectField) (JNIEnv *env, jclass clazz,
					 jfieldID fieldID, jobject value);	/* 154 */
  void (JNICALL *SetStaticBooleanField) (JNIEnv *env, jclass clazz,
					  jfieldID fieldID, jboolean value);	/* 155 */
  void (JNICALL *SetStaticByteField) (JNIEnv *env, jclass clazz,
				       jfieldID fieldID, jbyte value);	/* 156 */
  void (JNICALL *SetStaticCharField) (JNIEnv *env, jclass clazz,
				       jfieldID fieldID, jchar value);	/* 157 */
  void (JNICALL *SetStaticShortField) (JNIEnv *env, jclass clazz,
					jfieldID fieldID, jshort value);	/* 158 */
  void (JNICALL *SetStaticIntField) (JNIEnv *env, jclass clazz,
				      jfieldID fieldID, jint value);	/* 159 */
  void (JNICALL *SetStaticLongField) (JNIEnv *env, jclass clazz,
				       jfieldID fieldID, jlong value);	/* 160 */
  void (JNICALL *SetStaticFloatField) (JNIEnv *env, jclass clazz,
					jfieldID fieldID, jfloat value);	/* 161 */
  void (JNICALL *SetStaticDoubleField) (JNIEnv *env, jclass clazz,
					 jfieldID fieldID, jdouble value);	/* 162 */
  jstring (JNICALL *NewString) (JNIEnv *env, const jchar *uChars, jsize len);	/* 163 */
  jsize (JNICALL *GetStringLength) (JNIEnv *env, jstring string);	/* 164 */
  const jchar *(JNICALL *GetStringChars) (JNIEnv *env, jstring string,
					   jboolean *isCopy);	/* 165 */
  void (JNICALL *ReleaseStringChars) (JNIEnv *env, jstring string,
				       const jchar *chars);	/* 166 */
  jstring (JNICALL *NewStringUTF) (JNIEnv *env, const char *bytes);	/* 167 */
  jsize (JNICALL *GetStringUTFLength) (JNIEnv *env, jstring string);	/* 168 */
  const jbyte *(JNICALL *GetStringUTFChars) (JNIEnv *env, jstring string,
					      jboolean *isCopy);	/* 169 */
  void (JNICALL *ReleaseStringUTFChars) (JNIEnv *env, jstring string,
					  const char *utf);	/* 170 */
  jsize (JNICALL *GetArrayLength) (JNIEnv *env, jarray array);	/* 171 */
  jarray (JNICALL *NewObjectArray) (JNIEnv *env, jsize length,
				     jclass elementType, jobject initialElement);	/* 172 */
  jobject (JNICALL *GetObjectArrayElement) (JNIEnv *env, jobjectArray array,
					    jsize indx);	/* 173 */
  void (JNICALL *SetObjectArrayElement) (JNIEnv *env, jobjectArray array,
					 jsize indx, jobject value);	/* 174 */
  jbooleanArray (JNICALL *NewBooleanArray) (JNIEnv *env, jsize length);	/* 175 */
  jbyteArray (JNICALL *NewByteArray) (JNIEnv *env, jsize length);	/* 176 */
  jcharArray (JNICALL *NewCharArray) (JNIEnv *env, jsize length);	/* 177 */
  jshortArray (JNICALL *NewShortArray) (JNIEnv *env, jsize length);	/* 178 */
  jintArray (JNICALL *NewIntArray) (JNIEnv *env, jsize length);	/* 179 */
  jlongArray (JNICALL *NewLongArray) (JNIEnv *env, jsize length);	/* 180 */
  jfloatArray (JNICALL *NewFloatArray) (JNIEnv *env, jsize length);	/* 181 */
  jdoubleArray (JNICALL *NewDoubleArray) (JNIEnv *env, jsize length);	/* 182 */
  jboolean *(JNICALL *GetBooleanArrayElements) (JNIEnv *env,
						jbooleanArray array, jboolean *isCopy);	/* 183 */
  jbyte *(JNICALL *GetByteArrayElements) (JNIEnv *env, jbyteArray array,
					  jboolean *isCopy);	/* 184 */
  jchar *(JNICALL *GetCharArrayElements) (JNIEnv *env, jcharArray array,
					  jboolean *isCopy);	/* 185 */
  jshort *(JNICALL *GetShortArrayElements) (JNIEnv *env, jshortArray array,
					    jboolean *isCopy);	/* 186 */
  jint *(JNICALL *GetIntArrayElements) (JNIEnv *env, jintArray array, jboolean *isCopy);	/* 187 */
  jlong *(JNICALL *GetLongArrayElements) (JNIEnv *env, jlongArray array,
					  jboolean *isCopy);	/* 188 */
  jfloat *(JNICALL *GetFloatArrayElements) (JNIEnv *env, jfloatArray array,
					    jboolean *isCopy);	/* 189 */
  jdouble *(JNICALL *GetDoubleArrayElements) (JNIEnv *env, jdoubleArray array,
					      jboolean *isCopy);	/* 190 */
  void (JNICALL *ReleaseBooleanArrayElements) (JNIEnv *env,
						jbooleanArray array,
						jboolean *elems, jint mode);	/* 191 */
  void (JNICALL *ReleaseByteArrayElements) (JNIEnv *env, jbyteArray array,
					     jbyte *elems, jint mode);	/* 192 */
  void (JNICALL *ReleaseCharArrayElements) (JNIEnv *env, jcharArray array,
					     jchar *elems, jint mode);	/* 193 */
  void (JNICALL *ReleaseShortArrayElements) (JNIEnv *env, jshortArray array,
					      jshort *elems, jint mode);	/* 194 */
  void (JNICALL *ReleaseIntArrayElements) (JNIEnv *env, jintArray array,
					    jint *elems, jint mode);	/* 195 */
  void (JNICALL *ReleaseLongArrayElements) (JNIEnv *env, jlongArray array,
					     jlong *elems, jint mode);	/* 196 */
  void (JNICALL *ReleaseFloatArrayElements) (JNIEnv *env, jfloatArray array,
					      jfloat *elems, jint mode);	/* 197 */
  void (JNICALL *ReleaseDoubleArrayElements) (JNIEnv *env,
					       jdoubleArray array,
					       jdouble *elems, jint mode);	/* 198 */
  void (JNICALL *GetBooleanArrayRegion) (JNIEnv *env, jbooleanArray array,
					  jsize start, jsize len, jboolean *buf);	/* 199 */
  void (JNICALL *GetByteArrayRegion) (JNIEnv *env, jbyteArray array,
				       jsize start, jsize len, jbyte *buf);	/* 200 */
  void (JNICALL *GetCharArrayRegion) (JNIEnv *env, jcharArray array,
				       jsize start, jsize len, jchar *buf);	/* 201 */
  void (JNICALL *GetShortArrayRegion) (JNIEnv *env, jshortArray array,
					jsize start, jsize len, jshort *buf);	/* 202 */
  void (JNICALL *GetIntArrayRegion) (JNIEnv *env, jintArray array,
				      jsize start, jsize len, jint *buf);	/* 203 */
  void (JNICALL *GetLongArrayRegion) (JNIEnv *env, jlongArray array,
				       jsize start, jsize len, jlong *buf);	/* 204 */
  void (JNICALL *GetFloatArrayRegion) (JNIEnv *env, jfloatArray array,
					jsize start, jsize len, jfloat *buf);	/* 205 */
  void (JNICALL *GetDoubleArrayRegion) (JNIEnv *env, jdoubleArray array,
					 jsize start, jsize len, jdouble *buf);	/* 206 */
  void (JNICALL *SetBooleanArrayRegion) (JNIEnv *env, jbooleanArray array,
					  jsize start, jsize len, jboolean *buf);	/* 207 */
  void (JNICALL *SetByteArrayRegion) (JNIEnv *env, jbyteArray array,
				       jsize start, jsize len, jbyte *buf);	/* 208 */
  void (JNICALL *SetCharArrayRegion) (JNIEnv *env, jcharArray array,
				       jsize start, jsize len, jchar *buf);	/* 209 */
  void (JNICALL *SetShortArrayRegion) (JNIEnv *env, jshortArray array,
					jsize start, jsize len, jshort *buf);	/* 210 */
  void (JNICALL *SetIntArrayRegion) (JNIEnv *env, jintArray array,
				      jsize start, jsize len, jint *buf);	/* 211 */
  void (JNICALL *SetLongArrayRegion) (JNIEnv *env, jlongArray array,
				       jsize start, jsize len, jlong *buf);	/* 212 */
  void (JNICALL *SetFloatArrayRegion) (JNIEnv *env, jfloatArray array,
					jsize start, jsize len, jfloat *buf);	/* 213 */
  void (JNICALL *SetDoubleArrayRegion) (JNIEnv *env, jdoubleArray array,
					 jsize start, jsize len, jdouble *buf);	/* 214 */
  jint (JNICALL *RegisterNatives) (JNIEnv *env, jclass clazz,
				    const JNINativeMethod *methods, jint nMethods);	/* 215 */
  jint (JNICALL *UnregisterNatives) (JNIEnv *env, jclass clazz);	/* 216 */
  jint (JNICALL *MonitorEnter) (JNIEnv *env, jobject obj);	/* 217 */
  jint (JNICALL *MonitorExit) (JNIEnv *env, jobject obj);	/* 218 */
  jint (JNICALL *GetJavaVM) (JNIEnv *env, JavaVM **vm);	/* 219 */
  void (JNICALL *GetStringRegion) (JNIEnv *env, jstring str, jsize start,
				    jsize len, jchar *buf);	/* 220 */
  void (JNICALL *GetStringUTFRegion) (JNIEnv *env, jstring str, jsize start,
				       jsize len, char *buf);	/* 221 */
  void *(JNICALL *GetPrimitiveArrayCritical) (JNIEnv *env, jarray array,
					       jboolean *isCopy);	/* 222 */
  void (JNICALL *ReleasePrimitiveArrayCritical) (JNIEnv *env, jarray array,
						  void *carray, jint mode);	/* 223 */
  const jchar *(JNICALL *GetStringCritical) (JNIEnv *env, jstring string,
					      jboolean *isCopy);	/* 224 */
  void (JNICALL *ReleaseStringCritical) (JNIEnv *env, jstring string,
					  const jchar *carray);	/* 225 */
  jweak (JNICALL *NewWeakGlobalRef) (JNIEnv *env, jobject obj);	/* 226 */
  void (JNICALL *DeleteWeakGlobalRef) (JNIEnv *env, jweak wref);	/* 227 */
  jboolean (JNICALL *ExceptionCheck) (JNIEnv *env);	/* 228 */
  jobject (JNICALL *NewDirectByteBuffer) (JNIEnv* env, void* address, jlong capacity); /* 229 */
  void* (JNICALL *GetDirectBufferAddress) (JNIEnv* env, jobject buf); /* 230 */
  jlong (JNICALL *GetDirectBufferCapacity) (JNIEnv* env, jobject buf); /* 231 */
};

#ifdef __cplusplus
struct JNIEnv_
{
  const JNINativeInterface * functions;

  jint GetVersion() 
  {
    return functions->GetVersion(this);
  }

  jclass DefineClass(const char *name, jobject loader, const jbyte *buf, jsize bufLen) 
  {
    return functions->DefineClass(this, name, loader, buf, bufLen);
  }

  jclass FindClass(const char *name) 
  {
    return functions->FindClass(this, name);
  }

  jmethodID FromReflectedMethod(jobject method) 
  {
    return functions->FromReflectedMethod(this, method);
  }

  jfieldID FromReflectedField(jobject field) 
  {
    return functions->FromReflectedField(this, field);
  }

  jobject ToReflectedMethod(jclass cls, jmethodID methodID, jboolean isStatic) 
  {
    return functions->ToReflectedMethod(this, cls, methodID, isStatic);
  }

  jclass GetSuperclass(jclass clazz) 
  {
    return functions->GetSuperclass(this, clazz);
  }

  jboolean IsAssignableFrom(jclass clazz1, jclass clazz2) 
  {
    return functions->IsAssignableFrom(this, clazz1, clazz2);
  }

  jobject ToReflectedField(jclass cls, jfieldID fieldID, jboolean isStatic) 
  {
    return functions->ToReflectedField(this, cls, fieldID, isStatic);
  }

  jint Throw(jthrowable obj) 
  {
    return functions->Throw(this, obj);
  }

  jint ThrowNew(jclass clazz, const char *message) 
  {
    return functions->ThrowNew(this, clazz, message);
  }

  jthrowable ExceptionOccurred() 
  {    
    return functions->ExceptionOccurred(this);
  }

  void ExceptionDescribe() 
  {    
    functions->ExceptionDescribe(this);
  }

  void ExceptionClear() 
  {    
    functions->ExceptionClear(this);
  }

  void FatalError(const char *msg) 
  {
    functions->FatalError(this, msg);
  }

  jint PushLocalFrame(jint capacity) 
  {
    return functions->PushLocalFrame(this, capacity);
  }

  jobject PopLocalFrame(jobject result) 
  {
    return functions->PopLocalFrame(this, result);
  }

  jobject NewGlobalRef(jobject obj) 
  {
    return functions->NewGlobalRef(this, obj);
  }

  void DeleteGlobalRef(jobject gref) 
  {
    functions->DeleteGlobalRef(this, gref);
  }

  void DeleteLocalRef(jobject lref) 
  {
    functions->DeleteLocalRef(this, lref);
  }

  jboolean IsSameObject(jobject ref1, jobject ref2) 
  {
    return functions->IsSameObject(this, ref1, ref2);
  }

  jobject NewLocalRef(jobject ref) 
  {
    return functions->NewLocalRef(this, ref);
  }

  jint EnsureLocalCapacity(jint capacity) 
  {
    return functions->EnsureLocalCapacity(this,capacity);
  }

  jobject AllocObject(jclass clazz) 
  {
    return functions->AllocObject(this, clazz);
  }

  jobject NewObject(jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jobject result;
    va_start(args, methodID);
    result = functions->NewObjectV(this,clazz,methodID,args);
    va_end(args);
    return result;
  }

  jobject NewObjectV(jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->NewObjectV(this, clazz, methodID, args);
  }

  jobject NewObjectA(jclass clazz, jmethodID methodID, jvalue *args) 
  {
    return functions->NewObjectA(this, clazz, methodID, args);
  }

  jclass GetObjectClass(jobject obj) 
  {
    return functions->GetObjectClass(this, obj);
  }

  jboolean IsInstanceOf(jobject obj, jclass clazz) 
  {
    return functions->IsInstanceOf(this, obj, clazz);
  }

  jmethodID GetMethodID(jclass clazz, const char *name, const char *sig) 
  {
    return functions->GetMethodID(this, clazz, name, sig);
  }

  jobject CallObjectMethod(jobject obj, jmethodID methodID, ...) 
  {
    va_list args;
    jobject result;
    va_start(args,methodID);
    result = functions->CallObjectMethodV(this, obj, methodID, args);
    va_end(args);
    return result;
  }

  jobject CallObjectMethodV(jobject obj, jmethodID methodID, va_list args) 
  {
    return functions->CallObjectMethodV(this, obj, methodID, args);
  }

  jobject CallObjectMethodA(jobject obj, jmethodID methodID, jvalue * args) 
  {
    return functions->CallObjectMethodA(this, obj, methodID, args);
  }

  jboolean CallBooleanMethod(jobject obj, jmethodID methodID, ...) 
  {
    va_list args;
    jboolean result;
    va_start(args,methodID);
    result = functions->CallBooleanMethodV(this,obj,methodID,args);
    va_end(args);
    return result;
  }

  jboolean CallBooleanMethodV(jobject obj, jmethodID methodID, va_list args) 
  {
    return functions->CallBooleanMethodV(this, obj, methodID, args);
  }

  jboolean CallBooleanMethodA(jobject obj, jmethodID methodID, jvalue * args) 
  {
    return functions->CallBooleanMethodA(this, obj, methodID, args);
  }

  jbyte CallByteMethod(jobject obj, jmethodID methodID, ...) 
  {
    va_list args;
    jbyte result;
    va_start(args,methodID);
    result = functions->CallByteMethodV(this, obj, methodID, args);
    va_end(args);
    return result;
  }

  jbyte CallByteMethodV(jobject obj, jmethodID methodID, va_list args) 
  {
    return functions->CallByteMethodV(this, obj, methodID, args);
  }

  jbyte CallByteMethodA(jobject obj, jmethodID methodID, jvalue * args) 
  {
    return functions->CallByteMethodA(this,obj,methodID,args);
  }

  jchar CallCharMethod(jobject obj, jmethodID methodID, ...) 
  {
    va_list args;
    jchar result;
    va_start(args,methodID);
    result = functions->CallCharMethodV(this, obj, methodID, args);
    va_end(args);
    return result;
  }

  jchar CallCharMethodV(jobject obj, jmethodID methodID, va_list args) 
  {
    return functions->CallCharMethodV(this, obj, methodID, args);
  }

  jchar CallCharMethodA(jobject obj, jmethodID methodID, jvalue * args) 
  {
    return functions->CallCharMethodA(this, obj, methodID, args);
  }

  jshort CallShortMethod(jobject obj, jmethodID methodID, ...) 
  {
    va_list args;
    jshort result;
    va_start(args,methodID);
    result = functions->CallShortMethodV(this, obj, methodID, args);
    va_end(args);
    return result;
  }

  jshort CallShortMethodV(jobject obj, jmethodID methodID, va_list args) 
  {
    return functions->CallShortMethodV(this, obj, methodID, args);
  }

  jshort CallShortMethodA(jobject obj, jmethodID methodID, jvalue * args) 
  {
    return functions->CallShortMethodA(this, obj, methodID, args);
  }

  jint CallIntMethod(jobject obj, jmethodID methodID, ...) 
  {
    va_list args;
    jint result;
    va_start(args,methodID);
    result = functions->CallIntMethodV(this, obj, methodID, args);
    va_end(args);
    return result;
  }

  jint CallIntMethodV(jobject obj, jmethodID methodID, va_list args) 
  {
    return functions->CallIntMethodV(this, obj, methodID, args);
  }

  jint CallIntMethodA(jobject obj, jmethodID methodID, jvalue * args) 
  {
    return functions->CallIntMethodA(this, obj, methodID, args);
  }

  jlong CallLongMethod(jobject obj, jmethodID methodID, ...) 
  {
    va_list args;
    jlong result;
    va_start(args,methodID);
    result = functions->CallLongMethodV(this,obj,methodID,args);
    va_end(args);
    return result;
  }

  jlong CallLongMethodV(jobject obj, jmethodID methodID, va_list args) 
  {
      return functions->CallLongMethodV(this, obj, methodID, args);
  }

  jlong CallLongMethodA(jobject obj, jmethodID methodID, jvalue * args) 
  {
    return functions->CallLongMethodA(this, obj, methodID, args);
  }

  jfloat CallFloatMethod(jobject obj, jmethodID methodID, ...) 
  {
    va_list args;
    float result;
    va_start(args,methodID);
    result = functions->CallFloatMethodV(this, obj, methodID, args);
    va_end(args);
    return result;
  }

  jfloat CallFloatMethodV(jobject obj, jmethodID methodID, va_list args) 
  {
    return functions->CallFloatMethodV(this, obj, methodID, args);
  }

  jfloat CallFloatMethodA(jobject obj, jmethodID methodID, jvalue * args) 
  {
    return functions->CallFloatMethodA(this,obj,methodID,args);
  }

  jdouble CallDoubleMethod(jobject obj, jmethodID methodID, ...) 
  {
    va_list args;
    jdouble result;
    va_start(args,methodID);
    result = functions->CallDoubleMethodV(this, obj, methodID, args);
    va_end(args);
    return result;
  }

  jdouble CallDoubleMethodV(jobject obj, jmethodID methodID, va_list args) 
  {
    return functions->CallDoubleMethodV(this, obj, methodID, args);
  }

  jdouble CallDoubleMethodA(jobject obj, jmethodID methodID, jvalue * args) 
  {
    return functions->CallDoubleMethodA(this, obj, methodID, args);
  }

  void CallVoidMethod(jobject obj, jmethodID methodID, ...) 
  {
    va_list args;
    va_start(args,methodID);
    functions->CallVoidMethodV(this, obj, methodID, args);
    va_end(args);
  }

  void CallVoidMethodV(jobject obj, jmethodID methodID, va_list args) 
  {
    functions->CallVoidMethodV(this,obj,methodID,args);
  }

  void CallVoidMethodA(jobject obj, jmethodID methodID, jvalue * args) 
  {
    functions->CallVoidMethodA(this, obj, methodID, args);
  }

  jobject CallNonvirtualObjectMethod(jobject obj, jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jobject result;
    va_start(args,methodID);
    result = functions->CallNonvirtualObjectMethodV(this, obj, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jobject CallNonvirtualObjectMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallNonvirtualObjectMethodV(this, obj, clazz, methodID, args);
  }

  jobject CallNonvirtualObjectMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue * args) 
  {
    return functions->CallNonvirtualObjectMethodA(this, obj, clazz, methodID, args);
  }

  jboolean CallNonvirtualBooleanMethod(jobject obj, jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jboolean result;
    va_start(args,methodID);
    result = functions->CallNonvirtualBooleanMethodV(this, obj, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jboolean CallNonvirtualBooleanMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallNonvirtualBooleanMethodV(this, obj, clazz, methodID, args);
  }

  jboolean CallNonvirtualBooleanMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue * args) 
  {
    return functions->CallNonvirtualBooleanMethodA(this, obj, clazz, methodID, args);
  }

  jbyte CallNonvirtualByteMethod(jobject obj, jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jbyte result;
    va_start(args,methodID);
    result = functions->CallNonvirtualByteMethodV(this, obj, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jbyte CallNonvirtualByteMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallNonvirtualByteMethodV(this, obj, clazz, methodID, args);
  }

  jbyte CallNonvirtualByteMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue * args) 
  {
    return functions->CallNonvirtualByteMethodA(this,obj,clazz,methodID,args);
  }

  jchar CallNonvirtualCharMethod(jobject obj, jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jchar result;
    va_start(args,methodID);
    result = functions->CallNonvirtualCharMethodV(this, obj, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jchar CallNonvirtualCharMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallNonvirtualCharMethodV(this, obj, clazz, methodID, args);
  }

  jchar CallNonvirtualCharMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue * args) 
  {
    return functions->CallNonvirtualCharMethodA(this, obj, clazz, methodID, args);
  }

  jshort CallNonvirtualShortMethod(jobject obj, jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jshort result;
    va_start(args,methodID);
    result = functions->CallNonvirtualShortMethodV(this, obj, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jshort CallNonvirtualShortMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallNonvirtualShortMethodV(this, obj, clazz, methodID, args);
  }

  jshort CallNonvirtualShortMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue * args) 
  {
    return functions->CallNonvirtualShortMethodA(this, obj, clazz, methodID, args);
  }

  jint CallNonvirtualIntMethod(jobject obj, jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jint result;
    va_start(args,methodID);
    result = functions->CallNonvirtualIntMethodV(this, obj, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jint CallNonvirtualIntMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallNonvirtualIntMethodV(this, obj, clazz, methodID, args);
  }

  jint CallNonvirtualIntMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue * args) 
  {
    return functions->CallNonvirtualIntMethodA(this, obj, clazz, methodID, args);
  }

  jlong CallNonvirtualLongMethod(jobject obj, jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jlong result;
    va_start(args,methodID);
    result = functions->CallNonvirtualLongMethodV(this, obj, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jlong CallNonvirtualLongMethodV(jobject obj, jclass clazz,jmethodID methodID, va_list args) 
  {
    return functions->CallNonvirtualLongMethodV(this, obj, clazz, methodID, args);
  }

  jlong CallNonvirtualLongMethodA(jobject obj, jclass clazz,jmethodID methodID, jvalue * args) 
  {
    return functions->CallNonvirtualLongMethodA(this, obj, clazz, methodID, args);
  }

  jfloat CallNonvirtualFloatMethod(jobject obj, jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jfloat result;
    va_start(args,methodID);
    result = functions->CallNonvirtualFloatMethodV(this, obj, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jfloat CallNonvirtualFloatMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallNonvirtualFloatMethodV(this, obj, clazz, methodID, args);
  }

  jfloat CallNonvirtualFloatMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue * args) 
  {
    return functions->CallNonvirtualFloatMethodA(this, obj, clazz, methodID, args);
  }

  jdouble CallNonvirtualDoubleMethod(jobject obj, jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jdouble result;
    va_start(args,methodID);
    result = functions->CallNonvirtualDoubleMethodV(this, obj, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jdouble CallNonvirtualDoubleMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallNonvirtualDoubleMethodV(this, obj, clazz, methodID, args);
  }

  jdouble CallNonvirtualDoubleMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue * args) 
  {
    return functions->CallNonvirtualDoubleMethodA(this, obj, clazz, methodID, args);
  }

  void CallNonvirtualVoidMethod(jobject obj, jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    va_start(args,methodID);
    functions->CallNonvirtualVoidMethodV(this, obj, clazz, methodID, args);
    va_end(args);
  }

  void CallNonvirtualVoidMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) 
  {
    functions->CallNonvirtualVoidMethodV(this, obj, clazz, methodID, args);
  }

  void CallNonvirtualVoidMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue * args) 
  {
    functions->CallNonvirtualVoidMethodA(this, obj, clazz, methodID, args);
  }

  jfieldID GetFieldID(jclass clazz, const char *name, const char *sig) 
  {
    return functions->GetFieldID(this, clazz, name, sig);
  }

  jobject GetObjectField(jobject obj, jfieldID fieldID) 
  {
    return functions->GetObjectField(this, obj, fieldID);
  }

  jboolean GetBooleanField(jobject obj, jfieldID fieldID) 
  {
    return functions->GetBooleanField(this, obj, fieldID);
  }

  jbyte GetByteField(jobject obj, jfieldID fieldID) 
  {
    return functions->GetByteField(this, obj, fieldID);
  }

  jchar GetCharField(jobject obj, jfieldID fieldID) 
  {
    return functions->GetCharField(this, obj, fieldID);
  }

  jshort GetShortField(jobject obj, jfieldID fieldID) 
  {
    return functions->GetShortField(this, obj, fieldID);
  }

  jint GetIntField(jobject obj, jfieldID fieldID) 
  {
    return functions->GetIntField(this, obj, fieldID);
  }

  jlong GetLongField(jobject obj, jfieldID fieldID) 
  {
    return functions->GetLongField(this, obj, fieldID);
  }

  jfloat GetFloatField(jobject obj, jfieldID fieldID) 
  {
    return functions->GetFloatField(this, obj, fieldID);
  }

  jdouble GetDoubleField(jobject obj, jfieldID fieldID) 
  {
    return functions->GetDoubleField(this, obj, fieldID);
  }

  void SetObjectField(jobject obj, jfieldID fieldID, jobject value) 
  {
    functions->SetObjectField(this, obj, fieldID, value);
  }

  void SetBooleanField(jobject obj, jfieldID fieldID, jboolean value) 
  {
    functions->SetBooleanField(this, obj, fieldID, value);
  }

  void SetByteField(jobject obj, jfieldID fieldID, jbyte value) 
  {
    functions->SetByteField(this, obj, fieldID, value);
  }

  void SetCharField(jobject obj, jfieldID fieldID, jchar value) 
  {
    functions->SetCharField(this, obj, fieldID, value);
  }

  void SetShortField(jobject obj, jfieldID fieldID, jshort value) 
  {
    functions->SetShortField(this, obj, fieldID, value);
  }

  void SetIntField(jobject obj, jfieldID fieldID, jint value) 
  {
    functions->SetIntField(this, obj, fieldID, value);
  }

  void SetLongField(jobject obj, jfieldID fieldID, jlong value) 
  {
    functions->SetLongField(this, obj, fieldID, value);
  }

  void SetFloatField(jobject obj, jfieldID fieldID, jfloat value) 
  {
    functions->SetFloatField(this, obj, fieldID, value);
  }

  void SetDoubleField(jobject obj, jfieldID fieldID, jdouble value) 
  {
    functions->SetDoubleField(this, obj, fieldID, value);
  }

  jmethodID GetStaticMethodID(jclass clazz, const char *name, const char *sig) 
  {
    return functions->GetStaticMethodID(this, clazz, name, sig);
  }

  jobject CallStaticObjectMethod(jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jobject result;
    va_start(args,methodID);
    result = functions->CallStaticObjectMethodV(this, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jobject CallStaticObjectMethodV(jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallStaticObjectMethodV(this, clazz, methodID, args);
  }

  jobject CallStaticObjectMethodA(jclass clazz, jmethodID methodID, jvalue *args) 
  {
    return functions->CallStaticObjectMethodA(this, clazz, methodID, args);
  }

  jboolean CallStaticBooleanMethod(jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jboolean result;
    va_start(args,methodID);
    result = functions->CallStaticBooleanMethodV(this, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jboolean CallStaticBooleanMethodV(jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallStaticBooleanMethodV(this, clazz, methodID, args);
  }

  jboolean CallStaticBooleanMethodA(jclass clazz, jmethodID methodID, jvalue *args) 
  {
    return functions->CallStaticBooleanMethodA(this, clazz, methodID, args);
  }

  jbyte CallStaticByteMethod(jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jbyte result;
    va_start(args,methodID);
    result = functions->CallStaticByteMethodV(this, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jbyte CallStaticByteMethodV(jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallStaticByteMethodV(this, clazz, methodID, args);
  }

  jbyte CallStaticByteMethodA(jclass clazz, jmethodID methodID, jvalue *args) 
  {
    return functions->CallStaticByteMethodA(this, clazz, methodID, args);
  }

  jchar CallStaticCharMethod(jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jchar result;
    va_start(args,methodID);
    result = functions->CallStaticCharMethodV(this, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jchar CallStaticCharMethodV(jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallStaticCharMethodV(this, clazz, methodID, args);
  }

  jchar CallStaticCharMethodA(jclass clazz, jmethodID methodID, jvalue *args) 
  {
    return functions->CallStaticCharMethodA(this, clazz, methodID, args);
  }

  jshort CallStaticShortMethod(jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jshort result;
    va_start(args,methodID);
    result = functions->CallStaticShortMethodV(this, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jshort CallStaticShortMethodV(jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallStaticShortMethodV(this, clazz, methodID, args);
  }

  jshort CallStaticShortMethodA(jclass clazz, jmethodID methodID, jvalue *args) 
  {
    return functions->CallStaticShortMethodA(this, clazz, methodID, args);
  }


  jint CallStaticIntMethod(jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jint result;
    va_start(args,methodID);
    result = functions->CallStaticIntMethodV(this, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jint CallStaticIntMethodV(jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallStaticIntMethodV(this, clazz, methodID, args);
  }

  jint CallStaticIntMethodA(jclass clazz, jmethodID methodID, jvalue *args) 
  {
    return functions->CallStaticIntMethodA(this, clazz, methodID, args);
  }


  jlong CallStaticLongMethod(jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jlong result;
    va_start(args,methodID);
    result = functions->CallStaticLongMethodV(this, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jlong CallStaticLongMethodV(jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallStaticLongMethodV(this, clazz, methodID, args);
  }

  jlong CallStaticLongMethodA(jclass clazz, jmethodID methodID, jvalue *args) 
  {
    return functions->CallStaticLongMethodA(this, clazz, methodID, args);
  }

  jfloat CallStaticFloatMethod(jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jfloat result;
    va_start(args,methodID);
    result = functions->CallStaticFloatMethodV(this, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jfloat CallStaticFloatMethodV(jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallStaticFloatMethodV(this, clazz, methodID, args);
  }

  jfloat CallStaticFloatMethodA(jclass clazz, jmethodID methodID, jvalue *args) 
  {
    return functions->CallStaticFloatMethodA(this, clazz, methodID, args);
  }

  jdouble CallStaticDoubleMethod(jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    jdouble result;
    va_start(args,methodID);
    result = functions->CallStaticDoubleMethodV(this, clazz, methodID, args);
    va_end(args);
    return result;
  }

  jdouble CallStaticDoubleMethodV(jclass clazz, jmethodID methodID, va_list args) 
  {
    return functions->CallStaticDoubleMethodV(this, clazz, methodID, args);
  }

  jdouble CallStaticDoubleMethodA(jclass clazz, jmethodID methodID, jvalue *args) 
  {
    return functions->CallStaticDoubleMethodA(this, clazz, methodID, args);
  }

  void CallStaticVoidMethod(jclass clazz, jmethodID methodID, ...) 
  {
    va_list args;
    va_start(args,methodID);
    functions->CallStaticVoidMethodV(this, clazz, methodID, args);
    va_end(args);
  }

  void CallStaticVoidMethodV(jclass clazz, jmethodID methodID, va_list args) 
  {
    functions->CallStaticVoidMethodV(this, clazz, methodID, args);
  }

  void CallStaticVoidMethodA(jclass clazz, jmethodID methodID, jvalue * args) 
  {
    functions->CallStaticVoidMethodA(this, clazz, methodID, args);
  }

  jfieldID GetStaticFieldID(jclass clazz, const char *name, const char *signature) 
  {
    return functions->GetStaticFieldID(this, clazz, name, signature);
  }

  jobject GetStaticObjectField(jclass clazz, jfieldID fieldID) 
  {
    return functions->GetStaticObjectField(this, clazz, fieldID);
  }

  jboolean GetStaticBooleanField(jclass clazz, jfieldID fieldID) 
  {
    return functions->GetStaticBooleanField(this,clazz,fieldID);
  }

  jbyte GetStaticByteField(jclass clazz, jfieldID fieldID) 
  {
    return functions->GetStaticByteField(this, clazz, fieldID);
  }

  jchar GetStaticCharField(jclass clazz, jfieldID fieldID) 
  {
    return functions->GetStaticCharField(this, clazz, fieldID);
  }

  jshort GetStaticShortField(jclass clazz, jfieldID fieldID) 
  {
    return functions->GetStaticShortField(this, clazz, fieldID);
  }

  jint GetStaticIntField(jclass clazz, jfieldID fieldID) 
  {
    return functions->GetStaticIntField(this, clazz, fieldID);
  }

  jlong GetStaticLongField(jclass clazz, jfieldID fieldID) 
  {
    return functions->GetStaticLongField(this, clazz, fieldID);
  }

  jfloat GetStaticFloatField(jclass clazz, jfieldID fieldID) 
  {
    return functions->GetStaticFloatField(this, clazz, fieldID);
  }

  jdouble GetStaticDoubleField(jclass clazz, jfieldID fieldID) 
  {
    return functions->GetStaticDoubleField(this, clazz, fieldID);
  }

  void SetStaticObjectField(jclass clazz, jfieldID fieldID, jobject value) 
  {
    functions->SetStaticObjectField(this, clazz, fieldID, value);
  }

  void SetStaticBooleanField(jclass clazz, jfieldID fieldID, jboolean value) 
  {
    functions->SetStaticBooleanField(this, clazz, fieldID, value);
  }

  void SetStaticByteField(jclass clazz, jfieldID fieldID, jbyte value) 
  {
    functions->SetStaticByteField(this, clazz, fieldID, value);
  }

  void SetStaticCharField(jclass clazz, jfieldID fieldID, jchar value) 
  {
    functions->SetStaticCharField(this, clazz, fieldID, value);
  }

  void SetStaticShortField(jclass clazz, jfieldID fieldID, jshort value) 
  {
    functions->SetStaticShortField(this, clazz, fieldID, value);
  }

  void SetStaticIntField(jclass clazz, jfieldID fieldID, jint value) 
  {
    functions->SetStaticIntField(this, clazz, fieldID, value);
  }

  void SetStaticLongField(jclass clazz, jfieldID fieldID, jlong value) 
  {
    functions->SetStaticLongField(this, clazz, fieldID, value);
  }

  void SetStaticFloatField(jclass clazz, jfieldID fieldID, jfloat value) 
  {
    functions->SetStaticFloatField(this, clazz, fieldID, value);
  }

  void SetStaticDoubleField(jclass clazz, jfieldID fieldID, jdouble value) 
  {
    functions->SetStaticDoubleField(this, clazz, fieldID, value);
  }

  jstring NewString(const jchar *uChars, jsize len) 
  {
    return functions->NewString(this, uChars, len);
  }

  jsize GetStringLength(jstring str) 
  {
    return functions->GetStringLength(this, str);
  }

  const jchar *GetStringChars(jstring str, jboolean *isCopy) 
  {
    return functions->GetStringChars(this, str, isCopy);
  }

  void ReleaseStringChars(jstring str, const jchar *chars) 
  {
    functions->ReleaseStringChars(this, str, chars);
  }

  jstring NewStringUTF(const char *bytes) 
  {
    return functions->NewStringUTF(this, bytes);
  }

  jsize GetStringUTFLength(jstring str) 
  {
    return functions->GetStringUTFLength(this, str);
  }

  const char *GetStringUTFChars(jstring str, jboolean *isCopy) 
  {
    return (const char *) functions->GetStringUTFChars(this, str, isCopy);
  }

  void ReleaseStringUTFChars(jstring str, const char * utf) 
  {
    functions->ReleaseStringUTFChars(this, str, utf);
  }

  jsize GetArrayLength(jarray array) 
  {
    return functions->GetArrayLength(this, array);
  }

  jobjectArray NewObjectArray(jsize len, jclass elementType, jobject initialElement) 
  {
    return functions->NewObjectArray(this, len, elementType, initialElement);
  }

  jobject GetObjectArrayElement(jobjectArray array, jsize indx) 
  {
    return functions->GetObjectArrayElement(this, array, indx);
  }

  void SetObjectArrayElement(jobjectArray array, jsize indx, jobject value) 
  {
    functions->SetObjectArrayElement(this, array, indx, value);
  }

  jbooleanArray NewBooleanArray(jsize length) 
  {
    return functions->NewBooleanArray(this, length);
  }

  jbyteArray NewByteArray(jsize length) 
  {
    return functions->NewByteArray(this, length);
  }

  jcharArray NewCharArray(jsize length) 
  {
    return functions->NewCharArray(this, length);
  }

  jshortArray NewShortArray(jsize length) 
  {
    return functions->NewShortArray(this, length);
  }

  jintArray NewIntArray(jsize length) 
  {
    return functions->NewIntArray(this, length);
  }

  jlongArray NewLongArray(jsize length) 
  {
    return functions->NewLongArray(this, length);
  }

  jfloatArray NewFloatArray(jsize length) 
  {
    return functions->NewFloatArray(this, length);
  }

  jdoubleArray NewDoubleArray(jsize length) 
  {
    return functions->NewDoubleArray(this, length);
  }

  jboolean * GetBooleanArrayElements(jbooleanArray array, jboolean *isCopy) 
  {
    return functions->GetBooleanArrayElements(this, array, isCopy);
  }

  jbyte * GetByteArrayElements(jbyteArray array, jboolean *isCopy) 
  {
    return functions->GetByteArrayElements(this, array, isCopy);
  }

  jchar * GetCharArrayElements(jcharArray array, jboolean *isCopy) 
  {
    return functions->GetCharArrayElements(this, array, isCopy);
  }

  jshort * GetShortArrayElements(jshortArray array, jboolean *isCopy) 
  {
    return functions->GetShortArrayElements(this, array, isCopy);
  }

  jint * GetIntArrayElements(jintArray array, jboolean *isCopy) 
  {
    return functions->GetIntArrayElements(this, array, isCopy);
  }

  jlong * GetLongArrayElements(jlongArray array, jboolean *isCopy) 
  {
    return functions->GetLongArrayElements(this, array, isCopy);
  }

  jfloat * GetFloatArrayElements(jfloatArray array, jboolean *isCopy) 
  {
    return functions->GetFloatArrayElements(this, array, isCopy);
  }

  jdouble * GetDoubleArrayElements(jdoubleArray array, jboolean *isCopy) 
  {
    return functions->GetDoubleArrayElements(this, array, isCopy);
  }

  void ReleaseBooleanArrayElements(jbooleanArray array, jboolean *elems, jint mode) 
  {
    functions->ReleaseBooleanArrayElements(this, array, elems, mode);
  }

  void ReleaseByteArrayElements(jbyteArray array, jbyte *elems, jint mode) 
  {
    functions->ReleaseByteArrayElements(this, array, elems, mode);
  }

  void ReleaseCharArrayElements(jcharArray array, jchar *elems, jint mode) 
  {
    functions->ReleaseCharArrayElements(this, array, elems, mode);
  }

  void ReleaseShortArrayElements(jshortArray array, jshort *elems, jint mode) 
  {
    functions->ReleaseShortArrayElements(this, array, elems, mode);
  }

  void ReleaseIntArrayElements(jintArray array, jint *elems, jint mode) 
  {
    functions->ReleaseIntArrayElements(this, array, elems, mode);
  }

  void ReleaseLongArrayElements(jlongArray array, jlong *elems, jint mode) 
  {
    functions->ReleaseLongArrayElements(this, array, elems, mode);
  }

  void ReleaseFloatArrayElements(jfloatArray array, jfloat *elems, jint mode) 
  {
    functions->ReleaseFloatArrayElements(this, array, elems, mode);
  }

  void ReleaseDoubleArrayElements(jdoubleArray array, jdouble *elems, jint mode) 
  {
    functions->ReleaseDoubleArrayElements(this, array, elems, mode);
  }

  void GetBooleanArrayRegion(jbooleanArray array, jsize start, jsize len, jboolean *buf) 
  {
    functions->GetBooleanArrayRegion(this, array, start, len, buf);
  }

  void GetByteArrayRegion(jbyteArray array, jsize start, jsize len, jbyte *buf) 
  {
    functions->GetByteArrayRegion(this, array, start, len, buf);
  }

  void GetCharArrayRegion(jcharArray array, jsize start, jsize len, jchar *buf) 
  {
    functions->GetCharArrayRegion(this, array, start, len, buf);
  }

  void GetShortArrayRegion(jshortArray array, jsize start, jsize len, jshort *buf) 
  {
    functions->GetShortArrayRegion(this, array, start, len, buf);
  }

  void GetIntArrayRegion(jintArray array, jsize start, jsize len, jint *buf) 
  {
    functions->GetIntArrayRegion(this, array, start, len, buf);
  }

  void GetLongArrayRegion(jlongArray array, jsize start, jsize len, jlong *buf) 
  {
    functions->GetLongArrayRegion(this, array, start, len, buf);
  }

  void GetFloatArrayRegion(jfloatArray array, jsize start, jsize len, jfloat *buf) 
  {
    functions->GetFloatArrayRegion(this, array, start, len, buf);
  }

  void GetDoubleArrayRegion(jdoubleArray array, jsize start, jsize len, jdouble *buf) 
  {
    functions->GetDoubleArrayRegion(this, array, start, len, buf);
  }

  void SetBooleanArrayRegion(jbooleanArray array, jsize start, jsize len, jboolean *buf) 
  {
    functions->SetBooleanArrayRegion(this, array, start, len, buf);
  }

  void SetByteArrayRegion(jbyteArray array, jsize start, jsize len, jbyte *buf) 
  {
    functions->SetByteArrayRegion(this, array, start, len, buf);
  }

  void SetCharArrayRegion(jcharArray array, jsize start, jsize len, jchar *buf) 
  {
    functions->SetCharArrayRegion(this, array, start, len, buf);
  }

  void SetShortArrayRegion(jshortArray array, jsize start, jsize len, jshort *buf) 
  {
    functions->SetShortArrayRegion(this, array, start, len, buf);
  }

  void SetIntArrayRegion(jintArray array, jsize start, jsize len, jint *buf) 
  {
    functions->SetIntArrayRegion(this, array, start, len, buf);
  }

  void SetLongArrayRegion(jlongArray array, jsize start, jsize len, jlong *buf) 
  {
    functions->SetLongArrayRegion(this, array, start, len, buf);
  }

  void SetFloatArrayRegion(jfloatArray array, jsize start, jsize len, jfloat *buf) 
  {
    functions->SetFloatArrayRegion(this, array, start, len, buf);
  }

  void SetDoubleArrayRegion(jdoubleArray array, jsize start, jsize len, jdouble *buf) 
  {
    functions->SetDoubleArrayRegion(this, array, start, len, buf);
  }

  jint RegisterNatives(jclass clazz, const JNINativeMethod *methods, jint nMethods) 
  {
    return functions->RegisterNatives(this, clazz, methods, nMethods);
  }

  jint UnregisterNatives(jclass clazz) 
  {
    return functions->UnregisterNatives(this, clazz);
  }

  jint MonitorEnter(jobject obj) 
  {
    return functions->MonitorEnter(this, obj);
  }

  jint MonitorExit(jobject obj) 
  {
    return functions->MonitorExit(this, obj);
  }

  jint GetJavaVM(JavaVM **vm) 
  {
    return functions->GetJavaVM(this, vm);
  }

  void GetStringRegion(jstring str, jsize start, jsize len, jchar *buf) 
  {
    functions->GetStringRegion(this, str, start, len, buf);
  }

  void GetStringUTFRegion(jstring str, jsize start, jsize len, char *buf) 
  {
    functions->GetStringUTFRegion(this, str, start, len, buf);
  }

  void * GetPrimitiveArrayCritical(jarray array, jboolean *isCopy) 
  {
    return functions->GetPrimitiveArrayCritical(this, array, isCopy);
  }

  void ReleasePrimitiveArrayCritical(jarray array, void *carray, jint mode) 
  {
    functions->ReleasePrimitiveArrayCritical(this, array, carray, mode);
  }

  const jchar * GetStringCritical(jstring string, jboolean *isCopy) 
  {
    return functions->GetStringCritical(this, string, isCopy);
  }

  void ReleaseStringCritical(jstring string, const jchar *carray) 
  {
    functions->ReleaseStringCritical(this, string, carray);
  }

  jweak NewWeakGlobalRef(jobject obj) 
  {
    return functions->NewWeakGlobalRef(this, obj);
  }

  void DeleteWeakGlobalRef(jweak wref) 
  {
    functions->DeleteWeakGlobalRef(this, wref);
  }

  jboolean ExceptionCheck() 
  {
    return functions->ExceptionCheck(this);
  }
};
#endif
/* *INDENT-ON* */

  /* JNIInvokeInterface types */

/* *INDENT-OFF* */
struct JNIInvokeInterface_struct
{
  void *null_0;
  void *null_1;
  void *null_2;
  jint (JNICALL *DestroyJavaVM) (JavaVM *vm);	/* 3 */
  jint (JNICALL *AttachCurrentThread) (JavaVM *vm, void **penv, void *args);	/* 4 */
  jint (JNICALL *DetachCurrentThread) (JavaVM *vm);	/* 5 */
  jint (JNICALL *GetEnv) (JavaVM *vm, void **penv, jint interface_id);	/* 6 */
};

#ifdef __cplusplus
struct JavaVM_ 
{
  const JNIInvokeInterface * functions;

  jint DestroyJavaVM() 
  {
    return functions->DestroyJavaVM(this);
  }

  jint AttachCurrentThread(void **penv, void *args) 
  {
    return functions->AttachCurrentThread(this, penv, args);
  }

  jint DetachCurrentThread() 
  {
    return functions->DetachCurrentThread(this);
  }

  jint GetEnv(void **penv, jint version) 
  {
    return functions->GetEnv(this, penv, version);
  }
};
#endif
/* *INDENT-ON* */

  /* invocation API */

  JNIIMPORT jint JNICALL JNI_GetDefaultJavaVMInitArgs (void *vm_args);
  JNIIMPORT jint JNICALL JNI_GetCreatedJavaVMs (JavaVM **vmBuf, jsize bufLen,
						jsize *nVMs);
  JNIIMPORT jint JNICALL JNI_CreateJavaVM (JavaVM **pvm, void **penv,
					   void *vm_args);

  /* library and version management */

#ifndef _VIRTUAL_MACHINE
  JNIEXPORT jint JNICALL JNI_OnLoad (JavaVM *vm, void *reserved);
  JNIEXPORT void JNICALL JNI_OnUnload (JavaVM *vm, void *reserved);
#endif

#ifdef __cplusplus
}
#endif

#endif				/* NOT SVM_JNI_H */
