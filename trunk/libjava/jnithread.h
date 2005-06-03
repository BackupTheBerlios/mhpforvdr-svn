/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

 
#ifndef JNITHREAD_H
#define JNITHREAD_H

#include <vdr/thread.h>

namespace JNI {

//see comments in javainterface.h
class Thread : public cThread {
public:
   static void CheckAttachThread();
protected:
   void CheckDetachThread();
};

}


#endif

