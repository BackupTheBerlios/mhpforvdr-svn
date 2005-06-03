
-include ../../Make.config

SUBDIRS = DirectFB de

OBJS = DirectFB/libdirectfbawt.so de/libvdrmhp.so

all: subdirs

subdirs:
	@for i in $(SUBDIRS) ;\
	do \
	    ( cd $$i;\
              $(MAKE);\
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
	@install -d $(MHPLIBDIR)
	@for i in $(OBJS) ;\
	do \
      ( cp $$i $(MHPLIBDIR) ) \
	|| exit 1;\
	done
