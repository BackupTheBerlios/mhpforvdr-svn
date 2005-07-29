#
# Makefile for a Video Disk Recorder plugin
#
# $Id$

include ../Make.config
PLUGINDIR_RELATIVE = ../

### The directory environment:


INCLUDES += $(STANDARD_INCLUDE_OPTIONS)

DEFINES += 

LIBS +=

### The object files (add further files here):

OBJS = ait.o applications.o

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

all: libait.a

libait.a : $(OBJS)
	$(AR) $(ARFLAGS) $@ $(OBJS)
#	$(CXX) $(CXXFLAGS) -shared $(OBJS) $(LIBS) -o $@ 

clean:
	@-rm -f $(OBJS) $(DEPFILE) *.a *.so *.tgz core* *~
