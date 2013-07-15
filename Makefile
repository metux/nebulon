
EXECUTABLE=nbtest
MAIN_CLASS=de.metux.nebulon.nbtest
GCJ_ARGS=-rdynamic -fjni -O2

PREFIX?=/usr
SBINDIR?=$(PREFIX)/sbin

compile:	$(EXECUTABLE)

$(EXECUTABLE):
	@echo "Building $@"
	@rm -Rf classes
	@mkdir -p classes
	@javac -d classes `find src -name "*.java"`
	@gcj $(GCJ_ARGS) -rdynamic -fjni `find src -name "*.java"` -o $(EXECUTABLE) --main=$(MAIN_CLASS)

clean:
	@rm -Rf tmp classes $(EXECUTABLE)

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
