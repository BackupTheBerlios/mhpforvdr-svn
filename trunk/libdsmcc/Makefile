#
# Makefile for a Video Disk Recorder plugin
#
# $Id$

VDRDIR = ../../../..

### The C++ compiler and options:

CXX      ?= g++
CXXFLAGS ?= -O2 -g -Wall -Woverloaded-virtual
AR = ar
ARFLAGS = ru
RANLIB = ranlib


### The directory environment:


INCLUDES += -I$(VDRDIR)/include

DEFINES += 

LIBS += -lz

### The object files (add further files here):

OBJS = util.o dsmcc.o biop.o descriptor.o

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

all: libdsmcc.a

libdsmcc.a : $(OBJS)
	$(AR) $(ARFLAGS) $@ $(OBJS)
#	$(CXX) $(CXXFLAGS) -shared $(OBJS) $(LIBS) -o $@ 

clean:
	@-rm -f $(OBJS) $(DEPFILE) *.a *.so *.tgz core* *~
