#
# Makefile for a Video Disk Recorder plugin
#
# $Id$

-include Make.config

# ***Documentation***
# Useful commands available in this Makefile:
# all       makes native and Java code
# install   installs everything on the system (/usr/local/vdr/mhp)
# ai        = all install
# java      compiles Java libraries
# javas     compiles and installs Java libraries
# jai       = java all install
# getjikes  prints a jikes command you can use to compile a "local application",
#           per default located in /usr/local/vdr/apps, with jikes,
#           of you are located in the main directory of the plugin.
#           Only the last parts of the path to Java files of the app must be typed.
# testapps-install  copy the contents of testapps to /usr/local/vdr/apps



# The official name of this plugin.
# This name will be used in the '-P...' option of VDR to load the plugin.
# By default the main source file also carries this name.
#
PLUGIN = mhp

### The version number of this plugin (taken from the main source file):

VERSION = $(shell grep 'static const char \*VERSION *=' $(PLUGIN).c | awk '{ print $$6 }' | sed -e 's/[";]//g')

### The C++ compiler and options:

CXX      ?= g++
CXXFLAGS ?= -g -O0 -Wall -Woverloaded-virtual

-include mhp/Make.config

### The directory environment:

#set in Make.config
#DVBDIR = ../../../../DVB
#VDRDIR = ../../..
#LIBDIR = ../../lib
TMPDIR = /tmp

### Allow user defined options to overwrite defaults:

-include $(VDRDIR)/Make.config

### The version number of VDR (taken from VDR's "config.h"):

VDRVERSION = $(shell grep 'define VDRVERSION ' $(VDRDIR)/config.h | awk '{ print $$3 }' | sed -e 's/"//g')

### The name of the distribution archive:

ARCHIVE = $(PLUGIN)-$(VERSION)
PACKAGE = vdr-$(ARCHIVE)

### Includes and Defines (add further entries here):

INCLUDES += -I$(VDRDIR)/include -I$(DVBDIR)/include -I$(DIRECTFB_INCLUDES) -I.
#INCLUDES += -I$(VDRDIR)/include -I$(DVBDIR)/include -I$(KAFFE_INCLUDES) -I$(KAFFE_INCLUDES2) -I$(DIRECTFB_INCLUDES) -I.

DEFINES += -DPLUGIN_NAME_I18N='"$(PLUGIN)"' -DBASEDIR='"$(BASEDIR)"' -DLOCALAPPSDIR='"$(LOCALAPPSDIR)"' \
           -DMHPDIR='"$(MHPDIR)"' -DPLUGINLIBDIR=\"$(PLUGINLIBDIR)\"

#the --whole-archive option makes ld add all symbols of the static library to the plugin
#instead of only the ones the plugin uses. This is necessary because the libraries loaded
#by the MHP stack will make use of these libraries.

LIBS += -Wl,--whole-archive libdsmccreceiver/libdsmccreceiver.a libdsmcc/libdsmcc.a -lz \
        libmhpoutput/libmhpoutput.a  -Wl,--no-whole-archive -lm -lavcodec \
        libait/libait.a -Wl,--whole-archive libdvbsi/libdvbsi.a libservice/libservice.a -Wl,--no-whole-archive \
        libjava/libjava.a  -ldfb++ -ldirectfb $(VM_LINK_PATH)
        
### The object files (add further files here):

OBJS = $(PLUGIN).o applicationmenu.o mhpcontrol.o i18n.o

### The subdirectories:

SUBDIRS = libdsmcc libdsmccreceiver libait libmhpoutput libdvbsi libjava libservice mhp
LIBXMI = libxmi

### Implicit rules:

.PHONY: all all-redirect
all-redirect: all

%.o: %.c
	$(CXX) $(CXXFLAGS) -c $(DEFINES) $(INCLUDES) $<

# Dependencies:

MAKEDEP = g++ -MM -MG
DEPFILE = .dependencies
$(DEPFILE): Makefile
	@$(MAKEDEP) $(DEFINES) $(INCLUDES) $(OBJS:%.o=%.c) > $@

-include $(DEPFILE)

### Targets:

all: libvdr-$(PLUGIN).so reminder

ai: all install

jai: java all install

libvdr-$(PLUGIN).so: subdirs $(OBJS)
	$(CXX) $(CXXFLAGS) -shared -export-dynamic $(OBJS) $(LIBS) -o $@
	@cp $@ $(LIBDIR)/$@.$(VDRVERSION)

subdirs: libxmi
	@for i in $(SUBDIRS) ;\
	do \
	    ( cd $$i;\
              $(MAKE) all;\
       ) \
	|| exit 1;\
	done

.PHONY: libxmi
libxmi: ./$(LIBXMI)/config.h
	@\
        ( cd $(LIBXMI);\
            $(MAKE) all;\
        )
#	ar ru $(LIBXMI)/libxmi.a $(LIBXMI)/*.o

./$(LIBXMI)/config.h:
	@\
        ( cd $(LIBXMI);\
            ./configure --enable-static --disable-shared \
        ) \
        
libxmi-distclean:
	@\
        ( cd $(LIBXMI);\
            test -e Makefile || exit 0 ;\
            $(MAKE) distclean;\
        ) \
        
libxmi-clean:
	@\
        ( cd $(LIBXMI);\
            test -e Makefile || exit 0 ;\
            $(MAKE) clean;\
        ) \
   
#includes java-install
install:
	$(MAKE) -C mhp/ install
	$(MAKE) -C libmhpoutput/ install
  
dist: distclean
	@-rm -rf $(TMPDIR)/$(ARCHIVE)
	@mkdir $(TMPDIR)/$(ARCHIVE)
	@cp -a * $(TMPDIR)/$(ARCHIVE)
	@tar czf $(PACKAGE).tgz -C $(TMPDIR) $(ARCHIVE)
	@-rm -rf $(TMPDIR)/$(ARCHIVE)
	@echo Distribution package created as $(PACKAGE).tgz

clean: plugin-clean libxmi-clean

distclean: plugin-clean libxmi-distclean java-clean

plugin-clean:
	@-rm -f $(OBJS) $(DEPFILE) *.so *.tgz core* *~
	@for i in $(SUBDIRS) ;\
	do \
	    ( cd $$i;\
              $(MAKE) clean;\
       ) \
	|| exit 1;\
	done
   
testapps-install:
	@install -d $(BASEDIR)/apps
	cp -r testapps/* $(BASEDIR)/apps

##Do not delete testapps in distclean, include .class files in distribution
testapps-clean-all:
	@find testapps/ -name \*.class -exec rm {} \;

#the java targets are only for developer's convenience
java:
	make -C mhp/java java

java-install:
	make -C mhp/java install
   
java-clean:
	make -C mhp/java java-clean
   
javas: java java-install

testcase:
	make -C mhp/java testcase
        
getjikes:
	make -C mhp/java getjikes-for-topdir
        
patch:
	cat patches/*.patch > vdr.mhp.patch
   
reminder:
	@echo "\"make all\" has finished. Do not forget the \"make install\""
        
