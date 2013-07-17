
EXECUTABLE=nbtest
MAIN_CLASS=de.metux.nebulon.nbtest
GCJ_ARGS=-rdynamic -O2

PREFIX?=/usr
SBINDIR?=$(PREFIX)/sbin

BCPROV_JAR=$(SYSROOT)/usr/share/java/bcprov.jar

compile:	$(EXECUTABLE)

OPENSSL_CNI_JAVA=src/de/metux/nebulon/crypt/OpenSSL.java
OPENSSL_CNI_OBJECT=OpenSSL.o

cni:
	gcj -C $(OPENSSL_CNI_JAVA) -d tmp/cni
	gcjh -classpath tmp/cni de.metux.nebulon.crypt.OpenSSL -o tmp/cni/de_metux_nebulon_crypt_OpenSSL.h
	g++ -c src/OpenSSL.cpp -Itmp/cni -o $(OPENSSL_CNI_OBJECT)

$(EXECUTABLE): cni
	@echo "Building $@"
	@rm -Rf classes
	@mkdir -p classes
	gcj $(GCJ_ARGS) -rdynamic `find src -name "*.java"` $(OPENSSL_CNI_OBJECT) --classpath $(BCPROV_JAR) -o $(EXECUTABLE) --main=$(MAIN_CLASS)

clean:
	@rm -Rf tmp classes $(EXECUTABLE) $(OPENSSL_CNI_OBJECT)

run:	compile
	./$(EXECUTABLE)

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

test:	$(EXECUTABLE)
	./$(EXECUTABLE) 2> 1.out
	./$(EXECUTABLE) 2> 2.out
	./$(EXECUTABLE) 2> 3.out
	./$(EXECUTABLE) 2> 4.out
	./$(EXECUTABLE) 2> 5.out
	./$(EXECUTABLE) 2> 6.out
	./$(EXECUTABLE) 2> 7.out
	./$(EXECUTABLE) 2> 8.out
