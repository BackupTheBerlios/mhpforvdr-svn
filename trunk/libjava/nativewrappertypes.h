
#ifndef LIBJAVA_NATIVEWRAPPERTYPES_H
#define LIBJAVA_NATIVEWRAPPERTYPES_H

#include "jniinterface.h"

// This file is not part of the JNI or Java interfaces.
// It is only a centralized place to keep some typedefs used here
// and in JNI code of the implementation.
// It is not included in any other header files of libjava.

#ifdef LIBAIT_APPLICATIONS_H
typedef JNI::ReferenceNativeData<ApplicationInfo::cApplication::Ptr> NativeApplicationData;
typedef JNI::PointerNativeData<ApplicationInfo::cApplicationsDatabase> NativeDBData;
#endif

#ifdef __CHANNELS_H
typedef JNI::PointerNativeData<cChannel> NativeChannelData;
#endif

#ifdef DVBSI_DATABASE_H
typedef JNI::ReferenceNativeData<DvbSi::Database::Ptr> NativeDvbsiDatabaseData;
#endif

#endif

