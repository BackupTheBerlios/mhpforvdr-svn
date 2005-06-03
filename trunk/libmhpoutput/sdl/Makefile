
-include ../../Make.config

OBJS  = sdl.o
INCLUDES = -I$(DFB++_INCLUDES) -I$(DIRECTFB_INCLUDES)  -I../.. -I../../mhp/$(VDRDIR_RELATIVE)
LIBS = $(DIRECTFB_LIBS)



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
