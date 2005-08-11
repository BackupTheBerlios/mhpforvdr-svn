#
# Makefile for a Video Disk Recorder plugin
#
# $Id$

### The C++ compiler and options:

##CXX      ?= g++
##CXXFLAGS ?= -O2 -g -Wall -Woverloaded-virtual

include ../Make.config
PLUGINDIR_RELATIVE = ../



### The directory environment:

EXTRA_CLASSPATH_PREFIX = $(MHPJARDIR)
GLOBAL_EXTRA_CLASSPATH := $(EXTRA_CLASSPATH)

INCLUDES += $(VM_INCLUDE_OPTIONS) $(STANDARD_INCLUDE_OPTIONS)
DEFINES  += $(VM_DEFINES) -DPLUGINLIBDIR=\"$(VDR_PLUGINLIBDIR)\" \
            -DGLOBAL_EXTRA_CLASSPATH=\"$(GLOBAL_EXTRA_CLASSPATH)\" \
            -DMHPJARFILE=\"$(MHPJARFILE)\" -DMHPJARDIR=\"$(MHPJARDIR)\" -DMHPLIBDIR=\"$(MHPLIBDIR)\"

### The object files (add further files here):

OBJS = javavm-$(VM).o javainterface.o jnithread.o

all-redirect: all

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

all: libjava.a

libjava.a : $(OBJS)
	$(AR) $(ARFLAGS) $@ $(OBJS)
#	$(CXX) $(CXXFLAGS) -shared $(OBJS) $(LIBS) -o $@

#test: javavm.o
#	$(CXX) $(CXXFLAGS) $(DEFINES) $(INCLUDES) javainterface.c javavm.o -ldl -Wl,--rpath=$(KAFFE_NATIVE) -L$(KAFFE_NATIVE) -lkaffevm -o kaffe_test
 

clean:
	@-rm -f $(OBJS) javavm*.o $(DEPFILE) *.a *.so *.tgz core* *~ kaffe_test
