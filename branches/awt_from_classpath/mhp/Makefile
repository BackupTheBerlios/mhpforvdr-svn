
-include ../Make.config

SUBDIRS = native java ttf-bitstream-vera-1.10

all: subdirs

subdirs:
	@for i in $(SUBDIRS) ;\
	do \
	    ( cd $$i;\
              $(MAKE) all;\
       ) \
	|| exit 1;\
	done
   
subplugins:
	$(MAKE) -C native subplugins
   
clean:
	@for i in $(SUBDIRS) ;\
	do \
	    ( cd $$i;\
              $(MAKE) clean;\
       ) \
	done
	@-rm -f *.so core* *~

install: all subplugins
	@for i in $(SUBDIRS) ;\
	do \
	    ( cd $$i;\
              $(MAKE) install;\
       ) \
	|| exit 1;\
	done
   
java-clean:
	( cd java;\
              $(MAKE) java-clean;\
   ) ;\
   

#for developer's use only
devel-dist: clean java-clean
	@tar cvjf mhp.tar.bz2 .  --exclude ./java/thirdparty/log4j.jar --exclude mhp.tar.bz2


