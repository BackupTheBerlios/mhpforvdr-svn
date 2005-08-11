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

#include <directfb.h>

#include <core/coredefs.h>
#include <core/coretypes.h>

#include <core/input.h>
#include <core/system.h>
#include <core/thread.h>

#include <misc/conf.h>
#include <misc/mem.h>

#include <core/input_driver.h>


DFB_INPUT_DRIVER( vdrinput )

#include "vdrinput.h"


InputDevice *inputDevice;


void translateAndPostEvent(enum eKeys Key) {
   DFBInputEvent evt;
   memset( &evt, 0, sizeof(DFBInputEvent) );
   
   if ( (evt.key_symbol=translateEKeys(Key)) != DIKS_NULL ) {
      evt.type = DIET_KEYPRESS;
      evt.flags = DIEF_KEYSYMBOL;
      dfb_input_dispatch( inputDevice, &evt );

      evt.type = DIET_KEYRELEASE;
      evt.flags = DIEF_KEYSYMBOL;
      dfb_input_dispatch( inputDevice, &evt );
   }
}

DFBInputDeviceKeySymbol translateEKeys(enum eKeys Key) {
   switch (Key) {
      //case kMenu:
      case kOk:
         return DIKS_OK;
      case kBack:
         return DIKS_BACK;
      
      case kUp:
         return DIKS_CURSOR_UP;
      case kDown:
         return DIKS_CURSOR_DOWN;
      case kLeft:
         return DIKS_CURSOR_LEFT;
      case kRight:
         return DIKS_CURSOR_RIGHT;
      
      case kRed:
         return DIKS_RED;
      case kGreen:
         return DIKS_GREEN;
      case kYellow:
         return DIKS_YELLOW;
      case kBlue:
         return DIKS_BLUE;
      
      case k0:
         return DIKS_0;
      case k1:
         return DIKS_1;
      case k2:
         return DIKS_2;
      case k3:
         return DIKS_3;
      case k4:
         return DIKS_4;
      case k5:
         return DIKS_5;
      case k6:
         return DIKS_6;
      case k7:
         return DIKS_7;
      case k8:
         return DIKS_8;
      case k9:
         return DIKS_9;
      
      case kPlay:
         return DIKS_PLAY;
      case kStop:
         return DIKS_STOP;
      case kFastFwd:
         return DIKS_FASTFORWARD;
      case kFastRew:
         return DIKS_REWIND;
      //case kPause:
      //case kRecord:
      
      //case kChanUp:
      //case kChanDn:
      //case kVolUp:
      //case kVolDn:
      //case kMute:
      default:
         return DIKS_NULL;
   }
}

/* driver functions */

static int
driver_get_available()
{
   return 1;
}

static void
driver_get_info( InputDriverInfo *info )
{
     /* fill driver info structure */
     snprintf( info->name,
               DFB_INPUT_DRIVER_INFO_NAME_LENGTH, "VDR Driver" );

     snprintf( info->vendor,
               DFB_INPUT_DRIVER_INFO_VENDOR_LENGTH,
               "It's me" );

     info->version.major = 0;
     info->version.minor = 1;
}

static DFBResult
driver_open_device( InputDevice      *device,
                    unsigned int      number,
                    InputDeviceInfo  *info,
                    void            **driver_data )
{
    /* fill device info structure */
     snprintf( info->desc.name,
               DFB_INPUT_DEVICE_DESC_NAME_LENGTH, "VDR control" );

     snprintf( info->desc.vendor,
               DFB_INPUT_DEVICE_DESC_VENDOR_LENGTH, "Unknown" );

     info->prefered_id = DIDID_ANY;

     info->desc.type   = DIDTF_REMOTE | DIDTF_KEYBOARD;
     info->desc.caps   = DICAPS_KEYS;

     info->desc.min_keycode = -1;//0;
     info->desc.max_keycode = -1;//127;

     /* start input thread */
     //data->thread = dfb_thread_create( CTT_INPUT, keyboardEventThread, data );

     /* set private data pointer */
     //*driver_data = data;
     inputDevice=device;

     return DFB_OK;
}

/*
 * Fetch one entry from the kernel keymap.
 */
static DFBResult
driver_get_keymap_entry( InputDevice               *device,
                         void                      *driver_data,
                         DFBInputDeviceKeymapEntry *entry )
{
     return DFB_UNSUPPORTED;
}

static void
driver_close_device( void *driver_data )
{
     /* stop input thread */
     /*dfb_thread_cancel( data->thread );
     dfb_thread_join( data->thread );
     dfb_thread_destroy( data->thread );*/
}



