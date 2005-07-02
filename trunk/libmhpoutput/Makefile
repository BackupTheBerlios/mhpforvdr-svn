#
# Makefile for a Video Disk Recorder plugin
#
# $Id$

VDRDIR = ../../../..

### The C++ compiler and options:

##CXX      ?= g++
##CXXFLAGS ?= -O2 -g -Wall -Woverloaded-virtual

include ../Make.config



### The directory environment:


INCLUDES += -I$(VDRDIR)/include -I$(DIRECTFB_INCLUDES) -I..

DEFINES += -DMHPLIBDIR=\"$(MHPLIBDIR)\" 

LIBS +=

### The object files (add further files here):

OBJS = output.o outputadministration.o avcodecsupport.o

MODULES = mpegpes-$(DIRECTFB_VERSION) sdl
#DIRECTFB_VERSION is defined in Make.config, the value is taken from the directfb-config program
#Edit Make.config if this fails for some reason.

CLEAN_SUBDIRS = mpegpes-[0-9]* sdl

### Implicit rules:

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

all: libmhpoutput.a modules

install: modules-install

libmhpoutput.a : $(OBJS)
	$(AR) $(ARFLAGS) $@ $(OBJS)
#	$(CXX) $(CXXFLAGS) -shared $(OBJS) $(LIBS) -o $@ 

modules:
	@for i in $(MODULES) ;\
	do \
	    ( cd $$i;\
              $(MAKE) all;\
       ) \
	|| exit 1;\
	done
   
modules-install: all
	@install -d $(MHPLIBDIR)
	@for i in $(MODULES) ;\
	do \
	    ( cd $$i;\
              $(MAKE) install;\
       ) \
	|| exit 1;\
	done
   
clean:
	@-rm -f $(OBJS) $(DEPFILE) *.a *.so *.tgz core* *~
	@for i in $(CLEAN_SUBDIRS) ;\
	do \
	    ( cd $$i;\
              $(MAKE) clean;\
       ) \
	|| exit 1;\
	done
