/***************************************************************************
 *       Copyright (c) 2004 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

 
#ifndef MHP_MHPMESSAGE_H
#define MHP_MHPMESSAGE_H

//Put this into an extra header because mhpcontrol.h has many header dependencies
//Implementation is in mhpcontrol.c

class MhpMessages {
public:
   enum Messages { NoMessage, LoadingFailed, StartingFailed, OutputSystemError, JavaSystemError, JavaStartError };
   static void DisplayMessage(Messages message);
private:
   friend class MhpControl;
   static Messages message;   
};

extern const char *MhpConfigPath;





#endif

