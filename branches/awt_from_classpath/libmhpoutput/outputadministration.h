/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef LIBMHPOUTPUT_OUTPUTADMINISTRATION_H
#define LIBMHPOUTPUT_OUTPUTADMINISTRATION_H

namespace MhpOutput {

class Administration {
public:
   static void Init(const char *system, const char *arg="");
   static bool CheckSystem();
   static void CleanUp();
private:
   static bool error;
};



}

#endif

