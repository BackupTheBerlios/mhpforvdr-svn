

//This file defines some constants that inform about supported features
//Keep in sync with ApplicationManager.setupProperties

//#include <libait/application.h>
//Protocol stack 1.0.2: This is the version used today.
//  Convergence have their platform support this version.
//  Version 1.1.0 adds DVB-HTML support
//The enhanced broadcast profile is the profile without a return channel
#define MHP_IMPLEMENTATION_VERSION_MAJOR   1
#define MHP_IMPLEMENTATION_VERSION_MINOR   0
#define MHP_IMPLEMENTATION_VERSION_MICRO   2

#define MHP_IMPLEMENTATION_HIGHEST_PROFILE ApplicationInfo::cApplication::EnhancedBroadcast

#define MHP_TRANSPORT_VIA_OC true
#define MHP_TRANSPORT_IP_VIA_DVB false
#define MHP_TRANSPORT_HTTP_OVER_INTERACTIONCHANNEL false

