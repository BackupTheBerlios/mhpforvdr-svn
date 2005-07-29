/*
 * mhp.c: A plugin for the Video Disk Recorder
 *
 * See the README file for copyright information and how to reach the author.
 *
 * $Id$
 */

#include <vdr/plugin.h>
#include <vdr/videodir.h>
#include <vdr/tools.h>
#include <vdr/keys.h>
#include <vdr/remote.h>

#include <sys/types.h>
#include <dirent.h>
#include <sys/stat.h> 
#include <unistd.h> 
#include <getopt.h>

 
#include "applicationmenu.h"
#include "i18n.h"
#include "mhpmessages.h"
#include "mhploading.h"
#include "mhpcontrol.h"
#include <libjava/javainterface.h>
#include <libmhpoutput/output.h>
#include <libmhpoutput/outputadministration.h>


static const char *VERSION        = "0.3pre";
static const char *DESCRIPTION    = "An MHP implementation";
static const char *MAINMENUENTRY  = "MHP";


class cPluginMhp : public cPlugin {
private:
  // Add any member variables or functions you may need here.
  std::list<ApplicationInfo::cApplication::Ptr> localApps;
  const char *localAppPath;
  const char *outputSystem;
  const char *debugLocalApp;
  //const char *VDRPluginPath;
  //const char *mhpLibDir;
  void InitializeLocalApps();
  bool startedDebugApp;
  //LibraryPreloader ownPlugin;
public:
  cPluginMhp(void);
  virtual ~cPluginMhp();
  virtual const char *Version(void) { return VERSION; }
  virtual const char *Description(void) { return tr(DESCRIPTION); }
  virtual const char *CommandLineHelp(void);
  virtual bool ProcessArgs(int argc, char *argv[]);
  virtual bool Initialize(void);
  virtual bool Start(void);
  virtual void Housekeeping(void);
  virtual const char *MainMenuEntry(void) { return tr(MAINMENUENTRY); }
  virtual cOsdObject *MainMenuAction(void);
  virtual cMenuSetupPage *SetupMenu(void);
  virtual bool SetupParse(const char *Name, const char *Value);
  };

cPluginMhp::cPluginMhp(void)
{
  // Initialize any member variables here.
  // DON'T DO ANYTHING ELSE THAT MAY HAVE SIDE EFFECTS, REQUIRE GLOBAL
  // VDR OBJECTS TO EXIST OR PRODUCE ANY OUTPUT!
  localAppPath=LOCALAPPSDIR;
  outputSystem="mpegpes";
  MhpConfigPath=0;
  debugLocalApp=0;
  //VDRPluginPath=PLUGINLIBDIR;
  startedDebugApp=false;
  //mhpLibDir=MHPDIR;
}

cPluginMhp::~cPluginMhp()
{
  // Clean up after yourself!
  //delete status;
  JavaInterface::ShutdownMHP();
  DvbSi::Database::CleanUp();
  JavaInterface::CleanUp();
  MhpLoadingManager::getManager()->CleanUp();
  MhpOutput::Administration::CleanUp();
}

const char *cPluginMhp::CommandLineHelp(void)
{
  // Return a string that describes all known command line options.
  return /*"  -L DIR,   --lib=DIR       The plugin directory\n"
         "                            as given to VDR with the -L option\n"
         "                            (required if set for VDR!)\n"*/
         "  -a DIR    --apppath=DIR   The path to local applications\n"
         "                            (default: /usr/local/vdr/apps)\n"
         "  -o NAME   --output=NAME   The output module:\n"
         "                            \"mpegpes\" for MPEG-1 output (default),\n"
         "                            \"sdl\" for preliminary SDL output.\n"
         "  -c DIR,   --config=DIR    Directory for options and data possibly\n"
         "                            stored by MHP applications (default is\n"
         "                            VIDEODIR/mhp)\n"
         //"  -D NAME   --debugapp=NAME Debug mode: immediately start\n"
         //"                            local application NAME after VDR\n"
         //"                            has started.\n"
        // "  -m        --mhpdir        The path to the MHP implementation\n"
        /* "                            (default: " MHPDIR ")\n"*/;
}

bool cPluginMhp::ProcessArgs(int argc, char *argv[])
{
  // Implement command line argument processing here if applicable.
  //TODO: make localAppPath, mhpLibDir configurable
   static struct option long_options[] = {
       //{ "lib",      required_argument,       NULL, 'L' },
       { "apppath",  required_argument,       NULL, 'a' },
       { "output",   required_argument,       NULL, 'o' },
       { "config",   required_argument,       NULL, 'c' },
       { "debugapp", required_argument,       NULL, 'D' },
       //{ "mhpdir",required_argument,       NULL, 'm' },
       { NULL }
       };
     
   int c;
   while ((c = getopt_long(argc, argv, "a:L:o:c:D:", long_options, NULL)) != -1) {
      switch (c) {
         case 'a':
            localAppPath=optarg;
            break;
         /*case 'm':
            mhpLibDir=optarg;
            break;
         case 'L':
            VDRPluginPath=optarg;
            break;*/
         case 'o':
            outputSystem=optarg;
            break;
         case 'c':
            MhpConfigPath=optarg;
            break;
         case 'D':
            debugLocalApp=optarg;
            break;
         default:
            esyslog("MHP: Unknown option %c, ignoring", c);
            break;
         }
   }
   return true;
}

//from vdr/plugin.c
#define LIBVDR_PREFIX  "libvdr-"
#define SO_INDICATOR   ".so."

bool cPluginMhp::Initialize(void)
{
   // Initialize any background activities the plugin shall perform.
   
/*
   //VDR loads plugins without the RTDL_GLOBAL flag in dlopen.
   //Dynamic modules dlopen'ed by the plugin, however, will link against symbols from
   //the plugin itself, so it is necessary to reload its symbols globally.
   //Needed by the MHPOutput module and Java native libraries.
   char *buffer = NULL;
   //from vdr/plugin.c
   asprintf(&buffer, "%s/%s%s%s%s", VDRPluginPath, LIBVDR_PREFIX, "mhp", SO_INDICATOR, VDRVERSION);
   if (!ownPlugin.Load(buffer)) {
      esyslog("MHP: Failed to re-open own dynamic object (this plugin)."
              "Remember to give the plugin the same \"-L\" option as VDR!");
      printf("MHP: Failed to re-dlopen the dynamic object of this plugin.\n"
             "You have to give the plugin the same \"-L\" option as you specified on the command line for VDR!\n");
      return false;
   }
   free(buffer);
*/

   JavaInterface::InitializeSystem();
   InitializeLocalApps();
   
   if (MhpConfigPath==0) {
      char *c;
      asprintf(&c, "%s%s", VideoDirectory, "/mhp");
      MhpConfigPath=c;
   }
   MakeDirs(MhpConfigPath, true);
   if (!DirectoryOk(MhpConfigPath, true))
      esyslog("MHP: Config directory is not accessible. Settings will not be stored.");
   
   return true;
}

void cPluginMhp::InitializeLocalApps() {
//SPECIFICATION for local apps:
//The directory localAppPath can be specified by an command line argument.
//The default is "/usr/local/vdr/apps".
//An application with the name "XYApp" must be have an initial class "XYApp"
//defined in the class file "XYApp.class" in the directory "XYApp" under the localAppPath.
//Example: "/usr/local/vdr/apps/XYApp/XYApp.class".
//The classpath will contain "/usr/local/vdr/apps/XYApp/".
   DIR *dir=opendir(localAppPath);
   ApplicationInfo::cTransportProtocolLocal *tp=0;
   if (dir) {
      struct dirent *entry;
      struct stat st;
      while ((entry=readdir(dir))) {
         char path[PATH_MAX];
         sprintf(path, "%s/%s", localAppPath, entry->d_name);
         if ( (stat(path, &st) != -1) && S_ISDIR(st.st_mode)) {
            sprintf(path, "%s/%s/%s.class", localAppPath, entry->d_name, entry->d_name);
            //int filedes;
            if ( (stat(path, &st) != -1) && (S_ISREG(st.st_mode) || S_ISLNK(st.st_mode)) && access(path, R_OK)==0/*((filedes=open(path, O_RDONLY))!=-1)*/ ) {
               if (!tp) {
                  tp=new ApplicationInfo::cTransportProtocolLocal();
                  tp->SetPath(localAppPath);
               }
               ApplicationInfo::cApplication *app=new cLocalApplication(entry->d_name, entry->d_name, entry->d_name, tp);
               localApps.push_back(app);
               //close(filedes);
            }
         }
      }
      closedir(dir);
   }
}

/*
 //Debug
 #include "mhpcontrol.h"
 #include <libdvbsi/database.h>
 #include <typeinfo>
 class List : public DvbSi::Listener, public DvbSi::DataSwitchListener {
 public:
 protected:
    virtual void DataSwitch(DvbSi::Database *db) {
       this->db=db;
       printf("adding request\n");
       db->retrieveServiceTable(this); //1
       db->retrieveNetworks(this); //1
       db->retrieveBouquets(this); //6
       db->retrieveTDT(this); //1
       db->retrieveTOT(this); //1
       db->retrieveActualServices(this); //1
       db->retrieveActualNetwork(this); //1
       db->retrieveServices(this, 1, new DvbSi::IdTracker(1201), new DvbSi::IdTracker()); //6
       db->retrieveActualTransportStream(this); //1
       db->retrievePMTServices(this); //1
       db->retrievePMTElementaryStreams(this, 28113); //1
       db->retrieveEventTable(this, false); //1
       db->retrieveEventTableOther(this, false); //1
       db->retrieveScheduledEvents(this, 33, 898); //
       db->retrievePresentFollowingEvent(this, 33, 46, false); //
       db->retrieveTransportStreamDescription(this);
    }
    virtual void Result(DvbSi::Request *req) {
       printf(" Received result, code %d, %s\n", req->getResultCode(), typeid(*req).name());
       delete req;
       if (req->getAppData() == (void *)1) {
          printf("Received second request\n");
          return;
       }
       DvbSi::ActualServicesRequest *areq=(DvbSi::ActualServicesRequest *)req;
       
       db->retrievePresentFollowingEvent(this, areq->list.front().getTransportStreamId(),
                28113, true, DvbSi::FromCacheOrStream, (void *)1);
    }
    DvbSi::Database *db;
 };
*/ 

bool cPluginMhp::Start(void)
{
   RegisterI18n(MhpI18nPhrases);
   //Cannot call this in Initialize(), it may use VDR structures like devices
   printf("Init output\n");
   MhpOutput::Administration::Init(outputSystem);
   
   // Start any background activities the plugin shall perform.
   printf("Start monitoring\n");
   ApplicationInfo::Applications.StartMonitoring();
   
/*   if (debugLocalApp) {
      cKeyMacro *macro=new cKeyMacro();
      macro->Parse(strdup("User9 @mhp"));
      KeyMacros.Add(macro);
      cRemote::Put(kOk);
      cRemote::Put(kUser9);
   }
*/
   return true;
}

void cPluginMhp::Housekeeping(void)
{
  // Perform any cleanup or other regular tasks.
}

cOsdObject *cPluginMhp::MainMenuAction(void)
{
  // Perform the action when selected from the main VDR menu.
   
   //Debug
   //For fast debugging with test applications, no need to start them from the menu
   //Need to do this here, only way to enter main thread
/*   if (!startedDebugApp && debugLocalApp) {
      startedDebugApp=true;
      printf("Start app\n");
      ApplicationInfo::cApplication *app;
      for (app=localApps.First(); app; app=localApps.Next(app)) {
         //if (app->GetName(0)->name=="HaviExample") {
         //if (app->GetName(0)->name=="SimpleXlet") {
         if (app->GetName(0)->name==debugLocalApp) {
            MhpControl::Start(app);
            break;
         }
      }
      if (app==localApps.Last())
         printf("Did not find debug application %s. Path for local applications is %s\n", debugLocalApp, localAppPath);
   }
*/
   return new MhpApplicationMenu(&localApps);
}

cMenuSetupPage *cPluginMhp::SetupMenu(void)
{
  // Return a setup menu in case the plugin supports one.
  return NULL;
}

bool cPluginMhp::SetupParse(const char *Name, const char *Value)
{
  // Parse your own setup parameters and store their values.
  return false;
}

VDRPLUGINCREATOR(cPluginMhp); // Don't touch this!
