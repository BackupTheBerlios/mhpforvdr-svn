
include ../../../Make.config
PLUGINDIR_RELATIVE = ../../..

OBJS =  \
	syslog.o \
	dirinfo.o \
	application.o \
	si.o \
	service.o \
	storage.o \
	nativedata.o
   
LIBS = $(MAIN_LIB_OPTIONS)

CXXFLAGS += -DMHPLIBDIR=\"$(MHPLIBDIR)\" -DMHPDATADIR=\"$(MHPDATADIR)\" -DMHPFONTDIR=\"$(MHPFONTDIR)\" 

INCLUDES = $(VM_INCLUDE_OPTIONS) $(STANDARD_INCLUDE_OPTIONS)

%.o: %.c
	$(CXX) $(CXXFLAGS) -c $(DEFINES) $(INCLUDES) $<
   
all: libmhpjni_dvb.so

# Dependencies:

MAKEDEP = $(CXX) -MM -MG
DEPFILE = .dependencies
$(DEPFILE): Makefile
	@$(MAKEDEP) $(DEFINES) $(INCLUDES) $(OBJS:%.o=%.c) > $@

-include $(DEPFILE)

libmhpjni_dvb.so: $(OBJS)
	$(CXX) $(CXXFLAGS) -shared $(OBJS) $(ADDOBJS) $(LIBS) -o $@
   
clean:
	@-rm -f $(OBJS) $(DEPFILE) *.so *.tgz core* *~ *.a


