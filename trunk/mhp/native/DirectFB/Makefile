
include ../../../Make.config

OBJS =  \
	font.o \
	graphics.o \
	image.o \
	window.o \
          
LIBS = $(PLUGINDIR_RELATIVE)/../../../libxmi/.libs/libxmi.a


INCLUDES = -I$(DIRECTFB_INCLUDES) $(VM_INCLUDE_OPTIONS) -I$(PLUGINDIR_RELATIVE)/../../../ -I$(PLUGINDIR_RELATIVE)/../../../libxmi -I../../../$(VDRDIR_RELATIVE)

DEFINES = -DMHPFONTDIR=\"$(MHPFONTDIR)\" -DHAVE_BOOL

%.o: %.c
	$(CXX) $(CXXFLAGS) -O0 -c $(DEFINES) $(INCLUDES) $<
   
all: libdirectfbawt.so

# Dependencies:

MAKEDEP = $(CXX) -MM -MG
DEPFILE = .dependencies
$(DEPFILE): Makefile
	@$(MAKEDEP) $(DEFINES) $(INCLUDES) $(OBJS:%.o=%.c) > $@

-include $(DEPFILE)

libdirectfbawt.so: $(OBJS)
	$(CXX) $(CXXFLAGS) -shared $(OBJS) $(LIBS) -o $@
   
clean:
	@-rm -f $(OBJS) $(DEPFILE) *.so *.tgz core* *~ *.a


