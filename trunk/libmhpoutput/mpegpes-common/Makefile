
include ../../Make.config
PLUGINDIR_RELATIVE = ../..

COBJS = shm.o primary.o
OBJS  = mpegpes.o
INCLUDES = -I$(DFB++_INCLUDES) -I$(DIRECTFB_INCLUDES) $(STANDARD_INCLUDE_OPTIONS)
CINCLUDES = -I$(DIRECTFB_INCLUDES) -I$(DIRECTFB_INCLUDES_INTERNAL) -Idfb_config
CFLAGS += -D_REENTRANT -fPIC -DPIC -ffast-math -D_GNU_SOURCE
LIBS = $(DIRECTFB_LIBS) $(MAIN_LIB_OPTIONS)



shm.o:
	$(CC) $(CFLAGS) -c $(DEFINES) $(CINCLUDES) shm.c
primary.o:
	$(CC) $(CFLAGS) -c $(DEFINES) $(CINCLUDES) primary.c
   
%.o: %.c
	$(CXX) $(CXXFLAGS) -c $(DEFINES) $(INCLUDES) $<
   
all: libmhpoutputmpegpes.so

install: 
	cp libmhpoutputmpegpes.so $(MHPLIBDIR)


# Dependencies:

MAKEDEP = $(CC) -MM -MG
DEPFILE = .dependencies
$(DEPFILE): Makefile
	@$(MAKEDEP) $(DEFINES) $(INCLUDES) $(CINCLUDES) $(OBJS:%.o=%.c) $(COBJS:%.o=%.c) > $@

-include $(DEPFILE)

#libmhpoutputmpegpes.a: $(OBJS)
#	$(AR) $(ARFLAGS) $@ $(OBJS) 
   
libmhpoutputmpegpes.so: $(OBJS) $(COBJS)
	$(CXX) $(CFLAGS) -shared -export-dynamic $(OBJS) $(COBJS) $(LIBS) -o $@
   
clean:
	@-rm -f $(OBJS) $(COBJS) $(DEPFILE) *.so *.tgz core* *~ *.a
