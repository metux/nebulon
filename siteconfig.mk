PREFIX?=/usr
BINDIR?=$(PREFIX)/bin
SBINDIR?=$(PREFIX)/sbin
PKG_CONFIG?=pkg-config

OPENSSL_CNI_LIBS?=`$(PKG_CONFIG) --libs openssl-cni`
OPENSSL_CNI_CFLAGS?=`$(PKG_CONFIG) --cflags openssl-cni`
