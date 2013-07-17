#include <gcj/cni.h>
#include <java/lang/Object.h>
#include <gcj/array.h>
#include <java/util/Date.h>
#include <java/util/TreeSet.h>
#include <java/lang/Integer.h>
#include <stdio.h>
#include <java/lang/System.h>
#include <java/io/PrintStream.h>
#include <malloc.h>
using java::lang::System;
using java::lang::Class;
#include "de_metux_nebulon_crypt_OpenSSL.h"

void de::metux::nebulon::crypt::OpenSSL::dummy(::java::lang::String * str) {
	printf("FOO BAR\n");
	System::out->println(str);
	printf("HMM\n");
}

JArray< jbyte > * de::metux::nebulon::crypt::OpenSSL::AES_encrypt(JArray< jbyte > * key, JArray< jbyte > * content) {
	if (content == NULL) {
		printf("content is NULL\n");
	} else {
		printf("X content is not null:\n", content);
//		content= JvNewByteArray(5);
		printf("size=%d\n", content->length);
		jbyte *intp = elements(content);
//		char* buffer = (char*)malloc(content->length);
//		memcpy(buffer, content->elements, content->length);
//		jbyte b = intp[0];
//		printf("%c%c%c%c\n", intp[0], intp[1], intp[2], intp[3]);
		System::out->println(content->length);
		printf("FOO\n");
	}
	return content;
}

JArray< jbyte > * de::metux::nebulon::crypt::OpenSSL::AES_decrypt(JArray< jbyte > * key, JArray< jbyte > * crypttext) {
	return key;
}
