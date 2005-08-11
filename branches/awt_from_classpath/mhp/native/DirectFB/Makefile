
include ../../../Make.config
PLUGINDIR_RELATIVE = ../../..

OBJS =  \
	font.o \
	graphics.o \
	image.o \
	window.o \
          
LIBS = $(PLUGINDIR_RELATIVE)/libxmi/.libs/libxmi.a $(MAIN_LIB_OPTIONS)


INCLUDES = -I$(DIRECTFB_INCLUDES) $(VM_INCLUDE_OPTIONS) -I$(PLUGINDIR_RELATIVE)/libxmi $(STANDARD_INCLUDE_OPTIONS)

DEFINES = -DMHPFONTDIR=\"$(MHPFONTDIR)\" -DHAVE_BOOL

%.o: %.c
	$(CXX) $(CXXFLAGS) -O0 -c $(DEFINES) $(INCLUDES) $<
   
all: libmhpjni_directfbawt.so

# Dependencies:

MAKEDEP = $(CXX) -MM -MG
DEPFILE = .dependencies
$(DEPFILE): Makefile
	@$(MAKEDEP) $(DEFINES) $(INCLUDES) $(OBJS:%.o=%.c) > $@

-include $(DEPFILE)

libmhpjni_directfbawt.so: $(OBJS)
	$(CXX) $(CXXFLAGS) -shared $(OBJS) $(LIBS) -o $@
   
clean:
	@-rm -f $(OBJS) $(DEPFILE) *.so *.tgz core* *~ *.a


