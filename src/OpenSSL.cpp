#include <gcj/cni.h>
#include <java/lang/Object.h>
#include <gcj/array.h>
#include <java/util/Date.h>
#include <java/util/TreeSet.h>
#include <java/lang/Integer.h>
#include <stdio.h>
#include <java/lang/System.h>
#include <java/io/PrintStream.h>
#include <openssl/aes.h>
#include <openssl/blowfish.h>
#include <openssl/evp.h>
#include <openssl/err.h>
#include <malloc.h>
using java::lang::System;
using java::lang::Class;
#include "de_metux_nebulon_crypt_OpenSSL.h"

int handleErrors()
{
	fprintf(stderr, "openssl error\n");
	ERR_print_errors_fp(stderr);
}

int encrypt(unsigned char *plaintext, int plaintext_len, unsigned char *key,
  unsigned char *iv, unsigned char *ciphertext)
{
  EVP_CIPHER_CTX *ctx;

  int len;

  int ciphertext_len;

  /* Create and initialise the context */
  if(!(ctx = EVP_CIPHER_CTX_new())) handleErrors();

  /* Initialise the encryption operation. IMPORTANT - ensure you use a key
   * and IV size appropriate for your cipher
   * In this example we are using 256 bit AES (i.e. a 256 bit key). The
   * IV size for *most* modes is the same as the block size. For AES this
   * is 128 bits */
  if(1 != EVP_EncryptInit_ex(ctx, EVP_aes_256_cbc(), NULL, key, iv))
    handleErrors();

  /* Provide the message to be encrypted, and obtain the encrypted output.
   * EVP_EncryptUpdate can be called multiple times if necessary
   */
  if(1 != EVP_EncryptUpdate(ctx, ciphertext, &len, plaintext, plaintext_len))
    handleErrors();
  ciphertext_len = len;

  /* Finalise the encryption. Further ciphertext bytes may be written at
   * this stage.
   */
  if(1 != EVP_EncryptFinal_ex(ctx, ciphertext + len, &len)) handleErrors();
  ciphertext_len += len;

  /* Clean up */
  EVP_CIPHER_CTX_free(ctx);

  return ciphertext_len;
}

int decrypt(unsigned char *ciphertext, int ciphertext_len, unsigned char *key,
  unsigned char *iv, unsigned char *plaintext)
{
  EVP_CIPHER_CTX *ctx;

  int len;

  int plaintext_len;

  /* Create and initialise the context */
  if(!(ctx = EVP_CIPHER_CTX_new())) handleErrors();

  /* Initialise the decryption operation. IMPORTANT - ensure you use a key
   * and IV size appropriate for your cipher
   * In this example we are using 256 bit AES (i.e. a 256 bit key). The
   * IV size for *most* modes is the same as the block size. For AES this
   * is 128 bits */
  if(1 != EVP_DecryptInit_ex(ctx, EVP_aes_256_cbc(), NULL, key, iv))
    handleErrors();

  /* Provide the message to be decrypted, and obtain the plaintext output.
   * EVP_DecryptUpdate can be called multiple times if necessary
   */
  if(1 != EVP_DecryptUpdate(ctx, plaintext, &len, ciphertext, ciphertext_len))
    handleErrors();
  plaintext_len = len;

  /* Finalise the decryption. Further plaintext bytes may be written at
   * this stage.
   */
  if(1 != EVP_DecryptFinal_ex(ctx, plaintext + len, &len)) handleErrors();
  plaintext_len += len;

  /* Clean up */
  EVP_CIPHER_CTX_free(ctx);

  return plaintext_len;
}


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

	printf("content size=%d\n", (int)content->length);
	printf("key size=%d\n", (int)key->length);

	/** hmm, really a good idea ? **/
	unsigned char buffer[65535];
	unsigned char tag[1024];
	int sz = encrypt(
		(unsigned char*)elements(content),		/** plaintext **/
		content->length,
		(unsigned char*)elements(key),			/** key **/
		(unsigned char*)elements(key),			/** IV **/
		buffer
	);

	printf("==> encrypted size=%d\n", sz);
	JArray< jbyte >* result = JvNewByteArray(sz);
	memcpy(elements(result), buffer, sz);

	return result;
}

JArray< jbyte > * de::metux::nebulon::crypt::OpenSSL::AES_decrypt(JArray< jbyte > * key, JArray< jbyte > * content) {
	if (content == NULL) {
		printf("AES: content is null\n");
		return NULL;
	}

	if (key == NULL) {
		printf("AES: key is null\n");
		return NULL;
	}

	printf("content size=%d\n", (int)content->length);
	printf("key size=%d\n", (int)key->length);

	/** hmm, really a good idea ? **/
	unsigned char buffer[65535];
	unsigned char tag[1024];
	int sz = decrypt(
		(unsigned char*)elements(content),		/** plaintext **/
		content->length,
		(unsigned char*)elements(key),			/** key **/
		(unsigned char*)elements(key),			/** IV **/
		buffer
	);

	printf("==> encrypted size=%d\n", sz);
	JArray< jbyte >* result = JvNewByteArray(sz);
	memcpy(elements(result), buffer, sz);

	return result;
}

JArray< jbyte > * de::metux::nebulon::crypt::OpenSSL::Blowfish_encrypt(JArray< jbyte > * key, JArray< jbyte > * content) {
	if (key == NULL) {
		printf("Blowfish: key is null\n");
		return NULL;
	}

	if (content == NULL) {
		printf("Blowfish: content is null\n");
		return NULL;
	}

	printf("key size=%d\n", (int)key->length);
	printf("content size=%d\n", (int)content->length);
	jbyte *intp = elements(key);

//	for (int x=0; x<(key->length); x++) {
//		printf("%02X ", (intp[x] & 0xFF));
//	}
//	printf("\n");

	return NULL;
}

JArray< jbyte > * de::metux::nebulon::crypt::OpenSSL::Blowfish_decrypt(JArray< jbyte > * key, JArray< jbyte > * crypttext) {
	return key;
}

