
-include ../../Make.config

NATIVEDIR = ..

### Includes and Defines (add further entries here):

INCLUDES +=  -I$(DIRECTFB_INCLUDES) -I$(DIRECTFB_INCLUDES_INTERNAL) -I./dfb_config/ -I$(NATIVEDIR)

LIBS     += 

DEFINES += 

### The object files 
#(add further files here):

OBJS = vdrinput.o

### Implicit rules:

%.o: %.c
	$(CC) $(CFLAGS) -c $(DEFINES) $(INCLUDES) $<

# Dependencies:

MAKEDEP = g++ -MM -MG
DEPFILE = .dependencies
$(DEPFILE): Makefile
	@$(MAKEDEP) $(DEFINES) $(INCLUDES) $(OBJS:%.o=%.c) > $@

-include $(DEPFILE)

### Targets:

all: $(OBJS)


clean:
	@-rm -f $(OBJS) $(DEPFILE) *.so *.tgz core* *.a *~

#lib$(LIB).a: $(OBJS)
#	$(AR) $(ARFLAGS) $@ $(OBJS)
