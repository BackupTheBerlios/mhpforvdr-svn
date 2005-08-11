/*
   Copyright (c) 2003 Marcel Wiesweg
   
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.
*/
#ifndef VDRINPUT_H
#define VDRINPUT_H

#ifndef FROM_CPLUSPLUS
#include <directfb.h>

#define VDRSUPPORT_INCLUDE_DECLARATIONS //include client side includes
#define VDRSUPPORT_NO_CPLUSPLUS  //this file here is C, not C++, so do not include class declarations
//#include <libcommoncode/connectormessage.h>
#include <vdrsupport/vdrsupport.h>
#undef VDRSUPPORT_INCLUDE_DECLARATIONS
#undef VDRSUPPORT_NO_CPLUSPLUS
#endif //FROM_CPLUSPLUS

#ifdef FROM_CPLUSPLUS
extern "C" {
#endif
     
void translateAndPostEvent(enum eKeys Key);

#ifdef FROM_CPLUSPLUS
}
#endif

#endif
