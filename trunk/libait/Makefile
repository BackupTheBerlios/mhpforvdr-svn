#
# Makefile for a Video Disk Recorder plugin
#
# $Id$

include ../Make.config

VDRDIR = ../../../..

### The directory environment:


INCLUDES += -I$(VDRDIR)/include -I..

DEFINES += 

LIBS +=

### The object files (add further files here):

OBJS = ait.o applications.o

### Implicit rules:

%.o: %.c
	$(CXX) $(CXXFLAGS) -c $(DEFINES) $(INCLUDES) $<

# Dependencies:

MAKEDEP = g++ -MM -MG
DEPFILE = .dependencies
$(DEPFILE): Makefile
	@$(MAKEDEP) $(DEFINES) $(INCLUDES) $(OBJS:%.o=%.c) > $@

-include $(DEPFILE)

### Targets:

all: libait.a

libait.a : $(OBJS)
	$(AR) $(ARFLAGS) $@ $(OBJS)
#	$(CXX) $(CXXFLAGS) -shared $(OBJS) $(LIBS) -o $@ 

clean:
	@-rm -f $(OBJS) $(DEPFILE) *.a *.so *.tgz core* *~
