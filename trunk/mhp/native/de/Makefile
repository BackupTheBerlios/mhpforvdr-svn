
include ../../../Make.config

OBJS =  \
	syslog.o \
	dirinfo.o \
	application.o \
	si.o \
	service.o \
	storage.o
   
LIBS = 
#CXXFLAGS += -O0
##these objects do not form a shared or static library, they are included in this lib
#ADD_OBJS= ../../lib/inputdriver/vdrinput.o \

CXXFLAGS += -DMHPLIBDIR=\"$(MHPLIBDIR)\" -DMHPDATADIR=\"$(MHPDATADIR)\" -DMHPFONTDIR=\"$(MHPFONTDIR)\" 

INCLUDES = $(VM_INCLUDE_OPTIONS) -I ../../lib -I ../../.. -I../../../$(VDRDIR_RELATIVE)

%.o: %.c
	$(CXX) $(CXXFLAGS) -c $(DEFINES) $(INCLUDES) $<
   
all: libvdrmhp.so

# Dependencies:

MAKEDEP = $(CXX) -MM -MG
DEPFILE = .dependencies
$(DEPFILE): Makefile
	@$(MAKEDEP) $(DEFINES) $(INCLUDES) $(OBJS:%.o=%.c) > $@

-include $(DEPFILE)

libvdrmhp.so: $(OBJS)
	$(CXX) $(CXXFLAGS) -shared $(OBJS) $(ADDOBJS) $(LIBS) -o $@
   
clean:
	@-rm -f $(OBJS) $(DEPFILE) *.so *.tgz core* *~ *.a


