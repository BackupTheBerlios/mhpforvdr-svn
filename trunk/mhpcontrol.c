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

namespace Mhp {

/* --------- Control ------------- */


Messages::Message Messages::message = Messages::NoMessage;
const char *ConfigPath = 0;

//static function
void Messages::DisplayMessage(Message m) {
   message=m;
}

Control::Control() 
 : cControl (player=new Player(this)), app(0)
{
   status=Waiting;
   
   display=0;
   visible=false;
   doShow=true;
   
   appName=unknownName=tr("<unknown name>");
}

Control::~Control() {
   Hide();
   Stop();
}

void Control::Hide(void) {
   if (visible) {
      delete display;
      display=0;
      //Interface->Close();
      visible=false;
   }
}

void Control::Stop() {
   printf("Control::Stop()\n");
   //set status to Stopped so that osEnd is returned when ProcessKey is called again
   //(asynchronous stop)
   status=Stopped;
   delete player;
   player=0;
   Messages::message=Messages::NoMessage;
}

eOSState Control::ProcessKey(eKeys Key) {
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
         ControlLoadingManager::getManager()->ProgressInfo(this);
         CheckMessage();
      }
      break;
    }*/
   case Running:
      switch (Key) {
      case kBack:
         printf("Status Running, kBack\n");
         RunningManager::getManager()->Stop(app);
         ((ControlRunningManager *)RunningManager::getManager())->ShutdownControl();
         status=Waiting;
         return osEnd;
      case kNone:
         LoadingManager::getManager()->ProgressInfo(this);
         CheckMessage();
         break;
      default:
         JavaInterface::ProcessKey(Key);
      }
      break;
   }
   return osContinue;
}

void Control::CheckMessage() {
   switch (Messages::message) {
   case Messages::NoMessage:
      break;
   case Messages::LoadingFailed:
      Skins.Message(mtError, tr("Loading failed"));
      break;
   case Messages::StartingFailed:
      Skins.Message(mtError, tr("Starting failed"));
      break;
   case Messages::OutputSystemError:
      Skins.Message(mtError, tr("Error in output system: Plugin disabled"));
      break;
  case Messages::JavaSystemError:
      Skins.Message(mtError, tr("Error in Java system: Plugin disabled"));
      break;
  case Messages::JavaStartError:
      Skins.Message(mtError, tr("Error while starting Java environment"));
      break;
  case Messages::AlreadyRunning:
      Skins.Message(mtInfo, tr("The application has already been started"));
   }
   Messages::message=Messages::NoMessage;

}


void Control::ShowProgress(float progress, int currentSize, int totalSize) {
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

void Control::SetApplicationName(const std::string &a) {
   appName=a;
}

void Control::HideProgress() {
   Hide();
   appName=unknownName;
}

void Control::SetApplication(ApplicationInfo::cApplication::Ptr a) {
   app=a;
}

//#include <dfb++/dfb++.h>
//#include <libmhpoutput/output.h>
void Control::StartMhp() {
   printf("Control::StartMhp()\n");
   if (status==Stopped)
      return;
   //Check that everything essential is working, as far as we can see it.
   /*if (!JavaInterface::CheckStart()) {
      Hide();
      Messages::DisplayMessage(Messages::JavaStartError);
      CheckMessage();
      Stop();
      return;
   }*/
   //Now the ApplicationManager in the Java stack will take control.
   //It will interact with the ControlLoadingManager if necessary, 
   //also starting DSMCC downloads.
   //Control's ProcessKey will call ControlLoadingManager::ProgressInfo
   //to take care for OSD progress display if necessary.
   if (app && JavaInterface::StartApplication(app)) {
      status=Running;
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
      //IDirec   printf("RunningManager: Notified about start of application %p\n", a.getPointer());
tFBSurface *s=l->GetSurface();
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

/* --------- Player ------------- */


Player::~Player() {
   Detach();
}

void Player::Activate(bool On) {
   //printf("Player::Activate %d\n", On);
   if (On) {
      //intercept activation to display loading progress
      control->StartMhp();
   } else 
      MhpOutput::Player::Activate(On);
}

void Player::ActivateParent() {
   MhpOutput::Player::Activate(true);
}


/* --------- RunningManager and ControlRunningManager ------------- */

/*
   The running manager handles the starting of applications initiated
   from the ApplicationMenu or possibly other parts of the VDR plugin in contrast
   two the facilities provided by the ApplicationManager and the org.dvb.application
   package on the Java side. This Java code keeps the state machine for applications
   and is perfectly capable of initiating loading and starting itself. It will
   however inform the RunningManager about starting and stopping.
   The RunningManager keeps a list of running applications, creates and launches the
   cControl object when the first application is started and deletes it when
   the last application is stopped.
   In theory multiple applications can run concurrently, but this will only be
   initiated from the Java stack. The running manager has the notion of a
   foreground application which is launched from the ApplicationMenu. Launching
   another application via the RunningManager will therefore cause the currently running
   one to be stopped.
   
   This policy may be adapted to future needs.
*/

RunningManager *RunningManager::s_self=0;

RunningManager *RunningManager::getManager() {
   if (!s_self)
      s_self=new ControlRunningManager;
   return s_self;
}

RunningManager::RunningManager() {
   s_self = this;
}

RunningManager::~RunningManager() {
   s_self = 0;
}

ControlRunningManager::ControlRunningManager() 
  : foregroundApp(0), control(0)
{
}

ControlRunningManager::~ControlRunningManager() {
}

void ControlRunningManager::Initialize() {
   //create manager singleton
   getManager();
}

void ControlRunningManager::CleanUp() {
   delete this;
}

//Start the given application
void ControlRunningManager::Start(ApplicationInfo::cApplication::Ptr app) {
   cMutexLock lock(&mutex);
   
   //check if app is already running
   if (foregroundApp==app || apps.find(app) != apps.end()) {
      Messages::DisplayMessage(Messages::AlreadyRunning);
      Control::CheckMessage();
      return;
   }
   
   //perform availability checks
   if (!MhpOutput::Administration::CheckSystem()) {
      Messages::DisplayMessage(Messages::OutputSystemError);
      Control::CheckMessage();
      return;
   }
   if (!JavaInterface::CheckSystem()) {
      Messages::DisplayMessage(Messages::JavaSystemError);
      Control::CheckMessage();
      return;
   }
   
   //Start Java VM (if not already running)
   if (!JavaInterface::CheckStart()) {
      Messages::DisplayMessage(Messages::JavaStartError);
      Control::CheckMessage();
      return;
   }
   
   //stop current foreground app
   if (foregroundApp && foregroundApp != app) {
      Stop(foregroundApp);
   }
   
   if (!control) {
      //There is currently no control object, and we cannot be 100% sure that attaching the cControl/cPlayer
      //will be allowed. So we defer further loading:
      //When created, Control will create a Player.
      //As soon as VDR attached this player, Player::Activate is called,
      //which in turn will call StartMhp()
      control = new Mhp::Control();
      printf("Control::Start(): Launching control %p\n", control);
      control->SetApplication(app);
      cControl::Launch(control);
   } else {
      control->SetApplication(app);
      control->StartMhp();
   }
}

//Stop the given application, return control to VDR if no other application remains running
void ControlRunningManager::Stop(ApplicationInfo::cApplication::Ptr a) {
   cMutexLock lock(&mutex);
   JavaInterface::StopApplication(a);
}

//Stop all applications, return control to VDR
void ControlRunningManager::Stop() {
   cMutexLock lock(&mutex);
   if (control) {
      //stop all applications
      JavaInterface::StopApplications();
      //detach main thread from VM
      JavaInterface::CheckDetachCurrentThread();
      ShutdownControl();
   }
}

void ControlRunningManager::ShutdownControl() {
   cMutexLock lock(&mutex);
   control->Stop();
   //do not delete control, ownership is passed to VDR
   control=0;
}

//Inform manager that the given application has been started
void ControlRunningManager::ApplicationStarted(ApplicationInfo::cApplication::Ptr a) {
   cMutexLock lock(&mutex);
   printf("RunningManager: Notified about start of application %p\n", a.getPointer());
   apps.insert(a);
}

//Inform manager that the given application is no longer running;
//the manager may return control to VDR if no other application remains running
void ControlRunningManager::ApplicationStopped(ApplicationInfo::cApplication::Ptr a) {
   cMutexLock lock(&mutex);
   printf("RunningManager: Notified about stop of application %p\n", a.getPointer());
   apps.erase(a);
   if (apps.empty()) {
      Stop();
   }
}

void ControlRunningManager::NewApplication(ApplicationInfo::cApplication::Ptr app) {
   JavaInterface::NewApplication(app);
}

void ControlRunningManager::ApplicationRemoved(ApplicationInfo::cApplication::Ptr app) {
   JavaInterface::ApplicationRemoved(app);
}

/* --------- LoadingManager and ControlLoadingManager ------------- */


LoadingManager *LoadingManager::s_self=0;

LoadingManager *LoadingManager::getManager() {
   if (!s_self)
      s_self=new ControlLoadingManager;
   return s_self;
}

LoadingManager::LoadingManager() {
   s_self = this;
}

LoadingManager::~LoadingManager() {
   s_self = 0;
}

void ControlLoadingManager::Initialize() {
   //create manager singleton
   getManager();
}

void ControlLoadingManager::CleanUp() {
   delete this;
}

ControlLoadingManager::ControlLoadingManager() : hibernatedCount(0), loadingApp(0) {
   preloader = new CarouselPreloader();
   watch = new ChannelWatch(preloader);
   selectionProvider = new ControlServiceSelectionProvider(watch);
}

ControlLoadingManager::~ControlLoadingManager() {
   s_self=0;
   for (AppMap::iterator it=apps.begin(); it != apps.end(); ++it) {
      delete it->second;
   }
   apps.clear();
   delete selectionProvider;
   delete watch;
   delete preloader;
}

/*void Add(ApplicationInfo::cApplication::Ptr a) {
   AppMap::Iterator it=apps.find(a);
   if (it == apps.end()) {
      apps[a]=new CarouselLoader(a);
   }
}*/

void ControlLoadingManager::Load(ApplicationInfo::cApplication::Ptr a, bool foreground) {
   printf("ControlLoadingManager::Load\n");
   cMutexLock lock(&mutex);
   AppMap::iterator it=apps.find(a);
   if (it == apps.end()) {
      CarouselLoader *loader=new CarouselLoader(a);
      apps[a]=loader;
      if (foreground) {
         loadingApp=loader;
         loader->SetForeground();
      }
      loader->Start();
   } else {
      Load(it->second, foreground);
   }
}

void ControlLoadingManager::Stop(ApplicationInfo::cApplication::Ptr a) {
   printf("ControlLoadingManager::Stop\n");
   cMutexLock lock(&mutex);
   AppMap::iterator it=apps.find(a);
   if (it != apps.end()) {
   }
}

/*void Hibernate() {
}*/

LoadingState ControlLoadingManager::getState(ApplicationInfo::cApplication::Ptr a) {
   printf("ControlLoadingManager::getState\n");
   cMutexLock lock(&mutex);
   AppMap::iterator it=apps.find(a);
   if (it != apps.end()) {
      return it->second->getState();
   }
   return LoadingStateWaiting;
}

SmartPtr<Cache::Cache> ControlLoadingManager::getCache(ApplicationInfo::cApplication::Ptr a) {
   cMutexLock lock(&mutex);
   AppMap::iterator it=apps.find(a);
   if (it != apps.end()) {
      return it->second->getCache();
   }
   return SmartPtr<Cache::Cache>(0);
}

void ControlLoadingManager::NewApplication(ApplicationInfo::cApplication::Ptr app) {
}

void ControlLoadingManager::ApplicationRemoved(ApplicationInfo::cApplication::Ptr app) {
   cMutexLock lock(&mutex);
   AppMap::iterator it=apps.find(app);
   if (it != apps.end()) {
      it->second->Stop();
      //apps.erase(it);
   }   
}

void ControlLoadingManager::ChannelSwitch(const cDevice *device, Service::TransportStreamID oldTs, Service::TransportStreamID newTs) {
   cMutexLock lock(&mutex);
   for (AppMap::iterator it=apps.begin(); it != apps.end(); ++it) {
      if (it->second->ChannelSwitchedAway(device, oldTs, newTs)) {
         //it is cleaner to stop/hibernate first, then to retry loading if needed.
         Stop(it->second);
         if (it->second->IsForeground()) {
            Load(it->second, false);
         }
      }
   }
}

void ControlLoadingManager::Load(CarouselLoader *l, bool foreground) {
   switch (l->getState()) {
   case LoadingStateWaiting:
   case LoadingStateError:
      if (foreground) {
         loadingApp=l;
         l->SetForeground();
      }
      l->Start();
      break;
   case LoadingStateHibernated:
      if (foreground) {
         loadingApp=l;
         l->SetForeground();
      }
      l->WakeUp();
      break;
   default:
      break;
   }
}

void ControlLoadingManager::Stop(CarouselLoader *l) {
   //manage hibernating
   if (l==loadingApp)
      loadingApp=0;
   l->Hibernate();
   if (++hibernatedCount >= MAX_HIBERNATED_APPS) {
      CarouselLoader *oldest=0;
      time_t oldestTime=0;
      for (AppMap::iterator it=apps.begin();it!=apps.end();++it) {
         if (it->second->getState()==LoadingStateHibernated && it->second->getHibernationTime()>oldestTime) {
            oldest=it->second;
            oldestTime=oldest->getHibernationTime();
         }
      }
      oldest->Stop();
      hibernatedCount--;
   }
}

void ControlLoadingManager::ProgressInfo(ProgressIndicator *pi) {
   cMutexLock lock(&mutex);
   //There may be more than one loading app, loadingApp is the last started
   if (loadingApp) {
      switch (loadingApp->getState()) {
      case LoadingStateError:
         Messages::DisplayMessage(Messages::LoadingFailed);
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

/* --------- ChannelWatch ------------ */

/*
  This class observes the channel switches. The information is
  delegated to other classes were the following action is taken:
  - switches on the same transponder are handled by DsmccReceiver.
    If it is a transponder switch, look for a different device or hibernate
  - independent from this, inform the CarouselPreloader of the channel
    switch. This class will take care for preloading the apps on the new
    transponder
*/

ChannelWatch::ChannelWatch(CarouselPreloader* preloader) 
  : preloader(preloader)
{
}

void ChannelWatch::ChannelSwitch(const cDevice *Device, int ChannelNumber) {
   if (Device && ChannelNumber) {
      cChannel *chan=Channels.GetByNumber(ChannelNumber);
      //we cannot access Applications' transport stream database because the channel switch is just about to happen,
      //and if it is a new transponder the AIT is yet to be received.
      //ApplicationInfo::cTransportStream *str=ApplicationInfo::Applications.findTransportStream(chan->Source(), chan->Nid(), chan->Tid());
      Service::TransportStream str(chan);
      printf("ChannelWatch: Switch to stream %d-%d-%d\n", str.GetSource(), str.GetNid(), str.GetTid());
      Service::TransportStreamID oldTs=ts;
      ts=str.GetTransportStreamID();
      ControlLoadingManager::getManager()->ChannelSwitch(Device, oldTs, ts);
      preloader->PreloadForTransportStream(oldTs, ts);
   } else {
   }
}


/* --------- CarouselPreloader ------------- */

CarouselPreloader::CarouselPreloader() 
  : SchedulerBySeconds(10),
    currentLoader(0)
{
}

/*
  Attention: the implementation is currently simple, there will be only one TimedPreloader at a time.
  This is sufficient as long as only the CurrentChannel is monitored!
  If all devices are monitored and a channel switch on any device causes a call of this
  method here, there must be a map Transport Stream -> TimedPreloader maintained here!!
*/
void CarouselPreloader::PreloadForTransportStream(Service::TransportStreamID oldTs, Service::TransportStreamID newTs) {
   if (newTs != oldTs) {
      Remove(currentLoader);
      delete currentLoader;
      currentLoader=new TimedPreloader(newTs);
      Add(currentLoader, false); //no initial execution
   }
}

//Wait 30 seconds before starting prefetching.
//This waiting period will be stopped as soon as the user switches to a different transponder.
#define INITIAL_WAIT 30
//Check status of loading app every 15 seconds.
#define CHECK_PERIOD 15
//Preload again to check for changes every ten minutes
#define REPRELOAD_WAIT 10*60

CarouselPreloader::TimedPreloader::TimedPreloader(Service::TransportStreamID newTs)
  : TimedBySeconds(INITIAL_WAIT),
    loading(false), ts(newTs)
{
}

//No need for a destructor. ControlLoadingManager will take care for hibernation/detaching.

void CarouselPreloader::TimedPreloader::Execute() {
   printf("TimedPreloader::Execute, loading is %d\n", loading);
   if (loading) {
      if (ControlLoadingManager::getManager()->getState(*currentPosition) != LoadingStateLoading) {
         //preload next in list
         ++currentPosition;
         //if end is reached, wait REPRELOAD_WAIT for the next round.
         if (currentPosition == apps.end()) {
            ChangeInterval(REPRELOAD_WAIT);
            loading=false;
         } else {
            printf("CarouselPreloader: Preloading next application %p, %s\n", (*currentPosition).getPointer(), (*currentPosition)->GetNumberOfNames() ? (*currentPosition)->GetName(0)->name.c_str() : "<unknown>");
            ControlLoadingManager::getManager()->Load((*currentPosition), false);
            ChangeInterval(CHECK_PERIOD);
            loading=true;
         }
      }
   } else {
      printf("CarouselPreloader: Beginning preloading\n");
      if (ApplicationInfo::Applications.findApplicationsForTransportStream(apps, ts.source, ts.onid, ts.tid) && apps.size()) {
         currentPosition=apps.begin();
         if (currentPosition != apps.end()) {
            printf("CarouselPreloader: Preloading next application %p, %s\n", (*currentPosition).getPointer(), (*currentPosition)->GetNumberOfNames() ? (*currentPosition)->GetName(0)->name.c_str() : "<unknown>");
            ControlLoadingManager::getManager()->Load((*currentPosition), false);
            ChangeInterval(CHECK_PERIOD);
            loading=true;
         }
      } else {
         ChangeInterval(REPRELOAD_WAIT);
         loading=false;
      }
   }
}


/* --------- ControlServiceSelectionProvider ------------- */

ControlServiceSelectionProvider::ControlServiceSelectionProvider(ChannelWatch *watch)
  : watch(watch)
{
}

void ControlServiceSelectionProvider::SelectService(cChannel *service) {
   //TODO: This needs more thought, or testing
   printf("ControlServiceSelectionProvider::SelectService(): Selection channel %s\n", (const char*)service->ToText());
   if (!service) {
      Service::ServiceStatus::MsgServiceEvent(Service::ServiceStatus::MessageContentNotFound, Service::Service(service));
      return;
   }
   if (cDevice::PrimaryDevice()->SwitchChannel(service, true)) {
      Service::ServiceStatus::MsgServiceEvent(Service::ServiceStatus::MessageServiceSelected, Service::Service(service));
   } else {
      Service::ServiceStatus::MsgServiceEvent(Service::ServiceStatus::MessageInsufficientResources, Service::Service(service));
   }
}

void ControlServiceSelectionProvider::StopPresentation() {
   //TODO: Find out what to do here
   printf("ControlServiceSelectionProvider::StopPresentation(): Doing nothing\n");
   Service::ServiceStatus::MsgServiceEvent(Service::ServiceStatus::MessageUserStop, Service::Service(Channels.GetByNumber(cDevice::CurrentChannel())));
}



/* --------- CarouselLoader ------------- */

/*
   This class wraps all direct access to cDsmccReceiver. It knows about devices, transport streams,
   components of an application, different TransportProtocols.
   Although the infrastructure supports multiple carousels per receiver, here there is always only
   one carousel and one receiver.
   All methods are invoked exclusively by the ControlLoadingManager, so many sanity/status checks
   are already done.
*/

CarouselLoader::CarouselLoader(ApplicationInfo::cApplication::Ptr a) 
  : app(a), receiver(0), carousel(0), filterDevice(0), state(LoadingStateWaiting), hibernatedTime(0), totalSize(0), foreground(false)
{
   protocol=app->GetTransportProtocol()->GetProtocol();
}

CarouselLoader::~CarouselLoader() {
   Stop();
}

LoadingState CarouselLoader::getState() {
   //always check if loading completed
   if (state==LoadingStateLoading) {
      int a,b;
      getProgress(a,b);
   }
   return state;
}

float CarouselLoader::getProgress(int &currentSize, int &retTotalSize) {
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

void CarouselLoader::Start() {
   switch(protocol) {
      case ApplicationInfo::cTransportProtocol::ObjectCarousel:
         return StartObjectCarousel();
      case ApplicationInfo::cTransportProtocol::Local:
         return StartLocalApp();
      default:
         return;
   }
}

void CarouselLoader::Stop() {
   //detaches itself
   delete receiver;
   receiver=0;
   filterDevice=0;
   //unless hibernated, deleted by receiver
   if (state==LoadingStateHibernated)
      delete carousel;
   carousel=0;
   state=LoadingStateWaiting;
   foreground=false;
}

void CarouselLoader::Hibernate() {
   if ( (state == LoadingStateLoading || state == LoadingStateLoaded)
         && protocol != ApplicationInfo::cTransportProtocol::Local) {
      state=LoadingStateHibernated;
      carousel=receiver->HibernateCarousel(carousel->getId());
      //detaches itself
      delete receiver;
      receiver=0;
      filterDevice=0;
      foreground=false;
      hibernatedTime=time(0);
   }
}

void CarouselLoader::WakeUp() {
   if (state==LoadingStateHibernated && protocol != ApplicationInfo::cTransportProtocol::Local) {
      StartObjectCarousel(carousel);
      //status is set by StartObjectCarousel
      hibernatedTime=0;
   }
}

void CarouselLoader::StartObjectCarousel(Dsmcc::ObjectCarousel *hibernated) {
   ApplicationInfo::cTransportProtocolViaOC *tp=dynamic_cast<ApplicationInfo::cTransportProtocolViaOC*>(app->GetTransportProtocol());
   //find device
   filterDevice=cDevice::GetDevice(app->GetChannel(), 0);
   if (!filterDevice) {
      esyslog("Failed to find device for object carousel of application %s on channel %s", 
               app->GetNumberOfNames() ? app->GetName(0)->name.c_str() : "<unknown>",
               app->GetChannel() ? (const char*)app->GetChannel()->ToText() : "<null>");
      //if this is foreground, an unavailable channel is an error.
      //If this is background, however, an unavailable channel is a common situation
      //if the carousel is hibernated and reeattached by background activities.
      if (foreground && hibernated)
         return; //dont change status
      else
         state=LoadingStateError;
      return;
   }
   //identify service
   ApplicationInfo::cTransportStream::ApplicationService *service=app->GetService();
   //create receiver
   receiver=new cDsmccReceiver(service->GetChannel()->Name(), service->GetTransportStream()->GetTransportStreamID());
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
   filterDevice->AttachFilter(receiver);
   //make the receiver receive the main stream
   receiver->ActivateStream(tp->GetPid());
   state=LoadingStateLoading;   
}

void CarouselLoader::StartLocalApp() {
   state=LoadingStateLoaded;
}

SmartPtr<Cache::Cache> CarouselLoader::getCache() {
   if (carousel)
      return carousel->getCache();
   else
      return SmartPtr<Cache::Cache>(0);
}

ApplicationInfo::cApplication::ApplicationName *CarouselLoader::getName() {
   if (app->GetNumberOfNames())
      return app->GetName(0);
   else
      return 0;
}

bool CarouselLoader::ChannelSwitchedAway(const cDevice *device, Service::TransportStreamID oldTs, Service::TransportStreamID newTs) {
   return filterDevice==device && oldTs != newTs;
}


} //namespace Mhp

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





