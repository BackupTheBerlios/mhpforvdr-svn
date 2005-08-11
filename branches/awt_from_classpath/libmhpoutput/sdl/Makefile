
include ../../Make.config
PLUGINDIR_RELATIVE = ../..

OBJS  = sdl.o
INCLUDES = -I$(DFB++_INCLUDES) -I$(DIRECTFB_INCLUDES) $(STANDARD_INCLUDE_OPTIONS)
LIBS = $(DIRECTFB_LIBS) $(MAIN_LIB_OPTIONS)



%.o: %.c
	$(CXX) $(CXXFLAGS) -c $(DEFINES) $(INCLUDES) $<
   
all: libmhpoutputsdl.so

install: 
	cp libmhpoutputsdl.so $(MHPLIBDIR)


# Dependencies:

MAKEDEP = $(CC) -MM -MG
DEPFILE = .dependencies
$(DEPFILE): Makefile
	@$(MAKEDEP) $(DEFINES) $(INCLUDES) $(CINCLUDES) $(OBJS:%.o=%.c) $(COBJS:%.o=%.c) > $@

-include $(DEPFILE)

libmhpoutputsdl.so: $(OBJS) $(COBJS)
	$(CXX) $(CFLAGS) -shared $(OBJS) $(COBJS) $(LIBS) -o $@
   
clean:
	@-rm -f $(OBJS) $(COBJS) $(DEPFILE) *.so *.tgz core* *~ *.a
