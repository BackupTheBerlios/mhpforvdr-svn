/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include <dlfcn.h>
#include <vdr/tools.h>

#include "output.h"
#include "outputadministration.h"
 
namespace MhpOutput {

bool Administration::error = false;

void Administration::Init(const char *system, const char *arg) {
   char libpath[PATH_MAX];
   sprintf(libpath, MHPLIBDIR "/libmhpoutput%s.so", system);
   void *dlhandle=dlopen(libpath, RTLD_NOW);
   const char *errorMessage = dlerror();
   if (errorMessage) {
      esyslog("MHP: Failed to load output driver \"%s\", reason: %s. Disabling the plugin. "
              "Please see if there are other error messages above explaining this error.", libpath, errorMessage);
      return;
   }
   
   void *(*creator)(void);
   creator = (void *(*)())dlsym(dlhandle, "MhpOutputPluginCreator");
   if ( (errorMessage=dlerror()) ) {
      esyslog("MHP: Failed to load symbol from output driver \"%s\", reason: %s. Disabling the plugin.", libpath, errorMessage);
      dlclose(dlhandle);
      return;
   }
   
   System::s_self = (System *)creator();
   System::s_self->dlhandle=dlhandle;
   
   if (!System::s_self->Initialize(arg)) {
      esyslog("MHP: Error initializing output system. Disabling the plugin.");
      CleanUp();
      error=true;
   } else
      error=false;
}

void Administration::CleanUp() {
   if (System::s_self) {
      //need to delete object before closing library
      void *dlhandle=System::s_self->dlhandle;
      delete System::s_self;
      if (dlhandle)
         dlclose(dlhandle);
   }
}

bool Administration::CheckSystem() {
   return !error;
}


}

