/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef MHPOUTPUT_MPEGPES_PESHEADERS_H
#define MHPOUTPUT_MPEGPES_PESHEADERS_H

#include <endian.h>

namespace MhpOutput {

struct PTS {
   PTS()
     : marker_bit_1(0x1), magic(0x2), marker_bit_2(0x1), marker_bit_3(0x1) 
    {}
   unsigned long long GetPts() {
      return (  (((unsigned long long)pts_hi)<<30)
              | (((unsigned long long)pts_mid_hi)<<22)
              | (((unsigned long long)pts_mid_lo)<<15)
              | (((unsigned long long)pts_lo_hi)<<7)
              | (((unsigned long long)pts_lo_lo))
             );
   }
   void SetPts(unsigned long long pts) {
      pts_hi=((pts & 0x1C0000000LL) >> 30);//11100 0000 0000 0000 0000 0000 0000 0000
      pts_mid_hi=((pts & 0x3FC00000) >> 22); // 11 1111 1100 0000 0000 0000 0000 0000
      pts_mid_lo=((pts & 0x3F8000) >> 15);   // 00 0000 0011 1111 1000 0000 0000 0000
      pts_lo_hi=((pts & 0x7F80) >> 7);       // 00 0000 0000 0000 0111 1111 1000 0000
      pts_lo_lo=(pts & 0x7F);                // 00 0000 0000 0000 0000 0000 0111 1111
   }
   
#if BYTE_ORDER == BIG_ENDIAN
   uchar  magic         :4; //0010 (PTS only) or 0011 (PTS and DTS)
   uchar  pts_hi        :3;
   uchar  marker_bit_1  :1;
#else
   uchar  marker_bit_1  :1;
   uchar  pts_hi        :3;
   uchar  magic         :4; //0010 (PTS only) or 0011 (PTS and DTS)
#endif

   uchar  pts_mid_hi    :8;
   
#if BYTE_ORDER == BIG_ENDIAN
   uchar  pts_mid_lo    :7;
   uchar  marker_bit_2  :1;
#else
   uchar  marker_bit_2  :1;
   uchar  pts_mid_lo    :7;
#endif

   uchar  pts_lo_hi     :8;
   
#if BYTE_ORDER == BIG_ENDIAN
   uchar  pts_lo_lo     :7;
   uchar  marker_bit_3  :1;
#else
   uchar  marker_bit_3  :1;
   uchar  pts_lo_lo     :7;
#endif
};

enum StreamType { 
                  unknown = 0,
                  program_stream_map = 0x1F,
                  private_stream_1 = 0xBD,
                  private_stream_2 = 0xBF,
                  padding_stream = 0xBE,
                  //For audio stream IDs, the five least significant bits
                  //are the stream numbers (0x0...0x1F),
                  //for video the _four_ least significant bits are the
                  //stream number (0x0...0xF)
                  audio_stream_first = 0xC0,
                  audio_stream_last = 0xDF,
                  video_stream_first = 0xE0,
                  video_stream_last = 0xEF,
                  DSMCC_stream = 0xF2
                };

struct PESHeader {
   //this constructor fills the structure with reasonable default values.
   //For a description of their meaning please refer to ISO-13818-1
   PESHeader(bool hasPts=false, StreamType streamId=private_stream_1, bool dataAlignment=false)
     : magic_1(0x00), magic_2(0x00), magic_3(0x01),
       stream_id(streamId), packet_length_hi(0), packet_length_lo(0),
       original_or_copy(0), copyright(1), data_alignment_indicator(dataAlignment),
       PES_priority(0), PES_scrambling_control(0x00), magic_4(0x2),
       PES_extension_flag(0), PES_CRC_flag(0), additional_copy_info_flag(0),
       DSM_trick_mode_flag(0), ES_rate_flag(0), ESCR_flag(0), PTS_DTS_flags(hasPts ? 0x2 : 0x0),
       PES_header_data_length(hasPts ? sizeof(PTS) : 0)
       {}
   void SetLength(int length) { packet_length_hi = (length & 0xFF00) >> 8; packet_length_lo= (length & 0xFF); }
       
   uchar   magic_1      :8; //0x00
   uchar   magic_2      :8; //0x00
   uchar   magic_3      :8; //0x01
   uchar   stream_id    :8;
   
   uchar   packet_length_hi         :8;
   uchar   packet_length_lo         :8;
   
#if BYTE_ORDER == BIG_ENDIAN
   uchar   magic_4                  :2; // 10
   uchar   PES_scrambling_control   :2;
   uchar   PES_priority             :1;
   uchar   data_alignment_indicator :1;
   uchar   copyright                :1;
   uchar   original_or_copy         :1;
#else
   uchar   original_or_copy         :1;
   uchar   copyright                :1;
   uchar   data_alignment_indicator :1;
   uchar   PES_priority             :1;
   uchar   PES_scrambling_control   :2;
   uchar   magic_4                  :2; // 10
#endif

#if BYTE_ORDER == BIG_ENDIAN
   uchar   PTS_DTS_flags            :2;
   uchar   ESCR_flag                :1;
   uchar   ES_rate_flag             :1;
   uchar   DSM_trick_mode_flag      :1;
   uchar   additional_copy_info_flag:1;
   uchar   PES_CRC_flag             :1;
   uchar   PES_extension_flag       :1;
#else
   uchar   PES_extension_flag       :1;
   uchar   PES_CRC_flag             :1;
   uchar   additional_copy_info_flag:1;
   uchar   DSM_trick_mode_flag      :1;
   uchar   ES_rate_flag             :1;
   uchar   ESCR_flag                :1;
   uchar   PTS_DTS_flags            :2;
#endif
   
   uchar PES_header_data_length     :8;
   //now follows PTS if flags are set
};
              
 
}



#endif

