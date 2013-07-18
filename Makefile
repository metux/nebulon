
EXECUTABLE=nbtest
MAIN_CLASS=de.metux.nebulon.nbtest
GCJ_ARGS=-rdynamic -O2

PREFIX?=/usr
SBINDIR?=$(PREFIX)/sbin

BUILDDIR=tmp

BCPROV_JAR=$(SYSROOT)/usr/share/java/bcprov.jar

OPENSSL_CNI_JAVA=src/de/metux/nebulon/crypt/OpenSSL.java
OPENSSL_CNI_OBJECT=$(BUILDDIR)/OpenSSL.o
OPENSSL_CNI_HEADER=$(BUILDDIR)/cni/de_metux_nebulon_crypt_OpenSSL.h
OPENSSL_CNI_SRC=src/OpenSSL.cpp
OPENSSL_CNI_CLASS=de.metux.nebulon.crypt.OpenSSL
OPENSSL_CNI_LIBS=-lcrypto

MAIN_OBJECT=tmp/main.o

JAVA_SRC=`find src -name "*.java"`

compile:	$(EXECUTABLE)

$(OPENSSL_CNI_HEADER):	$(OPENSSL_CNI_JAVA)
	@gcj -C $(OPENSSL_CNI_JAVA) -d $(BUILDDIR)/cni
	@gcjh -classpath $(BUILDDIR)/cni $(OPENSSL_CNI_CLASS) -o $(OPENSSL_CNI_HEADER)

$(MAIN_OBJECT):
	@gcj -c $(GCJ_ARGS) $(JAVA_SRC) --classpath $(BCPROV_JAR) -o $(MAIN_OBJECT)

$(OPENSSL_CNI_OBJECT): $(OPENSSL_CNI_HEADER)
	@g++ -fpic -c $(OPENSSL_CNI_SRC) -I$(BUILDDIR)/cni -o $(OPENSSL_CNI_OBJECT)

$(EXECUTABLE): $(OPENSSL_CNI_OBJECT) $(MAIN_OBJECT)
	@gcj $(MAIN_OBJECT) $(OPENSSL_CNI_OBJECT) $(OPENSSL_CNI_LIBS) --main=$(MAIN_CLASS) -o $(EXECUTABLE)

clean:
	@rm -Rf tmp classes $(EXECUTABLE) $(OPENSSL_CNI_OBJECT) $(MAIN_OBJECT)

run:	compile
	@(find -name "*.java" -exec "cat" "{}" ";")>TEST.IN
	@./$(EXECUTABLE)
	@diff -ruN TEST.IN TEST.OUT

policy:
	@for i in `find -name "*.java"` ; do \
		astyle --style=java --indent=tab --suffix=none --indent-switches < "$$i" > "$$i.tmp" 2>&1 | grep -ve "^unchanged" ; \
		mv "$$i.tmp" "$$i" ; \
	done

doc:
	@javadoc -d javadoc `find src -name "*.java"`

install:	$(EXECUTABLE)
	@mkdir -p $(DESTDIR)/$(SBINDIR)
	@cp $(EXECUTABLE) $(DESTDIR)/$(SBINDIR)
	@chmod u+x $(DESTDIR)/$(SBINDIR)/$(EXECUTABLE)
