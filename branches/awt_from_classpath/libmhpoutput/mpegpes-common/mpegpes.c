/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/


#include "mpegpes.h"
#include <libmhpoutput/avcodecsupport.h>

extern "C" {
#include "plain_c_bridge.h"
}

namespace MhpOutput {

class cStatistics {
public:
   cStatistics();
   void Start();
   void Stop();
   void Print();
private:
   timeval min,max,average;
   timeval last;
   uint count;
};


/*
  The TwoPointers class keeps two pointers (in our case, the YUV buffers).
  These have four possible states: Available -> Writing <-> Changed -> Reading -> Available
  When DirectFB has drawn, it notifies the system and the call reaches Encoder::UpdateRegion
  which writes to one buffer. If one is changed, the encoding thread will read it.
*/
TwoPointers::TwoPointers() {
   Set(0,0);
}

void TwoPointers::Set(unsigned char *a, unsigned char *b) {
   p[0]=a;
   p[1]=b;
   state[0]=Available;
   state[1]=Available;
}

//The four access functions must be protected with a mutex when used

unsigned char *TwoPointers::getNextForWriting() {   
   for (int i=0;i<2;i++) {
      if (state[i]==Changed) {
         state[i]=Writing;
         return p[i];
      }
   }
   
   for (int i=0;i<2;i++) {
      if (state[i]==Available) {
         state[i]=Writing;
         return p[i];
      }
   }
   return 0;
}

unsigned char *TwoPointers::getNextForReading() {  
   for (int i=0;i<2;i++) {
      if (state[i]==Writing) {
         return 0;
      }
   }
   
   for (int i=0;i<2;i++) {
      if (state[i]==Changed) {
         state[i]=Reading;
         return p[i];
      }
   }
   return 0;
}

void TwoPointers::releaseChanged(unsigned char *d) {
   for (int i=0;i<2;i++) {
      if (p[i]==d && state[i]==Writing) {
         state[i]=Changed;
         return;
      }
   }         
}

void TwoPointers::releaseAvailable(unsigned char *d) {
   for (int i=0;i<2;i++) {
      if (p[i]==d && state[i]==Reading) {
         state[i]=Available;
         return;
      }
   }
}


/*** MpegPesSystem ***/

MpegPesSystem::MpegPesSystem() :dfb(0), layer(0) {
   //do everything in Initialize()
}

/*DFBEnumerationResult MpegPesSystem::layerCallback(DFBDisplayLayerID layer_id, DFBDisplayLayerDescription desc, void *callbackdata) {
   //this output system only has one layer
   ((MpegPesSystem *)callbackdata)->layer=((MpegPesSystem *)callbackdata)->dfb->GetDisplayLayer(layer_id);
   return DFENUM_CANCEL;
}*/

MpegPesSystem::~MpegPesSystem() {
   init=false;
   //order is important, don't destroy a layer after dfb e.g.
   if (dfb) {
      if (layer) {
         if (primarySurface) {
            primarySurface->Release();
         }
         layer->Release();
      }
      dfb->Release();
   }
}

bool MpegPesSystem::Initialize(const char *arg) {
   //Initialize Encoder (libavcodec etc.)
   if (!Encoder::Initialize())
      return false;

   //check here for possible libavcodec errors
   if (!avCodec)
      return false;
      
   /*int argc=1;
   char *argv[1];
   argv[0]="--dfb:system=shm";
   char **eins=argv;
   char ***zwei=&eins;
   DirectFB::Init(&argc, zwei);*/
   const char *dfbargs="DFBARGS";
   const char *newArgs="system=shm";
   char *previousArgs=getenv(dfbargs);
   
   if (previousArgs) {
      int len=strlen(newArgs);
      int prevLen=strlen(previousArgs);
      char args[len+prevLen+2];
      strcpy(args, previousArgs);
      args[prevLen]=' ';
      strcpy(args+prevLen+1, newArgs);
      setenv(dfbargs, args, true);
   } else {
      setenv(dfbargs, newArgs, true);
   }
   
   try {
      DirectFB::Init();
      
      dfb=DirectFB::Create();
      layer=dfb->GetDisplayLayer(DLID_PRIMARY);
      layer->SetCooperativeLevel(DLSCL_ADMINISTRATIVE);
      primarySurface=layer->GetSurface();
   } catch (DFBException *e) {
        esyslog("MhpOutput: DirectFB: Error %s, %s.", e->GetAction(), e->GetResult());
        delete e;
        return false;
   }
   
   try {
      layer->EnableCursor(false);
      //layer->SetBackgroundColor(0,0,0,255); //or better white?
   } catch (DFBException *e) {
        esyslog("MhpOutput: DirectFB: Error %s, %s.", e->GetAction(), e->GetResult());
        delete e;
   }
   
   //init is used for Encoder::UpdateRegion, it indicates that it is okay
   //to write to the buffers
   init=true;
   return true;
   
   //dripFeed=new AVCodecDripFeed();
}


/*** Encoder ***/

Encoder::Encoder()
 : primarySurface(0), init(false), avCodec(0),
   running(false), open(false), avContext(0),
   avFrame1(0), avFrame2(0), avFrameRGB(0),
   mpegBuffer(0), mpegBufferSize(0), mpegSize(0)
{
   //Do everything in Initialize. 
   //There we can call virtual functions and return a success value.
}

bool Encoder::Initialize() {
   AVCodecInitialisation::Check();

   width=MhpOutput::System::self()->GetDisplayWidth();
   height=MhpOutput::System::self()->GetDisplayHeight();

   /* find the mpeg1 video encoder */
   if (!(avCodec = avcodec_find_encoder(CODEC_ID_MPEG2VIDEO)) ) {
       esyslog("MhpOutput: Encoder: Fatal: codec not found.");
       return false;
   }

   mpegSize = 0;
   mpegBufferSize = width*height; //this is guessing, but should be enough for an I-Frame
   mpegBuffer = new unsigned char[mpegBufferSize];
   
   avContext = avcodec_alloc_context();
   avFrame1 = avcodec_alloc_frame();
   #ifndef MPEGPES_LAYER_I420
   avFrame2 = avcodec_alloc_frame();
   avFrameRGB = avcodec_alloc_frame();
   #endif
   
   avcodec_get_context_defaults(avContext);

   //Most values are taken from ffmpeg.c.
   //The comments give some minimum, maximum and default values.
   
   //resolution must be a multiple of two
   avContext->width = width;
   avContext->height = height;
   
   avContext->bit_rate = 800000; //4, 240000000, 800000
   avContext->bit_rate_tolerance = 4000000;
   
   #if (FFMPEG_VERSION_INT >= 0x000409)
   if (MhpOutput::System::self()->GetVideoFormat() == FourToThree) {
      avContext->sample_aspect_ratio.num=4;
      avContext->sample_aspect_ratio.den=3;
   } else {
      avContext->sample_aspect_ratio.num=16;
      avContext->sample_aspect_ratio.den=9;
   }
   #else
   avContext->aspect_ratio = (MhpOutput::System::self()->GetVideoFormat() == FourToThree ? FF_ASPECT_4_3_625 : FF_ASPECT_16_9_625);
   #endif
   avContext->pix_fmt=PIX_FMT_YUV420P;
   
   //PAL hax exactly 25 frames / s, NTSC has 30000 frames per 1001 seconds (~29.97)
   #if (LIBAVCODEC_BUILD >= 4754)
   avContext->time_base.den = (MhpOutput::System::self()->GetVideoSystem() == vsPAL ? 25 : 30000);
   avContext->time_base.num =(MhpOutput::System::self()->GetVideoSystem() == vsPAL ? 1 : 1001);
   #else
   avContext->frame_rate = (MhpOutput::System::self()->GetVideoSystem() == vsPAL ? 25 : 30000);
   avContext->frame_rate_base=(MhpOutput::System::self()->GetVideoSystem() == vsPAL ? 1 : 1001);
   #endif
   
   avContext->gop_size = 0; //only I-frames   
   avContext->me_method = ME_ZERO; /*motion estimation type*/
   
   avContext->qmin = 2; //1, 31, 2
   avContext->qmax = 15; //1, 31, 31
   avContext->mb_qmin = 2; //1, 31, 2
   avContext->mb_qmax = 15; //1, 31, 31
   avContext->max_qdiff = 3;
   avContext->qblur = 0.5;
   avContext->qcompress = 0.5;
   avContext->workaround_bugs = FF_BUG_AUTODETECT;
   
   //Copied from ffmpeg.c
   //avContext->rc_max_rate = 0;
   //avContext->rc_min_rate = 0;
   //avContext->rc_buffer_size = 0;
   //avContext->rc_buffer_aggressivity= 1.0;
   //avContext->rc_initial_cplx= 0;
   //avContext->i_quant_factor = -0.8;
   //avContext->b_quant_factor = 1.25;
   //avContext->i_quant_offset = 0.0;
   //avContext->b_quant_offset = 1.25;
   //avContext->dct_algo = 0;
   //avContext->idct_algo = 0;
   //avContext->strict_std_compliance = 0;
   //avContext->rc_eq="tex^qComp"; //I don't know what that means
   
   //These two set a constant quality - better use VBR
   //avFrame->quality = 1; //1, 31
   //avContext->flags |= CODEC_FLAG_QSCALE;
   return true;
}

Encoder::~Encoder() {
   if (open)
      avcodec_close(avContext);
   delete avFrame1->data[0];
   #ifndef MPEGPES_LAYER_I420
   delete avFrame2->data[0];
   delete avFrameRGB->data[0];
   free(avFrameRGB);
   free(avFrame2);
   #endif   
   free(avFrame1);
   free(avContext);
   delete mpegBuffer;
}

bool Encoder::SetConfiguration(int width, int height, DFBSurfacePixelFormat pixelformat) {
   //maybe do sanity test
   return true;
}

void Encoder::Activate(Player *play, bool On) {
   printf("Encoder::Activate %d %p\n", On, play);
   if (On) {
      player=play;
      if (!running)
         cThread::Start();
   } else {
      if (running) {
         running=false;
         Cancel(2);
      }
      player=0;
   }
}

void *Encoder::GetBuffer(int w, int h) {
   if (w != width || h != height)
      return 0;
      
   if (avFrame1->data[0])
      return avFrame1->data[0];
      
   #ifdef MPEGPES_LAYER_I420
   unsigned char *buffer=new unsigned char[avpicture_get_size(PIX_FMT_YUV420P, width, height)];
   avpicture_fill((AVPicture *)avFrame1, buffer, PIX_FMT_YUV420P, width, height);
   
   Set(buffer, 0);
   
   /*if (!avFrame->data[0]) {
      avFrame->data[0]=new unsigned char[calcI420Size];
      avFrame->data[1]=avFrame->data[0]+calcI420Size*4/6;
      avFrame->data[2]=avFrame->data[0]+calcI420Size*5/6;
      avFrame->linesize[0] = width;
      avFrame->linesize[1] = width/2;
      avFrame->linesize[2] = width/2;
   }*/
   return avFrame1->data[0];
   
   #else
   unsigned char *rgbBuffer=new unsigned char[avpicture_get_size(PIX_FMT_BGR24, width, height)];
   unsigned char *buffer1=new unsigned char[avpicture_get_size(PIX_FMT_YUV420P, width, height)];
   unsigned char *buffer2=new unsigned char[avpicture_get_size(PIX_FMT_YUV420P, width, height)];
   avpicture_fill((AVPicture *)avFrameRGB, rgbBuffer, PIX_FMT_BGR24, width, height);
   avpicture_fill((AVPicture *)avFrame1, buffer1, PIX_FMT_YUV420P, width, height);
   avpicture_fill((AVPicture *)avFrame2, buffer2, PIX_FMT_YUV420P, width, height);
   
   Set(buffer1, buffer2);
   
   /*if (!avFrame->data[0]) {
      avFrame->data[0]=new unsigned char[calcI420Size];
      avFrame->data[1]=avFrame->data[0]+calcI420Size*4/6;
      avFrame->data[2]=avFrame->data[0]+calcI420Size*5/6;
      avFrame->linesize[0] = width;
      avFrame->linesize[1] = width/2;
      avFrame->linesize[2] = width/2;
   }
   if (!avFrameRGB->data[0]) {
      avFrameRGB->data[0]=new unsigned char[width*height*3];
      avFrameRGB->data[1]=0;
      avFrameRGB->data[2]=0;
      avFrameRGB->linesize[0] = width*3;
      avFrameRGB->linesize[1] = 0;
      avFrameRGB->linesize[2] = 0;
   }*/
   return avFrameRGB->data[0];
   
   #endif
}

//turn benchmarking on/off:
#define BENCHMARK(x) 
//#define BENCHMARK(x) x

void Encoder::Action() {
   //printf("Encoding disabled\n");
   //return;
   printf("Starting encoding thread\n");
   if (!open) {
      if (avcodec_open(avContext, avCodec) < 0) {
         esyslog("MPEG PES output: Fatal: could not open codec. Check your ffmpeg installation.");
         return;
      }
      open=true;
   }
   
   running=true;
   
   BENCHMARK(cStatistics encStats;)
   BENCHMARK(cStatistics sendStats;)
   
   PTSGenerator::Start(0);
   
/* Currently, a frame is encoded, sent and then the thread sleeps for some time.
   On my system, a 1.2 Ghz Pentium III with 128 MB RAM, and a fixed sleep of 120
   milliseconds, this typically yields:
   30-45% system load
   20000 microseconds average for colorspace conversion
   25000 microseconds average for encoding
   300 microseconds average for sending
   
   If nothing changes, nothing will be re-encoded, but sent.
*/
   mpegSize=0;
   unsigned char *d=0;
   while (running) {
      //printf("Encoding frame\n");
      
      //If d is non-null, we have changes to be encoded      
      if (d) {
         AVFrame *frame = ( d==avFrame1->data[0] ? avFrame1 : avFrame2 );
         
         BENCHMARK(encStats.Start();)
         mpegSize = avcodec_encode_video(avContext, mpegBuffer, mpegBufferSize, frame);
         BENCHMARK(encStats.Stop();)
         
         BENCHMARK(encStats.Print();)
      }
      
      //Always sends the next frame, be it newly encoded or the same as before
      if (mpegSize) {
         BENCHMARK(sendStats.Start();)
         SendData(mpegBuffer, mpegSize);
         BENCHMARK(sendStats.Stop();)
         BENCHMARK(sendStats.Print();)
      }
      
      //This mutex protects both the cond var and the TwoPointers access
      cMutexLock lock(&mutex);
      
      #ifdef MPEGPES_LAYER_I420
      for (int i=0;i<3;i++)
         condVar.TimedWait(mutex, 40);
      //abuse as flag
      d = (avFrame2 ? avFrame1->data[0] : 0);
      avFrame2=0;
      #else
      //if we has a d, it was encoded and can now be released
      if (d) {
         releaseAvailable(d);
         d=0;
      }
      
      //waits until either a change occurs or the time is up
      //If a change occurred, the cond var will be broadcast so that no time is lost
      for (int i=0;i<15 && !(d=getNextForReading());i++)
         condVar.TimedWait(mutex, 20);
      #endif
      
      /*static int deb=0;
      if (deb++==10) {
         FILE *f=fopen("dump.yuv", "w");
         fwrite(avFrame->data[0], 1, avpicture_get_size(PIX_FMT_YUV420P, width, height), f);
         fclose(f);
      }*/
   }
}

void Encoder::UpdateRegion(DFBRegion *region) {
   //need to see if everything is set up properly.
   //The method might be called in the initialization process
   //when not everything is available.
   if (!init)
      return;
      
   //This mutex protects both the cond var and the TwoPointers access
   cMutexLock lock(&mutex);
   
   #ifndef MPEGPES_LAYER_I420
   unsigned char *d=getNextForWriting();
   BENCHMARK(static cStatistics convertStats;)
   
   //If d is null, something is wrong in the TwoPointers class.
   //There are two buffers so that always one is available for _writing_.
   //Reading may wait, but writing is a bit time-critical (called effectively
   //from the high-level drawing process)
   if (!d) {
      printf("Encoder::UpdateRegion: ERROR!! No d.\n");
      return;
   }
   
   //Locking is probably unnecessary. Anyway, it does not do what you would expect
   //from a Lock() function, i.e. it does not lock in the mutex sense.
   void *ptr;
   int pitch;
   primarySurface->Lock(DSLF_READ, &ptr, &pitch);
   
   AVPicture *pict= (AVPicture *)( d==avFrame1->data[0] ? avFrame1 : avFrame2 );
   
   BENCHMARK(convertStats.Start();)
   img_convert( pict, PIX_FMT_YUV420P, (AVPicture *)avFrameRGB, PIX_FMT_BGR24, width, height);
   BENCHMARK(convertStats.Stop();)
         
   primarySurface->Unlock();
   releaseChanged(d);

   BENCHMARK(convertStats.Print();)
   #else
   //abuse as flag
   avFrame2=1;
   #endif
   
   condVar.Broadcast();
}


/*** PESBuilder ***/

PESBuilder::PESBuilder() : 
   player(0), headerWithPts(true, video_stream_first, true), headerWithoutPts(false, video_stream_first, false)
{
}

void PESBuilder::SendData(unsigned char *data, uint len) {
   /*static FILE *f=0;
   if (!f)
         f=fopen("dump.mpg", "w");*/
   //printf("Sending frame with timestamp %lld\n", GetTimestamp());
   
   pts.SetPts(GetTimestamp());
   PESHeader *header=&headerWithPts;
   bool first=true;
   unsigned char *ptr=data;
   
   //The data is wrapped in PES packets with maximum size PES_MAX_SIZE (2048)
   //The very first PES header will give PTS (timing) information, while
   //the subsequent headers won't.

//I cannot tell which version is faster
#ifdef NO_MEMCPY
   //optimized to avoid memcpy
   while (len > 0) {
      int payload_size = len;
      int header_size = (first ? sizeof(PESHeader)+sizeof(PTS) : sizeof(PESHeader));

      if (payload_size + header_size > PES_MAX_SIZE)
         payload_size = PES_MAX_SIZE - header_size;
         
      //The first 6 bytes of the PES header are not counted in the packet_length field.
      header->SetLength(payload_size + header_size - 6);
      if (first) {
         //fwrite((uchar *)header, 1, sizeof(PESHeader), f);
         player->PlayVideo((uchar *)header, sizeof(PESHeader));
         //fwrite((uchar *)&pts, 1, sizeof(PTS), f);
         player->PlayVideo((uchar *)&pts, sizeof(PTS));
         first=false;
         header=&headerWithoutPts;
      } else {
         //fwrite((uchar *)header, 1, sizeof(PESHeader), f);
         player->PlayVideo((uchar *)header, sizeof(PESHeader));
      }
         
      //fwrite(ptr, 1, payload_size, f);
      player->PlayVideo(ptr, payload_size);
      
      len -= payload_size;
      ptr += payload_size;
   }
#else
   //optimized to reduce PlayVideo calls
   unsigned char buffer[len + (len/(PES_MAX_SIZE-sizeof(PESHeader)) +1)*(sizeof(PESHeader)) + sizeof(PTS) + 100];
   unsigned char *b=buffer;
   while (len > 0) {
      int payload_size = len;
      int header_size = (first ? sizeof(PESHeader)+sizeof(PTS) : sizeof(PESHeader));

      if (payload_size + header_size > PES_MAX_SIZE)
         payload_size = PES_MAX_SIZE - header_size;
         
      //The first 6 bytes of the PES header are not counted in the packet_length field.
      header->SetLength(payload_size + header_size - 6);
      if (first) {
         memcpy(b, (uchar *)header, sizeof(PESHeader));
         b+=sizeof(PESHeader);
         
         memcpy(b, (uchar *)&pts, sizeof(PTS));
         b+=sizeof(PTS);
         
         first=false;
         header=&headerWithoutPts;
      } else {
         memcpy(b, (uchar *)header, sizeof(PESHeader));
         b+=sizeof(PESHeader);
      }
         
      memcpy(b, ptr, payload_size);
      b+=payload_size;
            
      len -= payload_size;
      ptr += payload_size;
   }
   player->PlayVideo(buffer, b-buffer);
#endif
}


/*** PTSGenerator ***/

PTSGenerator::PTSGenerator() : speed_factor(90000.0) {
   Start(0);
}

void PTSGenerator::Start(unsigned long long startPts) {
  gettimeofday(&cur_time, NULL);
  cur_pts = startPts;
}

void PTSGenerator::Adjust(unsigned long long pts) {
  struct   timeval tv;
  gettimeofday(&tv, NULL);
  cur_time.tv_sec=tv.tv_sec;
  cur_time.tv_usec=tv.tv_usec;
  cur_pts = pts;
}

unsigned long long PTSGenerator::GetTimestamp() {
  struct   timeval tv;
  double   pts_calc; 

  gettimeofday(&tv, NULL);
  
  pts_calc = (tv.tv_sec  - cur_time.tv_sec) * speed_factor;
  pts_calc += (tv.tv_usec - cur_time.tv_usec) * speed_factor / 1e6;

  return cur_pts + (unsigned long long)pts_calc;
}



//Utility for performance measurement
cStatistics::cStatistics() {
   timerclear(&min);
   timerclear(&max);
   timerclear(&average);
   timerclear(&last);
   count=0;
}

void cStatistics::Start() {
   gettimeofday(&last, 0);
}

void cStatistics::Stop() {
   timeval timenow,diff;
   gettimeofday(&timenow, 0);
   timersub(&timenow,&last,&diff);
   count++;
   if (diff.tv_sec==0 && diff.tv_usec==0)
      return;
   if (timercmp(&min, &diff, > ) || !timerisset(&min))
      min=diff;
   if (timercmp(&max, &diff, < ))
      max=diff;
   average.tv_sec= (long) ((double)( average.tv_sec*(count-1) + diff.tv_sec ) / (double)( count ));
   average.tv_usec= (long) ((double)( average.tv_usec*(count-1) + diff.tv_usec ) / (double)( count ));
   if (average.tv_usec >= 1000000) {
      average.tv_sec++;
      average.tv_usec-=1000000;
   }
}

void cStatistics::Print() {
   printf("MpegPES: Statistics min/avg/max = %ld:%ld/%ld:%ld/%ld:%ld sec:microsec\n",
            min.tv_sec, min.tv_usec, average.tv_sec, average.tv_usec, max.tv_sec, max.tv_usec);
}





} //end of namespace




/*** C-Functions ***/

extern "C" {

int mpegpes_get_default_width() {
   return MhpOutput::System::self()->GetDisplayWidth();
}

int mpegpes_get_default_height() {
   return MhpOutput::System::self()->GetDisplayHeight();
}

DFBResult  mpegpes_set_configuration(int width, int height, DFBSurfacePixelFormat pixelformat) {
   ((MhpOutput::MpegPesSystem *)MhpOutput::System::self())->SetConfiguration(width, height, pixelformat);
   return DFB_OK;
}

DFBResult  mpegpes_update_region(DFBRegion *region) {
   ((MhpOutput::MpegPesSystem *)MhpOutput::System::self())->UpdateRegion(region);
   return DFB_OK;
}

void *mpegpes_allocate(int width, int height) {
   return ((MhpOutput::MpegPesSystem *)MhpOutput::System::self())->GetBuffer(width, height);
}

}//end of "C"

MHPOUTPUTPLUGINCREATOR(MhpOutput::MpegPesSystem)


