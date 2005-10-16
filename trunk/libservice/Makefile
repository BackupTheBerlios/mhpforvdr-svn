#
# Makefile for a Video Disk Recorder plugin
#
# $Id$

include ../Make.config
PLUGINDIR_RELATIVE = ../


### The directory environment:


INCLUDES += -I$(DIRECTFB_INCLUDES) $(STANDARD_INCLUDE_OPTIONS)

DEFINES += 

LIBS +=

### The object files (add further files here):

OBJS = servicecontext.o service.o filter.o

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

all: libservice.a

libservice.a : $(OBJS)
	$(AR) $(ARFLAGS) $@ $(OBJS)
#	$(CXX) $(CXXFLAGS) -shared $(OBJS) $(LIBS) -o $@ 

clean:
	@-rm -f $(OBJS) $(DEPFILE) *.a *.so *.tgz core* *~
