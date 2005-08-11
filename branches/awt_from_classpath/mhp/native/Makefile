
-include ../../Make.config

SUBDIRS = DirectFB vdr

OBJS = DirectFB/libmhpjni_directfbawt.so vdr/libmhpjni_dvb.so


all:
	@echo "\"make all\" does nothing in this directory. Make \"subplugins\" to compile subdirs."
subplugins: subdirs

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
