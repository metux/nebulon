
include ./siteconfig.mk

TEST_EXECUTABLE=nbtest
TEST_MAIN_CLASS=de.metux.nebulon.nbtest
TEST_MAIN_OBJECT=tmp/main.o
CFLAGS+=-rdynamic -O2

BUILDDIR=tmp

JAVA_SRC=`find src -name "*.java"`

compile:	$(TEST_EXECUTABLE)

$(TEST_MAIN_OBJECT):
	@mkdir -p `dirname "$@"`
	@gcj -c $(CFLAGS) $(OPENSSL_CNI_CFLAGS) $(JNANOWEB_CNI_CFLAGS) $(JAVA_SRC) -o $@

$(TEST_EXECUTABLE): $(TEST_MAIN_OBJECT)
	@mkdir -p `dirname "$@"`
	@gcj $< $(OPENSSL_CNI_LIBS) $(JNANOWEB_CNI_LIBS) --main=$(TEST_MAIN_CLASS) -o $@

clean:
	@rm -Rf tmp classes $(TEST_EXECUTABLE) $(TEST_MAIN_OBJECT)

run:	compile
	@(find -name "*.java" -exec "cat" "{}" ";")>TEST.IN
	@./$(TEST_EXECUTABLE)
	@diff -ruN TEST.IN TEST.OUT

policy:
	@for i in `find -name "*.java"` ; do \
		astyle --style=java --indent=tab --suffix=none --indent-switches < "$$i" > "$$i.tmp" 2>&1 | grep -ve "^unchanged" ; \
		mv "$$i.tmp" "$$i" ; \
	done

doc:
	@javadoc -d javadoc `find src -name "*.java"`

install:	$(TEST_EXECUTABLE)
	@mkdir -p $(DESTDIR)/$(SBINDIR)
	@cp $(TEST_EXECUTABLE) $(DESTDIR)/$(SBINDIR)
	@chmod u+x $(DESTDIR)/$(SBINDIR)/$(TEST_EXECUTABLE)
