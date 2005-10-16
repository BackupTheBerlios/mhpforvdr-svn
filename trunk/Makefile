#
# Makefile for a Video Disk Recorder plugin
#
# $Id$

include Make.config
PLUGINDIR_RELATIVE = .

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
# testapps-install  copy the contents of testapps to /usr/local/vdr/apps,
#                   currently simply copies the whole dir to that location
# testcase  Compile the "SimpleXlet" test suite found in testapps/
# ti        = testcase testapps-install



# The official name of this plugin.
# This name will be used in the '-P...' option of VDR to load the plugin.
# By default the main source file also carries this name.
#
PLUGIN = mhp

### The version number of this plugin (taken from the main source file):

VERSION = $(shell grep 'static const char \*VERSION *=' $(PLUGIN).c | awk '{ print $$6 }' | sed -e 's/[";]//g')

### The C++ compiler and options:

##CXXFLAGS += -g -O0

### The directory environment:

TMPDIR = /tmp

### Allow user defined options to overwrite defaults:

-include $(VDRDIR)/Make.config

### The version number of VDR (taken from VDR's "config.h"):

VDRVERSION = $(shell grep 'define VDRVERSION ' $(VDRDIR)/config.h | awk '{ print $$3 }' | sed -e 's/"//g')

### The name of the distribution archive:

ARCHIVE = $(PLUGIN)-$(VERSION)
PACKAGE = vdr-$(ARCHIVE)

### Includes and Defines (add further entries here):

INCLUDES += $(STANDARD_INCLUDE_OPTIONS) -I$(DIRECTFB_INCLUDES) $(VM_INCLUDE_OPTIONS)

DEFINES += -DPLUGIN_NAME_I18N='"$(PLUGIN)"' -DBASEDIR='"$(BASEDIR)"' -DLOCALAPPSDIR='"$(LOCALAPPSDIR)"' \
           -DMHPDIR='"$(MHPDIR)"' -DPLUGINLIBDIR=\"$(VDR_PLUGINLIBDIR)\"
           

### The subdirectories:

SUBDIRS = libdsmcc libdsmccreceiver libait libmhpoutput libdvbsi libjava libservice mhp
SUBPLUGINDIRS = libmhpoutput mhp
LIBXMI = libxmi
dfb++ = DFB++

##The --whole-archive option makes ld add all symbols of the static library to the binary
##instead of only the ones actually used in this binary. This is necessary because other
##dynamic objects (the libraries for JNI, output) will link to this library and use additional symbols.

LIBS_PLUGIN += $(MAIN_LIB_OPTIONS)
LIBS_LIBRARY += -lm -lavcodec -lz -Wl,--whole-archive \
        libdsmccreceiver/libdsmccreceiver.a libdsmcc/libdsmcc.a  libmhpoutput/libmhpoutput.a  \
        libait/libait.a libdvbsi/libdvbsi.a libservice/libservice.a  libjava/libjava.a \
        $(dfb++)/dfb++/.libs/libdfb++.a \
        -Wl,--no-whole-archive -ldirectfb $(VM_LINK_PATH)
        
### The object files (add further files here):

OBJS_PLUGIN = $(PLUGIN).o
OBJS_LIBRARY = mhpcontrol.o applicationmenu.o i18n.o
OBJS = $(OBJS_PLUGIN) $(OBJS_LIBRARY)

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

all: libvdr-$(PLUGIN).so subplugins reminder

ai: all install

jai: java all install

libvdr-$(PLUGIN).so: $(MAIN_LIBRARY) $(OBJS_PLUGIN)
	$(CXX) $(CXXFLAGS) -shared $(OBJS_PLUGIN) $(LIBS_PLUGIN) -o $@
	@cp $@ $(VDR_PLUGINLIBDIR)/$@.$(VDRVERSION)
	
$(MAIN_LIBRARY): subdirs $(OBJS_LIBRARY)
	$(CXX) $(CXXFLAGS) -shared -export-dynamic $(OBJS_LIBRARY) $(LIBS_LIBRARY) -o $@

subdirs: libxmi dfb++
	@for i in $(SUBDIRS) ;\
	do \
	    ( cd $$i;\
              $(MAKE) all;\
       ) \
	|| exit 1;\
	done
	
subplugins:
	@for i in $(SUBPLUGINDIRS) ;\
	do \
	    ( cd $$i;\
              $(MAKE) subplugins;\
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

.PHONY: dfb++
dfb++: ./$(dfb++)/config.h
	@\
        ( cd $(dfb++);\
            $(MAKE) all;\
        )

./$(dfb++)/config.h:
	@\
        ( cd $(dfb++);\
            PKG_CONFIG_PATH=$$PKG_CONFIG_PATH:$(DIRECTFB_ROOT)/lib/pkgconfig/;\
            ./configure --enable-static --disable-shared \
        ) \
        
dfb++-distclean:
	@\
        ( cd $(dfb++);\
            test -e Makefile || exit 0 ;\
            $(MAKE) distclean;\
        ) \
        
dfb++-clean:
	@\
        ( cd $(dfb++);\
            test -e Makefile || exit 0 ;\
            $(MAKE) clean;\
        ) \
   
## includes java-install
install: library-install
	$(MAKE) -C mhp/ install
	$(MAKE) -C libmhpoutput/ install

library-install:
	@install -d $(MHPLIBDIR)
	@install -v $(MAIN_LIBRARY) $(MHPLIBDIR)
  
dist: distclean
	@-rm -rf $(TMPDIR)/$(ARCHIVE)
	@mkdir $(TMPDIR)/$(ARCHIVE)
	@find -not -path "*.svn*" -and -not -type d -exec cp -a --parents "{}" $(TMPDIR)/$(ARCHIVE) \;
##	@cp -a * $(TMPDIR)/$(ARCHIVE)
	@tar czf $(PACKAGE).tgz -C $(TMPDIR) $(ARCHIVE)
	@-rm -rf $(TMPDIR)/$(ARCHIVE)
	@echo Distribution package created as $(PACKAGE).tgz

clean: plugin-clean libxmi-clean dfb++-clean

distclean: plugin-clean libxmi-distclean dfb++-distclean java-clean

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
	@echo Copying ./testapps/ to $(BASEDIR)/apps
	@( cd testapps; \
	  find -not -path "*.svn*" -and -not -type d -and -not -name "*.java" -exec cp -a --parents "{}" $(BASEDIR)/apps \; \
	)

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

ti: testcase testapps-install
        
getjikes:
	make -C mhp/java getjikes-for-topdir
        
patch:
	cat patches/*.patch > vdr.mhp.patch
   
reminder:
	@echo "\"make all\" has finished. Do not forget the \"make install\""
        
