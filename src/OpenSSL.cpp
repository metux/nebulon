#include <gcj/cni.h>
#include <java/lang/Object.h>
#include <gcj/array.h>
#include <java/util/Date.h>
#include <java/util/TreeSet.h>
#include <java/lang/Integer.h>
#include <stdio.h>
#include <java/lang/System.h>
#include <java/io/PrintStream.h>
//#include <openssl/aes.h>
#include <malloc.h>
using java::lang::System;
using java::lang::Class;
#include "de_metux_nebulon_crypt_OpenSSL.h"

extern "C" {

#include <openssl/aes.h>

};

void de::metux::nebulon::crypt::OpenSSL::dummy(::java::lang::String * str) {
	printf("FOO BAR\n");
	System::out->println(str);
	printf("HMM\n");
}

JArray< jbyte > * de::metux::nebulon::crypt::OpenSSL::AES_encrypt(JArray< jbyte > * key, JArray< jbyte > * content) {
	if (content == NULL) {
		printf("AES: content is null\n");
		return NULL;
	}

	if (key == NULL) {
		printf("AES: key is null\n");
		return NULL;
	}

	printf("content size=%d\n", content->length);
	printf("key size=%d\n", key->length);
	jbyte *intp = elements(key);

//	for (int x=0; x<(key->length); x++) {
//		printf("%02X ", (intp[x] & 0xFF));
//	}
//	printf("\n");

	AES_KEY enc_key;
	AES_set_encrypt_key((const unsigned char*)intp, (key->length)*8, &enc_key);

	JArray< jbyte >* result = JvNewByteArray(content->length);

	return result;
}

JArray< jbyte > * de::metux::nebulon::crypt::OpenSSL::AES_decrypt(JArray< jbyte > * key, JArray< jbyte > * crypttext) {
	return key;
}
