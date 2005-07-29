
-include ../../Make.config

JIKES = jikes

##CLASSPATH = .:$(VM_CLASSPATH):thirdparty/log4j.jar:thirdparty/com_stevesoft_regex.jar:thirdparty/jmf.jar
##for compile
EXTRA_CLASSPATH_SEPARATOR = :
EXTRA_CLASSPATH_PREFIX = ./thirdparty
LOCAL_EXTRA_CLASSPATH := $(EXTRA_CLASSPATH)

CLASSPATH = .:$(VM_CLASSPATH):$(LOCAL_EXTRA_CLASSPATH)


## for thirdparty-install
EXTRA_CLASSPATH_SEPARATOR = "" 
        ##This is two quotation marks and a trailing blank!
EXTRA_CLASSPATH_PREFIX = ./thirdparty
INSTALL_EXTRA_CLASSPATH := $(EXTRA_CLASSPATH)

## for getjikes
EXTRA_CLASSPATH_SEPARATOR = :
EXTRA_CLASSPATH_PREFIX = mhp/java
TOPDIR_EXTRA_CLASSPATH := $(EXTRA_CLASSPATH)


SUBDIRS = vdrsupport libcommoncode connector inputdriver

JAVAFILELIST = ./javafilelist
CLASSFILELIST = ./classfilelist

JARFILE = ./$(MHPJARFILENAME)

all: $(JARFILE)
##per default, java files come precompiled
##If file does not exist (checkout from SVN), compile it.

##use "make java" to recompile Java classes
java: compile jar

compile: check-jmf javafilelist
	$(JIKES) +Z0 -classpath $(CLASSPATH) \@$(JAVAFILELIST)
   
jar: classfilelist
##	jar cf $(JARFILE) `cat $(CLASSFILELIST)`
	zip -q $(JARFILE) `cat $(CLASSFILELIST)`

$(JARFILE):
	@echo "Precompiled Java files are not found, compiling them now with jikes"
	make java

javafilelist:
	@find -name \*.java -and -not -path \*_unimplemented\* -not -path \*_deprecated\* -printf "%P\n" > $(JAVAFILELIST)
   
classfilelist:
	@find -name \*.class -printf "%P\n" > $(CLASSFILELIST)
   
.PHONY: classfilelist javafilelist
   
clean:
#do nothing for usual "clean". Developers can do "make java-clean"
	@echo "To delete the Java class files, do \"make java-clean\""
	@echo "To delete the Java class and the JAR file, do \"make java-clean-all\""
   
java-clean:
	@find -name \*.class -exec rm {} \;
	@-rm -f $(JAVAFILELIST) $(CLASSFILELIST)

##Do not delete mhp.jar file for distribution!
java-distclean: java-clean
        
java-clean-all: java-distclean
	@-rm -f $(JARFILE)


install: jarfile-install thirdparty-install

jarfile-install:
	install -d $(MHPJARDIR)
	cp $(JARFILE) $(MHPJARFILE)
	
thirdparty-install: check-jmf
	cp $(INSTALL_EXTRA_CLASSPATH) $(MHPJARDIR)

check-jmf:
	@if [ ! -e thirdparty/jmf.jar ]; then \
		 echo ;\
		 echo "JMF file not found.";\
		 echo "Please obtain the JMF implementation from SUN";\
		 echo " ( http://java.sun.com/products/java-media/jmf/ )";\
		 echo "and copy the file \"jmf.jar\" to \"mhp/java/thirdparty/\".";\
		 echo ;\
		 exit 1; \
	 fi
   
testcase:
	$(JIKES) -classpath $(CLASSPATH) ../../testapps/SimpleXlet/SimpleXlet.java

getjikes-for-topdir:
	@echo
	@echo "Use the following command to compile a local application stored under "$(BASEDIR)/apps/". Append \"MyApp/MyApp.java\":"
	@echo '$(JIKES) -classpath mhp/java:$(VM_CLASSPATH):$(TOPDIR_EXTRA_CLASSPATH)  $(BASEDIR)/apps/'
	@echo
