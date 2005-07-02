/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *       parts taken from MP3 plugin,                                      *
 *          (c) 2001,2002 Stefan Huelswitt <huels@iname.com>               *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include <vdr/device.h>
#include <vdr/config.h>
#include <vdr/skins.h>

#include <libjava/javainterface.h>
#include <libait/ait.h>
#include <libmhpoutput/outputadministration.h>

#include "mhpcontrol.h"
#include "mhpmessages.h"
#include "i18n.h"


/* --------- MhpControl ------------- */


//static function
void MhpControl::Start(ApplicationInfo::cApplication *a) {
   if (!MhpOutput::Administration::CheckSystem()) {
      MhpMessages::DisplayMessage(MhpMessages::OutputSystemError);
      CheckMessage();
      return;
   }
   if (!JavaInterface::CheckSystem()) {
      MhpMessages::DisplayMessage(MhpMessages::JavaSystemError);
      CheckMessage();
      return;
   }
   
   //we can allow any status of the MhpControl object here,
   //old object is deleted by cControl::Launch, stopping everything.
   
   //When created, MhpControl will create an MhpPlayer.
   //As soon as VDR attached this player, MhpPlayer::Activate is called,
   //which in turn will call MhpControl::StartMhp().
   printf("MhpControl::Start(): Launching control\n");
   cControl::Launch(new MhpControl(a));
}

MhpMessages::Messages MhpMessages::message = MhpMessages::NoMessage;
const char *MhpConfigPath = 0;

//static function
//Called from the Java stack
void MhpMessages::DisplayMessage(Messages m) {
   message=m;
}


MhpControl::MhpControl(ApplicationInfo::cApplication *a) 
 : cControl (player=new MhpPlayer(this)) 
{   
   app=a;
   monitor=0;   
   status=Waiting;
   
   display=0;
   visible=false;
   doShow=true;
   
   appName=unknownName=tr("<unknown name>");
}

MhpControl::~MhpControl() {
   Hide();
   Stop();
}

void MhpControl::Hide(void) {
   if (visible) {
      delete display;
      display=0;
      //Interface->Close();
      visible=false;
   }
}

void MhpControl::Stop() {
   printf("MhpControl::Stop()\n");
   delete monitor;
   monitor=0;
   if (status==Running)
      JavaInterface::StopApplications();
   //detach main thread from VM - doesn't work with kaffe anyway, but I hope it will one day
   JavaInterface::CheckDetachCurrentThread();
   //set status to Stopped so that osEnd is returned if ProcessKey is called again
   //(asynchronous stop)
   status=Stopped;
   delete player;
   player=0;
   MhpMessages::message=MhpMessages::NoMessage;
}

eOSState MhpControl::ProcessKey(eKeys Key) {
   //status shall be set to Waiting if and only if returning osEnd
   switch (status) {
   case Stopped:
      status=Waiting;
      return osEnd;
   case Waiting:
      break;
   /*case Loading: 
    {
      switch (Key) {
      case kBack:
         Hide();
         Stop();
         status=Waiting;
         return osEnd;
      case kOk:
         if (visible) {
            doShow=false;
            Hide();
         } else
            doShow=true;
      default:
         LoadingManager::getManager()->OnceASecond(this);
         CheckMessage();
      }
      break;
    }*/
   case Running:
      switch (Key) {
      case kBack:
         printf("Status Running, kBack\n");
         Hide();
         Stop();
         status=Waiting;
         return osEnd;
      case kNone:
         MhpLoadingManager::getManager()->OnceASecond(this);
         CheckMessage();
         break;
      default:
         JavaInterface::ProcessKey(Key);
      }
      break;
   }
   return osContinue;
}

void MhpControl::CheckMessage() {
   switch (MhpMessages::message) {
   case MhpMessages::NoMessage:
      break;
   case MhpMessages::LoadingFailed:
      Skins.Message(mtError, tr("Loading failed"));
      break;
   case MhpMessages::StartingFailed:
      Skins.Message(mtError, tr("Starting failed"));
      break;
   case MhpMessages::OutputSystemError:
      Skins.Message(mtError, tr("Error in output system: Plugin disabled"));
      break;
  case MhpMessages::JavaSystemError:
      Skins.Message(mtError, tr("Error in Java system: Plugin disabled"));
      break;
  case MhpMessages::JavaStartError:
      Skins.Message(mtError, tr("Error while starting Java environment"));
      break;
   }
   MhpMessages::message=MhpMessages::NoMessage;

}


void MhpControl::ShowProgress(float progress, int currentSize, int totalSize) {
   if (!doShow)
      return;
   if (!visible) {
      display = Skins.Current()->DisplayReplay(false);
      char *title;
      if (asprintf(&title, tr("Loading %s"), appName.c_str()) != -1) {
         display->SetTitle(title);
         free(title);
      }
      visible=true;
   }
   //int progressPercentage=(int)(100.0 *progress);
   display->SetProgress(currentSize, totalSize);
   char text[32];
   snprintf(text, sizeof(text), "%d B", currentSize);
   display->SetCurrent(text);
   if (totalSize != 0) {
      //initially, totalSize maybe 0, don't display this
      snprintf(text, sizeof(text), "%d B", totalSize);
      display->SetTotal(text);
   }
   
}

void MhpControl::SetApplicationName(const std::string &a) {
   appName=a;
}

void MhpControl::HideProgress() {
   Hide();
   appName=unknownName;
}


//#include <dfb++/dfb++.h>
//#include <libmhpoutput/output.h>
void MhpControl::StartMhp() {
   printf("MhpControl::StartMhp()\n");
   //Check that everything essential is working, as far as we can see it.
   if (!JavaInterface::CheckStart()) {
      Hide();
      MhpMessages::DisplayMessage(MhpMessages::JavaStartError);
      CheckMessage();
      Stop();
      return;
   }
   //Now the ApplicationManager in the Java stack will take control.
   //It will interact with the MhpLoadingManager if necessary, 
   //also starting DSMCC downloads.
   //MhpControl's ProcessKey will call MhpLoadingManager::OnceASecond
   //to take care for OSD progress display if necessary.
   if (JavaInterface::StartApplication(app)) {
      status=Running;
      monitor=new cMyApplicationStatus();
      //Actually activate output on TV
      player->ActivateParent();
   }
   
   //Simple testcase for the output system
   /*player->ActivateParent();
   IDirectFBDisplayLayer *l=MhpOutput::System::self()->GetMainLayer();
   try {
     IDirectFBWindow       *window, *   window2;
     DFBWindowDescription  desc;
     //l->SetCooperativeLevel(DLSCL_EXCLUSIVE);
     desc.flags=(DFBWindowDescriptionFlags)0;
     
     if (!getenv( "MHP_NO_ALPHA" )) {
          desc.caps=(DFBWindowCapabilities)0;
          DFB_ADD_WINDOW_DESC(desc.flags, DWDESC_CAPS);
          DFB_ADD_WINDOW_CAPS(desc.caps, DWCAPS_ALPHACHANNEL);
          DFB_ADD_WINDOW_CAPS(desc.caps, DWCAPS_DOUBLEBUFFER);
     }
          DFB_ADD_WINDOW_DESC(desc.flags, DWDESC_POSX);
          desc.posx = 0;
          DFB_ADD_WINDOW_DESC(desc.flags, DWDESC_POSY);
          desc.posy = 0;
          DFB_ADD_WINDOW_DESC(desc.flags, DWDESC_WIDTH);
          desc.width = 720;
          DFB_ADD_WINDOW_DESC(desc.flags, DWDESC_HEIGHT);
          desc.height = 576;
      window=l->CreateWindow(desc);
      desc.posx=100;desc.posy=100;desc.width=100;desc.height=75;
      window2=l->CreateWindow(desc);
      window->SetStackingClass(DWSC_MIDDLE);
      window2->SetStackingClass(DWSC_UPPER);
      IDirectFBSurface *s=window->GetSurface();
      IDirectFBSurface *s2=window2->GetSurface();
      
      DFBFontDescription fdesc;fdesc.flags=DFDESC_HEIGHT;fdesc.height=25;
      IDirectFBFont *font=MhpOutput::System::self()->Interface()->CreateFont("/usr/local/vdr/mhp/data/fonts/vera.ttf", fdesc);
      //IDirectFBSurface *s=l->GetSurface();
      printf("Starting to draw\n");
      s->SetDrawingFlags(DSDRAW_BLEND);
      s->SetColor(255, 0, 0, 255);
      s->FillRectangle(5, 5, 300, 450);
      s->SetColor(230, 230, 230, 124);
      s->FillTriangle(300, 20, 20, 500, 500, 550);
      s->SetColor(0, 0, 0, 255);
      s->SetFont(font);
      s->DrawString("Hallo, was soll das?", 20, 300, 250, DSTF_LEFT);
      s->Flip();
      s2->SetColor(0, 0, 0, 0);
      s2->FillRectangle(0,0,100,75);
      s2->SetColor(120, 120, 0, 120);
      s2->FillTriangle(20,20,70,20,45,70);
      s2->Flip();
      window->SetOpacity(0xff);
      window2->SetOpacity(0xff);
      printf("Drawn\n");
      //l->GetSurface()->Blit(s, 0, 0, 0);
      //IDirectFBSurface *ls=l->GetSurface();
      //void *ptr;
      //int pitch;
      //s->Lock(DSLF_READ, &ptr, &pitch);
      //FILE *fd=fopen("surface.rgb", "w");
      //int width, height;
      //s->GetSize(&width, &height);
      //printf("Dumping surface of format %d, %dx%d, %d, address %p\n", s->GetPixelFormat(), width, height, pitch, ptr);
      //fwrite(ptr, DFB_BYTES_PER_LINE(s->GetPixelFormat(), width)*height, 1, fd);
      //fclose(fd);
      //s->Unlock();
      
      //window->Release();
      //window2->Release();
      //s->Release();
      //s2->Release();
   } catch (DFBException *e) {
      printf("DirectFB: Error %s, %s\n", e->GetAction(), e->GetResult());
      delete e;
   }*/
  
   
}

void MhpControl::cMyApplicationStatus::NewApplication(ApplicationInfo::cApplication *app) {
   JavaInterface::NewApplication(app);
}

void MhpControl::cMyApplicationStatus::ApplicationRemoved(ApplicationInfo::cApplication *app) {
   JavaInterface::ApplicationRemoved(app);
}


/* --------- MhpPlayer ------------- */


MhpPlayer::~MhpPlayer() {
   Detach();
}

void MhpPlayer::Activate(bool On) {
   //printf("MhpPlayer::Activate %d\n", On);
   if (On) {
      //intercept activation to display loading progress
      control->StartMhp();
   } else 
      Player::Activate(On);
}

void MhpPlayer::ActivateParent() {
   Player::Activate(true);
}


/* --------- MhpLoadingManager ------------- */


MhpLoadingManager *MhpLoadingManager::s_self=0;

MhpLoadingManager *MhpLoadingManager::getManager() {
   if (!s_self)
      s_self=new MhpLoadingManager;
   return s_self;
}

void MhpLoadingManager::CleanUp() {
   delete s_self;
}

MhpLoadingManager::MhpLoadingManager() : hibernatedCount(0), loadingApp(0) {
   watch = new MhpChannelWatch();
   preloader = new MhpCarouselPreloader();
}

MhpLoadingManager::~MhpLoadingManager() {
   s_self=0;
   for (AppMap::iterator it=apps.begin(); it != apps.end(); ++it) {
      delete it->second;
   }
   apps.clear();
   delete watch;
   delete preloader;
}

/*void Add(ApplicationInfo::cApplication *a) {
   AppMap::Iterator it=apps.find(a);
   if (it == apps.end()) {
      apps[a]=new MhpCarouselLoader(a);
   }
}*/

void MhpLoadingManager::Load(ApplicationInfo::cApplication *a) {
   printf("MhpLoadingManager::Load\n");
   cMutexLock lock(&mutex);
   AppMap::iterator it=apps.find(a);
   if (it == apps.end()) {
      MhpCarouselLoader *loader=new MhpCarouselLoader(a);
      apps[a]=loader;
      loadingApp=loader;
      loader->Start();
   } else {
      switch (it->second->getState()) {
      case LoadingStateWaiting:
         loadingApp=it->second;
         it->second->Start();
         break;
      case LoadingStateHibernated:
         loadingApp=it->second;
         it->second->WakeUp();
         break;
      default:
         break;
      }
   }
}

void MhpLoadingManager::Stop(ApplicationInfo::cApplication *a) {
   printf("MhpLoadingManager::Stop\n");
   cMutexLock lock(&mutex);
   AppMap::iterator it=apps.find(a);
   if (it != apps.end()) {
      //manage hibernating
      MhpCarouselLoader *l=it->second;
      if (l==loadingApp)
         loadingApp=0;
      l->Hibernate();
      if (++hibernatedCount >= MAX_HIBERNATED_APPS) {
         MhpCarouselLoader *oldest=0;
         time_t oldestTime=0;
         for (it=apps.begin();it!=apps.end();++it) {
            if (it->second->getState()==LoadingStateHibernated && it->second->getHibernationTime()>oldestTime) {
               oldest=it->second;
               oldestTime=oldest->getHibernationTime();
            }
         }
         oldest->Stop();
         hibernatedCount--;
      }
   }
}

/*void Hibernate() {
}*/

LoadingState MhpLoadingManager::getState(ApplicationInfo::cApplication *a) {
   printf("MhpLoadingManager::getState\n");
   cMutexLock lock(&mutex);
   AppMap::iterator it=apps.find(a);
   if (it != apps.end()) {
      return it->second->getState();
   }
   return LoadingStateWaiting;
}

SmartPtr<Cache::Cache> MhpLoadingManager::getCache(ApplicationInfo::cApplication *a) {
   cMutexLock lock(&mutex);
   AppMap::iterator it=apps.find(a);
   if (it != apps.end()) {
      return it->second->getCache();
   }
   return SmartPtr<Cache::Cache>(0);
}

void MhpLoadingManager::NewApplication(ApplicationInfo::cApplication *app) {
}

void MhpLoadingManager::ApplicationRemoved(ApplicationInfo::cApplication *app) {
   cMutexLock lock(&mutex);
   AppMap::iterator it=apps.find(app);
   if (it != apps.end()) {
      it->second->Stop();
      apps.erase(it);
   }   
}

void MhpLoadingManager::OnceASecond(ProgressIndicator *pi) {
   cMutexLock lock(&mutex);
   //There may be more than one loading app, loadingApp is the last started
   if (loadingApp) {
      switch (loadingApp->getState()) {
      case LoadingStateError:
         MhpMessages::DisplayMessage(MhpMessages::LoadingFailed);
         loadingApp=0;
         break;
      case LoadingStateLoading:
         break;
      default:
         loadingApp=0;
         break;
      }
      
      if (!loadingApp) {
         //if there is another app loading, find this one
         for (AppMap::iterator it=apps.begin();it!=apps.end();++it) {
            if (it->second->getState()==LoadingStateLoading) {
               loadingApp=it->second;
               break;
            }
         }
      }
      
      if (!loadingApp)
         //no app loading: Hide status bar
         pi->HideProgress();
      else {
         ApplicationInfo::cApplication::ApplicationName *name=loadingApp->getName();
         if (name)
            pi->SetApplicationName(name->name);
         int currentSize, totalSize;
         float progress=loadingApp->getProgress(currentSize, totalSize);
         pi->ShowProgress(progress, currentSize, totalSize);
      }
   }
}

/* --------- MhpChannelWatch ------------ */

void MhpChannelWatch::ChannelSwitch(const cDevice *Device, int ChannelNumber) {
}


/* --------- MhpCarouselPreloader ------------- */

MhpCarouselPreloader::MhpCarouselPreloader() 
  : SchedulerBySeconds(10),
    currentLoader(0)
{
}

//!!
//Attention: the implementation is currently simple, there will be only one TimedPreloader at a time.
//This is sufficient as long as only the CurrentChannel is monitored!
//If all devices are monitored and a channel switch on any device causes a call of this
//method here, there must be a map Transport Stream -> TimedPreloader maintained here!!
void MhpCarouselPreloader::PreloadForTransportStream(ApplicationInfo::TransportStreamID newTs) {
   if (!(newTs == ts)) {
      Remove(currentLoader);
      delete currentLoader;
      currentLoader=new TimedPreloader(newTs);
   }
}

//Wait 30 seconds before starting prefetching.
//This waiting period will be stopped as soon as the user switches to a different transponder.
#define INITIAL_WAIT 30
//Check status of loading app every 15 seconds.
#define CHECK_PERIOD 15
//Preload again to check for changes every ten minutes
#define REPRELOAD_WAIT 10*60

MhpCarouselPreloader::TimedPreloader::TimedPreloader(ApplicationInfo::TransportStreamID newTs)
  : TimedBySeconds(INITIAL_WAIT),
    loading(false), ts(newTs)
{
}

//No need for a destructor. MhpLoadingManager will take care for hibernation/detaching.

void MhpCarouselPreloader::TimedPreloader::Execute() {
   if (loading) {
      if (MhpLoadingManager::getManager()->getState((*currentPosition)) != LoadingStateLoading) {
         //preload next in list
         ++currentPosition;
         //if end is reached, wait REPRELOAD_WAIT for the next round.
         if (currentPosition == apps.end()) {
            ChangeInterval(REPRELOAD_WAIT);
            loading=false;
         } else {
            MhpLoadingManager::getManager()->Load((*currentPosition));
            ChangeInterval(CHECK_PERIOD);
            loading=true;
         }
      }
   } else {
      if (ApplicationInfo::Applications.findApplicationsForTransportStream(apps, ts.source, ts.nid, ts.tid) && apps.size()) {
         currentPosition=apps.begin();
         if (currentPosition != apps.end()) {
            MhpLoadingManager::getManager()->Load((*currentPosition));
            ChangeInterval(CHECK_PERIOD);
            loading=true;
         }
      } else {
         ChangeInterval(REPRELOAD_WAIT);
         loading=false;
      }
   }
}

/* --------- MhpCarouselLoader ------------- */


MhpCarouselLoader::MhpCarouselLoader(ApplicationInfo::cApplication *a) 
  : app(a), receiver(0), carousel(0), state(LoadingStateWaiting), hibernatedTime(0), totalSize(0)
{
   protocol=app->GetTransportProtocol()->GetProtocol();
}

MhpCarouselLoader::~MhpCarouselLoader() {
   Stop();
}

LoadingState MhpCarouselLoader::getState() {
   //always check if loading completed
   if (state==LoadingStateLoading) {
      int a,b;
      getProgress(a,b);
   }
   return state;
}

float MhpCarouselLoader::getProgress(int &currentSize, int &retTotalSize) {
   switch (state) {
      case LoadingStateError:
      case LoadingStateWaiting:
         currentSize=0;
         retTotalSize=0;
         return 0.0;
      case LoadingStateLoading:
         {
         //note that state Loading implies a non-local app
         float progress=carousel->getProgress(&currentSize, &totalSize);
         retTotalSize=totalSize;
         if (progress==1.0)
            state=LoadingStateLoaded;
         return progress;
         }
      case LoadingStateLoaded:
         currentSize=totalSize;
         retTotalSize=totalSize;
         return 1.0;
      case LoadingStateHibernated:
         currentSize=0;
         retTotalSize=0;
         return 0.0;
      default:
         currentSize=0;
         retTotalSize=0;
         return 0.0;
   }
}

void MhpCarouselLoader::Start() {
   switch(protocol) {
      case ApplicationInfo::cTransportProtocol::ObjectCarousel:
         return StartObjectCarousel();
      case ApplicationInfo::cTransportProtocol::Local:
         return StartLocalApp();
      default:
         return;
   }
}

void MhpCarouselLoader::Stop() {
   delete receiver;
   receiver=0;
   //unless hibernated, deleted by receiver
   if (state==LoadingStateHibernated)
      delete carousel;
   carousel=0;
   state=LoadingStateWaiting;
}

void MhpCarouselLoader::Hibernate() {
   if ( (state == LoadingStateLoading || state == LoadingStateLoaded)
         && protocol != ApplicationInfo::cTransportProtocol::Local) {
      state=LoadingStateHibernated;
      carousel=receiver->HibernateCarousel(carousel->getId());
      delete receiver;
      receiver=0;
      hibernatedTime=time(0);
   }
}

void MhpCarouselLoader::WakeUp() {
   if (state==LoadingStateHibernated && protocol != ApplicationInfo::cTransportProtocol::Local) {
      StartObjectCarousel(carousel);
      //status is set by StartObjectCarousel
      hibernatedTime=0;
   }
}

void MhpCarouselLoader::StartObjectCarousel(Dsmcc::ObjectCarousel *hibernated) {
   ApplicationInfo::cTransportProtocolViaOC *tp=dynamic_cast<ApplicationInfo::cTransportProtocolViaOC*>(app->GetTransportProtocol());
   //find device
   cDevice *dev=cDevice::GetDevice(app->GetChannel(), 0);
   //identify service
   ApplicationInfo::cTransportStream::Service *service=app->GetService();
   //create receiver
   receiver=new cDsmccReceiver(service->GetChannel()->Name());
   //add possible streams
   std::list<ApplicationInfo::cTransportStream::Component>::iterator it;
   for (it=service->GetComponents()->begin(); it != service->GetComponents()->end(); ++it) {
      receiver->AddStream( (*it).pid, (*it).componentTag );
      printf("Adding assoc_tag %d\n", (*it).componentTag);
   }
   //create or add carousel
   if (hibernated)
      carousel=receiver->AddHibernatedCarousel(hibernated);
   else
      carousel=receiver->AddCarousel(tp->GetCarouselId());
   //inform app about the cache in which its data is stored
   tp->SetCache(carousel->getCache());
   //identify the stream carrying the DSI (assoc_tag has already been added above)
   receiver->AddStream(tp->GetPid(), 0, carousel);
   //attach to device
   dev->AttachFilter(receiver);
   //make the receiver receive the main stream
   receiver->ActivateStream(tp->GetPid());
   state=LoadingStateLoading;   
}

void MhpCarouselLoader::StartLocalApp() {
   state=LoadingStateLoaded;
}

SmartPtr<Cache::Cache> MhpCarouselLoader::getCache() {
   if (carousel)
      return carousel->getCache();
   else
      return SmartPtr<Cache::Cache>(0);
}

ApplicationInfo::cApplication::ApplicationName *MhpCarouselLoader::getName() {
   if (app->GetNumberOfNames())
      return app->GetName(0);
   else
      return 0;
}



/* --------- cProgressBar ------------- */


/*cProgressBar::cProgressBar(int Width, int Height, int Current, int Total)
:cBitmap(Width, Height, 2)
{
  if(Total > 0) {
    int p = Current * Width / Total;;
#if VDRVERSNUM >= 10307
    DrawRectangle(0, 0, p, Height - 1, clrGreen);
    DrawRectangle(p + 1, 0, Width - 1, Height - 1, clrWhite);
#else
    Fill(0, 0, p, Height - 1, clrGreen);
    Fill(p + 1, 0, Width - 1, Height - 1, clrWhite);
#endif
    }
}*/





