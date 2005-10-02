
#ifndef LIBJAVA_NATIVEWRAPPERTYPES_H
#define LIBJAVA_NATIVEWRAPPERTYPES_H

#include "jniinterface.h"

// This file is not part of the JNI or Java interfaces.
// It is only a centralized place to keep some typedefs used here
// and in JNI code of the implementation.
// It is not included in any other header files of libjava.

#ifdef LIBAIT_APPLICATIONS_H
class ApplicationDeleter : public JNI::NativeData::ReferenceDeleter<ApplicationInfo::cApplication::Ptr> {
   public: static ApplicationDeleter deleter;
};
typedef JNI::ReferenceDeleterNativeData<ApplicationInfo::cApplication::Ptr, ApplicationDeleter> NativeApplicationData;
typedef JNI::PointerNativeData<ApplicationInfo::cApplicationsDatabase> NativeDBData;
#endif

#ifdef LIBSERVICE_SERVICE_H
typedef JNI::PointerNativeData<Service::Service> NativeChannelData;
#endif

#ifdef DVBSI_DATABASE_H
class DvbSiDatabaseDeleter : public JNI::NativeData::ReferenceDeleter<DvbSi::Database::Ptr> {
   public: static DvbSiDatabaseDeleter deleter;
};
typedef JNI::ReferenceDeleterNativeData<DvbSi::Database::Ptr, DvbSiDatabaseDeleter> NativeDvbsiDatabaseData;
#endif

#endif

