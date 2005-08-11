
-include ../Make.config

SUBDIRS = vdrinput

OBJS =

#all: subdirs
#nothing to do
all:

#used by the plugin to connect to the app, so the Message and AppConnector classes are included
#libappconnector.a:
#	$(AR) $(ARFLAGS) $@ connector/appconnector.o connector/appmessages.o
   
#the ParentConnector classes are included in libvdrmhp.so in ../native.
#That library is loaded by the Java application. For some reason the object
#files are linked directly in the library, no static library created here!

subdirs:
	@for i in $(SUBDIRS) ;\
	do \
	    ( cd $$i;\
              $(MAKE) all ;\
       ) \
	|| exit 1;\
	done
   
clean:
	@for i in $(SUBDIRS) ;\
	do \
	    ( cd $$i;\
              $(MAKE) clean;\
       ) \
	|| exit 1;\
	done
	@-rm -f *.so core* *~

install: all
